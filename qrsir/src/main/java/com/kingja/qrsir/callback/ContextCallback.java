package com.kingja.qrsir.callback;

import android.os.Handler;

import com.kingja.qrsir.ScanView;
import com.kingja.qrsir.camera.CameraManager;


/**
 * Description:TODO
 * Create Time:2018/1/31 15:42
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public interface ContextCallback {
    Handler getHandler();

    ScanView getScanView();

    CameraManager getCameraManager();
    void drawViewfinder();
    void setResult(String result);
}
