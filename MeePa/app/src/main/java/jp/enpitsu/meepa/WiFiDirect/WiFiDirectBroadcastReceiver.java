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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

/**
 * A BroadcastReceiver that notifies of important wifi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    // for debug
    public static final String TAG = "wifi_direct_broadcaster";

    private WifiP2pManager manager;
    private Channel channel;
    private WiFiDirect wfd;
    private WiFiDirectConnector connector;
    private WiFiDirectCommunicator communicator;

    /**
     * @param manager WifiP2pManager system service
     * @param channel Wifi p2p channel
     * @param wfd wfd associated with the receiver
     */
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
                                       WiFiDirect wfd) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.wfd = wfd;
        connector = wfd.connector;
        communicator = wfd.communicator;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) { // Called if WIFI On/Off changed.

            // UI update to indicate wifi p2p status.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
                wfd.setIsWifiP2pEnabled(true);
            } else {
                wfd.setIsWifiP2pEnabled(false);
                wfd.resetData();
            }

            Log.d(TAG, "onReceive : P2P state changed - " + state);

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) { // Called if this device found something other device.

            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling wfd is notified with a
            // callback on PeerListListener.onPeersAvailable()

            if (manager == null) {
                return;
            }

            manager.requestPeers(channel, connector);

            Log.d(TAG, "onReceive : P2P peers changed");

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) { // Called if this device connect/disconnect opponent device.

            if (manager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // we are connected with the other device, request connection
                // info to find group owner IP

                manager.requestConnectionInfo(channel, communicator);

            } else {
                // It's a disconnect
                //wfd.resetData();
            }

            Log.d(TAG,"BroadCaster : P2P Connection Changed");

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) { // Called if changed this device status

            WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            wfd.updateThisDevice(device);

            Log.d(TAG,"onReceive : This Device Status is Changed");
        }
    }
}
