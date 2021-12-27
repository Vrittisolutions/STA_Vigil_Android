package com.services;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.beanclasses.AdvVideoDataBean;
import com.database.DBInterface;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.stavigilmonitoring.Config;
import com.stavigilmonitoring.DatabaseHandler;
import com.beanclasses.LmsConnectionStatewiseBean;
import com.stavigilmonitoring.R;
import com.beanclasses.StatelevelList;
import com.beanclasses.TvStatusStateBean;
import com.stavigilmonitoring.SelectMenu;
import com.stavigilmonitoring.utility;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.stavigilmonitoring.utility.OpenConnection;

public class JobService_SyncDataCount extends JobService {

    private static final String TAG1 = "MyJobService_SyncDataCount";

    private static final int NOTIFICATION = 1337;
    private NotificationManager mNotificationManager;
    String urlStringToken="", urlStringToken2="";
    private int notificationID = 100;
    String url, mobno, activityid, projectid, curdate, fromtime, totime, desc,
            iscomplete;
    String InstallationId = " ";
    utility ut = new utility();
    static SimpleDateFormat dff;
    static String Ldate;
    String responsemsg = "k";
    boolean isc;
    String msg;
    String sumdata2 ="1";

    private String link;
    private Date date;
    private String datestring;
    int totalstation = 0;
    DatabaseHandler db;
    SQLiteDatabase sql;

    /*unreleased adv*/
    String AdvertisementCode="",AdvertisementDesc="", ApproveDate ="", SOPReleaseDate = "",
            SOHeaderStatus = "", SoNumber = "", NetworkCode ="", Statuschangedate ="";
    ArrayList<AdvVideoDataBean> list_advdata;

    long AlarmStopTime;
    boolean setAlarm = false;
    boolean setAlarmFinal = false;
    boolean setAlarmFinalNonReportStation = false;
    boolean setAlarmFinalCSN = false;


    @Override
    public boolean onStartJob(JobParameters job) {
        Log.e("Back Timer : ","JobServiceStarted");

        init();

        return true;
    }

    @SuppressLint("LongLogTag")
    @Override
    public boolean onStopJob(JobParameters job) {
        Log.d(TAG1, "Job cancelled!");
        return false;
    }

    public void init(){

        registerReceiver(mHandleMessageReceiver, new IntentFilter(Config.DISPLAY_MESSAGE_ACTION));

        System.out.println("...............csn service started............");
        Log.e("Tag", " ******* WORKING ON SYNCDATA *********");

        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();

        db = new DatabaseHandler(getBaseContext());
        sql = db.getWritableDatabase();



        Calendar cal = Calendar.getInstance();
        if (cal.HOUR <= 20 && cal.HOUR >= 6 && isnet())
            if (isnet()){
                new UploadTS_new().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				/*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					new UploadTS_new().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			     else
			         new UploadTS_new().execute();*/
            }

        list_advdata = new ArrayList<AdvVideoDataBean>();



    }

