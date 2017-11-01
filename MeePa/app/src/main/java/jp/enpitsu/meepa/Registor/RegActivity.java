package jp.enpitsu.meepa.Registor;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import jp.enpitsu.meepa.Global.MeePaApp;
import jp.enpitsu.meepa.R;
import jp.enpitsu.meepa.Rader.ARObjects.OpenGLES20.TargetObject;
import jp.enpitsu.meepa.Start.StartActivity;

/*
 * Created by owner on 2016/09/25.
 */

public class RegActivity extends Activity {

    private TextView textView_complain;
    private EditText editText_userInfo;        //ユーザ名を入力するbox
    private Button button_cancel;
    private Button button_create;
    private Button button_share;
    private String user_name, myID;

    private String wifi_key = "paselow_cathy";
    private MeePaApp app;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // アクティビティのライフサイクル //////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        //xmlファイルとの紐づけ
        textView_complain = (TextView)findViewById( R.id.textView_complain );
        editText_userInfo = (EditText)findViewById( R.id.editText_userInfo );
        button_cancel = (Button)findViewById( R.id.button_cancel );
        button_create = (Button)findViewById( R.id.button_create );
        button_share  = (Button)findViewById( R.id.button_share );

        //各ボタンのClickListenerの宣言
        textView_complain.setOnClickListener(hideListener);
        button_cancel.setOnClickListener(onClick_buttonCancelListener);
        button_create.setOnClickListener(onClick_buttonCreateListener);
        button_share.setOnClickListener(onClick_buttonShareListener);
    }


    // 別のアクティビティから帰ってきた際も最新のデータを読み込みたい
    @Override
    protected void onResume() {
        super.onResume();

        // Globalクラスからのデータ読み込み
        app = (MeePaApp) this.getApplication();
        app.loadUserInfo();
        user_name = app.getSelfUserName();
        myID = app.getSelfUserId();
        if( myID.equals("") == false ) displayUserID(); // ユーザIDが発行済みの場合
        else if( user_name.equals("") == false ) displayImputUserName();// ユーザ名が未登録の場合

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // ボタン押下時の処理等 ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //隠しボタンの処理
    private View.OnClickListener hideListener= new View.OnClickListener() {
        public void onClick(View v) {
            app.resetUserInfo();
            user_name = "";
            myID = "";
            displayImputUserName();
        }
    };


    //キャンセルボタンの処理
    private View.OnClickListener onClick_buttonCancelListener= new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(RegActivity.this, StartActivity.class);
            try {
                startActivity(intent);
            } catch (Exception e){
                Log.d("RegActivity","intent error to StartActivity");
            }
        }
    };

    //ID発行ボタンの処理
    private View.OnClickListener onClick_buttonCreateListener = new View.OnClickListener() {
        public void onClick(View v) {
            // 入力されたユーザ名取得
            user_name = editText_userInfo.getText().toString().replaceAll(" |\n|\r|\t", "");
            if(TextUtils.isEmpty(user_name) == false && TextUtils.isEmpty(myID) == true) {
                HttpComOnRegistor httpComReg = new HttpComOnRegistor(
                        new HttpComOnRegistor.AsyncTaskCallback() {
                            @Override
                            public void postExecute(String result) {
                                // ID発行完了時の処理
                                myID = result.replaceAll("\n", "");
                                if ( myID.equals( "error" ) ) {
                                    toast("ID発行エラー\n" +
                                            "通信環境を見直し、再度[REGISTER]ボタンを\n押してみてください。",
                                            Toast.LENGTH_LONG, Gravity.CENTER );
                                    myID = null;
                                    return;
                                }
                                // Globalなデータ管理クラスに保存
                                app.setSelfUserInfo( user_name, myID );
                                app.saveUserInfo();
                                Log.d("PrilyClass_name",app.getSelfUserName());
                                Log.d("PrilyClass_id",app.getSelfUserId());

                                displayUserID();
                            }
                        }
                );
                httpComReg.setUserInfo(user_name, wifi_key);
                httpComReg.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            else {
                if (TextUtils.isEmpty(user_name) == true) {
                    toast("YourNameが入力されていません．", Toast.LENGTH_LONG, Gravity.CENTER);
                    Log.d("RegActivity", "UserName is null.");
                }
                else if(TextUtils.isEmpty(myID) == false ) {
                    toast("IDはすでに発行されています．", Toast.LENGTH_LONG, Gravity.CENTER);
                    Log.d("RegActivity", "UserID is showed already.");
                }
                else {
                    Log.d("RegActivity", "mystery error.");
                }
            }
        }
    };

    //ID共有ボタンの処理
    private View.OnClickListener onClick_buttonShareListener = new View.OnClickListener() {
        public void onClick(View v) {
            if(TextUtils.isEmpty(myID) == false) {
                try {
                    Intent intent_sha = new Intent();
                    intent_sha.setAction(Intent.ACTION_SEND);
                    intent_sha.setType("text/plain");
                    intent_sha.putExtra(Intent.EXTRA_TEXT,
                            "集GO!しよう(*・・)ノ\n" +
                            " 私( "+ user_name +" )のIDは " + myID + " です。\n" +
                            "集GO!を起動する→MeePa://shareID/" + myID
                    );
                    startActivity(intent_sha);
                } catch (Exception e) {
                    Log.d("ActionSend", "intent other app error");
                }
            }
            else {
                toast("あなたのIDを発行してください．", Toast.LENGTH_LONG, Gravity.CENTER);
                Log.d("RegActivity", "UserID is null.");
            }
        }
    };

    // ID発行前の画面表示
    private void displayImputUserName() {
        textView_complain.setText("User Name"); // 表示テキストの変更
        editText_userInfo.setText("");
        button_share.setVisibility(View.GONE); // Shareボタン除去
        button_cancel.setVisibility(View.VISIBLE); // cancelボタン表示
        button_create.setVisibility(View.VISIBLE); // createボタン表示
    }

    // ID発行後の画面表示
    private void displayUserID() {
        textView_complain.setText("Your ID"); // 表示テキストの変更
        editText_userInfo.setText(myID);
        button_share.setVisibility(View.VISIBLE); // Shareボタン表示
        button_cancel.setVisibility(View.GONE); // cancelボタン除去
        button_create.setVisibility(View.GONE); // createボタン除去
    }


    /*
     * Toast
     * 引数   msg         : 表示するメッセージ
     *        time_length : メッセージを表示する時間
     *        gravity     : メッセージ表示位置
     */
    private void toast( String msg, int time_length, int gravity ) {

        Toast toast = Toast.makeText( RegActivity.this, msg, time_length );
        toast.setGravity( gravity, 0, 0 );
        toast.show();
    }
}
