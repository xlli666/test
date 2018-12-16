package xies.academy.hebuni.com.cateringordersystem.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.Locale;

import xies.academy.hebuni.com.cateringordersystem.activity.FragMainActivity;
import xies.academy.hebuni.com.cateringordersystem.activity.R;
import xies.academy.hebuni.com.cateringordersystem.adapter.CartAdapter;
import xies.academy.hebuni.com.cateringordersystem.inf.AlertConfirm;
import xies.academy.hebuni.com.cateringordersystem.util.DBUtil;
import xies.academy.hebuni.com.cateringordersystem.util.InfoUtil;
import xies.academy.hebuni.com.cateringordersystem.util.ToastUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {
    //参数
    private SharedPreferences sharedUserInfo;
    private SharedPreferences sharedOrdersInfo;
    //声明组件
    private RecyclerView rvCart;
    private TextView tvTotalPrice;
    private Button btnSettle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedUserInfo = getActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        sharedOrdersInfo = getActivity().getSharedPreferences("ORDER_INFO", Context.MODE_PRIVATE);
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        rvCart = view.findViewById(R.id.rv_cart);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        btnSettle = view.findViewById(R.id.btn_settle);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvCart.setLayoutManager(layoutManager);

        String cart = sharedOrdersInfo.getString("orders", "");
        if (!"".equals(cart)) {
            JSONArray cartArr = removeZero(cart);
            initData(cartArr);
        }
        initAction();
        return view;
    }

    /**
     * 适配器数据及总价格填充
     * @param jsonArray 数据信息
     */
    private void initData(JSONArray jsonArray) {
        tvTotalPrice.setText(InfoUtil.totalPrice(jsonArray));
        CartAdapter cateAdapter = new CartAdapter(jsonArray, getActivity());
        rvCart.setAdapter(cateAdapter);
    }

    /**
     * 按钮事件监听
     */
    private void initAction() {
        btnSettle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("0.0".equals(tvTotalPrice.getText())) {
                    ToastUtil.SHOW(getActivity(),"购物车是空的！请先点餐",false);
                } else if ("".equals(sharedUserInfo.getString("userId",""))) {
                    ToastUtil.SHOW(getActivity(),"请先登录！",false);
                } else {
                    ((FragMainActivity)getActivity()).alertDialog("温馨提醒", "确认点餐吗？", new AlertConfirm() {
                        @Override
                        public void confirm() {
                            ((FragMainActivity)getActivity()).showProgress("",0);
                            new Thread(cateDataRunnable).start();
                        }

                        @Override
                        public void cancel() {

                        }
                    });

                }
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
                    settleCart((Boolean) msg.obj);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    /**
     * 结算反馈
     * @param insertResult 结算结果
     */
    private void settleCart(boolean insertResult) {
        if (insertResult) {
            ToastUtil.SHOW(getActivity(),"结算成功！感谢您的点餐！",false);
            initData(new JSONArray());
            sharedOrdersInfo.edit().clear().apply();
        } else {
            ToastUtil.SHOW(getActivity(),"系统异常！请稍后重新点餐！",false);
            initData(new JSONArray());
            sharedOrdersInfo.edit().clear().apply();
        }
    }

    /**
     * 去除没有点餐的菜品
     * @param orders 菜品数据
     * @return 清除结果
     */
    private JSONArray removeZero(String orders) {
        JSONArray cleanedArr = new JSONArray();
        if (orders == null || "".equals(orders)) {
            return cleanedArr;
        }
        try {
            JSONArray tempArr = new JSONArray(orders);
            for (int i = 0; i < tempArr.length(); i++) {
                JSONObject tempObj = tempArr.optJSONObject(i);
                if (!"0".equals(tempObj.optString("CATE_NUM"))) {
                    cleanedArr.put(tempObj);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cleanedArr;
    }

    //数据库执行线程
    Runnable cateDataRunnable = new Runnable() {
        private Connection conn = null;
        @Override
        public void run() {
            boolean insertResult = true;
            //订单头数据
            String userId = sharedUserInfo.getString("userId","");
            String orderNo = InfoUtil.getCurrTime()+userId;
            JSONArray cartArr = new JSONArray();
            try {
                cartArr = new JSONArray(sharedOrdersInfo.getString("orders", ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String totalPrice = InfoUtil.totalPrice(cartArr);
            String status = "00";
            String sqlHead = "insert into user_order_head(NO,USERID,TOTAL_PRICE,STATUS) values(?,?,?,?)";
            conn = DBUtil.openConnection();
            JSONObject dbHeadResult = DBUtil.insert(conn,sqlHead,new String[]{orderNo, userId, totalPrice,status});
            if (!dbHeadResult.optBoolean("isOK")) {
                insertResult = false;
            }
            //订单明细数据
            for (int i=0;i<cartArr.length();i++) {
                JSONObject jsonObject = cartArr.optJSONObject(i);
                String cateNo = jsonObject.optString("NO");
                String cateNum = jsonObject.optString("CATE_NUM");
                String cartPrice = String.format(Locale.CHINA,"%.2f",jsonObject.optDouble("CATE_NUM")*jsonObject.optDouble("PRICE"));
                String sqlDetail = "insert into user_order_detail(ORDER_NO,CATE_NO,CATE_NUM,CATE_T_PRICE) values(?,?,?,?)";
                JSONObject dbDetailResult = DBUtil.insert(conn,sqlDetail,new String[]{orderNo, cateNo, cateNum, cartPrice});
                if (!dbDetailResult.optBoolean("isOK")) {
                    insertResult = false;
                    break;
                }
            }
            DBUtil.closeConnection(conn);
            Message msg = new Message();
            msg.what = 100;
            msg.obj = insertResult;
            mHandler.sendMessage(msg);
        }
    };

}
