package jp.enpitsu.meepa.Develop;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import jp.enpitsu.meepa.Global.MeePaApp;
import jp.enpitsu.meepa.R;
import jp.enpitsu.meepa.Rader.LocationData;
import jp.enpitsu.meepa.WiFiDirect.WiFiDirect;
import jp.enpitsu.meepa.WiFiDirect.WiFiDirectEventListener;

/**
 * Created by Prily on 2016/12/01.
 */

public class SecretActivity extends Activity {

    public static final String TAG = "secret_activity";
    WiFiDirect wfd;

    // app
    MeePaApp app;

    // ui
    Button chat_button,loc_button;
    CompoundButton wfd_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret);
        app = (MeePaApp) getApplication();
        wfd = new WiFiDirect(SecretActivity.this);

        //test button
        chat_button = (Button) findViewById(R.id.sc_chat);
        chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wfd.sendChat("hoge");
            }
        });
        loc_button = (Button) findViewById(R.id.sc_loc);
        loc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wfd.sendGPSLocation(new LocationData(1.2,1.4,1.5));
            }
        });


        wfd.setWiFiDirectEventListener(new WiFiDirectEventListener() {
            @Override
            public void receiveChat(String str) {
                wfd.toast(str);
                Log.d(TAG,str);
            }

            @Override
            public void receiveGPSLocation(LocationData loc) {
                wfd.toast(loc.dump());
                Log.d(TAG,loc.dump());
            }
        });

        //for wifi direct
        wfd_button = (CompoundButton) findViewById(R.id.sc_wifi_direct);
        wfd.setCompoundButton(wfd_button);
    }

    @Override
    public void onResume(){
        super.onResume();
        wfd.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        wfd.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        wfd.onDestroy();
    }

}
