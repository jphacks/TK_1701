package jp.enpitsu.meepa.Lookfor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import jp.enpitsu.meepa.Global.MeePaApp;
import jp.enpitsu.meepa.R;
import jp.enpitsu.meepa.Rader.RaderActivity;

public class LookActivity extends Activity {

    private Button search;
    private ImageButton find;
    private TextView name,id2, e_message,text1,word,textView3;
    private EditText id;
    private String oppName, macAdr, reqID;
    private MeePaApp app;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look);

        id2 = (TextView) findViewById(R.id.id_show);
        name = (TextView) findViewById(R.id. name_show);
        e_message = (TextView) findViewById(R.id.error);
        search = (Button) findViewById(R.id.button1);
        find = (ImageButton) findViewById(R.id.button2);
        id = (EditText) findViewById(R.id.id_enter);
        search.setOnClickListener(searchListener);
        find.setOnClickListener(findListener);
        text1 = (TextView)findViewById(R.id.text1);
        word = (TextView)findViewById(R.id.word);
        textView3 = (TextView)findViewById(R.id.textView3);
        app = (MeePaApp) this.getApplication();

        //Globalクラス適用
        app.loadUserInfo();
        oppName = app.getOpponentUserName();
        reqID = app.getOpponentUserId();
        id.setText(reqID);
        id2.setText(reqID);
        name.setText(oppName);

        // URLから暗黙的intentで起動
        Intent intent = getIntent();
        Uri uri = intent.getData();  // uriの取得
        if( uri != null ) { // nullでないとき
            // uriは，「 syugo://shareID/[reqID] 」の形で来る
            String[] parse = uri.toString().split("/"); // "/"で分割
            reqID = parse[ parse.length-1 ]; // 一番最後の要素が共有されたID
        }
        id.setText( reqID );


        //フォント設定
        id2.setTypeface( Typeface.createFromAsset( getAssets(), "fonts/FLOPDesignFont.ttf"), Typeface.NORMAL );
        name.setTypeface( Typeface.createFromAsset( getAssets(), "fonts/FLOPDesignFont.ttf"), Typeface.NORMAL );
        e_message.setTypeface( Typeface.createFromAsset( getAssets(), "fonts/FLOPDesignFont.ttf"), Typeface.NORMAL );
        search.setTypeface( Typeface.createFromAsset( getAssets(), "fonts/FLOPDesignFont.ttf"), Typeface.NORMAL );
        id.setTypeface( Typeface.createFromAsset( getAssets(), "fonts/FLOPDesignFont.ttf"), Typeface.NORMAL );
        text1.setTypeface( Typeface.createFromAsset( getAssets(), "fonts/FLOPDesignFont.ttf"), Typeface.NORMAL );
        word.setTypeface( Typeface.createFromAsset( getAssets(), "fonts/FLOPDesignFont.ttf"), Typeface.NORMAL );
        textView3.setTypeface( Typeface.createFromAsset( getAssets(), "fonts/FLOPDesignFont.ttf"), Typeface.NORMAL );

// ボタンの幅，高さが決定してから幅=高さに揃える
        // ViewTreeObserverを利用
        // 参考 : http://tech.admax.ninja/2014/09/17/how-to-get-the-height-and-width-of-the-view/
        //        https://anz-note.tumblr.com/post/96096731156/androidで動的に縦幅あるいは横幅に合わせて正方形のviewを作成したい
        final ViewTreeObserver observer = find.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // ボタンの幅=高さにする
//                        Log.d("btn", btn_share.getWidth() + ", " + btn_share.getHeight());
                        ViewGroup.LayoutParams params = find.getLayoutParams();
                        // 短辺の長さに長辺を揃える
                        if (find.getWidth()/3 < find.getHeight()/3) {
                            params.height = find.getWidth() / 3;
                            params.width = find.getWidth() / 3;
                        }
                        else{
                            params.width = find.getHeight()/3;
                            params.height = find.getHeight()/3;
                        }

                        find.setLayoutParams( params );
                        find.setLayoutParams( params );

                        removeOnGlobalLayoutListener(find.getViewTreeObserver(), this);
                    }
                });
    }
    // onGlobalLayout()が1回目以降呼ばれないようにリス名を外す
    private static void removeOnGlobalLayoutListener(ViewTreeObserver observer, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (observer == null) {
            return ;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            observer.removeGlobalOnLayoutListener(listener);
        } else {
            observer.removeOnGlobalLayoutListener(listener);
        }
    }
    // コードでユーザー検索"name,mac"
    private View.OnClickListener searchListener = new View.OnClickListener() {
        public void onClick(View v) {
            e_message.setText( "" );
            if ( TextUtils.isEmpty( id.getText().toString() ) == false ) { // 検索するIDが入力されている場合
                reqID = id.getText().toString(); // 相手ID入力テキストボックスから相手のID取得

                if ( reqID.equals( app.getSelfUserId() ) ) { // 自分のIDが入力されている場合
                    e_message.setText("自分のIDが入力されています。正しいIDを入力して下さい。");
                    return;
                }

                id2.setText(reqID); // 入力された相手IDをメッセージ部分に表示
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
                                    name.setText("接続エラー");
                                } else if ('0' == result.charAt(1)) { // reqIDに一致するIDのユーザ名が見つからなかった場合
                                    name.setText("失敗");
                                } else {
                                    try {
                                        // resultは「相手のユーザ名,MACアドレス」の形で返ってくる
                                        oppName = result.substring(1, result.indexOf(",") + 0);
                                        // 最初から","が現れるまでの部分文字列(なんか先頭文字に改行が入ってるっぽいのでインデックス1～を指定)
                                        macAdr = result.substring(result.indexOf(",") + 1, result.length());
                                        // ","の次の文字から最後までの部分文字列
                                        name.setText(oppName); // [検索結果]相手のユーザ名を表示
                                        app.setOpponentUserInfo(oppName, reqID);
                                        app.saveUserInfo();
                                        Log.d("PrilyClass_name", app.getOpponentUserName());
                                        Log.d("PrilyClass_id", app.getOpponentUserId());
                                    } catch (Exception e) {
                                        name.setText(result);
                                        Log.d("@LookActivity", "postExecute -> error:" + e.toString());
                                    }
                                }
                            }
                        }
                );
                httpComLookFor.setUserInfo(reqID);
                httpComLookFor.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } // if ( TextUtils.isEmpty(oppName) == false )...
            else {
                e_message.setText( "IDを入力してください。" );
            }
        }  // onClick
    };

    //検索ボタン押してマップ画面へ
    private View.OnClickListener findListener = new View.OnClickListener() {
        public void onClick(View v) {
            if(TextUtils.isEmpty(oppName) == false) {
            try {
                Intent intent_find = new Intent(LookActivity.this, RaderActivity.class);
                intent_find.putExtra("reqID", reqID);         // 相手ID
                intent_find.putExtra("macAdr", macAdr);       // MACアドレス
                intent_find.putExtra("oppName", oppName);     // 相手のユーザ名
                startActivity(intent_find);
            } catch (Exception e) {
                Log.d("LookActivity", "intent error RaderActivity");
            }
        }
        else{
            e_message.setText("正しいIDを入力して下さい");
                Log.d("LookActivity","UserName is null.");
        }
        }

    };
}
