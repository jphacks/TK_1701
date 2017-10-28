package jp.enpitsu.meepa.WiFiDirect;

import java.util.EventListener;

import jp.enpitsu.meepa.Rader.LocationData;

/**
 * Created by Prily on 2016/12/06.
 */

public interface WiFiDirectEventListener extends EventListener {
    public void receiveChat(String str);
    public void receiveGPSLocation(LocationData loc);
}
