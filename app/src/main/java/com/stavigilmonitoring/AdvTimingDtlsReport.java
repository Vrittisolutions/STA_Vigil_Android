package com.stavigilmonitoring;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapters.AdvTimingDtlsRprtAdapter;
import com.beanclasses.AdvFirstPlayClipRprt;
import com.database.DBInterface;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class AdvTimingDtlsReport extends Activity {
    private Context parent;

    TextView hdrnetwrks, txtdate, txtcount;
    ListView lststndetails;
    ImageView mRefresh;
    ProgressBar mprogress;
    String intntFrom, stationName, InstallationID, ClipNo, Network, SelectedDate, DateToPass;

    ArrayList<AdvFirstPlayClipRprt> listStations;
    AdvTimingDtlsRprtAdapter advAdapter;
    String sop;
    String responsemsg = "k";
    private String DateToStr;
    String mobno;
    utility ut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_adv_timing_dtls_report);

        init();

        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();

        //fetchData();
        new DownloadxmlsDataURL_new().execute();

        setListeners();

    }

    public void init(){
        parent = AdvTimingDtlsReport.this;

        mRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.imgbtnrfrsh);
        mprogress = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1);

        hdrnetwrks = (TextView)findViewById(R.id.hdrnetwrks);
        lststndetails = (ListView)findViewById(R.id.lststndetails);

        txtdate = (TextView)findViewById(R.id.txtdate);
        txtcount = (TextView)findViewById(R.id.txtcount);

        listStations = new ArrayList<AdvFirstPlayClipRprt>();
        ut = new utility();

        Intent intent = getIntent();
        stationName = intent.getStringExtra("Stationname");
        InstallationID = intent.getStringExtra("InstallationID");
        ClipNo = intent.getStringExtra("ClipNo");
        Network = intent.getStringExtra("Network");
        SelectedDate = intent.getStringExtra("SelectedDate");
        DateToPass = intent.getStringExtra("DateToPass");

        hdrnetwrks.setText(" " + Network + " - " +  stationName + " - " + ClipNo + " " );
        txtdate.setText("Schedule Date : "+ SelectedDate );
       // txtcount.setText("Day Count : "+ "18" );

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
                    ut.showD(AdvTimingDtlsReport.this, "nonet");
                }
            }
        });

    }

    public class DownloadxmlsDataURL_new extends
            AsyncTask<String, Void, String> {
        String AdvertisementDesc, ScheduleTime, ActualTime, Date, AmPm, Time;

        @Override
        protected String doInBackground(String... params) {
            listStations.clear();
            utility ut = new utility();

            sop = "valid";

            String url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetAdvDateTimeDetailReport" +
                    "?installationid="+InstallationID+"&mobileno="+mobno+"&clipid="+ClipNo+"&date="+DateToPass;

            Log.e("csn status", "url : " + url);
            url = url.replaceAll(" ", "%20");

            try {
                responsemsg = ut.httpGet(url);

                if (responsemsg.contains("<AdvertisementDesc>")) {
                    sop = "valid";
                    NodeList nl1 = ut.getnode(responsemsg, "TableResult");

                    for (int i = 0; i < nl1.getLength(); i++) {

                        Element e = (Element) nl1.item(i);

                        AdvertisementDesc = ut.getValue(e,"AdvertisementDesc");
                        ScheduleTime = ut.getValue(e,"ScheduleTime");
                        ActualTime = ut.getValue(e,"ActualTime");

                        String[] Format_ActualTime = ActualTime.split("T"); //2019-05-31  08:10:33+05:30

                        String[] formatTime = Format_ActualTime[1].split("\\+");  //08:10:33
                        String ScheduleDate = dateconvert(Format_ActualTime[0]);
                        String Actual_Time =formatTime[0];

                        //Schedule Time
                        String SCHED_TIME[] = ScheduleTime.split(" ");
                        Date = dateconvert_1(SCHED_TIME[0]);
                        Time = SCHED_TIME[1];
                        AmPm = SCHED_TIME[2];

                        //Actual play time
                        String hr_new = "", min_new ="", sec_new ="", hhmmss = "";
                        int hr,min,sec;
                        String[] hrMnSec = Actual_Time.split(":");
                        hr = Integer.parseInt(hrMnSec[0]);
                        min = Integer.parseInt(hrMnSec[1]);
                        sec = Integer.parseInt(hrMnSec[2]);

                        min_new = String.valueOf(min);
                        sec_new = String.valueOf(sec);

                        if(hr > 12){
                           /* if(hr < 10){
                                hr = Integer.parseInt("0"+ hr);
                            }*/
                           if(min < 10){
                                min_new ="0"+ min;
                            }

                            if(sec < 10){
                                sec_new = "0"+ sec;
                            }

                            hhmmss=((hr-12)+":"+min_new+":"+sec_new);

                        }else{

                             /* if(hr < 10){
                                hr = Integer.parseInt("0"+ hr);
                            }*/
                             if(min < 10){
                                min_new ="0"+ min;
                            }

                            if(sec < 10){
                                sec_new = "0"+ sec;
                            }

                            hhmmss=(hr+":"+min_new+":"+sec_new);
                        }

                        AdvFirstPlayClipRprt advitem = new AdvFirstPlayClipRprt();
                        advitem.setAdvertisementDesc(AdvertisementDesc);
                        advitem.setSchedule_time_date(Time + "  " +AmPm);
                        advitem.setActual_time_date(hhmmss + "  "+ AmPm/*Actual_Time + "  "+AmPm*/);
                        advitem.setSchDate_tosort(ScheduleTime);

                        listStations.add(advitem);

                        //Collections.sort(assignedlist, String.CASE_INSENSITIVE_ORDER);        //if string list

                        Collections.sort(listStations, new Comparator<AdvFirstPlayClipRprt>() {
                            public int compare(AdvFirstPlayClipRprt o1, AdvFirstPlayClipRprt o2) {
                                if (o1.getScheduleTime() == null || o2.getScheduleTime() == null)
                                    return 0;
                                return o2.getScheduleTime().compareTo(o1.getScheduleTime());
                            }
                        });

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

                mRefresh.setVisibility(View.VISIBLE);
                mprogress.setVisibility(View.GONE);

                if (sop.equals("valid")) {
                    //display list to adapter
                    txtcount.setText("Day Count : "+String.valueOf(listStations.size()));

                    advAdapter = new AdvTimingDtlsRprtAdapter(parent, listStations);
                    lststndetails.setAdapter(advAdapter);

                } else {
                    ut.showD(parent,"nodata");
                }

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

    public String dateconvert(String Date_to_convert){

        SimpleDateFormat Format = new SimpleDateFormat("dd MMM yyyy");//Feb 23 2016 12:16PM
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        //SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        SimpleDateFormat toFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date d1 = null;

        try {
            d1 = format.parse(Date_to_convert);
            //DateToStr = toFormat.format(date);
            DateToStr = Format.format(d1);
            System.out.println(DateToStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateToStr;
    }

    public String dateconvert_1(String Date_to_convert){ //5/31/2019 1:10:33 PM

        SimpleDateFormat Format = new SimpleDateFormat("dd MMM yyyy");//Feb 23 2016 12:16PM
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        //SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat toFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date d1 = null;

        try {
            //d1 = format.parse(DoAck);
            d1 = format.parse(Date_to_convert);
            //DateToStr = toFormat.format(date);
            DateToStr = Format.format(d1);
            System.out.println(DateToStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateToStr;
    }

}
