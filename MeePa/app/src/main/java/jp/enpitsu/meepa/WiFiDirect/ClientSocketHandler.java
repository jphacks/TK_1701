
package jp.enpitsu.meepa.WiFiDirect;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientSocketHandler extends Thread {

    private static final String TAG = "wifi_direct_c_handler";
    private Handler handler;
    private CommManager manager;
    private InetAddress mAddress;

    public ClientSocketHandler(Handler handler, InetAddress groupOwnerAddress) {
        this.handler = handler;
        this.mAddress = groupOwnerAddress;
    }

    @Override
    public void run() {
        Log.d(TAG,"run");
        Socket socket = new Socket();
        try {
            /*
            note: setReuseAddress
            When closing a TCP connection, the connection may stay in a time-out state (usually
            referred to as TIME_WAIT state or 2 MSL wait state) for a period of time after the
            close of the connection. For applications that use the well-known socket address or port,
            sockets associated with a socket address or port may be unable to bind to the required
            SocketAddress if it is in the timeout state.
             */
            InetSocketAddress addr = new InetSocketAddress(mAddress.getHostAddress(),WiFiDirectCommunicator.PORT);
            socket.bind(null);
            socket.setReuseAddress(true);
            socket.connect(addr,WiFiDirectCommunicator.TIMEOUT);
            Log.d(TAG, "Launching the I/O handler");
            manager = new CommManager(socket, handler);
            new Thread(manager).start();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
    }
}
