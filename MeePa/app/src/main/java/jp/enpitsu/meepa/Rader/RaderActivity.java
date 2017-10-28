package jp.enpitsu.meepa.Rader;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.PermissionChecker;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Date;

import jp.enpitsu.meepa.Global.MeePaApp;
import jp.enpitsu.meepa.Lookfor.LookActivity;
import jp.enpitsu.meepa.Rader.ARObjects.OpenGLES20.MyGLSurfaceView;
import jp.enpitsu.meepa.R;
import jp.enpitsu.meepa.Rader.ARObjects.OpenGLES20.RADER_VALUES;
import jp.enpitsu.meepa.ShareCameraView.ShareCameraViewActivity;
import jp.enpitsu.meepa.WiFiDirect.WiFiDirect;
import jp.enpitsu.meepa.Registor.RegActivity;
import jp.enpitsu.meepa.WiFiDirect.WiFiDirectEventListener;

/**
 * Created by iyobe on 2016/09/26.
 */
public class RaderActivity extends Activity {
    private Camera2 mCamera;

    private MyGLSurfaceView glView;

    private PermissionManager permissionManager;

    ToggleButton button_AR, button_Vibration, button_WifiDirect;
    ImageView backgroundImageView;
    TextureView textureView;

    LinearLayout linearLayout_raderMessages;
    TextView textView_DistanceMessage;
    TextView textView_AccuracyMessage;

    LinearLayout linearLayout_ARMessages;
    TextView textView_reqNameAR;
    TextView textView_distanceAR;

    Handler handler;

    //WiFiDirect
    WiFiDirect wfd;

    TextView textView_WifiDirectMessage;

    Button button_info;
    TextView textView_info;
    boolean isInfoVisible = false;


    private MeePaApp meepaApp; // グローバルクラス


    ////////////////////////////////////////////////////////////
    // コンパス用のセンサ関連
    private SensorManager mSensorManager = null;
    private SensorEventListener mSensorEventListener = null;

    private float[] fAccell = null;
    private float[] fMagnetic = null;
    ///////////////////////////////////////////////////////////

    // バイブレータ
    Vibrator vibrator;
    private boolean flag_vibrator = true; // 振動させるかさせないか

    String myID, oppID;
    String oppName; // 相手ユーザ名

    private LocationData myLocationData;  // 自分の位置情報
    private LocationData oppLocationData; // 相手の位置情報

    /** 位置情報の更新を受信するためのリスナー。これを、ARchitectViewに通知して、ARchitect Worldの位置情報を更新します。*/
    protected LocationListener locationListener;

    /** 最も基本的なLocation戦略のサンプル実装（※「 http://goo.gl/pvkXV 」を参照）。LocationProvider.javaファイルのコードを自由にカスタマイズして処理を洗煉させてください。*/
    protected ILocationProvider locationProvider;

    /** 最新のユーザー位置情報。本サンプルでは位置情報が取得されているかどうかの判定で使われています（※本サンプルではコードはありますが実質的には使っていません）。*/
    protected Location lastKnownLocaton;


