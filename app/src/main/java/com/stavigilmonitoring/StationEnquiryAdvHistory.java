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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapters.StationEnqAdvDetailsAdapter;
import com.beanclasses.StnEnqAdvList;
import com.database.DBInterface;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StationEnquiryAdvHistory extends Activity{
    Context parent;
    private ImageView mRefresh;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private LinearLayout mAllnet;
    private TextView mText;
    static SimpleDateFormat dff;
    static String Ldate;
    private com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
    String responsemsg,  sop;
    StationEnqAdvDetailsAdapter listAdapter;
    List<StnEnqAdvList> searchResults;
    static DownloadxmlsDataURL_new asyncfetch_non;
    private String mobno, type, conn;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.stavigilmonitoring.R.layout.stationenquiryworkhistory);

        mRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.buttn_refresh_work_his);
        mProgressBar = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar_work);
        mText = (TextView) findViewById(com.stavigilmonitoring.R.id.work_Station);
        mListView = (ListView) findViewById(com.stavigilmonitoring.R.id.worklist);
        parent = StationEnquiryAdvHistory.this;
        searchResults = new ArrayList<StnEnqAdvList>();

        db = new DatabaseHandler(getApplicationContext());

        DBInterface dbi = new DBInterface(parent);
        //mobno = "0";
        mobno = dbi.GetPhno();
        dbi.Close();
        Intent i = getIntent();
        conn = i.getStringExtra("stnname");
        type = i.getStringExtra("stninst");
        mText.setText("Advertisement History - " + conn);
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (ut.isnet(parent)) {
                    fetchdata();
                } else {
                    ut.showD(parent, "nonet");
                }
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

            }
        });
         if (ut.isnet(getApplicationContext())) {
            fetchdata();
        } else {
            ut.showD(StationEnquiryAdvHistory.this, "nonet");
        }
    }


    private void fetchdata() {
        asyncfetch_non = new DownloadxmlsDataURL_new();
        asyncfetch_non.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private void updatelist() {
        // TODO Auto-generated method stub
        searchResults.clear();
        //DatabaseHandler db = new DatabaseHandler(parent);
        SQLiteDatabase sql = db.getWritableDatabase();

        Cursor c = sql
                .rawQuery(
                        "SELECT ScheduleTime,AdvertisementCode,AdvertisementDesc,AudioOutPut FROM AdvDetailsTable ORDER BY ScheduleTime", null);

        //c.moveToFirst();
        int cnt = 0;
        Log.e("Pending n/w count", "" + c.getCount());
        if (c.getCount() == 0) {

            c.close();
        } else {
            c.moveToFirst();
            do {
                searchResults.add(new StnEnqAdvList(c.getString(c
                        .getColumnIndex("AdvertisementDesc")), c.getString(c
                        .getColumnIndex("AdvertisementCode")), c.getString(c
                        .getColumnIndex("ScheduleTime")), c
                        .getString(c.getColumnIndex("AudioOutPut"))));
            } while (c.moveToNext());
        }

        listAdapter = new StationEnqAdvDetailsAdapter(parent, searchResults);
        mListView.setAdapter(listAdapter);
    }

    public class DownloadxmlsDataURL_new extends
            AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

           // DatabaseHandler db = new DatabaseHandler(getBaseContext());
            SQLiteDatabase sql = db.getWritableDatabase();
            String url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetAdvertisementHistory?InstallationId="+type;

            Log.e("csn status", "url : " + url);
            url = url.replaceAll(" ", "%20");
            try {
                responsemsg = com.stavigilmonitoring.utility.httpGet(url);
                Log.e("csn status", "resmsg : " + responsemsg);
                //sql.execSQL("DROP TABLE IF EXISTS AdvDetailsTable");
                //sql.execSQL(ut.getAdvDetails());
                sql.delete("AdvDetailsTable",null,null);

                Cursor cur = sql
                        .rawQuery("SELECT * FROM AdvDetailsTable", null);
               // cur.moveToFirst();
                if (responsemsg.contains("<ScheduleTime>")) {
                    sop = "valid";
                    String columnName, columnValue;

                    ContentValues values1 = new ContentValues();
                    NodeList nl1 = ut.getnode(responsemsg, "TableResult");

                    Log.e("All Station data...",
                            " fetch data : " + nl1.getLength());
                    for (int i = 0; i < nl1.getLength(); i++) {
                        Element e = (Element) nl1.item(i);
                        for (int j = 0; j < cur.getColumnCount(); j++) {
                            columnName = cur.getColumnName(j);

                            columnValue = ut.getValue(e, columnName);
                            if(columnValue==null||columnValue.equalsIgnoreCase("")||columnValue.equalsIgnoreCase("null")){
                                columnValue="No Info";
                            }
                            values1.put(columnName, columnValue);
                        }
                        sql.insert("AdvDetailsTable", null, values1);
                    }

                    cur.close();

                } else {
                    sop = "invalid";
                    cur.close();

                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                dff = new SimpleDateFormat("HH:mm:ss");
                Ldate = dff.format(new Date());

                StackTraceElement l = new Exception().getStackTrace()[0];
                System.out.println(l.getClassName() + "/" + l.getMethodName()
                        + ":" + l.getLineNumber());
                ut = new com.stavigilmonitoring.utility();
                if (!ut.checkErrLogFile()) {

                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()) {
                    ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                            + ":" + l.getLineNumber() + "	" + e.getMessage()
                            + " " + Ldate);
                }

            } catch (IOException e) {
                e.printStackTrace();
                dff = new SimpleDateFormat("HH:mm:ss");
                Ldate = dff.format(new Date());

                StackTraceElement l = new Exception().getStackTrace()[0];
                System.out.println(l.getClassName() + "/" + l.getMethodName()
                        + ":" + l.getLineNumber());
                ut = new com.stavigilmonitoring.utility();
                if (!ut.checkErrLogFile()) {

                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()) {
                    ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                            + ":" + l.getLineNumber() + "	" + e.getMessage()
                            + " " + Ldate);
                }

            }
            return sop;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                mRefresh.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                if (sop == "valid") {
                    updatelist();
                } else {
                    ut.showD(StationEnquiryAdvHistory.this, "nodata");
                }


            } catch (NullPointerException e) {
                e.printStackTrace();
                dff = new SimpleDateFormat("HH:mm:ss");
                Ldate = dff.format(new Date());

                StackTraceElement l = new Exception().getStackTrace()[0];
                System.out.println(l.getClassName() + "/" + l.getMethodName()
                        + ":" + l.getLineNumber());
                ut = new com.stavigilmonitoring.utility();
                if (!ut.checkErrLogFile()) {

                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()) {
                    ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                            + ":" + l.getLineNumber() + "	" + e.getMessage()
                            + " " + Ldate);
                }

            } catch (Exception e) {
                e.printStackTrace();
                dff = new SimpleDateFormat("HH:mm:ss");
                Ldate = dff.format(new Date());

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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRefresh.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

    }

}
