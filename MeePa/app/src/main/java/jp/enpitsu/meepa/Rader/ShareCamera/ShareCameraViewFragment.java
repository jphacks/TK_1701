package jp.enpitsu.meepa.Rader.ShareCamera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;

import io.skyway.Peer.Browser.Canvas;
import io.skyway.Peer.Browser.MediaConstraints;
import io.skyway.Peer.Browser.MediaStream;
import io.skyway.Peer.Browser.Navigator;
import io.skyway.Peer.CallOption;
import io.skyway.Peer.MediaConnection;
import io.skyway.Peer.OnCallback;
import io.skyway.Peer.Peer;
import io.skyway.Peer.PeerError;
import io.skyway.Peer.PeerOption;

import jp.enpitsu.meepa.R;
import jp.enpitsu.meepa.Rader.RaderActivity;

/**
 * Created by ishilab on 2017/11/08.
 */
public class ShareCameraViewFragment extends Fragment {
    /* ---------------------------------------------------------------------- */
    /* Field                                                                  */
    /* ---------------------------------------------------------------------- */
    public static final String TAG = ShareCameraViewFragment.class.getSimpleName();
    private RaderActivity activity = null;
    private View view = null;
    //private ${NAME}Listener listener = null;

    private String selfName, selfID, oppName, oppID;

    //
    // Set your APIkey and Domain
    //
    private static final String API_KEY = "29e9e10c-d881-43eb-a184-c01780af1deb";
    private static final String DOMAIN = "localhost";

    //
    // declaration
    //
    private Peer _peer;			       	// Peerオブジェクト
    private MediaStream _localStream;		// 自分自身のMediaStreamオブジェクト
    private MediaStream _remoteStream;		// 相手のMediaStreamオブジェクト
    private MediaConnection _mediaConnection;	// MediaConnectionオブジェクト

    private String			_strOwnId;
    private boolean		_bConnected = false;
    private boolean		_bExistPeerID = false;

    private Handler _handler;

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

        this.activity = (RaderActivity)activity;
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

        Bundle bundle = getArguments();
        selfID   = bundle.getString("selfID");
        selfName = bundle.getString("selfName");
        oppID    = bundle.getString("oppID");
        oppName  = bundle.getString("oppName");

        // TODO : デバック用
        oppID = "paseri";

        //
        // Set UI handler
        //UIスレッド処理のためのHandlerを生成
        //
        _handler = new Handler(Looper.getMainLooper());

        //
        // Initialize Peer
        // PeerOptionクラスを利用し、APIキー、ドメイン名、デバッグレベルを指定
        //
        PeerOption option = new PeerOption();
        option.key = API_KEY;
        option.domain = DOMAIN;
        option.debug = Peer.DebugLevelEnum.ALL_LOGS;

        _peer = new Peer( activity, selfID, option ); // 自分のIDをPeerIDとしてPeerID発行

        //
        // Set Peer event callbacks
        // 接続成功・失敗・切断時の処理
        //

        // OPEN
        // このイベント成功後にいろいろ行われるよ
		/*
		 * PeerIDと呼ばれるクライアント識別用のIDがシグナリングサーバで発行され、
		 * コールバックイベントで取得できます。
		 * PeerIDはクライアントサイドで指定することもできます。
		 */
        _peer.on(Peer.PeerEventEnum.OPEN, new OnCallback() {
            @Override
            public void onCallback(Object object) {
                // PeerIDが発行されたらそれを表示
                // Show my ID
                _strOwnId = (String) object;
                _bExistPeerID = true;
                Log.d(TAG, "【MY PEER ID】 " + _strOwnId);
//                TextView tvOwnId = (TextView) findViewById( R.id.tvOwnId );
//                tvOwnId.setText(myName + " ( ID : " + _strOwnId + " )");

            }
        });


