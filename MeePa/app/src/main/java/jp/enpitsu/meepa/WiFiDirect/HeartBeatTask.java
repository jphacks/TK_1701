package jp.enpitsu.meepa.WiFiDirect;

import android.os.Handler;

/**
 * Created by Prily on 2016/12/06.
 */

public class HeartBeatTask implements Runnable {
    static boolean respond;
    static final int INTERVAL = 8000;
    WiFiDirectCommunicator communicator;
    Handler handler;

    HeartBeatTask(WiFiDirectCommunicator communicator, Handler handler){
        this.communicator = communicator;
        this.handler = handler;
        this.respond = true;
    }

    @Override
    public void run() {
        // send Heartbeat
        if (respond){
            communicator.sendPing();
            respond = false;
            handler.postDelayed(this,INTERVAL);
        }else{
            communicator.socketNotResponding();
        }
    }
}
