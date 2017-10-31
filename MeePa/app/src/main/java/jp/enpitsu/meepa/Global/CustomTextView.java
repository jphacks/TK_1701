package jp.enpitsu.meepa.Global;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatTextView;

import jp.enpitsu.meepa.R;

/*
 * 独自のフォント利用のためのテキストビュー
 * デフォルトのフォント : assets/fonts/AbadiMTCondensedExtraBold.ttf
 * xmlからフォント変更できるようになるよ！
 */
public class CustomTextView extends AppCompatTextView {

    private String mFont = "fonts/AbadiMTCondensedExtraBold.ttf";

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getFont(context, attrs);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getFont(context, attrs);
        init();
    }

    public CustomTextView(Context context) {
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
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        mFont = a.getString(R.styleable.CustomTextView_font);
        a.recycle();
    }

    /**
      * フォントを反映
      */
    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), mFont);
        setTypeface(tf);
    }
}