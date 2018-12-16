package xies.academy.hebuni.com.cateringordersystem.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;

import xies.academy.hebuni.com.cateringordersystem.activity.OrdersActivity;
import xies.academy.hebuni.com.cateringordersystem.activity.R;
import xies.academy.hebuni.com.cateringordersystem.activity.ReviewActivity;
import xies.academy.hebuni.com.cateringordersystem.util.DBUtil;
import xies.academy.hebuni.com.cateringordersystem.util.ToastUtil;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder>{

    private JSONArray mJsonArray;
    private Context mContext;
    private SharedPreferences sharedUserInfo;
    private String orderNo;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderNo;
        TextView tvOrderTime;
        TextView tvOrderStatus;
        TextView tvOrderDetail;
        Button btnOrderConfirm;
        Button btnOrderReview;
        ViewHolder(View itemView) {
            super(itemView);
            tvOrderNo = itemView.findViewById(R.id.tv_order_no);
            tvOrderTime = itemView.findViewById(R.id.tv_order_time);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvOrderDetail = itemView.findViewById(R.id.tv_order_detail);
            btnOrderConfirm = itemView.findViewById(R.id.btn_order_confirm);
            btnOrderReview = itemView.findViewById(R.id.btn_order_review);
        }
    }

    /**
     * 构造函数
     */
    public OrdersAdapter(JSONArray jsonArray,Context context) {
        mJsonArray = jsonArray;
        mContext = context;
        sharedUserInfo = mContext.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_orders, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.btnOrderConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                JSONObject jsonObject = mJsonArray.optJSONObject(position);
                orderNo = jsonObject.optString("NO");
                new Thread(statusUpdateRunnable).start();
            }
        });
        holder.btnOrderReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                JSONObject jsonObject = mJsonArray.optJSONObject(position);
                Intent intent = new Intent(mContext, ReviewActivity.class);
                intent.putExtra("orderNo",jsonObject.optString("NO"));
                intent.putExtra("orderTime",jsonObject.optString("CREATE_TIME"));
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JSONObject jsonObject = mJsonArray.optJSONObject(position);
        holder.tvOrderNo.setText(jsonObject.optString("NO"));
        holder.tvOrderTime.setText(jsonObject.optString("CREATE_TIME"));
        holder.tvOrderDetail.setText(jsonObject.optString("USER_REVIEW"));
        if ("99".equals(jsonObject.optString("STATUS"))) {
            holder.tvOrderStatus.setText("已评价");
            holder.btnOrderReview.setVisibility(View.GONE);
            holder.btnOrderConfirm.setVisibility(View.GONE);
        } else if ("10".equals(jsonObject.optString("STATUS"))) {
            holder.tvOrderStatus.setText("已送达");
            holder.btnOrderReview.setVisibility(View.VISIBLE);
            holder.btnOrderConfirm.setVisibility(View.GONE);
        } else {
            holder.tvOrderStatus.setText("配送中");
            holder.btnOrderReview.setVisibility(View.GONE);
            holder.btnOrderConfirm.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mJsonArray.length();
    }

    //Handler处理
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    orderConfirm((JSONObject) msg.obj);
                    break;
                default:
                    break;
            }
            return false;
        }
    });
    private void orderConfirm(JSONObject dbResult) {
        if (dbResult.optBoolean("isOK")) {
            ((OrdersActivity)mContext).recreate();
        } else {
            ToastUtil.SHOW(mContext,"系统异常请稍后重试！", false);
        }
    }
    //数据库执行线程
    private Runnable statusUpdateRunnable = new Runnable() {
        private Connection conn = null;
        @Override
        public void run() {
            String sql = "update user_order_head set STATUS='10' where NO=? and USERID=?";;
            conn = DBUtil.openConnection();
            String sss = sharedUserInfo.getString("userId", "");
            JSONObject dbResult = DBUtil.update(conn, sql, new String[]{orderNo,sharedUserInfo.getString("userId", "")});
            DBUtil.closeConnection(conn);
            Message msg = new Message();
            msg.what = 100;
            msg.obj = dbResult;
            mHandler.sendMessage(msg);
        }
    };

}
