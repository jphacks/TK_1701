
package jp.enpitsu.meepa.WiFiDirect;


import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Handles reading and writing of messages with socket buffers. Uses a Handler
 * to post messages to UI thread for UI updates.
 */
public class CommManager implements Runnable {

    private Socket socket = null;
    private Handler handler;
    private InputStream iStream;
    private OutputStream oStream;
    private static final String TAG = "wifi_direct_commanager";

    public CommManager(Socket socket, Handler handler) {
        this.socket = socket;
        this.handler = handler;
        Log.d(TAG,"instantiate");
    }


    @Override
    public void run() {
        Log.d(TAG,"run");
        try {
            iStream = socket.getInputStream();
            oStream = socket.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytes;
            handler.obtainMessage(WiFiDirectCommunicator.MY_HANDLE, this)
                    .sendToTarget();
            Log.d(TAG,"send manager");

            while (true) {
                try {
                    // Read from the InputStream
                    bytes = iStream.read(buffer);
                    if (bytes == -1) {
                        break;
                    }

                    // Send the obtained bytes to the UI Activity
                    Log.d(TAG, "received :" + String.valueOf(buffer));
                    handler.obtainMessage(WiFiDirectCommunicator.MESSAGE_READ,bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(byte[] buffer) {
        try {
            oStream.write(buffer);
            oStream.flush();
            Log.d(TAG,"write to buffer" + String.valueOf(buffer));
        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
        }
    }

}
