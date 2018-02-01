package com.kingja.qrsir;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kingja.qrsir.callback.ContextCallback;
import com.kingja.qrsir.camera.CameraManager;
import com.kingja.qrsir.decode.CaptureActivityHandler;
import com.kingja.qrsir.view.ScanView;


/**
 * Description:TODO
 * Create Time:2018/1/31 16:26
 * @author:KingJA
 * Email:kingjavip@gmail.com
 */
public class QrSir implements SurfaceHolder.Callback, ContextCallback {
    private final String TAG = "QrSir";
    private ScanView scanView;
    private final SurfaceView surfaceView;
    private boolean hasSurface;
    private CaptureActivityHandler handler;
    private CameraManager cameraManager;
    private Activity activity;

    public QrSir(Activity activity, ScanView scanView, SurfaceView surfaceView) {
        this.activity = activity;
        this.scanView = scanView;
        this.surfaceView = surfaceView;
    }


    public static QrSir getQrSir(Activity qrActivity, ScanView scanView, SurfaceView surfaceView) {
        return new QrSir(qrActivity, scanView, surfaceView);
    }

    public void onResume() {
        cameraManager = new CameraManager(activity.getApplication());
//        /*设置扫描框尺寸*/
//        cameraManager.setManualFramingRect(200, 200);
//        /*选择相机 前1后0 CameraInfo.CAMERA_FACING_BACK*/
//        cameraManager.setManualCameraId(-1);
        scanView.setCameraManager(cameraManager);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            Log.e(TAG, "initCamera: ");
            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        } else {
            // Install the callback and wait for surfaceCreated() to init the camera.
            Log.e(TAG, "addCallback: ");
            surfaceHolder.addCallback(this);
        }
    }

    public void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        cameraManager.closeDriver();
        //historyManager = null; // Keep for onActivityResult
        if (!hasSurface) {
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated: ");
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed: ");
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // do nothing
        Log.e(TAG, "surfaceChanged: ");
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager);
            }
        } catch (Exception e) {
            Log.w(TAG, e);
        }
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    public void drawViewfinder() {
//        scanView.drawViewfinder();
    }

    @Override
    public void setResult(String result) {
        Intent intent = new Intent();
        intent.putExtra("result", result);
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
    }
}
