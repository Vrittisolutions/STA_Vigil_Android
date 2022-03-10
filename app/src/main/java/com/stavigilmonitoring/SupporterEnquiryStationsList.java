package com.stavigilmonitoring;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapters.SupEnqStationsListAdapter;
import com.beanclasses.StateList;
import com.database.DBInterface;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class SupporterEnquiryStationsList extends Activity {
    private Context parent;
    GridView list_supenqstn;
    EditText mTextview;
    TextView supEnquiry;
    ImageView mImageFilter,mImage;
    ProgressBar mProgress;
    com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
    String mType, mobno, filter, Subnetwork;
    private String sop, Ldate, dff, resposmsg;
    ArrayList<StateList> searchResults;
    private SupEnqStationsListAdapter StationAdaptor;
    DownloadStation mDownloadStation;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.stavigilmonitoring.R.layout.activity_supporter_enquiry_stations_list);

        init();

        Intent i = getIntent();
        mType = i.getStringExtra("Type");
        Subnetwork = i.getStringExtra("SubType");

        if(Subnetwork.equalsIgnoreCase("NoSubNetwork")){
            supEnquiry.setText("Supporter Enquiry - "+ mType);
        }else {
            supEnquiry.setText("Supporter Enquiry - "+ Subnetwork);
        }

        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();

        if (dbvalue()) {

            if(Subnetwork.equalsIgnoreCase("NoSubNetwork") || Subnetwork == null){
               //display all stations list
                updatelist();
            }else {
                updatelist_supporterwise();
            }

        } else if (ut.isnet(getApplicationContext())) {
            fetchdata();
        } else {
            try{
                ut.showD(SupporterEnquiryStationsList.this, "nonet");
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        setListener();
    }

    public void init(){
        parent = SupporterEnquiryStationsList.this;

        list_supenqstn = findViewById(com.stavigilmonitoring.R.id.list_supenqstn);
        supEnquiry = (TextView) findViewById(com.stavigilmonitoring.R.id.supEnquiry);
        mTextview = (EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext_search);
        mImageFilter = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_Enquiry_filter);
        mImage = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_enquiry);
        mProgress = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressenqry);
        db = new DatabaseHandler(getBaseContext());
        searchResults = new ArrayList<StateList>();

    }

    public void setListener(){
        mImageFilter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mTextview.getVisibility() == View.VISIBLE) {
                    mTextview.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus()
                            .getWindowToken(), 0);
                } else if (mTextview.getVisibility() == View.GONE) {
                    mTextview.setVisibility(View.VISIBLE);
                    mTextview.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mTextview,
                            InputMethodManager.SHOW_IMPLICIT);
                }

            }
        });

        mImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (ut.isnet(getApplicationContext())) {

                    fetchdata();
                } else {
                    try{
                        ut.showD(SupporterEnquiryStationsList.this, "nonet");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        });

        list_supenqstn.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Bundle dataBundle = new Bundle();
                dataBundle.putString("Subnet",Subnetwork);
                //	dataBundle.putString("frompage", frompage);
                dataBundle.putString("SubType", Subnetwork);
                dataBundle.putString("Type",mType);
                dataBundle.putString("StationName",searchResults.get(position).getStatioName());
                dataBundle.putString("Installationid",searchResults.get(position).getInstallationId());

                Intent i = new Intent(getApplicationContext(), SupporterList_stnwise.class);
                i.putExtras(dataBundle);
                startActivity(i);

            }
        });

        mTextview.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                filter = s.toString().trim();
                StationAdaptor.filter((filter).toLowerCase(Locale
                        .getDefault()));
            }
        });
    }

    private void fetchdata() {
        mDownloadStation = null;
        if (mDownloadStation == null) {
            mImage.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);

            Log.e("async", "null");
            mDownloadStation = new DownloadStation();
            mDownloadStation.execute();
        } else {
            if (mDownloadStation.getStatus() == AsyncTask.Status.RUNNING) {
                Log.e("async", "running");
                mImage.setVisibility(View.GONE);
                mProgress.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean dbvalue() {

        try {

            SQLiteDatabase sql = db.getWritableDatabase();
            Cursor cursor = sql
                    .rawQuery(
                            "Select distinct InstallationDesc from ConnectionStatusFiltermob",
                            null);// SoundLevel_new

            System.out.println("----------  dbvalue screen cursor count -- "
                    + cursor.getCount());

            if (cursor != null && cursor.getCount() > 0) {

                cursor.close();
                return true;

            } else {

                cursor.close();
                return false;
            }
        } catch (Exception e) {

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

            return false;
        }
    }

    private void updatelist_supporterwise() {
        searchResults.clear();
       // DatabaseHandler db = new DatabaseHandler(parent);
        SQLiteDatabase sql = db.getWritableDatabase();
        int count = 0;
        Cursor c = sql.rawQuery(
                "Select distinct InstallationDesc,InstalationId from ConnectionStatusFiltermob where SubNetworkCode='"+Subnetwork+"' Order by InstallationDesc",
                null);
        // ,InstallationId
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                count++;
                String StationName = c.getString(c.getColumnIndex("InstallationDesc"));
                String InstallationId = c.getString(c.getColumnIndex("InstalationId"));
                count = c.getCount();

                // Type = Type.replaceAll("0", "");
                // Type = Type.replaceAll("1", "");
                // if (!Type.trim().equalsIgnoreCase("")) {
                StateList sitem = new StateList();

                sitem.setStatioName(StationName);
                sitem.setInstallationId(InstallationId);
                sitem.Setcount(count);
                searchResults.add(sitem);
                // }
            } while (c.moveToNext());
        }

        StationAdaptor = new SupEnqStationsListAdapter(parent, searchResults);
        list_supenqstn.setAdapter(StationAdaptor);

    }

    private void updatelist() {
        searchResults.clear();
       // DatabaseHandler db = new DatabaseHandler(parent);
        SQLiteDatabase sql = db.getWritableDatabase();
        int count = 0;
        Cursor c = sql.rawQuery(
                        "Select distinct InstallationDesc,InstalationId from ConnectionStatusFiltermob where NetworkCode='"+mType+"' Order by InstallationDesc",
                        null);
        // ,InstallationId
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                count++;
                String StationName = c.getString(c.getColumnIndex("InstallationDesc"));
                String InstallationId = c.getString(c.getColumnIndex("InstalationId"));
                count = c.getCount();

                // Type = Type.replaceAll("0", "");
                // Type = Type.replaceAll("1", "");
                // if (!Type.trim().equalsIgnoreCase("")) {
                StateList sitem = new StateList();

                sitem.setStatioName(StationName);
                sitem.setInstallationId(InstallationId);
                sitem.Setcount(count);
                searchResults.add(sitem);
                // }
            } while (c.moveToNext());
        }

        StationAdaptor = new SupEnqStationsListAdapter(parent, searchResults);
        list_supenqstn.setAdapter(StationAdaptor);

    }

    public class DownloadStation extends AsyncTask<String, Void, String> {

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
              //  DatabaseHandler db = new DatabaseHandler(getBaseContext());
                SQLiteDatabase sql = db.getWritableDatabase();
                String columnName, columnValue;
               // sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusFiltermob");
               // sql.execSQL(ut.getConnectionStatusFiltermob());
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
            mImage.setVisibility(View.GONE);
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                if (sop.equals("valid")) {
                    if(Subnetwork.equalsIgnoreCase("NoSubNetwork") || Subnetwork != null){
                        //display all stations list
                        updatelist();
                    }else {
                        //updatelist_supwise();
                        updatelist_supporterwise();
                    }

                   // updatelist();
                } else {
                    try{
                        ut.showD(SupporterEnquiryStationsList.this, "invalid");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                mImage.setVisibility(View.VISIBLE);
                mProgress.setVisibility(View.GONE);

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
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}
