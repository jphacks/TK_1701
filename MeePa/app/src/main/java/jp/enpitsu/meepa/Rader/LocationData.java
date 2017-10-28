package jp.enpitsu.meepa.Rader;


import java.nio.ByteBuffer;

/**
 * Created by soniyama on 2016/08/29.
 */
public class LocationData {
    double lat, lon, acc;
    long gettime; // 情報取得時間
    public LocationData( double lat, double lon, double acc ) {
        this.lat = lat;
        this.lon = lon;
        this.acc = acc;

        // 現在の時刻を取得
        this.gettime = System.currentTimeMillis();
    }

    public LocationData( double lat, double lon, double acc, long gettime ) {
        this.lat  = lat;
        this.lon  = lon;
        this.acc  = acc;
        this.gettime = gettime;
    }

    // for Serializer
    public LocationData( byte[] bytes ) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        this.lat = buffer.getDouble();
        this.lon = buffer.getDouble();
        this.acc = buffer.getDouble();
        this.gettime = buffer.getLong();
    }
    public byte[] getBytes() {
        int size = Double.SIZE / 8 * 3 + Long.SIZE/8;
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.clear();
        buffer.putDouble(lat);
        buffer.putDouble(lon);
        buffer.putDouble(acc);
        buffer.putLong(gettime);
        return buffer.array();
    }
    public String dump() {
        return String.valueOf(lat) + " , " + String.valueOf(lon) + " , " + String.valueOf(acc) + " , " + String.valueOf(gettime);
    }

}
