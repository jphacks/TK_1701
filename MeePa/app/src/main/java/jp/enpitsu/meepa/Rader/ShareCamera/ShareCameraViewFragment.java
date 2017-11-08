package jp.enpitsu.meepa.Rader.ShareCamera;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jp.enpitsu.meepa.R;

/**
 * Created by ishilab on 2017/11/08.
 */
public class ShareCameraViewFragment extends Fragment {
    /* ---------------------------------------------------------------------- */
    /* Field                                                                  */
    /* ---------------------------------------------------------------------- */
    public static final String TAG = ShareCameraViewFragment.class.getSimpleName();
    private Activity activity = null;
    private View view = null;
    //private ${NAME}Listener listener = null;

    private String selfName, selfID, oppName, oppID;
    private boolean isInitUserInfo = false;

    /* ---------------------------------------------------------------------- */
    /* Listener                                                               */
    /* ---------------------------------------------------------------------- */
//    public interface ${NAME}Listener {
//        void onHogeEvent();
//    }

    /* ---------------------------------------------------------------------- */
    /* Lifecycle                                                              */
    /* ---------------------------------------------------------------------- */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach");
//        if (!(activity instanceof ${NAME}Listener)) {
//        throw new UnsupportedOperationException(
//        TAG + ":" + "Listener is not Implementation.");
//        } else {
//        listener = (${NAME}Listener) activity;
//        }

        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_sharecameraview, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");


        

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }


    public void setUserInfo( String selfID, String selfName, String oppID, String oppName ) {
        this.selfID   = selfID;
        this.selfName = selfName;
        this.oppID    = oppID;
        this.oppName  = oppName;

        isInitUserInfo = true;
    }

}

//
//public class ShareCameraViewFragment extends Fragment {
//
//    private TextView mTextView;
//
//
//    // Fragmentで表示するViewを作成するメソッド
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        super.onCreateView(inflater, container, savedInstanceState);
//        // 先ほどのレイアウトをここでViewとして作成します
//        return inflater.inflate(R.layout.fragment_sharecameraview, container, false);
//
//    }
//
//
//    // Viewが生成し終わった時に呼ばれるメソッド
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        // TextViewをひも付けます
//        mTextView = (TextView) view.findViewById(R.id.textView_fragmentTest);
//        // Buttonのクリックした時の処理を書きます
//        view.findViewById(R.id.push_buttonTest).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mTextView.setText(mTextView.getText() + "!");
//            }
//        });
//
//    }
//
//}
