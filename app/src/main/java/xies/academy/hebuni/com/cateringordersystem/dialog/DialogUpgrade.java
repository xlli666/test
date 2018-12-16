package xies.academy.hebuni.com.cateringordersystem.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import xies.academy.hebuni.com.cateringordersystem.activity.R;
import xies.academy.hebuni.com.cateringordersystem.bean.UpdateBean;
import xies.academy.hebuni.com.cateringordersystem.db.UpgradeDB;
import xies.academy.hebuni.com.cateringordersystem.download.DownloadManager;
import xies.academy.hebuni.com.cateringordersystem.inf.ProgressBarListener;
import xies.academy.hebuni.com.cateringordersystem.util.InfoUtil;
import xies.academy.hebuni.com.cateringordersystem.util.ParamUtil;
import xies.academy.hebuni.com.cateringordersystem.util.ToastUtil;
import xies.academy.hebuni.com.cateringordersystem.util.XmlParseUtil;

/**
 * 检测更新对话框
 */
public class DialogUpgrade extends Dialog {

    private Context mContext = null;

    private ProgressBar pb;
    private TextView tv_info;
    private DownloadManager manager;
    private UpgradeDB dao;
    private UpdateBean bean;
    private static final int ERROR_DOWNLOAD = 250; // 下载失败
    private static final int SET_PROGRESS_MAX = 251;
    private static final int UPDATE_PROGRESS = 252;
    private final int UPDATE = 253;// 更新
    private final int CONNECT_ERROR = 254;// 连接服务器失败
    private final int SERVICE_ERROR = 255;// 服务器出错
    private final int SERVICE_NO_FILE = 256;// 服务器没有这个文件
    private static final int INSTALL_FILE = 351;
    private static final int DELAY_NETWORK = 352;
    private static final int CLOSE_ME = 353;

    private Message msg = new Message();
    private RelativeLayout ll_flash;
    private LinearLayout ll_upgrade;
    private SharedPreferences sharedPreferences = null;

    public DialogUpgrade(Context context) {
        super(context, R.style.loading_dialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = mContext.getSharedPreferences("apk", Context.MODE_PRIVATE);
        setContentView(R.layout.pub_upgrade);
        ParamUtil.isPause = false;
        pb = findViewById(R.id.pb);
        tv_info = findViewById(R.id.tv_info);
        ll_flash = findViewById(R.id.dialog_ll_flash);

        ImageView spaceshipImage = findViewById(R.id.img);
        ll_upgrade = findViewById(R.id.dialog_ll_upgrade);
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(mContext, R.anim.loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);

        download();
        this.setCanceledOnTouchOutside(false);// 点击对话框外部取消对话框显示

    }

    /**
     * 检测是否有网络
     *
     * @return 是否联网
     */

    private boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] infoS = connectivity.getAllNetworkInfo();
            if (infoS != null)
                for (NetworkInfo info : infoS)
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE:// 进行更新提示
                    Toast.makeText(mContext, "升级版本:" + bean.getVersion() + "\n" + "当前版本:" + InfoUtil.getVersionCode(mContext), Toast.LENGTH_LONG).show();
                    ll_flash.setVisibility(View.GONE);
                    ll_upgrade.setVisibility(View.VISIBLE);
                    downApk(bean.getApkUrl());
                    Editor editor = sharedPreferences.edit();
                    editor.putString("apk", getFileName(bean.getApkUrl()));
                    editor.apply();
                    break;
                case CONNECT_ERROR:// 连接服务器失败
                    Toast.makeText(mContext, "服务器连接异常!", Toast.LENGTH_LONG).show();
                    dismiss();
                    break;
                case SERVICE_ERROR:// 服务器出错
//				    Toast.makeText(mContext, "未找到下载地址!", Toast.LENGTH_LONG).show();
                    dismiss();
                    break;
                case ERROR_DOWNLOAD:
                    Toast.makeText(mContext, "下载失败!", Toast.LENGTH_LONG).show();
                    dismiss();
                    break;
                case CLOSE_ME:
                    Toast.makeText(mContext, "已是最新版本!", Toast.LENGTH_LONG).show();
                    dismiss();
                    break;
                case SET_PROGRESS_MAX:
                    int max = (Integer) msg.obj;
                    pb.setMax(max);
                    break;
                case SERVICE_NO_FILE:
                    //通常是能读取服务文件，但没有权限写到手机本地
                    Toast.makeText(mContext, "服务器没有此文件!", Toast.LENGTH_LONG).show();
                    dismiss();
                    break;
                case DELAY_NETWORK:
                    if (isConnectingToInternet()) {
                        dao = new UpgradeDB(mContext);
                        manager = new DownloadManager(mContext, dao, mHandler);
                        download();
                    } else
                        Toast.makeText(mContext, "请打开网络连接!", Toast.LENGTH_LONG).show();
                    dismiss();
                    break;
                case INSTALL_FILE:
                    dismiss();
                    String str = sharedPreferences.getString("apk", "");
                    String fileName = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/" + str;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri downloadUri = FileProvider.getUriForFile(mContext,
                            "xies.academy.hebuni.com.cateringordersystem.activity.fileprovider", new File(fileName));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(downloadUri, "application/vnd.android.package-archive");
