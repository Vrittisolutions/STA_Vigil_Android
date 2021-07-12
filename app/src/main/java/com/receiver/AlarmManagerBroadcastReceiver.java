package com.receiver;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.stavigilmonitoring.DatabaseHandler;
 
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
	 
	public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
	 
	 final public static String ONE_TIME = "onetime";
	 private int itime;

	 @Override
 public void onReceive(Context context, Intent intent) {
		 DatabaseHandler db=new DatabaseHandler(context);	
		 	String val=db.getSetting();
			 itime=Integer.parseInt(val);
			 
		 if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			//long aTime = 1000 * 60 * itime;
			 long aTime = 1000 * 60 * 30;
			
			Intent igpsalarm = new Intent(context,	com.services.SynchDtataCount.class);

			/* Intent igpsalarm = new Intent(context, com.services.SynchCSNnNonRepoCount.class);*/

			PendingIntent piHeartBeatService = PendingIntent.getService(context, 0,
										igpsalarm, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarmManager = (AlarmManager) context
										.getSystemService(Context.ALARM_SERVICE);
					alarmManager.cancel(piHeartBeatService);
					alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
							System.currentTimeMillis(), aTime, piHeartBeatService);
		 }
		 else
		 {
			 //long aTime = 1000 * 60 * itime;
			 long aTime = 1000 * 60 * 30;
				
				Intent igpsalarm = new Intent(context,com.services.SynchDtataCount.class);/*
			 Intent igpsalarm = new Intent(context,
					 com.services.SynchCSNnNonRepoCount.class);*/
				PendingIntent piHeartBeatService = PendingIntent.getService(context, 0,
											igpsalarm, PendingIntent.FLAG_UPDATE_CURRENT);
				AlarmManager alarmManager = (AlarmManager) context
											.getSystemService(Context.ALARM_SERVICE);
						alarmManager.cancel(piHeartBeatService);
						alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
								System.currentTimeMillis(), aTime, piHeartBeatService);
		 }
	//}		
	 }
	 
	 public void SetAlarm(Context context)
	    {
	        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
	        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
	        //After after 5 seconds
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5 , pi);
	    }
	 
	    public void CancelAlarm(Context context)
	    {
	        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
	        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
	        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(sender);
    }
	 
	    public void setOnetimeTimer(Context context){
	     AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
	        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
	        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
	    }
	}
