package com.stavigilmonitoring;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;

import com.adapters.SEActivityHistoryAdapter;
import com.beanclasses.StateDetailsList;
import com.database.DBInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SE_StationwiseHistory_Activity extends AppCompatActivity {
    private Context parent;
    ListView listhistory;

    ArrayList<StateDetailsList> list;
    SEActivityHistoryAdapter adapter;
    DatabaseHandler db;
    SQLiteDatabase sql;
    String StationName,mobno,network,subnetwork,IssuedToName,Activity=null,SubType1,Type1,frompage1,actIssuedToPersonName, userName,IssuedUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_seactivities_history);

        init();

        updatelist();

        setListeners();
    }

    public void init(){
        parent = SE_StationwiseHistory_Activity.this;

        listhistory = findViewById(R.id.listhistory);
        db = new DatabaseHandler(getBaseContext());
        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();

        Intent intent = getIntent();
        IssuedUserName = intent.getStringExtra("IssuedUserName");

        userName = Common.UserName;

        list = new ArrayList<StateDetailsList>();
        db = new DatabaseHandler(parent);
        sql = db.getWritableDatabase();
    }

    public void setListeners(){

    }

    private void updatelist() {
        list.clear();
        //  DatabaseHandler db = new DatabaseHandler(this);
        SQLiteDatabase sql = db.getWritableDatabase();
        Cursor c = sql.rawQuery(
                "SELECT ActivityName, ActualStartDate, ActualEndDate, Status, StationName, ActivityId, UserName,UserMasterId,IssuedUserName," +
                        "NetworkCode,SubNetworkCode,InstallationId FROM WorkAssignedTable WHERE UserName='"
                        + IssuedUserName + "' AND ActivityName like '%Video recording for%' ORDER BY ActualEndDate", null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                Log.e("DATA", "Yes Data present");
                StateDetailsList sitem = new StateDetailsList();
                sitem.SetDMDesc(c.getString(c.getColumnIndex("ActivityName")));
                sitem.Setdmcstatus(c.getString(c.getColumnIndex("Status")));
                sitem.SetInstallationIdForStateDetailsList(c.getString(c.getColumnIndex("InstallationId")));
                sitem.SetActivityId(c.getString(c.getColumnIndex("ActivityId")));
                sitem.SetActualStartDate(ConvertDate(c.getString(c.getColumnIndex("ActualStartDate"))));
                sitem.SetActualEndDate(ConvertDate(c.getString(c.getColumnIndex("ActualEndDate"))));
                sitem.SetGenrateFileName(c.getString(c.getColumnIndex("UserName")));   //activity assigned by
                sitem.SetSONumber(c.getString(c.getColumnIndex("UserMasterId")));   //activity assigner's ID
                sitem.SetIssuedToName(c.getString(c.getColumnIndex("IssuedUserName")));  //activity assigned to personname display supportername on screen
                sitem.setStationName(c.getString(c.getColumnIndex("StationName")));
                network = c.getString(c.getColumnIndex("NetworkCode"));
                subnetwork = c.getString(c.getColumnIndex("SubNetworkCode"));
                list.add(sitem);

                actIssuedToPersonName = c.getString(c.getColumnIndex("IssuedUserName"));
                Log.e("DATA", "Yes Data added");

            } while (c.moveToNext());

        }

        if(list.isEmpty()){
            // showD("NoActivities");
        }else {
            adapter = new SEActivityHistoryAdapter(this,list);
            listhistory.setAdapter(adapter);
        }
    }

    private String ConvertDate(String amcExpireDt) {
        String result = null;
        // 2017-10-30T00:00:00+05:30
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss+05:30", Locale.ENGLISH);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM,yyyy");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+05:30", Locale.ENGLISH);
        try {
            if (amcExpireDt.contains(".")) {
                Date date2 = dateFormat1.parse(amcExpireDt);
                result = dateFormat2.format(date2);
            }else{
                Date date2 = dateFormat3.parse(amcExpireDt);
                result = dateFormat2.format(date2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
