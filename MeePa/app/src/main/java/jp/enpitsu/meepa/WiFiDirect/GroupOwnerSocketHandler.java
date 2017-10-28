
package jp.enpitsu.meepa.WiFiDirect;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The implementation of a ServerSocket handler. This is used by the wifi p2p
 * group owner.
 */
public class GroupOwnerSocketHandler extends Thread {

    ServerSocket server_socket = null;
    private final int THREAD_COUNT = 10;
    private Handler handler;
    private static final String TAG = "wifi_direct_s_handler";

    public GroupOwnerSocketHandler(Handler handler) throws IOException {
        try {
            server_socket = new ServerSocket();
            server_socket.setReuseAddress(true);
            server_socket.bind(new InetSocketAddress(WiFiDirectCommunicator.PORT));
            this.handler = handler;
            Log.d(TAG, "Socket Started");
        } catch (IOException e) {
            e.printStackTrace();
            pool.shutdownNow();
            throw e;
        }

    }

    /**
     * A ThreadPool for client server_sockets.
     */
    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            THREAD_COUNT, THREAD_COUNT, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    @Override
    public void run() {
        Log.d(TAG,"run");
        while (true) {
            try {
                // A blocking operation. Initiate a GPSCommManager instance when
                // there is a new connection
                //pool.execute(new GPSCommManager(server_socket.accept(), handler));
                Socket socket = server_socket.accept(); // blocking method. The Program stops until a connection request is received from the client.
                new Thread(new CommManager(socket, handler)).start();
                Log.d(TAG, "Launching the I/O handler");

            } catch (IOException e) {
                try {
                    if (server_socket != null && !server_socket.isClosed())
                        Log.d(TAG,"server_socket close");
                        server_socket.close();
                } catch (IOException ioe) {
                    Log.d(TAG,"server_socket close failed");
                }
                Log.d(TAG,"pool shutdown");
                e.printStackTrace();
                pool.shutdownNow();
                break;
            }
        }
    }

}
