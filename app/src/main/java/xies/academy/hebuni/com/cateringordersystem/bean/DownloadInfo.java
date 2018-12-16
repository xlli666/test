package xies.academy.hebuni.com.cateringordersystem.bean;

public class DownloadInfo {


    private int _id;
    private String path;
    private int threadid;
    private int downloadsize;
    private int filesize;

    public int getFilesize() {
        return filesize;
    }

    public void setFilesize(int filesize) {
        this.filesize = filesize;
    }

    public DownloadInfo() {
        super();
    }

    public DownloadInfo(int _id, String path, int threadid, int downloadsize) {
        super();
        this._id = _id;
        this.path = path;
        this.threadid = threadid;
        this.downloadsize = downloadsize;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getThreadid() {
        return threadid;
    }

    public void setThreadid(int threadid) {
        this.threadid = threadid;
    }

    public int getDownloadsize() {
        return downloadsize;
    }

    public void setDownloadsize(int downloadsize) {
        this.downloadsize = downloadsize;
    }


}
