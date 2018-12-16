package xies.academy.hebuni.com.cateringordersystem.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.sql.Connection;

import xies.academy.hebuni.com.cateringordersystem.activity.FragMainActivity;
import xies.academy.hebuni.com.cateringordersystem.activity.OrdersActivity;
import xies.academy.hebuni.com.cateringordersystem.activity.R;
import xies.academy.hebuni.com.cateringordersystem.activity.RegisterActivity;
import xies.academy.hebuni.com.cateringordersystem.bean.UpdateBean;
import xies.academy.hebuni.com.cateringordersystem.dialog.DialogUpgrade;
import xies.academy.hebuni.com.cateringordersystem.util.DBUtil;
import xies.academy.hebuni.com.cateringordersystem.util.InfoUtil;
import xies.academy.hebuni.com.cateringordersystem.util.ToastUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {
    //参数
    private SharedPreferences sharedUserInfo;
    private String loginUser;
    private String loginPwd;
    private String usrSuggest;
    //声明组件
    private RelativeLayout rvLogin;
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;
    private LinearLayout llLogged;
    private TextView tvLoginUser;
    private RelativeLayout rlOrders;
    private EditText etSuggest;
    private Button btnSubmit;
    private TextView tvVersion;
    private Button btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedUserInfo = getActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        //登录
        rvLogin = view.findViewById(R.id.rl_login);
        etUsername = view.findViewById(R.id.username);
        etPassword = view.findViewById(R.id.password);
        btnLogin = view.findViewById(R.id.login);
        btnRegister = view.findViewById(R.id.register);
        //登录完成
        llLogged = view.findViewById(R.id.ll_logged);
        tvLoginUser = view.findViewById(R.id.login_user);
        rlOrders = view.findViewById(R.id.rl_orders);
        etSuggest = view.findViewById(R.id.et_adv);
        btnSubmit = view.findViewById(R.id.submit);
        tvVersion = view.findViewById(R.id.tv_version);
        btnLogout = view.findViewById(R.id.logout);

        if ("".equals(sharedUserInfo.getString("username", ""))) {
            rvLogin.setVisibility(View.VISIBLE);
            llLogged.setVisibility(View.GONE);
        } else {
            String loggedUser = sharedUserInfo.getString("username", "");
            tvLoginUser.setText(loggedUser);
            //版本处理
            tvVersion.setText(InfoUtil.getVersionName(getActivity()));
            UpdateBean bean = InfoUtil.getServiceInfo();
            if (bean.getVersion()!= null && !String.valueOf(InfoUtil.getVersionCode(getActivity())).equals(bean.getVersion())) {
                tvVersion.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
            rvLogin.setVisibility(View.GONE);
            llLogged.setVisibility(View.VISIBLE);
        }
        initAction();
        return view;
    }

    /**
     * 按钮事件监听
     */
    private void initAction() {
        //登录
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser = etUsername.getText().toString();
                loginPwd = etPassword.getText().toString();
                if ("".equals(loginUser)) {
                    ToastUtil.SHOW(getActivity(), "账号不能为空！", false);
                    return;
                }
                if ("".equals(loginPwd)) {
                    ToastUtil.SHOW(getActivity(), "密码不能为空！", false);
                    return;
                }
                ((FragMainActivity)getActivity()).showProgress("",0);
                new Thread(loginRunnable).start();
            }
        });
        //注册
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
            }
        });
        //意见反馈
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usrSuggest = etSuggest.getText().toString();
                if ("".equals(usrSuggest)) {
                    ToastUtil.SHOW(getActivity(), "请填写建议！", false);
                    return;
                }
                ((FragMainActivity)getActivity()).showProgress("",0);
                new Thread(sugRunnable).start();
            }
        });
        //订单状态查看
        rlOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrdersActivity.class);
                startActivity(intent);
            }
        });
        //登出
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedUserInfo.edit().clear().apply();
                rvLogin.setVisibility(View.VISIBLE);
                llLogged.setVisibility(View.GONE);
            }
        });
        //版本更新
        tvVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUpgrade dialogUpgrade = new DialogUpgrade(getActivity());
                dialogUpgrade.show();
            }
        });
    }

    //Handler处理
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    ((FragMainActivity)getActivity()).dismissProgress();
                    userLogin((JSONObject) msg.obj);
                    break;
                case 102:
                    ((FragMainActivity)getActivity()).dismissProgress();
                    usrSuggest((JSONObject) msg.obj);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    /**
     * 登录操作
     * @param loginResult 数据库登录结果
     */
    private void userLogin(JSONObject loginResult) {
//        if ("".equals("")) {
        if (loginResult.optBoolean("isOK")) {
            JSONObject dbRlt = loginResult.optJSONArray("results").optJSONObject(0);
            sharedUserInfo.edit().putString("username",dbRlt.optString("USERNAME")).apply();
            sharedUserInfo.edit().putString("userId",dbRlt.optString("ID")).apply();
            tvLoginUser.setText(loginUser);
            //版本处理
            tvVersion.setText(InfoUtil.getVersionName(getActivity()));
            UpdateBean bean = InfoUtil.getServiceInfo();
            if (bean.getVersion()!= null && !String.valueOf(InfoUtil.getVersionCode(getActivity())).equals(bean.getVersion())) {
                tvVersion.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            rvLogin.setVisibility(View.GONE);
            llLogged.setVisibility(View.VISIBLE);
        } else {
            rvLogin.setVisibility(View.VISIBLE);
            llLogged.setVisibility(View.GONE);
            ToastUtil.SHOW(getActivity(), "账号密码错误！", false);
        }
    }

    /**
     * 提交意见反馈
     * @param sugResult 数据库执行结果
     */
    private void usrSuggest(JSONObject sugResult) {
        if (sugResult.optBoolean("isOK")) {
            etSuggest.setText("");
            ToastUtil.SHOW(getActivity(), "提交成功！感谢您的反馈！", false);
        } else {
            ToastUtil.SHOW(getActivity(), "系统异常！请稍后重试", false);
        }
    }

    //数据库执行线程
    Runnable loginRunnable = new Runnable() {
        private Connection conn = null;
        @Override
        public void run() {
            String sql = "select * from user_info t where t.username=? and t.password=?";
            conn = DBUtil.openConnection();
            JSONObject dbResult = DBUtil.query(conn, sql, new String[]{loginUser, InfoUtil.EncodeMD5(loginPwd)});
            DBUtil.closeConnection(conn);
            Message msg = new Message();
            msg.what = 100;
            msg.obj = dbResult;
            mHandler.sendMessage(msg);
        }
    };
    Runnable sugRunnable = new Runnable() {
        private Connection conn = null;
        @Override
        public void run() {
            String sql = "insert into user_suggest(USERID, DETAIL) values(?,?)";
            conn = DBUtil.openConnection();
            JSONObject dbResult = DBUtil.insert(conn, sql, new String[]{sharedUserInfo.getString("userId", ""), usrSuggest});
            DBUtil.closeConnection(conn);
            Message msg = new Message();
            msg.what = 102;
            msg.obj = dbResult;
            mHandler.sendMessage(msg);
        }
    };

}
