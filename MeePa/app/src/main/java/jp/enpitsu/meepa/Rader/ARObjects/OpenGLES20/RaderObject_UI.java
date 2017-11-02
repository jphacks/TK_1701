package jp.enpitsu.meepa.Rader.ARObjects.OpenGLES20;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;



// TODO : 頭悪すぎるからうまくやれ

/**
 * Created by iyobe on 2016/09/28.
 */

class Buffers {

    FloatBuffer vertexBuffer;   //頂点バッファ
    ByteBuffer  indexBuffer;    //インデックスバッファ
    FloatBuffer normalBuffer;   //法線バッファ

};


public class RaderObject_UI {

    float frameCount;

    //バッファ
    private ArrayList<CircleBuffers> circleBuffersesList;

    // レーダー圏外時用のうっすらな円弧
    private FloatBuffer arc_vertexBuffer;//頂点バッファ
    private ByteBuffer  arc_indexBuffer; //インデックスバッファ
    private FloatBuffer arc_normalBuffer;//法線バッファ
    // うっすらな円弧の円周上の線
    private FloatBuffer arcLine_vertexBuffer;//頂点バッファ
    private ByteBuffer  arcLine_indexBuffer; //インデックスバッファ
    private FloatBuffer arcLine_normalBuffer;//法線バッファ

    // レーダーの背景
    private FloatBuffer fillCircle_vertexBuffer;//頂点バッファ
    private ByteBuffer  fillCircle_indexBuffer; //インデックスバッファ
    private FloatBuffer fillCircle_normalBuffer;//法線バッファ

    // レーダー上の空いての位置
    private FloatBuffer target_vertexBuffer;//頂点バッファ
    private ByteBuffer  target_indexBuffer; //インデックスバッファ
    private FloatBuffer target_normalBuffer;//法線バッファ

    // まわるやつ
    private FloatBuffer bar_vertexBuffer;//頂点バッファ
    private ByteBuffer  bar_indexBuffer; //インデックスバッファ
    private FloatBuffer bar_normalBuffer;//法線バッファ

    // 中間領域用の半透明の輪
    private FloatBuffer ring_vertexBuffer;//頂点バッファ
    private ByteBuffer  ring_indexBuffer; //インデックスバッファ
    private FloatBuffer ring_normalBuffer;//法線バッファ

    // 枠線とか
    private FloatBuffer frameLines_vertexBuffer;//頂点バッファ
    private ByteBuffer  frameLines_indexBuffer; //インデックスバッファ
    private FloatBuffer frameLines_normalBuffer;//法線バッファ

    // 相手のいる領域
    private FloatBuffer arcRing_vertexBuffer;//頂点バッファ
    private ByteBuffer  arcRing_indexBuffer; //インデックスバッファ
    private FloatBuffer arcRing_normalBuffer;//法線バッファ

    private Buffers outerRing;  // いっちゃん外側の輪
    private Buffers midRing;    // 真ん中の輪
    private Buffers innerRing;  // いっちゃん内側の輪
    private Buffers arcRing_oppArea; // 相手のいる領域を示す弧
    private Buffers northMark;  // 北を示すマーク

    private Buffers bar;        // まわるやつ


    RaderObject_UI() {

        //外側の半径，内側の半径，台形近似する台形の数，台形を高さ方向分割する数, 格納先バッファ
        outerRing = initRing2( RADER_VALUES.RADIUS, RADER_VALUES.RADIUS-0.15f, 50, 1);    // いっちゃん外側の輪
        midRing   = initRing2( RADER_VALUES.BORDER_NEAR, RADER_VALUES.BORDER_NEAR-0.02f, 50, 1);     // 真ん中の輪
        innerRing = initRing2( RADER_VALUES.BORDER_NEAREST, RADER_VALUES.BORDER_NEAREST-0.02f, 50, 1);   // いっちゃん内側の輪

        bar = initBar2( 0f, 0f, 0f, RADER_VALUES.RADIUS-0.1f ); // まわるやつ

        northMark = initNorthMark( RADER_VALUES.BORDER_NEAR-0.06f, ((RADER_VALUES.RADIUS/3)-0.15f)/2 ); // 北側を示すマーク
        ////////////////////////////////////////////////////////////////////////////////////////////
        circleBuffersesList = new ArrayList<CircleBuffers>();

        for( float i = RADER_VALUES.RADIUS; i >= RADER_VALUES.RADIUS - 0.05f; i = i - 0.007f ) {
            initCircle( 0f, 0f, 0f, i );
        }
        initFillArc( 0f, 0f, 0f, RADER_VALUES.RADIUS );

        initFillCircle( 0f, 0f, 0f, RADER_VALUES.RADIUS );

        initTarget( 0f, 0f, 0f, 0.07f );

        initBar( 0f, 0f, 0f, RADER_VALUES.RADIUS-0.01f );
        //外側の半径，内側の半径，台形近似する台形の数，台形を高さ方向分割する数
        initRing( (RADER_VALUES.RADIUS*2)/3, RADER_VALUES.RADIUS/3, 50, 1 );
        initArcRing( (RADER_VALUES.RADIUS*2)/3, RADER_VALUES.RADIUS/3, 50, 1 );

        initFrameLines( 0f, 0f, 0f, RADER_VALUES.RADIUS );
        initArcLine( 0f, 0f, 0f, RADER_VALUES.RADIUS );
    }

