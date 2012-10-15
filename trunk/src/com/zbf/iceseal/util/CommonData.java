package com.zbf.iceseal.util;

import android.os.Environment;

public class CommonData {
	
	/** 默认外部存储路径 */
	public static final String EXTERNALSTORAGEDIRECTORY = Environment
			.getExternalStorageDirectory().getPath();
	/** 歌曲专辑图片缓存路径 */
	public static final String IMG_TEMPPATH = EXTERNALSTORAGEDIRECTORY
			+ "/.BFPlayer/temp/image";

	/** PlayerEvent */
	public static final String IK_PLAYER_EVENT_TYPE = "PLAYER_EVENT_TYPE";
	public static final int PLAYER_EVENT_READY = 0xfeff;
	public static final int PLAYER_EVENT_PROGRESS = 0xff00;
	public static final String IK_PLAYER_EVENT_PROGRESS = "PLAYER_EVENT_PROGRESS";
	public static final int PLAYER_EVENT_SONG_CHANGE = 0xff01;
	public static final String IK_PLAYER_EVENT_SONG_CHANGE_POSITION = "PLAYER_EVENT_SONG_CHANGE_POSITION";
	public static final int PLAYER_EVENT_SONGLIST_CHANGE = 0xff02;
	public static final String IK_PLAYER_EVENT_SONGLIST_CHANGE_TYPE = "PLAYER_EVENT_SONGLIST_CHANGE_TYPE";
	public static final String IK_PLAYER_EVENT_SONGLIST_CHANGE_PAREMETE = "PLAYER_EVENT_SONGLIST_CHANGE_PAREMETE";
	public static final int PLAYER_EVENT_LOOPMODE_CHANGE = 0xff03;
	public static final String IK_PLAYER_EVENT_LOOPMODE_CHANGE = "PLAYER_EVENT_LOOPMODE_CHANGE";
	public static final int PLAYER_EVENT_START = 0xff04;
	public static final String IK_PLAYER_EVENT_START = "PLAYER_EVENT_START";
	public static final int PLAYER_EVENT_PAUSE = 0xff05;
	public static final String IK_PLAYER_EVENT_PAUSE = "PLAYER_EVENT_PAUSE";
	
	/** 歌曲信息表名 */
	public static final String DBT_SONGSTABLENAME = "m_songs";
	/** 歌曲文件夹表名 */
	public static final String DBT_SONGSFOLDERTABLENAME = "m_songfolder";
	/** 播放列表表名 */
	public static final String DBT_PLAYLISTTABLENAME = "m_playlist";

	/** 数据库版本1.0 */
	public static final int DB_VERSION = 1;
	/** 数据库名称 */
	public static final String DB_NAME = "bfplayerdb";
	
	/** 列表类型:播放列表 */
	public static final int LT_PLAYLIST = -101;
	/** 列表类型:文件夹 */
	public static final int LT_FOLDER = -102;
	/** 列表类型:专辑 */
	public static final int LT_ALBUM = -103;
	/** 列表类型:艺术家 */
	public static final int LT_ARTIST = -104;
	
	/** JSON key 值 */
	public static final String JSONK_TABLENAME = "tablename";
	
	/** SharedPreferences */
	public static final String SPN_PLAYER_CONFIGURATION = "PLAYER_CONFIGURATION";
	public static final String SPK_TAG_READPRIORITY = "TAGREADPRIORITY";
	

	public static final String SPN_PLAYER_STATE = "PLAYER_STATE";
	public static final String SPK_PLAYER_STATE_LISTTYPE = "PLAYER_STATE_LISTTYPE";
	public static final String SPK_PLAYER_STATE_LISTPARAMETE = "PLAYER_STATE_LISTPARAMETE";
	public static final String SPK_PLAYER_STATE_POSITION= "PLAYER_STATE_POSITION";
	public static final String SPK_PLAYER_STATE_TIME = "PLAYER_STATE_TIME";
	public static final String SPK_PLAYER_STATE_LOOPMODE = "PLAYER_STATE_LOOPMODE";
	public static final int LOOP_MODE_LIST_ONCE = 100;
	public static final int LOOP_MODE_LIST_REPEAT = 101;
	public static final int LOOP_MODE_SINGLE_REPEAT = 102;
	public static final int LOOP_MODE_LIST_RANDOM = 103;
	public static final int[] LOOP_MODE = new int[]{LOOP_MODE_LIST_ONCE, LOOP_MODE_LIST_REPEAT, LOOP_MODE_SINGLE_REPEAT, LOOP_MODE_LIST_RANDOM};
	
}
