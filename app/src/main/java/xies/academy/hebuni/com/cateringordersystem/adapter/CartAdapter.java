package xies.academy.hebuni.com.cateringordersystem.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import xies.academy.hebuni.com.cateringordersystem.activity.R;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private JSONArray mJsonArray;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCateImg;
        TextView tvCateName;
        TextView tvCateNum;
        TextView tvCartPrice;

        ViewHolder(View itemView) {
            super(itemView);
            ivCateImg = itemView.findViewById(R.id.iv_cate_img);
            tvCateName = itemView.findViewById(R.id.tv_cate_name);
            tvCateNum = itemView.findViewById(R.id.tv_cate_num);
            tvCartPrice = itemView.findViewById(R.id.tv_cart_prices);
        }
    }

    /**
     * 构造函数
     */
    public CartAdapter(JSONArray jsonArray,Context context) {
        mJsonArray = jsonArray;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JSONObject jsonObject = mJsonArray.optJSONObject(position);

        try {
            InputStream inputStream = mContext.getAssets().open(jsonObject.optString("IMG"));
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            holder.ivCateImg.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.tvCateName.setText(jsonObject.optString("NAME"));
        holder.tvCateNum.setText("x"+jsonObject.optString("CATE_NUM"));
        String cartPrice = String.format(Locale.CHINA,"%.2f",jsonObject.optDouble("CATE_NUM")*jsonObject.optDouble("PRICE"));
        holder.tvCartPrice.setText("￥"+cartPrice);
    }

    @Override
    public int getItemCount() {
        return mJsonArray.length();
    }

}
