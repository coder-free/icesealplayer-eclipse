package com.zbf.iceseal.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.widget.RemoteViews;

import com.zbf.iceseal.R;
import com.zbf.iceseal.activity.MainplayActivity;
import com.zbf.iceseal.base.BaseDao;
import com.zbf.iceseal.bean.SongBean;
import com.zbf.iceseal.util.CommonData;
import com.zbf.iceseal.util.ImageTools;
import com.zbf.iceseal.util.UtilTools;

public class PlayerService extends Service implements OnPreparedListener, OnCompletionListener, OnErrorListener {
	
	/** intent Action */
	public static final String PLAYER_SERVICE_ACTION = "com.zbf.iceseal.PLAYER_SERVICE";
	public static final String PLAYER_EVENT_ACTION = "com.zbf.iceseal.PLAYER_EVENT";
	
	public static final int PLAYER_EVENT_PROGRESS_UPDATE = 0xf0f1;
	
	public List<SongBean> songlist;
	private PlayerState state;
	private MediaPlayer mPlayer = new MediaPlayer();
	private boolean isReady;
	private RemoteViews notificationViews;
	private Notification notification;
	private boolean isAutoPlay;
	private Handler progressUpdateHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PLAYER_EVENT_PROGRESS_UPDATE:
				if(mPlayer == null || !mPlayer.isPlaying()) {
					progressUpdateHandler.removeMessages(PLAYER_EVENT_PROGRESS_UPDATE);
					break;
				}
				sendProgressUpdateBroadcast(mPlayer.getCurrentPosition());
				progressUpdateHandler.removeMessages(PLAYER_EVENT_PROGRESS_UPDATE);
				progressUpdateHandler.sendEmptyMessageDelayed(PLAYER_EVENT_PROGRESS_UPDATE, 500);
				break;

