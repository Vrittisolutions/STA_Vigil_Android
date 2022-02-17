package com.video_photo;

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
import com.stavigilmonitoring.DatabaseHandler;
import com.stavigilmonitoring.DmCstnwiseActivity;
import com.stavigilmonitoring.R;
import com.stavigilmonitoring.utility;

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

public class VideoPhotostnwiseActivity extends Activity {
    List<DmCstnwiseActivity.StateList> searchResults;
    ImageView iv,btnfilter;
    GridView lstcsn;
    TextView header;
    Context parent;

    DMCStateAdapter listAdapter;
    DatabaseHandler db;

    String mobno;
    private utility ut;
    private static DmCRefresh asynk_new;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.csnstatewise);

        initView();
        updatelist();
        setListener();
    }

    private void setListener() {
        ((EditText) findViewById(R.id.edfitertext))
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
                                .filter_Station(((EditText) findViewById(R.id.edfitertext))
                                        .getText().toString().trim()
                                        .toLowerCase(Locale.getDefault()));
                        listAdapter.notifyDataSetChanged();
                    }
                });

        lstcsn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                    Intent i = new Intent(VideoPhotostnwiseActivity.this, VideoPhotoStateFilter.class);
                    //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.putExtra("Type", searchResults.get(position).GetStateName());
                    startActivity(i);
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

    public void FilterClick(View v) {
        if (((EditText) findViewById(R.id.edfitertext)).getVisibility() == View.VISIBLE) {
            ((EditText) findViewById(R.id.edfitertext))
                    .setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } else if (((EditText) findViewById(R.id.edfitertext)).getVisibility() == View.GONE) {
            ((EditText) findViewById(R.id.edfitertext))
                    .setVisibility(View.VISIBLE);
            EditText textView = (EditText) findViewById(R.id.edfitertext);
            textView.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*Intent intent = new Intent(getApplicationContext(), DmCcategorizeActivity.class);
        startActivity(intent);*/
        finish();
    }


    private void updatelist() {
        searchResults.clear();
        //DatabaseHandler db = new DatabaseHandler(this);
        SQLiteDatabase sql = db.getWritableDatabase();
        Cursor c = sql.rawQuery(
                "SELECT DISTINCT NetworkCode FROM VideoPhotoTable ORDER BY NetworkCode", null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                int stncnt = 0,overduecnt=0;
                String Type = c.getString(0);

                Cursor c1 = sql
                        .rawQuery(
                                "SELECT StationName,ActualEndDate FROM VideoPhotoTable WHERE NetworkCode='"
                                        + c.getString(0)
                                        + "' ORDER BY NetworkCode Desc", null);
                if (c1.getCount() > 0) {
                    c1.moveToFirst();
                    do {
                        String column1 = c1.getString(c1.getColumnIndex("ActualEndDate"));
                        if (checkOverdue(column1)){
                            overduecnt = overduecnt + 1;
                        }
                                stncnt = stncnt + 1;

                    } while (c1.moveToNext());
                }
                if (!Type.trim().equalsIgnoreCase("")) {
                    DmCstnwiseActivity.StateList sitem = new DmCstnwiseActivity.StateList();
                    sitem.SetStateName(Type);
                    sitem.SetCount(stncnt);
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

    public static boolean checkOverdue(String amcExpireDt) {
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

    public static class StateList {
        String StateName,DMDesc,DueDate,CertificateDate,AdvDate, InstallationId;
        int scount,ocount, dayscount;

        public StateList() {
        }

        public String getInstallationId() {
            return InstallationId;
        }

        public void setInstallationId(String installationId) {
            InstallationId = installationId;
        }

        public int Getdayscount() {
            return dayscount;
        }

        public void Setdayscount(int o) {
            this.dayscount = o;
        }

        public String GetDueDate() {
            return DueDate;
        }

        public void SetDueDate(String s) {
            this.DueDate = s;
        }

        public String GetCertificateDate() {
            return CertificateDate;
        }

        public void SetCertificateDate(String s) {
            this.CertificateDate = s;
        }

        public String GetAdvDate() {
            return AdvDate;
        }

        public void SetAdvDate(String s) {
            this.AdvDate = s;
        }

        public String GetDMDesc() {
            return DMDesc;
        }

        public void SetDMDesc(String s) {
            this.DMDesc = s;
        }

        public String GetStateName() {
            return StateName;
        }

        public void SetStateName(String s) {
            this.StateName = s;
        }

        public int GetSCount() {
            return scount;
        }

        public void SetCount(int s) {
            this.scount = s;
        }

        public int GetOverdueCnt() {
            return ocount;
        }

        public void SetOverdueCnt(int o) {
            this.ocount = o;
        }

    }

    private void fetchdata(){
        asynk_new = null;
        asynk_new = new DmCRefresh();
        asynk_new.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public class DmCRefresh extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        //String sumdata2;
        @Override
        protected String doInBackground(String... params) {
            String	responsemsg;

            String bb= "";
            utility ut = new utility();
            String urls = "http://vritti.co/imedia/STA_Announcement/DmCertificate.asmx/GetListOfPendingDMPhotoVideo?Mobile="
                    + mobno;
            urls = urls.replaceAll(" ", "%20");

            try {
                System.out.println("-------  activity url --- " + urls);
                responsemsg = ut.httpGet(urls);

                System.out.println("-------------  xx vale of non repeated-- "
                        + responsemsg);

               // DatabaseHandler db = new DatabaseHandler(getBaseContext());
                SQLiteDatabase sql = db.getWritableDatabase();

               // sql.execSQL("DROP TABLE IF EXISTS DmCertificateTable");
                //sql.execSQL(ut.getDmCertificateTable());
                sql.delete("VideoPhotoTable",null,null);

                Log.e("dm certificate", "resmsg : " + responsemsg);

                if (responsemsg.contains("<DMPhotoVideoHeaderId>")) {

                    String columnName, columnValue;
                    Cursor cur = sql.rawQuery("SELECT * FROM VideoPhotoTable", null);
                    ContentValues values1 = new ContentValues();
                    NodeList nl1 = ut.getnode(responsemsg, "Table1");

                    Cursor c = sql.rawQuery("SELECT * FROM VideoPhotoTable",null);
                    ContentValues values = new ContentValues();
                    NodeList nl = ut.getnode(responsemsg, "Table1");
                    Log.e("DmCertificate data...",
                            " fetch data : " + nl1.getLength());
                    for (int i = 0; i < nl1.getLength(); i++) {
                        Element e = (Element) nl1.item(i);
                        for (int j = 0; j < cur.getColumnCount(); j++) {
                            columnName = cur.getColumnName(j);

                            columnValue = ut.getValue(e, columnName);
                            values1.put(columnName, columnValue);
                        }
                        sql.insert("VideoPhotoTable",
                                null, values1);
                    }

                    cur.close();

                } else {
                    //	sop = "invalid";
                    System.out.println("--------- invalid for DmC list --- ");
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

                responsemsg = "wrong" + e.toString();
                System.out.println("--------- invalid for message type list --- "
                        + responsemsg);

            }

            return null;
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
                //int totalDMC =  dbvalueDMC();
                String totalDMC = dbvalueDMC(getApplicationContext());

               /* CharSequence text = "DmCertificate : "+totalDMC;
                String z = String.valueOf(totalDMC);
                SharedPreferences prefDMC = getApplicationContext()
                        .getSharedPreferences("PrefDMC", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorDMC = prefDMC.edit();

                editorDMC.putString("DMC",
                        String.valueOf(totalDMC));
                editorDMC.commit();*/

                updatelist();
                progressDialog.dismiss();
                //Log.e("prgdlg", "Ended");
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

    }

    public static String dbvalueDMC(Context context) {
        DatabaseHandler db = new DatabaseHandler(context);
        SQLiteDatabase sql = db.getWritableDatabase();
        int count = 0;
        Cursor c = sql.rawQuery("SELECT * FROM VideoPhotoTable", null);
        int stncnt = 0,overduecnt=0;
        if (c.getCount() > 0) {
            count = c.getCount();
                Cursor c1 = sql
                        .rawQuery(
                                "SELECT StationName,ActualEndDate FROM VideoPhotoTable ORDER BY NetworkCode Desc", null);
                if (c1.getCount() > 0) {
                    c1.moveToFirst();
                    do {
                        String column1 = c1.getString(c1.getColumnIndex("ActualEndDate"));
                        if (checkOverdue(column1)){
                            overduecnt = overduecnt + 1;
                        }
                        stncnt = stncnt + 1;

                    } while (c1.moveToNext());
                }
        }
        return overduecnt+"/"+count;
    }

    private void initView() {
        parent = VideoPhotostnwiseActivity.this;

        iv = (ImageView) findViewById(R.id.button_refresh_nonrepeated_main);
        btnfilter = (ImageView) findViewById(R.id.button_filter);
        btnfilter.setVisibility(View.VISIBLE);
        lstcsn = findViewById(R.id.lstcsn);
        header = (TextView) findViewById(R.id.header);
        header.setText("Photo/Video Station Wise");
        searchResults = new ArrayList<DmCstnwiseActivity.StateList>();

        db = new DatabaseHandler(getApplicationContext());
        ut = new utility();

        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();
    }
}
