package com.services;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.stavigilmonitoring.R;


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
      //  MyAlarmReceiver.stopAlarm();


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(Desc)
                .setContentText(Desc)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(Desc))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.sta_logo))
                .setSmallIcon(R.drawable.sta_logo)
                .setContentIntent(pendingIntent)
                .setSubText("Click to stop alarm")
                .build();
        startForeground(1, notification);
        //do heavy work on a background thread
        //stopSelf();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
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
                    "alram_notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}