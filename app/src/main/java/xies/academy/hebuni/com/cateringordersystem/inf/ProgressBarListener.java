package xies.academy.hebuni.com.cateringordersystem.inf;

/**
 * 进度条监听器
 */
public interface ProgressBarListener {

	void getMax(int length);//得到文件的长度

	void getDownload(int length);//得到每次下载的长度

}
