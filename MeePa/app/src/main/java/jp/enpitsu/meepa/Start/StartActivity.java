package jp.enpitsu.meepa.Start;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import jp.enpitsu.meepa.Develop.SecretActivity;
import jp.enpitsu.meepa.R;
import jp.enpitsu.meepa.Rader.RaderActivity;
import jp.enpitsu.meepa.Registor.RegActivity;

import jp.enpitsu.meepa.Global.MeePaApp;

/**
 * Created by owner on 2016/10/25.
 */
public class StartActivity extends Activity {

    private Button      button_reg;
    private Button      button_meetUp;
    private EditText    editText_oppId;
    private TextView    textView_title;

    private MeePaApp app;
    private String selfID, oppID, oppName, oppMacAdr;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // レイアウトxmlとの結びつけ
        textView_title = (TextView)findViewById( R.id.textView_title );
        editText_oppId = (EditText)findViewById( R.id.editText_oppId );
        button_meetUp = (Button)findViewById( R.id.button_meetUp );
        button_reg = (Button)findViewById( R.id.button_createNewPIN );

        // リスナのセット
        textView_title.setOnClickListener(onClick_TextTileListener);
        button_meetUp.setOnClickListener(onClick_ButtonMeetUpListener);
        button_reg.setOnClickListener(onClick_ButtonRegListener);

        //Globalクラス利用
        app = (MeePaApp) this.getApplication();
        app.loadUserInfo();
        oppID = app.getOpponentUserId(); // 相手のIDを取得
        selfID = app.getSelfUserId(); // 自分のIDを取得
        if ( oppID.equals("") == false ) editText_oppId.setText( oppID ); // 相手のIDが前回起動時に入力済みの場合

    }

    //隠しボタン
    private View.OnClickListener onClick_TextTileListener =new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent_hide = new Intent(StartActivity.this, SecretActivity.class);
            try {
                startActivity(intent_hide);
            } catch (Exception e){
                Log.d("StartActivity","intent error to SecretActivity");
            }
        }
    };

    // [ Meet Up ]ボタン押下
    private View.OnClickListener onClick_ButtonMeetUpListener = new View.OnClickListener() {
        public void onClick(View v) {

            Intent intent = new Intent(StartActivity.this, RegActivity.class);

            // 自分のID登録等が済んでいるか
            if( selfID.equals("") ) { // 未登録ならID発行画面に

                try {
                    toast( "まずは自分のIDを発行してください", Toast.LENGTH_LONG, Gravity.CENTER );
                    startActivity(intent);
                    return;
                } catch (Exception e) {
                    Log.d("StartActivity", "intent error to RegActivity");
                }
            }

            // 相手のIDが入力済みか
            oppID = editText_oppId.getText().toString();
            if( oppID.equals("") ) { // 未入力なら何もしない

                toast("会いたい人のIDを入力してください", Toast.LENGTH_LONG, Gravity.BOTTOM);
                return;
            }
            else { // 入力済みなら
                // サーバと通信，入力された相手のIDに紐付いた情報が正常に取得できればレーダー画面へ
                checkOppIdAndMeetUp();
            }
        }
    };



    // [ Create New PIN ]ボタン押下
    private View.OnClickListener onClick_ButtonRegListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(StartActivity.this, RegActivity.class);
            try {
                startActivity(intent);
            } catch (Exception e) {
                Log.d("StartActivity", "intent error to MainActivity");
            }
        }
    };



    // サーバと通信，入力された相手のIDに紐付いた情報が正常に取得できればレーダー画面へ
    private void checkOppIdAndMeetUp() {
        HttpComLookFor httpComLookFor = new HttpComLookFor(
                new HttpComLookFor.AsyncTaskCallback() {
                    @Override
                    public void postExecute(String result) {
//                            // 検索結果として"0"が返ってきた場合，ふつうに出力すると"0"だけどbyteとかlengthとか見ると別のものもくっついてるっぽい
//                            // のでID検索一致0の場合の判定で妙なことしてます
//                            Log.d("result byte  ", result.getBytes() + ", " + "0".getBytes() );
//                            Log.d("result length", "" + result.length() );
//                            Log.d("result char  ", (int)result.charAt(0) + ", " + (int)result.charAt(1) + " : " + (int)'0' );

                        if ("error".equals(result)) { // サーバ側の不具合で検索に失敗した場合"error"が入ってる
                            editText_oppId.setText("接続エラー");
                        } else if ('0' == result.charAt(1)) { // reqIDに一致するIDのユーザ名が見つからなかった場合
                            editText_oppId.setText("失敗");
                        } else {
                            try {

                                // resultは「相手のユーザ名,MACアドレス」の形で返ってくる
                                oppName = result.substring(1, result.indexOf(",") + 0);
                                // 最初から","が現れるまでの部分文字列(なんか先頭文字に改行が入ってるっぽいのでインデックス1～を指定)
                                oppMacAdr = result.substring(result.indexOf(",") + 1, result.length()); // ","の次の文字から最後までの部分文字列
                                // グローバルクラスに保存
                                app.setOpponentUserInfo(oppName, oppID);
                                app.saveUserInfo();

                                // 問題なければレーダー画面に遷移
                                Intent intent = new Intent(StartActivity.this, RaderActivity.class);
                                try {
                                    startActivity(intent);
                                } catch (Exception e) {
                                    Log.d("StartActivity", "intent error to RaderActivity");
                                }

                            } catch (Exception e) {
                                editText_oppId.setText(result);
                                Log.d("@LookActivity", "postExecute -> error:" + e.toString());
                            }
                        }
                    }
                }
        );
        httpComLookFor.setUserInfo( oppID );
        httpComLookFor.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /*
     * Toast
     * 引数   msg         : 表示するメッセージ
     *        time_length : メッセージを表示する時間
     *        gravity     : メッセージ表示位置
     */
    private void toast( String msg, int time_length, int gravity ) {

        Toast toast = Toast.makeText( StartActivity.this, msg, time_length );
        toast.setGravity( gravity, 0, 0 );
        toast.show();
    }
}
