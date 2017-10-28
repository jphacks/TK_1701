package jp.enpitsu.meepa.Rader.ARObjects.OpenGLES20;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.util.ArrayList;

//シェーダ操作
public class GLES {

    //頂点シェーダのコード
    private final static String VERTEX_CODE=
            //光源
            "uniform vec4 u_LightAmbient;"+ //光源の環境光色
                    "uniform vec4 u_LightDiffuse;"+ //光源の拡散光色
                    "uniform vec4 u_LightSpecular;"+//光源の鏡面光色
                    "uniform vec4 u_LightPos;"+     //光源の位置

                    //マテリアル
                    "uniform vec4 u_MaterialAmbient;"+   //マテリアルの環境光色
                    "uniform vec4 u_MaterialDiffuse;"+   //マテリアルの拡散光色
                    "uniform vec4 u_MaterialSpecular;"+  //マテリアルの鏡面光色
                    "uniform float u_MaterialShininess;"+//マテリアルの鏡面指数

                    //行列
                    "uniform mat4 u_MMatrix;"+     //モデルビュー行列
                    "uniform mat4 u_PMatrix;"+     //射影行列
                    "uniform mat4 u_NormalMatrix;"+//モデルビュー行列の逆転置行列

                    //頂点情報
                    "attribute vec4 a_Position;"+//位置
                    "attribute vec3 a_Normal;"+  //法線

                    // UV情報
                    "attribute vec2 a_UV;"+
                    "varying vec2 v_UV;"+

                    //出力
                    "varying vec4 v_Color;"+
                    "void main(){"+
                        //環境光の計算
                        "vec4 ambient=u_LightAmbient*u_MaterialAmbient;"+

                        //拡散光の計算
                        "vec3 P=vec3(u_MMatrix*a_Position);"+
                        "vec3 L=normalize(vec3(u_LightPos)-P);"+
                        "vec3 N=normalize(mat3(u_NormalMatrix)*a_Normal);"+
                        "vec4 diffuseP=vec4(max(dot(L,N),0.0));"+
                        "vec4 diffuse=diffuseP*u_LightDiffuse*u_MaterialDiffuse;"+

                        //鏡面光の計算
                        "vec3 S=normalize(L+vec3(0.0,0.0,1.0));"+
                        "float specularP=pow(max(dot(N,S),0.0),u_MaterialShininess);"+
                        "vec4 specular=specularP*u_LightSpecular*u_MaterialSpecular;"+

                        //色の指定
                        "v_Color=ambient+diffuse+specular;"+

                        //位置の指定
                        "gl_Position=u_PMatrix*u_MMatrix*a_Position;"+

                        "v_UV=a_UV;"+
                    "}";

    //フラグメントシェーダのコード
    private final static String FRAGMENT_CODE=
            "precision mediump float;"+
                    //テクスチャ
                    "uniform sampler2D u_Tex;"+ // テクスチャ
                    "uniform int u_UseTex;"+    //テクスチャ利用

                    // 入力
                    "varying vec4 v_Color;"+ // 色
                    "varying vec2 v_UV;"+    // UV
                    "void main(){"+
                        "if (u_UseTex==1){"+
                            "gl_FragColor=texture2D(u_Tex,v_UV);"+
                        "}else {"+
                            "gl_FragColor=v_Color;"+
                        "}"+
                    "}";


    //システム
    private static int program;//プログラムオブジェクト
    public static Context context;

    //光源のハンドル
    public static int lightAmbientHandle; //光源の環境光色ハンドル
    public static int lightDiffuseHandle; //光源の拡散光色ハンドル
    public static int lightSpecularHandle;//光源の鏡面光色ハンドル
    public static int lightPosHandle;     //光源の位置ハンドル

    //マテリアルのハンドル
    public static int materialAmbientHandle;  //マテリアルの環境光色ハンドル
    public static int materialDiffuseHandle;  //マテリアルの拡散光色ハンドル
    public static int materialSpecularHandle; //マテリアルの鏡面光色ハンドル
    public static int materialShininessHandle;//マテリアルの鏡面指数ハンドル

    //行列のハンドル
    public static int mMatrixHandle;     //モデルビュー行列ハンドル
    public static int pMatrixHandle;     //射影行列ハンドル
    public static int normalMatrixHandle;//モデルビュー行列の逆転置行列ハンドル

    //頂点のハンドル
    public static int positionHandle;//位置ハンドル
    public static int normalHandle;  //法線ハンドル
    public static int uvHandle;      //UVハンドル

    // テクスチャ関連のハンドル
    public static int texHandle;     //テクスチャハンドル
    public static int useTexHandle;   //テクスチャの利用ハンドル

    //行列
    public static float[] mMatrix=new float[16];//モデルビュー行列
    public static float[] pMatrix=new float[16];//射影行列

    private static ArrayList<float[]> mMatrixs=new ArrayList<float[]>();


