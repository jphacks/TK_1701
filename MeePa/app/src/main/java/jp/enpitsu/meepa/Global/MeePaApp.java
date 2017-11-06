package jp.enpitsu.meepa.Global;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import jp.enpitsu.meepa.Rader.LocationData;

/**
 * Created by Prily on 2016/11/24.
 */

class UserInfo {

    public String name,id;
    public String macAddress;
    public LocationData locationData;

    public UserInfo(String name, String id, LocationData loc){
        this.name = name;
        this.id = id;
        this.macAddress = "";
        this.locationData = loc;
    }

    public UserInfo(String name, String id, String macAdr, LocationData loc){
        this.name = name;
        this.id = id;
        this.macAddress = macAdr;
        this.locationData = loc;
    }

}

public class MeePaApp extends android.app.Application {
    private final String TAG = "MeePaApp";

    private UserInfo self = new UserInfo("","", "", new LocationData(0,0,0));
    private UserInfo opponent = new UserInfo("","", "", new LocationData(0,0,0));

    /* -----------------------------------------------------

    life cycle

    ------------------------------------------------------- */

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"SyuGo!");
        loadUserInfo();
    }

    @Override
    public void onTerminate() {
        //呼ばれません（かなしい）
        Log.d(TAG,"kaisan...");
        super.onTerminate();
    }

    /* -----------------------------------------------------

    save & load

    ------------------------------------------------------- */

    public void saveUserInfo (){
        SharedPreferences data = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString("self_name",self.name);
        editor.putString("self_id",self.id);
        editor.putString("self_macAddress",self.macAddress);
        editor.putString("opp_name",opponent.name);
        editor.putString("opp_id",opponent.id);
        editor.putString("opp_macAddress",opponent.macAddress);
        editor.apply();
        Log.d(TAG,"data save");
        dump();
    }

    public void loadUserInfo(){
        SharedPreferences data = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        self.name = data.getString("self_name","");
        self.id = data.getString("self_id","");
        self.macAddress = data.getString("self_macAddress","");
        opponent.name = data.getString("opp_name","");
        opponent.id = data.getString("opp_id","");
        opponent.macAddress = data.getString("opp_macAddress","");
        Log.d(TAG,"data load");
        dump();
    }

    public void resetUserInfo(){
        self = new UserInfo("","",new LocationData(0,0,0));
        opponent = new UserInfo("","",new LocationData(0,0,0));
        SharedPreferences data = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        data.edit().clear().commit();
        Log.d(TAG,"data reset");
    }


    /* -----------------------------------------------------

    getter & setter

    ------------------------------------------------------- */

    public void setSelfUserInfo(String name, String id){
        self.name = name;
        self.id = id;
    };

    public void setSelfUserInfo(String name, String id, String macAdr){
        self.name = name;
        self.id = id;
        self.macAddress = macAdr;
    };


    public void setSelfLocationData( LocationData locationData ) {
        self.locationData = locationData;
    }

    public String getSelfUserName(){
        return self.name;
    }

    public String getSelfUserId(){
        return self.id;
    }

    public String getSelfUserMacAddress() { return self.macAddress; }

    public LocationData getSelfLocationData() { return self.locationData; }

    public void setOpponentUserInfo(String name, String id){
        opponent.name = name;
        opponent.id = id;
    };

    public void setOpponentUserInfo(String name, String id, String macAdr){
        opponent.name = name;
        opponent.id = id;
        opponent.macAddress = macAdr;
    };

    public void setOpponentLocationData( LocationData locationData ) {
        opponent.locationData = locationData;
    }

    public String getOpponentUserName(){
        return opponent.name;
    }

    public String getOpponentUserId(){
        return opponent.id;
    }

    public String getOpponentUserMacAddress() { return opponent.macAddress; }

    public LocationData getOpponentLocationData() { return opponent.locationData; }

    public void dump(){
        Log.d(TAG,self.name);
        Log.d(TAG,self.id);
        Log.d(TAG,opponent.name);
        Log.d(TAG,opponent.id);
    }
}
