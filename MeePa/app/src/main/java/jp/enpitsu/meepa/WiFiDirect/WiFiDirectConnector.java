package jp.enpitsu.meepa.WiFiDirect;

import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prily on 2016/11/25.
 */

public class WiFiDirectConnector implements WifiP2pManager.PeerListListener {
    static final String TAG = "wifi_direct_cnctr";
    WiFiDirect wfd;
    static boolean onConnecting = false;

    WiFiDirectConnector(WiFiDirect wfd){
        this.wfd = wfd;
    }

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        peers.clear();
        peers.addAll(peerList.getDeviceList());
        //Log.d(TAG,peers.toString());

        // if Device on Inviting or Connected, terminate.
        if (onConnecting || wfd.status.p2p_status.equals("Connected") || wfd.status.p2p_status.equals("Invited")){
            return;
        }

        // Search Opponent Device in Peer List
        for(int i=0; i<peers.size(); ++i){
            if (peers.get(i).deviceName.equals(wfd.getOpponentID())){
                onConnecting = true;

                WifiP2pDevice device = peers.get(i);
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;

                Log.d(TAG,"connect challenge");
                wfd.connect(config);
                return;
            }
        }

        // Can't Found Opponent Device
        Log.d(TAG,"can't found device");
    }

}
