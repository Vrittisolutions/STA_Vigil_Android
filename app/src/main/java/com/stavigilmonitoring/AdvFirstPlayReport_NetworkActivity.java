package com.stavigilmonitoring;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adapters.SupporterEnuiryAdptr;
import com.beanclasses.SupportEnquiryHelper;
import com.database.DBInterface;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.stavigilmonitoring.WorkAssign_AssignActivity.Year;
import static com.stavigilmonitoring.WorkAssign_AssignActivity.day;
import static com.stavigilmonitoring.WorkAssign_AssignActivity.month;

public class AdvFirstPlayReport_NetworkActivity extends Activity {
    private Context parent;

    GridView listnw;
    ImageView mRefresh;
    ProgressBar mprogress;
    DatabaseHandler db;
    SQLiteDatabase sqlDb;
    String resposmsg, sop,mobno;
    private utility ut;
    private static DownloadnetWork asynk;
    private ArrayList<SupportEnquiryHelper> mSearchlist;
    String intntFrom;
    String trnselectDate = "", SelectedDate = "", DateToPass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_adv_first_play_report__network);

        init();

        Intent intent = getIntent();
        intntFrom = intent.getStringExtra("CallFrom");

        if (dbvalue()) {
            updatelist();

        } else if (ut.isnet(getApplicationContext())) {

            fetchdata();

        } else {
            ut.showD(AdvFirstPlayReport_NetworkActivity.this, "nonet");
        }

        setListeners();
    }

    public void init(){
        parent = AdvFirstPlayReport_NetworkActivity.this;

        listnw = findViewById(R.id.list1playrprt);

        mRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.imgbtnrfrsh);
        mprogress = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1);

        ut = new utility();

        db = new DatabaseHandler(getBaseContext());
        sqlDb = db.getWritableDatabase();

        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();

        mSearchlist = new ArrayList<SupportEnquiryHelper>();

    }

    public void setListeners(){

        mRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (ut.isnet(getApplicationContext())) {
                    fetchdata();
                } else {
                    ut.showD(AdvFirstPlayReport_NetworkActivity.this, "nonet");
                }
            }
        });

        listnw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EditBox_ClipID(mSearchlist.get(position).getSubnetwok());
            }
        });
    }

    private boolean dbvalue() {
        try {
            Cursor cursor = sqlDb.rawQuery("SELECT  NetworkCode FROM ConnectionStatusFiltermob", null);
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.getColumnIndex("NetworkCode") < 0) {
                    return false;
                } else {
                    return true;
                }
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }

    private void fetchdata() {
        if (asynk == null) {
            mRefresh.setVisibility(View.VISIBLE);
            mprogress.setVisibility(View.GONE);

            Log.e("async", "null");
            asynk = new DownloadnetWork();
            asynk.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            if (asynk.getStatus() == AsyncTask.Status.RUNNING) {
                Log.e("async", "running");
                mRefresh.setVisibility(View.GONE);
                mprogress.setVisibility(View.VISIBLE);
            }
        }
    }

    public class DownloadnetWork extends AsyncTask<String, Void, String> {

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
                String columnName, columnValue;
                //	sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusFiltermob");
                //	sql.execSQL(ut.getConnectionStatusFiltermob());
                sqlDb.delete("ConnectionStatusFiltermob",null,null);

                Cursor cur1 = sqlDb.rawQuery(
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
                    sqlDb.insert("ConnectionStatusFiltermob", null, values2);
                }

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
            mRefresh.setVisibility(View.GONE);
            mprogress.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                if (sop.equals("valid")) {
                    updatelist();
                } else {
                    ut.showD(AdvFirstPlayReport_NetworkActivity.this, "invalid");
                }
                mRefresh.setVisibility(View.VISIBLE);
                mprogress.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updatelist() {
        mSearchlist.clear();
        Cursor c = sqlDb
                .rawQuery(
                        "Select distinct NetworkCode from ConnectionStatusFiltermob Order by NetworkCode",
                        null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                int count = 0;
                String StationName = c.getString(c
                        .getColumnIndex("NetworkCode"));
                Cursor c1 = sqlDb.rawQuery("select distinct InstallationDesc from ConnectionStatusFiltermob where NetworkCode='"+StationName+"'",null);
                count = c1.getCount();

                SupportEnquiryHelper sitem = new SupportEnquiryHelper();
                sitem.setSubnetwok(StationName);
                mSearchlist.add(sitem);

                // }
            } while (c.moveToNext());
        }

        SupporterEnuiryAdptr adp = new SupporterEnuiryAdptr(AdvFirstPlayReport_NetworkActivity.this, mSearchlist);
        adp.notifyDataSetChanged();
        listnw.setAdapter(adp);
    }

    protected void EditBox_ClipID(final String NetworkCode) {
        // TODO Auto-generated method stub

        final Dialog myDialog = new Dialog(AdvFirstPlayReport_NetworkActivity.this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.dialogenterclip);
        myDialog.setCancelable(true);
        // myDialog.getWindow().setGravity(Gravity.BOTTOM);

        TextView txt = (TextView) myDialog.findViewById(R.id.txtmsg);
        final Button btndate = (Button)myDialog.findViewById(R.id.btndate);

        if(intntFrom.equalsIgnoreCase("DayCountStationwiseTab")){
            btndate.setVisibility(View.VISIBLE);
        }else {
            btndate.setVisibility(View.GONE);
        }

        final EditText edtclipno = (EditText)myDialog.findViewById(R.id.edtclipno);

        Button btn = (Button) myDialog .findViewById(com.stavigilmonitoring.R.id.gotodcliptls);

        btndate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                //intent call

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

                                btndate.setText(dayOfMonth + " - "
                                        + (monthOfYear + 1) + " - " + year);
                                trnselectDate = year + " - " + (monthOfYear + 1)  + " - " + dayOfMonth+ " 00:00:00.000";

                                String seldate = trnselectDate;
                                SelectedDate = (dayOfMonth + " - " + (monthOfYear + 1) + " - " + year);
                                DateToPass = (year+"-"+(monthOfYear + 1)+"-"+dayOfMonth);

                            }
                        }, Year, month, day);
                datePickerDialog.show();
            }
        });

            btn.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    //call intent
                    String ClipId = edtclipno.getText().toString().trim();

                        if(intntFrom.equalsIgnoreCase("DayCountStationwiseTab")){
                            //intent call
                            if(ClipId.equalsIgnoreCase("") || ClipId.equalsIgnoreCase(null)){
                                Toast.makeText(parent,"Please enter clip number",Toast.LENGTH_SHORT).show();

                            }else if(SelectedDate.equalsIgnoreCase("") ||  SelectedDate.equalsIgnoreCase(null)){
                                Toast.makeText(parent,"Please select date",Toast.LENGTH_SHORT).show();

                            }else {
                                Intent intent = new Intent(parent, AdvStationDayCount.class);
                                intent.putExtra("CallFrom",intntFrom);
                                intent.putExtra("Network",NetworkCode);
                                intent.putExtra("ClipNo",ClipId);
                                intent.putExtra("Date",DateToPass);
                                startActivity(intent);

                                myDialog.dismiss();
                            }

                        }else if(intntFrom.equalsIgnoreCase("FirstPlayReportTab")){

                            if(ClipId.equalsIgnoreCase("") || ClipId.equalsIgnoreCase(null)){
                                Toast.makeText(parent,"Please enter clip number",Toast.LENGTH_SHORT).show();

                            }else {
                                //intent call
                                Intent intent = new Intent(parent, AdvFirstPlayRprt_StationsList.class);
                                intent.putExtra("CallFrom",intntFrom);
                                intent.putExtra("Network",NetworkCode);
                                intent.putExtra("ClipNo",ClipId);
                                startActivity(intent);

                                myDialog.dismiss();
                            }

                        }else if(intntFrom.equalsIgnoreCase("TimingDetailsReportTab")){

                            if(ClipId.equalsIgnoreCase("") || ClipId.equalsIgnoreCase(null)){
                                Toast.makeText(parent,"Please enter clip number",Toast.LENGTH_SHORT).show();

                            }else {
                                //intent call
                                Intent intent = new Intent(parent, Adv_StationsList.class);
                                intent.putExtra("CallFrom",intntFrom);
                                intent.putExtra("Network",NetworkCode);
                                intent.putExtra("ClipNo",ClipId);
                                startActivity(intent);

                                myDialog.dismiss();
                            }
                        }
                }
            });

        myDialog.show();
    }
}