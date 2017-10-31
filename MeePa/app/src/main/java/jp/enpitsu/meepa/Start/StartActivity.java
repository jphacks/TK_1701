package jp.enpitsu.meepa.Start;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import jp.enpitsu.meepa.Develop.SecretActivity;
import jp.enpitsu.meepa.Lookfor.LookActivity;
import jp.enpitsu.meepa.R;
import jp.enpitsu.meepa.Registor.RegActivity;

/**
 * Created by owner on 2016/10/25.
 */
public class StartActivity extends Activity {

    Button btn_hide;
    ImageButton imbtn_registmode;
    ImageButton imbtn_searchmode;
    TextView first_msg,text_get,text_lets;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btn_hide = (Button)findViewById(R.id.button);
        imbtn_registmode = (ImageButton)findViewById(R.id.imageButton_regist);
        imbtn_searchmode = (ImageButton)findViewById(R.id.imageButton_search);
        first_msg = (TextView)findViewById(R.id.first_msg);
        text_get = (TextView)findViewById(R.id.text_getid);
        text_lets = (TextView)findViewById(R.id.text_lets);

        //フォント適用
        first_msg.setTypeface( Typeface.createFromAsset( getAssets(), "fonts/FLOPDesignFont.ttf"), Typeface.NORMAL );
        text_get.setTypeface( Typeface.createFromAsset( getAssets(), "fonts/FLOPDesignFont.ttf"), Typeface.NORMAL );
        text_lets.setTypeface( Typeface.createFromAsset( getAssets(), "fonts/FLOPDesignFont.ttf"), Typeface.NORMAL );

        btn_hide.setOnClickListener(hideListener);
        imbtn_registmode.setOnClickListener(regListener);
        imbtn_searchmode.setOnClickListener(seaListener);
    }

    //隠しボタン
    private View.OnClickListener hideListener =new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent_hide = new Intent(StartActivity.this, SecretActivity.class);
            try {
                startActivity(intent_hide);
            } catch (Exception e){
                Log.d("StartActivity","intent error to SecretActivity");
            }
        }
    };

    //ユーザ登録ボタンの処理
    private View.OnClickListener regListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent_reg = new Intent(StartActivity.this, RegActivity.class);
            try {
                startActivity(intent_reg);
            } catch (Exception e){
                Log.d("StartActivity","intent error to RegActivity");
            }
        }
    };

    //検索モードへボタンの処理
    private View.OnClickListener seaListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent_sea = new Intent(StartActivity.this, LookActivity.class);
            try {
                startActivity(intent_sea);
            } catch (Exception e) {
                Log.d("StartActivity", "intent error to MainActivity");
            }
        }
    };
}
