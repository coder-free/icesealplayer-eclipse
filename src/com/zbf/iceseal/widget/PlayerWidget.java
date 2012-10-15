package com.zbf.iceseal.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class PlayerWidget extends AppWidgetProvider {
	private static PlayerWidget sInstance;
	public static final String WIDGET_ACTION = "com.zbf.widget.PlayerWidget";
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		System.out.println("onReceive" + intent.getAction());
		if(intent != null && intent.getAction().equals(WIDGET_ACTION)) {
			System.out.println("onReceive = " + intent.getIntExtra("desktop", 0));
			try {
				Intent event = new Intent("com.zbf.service.PlayerService");
				int action = 0;
				switch (intent.getIntExtra("desktop", 0)) {
//				case CommonData.P_DESKTOP_PLAY:
////					PlayerSecretary.forceChangeState(context);
//					action = PlayerSecretary.CHANGESTATE_FORCE;
//					break;
//				case CommonData.P_DESKTOP_NEXT:
////					PlayerSecretary.forceNext(context);
//					action = PlayerSecretary.NEXT_FORCE;
//					break;
//				case CommonData.P_DESKTOP_LAST:
////					PlayerSecretary.forceLast(context);
//					action = PlayerSecretary.LAST_FORCE;
//					break;
//				case CommonData.P_DESKTOP_PLAYLIST:
////					System.out.println("playlist");
//					action = PlayerSecretary.PLAYLIST;
//					break;
//				case CommonData.P_DESKTOP_CHANGEMODE:
////					PlayerConfig.getConfig().changeMode();
//					action = PlayerSecretary.CHANGEMODE;
//					break;
//				case CommonData.P_DESKTOP_TOMAIN:
//					if(MainPlayer.getPlayer() != null) {
//						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//						Intent intentMain = new Intent("com.zbf.activity.MainActivity");
//						PendingIntent pendingIntent = PendingIntent.getActivity(context, 22, intentMain, PendingIntent.FLAG_UPDATE_CURRENT);
//						pendingIntent.send();
//						return;
//					}
//					break;
//				case CommonData.P_NOTIFICATION_PAUSE:
////					PlayerSecretary.pause();
//					action = PlayerSecretary.PAUSE_FORCE;
//					break;
				default:
					break;
				}
				event.putExtra("myaction", action);
				PendingIntent pendingIntent = PendingIntent.getService(context, 23, event, PendingIntent.FLAG_UPDATE_CURRENT);
				pendingIntent.send();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
			System.out.println("PlayerWidget onUpdate");
			Intent service = new Intent("com.zbf.service.PlayerService");
			service.putExtra("desktop", 0);
			service.putExtra("ids", appWidgetIds);
			context.startService(service);
	}
	
	public void updateAppWidget(Context context, int[] appWidgetIds, RemoteViews views) {
		System.out.println("updatewidget");
		if(appWidgetIds == null) {
			updateNew(context);
		} else {
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			appWidgetManager.updateAppWidget(appWidgetIds, views);
		}
	}
	
	private void updateNew(Context context) {
//		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//		System.out.println("update");
//		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_player);
//		
//		Intent intentPlay = new Intent(WIDGET_ACTION);
//		intentPlay.putExtra("desktop", CommonData.P_DESKTOP_PLAY);
//		PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 10, intentPlay, 0);
//		views.setOnClickPendingIntent(R.id.btnPlay, pendingIntentPlay);
//		
//		Intent intentNext = new Intent(WIDGET_ACTION);
//		intentNext.putExtra("desktop", CommonData.P_DESKTOP_NEXT);
//		PendingIntent pendingIntentNext = PendingIntent.getBroadcast(context, 11, intentNext, 0);
//		views.setOnClickPendingIntent(R.id.btnNext, pendingIntentNext);
//		
//		Intent intentLast = new Intent(WIDGET_ACTION);
//		intentLast.putExtra("desktop", CommonData.P_DESKTOP_LAST);
//		PendingIntent pendingIntentLast = PendingIntent.getBroadcast(context, 12, intentLast, 0);
//		views.setOnClickPendingIntent(R.id.btnLast, pendingIntentLast);
//		
//		Intent intentSonglist = new Intent(WIDGET_ACTION);
//		intentSonglist.putExtra("desktop", CommonData.P_DESKTOP_PLAYLIST);
//		PendingIntent pendingIntentSonglist = PendingIntent.getBroadcast(context, 13, intentSonglist, 0);
//		views.setOnClickPendingIntent(R.id.ivSonglist, pendingIntentSonglist);
//		
//		Intent intentCirculMode = new Intent(WIDGET_ACTION);
//		intentCirculMode.putExtra("desktop", CommonData.P_DESKTOP_CHANGEMODE);
//		PendingIntent pendingIntentCirculMode = PendingIntent.getBroadcast(context, 14, intentCirculMode, 0);
//		views.setOnClickPendingIntent(R.id.ivCirculMode, pendingIntentCirculMode);
//		int srcId = R.drawable.circul_list_once;
//		SharedPreferences sp = context.getSharedPreferences(CommonData.SPF_SETTING, Context.MODE_PRIVATE);
//		switch (sp.getInt("circulMode", PlayerConfig.CIRCUL_MODE_LIST_ONCE)) {
//		case PlayerConfig.CIRCUL_MODE_LIST_ONCE:
//			break;
//		case PlayerConfig.CIRCUL_MODE_LIST_REPEAT:
//			srcId = R.drawable.circul_list_repeat;
//			break;
//		case PlayerConfig.CIRCUL_MODE_SINGLE_REPEAT:
//			srcId = R.drawable.circul_single_repeat;
//			break;
//		case PlayerConfig.CIRCUL_MODE_LIST_RANDOM:
//			srcId = R.drawable.circul_random;
//			break;
//		default:
//			break;
//		}
//		views.setImageViewResource(R.id.ivCirculMode, srcId);
//		
//		Intent intentToMain = new Intent(WIDGET_ACTION);
//		intentToMain.putExtra("desktop", CommonData.P_DESKTOP_TOMAIN);
//		PendingIntent pendingIntentToMain = PendingIntent.getBroadcast(context, 15, intentToMain, 0);
//		views.setOnClickPendingIntent(R.id.gAlbum, pendingIntentToMain);
//		appWidgetManager.updateAppWidget(new ComponentName(context.getPackageName(), this.getClass().getName()), views);
	}
	
	public static synchronized PlayerWidget getInstance() {
        if (sInstance == null) {
            sInstance = new PlayerWidget();
        }
        return sInstance;
    }

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		System.out.println("PlayerWidget onDeleted");
	}

	@Override
	public void onEnabled(Context context) {
		System.out.println("PlayerWidget onEnabled");
	}

	@Override
	public void onDisabled(Context context) {
		System.out.println("PlayerWidget onDisabled");
	}
	
}