    protected boolean isnet() {
        // TODO Auto-generated method stub
        Context context = this.getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public class UploadTS_new extends AsyncTask<String, Void, String> {
        //SQLiteDatabase sql = db.getWritableDatabase();
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {

            try {
                Log.e("Tag", " ******* WORKING ON SYNCDATA *********");
                String xx = "";

                Cursor cusers = sql.rawQuery(
                        "SELECT *   FROM ConnectionStatusUser1", null);
                if (cusers.getCount() == 0) {
                    String url = "http://vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetAllStationWithUserName_Android?Mobile="
                            + mobno;

                    Log.e("csn status", "url : " + url);
                    url = url.replaceAll(" ", "%20");
                    try {
                        responsemsg = ut.httpGet(url);
                        Log.e("csn status", "resmsg : " + responsemsg);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        dff = new SimpleDateFormat("HH:mm:ss");
                        Ldate = dff.format(new Date());

                        StackTraceElement l = new Exception().getStackTrace()[0];
                        System.out.println(l.getClassName() + "/"
                                + l.getMethodName() + ":" + l.getLineNumber());
                        ut = new utility();
                        if (!ut.checkErrLogFile()) {

                            ut.ErrLogFile();
                        }
                        if (ut.checkErrLogFile()) {
                            ut.addErrLog(l.getClassName() + "/"
                                    + l.getMethodName() + ":"
                                    + l.getLineNumber() + "	" + e.getMessage()
                                    + " " + Ldate);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        dff = new SimpleDateFormat("HH:mm:ss");
                        Ldate = dff.format(new Date());

                        StackTraceElement l = new Exception().getStackTrace()[0];
                        System.out.println(l.getClassName() + "/"
                                + l.getMethodName() + ":" + l.getLineNumber());
                        ut = new utility();
                        if (!ut.checkErrLogFile()) {

                            ut.ErrLogFile();
                        }
                        if (ut.checkErrLogFile()) {
                            ut.addErrLog(l.getClassName() + "/"
                                    + l.getMethodName() + ":"
                                    + l.getLineNumber() + "	" + e.getMessage()
                                    + " " + Ldate);
                        }

                    }

                    if (responsemsg.contains("<IId>")) {
                        // sop = "valid";

                        //sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusUser1");
                        //sql.execSQL(ut.getConnectionStatusUser1());

                        sql.delete("ConnectionStatusUser1",null,null);

                        Cursor c = sql.rawQuery(
                                "SELECT *   FROM ConnectionStatusUser1", null);
                        ContentValues values = new ContentValues();
                        NodeList nl = ut.getnode(responsemsg, "Table1");
                        String msg = "";
                        String columnName, columnValue;
                        //Log.e("sts main...", " fetch data : " + nl.getLength());
                        for (int i = 0; i < nl.getLength(); i++) {
                            Element e = (Element) nl.item(i);
                            for (int j = 0; j < c.getColumnCount(); j++) {
                                columnName = c.getColumnName(j);

                                String ncolumnname = "";
                                if (columnName
                                        .equalsIgnoreCase("InstallationId"))
                                    ncolumnname = "IId";
                                else if (columnName
                                        .equalsIgnoreCase("InstallationDesc"))
                                    ncolumnname = "SN";
                                else if (columnName.equalsIgnoreCase("UserName"))
                                    ncolumnname = "UN";
                                else if (columnName
                                        .equalsIgnoreCase("MobileNo"))
                                    ncolumnname = "UN1";

                                else if (columnName.equalsIgnoreCase("SUP"))
                                    ncolumnname = "SUP";

                                columnValue = ut.getValue(e, ncolumnname);

                                if(columnValue.contains(" /")){
                                    columnValue = columnValue.replaceAll(" /","/");
                                }

                                if (ncolumnname == "SUP"
                                        && columnValue.contains(",")) {
                                    columnValue = columnValue.substring(0,columnValue.indexOf(","));
                                }
                                values.put(columnName, columnValue);
                                // Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
                            }
                            sql.insert("ConnectionStatusUser1", null, values);
                        }

                    }
                }

                // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                String url = "http://vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetAllStation_Android";

                Log.e("csn status", "url : " + url);
                Log.e("Tag", " ******* WORKING ON SYNCDATA *********");
                url = url.replaceAll(" ", "%20");
                try {
                    responsemsg = utility.httpGet(url);
                    Log.e("csn status", "resmsg : " + responsemsg);
                }
                catch (NullPointerException e) {
                    e.printStackTrace();
                    dff = new SimpleDateFormat("HH:mm:ss");
                    Ldate = dff.format(new Date());

                    StackTraceElement l = new Exception().getStackTrace()[0];
                    System.out.println(l.getClassName() + "/"
                            + l.getMethodName() + ":" + l.getLineNumber());
                    ut = new utility();
                    if (!ut.checkErrLogFile()) {

                        ut.ErrLogFile();
                    }
                    if (ut.checkErrLogFile()) {
                        ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                                + ":" + l.getLineNumber() + "	"
                                + e.getMessage() + " " + Ldate);
                    }

                }
                catch (IOException e) {
                    e.printStackTrace();
                    dff = new SimpleDateFormat("HH:mm:ss");
                    Ldate = dff.format(new Date());

                    StackTraceElement l = new Exception().getStackTrace()[0];
                    System.out.println(l.getClassName() + "/"
                            + l.getMethodName() + ":" + l.getLineNumber());
                    ut = new utility();
                    if (!ut.checkErrLogFile()) {

                        ut.ErrLogFile();
                    }
                    if (ut.checkErrLogFile()) {
                        ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                                + ":" + l.getLineNumber() + "	"
                                + e.getMessage() + " " + Ldate);
                    }

                }

                //db = new DatabaseHandler(getBaseContext());
                //sql = db.getWritableDatabase();
                //sql.beginTransaction();

                try {

                    if (responsemsg.contains("<NetworkCode>")) {

                        String columnName, columnValue;

                        //sql.execSQL("DROP TABLE IF EXISTS AllStation");
                        //sql.execSQL(ut.getAllStation());
                        sql.delete("AllStation",null,null);

                        Cursor cur = sql.rawQuery("SELECT * FROM AllStation", null);
                        ContentValues values1 = new ContentValues();
                        NodeList nl1 = ut.getnode(responsemsg, "Table1");
                        // String msg = "";
                        // String columnName, columnValue;
					/*Log.e("All Station data...",
							" fetch data : " + nl1.getLength());*/
                        for (int i = 0; i < nl1.getLength(); i++) {
                            Element e = (Element) nl1.item(i);
                            for (int j = 0; j < cur.getColumnCount(); j++) {
                                columnName = cur.getColumnName(j);

                                columnValue = ut.getValue(e, columnName);
                                values1.put(columnName, columnValue);

                                // Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
                            }
                            sql.insert("AllStation", null, values1);
                        }
                    }

                    //sql.setTransactionSuccessful();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    //sql.endTransaction();
                }

                // utility ut = new utility();

                url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/GetListOfPendingDownloadingAdvertisment?Mobile="
                        + mobno + "&NetworkCode='ksrtc'";
                Log.e("Tag", " ******* WORKING ON SYNCDATA *********");

                Log.e("csn status", "url : " + url);
                url = url.replaceAll(" ", "%20");
                try {
                    responsemsg = utility.httpGet(url);
                    Log.e("csn status", "resmsg : " + responsemsg);
                    //db = new DatabaseHandler(getBaseContext());
                    //sql = db.getWritableDatabase();

                    if (responsemsg.contains("<instalationid>")) {

                        String columnName, columnValue;
                        //sql.execSQL("DROP TABLE IF EXISTS PendingClips");
                        //sql.execSQL(ut.getPendingClips());
                        sql.delete("PendingClips",null,null);

                        Cursor cur = sql.rawQuery("SELECT * FROM PendingClips",
                                null);
                        ContentValues values1 = new ContentValues();
                        NodeList nl1 = ut.getnode(responsemsg, "Table1");
                        for (int i = 0; i < nl1.getLength(); i++) {
                            Element e = (Element) nl1.item(i);
                            for (int j = 0; j < cur.getColumnCount(); j++) {
                                columnName = cur.getColumnName(j);

                                columnValue = ut.getValue(e, columnName);
                                values1.put(columnName, columnValue);

                            }
                            sql.insert("PendingClips", null, values1);
                        }

                        cur.close();

                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    dff = new SimpleDateFormat("HH:mm:ss");
                    Ldate = dff.format(new Date());

                    StackTraceElement l = new Exception().getStackTrace()[0];
                    System.out.println(l.getClassName() + "/"
                            + l.getMethodName() + ":" + l.getLineNumber());
                    ut = new utility();
                    if (!ut.checkErrLogFile()) {

                        ut.ErrLogFile();
                    }
                    if (ut.checkErrLogFile()) {
                        ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                                + ":" + l.getLineNumber() + "	"
                                + e.getMessage() + " " + Ldate);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    dff = new SimpleDateFormat("HH:mm:ss");
                    Ldate = dff.format(new Date());

                    StackTraceElement l = new Exception().getStackTrace()[0];
                    System.out.println(l.getClassName() + "/"
                            + l.getMethodName() + ":" + l.getLineNumber());
                    ut = new utility();
                    if (!ut.checkErrLogFile()) {

                        ut.ErrLogFile();
                    }
                    if (ut.checkErrLogFile()) {
                        ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                                + ":" + l.getLineNumber() + "	"
                                + e.getMessage() + " " + Ldate);
                    }

                }

                //Connection Status
                url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetCSNStatus_Android_new?Mobile="
                        + mobno;

                Log.e("csn status", "url : " + url);
                Log.e("Tag", " ******* WORKING ON SYNCDATA *********");
                url = url.replaceAll(" ", "%20");
                try {
                    responsemsg = ut.httpGet(url);
                    Log.e("csn status", "resmsg : " + responsemsg);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    dff = new SimpleDateFormat("HH:mm:ss");
                    Ldate = dff.format(new Date());

                    StackTraceElement l = new Exception().getStackTrace()[0];
                    System.out.println(l.getClassName() + "/"
                            + l.getMethodName() + ":" + l.getLineNumber());
                    ut = new utility();
                    if (!ut.checkErrLogFile()) {

                        ut.ErrLogFile();
                    }
                    if (ut.checkErrLogFile()) {
                        ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                                + ":" + l.getLineNumber() + "	"
                                + e.getMessage() + " " + Ldate);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    dff = new SimpleDateFormat("HH:mm:ss");
                    Ldate = dff.format(new Date());

                    StackTraceElement l = new Exception().getStackTrace()[0];
                    System.out.println(l.getClassName() + "/"
                            + l.getMethodName() + ":" + l.getLineNumber());
                    ut = new utility();
                    if (!ut.checkErrLogFile()) {

                        ut.ErrLogFile();
                    }
                    if (ut.checkErrLogFile()) {
                        ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                                + ":" + l.getLineNumber() + "	"
                                + e.getMessage() + " " + Ldate);
                    }

                }

                //db = new DatabaseHandler(getBaseContext());
                //sql = db.getWritableDatabase();

                if (responsemsg.contains("<A>")) {
                    // sop = "valid";
                    String columnName, columnValue;
                    //sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusUser");
                    //sql.execSQL(ut.getConnectionStatusUser());
                    sql.delete("ConnectionStatusUser",null,null);

                    Cursor cur = sql.rawQuery(
                            "SELECT *   FROM ConnectionStatusUser", null);
                    ContentValues values1 = new ContentValues();
                    NodeList nl1 = ut.getnode(responsemsg, "Table1");
                    // String msg = "";
                    // String columnName, columnValue;
                    //Log.e("sts main...", " fetch data : " + nl1.getLength());
                    for (int i = 0; i < nl1.getLength(); i++) {
                        String conn = "invalid";
                        Element e = (Element) nl1.item(i);
                        for (int j = 0; j < cur.getColumnCount(); j++) {
                            columnName = cur.getColumnName(j);

                            String ncolumnname = "";
                            if (columnName.equalsIgnoreCase("InstallationId"))
                                ncolumnname = "A";
                            else if (columnName.equalsIgnoreCase("ServerTime"))
                                ncolumnname = "B";
                            else if (columnName.equalsIgnoreCase("StartTime"))
                                ncolumnname = "C";
                            else if (columnName.equalsIgnoreCase("EndTime"))
                                ncolumnname = "D";
                            else if (columnName.equalsIgnoreCase("Remarks"))
                                ncolumnname = "E";
                            else if (columnName
                                    .equalsIgnoreCase("InstallationDesc"))
                                ncolumnname = "F";
                            else if (columnName.equalsIgnoreCase("TVStatus"))
                                ncolumnname = "G";
                                // else if (columnName
                                // .equalsIgnoreCase("Last7DaysPerFormance"))
                                // ncolumnname = "H";
                                // else if (columnName
                                // .equalsIgnoreCase("QuickHealStatus"))
                                // ncolumnname = "I";
                            else if (columnName.equalsIgnoreCase("STAVersion"))
                                ncolumnname = "J";
                            else if (columnName
                                    .equalsIgnoreCase("AscOrderServerTime"))
                                ncolumnname = "K";
                            else if (columnName
                                    .equalsIgnoreCase("LatestDowntimeReason"))
                                ncolumnname = "L";
                                // else if (columnName.equalsIgnoreCase("UserName"))
                                // ncolumnname = "M";
                            else if (columnName.equalsIgnoreCase("Type"))
                                ncolumnname = "N";
                            else if (columnName
                                    .equalsIgnoreCase("SubNetworkCode"))
                                ncolumnname = "R";
                            // else if (columnName
                            // .equalsIgnoreCase("SubHeadPH_No"))
                            // ncolumnname = "O";
                            // else if (columnName
                            // .equalsIgnoreCase("SupportAgencyName"))
                            // ncolumnname = "P";

                            columnValue = ut.getValue(e, ncolumnname);

                            if (columnName.equalsIgnoreCase("ServerTime")) {
                                try {
                                    Calendar cal = Calendar.getInstance();
                                    // SimpleDateFormat format = new
                                    // SimpleDateFormat("MM/dd/yyyy HH:mm:ss aa");

                                    SimpleDateFormat format = new SimpleDateFormat(
                                            "MM/dd/yyyy hh:mm:ss aa");

                                    Date Startdate = format.parse(columnValue);
                                    Date Enddate = cal.getTime();
                                    long diff = Enddate.getTime()
                                            - Startdate.getTime();
                                    long diffSeconds = diff / 1000 % 60;
                                    long diffMinutes = diff / (60 * 1000) % 60;
                                    long diffHours = diff / (60 * 60 * 1000) % 24;
                                    long diffDays = diff / (24 * 60 * 60 * 1000);

									/*Log.e("getdetails", "sd : " + Startdate
											+ " ed: " + Enddate + " d: "
											+ diffDays + " h: " + diffHours
											+ " m:" + diffMinutes);*/

                                    //if time is greater than 1 then ring alarm

                                    if (diffDays == 0 && diffHours == 0 && diffMinutes <= 15) {

                                    } else {
                                        if(setAlarmFinalCSN){
                                            //play alarm again
                                            Intent intent = new Intent(JobService_SyncDataCount.this, MyAlarmReceiver.class);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(JobService_SyncDataCount.this, 234324243, intent, 0);
                                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1 * 1000), pendingIntent);
                                            Toast.makeText(JobService_SyncDataCount.this, "Alarm set in 1 seconds", Toast.LENGTH_LONG).show();
                                            Intent serviceIntent = new Intent(JobService_SyncDataCount.this, AlarmForegroundService.class);
                                            serviceIntent.putExtra("inputExtra",columnName
                                                    .equalsIgnoreCase("InstallationDesc"));

                                            ContextCompat.startForegroundService(JobService_SyncDataCount.this, serviceIntent);
                                        }

                                        conn = "valid";
                                    }
                                } catch (Exception ex) {

                                }
                            }

                            values1.put(columnName, columnValue);

                            // Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
                        }

                        if (conn == "valid") {
                            sql.insert("ConnectionStatusUser", null, values1);
                        }

                    }

                    cur.close();

                } else {
                    System.out
                            .println("--------- invalid for project list --- ");
                }
            } catch (Exception e) {
                dff = new SimpleDateFormat("HH:mm:ss");
                Ldate = dff.format(new Date());

                StackTraceElement l = new Exception().getStackTrace()[0];
                System.out.println(l.getClassName() + "/" + l.getMethodName()
                        + ":" + l.getLineNumber());
                ut = new utility();
                if (!ut.checkErrLogFile()) {

                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()) {
                    ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                            + ":" + l.getLineNumber() + "	" + e.getMessage()
                            + " " + Ldate);
                }

            }
            /*--------------------------------------------------------------------------------------------------------------------------------*/

            url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/GetTVStatus_Android_new?Mobile="
                    + mobno;

            Log.e("csn status", "url : " + url);
            Log.e("Tag", " ******* WORKING ON SYNCDATA *********");
            url = url.replaceAll(" ", "%20");
            try {
                responsemsg = ut.httpGet(url);
                Log.e("csn status", "resmsg : " + responsemsg);
                // db = new DatabaseHandler(getBaseContext());
                //sql = db.getWritableDatabase();

                if (responsemsg.contains("<A>")) {
                    String columnName, columnValue;
                    /*
                     * DatabaseHandler db = new
                     * DatabaseHandler(getBaseContext()); SQLiteDatabase sql =
                     * db.getWritableDatabase();
                     */
                    //sql.execSQL("DROP TABLE IF EXISTS TvStatus");
                    //sql.execSQL(ut.getTvStatus());
                    sql.delete("TvStatus",null,null);

                    ContentValues values1 = new ContentValues();
                    NodeList nl1 = ut.getnode(responsemsg, "Table1");
                    Cursor cur = sql.rawQuery("SELECT *   FROM TvStatus", null);
                    values1 = new ContentValues();
                    nl1 = ut.getnode(responsemsg, "Table1");
                    // String msg = "";
                    // String columnName, columnValue;
                    //Log.e("sts main...", " fetch data : " + nl1.getLength());
                    for (int i = 0; i < nl1.getLength(); i++) {
                        Element e = (Element) nl1.item(i);
                        for (int j = 0; j < cur.getColumnCount(); j++) {
                            columnName = cur.getColumnName(j);

                            String ncolumnname = "";
                            if (columnName.equalsIgnoreCase("InstallationId"))
                                ncolumnname = "A";
                            else if (columnName
                                    .equalsIgnoreCase("TVStatusReason"))
                                ncolumnname = "G";
                            else if (columnName.equalsIgnoreCase("TVStatus"))
                                ncolumnname = "J";
                            else if (columnName.equalsIgnoreCase("Type"))
                                ncolumnname = "P";
                            else
                                ncolumnname = columnName;
                            columnValue = ut.getValue(e, ncolumnname);
                            values1.put(columnName, columnValue);

							/*Log.e("DownloadxmlsDataURL_new...on back....",
									" count i: " + i + "  j:" + j);*/
                        }
                        sql.insert("TvStatus", null, values1);
                    }

                    cur.close();

                } else {
                    System.out
                            .println("--------- invalid for project list --- ");
                }
            }

            catch (NullPointerException e) {
                e.printStackTrace();
                dff = new SimpleDateFormat("HH:mm:ss");
                Ldate = dff.format(new Date());

                StackTraceElement l = new Exception().getStackTrace()[0];
                System.out.println(l.getClassName() + "/" + l.getMethodName()
                        + ":" + l.getLineNumber());
                ut = new utility();
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
                ut = new utility();
                if (!ut.checkErrLogFile()) {

                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()) {
                    ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                            + ":" + l.getLineNumber() + "	" + e.getMessage()
                            + " " + Ldate);
                }

            }

            /*--------------------------------------------------------------------------------------------------------------------------------*/

            try {

                String xx = "";
                String url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/GetNonReportedAdvt_Android_new?Mobile="
                        + mobno;

                url = url.replaceAll(" ", "%20");
                Log.e("Tag", " ******* WORKING ON SYNCDATA *********");

                try {
                    responsemsg = ut.httpGet(url);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    dff = new SimpleDateFormat("HH:mm:ss");
                    Ldate = dff.format(new Date());

                    StackTraceElement l = new Exception().getStackTrace()[0];
                    System.out.println(l.getClassName() + "/"
                            + l.getMethodName() + ":" + l.getLineNumber());
                    ut = new utility();
                    if (!ut.checkErrLogFile()) {

                        ut.ErrLogFile();
                    }
                    if (ut.checkErrLogFile()) {
                        ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                                + ":" + l.getLineNumber() + "	"
                                + e.getMessage() + " " + Ldate);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    dff = new SimpleDateFormat("HH:mm:ss");
                    Ldate = dff.format(new Date());

                    StackTraceElement l = new Exception().getStackTrace()[0];
                    System.out.println(l.getClassName() + "/"
                            + l.getMethodName() + ":" + l.getLineNumber());
                    ut = new utility();
                    if (!ut.checkErrLogFile()) {

                        ut.ErrLogFile();
                    }
                    if (ut.checkErrLogFile()) {
                        ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                                + ":" + l.getLineNumber() + "	"
                                + e.getMessage() + " " + Ldate);
                    }

                }
                //db = new DatabaseHandler(getBaseContext());
                //sql = db.getWritableDatabase();

                if (responsemsg.contains("<A>")) {

                    /*
                     * DatabaseHandler db = new
                     * DatabaseHandler(getBaseContext()); SQLiteDatabase sql =
                     * db.getWritableDatabase();
                     */
                    //sql.execSQL("DROP TABLE IF EXISTS NonrepeatedAd");
                    //sql.execSQL(ut.getNonrepeatedAd());
                    sql.delete("NonrepeatedAd",null,null);

                    Cursor c = sql.rawQuery("SELECT *   FROM NonrepeatedAd",
                            null);
                    ContentValues values = new ContentValues();
                    NodeList nl = ut.getnode(responsemsg, "Table1");
                    String msg = "";
                    String columnName = null, columnValue;
                    for (int i = 0; i < nl.getLength(); i++) {
                        Element e = (Element) nl.item(i);
                        for (int j = 0; j < c.getColumnCount(); j++) {
                            columnName = c.getColumnName(j);

                            String ncolumnname = "";
                            if (columnName.equalsIgnoreCase("StationMasterId"))
                                ncolumnname = "A";
                            else if (columnName
                                    .equalsIgnoreCase("AdvertisementCode"))
                                ncolumnname = "B";
                            else if (columnName
                                    .equalsIgnoreCase("AdvertisementDesc"))
                                ncolumnname = "C";
                            else if (columnName
                                    .equalsIgnoreCase("InstallationDesc"))
                                ncolumnname = "D";
                            else if (columnName
                                    .equalsIgnoreCase("EffectiveDateFrom"))
                                ncolumnname = "E";
                            else if (columnName
                                    .equalsIgnoreCase("EffectiveDateTo"))
                                ncolumnname = "F";
                            //G - network code
                            else if (columnName.equalsIgnoreCase("Type"))
                                ncolumnname = "G";
                            else if (columnName.equalsIgnoreCase("ClipId"))
                                ncolumnname = "H";
                            else if (columnName
                                    .equalsIgnoreCase("IsmasterRecordDownloaded"))
                                ncolumnname = "I";
                            else if (columnName
                                    .equalsIgnoreCase("IsDetailRecordDownloaded"))
                                ncolumnname = "J";
                            else if (columnName
                                    .equalsIgnoreCase("IsClipMasterRecordDownloaded"))
                                ncolumnname = "K";
                            else if (columnName
                                    .equalsIgnoreCase("InstallationCount"))
                                ncolumnname = "L";
                            else if (columnName
                                    .equalsIgnoreCase("LastServerTime"))
                                ncolumnname = "M";
                            else if (columnName
                                    .equalsIgnoreCase("FirstReportingDate"))
                                ncolumnname = "N";
                            else if (columnName
                                    .equalsIgnoreCase("LatestAddeDate"))
                                ncolumnname = "O";
                            else if (columnName.equalsIgnoreCase("CSR"))
                                ncolumnname = "CSR";
                            else if (columnName.equalsIgnoreCase("LA"))
                                ncolumnname = "LA";
                            else if (columnName.equalsIgnoreCase("LB"))
                                ncolumnname = "LB";
                            else if (columnName.equalsIgnoreCase("LBR"))
                                ncolumnname = "LBR";

                            columnValue = ut.getValue(e, ncolumnname);
                            values.put(columnName, columnValue);
                        }
                        sql.insert("NonrepeatedAd", null, values);
                        if(setAlarmFinalNonReportStation){
                            Intent intent = new Intent(JobService_SyncDataCount.this, MyAlarmReceiver.class);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(JobService_SyncDataCount.this, 234324243, intent, 0);
                                           AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1 * 1000), pendingIntent);
                                           Toast.makeText(JobService_SyncDataCount.this, "Alarm set in 1 seconds", Toast.LENGTH_LONG).show();
                            Intent serviceIntent = new Intent(JobService_SyncDataCount.this, AlarmForegroundService.class);
                            serviceIntent.putExtra("inputExtra",columnName
                                    .equalsIgnoreCase("AdvertisementDesc"));

                            ContextCompat.startForegroundService(JobService_SyncDataCount.this, serviceIntent);
                   
                        }
                        //when inserted set alarm
                    }

                    c.close();

                }
            } catch (Exception e) {
                dff = new SimpleDateFormat("HH:mm:ss");
                Ldate = dff.format(new Date());

                StackTraceElement l = new Exception().getStackTrace()[0];
                System.out.println(l.getClassName() + "/" + l.getMethodName()
                        + ":" + l.getLineNumber());
                ut = new utility();
                if (!ut.checkErrLogFile()) {

                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()) {
                    ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                            + ":" + l.getLineNumber() + "	" + e.getMessage()
                            + " " + Ldate);
                }

            }


            // **********************************************************************************************************/
            url  = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/AlertCount?Mobile="
                    +mobno;

            Log.e("Alert Count", "url : " + url);
            url = url.replaceAll(" ", "%20");

            //db = new DatabaseHandler(getBaseContext());
            //sql = db.getWritableDatabase();
            try {
                responsemsg = utility.httpGet(url);

                Log.e("Alert Count", "resmsg : " + responsemsg);
            } catch (NullPointerException e) {
                e.printStackTrace();
                dff = new SimpleDateFormat("HH:mm:ss");
                Ldate = dff.format(new Date());

                StackTraceElement l = new Exception().getStackTrace()[0];
                System.out.println(l.getClassName() + "/"
                        + l.getMethodName() + ":" + l.getLineNumber());
                ut = new utility();
                if (!ut.checkErrLogFile()) {

                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()) {
                    ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                            + ":" + l.getLineNumber() + "	"
                            + e.getMessage() + " " + Ldate);
                }

            } catch (IOException e) {
                e.printStackTrace();
                dff = new SimpleDateFormat("HH:mm:ss");
                Ldate = dff.format(new Date());

                StackTraceElement l = new Exception().getStackTrace()[0];
                System.out.println(l.getClassName() + "/"
                        + l.getMethodName() + ":" + l.getLineNumber());
                ut = new utility();
                if (!ut.checkErrLogFile()) {

                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()) {
                    ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                            + ":" + l.getLineNumber() + "	"
                            + e.getMessage() + " " + Ldate);
                }

            }
            //String columnName, columnValue;

            //sql.execSQL("DROP TABLE IF EXISTS AlrtCountTable");
            Log.e("AlrtCountTable", "Drop DONE");
            //sql.execSQL(ut.getAlrtCountTable());

            sql.delete("AlrtCountTable",null,null);

            Log.e("AlrtCountTable", "CREATE DONE");
            if (responsemsg.contains("<InstalationId>")) {
                String columnName, columnValue;
                Cursor cur = sql.rawQuery("SELECT * FROM AlrtCountTable", null);
                ContentValues values1 = new ContentValues();
                NodeList nl1 = ut.getnode(responsemsg, "TableResult");
                // String msg = "";
                // String columnName, columnValue;
                //Log.e("All Station data...",
                //" fetch data : " + nl1.getLength());
                for (int i = 0; i < nl1.getLength(); i++) {
                    Element e = (Element) nl1.item(i);
                    for (int j = 0; j < cur.getColumnCount(); j++) {
                        columnName = cur.getColumnName(j);

                        columnValue = ut.getValue(e, columnName);
                        values1.put(columnName, columnValue);

                        // Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
                    }
                    sql.insert("AlrtCountTable", null, values1);
                }
            } else if (responsemsg.contains("Record are not Found...!")){
                sumdata2 = "0";
            }

			/*SharedPreferences pref = getApplicationContext()
					.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
			Editor editor = pref.edit();
			editor.putString("csnStatusCount", String.valueOf(lstStn.size()));
			editor.commit();
			Log.e("get details.....", "---kk add STn : " + lstStn.size());*/
            //updateAlertCount();

            //////////////////////////////////////////////////////////////////////////////////////
            String sop;
            url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/AlertGet?InstallationId=" +"" +"&AddedBy=" + mobno;

            Log.e("csn status", "url : " + url);
            url = url.replaceAll(" ", "%20");

            try {
                String responsemsg = ut.httpGet(url);
                //sql.execSQL("DROP TABLE IF EXISTS AlrtListTable");
                //sql.execSQL(ut.getAlrtListTable());
                sql.delete("AlrtListTable",null,null);
                Log.e("ALERT GETs", "resmsg : " + responsemsg);

                if (responsemsg.contains("<AlertId>")) {
                    sop = "valid";
                    String columnName, columnValue;
                    /*
                     * DatabaseHandler db = new
                     * DatabaseHandler(getBaseContext()); SQLiteDatabase sql =
                     * db.getWritableDatabase();
                     */


                    Cursor cur = sql.rawQuery("SELECT * FROM AlrtListTable",null);
                    ContentValues values1 = new ContentValues();
                    NodeList nl1 = ut.getnode(responsemsg, "TableResult");
                    Log.e("All AlrtListTable data",
                            " fetch data : " + nl1.getLength());
                    for (int i = 0; i < nl1.getLength(); i++) {
                        Element e = (Element) nl1.item(i);
                        for (int j = 0; j < cur.getColumnCount(); j++) {
                            columnName = cur.getColumnName(j);

                            columnValue = ut.getValue(e, columnName);
                            values1.put(columnName, columnValue);

                            // Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
                        }
                        sql.insert("AlrtListTable",
                                null, values1);
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
                ut = new utility();
                if (!ut.checkErrLogFile()) {
                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()) {
                    ut.addErrLog(l.getClassName() + "/" + l.getMethodName() + ":" + l.getLineNumber() + "	" + e.getMessage() + " " + Ldate);
                }

            } catch (IOException e) {
                e.printStackTrace();
                dff = new SimpleDateFormat("HH:mm:ss");
                Ldate = dff.format(new Date());

                StackTraceElement l = new Exception().getStackTrace()[0];
                System.out.println(l.getClassName() + "/" + l.getMethodName() + ":" + l.getLineNumber());
                ut = new utility();
                if (!ut.checkErrLogFile()) {
                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()) {
                    ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                            + ":" + l.getLineNumber() + "	" + e.getMessage()
                            + " " + Ldate);
                }
            }

// **********************************************************************************************************/
            /*unreleased advs*/
            list_advdata.clear();
            url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetUnreleasedAdv";

            Log.e("unreleased advs", "url : " + url);
            url = url.replaceAll(" ", "%20");

            try {
                String responsemsg = ut.httpGet(url);

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

                       //String strDate = "22 Jul 2021 11:41:07:000";
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
                        Date date = null;
                        try {
                            date = dateFormat.parse(ApproveDate);
                        } catch (ParseException ep) {
                            ep.printStackTrace();
                        }

                        long tstamp = date.getTime();

                        AdvVideoDataBean adv = new AdvVideoDataBean();
                        adv.setAdvertisementCode(AdvertisementCode);
                        adv.setAdvertisementDesc(AdvertisementDesc);
                        adv.setApproveDate(ApproveDate);
                        adv.setSOPReleaseDate(SOPReleaseDate);
                        adv.setSOHeaderStatus(SOHeaderStatus);
                        adv.setSoNumber(SoNumber);
                        adv.setNetworkCode(NetworkCode);
                        adv.setStatuschangedate(Statuschangedate);

                        list_advdata.add(adv);

                        Calendar c1 = Calendar.getInstance();
                        int hour = c1.get(Calendar.HOUR_OF_DAY);
                        int minute = c1.get(Calendar.MINUTE);
                        if(hour > 6 && hour < 19){
                            if(hour == 6 && minute >= 0) {
                                if(setAlarm == true) {

                                    if (setAlarmFinal == true) {
                                        if (tstamp > AlarmStopTime) {
                                            //play alarm again
                                            Intent intent = new Intent(JobService_SyncDataCount.this, MyAlarmReceiver.class);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(JobService_SyncDataCount.this, 234324243, intent, 0);
                                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1 * 1000), pendingIntent);
                                            Toast.makeText(JobService_SyncDataCount.this, "Alarm set in 1 seconds", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }else if(hour == 19 && minute > 0){

                            }else{
                                if(setAlarm == true) {

                                    if (setAlarmFinal == true) {
                                        if (tstamp > AlarmStopTime) {
                                            //play alarm again
                                            Intent intent = new Intent(JobService_SyncDataCount.this, MyAlarmReceiver.class);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(JobService_SyncDataCount.this, 234324243, intent, 0);
                                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1 * 1000), pendingIntent);
                                            Toast.makeText(JobService_SyncDataCount.this, "Alarm set in 1 seconds", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }
                        }
                    }

                } else {
                    sop = "invalid";
                    System.out
                            .println("--------- invalid for project list --- ");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*******************************************************************************************/

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                getDate();
                Log.e("Tag", " ******* WORKING ON SYNCDATA LAST *********");
                //progressDialog.dismiss();
                int scountf = dbvalueCSN();
                int TVCount = GetTvCount();
                int nonreprtedCnt = dbvalueNonReported();
                int clipscnt = dbvaluePendingClips();
                // int totalSoundlevel = dbvalueSoundLevel();
                //int PlaylistCount = dbPlaylistCount();
                int totalLMSC = dbvalLMSConn();

                String z = String.valueOf(scountf);
                CharSequence title = "STA";
                int icon = R.drawable.sta_logo;
                long time = System.currentTimeMillis();

                CharSequence text = "Connection Status : "+scountf + " Nonreported station : " + nonreprtedCnt;
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                String hours = String.valueOf(hour);
                int s1 = 6;
                int s2 = 22;
                // /////////////////////////////////////////////////////
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPrefnon", Context.MODE_PRIVATE); // 0

                SharedPreferences.Editor editor = pref.edit();
                editor.putString("nonreportedStatus",
                        String.valueOf(nonreprtedCnt));
                editor.commit();
                // ///////////////////////////////////////////////////
				/*SharedPreferences prefbg = getApplicationContext()
						.getSharedPreferences("bgpref", Context.MODE_PRIVATE); // 0

				Editor editorbg = prefbg.edit();// bgprefbgPlayCount
				editorbg.putString("bgPlayCount", String.valueOf(PlaylistCount));
				editorbg.commit();

				Log.e("get details.....", "---kk add STn : " + PlaylistCount);*/
                // ***************connectionStatusCount**********//////////////////
                SharedPreferences pref1 = getApplicationContext()
                        .getSharedPreferences("MyPref", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor1 = pref1.edit();
                editor1.putString("csnStatusCount", String.valueOf(scountf));
                editor1.commit();
                // ///////////////////////////////////////////////
                SharedPreferences prefclips = getApplicationContext()
                        .getSharedPreferences("MyPrefclips",
                                Context.MODE_PRIVATE); // 0 - for private mode
                SharedPreferences.Editor editorclips = prefclips.edit();
                editorclips.putString("clips", "" + clipscnt);

                editorclips.commit();
                // ///////////////////////////////////////////////
                SharedPreferences prefTV1 = getApplicationContext()
                        .getSharedPreferences("PrefTVStatus",
                                Context.MODE_PRIVATE); // 0 - for private mode
                SharedPreferences.Editor editorTV1 = prefTV1.edit();
                editorTV1.putString("TVStatus",
                        String.valueOf(TVCount));
                editorTV1.commit();
                // ///////////////lms connetion/////////
                SharedPreferences preflmsconn = getApplicationContext()
                        .getSharedPreferences("PrefLmsCount",
                                Context.MODE_PRIVATE);
                SharedPreferences.Editor editorlmsConne = preflmsconn.edit();
                editorlmsConne.putString("LmsCount", "" + totalLMSC);

                editorlmsConne.commit();


                updateAlertCount();

                if (net()) {

                    if ((hour > s1) && (hour < s2)) {

                        try {
                            //Common.TOKEN = registerGCM();
                            //Common.UserName = GetUserName();
						/*JSONObject jsonObject = new JSONObject();
						jsonObject.put("Pkg_name", "com.stavigilmonitoring");
						jsonObject.put("Mobile", strmobileno);
						jsonObject.put("UserName", Common.UserName);
						jsonObject.put("Device_Id", Common.TOKEN);
						paramToken=jsonObject.toString();*/
							/*urlStringToken = "http://ccs.ekatm.com" //AdatSoftData.URL
									+ "/api/AddNotification?"// AdatSoftData.METHOD_SAVE_DATA
									+	"sessionId=" + "vigil"// AdatSoftData.SESSION_ID
									+ "&handler=" + "0" //AdatSoftData.HANDLE
									+ "&pkg_name=com.stavigilmonitoring"
									+ "&ToMobile=" + mobno
									+ "&message=" +text
									+ "&FromMobile=" + mobno;*/
                            urlStringToken = "http://punbus.vritti.co/api/Values/AddNotification?"// AdatSoftData.METHOD_SAVE_DATA
                                    +	"Message=" +text
                                    + "&PkgName=com.stavigilmonitoring"
                                    + "&FromMobile=" + mobno
                                    + "&ToMobile=" + mobno;
							/*urlStringToken2 = "http://ccs.ekatm.com" //AdatSoftData.URL
									+ "/api/AddNotification?"// AdatSoftData.METHOD_SAVE_DATA
									+	"sessionId=" + "vigil"// AdatSoftData.SESSION_ID
									+ "&handler=" + "0" ;//AdatSoftData.HANDLE*/
                            urlStringToken2 = "http://punbus.vritti.co/api/Values/SendNotification?"// AdatSoftData.METHOD_SAVE_DATA
                                    +	"sessionId=" + "vigil"// AdatSoftData.SESSION_ID
                                    + "&handler=" + "0" ;//AdatSoftData.HANDLE
								/*+ "&pkg_name=com.stavigilmonitoring"
								+ "&ToMobile=" + mobno
								+ "&message=" + "New Alert is created for "+ edtSTN
								+ "&FromMobile=" + mobno;*/
							/*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
								new UploadTS_new().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							else
								new UploadTS_new().execute();*/
                            //new NotificationCreateAPI().execute(urlStringToken, null, null, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        System.out.println("Alarm stop...........");
                    }
                } else {
                    System.out.println("No internet working...........");
                }

            } catch (Exception e) {
                e.printStackTrace();
                dff = new SimpleDateFormat("HH:mm:ss");
                Ldate = dff.format(new Date());

                StackTraceElement l = new Exception().getStackTrace()[0];
                System.out.println(l.getClassName() + "/" + l.getMethodName()
                        + ":" + l.getLineNumber());
                ut = new utility();
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

        private void updateAlertCount() {
            // searchResults.clear();
            //searchResults.clear();
            //SQLiteDatabase sql = db.getWritableDatabase();
            int count = 0;
            //sumdata2 = "0";
            Log.e("cursor", String.valueOf("get"));
            Log.e("cursor", String.valueOf("get"));
            Log.e("cursor", String.valueOf("get"));
            Log.e("cursor", String.valueOf("get"));
            Log.e("cursor", String.valueOf("get"));
            Log.e("cursor", String.valueOf("get"));
            Log.e("cursor", String.valueOf("get"));
            Log.e("cursor", String.valueOf("get"));
            Log.e("cursor", String.valueOf("get"));
            Log.e("cursor", String.valueOf("get"));
            if (sumdata2.equals("1")){
                Cursor c = sql.rawQuery(
                        "SELECT SUM(CAST(cnt AS INT)) as sumdata FROM AlrtCountTable", null);
                Log.e("cursor", String.valueOf("IF Part"));
                Log.e("COUNT Alert", String.valueOf(c.getCount()));
                //Log.e("cursor", String.valueOf(c.getString(c.getColumnIndex("sumdata"))));
                if (c.moveToFirst()){
                    do{
                        sumdata2 = c.getString(c.getColumnIndex("sumdata"));
                        Log.e("cursor", String.valueOf(c.getString(c.getColumnIndex("sumdata"))));
                        // do what ever you want here
                    }while(c.moveToNext());


                    SharedPreferences prefalertcount = getApplicationContext()
                            .getSharedPreferences("Prefalertcount", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editoralertcount = prefalertcount.edit();
                    // String TVsound = prefsound.getString("TVSound", "");
                    editoralertcount.putString("AlertCount", sumdata2);
                    editoralertcount.commit();
                    //Log.e("get details.....", "---kk add STn : " + totalstation);
                    //alertcounts.setText(String.valueOf(sumdata2));
                }
                c.close();
            }else if (sumdata2.equals("0")){
                Log.e("cursor", String.valueOf("ELSE Part"));
                SharedPreferences prefalertcount = getApplicationContext()
                        .getSharedPreferences("Prefalertcount", Context.MODE_PRIVATE);
                SharedPreferences.Editor editoralertcount = prefalertcount.edit();
                // String TVsound = prefsound.getString("TVSound", "");
                editoralertcount.putString("AlertCount", sumdata2);
                editoralertcount.commit();
                //Log.e("get details.....", "---kk add STn : " + totalstation);
                //alertcounts.setText(String.valueOf(sumdata2));
            }
        }


        private String[] splitfrom(String tf) {
            // TODO Auto-generated method stub
            // 12/31/2015 7:05:00 AM---
            String time1, time2 = null;
            String time[];
            String k = "";
            // String str = "18/01/2013 5:00:00 pm";
            SimpleDateFormat input = new SimpleDateFormat(
                    "MM/dd/yyyy hh:mm:ss a");
            Date dt;
            try {
                dt = input.parse(tf);

                SimpleDateFormat output = new SimpleDateFormat(
                        "MM/dd/yyyy HH:mm");// 2016/02/18
                String formattedDate = output.format(dt); // contains 18/01/2013
                // 17:00:00
                // String k =
                // 01/14/2016
                // 07:05
                // formattedDate.substring(formattedDate.indexOf(" "));
                // fromtimetw = k.substring(0, 5); // = "ab"

                time = formattedDate.split(" ");
                time1 = time[0];
                time2 = time[1];

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                dff = new SimpleDateFormat("HH:mm:ss");
                Ldate = dff.format(new Date());

                StackTraceElement l = new Exception().getStackTrace()[0];
                System.out.println(l.getClassName() + "/" + l.getMethodName()
                        + ":" + l.getLineNumber());
                ut = new utility();
                if (!ut.checkErrLogFile()) {

                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()) {
                    ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                            + ":" + l.getLineNumber() + "	" + e.getMessage()
                            + " " + Ldate);
                }

            }

            String[] v1 = { time2 };

            return v1;
        }

        private String[] splittime(String tf) {
            // TODO Auto-generated method stub
            System.out.println("---value of tf for date...." + tf);
            String fromtimetw = "";

            String k = tf.substring(0, tf.indexOf(" "));
            System.out.println("---value of k for date..." + k);

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date myDate = null;
            try {
                myDate = dateFormat.parse(k);
                System.out.println("..........value of my date after conv"
                        + myDate);

            } catch (ParseException e) {
                e.printStackTrace();
                dff = new SimpleDateFormat("HH:mm:ss");
                Ldate = dff.format(new Date());

                StackTraceElement l = new Exception().getStackTrace()[0];
                System.out.println(l.getClassName() + "/" + l.getMethodName()
                        + ":" + l.getLineNumber());
                ut = new utility();
                if (!ut.checkErrLogFile()) {

                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()) {
                    ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                            + ":" + l.getLineNumber() + "	" + e.getMessage()
                            + " " + Ldate);
                }

            }
            SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM");
            String finalDate = timeFormat.format(myDate);

            String[] v2 = { finalDate };

            return v2;
        }

        private int dbvalLMSConn() {
            // TODO Auto-generated method stub
            // LmsConnectionStatewiseBean
            ArrayList<LmsConnectionStatewiseBean> searchResults = new ArrayList<LmsConnectionStatewiseBean>();
            searchResults.clear();
            //DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
            //SQLiteDatabase sql = db.getWritableDatabase();
            Cursor c = sql.rawQuery(
                    "SELECT DISTINCT NetworkCode FROM LmsConnectionStatus",
                    null);
            int con = c.getCount();
            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    int stncnt = 0;
                    String Type = c.getString(0);

                    Cursor c1 = sql.rawQuery(
                            "SELECT LastConnectionTime  FROM LmsConnectionStatus WHERE NetworkCode='"
                                    + c.getString(0)
                                    + "' ORDER BY NetworkCode Desc", null);
                    if (c1.getCount() > 0) {
                        c1.moveToFirst();
                        do {
                            String column1 = c1.getString(c1
                                    .getColumnIndex("LastConnectionTime"));
                            try {
                                String s = column1.substring(0,
                                        column1.indexOf("."));
                                Calendar cal = Calendar.getInstance();
                                SimpleDateFormat format = new SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss");

                                Date Startdate = format.parse(s);
                                Date Enddate = cal.getTime();
                                long diff = Enddate.getTime()
                                        - Startdate.getTime();
                                long diffSeconds = diff / 1000 % 60;
                                long diffMinutes = diff / (60 * 1000) % 60;
                                long diffHours = diff / (60 * 60 * 1000) % 24;
                                long diffDays = diff / (24 * 60 * 60 * 1000);

                                if (diffDays == 0 && diffHours == 0
                                        && diffMinutes <= 30) {

                                } else {
                                    stncnt = stncnt + 1;
                                }
                            } catch (Exception ex) {
                                dff = new SimpleDateFormat("HH:mm:ss");
                                Ldate = dff.format(new Date());

                                StackTraceElement l = new Exception()
                                        .getStackTrace()[0];
                                System.out.println(l.getClassName() + "/"
                                        + l.getMethodName() + ":"
                                        + l.getLineNumber());

                                if (!ut.checkErrLogFile()) {

                                    ut.ErrLogFile();
                                }
                                if (ut.checkErrLogFile()) {
                                    ut.addErrLog(l.getClassName() + "/"
                                            + l.getMethodName() + ":"
                                            + l.getLineNumber() + "	"
                                            + ex.getMessage() + " " + Ldate);
                                }
                            }

                        } while (c1.moveToNext());

                        c1.close();
                    }
                    Type = Type.replaceAll("0", "");
                    Type = Type.replaceAll("1", "");
                    if (!Type.trim().equalsIgnoreCase("")) {
                        LmsConnectionStatewiseBean sitem = new LmsConnectionStatewiseBean();
                        sitem.setNetcode(Type);
                        sitem.setNetcodeCount(stncnt);
                        searchResults.add(sitem);
                    }
                } while (c.moveToNext());
                c.close();

            }

            int scount = 0;
            for (int i = 0; i < searchResults.size(); i++) {
                scount = scount + searchResults.get(i).getNetcodeCount();
            }

            SharedPreferences preflmsconn = getApplicationContext()
                    .getSharedPreferences("PrefLmsCount", Context.MODE_PRIVATE);
            SharedPreferences.Editor editorlmsConne = preflmsconn.edit();

            editorlmsConne.putString("LmsCount", scount + "");

            editorlmsConne.commit();

            return scount;
        }

    }

    private int dbvalueNonReported() {
        try {
            // TODO Auto-generated method stub
            //DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
            //SQLiteDatabase sql = db.getWritableDatabase();
            Cursor cursor = sql
                    .rawQuery(
                            "SELECT DISTINCT InstallationDesc FROM NonrepeatedAd",
                            null);

            System.out.println("----------  dbvalue screen cursor count -- "
                    + cursor.getCount());

            if (cursor != null && cursor.getCount() > 0) {
                int csnCount = cursor.getCount();
                // String scountCSN=String.valueOf(csnCount);
                // do your action
                // Fetch your data

                cursor.close();
                return csnCount;

            } else {

                cursor.close();
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    private int dbvaluePendingClips() {
        try {
            // TODO Auto-generated method stub
            //DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
            //SQLiteDatabase sql = db.getWritableDatabase();
            Cursor cursor = sql.rawQuery(
                    "SELECT DISTINCT InstallationDesc FROM PendingClips", null);

            System.out.println("----------  dbvalue screen cursor count -- "
                    + cursor.getCount());

            if (cursor != null && cursor.getCount() > 0) {
                int csnCount = cursor.getCount();
                // String scountCSN=String.valueOf(csnCount);
                // do your action
                // Fetch your data

                cursor.close();
                return csnCount;

            } else {

                cursor.close();
                return 0;
            }
        } catch (Exception e) {
            dff = new SimpleDateFormat("HH:mm:ss");
            Ldate = dff.format(new Date());

            StackTraceElement l = new Exception().getStackTrace()[0];
            System.out.println(l.getClassName() + "/" + l.getMethodName() + ":"
                    + l.getLineNumber());
            ut = new utility();
            if (!ut.checkErrLogFile()) {

                ut.ErrLogFile();
            }
            if (ut.checkErrLogFile()) {
                ut.addErrLog(l.getClassName() + "/" + l.getMethodName() + ":"
                        + l.getLineNumber() + "	" + e.getMessage() + " "
                        + Ldate);
            }

            return 0;
        }
    }

    private int dbPlaylistCount() {
        List<StatelevelList> searchResults = new ArrayList<StatelevelList>();
        int totalstation_bg = 0;
        try {

            searchResults.clear();
            //DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
            //SQLiteDatabase sql = db.getWritableDatabase();
            int count = 0;
            Cursor c = sql
                    .rawQuery(
                            "SELECT DISTINCT NetworkCode FROM Backgroundplaylist",
                            null);

            if (c.getCount() == 0) {
                totalstation_bg = 0;

            } else {
                c.moveToFirst();
                do {

                    String Type = c.getString(0);

                    Cursor c1 = sql.rawQuery(
                            "SELECT DISTINCT InstalationId FROM Backgroundplaylist WHERE NetworkCode='"
                                    + Type + "'", null);
                    count = c1.getCount();

                    Type = Type.replaceAll("0", "");
                    Type = Type.replaceAll("1", "");
                    if (!Type.trim().equalsIgnoreCase("")) {
                        StatelevelList sitem = new StatelevelList();
                        sitem.SetNetworkCode(Type);
                        sitem.Setcount(count);
                        searchResults.add(sitem);

                    }
                    totalstation_bg = totalstation_bg + count;
                    c1.close();
                } while (c.moveToNext());

            }
            c.close();

        } catch (Exception e) {
            dff = new SimpleDateFormat("HH:mm:ss");
            Ldate = dff.format(new Date());

            StackTraceElement l = new Exception().getStackTrace()[0];
            System.out.println(l.getClassName() + "/" + l.getMethodName() + ":"
                    + l.getLineNumber());
            ut = new utility();
            if (!ut.checkErrLogFile()) {

                ut.ErrLogFile();
            }
            if (ut.checkErrLogFile()) {
                ut.addErrLog(l.getClassName() + "/" + l.getMethodName() + ":"
                        + l.getLineNumber() + "	" + e.getMessage() + " "
                        + Ldate);
            }
            return 0;

        }
        return totalstation_bg;
    }

    public void getDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        // get current date time with Date()
        date = new Date();
        datestring = dateFormat.format(date);
        System.out.println("value of date is......" + datestring);
        SharedPreferences prefDate = getApplicationContext()
                .getSharedPreferences("MyPrefDate", Context.MODE_PRIVATE); // 0
        // -
        // for
        // private
		/*// get a list of running processes and iterate through them
		ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		// get the info from the currently running task
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

		Log.i("current task :", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());*/

        SharedPreferences.Editor editorDate = prefDate.edit();
        editorDate.putString("Dates", datestring);
        editorDate.commit();

    }

    protected boolean net() {
        // TODO Auto-generated method stub
        Context context = this.getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private int dbvalueCSN() {
        try {
            int cnt = 0;
            //DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
            //SQLiteDatabase sql = db.getWritableDatabase();
            /*
             * Cursor cursor = sql.rawQuery(
             * "SELECT *   FROM ConnectionStatusUser", null);
             */
            Cursor cursor = sql
                    .rawQuery(
                            "SELECT s.InstallationId,ServerTime,Remarks,Last7DaysPerFormance, QuickHealStatus, STAVersion, LatestDowntimeReason,InstallationDesc FROM ConnectionStatusUser s "
                                    + " INNER JOIN ConnectionStatusUser1 s1 ON s.InstallationId=s1.InstallationId ORDER BY ServerTime",
                            null);

            return cursor.getCount();

        } catch (Exception e) {
            return 0;
        }

    }

    public int GetTvCount() {
        try {

            // arrlist.clear();
            int cnt = 0;
            List<TvStatusStateBean> searchResults = new ArrayList<TvStatusStateBean>();
            //DatabaseHandler db = new DatabaseHandler(this);
            //SQLiteDatabase sql = db.getWritableDatabase();

            Cursor c1 = sql.rawQuery("select distinct Type from TvStatus", null);
            int Scnt = 0;
            c1.moveToFirst();
            do {
                Log.e("Table count", "" + c1.getCount());
                String Type = c1.getString(c1.getColumnIndex("Type"));
                // String Type = c1.getString(c1.getColumnIndex("N"));
                Cursor c = sql
                        .rawQuery(
                                "Select distinct c1.InstallationId,c1.TVStatus,c1.flg from TvStatus c1  inner join AllStation c2  on c1.InstallationId=c2.InstallationId where c2.NetworkCode='"
                                        + Type + "'", null);

                if (c.getCount() == 0) {
                    c.close();
                } else {
                    TvStatusStateBean sitem = new TvStatusStateBean();
                    c.moveToFirst();
                    int column = 0;
                    do {

                        int i = 0;
                        int s = c.getInt(c.getColumnIndex("flg"));
                        String s1 = c.getString(c.getColumnIndex("TVStatus"));

                        for (char d : s1.toCharArray()) {
                            if (d == '0') {
                                cnt++;
                            }
                        }

                        if (!Type.trim().equalsIgnoreCase("") && s != 0) {// if
                            // (!Type.trim().equalsIgnoreCase("")
                            // && cnt != 8) {

                            column++;
                            sitem.Setcount(cnt);
                            int a = sitem.Getcount();
                            if (a != 0) {
                                Scnt++;
                            }
                            sitem.SetNetworkCode(Type);
                            sitem.SetScount(Scnt);
                        }

                        cnt = 0;

                    } while (c.moveToNext());

                    if (column != 0) {
                        sitem.SettotalStation(column);
                        searchResults.add(sitem);
                    }
                    Log.e("TV count", "" + column);
                    Scnt = 0;
                }

            } while (c1.moveToNext());

            c1.close();

            int scount = 0;
            for (int i = 0; i < searchResults.size(); i++)
                scount = scount + searchResults.get(i).Getcount();


            return scount;
        } catch (Exception e) {
            dff = new SimpleDateFormat("HH:mm:ss");
            Ldate = dff.format(new Date());

            StackTraceElement l = new Exception().getStackTrace()[0];
            System.out.println(l.getClassName() + "/" + l.getMethodName() + ":"
                    + l.getLineNumber());
            ut = new utility();
            if (!ut.checkErrLogFile()) {

                ut.ErrLogFile();
            }
            if (ut.checkErrLogFile()) {
                ut.addErrLog(l.getClassName() + "/" + l.getMethodName() + ":"
                        + l.getLineNumber() + "	" + e.getMessage() + " "
                        + Ldate);
            }

            return 0;
        }

    }

    // @Override
    // public void onDestroy() {
    // // TODO Auto-generated method stub
    // super.onDestroy();
    // }

    private String[] splitfromtym(String tym) {
        System.out.println("---value of tym differ...." + tym);
        String fromtimetw = "";
        final String dateStart = tym;
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        Date date = new Date();
        System.out.println("date format of system......................"
                + dateFormat.format(date));
        System.out
                .println("date format of web tym......................" + tym);

        final String dateStop = dateFormat.format(date);
        Date d1 = null;
        Date d2 = null;
        String diffTym = "";

        try {
            d1 = dateFormat.parse(dateStart);
            d2 = dateFormat.parse(dateStop);
            System.out.println("d2......................" + d2);
            // in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            String days = String.valueOf(diffDays);
            String hours = String.valueOf(diffHours);
            String minutes = String.valueOf(diffMinutes);
            if (days.equals("0")) {
                if (hours.equals("0")) {
                    // add code for minutes

                    int i = Integer.parseInt(minutes);
                    if (i >= 30) {
                        // end
                        diffTym = diffMinutes + " Minutes ";
                    } else {
                        diffTym = "";
                    }
                }
            } else {
                diffTym = diffDays + " Days ";
            }

        } catch (Exception e) {
            e.printStackTrace();
            dff = new SimpleDateFormat("HH:mm:ss");
            Ldate = dff.format(new Date());

            StackTraceElement l = new Exception().getStackTrace()[0];
            System.out.println(l.getClassName() + "/" + l.getMethodName() + ":"
                    + l.getLineNumber());
            ut = new utility();
            if (!ut.checkErrLogFile()) {

                ut.ErrLogFile();
            }
            if (ut.checkErrLogFile()) {
                ut.addErrLog(l.getClassName() + "/" + l.getMethodName() + ":"
                        + l.getLineNumber() + "	" + e.getMessage() + " "
                        + Ldate);
            }

        }

        String[] s = { diffTym };
        return s;
    }

    class NotificationCreateAPI extends AsyncTask<String, Void, String> {
        Object res;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
			/*progressDialog = new ProgressDialog(ConfirmOTPActivity.this);
			progressDialog.setMessage("Processing...");
			progressDialog.show();*/
        }
        @Override
        protected String doInBackground(String... params) {

       /* responsemsg = "";
        inwid = "";
        inwtab = "";*/
            try {
                Log.e("URL",params[0]);
                res = OpenConnection(params[0]);
                responsemsg = res.toString();
                Log.e("URL res",responsemsg);
            } catch (NullPointerException e) {
                responsemsg = "error";
                e.printStackTrace();
            } catch (Exception e) {
                responsemsg = "error";
                e.printStackTrace();
            }
            return responsemsg;
        }
        @Override
        protected void onPostExecute(String result) {
            //String table = "";
            if (result.contains("error")||result.contains("E")) {
                Toast.makeText(getBaseContext(), "error!", Toast.LENGTH_LONG).show();
            } else if (result.contains("Y")) {
                try{
					/*Log.e("URL",urlStringToken2);
					res = OpenConnection(urlStringToken2);
					responsemsg = res.toString();
					Log.e("URL res",responsemsg);*/
                } catch (NullPointerException e) {
                    responsemsg = "error";
                    e.printStackTrace();
                } catch (Exception e) {
                    responsemsg = "error";
                    e.printStackTrace();
                }

                //Toast.makeText(getBaseContext(), "Token Added Successfully..", Toast.LENGTH_LONG).show();
            }
        }
    }


    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(
                    Config.EXTRA_MESSAGE);
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());

            /**
             * Take appropriate action on this message depending upon your app
             * requirement For now i am just displaying it on the screen
             * */

            // Showing received message
            // lblMessage.append(newMessage + "\n");
            Toast.makeText(getApplicationContext(),
                    "New Message: " + newMessage, Toast.LENGTH_LONG).show();

            // Releasing wake lock
            WakeLocker.release();
        }
    };

    @Override
    public void onDestroy() {
        // Cancel AsyncTask

        try {
            // Unregister Broadcast Receiver
            unregisterReceiver(mHandleMessageReceiver);

            // Clear internal resources.

        } catch (Exception e) {
            Log.e("UnRgtr Receiver Error", "> " + e.getMessage());
            dff = new SimpleDateFormat("HH:mm:ss");
            Ldate = dff.format(new Date());

            StackTraceElement l = new Exception().getStackTrace()[0];
            System.out.println(l.getClassName() + "/" + l.getMethodName() + ":"
                    + l.getLineNumber());
            ut = new utility();
            if (!ut.checkErrLogFile()) {

                ut.ErrLogFile();
            }
            if (ut.checkErrLogFile()) {
                ut.addErrLog(l.getClassName() + "/" + l.getMethodName() + ":"
                        + l.getLineNumber() + "	" + e.getMessage() + " "
                        + Ldate);
            }

        }
        super.onDestroy();
    }
}
