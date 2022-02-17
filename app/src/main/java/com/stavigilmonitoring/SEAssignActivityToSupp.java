package com.stavigilmonitoring;

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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adapters.SuppStationAdapter;
import com.beanclasses.SuppStationBean;
import com.database.DBInterface;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SEAssignActivityToSupp extends AppCompatActivity {
    private Context parent;
    TextView txtdesc,txtsupporter,txtstation;
    EditText edtremark;
    Button btnadd,btnassign;
    ListView listsupporters;
    DatabaseHandler db;
    SQLiteDatabase sql;
    String mobno = "",stationid = "",mType = "",installationid = "";
    ArrayList<SuppStationBean> listData;
    SuppStationAdapter adapter;
    int NETWORK = 2;
    int STATION = 1;
    String NetworkCode = "",SubNetwork = "",SupporterFullName = "",supporterMob = "",StationName = "",InstallationId = "";
    String trnstartDate,trnendDate,trnassignto, trnassigndesc,trnremark="testing",remark="Remote",
            Supp_id = "", Supp_Name = "", Supp_Mobile = "", installationId = "";
    utility ut;
    int flag = 0;
    int actAssignCnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_seassign_supp);

        init();
        setListeners();
    }

    public void init(){
        parent = SEAssignActivityToSupp.this;

        txtdesc = findViewById(R.id.txtdesc);
        txtsupporter = findViewById(R.id.txtsupporter);
        txtstation = findViewById(R.id.txtstation);
        edtremark = findViewById(R.id.edtremark);
        btnadd = findViewById(R.id.btnadd);
        btnassign = findViewById(R.id.btnassign);
        listsupporters = findViewById(R.id.listsupporters);

        Intent intent = getIntent();
        trnassigndesc = intent.getStringExtra("AdvDesc");
        trnstartDate = ConvertDate(intent.getStringExtra("StartDate"));
        trnendDate = ConvertDate(intent.getStringExtra("EndDate"));

        txtdesc.setText(trnassigndesc);

        db = new DatabaseHandler(parent);
        sql = db.getWritableDatabase();

        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();

        ut = new utility();

        listData = new ArrayList<SuppStationBean>();
        /*adapter = new SuppStationAdapter(parent,listData);
        listsupporters.setAdapter(adapter);*/
    }

    public void setListeners(){

        btnassign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate get startdte, enddate, remark, installation id and call activity

                installationId = listData.get(0).getInstallationId();
                Supp_Mobile = listData.get(0).getMobile();
                trnremark = edtremark.getText().toString().trim();

                new AssignActivityToTLUrl().execute(installationId,Supp_Mobile);

            }
        });

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add record to list

                if(txtsupporter.getText().toString().equalsIgnoreCase("") ||
                txtsupporter.getText().toString().equalsIgnoreCase(null) ||
                txtstation.getText().toString().equalsIgnoreCase("") ||
                txtstation.getText().toString().equalsIgnoreCase(null) ){
                    Toast.makeText(parent,"Please fill details",Toast.LENGTH_SHORT).show();
                }else {
                    SuppStationBean suppBean = new SuppStationBean();
                    suppBean.setInstallationdesc(txtstation.getText().toString());
                    suppBean.setSubnetworkcode(txtsupporter.getText().toString());
                    suppBean.setNetworkCOde(NetworkCode);
                    suppBean.setInstallationId(InstallationId);
                    //suppBean.setSupporterId(SupporterFullName);
                    suppBean.setMobile(supporterMob);

                    listData.add(suppBean);
                    adapter = new SuppStationAdapter(parent,listData);
                    listsupporters.setAdapter(adapter);
                }
            }
        });

        txtsupporter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtstation.setText("");

                Intent intent = new Intent(parent, NetworkListActivity.class);
                intent.putExtra("mobileno", mobno);
                startActivityForResult(intent, NETWORK);
            }
        });

        txtstation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(parent, workassignStateselectActivity.class);
                Intent intent = new Intent(parent, SupporterWiseStationsListActivity.class);
                intent.putExtra("Type", NetworkCode);
                intent.putExtra("SubNW", SubNetwork);
                //startActivityForResult(intent, Common.WorkAssignStn1);
                startActivityForResult(intent, STATION);
            }
        });
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

    private void GetSupporterList() {
        if (ut.isnet(parent)) {
            new UpdateUsers().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    class UpdateUsers extends AsyncTask<Void, Void, String> {
        ProgressDialog progressDialog;
        String exceptionString = "ok";
        String resposmsg =  "", sop = "";

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
                }
                progressDialog.dismiss();

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String ConvertDate(String amcExpireDt) {
        String result = null;
        // 2017-10-30T00:00:00+05:30
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM,yyyy");
        try {
                result = dateFormat1.format(dateFormat2.parse(amcExpireDt));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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
            installationId = params[0];
            Supp_Mobile = params[1];

            com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
            String url;
            url ="http://vritti.co/imedia/STA_Announcement/DMcertificate.asmx/AssignWorkActivities?"+"InstallationId="+ installationId +
                    "&AssignTo=" + Supp_Mobile + "&ActivityDescription=" + trnassigndesc + "&StartDate=" + trnstartDate +
                    "&EndDate=" + trnendDate + "&AssignedBy=" + mobno + "&Remark=" + trnremark;
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
                    actAssignCnt++;

                    if(actAssignCnt < listData.size()){
                        //assign activity to another supporter for station get suppmob, installationid etc.
                        installationId = listData.get(actAssignCnt).getInstallationId();
                        Supp_Mobile = listData.get(actAssignCnt).getMobile();
                        trnremark = edtremark.getText().toString().trim();

                        new AssignActivityToTLUrl().execute(installationId,Supp_Mobile);
                    }else {
                        showD(">OK<");
                        new GetWorkAssignList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }

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
                    ut.showD(parent, ">OK<");

                } else if(sop.equals("nodata")){
                    ut.showD(parent, ">OK<");
                } else {
                    ut.showD(parent, "invalid");
                }

                finish();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    protected void showD(String string) {
        // TODO Auto-generated method stub

        final Dialog myDialog = new Dialog(SEAssignActivityToSupp.this);
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

    private String getSuppMobileNo(String supName) {
        String mob = "";
        String qry = "Select Mobile from WorkAssignSupporter WHERE Support like '%"+supName+"%'";
        Cursor c = sql.rawQuery(qry,null);
        if(c.getCount()>0){
            c.moveToFirst();
            do{
                mob = c.getString(c.getColumnIndex("Mobile"));
            }while (c.moveToNext());

        }
        return mob;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            if (requestCode == Common.WorkAssignStn1) {
                try{
                    String MaterialStation = data.getStringExtra("StatioName");
                    txtstation.setText(MaterialStation);
                    stationid = data.getStringExtra("StatioNameID");
                    //mType = MaterialStation;
                    installationid = stationid;
                }catch (Exception e){
                    e.printStackTrace();
                    txtstation.setText("");
                    stationid="";
                    mType = "";
                    installationid = stationid;
                }
            }else if(resultCode == NETWORK && requestCode ==  NETWORK){
                SubNetwork = data.getStringExtra("SupporterName");
                NetworkCode = data.getStringExtra("NetworkCode");
                SupporterFullName = data.getStringExtra("SupporterFullName");
                txtsupporter.setText(SubNetwork);

                supporterMob = getSuppMobileNo(SupporterFullName);
            }
            else if(resultCode == STATION && requestCode ==  STATION){
                StationName = data.getStringExtra("InstallationName");
                InstallationId = data.getStringExtra("InstallationId");
                txtstation.setText(StationName);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