			default:
				break;
			}
		}
		
	};
	
	private IPlayerService.Stub playerService = new IPlayerService.Stub() {
		
		@Override
		public void seekTo(int msec) throws RemoteException {
			mPlayer.seekTo(msec);
		}
		
		@Override
		public void next() throws RemoteException {
			if(state == null) {
				state = getPlayerState();
			}
			state.position++;
			if(state.loopMode == CommonData.LOOP_MODE_LIST_RANDOM) {
				state.position = getRandomPosition(RANDOM_OPT_NEXT);
			} else if(state.loopMode == CommonData.LOOP_MODE_LIST_ONCE){
				if(state.position == songlist.size()) {
					state.position = -1;
					mPlayer.reset();
					sendSongChangeBroadcast(-1);
					return;
				}
			} else if(state.loopMode == CommonData.LOOP_MODE_LIST_REPEAT){
				if(state.position == songlist.size()) {
					state.position = 0;
				}
			}
			prepareMyPlayer();
		}
		
		@Override
		public void last() throws RemoteException {
			if(state == null) {
				state = getPlayerState();
			}
			state.position--;
			if(state.loopMode == CommonData.LOOP_MODE_LIST_RANDOM) {
				state.position = getRandomPosition(RANDOM_OPT_LAST);
			}
			prepareMyPlayer();
		}
		
		@Override
		public void changeState() throws RemoteException {
			try {

				if(mPlayer.isPlaying()) {
					try {
						mPlayer.pause();
					} catch (IllegalStateException e) {
						mPlayer.reset();
					}
					stopForeground(true);
					sendPlayerPauseBroadcast();
					if(UtilTools.hasActivity(PlayerService.this)) {
						stopPlayerService();
					}
					progressUpdateHandler.removeMessages(PLAYER_EVENT_PROGRESS_UPDATE);
				} else {
					try {
						if(isReady) {
							mPlayer.start();
							showNotification(songlist.get(state.position));
							sendSongChangeBroadcast(state.position);
							progressUpdateHandler.sendEmptyMessage(PLAYER_EVENT_PROGRESS_UPDATE);
						} else {
							prepareMyPlayer();
						}
					} catch (IllegalStateException e) {
						e.printStackTrace();
						prepareMyPlayer();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				prepareMyPlayer();
			}
		}
		
		@Override
		public void changeMode() throws RemoteException {
			if(state == null) {
				state = getPlayerState();
			}
			state.loopMode ++;
			if(state.loopMode > CommonData.LOOP_MODE[CommonData.LOOP_MODE.length - 1]) {
				state.loopMode = CommonData.LOOP_MODE[0];
			}
			sendLoopmodeChangeBroadcast(state.loopMode);
		}

		@Override
		public void setVolume(int leftVolume, int rightVolume)
				throws RemoteException {
			mPlayer.setVolume(leftVolume, rightVolume);
		}

		@Override
		public void playThis(int position, int listType, String listParamete)
				throws RemoteException {
			if(state == null) {
				state = getPlayerState();
			}
			if(listParamete != null) {
				state.listType = listType;
				state.listParamete = listParamete;
				BaseDao dao = new BaseDao(PlayerService.this);
				try {
					songlist = dao.getSongList(state.listType, state.listParamete);
					sendSonglistChangeBroadcast(state.listType, state.listParamete);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(songlist == null) {
					stopSelf();
				}
			}
			state.position = position;
			addRandomPosition(position);
			sendPlayerServiceReadyBroadcast();
			prepareMyPlayer();
		}

		@Override
		public void broadcastSongChange() throws RemoteException {
			if(state == null) {
				state = getPlayerState();
			}
			sendSonglistChangeBroadcast(state.listType, state.listParamete);
			sendSongChangeBroadcast(state.position);
			sendLoopmodeChangeBroadcast(state.loopMode);
		}

		@Override
		public void stopSelf() throws RemoteException {
			try {
				if(!mPlayer.isPlaying()) {
					PlayerService.this.stopPlayerService();
				}
			} catch (Exception e) {
				PlayerService.this.stopPlayerService();
			}
		}
	};
	
	private void prepareMyPlayer() {
		if(state == null) {
			state = getPlayerState();
		}
		if(state.position == -1) {
			state.position = 0;
		}
		if(state.listType == - 101 && ("2".equals(state.listParamete) || "3".equals(state.listParamete))) {
			try {
				songlist = new BaseDao(this).getSongList(state.listType, state.listParamete);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		progressUpdateHandler.removeMessages(PLAYER_EVENT_PROGRESS_UPDATE);
		mPlayer.reset();
		isReady = true;
		mPlayer.setOnPreparedListener(this);
		mPlayer.setOnCompletionListener(this);
		mPlayer.setOnErrorListener(this);
		try {
			mPlayer.setDataSource(songlist.get(state.position).getPath());
			mPlayer.prepareAsync();
			sendSongChangeBroadcast(state.position);
			BaseDao dao = new BaseDao(this);
			dao.addPlayCountAndSetLastplayTime(songlist.get(state.position).getId());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		showNotification(songlist.get(state.position));
		mp.start();
		progressUpdateHandler.sendEmptyMessage(PLAYER_EVENT_PROGRESS_UPDATE);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		isReady = false;
		if(state == null) {
			state = getPlayerState();
		}
		if(state.loopMode != CommonData.LOOP_MODE_SINGLE_REPEAT) {
			state.position++;
		}
		if(state.loopMode == CommonData.LOOP_MODE_LIST_RANDOM) {
			state.position = getRandomPosition(RANDOM_OPT_NEXT);
		} else if(state.loopMode == CommonData.LOOP_MODE_LIST_ONCE){
			if(state.position == songlist.size()) {
				state.position = -1;
				mp.reset();
				sendSongChangeBroadcast(-1);
				if(UtilTools.hasActivity(this)) {
					stopPlayerService();
				}
				return;
			}
		} else if(state.loopMode == CommonData.LOOP_MODE_LIST_REPEAT){
			if(state.position == songlist.size()) {
				state.position = 0;
			}
		}
		prepareMyPlayer();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		isReady = false;
		mp.reset();
		progressUpdateHandler.removeMessages(PLAYER_EVENT_PROGRESS_UPDATE);
		return false;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return playerService;
	}

	@Override
	public void onCreate() {
		state =  getPlayerState();
		BaseDao dao = new BaseDao(this);
		try {
			songlist = dao.getSongList(state.listType, state.listParamete);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(state == null) {
			state = getPlayerState();
		}
		if(songlist == null) {
			stopPlayerService();
			return Service.START_NOT_STICKY;
		}
		if(intent.hasExtra("notificationExit")) {
			try {
				playerService.changeState();
				return Service.START_NOT_STICKY;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else if(intent.hasExtra("autoPause")) {
			try {
				if(mPlayer != null && mPlayer.isPlaying()) {
					playerService.changeState();
					System.out.println("自动暂停");
					isAutoPlay = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return Service.START_NOT_STICKY;
		} else if(intent.hasExtra("autoRecover")) {
			try {
				if(mPlayer != null && !mPlayer.isPlaying() && isAutoPlay) {
					playerService.changeState();
					System.out.println("自动播放");
					isAutoPlay = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return Service.START_NOT_STICKY;
		}
		sendPlayerServiceReadyBroadcast();
		sendSonglistChangeBroadcast(state.listType, state.listParamete);
		return Service.START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		savePlayerState();
		mPlayer.release();
		super.onDestroy();
	}
	
	class PlayerState {
		int listType;
		String listParamete;
		int position;
		long time;
		int loopMode;
	}
	
	private PlayerState getPlayerState() {
		SharedPreferences sp = this.getSharedPreferences(CommonData.SPN_PLAYER_STATE, Context.MODE_PRIVATE);
		PlayerState ps = new PlayerState();
		ps.listType = sp.getInt(CommonData.SPK_PLAYER_STATE_LISTTYPE, 0);
		ps.listParamete = sp.getString(CommonData.SPK_PLAYER_STATE_LISTPARAMETE, "0");
		ps.position = sp.getInt(CommonData.SPK_PLAYER_STATE_POSITION, 0);
		ps.time = sp.getLong(CommonData.SPK_PLAYER_STATE_TIME, 0);
		ps.loopMode = sp.getInt(CommonData.SPK_PLAYER_STATE_LOOPMODE, CommonData.LOOP_MODE_LIST_ONCE);
		return ps;
	}
	
	private void savePlayerState() {
		if(state == null) {
			return;
		}
		SharedPreferences sp = this.getSharedPreferences(CommonData.SPN_PLAYER_STATE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(CommonData.SPK_PLAYER_STATE_LISTTYPE, state.listType);
		editor.putString(CommonData.SPK_PLAYER_STATE_LISTPARAMETE, state.listParamete);
		editor.putInt(CommonData.SPK_PLAYER_STATE_POSITION, state.position);
		editor.putLong(CommonData.SPK_PLAYER_STATE_TIME, state.time);
		editor.putInt(CommonData.SPK_PLAYER_STATE_LOOPMODE, state.loopMode);
		editor.commit();
	}

	private List<Integer> mRandomOrder;
	private static final int RANDOM_OPT_LAST = 1;
	private static final int RANDOM_OPT_NEXT = 2;
	private int randomOffset = 0;
	private int getRandomPosition(int opt) {
		if(mRandomOrder == null) {
			mRandomOrder = new ArrayList<Integer>();
		}
		Random random = new Random();
		int position = 0;
		if(opt == RANDOM_OPT_NEXT) {
			position = random.nextInt(songlist.size());
			mRandomOrder.add(position);
			randomOffset = -2;
			if(mRandomOrder.size() > songlist.size()) {
				mRandomOrder.remove(0);
				randomOffset = -1;
			}
		} else if(opt == RANDOM_OPT_LAST) {
			position = mRandomOrder.get(mRandomOrder.size() + randomOffset);
			mRandomOrder.remove(mRandomOrder.size() - 1);
			if(mRandomOrder.size() < songlist.size()) {
				mRandomOrder.add(0, random.nextInt(songlist.size()));
			}
		}
		return position;
	}
	
	private void addRandomPosition(int position) {
		if(mRandomOrder == null) {
			mRandomOrder = new ArrayList<Integer>();
		}
		mRandomOrder.add(position);
		randomOffset = -2;
		if(mRandomOrder.size() > songlist.size()) {
			mRandomOrder.remove(0);
			randomOffset = -1;
		}
	}
	
	private void stopPlayerService() {
		progressUpdateHandler.removeMessages(PLAYER_EVENT_PROGRESS_UPDATE);
		mPlayer.release();
		stopSelf();
	}
	
	private void sendProgressUpdateBroadcast(int progress) {
		Intent intent = new Intent(PLAYER_EVENT_ACTION);
		intent.putExtra(CommonData.IK_PLAYER_EVENT_TYPE, CommonData.PLAYER_EVENT_PROGRESS);
		intent.putExtra(CommonData.IK_PLAYER_EVENT_PROGRESS, progress);
		this.sendBroadcast(intent);
	}
	
	private void sendSongChangeBroadcast(int position) {
		Intent intent = new Intent(PLAYER_EVENT_ACTION);
		intent.putExtra(CommonData.IK_PLAYER_EVENT_TYPE, CommonData.PLAYER_EVENT_SONG_CHANGE);
		intent.putExtra(CommonData.IK_PLAYER_EVENT_SONG_CHANGE_POSITION, position);
		this.sendBroadcast(intent);
		SharedPreferences sp = this.getSharedPreferences(CommonData.SPN_PLAYER_STATE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(CommonData.SPK_PLAYER_STATE_POSITION, state.position);
		editor.commit();
	}
	
	private void sendSonglistChangeBroadcast(int type, String paramete) {
		Intent intent = new Intent(PLAYER_EVENT_ACTION);
		intent.putExtra(CommonData.IK_PLAYER_EVENT_TYPE, CommonData.PLAYER_EVENT_SONGLIST_CHANGE);
		intent.putExtra(CommonData.IK_PLAYER_EVENT_SONGLIST_CHANGE_TYPE, type);
		intent.putExtra(CommonData.IK_PLAYER_EVENT_SONGLIST_CHANGE_PAREMETE, paramete);
		this.sendBroadcast(intent);
		SharedPreferences sp = this.getSharedPreferences(CommonData.SPN_PLAYER_STATE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(CommonData.SPK_PLAYER_STATE_LISTTYPE, state.listType);
		editor.putString(CommonData.SPK_PLAYER_STATE_LISTPARAMETE, state.listParamete);
		editor.commit();
	}
	
	private void sendPlayerServiceReadyBroadcast() {
		Intent intent = new Intent(PLAYER_EVENT_ACTION);
		intent.putExtra(CommonData.IK_PLAYER_EVENT_TYPE, CommonData.PLAYER_EVENT_READY);
		this.sendBroadcast(intent);
	}
	
	private void sendPlayerPauseBroadcast() {
		Intent intent = new Intent(PLAYER_EVENT_ACTION);
		intent.putExtra(CommonData.IK_PLAYER_EVENT_TYPE, CommonData.PLAYER_EVENT_PAUSE);
		this.sendBroadcast(intent);
	}

	private void sendLoopmodeChangeBroadcast(int mode) {
		Intent intent = new Intent(PLAYER_EVENT_ACTION);
		intent.putExtra(CommonData.IK_PLAYER_EVENT_TYPE, CommonData.PLAYER_EVENT_LOOPMODE_CHANGE);
		intent.putExtra(CommonData.IK_PLAYER_EVENT_LOOPMODE_CHANGE, mode);
		this.sendBroadcast(intent);
		SharedPreferences sp = this.getSharedPreferences(CommonData.SPN_PLAYER_STATE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(CommonData.SPK_PLAYER_STATE_LOOPMODE, state.loopMode);
		editor.commit();
	}
	
	private void showNotification(SongBean song) {
		notification = new Notification(R.drawable.notification_icon, "IceSealPlayer - " + song.getArtist() + "-" + song.getName(), System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		Intent i = new Intent(this, MainplayActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.contentIntent = contentIntent;
		if(notificationViews == null) {
			notificationViews = new RemoteViews(this.getPackageName(), R.layout.notification);
		}
		Bitmap albumBitmap= ImageTools.getAlbumImage(song.getPath(), this.getResources(), R.drawable.defaultalbumimage);
		if(albumBitmap == null) {
			notificationViews.setImageViewResource(R.id.ivCurplay, R.drawable.defaultalbumimage);
		} else {
			notificationViews.setImageViewBitmap(R.id.ivCurplay, albumBitmap);
		}
		Intent btnI = new Intent(PLAYER_SERVICE_ACTION);
		btnI.putExtra("notificationExit", 0);
		PendingIntent btnIntent = PendingIntent.getService(this, 1, btnI, 0);
		notificationViews.setOnClickPendingIntent(R.id.ivExit, btnIntent);
		notificationViews.setTextViewText(R.id.tvCurplayName_Artist, song.getArtist() + " - " + song.getName());
		notification.contentView = notificationViews;
		startForeground(10, notification);
	}

}
