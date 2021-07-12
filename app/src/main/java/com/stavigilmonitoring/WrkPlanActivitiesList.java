package com.stavigilmonitoring;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import com.adapters.WorkAssign_ActivityDetailsAdapter;
import com.adapters.WorkPlan_ActivitiesListAdapter;
import com.beanclasses.StateDetailsList;
import com.beanclasses.SupportersNames;
import com.database.DBInterface;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WrkPlanActivitiesList extends Activity {
    Context parent;
    String MyFlag = "no flag",mobileUserName;
    ListView lstcsn;
    TextView header;
    com.stavigilmonitoring.utility ut;
    ImageView iv,btnfilter,btnadd;
    private ArrayList<String> NameList;
    WorkAssign_ActivityDetailsAdapter listAdapter;
    WorkPlan_ActivitiesListAdapter supact_ListAdapter;

    List<StateDetailsList> searchResults;
    String StationName,mobno,network,subnetwork,IssuedToName,Activity=null,SubType1,Type1,frompage1;
    private static WorkAssignStation_ActivityDetails.DownloadnetWork_New asynk_new;
    String mobi, ReassignBy, activityId, Remark, ReassignTo, worktype ="";
    String sop = "t", resposmsg ="n";
    String SuppUsername, SuppUserID, DateToPass, NetworkName;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.stavigilmonitoring.R.layout.activity_wrk_plan_activities_list);

        parent = WrkPlanActivitiesList.this;

        init();

        new SupportersActivities_Datewise().execute();

        //Toast.makeText(parent,"Activities list "+SuppUsername+"& "+DateToPass, Toast.LENGTH_SHORT).show();

    }

    public void init(){

        ut = new com.stavigilmonitoring.utility();
        NameList = new ArrayList<String>();
        iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_nonrepeated_main);
        btnadd = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_alert_add);
        btnadd.setImageResource(com.stavigilmonitoring.R.drawable.work_assign);
        btnadd.setVisibility(View.GONE);
        btnfilter = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_filter);
        btnfilter.setVisibility(View.GONE);
        lstcsn = (ListView) findViewById(com.stavigilmonitoring.R.id.lstcsn);
        header = (TextView) findViewById(com.stavigilmonitoring.R.id.header);

        searchResults = new ArrayList<>();

        Intent intent = getIntent();
        SuppUsername = intent.getStringExtra("SuppName");
        SuppUserID = intent.getStringExtra("Supp_userID");
        DateToPass = intent.getStringExtra("DateToPass");
        NetworkName = intent.getStringExtra("Type");

        header.setText("Supporter Wise Activities -  "+SuppUsername);

        db = new DatabaseHandler(getApplicationContext());
        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();

    }

    public class SupportersActivities_Datewise extends
            AsyncTask<String, Void, String> {

        String supporterName, issueToID, responsemsg;
        String ActivityName, ActualStartDate, ActualEndDate, Status, StationName, ActivityId,
                UserName,UserMasterId,IssuedUserName,NetworkCode,SubNetworkCode;
        String DMHeaderId;
        String count;
        Date date1;

        @Override
        protected String doInBackground(String... params) {
            searchResults.clear();
            com.stavigilmonitoring.utility ut = new utility();

            sop = "valid";

            String url = "http://vritti.co/iMedia/STA_Announcement/DmCertificate.asmx/GetworkPlanSupporterWise" +
                    "?UserMasterId="+SuppUserID+"&Date="+DateToPass;

            Log.e("csn status", "url : " + url);
            url = url.replaceAll(" ", "%20");

            try {
                responsemsg = ut.httpGet(url);

                if (responsemsg.contains("<ActivityId>")) {
                    sop = "valid";
                    NodeList nl1 = ut.getnode(responsemsg, "TableResult");

                    for (int i = 0; i < nl1.getLength(); i++) {

                        Element e = (Element) nl1.item(i);

                        issueToID = ut.getValue(e,"IssuedTo"); //activity assigned to
                        ActivityName = ut.getValue(e,"ActivityName");
                        ActivityId = ut.getValue(e,"ActivityId");
                        UserMasterId = ut.getValue(e,"UserMasterId"); //activity assigned by
                        Status = ut.getValue(e,"Status");
                        ActualStartDate = ut.getValue(e,"ActualStartDate");
                        ActualEndDate = ut.getValue(e,"ActualEndDate");
                        UserName = ut.getValue(e,"UserName");

                        DMHeaderId = ut.getValue(e,"DMHeaderId");
                        //UserName = getActAssignByname(UserMasterId);

                        SupportersNames supnames = new SupportersNames();
                        supnames.setSupp_Name(SuppUsername);
                        supnames.setIssuedTo(issueToID);

                        StateDetailsList sitem = new StateDetailsList();
                        sitem.SetDMDesc(ActivityName); //activityname
                        sitem.Setdmcstatus(Status); //status

                        //sitem.SetInstallationIdForStateDetailsList(c.getString(8)); //IssuedUserName
                        //  sitem.SetInstallationIdForStateDetailsList(SuppUsername); //IssuedUserName act assigned to
                          sitem.setStationName(SuppUsername); //IssuedUserName act assigned to

                        sitem.SetActivityId(ActivityId); //ActivityId
                        sitem.SetActualStartDate(ConvertDate(ActualStartDate)); //ActualStartDate
                        sitem.SetActualEndDate(ConvertDate(ActualEndDate)); //ActualEndDate

                        //sitem.SetGenrateFileName(c.getString(6)); //UserName
                        sitem.SetGenrateFileName(UserName); //UserName activity assigned by

                        sitem.SetSONumber(issueToID); //UserMasterId activity assigned to ID

                        //sitem.SetIssuedToName(c.getString(4)); //StationName
                        sitem.SetIssuedToName("No data"); //StationName

                        network = NetworkName; //network
                        subnetwork = SuppUsername; //supportername
                        searchResults.add(sitem);
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

                    //display supporters list to adapter

                    supact_ListAdapter = new WorkPlan_ActivitiesListAdapter(parent, searchResults);
                    lstcsn.setAdapter(supact_ListAdapter);

                }
                iv.setVisibility(View.VISIBLE);
                ((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
                        .setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            iv.setVisibility(View.GONE);
            ((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
                    .setVisibility(View.VISIBLE);
        }
    }

    public String getActAssignByname(String UserId){
        String UserName = null, UserMasterId = null;

        //DatabaseHandler db = new DatabaseHandler(this);
        SQLiteDatabase sql = db.getWritableDatabase(); //DISTINCT SubNetworkCode
        /*Cursor c = sql.rawQuery(
                "SELECT DISTINCT SubNetworkCode FROM WorkAssignedTable WHERE UserName='"
                        + SuprtrName, null);*/
        Cursor c = sql.rawQuery("SELECT UserName FROM WorkAssignedTable WHERE UserMasterId='"+UserId+"'", null);

        if(c.getCount()>0){
            c.moveToFirst();
            do{
                 UserName = c.getString(c.getColumnIndex("UserName"));
                // UserMasterId = c.getString(c.getColumnIndex("UserMasterId"));
            }while (c.moveToNext());

        }else {
            Log.e("Count", String.valueOf(c.getCount()));
        }
        return UserName;

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
}
