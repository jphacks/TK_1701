package jp.enpitsu.meepa.Rader;

/**
 * Created by iyobe on 2016/09/29.
 */
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

public class SensorFilter {

    private ArrayList<Float> mFirst     = new ArrayList<Float>();
    private ArrayList<Float> mSecond    = new ArrayList<Float>();
    private ArrayList<Float> mThrad     = new ArrayList<Float>();

    public int sampleCount=9;//サンプリング数
    public int sampleNum = 5;//サンプリングした値の使用値のインデックス

    private float[] mParam = new float[3];// フィルタをかけた後の値
    private float[] lastParam = new float[3];// 前回フィルタをかけた結果の値

    private boolean mSampleEnable=false;//規定のサンプリング数に達したか

    /**
     * フィルタをかけた値を返す
     * @return
     */
    public float[] getParam()
    {
        return mParam;
    }

    /**
     * サンプリングする値を追加
     * @param sample 要素３のfloatの配列オブジェクト
     */
    public void addSample(float[] sample)
    {
        //サンプリング数の追加
        mFirst.add(sample[0]);
        mSecond.add(sample[1]);
        mThrad.add(sample[2]);

        for( int i = 0; i < sample.length; ++i ) {
            if( sample[i] < 0 ) sample[i] = sample[i] + 360;
        }

        if( sample[0] < 0 || sample[1] < 0 || sample[2] < 0 )
            Log.d( "Sensor minus", sample[0] + "," + sample[1] + "," + sample[2] );


        //必要なサンプリング数に達したら
        if(mFirst.size() == sampleCount)
        {
            // TODO: 0と360の境界をなくしたい

            //メディアンフィルタ(サンプリング数をソートして中央値を使用)かけて値を取得
            //その値にさらにローパスフィルタをかける

            ArrayList<Float> lst = (ArrayList<Float>) mFirst.clone();
            Collections.sort(lst);
            mParam[0] =(mParam[0]*0.9f) + lst.get(sampleNum)*0.1f;

            lst = (ArrayList<Float>) mSecond.clone();
            Collections.sort(lst);
            mParam[1] = (mParam[1]*0.9f) +lst.get(sampleNum)*0.1f;

            lst = (ArrayList<Float>) mThrad.clone();
            Collections.sort(lst);
            mParam[2] = (mParam[2]*0.9f) +lst.get(sampleNum)*0.1f;

            mSampleEnable = true;

            //一番最初の値を削除
            mFirst.remove(0);
            mSecond.remove(0);
            mThrad.remove(0);
        }
    }

    /**
     * 規定のサンプリング数に達したか
     * @return
     */
    public boolean isSampleEnable()
    {
        return mSampleEnable;
    }

}