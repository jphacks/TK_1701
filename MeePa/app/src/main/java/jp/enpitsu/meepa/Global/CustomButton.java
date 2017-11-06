package jp.enpitsu.meepa.Global;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import jp.enpitsu.meepa.R;

/**
 * xmlでフォントを変更できるボタンのクラス
 */
public class CustomButton extends Button {

    private String mFont = "AbadiMTCondensedExtraBold.ttf";
    private String mFontDir = "fonts/";

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getFont(context, attrs);
        init();
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        getFont(context, attrs);
        init();
    }

    public CustomButton(Context context) {
        super(context);
        init();
    }


    /**
          * フォントファイルを読み込む
          *
          * @param context
          * @param attrs
          */
    private void getFont(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomButton);
        mFont = a.getString(R.styleable.CustomButton_font);
        if( mFont == null ) mFont = "AbadiMTCondensedExtraBold.ttf";
        a.recycle();
    }

    /**
      * フォントを反映
      */
    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), mFontDir + mFont);
        setTypeface(tf);

        setAllCaps(false);
    }


}
