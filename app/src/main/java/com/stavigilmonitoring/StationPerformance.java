package com.stavigilmonitoring;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.adapters.SupporterEnuiryAdptr;
import com.beanclasses.SupportEnquiryHelper;
import com.database.DBInterface;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;

public class StationPerformance extends Activity {
    private Context parent;

    ListView listnw;
    ImageView mRefresh;
    ProgressBar mprogress;
    DatabaseHandler db;
    SQLiteDatabase sqlDb;
    String resposmsg, sop,mobno;
    private utility ut;
    private static DownloadnetWork asynk;
    private ArrayList<SupportEnquiryHelper> mSearchlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_station_performance);

        init();

        if (dbvalue()) {
            updatelist();

        } else if (ut.isnet(getApplicationContext())) {

            fetchdata();

        } else {
            ut.showD(StationPerformance.this, "nonet");
        }

        setListeners();
    }

    public void init(){
        parent = StationPerformance.this;

        listnw = (ListView)findViewById(R.id.listnetworks);

        mRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.imgbtnrfrsh);
        mprogress = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1);

        ut = new utility();

        db = new DatabaseHandler(getBaseContext());
        sqlDb = db.getWritableDatabase();

        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();

        mSearchlist = new ArrayList<SupportEnquiryHelper>();

    }

    public void setListeners(){
        mRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (ut.isnet(getApplicationContext())) {
                    fetchdata();
                } else {
                    ut.showD(StationPerformance.this, "nonet");
                }
            }
        });

        listnw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(StationPerformance.this,
                        StnPerformance_dayselection.class);
                intent.putExtra("Network",mSearchlist.get(position).getSubnetwok());
                startActivity(intent);

            }
        });
    }

    private boolean dbvalue() {
        try {
            Cursor cursor = sqlDb.rawQuery("SELECT  NetworkCode FROM ConnectionStatusFiltermob", null);
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.getColumnIndex("NetworkCode") < 0) {
                    return false;
                } else {
                    return true;
                }
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }

    private void fetchdata() {
        if (asynk == null) {
            mRefresh.setVisibility(View.VISIBLE);
            mprogress.setVisibility(View.GONE);

            Log.e("async", "null");
            asynk = new DownloadnetWork();
            asynk.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            if (asynk.getStatus() == AsyncTask.Status.RUNNING) {
                Log.e("async", "running");
                mRefresh.setVisibility(View.GONE);
                mprogress.setVisibility(View.VISIBLE);
            }
        }
    }

    public class DownloadnetWork extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String Url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMasterMobile?Mobile="+mobno;
            Log.e("All Station", "Url=" + Url);

            try {
                resposmsg = ut.httpGet(Url);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (resposmsg.contains("<InstalationId>")) {
                sop = "valid";
                String columnName, columnValue;
                //	sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusFiltermob");
                //	sql.execSQL(ut.getConnectionStatusFiltermob());
                sqlDb.delete("ConnectionStatusFiltermob",null,null);

                Cursor cur1 = sqlDb.rawQuery(
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
                    sqlDb.insert("ConnectionStatusFiltermob", null, values2);
                }

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
                    ut.showD(StationPerformance.this, "invalid");
                }
                mRefresh.setVisibility(View.VISIBLE);
                mprogress.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updatelist() {
        mSearchlist.clear();
        Cursor c = sqlDb
                .rawQuery(
                        "Select distinct NetworkCode from ConnectionStatusFiltermob Order by NetworkCode",
                        null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                int count = 0;
                String StationName = c.getString(c
                        .getColumnIndex("NetworkCode"));
                Cursor c1 = sqlDb.rawQuery("select distinct InstallationDesc from ConnectionStatusFiltermob where NetworkCode='"+StationName+"'",null);
                count = c1.getCount();

                SupportEnquiryHelper sitem = new SupportEnquiryHelper();
                sitem.setSubnetwok(StationName);
                mSearchlist.add(sitem);

                // }
            } while (c.moveToNext());
        }

        SupporterEnuiryAdptr adp = new SupporterEnuiryAdptr(StationPerformance.this, mSearchlist);
        adp.notifyDataSetChanged();
        listnw.setAdapter(adp);
    }

}
