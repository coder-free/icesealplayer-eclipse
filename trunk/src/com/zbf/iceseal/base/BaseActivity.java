package com.zbf.iceseal.base;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.zbf.iceseal.R;
import com.zbf.iceseal.activity.MainplayActivity;
import com.zbf.iceseal.bean.SongBean;
import com.zbf.iceseal.service.IMusicImportService;
import com.zbf.iceseal.service.IMusicImportServiceCallback;
import com.zbf.iceseal.service.IPlayerService;
import com.zbf.iceseal.service.MusicImportService;
import com.zbf.iceseal.service.PlayerService;
import com.zbf.iceseal.util.CommonData;
import com.zbf.iceseal.util.ImageTools;
import com.zbf.iceseal.util.UtilTools;
import com.zbf.iceseal.view.SpecialGallery;
import com.zbf.iceseal.view.ViewTools;

abstract public class BaseActivity extends Activity {
	
	protected static final int CURPLAYBAR_EVENT_ALBUM_CLICK = 0xfff0;
	protected static final int CURPLAYBAR_EVENT_ALBUM_LONGCLICK = 0xfff1;
	protected static final int CURPLAYBAR_EVENT_PROGRESSBAR_CLICK = 0xfff2;
	protected static final int CURPLAYBAR_EVENT_PROGRESSBAR_SEEK = 0xfff3;
	protected static final int CURPLAYBAR_EVENT_SCROLL_LEFT = 0xfff4;
	protected static final int CURPLAYBAR_EVENT_SCROLL_RIGHT = 0xfff5;
	protected static final int CURPLAYBAR_EVENT_BTN_CLICK = 0xfff6;
	
	protected Activity mContext = this;
	protected PlayerEventReceiver playerEventReceiver;
	protected int layoutResID;
	protected IPlayerService mPlayerService;
	protected ServiceConnection mPlayerConn;
	protected ServiceConnection mMusicImportConn;
	protected List<SongBean> songlist;
	protected int listType;
	protected String listParamete;
	protected int position;
	protected OnMusicImportListener musicImportListener;

	private boolean isHasCurplayBar;
	private ImageView ivCurplay;
	private ProgressBar pbCurplay;
	private ToggleButton btnCurplay;
	private SpecialGallery sgCurplayText;
	private ProgressDialog progressDialog;
	private TextGalleryAdapter sgTextGalleryAdapter;
	private Bitmap curPlayBmp;
	private Camera mCamera;
	private Matrix mMatrix;
	
