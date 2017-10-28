/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.enpitsu.meepa.WiFiDirect;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jp.enpitsu.meepa.Global.MeePaApp;
import jp.enpitsu.meepa.R;
import jp.enpitsu.meepa.Rader.LocationData;

import static android.os.Looper.getMainLooper;


/**
 * An activity that uses WiFi Direct APIs to discover and connect with available
 * devices. WiFi Direct APIs are asynchronous and rely on callback mechanism
 * using interfaces to notify the application of operation success or failure.
 * The application should also register a BroadcastReceiver for notification of
 * WiFi state related events.
 */

/*
note :
unknown -> available -> discovering -> found -> socket connecting -> connected

Wifi P2p
    enable or disable. check it myself

P2p Status
    available/unavailable -> invited -> connected/failed. be notified by updateThisDevice()

Socket Status
    connected or unconnected. Communicator notifies us.

 */
public class WiFiDirect {

    public class WiFiDirectStatus {
        public boolean isWifiP2pEnabled = false;
        public String selfDeviceName="unknown", opponentDeviceName = "unknown";
        public String selfDeviceStatus="unknown",opponentDeviceStatus="unknown";
        public String p2p_status; //available, connected, other
        public String socket_status; //not connected, connected
        public String socket_side; //server , client
    }

    // For Debug
    public static final String TAG = "wifi_direct";

    // app, parent activity
    MeePaApp app;
    Activity activity;

    // Instances
    private WifiP2pManager manager;
    private Channel channel;
    private BroadcastReceiver receiver = null;
    public WiFiDirectConnector connector;
    public WiFiDirectCommunicator communicator;

    // Status
    WiFiDirectStatus status = new WiFiDirectStatus();

    // Button
    CompoundButton wfd_button; // Manipulated by parent Activity
    static AlphaAnimation alphaAnim;

    // Intent Filter
    private final IntentFilter intentFilter = new IntentFilter();

    // Toast
    Toast wfd_toast;


    // TextView
    TextView wfd_textView; // Manipulated by parent Activity

    public WiFiDirect(Activity activity) {
        // app, activity
        this.activity = activity;
        app = (MeePaApp) activity.getApplication();

        // Instantiate
        connector = new WiFiDirectConnector(this);
        communicator = new WiFiDirectCommunicator(this);

        // Register the Intent Filter
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        // Get manager & channel Instance
        manager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(activity, getMainLooper(), null);

        // Initialize IDs
        updateIDs();

        Log.d(TAG,"hello");

        //app.setOpponentUserInfo("enpitsublue","n8y6");
        //app.setSelfUserInfo("enpitsugreen","bh96");
        //app.saveUserInfo();
    }

    public void onResume() {
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        activity.registerReceiver(receiver, intentFilter);
    }

    public void onPause() {
        activity.unregisterReceiver(receiver);
    }

    public void onDestroy(){
        endConnection();
    }

    /* -----------------------------------------------------------
    Catch status by BroadcastReceiver : notify us wifip2p & device status
    -------------------------------------------------------------- */

    protected void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        status.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    protected void updateThisDevice(WifiP2pDevice device) {
        // status
        String s = getP2pDeviceStatus(device.status);
        status.p2p_status = s;

        // notify
        if(s.equals("Connected")){
            toast("P2P接続完了…");
        }else{
            // Turn off the light
            controlWfdButton(ButtonCmd.OFF);
        }
    }

    /* -----------------------------------------------------------
    Status for Connector : search opponent device, call connect()
    -------------------------------------------------------------- */

    protected String getOpponentID(){
        return status.opponentDeviceName;
    }

    /* -----------------------------------------------------------
    Catch status by Communicator : socket communication
    -------------------------------------------------------------- */

    protected void setOpponentDeviceInformation(String information_str){
        status.opponentDeviceStatus = information_str;
    }

    protected void setSocketDeviceSide(String str){
        status.socket_side = str;
        toast(str+"として接続したよ");
    }

    protected void setSocketConnection(String str){
        status.socket_status = str;
        if(str.equals("connected")){
            toast("Socket接続完了！");
            // Turn on the light
            controlWfdButton(ButtonCmd.ON);
        }else if (str.equals("unconnected")){
            // Turn off the light
            controlWfdButton(ButtonCmd.OFF);
        }else if (str.equals("disconnected")){
            toast("相手端末と通信隔絶…");
            endConnection();
        }
    }


    /* -----------------------------------------------------------

    Connection Managers

    -------------------------------------------------------------- */

    protected void resetData() {

    }

