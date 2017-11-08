package jp.enpitsu.meepa.Rader.ShareCamera;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;

import jp.enpitsu.meepa.R;

/**
 * Created by iyobe on 2016/02/24.
 */
public class LoadingView {
    Context mContext;
    ProgressDialog mProgressDialog;

    // コンストラクタ
    public LoadingView(Context context ){
        mContext = context;
        mProgressDialog = new ProgressDialog( context );

        mProgressDialog.getWindow().setBackgroundDrawable( new ColorDrawable(Color.TRANSPARENT ) );
    }

    // ロード画面表示
    public void show(){
        mProgressDialog.show();
        mProgressDialog.setContentView( R.layout.custom_progressdialog );
        mProgressDialog.setCancelable( false );
    }

    // ロード画面非表示
    public void close(){
        mProgressDialog.dismiss();
    }

    // Dialogを返す
    public static Dialog getDialog(Activity activity){
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_progressdialog);
        return dialog;
    }

}
