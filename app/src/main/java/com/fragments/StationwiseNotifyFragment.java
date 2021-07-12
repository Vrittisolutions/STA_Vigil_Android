package com.fragments;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.adapters.StationEnquiryAdptr;
import com.beanclasses.StateList;
import com.database.DBInterface;
import com.stavigilmonitoring.DatabaseHandler;
import com.stavigilmonitoring.R;
import com.stavigilmonitoring.STNotifyStnListAll;
import com.stavigilmonitoring.utility;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;

public class StationwiseNotifyFragment extends Fragment {
    private Context parent;
    GridView listnetwrks;
    private ImageView mRefresh;
    private ProgressBar mProgress;

    DatabaseHandler db;
    String mobno = "";
    utility ut;

    private ArrayList<StateList> mSearchList;

    @SuppressLint("ValidFragment")
    public StationwiseNotifyFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.tbuds_printed_bill);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        parent = getActivity();

        View view = inflater.inflate(R.layout.fragment_stnwise_notify, container, false);

        listnetwrks = view.findViewById(R.id.listnetwrks);
        //mRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_notify);
       // mProgress = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBarsupenq_notify);

        ut = new utility();
        mSearchList = new ArrayList<StateList>();

        db = new DatabaseHandler(getActivity());
        DBInterface dbi = new DBInterface(getActivity());
        mobno = dbi.GetPhno();
        dbi.Close();

        if (dbvalue()){
            updatelist();
        } else if (ut.isnet(getActivity())){
            fetchdata();
        } else {
            try{
                ut.showD(getActivity(), "nonet");
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        setListeners();

        return view;
    }

    public void setListeners(){

        listnetwrks.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                Log.e("Check List item Click", mSearchList.get(position).getNetworkcode() );
                Intent intent = new Intent(getActivity(), STNotifyStnListAll.class);
                intent.putExtra("intentFrom","StationWiseNotification");
                intent.putExtra("Type", mSearchList.get(position).getNetworkcode());
                //intent.putExtra("MsgType","");
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

        StationEnquiryAdptr adp = new StationEnquiryAdptr(getActivity(), mSearchList,"Default");
        adp.notifyDataSetChanged();
        listnetwrks.setAdapter(adp);
    }

    private void fetchdata(){
        new DownloadnetWork().execute();
    }

    public class DownloadnetWork extends AsyncTask<String, Void, String> {
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
           // mRefresh.setVisibility(View.GONE);
           // mProgress.setVisibility(View.VISIBLE);
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
                        ut.showD(getActivity(), "invalid");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
               // mRefresh.setVisibility(View.VISIBLE);
               // mProgress.setVisibility(View.GONE);
            }catch(Exception e){
                e.printStackTrace();

            }
        }

    }

}
