package jp.enpitsu.meepa.WiFiDirect;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;

import java.io.IOException;

import jp.enpitsu.meepa.Rader.LocationData;

/**
 * Created by Prily on 2016/11/24.
 */

/*
# GroupOwner(Server)
    1.communicator creates handler(server side)
    2.handler creates ServerSocket.
    3.handler creates manager on thread pool.
    3.manager reads bytes from socket input buffer, and send Message using handler.
        or, user writes something bytes to socket output buffer.
    4.communicator receives Message.

# Client
    1.communicator creates handler(client side)
    2.handler connects to server.
    3.handler creates manager thread.
    3.manager reads bytes from socket i/o buffer, and send Message using handler.
    3.manager reads bytes from socket input buffer, and send Message using handler.
        or, user writes something bytes to socket output buffer.
    4.communicator receives Message.


 */


public class WiFiDirectCommunicator implements WifiP2pManager.ConnectionInfoListener, Handler.Callback {

    public static final String TAG = "wifi_direct_comnktor";

    static final int PORT = 8898;
    static final int TIMEOUT = 5000;
    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int MY_HANDLE = 0x400 + 2;

    WiFiDirect wfd;
    CommManager manager;
    WiFiDirectEventListener listener;
    HeartBeatTask heartBeatTask;

    WiFiDirectCommunicator(WiFiDirect wfd){
        this.wfd = wfd;
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
        wfd.setOpponentDeviceInformation(p2pInfo.toString());

        Thread handler = null;
        /*
         * The group owner accepts connections using a server socket and then spawns a
         * client socket for every client. This is handled by {@code
         * GroupOwnerSocketHandler}
         */

        if (p2pInfo.isGroupOwner) {
            Log.d(TAG, "Connected as group owner");
            wfd.setSocketDeviceSide("server");
            try {
                handler = new GroupOwnerSocketHandler(new Handler(this));
                handler.start();
            } catch (IOException e) {
                Log.d(TAG,
                        "Failed to create a server thread - " + e.getMessage());
                return;
            }
        } else {
            wfd.setSocketDeviceSide("client");
            Log.d(TAG, "Connected as peer");
            handler = new ClientSocketHandler(
                    new Handler(this),
                    p2pInfo.groupOwnerAddress);
            handler.start();
        }
    }


    // receive message
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                Pair<Byte,Object> decoded = Serializer.Decode(readBuf);
                switch(decoded.first){
                    case Serializer.CHAT:
                        String str = (String)decoded.second;
                        if (listener!=null) {
                            listener.receiveChat(str);
                        }
                        break;
                    case Serializer.LOCATION:
                        LocationData loc = (LocationData)decoded.second;
                        if (listener!=null) {
                            listener.receiveGPSLocation(loc);
                        }
                        break;
                    case Serializer.PING:
                        if(heartBeatTask!=null){
                            sendAck();
                        }
                        break;
                    case Serializer.ACK:
                        if(heartBeatTask!=null){
                            heartBeatTask.respond = true;
                            Log.d(TAG,"opponent is live");
                        }
                        break;
                    default:
                        Log.e(TAG,"unknown type");
                }
                break;

            case MY_HANDLE:
                Object obj = msg.obj;
                manager = (CommManager) obj;
                Log.d(TAG, "manager obj received");
                wfd.setSocketConnection("connected");
                startHeartBeat();
        }
        return true;
    }

    public void startHeartBeat(){
        Handler handler = new Handler();
        heartBeatTask = new HeartBeatTask(this,handler);
        handler.postDelayed(heartBeatTask,HeartBeatTask.INTERVAL*2);
    }

    // send message
    public void sendChat(String str){
        if (manager != null) {
            manager.write(Serializer.Encode(str));
        }else{
            socketUnconnected();
        }
    }

    public void sendGPSLocation(LocationData loc){
        if (manager != null) {
            manager.write(Serializer.Encode(loc));
        }else{
            socketUnconnected();
        }
    }

    public void sendPing(){
        if (manager != null) {
            manager.write(Serializer.ping);
        }else{
            socketUnconnected();
        }
    }

    public void sendAck(){
        if (manager != null) {
            manager.write(Serializer.ack);
        }else{
            socketUnconnected();
        }
    }

    protected void socketUnconnected(){
        Log.d(TAG,"manager is null");
        wfd.setSocketConnection("unconnected");
    }
    protected void socketNotResponding(){
        Log.d(TAG,"socket disconnect");
        wfd.setSocketConnection("disconnected");
    }
}
