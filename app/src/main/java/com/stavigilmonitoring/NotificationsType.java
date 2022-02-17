package com.stavigilmonitoring;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NotificationsType extends Activity {
    private Context parent;
    LinearLayout lay_advfirstplay, lay_advnotrun, lay_busann, lay_pconoff, lay_soundlevel, lay_tvstatus;
    String installationID = "", Station = "";
    DatabaseHandler db;
    SQLiteDatabase sql;
    TextView txtfpltimecnt, txtnotruncnt, txtbusanncnt, txtpccnt, txtslcnt, txttvcnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_notifications_type);

        init();

        showNotificationCnt();

        setListeners();
    }

    public void init(){
        parent = NotificationsType.this;

        lay_advfirstplay = (LinearLayout)findViewById(R.id.lay_advfirstplay);
        lay_advnotrun = (LinearLayout)findViewById(R.id.lay_advnotrun);
        lay_busann = (LinearLayout)findViewById(R.id.lay_busann);
        lay_pconoff = (LinearLayout)findViewById(R.id.lay_pconoff);
        lay_soundlevel = (LinearLayout)findViewById(R.id.lay_soundlevel);
        lay_tvstatus = (LinearLayout)findViewById(R.id.lay_tvstatus);

        txtfpltimecnt = (TextView)findViewById(R.id.txtfpltimecnt);
        txtnotruncnt = (TextView)findViewById(R.id.txtnotruncnt);
        txtbusanncnt = (TextView)findViewById(R.id.txtbusanncnt);
        txtpccnt = (TextView)findViewById(R.id.txtpccnt);
        txtslcnt = (TextView)findViewById(R.id.txtslcnt);
        txttvcnt = (TextView)findViewById(R.id.txttvcnt);

        Intent intent = getIntent();
        installationID = intent.getStringExtra("InstallationID");
        Station = intent.getStringExtra("Station");

        db = new DatabaseHandler(parent);
        sql = db.getWritableDatabase();

    }

    public void setListeners(){
        lay_advfirstplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open notifications list activity
                Intent intent = new Intent(NotificationsType.this, NotificationsList.class);
                intent.putExtra("InstallationID",installationID);
                intent.putExtra("MsgType","ADVFPLAY");
                intent.putExtra("Station",Station);
                startActivity(intent);
            }
        });

        lay_advnotrun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationsType.this, NotificationsList.class);
                intent.putExtra("InstallationID",installationID);
                intent.putExtra("MsgType","ADVNOTRUN");
                intent.putExtra("Station",Station);
                startActivity(intent);
            }
        });

        lay_busann.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationsType.this, NotificationsList.class);
                intent.putExtra("InstallationID",installationID);
                intent.putExtra("MsgType","BUSANN");
                intent.putExtra("Station",Station);
                startActivity(intent);
            }
        });

        lay_pconoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationsType.this, NotificationsList.class);
                intent.putExtra("InstallationID",installationID);
                intent.putExtra("MsgType","PCONOFF");
                intent.putExtra("Station",Station);
                startActivity(intent);
            }
        });

        lay_soundlevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationsType.this, NotificationsList.class);
                intent.putExtra("InstallationID",installationID);
                intent.putExtra("MsgType","SL");
                intent.putExtra("Station",Station);
                startActivity(intent);
            }
        });

        lay_tvstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationsType.this, NotificationsList.class);
                intent.putExtra("InstallationID",installationID);
                intent.putExtra("MsgType","TVSTAT");
                intent.putExtra("Station",Station);
                startActivity(intent);
            }
        });
    }

    public void showNotificationCnt(){

        String curcnt = "", notifCount = "0";

        curcnt = "Select * from TableNotifications WHERE InstallationId='"+installationID+"'" +
                " AND MsgType='ADVFPLAY'";
        Cursor cnt_firstplay = sql.rawQuery(curcnt, null);
        if(cnt_firstplay.getCount() > 0){
            notifCount = String.valueOf(cnt_firstplay.getCount());
            txtfpltimecnt.setText(String.valueOf(cnt_firstplay.getCount()));
        }else {
            notifCount = "0";
            txtfpltimecnt.setText("0");
        }

        curcnt = "Select * from TableNotifications WHERE InstallationId='"+installationID+"'" +
                " AND MsgType='ADVNOTRUN'";
        Cursor cnt_notrun = sql.rawQuery(curcnt, null);
        if(cnt_notrun.getCount() > 0){
            notifCount = String.valueOf(cnt_notrun.getCount());
            txtnotruncnt.setText(String.valueOf(cnt_notrun.getCount()));
        }else {
            notifCount = "0";
            txtnotruncnt.setText("0");
        }

        curcnt = "Select * from TableNotifications WHERE InstallationId='"+installationID+"'" +
                " AND MsgType='BUSANN'";
        Cursor cnt_busann = sql.rawQuery(curcnt, null);
        if(cnt_busann.getCount() > 0){
            notifCount = String.valueOf(cnt_busann.getCount());
            txtbusanncnt.setText(String.valueOf(cnt_busann.getCount()));
        }else {
            notifCount = "0";
            txtbusanncnt.setText("0");
        }

        curcnt = "Select * from TableNotifications WHERE InstallationId='"+installationID+"'" +
                " AND MsgType='SL'";
        Cursor cnt_sl = sql.rawQuery(curcnt, null);
        if(cnt_sl.getCount() > 0){
            notifCount = String.valueOf(cnt_sl.getCount());
            txtslcnt.setText(String.valueOf(cnt_sl.getCount()));
        }else {
            notifCount = "0";
            txtslcnt.setText("0");
        }

        curcnt = "Select * from TableNotifications WHERE InstallationId='"+installationID+"'" +
                " AND MsgType='PCONOFF'";
        Cursor cnt_pconof = sql.rawQuery(curcnt, null);
        if(cnt_pconof.getCount() > 0){
            notifCount = String.valueOf(cnt_pconof.getCount());
            txtpccnt.setText(String.valueOf(cnt_pconof.getCount()));
        }else {
            notifCount = "0";
            txtpccnt.setText("0");
        }

        curcnt = "Select * from TableNotifications WHERE InstallationId='"+installationID+"'" +
                " AND MsgType='TVSTAT'";
        Cursor cnt_tvstatus = sql.rawQuery(curcnt, null);
        if(cnt_tvstatus.getCount() > 0){
            notifCount = String.valueOf(cnt_tvstatus.getCount());
            txttvcnt.setText(String.valueOf(cnt_tvstatus.getCount()));
        }else {
            notifCount = "0";
            txttvcnt.setText("0");
        }
    }
}
