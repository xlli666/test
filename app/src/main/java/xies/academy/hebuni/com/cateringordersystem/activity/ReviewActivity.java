package xies.academy.hebuni.com.cateringordersystem.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.sql.Connection;

import xies.academy.hebuni.com.cateringordersystem.util.DBUtil;
import xies.academy.hebuni.com.cateringordersystem.util.ToastUtil;

public class ReviewActivity extends CommonActivity {
    //参数
    private SharedPreferences sharedUserInfo;
    private String orderNo;
    private String orderTime;
    private String orderRev;
    //声明组件
    private TextView tvTitle;
    private TextView tvOrderNo;
    private TextView tvOrderTime;
    private EditText etOrderRev;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedUserInfo = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        orderNo = getIntent().getStringExtra("orderNo");
        orderTime = getIntent().getStringExtra("orderTime");
        setContentView(R.layout.activity_review);
        initView();
        initData();
        initAction();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        tvTitle = findViewById(R.id.center);
        tvOrderNo = findViewById(R.id.tv_order_no);
        tvOrderTime = findViewById(R.id.tv_order_time);
        etOrderRev = findViewById(R.id.et_order_review);
        btnSubmit = findViewById(R.id.btn_submit);
    }

    /**
     * 数据填充
     */
    private void initData() {
        tvTitle.setText("评价");
        tvOrderNo.setText(orderNo);
        tvOrderTime.setText(orderTime);
    }

    /**
     * 按钮事件监听
     */
    private void initAction() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderRev = etOrderRev.getText().toString();
                if ("".equals(orderRev)) {
                    ToastUtil.SHOW(ReviewActivity.this,"请填写评论！", false);
                    return;
                }
                showProgress("",0);
                new Thread(revSubmitRunnable).start();
            }
        });
    }

    //Handler处理
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    dismissProgress();
                    reviewResult((JSONObject) msg.obj);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    private void reviewResult(JSONObject dbResult) {
        if (dbResult.optBoolean("isOK")) {
            ToastUtil.SHOW(ReviewActivity.this, "感谢您的评价！", false);
            ReviewActivity.this.finish();
        } else {
            ToastUtil.SHOW(ReviewActivity.this, "系统异常！请稍后重试", false);
        }
    }

    //数据库执行线程
    Runnable revSubmitRunnable = new Runnable() {
        private Connection conn = null;
        @Override
        public void run() {
            String sql = "update user_order_head set STATUS='99', USER_REVIEW=? where NO=? and USERID=?";
            conn = DBUtil.openConnection();
            JSONObject dbResult = DBUtil.update(conn, sql, new String[]{orderRev, orderNo,sharedUserInfo.getString("userId", "")});
            DBUtil.closeConnection(conn);
            Message msg = new Message();
            msg.what = 100;
            msg.obj = dbResult;
            mHandler.sendMessage(msg);
        }
    };

}
