package com.stavigilmonitoring;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.database.DBInterface;
import com.stavigilmonitoring.utility;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;

/**
 * Created by Admin-3 on 11/8/2017.
 */

public class WorkAssigncategorizeActivity extends Activity {
    Context parent;
    LinearLayout  actstnwise, actsonowise, wrkassnplan, lay_wrkplanoptions,actsewise;
    TextView  tvstnwise, tvsonowise,txtwrkassn, header;
    private static DownloadnetWork_New asynk_new;
    private ProgressBar mprogressBar;
    private ImageView btnrefresh;
    private com.stavigilmonitoring.utility ut;
    String mobno, resposmsg="m", sop="invalid";
    Button btn_wrkplnstnwise, btn_wrkplnsowise;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.stavigilmonitoring.R.layout.activity_workassign_workplan);

        initViews();
        setListeners();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*Intent i = new Intent(WorkAssigncategorizeActivity.this, SelectMenu.class);
        startActivity(i);*/
        finish();
    }

    private void setListeners() {
        actstnwise.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), WorkAssignNetworkNameListActivity.class);
                startActivity(intent);
               // finish();
            }
        });

        actsonowise.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), WorkAssignSupporterNameListActivity.class);
                startActivity(intent);
               // finish();
            }
        });

        actsewise.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), SEWiseActivity.class);
                startActivity(intent);
            }
        });

        wrkassnplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lay_wrkplanoptions.setVisibility(View.VISIBLE);
            }
        });

        btn_wrkplnstnwise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(parent,"Station wise btn clicked",Toast.LENGTH_SHORT).show();
                Intent i_stnwise = new Intent(parent, WorkAssign_Networks.class);
                i_stnwise.putExtra("ButtonClickKey","StationWiseWorkPlan");
                startActivity(i_stnwise);
                lay_wrkplanoptions.setVisibility(View.GONE);
            }
        });

        btn_wrkplnsowise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(parent,"SO wise btn clicked",Toast.LENGTH_SHORT).show();
                Intent i_stnwise = new Intent(parent, WorkAssign_Networks.class);
                i_stnwise.putExtra("ButtonClickKey","SOWiseWorkPlan");
                startActivity(i_stnwise);
                lay_wrkplanoptions.setVisibility(View.GONE);
            }
        });

        btnrefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                fetchdata();
            }
        });
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
              //  DatabaseHandler db = new DatabaseHandler(getBaseContext());
                SQLiteDatabase sql = db.getWritableDatabase();
                String columnName, columnValue;
                //sql.execSQL("DROP TABLE IF EXISTS WorkAssignedTable");
                //sql.execSQL(ut.getWorkAssignList());
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

           //     cur.close();
           //     sql.close();
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
            progressDialog = new ProgressDialog(WorkAssigncategorizeActivity.this);
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
                } else if(sop.equals("nodata")){
                } else {
                    try{
                        ut.showD(WorkAssigncategorizeActivity.this, "invalid");
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
                    ut.addErrLog(l.getClassName()+"/"+l.getMethodName()+":"+l.getLineNumber()+"  "+e.getMessage()+ " ");
                }
            }
        }
    }

    private int dbvalueDMC() {
       // DatabaseHandler db = new DatabaseHandler(getBaseContext());
        SQLiteDatabase sql = db.getWritableDatabase();
        int count = 0;
        Cursor c = sql.rawQuery("SELECT * FROM DmCertificateTable", null);
        if (c.getCount() > 0) {
            count = c.getCount();
        }
        return count;
    }

    private void initViews() {
        parent = WorkAssigncategorizeActivity.this;

        header = (TextView) findViewById(com.stavigilmonitoring.R.id.tvheader);
        header.setText("Work Assign");

        actstnwise = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.actstnwise);
        actsonowise = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.actsonowise);
        actsewise = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.actsewise);
        btnrefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_alert);
        mprogressBar = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressinvent1);

        wrkassnplan = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.wrkassnplan);
        wrkassnplan.setVisibility(View.VISIBLE);
        lay_wrkplanoptions = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.lay_wrkplanoptions);
        lay_wrkplanoptions.setVisibility(View.GONE);

        btn_wrkplnstnwise = (Button)findViewById(com.stavigilmonitoring.R.id.btn_wrkplnstnwise);
        btn_wrkplnsowise = (Button)findViewById(com.stavigilmonitoring.R.id.btn_wrkplnsowise);
        ut = new utility();

        db = new DatabaseHandler(getBaseContext());
        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();
    }
}
