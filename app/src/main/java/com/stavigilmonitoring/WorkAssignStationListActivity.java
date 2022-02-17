package com.stavigilmonitoring;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.TextView;

import com.adapters.DMCStateAdapter;
import com.database.DBInterface;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Admin-3 on 11/13/2017.
 */

public class WorkAssignStationListActivity extends Activity {

    List<com.stavigilmonitoring.DmCstnwiseActivity.StateList> searchResults;
    GridView lstcsn;
    ImageView iv,btnfilter;
    TextView header;
    Context parent;
    String sop = "no";
    String responsemsg = "k", resposmsg ="n";

    DMCStateAdapter listAdapter;
    String network, subnetwork, InstallationId;

    String mobno;
    private com.stavigilmonitoring.utility ut;
    private static DmCRefresh asynk_new;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.stavigilmonitoring.R.layout.csnstatewise);
        initView();
        updatelist();
        setListener();
    }

    private void setListener() {
        ((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
                .addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        listAdapter
                                .filter_Station(((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
                                        .getText().toString().trim()
                                        .toLowerCase(Locale.getDefault()));
                        listAdapter.notifyDataSetChanged();
                    }
                });
        lstcsn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                /*Intent i = new Intent(WorkAssignStationListActivity.this, DmCStateStnSoNoDetails.class);
                i.putExtra("Network", network);
                i.putExtra("SubNetwork", subnetwork);
                i.putExtra("Type", searchResults.get(position).GetStateName());
                startActivity(i);
                finish();*/
                Log.e("Check List item Click", searchResults.get(position).GetStateName());
                Intent intent = new Intent(getApplicationContext(),WorkAssignStation_ActivityDetails.class);
                intent.putExtra("Type", searchResults.get(position).GetStateName());
                intent.putExtra("InstallationId", searchResults.get(position).getInstallationId());
                intent.putExtra("Activity", "WorkAssignStationListActivity");
                startActivity(intent);
                finish();
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

    private void fetchdata(){
        asynk_new = null;
        asynk_new = new DmCRefresh();
        asynk_new.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    public class DmCRefresh extends AsyncTask<String, Void, String> {
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
             //   DatabaseHandler db = new DatabaseHandler(getBaseContext());
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
                    sql.insert("WorkAssignedTable",null, values1);
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
            progressDialog = new ProgressDialog(WorkAssignStationListActivity.this);
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
                    updatelist();
                } else if(sop.equals("nodata")){
                    updatelist();
                } else {
                    ut.showD(WorkAssignStationListActivity.this, "invalid");
                }

            }catch(Exception e){
                e.printStackTrace();
                StackTraceElement l = new Exception().getStackTrace()[0];

                ut =new com.stavigilmonitoring.utility();
                if(!ut.checkErrLogFile()){
                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()){
                    ut.addErrLog(l.getClassName()+"/"+l.getMethodName()+":"+l.getLineNumber()+"  "+e.getMessage()+ " " );
                }
            }
        }
    }
    private int dbvalueDMC() {
    //    DatabaseHandler db = new DatabaseHandler(getBaseContext());
        SQLiteDatabase sql = db.getWritableDatabase();
        int count = 0;
        Cursor c = sql.rawQuery("SELECT * FROM WorkAssignedTable", null);
        if (c.getCount() > 0) {
            count = c.getCount();
        }
        return count;
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


    private void initView() {
        parent = WorkAssignStationListActivity.this;
        lstcsn =  findViewById(com.stavigilmonitoring.R.id.lstcsn);
        iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_nonrepeated_main);
        btnfilter = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_filter);
        btnfilter.setVisibility(View.VISIBLE);
        header = (TextView) findViewById(com.stavigilmonitoring.R.id.header);
        header.setText("Activities Stations wise");
        searchResults = new ArrayList<com.stavigilmonitoring.DmCstnwiseActivity.StateList>();
        Intent intent = getIntent();
        subnetwork = intent.getStringExtra("Type");
        network = intent.getStringExtra("Network");
        ut = new utility();

        db = new DatabaseHandler(getBaseContext());
        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(parent, WorkAssignSubNetworkListActivity.class);
        i.putExtra("Type", network);
        startActivity(i);
        finish();
    }

    private void updatelist() {
        searchResults.clear();
      //  DatabaseHandler db = new DatabaseHandler(this);
        SQLiteDatabase sql = db.getWritableDatabase();
        Cursor c = sql.rawQuery(
                "SELECT Distinct StationName FROM WorkAssignedTable WHERE NetworkCode='"
                        +network+"' and SubNetworkCode='"
                        + subnetwork
                        + "' ORDER BY ActualEndDate", null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                int stncnt = 0,overduecnt=0;
                String Type = c.getString(0);

                Cursor c1 = sql
                        .rawQuery(
                                "SELECT ActivityId, ActualEndDate,InstallationId FROM WorkAssignedTable WHERE NetworkCode='"
                                        +network+"' and SubNetworkCode='"
                                        +subnetwork+"' and StationName='"
                                        + c.getString(0)
                                        + "' ORDER BY ActualEndDate", null);
                if (c1.getCount() > 0) {
                    c1.moveToFirst();
                    do {
                        String column1 = c1.getString(c1.getColumnIndex("ActualEndDate"));
                        InstallationId = c1.getString(c1.getColumnIndex("InstallationId"));
                        if (checkOverdue(column1)){
                            overduecnt = overduecnt + 1;
                        }
                        stncnt = stncnt + 1;

                    } while (c1.moveToNext());
                }
                if (!Type.trim().equalsIgnoreCase("")) {
                    com.stavigilmonitoring.DmCstnwiseActivity.StateList sitem = new DmCstnwiseActivity.StateList();
                    sitem.SetStateName(Type);
                    sitem.SetCount(stncnt);
                    sitem.setInstallationId(InstallationId);
                    sitem.SetOverdueCnt(overduecnt);
                    searchResults.add(sitem);
                }
            } while (c.moveToNext());

        }
        listAdapter = new DMCStateAdapter(parent, searchResults);
        lstcsn.setAdapter(listAdapter);
        runOnUiThread(new Runnable() {
            public void run() {
                listAdapter.notifyDataSetChanged();
            }});
    }

    private boolean checkOverdue(String amcExpireDt) {
        String[] parts = amcExpireDt.split("T");
        amcExpireDt = parts[0];
        boolean result = false;
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date2 = dateFormat2.parse(amcExpireDt);
            Date date = new Date();
            String res = dateFormat2.format(date);
            date = dateFormat2.parse(res);
            if (date2.equals(date)){
                result = false;
            } else if (date.after(date2)){
                result = true;
            } else if (date2.after(date)){
                result = false;
            }
        }catch( Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
