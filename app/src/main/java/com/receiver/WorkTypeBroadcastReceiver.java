package com.receiver;

import com.services.WorkDoneService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class WorkTypeBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		System.out.println("StartMyServiceAtBootReceiver called.");

		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			try {
				Thread.sleep(50000);
				Intent activityIntent = new Intent(context, WorkDoneService.class);
				activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(activityIntent);
				/*if (Build.VERSION.SDK_INT >= 26*//*Build.VERSION_CODES.O*//*) {
					context.startForegroundService(activityIntent);
				} else {
					context.startService(activityIntent);
				}*/

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			try {
				Thread.sleep(50000);
				Intent activityIntent = new Intent(context, WorkDoneService.class);
				activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(activityIntent);
				/*if (Build.VERSION.SDK_INT >= 26*//*Build.VERSION_CODES.O*//*) {
					context.startForegroundService(activityIntent);
				} else {
					context.startService(activityIntent);
				}*/
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
