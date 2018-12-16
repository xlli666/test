package xies.academy.hebuni.com.cateringordersystem.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import xies.academy.hebuni.com.cateringordersystem.inf.AlertConfirm;

public class CommonActivity extends AppCompatActivity {

    private LayoutInflater inflater = null;
    private Dialog loadingDialog;
    public int clickInternal = 1200;
    private TextView tipTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgress();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.backzoomin, R.anim.backzoomout);
    }

    /**
     * 取消加载框
     */
    public void dismissProgress() {
        try {
            if (null != loadingDialog && loadingDialog.isShowing() && !isFinishing())
                loadingDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 消息对话框设置
     * @param text   设置话框的文本内容
     * @param isCloseSelf  点击取消是否关闭对话框   为1 点击对话框外边界消失，否则反之。
     * @return 对话框
     */
    public Dialog showProgress(String text, int isCloseSelf) {
        if (inflater == null)
            inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        v.setFocusableInTouchMode(true);
        tipTextView = v.findViewById(R.id.tipTextView);// 提示文字
        if (text != null && !text.equals("")) {
            tipTextView.setText(text);// 设置加载文字
        }
        if (loadingDialog == null) {
            loadingDialog = new Dialog(this, R.style.loading_dialog);// 创建自定义样式dialog
            loadingDialog.setCanceledOnTouchOutside(false);// 点击对话框外边界不消失
            if (isCloseSelf == 0) {
                loadingDialog.setCancelable(false);// 不可以用“返回键”取消
            } else {
                loadingDialog.setCancelable(true);// 可以用“返回键”取消
            }
            loadingDialog.setContentView(v, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
            loadingDialog.show();
        } else {
            loadingDialog.show();
            v.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    System.out.println("===========================loadingDialog==========================");
                    loadingDialog.dismiss();
                    return false;
                }
            });
        }
        return loadingDialog;
    }

    /**
     * 确认删除数据对话框
     */
    public void alertDialog(String title,String content,final AlertConfirm deleteConfirm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CommonActivity.this);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(null!=deleteConfirm){
                    deleteConfirm.confirm();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(null!=deleteConfirm){
                    deleteConfirm.cancel();
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 返回系统配置
     * @return
     */
    public SharedPreferences orderConfig(){
        return getSharedPreferences("ORDER_INFO", MODE_PRIVATE);
    }

    /**
     * 编辑系统配置
     * @return
     */
    public Editor editOrderConfig(){
        return getSharedPreferences("ORDER_INFO", MODE_PRIVATE).edit();
    }

    /**
     * 返回用户配置
     * @return
     */
    public SharedPreferences userConfig(){
        return getSharedPreferences("USERINFO", MODE_PRIVATE);
    }

    /**
     * 编辑用户配置
     * @return
     */
    public Editor edituserConfig(){
        return getSharedPreferences("USERINFO", MODE_PRIVATE).edit();
    }
}
