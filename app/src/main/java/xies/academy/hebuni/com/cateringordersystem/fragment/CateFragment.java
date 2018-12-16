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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;

import xies.academy.hebuni.com.cateringordersystem.activity.FragMainActivity;
import xies.academy.hebuni.com.cateringordersystem.activity.R;
import xies.academy.hebuni.com.cateringordersystem.adapter.CateAdapter;
import xies.academy.hebuni.com.cateringordersystem.util.DBUtil;
import xies.academy.hebuni.com.cateringordersystem.util.ToastUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class CateFragment extends Fragment {
    //参数
    private SharedPreferences sharedOrdersInfo;
    //声明组件
    private RecyclerView rvSpecial;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedOrdersInfo = getActivity().getSharedPreferences("ORDER_INFO", Context.MODE_PRIVATE);
        View view = inflater.inflate(R.layout.fragment_cate, container, false);
        rvSpecial = view.findViewById(R.id.rv_special);

        LinearLayoutManager layoutSpecialManager = new LinearLayoutManager(getActivity());
        rvSpecial.setLayoutManager(layoutSpecialManager);

        String orders = sharedOrdersInfo.getString("orders", "");
        if ("".equals(orders)) {
            ((FragMainActivity)getActivity()).showProgress("",0);
            new Thread(cateDataRunnable).start();
        } else {
            try {
                JSONArray cateArr = new JSONArray(orders);
                initData(cateArr);
            } catch (JSONException e) {
//                ToastUtil.SHOW(getActivity(),"系统异常！订单读取失败", false);
                e.printStackTrace();
            }
        }
        return view;
    }

    /**
     * 适配器数据填充
     *
     * @param jsonArray 数据信息
     */
    private void initData(JSONArray jsonArray) {
//        JSONArray cateSpecialArr = new JSONArray();
//        for (int i = 0; i < jsonArray.length(); i++) {
//            JSONObject jsonObject = jsonArray.optJSONObject(i);
//            cateSpecialArr.put(jsonObject);
//        }
        CateAdapter cateSpecialAdapter = new CateAdapter(jsonArray, getActivity());
        rvSpecial.setAdapter(cateSpecialAdapter);
    }

    //Handler处理
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    ((FragMainActivity)getActivity()).dismissProgress();
                    cateResult((JSONObject) msg.obj);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    private void cateResult(JSONObject dbResult) {
        if (dbResult.optBoolean("isOK")) {
            JSONArray jsonArray = dbResult.optJSONArray("results");
            sharedOrdersInfo.edit().putString("orders", jsonArray.toString()).apply();
            initData(jsonArray);
        } else {
            ToastUtil.SHOW(getActivity(), "系统异常！", false);
        }
    }

    //数据库执行线程
    Runnable cateDataRunnable = new Runnable() {
        private Connection conn = null;
        @Override
        public void run() {
            String sql = "select t.*, '0' as CATE_NUM from cate_info t order by t.TYPE,T.SORT";
            conn = DBUtil.openConnection();
            JSONObject dbResult = DBUtil.query(conn, sql, new String[]{});
            DBUtil.closeConnection(conn);
            Message msg = new Message();
            msg.what = 100;
            msg.obj = dbResult;
            mHandler.sendMessage(msg);
        }
    };

}