//                    intent.setDataAndType(Uri.fromFile(new File(fileName)),"application/vnd.android.package-archive");
                    mContext.startActivity(intent);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    break;
                case UPDATE_PROGRESS:
                    int length = (Integer) msg.obj;// 新下载的长度
                    int existProgress = pb.getProgress();// 得到当前的下载刻度
                    int progress = existProgress + length;
                    pb.setProgress(progress);

                    int maxProgress = pb.getMax();
                    float value = (float) progress / (float) maxProgress;
                    int percent = (int) (value * 100);
                    tv_info.setText("下载:" + percent + "%");
                    if (maxProgress == progress) {
                        // 删除下载记录
                        dao.delete(bean.getApkUrl());
                        if (dao != null)
                            dao.closeDB();
                        Message installFileMsg = new Message();
                        installFileMsg.what = INSTALL_FILE;
                        Toast.makeText(mContext, "下载成功!", Toast.LENGTH_SHORT).show();
                        mHandler.sendMessageDelayed(installFileMsg, 500);
                    }
                    break;
                case 99:
                    ToastUtil.SHOW(mContext, msg.obj.toString(), false);
                default:
                    break;
            }
            return false;
        }
    });

    // 得到文件的名称
    private String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    private void downApk(String apkUrl) {
        new Thread(new DownRun(apkUrl)).start();
    }

    class DownRun implements Runnable {
        String apkUrl;

        public DownRun(String apkUrl) {
            this.apkUrl = apkUrl;
        }

        public void run() {
            try {
                manager.download(apkUrl, new ProgressBarListener() {

                    public void getMax(int length) {
                        Message msg = new Message();
                        msg.what = SET_PROGRESS_MAX;
                        msg.obj = length;
                        mHandler.sendMessage(msg);
                    }

                    public void getDownload(int length) {
                        Message msg = new Message();
                        msg.what = UPDATE_PROGRESS;
                        msg.obj = length;
                        mHandler.sendMessage(msg);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

/*	private void deleteFile() {
        String str=sharedPreferences.getString("apk","");
		File file = new File(Environment.getExternalStorageDirectory(), str);
		if (file.exists()) {
			file.delete();
		}
	}*/

    public void download() {
        // 下载 是一个耗时的操作，应该放置在子线程
        new Thread() {
            public void run() {
                try {
                    //==============升级URL=================================================
                    URL url = new URL(ParamUtil.SV_URL + "upgradeApp.xml");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    // 打开输入流
                    con.setDoInput(true);
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(3000);
                    if (con.getResponseCode() == 200) {
                        // 连接成功
                        bean = XmlParseUtil.getUpdataInfo(con.getInputStream());
                        con.disconnect();

                        if (bean != null && bean.getVersion().trim().equals(String.valueOf(InfoUtil.getVersionCode(mContext)))) {
                            // 没有版本更新,弹开提示
                            mHandler.sendEmptyMessageDelayed(CLOSE_ME, 500);
                        } else {
                            //需要版本更新
                            dao = new UpgradeDB(mContext);
                            manager = new DownloadManager(mContext, dao, mHandler);
                            msg.what = UPDATE;
                            mHandler.sendMessage(msg);
                        }
                    } else {
                        // 连接失败,服务器出错
                        msg.what = SERVICE_ERROR;
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // 服务器没有打开
                    msg.what = CONNECT_ERROR;
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            ParamUtil.isPause = true;
            if (dao != null)
                dao.closeDB();
            //System.exit(1);
        }
        return super.onKeyDown(keyCode, event);
    }

}
