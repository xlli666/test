package xies.academy.hebuni.com.cateringordersystem.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import xies.academy.hebuni.com.cateringordersystem.adapter.BannerAdapter;

/**
 *
 */
public class CommMainActivity extends AppCompatActivity {
    //声明组件
    private ViewPager mViewPager;
    private List<ImageView> imageViewList;
    private TextView tvImgDesc;
    private LinearLayout llDots;
    //广告素材
    private int[] bannerImages = {R.mipmap.h1, R.mipmap.h2, R.mipmap.h3, R.mipmap.h4, R.mipmap.h5};
    //广告语
    private String[] bannerTexts = {"图1", "图2", "图3", "图4", "图5"};
    // ViewPager适配器与监听器
    BannerAdapter bannerAdapter;
    BannerListener bannerListener;
    //圆点标志位
    private int positionIndex = 0;
    //线程池
    private ScheduledExecutorService scheduledExecutorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_comm);
        initView();
        initData();
        initAction();
    }

    /**
     * 利用线程池定时执行图片轮播
     */
    @Override
    protected void onStart() {
        super.onStart();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(), 10, 10, TimeUnit.SECONDS);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            scheduledExecutorService = null;
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            return false;
        }
    });

    /**
     * 初始化组件
     */
    private void initView() {
        mViewPager = findViewById(R.id.view_page);
        tvImgDesc = findViewById(R.id.tv_banner_text);
        llDots = findViewById(R.id.ll_points);
    }

    /**
     * BANNER填充图片信息
     */
    private void initData() {
        imageViewList = new ArrayList<>();
        View view;
        LinearLayout.LayoutParams params;
        ImageView imageView;
        for (int bannerImg:bannerImages) {
            //设置广告图
            imageView = new ImageView(CommMainActivity.this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            imageView.setBackgroundResource(bannerImg);
            imageViewList.add(imageView);
            //设置圆点
            view = new View(CommMainActivity.this);
            params = new LinearLayout.LayoutParams(5, 5);
            params.leftMargin = 10;
            view.setLayoutParams(params);
            view.setBackgroundResource(R.drawable.point_background);
            view.setEnabled(false);

            llDots.addView(view);
        }
        bannerAdapter = new BannerAdapter(imageViewList);
        mViewPager.setAdapter(bannerAdapter);
    }

    /**
     * BANNER设置事件监听
     */
    private void initAction() {
        bannerListener = new BannerListener();
        mViewPager.addOnPageChangeListener(bannerListener);
        //取中间数来作为起始位置
        int index = (Integer.MAX_VALUE / 2) - (Integer.MAX_VALUE / 2 % imageViewList.size());
        //触发监听
        mViewPager.setCurrentItem(index);
        llDots.getChildAt(positionIndex).setEnabled(true);
    }

    //实现ViewPager监听器接口
    private class BannerListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            int newPosition = position % bannerImages.length;
            tvImgDesc.setText(bannerTexts[newPosition]);
            llDots.getChildAt(newPosition).setEnabled(true);
            llDots.getChildAt(positionIndex).setEnabled(false);
            // 更新标志位
            positionIndex = newPosition;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    //图片轮播任务
    private class ViewPagerTask implements Runnable {

        @Override
        public void run() {
            mHandler.sendEmptyMessage(0);
        }
    }
}
