package xies.academy.hebuni.com.cateringordersystem.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class CateDetailActivity extends CommonActivity {
//    private static final int REQUEST_EXTERNAL_STORAGE = 1;
//    private static String[] PERMISSIONS_STORAGE = {
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//    };
    //参数
    private String cateImg;
    private String cateName;
    private String catePrice;
    private String cateDesc;
    //声明组件
    private TextView tvTitle;
    private ImageView ivCateImg;
    private TextView tvCateName;
    private TextView tvCatePrice;
    private TextView tvCateDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        int permission = ActivityCompat.checkSelfPermission(CateDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(
//                    CateDetailActivity.this,
//                    PERMISSIONS_STORAGE,
//                    REQUEST_EXTERNAL_STORAGE
//            );
//        }
        cateImg = getIntent().getStringExtra("cateImg");
        cateName = getIntent().getStringExtra("cateName");
        catePrice = getIntent().getStringExtra("catePrice");
        cateDesc = getIntent().getStringExtra("cateDesc");
        setContentView(R.layout.activity_cate_detail);
        initView();
        initData();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        tvTitle = findViewById(R.id.center);
        ivCateImg = findViewById(R.id.iv_cate_img);
        tvCateName = findViewById(R.id.tv_cate_name);
        tvCatePrice = findViewById(R.id.tv_cate_price);
        tvCateDesc = findViewById(R.id.tv_cate_desc);
    }

    /**
     * 数据填充
     */
    private void initData() {
        tvTitle.setText("菜品");
//        String s = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/123.jpg";
//        Uri uri = Uri.fromFile(new File(s));
//        ivCateImg.setImageURI(uri);
        try {
            InputStream inputStream = getAssets().open(cateImg);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ivCateImg.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tvCateName.setText(cateName);
        tvCatePrice.setText("￥"+catePrice);
        tvCateDesc.setText(cateDesc);
    }

}
