package xies.academy.hebuni.com.cateringordersystem.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import xies.academy.hebuni.com.cateringordersystem.util.ParamUtil;

public class DownloadDBHelper extends SQLiteOpenHelper {

	private final static String name = "download.db";

	public DownloadDBHelper(Context context) {
		super(context, name, null, ParamUtil.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table upgrade(_id integer primary key autoincrement,"
				+ "path text,"
				+ "threadid integer,"
				+ "downloadlength integer," + "filesize integer);");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS download");
		onCreate(db);
	}

}
