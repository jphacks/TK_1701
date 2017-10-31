package jp.enpitsu.meepa.Start;

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

public class HttpComLookFor extends AsyncTask<Integer, Integer, String> {
    String oppID, reqID;



    // Activiyへのコールバック用interface
    public interface AsyncTaskCallback {
        void postExecute(String result);
    }
    private HttpComLookFor.AsyncTaskCallback callback = null;
    HttpComLookFor(HttpComLookFor.AsyncTaskCallback callback) {
        this.callback = callback;
    }

    // 相手のid
    @Override
    protected String doInBackground(Integer... id) {

        StringBuilder uri = new StringBuilder(
                "http://ubermensch.noor.jp/enPiT/search_user.php?" + "opponentcode=" + oppID);

        Log.d("HttpURL", uri.toString());

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.d("HttpRes", e.toString());
        }

        HttpURLConnection urlConnection = null;
        String result = "";
        try {
            // 接続用HttpURLConnectionオブジェクト作成
            urlConnection = (HttpURLConnection) url.openConnection();
            // リクエストメソッドの設定
            urlConnection.setRequestMethod("GET");

            urlConnection.setRequestProperty("charset", "utf8");

            // 結果の受信
            // レスポンスコードを受け取る
            final int responseCode = urlConnection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("invalid responcecode : " + responseCode);
            }

            // 受信データ処理
            result = recieveResult(urlConnection.getInputStream());

        } catch (IOException e) {
            Log.d("HttpRes", e.toString());
        } finally {
            urlConnection.disconnect();
        }

        if (!result.equals("")) { // データを受け取れている場合
            try {
                reqID = result; // resultが0の場合は検索結果が0ってことで
            } catch (Exception e) {
                Log.d("Http", e.toString());
            }
        } else
            reqID = "error";

        // 返値について
        // 【成功】           0(reqIDと一致する件数が0) or reqIDに対応するユーザ名
        // 【DB接続等に失敗】"error"
        return reqID;
    }

    @Override
    protected void onPostExecute(String result) {
        callback.postExecute(result);
        Log.d("Http", "onPostExecute");
    }


    String recieveResult(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        // 先頭行が空行以外の場合はエラー
        boolean error_responce = false;
        String s = "";
        String out = "";

        // 2行目以降
        while (null != (s = br.readLine())) {
            if (0 != out.length()) {
                out += s;
            } else {
                out += "\n" + s;
            }
        }
        if (error_responce) {
            throw new RuntimeException("failuer of analyze: " + out);
        }

        return out;
    }
    void setUserInfo(String user_name) {
        this.oppID = user_name;
    }
}


