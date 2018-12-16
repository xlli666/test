package xies.academy.hebuni.com.cateringordersystem.adapter;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class BannerAdapter extends PagerAdapter {
    //数据源
    private List<ImageView> imageViewList;

    public BannerAdapter(List<ImageView> list) {
        imageViewList = list;
    }

    @Override
    public int getCount() {
        //取超大的数，实现无线循环效果
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        try {
            container.addView(imageViewList.get(position % imageViewList.size()), 0);
        } catch (Exception e) {
            Log.e("BannerPagerAdapter", "" + e.getLocalizedMessage());
        }
        return imageViewList.get(position % imageViewList.size());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //图片小于3张需要注释掉此句
        container.removeView(imageViewList.get(position % imageViewList.size()));
    }
}
