package jp.enpitsu.meepa.Rader;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;


// HttpUrlConnection
// http://yukimura1227.blog.fc2.com/blog-entry-36.html

// * AsyncTask<[1], [2], [3]>
// [1] doInBackgroundメソッドの引数の型
// [2] onProgressUpdateメソッドの引数の型
// [3] onPostExecuteメソッドの引数の型(doInBackgroundメソッドの戻り値)

public class HttpCommunication extends AsyncTask<Integer, Integer, LocationData>
{
    String myID, oppID;

    double lat, lon, acc;
    long gettime;

    // Activiyへのコールバック用interface
    public interface AsyncTaskCallback {
        void postExecute(LocationData result);
    }

    private AsyncTaskCallback callback = null;

    HttpCommunication( AsyncTaskCallback callback ) {
        this.callback = callback;
    }


    // 自分のid(コード)<id>
    @Override
    protected LocationData doInBackground(Integer... id) {

        StringBuilder uri = new StringBuilder(
                "http://ubermensch.noor.jp/enPiT/get_gps.php?" +
                        "code=" + myID + "&opponentcode=" + oppID + "&alt=30" +
                        "&lat=" + lat + "&lan=" + lon + "&accuracy=" + acc + "&gettime="+ gettime );

        Log.d("HttpURL", uri.toString());

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch( MalformedURLException e ) {
            Log.d("Http", e.toString());
        }

        HttpURLConnection urlConnection = null;
        String result = "";
        try {
            // 接続用HttpURLConnectionオブジェクト作成
            urlConnection = (HttpURLConnection)url.openConnection();
            // リクエストメソッドの設定
            urlConnection.setRequestMethod("GET");

            urlConnection.setRequestProperty( "charset", "utf8" );

            // 結果の受信
            // レスポンスコードを受け取る
            final int responseCode = urlConnection.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK ) {
                throw new RuntimeException("invalid responce code : " + responseCode);
            }

            // 受信データ処理
            result = recieveResult( urlConnection.getInputStream() );


        } catch( IOException e ) {
            Log.d("Http", e.toString());
        } finally {
            urlConnection.disconnect();
        }



        LocationData data = new LocationData( 30, 30, 30, 30 );
        //if( HttpStatus.SC_OK == status ) {
        if( !result.equals("") ) { // データを受け取れている場合
            try {
                Log.d("Http", result);
                // ","で分割
                // items [0]名前, [1]高度, [2]緯度, [3]経度, [4]精度, [5]macアドレス, [6]データ取得時間
                String[] items = result.split(",");

                // 結果をdataに格納
                if( items.length == 7 && !items[6].equals("") ) {
                    // items[6] には「yyyy-mm-dd kk:mm:ss」の形で取得時間が入っている
                    String[] date_time = items[6].split(" "); // " "で分割→ [0] 年月日, [1]時刻 となる
                    String[] date = date_time[0].split("-");  // "-"で分割→ [0] 年, [1] 月, [2] 日
                    String[] time = date_time[1].split(":");  // ":"で分割→ [0] 時, [1] 分, [2] 秒

                    Calendar calendar = Calendar.getInstance();
                    calendar.clear();
                    calendar.set( Integer.parseInt( date[0] ),   // 年
                                  Integer.parseInt( date[1] )-1, // 月(Calendarクラスは0月～11月の12か月らしいぜ)
                                  Integer.parseInt( date[2] ),   // 日
                                  Integer.parseInt( time[0] ),   // 時
                                  Integer.parseInt( time[1] ),   // 分
                                  Integer.parseInt( time[2] )    // 秒
                                );
//                    Log.d("CALENDAR", "items[6]  : " + items[6] );
//                    Log.d("CALENDAR", "date_time : " + date_time[0] + ", " + date_time[1] );
//                    Log.d("CALENDAR", "date      : " + date[0] + ", " + date[1] + ", " + date[2] );
//                    Log.d("CALENDAR", "time      : " + time[0] + ", " + time[1] + ", " + time[2] );

                    data = new LocationData(
                            Double.parseDouble(items[2]), Double.parseDouble(items[3]), Double.parseDouble(items[4]), calendar.getTimeInMillis() );
                }
                else data = new LocationData(
                        Double.parseDouble(items[2]), Double.parseDouble(items[3]), Double.parseDouble(items[4]) );
            } catch( Exception e ) {
                Log.d("Http", e.toString());
            }
        }

        return data;
    }

    @Override
    protected void onPostExecute( LocationData result ) {
        callback.postExecute( result );

        Log.d( "Http", "onPostExecute" );
    }




    String recieveResult( InputStream in ) throws IOException
    {
        BufferedReader br = new BufferedReader( new InputStreamReader( in ) );

        // 先頭行が空行以外の場合はエラー
        boolean error_responce = false;
        String s = "";
        String out = "";

        // 2行目以降
        while( null != ( s = br.readLine() ) ) {
            if( 0 != out.length() ) {
                out += s;
            } else {
                out += "\n" + s;
            }
        }

        if( error_responce ) {
            throw new RuntimeException( "failuer of analyze: " + out );
        }

        return out;
    }

    void setID( String myID, String oppID ) {
        this.myID = myID;
        this.oppID = oppID;
    }

    void setLocation( LocationData locationData ) {
        this.lon = locationData.lon;
        this.lat = locationData.lat;
        this.acc = locationData.acc;
        this.gettime = locationData.gettime;
    }

}