    // 輪の頂点準備
    //外側の半径，内側の半径，台形近似する台形の数，台形を高さ方向分割する数, 格納先バッファ
    public Buffers initRing2(float RadiusOuter, float RadiusInner, int nSlices, int nStacks) {
        // TODO : 端の部分詰める
        Buffers buffers = new Buffers();
        //頂点座標
        int numPoints = (nSlices+1)*(nStacks+1);
        int nIndexs = nSlices*(nStacks+1)*2;
        int sizeArray=numPoints*3;
        float[] vertexs= new float[sizeArray];
        float[] normals= new float[sizeArray];
        byte[] indexs= new byte[nIndexs];
        int i,j;
        double theta0=2.0*3.1415956535/nSlices;
        double theta;
        double dr = (RadiusOuter-RadiusInner)/nStacks;
        double r;
        int p=0;
        for (i=0; i<=nSlices; i++) {
            theta = theta0 * i;
            for (j = 0; j <= nStacks; j++) {
                r = (RadiusOuter - j * dr);
                vertexs[p++] = (float) (r * Math.sin(theta));
                vertexs[p++] = (float) (r * Math.cos(theta));
                vertexs[p++] = 0f;
            }
        }
        p=0;
        for (i=0; i<=nSlices; i++) {
            for (j = 0; j <= nStacks; j++) {
                normals[p++] = 0f;
                normals[p++] = 0f;
                normals[p++] = 1f;
            }
        }
        p=0;
        int nStacks1=nStacks+1;
        for (i=0; i<nSlices; i++) {
            for (j = 0; j <= nStacks; j++) {
                indexs[p++] = (byte)(i*nStacks1+j);
                indexs[p++] = (byte)((i+1)*nStacks1+j);
            }
        }
        buffers.vertexBuffer = makeFloatBuffer(vertexs);
        buffers.indexBuffer = makeByteBuffer( indexs );
        buffers.normalBuffer = makeFloatBuffer(normals);

        return buffers;
    }

    // 回すための線の準備
    private Buffers initBar2( float centerX, float centerY, float centerZ, float r) {
        Buffers buffers = new Buffers();
        int length=100+2;

        float [] vertexs = new float[ (100+2) * 3 ];//頂点の数はn角形の場合はn*3*2になる
        byte [] indexs = new byte[ (100+2) * 3 ];

        //頂点配列情報
        vertexs[0] = centerX;
        vertexs[1] = centerY;
        vertexs[2] = centerZ;
        indexs[0] = 0;
        int count = 0;
        for (int i=1;i<length;i++) {

            float angle=(float)(2*Math.PI*i/(length-2));
            vertexs[i*3+0]=(float)( centerX+Math.cos(angle)*r);
            vertexs[i*3+1]=(float)(-centerY+Math.sin(angle)*r);
            vertexs[i*3+2]=centerZ;
        }
        count = 0;
        for( int i = 0; count < indexs.length; ++i ) {
            indexs[ count ] = (byte)0;
            indexs[ count+1 ] = (byte)(i+1);
            indexs[ count+2 ] = (byte)(i+2);
            count += 3;
        }
        buffers.vertexBuffer = makeFloatBuffer( vertexs );
        buffers.indexBuffer = makeByteBuffer( indexs );

        //法線バッファの生成
        float[] normals= new float[ vertexs.length ];
        for( int j = 0; j < vertexs.length; j += 3 ) {
            normals[j  ] = 0.0f;
            normals[j+1] = 0.0f;
            normals[j+2] = 1.0f;
        }
        float div=(float)Math.sqrt(
                (1.0f*1.0f)+(1.0f*1.0f)+(1.0f*1.0f));
        for (int i=0;i<normals.length;i++) normals[i]/=div;
        buffers.normalBuffer = makeFloatBuffer(normals);

        return buffers;
    }

    // 輪の弧？弧の線に幅をもたせたやつ？の準備
    public Buffers initArcRing2(float RadiusOuter, float RadiusInner, int nSlices, int nStacks) {
        Buffers buffers = new Buffers();
        //頂点座標
        int numPoints = (nSlices+1)*(nStacks+1);
        int nIndexs = nSlices*(nStacks+1)*2;
        int sizeArray=numPoints*3;
        float[] vertexs= new float[sizeArray];
        float[] normals= new float[sizeArray];
        byte[] indexs= new byte[nIndexs];
        int i,j;
        double theta0=2.0*3.1415956535/nSlices;
        double theta;
        double dr = (RadiusOuter-RadiusInner)/nStacks;
        double r;

        nSlices /= 6;

        int p=0;
        for (i=0; i<=nSlices; i++) {
            theta = theta0 * i;
            for (j = 0; j <= nStacks; j++) {
                r = (RadiusOuter - j * dr);
                vertexs[p++] = (float) (r * Math.sin(theta));
                vertexs[p++] = (float) (r * Math.cos(theta));
                vertexs[p++] = 0f;
            }
        }
        p=0;
        for (i=0; i<=nSlices; i++) {
            for (j = 0; j <= nStacks; j++) {
                normals[p++] = 0f;
                normals[p++] = 0f;
                normals[p++] = 1f;
            }
        }
        p=0;
        int nStacks1=nStacks+1;
        for (i=0; i<nSlices; i++) {
            for (j = 0; j <= nStacks; j++) {
                indexs[p++] = (byte)(i*nStacks1+j);
                indexs[p++] = (byte)((i+1)*nStacks1+j);
            }
            indexs[p] = indexs[p-3];
            indexs[p+1] = indexs[p-2];
        }

        buffers.vertexBuffer = makeFloatBuffer(vertexs);
        buffers.indexBuffer = makeByteBuffer( indexs );
        buffers.normalBuffer = makeFloatBuffer(normals);

        return buffers;
    }

