package xies.academy.hebuni.com.cateringordersystem.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;

import xies.academy.hebuni.com.cateringordersystem.bean.DownloadInfo;
import xies.academy.hebuni.com.cateringordersystem.db.UpgradeDB;
import xies.academy.hebuni.com.cateringordersystem.inf.ProgressBarListener;
import xies.academy.hebuni.com.cateringordersystem.util.ParamUtil;

public class DownloadThread extends Thread {
    private int threadid;// 线程下载的id
    private String path;// 下载的路径
    private File file;// 保存的文件
    private ProgressBarListener listener;// 进度条更新的接口

    private int startposition;// 下载的开始位置
    private int endposition;// 下载的结束位置
    private RandomAccessFile brafWriteFile = null;
    private BufferedInputStream bufferedInputStream = null;
    private HttpURLConnection conn = null;
    private UpgradeDB dao;

    public DownloadThread(int threadid, String path, File file, int block,
                          ProgressBarListener listener, Context context, UpgradeDB dao) {
        super();
        this.threadid = threadid;
        this.path = path;
        this.file = file;
        this.listener = listener;
        this.startposition = threadid * block;
        this.endposition = (threadid + 1) * block - 1;
        this.dao = dao;
    }

    @Override
    public void run() {
        super.run();
        try {
            // 判断该线程是否有下载记录
            DownloadInfo info = new DownloadInfo();
            info.setPath(path);
            info.setThreadid(threadid);
            int length = dao.query(info);
            startposition = startposition + length;
            // BufferedRandomAccessFile brafWriteFile = new
            // BufferedRandomAccessFile(file, "rwd");
            brafWriteFile = new RandomAccessFile(file, "rwd");
            // BufferedOutputStream brafWriteFile=new BufferedOutputStream(new
            // FileOutputStream(file));
            brafWriteFile.seek(startposition);
            URL url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            // 指定下载的位置
            conn.setRequestProperty("Range", "bytes=" + startposition + "-"
                    + endposition);
            // 不用再次去判断相应码是否为200
            bufferedInputStream = new BufferedInputStream(conn.getInputStream());
            byte[] buffer = new byte[4000];
            int readcount = -1;
            int count = length;// 该线程下载的总数据量
            long start = System.currentTimeMillis();
            while (!ParamUtil.isPause
                    && (readcount = bufferedInputStream.read(buffer)) != -1) {
                brafWriteFile.write(buffer, 0, readcount);
                count = count + readcount;
                info.setDownloadsize(count);
                if (dao != null)
                    dao.update(info);
                listener.getDownload(readcount);
            }
            System.out.println("BufferedRandomAccessFile Copy & Write File: "
                    + "    FileSize: " + conn.getContentLength() / 1000
                    + " (KB)    " + "Spend: "
                    + (double) (System.currentTimeMillis() - start) / 1000
                    + "(s)");
        } catch (Exception e) {
            e.printStackTrace();
            if (dao != null)
                dao.closeDB();
        } finally {
            try {
                if (brafWriteFile != null)
                    brafWriteFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (bufferedInputStream != null)
                    bufferedInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
