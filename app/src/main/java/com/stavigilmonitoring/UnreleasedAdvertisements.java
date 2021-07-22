package com.stavigilmonitoring;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adapters.AdvDetailsAdapter;
import com.adapters.UnreleasedAdvDetailsAdapter;
import com.beanclasses.AdvVideoDataBean;
import com.database.DBInterface;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class UnreleasedAdvertisements extends AppCompatActivity {
    Context parent;
    ListView listadvs;
    ImageView button_refresh_work_Done;
    ProgressBar progressbar;
    ProgressDialog pdialogue;

    DatabaseHandler db;
    SQLiteDatabase sql;
    utility ut = new utility();

    private String sop, Ldate, dff,mobno;
    String responsemsg="";
    String AdvertisementCode="",AdvertisementDesc="", ApproveDate ="", SOPReleaseDate = "",
            SOHeaderStatus = "", SoNumber = "", NetworkCode ="", Statuschangedate ="";

    ArrayList<AdvVideoDataBean> list_advdata;
    UnreleasedAdvDetailsAdapter dtlAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unreleased_advertisements);

        //Data not Found
        init();

        if(isnet()){
            new DownloadxmlsDataURL_new().execute();
        }else {
            Toast.makeText(this,"No internet available",Toast.LENGTH_SHORT);
        }

        setListeners();

    }

    public void init(){
        parent  = UnreleasedAdvertisements.this;

        listadvs = findViewById(R.id.listadvs);
        button_refresh_work_Done = findViewById(R.id.button_refresh_work_Done);
        progressbar = findViewById(R.id.progressbar);

        db = new DatabaseHandler(getBaseContext());
        sql = db.getWritableDatabase();
        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();

        list_advdata = new ArrayList<AdvVideoDataBean>();
        pdialogue = new ProgressDialog(parent);
    }

    public void setListeners(){

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
            button_refresh_work_Done.setVisibility(View.GONE);
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

            String url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetUnreleasedAdv";

            Log.e("csn status", "url : " + url);
            url = url.replaceAll(" ", "%20");

            try {
                responsemsg = ut.httpGet(url);

                if (responsemsg.contains("<AdvertisementCode>")) {
                    sop = "valid";
                    NodeList nl1 = ut.getnode(responsemsg, "Table1");

                    for (int i = 0; i < nl1.getLength(); i++) {

                        Element e = (Element) nl1.item(i);
                        AdvertisementCode = ut.getValue(e,"AdvertisementCode");
                        AdvertisementDesc = ut.getValue(e,"AdvertisementDesc");
                        ApproveDate = ut.getValue(e,"ApproveDate");
                        SOPReleaseDate = ut.getValue(e,"SOPReleaseDate");
                        SOHeaderStatus = ut.getValue(e,"SOHeaderStatus");
                        SoNumber = ut.getValue(e,"SoNumber");
                        NetworkCode = ut.getValue(e,"NetworkCode");
                        Statuschangedate = ut.getValue(e,"Statuschangedate");

                        AdvVideoDataBean adv = new AdvVideoDataBean();
                        adv.setAdvertisementCode(AdvertisementCode);
                        adv.setAdvertisementDesc(AdvertisementDesc);
                        adv.setApproveDate(ApproveDate);
                        adv.setSOPReleaseDate(SOPReleaseDate);
                        adv.setSOHeaderStatus(SOHeaderStatus);
                        adv.setSoNumber(SoNumber);
                        adv.setNetworkCode(NetworkCode);
                        adv.setStatuschangedate(Statuschangedate);

                       /* db.addAdvDetail(NetworkCode,InstalationId,InstalationName,AdvertisementCode,
                                AdvertisementDesc,URL_clipPath,EffectiveDateTo,EffectiveDatefrom);*/
                        //Log.e("tabledata", String.valueOf(i));

                        list_advdata.add(adv);

                    }

                    /*dtlAdapter = new UnreleasedAdvDetailsAdapter(parent, list_advdata);
                    listadvs.setAdapter(dtlAdapter);*/

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
                  //  setDataToList();
                    dtlAdapter = new UnreleasedAdvDetailsAdapter(parent, list_advdata);
                    listadvs.setAdapter(dtlAdapter);

                } else {
                    ut.showD(parent,"nodata");
                }
                button_refresh_work_Done.setVisibility(View.VISIBLE);
                progressbar.setVisibility(View.GONE);
                pdialogue.dismiss();

            } catch (Exception e) {
                e.printStackTrace();
                button_refresh_work_Done.setVisibility(View.VISIBLE);
                progressbar.setVisibility(View.GONE);
                pdialogue.dismiss();
                Toast.makeText(UnreleasedAdvertisements.this,"No data available",Toast.LENGTH_LONG).show();
            }
        }
    }
    
}