    // 北側を示すマークの準備
    public Buffers initNorthMark( float RadiusOuter, float height ){
        Buffers buffers = new Buffers();

        float[] vertexs = new float[ 3 * 3 ];//頂点の数は三角形だから(3つの頂点) * (x, y, z 座標) =  3 * 3
        byte [] indexs  = new byte[ 3 ]; // 頂点の数

        // 縦線
        int index = 0;
        vertexs[ index*3   ] =  0.0f;               // x1
        vertexs[ index*3+1 ] =  RadiusOuter;        // y1
        vertexs[ index*3+2 ] =  0.0f;               // z1
        indexs[ index ] = (byte)index; ++index;

        vertexs[ index*3   ] =  -height/3 * 1.7f;          // x2
        vertexs[ index*3+1 ] =  RadiusOuter-height; // y2
        vertexs[ index*3+2 ] =  0.0f;               // z2
        indexs[ index ] = (byte)index; ++index;

        vertexs[ index*3   ] =  height/3 * 1.7f;           // x3
        vertexs[ index*3+1 ] =  RadiusOuter-height; // y3
        vertexs[ index*3+2 ] =  0.0f;               // z3
        indexs[ index ] = (byte)index; ++index;



        //法線バッファの生成
        float[] normals= new float[ vertexs.length ];
        for( int j = 0; j < vertexs.length; j += 3 ) {
            normals[j  ] = 0.0f;
            normals[j+1] = 0.0f;
            normals[j+2] = 1.0f;
        }
        float div=(float)Math.sqrt(
                (1.0f*1.0f)+(1.0f*1.0f)+(1.0f*1.0f));
        for (int i=0;i<normals.length;i++) normals[i]/=div;

        buffers.vertexBuffer = makeFloatBuffer( vertexs );
        buffers.indexBuffer  = makeByteBuffer( indexs );
        buffers.normalBuffer = makeFloatBuffer( normals );

        return buffers;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // レーダーの枠線等を描画する準備
    // 引数は円の中心の(x,y,z)座標とr(半径)
    private void initFrameLines( float centerX, float centerY, float centerZ, float r ) {
        int length = 100;

        float [] vertexs = new float[ ( length * 3 ) + 6*3 ];//頂点の数はn角形の場合はn*3*2になる
        byte [] indexs = new byte[ length + 6 ];

        //頂点配列情報
        for (int j = 0; j < length; j++) {
            float angle = (float) (2 * Math.PI * j / length);
            vertexs[ j * 3 + 0] = (float) (centerX + Math.cos(angle) * r);
            vertexs[ j * 3 + 1] = (float) (-centerY + Math.sin(angle) * r);
            vertexs[ j * 3 + 2] = centerZ;
            indexs[ j ] = (byte) j;
        }
        int k = length;
        r = r + 0.1f;
        // 縦線
        vertexs[ k*3   ] =  0f; // x1
        vertexs[ k*3+1 ] =  r; // y1
        vertexs[ k*3+2 ] =  0f; // z1
        indexs[ k ] = (byte)k; ++k;
        vertexs[ k*3   ] =  0f; // x2
        vertexs[ k*3+1 ] = -r; // y2
        vertexs[ k*3+2 ] =  0f; // z2
        indexs[ k ] = (byte)k; ++k;
        // 横線
        vertexs[ k*3   ] =  r; // x1
        vertexs[ k*3+1 ] =  0f; // y1
        vertexs[ k*3+2 ] =  0f; // z1
        indexs[ k ] = (byte)k; ++k;
        vertexs[ k*3   ] = -r; // x2
        vertexs[ k*3+1 ] =  0f; // y2
        vertexs[ k*3+2 ] =  0f; // z2
        indexs[ k ] = (byte)k; ++k;
        // 北側の縦線
        vertexs[ k*3   ] =  0f; // x1
        vertexs[ k*3+1 ] =  r; // y1
        vertexs[ k*3+2 ] =  0f; // z1
        indexs[ k ] = (byte)k; ++k;
        vertexs[ k*3   ] =  0f; // x2
        vertexs[ k*3+1 ] =  0f; // y2
        vertexs[ k*3+2 ] =  0f; // z2
        indexs[ k ] = (byte)k; ++k;


        //法線バッファの生成
        float[] normals= new float[ vertexs.length ];
        for( int j = 0; j < vertexs.length; j += 3 ) {
            normals[j  ] = 0.0f;
            normals[j+1] = 0.0f;
            normals[j+2] = 1.0f;
        }
        float div=(float)Math.sqrt(
                (1.0f*1.0f)+(1.0f*1.0f)+(1.0f*1.0f));
        for (int i=0;i<normals.length;i++) normals[i]/=div;

        frameLines_vertexBuffer = makeFloatBuffer( vertexs );
        frameLines_indexBuffer  = makeByteBuffer( indexs );
        frameLines_normalBuffer = makeFloatBuffer( normals );
    }

    // 円を描画する準備
    private void initCircle( float centerX, float centerY, float centerZ, float r ) {
        int length = 100;

        float [] vertexs = new float[ ( length * 3 ) ];//頂点の数はn角形の場合はn*3*2になる
        byte [] indexs = new byte[ length ];

        //頂点配列情報
        for (int j = 0; j < length; j++) {
            float angle = (float) (2 * Math.PI * j / length);
            vertexs[ j * 3 + 0] = (float) (centerX + Math.cos(angle) * r);
            vertexs[ j * 3 + 1] = (float) (-centerY + Math.sin(angle) * r);
            vertexs[ j * 3 + 2] = centerZ;
            indexs[ j ] = (byte) j;
        }

        //法線バッファの生成
        float[] normals= new float[ vertexs.length ];
        for( int j = 0; j < vertexs.length; j += 3 ) {
            normals[j  ] = 0.0f;
            normals[j+1] = 0.0f;
            normals[j+2] = 1.0f;
        }
        float div=(float)Math.sqrt(
                (1.0f*1.0f)+(1.0f*1.0f)+(1.0f*1.0f));
        for (int i=0;i<normals.length;i++) normals[i]/=div;

        circleBuffersesList.add( new CircleBuffers( makeFloatBuffer( vertexs ),
                makeByteBuffer( indexs ),
                makeFloatBuffer( normals )
        ) );
    }

    // 円弧を描画する準備
    private void initFillArc( float centerX, float centerY, float centerZ, float r ) {
        int length=100+2;

        float [] vertexs = new float[ (100+2) * 3 ];//頂点の数はn角形の場合はn*3*2になる
        byte [] indexs = new byte[ 100+2 ];

        //頂点配列情報
        vertexs[0] = centerX;
        vertexs[1] = centerY;
        vertexs[2] = centerZ;
        indexs[0] = 0;
//        for (int i=1;i<(length*5/6);i++) {
        for (int i=1;i<(length/6);i++) {

            float angle=(float)(2*Math.PI*i/(length-2));
            vertexs[i*3+0]=(float)( centerX+Math.cos(angle)*r);
            vertexs[i*3+1]=(float)(-centerY+Math.sin(angle)*r);
            vertexs[i*3+2]=centerZ;

            indexs[i] = (byte)i;
        }
        arc_vertexBuffer = makeFloatBuffer( vertexs );
        arc_indexBuffer = makeByteBuffer( indexs );

        //法線バッファの生成
        float[] normals= new float[ vertexs.length ];
        for( int j = 0; j < vertexs.length; j += 3 ) {
            normals[j  ] = 0.0f;
            normals[j+1] = 0.0f;
            normals[j+2] = 1.0f;
        }
        float div=(float)Math.sqrt(
                (1.0f*1.0f)+(1.0f*1.0f)+(1.0f*1.0f));
        for (int i=0;i<normals.length;i++) normals[i]/=div;
        arc_normalBuffer = makeFloatBuffer(normals);
    }

    // 塗りつぶした円を描画する準備
    private void initFillCircle( float centerX, float centerY, float centerZ, float r ) {
        int length=100+2;

        float [] vertexs = new float[ (100+2) * 3 ];//頂点の数はn角形の場合はn*3*2になる
        byte [] indexs = new byte[ 100+2 ];

        //頂点配列情報
        vertexs[0] = centerX;
        vertexs[1] = centerY;
        vertexs[2] = centerZ;
        indexs[0] = 0;
        for (int i=1;i<length;i++) {

            float angle=(float)(2*Math.PI*i/(length-2));
            vertexs[i*3+0]=(float)( centerX+Math.cos(angle)*r);
            vertexs[i*3+1]=(float)(-centerY+Math.sin(angle)*r);
            vertexs[i*3+2]=centerZ;

            indexs[i] = (byte)i;
        }
        fillCircle_vertexBuffer = makeFloatBuffer( vertexs );
        fillCircle_indexBuffer = makeByteBuffer( indexs );

        //法線バッファの生成
        float[] normals= new float[ vertexs.length ];
        for( int j = 0; j < vertexs.length; j += 3 ) {
            normals[j  ] = 0.0f;
            normals[j+1] = 0.0f;
            normals[j+2] = 1.0f;
        }
        float div=(float)Math.sqrt(
                (1.0f*1.0f)+(1.0f*1.0f)+(1.0f*1.0f));
        for (int i=0;i<normals.length;i++) normals[i]/=div;
        fillCircle_normalBuffer = makeFloatBuffer(normals);
    }

    // レーダー上の相手の位置(塗りつぶした円)を描画する準備
    private void initTarget( float centerX, float centerY, float centerZ, float r ) {
        int length=100+2;

        float [] vertexs = new float[ (100+2) * 3 ];//頂点の数はn角形の場合はn*3*2になる
        byte [] indexs = new byte[ 100+2 ];

        //頂点配列情報
        vertexs[0] = centerX;
        vertexs[1] = centerY;
        vertexs[2] = centerZ;
        indexs[0] = 0;
        for (int i=1;i<length;i++) {

            float angle=(float)(2*Math.PI*i/(length-2));
            vertexs[i*3+0]=(float)( centerX+Math.cos(angle)*r);
            vertexs[i*3+1]=(float)(-centerY+Math.sin(angle)*r);
            vertexs[i*3+2]=centerZ+0.2f;

            indexs[i] = (byte)i;
        }
        target_vertexBuffer = makeFloatBuffer( vertexs );
        target_indexBuffer = makeByteBuffer( indexs );

        //法線バッファの生成
        float[] normals= new float[ vertexs.length ];
        for( int j = 0; j < vertexs.length; j += 3 ) {
            normals[j  ] = 0.0f;
            normals[j+1] = 0.0f;
            normals[j+2] = 1.0f;
        }
        float div=(float)Math.sqrt(
                (1.0f*1.0f)+(1.0f*1.0f)+(1.0f*1.0f));
        for (int i=0;i<normals.length;i++) normals[i]/=div;
        target_normalBuffer = makeFloatBuffer(normals);
    }

    private void initBar( float centerX, float centerY, float centerZ, float r ) {
        int length=100+2;

        float [] vertexs = new float[ (100+2) * 3 ];//頂点の数はn角形の場合はn*3*2になる
        byte [] indexs = new byte[ (100+2) * 3 ];

        //頂点配列情報
        vertexs[0] = centerX;
        vertexs[1] = centerY;
        vertexs[2] = centerZ;
        indexs[0] = 0;
        int count = 0;
        for (int i=1;i<length;i++) {

            float angle=(float)(2*Math.PI*i/(length-2));
            vertexs[i*3+0]=(float)( centerX+Math.cos(angle)*r);
            vertexs[i*3+1]=(float)(-centerY+Math.sin(angle)*r);
            vertexs[i*3+2]=centerZ;
        }
        count = 0;
        for( int i = 0; count < indexs.length; ++i ) {
            indexs[ count ] = (byte)0;
            indexs[ count+1 ] = (byte)(i+1);
            indexs[ count+2 ] = (byte)(i+2);
            count += 3;
        }
        bar_vertexBuffer = makeFloatBuffer( vertexs );
        bar_indexBuffer = makeByteBuffer( indexs );

        //法線バッファの生成
        float[] normals= new float[ vertexs.length ];
        for( int j = 0; j < vertexs.length; j += 3 ) {
            normals[j  ] = 0.0f;
            normals[j+1] = 0.0f;
            normals[j+2] = 1.0f;
        }
        float div=(float)Math.sqrt(
                (1.0f*1.0f)+(1.0f*1.0f)+(1.0f*1.0f));
        for (int i=0;i<normals.length;i++) normals[i]/=div;
        bar_normalBuffer = makeFloatBuffer(normals);
    }

    public void initRing(float RadiusOuter, float RadiusInner, int nSlices, int nStacks) {
        // TODO : 端の部分詰める
        //頂点座標
        int numPoints = (nSlices+1)*(nStacks+1);
        int nIndexs = nSlices*(nStacks+1)*2;
        int sizeArray=numPoints*3;
        float[] vertexs= new float[sizeArray];
        float[] normals= new float[sizeArray];
        byte[] indexs= new byte[nIndexs];
        int i,j;
        double theta0=2.0*3.1415956535/nSlices;
        double theta;
        double dr = (RadiusOuter-RadiusInner)/nStacks;
        double r;
        int p=0;
        for (i=0; i<=nSlices; i++) {
            theta = theta0 * i;
            for (j = 0; j <= nStacks; j++) {
                r = (RadiusOuter - j * dr);
                vertexs[p++] = (float) (r * Math.sin(theta));
                vertexs[p++] = (float) (r * Math.cos(theta));
                vertexs[p++] = 0f;
            }
        }
        p=0;
        for (i=0; i<=nSlices; i++) {
            for (j = 0; j <= nStacks; j++) {
                normals[p++] = 0f;
                normals[p++] = 0f;
                normals[p++] = 1f;
            }
        }
        p=0;
        int nStacks1=nStacks+1;
        for (i=0; i<nSlices; i++) {
            for (j = 0; j <= nStacks; j++) {
                indexs[p++] = (byte)(i*nStacks1+j);
                indexs[p++] = (byte)((i+1)*nStacks1+j);
            }
        }
        ring_vertexBuffer = makeFloatBuffer(vertexs);
        ring_indexBuffer = makeByteBuffer( indexs );
        ring_normalBuffer = makeFloatBuffer(normals);
    }


    public void initArcRing(float RadiusOuter, float RadiusInner, int nSlices, int nStacks) {
        //頂点座標
        int numPoints = (nSlices+1)*(nStacks+1);
        int nIndexs = nSlices*(nStacks+1)*2;
        int sizeArray=numPoints*3;
        float[] vertexs= new float[sizeArray];
        float[] normals= new float[sizeArray];
        byte[] indexs= new byte[nIndexs];
        int i,j;
        double theta0=2.0*3.1415956535/nSlices;
        double theta;
        double dr = (RadiusOuter-RadiusInner)/nStacks;
        double r;

        nSlices /= 6;

        int p=0;
        for (i=0; i<=nSlices; i++) {
            theta = theta0 * i;
            for (j = 0; j <= nStacks; j++) {
                r = (RadiusOuter - j * dr);
                vertexs[p++] = (float) (r * Math.sin(theta));
                vertexs[p++] = (float) (r * Math.cos(theta));
                vertexs[p++] = 0f;
            }
        }
        p=0;
        for (i=0; i<=nSlices; i++) {
            for (j = 0; j <= nStacks; j++) {
                normals[p++] = 0f;
                normals[p++] = 0f;
                normals[p++] = 1f;
            }
        }
        p=0;
        int nStacks1=nStacks+1;
        for (i=0; i<nSlices; i++) {
            for (j = 0; j <= nStacks; j++) {
                indexs[p++] = (byte)(i*nStacks1+j);
                indexs[p++] = (byte)((i+1)*nStacks1+j);
            }
            indexs[p] = indexs[p-3];
            indexs[p+1] = indexs[p-2];
        }

        arcRing_vertexBuffer = makeFloatBuffer(vertexs);
        arcRing_indexBuffer = makeByteBuffer( indexs );
        arcRing_normalBuffer = makeFloatBuffer(normals);
    }

    // 円弧（線）を描画する準備
    private void initArcLine( float centerX, float centerY, float centerZ, float r ) {
        int length = 100;

        float [] vertexs = new float[ ( length * 3 ) ];//頂点の数はn角形の場合はn*3*2になる
        byte [] indexs = new byte[ length * 2 ];

        //頂点配列情報
        for (int j = 0; j < length/6; j++) {
            float angle = (float) (2 * Math.PI * j / length);
            vertexs[ j * 3 + 0] = (float) (centerX + Math.cos(angle) * r);
            vertexs[ j * 3 + 1] = (float) (-centerY + Math.sin(angle) * r);
            vertexs[ j * 3 + 2] = centerZ;
//            indexs[ j ] = (byte) j;
        }
        int count = 0;
        for( int j = 0; (j+1) < (length/6); ++j ) {
            indexs[ count++ ] = (byte) j;
            indexs[ count++ ] = (byte) (j+1);
        }


        //法線バッファの生成
        float[] normals= new float[ vertexs.length ];
        for( int j = 0; j < vertexs.length; j += 3 ) {
            normals[j  ] = 0.0f;
            normals[j+1] = 0.0f;
            normals[j+2] = 1.0f;
        }
        float div=(float)Math.sqrt(
                (1.0f*1.0f)+(1.0f*1.0f)+(1.0f*1.0f));
        for (int i=0;i<normals.length;i++) normals[i]/=div;

        arcLine_vertexBuffer = makeFloatBuffer( vertexs );
        arcLine_indexBuffer  = makeByteBuffer( indexs );
        arcLine_normalBuffer = makeFloatBuffer( normals );
    }


    public void draw() {
        ////////////////////////////////////////////////////////////////////////////////////////////
        RADER_VALUES.distance_state = 2;
        RADER_VALUES.distance = RADER_VALUES.MAX_DISTANCE;
        ////////////////////////////////////////////////////////////////////////////////////////////

        if ( RADER_VALUES.isModeAR == true ) {
            //光源位置の指定
            GLES20.glUniform4f(GLES.lightPosHandle, 0f, 10f, 0f, 1.0f);

            GLES.glPushMatrix();
            Matrix.translateM(GLES.mMatrix, 0, 0, -1.4f, -5); // 初期配置（画面下側に寄せる）
            Matrix.scaleM(GLES.mMatrix, 0, 0.8f, 0.8f, 0.8f); // 渾身の微調整（ごり押し）

            GLES.glPushMatrix();
            Matrix.rotateM(GLES.mMatrix, 0, -90, 1, 0, 0); // 遠近感ある感じに回転
        }
        else { // ARモードでないとき
            //光源位置の指定
            GLES20.glUniform4f(GLES.lightPosHandle,0f,0f,0f,1.0f);

            GLES.glPushMatrix();
            GLES.glPushMatrix(); // 倍プッシュ（ARモードのときと同じ回数pushしたい）
            Matrix.translateM(GLES.mMatrix, 0, 0, 0, -6f);    // レーダーが真正面に来るように移動
        }

            GLES.glPushMatrix();
            Matrix.rotateM(GLES.mMatrix, 0, RADER_VALUES.northDirection, 0, 0, 1); // 北の方向に回転
            GLES.updateMatrix();
//            drawFrameLines( 0f, 150f/255f, 255f/255f, 1f );      // レーダーの枠線等
            drawNorthMark( northMark, 255f/255f, 189f/255f,  48f/255f, 1f ); // 北側を示すマーク

            drawRing2( outerRing, 255f/255f, 189f/255f,  48f/255f, 1f ); // いっちゃん外側の輪（オレンジ）
            drawRing2(   midRing, 70f/255f,   68f/255f,  69f/255f, 1f ); // 真ん中の輪（グレー）
            drawRing2( innerRing, 255f/255f,  189f/255f, 48f/255f, 1f ); // いっちゃん内側の輪（オレンジ）

            Matrix.rotateM(GLES.mMatrix, 0, RADER_VALUES.ROTATE_TO_DEFAULT, 0, 0, 1); // 初期配置（レーダーが真上を指すように回転）
            Matrix.rotateM(GLES.mMatrix, 0, -RADER_VALUES.rotation, 0, 0, 1); // 初期配置（レーダーが真上を指すように回転）

            GLES.updateMatrix();
//            drawFillCircle( 0f, 0f, 0f, 0.2f );   // 半透明の円
            if( RADER_VALUES.distance_state == -1 ) {
                drawFillArc( 1f, 1f, 1f, 0.020f);       // 円弧
            }
//            drawRing( 1f, 1f, 1f, 0.03f );         // なんか輪

            // それっぽく回るやつ
            GLES.glPushMatrix();
            Matrix.rotateM( GLES.mMatrix, 0, -RADER_VALUES.rotation + frameCount, 0f, 0f, -1f );
            GLES.updateMatrix();
            drawBar( 70f/255f, 68f/255f, 69f/255f, 0.2f );
            GLES.glPopMatrix();

            // 相手の位置の描画
            // 相手との距離がMAX_DISTANCEm以内のとき
            if( RADER_VALUES.distance <= RADER_VALUES.MAX_DISTANCE ) {
                GLES.glPushMatrix();
                Matrix.rotateM(GLES.mMatrix, 0, -RADER_VALUES.ROTATE_TO_DEFAULT, 0, 0, 1); // 初期配置（レーダーが指す範囲の中心に来るように）

                if( RADER_VALUES.distanceOnRader <= RADER_VALUES.BORDER_NEAR ) { // [近い]圏内
                    if ( RADER_VALUES.distanceOnRader <= RADER_VALUES.BORDER_NEAREST ) { // [めっちゃ近い]圏内
                        if( RADER_VALUES.distance_state != 0 ) { // 状態が変化する場合
                            RADER_VALUES.distance_state = 0;
                            arcRing_oppArea = initArcRing2( RADER_VALUES.BORDER_NEAREST, 0f, 50, 1 );
//                            initArcRing( RADER_VALUES.BORDER_NEAREST, 0f, 50, 1 );
                        }
                    }
                    else {
                        if( RADER_VALUES.distance_state != 1 ) { // 状態が変化する場合
                            RADER_VALUES.distance_state = 1;
                            arcRing_oppArea = initArcRing2( RADER_VALUES.BORDER_NEAR, RADER_VALUES.BORDER_NEAREST, 50, 1 );
//                            initArcRing( RADER_VALUES.BORDER_NEAR, RADER_VALUES.BORDER_NEAREST, 50, 1 );
                        }
                    }
                }
                else {
                    if( RADER_VALUES.distance_state != 2 ) { // 状態が変化する場合
                        RADER_VALUES.distance_state = 2;
                        arcRing_oppArea = initArcRing2( RADER_VALUES.RADIUS, RADER_VALUES.BORDER_NEAR, 50, 1 );
//                        initArcRing( RADER_VALUES.RADIUS-0.01f, RADER_VALUES.BORDER_NEAR, 50, 1 );
                    }
                }

                GLES.glPushMatrix();
                Matrix.rotateM(GLES.mMatrix, 0, 30.5f, 0f, 0f, 1f);
                GLES.updateMatrix();
                arcRing_oppArea = initArcRing2( RADER_VALUES.RADIUS, RADER_VALUES.BORDER_NEAR, 50, 1 );
                drawArcRing2( arcRing_oppArea, 1f, 1f, 1f, 0.125f );
//                drawArcRing(1f, 0.2f, 0.5f, 0.08f);
                GLES.glPopMatrix();

                // 1m当たりのレーダー上での距離 = RADIUS[レーダーの半径]/MAX_DISTANCE[距離(m)]
                GLES.glPushMatrix();
                Matrix.translateM(GLES.mMatrix, 0, 0f, (float)RADER_VALUES.distanceOnRader, 0f);
                GLES.updateMatrix();
                drawTarget( 1f, 0.2f, 0.5f, 0.1f );       // レーダー上の相手の位置
                GLES.glPopMatrix();

            } else {
                if( RADER_VALUES.distance_state != -1 ) // 上体が変化するとき
                    RADER_VALUES.distance_state = -1; // レーダー圏外
            }

            if( RADER_VALUES.distance_state == -1 ) {
                GLES.glPushMatrix();
                Matrix.rotateM(GLES.mMatrix, 0, 3f, 0f, 0f, 1f);
                GLES.updateMatrix();
//                drawArcLine( 1f, 0.2f, 0.5f, 0.3f );
                GLES.glPopMatrix();
            }

            GLES.glPopMatrix();
            GLES.glPopMatrix();
            GLES.glPopMatrix();

        frameCount = ( frameCount + 2 ) % 360;
    }

    // レーダーの枠線等の描画
    private void drawFrameLines( float r, float g, float b, float a ) {
        //頂点バッファの指定
        GLES20.glVertexAttribPointer( GLES.positionHandle, 3,
                GLES20.GL_FLOAT, false, 0, frameLines_vertexBuffer );

        //法線バッファの指定
        GLES20.glVertexAttribPointer( GLES.normalHandle, 3,
                GLES20.GL_FLOAT, false, 0, frameLines_normalBuffer );

        //描画
        setMaterial( r, g, b, a );
        GLES20.glLineWidth( 6f );
        // 円の枠線
        frameLines_indexBuffer.position( 0 );
        GLES20.glDrawElements( GLES20.GL_LINE_LOOP,
                frameLines_indexBuffer.capacity()-6, GLES20.GL_UNSIGNED_BYTE, frameLines_indexBuffer );
        // 十字線
        frameLines_indexBuffer.position( frameLines_indexBuffer.capacity()-6 );
        GLES20.glDrawElements( GLES20.GL_LINES,
                4, GLES20.GL_UNSIGNED_BYTE, frameLines_indexBuffer );

        // 北側
        setMaterial( 1, 0, 0, a );
        frameLines_indexBuffer.position( frameLines_indexBuffer.capacity()-2 );
        GLES20.glDrawElements( GLES20.GL_LINES,
                2, GLES20.GL_UNSIGNED_BYTE, frameLines_indexBuffer );
    }

    //円の描画
    private void drawCircle( float r, float g, float b, float a ) {
        for( int i = 0; i < circleBuffersesList.size(); ++i  ) {
            //頂点バッファの指定
            GLES20.glVertexAttribPointer( GLES.positionHandle, 3,
                    GLES20.GL_FLOAT, false, 0, circleBuffersesList.get(i).vertexBuffer );

            //法線バッファの指定
            GLES20.glVertexAttribPointer( GLES.normalHandle, 3,
                    GLES20.GL_FLOAT, false, 0, circleBuffersesList.get(i).normalBuffer );

            //描画
            setMaterial( r, g, b, a );
            circleBuffersesList.get(i).indexBuffer.position(0);
            GLES20.glLineWidth( 100f );
            GLES20.glDrawElements( GLES20.GL_LINE_LOOP,
                    circleBuffersesList.get(i).indexBuffer.capacity(), GLES20.GL_UNSIGNED_BYTE, circleBuffersesList.get(i).indexBuffer );
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // 輪の（線に幅をもたせた輪）描画
    public void drawRing2( Buffers buffers, float r, float g, float b, float a ) {
        //頂点バッファの指定
        GLES20.glVertexAttribPointer( GLES.positionHandle, 3,
                GLES20.GL_FLOAT, false, 0, buffers.vertexBuffer );

        //法線バッファの指定
        GLES20.glVertexAttribPointer( GLES.normalHandle, 3,
                GLES20.GL_FLOAT, false, 0, buffers.normalBuffer );

        //描画
        setMaterial( r, g, b, a );
        buffers.indexBuffer.position(0);
        GLES20.glDrawElements( GLES20.GL_TRIANGLE_STRIP,
                buffers.indexBuffer.capacity(), GLES20.GL_UNSIGNED_BYTE, buffers.indexBuffer );
        buffers.indexBuffer.position(0);
        GLES20.glDrawElements( GLES20.GL_TRIANGLE_STRIP,
                3, GLES20.GL_UNSIGNED_BYTE, buffers.indexBuffer );
        buffers.indexBuffer.position(buffers.indexBuffer.capacity()-3);
        GLES20.glDrawElements( GLES20.GL_TRIANGLE_STRIP,
                3, GLES20.GL_UNSIGNED_BYTE, buffers.indexBuffer );
    }

    // 幅を持った弧（）描画
    public void drawArcRing2( Buffers buffers, float r, float g, float b, float a ) {
        //頂点バッファの指定
        GLES20.glVertexAttribPointer( GLES.positionHandle, 3,
                GLES20.GL_FLOAT, false, 0, buffers.vertexBuffer );

        //法線バッファの指定
        GLES20.glVertexAttribPointer( GLES.normalHandle, 3,
                GLES20.GL_FLOAT, false, 0, buffers.normalBuffer );

        //描画
        setMaterial( r, g, b, a );
        buffers.indexBuffer.position( 0 );
        GLES20.glDrawElements( GLES20.GL_TRIANGLE_STRIP,
                33, GLES20.GL_UNSIGNED_BYTE, buffers.indexBuffer );
        buffers.indexBuffer.position( 0 );
        GLES20.glDrawElements( GLES20.GL_TRIANGLE_STRIP,
                3, GLES20.GL_UNSIGNED_BYTE, buffers.indexBuffer );
    }

    // レーダーの枠線等の描画
    private void drawNorthMark( Buffers buffers, float r, float g, float b, float a ) {
        //頂点バッファの指定
        GLES20.glVertexAttribPointer( GLES.positionHandle, 3,
                GLES20.GL_FLOAT, false, 0, buffers.vertexBuffer );

        //法線バッファの指定
        GLES20.glVertexAttribPointer( GLES.normalHandle, 3,
                GLES20.GL_FLOAT, false, 0, buffers.normalBuffer );

        setMaterial( r, g, b, a );
        GLES20.glLineWidth( 1f );
        //描画
        buffers.indexBuffer.position( 0 );
        GLES20.glDrawElements( GLES20.GL_TRIANGLES,
                buffers.indexBuffer.capacity(), GLES20.GL_UNSIGNED_BYTE, buffers.indexBuffer );
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // 円弧の描画
    private void drawFillArc( float r, float g, float b, float a ) {
        //頂点バッファの指定
        GLES20.glVertexAttribPointer( GLES.positionHandle, 3,
                GLES20.GL_FLOAT, false, 0, arc_vertexBuffer );

        //法線バッファの指定
        GLES20.glVertexAttribPointer( GLES.normalHandle, 3,
                GLES20.GL_FLOAT, false, 0, arc_normalBuffer );

        //描画
        setMaterial( r, g, b, a );
        arc_indexBuffer.position(0);
        GLES20.glDrawElements( GLES20.GL_TRIANGLE_FAN,
                arc_indexBuffer.capacity(), GLES20.GL_UNSIGNED_BYTE, arc_indexBuffer );
    }


    private void drawFillCircle( float r, float g, float b, float a ) {
        //頂点バッファの指定
        GLES20.glVertexAttribPointer( GLES.positionHandle, 3,
                GLES20.GL_FLOAT, false, 0, fillCircle_vertexBuffer );

        //法線バッファの指定
        GLES20.glVertexAttribPointer( GLES.normalHandle, 3,
                GLES20.GL_FLOAT, false, 0, fillCircle_normalBuffer );

        //描画
        setMaterial( r, g, b, a );
        fillCircle_indexBuffer.position(0);
        GLES20.glDrawElements( GLES20.GL_TRIANGLE_FAN,
                fillCircle_indexBuffer.capacity(), GLES20.GL_UNSIGNED_BYTE, fillCircle_indexBuffer );
    }

    private void drawTarget( float r, float g, float b, float a ) {
        //頂点バッファの指定
        GLES20.glVertexAttribPointer( GLES.positionHandle, 3,
                GLES20.GL_FLOAT, false, 0, target_vertexBuffer );

        //法線バッファの指定
        GLES20.glVertexAttribPointer( GLES.normalHandle, 3,
                GLES20.GL_FLOAT, false, 0, target_normalBuffer );

        //描画
        setMaterial( r, g, b, a );
        target_indexBuffer.position(0);
        GLES20.glDrawElements( GLES20.GL_TRIANGLE_FAN,
                target_indexBuffer.capacity(), GLES20.GL_UNSIGNED_BYTE, target_indexBuffer );
    }

    private void drawBar( float r, float g, float b, float a ) {
        //頂点バッファの指定
        GLES20.glVertexAttribPointer( GLES.positionHandle, 3,
                GLES20.GL_FLOAT, false, 0, bar_vertexBuffer );

        //法線バッファの指定
        GLES20.glVertexAttribPointer( GLES.normalHandle, 3,
                GLES20.GL_FLOAT, false, 0, bar_normalBuffer );

        //描画
        setMaterial( r, g, b, a );
        GLES20.glLineWidth( 1f );
        bar_indexBuffer.position( 0 );
        GLES20.glDrawElements(GLES20.GL_LINE_STRIP,
                2, GLES20.GL_UNSIGNED_BYTE, bar_indexBuffer);
        float alpha = 0.05f;
        for( int i = 0; i < bar_indexBuffer.capacity(); i += 3 ) {
            setMaterial( r, g, b, alpha );
            alpha -= 0.001f;
            bar_indexBuffer.position( i );
            GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP,
                    3, GLES20.GL_UNSIGNED_BYTE, bar_indexBuffer);

            if( alpha < 0 ) break;
        }
    }

    public void drawRing( float r, float g, float b, float a ) {
        //頂点バッファの指定
        GLES20.glVertexAttribPointer( GLES.positionHandle, 3,
                GLES20.GL_FLOAT, false, 0, ring_vertexBuffer );

        //法線バッファの指定
        GLES20.glVertexAttribPointer( GLES.normalHandle, 3,
                GLES20.GL_FLOAT, false, 0, ring_normalBuffer );

        //描画
        setMaterial( r, g, b, a );
        ring_indexBuffer.position(0);
        GLES20.glDrawElements( GLES20.GL_TRIANGLE_STRIP,
                ring_indexBuffer.capacity(), GLES20.GL_UNSIGNED_BYTE, ring_indexBuffer );
        ring_indexBuffer.position(0);
        GLES20.glDrawElements( GLES20.GL_TRIANGLE_STRIP,
                3, GLES20.GL_UNSIGNED_BYTE, ring_indexBuffer );
        ring_indexBuffer.position(ring_indexBuffer.capacity()-3);
        GLES20.glDrawElements( GLES20.GL_TRIANGLE_STRIP,
                3, GLES20.GL_UNSIGNED_BYTE, ring_indexBuffer );
    }

    public void drawArcRing( float r, float g, float b, float a ) {
        //頂点バッファの指定
        GLES20.glVertexAttribPointer( GLES.positionHandle, 3,
                GLES20.GL_FLOAT, false, 0, arcRing_vertexBuffer );

        //法線バッファの指定
        GLES20.glVertexAttribPointer( GLES.normalHandle, 3,
                GLES20.GL_FLOAT, false, 0, arcRing_normalBuffer );

        //描画
        setMaterial( r, g, b, a );
        arcRing_indexBuffer.position( 0 );
        GLES20.glDrawElements( GLES20.GL_TRIANGLE_STRIP,
                33, GLES20.GL_UNSIGNED_BYTE, arcRing_indexBuffer );
        arcRing_indexBuffer.position( 0 );
        GLES20.glDrawElements( GLES20.GL_TRIANGLE_STRIP,
                3, GLES20.GL_UNSIGNED_BYTE, arcRing_indexBuffer );
    }

    //円弧（線）の描画
    private void drawArcLine( float r, float g, float b, float a ) {
        //頂点バッファの指定
        GLES20.glVertexAttribPointer( GLES.positionHandle, 3,
                GLES20.GL_FLOAT, false, 0, arcLine_vertexBuffer );

        //法線バッファの指定
        GLES20.glVertexAttribPointer( GLES.normalHandle, 3,
                GLES20.GL_FLOAT, false, 0, arcLine_normalBuffer );

        //描画
        setMaterial( r, g, b, a );
        arcLine_indexBuffer.position(0);
        GLES20.glLineWidth( 7f );
        GLES20.glDrawElements( GLES20.GL_LINES,
                arcLine_indexBuffer.capacity(), GLES20.GL_UNSIGNED_BYTE, arcLine_indexBuffer );
    }

    //マテリアルの指定
    private void setMaterial(float r,float g,float b,float a) {
        //マテリアルの環境光色の指定
        GLES20.glUniform4f(GLES.materialAmbientHandle,r,g,b,a);

        //マテリアルの拡散光色の指定
        GLES20.glUniform4f(GLES.materialDiffuseHandle,r,g,b,a);

        //マテリアルの鏡面光色と鏡面指数の指定
        GLES20.glUniform4f(GLES.materialSpecularHandle,r,g,b,a);
        GLES20.glUniform1f(GLES.materialShininessHandle,0.6f);
    }

    //float配列をFloatBufferに変換
    private FloatBuffer makeFloatBuffer(float[] array) {
        FloatBuffer fb=ByteBuffer.allocateDirect(array.length*4).order(
                ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(array).position(0);
        return fb;
    }

    //byte配列をByteBufferに変換
    private ByteBuffer makeByteBuffer(byte[] array) {
        ByteBuffer bb=ByteBuffer.allocateDirect(array.length).order(
                ByteOrder.nativeOrder());
        bb.put(array).position(0);
        return bb;
    }
}
