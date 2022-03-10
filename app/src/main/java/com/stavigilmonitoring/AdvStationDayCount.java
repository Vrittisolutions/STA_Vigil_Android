package com.stavigilmonitoring;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapters.AdvStationDayCountAdapter;
import com.beanclasses.AdvFirstPlayClipRprt;
import com.database.DBInterface;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class AdvStationDayCount extends Activity {
    private Context parent;

    TextView hdrnetwrks;
    GridView listdaycnt;
    ImageView mRefresh;
    ProgressBar mprogress;
    String intntFrom, Network, ClipNo, Stationname, SelectedDate;

    ArrayList<AdvFirstPlayClipRprt> listStations;
    AdvStationDayCountAdapter advAdapter;
    String sop;
    String responsemsg = "k";
    String mobno;
    utility ut;
    String AdvDesc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_adv_station_day_count);

        init();

        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();

        //fetchData();
        new DownloadxmlsDataURL_new().execute();

        setListeners();
    }

    public void init(){
        parent = AdvStationDayCount.this;

        listdaycnt = findViewById(R.id.listdaycnt);

        mRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.imgbtnrfrsh);
        mprogress = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1);

        hdrnetwrks = (TextView)findViewById(R.id.hdrnetwrks);

        Intent intent = getIntent();
        intntFrom = intent.getStringExtra("CallFrom");
        Network = intent.getStringExtra("Network");
        ClipNo = intent.getStringExtra("ClipNo");
        SelectedDate = intent.getStringExtra("Date");
        hdrnetwrks.setText(Network);

        listStations = new ArrayList<AdvFirstPlayClipRprt>();
        ut = new utility();

    }

    public void setListeners(){

        mRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (ut.isnet(getApplicationContext())) {
                    //fetchData();
                    new DownloadxmlsDataURL_new().execute();
                } else {
                    ut.showD(AdvStationDayCount.this, "nonet");
                }
            }
        });

    }

    public class DownloadxmlsDataURL_new extends
            AsyncTask<String, Void, String> {
        String InstalationId, InstalationName, AdvertisementDesc, DayCount;

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            listStations.clear();
            com.stavigilmonitoring.utility ut = new utility();

            sop = "valid";

            String url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetAdvStationDayCount" +
                    "?NetworkCode="+Network+"&mobileno="+mobno+"&clipid="+ClipNo+"&date="+SelectedDate;

            Log.e("csn status", "url : " + url);
            url = url.replaceAll(" ", "%20");

            try {
                responsemsg = ut.httpGet(url);

                if (responsemsg.contains("<InstallationId>")) {
                    sop = "valid";
                    NodeList nl1 = ut.getnode(responsemsg, "TableResult");

                    for (int i = 0; i < nl1.getLength(); i++) {

                        Element e = (Element) nl1.item(i);

                        InstalationId = ut.getValue(e,"InstallationId");
                        InstalationName = ut.getValue(e,"InstalationName");
                        AdvertisementDesc = ut.getValue(e,"AdvertisementDesc");
                        DayCount = ut.getValue(e,"Column1");

                        AdvDesc = AdvertisementDesc;

                        AdvFirstPlayClipRprt advitem = new AdvFirstPlayClipRprt();
                        // advitem.SetNetworkCode(NW_CodeName);
                        advitem.setInstallationID(InstalationId);
                        advitem.setStationName(InstalationName);
                        advitem.setAdvertisementDesc(AdvertisementDesc);
                        advitem.setDayCount(DayCount);

                        listStations.add(advitem);

                           /* Collections.sort(searchResults, new Comparator<StateList>() {
                                public int compare(StateList o1, StateList o2) {
                                    if (o1.getLsecdate() == null || o2.getLsecdate() == null)
                                        return 0;
                                    return o2.getLsecdate().compareTo(o1.getLsecdate());
                                }
                            });*/

                         /*   Collections.sort(listStations, new Comparator<StateList>() {
                                public int compare(StateList o1, StateList o2) {
                                    if (o1.getDdate() == null || o2.getDdate() == null)
                                        return 0;
                                    return o2.getDdate().compareTo(o1.getDdate());
                                }
                            });*/
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
                    advAdapter = new AdvStationDayCountAdapter(parent, listStations);
                    listdaycnt.setAdapter(advAdapter);

                    hdrnetwrks.setText(Network +"- "+ClipNo+"- "+AdvertisementDesc);

                } else {
                    ut.showD(parent,"nodata");
                }
                mRefresh.setVisibility(View.VISIBLE);
                mprogress.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRefresh.setVisibility(View.GONE);
            mprogress.setVisibility(View.VISIBLE);
        }
    }
}
