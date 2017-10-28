package jp.enpitsu.meepa.Rader.ARObjects.OpenGLES20;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;

/**
 * Created by soniyama on 2016/09/28.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private static final int OPENGL_ES_VERSION = 2;
    private MyRenderer mRenderer;

    Context context;

    public MyGLSurfaceView(Context context) {
        super(context);
        this.context = context;

        mRenderer = new MyRenderer( this, context );

        setEGLConfigChooser(8, 8, 8, 8, 0, 0); // setRendererする前にやらんとerror吐く
        setEGLContextClientVersion( OPENGL_ES_VERSION );
        setRenderer( mRenderer );
//        setRenderMode( RENDERMODE_WHEN_DIRTY ); // 描画命令時に描画
        setRenderMode( RENDERMODE_CONTINUOUSLY ); // 常時描画

        setZOrderOnTop(true);                            // 最前面に描画
        getHolder().setFormat(PixelFormat.TRANSLUCENT); // 透明部分を透過
    }

}
