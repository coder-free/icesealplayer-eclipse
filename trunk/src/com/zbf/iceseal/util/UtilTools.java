package com.zbf.iceseal.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.text.format.DateFormat;

public class UtilTools {

	public static String getShowTime(long milliseconds) {
		if(milliseconds < 3600000) {
			return DateFormat.format("mm:ss", milliseconds).toString();
		}
		String time = DateFormat.format("HH:mm:ss", milliseconds).toString();
		int hour = Integer.parseInt(time.substring(0, 2));
		return (hour*60 + Integer.parseInt(time.substring(3, 5))) + time.substring(6);
	}
	public static boolean isLastActivity(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		return am.getRunningTasks(2).get(0).numActivities <= 1;
	}
	public static boolean hasActivity(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		return am.getRunningTasks(2).get(0).numActivities < 1;
	}
	public static String[] getCurShowtime(int duration, int progress) {
		if(duration < progress) {
			return new String[]{"", ""};
		}
		String[] shouTimes = new String[2];
		shouTimes[0] = UtilTools.getShowTime(progress);
		shouTimes[1] = "-" + UtilTools.getShowTime(duration - progress);
		return shouTimes;
	}
	public static <T> boolean isServiceRunning(Context context, Class<T> cls) {
		ActivityManager am = (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
		List<RunningServiceInfo> services = am.getRunningServices(50);
		for(RunningServiceInfo service : services) {
			if(service.service.getClassName().equals(cls.getClass().getName())) {
				return true;
			}
		}
		return false;
	}
	public static boolean isServiceRunning(Context context, String name) {
		ActivityManager am = (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
		List<RunningServiceInfo> services = am.getRunningServices(50);
		for(RunningServiceInfo service : services) {
			if(service.service.getClassName().equals(name)) {
				return true;
			}
		}
		return false;
	}
}
