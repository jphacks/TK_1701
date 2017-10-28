package jp.enpitsu.meepa.Rader.ARObjects.OpenGLES20;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//レンダラー
public class MyRenderer implements GLSurfaceView.Renderer {
    //システム
    private float aspect;//アスペクト比

    private  MyGLSurfaceView glView;
    private  RaderObject_UI raderObject;
    private TargetObject targetObject;

    // コンストラクタ
    MyRenderer( MyGLSurfaceView glView, Context context ) {
        this.glView = glView;
        GLES.context = context;

//        mCamAngle = new float[] { 60f, 60f }; // カメラアングルを60度に初期化
    }


    // サーフェイス生成時に呼ばれる
    @Override
    public void onSurfaceCreated(GL10 gl10,EGLConfig eglConfig) {
        // プログラムの生成
        GLES.makeProgram();

        // 頂点配列の有効化
        GLES20.glEnableVertexAttribArray(GLES.positionHandle);
        GLES20.glEnableVertexAttribArray(GLES.normalHandle);

        // デプスバッファの有効化
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        // アルファブレンド有効化
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);

        //光源色の指定
        GLES20.glUniform4f(GLES.lightAmbientHandle,0.2f,0.2f,0.2f,1.0f);
        GLES20.glUniform4f(GLES.lightDiffuseHandle,0.7f,0.7f,0.7f,1.0f);
        GLES20.glUniform4f(GLES.lightSpecularHandle,0.0f,0.0f,0.0f,1.0f);

        raderObject = new RaderObject_UI();
        targetObject = new TargetObject();
    }

    //画面サイズ変更時に呼ばれる
    @Override
    public void onSurfaceChanged(GL10 gl10,int w,int h) {
        //ビューポート変換
        GLES20.glViewport(0,0,w,h);
        aspect=(float)w/(float)h;
    }

    //毎フレーム描画時に呼ばれる
    @Override
    public void onDrawFrame(GL10 gl10) {
        // 画面をglClearColorで指定した色で初期化
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|
                GLES20.GL_DEPTH_BUFFER_BIT);

        //射影変換
        Matrix.setIdentityM(GLES.pMatrix,0);
        GLES.gluPerspective(GLES.pMatrix,
                60.0f,  //Y方向の画角
                aspect, //アスペクト比
                0.1f,   //ニアクリップ
                1000.0f);//ファークリップ

        //光源位置の指定
        GLES20.glUniform4f(GLES.lightPosHandle,0f,0f,0f,1.0f);


        Matrix.setIdentityM(GLES.mMatrix,0);
        //ビュー変換
        GLES.gluLookAt(GLES.mMatrix,
                0.0f,0.0f,0.0f, //カメラの視点
                0.0f,0.0f,-0.01f, //カメラの焦点
                0.0f,1.0f,0.0f);//カメラの上方向

        // ARのターゲット描画
        targetObject.draw();
        // レーダー描画
        raderObject.draw();

    }
}
