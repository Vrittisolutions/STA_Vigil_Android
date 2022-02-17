package com.stavigilmonitoring;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapters.BusReporting_detailsAdapter;
import com.beanclasses.StationEnqBusBean;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class BusReportingDetails extends Activity {
    private Context parent;
    String InstallationId, date,stnname;
    int buscnt;
    TextView work_Station;
    ListView list_busrprt_details;
    BusReporting_detailsAdapter busrepadpt;
    ArrayList<StationEnqBusBean> busrepList;
    static DownloadxmlsDataURL_new asynk;
    SQLiteDatabase sql;
    DatabaseHandler db;
    String responsemsg, Syncdate, sop, urlnet;
    private com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
    String Reportingtime, Source, Destination,BusNo;
    ImageView buttn_refresh_work_his;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bus_reporting_details);

        initialise();

        fetchdata();

        setListener();
    }

    public void initialise(){
        parent = BusReportingDetails.this;

        work_Station = (TextView)findViewById(R.id.work_Station);
        list_busrprt_details = (ListView)findViewById(R.id.list_busrprt_details);
        buttn_refresh_work_his = (ImageView)findViewById(R.id.buttn_refresh_work_his);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_work);

        Intent intent = getIntent();
        InstallationId = intent.getStringExtra("InstallationId");
        date = intent.getStringExtra("date");
        stnname = intent.getStringExtra("stnname");
        work_Station.setText("Bus Reporting - "+ stnname + " - " + split(date));

       // db = new DatabaseHandler(parent);
       // sql = db.getWritableDatabase();

        busrepList =  new ArrayList<StationEnqBusBean>();

    }

    private String split(String data) {
        // TODO Auto-generated method stub
        Date conn1 = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 2016-05-12T20:36:08+05:30//09/05/2016

            conn1 = dateFormat.parse(data);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd MMM, yyyy");
        String dat = dateFormat1.format(conn1);

        return dat;
    }

    private void fetchdata() {
        // TODO Auto-generated method stub
        busrepList.clear();
        asynk = null;
        asynk = new DownloadxmlsDataURL_new();
        asynk.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setListener(){

        buttn_refresh_work_his.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchdata();
            }
        });

    }

    public class DownloadxmlsDataURL_new extends
            AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetBusreportingDetail?installationid="
                    + InstallationId + "&dt="+date;

            Log.e("csn status", "url : " + url);
            url = url.replaceAll(" ", "%20");
            try {
                responsemsg = com.stavigilmonitoring.utility.httpGet(url);
                Log.e("csn status", "resmsg : " + responsemsg);
                //sql.delete("BusReporting",null,null);

                //Cursor cur = sql.rawQuery("SELECT * FROM BusReporting", null);
                //Log.e("Counr----------", "" + cur.getCount());

                if (responsemsg.contains("<Reportingtime>")) {
                    sop = "valid";

                    ContentValues values1 = new ContentValues();
                    NodeList nl1 = ut.getnode(responsemsg, "Table");
                    Log.e("All Station data..."," fetch data : " + nl1.getLength());

                    for (int i = 0; i < nl1.getLength(); i++) {

                        Element e = (Element) nl1.item(i);

                        Reportingtime = ut.getValue(e,"Reportingtime");
                        Source = ut.getValue(e,"Source");
                        Destination = ut.getValue(e,"Destination");
                        BusNo = ut.getValue(e,"BUSNO");

                        StationEnqBusBean bean = new StationEnqBusBean();

                        bean.setReportingtime(Reportingtime);
                        bean.setSource(Source);
                        bean.setDestination(Destination);
                        bean.setBusno(BusNo);

                        busrepList.add(bean);

                           /* Collections.sort(searchResults, new Comparator<StateList>() {
                                public int compare(StateList o1, StateList o2) {
                                    if (o1.getLsecdate() == null || o2.getLsecdate() == null)
                                        return 0;
                                    return o2.getLsecdate().compareTo(o1.getLsecdate());
                                }
                            });*/

                        Collections.sort(busrepList, new Comparator<StationEnqBusBean>() {
                            public int compare(StationEnqBusBean o1, StationEnqBusBean o2) {
                                if (o1.getDate() == null || o2.getDate() == null)
                                    return 0;
                                return o2.getDate().compareTo(o1.getDate());
                            }
                        });
                    }

                } else {
                    sop = "invalid";

                }
            } catch (NullPointerException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }
            return sop;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (sop == "valid") {
                    //setAdapter
                    buttn_refresh_work_his.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);

                     busrepadpt = new BusReporting_detailsAdapter(parent, busrepList);
                     list_busrprt_details.setAdapter(busrepadpt);
                } else {
                    ut.showD(parent, "nodata");
                }

            } catch (NullPointerException e) {
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            buttn_refresh_work_his.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }
}
