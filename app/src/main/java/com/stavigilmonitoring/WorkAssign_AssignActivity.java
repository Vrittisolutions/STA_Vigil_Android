package com.stavigilmonitoring;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.database.DBInterface;
import com.stavigilmonitoring.ConnectionStatusStatewise;
import com.stavigilmonitoring.SoundLevelStateWise;
import com.stavigilmonitoring.utility;
import com.stavigilmonitoring.workassignStateselectActivity;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Admin-3 on 1/10/2018.
 */

public class WorkAssign_AssignActivity extends Activity {
    private TextView tvhead;
    private Button BtnSave,EdtSTN, BtnRtn, BtnItemName, EdtStartDate, EdtEndDate;
    private EditText  EdtDesc , EdtRemark;

    int flag = 0;
    private ImageView BtnDelete;
    private ImageView btnRefresh;
    private ProgressBar mprogressBar;
    private ArrayList<String> NameList;
    private AutoCompleteTextView editTextAssignTo;

    static int Year, month, day;
    String trnstartDate,trnendDate,trnassignto, trnassigndesc,trnremark="testing",remark="Remote";

    Context parent;
    com.stavigilmonitoring.utility ut;
    String mobno,mType,installationid,stationid,sup,ActivityName,IssuedToName,StnName;

    String sop = "no";
    String resposmsg ="n";
    String responsemsg = "k";
    DownloadnetWork_New async_new;
    private ArrayList<String> SpinnerList;
    DownloadxmlsDataURL_new asyncfetch_csnstate;
    Spinner sp_SpinnerList;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.stavigilmonitoring.R.layout.activityworkassign);

        initView();
        updateSpinner();
        GetSupporterList();
        SetListeners();
    }

    private void updateSpinner() {
        SpinnerList.clear();

        SpinnerList.add("Remote");
        SpinnerList.add("Station Visit");

        ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(parent,
                android.R.layout.simple_list_item_1, SpinnerList);
        sp_SpinnerList.setAdapter(adapter5);
    }

    protected boolean isvalid() {
        // TODO Auto-generated method stub
        if (!(EdtSTN.getText().toString().length() > 0)) {
            Toast.makeText(WorkAssign_AssignActivity.this, "Please Enter Station Name",
                    Toast.LENGTH_LONG).show();
            return false;
        } else if (!(trnassignto.length() > 0)) {
            Toast.makeText(WorkAssign_AssignActivity.this, "Please Select Supporter.",
                    Toast.LENGTH_LONG).show();
            return false;
        }else if (!(EdtDesc.getText().toString().length() > 0)) {
            Toast.makeText(WorkAssign_AssignActivity.this, "Please Enter Activity Description.",
                    Toast.LENGTH_LONG).show();
            return false;
        } else if (!(EdtStartDate.getText().toString().length() > 0)) {
            Toast.makeText(WorkAssign_AssignActivity.this, "Please select Start date.",
                    Toast.LENGTH_LONG).show();
            return false;
        }else if (!(EdtEndDate.getText().toString().length() > 0)) {
            Toast.makeText(WorkAssign_AssignActivity.this, "Please select End date.",
                    Toast.LENGTH_LONG).show();

            return false;
        }else{
            return true;
        }
    }

    private void SetListeners() {
        EdtSTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(WorkAssign_AssignActivity.this, workassignStateselectActivity.class);
                intent.putExtra("mobileno", mobno);
                startActivityForResult(intent, Common.WorkAssignStn1);
            }
        });

        BtnRtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
               /* if (ActivityName.equalsIgnoreCase("WorkAssignSupporterNameListActivity")) {
                    Intent i = new Intent(WorkAssign_AssignActivity.this,
                            WorkAssignSupporterNameListActivity.class);
                    startActivity(i);
                    finish();
                }else   if (ActivityName.equalsIgnoreCase("SoundLevelStateWise")) {
                    Intent i = new Intent(WorkAssign_AssignActivity.this,
                            SoundLevelStateWise.class);
                    startActivity(i);
                    finish();
                }else  if (ActivityName.equalsIgnoreCase("PendingClipsStateWise")) {
                    Intent i = new Intent(WorkAssign_AssignActivity.this,
                            PendingClipsStateWise.class);
                    startActivity(i);
                    finish();
                }else  if (ActivityName.equalsIgnoreCase("NonReportedAdStatewise")) {
                    Intent i = new Intent(WorkAssign_AssignActivity.this,
                            NonReportedAdStatewise.class);
                    startActivity(i);
                    finish();
                }else if (ActivityName.equalsIgnoreCase("ConnectionStatusStatewise")) {
                    Intent i = new Intent(WorkAssign_AssignActivity.this,
                            com.stavigilmonitoring.ConnectionStatusStatewise.class);
                    startActivity(i);
                    finish();//PendingClipsStateWise
                }else if (ActivityName.equalsIgnoreCase("WorkAssignStation_ActivityDetails")) {
                    Intent i = new Intent(WorkAssign_AssignActivity.this,
                            WorkAssignStation_ActivityDetails.class);
                    i.putExtra("Type", StnName);
                    startActivity(i);
                    finish();
                }else if (ActivityName.equalsIgnoreCase("WorkAssignSupporter_ActivityDetails")) {
                    Intent i = new Intent(WorkAssign_AssignActivity.this,
                            WorkAssignSupporter_ActivityDetails.class);
                    i.putExtra("Type", IssuedToName);
                    startActivity(i);
                    finish();
                }*/
            }
        });

        BtnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //sup = editTextAssignTo.getText().toString().trim();
                sup = editTextAssignTo.getText().toString();
                if (sup.equalsIgnoreCase("")||sup.equalsIgnoreCase(Common.UserName)){
                    trnassignto = mobno;
                } else {
                    getsupportermob();
                }

                if (isvalid()) {
                    trnremark = EdtRemark.getText().toString().trim();
                    trnassigndesc = EdtDesc.getText().toString().trim();
                    remark = sp_SpinnerList.getSelectedItem().toString();
                    DBInterface dbi = new DBInterface(getApplicationContext());
                    mobno = dbi.GetPhno();

                    if (ut.isnet(parent)) {
                        asyncfetch_csnstate = new DownloadxmlsDataURL_new();
                        asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        ut.showD(parent,"nonet");
                    }
                }
            }
        });
        EdtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Date date = new Date();
                final Calendar c = Calendar.getInstance();

                Year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(parent,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                // Display Selected date in textbox
                                if (year>=Year){
                                    if((year==Year)&&(monthOfYear>=month)){
                                        if((monthOfYear==month)&&(dayOfMonth>=day)){
                                            EdtStartDate.setText(dayOfMonth + "-"
                                                    + (monthOfYear + 1) + "-" + year);
                                            trnstartDate = year + "-" + (monthOfYear + 1)
                                                    + "-" + dayOfMonth+ " 00:00:00.000";
                                        }else if((monthOfYear==month)&&(dayOfMonth<day)){
                                            EdtStartDate.setText(day + "-"
                                                    + (month + 1) + "-" + Year);
                                            trnstartDate = Year + "-" + (month + 1)
                                                    + "-" + day+ " 00:00:00.000";
                                            //2018-01-15 16:43:40.440
                                            Toast.makeText(getApplicationContext(),
                                                    "Past date is not accepted",Toast.LENGTH_SHORT).show();
                                        }else if(monthOfYear>month){
                                            EdtStartDate.setText(dayOfMonth + "-"
                                                    + (monthOfYear + 1) + "-" + year);
                                            trnstartDate = year + "-" + (monthOfYear + 1)
                                                    + "-" + dayOfMonth+ " 00:00:00.000";
                                        }
                                    }else if(year>Year){
                                        EdtStartDate.setText(dayOfMonth + "-"
                                                + (monthOfYear + 1) + "-" + year);
                                        trnstartDate = year + "-" + (monthOfYear + 1)
                                                + "-" + dayOfMonth+ " 00:00:00.000";
                                    }else if((year==Year)&&(monthOfYear<month)){
                                        EdtStartDate.setText(day + "-"
                                                + (month + 1) + "-" + Year);
                                        trnstartDate = Year + "-" + (month + 1)
                                                + "-" + day+ " 00:00:00.000";
                                        Toast.makeText(getApplicationContext(),"Past date is not accepted",Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    EdtStartDate.setText(day + "-"
                                            + (month + 1) + "-" + Year);
                                    trnstartDate = Year + "-" + (month + 1)
                                            + "-" + day+ " 00:00:00.000";
                                    Toast.makeText(getApplicationContext(),"Past date is not accepted",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, Year, month, day);
                datePickerDialog.show();

            }
        });
        EdtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Date date = new Date();
                final Calendar c = Calendar.getInstance();

                Year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(parent,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker datePicker, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Display Selected date in textbox
                                if (year>=Year){
                                    if((year==Year)&&(monthOfYear>=month)){
                                        if((monthOfYear==month)&&(dayOfMonth>=day)){
                                            EdtEndDate.setText(dayOfMonth + "-"
                                                    + (monthOfYear + 1) + "-" + year);
                                            trnendDate = year + "-" + (monthOfYear + 1)
                                                    + "-" + dayOfMonth+ " 00:00:00.000";
                                        }else if((monthOfYear==month)&&(dayOfMonth<day)){
                                            EdtEndDate.setText(day + "-"
                                                    + (month + 1) + "-" + Year);
                                            trnendDate = Year + "-" + (month + 1)
                                                    + "-" + day+ " 00:00:00.000";
                                            Toast.makeText(getApplicationContext(),"Past date is not accepted",Toast.LENGTH_SHORT).show();
                                        }else if(monthOfYear>month){
                                            EdtEndDate.setText(dayOfMonth + "-"
                                                    + (monthOfYear + 1) + "-" + year);
                                            trnendDate = year + "-" + (monthOfYear + 1)
                                                    + "-" + dayOfMonth+ " 00:00:00.000";
                                        }
                                    }else if(year>Year){
                                        EdtEndDate.setText(dayOfMonth + "-"
                                                + (monthOfYear + 1) + "-" + year);
                                        trnendDate = year + "-" + (monthOfYear + 1)
                                                + "-" + dayOfMonth+ " 00:00:00.000";
                                    }else if((year==Year)&&(monthOfYear<month)){
                                        EdtEndDate.setText(day + "-"
                                                + (month + 1) + "-" + Year);
                                        trnendDate = Year + "-" + (month + 1)
                                                + "-" + day+ " 00:00:00.000";
                                        Toast.makeText(getApplicationContext(),"Past date is not accepted",Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    EdtEndDate.setText(day + "-"
                                            + (month + 1) + "-" + Year);
                                    trnendDate = Year + "-" + (month + 1)
                                            + "-" + day+ " 00:00:00.000";
                                    Toast.makeText(getApplicationContext(),"Past date is not accepted",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, Year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void getsupportermob() {
         //   DatabaseHandler db1 = new DatabaseHandler(parent);
            SQLiteDatabase sqldb = db.getWritableDatabase();

            String assigntoname = sup.trim();
            String a = assigntoname;

        Cursor ctest = sqldb.rawQuery("Select Mobile from WorkAssignSupporter where Support ='Farhan Shaikh '", null);

        if (ctest.getCount() != 0) {
            ctest.moveToFirst();
            do {
                trnassignto = ctest.getString(0);
            } while (ctest.moveToNext());

            ctest.close();
        }

            Cursor cursor = sqldb.rawQuery("Select Mobile from WorkAssignSupporter where Support ='"+sup+"'", null);

            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                do {
                    trnassignto = cursor.getString(0);
                } while (cursor.moveToNext());

                cursor.close();
            }
    }

    private String gettodaydate() {
        String result= null;
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("d-M-yyyy");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-M-d");
        try {
            //LocalDate localDate = LocalDate.now();
            Date date2 = new Date();
            result = dateFormat1.format(date2);
            trnstartDate = dateFormat2.format(date2)+ " 00:00:00.000";

        }catch( Exception e){
            e.printStackTrace();
        }
        return result;
    }

    private String gettomorrowdate() {
        String result= null;
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("d-M-yyyy");

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-M-d");
        try {
            //LocalDate localDate = LocalDate.now();
            Date dt = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(dt);
            c.add(Calendar.DATE, 1);
            dt = c.getTime();
            result = dateFormat1.format(dt);
            trnendDate = dateFormat2.format(dt)+ " 00:00:00.000";
        }catch( Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public class DownloadxmlsDataURL_new extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();/*
            btnRefresh.setVisibility(View.GONE);
            mprogressBar.setVisibility(View.VISIBLE);*/

            progressDialog = new ProgressDialog(WorkAssign_AssignActivity.this);
            progressDialog.setMessage("Processing...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }
        @Override
        protected String doInBackground(String... params) {
            com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
            String url;
            url ="http://vritti.co/imedia/STA_Announcement/DMcertificate.asmx/AssignWorkActivities?"+"InstallationId="
                    + installationid
                    + "&AssignTo="
                    + trnassignto
                    + "&ActivityDescription="
                    + trnassigndesc
                    + "&StartDate="
                    + trnstartDate
                    + "&EndDate="
                    + trnendDate
                    + "&AssignedBy="
                    + mobno
                    + "&Remark="
                    + trnremark;
            url = url.replaceAll(" ", "%20");

            try {
                responsemsg = ut.httpGet(url);
                Log.e("Adhicha response",responsemsg);
            } catch (NullPointerException e) {
                responsemsg = "Error";
                e.printStackTrace();
                SimpleDateFormat dff = new SimpleDateFormat("HH:mm:ss");
                String Ldate = dff.format(new Date());

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

                responsemsg = "Error";

                SimpleDateFormat dff = new SimpleDateFormat("HH:mm:ss");
                String Ldate = dff.format(new Date());

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
            return responsemsg;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                Log.e("Post madhe responsemsg", responsemsg);
                progressDialog.dismiss();
                if (responsemsg.equals("Error")) {
                    ut.showD(parent,"Error");
                } else if (responsemsg.equals("Not saved")) {
                    ut.showD(parent,"Error");
                } else if (responsemsg.contains("Already exists")) {
                    flag=2;
                    async_new = null;
                    async_new = new DownloadnetWork_New();
                    async_new.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    showD("Already exists");
                } else if (responsemsg.contains(">OK<")) {
                    flag = 5;
                    async_new = null;
                    async_new = new DownloadnetWork_New();
                    async_new.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    showD(">OK<");

                   // mprogressBar.setVisibility(View.GONE);
                }


            } catch (Exception e) {
                e.printStackTrace();
                SimpleDateFormat dff = new SimpleDateFormat("HH:mm:ss");
                String Ldate = dff.format(new Date());

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
    }

    public class DownloadnetWork_New extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String sumdata2;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String Url = "http://vritti.co/imedia/STA_Announcement/DmCertificate.asmx/GetWorkAssignList?Mobile="
                    + mobno;
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
               // DatabaseHandler db = new DatabaseHandler(getBaseContext());
                SQLiteDatabase sql = db.getWritableDatabase();
                String columnName, columnValue;
               // sql.execSQL("DROP TABLE IF EXISTS WorkAssignedTable");
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
                    sql.insert("WorkAssignedTable",
                            null, values1);
                }

            //    cur.close();
            //    sql.close();
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
            progressDialog = new ProgressDialog(WorkAssign_AssignActivity.this);
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
                    Log.e("Tag", " ******* WORKING ON ALERTCOUNT *********");

                    ut.showD(WorkAssign_AssignActivity.this, ">OK<");

                } else if(sop.equals("nodata")){

                    ut.showD(WorkAssign_AssignActivity.this, ">OK<");
                } else {
                    ut.showD(WorkAssign_AssignActivity.this, "invalid");
                }

            }catch(Exception e){
                e.printStackTrace();
                StackTraceElement l = new Exception().getStackTrace()[0];

                ut =new com.stavigilmonitoring.utility();
                if(!ut.checkErrLogFile()){
                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()){
                    ut.addErrLog(l.getClassName()+"/"+l.getMethodName()+":"+l.getLineNumber()+"  "+e.getMessage()+ " ");
                }
            }
        }

    }

    private void GetSupporterList() {
        if (ut.isnet(parent)) {
            new UpdateUsers().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (dbvalueforspinner()) {
            updateCustomerSpinner();
        }
    }

    private void updateCustomerSpinner() {
        NameList.clear();
       // DatabaseHandler db1 = new DatabaseHandler(parent);
        SQLiteDatabase sqldb = db.getWritableDatabase();

        Cursor cursor = sqldb.rawQuery(
                "Select Support from WorkAssignSupporter order by Support ASC", null);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                NameList.add(cursor.getString(0));
            } while (cursor.moveToNext());

            cursor.close();
            //db.close();
            //db1.close();
        }

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(parent,
                android.R.layout.simple_list_item_1, NameList);
        editTextAssignTo.setThreshold(1);
        editTextAssignTo.setAdapter(adapter1);

        editTextAssignTo.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                editTextAssignTo.showDropDown();
                return false;
            }
        });

    }

    protected void showD(String string) {
        // TODO Auto-generated method stub

        final Dialog myDialog = new Dialog(WorkAssign_AssignActivity.this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoginfosmall);
        myDialog.setCancelable(true);

        TextView txt = (TextView) myDialog
                .findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall);
        if (string.equals("empty")) {
            myDialog.setTitle("Error...");
            txt.setText("Please Fill required data..");
        } else if (string.equals("Already exists")) {
            myDialog.setTitle("Error...");
            txt.setText("Activity already exist");
        } else if (string.equals("nonet")) {
            myDialog.setTitle("Error...");
            txt.setText("No Internet Connection Found.Please Activate internet Connectin on Device..");
        } else if (string.equals("invalid")) {
            myDialog.setTitle(" ");
            txt.setText("No Refresh Data Available.Please check internet connection...");
        } else if (string.equals("Error")) {
            myDialog.setTitle(" ");
            txt.setText("Server Error.. Please try after some time");
        } else if (string.equals("Done")) {
            myDialog.setTitle(" ");
            txt.setText("Alert send successfully");
        } else if (string.equals(">OK<")) {
            myDialog.setTitle(" ");
            txt.setText("Activity assigned");
        }

        Button btn = (Button) myDialog.findViewById(com.stavigilmonitoring.R.id.gotobtndialoginfosmall);
        btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                myDialog.dismiss();
                if (flag == 5){
                    Intent intent = new Intent(WorkAssign_AssignActivity.this,WorkAssignSupporterNameListActivity.class);
                    startActivity(intent);
                    WorkAssign_AssignActivity.this.finish();
                }

            }
        });

        myDialog.show();

    }

    private boolean dbvalueforspinner() {
        try {
           // DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
            SQLiteDatabase sql = db.getWritableDatabase();
            Cursor cursor = sql.rawQuery("SELECT * FROM WorkAssignSupporter", null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.close();	sql.close();	//db1.close();
                return true;
            } else {
                cursor.close();	sql.close();	//db1.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void initView() {
        // TODO Auto-generated method stub
        parent = WorkAssign_AssignActivity.this;
        NameList = new ArrayList<String>();
        ut = new utility();
        tvhead = (TextView) findViewById(com.stavigilmonitoring.R.id.stationInvent);
        btnRefresh =(ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_invent);
        btnRefresh.setVisibility(View.GONE);
        mprogressBar = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressinvent);
        BtnSave = (Button) findViewById(com.stavigilmonitoring.R.id.button_save);
        editTextAssignTo = (AutoCompleteTextView) findViewById(com.stavigilmonitoring.R.id.editTextSupporter);
        EdtStartDate = (Button) findViewById(com.stavigilmonitoring.R.id.ed_startdate);
        EdtEndDate = (Button) findViewById(com.stavigilmonitoring.R.id.ed_enddate);
        EdtStartDate.setText(gettodaydate());
        EdtEndDate.setText(gettomorrowdate());
        EdtSTN = (Button) findViewById(com.stavigilmonitoring.R.id.editTextSTNName);
        EdtDesc = (EditText) findViewById(com.stavigilmonitoring.R.id.editTextActivityDesc);
        EdtRemark = (EditText) findViewById(com.stavigilmonitoring.R.id.editTextRemark);
        BtnRtn = (Button) findViewById(com.stavigilmonitoring.R.id.button_return);
        BtnDelete = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_invent_delete);
        BtnDelete.setVisibility(View.GONE);
        sp_SpinnerList = (Spinner) findViewById(com.stavigilmonitoring.R.id.sp_SpinnerList);
        sp_SpinnerList.setVisibility(View.GONE);
        SpinnerList = new ArrayList<String>();

        Intent intent = getIntent();
        ActivityName = intent.getStringExtra("Activity");
        if (ActivityName.equalsIgnoreCase("WorkAssignStation_ActivityDetails")) {
            StnName = intent.getStringExtra("Type");
        }else if (ActivityName.equalsIgnoreCase("WorkAssignSupporter_ActivityDetails")) {
            IssuedToName = intent.getStringExtra("Type");
        }

        db = new DatabaseHandler(parent);
        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();
        editTextAssignTo.setText(IssuedToName);
    }

    class UpdateUsers extends AsyncTask<Void, Void, String> {
        ProgressDialog progressDialog;
        String exceptionString = "ok";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(parent);
            progressDialog.setMessage("Updating database...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            String Url = "http://vritti.co/imedia/STA_Announcement/DmCertificate.asmx/GetWorkAssignSupporter?Mobile=" + mobno;
            Url = Url.replaceAll(" ", "%20");
            try {
                resposmsg = ut.httpGet(Url);
                Log.e("Response", resposmsg);
            } catch (IOException e) {
                sop = "ServerError";
                e.printStackTrace();

            }

            if (resposmsg.contains("Record are not Found...!")) {
                sop = "nodata";
               // DatabaseHandler db = new DatabaseHandler(getBaseContext());
                SQLiteDatabase sql = db.getWritableDatabase();
                sql.execSQL("Delete from WorkAssignSupporter");

            } else if (resposmsg.contains("<UserMasterId>")) {
                sop = "valid";
               // DatabaseHandler db = new DatabaseHandler(getBaseContext());
                SQLiteDatabase sql = db.getWritableDatabase();
                String columnName, columnValue;
                //sql.execSQL("DROP TABLE IF EXISTS WorkAssignSupporter");
                //sql.execSQL(ut.getWorkAssignSupporter());
                sql.delete("WorkAssignSupporter",null,null);

                Cursor cur1 = sql.rawQuery("SELECT * FROM WorkAssignSupporter", null);
                int count = cur1.getCount();
                ContentValues values2 = new ContentValues();
                NodeList nl2 = ut.getnode(resposmsg, "Table1");

                for (int i = 0; i < nl2.getLength(); i++) {
                    Element e = (Element) nl2.item(i);
                    for (int j = 0; j < cur1.getColumnCount(); j++) {
                        columnName = cur1.getColumnName(j);
                        columnValue = ut.getValue(e, columnName);

                        values2.put(columnName, columnValue);
                    }
                    sql.insert("WorkAssignSupporter", null, values2);
                }
                cur1.close();
                sql.close();
                //db.close();

            } else {
                sop = "invalid";
            }

            return sop;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                if (sop == "valid") {
                    updateCustomerSpinner();
                }
                progressDialog.dismiss();

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Common.WorkAssignStn1) {
            try{
                String MaterialStation = data.getStringExtra("StatioName");
                EdtSTN.setText(MaterialStation);
                stationid=data.getStringExtra("StatioNameID");
                mType = MaterialStation;
                installationid = stationid;
            }catch (Exception e){
                e.printStackTrace();
                EdtSTN.setText("");
                stationid="";
                mType = "";
                installationid = stationid;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (ActivityName.equalsIgnoreCase("WorkAssignSupporterNameListActivity")) {
            /*Intent i = new Intent(WorkAssign_AssignActivity.this,
                    WorkAssignSupporterNameListActivity.class);
            startActivity(i);*/
            finish();
        }else if (ActivityName.equalsIgnoreCase("ConnectionStatusStatewise")) {
            /*Intent i = new Intent(WorkAssign_AssignActivity.this,
                    ConnectionStatusStatewise.class);
            startActivity(i);*/
            finish();
        }else if (ActivityName.equalsIgnoreCase("PendingClipsStateWise")) {
           /* Intent i = new Intent(WorkAssign_AssignActivity.this,
                    WorkAssignSupporterNameListActivity.class);
            startActivity(i);*/
            finish();
        }else if (ActivityName.equalsIgnoreCase("WorkAssignStation_ActivityDetails")) {
            /*Intent i = new Intent(WorkAssign_AssignActivity.this,
                    WorkAssignStation_ActivityDetails.class);
            i.putExtra("Type", StnName);
            startActivity(i);*/
            finish();
        }else if (ActivityName.equalsIgnoreCase("WorkAssignSupporter_ActivityDetails")) {
           /* Intent i = new Intent(WorkAssign_AssignActivity.this,
                    WorkAssignSupporter_ActivityDetails.class);
            i.putExtra("Type", IssuedToName);
            startActivity(i);*/
            finish();
        }

    }

}
