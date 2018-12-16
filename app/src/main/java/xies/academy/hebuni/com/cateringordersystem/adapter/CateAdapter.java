package xies.academy.hebuni.com.cateringordersystem.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import xies.academy.hebuni.com.cateringordersystem.activity.CateDetailActivity;
import xies.academy.hebuni.com.cateringordersystem.activity.FragMainActivity;
import xies.academy.hebuni.com.cateringordersystem.activity.R;

public class CateAdapter extends RecyclerView.Adapter<CateAdapter.ViewHolder> {

    private JSONArray mJsonArray;
    private Context mContext;
    private SharedPreferences sharedOrderInfo;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCateImg;
        TextView tvCateName;
        TextView tvCatePrice;
        ImageView ivCatePlus;
        ImageView ivCateMinus;
        TextView tvCateNum;

        ViewHolder(View itemView) {
            super(itemView);
            ivCateImg = itemView.findViewById(R.id.iv_cate_img);
            tvCateName = itemView.findViewById(R.id.tv_cate_name);
            tvCatePrice = itemView.findViewById(R.id.tv_cate_price);
            ivCatePlus = itemView.findViewById(R.id.iv_cate_plus);
            ivCateMinus = itemView.findViewById(R.id.iv_cate_minus);
            tvCateNum = itemView.findViewById(R.id.tv_cate_num);
        }
    }

    /**
     * 构造函数
     */
    public CateAdapter(JSONArray jsonArray, Context context) {
        mJsonArray = jsonArray;
        mContext = context;
        sharedOrderInfo = mContext.getSharedPreferences("ORDER_INFO", Context.MODE_PRIVATE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_cate, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        //加菜处理
        holder.ivCatePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                holder.tvCateNum.setText(String.valueOf(Integer.valueOf(holder.tvCateNum.getText().toString()) + 1));
                if ("1".equals(holder.tvCateNum.getText())) {
                    holder.tvCateNum.setVisibility(View.VISIBLE);
                    holder.ivCateMinus.setVisibility(View.VISIBLE);
                }
                try {
                    mJsonArray.optJSONObject(position).put("CATE_NUM", holder.tvCateNum.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.v("mJsonArray: ", mJsonArray.toString());
                sharedOrderInfo.edit().putString("orders", mJsonArray.toString()).apply();
                ((FragMainActivity)mContext).modifyDot();
            }
        });
        //减菜处理
        holder.ivCateMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                holder.tvCateNum.setText(String.valueOf(Integer.valueOf(holder.tvCateNum.getText().toString()) - 1));
                if ("0".equals(holder.tvCateNum.getText())) {
                    holder.tvCateNum.setVisibility(View.GONE);
                    holder.ivCateMinus.setVisibility(View.GONE);
                }
                try {
                    mJsonArray.optJSONObject(position).put("CATE_NUM", holder.tvCateNum.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sharedOrderInfo.edit().putString("orders", mJsonArray.toString()).apply();
                ((FragMainActivity)mContext).modifyDot();
            }
        });
        //查看明细
        holder.ivCateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                JSONObject cateObj = mJsonArray.optJSONObject(position);
                Intent intent = new Intent(mContext, CateDetailActivity.class);
                intent.putExtra("cateImg", cateObj.optString("IMG"));
                intent.putExtra("cateName", cateObj.optString("NAME"));
                intent.putExtra("catePrice", cateObj.optString("PRICE"));
                intent.putExtra("cateDesc", cateObj.optString("CATE_DESC"));
                mContext.startActivity(intent);
            }
        });
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
        holder.tvCatePrice.setText("￥ "+jsonObject.optString("PRICE"));
        holder.tvCateNum.setText(jsonObject.optString("CATE_NUM"));
        if ("0".equals(holder.tvCateNum.getText())) {
            holder.tvCateNum.setVisibility(View.GONE);
            holder.ivCateMinus.setVisibility(View.GONE);
        } else {
            holder.tvCateNum.setVisibility(View.VISIBLE);
            holder.ivCateMinus.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mJsonArray.length();
    }

}
