package xies.academy.hebuni.com.cateringordersystem.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import xies.academy.hebuni.com.cateringordersystem.bean.DownloadInfo;

public class UpgradeDB {


    private SQLiteDatabase sqlDb;
    private DownloadDBHelper helper;
    private final String TB_UPGRADE = "upgrade"; //表名称

    public UpgradeDB(Context context) {
        helper = new DownloadDBHelper(context);
        sqlDb = helper.getReadableDatabase();
    }

    public void insertData(DownloadInfo downloadInfo) {

        ContentValues cv = new ContentValues();
        cv.put("path", downloadInfo.getPath());
        cv.put("threadid", downloadInfo.getThreadid());
        sqlDb.insert(TB_UPGRADE, null, cv);
    }


    /**
     * @param fileSize
     */
    public void save(int fileSize) {
        ContentValues values = new ContentValues();
        values.put("filesize", fileSize);
        sqlDb.insert(TB_UPGRADE, null, values);
    }

    public void update(DownloadInfo info) {
        ContentValues values = new ContentValues();
        values.put("downloadlength", info.getDownloadsize());
        if (sqlDb != null && sqlDb.isOpen()) {
            int i = sqlDb.update(TB_UPGRADE, values, " path = ? and threadid = ? ", new String[]{info.getPath(), info.getThreadid() + ""});
            if (i != 1)
                System.out.println("=======================false==================================");
        }

    }

    public void delete(String path) {
        sqlDb.delete(TB_UPGRADE, " path = ?", new String[]{path});
    }

    public boolean isExist(String path) {
        boolean isExist = false;
        Cursor c = sqlDb.query(TB_UPGRADE, new String[]{"threadid"}, " path = ? ", new String[]{path}, null, null, null);
        if (c != null && c.moveToFirst()) {
            isExist = true;
        }
        if (c != null)
            c.close();
        return isExist;
    }

    public int queryCount(String path) {
        int count = 0;
        Cursor c = sqlDb.query(TB_UPGRADE, new String[]{"downloadlength"}, " path = ? ", new String[]{path}, null, null, null);
        while (c != null && c.moveToNext()) {
            int length = c.getInt(0);
            count = count + length;
        }
        if (c != null)
            c.close();
        return count;
    }

    public int query(DownloadInfo info) {
        int count = 0;
        Cursor c = sqlDb.query(TB_UPGRADE, new String[]{"downloadlength"}, " path = ? and threadid = ?", new String[]{info.getPath(), info.getThreadid() + ""}, null, null, null);
        if (c != null && c.moveToNext()) {
            count = c.getInt(0);
        }
        if (c != null)
            c.close();
        return count;
    }

    public int queryFileSize(DownloadInfo info) {
        int count = 0;
        Cursor c = sqlDb.query(TB_UPGRADE, new String[]{"filesize"}, " path = ?", new String[]{info.getPath()}, null, null, null);
        if (c != null && c.moveToNext()) {
            count = c.getInt(0);
        }
        if (c != null)
            c.close();
        return count;
    }

    public void closeDB() {
        if (sqlDb != null)
            sqlDb.close();
    }


}
