package jp.enpitsu.meepa.Rader;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by iyobe on 2016/12/05.
 * カメラとGPSの
 * パーミッション取得とかしてけろ
 */
public class PermissionManager extends RaderActivity {

    private int REQUEST_CODE_CAMERA_PERMISSION = 0x01;
    private int REQUEST_CODE_LOCATION_PERMISSION = 0x02;

    RaderActivity raderActivity;

    PermissionManager( RaderActivity raderActivity ) {
        this.raderActivity = raderActivity;
    }

    // カメラのパーミッション
    public void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale( raderActivity,
                Manifest.permission.CAMERA)) {

            Log.d( "REQUEST PERMISSION", "shouldShowRequestPermissionRationale:追加説明");
            // 権限チェックした結果、持っていない場合はダイアログを出す
            new AlertDialog.Builder( raderActivity )
                    .setTitle("パーミッションの追加説明")
                    .setMessage("AR機能を利用するにはパーミッションが必要です")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions( raderActivity,
                                    new String[]{ Manifest.permission.CAMERA },
                                    REQUEST_CODE_CAMERA_PERMISSION );
                        }
                    })
                    .create()
                    .show();
            return;
        }

        // 権限を取得する
        ActivityCompat.requestPermissions( raderActivity, new String[]{
                        Manifest.permission.CAMERA
                },
                REQUEST_CODE_CAMERA_PERMISSION);
        return;
    }

    // 位置情報のパーミッション
    public void requestLocationInfoPermission() {
        if ( ActivityCompat.shouldShowRequestPermissionRationale( raderActivity,
                Manifest.permission.ACCESS_FINE_LOCATION ) ) {

            Log.d( "REQUEST PERMISSION", "shouldShowRequestPermissionRationale:追加説明");
            // 権限チェックした結果、持っていない場合はダイアログを出す
            new AlertDialog.Builder( raderActivity )
                    .setTitle( "パーミッションの追加説明" )
                    .setMessage( "この機能を利用するにはパーミッションが必要です" )
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions( raderActivity,
                                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                                    REQUEST_CODE_LOCATION_PERMISSION );
                        }
                    })
                    .create()
                    .show();
            return;
        }

        // 権限を取得する
        ActivityCompat.requestPermissions( raderActivity, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                },
                REQUEST_CODE_LOCATION_PERMISSION );
        return;
    }

    public void onRequestPermissionsResult( int requestCode,
                                           String permissions[], int[] grantResults ) {

        if ( requestCode == REQUEST_CODE_CAMERA_PERMISSION ) {
            onRequestCameraPermission( grantResults );
        }
        else if ( requestCode == REQUEST_CODE_LOCATION_PERMISSION ) {
            onRequestLocationInfoPermission( grantResults );
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        //Fragmentの場合はgetContext().getPackageName()
        Uri uri = Uri.fromParts( "package", raderActivity.getPackageName(), null );
        intent.setData( uri );
        raderActivity.startActivity( intent );
    }

    // カメラのパーミッション確認ダイアログの結果に応じた処理
    private void onRequestCameraPermission( int[] grantResults ) {
        if (grantResults.length != 1 ||
                grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Log.d("REQUEST PERMISSION", "onRequestPermissionsResult:DENYED");

            if (ActivityCompat.shouldShowRequestPermissionRationale( raderActivity,
                    Manifest.permission.CAMERA)) {
                Log.d("REQUEST PERMISSION", "[show error]");
                new AlertDialog.Builder( raderActivity )
                        .setTitle("パーミッション取得エラー")
                        .setMessage("再試行する場合は、再度[AR ON/OFF]ボタンを押してください")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // 許可されなかった場合
                                // ARモード終了
                                raderActivity.button_AR.setChecked( false );
                            }
                        })
                        .create()
                        .show();

            } else {
                Log.d("REQUEST PERMISSION", "[show app settings guide]");
                new AlertDialog.Builder( raderActivity )
                        .setTitle("パーミッション取得エラー")
                        .setMessage("今後は許可しないが選択されました。アプリ設定＞権限をチェックしてください（権限をON/OFFすることで状態はリセットされます）")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                openSettings();
                            }
                        })
                        .create()
                        .show();
                // 許可されなかった場合②
                raderActivity.button_AR.setChecked( false );
            }
        } else {
            Log.d("REQUEST PERMISSION", "onRequestPermissionsResult:GRANTED");
            // 許可されたのでカメラにアクセス(AR起動)
            raderActivity.startARMode();
        }
    }

    // 位置情報のパーミッション確認ダイアログの結果に応じた処理
    private void onRequestLocationInfoPermission( int[] grantResults ) {
        if ( grantResults.length != 1 ||
                grantResults[0] != PackageManager.PERMISSION_GRANTED ) {
            Log.d("REQUEST PERMISSION", "onRequestPermissionsResult:DENYED");

            if (ActivityCompat.shouldShowRequestPermissionRationale( raderActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION ) ) {
                Log.d("REQUEST PERMISSION", "[show error]");
                new AlertDialog.Builder( raderActivity )
                        .setTitle("パーミッション取得エラー")
                        .setMessage("再試行する場合は、再度[Search]ボタンを押してください")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // 許可されなかった場合
                                // RaderActivity終了
                                raderActivity.finish();
                            }
                        })
                        .create()
                        .show();

            } else {
                Log.d("REQUEST PERMISSION", "[show app settings guide]");
                new AlertDialog.Builder( raderActivity )
                        .setTitle("パーミッション取得エラー")
                        .setMessage("今後は許可しないが選択されました。アプリ設定＞権限をチェックしてください（権限をON/OFFすることで状態はリセットされます）")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                openSettings();

                                raderActivity.finish();
                            }
                        })
                        .create()
                        .show();
                // 許可されなかった場合②
            }
        } else {
            Log.d("REQUEST PERMISSION", "onRequestPermissionsResult:GRANTED");
            // 許可された
            return;
        }
    }

}
