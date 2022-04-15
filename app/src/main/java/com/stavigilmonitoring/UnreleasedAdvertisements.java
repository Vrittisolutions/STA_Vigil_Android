package com.stavigilmonitoring;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
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
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.new_development.Util_New;
import com.services.AlarmForegroundService;
import com.services.JobService_SyncDataCount;
import com.services.MyAlarmReceiver;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    long AlarmStopTime;
    public static Job myJob = null;
    public static FirebaseJobDispatcher dispatcher ;
    boolean AppCommon = false;

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
            utility ut = new utility();

            sop = "valid";

            String url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetUnreleasedAdv";

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

                        /*SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
                        Date date = null;
                        try {
                            date = dateFormat.parse(ApproveDate);
                        } catch (ParseException ep) {
                            ep.printStackTrace();
                        }
                        long tstamp = date.getTime();
*/
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

                       /* SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);

                        Calendar c1 = Calendar.getInstance();
                        int hour = c1.get(Calendar.HOUR_OF_DAY);
                        int minute = c1.get(Calendar.MINUTE);
                        if(hour > 6 && hour < 19){
                            boolean setAlarmFinal=pref.getBoolean("SetAlarmFinal",false);
                            boolean setAlarm = pref.getBoolean("SetAlarm", false);
                            if(hour == 6 && minute >= 0) {
                                if(setAlarm == true) {

                                    if (setAlarmFinal == true) {
                                        if (tstamp > AlarmStopTime) {
                                            //play alarm again
                                            Intent intent = new Intent(UnreleasedAdvertisements.this, MyAlarmReceiver.class);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(UnreleasedAdvertisements.this, 234324243, intent, 0);
                                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1 * 1000), pendingIntent);
                                            }else {
                                                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1 * 1000), pendingIntent);

                                            }
                                          //  Toast.makeText(UnreleasedAdvertisements.this, "Alarm set in 1 seconds", Toast.LENGTH_LONG).show();
                                            Intent serviceIntent = new Intent(UnreleasedAdvertisements.this, AlarmForegroundService.class);
                                            serviceIntent.putExtra("inputExtra",AdvertisementDesc);
                                            ContextCompat.startForegroundService(UnreleasedAdvertisements.this,serviceIntent);

                                        }
                                    }
                                }
                            }else if(hour == 19 && minute > 0){

                            }else{
                                if(setAlarm == true) {

                                    if (setAlarmFinal == true) {
                                        if (tstamp > AlarmStopTime) {
                                            //play alarm again
                                            Intent intent = new Intent(UnreleasedAdvertisements.this, MyAlarmReceiver.class);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(UnreleasedAdvertisements.this, 234324243, intent, 0);
                                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1 * 1000), pendingIntent);
                                            } else {
                                                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1 * 1000), pendingIntent);

                                            }
                                         //   Toast.makeText(UnreleasedAdvertisements.this, "Alarm set in 1 seconds", Toast.LENGTH_LONG).show();
                                            Intent serviceIntent = new Intent(UnreleasedAdvertisements.this, AlarmForegroundService.class);
                                            serviceIntent.putExtra("inputExtra", AdvertisementDesc);
                                            ContextCompat.startForegroundService(UnreleasedAdvertisements.this,serviceIntent);

                                        }
                                    }
                                }
                            }
                        }*/
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
                    Toast.makeText(UnreleasedAdvertisements.this,"No records found!",Toast.LENGTH_SHORT).show();
                    //ut.showD(parent,"nodata");
                }
                button_refresh_work_Done.setVisibility(View.VISIBLE);
                progressbar.setVisibility(View.GONE);
                pdialogue.dismiss();

                setJobShedulder("SyncDataCountService");


            } catch (Exception e) {
                e.printStackTrace();
                button_refresh_work_Done.setVisibility(View.VISIBLE);
                progressbar.setVisibility(View.GONE);
                pdialogue.dismiss();
                Toast.makeText(UnreleasedAdvertisements.this,"No data available",Toast.LENGTH_LONG).show();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setJobShedulder(String key) {

        // checkBatteryOptimized();
        if(myJob == null) {
            dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

            if(key.equalsIgnoreCase("DMCertificateService")){
                //callJobDispacher_DMCertificate();

            }else if(key.equalsIgnoreCase("SoundLevelService")){
                //callJobDispacher_soundlevel();

            }else if(key.equalsIgnoreCase("SyncDataCountService")){
                callJobDispacher_SyncDataCount();
            }

        }
        else{
			/*if(!AppCommon.getInstance(this).isServiceIsStart()){
				dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
				callJobDispacher();
			}else {
				dispatcher.cancelAll();
				dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
				myJob = null;
				callJobDispacher();
			}*/

            if(AppCommon){
                dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

                if(key.equalsIgnoreCase("DMCertificateService")){
                    // callJobDispacher_DMCertificate();

                }else if(key.equalsIgnoreCase("SoundLevelService")){
                    //callJobDispacher_soundlevel();

                }else if(key.equalsIgnoreCase("SyncDataCountService")){
                    callJobDispacher_SyncDataCount();
                }

            }else {
                AppCommon = true;
                dispatcher.cancelAll();
                dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
                myJob = null;

                if(key.equalsIgnoreCase("DMCertificateService")){
                    //callJobDispacher_DMCertificate();

                }else if(key.equalsIgnoreCase("SoundLevelService")){
                    //callJobDispacher_soundlevel();

                }else if(key.equalsIgnoreCase("SyncDataCountService")){
                    callJobDispacher_SyncDataCount();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void callJobDispacher_SyncDataCount() {
        Util_New.scheduleJob(UnreleasedAdvertisements.this);
/*
        try{


            myJob = dispatcher.newJobBuilder()
                    // the JobService that will be called
                    .setService(JobService_SyncDataCount.class)
                    // uniquely identifies the job
                    .setTag("test")
                    // one-off job
                    .setRecurring(true)
                    // don't persist past a device reboot
                    .setLifetime(Lifetime.FOREVER)

                    // start between 0 and 60 seconds from now
                    .setTrigger(Trigger.executionWindow(180, 240))
                    // don't overwrite an existing job with the same tag
                    .setReplaceCurrent(true)
                    // retry with exponential backoff
                    .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                    // constraints that need to be satisfied for the job to run
                    .setConstraints(

                            // only run on an unmetered network
                            Constraint.ON_ANY_NETWORK
                            // only run when the device is charging
                           //Constraint.DEVICE_IDLE
                    )
                    .build();

            dispatcher.schedule(myJob);

            //AppCommon.getInstance(this).setServiceStarted(true);
        }catch (Exception e){
            Log.e("SERVICECALL"," -Exception-> "+e);
        }
*/
    }



}
