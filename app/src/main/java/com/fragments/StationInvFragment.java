package com.fragments;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.adapters.StationInventoryItemListAdapter;
import com.beanclasses.StationInventoryItemBean;
import com.database.DBInterface;
import com.stavigilmonitoring.Common;
import com.stavigilmonitoring.DatabaseHandler;
import com.stavigilmonitoring.Inventory_WH_Stock_StationActivity;
import com.stavigilmonitoring.R;
import com.stavigilmonitoring.StationInventoryAddEditItems;
import com.stavigilmonitoring.utility;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class StationInvFragment extends Fragment {
    private TextView tvhead;
    private EditText SearchFilterText;
    private Button btnaddItem;
    private ImageView btnfilter, btnrefresh, btnaddItem2;
    private String mType, mobno, InstallationID1;
    String sop = "no";
    com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
    public String filter;
    static SimpleDateFormat dff;
    static String Ldate;
    Bundle dataBundle = new Bundle();
    private static InventItemListURL async;
    private ListView invtlist;
    String conn = "invalid";
    DatabaseHandler db;
    ArrayList<StationInventoryItemBean> StationInventoryItemBeanlist;
    StationInventoryItemBean StnInventoryItemBean;
    StationInventoryItemListAdapter stationInventoryItemListAdapter;
    String responsemsg = "k";
    ProgressDialog progressDialog;
    SQLiteDatabase sql;

    public StationInvFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_station_inv, container, false);

        tvhead = view.findViewById(R.id.stationInvent);
        btnaddItem = view.findViewById(R.id.txtAddInvtItem);
        //btnaddItem2 = view.findViewById(R.id.button_invent_add);
        //btnfilter = view.findViewById(R.id.button_invent_filter);
        btnrefresh = view.findViewById(R.id.button_refresh_invent);
        invtlist = view.findViewById(R.id.listInventitems);
        SearchFilterText = view.findViewById(R.id.edfitertext);

        progressDialog  = new ProgressDialog(getContext());

        StationInventoryItemBeanlist = new ArrayList<StationInventoryItemBean>();
        ut = new utility();

        Inventory_WH_Stock_StationActivity activity = (Inventory_WH_Stock_StationActivity) getActivity();
        String myDataFromActivity = activity.GetData();
        InstallationID1 = myDataFromActivity.split(",")[1];
        mType = myDataFromActivity.split(",")[0];
        tvhead.setText("Station Inventory - " + mType);

        /*Intent i = getIntent();
        mType = i.getStringExtra("Type");
        InstallationID1 = i.getStringExtra("InstallationId");
        Log.e("Type", mType);
        tvhead.setText("Station Inventory - " + mType);*/

        db = new DatabaseHandler(getContext());
        sql = db.getWritableDatabase();
        DBInterface dbi = new DBInterface(getContext());
        mobno = dbi.GetPhno();

        if (async != null
                && async.getStatus() == AsyncTask.Status.RUNNING) {
            Log.e("async", "running");
            btnrefresh.setVisibility(View.GONE);
        }

        if(getStnInvData() > 0){
            updatelist();
        }else {
            if (ut.isnet(getContext())) {
                fetchdata();
            }
            else {
                try{
                    ut.showD(getContext(), "nonet");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        setListeners();

        return view;
    }

    public void setListeners(){
        btnrefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                btnrefresh.setVisibility(View.GONE);
                fetchdata();
            }
        });

        btnaddItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dataBundle.putString("Option", "Add");
                dataBundle.putString("InstallationId", InstallationID1);
                AddEditItems(dataBundle);
            }
        });

        SearchFilterText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    filter = s.toString();
                    stationInventoryItemListAdapter.filter(SearchFilterText.getText().toString().trim().toLowerCase(Locale.getDefault()));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

       /* btnfilter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (SearchFilterText.getVisibility() == View.VISIBLE) {
                    SearchFilterText.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } else if (SearchFilterText.getVisibility() == View.GONE) {
                    SearchFilterText.setVisibility(View.VISIBLE);
                    SearchFilterText.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(INPUT_METHOD_SERVICE);
                    imm.showSoftInput(SearchFilterText, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });*/

        invtlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position,	long id) {
                dataBundle.putString("InventoryId", StationInventoryItemBeanlist.get(position).getInventoryId());
                dataBundle.putString("ItemName", StationInventoryItemBeanlist.get(position).getItemname());
                dataBundle.putString("SerialNum", StationInventoryItemBeanlist.get(position).getSrNo());
                dataBundle.putString("Remark",StationInventoryItemBeanlist.get(position).getReMark());
                dataBundle.putString("InstallationId", InstallationID1);
                dataBundle.putString("Option", "Edit");
                AddEditItems(dataBundle);
            }

        });

    }

    private void fetchdata() {
        // TODO Auto-generated method stub
        async = null;
        if (async == null) {
            try{
                btnrefresh.setVisibility(View.VISIBLE);

                Log.e("async", "null");
                async = new InventItemListURL();
                async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }catch(Exception e){
                e.printStackTrace();
            }
        } else {
            if (async.getStatus() == AsyncTask.Status.RUNNING) {
                Log.e("async", "running");
                btnrefresh.setVisibility(View.GONE);
            }
        }
    }

    protected void AddEditItems(Bundle dataBundle2) {
        // TODO Auto-generated method stub
		/*Intent intent = new Intent(getApplicationContext(),StationInventoryAddEditItems.class);
		startActivity(intent);	*/
        Intent intent = new Intent(getContext(), StationInventoryAddEditItems.class);
        //Intent intent = new Intent(getContext(), SendReqDispMaterial.class);
        intent.putExtras(dataBundle2);
        startActivityForResult(intent, Common.InvtAddEdit);
    }

    public class InventItemListURL extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnrefresh.setVisibility(View.GONE);
            progressDialog.setMessage("Updating data...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
            //DatabaseHandler db = new DatabaseHandler(getBaseContext());
            SQLiteDatabase sql = db.getWritableDatabase();
            String url = "http://sta.vritti.co/imedia/STA_Announcement/TimeTable.asmx/GetStaInventory?InstallationId="
                    +InstallationID1 +"&Mobile=" + "";

            Log.e("csn status", "url : " + url);
            url = url.replaceAll(" ", "%20");
            try {
                responsemsg = ut.httpGet(url);
                sql.delete("StationInventory",null,null);

                Log.e("csn status", "resmsg : " + responsemsg);

                if (responsemsg.contains("<InventoryId>")) {
                    sop = "valid";
                    String columnName, columnValue;

                    Cursor cur = sql.rawQuery("SELECT * FROM StationInventory",
                            null);
                    ContentValues values1 = new ContentValues();
                    NodeList nl1 = ut.getnode(responsemsg, "Table");
                    // String msg = "";
                    // String columnName, columnValue;
                    Log.e("All Station Inventory..",
                            " fetch data : " + nl1.getLength());
                    for (int i = 0; i < nl1.getLength(); i++) {
                        Element e = (Element) nl1.item(i);
                        for (int j = 0; j < cur.getColumnCount(); j++) {
                            columnName = cur.getColumnName(j);

                            columnValue = ut.getValue(e, columnName);
                            values1.put(columnName, columnValue);

                            // Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
                        }
                        sql.insert("StationInventory", null, values1);
                    }

                    cur.close();

                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                dff = new SimpleDateFormat("HH:mm:ss");
                Ldate = dff.format(new Date());

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
                dff = new SimpleDateFormat("HH:mm:ss");
                Ldate = dff.format(new Date());

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
            return sop;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                btnrefresh.setVisibility(View.VISIBLE);
                progressDialog.dismiss();

                if (sop == "valid") {
                    updatelist();
                } else {
                    try{
                        ut.showD(getContext(),"nodata");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
                dff = new SimpleDateFormat("HH:mm:ss");
                Ldate = dff.format(new Date());

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

            } catch (Exception e) {
                e.printStackTrace();
                dff = new SimpleDateFormat("HH:mm:ss");
                Ldate = dff.format(new Date());

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

    public void updatelist() {
        // TODO Auto-generated method stub
        SQLiteDatabase sql = db.getWritableDatabase();
        StationInventoryItemBeanlist = new ArrayList<StationInventoryItemBean>();
        StationInventoryItemBeanlist.clear();

        Cursor c = sql.rawQuery("Select * from StationInventory", null);
        if (c.getCount()== 0){
            c.close();
        }else{
            c.moveToFirst();
            int column = 0;
            do{
                String InventoryId  = c.getString( c.getColumnIndex("InventoryId") );
                String ItemName  = c.getString( c.getColumnIndex("ItemName") );
                String InstallationId  = c.getString( c.getColumnIndex("InstallationId") );
                String ItemSrNo  = c.getString( c.getColumnIndex("ItemSrNo") );
                String AddedBy  = c.getString( c.getColumnIndex("AddedBy") );
                String DateTime  = c.getString( c.getColumnIndex("AddedDt") );
                String[] Parts = DateTime.split("T");
                String date = Parts[0];
                String[] newparts = Parts[1].split("\\.");
                String time = newparts[0];
                String AddedDt = date+" "+time;
                String Mobile  = c.getString( c.getColumnIndex("Mobile") );
                String Remarks  = c.getString( c.getColumnIndex("Remarks") );
                String IsDeleted = c.getString( c.getColumnIndex("IsDeleted") );

                StnInventoryItemBean = new StationInventoryItemBean();
                StnInventoryItemBean.setInventoryId(InventoryId);
                StnInventoryItemBean.setItemName(ItemName);
                StnInventoryItemBean.setInstallationId(InstallationId);
                StnInventoryItemBean.setSrNo(ItemSrNo);
                StnInventoryItemBean.setAddedBy(AddedBy);
                StnInventoryItemBean.setAddedDt(AddedDt);
                StnInventoryItemBean.setMobile(Mobile);
                StnInventoryItemBean.setReMark(Remarks);
                StnInventoryItemBean.setIsDeleted(IsDeleted);
                StationInventoryItemBeanlist.add(StnInventoryItemBean);
            }while(c.moveToNext());
            c.close();
        }

        stationInventoryItemListAdapter = new StationInventoryItemListAdapter(getContext(), StationInventoryItemBeanlist);
        invtlist.setAdapter(stationInventoryItemListAdapter);

    }

    public int getStnInvData(){
        String qry = "Select * from StationInventory";
        Cursor c = sql.rawQuery(qry,null);
        if(c.getCount()>0){
            c.moveToFirst();
            return c.getCount();
        }else {
            return 0;
        }
    }

}
