package com.zbf.iceseal.broadcast;

import com.zbf.iceseal.service.PlayerService;
import com.zbf.iceseal.util.UtilTools;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class EventReceiver extends BroadcastReceiver {
	
	private Context mContext;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		if(!UtilTools.isServiceRunning(mContext, "com.zbf.iceseal.service.PlayerService")) {
			return;
		}
		if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			Intent service = new Intent(PlayerService.PLAYER_SERVICE_ACTION);
			service.putExtra("autoPause", 0);
			mContext.startService(service);
		} else {
			PhoneStateListener listener = new PhoneStateListener(){
				@Override
				public void onCallStateChanged(int state, String incomingNumber) {
					super.onCallStateChanged(state, incomingNumber);
					switch (state) {
					case TelephonyManager.CALL_STATE_IDLE:
						Intent service1 = new Intent(PlayerService.PLAYER_SERVICE_ACTION);
						service1.putExtra("autoRecover", 0);
						mContext.startService(service1);
						break;

					case TelephonyManager.CALL_STATE_OFFHOOK:
						break;

					case TelephonyManager.CALL_STATE_RINGING:
						Intent service2 = new Intent(PlayerService.PLAYER_SERVICE_ACTION);
						service2.putExtra("autoPause", 0);
						mContext.startService(service2);
						break;

					default:
						break;
					}
				}
			};
			TelephonyManager manager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
			manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		}
	}
}
