package jp.enpitsu.meepa.Registor;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



// HttpUrlConnection
// http://yukimura1227.blog.fc2.com/blog-entry-36.html

// * AsyncTask<[1], [2], [3]>
// [1] doInBackgroundメソッドの引数の型
// [2] onProgressUpdateメソッドの引数の型
// [3] onPostExecuteメソッドの引数の型(doInBackgroundメソッドの戻り値)

public class HttpComOnRegistor extends AsyncTask<Integer, Integer, String>
{
    String myName, wifi_key, myID;

    // Activiyへのコールバック用interface
    public interface AsyncTaskCallback {
        void postExecute(String result);
    }

    private AsyncTaskCallback callback = null;

    HttpComOnRegistor( AsyncTaskCallback callback ) {
        this.callback = callback;
    }


    // 自分のid(コード)<id>
    @Override
    protected String doInBackground(Integer... id) {

        StringBuilder uri = new StringBuilder(
                "http://ubermensch.noor.jp/enPiT/regist_user.php?" +
                        "name=" + myName + "&mac="+ wifi_key);

        Log.d("HttpURL", uri.toString());

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch( MalformedURLException e ) {
            Log.d("HttpRes", e.toString());
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
            Log.d("HttpRes", e.toString());
        } finally {
            urlConnection.disconnect();
        }

        //if( HttpStatus.SC_OK == status ) {
        if( !result.equals("") ) { // データを受け取れている場合
            try {
                if( result.equals("0") ) result = "error"; // 登録失敗
                myID = result;
            } catch( Exception e ) {
                Log.d("Http", e.toString());
            }
        }
        else
            myID = "error";

        return myID;
    }

    @Override
    protected void onPostExecute( String result ) {
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

    void setUserInfo( String user_name, String wifi_key ) {
        this.myName = user_name;
        this.wifi_key = wifi_key;
    }

}