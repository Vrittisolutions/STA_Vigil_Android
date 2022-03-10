package com.stavigilmonitoring;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adapters.WorkAssign_ActivityDetailsAdapter;
import com.beanclasses.StateDetailsList;
import com.database.DBInterface;
import com.services.GPSTracker;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Admin-3 on 1/22/2018.
 */

public class WorkAssignSupporter_ActivityDetails extends Activity {
    String MyFlag = "no flag",mobileUserName;
    ListView lstcsn;
    TextView header;
    Context parent;
    com.stavigilmonitoring.utility ut;
    ImageView iv,btnfilter,btnadd;
    private ArrayList<String> NameList;
    WorkAssign_ActivityDetailsAdapter listAdapter;

    List<StateDetailsList> searchResults;
    String IssuedToName,mobno, network, stationName, actIssuedToPersonName;
    private static DownloadnetWork_New asynk_new;
    String mobi, ReassignBy, activityId, Remark, ReassignTo, worktype ="", StationID, Network = "";
    String sop = "t", resposmsg ="n";
    DatabaseHandler db;

    double sup_latitude, sup_longitude, stn_latitude, stn_longitude;
    // GPSTracker class
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.wrk_assn_activitydtls);

        initView();
        /*if (dbvalueforspinner()) {
            updateCustomerSpinner();
        } else if (ut.isnet(WorkAssignSupporter_ActivityDetails.this)) {
            new UpdateDMCUsers().execute();
        } else {
            Toast.makeText(parent, "No internet connection found..",
                    Toast.LENGTH_LONG).show();
        }*/
        updatelist();
        setListener();
    }

    private void fetchdata(){
        asynk_new = null;
        asynk_new = new DownloadnetWork_New();
        asynk_new.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public class DownloadnetWork_New extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String sumdata2;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String Url = "http://sta.vritti.co/imedia/STA_Announcement/DmCertificate.asmx/GetWorkAssignList?Mobile=" + mobno;
            Url = Url.replaceAll(" ", "%20");
            try {
                resposmsg = ut.httpGet(Url);
                Log.e("Response", resposmsg);

            }catch(IOException e){
                sop = "ServerError";
                e.printStackTrace();
            }
            if(resposmsg.contains("<DMHeaderId>")){
                sop = "valid";
              //  DatabaseHandler db = new DatabaseHandler(getBaseContext());
                SQLiteDatabase sql = db.getWritableDatabase();
                String columnName, columnValue;
              //  sql.execSQL("DROP TABLE IF EXISTS WorkAssignedTable");
               // sql.execSQL(ut.getWorkAssignList());
                sql.delete("WorkAssignedTable",null,null);

                Cursor cur = sql.rawQuery("SELECT * FROM WorkAssignedTable", null);
                ContentValues values1 = new ContentValues();
                NodeList nl1 = ut.getnode(resposmsg, "Table1");
                Log.e("WorkAssignedTable data",
                        " fetch data : " + nl1.getLength());
                for (int i = 0; i < nl1.getLength(); i++) {
                    Element e = (Element) nl1.item(i);
                    for (int j = 0; j < cur.getColumnCount(); j++) {
                        columnName = cur.getColumnName(j);
                        columnValue = ut.getValue(e, columnName);
                        values1.put(columnName, columnValue);
                    }
                    sql.insert("WorkAssignedTable",null, values1);
                }

                //cur.close();
               // sql.close();
                //db.close();

            }else{
                sop = "invalid";
            }

            return sop;
        }
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(parent);
            progressDialog.setMessage("Loading...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try{
                progressDialog.dismiss();
                if(sop.equals("valid")){
                    Log.e("Tag", " ******* work assign *********");
                    updatelist();
                    if (MyFlag.equalsIgnoreCase("set flag")) {
                        MyFlag = "no flag";
                        showD( "Done");
                    }
                } else if(sop.equals("nodata")){
                    updatelist();
                } else {
                    try{
                        ut.showD(WorkAssignSupporter_ActivityDetails.this, "invalid");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }catch(Exception e){
                e.printStackTrace();
                StackTraceElement l = new Exception().getStackTrace()[0];

                ut =new com.stavigilmonitoring.utility();
                if(!ut.checkErrLogFile()){
                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()){
                    ut.addErrLog(l.getClassName()+"/"+l.getMethodName()+":"+l.getLineNumber()+"  "+e.getMessage());
                }
            }
        }

    }

    protected void showD(final String string) {
        // TODO Auto-generated method stub

        final Dialog myDialog = new Dialog(parent);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoginfosmall);
        myDialog.setCancelable(true);
        // myDialog.getWindow().setGravity(Gravity.BOTTOM);

        final TextView txt = (TextView) myDialog
                .findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall);
        if (string.equals("Done")) {
            myDialog.setTitle(" ");
            txt.setText("Data Saved");
        } else if (string.equals("Error")) {
            myDialog.setTitle(" ");
            txt.setText("Server Error.. Please try after some time");
        } else if (string.equals("Issue")) {
            myDialog.setTitle(" ");
            txt.setText("Sorry you cannot complete this activity");
        }

        Button btn = (Button) myDialog
                .findViewById(com.stavigilmonitoring.R.id.gotobtndialoginfosmall);
        btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                myDialog.dismiss();
            }
        });

        myDialog.show();

    }

    private void setListener() {

        ((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
                .addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void afterTextChanged(Editable s) {
                        listAdapter
                                .filter_details(((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
                                        .getText().toString().trim()
                                        .toLowerCase(Locale.getDefault()));
                    }
                });

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(),WorkAssign_AssignActivity.class);
                intent.putExtra("Activity", "WorkAssignSupporter_ActivityDetails");
                intent.putExtra("Type", IssuedToName);
                startActivity(intent);
                finish();
            }
        });

        lstcsn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                /*DBInterface dbi = new DBInterface(getApplicationContext());
                mobno = dbi.GetPhno();
                dbi.Close();*/
                mobi = searchResults.get(position).GetSONumber();
                activityId = searchResults.get(position).GetActivityId();
                ReassignBy = mobno;
                ReassignTo = searchResults.get(position).GetGenrateFileName();
               //IssuedToName = searchResults.get(position).GetIssuedToName();
                mobileUserName = Common.UserName;
                stationName = searchResults.get(position).getStationName();
                Network = searchResults.get(position).getNetworkCode();
                String startDate = searchResults.get(position).GetActualStartDate().trim();
                String endDate = searchResults.get(position).GetActualEndDate().trim();

                String DMDesc = searchResults.get(position).GetDMDesc();
                StationID = searchResults.get(position).GetInstallationIdForStateDetailsList();

                if(DMDesc.equalsIgnoreCase("StationVisit")){

                    //test whether GPS is on or not
                    CheckGPSAvailable();
                    //get station location and compare both locations if both get matched then open station visit form
                    new Download_StationLocation().execute();

                    /*************testing purpose**************/
                    /*Intent intent = new Intent(WorkAssignSupporter_ActivityDetails.this, StationVisitForm.class);
                    intent.putExtra("activityId",activityId);
                    intent.putExtra("StationName",stationName);
                    intent.putExtra("Network", Network);
                    startActivity(intent);*/
                    /*****************************************/
                }if(DMDesc.contains("Video recording ")){
                    //open assign activity to supporters
                    Intent intent = new Intent(WorkAssignSupporter_ActivityDetails.this,SEAssignActivityToSupp.class);
                    //dmdesc,activityid,issuedbyId,
                    intent.putExtra("AdvDesc",DMDesc);
                    intent.putExtra("StartDate",startDate);
                    intent.putExtra("EndDate",endDate);
                    startActivity(intent);

                }else {
                    showNewPrompt();
                }
            }
        });

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                fetchdata();
            }
        });
    }

    protected void showNewPrompt() {
        // TODO Auto-generated method stub

        final Dialog myDialog = new Dialog(parent);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoyesnowithremark);
        myDialog.setCancelable(true);
        // myDialog.getWindow().setGravity(Gravity.BOTTOM);
        myDialog.setTitle("Complete Activity");

        final TextView quest = (TextView) myDialog.findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall1);
        final LinearLayout btnll = (LinearLayout) myDialog.findViewById(com.stavigilmonitoring.R.id.btnll);
        btnll.setVisibility(View.VISIBLE);
        quest.setText(" Do you want to complete activity ?");

        final AutoCompleteTextView editTextAssignTo = (AutoCompleteTextView) myDialog
                .findViewById(com.stavigilmonitoring.R.id.editTextAssignTo);
        final Spinner editTextWorkType = (Spinner) myDialog
                .findViewById(com.stavigilmonitoring.R.id.editTextworktype);
        editTextAssignTo.setFocusableInTouchMode(false);
        editTextAssignTo.setFocusable(false);
        editTextAssignTo.setClickable(false);
        editTextAssignTo.setText(ReassignTo);
        final EditText editTextNarration = (EditText) myDialog
                .findViewById(com.stavigilmonitoring.R.id.editTextNarration);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                com.stavigilmonitoring.R.array.worktype_class, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        editTextWorkType.setAdapter(adapter);
        editTextWorkType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*Toast.makeText(parent.getContext(),
                        "OnItemSelectedListener : " +
                                parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();*/
                worktype = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        Button btnyes = (Button) myDialog
                .findViewById(com.stavigilmonitoring.R.id.yesbtndialogremark);
        btnyes.setText("Complete");
        btnyes.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Remark = editTextNarration.getText().toString();
                if(editTextAssignTo.getText().toString().equalsIgnoreCase("")){
                    editTextAssignTo.setError("Please Select Name");
                    Toast.makeText(parent, "Incorrect Data", Toast.LENGTH_LONG).show();
                } else if(editTextNarration.getText().toString().equalsIgnoreCase("")){
                    editTextNarration.setError("Please Enter Remark");
                    Toast.makeText(parent, "Incorrect Data", Toast.LENGTH_LONG).show();
                } else {
                    if ( worktype.equalsIgnoreCase("")||
                            (!worktype.equalsIgnoreCase("Remote")
                                    && !worktype.equalsIgnoreCase("Travel") )){
                        Toast.makeText(parent, "Please select worktype", Toast.LENGTH_LONG).show();
                    } else if(!mobi.equalsIgnoreCase("")&& mobi!=null &&
                            !Remark.equalsIgnoreCase("")&& Remark!=null) {
                        completeactivity();
                    }
                }
                myDialog.dismiss();
                // finish();
            }
        });

        Button btnno = (Button) myDialog
                .findViewById(com.stavigilmonitoring.R.id.nobtndialogremark);
        btnno.setText("Update Reason");
        btnno.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Remark = editTextNarration.getText().toString();
                if(editTextNarration.getText().toString().equalsIgnoreCase("")){
                    editTextNarration.setError("Please Enter Remark");
                    Toast.makeText(parent, "Incorrect Data", Toast.LENGTH_LONG).show();
                } else {
                    if(!Remark.equalsIgnoreCase("")&& Remark!=null) {
                        reasonUpdate();
                    }
                }
                myDialog.dismiss();
                // finish();

            }
        });

        myDialog.show();

    }

    protected void completeactivity() {
        // TODO Auto-generated method stub
        //if (IssuedToName.equalsIgnoreCase(mobileUserName)) {
        if (actIssuedToPersonName.equalsIgnoreCase(mobileUserName)) {

            String urlStringToken = "http://sta.vritti.co/imedia/STA_Announcement/DMcertificate.asmx/ReassignedWork?UserId="
                    + mobi
                    + "&ActivityId="
                    + activityId
                    + "&ReassignedBy="
                    + ReassignBy
                    + "&worktype="
                    + worktype
                    + "&Remark="
                    + Remark;
            new CompleteActivityAPI().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, urlStringToken);
        } else {
            showD("Issue");
        }

    }

    protected void reasonUpdate() {
        // TODO Auto-generated method stub

        String urlStringToken = "http://sta.vritti.co/imedia/STA_Announcement/DMcertificate.asmx/WorkReasonUpdate?Mobile="
                + mobno
                + "&ActivityId="
                + activityId
                + "&Remark="
                + Remark;
        new CompleteActivityAPI().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,urlStringToken);
    }

    public class CompleteActivityAPI extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String responsemsg = "m";
        com.stavigilmonitoring.utility ut;


        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            ut = new com.stavigilmonitoring.utility();
          //  DatabaseHandler db = new DatabaseHandler(parent);
            SQLiteDatabase sql = db.getWritableDatabase();
            //String url = "http://sta.vritti.co/imedia/STA_Announcement/TimeTable.asmx/AlertApproveAndRejected?AlertId="
            String url = params[0];

            Log.e("ReassignedWork", "url : " + url);
            url = url.replaceAll(" ", "%20");
            try {
                System.out.println("-------  activity url --- " + url);
                responsemsg = ut.httpGet(url);

                System.out.println("-------------reassign-- " + responsemsg);
                Log.e("Reassign", responsemsg);

            } catch (NullPointerException e) {
                responsemsg = "Error";
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
                responsemsg = "Error";
            }

            return responsemsg;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            try {
                if (responsemsg.contains("error")) {
                    Log.e("Reassign", responsemsg);
                    showD("Error");
                } else if (responsemsg.contains("Error")) {
                    Log.e("Reassign", responsemsg);
                    showD("Error");
                } else if (responsemsg.contains("OK")||responsemsg.contains("Ok")) {
                    MyFlag ="set flag";
                    fetchdata();
                    Log.e("Reassign", responsemsg);
                    //ut.showD(getApplicationContext(),"Done");
                } else {
                    Log.e("Reassign", responsemsg);
                    try{
                        showD("Error");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();

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
                            + " ");
                }

            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(parent);
            progressDialog.setMessage("Processing...");
            progressDialog.show();
            MyFlag = "no flag";
        }
    }

    public void FilterClick(View v) {
        if (((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext)).getVisibility() == View.VISIBLE) {
            ((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
                    .setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } else if (((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext)).getVisibility() == View.GONE) {
            ((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
                    .setVisibility(View.VISIBLE);
            EditText textView = (EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext);
            textView.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
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

    private void updatelist() {
        searchResults.clear();
       // DatabaseHandler db = new DatabaseHandler(this);
        SQLiteDatabase sql = db.getWritableDatabase();
        Cursor c = sql.rawQuery(
                "SELECT ActivityName, ActualStartDate, ActualEndDate, Status, StationName, ActivityId, UserName,UserMasterId,IssuedUserName, InstallationId,NetworkCode FROM WorkAssignedTable WHERE IssuedUserName='"
                        + IssuedToName
                        + "' ORDER BY ActualEndDate", null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                Log.e("DATA", "Yes Data present");
                StateDetailsList sitem = new StateDetailsList();
                /*sitem.Setdmcstatus(c.getString(3));
                sitem.SetInstallationIdForStateDetailsList(c.getString(4));
                sitem.SetActivityId(c.getString(5));
                sitem.SetActualStartDate(ConvertDate(c.getString(1)));
                sitem.SetActualEndDate(ConvertDate(c.getString(2)));
                sitem.SetGenrateFileName(c.getString(6));
                sitem.SetSONumber(c.getString(7));
                sitem.SetIssuedToName(c.getString(8));
                searchResults.add(sitem);*/

                sitem.SetDMDesc(c.getString(c.getColumnIndex("ActivityName")));
                sitem.Setdmcstatus(c.getString(c.getColumnIndex("Status")));
                sitem.SetInstallationIdForStateDetailsList(c.getString(c.getColumnIndex("InstallationId")));
                sitem.setStationName(c.getString(c.getColumnIndex("StationName")));
                sitem.SetActivityId(c.getString(c.getColumnIndex("ActivityId")));
                sitem.SetActualStartDate(ConvertDate(c.getString(c.getColumnIndex("ActualStartDate"))));
                sitem.SetActualEndDate(ConvertDate(c.getString(c.getColumnIndex("ActualEndDate"))));
                sitem.SetGenrateFileName(c.getString(c.getColumnIndex("UserName")));   //activity assigned by username
                sitem.SetSONumber(c.getString(c.getColumnIndex("UserMasterId")));
                sitem.SetIssuedToName(c.getString(c.getColumnIndex("StationName")));  //activity assigned for Stationname to display station name in activity
                sitem.setNetworkCode(c.getString(c.getColumnIndex("NetworkCode")));  //activity assigned for Stationname to display station name in activity

                //network = c.getString(c.getColumnIndex("NetworkCode"));
               // subnetwork = c.getString(c.getColumnIndex("SubNetworkCode"));
                actIssuedToPersonName = c.getString(c.getColumnIndex("IssuedUserName"));
                searchResults.add(sitem);

                Log.e("DATA", "Yes Data added");

            } while (c.moveToNext());

        }
        listAdapter = new WorkAssign_ActivityDetailsAdapter(parent, searchResults);
        lstcsn.setAdapter(listAdapter);

    }

    private void initView() {
        ut = new utility();
        NameList = new ArrayList<String>();
        parent = WorkAssignSupporter_ActivityDetails.this;
        iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_nonrepeated_main);
        btnadd = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_alert_add);
        btnadd.setImageResource(com.stavigilmonitoring.R.drawable.work_assign);
        btnadd.setVisibility(View.VISIBLE);
        btnfilter = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_filter);
        btnfilter.setVisibility(View.VISIBLE);
        lstcsn = findViewById(com.stavigilmonitoring.R.id.lstcsn);
        header = (TextView) findViewById(com.stavigilmonitoring.R.id.header);

        header.setText("Activity Details");
        searchResults = new ArrayList<StateDetailsList>();

        Intent intent = getIntent();
        IssuedToName = intent.getStringExtra("Type");
        network = intent.getStringExtra("Network");
        stationName = intent.getStringExtra("StationName");
        header.setText("Activity Details - "+IssuedToName );

        db = new DatabaseHandler(getBaseContext());
        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();
    }

    public void CheckGPSAvailable(){
        gps = new GPSTracker(this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            sup_latitude = gps.getLatitude();
            sup_longitude = gps.getLongitude();

            // \n is for new line
            // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "+ sup_latitude + "\nLong: " + sup_longitude, Toast.LENGTH_LONG).show();

        }else{
            gps.showSettingsAlert();
        }
    }

    public class Download_StationLocation extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String sumdata2;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String Url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/Getlatlantofstation?Installationid="+StationID;
            Url = Url.replaceAll(" ", "%20");
            try {
                resposmsg = ut.httpGet(Url);
                Log.e("Response", resposmsg);

            }catch(IOException e){
                sop = "ServerError";
                e.printStackTrace();
            }

            if(resposmsg.contains("<TableResult>")){
                sop = "valid";

                NodeList nl1 = ut.getnode(resposmsg, "TableResult");

                for (int i = 0; i < nl1.getLength(); i++) {

                    Element e = (Element) nl1.item(i);

                    stn_latitude = Double.parseDouble(ut.getValue(e,"Latitude"));
                    stn_longitude = Double.parseDouble(ut.getValue(e,"Longitude"));
                }
            }else{
                sop = "invalid";
            }
            return sop;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            iv.setVisibility(View.GONE);
            progressDialog = new ProgressDialog(parent);
            ((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
                    .setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try{
                if(sop.equals("valid")){
                    Toast.makeText(parent,"Station location available \n "+stn_latitude + "\n" +stn_longitude,
                            Toast.LENGTH_SHORT).show();

                    if(checkDistance()){
                        Toast.makeText(parent,"Both locations get matched",Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(parent, StationVisitForm.class);
                        intent.putExtra("activityId",activityId);
                        intent.putExtra("StationName",stationName);

                        intent.putExtra("Network", network);
                        startActivity(intent);

                    }else {
                        Toast.makeText(parent,"Locations are not getting matched",Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(parent,"No station location available", Toast.LENGTH_SHORT).show();
                    /*testing purpose*/

                }

                iv.setVisibility(View.VISIBLE);
                ((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
                        .setVisibility(View.GONE);

            }catch(Exception e){
                e.printStackTrace();
                StackTraceElement l = new Exception().getStackTrace()[0];

                ut =new utility();
                if(!ut.checkErrLogFile()){
                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()){
                    ut.addErrLog(l.getClassName()+"/"+l.getMethodName()+":"+l.getLineNumber()+"  "+e.getMessage());
                }
            }
        }

        private boolean checkDistance(){
            /*float radius =  100F;
            float[] results = new float[3];
            Location.distanceBetween(sup_latitude, sup_longitude, stn_latitude, stn_longitude, results);
          //  return (results[0] <= radius);*/

            Location location_supp = new Location("point A");

            location_supp.setLatitude(sup_latitude);
            location_supp.setLongitude(sup_longitude);

            Location location_stn = new Location("point B");

            /***************************************************************************/
            /*stn_latitude = 18.501950;
            stn_longitude = 73.816560;*/
            /***************************************************************************/

            location_stn.setLatitude(stn_latitude);
            location_stn.setLongitude(stn_longitude);

            float distance = location_supp.distanceTo(location_stn);

            if(distance < 150F){
                Toast.makeText(parent,"Distance is matched within 150mtr",Toast.LENGTH_SHORT).show();
                return true;
            }else {
                if(distance < 250F){
                    Toast.makeText(parent,"Distance is matched within 250mtr",Toast.LENGTH_SHORT).show();
                    return true;
                }else {
                    if(distance < 350F){
                        Toast.makeText(parent,"Distance is matched within 350mtr",Toast.LENGTH_SHORT).show();
                        return true;
                    }else {
                        if(distance < 500F){
                            Toast.makeText(parent,"Distance is matched within 500mtr",Toast.LENGTH_SHORT).show();
                            return true;
                        }else {
                            Toast.makeText(parent,"Distances are not getting matched",Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        //return false;
                    }
                    //return false;
                }
                //return false;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(WorkAssignSupporter_ActivityDetails.this,
                WorkAssignSupporterNameListActivity.class);
        startActivity(i);
        finish();
    }
}
