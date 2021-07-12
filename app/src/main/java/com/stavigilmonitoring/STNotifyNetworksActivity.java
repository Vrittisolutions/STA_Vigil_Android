package com.stavigilmonitoring;

import android.app.Activity;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.adapters.StationEnquiryAdptr;
import com.beanclasses.StateList;
import com.database.DBInterface;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;

public class STNotifyNetworksActivity extends Activity {
    private Context parent;
    GridView list_nw;
    private ImageView mRefresh;
    private ProgressBar mProgress;

    DatabaseHandler db;
    String mobno = "";
    utility ut;

    private ArrayList<StateList> mSearchList;
    String intentFROM = "", notificationType="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_stnotify_networks);

        init();

        db = new DatabaseHandler(getBaseContext());
        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();

        if (dbvalue()){
            updatelist();
        } else if (ut.isnet(getApplicationContext())){
            fetchdata();
        } else {
            try{
                ut.showD(STNotifyNetworksActivity.this, "nonet");
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        setListeners();

    }

    public void init(){
        parent = STNotifyNetworksActivity.this;

        list_nw = findViewById(R.id.list_nw);
        mRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_notify);
        mProgress = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBarsupenq_notify);

        ut = new utility();
        mSearchList = new ArrayList<StateList>();

        Intent intent = getIntent();
        intentFROM = intent.getStringExtra("intentFrom");
        notificationType = intent.getStringExtra("MsgType");

    }

    public void setListeners(){

        mRefresh.setOnClickListener(new  View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DownloadnetWork().execute();
            }
        });

        list_nw.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                Log.e("Check List item Click", mSearchList.get(position).getNetworkcode() );
                Intent intent = new Intent(getApplicationContext(),STNotifyStnListAll.class);
                intent.putExtra("intentFrom",intentFROM);
                intent.putExtra("Type", mSearchList.get(position).getNetworkcode());
                intent.putExtra("MsgType",notificationType);
                startActivity(intent);
            }

        });

    }

    private boolean dbvalue(){
        try{
            //DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
            SQLiteDatabase sql = db.getWritableDatabase();
            Cursor cursor = sql.rawQuery("SELECT NetworkCode FROM ConnectionStatusFiltermob", null);
            if (cursor != null && cursor.getCount()>0){
                if(cursor.getColumnIndex("NetworkCode")<0){
                    cursor.close();
                    return false;
                } else {
                    cursor.close();
                    return true;
                }
            }else{
                cursor.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void updatelist(){
        mSearchList.clear();
        //DatabaseHandler db = new DatabaseHandler(this);
        SQLiteDatabase sql = db.getWritableDatabase();
        Cursor c = sql
                .rawQuery(
                        "Select distinct NetworkCode from ConnectionStatusFiltermob Order by NetworkCode",
                        null);
        if (c.getCount()>0){
            c.moveToFirst();
            do{
                int count = 0;
                String StationName = c.getString(c.getColumnIndex("NetworkCode"));
                Cursor c1 = sql.rawQuery("select distinct InstallationDesc from ConnectionStatusFiltermob where NetworkCode='"+StationName+"'", null);
                count = c1.getCount();

                StateList sitem = new StateList();
                sitem.SetNetworkCode(StationName);
                sitem.Setcount(count);
                mSearchList.add(sitem);
            }while(c.moveToNext());
        }

        StationEnquiryAdptr adp = new StationEnquiryAdptr(STNotifyNetworksActivity.this, mSearchList,"Default");
        adp.notifyDataSetChanged();
        list_nw.setAdapter(adp);
    }

    private void fetchdata(){
        new DownloadnetWork().execute();
    }

    public class DownloadnetWork extends AsyncTask<String, Void, String>{
        String resposmsg = "", sop = "";

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String Url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMasterMobile?Mobile="+mobno;
            Log.e("All Station", "Url=" + Url);

            try{
                resposmsg = ut.httpGet(Url);
            }catch(IOException e){
                e.printStackTrace();
            }

            if(resposmsg.contains("<InstalationId>")){
                sop = "valid";
                //DatabaseHandler db = new DatabaseHandler(getBaseContext());
                SQLiteDatabase sql = db.getWritableDatabase();
                String columnName, columnValue;
                //sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusFiltermob");
                //sql.execSQL(ut.getConnectionStatusFiltermob());
                sql.delete("ConnectionStatusFiltermob",null,null);

                Cursor cur1 = sql.rawQuery("SELECT * FROM ConnectionStatusFiltermob", null);
                cur1.getCount();
                ContentValues values2 = new ContentValues();
                NodeList nl2 = ut.getnode(resposmsg, "Table");

                for(int i = 0; i < nl2.getLength(); i++){
                    Element e = (Element) nl2.item(i);
                    for (int j=0; j<cur1.getColumnCount(); j++){
                        columnName = cur1.getColumnName(j);
                        columnValue = ut.getValue(e, columnName);

                        values2.put(columnName, columnValue);
                    }
                    sql.insert("ConnectionStatusFiltermob", null, values2);
                }
                cur1.close();

            }else{
                sop = "invalid";
            }


            return sop;
        }
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mRefresh.setVisibility(View.GONE);
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try{
                if(sop.equals("valid")){
                    updatelist();
                } else {
                    try{
                        ut.showD(STNotifyNetworksActivity.this, "invalid");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                mRefresh.setVisibility(View.VISIBLE);
                mProgress.setVisibility(View.GONE);
            }catch(Exception e){
                e.printStackTrace();

            }
        }

    }

}
