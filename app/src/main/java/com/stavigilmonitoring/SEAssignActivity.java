package com.stavigilmonitoring;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beanclasses.reporteeBean;
import com.database.DBInterface;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SEAssignActivity extends AppCompatActivity {
    private Context parent;
    AutoCompleteTextView edt_tlname;
    TextView txtactivitydesc;
    EditText edt_count,edt_advname,edtremark;
    Button ed_startdate,ed_enddate,button_save,button_cancel;
    DatabaseHandler db;
    SQLiteDatabase sql;
    ArrayList<String> list_Tls;
    ArrayList<reporteeBean> list_TLeaders;
    static int Year, month, day;
    utility ut;
    String trnstartDate,trnendDate,trnassignto, trnassigndesc,trnremark="testing",remark="Remote",mobno = "",
            TL_id = "", TL_Name = "", TL_Mobile = "", installationId = "";
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_seassign);

        init();

        if(getTLCount() > 0){
            getDataFromDatabase();
        }else {
            if(isnet()){
                new DownloadxmlsDataURL_new().execute();
            }else {
                Toast.makeText(parent,"No network available",Toast.LENGTH_SHORT).show();
            }
        }
        setListeners();
    }

    public void init(){
        parent = SEAssignActivity.this;

        txtactivitydesc = findViewById(R.id.txtactivitydesc);
        edt_tlname = findViewById(R.id.edt_tlname);
        edt_count = findViewById(R.id.edt_count);
        edt_advname = findViewById(R.id.edt_advname);
        edtremark = findViewById(R.id.edtremark);
        ed_startdate = findViewById(R.id.ed_startdate);
        ed_enddate = findViewById(R.id.ed_enddate);
        button_save = findViewById(R.id.button_save);
        button_cancel = findViewById(R.id.button_cancel);

        db = new DatabaseHandler(getBaseContext());
        sql = db.getWritableDatabase();
        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();

        list_Tls = new ArrayList<String>();
        list_TLeaders = new ArrayList<reporteeBean>();

        ut = new utility();
    }

    public void setListeners(){

        edt_tlname.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edt_tlname.showDropDown();
                return false;
            }
        });


        edt_tlname.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TL_id = list_TLeaders.get(position).getReporteeID();
                TL_Name = list_TLeaders.get(position).getReporteeName();

                TL_Mobile = getTlMobileNo(TL_id);

                edt_tlname.setText(TL_Name);
            }
        });

       /* edt_tlname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TL_id = list_TLeaders.get(position).getReporteeID();
                TL_Name = list_TLeaders.get(position).getReporteeName();

                TL_Mobile = getTlMobileNo(TL_id);

                edt_tlname.setText(TL_Name);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //assign activity
                if (isvalid()) {
                    trnremark = edtremark.getText().toString().trim();
                    trnassigndesc = "Video recording for - "+edt_advname.getText().toString().trim();
                    remark = edtremark.getText().toString();

                    if (ut.isnet(parent)) {
                        new AssignActivityToTLUrl().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        ut.showD(parent,"nonet");
                    }
                }
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ed_startdate.setOnClickListener(new View.OnClickListener() {
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
                                            ed_startdate.setText(dayOfMonth + "-"
                                                    + (monthOfYear + 1) + "-" + year);
                                            trnstartDate = year + "-" + (monthOfYear + 1)
                                                    + "-" + dayOfMonth+ " 00:00:00.000";
                                        }else if((monthOfYear==month)&&(dayOfMonth<day)){
                                            ed_startdate.setText(day + "-"
                                                    + (month + 1) + "-" + Year);
                                            trnstartDate = Year + "-" + (month + 1)
                                                    + "-" + day+ " 00:00:00.000";
                                            //2018-01-15 16:43:40.440
                                            Toast.makeText(getApplicationContext(),
                                                    "Past date is not accepted",Toast.LENGTH_SHORT).show();
                                        }else if(monthOfYear>month){
                                            ed_startdate.setText(dayOfMonth + "-"
                                                    + (monthOfYear + 1) + "-" + year);
                                            trnstartDate = year + "-" + (monthOfYear + 1)
                                                    + "-" + dayOfMonth+ " 00:00:00.000";
                                        }
                                    }else if(year>Year){
                                        ed_startdate.setText(dayOfMonth + "-"
                                                + (monthOfYear + 1) + "-" + year);
                                        trnstartDate = year + "-" + (monthOfYear + 1)
                                                + "-" + dayOfMonth+ " 00:00:00.000";
                                    }else if((year==Year)&&(monthOfYear<month)){
                                        ed_startdate.setText(day + "-"
                                                + (month + 1) + "-" + Year);
                                        trnstartDate = Year + "-" + (month + 1)
                                                + "-" + day+ " 00:00:00.000";
                                        Toast.makeText(getApplicationContext(),"Past date is not accepted",Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    ed_startdate.setText(day + "-"
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

        ed_enddate.setOnClickListener(new View.OnClickListener() {
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
                                            ed_enddate.setText(dayOfMonth + "-"
                                                    + (monthOfYear + 1) + "-" + year);
                                            trnendDate = year + "-" + (monthOfYear + 1)
                                                    + "-" + dayOfMonth+ " 00:00:00.000";
                                        }else if((monthOfYear==month)&&(dayOfMonth<day)){
                                            ed_enddate.setText(day + "-"
                                                    + (month + 1) + "-" + Year);
                                            trnendDate = Year + "-" + (month + 1)
                                                    + "-" + day+ " 00:00:00.000";
                                            Toast.makeText(getApplicationContext(),"Past date is not accepted",Toast.LENGTH_SHORT).show();
                                        }else if(monthOfYear>month){
                                            ed_enddate.setText(dayOfMonth + "-"
                                                    + (monthOfYear + 1) + "-" + year);
                                            trnendDate = year + "-" + (monthOfYear + 1)
                                                    + "-" + dayOfMonth+ " 00:00:00.000";
                                        }
                                    }else if(year>Year){
                                        ed_enddate.setText(dayOfMonth + "-"
                                                + (monthOfYear + 1) + "-" + year);
                                        trnendDate = year + "-" + (monthOfYear + 1)
                                                + "-" + dayOfMonth+ " 00:00:00.000";
                                    }else if((year==Year)&&(monthOfYear<month)){
                                        ed_enddate.setText(day + "-"
                                                + (month + 1) + "-" + Year);
                                        trnendDate = Year + "-" + (month + 1)
                                                + "-" + day+ " 00:00:00.000";
                                        Toast.makeText(getApplicationContext(),"Past date is not accepted",Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    ed_enddate.setText(day + "-"
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

    private String getTlMobileNo(String tl_id) {
        String mob = "";
        String qry = "Select Mobile from WorkAssignSupporter WHERE UserMasterId='"+tl_id+"'";
        Cursor c = sql.rawQuery(qry,null);
        if(c.getCount()>0){
            c.moveToFirst();
            do{
                mob = c.getString(c.getColumnIndex("Mobile"));
            }while (c.moveToNext());

        }
        return mob;
    }

    public int getTLCount(){
        int cnt = 0;
       String qry = "Select * from TeamLeaders";
        Cursor c = sql.rawQuery(qry,null);
        if(c.getCount()>0){
            c.moveToFirst();
            cnt = c.getCount();
        }

        return cnt;
    }

    public void getDataFromDatabase(){

        if(list_TLeaders.size() > 0){
            list_TLeaders.clear();
            list_Tls.clear();
        }

        String qry = "Select * from TeamLeaders";
        Cursor c = sql.rawQuery(qry,null);
        if(c.getCount()>0){
            c.moveToFirst();
            do{
                String tlName = c.getString(c.getColumnIndex("username"));
                String tl_Id = c.getString(c.getColumnIndex("usermasterid"));

                reporteeBean bean = new reporteeBean();
                bean.setReporteeID(tl_Id);
                bean.setReporteeName(tlName);

                list_TLeaders.add(bean);
                list_Tls.add(tlName);

            }while (c.moveToNext());

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(parent,android.R.layout.simple_list_item_1,list_Tls);
            edt_tlname.setAdapter(adapter);
        }
    }

    protected boolean isnet() {
        // TODO Auto-generated method stub
        Context context = this.getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public class DownloadxmlsDataURL_new extends AsyncTask<String, Void, String> {
        String responsemsg = "", sop = "", username = "", usermasterid = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            com.stavigilmonitoring.utility ut = new utility();

            sop = "valid";

            String url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetTLName";

            Log.e("csn status", "url : " + url);
            url = url.replaceAll(" ", "%20");

            try {
                responsemsg = ut.httpGet(url);

                if (responsemsg.contains("<usermasterid>")) {
                    sop = "valid";
                    NodeList nl1 = ut.getnode(responsemsg, "TableResult");

                    for (int i = 0; i < nl1.getLength(); i++) {

                        Element e = (Element) nl1.item(i);
                        username = ut.getValue(e,"username");
                        usermasterid = ut.getValue(e,"usermasterid");

                        db.addTeamLeaders(username,usermasterid);
                        Log.e("tabledata", String.valueOf(i));
                    }
                } else {
                    sop = "invalid";
                    System.out
                            .println("--------- invalid for project list --- ");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return sop;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (sop.equals("valid")) {
                    //display list to adapter
                   getDataFromDatabase();

                } else {
                   // ut.showD(parent,"nodata");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class AssignActivityToTLUrl extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String responsemsg = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();/*
            btnRefresh.setVisibility(View.GONE);
            mprogressBar.setVisibility(View.VISIBLE);*/

            progressDialog = new ProgressDialog(parent);
            progressDialog.setMessage("Processing...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
            String url;
            url ="http://vritti.co/imedia/STA_Announcement/DMcertificate.asmx/AssignWorkActivities?"+"InstallationId="+ "&AssignTo=" + TL_Mobile
                    + "&ActivityDescription=" + trnassigndesc + "&StartDate=" + trnstartDate + "&EndDate=" + trnendDate
                    + "&AssignedBy=" + mobno + "&Remark=" + trnremark;
            url = url.replaceAll(" ", "%20");

            try {
                responsemsg = ut.httpGet(url);
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
                    new GetWorkAssignList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    showD("Already exists");
                } else if (responsemsg.contains(">OK<")) {
                    flag = 5;
                    new GetWorkAssignList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    public class GetWorkAssignList extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String sumdata2 = "", resposmsg = "", sop =  "";

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
                SQLiteDatabase sql = db.getWritableDatabase();
                String columnName, columnValue;
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
                    Log.e("Tag", " ******* WORKING ON ALERTCOUNT *********");
                    ut.showD(parent, ">OK<");

                } else if(sop.equals("nodata")){
                    ut.showD(parent, ">OK<");
                } else {
                    ut.showD(parent, "invalid");
                }

                finish();
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

    protected void showD(String string) {
        // TODO Auto-generated method stub

        final Dialog myDialog = new Dialog(SEAssignActivity.this);
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
                finish();
                /*if (flag == 5){
                    Intent intent = new Intent(parent,WorkAssignSupporterNameListActivity.class);
                    startActivity(intent);
                    WorkAssign_AssignActivity.this.finish();
                }*/

            }
        });

        myDialog.show();

    }

    protected boolean isvalid() {
        // TODO Auto-generated method stub
        if (!(edt_tlname.getText().toString().length() > 0)) {
            Toast.makeText(parent, "Please select Team Leader", Toast.LENGTH_LONG).show();
            return false;
        } else if (!(TL_Mobile.length() > 0)) {
            Toast.makeText(parent, "TL mobile not saved", Toast.LENGTH_LONG).show();
            return false;
        }else if (!(ed_startdate.getText().toString().length() > 0)) {
            Toast.makeText(parent, "Please select Start date", Toast.LENGTH_LONG).show();
            return false;
        } else if (!(ed_enddate.getText().toString().length() > 0)) {
            Toast.makeText(parent, "Please select End date.", Toast.LENGTH_LONG).show();
            return false;
        }else if (!(edt_advname.getText().toString().length() > 0)) {
            Toast.makeText(parent, "Please enter advertisement name", Toast.LENGTH_LONG).show();

            return false;
        }else{
            return true;
        }
    }

}