    private void discover(){
        controlWfdButton(ButtonCmd.FLASH);
        // discover
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG,"p2p discover(try) success");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.i(TAG,"p2p discover(try) failure" + reasonCode);
            }
        });
    }

    protected void connect(WifiP2pConfig config) {
        toast("みつけた！");
        manager.connect(channel, config, new ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                Log.i(TAG,"p2p connect(try) success");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.i(TAG,"p2p connect(try) failure" + reasonCode);
            }
        });
    }

    private void disconnect(){
        controlWfdButton(ButtonCmd.OFF);
        manager.removeGroup(channel, new ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.i(TAG,"p2p disconnect failure" + reasonCode);
            }

            @Override
            public void onSuccess() {
                Log.i(TAG,"p2p disconnect success");
            }
        });
    }


    /* -----------------------------------------------------------
    Methods for Management Device Information
    -------------------------------------------------------------- */

    private void updateIDs(){
        status.opponentDeviceName = app.getOpponentUserName() + "_" + app.getOpponentUserId();
        status.selfDeviceName = app.getSelfUserName() + "_" + app.getSelfUserId();
        setP2pDeviceName(status.selfDeviceName);
        toast(status.selfDeviceName + "->" + status.opponentDeviceName);
    }

    private void setP2pDeviceName(String devName) {
        try {
            Class[] paramTypes = new Class[3];
            paramTypes[0] = Channel.class;
            paramTypes[1] = String.class;
            paramTypes[2] = ActionListener.class;
            Method setDeviceName = manager.getClass().getMethod(
                    "setDeviceName", paramTypes);
            setDeviceName.setAccessible(true);

            Object arglist[] = new Object[3];
            arglist[0] = channel;
            arglist[1] = devName;
            arglist[2] = new ActionListener() {

                @Override
                public void onSuccess() {
                    Log.d(TAG,"setDeviceName succeeded");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(TAG,"setDeviceName failed");
                }
            };

            setDeviceName.invoke(manager, arglist);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static String getP2pDeviceStatus(int deviceStatus) {
        Log.d(TAG, "Peer status :" + deviceStatus);
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";
        }
    }

    /* -----------------------------------------------------------
    Action
    -------------------------------------------------------------- */

    public void openWiFiSettings(){
        if (manager != null && channel != null) {
            activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        } else {
            Log.e(TAG, "Activity : channel or manager is null");
        }
    }

    public void startConnection(){
        if (!status.isWifiP2pEnabled) {
            toast("端末のWifiDirect機能がオフになっています．設定してください！");
            openWiFiSettings();
            controlWfdButton(ButtonCmd.OFF);
            return;
        }
        connector.onConnecting = false;
        toast("相手の端末を検索中…");
        // Flashing the light
        discover();
    }

    public void endConnection() {
        disconnect();
        resetData();
    }

    /* -----------------------------------------------------------
    Notify
    -------------------------------------------------------------- */

    public void toast(String str){
        if(wfd_toast!=null) {
            wfd_toast.cancel();
        }
        wfd_toast = Toast.makeText(activity, str, Toast.LENGTH_SHORT);
        wfd_toast.setGravity(Gravity.CENTER,0,0);
        wfd_toast.show();

        if ( wfd_textView != null ) {
            String existText = wfd_textView.getText().toString();
            if ( !existText.equals("") ) existText += "\n";
            wfd_textView.setText( existText + str );
        }
    }

    /* -----------------------------------------------------------
    Button
    -------------------------------------------------------------- */

    public void setCompoundButton(CompoundButton button){
        wfd_button = button;
        wfd_button.setOnCheckedChangeListener(wfd_button_listener);
    }

    CompoundButton.OnCheckedChangeListener wfd_button_listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton view, boolean isChecked) {
            if (isChecked){
                if( wfd_textView != null )
                    wfd_textView.setVisibility( View.VISIBLE );
                startConnection();
            }else {
//                wfd_textView.setVisibility( View.GONE );
                endConnection();
            }
        }
    };

    private enum ButtonCmd {
        ON,
        OFF,
        FLASH;
    }

    private void controlWfdButton(ButtonCmd command){
        if(wfd_button != null) {
            switch (command) {
                case ON:
                    if (alphaAnim!=null) {
                        alphaAnim.cancel();
                    }
                    wfd_button.clearAnimation();
                    wfd_button.setChecked(true);
                    break;
                case OFF:
                    if (alphaAnim!=null) {
                        alphaAnim.cancel();
                    }
                    wfd_button.clearAnimation();
                    wfd_button.setChecked(false);
                    break;
                case FLASH:
                    alphaAnim = new AlphaAnimation(1f, 0.5f);
                    alphaAnim.setDuration(300);
                    alphaAnim.setRepeatCount(Animation.INFINITE);
                    alphaAnim.setRepeatMode(Animation.REVERSE);
                    wfd_button.startAnimation(alphaAnim);
            }
        }
    }

    /* -----------------------------------------------------------
    Communication
    -------------------------------------------------------------- */

    public void sendChat(String str){
        if(communicator!=null){
            communicator.sendChat(str);
        }
    }

    public void sendGPSLocation(LocationData loc){
        if(communicator!=null){
            communicator.sendGPSLocation(loc);
        }
    }

    public void setWiFiDirectEventListener(WiFiDirectEventListener listener){
        if(communicator!=null){
            communicator.listener = listener;
        }
    }







    /* -----------------------------------------------------------
    TextView
    -------------------------------------------------------------- */

    public void setTextView( TextView textView ){
        wfd_textView = textView;
        wfd_textView.setOnClickListener( wfd_textView_listener );
    }

    private View.OnClickListener wfd_textView_listener = new View.OnClickListener() {
        public void onClick(View v) {
            if( wfd_button.isChecked() == false ) {
                wfd_textView.setVisibility( View.GONE );
                wfd_textView.setText( "WifiDirect OFF時に\n" +
                        "このテキストエリアをタップすることで非表示にできます。\n" );
            }
        }
    };
}
