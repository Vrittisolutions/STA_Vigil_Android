package com.stavigilmonitoring;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.adapters.StationEnquiryAdptr;
import com.beanclasses.StateList;
import com.database.DBInterface;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NetworkListActivity extends AppCompatActivity {
    private Context parent;
    private ImageView mRefresh;
    private ProgressBar mprogress;
    private GridView mList;
    private ArrayList<StateList> mSearchlist;
    private com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
    static SimpleDateFormat dff;
    static String Ldate;
    String resposmsg, sop,mobno;
    private static DownloadnetWork asynk;
    DatabaseHandler db;
    int SUPPORTER = 1;
    int NETWORK = 2;
    String supporterName = "", SupporterFullName = "", NetworkCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_network_list);

        init();

        setListeners();

        if (dbvalue()) {
            updatelist();
        } else if (ut.isnet(getApplicationContext())) {
            fetchdata();
        } else {
            try{
                ut.showD(parent, "nonet");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void setListeners() {

        mRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (ut.isnet(getApplicationContext())) {
                    fetchdata();
                } else {
                    try{
                        ut.showD(parent, "nonet");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });


        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Boolean value = subnetworkcheck(mSearchlist.get(position).getNetworkcode());
                //if (value) {
                    NetworkCode = mSearchlist.get(position).getNetworkcode();
                    Intent i = new Intent(getApplicationContext(), SupportersListActivity.class);
                    i.putExtra("Type", mSearchlist.get(position).getNetworkcode());
                    startActivityForResult(i,SUPPORTER);
                // } else {
                   /* String Flag = "NoSubnetWork";
                    Intent intent = new Intent(getApplicationContext(), StationEnquiryStationList.class);
                    intent.putExtra("Type", mSearchlist.get(position).getNetworkcode());
                    intent.putExtra("subType", mSearchlist.get(position).getNetworkcode());
                    intent.putExtra("NoSubnetWork", Flag);
                    startActivity(intent);*/
               // }
            }
        });
    }

    private void init() {
        parent = NetworkListActivity.this;
        mRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_Stn_Enq);
        mprogress = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBarstnenq);
        mList =  findViewById(com.stavigilmonitoring.R.id.lststnenq);
        mSearchlist = new ArrayList<StateList>();

        db = new DatabaseHandler(getBaseContext());

        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();
    }

    private boolean subnetworkcheck(String type) {
        //DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
        SQLiteDatabase sql = db.getWritableDatabase();
        ArrayList<String> mSubnetworklist = new ArrayList<String>();
        Cursor c = sql
                .rawQuery("SELECT DISTINCT SubNetworkCode FROM ConnectionStatusFiltermob where NetworkCode='"
                                + type + "'", null);
        if (c.getCount() == 0) {
            c.close();
            return false;

        } else if (c.getCount() > 0) {

            c.moveToFirst();
            do {

                int stncnt = 0;
                String Type = c.getString(0);
                mSubnetworklist.add(Type);

            } while (c.moveToNext());
            c.close();
        }

        if (mSubnetworklist.contains(type) && mSubnetworklist.size() > 1) {
            return true;
        } else {

            if (mSubnetworklist.contains(type)) {
                return false;
            } else {
                return true;
            }
        }
    }

    private void updatelist() {
        mSearchlist.clear();
        //	DatabaseHandler db = new DatabaseHandler(this);
        SQLiteDatabase sql = db.getWritableDatabase();

        Cursor c = sql
                .rawQuery(
                        "Select distinct NetworkCode from ConnectionStatusFiltermob Order by NetworkCode",
                        null);
        // ,InstallationId
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                int count = 0;
                String StationName = c.getString(c
                        .getColumnIndex("NetworkCode"));
                Cursor c1 = sql.rawQuery("select distinct InstallationDesc from ConnectionStatusFiltermob where NetworkCode='"+StationName+"'",null);
                count = c1.getCount();

                StateList sitem = new StateList();
                sitem.SetNetworkCode(StationName);
                sitem.Setcount(count);
                mSearchlist.add(sitem);

                // }
            } while (c.moveToNext());
        }
        StationEnquiryAdptr adp = new StationEnquiryAdptr(parent, mSearchlist,"Default");
        adp.notifyDataSetChanged();
        mList.setAdapter(adp);

    }

    private void fetchdata() {
        // new DownloadxmlsDataURL_new().execute();
        if (asynk == null) {
            mRefresh.setVisibility(View.VISIBLE);
            mprogress.setVisibility(View.GONE);

            Log.e("async", "null");
            asynk = new DownloadnetWork();
            asynk.execute();
        } else {
            if (asynk.getStatus() == AsyncTask.Status.RUNNING) {
                Log.e("async", "running");
                mRefresh.setVisibility(View.GONE);
                mprogress.setVisibility(View.VISIBLE);
            }
        }

    }

    private boolean dbvalue() {
        try {
            //DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
            SQLiteDatabase sql = db.getWritableDatabase();
            Cursor cursor = sql.rawQuery(
                    "SELECT  NetworkCode FROM ConnectionStatusFiltermob", null);
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.getColumnIndex("NetworkCode") < 0) {
                    cursor.close();
                    return false;
                } else {
                    cursor.close();
                    return true;
                }
            } else {
                cursor.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            dff = new SimpleDateFormat("HH:mm:ss");
            Ldate = dff.format(new Date());

            StackTraceElement l = new Exception().getStackTrace()[0];
            System.out.println(l.getClassName() + "/" + l.getMethodName() + ":"
                    + l.getLineNumber());
            ut = new com.stavigilmonitoring.utility();
            if (!ut.checkErrLogFile()) {

                ut.ErrLogFile();
            }
            if (ut.checkErrLogFile()) {
                ut.addErrLog(l.getClassName() + "/" + l.getMethodName() + ":"
                        + l.getLineNumber() + "	" + e.getMessage() + " "
                        + Ldate);
            }

        }
        return false;
    }

    public class DownloadnetWork extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String Url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMasterMobile?Mobile="+mobno;
            Log.e("All Station", "Url=" + Url);

            try {
                resposmsg = ut.httpGet(Url);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (resposmsg.contains("<InstalationId>")) {
                sop = "valid";
                //DatabaseHandler db = new DatabaseHandler(getBaseContext());
                SQLiteDatabase sql = db.getWritableDatabase();
                String columnName, columnValue;
                //sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusFiltermob");
                //sql.execSQL(ut.getConnectionStatusFiltermob());
                sql.delete("ConnectionStatusFiltermob",null,null);

                Cursor cur1 = sql.rawQuery(
                        "SELECT * FROM ConnectionStatusFiltermob", null);
                cur1.getCount();
                ContentValues values2 = new ContentValues();
                NodeList nl2 = ut.getnode(resposmsg, "Table");

                Log.e("All Station Data ", "get length : " + nl2.getLength());
                for (int i = 0; i < nl2.getLength(); i++) {
                    Log.e("All Station Data ", "length : " + nl2.getLength());
                    Element e = (Element) nl2.item(i);
                    for (int j = 0; j < cur1.getColumnCount(); j++) {
                        columnName = cur1.getColumnName(j);
                        columnValue = ut.getValue(e, columnName);
                        Log.e("All Station Data ", "column Name : "
                                + columnName);
                        Log.e("All Station Data ", "column value : "
                                + columnValue);

                        values2.put(columnName, columnValue);

                    }
                    sql.insert("ConnectionStatusFiltermob", null, values2);
                }

                cur1.close();

            } else {
                sop = "invalid";
                System.out.println("--------- invalid for project list --- ");
            }
            return sop;

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mRefresh.setVisibility(View.GONE);
            mprogress.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                if (sop.equals("valid")) {
                    updatelist();
                } else {
                    try{
                        ut.showD(parent, "invalid");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                mRefresh.setVisibility(View.VISIBLE);
                mprogress.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
                // dff = new SimpleDateFormat("HH:mm:ss");
                // Ldate = dff.format(new Date());

                StackTraceElement l = new Exception().getStackTrace()[0];
                System.out.println(l.getClassName() + "/" + l.getMethodName()
                        + ":" + l.getLineNumber());
                ut = new utility();
                if (!ut.checkErrLogFile()) {

                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()) {
                    ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                            + ":" + l.getLineNumber() + "	" + e.getMessage()
                            + " " + Ldate);
                }

            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            if(resultCode == SUPPORTER && requestCode == SUPPORTER){
                supporterName = data.getStringExtra("SupporterName");
                SupporterFullName = data.getStringExtra("SupporterFullName");
                Intent intent = new Intent();
                //intent.putExtra("SupporterId", mSearchlist.get(position).getStnSupName());
                intent.putExtra("SupporterName", supporterName);
                intent.putExtra("NetworkCode",NetworkCode);
                intent.putExtra("SupporterFullName",SupporterFullName);
                setResult(NETWORK, intent);
                finish();
            }
        }catch (Exception e){
            e.printStackTrace();
            finish();
        }
    }
}