        // Request permissions
        // カメラ，(音声)のパーミッション
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
//                && ContextCompat.checkSelfPermission(activity,
//                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                ) {
//            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},0);
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.CAMERA},0);
        }
        else {
            // 権限がある場合
            // Get a local MediaStream & show it
//            startLocalStream(); // 映像，音声の取得開始
        }


        // ERROR
        // 何かしらエラーが発生したら呼ばれる→ログ表示
        _peer.on(Peer.PeerEventEnum.ERROR, new OnCallback() {
            @Override
            public void onCallback(Object object) {
                PeerError error = (PeerError) object;
                Log.d(TAG, "[On/Error]" + error);
            }
        });

        // CLOSE
        // Peer（相手）との接続が切れた際に呼ばれる
        _peer.on(Peer.PeerEventEnum.CLOSE, new OnCallback()	{
            @Override
            public void onCallback(Object object) {
                Log.d(TAG, "[On/Close]");
            }
        });

        // DISCONNECTED
        // シグナリングサーバとの接続が切れた際に呼ばれる
        _peer.on(Peer.PeerEventEnum.DISCONNECTED, new OnCallback() {
            @Override
            public void onCallback(Object object) {
                Log.d(TAG, "[On/Disconnected]");
            }
        });

        // CALL (Incoming call)
        // 着信処理
        // 相手から接続要求がきた場合に応答
        _peer.on(Peer.PeerEventEnum.CALL, new OnCallback() {
            @Override
            public void onCallback(Object object) { // 引数として相手との接続を管理するためのMediaConnectionオブジェクトが取得できる
                if (!(object instanceof MediaConnection)) {
                    return;
                }

                _mediaConnection = (MediaConnection) object;

                // TODO : ここで接続確認ダイアログ /////////////////////////////////////////////////
                new AlertDialog.Builder(getActivity())
                        .setTitle("接続要求")
                        .setMessage("相手が視点共有をしたいようです\n自分の視点（カメラ映像）を相手に共有しますか？")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // OK button pressed
                                activity.requestShowFragment();
                                startLocalStream(); // 自分側の映像取得開始

                                setMediaCallbacks();
                                _mediaConnection.answer(_localStream); // 接続要求に応答
                                // 引数に相手に送信する映像・音声（自分のストリーム）

                                _bConnected = true;
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

                // TODO : 切断処理
                _mediaConnection.close();
                _bConnected = false;

                activity.requestHideFragment();

//                updateActionButtonTitle();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        // Disable Sleep and Screen Lock
        Window wnd = activity.getWindow();
        wnd.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        wnd.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        // Set volume control stream type to WebRTC audio.
//        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    @Override
    public void onStop() {
        // Enable Sleep and Screen Lock
        Window wnd = activity.getWindow();
        wnd.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        wnd.clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onPause() {
        // Set default volume control stream type.
//        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);

        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        try {
            destroyPeer();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }


    //
    // Get a local MediaStream & show it
    //
    public void startLocalStream() {

        // MediaConstraintsクラスでカメラ映像・マイク音声取得に関するオプションを設定可能
        MediaConstraints constraints = new MediaConstraints();

        constraints.audioFlag = false; // 音声は扱いません

        // キャプチャ映像の横サイズ上限の設定（単位：ピクセル）
//		constraints.maxWidth = 960;
//		constraints.maxHeight = 540;
        constraints.maxWidth = 1000;
        constraints.maxHeight = 1000;
        // 前面（FRONT），背面（BACK）どちらのカメラを使うか
        constraints.cameraPosition = MediaConstraints.CameraPositionEnum.BACK;

        // 取得と再生
        Navigator.initialize(_peer);								// Navigatorクラスの初期化
        _localStream = Navigator.getUserMedia(constraints);		// getUserMediaメソッドの引数にconstraintsを指定
        // 自分のカメラ映像（ローカルストリーム）が取得可能
//
        Canvas canvas = (Canvas)view.findViewById(R.id.svLocalView);	// 映像表示用のcanvas
        // 取得したMediaStreamオブジェクトにビデオレンダラー(表示用のCanvasオブジェクト)を割り当て
        _localStream.addVideoRenderer(canvas,0);

        Log.d(TAG,"START LOCAL STREAM");
    }


    //
    // Set callbacks for MediaConnection.MediaEvents
    // MediaConnectionオブジェクトに必要なイベントコールバック
    void setMediaCallbacks() {

        // MediaConnection.MediaEventEnum.STREAMは相手のカメラ映像・マイク音声を受信した際に発火

        _mediaConnection.on(MediaConnection.MediaEventEnum.STREAM, new OnCallback() {
            @Override
            public void onCallback(Object object) {
                _remoteStream = (MediaStream) object;
                Canvas canvas = (Canvas)view.findViewById(R.id.svRemoteView);
                _remoteStream.addVideoRenderer(canvas,0); // 相手のMediaStreamオブジェクトをレンダラに登録
                Log.d(TAG, "相手のメディアストリームセット");
            }
        });


        _mediaConnection.on(MediaConnection.MediaEventEnum.CLOSE, new OnCallback()	{
            @Override
            public void onCallback(Object object) {
                closeRemoteStream();
                _bConnected = false;

                activity.requestHideFragment();
//                updateActionButtonTitle();
            }
        });


        _mediaConnection.on(MediaConnection.MediaEventEnum.ERROR, new OnCallback()	{
            @Override
            public void onCallback(Object object) {
                PeerError error = (PeerError) object;
                Log.d(TAG, "[On/MediaError]" + error);
            }
        });

    }


    //
    // Clean up objects
    //
	/* やってること --------------------------------------------------------------------------
	 * リモート/ローカルのメディアストリームのクローズ
	 * MediaConnectionオブジェクトに関するコールバックイベントの開放(unsetMediaCallbacks)
	 * Navigatorオブジェクトの初期化
	 * Peerオブジェクトに関するコールバックイベントの開放(unsetPeerCallback)
	 * シグナリングサーバとの切断とPeerオブジェクトの破棄
	 * ----------------------------------------------------------------------------------------
	 */
    private void destroyPeer() {
        closeRemoteStream();

        if (null != _localStream) {
//            Canvas canvas = (Canvas) findViewById(R.id.svLocalView);
//            _localStream.removeVideoRenderer(canvas,0);
            _localStream.close();
        }

        if (null != _mediaConnection)	{
            if (_mediaConnection.isOpen()) {
                _mediaConnection.close();
            }
            unsetMediaCallbacks();
        }

        Navigator.terminate();

        if (null != _peer) {
            unsetPeerCallback(_peer);
            if (!_peer.isDisconnected()) {
                _peer.disconnect();
            }

            if (!_peer.isDestroyed()) {
                _peer.destroy();
            }

            _peer = null;
        }
    }


    //
    // Unset callbacks for PeerEvents
    //
    void unsetPeerCallback(Peer peer) {
        if(null == _peer){
            return;
        }

        peer.on(Peer.PeerEventEnum.OPEN, null);
        peer.on(Peer.PeerEventEnum.CONNECTION, null);
        peer.on(Peer.PeerEventEnum.CALL, null);
        peer.on(Peer.PeerEventEnum.CLOSE, null);
        peer.on(Peer.PeerEventEnum.DISCONNECTED, null);
        peer.on(Peer.PeerEventEnum.ERROR, null);
    }


    //
    // Unset callbacks for MediaConnection.MediaEvents
    //
    void unsetMediaCallbacks() {
        if(null == _mediaConnection){
            return;
        }

        _mediaConnection.on(MediaConnection.MediaEventEnum.STREAM, null);
        _mediaConnection.on(MediaConnection.MediaEventEnum.CLOSE, null);
        _mediaConnection.on(MediaConnection.MediaEventEnum.ERROR, null);
    }


    //
    // Close a remote MediaStream
    //
    void closeRemoteStream(){
        if (null == _remoteStream) {
            return;
        }


        Canvas canvas = (Canvas)view.findViewById(R.id.svRemoteView);
        _remoteStream.removeVideoRenderer(canvas,0); // MediaStreamに割り当てられたビデオレンダラを取り外し
        _remoteStream.close();
    }


    //
    // Create a MediaConnection
    // 発信処理
    void onPeerSelected(String strPeerId) {
        if (null == _peer) {
            return;
        }

        if (null != _mediaConnection) {
            _mediaConnection.close();
        }
        activity.requestShowFragment();
        startLocalStream();


        // 相手のPeerID、自分自身のlocalStreamを引数にセットし発信
        CallOption option = new CallOption();
        _mediaConnection = _peer.call(strPeerId, _localStream, option);

        if (null != _mediaConnection) {
            setMediaCallbacks();
            _bConnected = true;
        }
        else {
            activity.requestHideFragment();
        }

//        updateActionButtonTitle();
    }


    //
    // Listing all peers
    //
    // showPeerIDsメソッドでは、listAllPeersメソッドを利用して、接続先のPeerID一覧を取得します。
    public int showPeerIDs() {
        if ((null == _peer) || (null == _strOwnId) || (0 == _strOwnId.length())) {
            Toast.makeText(activity, "Your PeerID is null or invalid.", Toast.LENGTH_SHORT).show();
            return -1;
        }

        // Get all IDs connected to the server
        final Context fContext = activity;
        _peer.listAllPeers(new OnCallback() {
            @Override
            public void onCallback(Object object) {
                if (!(object instanceof JSONArray)) {
                    return;
                }

                JSONArray peers = (JSONArray) object;
                ArrayList<String> _listPeerIds = new ArrayList<>();
                String peerId;

                // Exclude my own ID
                for (int i = 0; peers.length() > i; i++) {
                    try {
                        peerId = peers.getString(i);
                        if (!_strOwnId.equals(peerId)) {
                            _listPeerIds.add(peerId);
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }

                // Show IDs using DialogFragment
                if (0 < _listPeerIds.size()) {
                    for (int i = 0; peers.length() > i; i++) {
                        try {
                            peerId = peers.getString(i);
                            if ( oppID.equals(peerId) ) { // 相手のIDのpeerIDを見つけたとき
                                onPeerSelected( peerId ); // リストから選択されたら発信
                                break;
                            }
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    Toast.makeText(fContext, "PeerID list (other than your ID) is empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return 0;
    }


    //
    // onRequestPermissionResult
    //
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
//                    startLocalStream();
                }
                else {
//                    Toast.makeText(this,"Failed to access the camera and microphone.\nclick allow when asked for permission.", Toast.LENGTH_LONG).show();
                    Toast.makeText(activity,"Failed to access the camera.\nclick allow when asked for permission.", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }




    public boolean isExistPeerID() { return _bExistPeerID; }
    public boolean isConnected() { return _bConnected; }

    public void createPeer() {
        //
        // Initialize Peer
        // PeerOptionクラスを利用し、APIキー、ドメイン名、デバッグレベルを指定
        //
        PeerOption option = new PeerOption();
        option.key = API_KEY;
        option.domain = DOMAIN;
        option.debug = Peer.DebugLevelEnum.ALL_LOGS;

        _peer = new Peer( activity, selfID, option ); // 自分のIDをPeerIDとしてPeerID発行
    }

}