	private Handler updateProgressHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if(progressDialog != null) {
					progressDialog.setMessage(msg.obj.toString().replace("/mnt", ""));
				}
				if(musicImportListener != null) {
					musicImportListener.onImportUpdate(msg.obj.toString().replace("/mnt", ""));
				}
				break;
			case 1:
				progressDialog = ViewTools.showHorizontalProgressDialog(mContext, "正在导入...", "请稍候", 100, true, new DialogInterface.OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						progressDialog = null;
						System.out.println("cancel");
						if(mPlayerService == null) {
							bindPlayerService();
						}
					}
				});
				if(musicImportListener != null) {
					musicImportListener.onImportStart();
				}
				break;
			case 2:
				if(musicImportListener != null) {
					musicImportListener.onImportEnd(msg.arg1);
				}
				if(progressDialog != null) {
					progressDialog.dismiss();
				}
				ViewTools.showShortToast(mContext, "共导入" + msg.arg1 + "首歌曲...");
				if(mPlayerService == null) {
					bindPlayerService();
				}
				break;
			default:
				break;
			}
		}
		
	};
	
	private Handler changeSongHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			try {
				mPlayerService.playThis(msg.arg1, 0, null);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
	};
	
	private IMusicImportServiceCallback.Stub callback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCamera = new Camera();
		mMatrix = new Matrix();
	}
	
	protected void bindImportMusicService(ArrayList<String> selectedPaths) {
		Intent service = new Intent("com.zbf.iceseal.MUSIC_IMPORT");
		service.putExtra(MusicImportService.MUSIC_IMPORT_IK, selectedPaths);
		callback = new IMusicImportServiceCallback.Stub() {
			@Override
			public IBinder asBinder() {
				return callback;
			}
			
			@Override
			public void onProgressUpdate(String message) throws RemoteException {
				updateProgressHandler.sendMessage(updateProgressHandler.obtainMessage(0, message));
			}
			
			@Override
			public void onImportStart() throws RemoteException {
				updateProgressHandler.sendEmptyMessage(1);
			}
			
			@Override
			public void onImportComplete(int count) throws RemoteException {
				updateProgressHandler.sendMessage(updateProgressHandler.obtainMessage(2, count, 0));
			}
		};
		mMusicImportConn = getMusicImportConn(callback);
		this.bindService(service, mMusicImportConn, Service.BIND_AUTO_CREATE);
		this.startService(service);
	}

	public ServiceConnection getMusicImportConn(final IMusicImportServiceCallback.Stub callback) {
		ServiceConnection conn = new ServiceConnection() {

			IMusicImportService mService;
			IMusicImportServiceCallback.Stub mCallback = callback;
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				if(mService != null) {
					try {
						mService.unregisterCallback(mCallback);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					mService = null;
				}
				if(mPlayerService == null) {
					bindPlayerService();
				}
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mService = IMusicImportService.Stub.asInterface(service);
				if(mService != null) {
					try {
						mService.registerCallback(mCallback);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
		};
		return conn;
	}

	private void bindPlayerService() {
		Intent service = new Intent(PlayerService.PLAYER_SERVICE_ACTION);
		mPlayerConn = new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				mPlayerService = null;
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mPlayerService = IPlayerService.Stub.asInterface(service);
				if(mPlayerService != null) {
					try {
						mPlayerService.broadcastSongChange();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
		};
		bindService(service, mPlayerConn, Service.BIND_AUTO_CREATE);
		startService(service);
	}
	/**
	 * 应该最先调用此方法
	 * @param isShowCurplayBar 是否显示当前播放条
	 */
	protected void init(boolean isShowCurplayBar) {
		playerEventReceiver = new PlayerEventReceiver();
		initData(getIntent());
		isHasCurplayBar = isShowCurplayBar;
		initView();
		setListener();
		initOther();
		boolean hasSongs = ViewTools.checkMedialib(mContext, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				ViewTools.createFolderSelectWindow(mContext, ivCurplay, new ViewTools.OnFileSelectButtonClickListener() {
					
					@Override
					public void onFileSelectButtonClick(ArrayList<String> selectedPaths) {
						bindImportMusicService(selectedPaths);
					}

					@Override
					public void onCancelButtonClick() {
						ViewTools.showShortToast(mContext, "您没有选择任何歌曲...");
					}
					
				});
			}
			
		});
		if(hasSongs && mPlayerService == null) {
			bindPlayerService();
		}
	}
	
	@Override
	public void setContentView(int layoutResID) {
		this.layoutResID = layoutResID;
		if(isHasCurplayBar) {
			RelativeLayout baseLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.base_curplaybar, null);
			ivCurplay = (ImageView) baseLayout.findViewById(R.id.ivCurplay);
			pbCurplay = (ProgressBar) baseLayout.findViewById(R.id.pbCurplay);
			btnCurplay = (ToggleButton) baseLayout.findViewById(R.id.btnCurplay);
			sgCurplayText = (SpecialGallery) baseLayout.findViewById(R.id.sgCurplayText);
			View child = getLayoutInflater().inflate(layoutResID, null);
			baseLayout.addView(child, 0);
			super.setContentView(baseLayout);
			btnCurplay.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					try {
						mPlayerService.changeState();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			});
			ivCurplay.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, MainplayActivity.class);
					mContext.startActivity(intent);
				}
			});
			sgCurplayText.setOnGalleryChildMoveListener(new SpecialGallery.OnGalleryChildMoveListener() {
				
				@Override
				public void onChildMoving(int offset, View child, Transformation t) {
					if(curPlayBmp == null) {
						return;
					}
					final float angle = offset * 90.0f / 50;
					if(filterAngle(angle)){
						return;
					}
					mMatrix.reset();
					mCamera.save();
					mCamera.rotateY(angle);
					mCamera.getMatrix(mMatrix);
					mCamera.restore();
					float ivCenterX = ivCurplay.getLeft() + ivCurplay.getWidth() / 2;
					mMatrix.preTranslate(-ivCenterX, 0);
					mMatrix.postTranslate(ivCenterX, 0);
					Bitmap bmp = Bitmap.createBitmap(curPlayBmp, 0, 0, curPlayBmp.getWidth(), curPlayBmp.getHeight(), mMatrix, false);
					BitmapDrawable d = new BitmapDrawable(bmp);
					try {
						Method m = ImageView.class.getDeclaredMethod("updateDrawable", new Class[]{Drawable.class});
						m.setAccessible(true);
						m.invoke(ivCurplay, new Object[]{d});
						ivCurplay.invalidate();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				private boolean filterAngle(float angle) {
					return angle >= 89 && angle <= 91 || angle >= -91 && angle <= -89;
				}
				
			});
			sgCurplayText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					if(position == BaseActivity.this.position) {
						return;
					}
					curPlayBmp = ImageTools.getAlbumImage(songlist.get(position).getPath(), mContext.getResources(), R.drawable.defaultalbumimage);
					changeSongHandler.removeMessages(0);
					changeSongHandler.sendMessageDelayed(changeSongHandler.obtainMessage(0, position, 0), 800);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					
				}
			});
			sgTextGalleryAdapter = new TextGalleryAdapter();
			sgCurplayText.setAdapter(sgTextGalleryAdapter);
		} else {
			super.setContentView(layoutResID);
		}
	}

	abstract protected void initData(Intent intent);
	
	abstract protected void initView();
	
	abstract protected void setListener();
	
	abstract protected void initOther();
	
	abstract protected void PlayerEvent(Intent intent);
	
	protected class PlayerEventReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			PlayerEvent(intent);
			if(isHasCurplayBar) {
				switch (intent.getIntExtra(CommonData.IK_PLAYER_EVENT_TYPE, 0)) {
				
				case CommonData.PLAYER_EVENT_PROGRESS:
					pbCurplay.setProgress(intent.getIntExtra(CommonData.IK_PLAYER_EVENT_PROGRESS, 0));
					if(!btnCurplay.isChecked()) {
						btnCurplay.setChecked(true);
					}
					break;
					
				case CommonData.PLAYER_EVENT_SONG_CHANGE:
					if(listType == - 101 && ("2".equals(listParamete) || "3".equals(listParamete))) {
						try {
							songlist = new BaseDao(mContext).getSongList(listType, listParamete);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if(songlist == null || songlist.size() == 0) {
						System.out.println("songlist is null");
						break;
					}
					position = intent.getIntExtra(CommonData.IK_PLAYER_EVENT_SONG_CHANGE_POSITION, 0);
					pbCurplay.setProgress(0);
					if(position == -1) {
						position = 0;
						SongBean song = songlist.get(position);
						btnCurplay.setChecked(false);
						pbCurplay.setMax(song.getDuration().intValue());
						curPlayBmp = BitmapFactory.decodeResource(getResources(), R.drawable.defaultalbumimage);
					} else {
						SongBean song = songlist.get(position);
						pbCurplay.setMax(song.getDuration().intValue());
						curPlayBmp = ImageTools.getAlbumImage(song.getPath(), mContext.getResources(), R.drawable.defaultalbumimage);
					}
					ivCurplay.setImageBitmap(curPlayBmp);
					if(sgCurplayText.getSelectedItemId() != position){
						sgCurplayText.setSelection(position);
					}
					break;
					
				case CommonData.PLAYER_EVENT_SONGLIST_CHANGE:
					listType = intent.getIntExtra(CommonData.IK_PLAYER_EVENT_SONGLIST_CHANGE_TYPE, 0);
					listParamete = intent.getStringExtra(CommonData.IK_PLAYER_EVENT_SONGLIST_CHANGE_PAREMETE);
					BaseDao dao = new BaseDao(mContext);
					try {
						songlist = dao.getSongList(listType, listParamete);
						sgTextGalleryAdapter.notifyDataSetChanged();
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
					
				case CommonData.PLAYER_EVENT_PAUSE:
					if(btnCurplay.isChecked()) {
						btnCurplay.setChecked(false);
					}
					break;

				default:
					break;
				}
			}
		}
		
	}
	
	@Override
	protected void onStart() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(PlayerService.PLAYER_EVENT_ACTION);
		registerReceiver(playerEventReceiver, filter);
		if(isHasCurplayBar && mPlayerService != null) {
			try {
				mPlayerService.broadcastSongChange();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		super.onStart();
	}

	@Override
	protected void onStop() {
		unregisterReceiver(playerEventReceiver);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if(mPlayerConn != null) {
			unbindService(mPlayerConn);
		}
		if(mMusicImportConn != null) {
			unbindService(mMusicImportConn);
		}
		if(UtilTools.isLastActivity(mContext)) {
			if(mPlayerService != null) {
				try {
					mPlayerService.stopSelf();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		super.onDestroy();
	}

//	
//	private class MyViews {
//		private SparseArray<MyView> views;
//		private int maxCount = Integer.MAX_VALUE;
//		private int initCount = 3;
//		private int maxFree = 3;
//		private boolean isCleanView;
//		public MyViews() {
//			views = new SparseArray<MyView>();
//			while(initCount-- > 0) {
//				views.put(views.size(), new MyView());
//			}
//		}
//		public int getCurViewCount() {
//			return views.size();
//		}
//		public View getView(int position) {
//			MyView freeView = findFreeView();
//			if(isCleanView) {
//				cleanView();
//			}
//			if(freeView != null) {
//				freeView.setUsed(true);
//				freeView.setPosition(position);
//				return freeView.getView();
//			} else if(freeView == null  && views.size() < maxCount) {
//				MyView myView = new MyView();
//				views.put(views.size(), myView);
//				myView.setUsed(true);
//				myView.setPosition(position);
//				isCleanView = true;
//				return myView.getView();
//			}
//			return null;
//		}
//		private void cleanView() {
//			new AsyncTask<Void, Void, Void>() {
//				@Override
//				protected void onPreExecute() {
//					
//				}
//				@Override
//				protected void onPostExecute(Void result) {
//					isCleanView = false;
//				}
//				@Override
//				protected Void doInBackground(Void... params) {
//					int count = -1;
//					for(int i = 0; i < views.size(); i++) {
//						if(findFreeView() != null) {
//							if(count < maxFree) {
//								count ++;
//							} else {
//								views.remove(i);
//							}
//						}
//					}
//					return null;
//				}
//			}.execute();
//		}
//		public View getThisView(int position) {
//			for(int i = 0; i < views.size(); i++) {
//				if(views.get(i) != null  && views.get(i).getPosition() == position) {
//					return views.get(i).getView();
//				}
//			}
//			return null;
//		}
//		private MyView findFreeView() {
//			for(int i = 0; i < views.size(); i++) {
//				if(views.get(i) != null  && !views.get(i).isUsed()) {
//					return views.get(i);
//				}
//			}
//			return null;
//		}
//		public void returnView(View view) {
//			for(int i = 0; i < views.size(); i++) {
//				if(views.get(i) != null && views.get(i).getView() == view) {
//					views.get(i).setUsed(false);
//				}
//			}
//		}
//	}
//	
//	private class MyView {
//		private View view;
//		private boolean isUsed;
//		private int position;
//		public MyView() {
//			view = BaseActivity.this.getLayoutInflater().inflate(R.layout.curplaybar_gallery_item, null);
//		}
//		public boolean isUsed() {
//			return isUsed;
//		}
//		public void setUsed(boolean isUsed) {
//			this.isUsed = isUsed;
//		}
//		public View getView() {
//			return view;
//		}
//		public int getPosition() {
//			return position;
//		}
//		public void setPosition(int position) {
//			this.position = position;
//		}
//	}

	public class TextGalleryAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if(songlist != null) { 
				return songlist.size();
			}
			return 0;
		}
	
		@Override
		public Object getItem(int position) {
			if(songlist != null) { 
				return songlist.get(position);
			}
			return position;
		}
	
		@Override
		public long getItemId(int position) {
			return position;
		}
	
		class Holder {
			TextView tvCurplayName;
			TextView tvCurplayArtist;
			TextView tvCurplayPath;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SongBean song = songlist.get(position);
			Holder holder;
			if(convertView == null) {
				holder = new Holder();
				convertView = BaseActivity.this.getLayoutInflater().inflate(R.layout.curplaybar_gallery_item, null);
				holder.tvCurplayName = (TextView) convertView.findViewById(R.id.tvCurplayName);
				holder.tvCurplayArtist = (TextView) convertView.findViewById(R.id.tvCurplayArtist);
				holder.tvCurplayPath = (TextView) convertView.findViewById(R.id.tvCurplayPath);
			} else {
				holder = (Holder) convertView.getTag();
			}
			holder.tvCurplayName.setText(song.getName());
			holder.tvCurplayArtist.setText(song.getArtist());
			holder.tvCurplayPath.setText(song.getPath().replace(CommonData.EXTERNALSTORAGEDIRECTORY, ""));
			return convertView;
		}
		
	}
	
	protected interface OnMusicImportListener {
		public void onImportStart();
		public void onImportUpdate(String message);
		public void onImportEnd(int count);
	}
	
}
