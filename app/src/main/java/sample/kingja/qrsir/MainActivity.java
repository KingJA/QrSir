package sample.kingja.qrsir;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.kingja.qrsir.encoder.QRCodeEncoder;

public class MainActivity extends AppCompatActivity {

    private ImageView iv_qr_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_qr_img = findViewById(R.id.iv_qr_img);
    }

    public void createQrImg(View view) {
        Bitmap bitmap = QRCodeEncoder.createQr("KingJA", 600);
        if (bitmap != null) {
            iv_qr_img.setImageBitmap(bitmap);
        }
    }

    public void scan(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == 1) {
                String result = data.getStringExtra("result");
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
