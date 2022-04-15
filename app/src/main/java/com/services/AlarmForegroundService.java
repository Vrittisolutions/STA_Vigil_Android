package com.services;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.stavigilmonitoring.R;
import com.stavigilmonitoring.SelectMenu;
import com.stavigilmonitoring.UnreleasedAdvertisements;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class AlarmForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    String module="";
    String Title="",Notification_Title="",Opportunity="";
    private PendingIntent pendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotificationChannel();
        String Desc=intent.getStringExtra("inputExtra");

        //createDownloadNotification(Desc);
       // MyAlarmReceiver.stopAlarm();

        Intent notificationIntent = new Intent(this, SelectMenu.class);
        notificationIntent.putExtra("menu","Menu");
        notificationIntent.putExtra("STOPALARM","STOP");
        notificationIntent.putExtra("NOTIFICATIONID",startId);
        notificationIntent.setAction("showmessage");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(this,
                123123, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }


        /*Intent intentStopAlarm = new Intent(this, UnreleasedAdvertisements.class);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(getApplicationContext(), 1253, intentStopAlarm, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent1);
*/

        Notification notification = new NotificationCompat.Builder(
                this,
                CHANNEL_ID)
                .setContentTitle(Desc)
                .setContentText(Desc)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(Desc))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.sta_logo))
                .setSmallIcon(R.drawable.sta_logo)
                .setContentIntent(pendingIntent)
                .setSubText("Click to stop alarm")
                .addAction(R.drawable.action_clear,"Stop Alarm",pendingIntent )
                .build();


        startForeground(1337, notification);

       /* NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);*/

        //mNotificationManager.cancelAll();



/*
        AlarmManager am=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(Display.this, TwoAlarmService.class);
        PendingIntent pi = PendingIntent.getBroadcast(Display.this, AlarmNumber, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(pi);
*/


//        builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
//        notification.notify(0, mBuilder.build());

        //startForeground(1, notification);

        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //sendBroadcast(new Intent("InfiniteService"));

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "alarm_notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }

    }


    private void createDownloadNotification(String Desc) {
        Intent closeButton = new Intent(Desc);
        closeButton.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0, closeButton, 0);

        @SuppressLint("RemoteViewLayout")
        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.widget_update_notification);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher).setTicker("Ticker Text").setContent(notificationView);
        //notificationView.setProgressBar(R.id.pb_progress, 100, 12, false);
        notificationView.setOnClickPendingIntent(R.id.btn_close, pendingSwitchIntent);

        notificationManager.notify(1, builder.build());

        notificationManager.cancel(1);


    }



}