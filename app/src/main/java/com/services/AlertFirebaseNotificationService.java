package com.services;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.stavigilmonitoring.AlrtDetailsWithCommentsActivity;
import com.stavigilmonitoring.AlrtListActivity;
import com.stavigilmonitoring.AlrtsStnListAll;
import com.stavigilmonitoring.Common;
import com.stavigilmonitoring.DatabaseHandler;
import com.stavigilmonitoring.NotificationActivity;
import com.stavigilmonitoring.NotificationsList;
import com.stavigilmonitoring.R;
import com.stavigilmonitoring.SelectMenu;
import com.stavigilmonitoring.SoundLevel;
import com.stavigilmonitoring.StationVisitForm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static android.app.Notification.DEFAULT_LIGHTS;
import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;

/**
 * Created by Admin-3 on 6/1/2017.
 */

public class AlertFirebaseNotificationService extends FirebaseMessagingService {
    String message, messfrom;
    Intent intent;
    //DBHelper db1;
    private static final String TAG = "MyFirebaseMsgService";
    NotificationChannel channel;
    String channel_id = "one";
    JSONObject obj;
    String Msgcontent = "", MsgType = "", InstallationId = "", date = "",
            notification_data = "", MsgText = "", MsgVal = "", StationName = "";
    boolean flag;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
// [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String notification = remoteMessage.getData().toString();
        flag = false;
        // String notification1 = remoteMessage.getSentTime();
        String notification2 = remoteMessage.getMessageId().toString();
        notification_data = remoteMessage.getData().get("message");

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (isScreenOn == false) {
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
            wl.acquire(10000);
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");

            wl_cpu.acquire(10000);
        } else {

        }

