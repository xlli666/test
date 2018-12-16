package xies.academy.hebuni.com.cateringordersystem.activity;

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
import xies.academy.hebuni.com.cateringordersystem.util.InfoUtil;
import xies.academy.hebuni.com.cateringordersystem.util.ToastUtil;

/**
 * 用户注册
 */
public class RegisterActivity extends CommonActivity {
    //参数
    private String regUser;
    private String regPwd;
    //声明组件
    private TextView tvTitle;
    private EditText etRegUser;
    private EditText etRegPwd;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initData();
        initAction();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        tvTitle = findViewById(R.id.center);
        etRegUser = findViewById(R.id.et_reg_username);
        etRegPwd = findViewById(R.id.et_reg_password);
        btnRegister = findViewById(R.id.btn_reg_register);
    }

    /**
     * 数据填充
     */
    private void initData() {
        tvTitle.setText(getResources().getString(R.string.register));
    }

    /**
     * 按钮事件监听
     */
    private void initAction() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regUser = etRegUser.getText().toString();
                regPwd = etRegPwd.getText().toString();
                if ("".equals(regUser)) {
                    ToastUtil.SHOW(RegisterActivity.this, "账号不能为空！", false);
                    return;
                }
                if ("".equals(regPwd)) {
                    ToastUtil.SHOW(RegisterActivity.this, "密码不能为空！", false);
                    return;
                }
                showProgress("",0);
                new Thread(regRunnable).start();
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
                    userRegister((JSONObject) msg.obj);
                    break;
                case 101:
                    dismissProgress();
                    etRegUser.setText("");
                    etRegPwd.setText("");
                    ToastUtil.SHOW(RegisterActivity.this, "用户名重复！请重新输入", false);
                default:
                    break;
            }
            return false;
        }
    });

    private void userRegister(JSONObject regResult) {
        if (regResult.optBoolean("isOK")) {
            etRegUser.setText("");
            etRegPwd.setText("");
            ToastUtil.SHOW(RegisterActivity.this, "注册成功！请返回登录", false);
        } else {
            ToastUtil.SHOW(RegisterActivity.this, "系统异常！请稍后重试", false);
        }
    }

    //数据库执行线程
    Runnable regRunnable = new Runnable() {
        private Connection conn = null;
        @Override
        public void run() {
            //重名处理
            String query = "select * from user_info where username = ?";
            conn = DBUtil.openConnection();
            JSONObject dbQueryResult = DBUtil.query(conn,query,new String[]{regUser});
            DBUtil.closeConnection(conn);
            if (dbQueryResult.optBoolean("isOK")) {
                mHandler.sendEmptyMessage(101);
                return;
            }
            //不重名--开始注册
            String sql = "insert into user_info(username, password) values(?,?)";
            conn = DBUtil.openConnection();
            JSONObject dbResult = DBUtil.insert(conn,sql,new String[]{regUser, InfoUtil.EncodeMD5(regPwd)});
            DBUtil.closeConnection(conn);
            Message msg = new Message();
            msg.what = 100;
            msg.obj = dbResult;
            mHandler.sendMessage(msg);
        }
    };

}
