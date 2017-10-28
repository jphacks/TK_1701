package jp.enpitsu.meepa.Rader.ARObjects.OpenGLES20;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Created by iyobe on 2016/10/26.
 */
public class CircleBuffers {
    FloatBuffer vertexBuffer;//頂点バッファ
    ByteBuffer indexBuffer; //インデックスバッファ
    FloatBuffer normalBuffer;//法線バッファ

    CircleBuffers( FloatBuffer vertexBuffer, ByteBuffer indexBuffer, FloatBuffer normalBuffer ) {
//    CircleBuffers( FloatBuffer vertexBuffer, ByteBuffer indexBuffer ) {
        this.vertexBuffer = vertexBuffer;
        this.indexBuffer = indexBuffer;
        this.normalBuffer = normalBuffer;
    }
}
