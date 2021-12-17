package com.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

import com.stavigilmonitoring.R;

public class MyAlarmReceiver extends BroadcastReceiver {
    static MediaPlayer mp;

    @Override
    public void onReceive(Context context, Intent intent) {
        mp= MediaPlayer.create(context, R.raw.alarm);
        mp.setLooping(true);
        mp.start();
        //intent.getData();
        Toast.makeText(context, "Alarm....", Toast.LENGTH_LONG).show();
    }

    public static void stopAlarm(){
        if(mp != null) {
            mp.stop();
        }
    }

}