    // ボタンとかいろいろ初期化
    private void initViewsAndItems() {

        myLocationData  = new LocationData( 30, 30, 30 );
        oppLocationData = new LocationData( 30, 30, 30 );

        glView = new MyGLSurfaceView( this );
        glView.setZOrderOnTop(true);

        permissionManager = new PermissionManager( this );

        final View view = this.getLayoutInflater().inflate(R.layout.activity_rader, null);

        // GLSurfaceViewを最初にセット
        this.setContentView( glView,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

        // カメラプレビュー・コンパスのレイアウトをセット
        this.addContentView( view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT ));


        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        backgroundImageView = (ImageView)findViewById( R.id.backgroundImageView );

        button_AR          = (ToggleButton)findViewById( R.id.button_AR );
        button_Vibration  = (ToggleButton)findViewById( R.id.button_Vibe );
        button_WifiDirect = (ToggleButton)findViewById( R.id.button_wifiDirect );

        linearLayout_raderMessages = (LinearLayout)findViewById( R.id.linearLayout_raderMessages );
        textView_DistanceMessage = (TextView)findViewById( R.id.textView_DistanceMessage );
        textView_AccuracyMessage = (TextView)findViewById( R.id.textView_AccuracyMessage );

        linearLayout_ARMessages = (LinearLayout)findViewById( R.id.linearLayout_ARMassages );
        textView_reqNameAR = (TextView)findViewById( R.id.textView_reqNameAR );
        textView_distanceAR = (TextView)findViewById( R.id.textView_distanceAR );
        textView_reqNameAR.setText( oppName ); // 相手のユーザ名セット

        // デバッグ用
        button_info = (Button)findViewById( R.id.button_info );
        textView_info = (TextView)findViewById( R.id.textView_info );

        // フォント設定
        button_AR.setTypeface( Typeface.createFromAsset( getAssets(), "FLOPDesignFont.ttf" ), Typeface.NORMAL );
        button_WifiDirect.setTypeface( Typeface.createFromAsset( getAssets(), "FLOPDesignFont.ttf" ), Typeface.NORMAL );
        button_Vibration.setTypeface( Typeface.createFromAsset( getAssets(), "FLOPDesignFont.ttf" ), Typeface.NORMAL );
        textView_DistanceMessage.setTypeface( Typeface.createFromAsset( getAssets(), "FLOPDesignFont.ttf" ), Typeface.NORMAL );
        textView_AccuracyMessage.setTypeface( Typeface.createFromAsset( getAssets(), "FLOPDesignFont.ttf" ), Typeface.NORMAL );
        textView_reqNameAR.setTypeface( Typeface.createFromAsset( getAssets(), "FLOPDesignFont.ttf" ), Typeface.NORMAL );
        textView_distanceAR.setTypeface( Typeface.createFromAsset( getAssets(), "FLOPDesignFont.ttf" ), Typeface.NORMAL );


        textView_WifiDirectMessage = (TextView)findViewById( R.id.textView_WifiDirectMessage );
        textView_WifiDirectMessage.setTypeface( Typeface.createFromAsset( getAssets(), "FLOPDesignFont.ttf" ), Typeface.NORMAL );
        textView_WifiDirectMessage.setMovementMethod( ScrollingMovementMethod.getInstance() );

        //WiFiDirectクラスのインスタンス作成とボタンの登録
        wfd = new WiFiDirect( RaderActivity.this );
        wfd.setCompoundButton( button_WifiDirect );
        wfd.setTextView( textView_WifiDirectMessage );
        // WifiDirectのイベントリスナ
        wfd.setWiFiDirectEventListener(new WiFiDirectEventListener() {
            @Override
            public void receiveChat(String str) {
                wfd.toast( str );
                Log.d( "recieveChat@RaderAct", str );
            }

            @Override
            public void receiveGPSLocation(LocationData loc) {
                wfd.toast( loc.dump() );
                oppLocationData = loc; // 相手の位置情報更新
                getDistance();            // 距離更新

                Log.d( "recieveChat@RaderAct", loc.dump() );
            }
        });



        textureView = (TextureView) findViewById( R.id.texture_view );
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                // 通常はここでカメラ起動
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                // mCamAnge[0] : 横の画角
                //         [1] : 縦の画角
//                mCamAngle = mCamera.getAngle();
//                glView.setCameraAngle( mCamAngle );
            }
        });
    }

    // 磁気・加速度センサの利用
    private void useSensors() {
        ////////////////////////////////////////////////////////////////////////////////////////////
        // センサのコピペ //////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        mSensorManager = (SensorManager) getSystemService( Context.SENSOR_SERVICE );    // SensorManager取得

        // SensorManagerに加速度センサと自機センサについてSensorEventListenerを登録
        mSensorEventListener = new SensorEventListener()
        {
            SensorFilter sensorFilter = new SensorFilter();
            public void onSensorChanged (SensorEvent event) {
                // センサの取得値をそれぞれ保存しておく
                switch( event.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        fAccell = event.values.clone();
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        fMagnetic = event.values.clone();
                        break;
                }

                // fAccell と fMagnetic から傾きと方位角を計算する
                if( fAccell != null && fMagnetic != null ) {
                    // 回転行列を得る
                    float[] inR = new float[9];
                    SensorManager.getRotationMatrix(
                            inR,
                            null,
                            fAccell,
                            fMagnetic );
                    // ワールド座標とデバイス座標のマッピングを変換する
                    float[] outR = new float[9];
                    SensorManager.remapCoordinateSystem(
                            inR,
                            SensorManager.AXIS_X, SensorManager.AXIS_Y,
                            outR );
                    // 姿勢を得る
                    float[] fAttitude = new float[3];
                    SensorManager.getOrientation(
                            outR,
                            fAttitude );

                    // fAttitude[0] : 方位角（北が0, 時計回りに値増加）
                    //          [1] : 前後の傾斜
                    //          [2] : 左右の傾斜
                    fAttitude[0] = (float)rad2deg( fAttitude[0] );  // 方位角を変換(ラジアン→度)
                    if( fAttitude[0] < 0 ) {
                        // 0～360度の値にする
                        fAttitude[0] = 360f + fAttitude[0];
                    }
                    // フィルタを掛ける
                    sensorFilter.addSample( fAttitude );

                    // サンプルが必要数溜まったら
                    if( sensorFilter.isSampleEnable() ) {
                        fAttitude = sensorFilter.getParam();

                        double direction =  fAttitude[0];           // 端末の向いてる方向
                        double elevation = rad2deg( fAttitude[1] ); // 端末の前後の傾き
                        if( direction < 0 ) {
                            // 0～360度の値にする
                            direction = 360f + direction;
                        }
                        // レーダー更新
                        RADER_VALUES.invalidateDeviceDirection( (float)direction );
                        RADER_VALUES.invalidateElevation( (float) elevation );
                    }
                }
            }
            public void onAccuracyChanged (Sensor sensor, int accuracy) {}
        };
        ////////////////////////////////////////////////////////////////////////////////////////////
    }

    // 位置情報（GPS）の利用
    private void useGPS() {
        ////////////////////////////////////////////////////////////////////////////////////////////
        // 位置情報関連のコピペ ////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        permissionManager.requestLocationInfoPermission();
        if (this.isFinishing()) return;

        //  位置情報のリスナーを登録します。全ての位置情報更新はここで処理され、ここから本アプリ内で一元的に位置情報を管理するプロバイダー「locationProvider」に引き渡されます。
        this.locationListener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Location", "onStatusChanged");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("Location", "onProviderEnabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Location", "onProviderDisabled");
            }

            @Override
            public void onLocationChanged(final Location location) {
                Log.d("Location", "onLcationChanged");
                myLocationData = new LocationData( location.getLatitude(), location.getLongitude(), location.getAccuracy() );

                if ( button_WifiDirect.isChecked() == true ) { // WifiダイレクトのボタンがONになっている場合
                    // Wifiダイレクトの接続状況はわかんないけどとりあえず位置情報投げてみる方針
                    wfd.sendGPSLocation( myLocationData );
                }
                else {
                    // 自分の位置情報を送信しつつ相手の位置情報を得る
                    HttpCommunication httpCommunication = new HttpCommunication(
                            new HttpCommunication.AsyncTaskCallback() {
                                @Override
                                public void postExecute(LocationData result) {
                                    oppLocationData = result;
                                    getDistance(); // 相手の位置情報更新
                                }
                            }
                    );
                    httpCommunication.setID( myID, oppID );
                    httpCommunication.setLocation( myLocationData );
                    httpCommunication.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );
                }

                getDistance(); // 自分の位置情報更新

