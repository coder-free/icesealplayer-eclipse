package com.zbf.iceseal.base;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;

import com.zbf.iceseal.bean.FolderBean;
import com.zbf.iceseal.bean.SongBean;
import com.zbf.iceseal.bean.SonglistBean;
import com.zbf.iceseal.service.MusicImportService;
import com.zbf.iceseal.util.CommonData;
import com.zbf.iceseal.util.DateTools;
import com.zbf.iceseal.util.PinyinTools;
import com.zbf.iceseal.util.TagTools;

public class BaseDao {
	private DatabaseHelper dbHelper;
	public BaseDao(Context context) {
		if(dbHelper == null) {
			dbHelper = new DatabaseHelper(context, CommonData.DB_NAME, CommonData.DB_VERSION);
		}
	}
	public void add(JSONObject... datas) throws Exception {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		for(JSONObject data : datas) {
			@SuppressWarnings("rawtypes")
			Iterator iterator = data.keys();
			ContentValues values = new ContentValues();
			while(iterator.hasNext()) {
				String key = (String)iterator.next();
				if(!CommonData.JSONK_TABLENAME.equals(key) && !"id".equals(key)) {
					values.put(key, data.getString(key));
				}
			}
			database.insert(data.getString(CommonData.JSONK_TABLENAME), null, values);
		}
		database.close();
	}
	
	public void addOrUpdate(JSONObject... datas) throws Exception {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		for(JSONObject data : datas) {
			if(CommonData.DBT_SONGSTABLENAME.equals(data.getString(CommonData.JSONK_TABLENAME))) {
				Cursor cursor;
				cursor = database.query(data.getString(CommonData.JSONK_TABLENAME), new String[]{"id"}, "path=?", new String[]{data.getString("path")}, null, null, null);
				if(cursor.moveToNext()) {
					@SuppressWarnings("rawtypes")
					Iterator iterator = data.keys();
					ContentValues values = new ContentValues();
					while(iterator.hasNext()) {
						String key = (String)iterator.next();
						if(!CommonData.JSONK_TABLENAME.equals(key) && !"id".equals(key) && !"CREATOR".equals(key)) {
							values.put(key, data.getString(key));
						}
					}
					database.update(data.getString(CommonData.JSONK_TABLENAME), values, "id=?", new String[]{cursor.getString(cursor.getColumnIndex("id"))});
					cursor.close();
				} else {
					cursor.close();
					String id = "0";
					String songPath = data.getString("path");
					String folderPath = songPath.substring(0, songPath.lastIndexOf('/'));
					cursor = database.query(CommonData.DBT_SONGSFOLDERTABLENAME, new String[]{"id"}, "path=?", new String[]{folderPath}, null, null, null);
					if(cursor.moveToNext()) {
						id = cursor.getString(cursor.getColumnIndex("id"));
						cursor.close();
					} else {
						cursor.close();
						FolderBean folderBean = new FolderBean();
						File folder = new File(folderPath);
						folderBean.setName(folder.getName());
						folderBean.setPath(folderPath);
						JSONObject jsonFolder = folderBean.getJsonObject();
						jsonFolder.put(CommonData.JSONK_TABLENAME, CommonData.DBT_SONGSFOLDERTABLENAME);
						@SuppressWarnings("rawtypes")
						Iterator iterator = jsonFolder.keys();
						ContentValues values = new ContentValues();
						while(iterator.hasNext()) {
							String key = (String)iterator.next();
							if(!CommonData.JSONK_TABLENAME.equals(key) && !"id".equals(key) && !"count".equals(key)) {
								values.put(key, jsonFolder.getString(key));
							}
						}
						database.insert(CommonData.DBT_SONGSFOLDERTABLENAME, null, values);
						cursor = database.query(CommonData.DBT_SONGSFOLDERTABLENAME, new String[]{"id"}, "path=?", new String[]{folderPath}, null, null, null);
						if(cursor.moveToNext()) {
							id = cursor.getString(cursor.getColumnIndex("id"));
						}
						cursor.close();
					}
					@SuppressWarnings("rawtypes")
					Iterator iterator = data.keys();
					ContentValues values = new ContentValues();
					while(iterator.hasNext()) {
						String key = (String)iterator.next();
						if(!CommonData.JSONK_TABLENAME.equals(key) && !"id".equals(key) && !"CREATOR".equals(key)) {
							values.put(key, data.getString(key));
						}
					}
					values.put("folderid", id);
					values.put("createtime", DateTools.getNow());
					database.insert(data.getString(CommonData.JSONK_TABLENAME), null, values);
				}
			} else {
				@SuppressWarnings("rawtypes")
				Iterator iterator = data.keys();
				ContentValues values = new ContentValues();
				while(iterator.hasNext()) {
					String key = (String)iterator.next();
					if(!CommonData.JSONK_TABLENAME.equals(key) && !"id".equals(key) && !"count".equals(key)) {
						values.put(key, data.getString(key));
					}
				}
				database.insert(data.getString(CommonData.JSONK_TABLENAME), null, values);
			}
		}
		database.close();
	}
	
