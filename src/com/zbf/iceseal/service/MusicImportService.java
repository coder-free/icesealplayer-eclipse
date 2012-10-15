package com.zbf.iceseal.service;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.zbf.iceseal.base.BaseDao;
import com.zbf.iceseal.util.CommonData;
import com.zbf.iceseal.util.FileTools;
import com.zbf.iceseal.util.MusicFileTools;
import com.zbf.iceseal.util.StringTools;

public class MusicImportService extends Service {
	
	public static final String MUSIC_IMPORT_ACTION = "com.zbf.iceseal.MUSIC_IMPORT";
	public static final String MUSIC_IMPORT_IK = "com.zbf.iceseal.MUSIC_IMPORT.paths";
	
	public static final int TAG_READ_APEV2_ID3V2_ID3V1 = 10;
	public static final int TAG_READ_ID3V2_APEV2_ID3V1 = 11;
	public static final int TAG_READ_APEV2_ID3V1_ID3V2 = 12;
	public static final int TAG_READ_ID3V1_ID3V2_APEV2 = 13;
	public static final int TAG_READ_ONLY_DEFAULT = 14;
	private int tagReadPriority;
	private RemoteCallbackList<IMusicImportServiceCallback> mCallbacks = new RemoteCallbackList<IMusicImportServiceCallback>();
	private IMusicImportService.Stub musicImportService = new IMusicImportService.Stub() {
		
		@Override
		public void unregisterCallback(IMusicImportServiceCallback callback)
				throws RemoteException {
			if(callback != null) {
				mCallbacks.unregister(callback);
			}
		}
		
		@Override
		public void stopImport() throws RemoteException {
			if(mTask != null) {
				mTask.cancel(true);
			}
		}
		
		@Override
		public void registerCallback(IMusicImportServiceCallback callback)
				throws RemoteException {
			if(callback != null) {
				mCallbacks.register(callback);
			}
		}
	};
	
	private HandlerThread mThread;
	private MHandler mHandler;
	private AsyncTask<String, String, Integer> mTask;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(mHandler == null) {
			mThread = new HandlerThread("com.zbf.iceseal.service.MusicImportService");
			mThread.start();
			mHandler = new MHandler(mThread.getLooper());
		}
		mHandler.sendMessage(mHandler.obtainMessage(0, intent));
		return Service.START_NOT_STICKY;
	}

	@Override
	public void onCreate() {
		SharedPreferences sp = this.getSharedPreferences(CommonData.SPN_PLAYER_CONFIGURATION, Context.MODE_PRIVATE);
		tagReadPriority = sp.getInt(CommonData.SPK_TAG_READPRIORITY, TAG_READ_APEV2_ID3V2_ID3V1);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return musicImportService;
	}

	@SuppressLint({ "HandlerLeak" })
	class MHandler extends Handler {

		public MHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			final Intent intent = (Intent) msg.obj;
			List<String> paths = intent.getStringArrayListExtra(MUSIC_IMPORT_IK);
			if(paths == null) {
				paths = new ArrayList<String>();
				paths.add(CommonData.EXTERNALSTORAGEDIRECTORY);
			}
			if(msg.what == 0) {
				mTask = new AsyncTask<String, String, Integer>(){
					
					int count = 0;
					
					@Override
					protected void onPreExecute() {
						int callbackCount = mCallbacks.beginBroadcast();
						for(int i = 0; i < callbackCount; i++) {
							try {
								mCallbacks.getBroadcastItem(i).onImportStart();
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
						mCallbacks.finishBroadcast();
					}
	
					@Override
					protected void onPostExecute(Integer result) {
						int callbackCount = mCallbacks.beginBroadcast();
						for(int i = 0; i < callbackCount; i++) {
							try {
								mCallbacks.getBroadcastItem(i).onImportComplete(result);
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
						mCallbacks.finishBroadcast();
						if(!MHandler.this.hasMessages(0) && !MHandler.this.hasMessages(1)) {
							System.out.println("MusicImportService stop self");
							MusicImportService.this.stopSelf();
						}
						mHandler.sendMessage(mHandler.obtainMessage(1, intent));
					}
	
					@Override
					protected void onProgressUpdate(String... values) {
						int callbackCount = mCallbacks.beginBroadcast();
						for(int i = 0; i < callbackCount; i++) {
							try {
								mCallbacks.getBroadcastItem(i).onProgressUpdate(values[0]);
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
						mCallbacks.finishBroadcast();
					}
	
					@Override
					protected Integer doInBackground(String... params) {
						for(String dir : params) {
							File file = new File(dir);
							if(FileTools.filterFile(file, new String[]{".mp3", ".wma"})) {
								importAll(MusicImportService.this, file);
							}
						}
						return count;
					}
					
					private void importAll(Context context, File file) {
						if(MusicFileTools.isMusicFile(file)) {
							publishProgress(file.getPath());
							if(BaseDao.saveSongInfo(context, file.getPath(), TAG_READ_ONLY_DEFAULT)) {
								count ++;
							}
						} else if(file.isDirectory()){
							File[] files = file.listFiles();
							if(files != null) {
								for(File subfile: files) {
									importAll(context, subfile);
								}
							}
						}
					}
					
				}.execute(StringTools.toArray(paths));
			} else if(msg.what == 1) {
				mTask = new AsyncTask<String, String, Integer>(){
					
					int count = 0;
					
					@Override
					protected void onPreExecute() {
//						int callbackCount = mCallbacks.beginBroadcast();
//						for(int i = 0; i < callbackCount; i++) {
//							try {
//								mCallbacks.getBroadcastItem(i).onImportStart();
//							} catch (RemoteException e) {
//								e.printStackTrace();
//							}
//						}
//						mCallbacks.finishBroadcast();
					}
	
					@Override
					protected void onPostExecute(Integer result) {
//						int callbackCount = mCallbacks.beginBroadcast();
//						for(int i = 0; i < callbackCount; i++) {
//							try {
//								mCallbacks.getBroadcastItem(i).onImportComplete(result);
//							} catch (RemoteException e) {
//								e.printStackTrace();
//							}
//						}
//						mCallbacks.finishBroadcast();
						if(!MHandler.this.hasMessages(1) && !MHandler.this.hasMessages(0)) {
							System.out.println("MusicImportService stop self");
							MusicImportService.this.stopSelf();
						}
					}
	
					@Override
					protected void onProgressUpdate(String... values) {
//						int callbackCount = mCallbacks.beginBroadcast();
//						for(int i = 0; i < callbackCount; i++) {
//							try {
//								mCallbacks.getBroadcastItem(i).onProgressUpdate(values[0]);
//							} catch (RemoteException e) {
//								e.printStackTrace();
//							}
//						}
//						mCallbacks.finishBroadcast();
					}
	
					@Override
					protected Integer doInBackground(String... params) {
						for(String dir : params) {
							importAll(MusicImportService.this, new File(dir));
						}
						return count;
					}
					
					private void importAll(Context context, File file) {
						if(MusicFileTools.isMusicFile(file)) {
//							publishProgress(file.getPath());
							if(BaseDao.saveSongInfo(context, file.getPath(), tagReadPriority)) {
								count ++;
							}
						} else if(file.isDirectory()){
							File[] files = file.listFiles(new FileFilter() {
					            public boolean accept(File pathname) {
					                return pathname.isDirectory() || MusicFileTools.isMusicFile(pathname);
					            }
					        });
							if(files != null) {
								for(File subfile: files) {
									importAll(context, subfile);
								}
							}
						}
					}
					
				}.execute(StringTools.toArray(paths));
			}
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
}
