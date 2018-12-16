package xies.academy.hebuni.com.cateringordersystem.download;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import xies.academy.hebuni.com.cateringordersystem.bean.DownloadInfo;
import xies.academy.hebuni.com.cateringordersystem.db.UpgradeDB;
import xies.academy.hebuni.com.cateringordersystem.inf.ProgressBarListener;


public class DownloadManager {
	private final static int threadsize = 3;// 下载线程的数量
	private File file;
	private UpgradeDB dao;
	private Context context;
	private Handler mHandler;
	Message msg = new Message();
	/* 是否停止下载 */
	public boolean isPause;
	private final int SERVICENOFILE = 256;// 服务器没有这个文件

	public DownloadManager(Context context, UpgradeDB dao2, Handler handler) {
		this.context = context;
		dao = dao2;
		this.mHandler = handler;
	}

	// 下载
	public void download(String path, ProgressBarListener listener)
			throws Exception {
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(3000);
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("server no response!");
			} else {
				// 得到文件的大小
				int filesize = conn.getContentLength();
				conn.disconnect();
				// 设置进度条的最大刻度
				listener.getMax(filesize);

				// 判断下载记录是否存在
				boolean isExist = dao.isExist(path);
				if (isExist) {
					// 得到下载的总长度，设置进度条的刻度
					int count = dao.queryCount(path);
					if (filesize < count) {
						dao.delete(path);
						// 保存下载记录
						for (int i = 0; i < threadsize; i++) {
							DownloadInfo info = new DownloadInfo();
							info.setPath(path);
							info.setThreadid(i);
							dao.insertData(info);
						}
					} else {
						listener.getDownload(count);
					}
				} else {
					dao.save(filesize);
					// 保存下载记录
					for (int i = 0; i < threadsize; i++) {
						DownloadInfo info = new DownloadInfo();
						info.setPath(path);
						info.setThreadid(i);
						dao.insertData(info);
					}
				}
				// 创建一个和服务器大小一样的文件
				Log.v("文件路径", path);
				file = new File(Environment.getExternalStorageDirectory(),
						getFileName(path));
				RandomAccessFile doOut = new RandomAccessFile(file, "rwd");
				doOut.setLength(filesize);
				doOut.close();
				// 计算出每条线程下载的数据量
				int block = filesize % threadsize == 0 ? (filesize / threadsize)
						: (filesize / threadsize + 1);

				// 开始线程下载
				for (int i = 0; i < threadsize; i++) {
					new DownloadThread(i, path, file, block, listener, context,
							dao).start();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 服务器没有打开
			msg.what = SERVICENOFILE;
			mHandler.sendMessage(msg);
			if (dao != null)
				dao.closeDB();
		}
	}

	// 得到文件的名称
	private String getFileName(String path) {
		return path.substring(path.lastIndexOf("/") + 1);
	}
}
