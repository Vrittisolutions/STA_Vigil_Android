package com.stavigilmonitoring;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stavigilmonitoring.utility;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DT_Analysis_Details extends Activity {
    private Context parent;
    private LinearLayout LateStart, EarlyClose, DT_instance, DT_hours;
    private TextView LSCount, ECCount, DTInstCount, DTHrCount;
    TextView tvheader;
    String NW_CodeName;
    String Type, responsemsg,sop;
    com.stavigilmonitoring.utility ut;
    String DaysInstKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.stavigilmonitoring.R.layout.activity_dt__analysis__details);

        parent = DT_Analysis_Details.this;

        Intent intent = getIntent();
        NW_CodeName = intent.getStringExtra("NetWorkName");
        Type = intent.getStringExtra("Type");
        DaysInstKey = intent.getStringExtra("DaysInstKey");

        init();

       new DownloadxmlsDataURL_new().execute();

        setListener();
    }

    public void init(){
        tvheader = (TextView)findViewById(com.stavigilmonitoring.R.id.tvheader);
        tvheader.setText("DT Analysis - "+DaysInstKey+ " Days Instance - "+NW_CodeName);
        LateStart = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.dt_latestart_layout);
        EarlyClose = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.dt_earlyclose_layout);
        DT_instance = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.dt_instance_layout);
        DT_hours = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.dt_hours_layout);
        DT_hours.setVisibility(View.GONE);

        LSCount = (TextView)findViewById(com.stavigilmonitoring.R.id.tv_LS_count);
        ECCount = (TextView)findViewById(com.stavigilmonitoring.R.id.tv_EC_count);
        DTInstCount = (TextView)findViewById(com.stavigilmonitoring.R.id.txtdtinstcnt);
        DTHrCount = (TextView)findViewById(com.stavigilmonitoring.R.id.txtdthrcnt);
    }

    public void setListener(){
        LateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(parent,"latestart clicked",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), DTA_EC_LS_StationNames.class);
                intent.putExtra("NetWorkName",NW_CodeName);
                intent.putExtra("Type", Type);
                intent.putExtra("DaysInstKey",DaysInstKey);
                intent.putExtra("LS_EC_Key","LS");
                startActivity(intent);
            }
        });

        EarlyClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(parent,"earlyclose clicked",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), DTA_EC_LS_StationNames.class);
                intent.putExtra("NetWorkName",NW_CodeName);
                intent.putExtra("Type", Type);
                intent.putExtra("DaysInstKey",DaysInstKey);
                intent.putExtra("LS_EC_Key","EC");
                startActivity(intent);
            }
        });

        DT_instance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(parent,"DT instance clicked",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), DTA_EC_LS_StationNames.class);
                intent.putExtra("NetWorkName",NW_CodeName);
                intent.putExtra("Type", Type);
                intent.putExtra("DaysInstKey",DaysInstKey);
                intent.putExtra("LS_EC_Key","DT_INSTANCE");
                startActivity(intent);
            }
        });

        DT_hours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(parent,"DT hours clicked",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), DTA_EC_LS_StationNames.class);
                intent.putExtra("NetWorkName",NW_CodeName);
                intent.putExtra("Type", Type);
                intent.putExtra("DaysInstKey",DaysInstKey);
                intent.putExtra("LS_EC_Key","DT_HR");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*Intent i = new Intent(DownTimeAnalysis.this, SelectMenu.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);*/
        finish();
    }

    public class DownloadxmlsDataURL_new extends
            AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String EC_count,LS_count, TotDisconnectCnt, TotDisconMoreThn_30MinCntInst;

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            ut = new utility();

           //down time count for EC
            String url_EC = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetDowntimeAnalysisforstationstartandclose?" +
                    "downtype=EC&noofdays="+DaysInstKey+"&NetworkCode="+NW_CodeName;

            Log.e("EC count", "url : " + url_EC);
            url_EC = url_EC.replaceAll(" ", "%20");
            try {
                responsemsg = ut.httpGet(url_EC);
                if (responsemsg.contains("<earlyclosecount>")) {
                    sop = "valid";
                    NodeList nl1 = ut.getnode(responsemsg, "TableResult");
                    for (int i = 0; i < nl1.getLength(); i++) {
                        Element e = (Element) nl1.item(i);
                        EC_count = ut.getValue(e,"earlyclosecount");
                    }

                } else {
                    sop = "invalid";
                    System.out.println("--------- invalid for project list --- ");
                    EC_count="0";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            //down time count for LS
            String url_LS = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetDowntimeAnalysisforstationstartandclose?" +
                    "downtype=LS&noofdays="+DaysInstKey+"&NetworkCode="+NW_CodeName;

            Log.e("LS count", "url : " + url_LS);
            url_LS = url_LS.replaceAll(" ", "%20");
            try {
                responsemsg = ut.httpGet(url_LS);
                if (responsemsg.contains("<latestartcount>")) {
                    sop = "valid";
                    NodeList nl1 = ut.getnode(responsemsg, "TableResult");
                    for (int i = 0; i < nl1.getLength(); i++) {
                        Element e = (Element) nl1.item(i);
                        LS_count = ut.getValue(e,"latestartcount");
                    }

                } else {
                    sop = "invalid";
                    System.out
                            .println("--------- invalid for project list --- ");
                    LS_count="0";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            //down time count for DT instance
            String url_DT_Instance =  "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetDownTimeAnalysisfroInstances?"+
                    "Netwrokcode="+NW_CodeName+"&Noofdays="+DaysInstKey;

            Log.e("EC count", "url : " + url_EC);
            url_EC = url_EC.replaceAll(" ", "%20");
            try {
                responsemsg = ut.httpGet(url_DT_Instance);
                if (responsemsg.contains("<TotaldisconnectionCount>")) {
                    sop = "valid";
                    NodeList nl1 = ut.getnode(responsemsg, "TableResult");
                    for (int i = 0; i < nl1.getLength(); i++) {
                        Element e = (Element) nl1.item(i);
                        TotDisconnectCnt = ut.getValue(e,"TotaldisconnectionCount");
                        TotDisconMoreThn_30MinCntInst = ut.getValue(e,"Totalinstancescount");
                    }

                } else {
                    sop = "invalid";
                    System.out
                            .println("--------- invalid for project list --- ");
                    TotDisconnectCnt = "0";
                    TotDisconMoreThn_30MinCntInst="0";
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

                try{
                    LSCount.setText(LS_count);
                    ECCount.setText(EC_count);
                    DTInstCount.setText(TotDisconMoreThn_30MinCntInst +"/"+TotDisconnectCnt);
                }catch (Exception e){
                    e.printStackTrace();
                }

                if (sop.equals("valid")) {
                    //disp counts
                    LSCount.setText(LS_count);
                    ECCount.setText(EC_count);
                    DTInstCount.setText(TotDisconMoreThn_30MinCntInst +"/"+TotDisconnectCnt);

                } else {
                    try{
                        ut.showD(DT_Analysis_Details.this,"invalid");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
            progressDialog.dismiss();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DT_Analysis_Details.this);
            progressDialog.setMessage("Updating database...");
            //progressDialog.setCanceledOnTouchOutside(false);
            //progressDialog.setCancelable(false);
            progressDialog.show();
            //iv.setVisibility(View.GONE);
            //((ProgressBar) findViewById(R.id.progressBar1)).setVisibility(View.VISIBLE);
        }
    }
}