//                Log.d( "MyLocation", location.getLatitude() + ", " + location.getLongitude() + " ( " + location.getAccuracy() + " )" );

            }
        };

        // 位置情報を収集するために使うLocationProviderに、位置情報リスナー（locationListener）を指定してインスタンスを生成・取得
        this.locationProvider = getLocationProvider( this.locationListener );
        Log.d("Location", "LocationProviderにリスナ指定");

        ////////////////////////////////////////////////////////////////////////////////////////////
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        meepaApp = (MeePaApp)this.getApplication(); // グローバルクラス
        // グローバルクラスから自分・相手のID読み込み
        myID = meepaApp.getSelfUserId();
        oppID = meepaApp.getOpponentUserId();
        oppName = meepaApp.getOpponentUserName();

        // 例外処理
        Intent intent;
        if ( myID.equals("") ) {
            Toast.makeText(this, "自分のユーザ名を入力し、\nIDを取得してください", Toast.LENGTH_LONG).show();
            intent = new Intent( RaderActivity.this, RegActivity.class );
            startActivity( intent );
            this.finish();
        }
        else if ( oppID.equals("") || oppName.equals("") ) {
            Toast.makeText( this, "相手のユーザIDを検索し、\nユーザ名を確認してください", Toast.LENGTH_LONG ).show();
            intent = new Intent( RaderActivity.this, LookActivity.class );
            startActivity( intent );
            this.finish();
        }

        // 色々初期化したりするよ
        initViewsAndItems();
        useSensors();
        useGPS();

        // 定期実行したいよ
        handler = new Handler(); // 定期実行するためのHandler
        // 5秒ごとにgetDistanceしてくれるはず
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 自分の位置情報を送信しつつ相手の位置情報を得る
                HttpCommunication httpCommunication = new HttpCommunication(
                        new HttpCommunication.AsyncTaskCallback() {
                            @Override
                            public void postExecute(LocationData result) {
                                oppLocationData = result;
                                getDistance(); // 相手の位置情報更新
                            }
                        }
                );
                httpCommunication.setID( myID, oppID );
                httpCommunication.setLocation( myLocationData );
                httpCommunication.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );

                handler.postDelayed( this, 5000 );
            }
        }, 5000);
    }


    protected void onStart() { // ⇔ onStop
        super.onStart();

        mSensorManager.registerListener(
                mSensorEventListener,
                mSensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ),
                SensorManager.SENSOR_DELAY_UI );
        mSensorManager.registerListener(
                mSensorEventListener,
                mSensorManager.getDefaultSensor( Sensor.TYPE_MAGNETIC_FIELD ),
                SensorManager.SENSOR_DELAY_UI );
    }

    protected void onStop() { // ⇔ onStart
        super.onStop();

        mSensorManager.unregisterListener( mSensorEventListener );
    }

    private double rad2deg(double radian) {
        return radian * (180f / Math.PI);
    }

    public double deg2rad(double degrees) {
        return degrees * (Math.PI / 180f);
    }


    // アクティビティがユーザー操作可能になる時
    @Override
    protected void onResume() {
        super.onResume();
        wfd.onResume();

        // LocationProviderのライフサイクルメソッド「onResume」を呼び出す必要があります。
        // 通常、Resumeが通知されると位置情報の収集が再開され、ステータスバーのGPSインジケーターが点灯します。
        if (this.locationProvider != null) {
            try {
                this.locationProvider.onResume();
            } catch (Exception e ) {
                ;
            }
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        if ( button_AR.isChecked() == true ) {
            // ARを強制終了
            RADER_VALUES.switchARMode( false );
            // カメラ開放
            mCamera.close();
            mCamera = null;
            // 背景差し替え(imageView表示)
            backgroundImageView.setVisibility( backgroundImageView.VISIBLE );
            // AR用メッセージ非表示
            linearLayout_ARMessages.setVisibility( View.GONE );
            // レーダー用メッセージ表示
            linearLayout_raderMessages.setVisibility( View.VISIBLE );

            button_AR.setChecked( false );

            // Toast表示
            Toast toast = Toast.makeText( getApplicationContext(),
                    "バックグラウンドから復帰。\nARモードを終了しました。", Toast.LENGTH_SHORT );
            toast.setGravity( Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0 );
            toast.show();
        }
    }


    @Override
    protected void onPause(){
        super.onPause();
        wfd.onPause();

        vibrator.cancel();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        wfd.onDestroy();

//        vibrator = null;
        handler.removeCallbacksAndMessages( null );
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // region 位置情報更新の管理（※LocationProviderに一任しており、ここではそのプロバイダーを生成するのみです）
    /**
     * LocationProviderを取得します。
     * @param locationListener システムの位置情報リスナーを指定してください。
     * @return
     */
    public ILocationProvider getLocationProvider(final LocationListener locationListener) {
        return new LocationProvider(this, locationListener);
    }


    void getDistance() {
        float[] results = new float[3];
        // 距離を計算 ///////////////////////////
        // results[0] : 距離（メートル）
        //        [1] : 始点から終点までの方位角
        //        [2] : 終点から始点までの方位角
        Location.distanceBetween( myLocationData.lat, myLocationData.lon,
                                    oppLocationData.lat, oppLocationData.lon, results);
//        Location.distanceBetween( lat, lon, 36.56815810607431, 140.6476289042621, results);
//        Log.d( "DISTANCE", "distance`getDistance = " + results[0] );

        if( results[1] < 0 ) {
            // 0～360度の値にする
            results[1] = 360f + results[1];
        }

        // 円グラフを回転
        RADER_VALUES.invalidateLocation( results[1], results[0] );

        // 距離メッセージ変更
        textView_distanceAR.setText( (int)results[0] + "m");
        if( results[0] <= 20 ) textView_DistanceMessage.setText("近いよ");
        else if( results[0] == 0 ) textView_DistanceMessage.setText("やばいよ");
        else textView_DistanceMessage.setText("遠いよ");

        // 精度メッセージ変更
        if( oppLocationData.acc <= 3 ) textView_AccuracyMessage.setText("精度良好かも");
        else if( oppLocationData.acc > 3 && oppLocationData.acc <= 10 ) textView_AccuracyMessage.setText("ふつうの精度");
        else if ( oppLocationData.acc >= 15 ) textView_AccuracyMessage.setText("精度ひどいよ");
//        else if ( data.acc >= 15 ) textView_AccuracyMessage.setText("不安な精度");
        else textView_AccuracyMessage.setText( "" );



        ////////////////////////////////////////////////////////////////////////////////////////////
        // 位置情報取得頻度を変化させる ////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        if ( results[0] <= RADER_VALUES.MAX_DISTANCE ) { // レーダー圏内/圏外で1秒/5秒を変化
            locationProvider.setLocationUpdateMinTime( true );
        } else {
            locationProvider.setLocationUpdateMinTime( false );
        }
        ////////////////////////////////////////////////////////////////////////////////////////////


        // デバッグ用にデータを表示したいんじゃよ
        // 表示形式を設定
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy'年'MM'月'dd'日'　kk'時'mm'分'ss'秒'");

        String stringInfo = "【 自分( "+ myID + " ) 】\n" +
                "lat : " + myLocationData.lat + "\n" +
                "lon : " + myLocationData.lon + "\n" +
                "acc : " + myLocationData.acc + "\n" +
                "【 相手( "+ oppID + " ) 】\n" +
                "lat : " + oppLocationData.lat + "\n" +
                "lon : " + oppLocationData.lon + "\n" +
                "acc : " + oppLocationData.acc + "\n" +
                "\n"+
                "【 距離 】 " + results[0] + "m\n" +
                "【 自 → 相 】 " + results[1] + "\n" +
                "【 相 → 自 】 " + results[2] + "\n" +
                "【 取得時刻 】\n" +
                "(自)" + sdf.format( new Date( myLocationData.gettime ) ) + "\n" +
                "(相)" + sdf.format( new Date( oppLocationData.gettime ) );

        Log.d("Location", myLocationData.gettime + " " + oppLocationData.gettime );
        textView_info.setText( stringInfo );

        if( results[0] <= 40 && flag_vibrator == true ) {
            // ここでバイブレーション///////////////////////////////////////////
            // 振動
            viberation( results[0] );
        }

    }

    // PermissionMangerちゃんに任せましょうね～
    @Override
    public void onRequestPermissionsResult( int requestCode,
                                            String permissions[], int[] grantResults ) {
        permissionManager.onRequestPermissionsResult( requestCode, permissions, grantResults );
    }

    // ARモードのon/off切り替えボタンがクリックされたとき
    public void onARSwitchButtonClicked(View v) {
        if( button_AR.isChecked() == true ) { // OFF → ONのとき

            // パーミッションを持っているか確認する
            if (PermissionChecker.checkSelfPermission(
                    RaderActivity.this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // パーミッションをリクエストする
                permissionManager.requestCameraPermission();
                return;
            }
            Log.d( "REQUEST PERMISSION", "パーミッション取得済み" );
            // ARモード開始
            startARMode();
        }
        else { // ON → OFFのとき
            // ARモード終了
            RADER_VALUES.switchARMode( false );
            // カメラ開放
            mCamera.close();
            mCamera = null;
            // 背景差し替え(imageView表示)
            backgroundImageView.setVisibility( backgroundImageView.VISIBLE );
            // AR用メッセージ非表示
            linearLayout_ARMessages.setVisibility( View.GONE );
            // レーダー用メッセージ表示
            linearLayout_raderMessages.setVisibility( View.VISIBLE );
        }
    }

    public void startARMode() {
        RADER_VALUES.switchARMode( true );
        // カメラ起動
        if ( textureView.isAvailable() == true ) {
            mCamera = new Camera2(textureView, this);
            mCamera.open();
        }

        // 背景差し替え（imageView非表示）
        backgroundImageView.setVisibility( backgroundImageView.INVISIBLE );
        // AR用メッセージ表示
        linearLayout_ARMessages.setVisibility( View.VISIBLE );
        // レーダー用メッセージ非表示
        linearLayout_raderMessages.setVisibility( View.GONE );

    }


    // [振動止める/つける]ボタン押下
    public void onVibeSwitchClicked( View v ) {
        if( button_Vibration.isChecked() == true ) { // OFF → ONのとき
            flag_vibrator = true;
        }
        else { // ON → OFFのとき
            flag_vibrator = false;
            // 現在動作中の振動も止める
            vibrator.cancel();
        }
    }

    // 距離を受け取って、距離に応じて振動させるメソッド
    private void viberation( double distance ) {

//        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        // 振動パターンいくつか（数字が大きくなるにつれて間隔短めに）
        long[] pattern1 = { 0, 500, 2000, 500, 2000, 500 }; // OFF/ON/OFF/ON...
        long[] pattern2 = { 0, 500, 1000, 500, 1000, 500 }; // OFF/ON/OFF/ON...
        long[] pattern3 = { 0, 500, 700, 500, 700, 500 }; // OFF/ON/OFF/ON...
        long[] pattern4 = { 0, 500, 500, 500, 500, 500 }; // OFF/ON/OFF/ON...
        long[] pattern5 = { 0, 500, 300, 500, 300, 500 }; // OFF/ON/OFF/ON...
        long[] pattern6 = { 0, 500, 100, 500, 100, 500 }; // OFF/ON/OFF/ON...

        // ここでバイブレーション///////////////////////////////////////////
        vibrator.cancel();      // 現在動作中の振動止める
        if( distance <= 3 ) {
            vibrator.vibrate(pattern6, -1);
            Log.d("viberation", "pattern6");
//            Toast.makeText( this, "pattern6", Toast.LENGTH_SHORT ).show();
        }
        else if( distance <= 5 ) {
            vibrator.vibrate(pattern4, -1);
            Log.d("viberation", "pattern4");
//            Toast.makeText( this, "pattern4", Toast.LENGTH_SHORT ).show();
        }
        else if( distance <= 10 ) {
            vibrator.vibrate(pattern2, -1);
            Log.d("viberation", "pattern2");
//            Toast.makeText( this, "pattern2", Toast.LENGTH_SHORT ).show();
        }
        else {
            vibrator.vibrate(pattern1, -1);
            Log.d("viberation", "pattern1");
//            Toast.makeText( this, "pattern1", Toast.LENGTH_SHORT ).show();
        }
    }


    // デバッグ用情報を表示するTextviewの表示、非表示切り替え
    public void onClickButtonInfo( View view ) {
        if( isInfoVisible == false ) {
            textView_info.setVisibility(View.VISIBLE);
            isInfoVisible = true;
        } else {
            textView_info.setVisibility(View.GONE);
            isInfoVisible = false;
        }
    }


    // デバッグ用
    // ShareCameraView
    public void onClickDistanceText( View view ) {

        // SkyWay（WebRTC）のアクティビティ
        Intent intent_find = new Intent(RaderActivity.this, ShareCameraViewActivity.class);
        startActivity(intent_find);

    }
}