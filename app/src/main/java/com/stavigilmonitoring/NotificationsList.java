package com.stavigilmonitoring;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.adapters.NotificationsListAdapter;
import com.beanclasses.Notifications;
import com.database.DBInterface;

import java.util.ArrayList;

public class NotificationsList extends Activity {
    private Context parent;
    ListView listnotifications;
    TextView stationInvent;
    LinearLayout layout_sl, layout_pconoff, layout_advfplay, layout_busann, layout_advnotrun, layout_tvstatus;

    DatabaseHandler db;
    String mobno = "";
    utility ut;
    String installationID = "", notificationType="", Station = "";

    ArrayList<Notifications> notifyList;

    String notifyType="", instID="", notifyText="", notifyData="", dateTime="", msgVal;
    NotificationsListAdapter notifyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_notifications_list);

        init();

        getDataFromdatabase();

    }

    public void init(){
        parent = NotificationsList.this;

        listnotifications = (ListView)findViewById(R.id.listnotifications);
        stationInvent = (TextView) findViewById(R.id.stationInvent);

        layout_sl = (LinearLayout)findViewById(R.id.layout_sl);
        layout_pconoff = (LinearLayout)findViewById(R.id.layout_pconoff);
        layout_advfplay = (LinearLayout)findViewById(R.id.layout_advfplay);
        layout_busann = (LinearLayout)findViewById(R.id.layout_busann);
        layout_advnotrun = (LinearLayout)findViewById(R.id.layout_advnotrun);
        layout_tvstatus = (LinearLayout)findViewById(R.id.layout_tvstatus);

        db = new DatabaseHandler(parent);
        DBInterface dbi = new DBInterface(parent);
        mobno = dbi.GetPhno();
        dbi.Close();

        Intent intent = getIntent();
        installationID = intent.getStringExtra("InstallationID");
        notificationType = intent.getStringExtra("MsgType");
        Station = intent.getStringExtra("Station");

        if(notificationType.equalsIgnoreCase("ADVFPLAY")){
            layout_sl.setVisibility(View.GONE);
            layout_pconoff.setVisibility(View.GONE);
            layout_advfplay.setVisibility(View.VISIBLE);
            layout_busann.setVisibility(View.GONE);
            layout_advnotrun.setVisibility(View.GONE);
            layout_tvstatus.setVisibility(View.GONE);
            stationInvent.setText("Notifications - "+Station+" - Advertisement First Play");
        }else  if(notificationType.equalsIgnoreCase("BUSANN")){
            layout_sl.setVisibility(View.GONE);
            layout_pconoff.setVisibility(View.GONE);
            layout_advfplay.setVisibility(View.GONE);
            layout_busann.setVisibility(View.VISIBLE);
            layout_advnotrun.setVisibility(View.GONE);
            layout_tvstatus.setVisibility(View.GONE);
            stationInvent.setText("Notifications - "+Station+" - Bus Announcement");
        }else  if(notificationType.equalsIgnoreCase("ADVNOTRUN")){
            layout_sl.setVisibility(View.GONE);
            layout_pconoff.setVisibility(View.GONE);
            layout_advfplay.setVisibility(View.GONE);
            layout_busann.setVisibility(View.GONE);
            layout_advnotrun.setVisibility(View.VISIBLE);
            layout_tvstatus.setVisibility(View.GONE);
            stationInvent.setText("Notifications - "+Station+" - Advertisement Not Run");
        }else  if(notificationType.equalsIgnoreCase("TVSTAT")){
            layout_sl.setVisibility(View.GONE);
            layout_pconoff.setVisibility(View.GONE);
            layout_advfplay.setVisibility(View.GONE);
            layout_busann.setVisibility(View.GONE);
            layout_advnotrun.setVisibility(View.GONE);
            layout_tvstatus.setVisibility(View.VISIBLE);
            stationInvent.setText("Notifications - "+Station+" - TV Status");
        }else  if(notificationType.equalsIgnoreCase("PCONOFF")){
            layout_sl.setVisibility(View.GONE);
            layout_pconoff.setVisibility(View.VISIBLE);
            layout_advfplay.setVisibility(View.GONE);
            layout_busann.setVisibility(View.GONE);
            layout_advnotrun.setVisibility(View.GONE);
            layout_tvstatus.setVisibility(View.GONE);
            stationInvent.setText("Notifications - "+Station+" - PC On - Off");
        }else  if(notificationType.equalsIgnoreCase("SL")){
            layout_sl.setVisibility(View.VISIBLE);
            layout_pconoff.setVisibility(View.GONE);
            layout_advfplay.setVisibility(View.GONE);
            layout_busann.setVisibility(View.GONE);
            layout_advnotrun.setVisibility(View.GONE);
            layout_tvstatus.setVisibility(View.GONE);
            stationInvent.setText("Notifications - "+Station+" - Sound Level");
        }

        notifyList = new ArrayList();
    }

    public void getDataFromdatabase(){
       // notifyList.clear();
        SQLiteDatabase sql = db.getWritableDatabase();

        String query = "Select DISTINCT * from TableNotifications WHERE InstallationId='"+installationID+"' AND MsgType='"+notificationType+"' ";// +
               // "ORDER BY AddedDt DESC";
        Cursor c = sql.rawQuery(query,null);
        if(c.getCount() > 0){
            c.moveToLast();
            do{
                notifyType = c.getString(c.getColumnIndex("MsgType"));
                installationID = c.getString(c.getColumnIndex("InstallationId"));
                notifyText = c.getString(c.getColumnIndex("MsgText"));
               // notifyData = c.getString(c.getColumnIndex(""));
                dateTime = c.getString(c.getColumnIndex("AddedDt"));
                msgVal = c.getString(c.getColumnIndex("MsgVal"));

                Notifications notifications = new Notifications();
                notifications.setNotificationtype(notifyType);
                notifications.setInstallationID(installationID);
                notifications.setNotificationText(notifyText + "\n "+msgVal);
                notifications.setNotificationData("");
                notifications.setDateTime(dateTime);
                notifications.setVal_SL_BUSANN_TV(msgVal);

                notifyList.add(notifications);

               /* *//*Sort by Aplhabatically*//*
                Collections.sort(notifyList, new Comparator<Notifications>() {
                    @Override
                    public int compare(Notifications lhs, Notifications rhs) {
                        return rhs.getDateTime().compareTo(lhs.getDateTime());
                    }
                });*/

            }while (c.moveToPrevious());

            notifyAdapter = new NotificationsListAdapter(this, notifyList);
            listnotifications.setAdapter(notifyAdapter);

        }else {

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
