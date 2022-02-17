package com.stavigilmonitoring;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adapters.AdvDetailsAdapter;
import com.beanclasses.AdvVideoDataBean;
import com.beanclasses.StateList;
import com.database.DBInterface;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Locale;

public class AdvDetailActivity extends AppCompatActivity {
    private Context parent;
    ListView listadvs;
    utility ut = new utility();
    private EditText edfitertext_search;
    private ImageView btnrefresh;
    private ProgressBar progressbar;
    ProgressDialog pdialogue;
    private ImageView searchfilter;
    private String sop, Ldate, dff,mobno;
    ArrayList<StateList> searchResults;
    AdvDetailsAdapter dtlAdapter;
    private String filter;
    DatabaseHandler db;
    SQLiteDatabase sql;
    ArrayList<AdvVideoDataBean> list_advdata;
    ArrayList<AdvVideoDataBean> list_stations;
    String InstalationId = "", InstalationName = "", AdvertisementDesc = "", responsemsg = "",
            NetworkCode = "",AdvertisementCode = "", URL_clipPath = "",EffectiveDateTo = "", EffectiveDatefrom = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_adv_detail);

        init();

        if(isnet()){
            new DownloadxmlsDataURL_new().execute();
        }else {
            Toast.makeText(this,"No internet available",Toast.LENGTH_SHORT);
        }

        setListeners();
    }

    public void init(){
        parent = AdvDetailActivity.this;
        listadvs = findViewById(R.id.listadvs);
        btnrefresh = findViewById(R.id.button_refresh_work_Done);
        searchfilter = findViewById(R.id.button_work_filter);
        progressbar = findViewById(R.id.progressbar);
        edfitertext_search = findViewById(R.id.edfitertext_search);
        pdialogue = new ProgressDialog(this);

        /*adapter = new AdvDetailsAdapter(this,list);
        listadvs.setAdapter(adapter);*/

        db = new DatabaseHandler(getBaseContext());
        sql = db.getWritableDatabase();
        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();

        list_advdata = new ArrayList<AdvVideoDataBean>();
        list_stations = new ArrayList<AdvVideoDataBean>();

    }

    public void setListeners(){
        searchfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edfitertext_search.setVisibility(View.VISIBLE);
            }
        });

        btnrefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isnet()){
                    new DownloadxmlsDataURL_new().execute();
                }else {
                    Toast.makeText(parent,"No internet available",Toast.LENGTH_SHORT);
                }
            }
        });

        edfitertext_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String charText = s.toString();
                dtlAdapter.filter(charText.toLowerCase(Locale.getDefault()));
            }
        });

        listadvs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //display dialogue box with list
                String nw = list_advdata.get(position).getNetworkCode();
                String clipNo = list_advdata.get(position).getAdvertisementCode();
                //ArrayList<AdvVideoDataBean> listStations = list_advdata.get(position).getListStns();

                Intent intent = new Intent(AdvDetailActivity.this,AdvDtlsStationsList.class);
                intent.putExtra("Network",nw);
                intent.putExtra("ClipNo",clipNo);
                startActivity(intent);
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

    public class DownloadxmlsDataURL_new extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnrefresh.setVisibility(View.GONE);
            progressbar.setVisibility(View.VISIBLE);
            pdialogue.setTitle("Loading data please wait...");
            pdialogue.setCanceledOnTouchOutside(false);
            pdialogue.setCancelable(false);
            pdialogue.show();
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            com.stavigilmonitoring.utility ut = new utility();

            sop = "valid";

            String url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/AdvertisementVideoData";

            Log.e("csn status", "url : " + url);
            url = url.replaceAll(" ", "%20");

            try {
                responsemsg = ut.httpGet(url);

                if (responsemsg.contains("<InstalationId>")) {
                    sop = "valid";
                    NodeList nl1 = ut.getnode(responsemsg, "TableResult");

                    for (int i = 0; i < nl1.getLength(); i++) {

                        Element e = (Element) nl1.item(i);
                        NetworkCode = ut.getValue(e,"NetworkCode");
                        InstalationId = ut.getValue(e,"InstalationId");
                        InstalationName = ut.getValue(e,"InstalationName");
                        AdvertisementCode = ut.getValue(e,"AdvertisementCode");
                        AdvertisementDesc = ut.getValue(e,"AdvertisementDesc");
                        URL_clipPath = ut.getValue(e,"URL");
                        EffectiveDateTo = ut.getValue(e,"EffectiveDateTo");
                        EffectiveDatefrom = ut.getValue(e,"EffectiveDatefrom");

                        db.addAdvDetail(NetworkCode,InstalationId,InstalationName,AdvertisementCode,
                                AdvertisementDesc,URL_clipPath,EffectiveDateTo,EffectiveDatefrom);
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
                    setDataToList();

                } else {
                    ut.showD(parent,"nodata");
                }
                btnrefresh.setVisibility(View.VISIBLE);
                progressbar.setVisibility(View.GONE);
                pdialogue.dismiss();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setDataToList(){

        try{

            if(list_advdata.size() < 0){
                list_advdata.clear();
                list_stations.clear();
            }

            String qry = "Select DISTINCT AdvertisementCode,AdvertisementDesc,URL_clipPath FROM AdvAudioClipDtls";
            Cursor c = sql.rawQuery(qry,null);
            if(c.getCount() > 0){
                c.moveToFirst();
                do{
                    String AdvertisementCode = c.getString(c.getColumnIndex("AdvertisementCode"));
                    String AdvertisementDesc = c.getString(c.getColumnIndex("AdvertisementDesc"));
                    String URL_clipPath = c.getString(c.getColumnIndex("URL_clipPath"));

                    String NetworkCode = "";

                    String q1 = "Select DISTINCT NetworkCode from AdvAudioClipDtls WHERE AdvertisementCode='"+AdvertisementCode+"'";
                    Cursor c1 = sql.rawQuery(q1,null);
                    if(c1.moveToFirst()){
                        do{
                            NetworkCode = c1.getString(c1.getColumnIndex("NetworkCode"));

                       /* ArrayList<AdvVideoDataBean> stnList = new ArrayList<AdvVideoDataBean>();
                        String qryStns = "Select InstalationId,InstalationName,EffectiveDateTo,EffectiveDatefrom FROM AdvAudioClipDtls " +
                                "WHERE AdvertisementCode='"+AdvertisementCode+"' AND " +
                                "NetworkCode='"+NetworkCode+"'";
                        Cursor c2 = sql.rawQuery(qryStns,null);
                        if(c2.getCount()>0){
                            c2.moveToFirst();
                            do{
                                String instId = c2.getString(c2.getColumnIndex("InstalationId"));
                                String instName = c2.getString(c2.getColumnIndex("InstalationName"));
                                String EffectiveDatefrom = c2.getString(c2.getColumnIndex("EffectiveDatefrom"));
                                String EffectiveDateTo = c2.getString(c2.getColumnIndex("EffectiveDateTo"));

                                AdvVideoDataBean beanStn = new AdvVideoDataBean();
                                beanStn.setInstalationId(instId);
                                beanStn.setInstalationName(instName);
                                beanStn.setEffectiveDatefrom(EffectiveDatefrom);
                                beanStn.setEffectiveDateTo(EffectiveDateTo);
                                stnList.add(beanStn);

                            }while (c2.moveToNext());

                            //add here
                        }*/

                            AdvVideoDataBean beanMain = new AdvVideoDataBean();
                            beanMain.setNetworkCode(NetworkCode);
                            beanMain.setAdvertisementCode(AdvertisementCode);
                            beanMain.setAdvertisementDesc(AdvertisementDesc);
                            beanMain.setClipPath_URL(URL_clipPath);
                            //beanMain.setEffectiveDatefrom(EffectiveDatefrom);
                            //beanMain.setEffectiveDateTo(EffectiveDateTo);
                            //beanMain.setListStns(stnList);
                            list_advdata.add(beanMain);

                        }while (c1.moveToNext());
                    }
                }while (c.moveToNext());

                dtlAdapter = new AdvDetailsAdapter(parent, list_advdata);
                listadvs.setAdapter(dtlAdapter);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void showPrompt(String Network,String ClipNo) {
        // TODO Auto-generated method stub
        final Dialog myDialog = new Dialog(AdvDetailActivity.this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.dialoglist);
        myDialog.setCancelable(true);

        final ListView list_stn =  myDialog.findViewById(R.id.list_stn);
        ArrayList<String> stations = new ArrayList<String>();

        String qryStns = "Select InstalationId,InstalationName,EffectiveDateTo,EffectiveDatefrom FROM AdvAudioClipDtls " +
                "WHERE AdvertisementCode='"+ClipNo+"' AND " + "NetworkCode='"+Network+"'";
        Cursor c2 = sql.rawQuery(qryStns,null);
        if(c2.getCount()>0){
            c2.moveToFirst();
            do{
                String instId = c2.getString(c2.getColumnIndex("InstalationId"));
                String instName = c2.getString(c2.getColumnIndex("InstalationName"));
                String EffectiveDatefrom = c2.getString(c2.getColumnIndex("EffectiveDatefrom"));
                String EffectiveDateTo = c2.getString(c2.getColumnIndex("EffectiveDateTo"));

                AdvVideoDataBean beanStn = new AdvVideoDataBean();
                beanStn.setInstalationId(instId);
                beanStn.setInstalationName(instName);
                beanStn.setEffectiveDatefrom(EffectiveDatefrom);
                beanStn.setEffectiveDateTo(EffectiveDateTo);
               // stnList.add(beanStn);

                stations.add(instName);

            }while (c2.moveToNext());

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,stations);
            list_stn.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            myDialog.show();
        }
    }
}
