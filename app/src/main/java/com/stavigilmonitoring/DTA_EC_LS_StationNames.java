package com.stavigilmonitoring;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adapters.DT_StationList_Adapter;
import com.beanclasses.StateList;
import com.database.DBInterface;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DTA_EC_LS_StationNames extends Activity {
    private Context parent;
    TextView teststname;
    EditText searchSation;
    String NW_CodeName;
    private GridView lstcsn;
    TextView title;
    TextView tvheader;
    ImageView iv;
    ArrayList<StateList> searchResults;

    static DownloadxmlsDataURL_new asyncfetch_csnstate;

    String Type;
    String mobno, link;
    AsyncTask depattask;
    static SimpleDateFormat dff;
    static String Ldate;
    String sop = "no";

    com.stavigilmonitoring.utility ut;
    String responsemsg = "k";
    DT_StationList_Adapter StationListAdapter;
    String DaysInstKey, LS_EC_Key;
    private ImageView mImageFilter;
    private String filter;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.stavigilmonitoring.R.layout.activity_dta__ec__ls__station_names);

        parent = DTA_EC_LS_StationNames.this;

        Intent intent = getIntent();
        ut = new com.stavigilmonitoring.utility();
        NW_CodeName = intent.getStringExtra("NetWorkName");
        Type = intent.getStringExtra("Type");
        DaysInstKey = intent.getStringExtra("DaysInstKey");
        LS_EC_Key = intent.getStringExtra("LS_EC_Key");

        init();

        db = new DatabaseHandler(getBaseContext());

        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();

        if (asyncfetch_csnstate != null
                && asyncfetch_csnstate.getStatus() == AsyncTask.Status.RUNNING) {
            Log.e("async", "running");
            iv.setVisibility(View.GONE);
            ((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
                    .setVisibility(View.VISIBLE);
        }

        //Log.e("Sound level", " dbval : " + dbvalue());

        //get stations of LS

        if (isnet()) {
            fetchdata();

        } else {
            showD("nonet");
        }

        setListeners();
    }

    public void init(){
        tvheader = (TextView)findViewById(com.stavigilmonitoring.R.id.tvheader);
        tvheader.setText("Stations - "+DaysInstKey+ " Days Instance "+NW_CodeName);

        searchSation = (EditText)findViewById(com.stavigilmonitoring.R.id.edfiter_searchstation);

        lstcsn = findViewById(com.stavigilmonitoring.R.id.list_stationnames);
        iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_connection_main);
        title = (TextView) findViewById(com.stavigilmonitoring.R.id.materialreq);
        searchResults = new ArrayList<StateList>();
        teststname = (TextView)findViewById(com.stavigilmonitoring.R.id.txtteststnanme);
        teststname.setVisibility(View.GONE);

        mImageFilter = (ImageView)findViewById(com.stavigilmonitoring.R.id.button_filter);
    }

    public void setListeners(){
        teststname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(parent,"NW code clicked",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(parent, DT_Analysis_Details.class);
                startActivity(intent);
            }
        });

        lstcsn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //display details of station
                String stationName = searchResults.get(position).getStatioName();
                String InstallationId = searchResults.get(position).getInstallationId();

                Intent intent = new Intent(getApplicationContext(), DT_StationDetails.class);
                intent.putExtra("StationName", stationName);
                intent.putExtra("InstallationId",InstallationId);
                intent.putExtra("LS_EC_Key",LS_EC_Key);
                intent.putExtra("DaysInstKey",DaysInstKey);
                intent.putExtra("NW_CodeName",NW_CodeName);
                startActivity(intent);
            }
        });

        mImageFilter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (searchSation.getVisibility() == View.VISIBLE) {
                    searchSation.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus()
                            .getWindowToken(), 0);
                } else if (searchSation.getVisibility() == View.GONE) {
                    searchSation.setVisibility(View.VISIBLE);
                    searchSation.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(searchSation,
                            InputMethodManager.SHOW_IMPLICIT);
                }

            }
        });

        searchSation.addTextChangedListener(new TextWatcher() {

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
                StationListAdapter.filter((filter).toLowerCase(Locale
                        .getDefault()));
            }
        });
    }

    private boolean dbvalue() {

        try {
            //DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
            SQLiteDatabase sql = db.getWritableDatabase();
            Cursor cursor = sql.rawQuery(
                    "Select distinct StatioName from AllStation Where NetworkCode='"
                            + Type + "'", null);// SoundLevel_new

            System.out.println("----------  dbvalue screen cursor count -- "
                    + cursor.getCount());

            if (cursor != null && cursor.getCount() > 0) {

                return true;

            } else {

                return false;
            }
        } catch (Exception e) {
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
            return false;
        }
    }

    private void fetchdata() {
        //hit API to get station names
         searchResults.clear();

        new DownloadxmlsDataURL_new().execute();

        if (asyncfetch_csnstate == null) {
            iv.setVisibility(View.VISIBLE);
            ((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1)).setVisibility(View.GONE);
            Log.e("async", "null");
            searchResults.clear();

            asyncfetch_csnstate = new DownloadxmlsDataURL_new();
            asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        } else {
            if (asyncfetch_csnstate.getStatus() == AsyncTask.Status.RUNNING) {
                Log.e("async", "running");
                iv.setVisibility(View.GONE);
                ((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1)).setVisibility(View.VISIBLE);
            }
        }
    }

    //URL for LS & EC
    public class DownloadxmlsDataURL_new extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String LS_stationName, LS_stationID, count, TotalCount, InstanceCount;

        @Override
        protected String doInBackground(String... params) {
            com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();

            sop = "valid";
            String columnName, columnValue;

            if(LS_EC_Key.contains("LS") || LS_EC_Key.contains("EC")){

                //get stations list
                String url_LS = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetStationStartCloseDetailsStationWiseCount" +
                        "?Dwntype="+LS_EC_Key+"&noofdays="+DaysInstKey+"&NetworkCode="+NW_CodeName;

                Log.e("csn status", "url : " + url_LS);
                url_LS = url_LS.replaceAll(" ", "%20");

                try {
                    responsemsg = ut.httpGet(url_LS);

                    if (responsemsg.contains("<InstallationId>")) {
                        sop = "valid";
                        NodeList nl1 = ut.getnode(responsemsg, "TableResult");

                        for (int i = 0; i < nl1.getLength(); i++) {

                            Element e = (Element) nl1.item(i);

                            count = ut.getValue(e,"count");
                            LS_stationName = ut.getValue(e,"InstalationName");
                            LS_stationID = ut.getValue(e,"InstallationId");

                            StateList sitem = new StateList();
                            sitem.SetNetworkCode(Type);
                            sitem.setStatioName(LS_stationName);
                            sitem.setInstallationId(LS_stationID);
                            sitem.Setcount(Integer.parseInt(count));
                            sitem.setLS_EC_Key(LS_EC_Key);
                            searchResults.add(sitem);
                        }

                    } else {
                        sop = "invalid";
                        System.out
                                .println("--------- invalid for project list --- ");
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

            }else if(LS_EC_Key.contains("DT_INSTANCE")){
                //get stations list

                String url_DT_INSTANCE = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetDowntimeInstancesSuppoterwise" +
                        "?Mobileno="+mobno+"&Noofdays="+DaysInstKey+"&networkcode="+NW_CodeName;

                Log.e("csn status", "url : " + url_DT_INSTANCE);
                url_DT_INSTANCE = url_DT_INSTANCE.replaceAll(" ", "%20");

                try {
                    responsemsg = ut.httpGet(url_DT_INSTANCE);

                    if (responsemsg.contains("<InstallationID>")) {
                        sop = "valid";
                        NodeList nl1 = ut.getnode(responsemsg, "TableResult");

                        for (int i = 0; i < nl1.getLength(); i++) {

                            Element e = (Element) nl1.item(i);

                            LS_stationName = ut.getValue(e,"InstallationNmae");
                            LS_stationID = ut.getValue(e,"InstallationID");
                            TotalCount = ut.getValue(e,"TotalCount");
                            InstanceCount = ut.getValue(e,"InstanceCount");

                            StateList sitem = new StateList();
                            sitem.SetNetworkCode(Type);
                            sitem.setStatioName(LS_stationName);
                            sitem.setInstallationId(LS_stationID);
                            sitem.setTotDisconCnt(Integer.parseInt(TotalCount));
                            sitem.setTotDiscn_30minCntinst(Integer.parseInt(InstanceCount));
                            sitem.setLS_EC_Key(LS_EC_Key);
                            searchResults.add(sitem);
                        }

                    } else {
                        sop = "invalid";
                        System.out
                                .println("--------- invalid for project list --- ");
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
            }

            return sop;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                if (sop.equals("valid")) {

                    //display list to adapter
                   //Toast.makeText(getApplicationContext(),"Wait for a while! Loading Station names",Toast.LENGTH_SHORT).show();
                    StationListAdapter = new DT_StationList_Adapter(getApplicationContext(), searchResults);
                    lstcsn.setAdapter(StationListAdapter);

                } else {
                    showD("invalid");
                   // Toast.makeText(getApplicationContext(),"Sorry! No stations are available for this instance",Toast.LENGTH_SHORT).show();
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

            iv.setVisibility(View.GONE);
            ((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
                    .setVisibility(View.VISIBLE);
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DTA_EC_LS_StationNames.this);
            progressDialog.setMessage("Wait for a while loading station names");
            progressDialog.show();
            iv.setVisibility(View.GONE);
            ((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
                    .setVisibility(View.VISIBLE);
        }
    }

    protected void showD(String string) {
        // TODO Auto-generated method stub

        final Dialog myDialog = new Dialog(parent);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoginfosmall);
        myDialog.setCancelable(true);
        // myDialog.getWindow().setGravity(Gravity.BOTTOM);

        TextView txt = (TextView) myDialog
                .findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall);
        if (string.equals("empty")) {
            myDialog.setTitle("Error...");
            txt.setText("Please Fill required data..");
        } else if (string.equals("nonet")) {
            myDialog.setTitle("Error...");
            txt.setText("No Internet Connection Found.Please Activate internet Connectin on Device..");
        } else if (string.equals("invalid")) {
            myDialog.setTitle(" ");
            txt.setText("No Refresh Data Available.Please check internet connection...");
        }

        Button btn = (Button) myDialog
                .findViewById(com.stavigilmonitoring.R.id.gotobtndialoginfosmall);
        btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                myDialog.dismiss();
                // finish();
            }
        });

        myDialog.show();
    }

    private boolean isnet() {
        Context context = this.getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    //get all stations list
    private void updatelist() {
        searchResults.clear();
       // DatabaseHandler db = new DatabaseHandler(this);
        SQLiteDatabase sql = db.getWritableDatabase();
        int count = 0;
        Cursor c = sql.rawQuery(
                "Select distinct StatioName,InstallationId from AllStation Where NetworkCode='"
                        + Type + "'", null);
        //,InstallationId
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                String StationName = c.getString(c.getColumnIndex("StatioName"));
                String	InstallationId = c.getString(c.getColumnIndex("InstallationId"));
                count = c.getCount();

                // Type = Type.replaceAll("0", "");
                // Type = Type.replaceAll("1", "");
                // if (!Type.trim().equalsIgnoreCase("")) {
                StateList sitem = new StateList();
                sitem.SetNetworkCode(Type);
                sitem.setStatioName(StationName);
                sitem.setInstallationId(InstallationId);
                sitem.Setcount(count);
                searchResults.add(sitem);
                // }
            } while (c.moveToNext());
        }
        StationListAdapter = new DT_StationList_Adapter(getApplicationContext(), searchResults);
        lstcsn.setAdapter(StationListAdapter);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
