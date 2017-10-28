package jp.enpitsu.meepa.Rader.ARObjects.OpenGLES20;

/**
 * Created by iyobe on 2016/12/14.
 */
public class RADER_VALUES {

    public static float rotation = 0;        // 北側からの相手の方向（=レーダーを回転させる方向）
    public static float northDirection = 0; // 北側の方向
                                                // 相手の方向 = direction + rotation
    public static float distance = 9999;     // 相手との距離

    public static float RADIUS = 1.75f;         // レーダーの半径
    public static float MAX_DISTANCE = 40;      // レーダー中に表示される最大距離[m]
    public static float ROTATE_TO_DEFAULT = 60; // レーダーを初期状態にするための角度

    public static int distance_state = -1;     // 距離の状況
    // めっちゃ近い : 0 , 近い : 1, 遠い : 2, 圏外 : -1
    public static float BORDER_NEAR = (RADIUS*2)/3; // [遠い]と[近い]の境界
    public static float BORDER_NEAREST = RADIUS/3;  // [近い]と[めっちゃ近い]の境界

    public static float elevation = 0;          // 端末の仰角的な傾き
    public static double locationDirection =0;  // 位置情報から見た相手の方向
    public static double distanceOnRader = 0;   // 実際の距離に対するレーダー上の距離
                                                   //  = distance * ( RADIUS / MAX_DISTANCE )

    public static boolean isModeAR = false;  // ARモードのときtrue


    // 位置情報が変化したとき
    public static void invalidateLocation( float direction, float dist ) {
        // 角度更新
        locationDirection = direction;
        // 距離更新
        distance = dist;
        distanceOnRader = distance * ( RADIUS / MAX_DISTANCE );

        // rotationを更新
        getRotate();
    }

    // 端末の向きが変化したとき
    public static void invalidateDeviceDirection( float direction ) {
        // 角度更新
        northDirection = direction;
        // rotation更新
        getRotate();
    }

    // 端末の仰角が変化したとき
    public static void invalidateElevation( float elevate ) {
        elevation = elevate;
    }

    private static void getRotate() {
        // - [端末の向き] + [相手のいる方角]
        rotation = (float)( -northDirection + locationDirection );
    }

    public static void switchARMode( boolean bool ) {
        isModeAR = bool;
    }
}