    //プログラムの生成
    public static void makeProgram() {
        //シェーダーオブジェクトの生成
        int vertexShader=loadShader(GLES20.GL_VERTEX_SHADER,VERTEX_CODE);
        int fragmentShader=loadShader(GLES20.GL_FRAGMENT_SHADER,FRAGMENT_CODE);

        //プログラムオブジェクトの生成
        program=GLES20.glCreateProgram();
        GLES20.glAttachShader(program,vertexShader);
        GLES20.glAttachShader(program,fragmentShader);
        GLES20.glLinkProgram(program);

        //光源のハンドルの取得
        lightAmbientHandle=GLES20.glGetUniformLocation(program,"u_LightAmbient");
        lightDiffuseHandle=GLES20.glGetUniformLocation(program,"u_LightDiffuse");
        lightSpecularHandle=GLES20.glGetUniformLocation(program,"u_LightSpecular");
        lightPosHandle=GLES20.glGetUniformLocation(program,"u_LightPos");

        //マテリアルのハンドルの取得
        materialAmbientHandle=GLES20.glGetUniformLocation(program,"u_MaterialAmbient");
        materialDiffuseHandle=GLES20.glGetUniformLocation(program,"u_MaterialDiffuse");
        materialSpecularHandle=GLES20.glGetUniformLocation(program,"u_MaterialSpecular");
        materialShininessHandle=GLES20.glGetUniformLocation(program,"u_MaterialShininess");

        //行列のハンドルの取得
        mMatrixHandle=GLES20.glGetUniformLocation(program,"u_MMatrix");
        pMatrixHandle=GLES20.glGetUniformLocation(program,"u_PMatrix");
        normalMatrixHandle=GLES20.glGetUniformLocation(program,"u_NormalMatrix");

        //頂点のハンドルの取得
        positionHandle=GLES20.glGetAttribLocation(program,"a_Position");
        normalHandle=GLES20.glGetAttribLocation(program,"a_Normal");
        uvHandle =GLES20.glGetAttribLocation(program,"a_UV");

        // テクスチャ関連のハンドルの取得
        texHandle=GLES20.glGetUniformLocation(program,"u_Tex");
        useTexHandle=GLES20.glGetUniformLocation(program,"u_UseTex");

        //初期値
        GLES20.glUniform1f( useTexHandle, 0 );

        //プログラムオブジェクトの利用開始
        GLES20.glUseProgram(program);
    }

    //シェーダーオブジェクトの生成
    private static int loadShader(int type,String shaderCode) {
        int shader=GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader,shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    //透視変換の指定
    public static void gluPerspective(float[] m,
                                      float angle,float aspect,float near,float far) {
        float top=near*(float)Math.tan(angle*(Math.PI/360.0));
        float bottom=-top;
        float left=bottom*aspect;
        float right=top*aspect;
        float[] frustumM=new float[16];
        float[] resultM=new float[16];
        Matrix.frustumM(frustumM,0,left,right,bottom,top,near,far);
        Matrix.multiplyMM(resultM,0,m,0,frustumM,0);
        System.arraycopy(resultM,0,m,0,16);
    }

    //ビュー変換の指定
    public static void gluLookAt(float[] m,
                                 float eyeX,float eyeY,float eyeZ,
                                 float focusX,float focusY,float focusZ,
                                 float upX,float upY,float upZ) {
        float[] lookAtM=new float[16];
        float[] resultM=new float[16];
        Matrix.setLookAtM(lookAtM,0,
                eyeX,eyeY,eyeZ,focusX,focusY,focusZ,upX,upY,upZ);
        Matrix.multiplyMM(resultM,0,m,0,lookAtM,0);
        System.arraycopy(resultM,0,m,0,16);
    }

    //行列をシェーダに指定
    public static void updateMatrix() {
        //射影行列をシェーダに指定
        GLES20.glUniformMatrix4fv(pMatrixHandle,1,false,pMatrix,0);

        //モデルビュー行列をシェーダに指定
        GLES20.glUniformMatrix4fv(mMatrixHandle,1,false,mMatrix,0);

        //モデルビュー行列の逆転置行列の指定
        float[] normalM=new float[16];
        normalM(normalM,mMatrix);
        GLES20.glUniformMatrix4fv(normalMatrixHandle,1,false,normalM,0);
    }

    //行列の逆転置行列の計算(4)
    public static void normalM(float[] rm,float[] m) {
        float[] invertM=new float[16];
        Matrix.invertM(invertM,0,m,0);
        Matrix.transposeM(rm,0,invertM,0);
    }


    //行列のプッシュ
    public static void glPushMatrix() {
        float[] m=new float[16];
        System.arraycopy(mMatrix,0,m,0,16);
        mMatrixs.add(m);
    }

    //行列のポップ
    public static void glPopMatrix() {
        if (mMatrixs.size()==0) return;
        float[] m=mMatrixs.remove(mMatrixs.size()-1);
        System.arraycopy(m,0,mMatrix,0,16);
    }
}