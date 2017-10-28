package jp.enpitsu.meepa.Rader.ARObjects.OpenGLES20;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by iyobe on 2016/11/15.
 */
public class TargetObject {

    //バッファ
    private FloatBuffer vertexBuffer;//頂点バッファ
    private ByteBuffer indexBuffer; //インデックスバッファ
    private FloatBuffer normalBuffer;//法線バッファ
    private FloatBuffer uvBuffer;     //UVバッファ

    private int texID;  // テクスチャID
    private Bitmap bmpTexture;

    TargetObject() {
        // テクスチャ画像読み込み
        bmpTexture = null;
        String name= "rader_target1.png";
        try {
            AssetManager am = GLES.context.getResources().getAssets();
            InputStream is = am.open( name );
            bmpTexture = BitmapFactory.decodeStream( is );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // テクスチャ生成
        texID = makeTexture( this.bmpTexture );
        bmpTexture.recycle();
        bmpTexture = null;

        initTarget();
    }

    // 立方体を描画する準備
    private void initTarget() {
        //頂点バッファの生成
        //頂点バッファの生成
        float[] vertexs={
                -5.0f, 5.0f,0.0f,//頂点0
                -5.0f,-5.0f,0.0f,//頂点1
                5.0f, 5.0f,0.0f,//頂点2
                5.0f,-5.0f,0.0f,//頂点3
        };
        vertexBuffer = makeFloatBuffer(vertexs);

        //UVバッファの生成
        float[] uvs={
                0.0f,0.0f,//左上
                0.0f,1.0f,//左下
                1.0f,0.0f,//右上
                1.0f,1.0f,//右下
        };
        uvBuffer=makeFloatBuffer(uvs);

        //インデックスバッファの生成
        byte[] indexs = {
                0, 1, 2, 3, //面0
        };
        indexBuffer = makeByteBuffer(indexs);

        //法線バッファの生成
        float[] normals = {
                1.0f, 1.0f, 1.0f,//頂点0
                1.0f, 1.0f, -1.0f,//頂点1
                -1.0f, 1.0f, 1.0f,//頂点2
                -1.0f, 1.0f, -1.0f,//頂点3
        };
        float div = (float) Math.sqrt(
                (1.0f * 1.0f) + (1.0f * 1.0f) + (1.0f * 1.0f));
        for( int i = 0; i<normals.length; i++ ) normals[i] /= div;
        normalBuffer = makeFloatBuffer(normals);
    }

    public void draw() {

        if ( RADER_VALUES.isModeAR == true ) {
            //光源位置の指定
            GLES20.glUniform4f(GLES.lightPosHandle,0f,10f,0f,1.0f);

            GLES.glPushMatrix();
            Matrix.rotateM( GLES.mMatrix, 0, RADER_VALUES.northDirection - RADER_VALUES.rotation, 0, 1, 0 );
            Matrix.rotateM( GLES.mMatrix, 0, RADER_VALUES.elevation+95, 1, 0, 0 );

            Matrix.translateM( GLES.mMatrix, 0, 0, 0, -RADER_VALUES.distance*2 );
//            Matrix.translateM( GLES.mMatrix, 0, 0, 0, -100 );
            GLES.glPushMatrix();
            GLES.updateMatrix();

            drawTarget(); // 立方体描画

            GLES.glPopMatrix();
            GLES.glPopMatrix();
        }
    }

    private void drawTarget() {
        //頂点バッファの指定
        GLES20.glVertexAttribPointer(GLES.positionHandle,3,
                GLES20.GL_FLOAT,false,0,vertexBuffer);

        //法線バッファの指定
        GLES20.glVertexAttribPointer(GLES.normalHandle,3,
                GLES20.GL_FLOAT,false,0,normalBuffer);

        if ( texID != 0 ) {
            //テクスチャの指定
            GLES20.glEnableVertexAttribArray(GLES.uvHandle);
            GLES20.glEnable(GLES20.GL_TEXTURE_2D);
            GLES20.glUniform1i(GLES.useTexHandle, 1);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texID);
            GLES20.glUniform1i(GLES.texHandle, 0);


            //UVバッファの指定
            GLES20.glVertexAttribPointer(GLES.uvHandle, 2,
                    GLES20.GL_FLOAT, false, 0, uvBuffer);
        }
        //面0の描画
        setMaterial(50f/255f,1f,219f/255f,1f);
        indexBuffer.position(0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP,
                4,GLES20.GL_UNSIGNED_BYTE,indexBuffer);

        if ( texID != 0 ) {
            GLES20.glDisable(GLES20.GL_TEXTURE_2D);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glUniform1i(GLES.useTexHandle, 0);
            GLES20.glDisableVertexAttribArray(GLES.uvHandle);
        }
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


    //テクスチャの生成
    private int makeTexture(Bitmap bmp) {
        //テクスチャメモリの確保
        int[] textureIds=new int[1];
        GLES20.glGenTextures(1,textureIds,0);

        //テクスチャへのビットマップ指定
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureIds[0]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D,0,bmp,0);

        //テクスチャフィルタの指定
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_NEAREST);
        return textureIds[0];
    }

}
