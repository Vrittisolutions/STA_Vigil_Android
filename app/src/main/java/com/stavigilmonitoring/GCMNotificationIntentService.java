package com.stavigilmonitoring;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.stavigilmonitoring.R;
import com.stavigilmonitoring.utility;

public class GCMNotificationIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	static SimpleDateFormat dff;
	static String Ldate;
	com.stavigilmonitoring.utility ut;
	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "test";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		String msg = intent.getStringExtra("message");
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				for (int i = 0; i < 3; i++) {
					Log.i(TAG,
							"Working... " + (i + 1) + "/5 @ "
									+ SystemClock.elapsedRealtime());
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						dff = new SimpleDateFormat("HH:mm:ss");
						Ldate = dff.format(new Date());

						StackTraceElement l = new Exception().getStackTrace()[0];
						System.out.println(l.getClassName() + "/" + l.getMethodName() + ":"
								+ l.getLineNumber());
						ut = new utility();
						if (!ut.checkErrLogFile()) {

							ut.ErrLogFile();
						}
						if (ut.checkErrLogFile()) {
							ut.addErrLog(l.getClassName() + "/" + l.getMethodName() + ":"
									+ l.getLineNumber() + "	" + e.getMessage() + " "
									+ Ldate);
						}
					}
				}
				// String message = (String) extras.get("message");
				// Intent i = new Intent();
				// i.setAction("GCM_NOTIFY");
				// i.putExtra("message", message);
				// sendBroadcast(i);

				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());

				sendNotification("" + extras.get(Config.MESSAGE_KEY));
				Log.i(TAG, "Received: " + extras.toString());
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg) {
		Log.d(TAG, "Preparing to send notification...: " + msg);
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		CharSequence title = "Material Request";
		int icon = R.drawable.sta_logo;
		long time = System.currentTimeMillis();

		Notification notification = new Notification(icon, msg, time);

		Intent notificationIntent = new Intent(getApplicationContext(),
				MaterialReqPendingDetails.class);
		notificationIntent.putExtra("message", msg);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent contentIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, notificationIntent,
				PendingIntent.FLAG_ONE_SHOT);
		NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(GCMNotificationIntentService.this)
				.setSmallIcon(R.drawable.sta_logo) // notification icon
				.setContentTitle(title) // title for notification
				.setContentText(msg) // message for notification
				.setAutoCancel(true); // clear notification after click

//		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
//				this).setSmallIcon(R.drawable.sta_logo)
//				.setContentTitle("Material Request")
//				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
//				.setContentText(msg).setDefaults(Notification.DEFAULT_SOUND)//				.setAutoCancel(true);
		//
		// mBuilder.setContentIntent(contentIntent);
		//
		// int defaults = 0;
		// defaults = defaults | Notification.DEFAULT_LIGHTS;
		// defaults = defaults | Notification.DEFAULT_VIBRATE;
		// defaults = defaults | Notification.DEFAULT_SOUND;
		//
		// mBuilder.setDefaults(defaults);

//		 mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
//		 Log.d(TAG, "Notification sent successfully.");

		// NotificationManager mNotificationManager = (NotificationManager)
		// getSystemService(Context.NOTIFICATION_SERVICE);

		// Intent notificationIntent = new Intent(
		// getApplicationContext(), SelectMenu.class);
		// notificationIntent.putExtra("NotificationMessage", z);
		// notificationIntent
		// .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
		// | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		//
		// PendingIntent contentIntent = PendingIntent
		// .getActivity(getApplicationContext(), 0,
		// notificationIntent,
		// PendingIntent.FLAG_UPDATE_CURRENT);

		/*notification.setLatestEventInfo(getApplicationContext(), title, msg,
				contentIntent);*/

		// Clear the notification when it is pressed
		 notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.PRIORITY_HIGH;
		mNotificationManager.notify(NOTIFICATION_ID, notification);
	}
}
