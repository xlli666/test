package xies.academy.hebuni.com.cateringordersystem.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import xies.academy.hebuni.com.cateringordersystem.fragment.CartFragment;
import xies.academy.hebuni.com.cateringordersystem.fragment.CateFragment;
import xies.academy.hebuni.com.cateringordersystem.fragment.HomeFragment;
import xies.academy.hebuni.com.cateringordersystem.fragment.UserFragment;
import xies.academy.hebuni.com.cateringordersystem.util.InfoUtil;

/**
 * 使用Fragment的主界面
 */
public class FragMainActivity extends CommonActivity {
    //权限
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    TextBadgeItem textBadgeItem;
    //参数
    private SharedPreferences sharedOrdersInfo;
    //声明组件
    private Toolbar toolbar;
    private BottomNavigationBar mBottomNavigationBar;
    BottomNavBarListener bottomNavBarListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int permission = ActivityCompat.checkSelfPermission(FragMainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    FragMainActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        sharedOrdersInfo = getSharedPreferences("ORDER_INFO", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main_frag);
        initView();
        initData();
        initAction();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.startCall:
                startCall();
                break;
            case R.id.appShare:
                appShare();
                break;
            case R.id.appQrCode:
                appQrCode();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 初始化组件
     */
    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        mBottomNavigationBar = findViewById(R.id.bottom_navigation_bar);
    }


    /**
     * 底部导航数据样式修改
     */
    private void initData() {
        //设置圆点
        textBadgeItem = new TextBadgeItem();
//        textBadgeItem.setHideOnSelect(false).setText(modifyDotNum()).setBackgroundColor(R.color.orange).setBorderWidth(0);
        modifyDot();
        //样式设置
        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_SHIFTING);
        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        mBottomNavigationBar.setBarBackgroundColor(R.color.colorPrimaryDark);
        mBottomNavigationBar.setInActiveColor(R.color.lime);
        //添加item、icon和名称
        List<BottomNavigationItem> items = new ArrayList<>();
        items.add(new BottomNavigationItem(R.mipmap.icon_one,R.string.tab_one).setActiveColor(R.color.green));
        items.add(new BottomNavigationItem(R.mipmap.icon_two,R.string.tab_two).setActiveColor(R.color.green));
        items.add(new BottomNavigationItem(R.mipmap.icon_three,R.string.tab_three).setActiveColor(R.color.green).setBadgeItem(textBadgeItem));
        items.add(new BottomNavigationItem(R.mipmap.icon_four,R.string.tab_four).setActiveColor(R.color.green));
        for (BottomNavigationItem item : items) {
            mBottomNavigationBar.addItem(item);
        }
        mBottomNavigationBar.setFirstSelectedPosition(0).initialise();
    }

    /**
     * 导航栏设置事件监听
     */
    private void initAction() {
        setSupportActionBar(toolbar);
        bottomNavBarListener = new BottomNavBarListener();
        mBottomNavigationBar.setTabSelectedListener(bottomNavBarListener);
        //APP默认首页FRAGMENT
        replaceFragment(new HomeFragment());
    }

    /**
     *  fragment页面切换
     *
     * @param fragment fragment页
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.ll_content, fragment);
//        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * 修改圆点数据
     */
    public void modifyDot() {
        textBadgeItem.setHideOnSelect(false).setText(getDotsNum()).setBackgroundColor(R.color.orange).setBorderWidth(0);
        if ("0".equals(getDotsNum())) {
            textBadgeItem.hide();
        } else {
            textBadgeItem.show();
        }
    }

    /**
     * 获取圆点数据
     * @return 圆点数据
     */
    private String getDotsNum() {
        String orders = sharedOrdersInfo.getString("orders","");
        int cateCount = 0;
        try {
            JSONArray jsonArray = new JSONArray(orders);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                cateCount += jsonObject.optInt("CATE_NUM");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return String.valueOf(cateCount);
    }

    /**
     * 呼出电话界面
     */
    private void startCall() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:18846441562"));
        startActivity(intent);
    }

    /**
     * 分享APP下载
     */
    private void appShare() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        Bitmap bitmap = InfoUtil.createQRImg("http://www.baidu.com");
        String s = InfoUtil.bitmapFilePath(bitmap);
        Uri priUri = FileProvider.getUriForFile(this,
                "xies.academy.hebuni.com.cateringordersystem.activity.fileprovider", new File(s));
        intent.putExtra(Intent.EXTRA_STREAM, priUri);
        intent.setType("image/*");
        startActivity(Intent.createChooser(intent, "分享到"));
    }

    /**
     * APP显示二维码
     */
    private void appQrCode() {
        Intent intent = new Intent(FragMainActivity.this,QrCodeActivity.class);
        startActivity(intent);
    }

    private class BottomNavBarListener implements BottomNavigationBar.OnTabSelectedListener {

        @Override
        public void onTabSelected(int position) {
            switch (position) {
                case 0:
                    replaceFragment(new HomeFragment());
                    break;
                case 1:
                    replaceFragment(new CateFragment());
                    break;
                case 2:
                    replaceFragment(new CartFragment());
                    break;
                case 3:
                    replaceFragment(new UserFragment());
                    break;
                default:
                    replaceFragment(new HomeFragment());
                    break;
            }
        }

        @Override
        public void onTabUnselected(int position) {

        }

        @Override
        public void onTabReselected(int position) {

        }
    }
}
