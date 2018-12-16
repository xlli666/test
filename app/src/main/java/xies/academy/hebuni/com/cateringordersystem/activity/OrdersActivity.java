package xies.academy.hebuni.com.cateringordersystem.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;

import xies.academy.hebuni.com.cateringordersystem.adapter.OrdersAdapter;
import xies.academy.hebuni.com.cateringordersystem.util.DBUtil;
import xies.academy.hebuni.com.cateringordersystem.util.ToastUtil;

/**
 * 订单查看
 */
public class OrdersActivity extends CommonActivity {
    //参数
    private SharedPreferences sharedUserInfo;
    //声明组件
    private TextView tvTitle;
    private RecyclerView rclOrderAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedUserInfo = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_orders);
        initView();
        showProgress("",0);
        new Thread(orderInfoRunnable).start();
        initData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        showProgress("",0);
        new Thread(orderInfoRunnable).start();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        tvTitle = findViewById(R.id.center);
        rclOrderAll = findViewById(R.id.rcl_order_all);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rclOrderAll.setLayoutManager(layoutManager);
    }

    /**
     * 数据填充
     */
    private void initData() {
        tvTitle.setText("我的订单");
    }

    //Handler处理
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    dismissProgress();
                    orderInfoDeal((JSONObject) msg.obj);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    /**
     * 订单查询结果处理
     *
     * @param orderInfo 数据查询结果
     */
    private void orderInfoDeal(JSONObject orderInfo) {
        JSONArray orderArr = new JSONArray();
        if (orderInfo.optBoolean("isOK")) {
            JSONArray jsonArray = orderInfo.optJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                orderArr.put(jsonObject);
            }
            OrdersAdapter ordersAdapter = new OrdersAdapter(orderArr,OrdersActivity.this);
            rclOrderAll.setAdapter(ordersAdapter);
        } else {
            ToastUtil.SHOW(OrdersActivity.this, "您还没有订过餐！", false);
        }
    }

    //数据库执行线程
    Runnable orderInfoRunnable = new Runnable() {
        private Connection conn = null;
        @Override
        public void run() {
            String sql = "select t.*,date_format(t.CREATE_TIME_D,'%Y-%m-%d %T') CREATE_TIME from user_order_head t where t.USERID = ? order by t.CREATE_TIME_D desc ";
            conn = DBUtil.openConnection();
            JSONObject dbResult = DBUtil.query(conn, sql, new String[]{sharedUserInfo.getString("userId", "")});
            DBUtil.closeConnection(conn);
            Message msg = new Message();
            msg.what = 100;
            msg.obj = dbResult;
            mHandler.sendMessage(msg);
        }
    };

}
