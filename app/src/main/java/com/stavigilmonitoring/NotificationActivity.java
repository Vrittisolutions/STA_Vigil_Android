package com.stavigilmonitoring;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import com.adapters.NotificationAdapter;
import com.beanclasses.NotificationBean;

import java.util.ArrayList;

/**
 * Created by Admin-3 on 7/28/2017.
 */

public class NotificationActivity extends Activity {
    private Context parent;
    NotificationBean notificationBean;
    ArrayList<NotificationBean> notificationBeanList;
    ListView listView_notification;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.stavigilmonitoring.R.layout.activity_notification);/*
        getSupportActionBar().setTitle("Notification Center");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
        parent = NotificationActivity.this;
        //databaseHelper = new DatabaseHelper(parent);
        String idOffer = "";

        Intent startingIntent = getIntent();
        if (startingIntent != null) {
            idOffer = startingIntent.getStringExtra("id_offer"); // Retrieve the id
            //Common.listMessages = null;
        }

        db = new DatabaseHandler(getApplicationContext());
        listView_notification = (ListView) findViewById(com.stavigilmonitoring.R.id.listView_notification);

        notificationBeanList = new ArrayList<NotificationBean>();
        //getOfferDetails(id_offer);

        getDataFromDB();

    }

    private void getDataFromDB() {
        notificationBeanList.clear();
       // DatabaseHandler db = new DatabaseHandler(NotificationActivity.this);
        SQLiteDatabase sql = db.getWritableDatabase();

        try {
            String sqlstr = "DELETE FROM TableNotifications WHERE AddedDt <= date('now','-15 day')";
            sql.execSQL(sqlstr);
            Cursor c = sql.rawQuery("Select * from TableNotifications order by AddedDt desc", null);
            if (c.getCount()== 0){
                c.close();
            }else{
                c.moveToFirst();
                int column = 0;
                do{

                notificationBean = new NotificationBean();
                notificationBean.setNotificationNumber( c.getString( c.getColumnIndex("notificationNumber") ));
                notificationBean.setInstallationId(c.getString( c.getColumnIndex("InstallationId")));
                notificationBean.setAddedDt(c.getString( c.getColumnIndex("AddedDt")));
                notificationBean.setMessage(/*getMsg(*/c.getString( c.getColumnIndex("Message")));
                notificationBean.setStationName(c.getString( c.getColumnIndex("StationName")));
                notificationBeanList.add(notificationBean);
                }while(c.moveToNext());
                c.close();
            }
            listView_notification.setAdapter(new NotificationAdapter(parent, notificationBeanList));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private String getMsg(String message) {
        String parts[] = message.split(":");
        return parts[1];
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromDB();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Intent i = new Intent(parent, SelectMenu.class);
        //startActivity(i);
        finish();
    }
}