	public void update(String[] whereClause, List<String[]> whereArgs, JSONObject[] datas) throws Exception {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		for(int i = 0; i < ( ( datas.length < whereArgs.size() ? datas.length : whereArgs.size() ) < whereClause.length ? ( datas.length < whereArgs.size() ? datas.length : whereArgs.size() ) : whereClause.length ); i++) {
			JSONObject data = datas[i];
			@SuppressWarnings("rawtypes")
			Iterator iterator = data.keys();
			ContentValues values = new ContentValues();
			while(iterator.hasNext()) {
				String key = (String)iterator.next();
				if(!CommonData.JSONK_TABLENAME.equals(key) && !"id".equals(key)) {
					values.put(key, data.getString(key));
				}
			}
			database.update(data.getString(CommonData.JSONK_TABLENAME), values, whereClause[i], whereArgs.get(i));
		}
		database.close();
	}
	public void update(String whereClause, String[] whereArgs, JSONObject data) throws Exception {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		@SuppressWarnings("rawtypes")
		Iterator iterator = data.keys();
		ContentValues values = new ContentValues();
		while(iterator.hasNext()) {
			String key = (String)iterator.next();
			if(!CommonData.JSONK_TABLENAME.equals(key) && !"id".equals(key)) {
				values.put(key, data.getString(key));
			}
		}
		database.update(data.getString(CommonData.JSONK_TABLENAME), values, whereClause, whereArgs);
		database.close();
	}
	public void delete(String tableName, String whereClause, String[] whereArgs) throws Exception {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		database.delete(tableName, whereClause, whereArgs);
		database.close();
	}
	public JSONObject[] queryArray(String table, String[] columns, String whereClause, String[] whereArgs, String groupBy, String having, String orderBy, String limit) throws Exception {
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		Cursor cursor = database.query(table, columns, whereClause, whereArgs, groupBy, having, orderBy, limit);
		if(cursor.getCount() > 0) {
			JSONObject[] returnValues = new JSONObject[cursor.getCount()];
			for(int i = 0; cursor.moveToNext(); i++) {
				returnValues[i] = new JSONObject();
				for(int j = 0; j < cursor.getColumnCount(); j++) {
					returnValues[i].put(cursor.getColumnName(j), cursor.getString(j));
				}
			}
			cursor.close();
			database.close();
			return returnValues;
		}
		cursor.close();
		database.close();
		return new JSONObject[0];
	}
	public JSONObject queryBean(String table, String[] columns, String whereClause, String[] whereArgs) throws Exception {
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		Cursor cursor = database.query(table, columns, whereClause, whereArgs, null, null, null, null);
		if(cursor.moveToNext()) {
			JSONObject returnValue = new JSONObject();
			for(int i = 0; i < cursor.getColumnCount(); i++) {
				returnValue.put(cursor.getColumnName(i), cursor.getString(i));
			}
			cursor.close();
			database.close();
			return returnValue;
		}
		cursor.close();
		database.close();
		return null;
	}
	public boolean setLike(int id, boolean isLike) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("islike", isLike ? 1 : 0);
		int column = database.update(CommonData.DBT_SONGSTABLENAME, values, "id=?", new String[]{id+""});
		database.close();
		if(column>0){
			return true;
		}
		return false;
	}
	public boolean addPlayCount(int id) throws Exception {
		JSONObject bean = queryBean(CommonData.DBT_SONGSTABLENAME, new String[]{"playcount"}, "id=?", new String[]{id+""});
		if(bean == null) {
			System.out.println("BaseDao  addPlayCount  暂无歌曲信息");
			return false;
		}
		int playcount;
		if(bean.has("playcount")) {
			playcount = bean.getInt("playcount") + 1;
		} else {
			playcount = 1;
		}
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("playcount", playcount);
		int column = database.update(CommonData.DBT_SONGSTABLENAME, values, "id=?", new String[]{id+""});
		database.close();
		if(column>0){
			return true;
		}
		return false;
	}
	
	public boolean addPlayCountAndSetLastplayTime(int id) throws Exception {
		JSONObject bean = queryBean(CommonData.DBT_SONGSTABLENAME, new String[]{"playcount"}, "id=?", new String[]{id+""});
		if(bean == null) {
			System.out.println("BaseDao  addPlayCountAndSetLastplayTime  暂无歌曲信息");
			return false;
		}
		int playcount;
		if(bean.has("playcount")) {
			playcount = bean.getInt("playcount") + 1;
		} else {
			playcount = 1;
		}
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		ContentValues values1 = new ContentValues();
		values1.put("playcount", playcount);
		int column1 = database.update(CommonData.DBT_SONGSTABLENAME, values1, "id=?", new String[]{id+""});
		
		ContentValues values2 = new ContentValues();
		values2.put("lastplaytime", DateTools.getNow());
		int column2 = database.update(CommonData.DBT_SONGSTABLENAME, values2, "id=?", new String[]{id+""});
		
		
		
		database.close();
		if(column1 > 0 && column2 > 0){
			return true;
		}
		return false;
	}
	
	public boolean setLastplayTime(int id) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("lastplaytime", DateTools.getNow());
		int column = database.update(CommonData.DBT_SONGSTABLENAME, values, "id=?", new String[]{id+""});
		database.close();
		if(column>0){
			return true;
		}
		return false;
	}
	
	public List<SongBean> getSongList(String whereClause, String[] whereValues, String orderBy, String limit) throws Exception{
		List<SongBean> songList = new ArrayList<SongBean>();
		JSONObject[] results = queryArray(CommonData.DBT_SONGSTABLENAME, new String[]{"id", "name", "artist", "path", "duration", "islike", "playcount", "lastplaytime", "createtime"}, whereClause, whereValues, null, null, orderBy, limit);
		for(JSONObject result : results) {
			songList.add(new SongBean(result));
		}
		return songList;
	}
	
	public SonglistBean getSongListbean(int listType, String listParamete) throws Exception{
		SonglistBean songList = new SonglistBean();
		String titleName = "";
		String whereClause = null;
		String limit = null;
		String orderBy = "namespell asc";
		String[] whereValues = null;
		switch (listType) {
		case CommonData.LT_PLAYLIST:
			songList.setTitleType("播放列表");
			titleName = queryBean(CommonData.DBT_PLAYLISTTABLENAME, new String[]{"name"}, "id=?", new String[]{listParamete}).getString("name");
			int listid = Integer.parseInt(listParamete);
			if(listid>5) {
				whereClause = "listid=?";
				whereValues = new String[]{listParamete};
			} else {
				switch (listid) {
				case 1:
					whereClause = null;
					break;
				case 2:
					whereClause = "playcount>?";
					whereValues = new String[]{"0"};
					orderBy = "playcount desc";
					limit = "0,30";
					break;
				case 3:
					whereClause = "lastplaytime>?";
					Calendar cal1 = Calendar.getInstance();
					cal1.add(Calendar.DAY_OF_MONTH, -10);
					whereValues = new String[]{DateTools.getDate(cal1)};
					orderBy = "lastplaytime desc";
					break;
				case 4:
					whereClause = "createtime>?";
					Calendar cal2 = Calendar.getInstance();
					cal2.add(Calendar.DAY_OF_MONTH, -10);
					whereValues = new String[]{DateTools.getDate(cal2)};
					orderBy = "createtime desc";
					break;
				case 5:
					whereClause = "islike=?";
					whereValues = new String[]{"1"};
					break;
				default:
					break;
				}
			}
			break;
		case CommonData.LT_FOLDER:
			songList.setTitleType("文件夹");
			titleName = queryBean(CommonData.DBT_SONGSFOLDERTABLENAME, new String[]{"path"}, "id=?", new String[]{listParamete}).getString("path").replace(CommonData.EXTERNALSTORAGEDIRECTORY, "");
			whereClause = "folderid=?";
			whereValues = new String[]{listParamete};
			break;
		case CommonData.LT_ALBUM:
			songList.setTitleType("专辑");
			titleName = listParamete;
			whereClause = "album=?";
			whereValues = new String[]{listParamete};
			break;
		case CommonData.LT_ARTIST:
			songList.setTitleType("艺术家");
			titleName = listParamete;
			whereClause = "artist=?";
			whereValues = new String[]{listParamete};
			break;
		}
		songList.setTitleName(titleName);
		songList.setSongList(getSongList(whereClause, whereValues, orderBy, limit));
		return songList;
	}

	public List<SongBean> getSongList(int listType, String listParamete) throws Exception{
		String whereClause = null;
		String limit = null;
		String orderBy = "namespell asc";
		String[] whereValues = null;
		switch (listType) {
		case CommonData.LT_PLAYLIST:
			int listid = Integer.parseInt(listParamete);
			if(listid>5) {
				whereClause = "listid=?";
				whereValues = new String[]{listParamete};
			} else {
				switch (listid) {
				case 1:
					whereClause = null;
					break;
				case 2:
					whereClause = "playcount>?";
					whereValues = new String[]{"0"};
					orderBy = "playcount desc";
					limit = "0,30";
					break;
				case 3:
					whereClause = "lastplaytime>?";
					Calendar cal1 = Calendar.getInstance();
					cal1.add(Calendar.DAY_OF_MONTH, -10);
					whereValues = new String[]{DateTools.getDate(cal1)};
					orderBy = "lastplaytime desc";
					break;
				case 4:
					whereClause = "createtime>?";
					Calendar cal2 = Calendar.getInstance();
					cal2.add(Calendar.DAY_OF_MONTH, -10);
					whereValues = new String[]{DateTools.getDate(cal2)};
					orderBy = "createtime desc";
					break;
				case 5:
					whereClause = "islike=?";
					whereValues = new String[]{"1"};
					break;
				default:
					break;
				}
			}
			break;
		case CommonData.LT_FOLDER:
			whereClause = "folderid=?";
			whereValues = new String[]{listParamete};
			break;
		case CommonData.LT_ALBUM:
			whereClause = "album=?";
			whereValues = new String[]{listParamete};
			break;
		case CommonData.LT_ARTIST:
			whereClause = "artist=?";
			whereValues = new String[]{listParamete};
			break;
		}
		return getSongList(whereClause, whereValues, orderBy, limit);
	}

	public static boolean saveSongInfo(Context context, String songPath, int tagReadPriority) {
		boolean isSuccess = false;
		BaseDao dao = new BaseDao(context);
		SongBean songBean = null;
		try {
			switch (tagReadPriority) {
			case MusicImportService.TAG_READ_APEV2_ID3V2_ID3V1:
				songBean = TagTools.getApeV2Tag(songPath);
				if(songBean == null) {
					songBean = TagTools.getId3V2Tag(songPath);
					if(songBean == null) {
						songBean = TagTools.getId3V1Tag(songPath);
					} else {
						songBean = TagTools.getDefaultTag(songPath);
					}
				}
				break;
			case MusicImportService.TAG_READ_APEV2_ID3V1_ID3V2:
				songBean = TagTools.getApeV2Tag(songPath);
				if(songBean == null) {
					songBean = TagTools.getId3V1Tag(songPath);
					if(songBean == null) {
						songBean = TagTools.getId3V2Tag(songPath);
					} else {
						songBean = TagTools.getDefaultTag(songPath);
					}
				}
				break;
			case MusicImportService.TAG_READ_ID3V2_APEV2_ID3V1:
				songBean = TagTools.getId3V2Tag(songPath);
				if(songBean == null) {
					songBean = TagTools.getApeV2Tag(songPath);
					if(songBean == null) {
						songBean = TagTools.getId3V1Tag(songPath);
					} else {
						songBean = TagTools.getDefaultTag(songPath);
					}
				}
				break;
			case MusicImportService.TAG_READ_ID3V1_ID3V2_APEV2:
				songBean = TagTools.getId3V1Tag(songPath);
				if(songBean == null) {
					songBean = TagTools.getId3V2Tag(songPath);
					if(songBean == null) {
						songBean = TagTools.getApeV2Tag(songPath);
					} else {
						songBean = TagTools.getDefaultTag(songPath);
					}
				}
				break;
			case MusicImportService.TAG_READ_ONLY_DEFAULT:
				songBean = TagTools.getDefaultTag(songPath);
				break;

			default:
				songBean = TagTools.getDefaultTag(songPath);
				break;
			}
			if(tagReadPriority != MusicImportService.TAG_READ_ONLY_DEFAULT) {
				Cursor mCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Media.DURATION}, MediaStore.Audio.Media.DATA + "=?", new String[]{songPath}, null);
				if(mCursor.moveToNext() && mCursor.getInt(mCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)) > 0) {
					long duration = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
					songBean.setDuration(duration);
				} else {
					Uri uri = Uri.parse(songPath);
					long duration = MediaPlayer.create(context, uri).getDuration();
					songBean.setDuration(duration);
				}
				mCursor.close();
				songBean.setSize(new File(songPath).length());
			}
			try {
				songBean.setNamespell(PinyinTools.chineneToSpell(songBean.getName().charAt(0) + ""));
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
				songBean.setNamespell(songBean.getName().substring(0, 8));
			}
			JSONObject jsonSong = songBean.getJsonObject();
			jsonSong.put(CommonData.JSONK_TABLENAME, CommonData.DBT_SONGSTABLENAME);
			dao.addOrUpdate(jsonSong);
			isSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isSuccess;
	}
	
	public int getCount(String whereClause, String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(CommonData.DBT_SONGSTABLENAME, new String[]{"count(id)"}, whereClause, whereArgs, null, null, null);
		int count = 0;
		if(cursor.moveToNext()) {
			count = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return count;
	}
	
	public static boolean hasSongs(Context context) {
		BaseDao dao = new BaseDao(context);
		return dao.getCount(null, null) > 0;
	}
}
