package com.stavigilmonitoring;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapters.DT_INSTANCE_DateTimeAdapter;
import com.adapters.DT_LSEC_DateTimeAdapter;
import com.beanclasses.StateList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class DT_StationDetails extends Activity {
    private Context parent;
    String stationName, LS_EC_Key, InstallationId, DaysInstKey;
    String sop;
    String responsemsg = "k";
    String NW_CodeName;

    com.stavigilmonitoring.utility ut;
    ImageView iv;
    ListView lstcsn;

    LinearLayout eclstitlestrip, dtinstancetitlestrip;

    TextView txtstationname, txtdate, txtectime, txtectitle, txtlstime, txtntwrkname;
    TextView txtxnwname, txtxstnname, txteclstime;
    TextView tvheader;
    private String DateToStr;
    ArrayList<StateList> searchResults;
    DT_LSEC_DateTimeAdapter LsEC_DateTime_Adapter;
    DT_INSTANCE_DateTimeAdapter DT_inst_datetime_Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.stavigilmonitoring.R.layout.activity_dt__station_details);

        parent = DT_StationDetails.this;

        Intent intent = getIntent();
        stationName = intent.getStringExtra("StationName");
        LS_EC_Key = intent.getStringExtra("LS_EC_Key");
        InstallationId = intent.getStringExtra("InstallationId");
        DaysInstKey = intent.getStringExtra("DaysInstKey");
        NW_CodeName = intent.getStringExtra("NW_CodeName");

        init();

        new DownloadxmlsDataURL_new().execute();

      //  txtstationname.setText(stationName);
       // txtntwrkname.setText(NW_CodeName);

    }

    public void init(){
        tvheader = (TextView)findViewById(com.stavigilmonitoring.R.id.tvheader);
        tvheader.setText("Station Details - "+DaysInstKey+ " Days Instance ");
        //tvheader.setText(NW_CodeName +" - " + stationName +" : "+ DaysInstKey+ " Days Instance ");

       // txtntwrkname = (TextView)findViewById(R.id.txtntwrkname);
        iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_station);
        txtxnwname = (TextView)findViewById(com.stavigilmonitoring.R.id.txtxnwname);
        txtxnwname.setText(NW_CodeName + " - ");
        txtxstnname = (TextView)findViewById(com.stavigilmonitoring.R.id.txtxstnname);
        txtxstnname.setText(stationName);
        txteclstime = (TextView)findViewById(com.stavigilmonitoring.R.id.txteclstime);
       // txtstationname = (TextView)findViewById(R.id.txtstationname);
       // txtdate = (TextView)findViewById(R.id.txtdate);
       // txtectime = (TextView)findViewById(R.id.txtectime);
       // txtectitle = (TextView)findViewById(R.id.txtectitle);
        lstcsn = (ListView) findViewById(com.stavigilmonitoring.R.id.lsectimelist);

        eclstitlestrip = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.eclstitlestrip);
        dtinstancetitlestrip = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.dtinstancetitlestrip);

        searchResults = new ArrayList<StateList>();
        //txtlstime = (TextView)findViewById(R.id.txtlstime);

        if(LS_EC_Key.contains("LS") || LS_EC_Key.contains("EC")){
            eclstitlestrip.setVisibility(View.VISIBLE);
            dtinstancetitlestrip.setVisibility(View.GONE);

        }else if(LS_EC_Key.contains("DT_INSTANCE")){
            eclstitlestrip.setVisibility(View.GONE);
            dtinstancetitlestrip.setVisibility(View.VISIBLE);
        }

        if(LS_EC_Key.equals("EC")){
            txteclstime.setText("Early Close Time ");
        }else if(LS_EC_Key.equals("LS")) {
            txteclstime.setText("Late Start Time ");
        }
    }

    public class DownloadxmlsDataURL_new extends
            AsyncTask<String, Void, String> {
        String LS_stationName, LS_stationID, LS_PcOntime,Date, Time, AmPm, AddedDate, DownTime, StartTime,EndTime;
        String count;
        Date date1;

        @Override
        protected String doInBackground(String... params) {
            searchResults.clear();
            com.stavigilmonitoring.utility ut = new utility();

            sop = "valid";

            if(LS_EC_Key.contains("LS") || LS_EC_Key.contains("EC")){

                String url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetStationStartcloseStationWiseDetails" +
                        "?downtimetype="+LS_EC_Key+"&InstallationId="+InstallationId+"&noofdays="+DaysInstKey;

                Log.e("csn status", "url : " + url);
                url = url.replaceAll(" ", "%20");

                try {
                    responsemsg = ut.httpGet(url);

                    if (responsemsg.contains("<InstalationId>")) {
                        sop = "valid";
                        NodeList nl1 = ut.getnode(responsemsg, "TableResult");

                        for (int i = 0; i < nl1.getLength(); i++) {

                            Element e = (Element) nl1.item(i);

                            LS_stationID = ut.getValue(e,"InstalationId");

                            if(LS_EC_Key.equals("EC")){
                               // LS_PcOntime = ut.getValue(e,"PCffTime");
                                LS_PcOntime = ut.getValue(e,"PR");
                            }else if(LS_EC_Key.equals("LS")) {
                                //LS_PcOntime = ut.getValue(e,"PcOntime");
                                LS_PcOntime = ut.getValue(e,"PR");
                            }

                            String formatteddate = dateconvert(LS_PcOntime);
                            String data[] = LS_PcOntime.split(" ");

                            Date fdate = dateconvert_1(LS_PcOntime);
                            date1 = fdate;

                            Date = formatteddate;
                            Time = data[1];
                            AmPm = data[2];
                            LS_stationName = ut.getValue(e,"InstalationName");

                            StateList sitem = new StateList();
                            sitem.SetNetworkCode(NW_CodeName);
                            sitem.setStatioName(LS_stationName);
                            sitem.setLS_EC_Key(LS_EC_Key);
                            sitem.setInstallationId(LS_stationID);
                            sitem.setLsecdate(Date);
                            sitem.setLsectime(Time);
                            sitem.setLsecampm(AmPm);
                            sitem.setDdate(date1);

                            // sitem.Setcount(Integer.parseInt(count));
                            searchResults.add(sitem);

                           /* Collections.sort(searchResults, new Comparator<StateList>() {
                                public int compare(StateList o1, StateList o2) {
                                    if (o1.getLsecdate() == null || o2.getLsecdate() == null)
                                        return 0;
                                    return o2.getLsecdate().compareTo(o1.getLsecdate());
                                }
                            });*/

                            Collections.sort(searchResults, new Comparator<StateList>() {
                                public int compare(StateList o1, StateList o2) {
                                    if (o1.getDdate() == null || o2.getDdate() == null)
                                        return 0;
                                    return o2.getDdate().compareTo(o1.getDdate());
                                }
                            });
                        }

                    } else {
                        sop = "invalid";
                        System.out
                                .println("--------- invalid for project list --- ");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else if(LS_EC_Key.contains("DT_INSTANCE")){

                String url_DT_Inst = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetDowntimeInstancesStationwise" +
                        "?InstllationId="+InstallationId+"&noofdays="+DaysInstKey+"&networkcode="+NW_CodeName;

                Log.e("csn status", "url : " + url_DT_Inst);
                url_DT_Inst = url_DT_Inst.replaceAll(" ", "%20");

                try {
                    responsemsg = ut.httpGet(url_DT_Inst);

                    if (responsemsg.contains("<installationid>")) {
                        sop = "valid";
                        NodeList nl1 = ut.getnode(responsemsg, "TableResult");

                        for (int i = 0; i < nl1.getLength(); i++) {

                            Element e = (Element) nl1.item(i);

                            LS_stationID = ut.getValue(e,"installationid");

                            AddedDate = ut.getValue(e,"AddedDate");
                            DownTime = ut.getValue(e,"DownTime");
                            StartTime = ut.getValue(e,"StartTime");
                            EndTime = ut.getValue(e,"EndTime");

                            String DateSplit[] = AddedDate.split("T");
                            String a1 = DateSplit[0];

                            String formatteddate = dateconvert_INST(AddedDate);

                            Date fdate = dateconvert_INST_1(AddedDate);
                            date1 = fdate;

                            Date = formatteddate;
                            NW_CodeName = ut.getValue(e,"NetworkCode");
                            LS_stationName = ut.getValue(e,"InstalationName");

                            StateList sitem = new StateList();
                            sitem.SetNetworkCode(NW_CodeName);
                            sitem.setStatioName(LS_stationName);
                            sitem.setLS_EC_Key(LS_EC_Key);
                            sitem.setInstallationId(LS_stationID);
                            sitem.SetAddedDate(Date);
                            sitem.setDownTime(DownTime);
                            sitem.setStartTime(StartTime);
                            sitem.setEndTime(EndTime);
                            sitem.setDdate(date1);
                            // sitem.Setcount(Integer.parseInt(count));
                            searchResults.add(sitem);

                            Collections.sort(searchResults, new Comparator<StateList>() {
                                public int compare(StateList o1, StateList o2) {
                                    if (o1.getDdate() == null || o2.getDdate() == null)
                                        return 0;
                                    return o2.getDdate().compareTo(o1.getDdate());
                                }
                            });
                        }

                    } else {
                        sop = "invalid";
                        System.out
                                .println("--------- invalid for project list --- ");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    sop = "invalid";
                }

            }

            return sop;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (sop.equals("valid")) {

                    //display list to adapter

                    if(LS_EC_Key.contains("LS") || LS_EC_Key.contains("EC")){

                        LsEC_DateTime_Adapter = new DT_LSEC_DateTimeAdapter(getApplicationContext(), searchResults);
                        lstcsn.setAdapter(LsEC_DateTime_Adapter);

                    }else if(LS_EC_Key.contains("DT_INSTANCE")){

                        //set data to different adapter
                        DT_inst_datetime_Adapter = new DT_INSTANCE_DateTimeAdapter(getApplicationContext(), searchResults);
                        lstcsn.setAdapter(DT_inst_datetime_Adapter);

                    }

                   // txtdate.setText(Date);
                    //txtectime.setText(Time +" "+ AmPm);

                } else {
                    showD("invalid");
                }
                iv.setVisibility(View.VISIBLE);
                ((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressinvent1))
                        .setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            iv.setVisibility(View.GONE);
            ((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressinvent1))
                    .setVisibility(View.VISIBLE);
        }
    }

    public String dateconvert(String Date_to_convert){

        SimpleDateFormat Format = new SimpleDateFormat("dd MMM yyyy");//Feb 23 2016 12:16PM
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        SimpleDateFormat toFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date d1 = null;

        /*SimpleDateFormat Format = new SimpleDateFormat("dd MMM yyyy");//Feb 23 2016 12:16PM
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy ");
        SimpleDateFormat toFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date d1 = null;*/

        try {
            //d1 = format.parse(DoAck);
            d1 = format.parse(Date_to_convert);
            //DateToStr = toFormat.format(date);
            DateToStr = Format.format(d1);
            // DateToStr = format.format(d1);
            System.out.println(DateToStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateToStr;
    }

    public Date dateconvert_1(String Date_to_convert){

        SimpleDateFormat Format = new SimpleDateFormat("dd MMM yyyy");//Feb 23 2016 12:16PM
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        SimpleDateFormat toFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date d1 = null;

        /*SimpleDateFormat Format = new SimpleDateFormat("dd MMM yyyy");//Feb 23 2016 12:16PM
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy ");
        SimpleDateFormat toFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date d1 = null;*/

        try {
            //d1 = format.parse(DoAck);
            d1 = format.parse(Date_to_convert);
            //DateToStr = toFormat.format(date);
            DateToStr = Format.format(d1);
            System.out.println(DateToStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d1;
    }

    public String dateconvert_INST(String Date_to_convert){

        SimpleDateFormat Format = new SimpleDateFormat("dd MMM yyyy");//Feb 23 2016 12:16PM
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat toFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date d1 = null;

       // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       // sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            d1 = format.parse(Date_to_convert);
            DateToStr = Format.format(d1);
            System.out.println(DateToStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateToStr;
    }

    public Date dateconvert_INST_1(String Date_to_convert){

        SimpleDateFormat Format = new SimpleDateFormat("dd MMM yyyy");//Feb 23 2016 12:16PM
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat toFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date d1 = null;

        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            d1 = format.parse(Date_to_convert);
            DateToStr = Format.format(d1);
            System.out.println(DateToStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d1;
    }

    protected void showD(String string) {
        // TODO Auto-generated method stub

        final Dialog myDialog = new Dialog(parent);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoginfosmall);
        myDialog.setCancelable(true);
        // myDialog.getWindow().setGravity(Gravity.BOTTOM);

        TextView txt = (TextView) myDialog
                .findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall);
        if (string.equals("empty")) {
            myDialog.setTitle("Error...");
            txt.setText("Please Fill required data..");
        } else if (string.equals("nonet")) {
            myDialog.setTitle("Error...");
            txt.setText("No Internet Connection Found.Please Activate internet Connectin on Device..");
        } else if (string.equals("invalid")) {
            myDialog.setTitle(" ");
            txt.setText("No Refresh Data Available.Please check internet connection...");
        }

        Button btn = (Button) myDialog
                .findViewById(com.stavigilmonitoring.R.id.gotobtndialoginfosmall);
        btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                myDialog.dismiss();
                // finish();
            }
        });

        myDialog.show();
    }

}
