package jp.enpitsu.meepa.Rader;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

/**
 * 最も基本的なLocationProviderのサンプル実装（※「 http://goo.gl/pvkXV 」を参照）。自由にカスタマイズして処理を洗煉させてください。
 */
public class LocationProvider implements ILocationProvider {

    /** 位置更新のたびに呼ばれる位置情報リスナー。 */
    private final LocationListener locationListener;

    /** GPS／ネットワーク位置（ネットワーク基地局）にアクセスを許可するための、システムのLocationManager。 */
    private final LocationManager locationManager;

    /** 位置更新（GPS）が発生するミリ秒を指定します。おおよそ毎秒、発生するようにした方がよいです。これ例では1000ミリ秒＝1秒ごと。 */
    private static int        LOCATION_UPDATE_MIN_TIME_GPS	= 5000;

    /** 位置更新（GPS）のシグナルが発生する距離（m）を指定します。「0」mを指定すると、場所が前回と同じでもシグナルが発生します。 */
    private static int        LOCATION_UPDATE_DISTANCE_GPS	= 0;

    /** 位置更新（ネットワーク基地局）が発生するミリ秒を指定します。おおよそ毎秒、発生するようにした方がよいです。これ例では1000ミリ秒＝1秒ごと。 */
    private static int        LOCATION_UPDATE_MIN_TIME_NW		= 5000;

    /** 位置更新（ネットワーク基地局）のシグナルが発生する距離（m）を指定します。「0」mを指定すると、場所が前回と同じでもシグナルが発生します。 */
    private static int        LOCATION_UPDATE_DISTANCE_NW		= 0;

    /** 位置情報へのアクセスを早めるために、10分（＝1000ミリ秒×60秒×10分）前の古い位置情報であっても起動時に使用します。時間は微調整できます。 */
    private static int        LOCATION_OUTDATED_WHEN_OLDER_MS	= 1000 * 60 * 10;

    /** システム設定において、GPSプロバイダーやネットワークプロバイダーが有効になっているかどうかを示すフィールド変数。 */
    private boolean                 gpsProviderEnabled, networkProviderEnabled;

    // レーダー圏内，圏外の場合のGPS取得頻度
    private static final int UPDATE_MIN_TIME_IN_RADER_RANGE     = 1000;
    private static final int UPDATE_MIN_TIME_OUT_OF_RADER_RANGE = 5000;

    /** 現在実行中のコンテキスト。 */
    private final Context context;


    public LocationProvider( final Context context, LocationListener locationListener ) {
        super();
        this.locationManager = (LocationManager)context.getSystemService( Context.LOCATION_SERVICE );
        this.locationListener = locationListener;
        this.context = context;
        this.gpsProviderEnabled = this.locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER );
        this.networkProviderEnabled = this.locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER );
    }

    @Override
    public void onPause() {
        if ( this.locationListener != null && this.locationManager != null && (this.gpsProviderEnabled || this.networkProviderEnabled) ) {
            this.locationManager.removeUpdates( this.locationListener );
        }
    }

    @Override
    public void onResume() {
        if ( this.locationManager != null && this.locationListener != null ) {

            // 各プロバイダーが利用可能かどうかをチェックする。
            this.gpsProviderEnabled = this.locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER );
            this.networkProviderEnabled = this.locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER );

            // GPSプロバイダーが有効な場合の処理。
            if ( this.gpsProviderEnabled ) {
                final Location lastKnownGPSLocation = this.locationManager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
                if ( lastKnownGPSLocation != null && lastKnownGPSLocation.getTime() > System.currentTimeMillis() - LOCATION_OUTDATED_WHEN_OLDER_MS ) {
                    locationListener.onLocationChanged( lastKnownGPSLocation );
                }
                if (locationManager.getProvider(LocationManager.GPS_PROVIDER)!=null) {
                    this.locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, LOCATION_UPDATE_MIN_TIME_GPS, LOCATION_UPDATE_DISTANCE_GPS, this.locationListener );
                    Log.d("ProviderName", "gps");
                }
            }

            //  ネットワーク／WiFi位置情報（ネットワーク基地局）プロバイダーが有効な場合の処理。
            if ( this.networkProviderEnabled ) {
                final Location lastKnownNWLocation = this.locationManager.getLastKnownLocation( LocationManager.NETWORK_PROVIDER );
                if ( lastKnownNWLocation != null && lastKnownNWLocation.getTime() > System.currentTimeMillis() - LOCATION_OUTDATED_WHEN_OLDER_MS ) {
                    locationListener.onLocationChanged( lastKnownNWLocation );
                }
                if (locationManager.getProvider(LocationManager.NETWORK_PROVIDER)!=null) {
                    this.locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_MIN_TIME_NW, LOCATION_UPDATE_DISTANCE_NW, this.locationListener );
                    Log.d("ProviderName", "network");
                }
            }

            // ユーザーにより、位置情報が有効になっていない場合の処理。オススメ： このイベントはアプリで処理してください。例えば「new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS )」というコードで、ユーザーを直接ロケーションに誘導するなど。
            if ( !this.gpsProviderEnabled || !this.networkProviderEnabled ) {
                Toast.makeText( this.context, "［設定］－［位置情報］で［高精度］／［バッテリー節約］などを選択してGPSおよびモバイルネットワークの両方を有効にしてください。", Toast.LENGTH_LONG ).show();
            }
        }
    }

    /*
     * 位置情報の取得頻度（次の位置情報を取得するまでの最短時間）を設定
     * 相手との距離（レーダー圏内/圏外）に応じて変化
     */
    @Override
    public void setLocationUpdateMinTime( boolean isWithInRaderRange ) {
        if ( isWithInRaderRange == true ) {
            this.LOCATION_UPDATE_MIN_TIME_GPS = UPDATE_MIN_TIME_IN_RADER_RANGE;
            this.LOCATION_UPDATE_MIN_TIME_NW = UPDATE_MIN_TIME_IN_RADER_RANGE;
        } else {
            this.LOCATION_UPDATE_MIN_TIME_GPS = UPDATE_MIN_TIME_OUT_OF_RADER_RANGE;
            this.LOCATION_UPDATE_MIN_TIME_NW = UPDATE_MIN_TIME_OUT_OF_RADER_RANGE;
        }
    }

}
