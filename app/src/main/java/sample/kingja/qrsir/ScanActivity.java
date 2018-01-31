package sample.kingja.qrsir;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.kingja.qrsir.QrSir;
import com.kingja.qrsir.ScanView;

/**
 * Description:TODO
 * Create Time:2018/1/31 10:06
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class ScanActivity extends AppCompatActivity {
    private QrSir qrSir;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        //保持常亮
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(com.kingja.qrsir.R.layout.activity_scan);

        ScanView scanView = findViewById(com.kingja.qrsir.R.id.viewfinder_view);
        SurfaceView surfaceView = findViewById(com.kingja.qrsir.R.id.preview_view);
        qrSir = QrSir.getQrSir(this, scanView, surfaceView);
    }


    @Override
    protected void onResume() {
        super.onResume();
        qrSir.onResume();
    }


    @Override
    protected void onPause() {
        qrSir.onPause();
        super.onPause();
    }
}