        if (notification_data.contains("Station (")) {
            // do not display notification
        } else {
            try {
                getnotification(notification_data);    //off for testing purpose
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getDateinFormat(String amcExpireDt) {
        String result = null;
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM, yyyy hh:mm aa");
        try {
            Date date2 = dateFormat1.parse(amcExpireDt);
            result = dateFormat3.format(date2);
            //date2 = dateFormat3.parse(result);
            //result = dateFormat2.format(date2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getUserid(String InstaId) {
        String result = null;
        try {
            DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
            SQLiteDatabase sql = db1.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select InstalationId from " + "ConnectionStatusFilter" + " WHERE InstallationDesc='" + InstaId + "'", null);
            cursor.moveToFirst();
            if (cursor != null && cursor.getCount() > 0) {
                result = cursor.getString(cursor.getColumnIndex("InstalationId"));
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @SuppressLint("NewApi")
    private void GetNotificationData(String notifyTitle, String msgType, String InstallationId, String StationName) {
        Intent intent = new Intent(this, NotificationsList.class);
        intent.putExtra("MsgType", msgType);
        intent.putExtra("InstallationID", InstallationId);
        intent.putExtra("Station", StationName);

        PendingIntent piResult = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            channel = new NotificationChannel(channel_id,
                    "Level",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            // Sets whether notification posted to this channel should vibrate.
            channel.enableVibration(true);
            // Sets the notification light color for notifications posted to this channel
            channel.setLightColor(Color.GREEN);
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setShowBadge(true);

        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this, channel_id);
        NotificationCompat.Builder notification = mBuilder
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.sta_logo))
                .setContentTitle(notifyTitle)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(Msgcontent))       //msg
                .setContentIntent(piResult)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_ALL)
                .setGroup(Msgcontent)          //msg
                .setGroupSummary(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentText(Msgcontent);           //msg

        notification.setSmallIcon(R.drawable.sta_logo);

       /* if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.setSmallIcon(R.mipmap.dummy);
            notification.setColor(getResources().getColor(R.color.blue_color));
        } else {
    //                notificationBuilder.setSmallIcon(R.drawable.noti_imag);
    //                notificationBuilder.setColor(getResources().getColor(R.color.rating_bar_color));
            notification.setSmallIcon(R.drawable.vwb_logo);
        }
    */
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.createNotificationChannel(channel);
        }
        Random random = new Random();
        int code = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(code, notification.build());

    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);

        final String message = intent.getStringExtra("message");

        if (message != null) {
            // if (isAppRunning(message)) ;
            if (isApplicationSentToBackground(getApplicationContext())) {
                try {
                    getnotification(message);       //off for testing purpose
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isApplicationSentToBackground(Context mcontext) {
        ActivityManager am = (ActivityManager) mcontext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {

           /* try{
                ComponentName topActivity = tasks.get(0).topActivity;
                if (!topActivity.getPackageName().equals(mcontext.getPackageName())) {
                    return true;
                }
            }catch (Exception e){
                e.printStackTrace();
            }*/

            try {
                ComponentName topActivity = null;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    topActivity = tasks.get(0).topActivity;
                }

                if (!topActivity.getPackageName().equals(mcontext.getPackageName())) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void getnotification(String notification) {

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        SQLiteDatabase sql = db.getWritableDatabase();

        String notifyType_Title = "";

//        if (!flag) {

        try {
            obj = new JSONObject(notification);
            MsgType = obj.getString("MsgType");
            Msgcontent = obj.getString("MsgVal");
            InstallationId = obj.getString("InstallationId");
            date = obj.getString("date");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String query = "Select StatioName from AllStation Where InstallationId ='" + InstallationId + "'";
        Cursor c = sql.rawQuery(query, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            StationName = c.getString(c.getColumnIndex("StatioName"));
        }

        if (MsgType.equalsIgnoreCase("SL")) {
            notifyType_Title = "Sound Level";

            try {
                Msgcontent = obj.getString("MsgText");
                InstallationId = obj.getString("InstallationId");
                date = obj.getString("date");
                MsgText = obj.getString("MsgText");
                MsgVal = obj.getString("MsgVal");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (MsgText.contains("Station (")) {
                // do not display notification
            } else {
                GetNotificationData(notifyType_Title, MsgType, InstallationId, StationName);
            }

            // GetNotificationData(notifyType_Title);
            if (!flag) {
                // GetNotificationData(notifyType_Title);
                flag = true;
            }

            //add notifyID =  Type + 1st 4digit of instId + date(ddmmhhss)
            db.addNotification("", InstallationId, StationName, date, Msgcontent, MsgType, MsgVal, MsgText);

        } else if (MsgType.equalsIgnoreCase("PCONOFF")) {
            //pc on off
            notifyType_Title = "PC On/Off";

            try {
                Msgcontent = obj.getString("MsgText");
                InstallationId = obj.getString("InstallationId");
                date = obj.getString("date");
                MsgText = obj.getString("MsgText");
                MsgVal = obj.getString("MsgVal");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            GetNotificationData(notifyType_Title, MsgType, InstallationId, StationName);
            if (!flag) {
                // GetNotificationData(notifyType_Title);
                flag = true;
            }

            db.addNotification("", InstallationId, StationName, date, Msgcontent, MsgType, MsgVal, MsgText);
        } else if (MsgType.equalsIgnoreCase("ADVFPLAY")) {
            //pc on off
            notifyType_Title = "Advertisement First Play Time";

            try {
                Msgcontent = obj.getString("MsgText");
                InstallationId = obj.getString("InstallationId");
                date = obj.getString("date");
                MsgText = obj.getString("MsgText");
                MsgVal = obj.getString("MsgVal");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            GetNotificationData(notifyType_Title, MsgType, InstallationId, StationName);
            if (!flag) {
                // GetNotificationData(notifyType_Title);
                flag = true;
            }

            db.addNotification("", InstallationId, StationName, date, Msgcontent, MsgType, MsgVal, MsgText);
        } else if (MsgType.equalsIgnoreCase("BUSANN")) {
            //pc on off
            notifyType_Title = "Daily Bus Announcement";

            try {
                Msgcontent = obj.getString("MsgText");
                InstallationId = obj.getString("InstallationId");
                date = obj.getString("date");
                MsgText = obj.getString("MsgText");
                MsgVal = obj.getString("MsgVal");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            GetNotificationData(notifyType_Title, MsgType, InstallationId, StationName);
            if (!flag) {
                // GetNotificationData(notifyType_Title);
                flag = true;
            }

            db.addNotification("", InstallationId, StationName, date, Msgcontent, MsgType, MsgVal, MsgText);
        } else if (MsgType.equalsIgnoreCase("ADVNOTRUN")) {
            //pc on off
            notifyType_Title = "Advertisement Not Running";

            try {
                Msgcontent = obj.getString("MsgText");
                InstallationId = obj.getString("InstallationId");
                date = obj.getString("date");
                MsgText = obj.getString("MsgText");
                MsgVal = obj.getString("MsgVal");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            GetNotificationData(notifyType_Title, MsgType, InstallationId, StationName);
            if (!flag) {
                // GetNotificationData(notifyType_Title);
                flag = true;
            }

            db.addNotification("", InstallationId, StationName, date, Msgcontent, MsgType, MsgVal, MsgText);
        } else if (MsgType.equalsIgnoreCase("TVSTAT")) {
            //pc on off
            notifyType_Title = "TV Status";

            try {
                Msgcontent = obj.getString("MsgText");
                InstallationId = obj.getString("InstallationId");
                date = obj.getString("date");
                MsgText = obj.getString("MsgText");
                MsgVal = obj.getString("MsgVal");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            GetNotificationData(notifyType_Title, MsgType, InstallationId, StationName);
            if (!flag) {
                // GetNotificationData(notifyType_Title);
                flag = true;
            }

            db.addNotification("", InstallationId, StationName, date, Msgcontent, MsgType, MsgVal, MsgText);
        } else {
            if (MsgType.equals("CSN")) {

            } else if (MsgType.equals("NonReportedADV")) {

            } else if (MsgType.equals("")) {

            }
        }

          /*  } else {
                Log.e("Notification", "Already sent notification");
            }*/
    }

}