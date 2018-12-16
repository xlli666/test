package xies.academy.hebuni.com.cateringordersystem.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import xies.academy.hebuni.com.cateringordersystem.util.InfoUtil;

public class QrCodeActivity extends CommonActivity {
    private TextView tvTitle;
    private ImageView ivQrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        initView();
        initData();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        tvTitle = findViewById(R.id.center);
        ivQrCode = findViewById(R.id.iv_qr_code);
    }

    /**
     * 数据填充
     */
    private void initData() {
        tvTitle.setText("APP点餐");
        Bitmap bitmap = InfoUtil.createQRImg("http://www.baidu.com");
        ivQrCode.setImageBitmap(bitmap);
    }

}
