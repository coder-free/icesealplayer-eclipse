package com.zbf.iceseal.base;

import com.zbf.iceseal.util.CommonData;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static int defaultVersion = 1;

	public DatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	public DatabaseHelper(Context context, String name,
			CursorFactory factory) {
		this(context, name, factory, defaultVersion);
	}
	
	public DatabaseHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}
	
	public DatabaseHelper(Context context, String name) {
		this(context, name, defaultVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/**
		 * 创建歌曲表的SQL语句
		 */
		String sqlSongs = "CREATE  TABLE IF NOT EXISTS "
				+ CommonData.DBT_SONGSTABLENAME 
				+ "("
				+ "[id] integer PRIMARY KEY AUTOINCREMENT,"
				+ "[name] varchar(256)," 
				+ "[genre] varchar(256)," 
				+ "[album] varchar(256)," 
				+ "[artist] varchar(64),"
				+ "[path] varchar(1024),"
				+ "[duration] integer DEFAULT 0,"
				+ "[playcount] integer DEFAULT 0,"
				+ "[islike] integer DEFAULT 0," 
				+ "[listid] integer,"
				+ "[folderid] integer,"
				+ "[createtime] datetime,"
				+ "[size] integer,"
				+ "[namespell] varchar(8),"
				+ "[lastplaytime] datetime"
				+ ")";

		/**
		 * 创建播放列表表的SQL语句
		 */
		String sqlPlaylist = "CREATE  TABLE IF NOT EXISTS "
				+ CommonData.DBT_PLAYLISTTABLENAME
				+ "("
				+ "[id] integer PRIMARY KEY AUTOINCREMENT,"
				+ "[name] varchar(32)," 
				+ "[sort] integer"
				+ ")";
		
		/**
		 * 创建歌曲文件夹表的SQL语句
		 */
		String sqlFolder = "CREATE  TABLE IF NOT EXISTS "
				+ CommonData.DBT_SONGSFOLDERTABLENAME
				+ "("
				+ "[id] integer PRIMARY KEY AUTOINCREMENT,"
				+ "[name] varchar(256),"
				+ "[path] varchar(1024)" 
				+ ")";

		ContentValues values1 = new ContentValues();
		values1.put("name", "所有歌曲");
		values1.put("sort", "1");
		ContentValues values2 = new ContentValues();
		values2.put("name", "经常播放");
		values2.put("sort", "2");
		ContentValues values3 = new ContentValues();
		values3.put("name", "最近播放");
		values3.put("sort", "3");
		ContentValues values4 = new ContentValues();
		values4.put("name", "最近添加");
		values4.put("sort", "4");
		ContentValues values5 = new ContentValues();
		values5.put("name", "我的最爱");
		values5.put("sort", "5");
		db.beginTransaction(); // 手动设置开始事务
		try {
			db.execSQL(sqlSongs);
			db.execSQL(sqlPlaylist);
			db.execSQL(sqlFolder);
			db.insert(CommonData.DBT_PLAYLISTTABLENAME, null, values1);
			db.insert(CommonData.DBT_PLAYLISTTABLENAME, null, values2);
			db.insert(CommonData.DBT_PLAYLISTTABLENAME, null, values3);
			db.insert(CommonData.DBT_PLAYLISTTABLENAME, null, values4);
			db.insert(CommonData.DBT_PLAYLISTTABLENAME, null, values5);
			// 设置事务处理成功，不设置会自动回滚不提交。
			db.setTransactionSuccessful();
			// 在setTransactionSuccessful和endTransaction之间不进行任何数据库操作
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction(); // 处理完成
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
