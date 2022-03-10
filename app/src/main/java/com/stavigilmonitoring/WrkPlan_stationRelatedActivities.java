package com.stavigilmonitoring;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapters.SupportersListAdapter;
import com.adapters.WorkPlan_ActivitiesListAdapter;
import com.beanclasses.StateDetailsList;
import com.beanclasses.SupportersNames;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.stavigilmonitoring.WorkAssign_AssignActivity.Year;
import static com.stavigilmonitoring.WorkAssign_AssignActivity.day;
import static com.stavigilmonitoring.WorkAssign_AssignActivity.month;

public class WrkPlan_stationRelatedActivities extends Activity {
    private Context parent;
    Button btn_selectdate, btnview;
    ListView list_activitiesnames,list_sprtnames ;
    ImageView ivRefresh, ivFilter;
    TextView tvfilter, tvheader;
    ProgressBar mProgressBar;
    String Type, InstallationID, InstallationName, WorkPlanKey;
    String trnselectDate;
    LinearLayout lay_activities, lay_supportees;
    private static StationWiseActivities stnwiseactivities;
    private String sop, Ldate;
    com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
    private String resposmsg, responsemsg = "k";;
    String SelectedDate;
    ArrayList<SupportersNames> searchResults;
    ArrayList<SupportersNames> testlist;
    List<StateDetailsList> searchResults_stwise;
    SupportersListAdapter supListAdapter;
    WorkPlan_ActivitiesListAdapter wrkplan_stnadapter;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.stavigilmonitoring.R.layout.activity_wrk_plan_station_related_activities);

        parent = WrkPlan_stationRelatedActivities.this;

        init();

        Intent intent = getIntent();
        Type = intent.getStringExtra("Type");
        InstallationID = intent.getStringExtra("InstallationId");
        WorkPlanKey = intent.getStringExtra("ButtonClickKey");

        if(WorkPlanKey.equalsIgnoreCase("StationWiseWorkPlan")){
            //Toast.makeText(parent,"Station wise",Toast.LENGTH_SHORT).show();
            lay_activities.setVisibility(View.VISIBLE);
            lay_supportees.setVisibility(View.GONE);
            InstallationName = intent.getStringExtra("InstallationName");
            tvheader.setText("Station Wise Activities - "+ InstallationName);

            btnview.setText("Show Activities");
        }else if(WorkPlanKey.equalsIgnoreCase("SOWiseWorkPlan")){
            //Toast.makeText(parent,"SO wise",Toast.LENGTH_SHORT).show();
            lay_activities.setVisibility(View.GONE);
            lay_supportees.setVisibility(View.VISIBLE);
            tvheader.setText("Supporter Wise Activities - "+Type);
            btnview.setText("Show Supporters List");
        }
        setListener();
    }

    public void init(){
        tvheader = (TextView)findViewById(com.stavigilmonitoring.R.id.tvheader);
        btn_selectdate = (Button)findViewById(com.stavigilmonitoring.R.id.btn_selectdate);
        btnview = (Button)findViewById(com.stavigilmonitoring.R.id.btnview);
        list_activitiesnames = (ListView)findViewById(com.stavigilmonitoring.R.id.list_activitiesnames);
        list_sprtnames = (ListView)findViewById(com.stavigilmonitoring.R.id.list_sprtnames);
        lay_activities = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.lay_activities);
        lay_activities.setVisibility(View.GONE);
        lay_supportees = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.lay_supportees);
        lay_supportees.setVisibility(View.GONE);

        ivRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_connection_main);
        ivFilter = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_filter);
        ivFilter.setVisibility(View.GONE);
        mProgressBar = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.ProgressBar_01);
        //tvfilter = (TextView) findViewById(R.id.edfitertext_search_in_invt);
        searchResults = new ArrayList<SupportersNames>();
        testlist = new ArrayList<SupportersNames>();
        searchResults_stwise = new ArrayList<>();

        db = new DatabaseHandler(getApplicationContext());
    }

    public void setListener(){

        btn_selectdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Date date = new Date();
                final Calendar c = Calendar.getInstance();

                Year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(parent,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker datePicker, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Display Selected date in textbox

                                btn_selectdate.setText(dayOfMonth + "-"
                                        + (monthOfYear + 1) + "-" + year);
                                trnselectDate = year + "-" + (monthOfYear + 1)
                                        + "-" + dayOfMonth+ " 00:00:00.000";

                                String seldate = trnselectDate;
                                SelectedDate = (dayOfMonth + "-"
                                        + (monthOfYear + 1) + "-" + year);

                              /*  if (year>=Year){
                                    if((year==Year)&&(monthOfYear>=month)){
                                        if((monthOfYear==month)&&(dayOfMonth>=day)){
                                            btn_selectdate.setText(dayOfMonth + "-"
                                                    + (monthOfYear + 1) + "-" + year);
                                            trnselectDate = year + "-" + (monthOfYear + 1)
                                                    + "-" + dayOfMonth+ " 00:00:00.000";
                                        }*//*else if((monthOfYear==month)&&(dayOfMonth<day)){
                                            btn_selectdate.setText(day + "-"
                                                    + (month + 1) + "-" + Year);
                                            trnselectDate = Year + "-" + (month + 1)
                                                    + "-" + day+ " 00:00:00.000";
                                            //2018-01-15 16:43:40.440
                                            Toast.makeText(getApplicationContext(),
                                                    "Past date is not accepted",Toast.LENGTH_SHORT).show();
                                        }*//*else if(monthOfYear>month){
                                            btn_selectdate.setText(dayOfMonth + "-"
                                                    + (monthOfYear + 1) + "-" + year);
                                            trnselectDate = year + "-" + (monthOfYear + 1)
                                                    + "-" + dayOfMonth+ " 00:00:00.000";
                                        }
                                    }else if(year>Year){
                                        btn_selectdate.setText(dayOfMonth + "-"
                                                + (monthOfYear + 1) + "-" + year);
                                        trnselectDate = year + "-" + (monthOfYear + 1)
                                                + "-" + dayOfMonth+ " 00:00:00.000";
                                    }*//*else if((year==Year)&&(monthOfYear<month)){
                                        btn_selectdate.setText(day + "-"
                                                + (month + 1) + "-" + Year);
                                        trnselectDate = Year + "-" + (month + 1)
                                                + "-" + day+ " 00:00:00.000";
                                        Toast.makeText(getApplicationContext(),"Past date is not accepted",Toast.LENGTH_SHORT).show();
                                    }*//*
                                }else {
                                    btn_selectdate.setText(day + "-"
                                            + (month + 1) + "-" + Year);
                                    trnselectDate = Year + "-" + (month + 1)
                                            + "-" + day+ " 00:00:00.000";
                                    Toast.makeText(getApplicationContext(),"Past date is not accepted",Toast.LENGTH_SHORT).show();
                                }*/

                            }
                        }, Year, month, day);
                datePickerDialog.show();
            }
        });

            btnview.setClickable(true);
            btnview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(WorkPlanKey.equalsIgnoreCase("StationWiseWorkPlan")){
                        // Toast.makeText(parent,"Station wise activities",Toast.LENGTH_SHORT).show();
                        lay_activities.setVisibility(View.VISIBLE);
                        lay_supportees.setVisibility(View.GONE);
                        //call API get activities list

                        new StationWiseActivities().execute();

                    }else if(WorkPlanKey.equalsIgnoreCase("SOWiseWorkPlan")){
                        //Toast.makeText(parent,"SO wise supporters names",Toast.LENGTH_SHORT).show();
                        lay_activities.setVisibility(View.GONE);
                        lay_supportees.setVisibility(View.VISIBLE);
                        //call API get supporters list
                        new SupportersNames_Datewise().execute();
                    }
                }
            });

        //supporters list listener
        list_sprtnames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String spname = searchResults.get(position).getSupp_Name();
                String Supp_userID = searchResults.get(position).getIssuedTo();
                String DateToPass = SelectedDate;

                //getSuppUserID(spname);

                //open activities list in new activity
                Intent intent = new Intent(getApplicationContext(), WrkPlanActivitiesList.class);
                intent.putExtra("SuppName",spname);
                intent.putExtra("Supp_userID", Supp_userID);
                intent.putExtra("DateToPass", DateToPass);
                intent.putExtra("Type",Type);
                startActivity(intent);

            }
        });

        //station wise activities list listener
    }

    public void getSuppUserID(String SuprtrName){

      //  DatabaseHandler db = new DatabaseHandler(this);
        SQLiteDatabase sql = db.getWritableDatabase(); //DISTINCT SubNetworkCode
        /*Cursor c = sql.rawQuery(
                "SELECT DISTINCT SubNetworkCode FROM WorkAssignedTable WHERE UserName='"
                        + SuprtrName, null);*/
        Cursor c = sql.rawQuery("SELECT * FROM WorkAssignedTable WHERE NetworkCode='"+Type+"'", null);

        if(c.getCount()>0){
            c.moveToFirst();
            do{
                String SuppID_IssuedTo = c.getString(c.getColumnIndex("IssuedTo"));
                String IssuedUserName = c.getString(c.getColumnIndex("IssuedUserName"));
                String SubNetworkCode = c.getString(c.getColumnIndex("SubNetworkCode"));

            }while (c.moveToNext());

        }else {
            Log.e("Count", String.valueOf(c.getCount()));
        }

    }

    //show stationwise activities
    public class StationWiseActivities extends
            AsyncTask<String, Void, String> {

        String supporterName, issueToID, responsemsg;
        String ActivityName, ActualStartDate, ActualEndDate, Status, StationName, ActivityId,
                UserName,UserMasterId,IssuedUserName,NetworkCode,SubNetworkCode,SuppUsername;
        String DMHeaderId;
        String count;
        Date date1;

        @Override
        protected String doInBackground(String... params) {
            searchResults_stwise.clear();
            com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();

            sop = "valid";

            String url = "http://sta.vritti.co/iMedia/STA_Announcement/DmCertificate.asmx/GetWorkPlanstationWise" +
                    "?Installationid="+InstallationID+"&Date="+SelectedDate;

            Log.e("csn status", "url : " + url);
            url = url.replaceAll(" ", "%20");

            try {
                responsemsg = ut.httpGet(url);

                if (responsemsg.contains("<ActivityId>")) {
                    sop = "valid";
                    NodeList nl1 = ut.getnode(responsemsg, "TableResult");

                    for (int i = 0; i < nl1.getLength(); i++) {

                        Element e = (Element) nl1.item(i);

                        issueToID = ut.getValue(e,"IssuedTo");

                        ActivityName = ut.getValue(e,"ActivityName");
                        ActivityId = ut.getValue(e,"ActivityId");
                        UserMasterId = ut.getValue(e,"UserMasterId");
                        Status = ut.getValue(e,"Status");
                        ActualStartDate = ut.getValue(e,"ActualStartDate");
                        ActualEndDate = ut.getValue(e,"ActualEndDate");

                        DMHeaderId = ut.getValue(e,"DMHeaderId");
                        UserName = ut.getValue(e, "UserName");
                        SuppUsername = ut.getValue(e,"UserName1");

                       // UserName = getActAssignByname(UserMasterId);
                        //SuppUsername = getActIssuedToName(issueToID);

                        SupportersNames supnames = new SupportersNames();
                        supnames.setSupp_Name(SuppUsername);
                        supnames.setIssuedTo(issueToID);

                        StateDetailsList sitem = new StateDetailsList();
                        sitem.SetDMDesc(ActivityName); //activityname
                        sitem.Setdmcstatus(Status); //status

                        //sitem.SetInstallationIdForStateDetailsList(c.getString(8)); //IssuedUserName
                      //  sitem.SetInstallationIdForStateDetailsList(SuppUsername); //IssuedUserName act assigned to
                        sitem.setStationName(SuppUsername);     //IssuedUserName act assigned to

                        sitem.SetActivityId(ActivityId); //ActivityId
                        sitem.SetActualStartDate(ConvertDate(ActualStartDate)); //ActualStartDate
                        sitem.SetActualEndDate(ConvertDate(ActualEndDate)); //ActualEndDate

                        //sitem.SetGenrateFileName(c.getString(6)); //UserName
                        sitem.SetGenrateFileName(UserName); //UserName activity assigned by

                        sitem.SetSONumber(issueToID); //UserMasterId

                        //sitem.SetIssuedToName(c.getString(4)); //StationName
                        sitem.SetIssuedToName("No data"); //StationName

                        NetworkCode = Type; //network
                        SubNetworkCode = SuppUsername; //supportername
                        searchResults_stwise.add(sitem);
                        Log.e("DATA", "Yes Data added");

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

                    //display station wise activities
                    wrkplan_stnadapter = new WorkPlan_ActivitiesListAdapter(parent, searchResults_stwise);
                    list_activitiesnames.setAdapter(wrkplan_stnadapter);

                }else {

                    searchResults_stwise.clear();
                    wrkplan_stnadapter = new WorkPlan_ActivitiesListAdapter(parent, searchResults_stwise);
                    list_activitiesnames.setAdapter(wrkplan_stnadapter);

                    showD("invalid");
                }
                ivRefresh.setVisibility(View.VISIBLE);
                ((ProgressBar) findViewById(com.stavigilmonitoring.R.id.ProgressBar_01))
                        .setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ivRefresh.setVisibility(View.GONE);
            ((ProgressBar) findViewById(com.stavigilmonitoring.R.id.ProgressBar_01))
                    .setVisibility(View.VISIBLE);
        }
    }

    //show supporters list
    public class SupportersNames_Datewise extends
            AsyncTask<String, Void, String> {

        String supporterName, issueToID;
        String count;
        Date date1;

        @Override
        protected String doInBackground(String... params) {
            searchResults.clear();
            com.stavigilmonitoring.utility ut = new utility();
            boolean dup= false;
            int dupindex = 0;

            sop = "valid";

                String url = "http://sta.vritti.co/iMedia/STA_Announcement/DmCertificate.asmx/GetWorkplanNetwrokWise?" +
                        "NetworkCode="+Type+"&Date="+SelectedDate;

                Log.e("csn status", "url : " + url);
                url = url.replaceAll(" ", "%20");

                try {
                    responsemsg = ut.httpGet(url);

                    if (responsemsg.contains("<UserName>")) {
                        sop = "valid";
                        NodeList nl1 = ut.getnode(responsemsg, "TableResult");

                        for (int i = 0; i < nl1.getLength(); i++) {

                            Element e = (Element) nl1.item(i);

                            supporterName = ut.getValue(e,"UserName");
                            issueToID = ut.getValue(e,"IssuedTo");

                            SupportersNames supnames = new SupportersNames();
                            supnames.setSupp_Name(supporterName);
                            supnames.setIssuedTo(issueToID);

                            searchResults.add(supnames);
                            //testlist.add(supnames);

                           /* for (int j = 0; j<searchResults.size() ; j++){
                                String id2 = searchResults.get(j).getSupp_Name();
                                if(id2.equals(supporterName)){
                                    dup = true;
                                    dupindex =j;

                                }else {
                                    dup = false;

                                }
                            }

                            if(dup==true){
                                //do not add data to searchresult list
                                SupportersNames supnames1 = new SupportersNames();
                                supnames1.setSupp_Name(supporterName);
                                supnames1.setIssuedTo(issueToID);

                            }else{
                                //SupportersNames supnames = new SupportersNames();
                                supnames.setSupp_Name(supporterName);
                                supnames.setIssuedTo(issueToID);
                                searchResults.add(supnames);
                            }*/

                            Collections.sort(searchResults, new Comparator<SupportersNames>() {
                                public int compare(SupportersNames o1, SupportersNames o2) {
                                    String s1 = o1.getSupp_Name();
                                    String s2 = o2.getSupp_Name();

                                    return s1.compareToIgnoreCase(s2);
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

            return sop;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (sop.equals("valid")) {

                    //display supporters list to adapter

                        supListAdapter = new SupportersListAdapter(parent, searchResults);
                        list_sprtnames.setAdapter(supListAdapter);

                } else {
                    searchResults.clear();
                    supListAdapter = new SupportersListAdapter(parent, searchResults);
                    list_sprtnames.setAdapter(supListAdapter);

                    showD("invalid");
                }
                ivRefresh.setVisibility(View.VISIBLE);
                ((ProgressBar) findViewById(com.stavigilmonitoring.R.id.ProgressBar_01))
                        .setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ivRefresh.setVisibility(View.GONE);
            ((ProgressBar) findViewById(com.stavigilmonitoring.R.id.ProgressBar_01))
                    .setVisibility(View.VISIBLE);
        }
    }

    public String getActAssignByname(String UserId){
        String UserName = null, UserMasterId = null;

        //DatabaseHandler db = new DatabaseHandler(this);
        SQLiteDatabase sql = db.getWritableDatabase();

        Cursor c = sql.rawQuery("SELECT UserName FROM WorkAssignedTable WHERE UserMasterId='"+UserId+"'", null);

        if(c.getCount()>0){
            c.moveToFirst();
            do{
                UserName = c.getString(c.getColumnIndex("UserName"));

            }while (c.moveToNext());

        }else {
            Log.e("Count", String.valueOf(c.getCount()));
        }
        return UserName;

    }

    public String getActIssuedToName(String AssignedTo){
        String IssuedUserName = null;

     //   DatabaseHandler db = new DatabaseHandler(this);
        SQLiteDatabase sql = db.getWritableDatabase(); //DISTINCT SubNetworkCode
        /*Cursor c = sql.rawQuery(
                "SELECT DISTINCT SubNetworkCode FROM WorkAssignedTable WHERE UserName='"
                        + SuprtrName, null);*/
        Cursor c = sql.rawQuery("SELECT IssuedUserName FROM WorkAssignedTable WHERE IssuedTo='"+AssignedTo+"'", null);

        if(c.getCount()>0){
            c.moveToFirst();
            do{
                IssuedUserName = c.getString(c.getColumnIndex("IssuedUserName"));
            }while (c.moveToNext());

        }else {
            Log.e("Count", String.valueOf(c.getCount()));
        }
        return IssuedUserName;

    }

    private String ConvertDate(String amcExpireDt) {
        String result = null;
        // 2017-10-30T00:00:00+05:30
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss+05:30", Locale.ENGLISH);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM,yyyy");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+05:30", Locale.ENGLISH);
        try {
            if (amcExpireDt.contains(".")) {
                Date date2 = dateFormat1.parse(amcExpireDt);
                result = dateFormat2.format(date2);
            }else{
                Date date2 = dateFormat3.parse(amcExpireDt);
                result = dateFormat2.format(date2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    //dialog message
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
            txt.setText("No Activities Available on selected date");
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

