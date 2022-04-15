package com.new_development;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.services.MyAlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DownloadCancelReceiver extends android.content.BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("Received Cancelled Event");

        MyAlarmReceiver.stopAlarm();
        //btnsetalarm.setVisibility(View.VISIBLE);
        //btncancelalarm.setVisibility(View.GONE);

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm.ss aa");
        String output = dateFormat.format(currentTime);
        //Toast.makeText(getApplicationContext(),"Time Is :" + output, Toast.LENGTH_LONG).show();

        SharedPreferences pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong("AlarmStopTime", currentTime.getTime());
        //editor.putBoolean("SetAlarm", false);
        editor.apply();

    }
}