package com.stavigilmonitoring;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapters.ConnectionstatusAdaptMain;
import com.beanclasses.ConnectionstatusHelper;
import com.database.DBInterface;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SupEnq_ConnStatus extends Activity {
    private Context parent;
    String Type, subType, Flag;
    ImageView iv;
    ListView connectionstatus;
    String mobno;
    com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
    static DownloadxmlsDataURL_new asyncfetch;
    static SimpleDateFormat dff;
    static String Ldate;
    ArrayList<ConnectionstatusHelper> searchResults;
    ConnectionstatusAdaptMain listAdapter;
    String responsemsg = "k";
    String sop = "no";
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.stavigilmonitoring.R.layout.activity_sup_enq__conn_status);

        parent = SupEnq_ConnStatus.this;

       // Toast.makeText(parent, "Conn status clicked", Toast.LENGTH_SHORT).show();

        Intent intent = getIntent();
        // Bundle extras = getIntent().getExtras();
        Type = intent.getStringExtra("Type");
        subType = intent.getStringExtra("subType");
        Flag = intent.getStringExtra("LayoutFlag");

        ((TextView) findViewById(com.stavigilmonitoring.R.id.onactivitynamereassign)).setText("Connection Status - "+ subType);
        iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_connection_main);
        iv.setVisibility(View.GONE);
        connectionstatus = (ListView) findViewById(com.stavigilmonitoring.R.id.connectionstatusdetailmain);

        db = new DatabaseHandler(parent);
        DBInterface dbi = new DBInterface(parent);
        mobno = dbi.GetPhno();

        dbi.Close();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if (asyncfetch != null
                && asyncfetch.getStatus() == AsyncTask.Status.RUNNING) {
            Log.e("async", "running");
            iv.setVisibility(View.GONE);
            ((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
                    .setVisibility(View.VISIBLE);
        }
        ut.getConnectionStatusUser1();
        ut.getConnectionStatusUser();

        if (dbvalue()) {
            if(Flag.equalsIgnoreCase("Sup_connectionstatus")){
                updatelist();
            }
            updatelist();
            // prepareListData();
        } else if (ut.isnet(parent)) {

            if(Flag.equalsIgnoreCase("Sup_connectionstatus")){
                fetchdata();
            }

        } else {
            try{
                ut.showD(parent,"nonet");
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        iv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (ut.isnet(parent)) {
                    asyncfetch = null;
                    asyncfetch = new DownloadxmlsDataURL_new();
                    asyncfetch.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    // fetchdata();
                } else {
                    ut.showD(parent,"nonet");
                }

            }
        });
    }

    private boolean dbvalue() {
        // TODO Auto-generated method stub
        try {
            //DatabaseHandler db1 = new DatabaseHandler(parent);
            SQLiteDatabase sql = db.getWritableDatabase();
            Cursor cursor1 = sql.rawQuery(
                    "SELECT * FROM ConnectionStatusUser1", null);
            if (cursor1 != null && cursor1.getCount() > 0) {
                Cursor cursor = sql.rawQuery(
                        "SELECT * FROM ConnectionStatusUser", null);

                System.out
                        .println("----------  dbvalue screen cursor count -- "
                                + cursor.getCount());

                if (cursor != null && cursor.getCount() > 0) {

                    // do your action
                    // Fetch your data

                    cursor.close();
					/*sql.close();
					db1.close();*/
                    cursor1.close();
                    return true;

                } else {

                    cursor.close();
                    //sql.close();
                    cursor1.close();
                    //db1.close();
                    return false;
                }

            } else {
                cursor1.close();
				/*sql.close();
				db1.close();*/
                return false;

            }
        } catch (Exception e) {
            dff = new SimpleDateFormat("HH:mm:ss");
            Ldate = dff.format(new Date());

            StackTraceElement l = new Exception().getStackTrace()[0];
            System.out.println(l.getClassName() + "/" + l.getMethodName() + ":"
                    + l.getLineNumber());
            ut = new com.stavigilmonitoring.utility();
            if (!ut.checkErrLogFile()) {

                ut.ErrLogFile();
            }
            if (ut.checkErrLogFile()) {
                ut.addErrLog(l.getClassName() + "/" + l.getMethodName() + ":"
                        + l.getLineNumber() + "	" + e.getMessage() + " "
                        + Ldate);
            }

            return false;
        }

    }

    private void updatelist() {
        // TODO Auto-generated method stub
        // startService(new Intent(getBaseContext(),SynchDtataCount.class));
        ut.getConnectionStatusUser1();
        ut.getConnectionStatusUser();

        searchResults = GetDetail();
        ConnectionstatusHelper sr = new ConnectionstatusHelper();
        listAdapter = null;
        listAdapter = new ConnectionstatusAdaptMain(this, searchResults, "");
        connectionstatus.setAdapter(listAdapter);

    }

    private void fetchdata() {
        // TODO Auto-generated method stub

        if (asyncfetch == null) {
            iv.setVisibility(View.VISIBLE);
            ((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
                    .setVisibility(View.GONE);

            Log.e("async", "null");
            asyncfetch = new DownloadxmlsDataURL_new();
            asyncfetch.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            if (asyncfetch.getStatus() == AsyncTask.Status.RUNNING) {
                Log.e("async", "running");
                iv.setVisibility(View.GONE);
                ((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
                        .setVisibility(View.VISIBLE);
            }
        }
    }

    public class DownloadxmlsDataURL_new extends
            AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String xx = "";

            String url = "http://sta.vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetAllStationWithUserName_Android?Mobile="
                    + mobno;
          //  DatabaseHandler db = new DatabaseHandler(parent);
            SQLiteDatabase sql = db.getWritableDatabase();
            Log.e("csn status", "url : " + url);
            url = url.replaceAll(" ", "%20");
            try {
                responsemsg = ut.httpGet(url);
                Log.e("csn status", "resmsg : " + responsemsg);

                if (responsemsg.contains("<IId>")) {
                    sop = "valid";

                  //  sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusUser1");
                  //  sql.execSQL(ut.getConnectionStatusUser1());
                    sql.delete("ConnectionStatusUser1",null,null);

                    Cursor c = sql.rawQuery(
                            "SELECT *   FROM ConnectionStatusUser1", null);
                    ContentValues values = new ContentValues();
                    NodeList nl = ut.getnode(responsemsg, "Table1");
                    String msg = "";
                    String columnName, columnValue;
                    Log.e("sts main...", " fetch data : " + nl.getLength());
                    for (int i = 0; i < nl.getLength(); i++) {
                        Element e = (Element) nl.item(i);
                        for (int j = 0; j < c.getColumnCount(); j++) {
                            columnName = c.getColumnName(j);

                            String ncolumnname = "";
                            if (columnName.equalsIgnoreCase("InstallationId"))
                                ncolumnname = "IId";
                            else if (columnName
                                    .equalsIgnoreCase("InstallationDesc"))
                                ncolumnname = "SN";
                            else if (columnName.equalsIgnoreCase("UserName"))
                                ncolumnname = "UN";
							/*else if (columnName.equalsIgnoreCase("MobileNo"))
								ncolumnname = "Mob";*/
                            else if (columnName.equalsIgnoreCase("MobileNo"))
                                ncolumnname = "UN1";
                            else if (columnName.equalsIgnoreCase("SUP"))
                                ncolumnname = "SUP";

                            columnValue = ut.getValue(e, ncolumnname);
                            if(columnValue.contains(" /")){
                                columnValue = columnValue.replaceAll(" /","/");
                            }

                            if (ncolumnname == "SUP"
                                    && columnValue.contains(",")) {
                                columnValue = columnValue.substring(0,
                                        columnValue.indexOf(","));
                            }
                            values.put(columnName, columnValue);
                            // Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
                        }
                        sql.insert("ConnectionStatusUser1", null, values);
                    }

                    c.close();

                }

            } catch (NullPointerException e) {
                e.printStackTrace();
                dff = new SimpleDateFormat("HH:mm:ss");
                Ldate = dff.format(new Date());

                StackTraceElement l = new Exception().getStackTrace()[0];
                System.out.println(l.getClassName() + "/" + l.getMethodName()
                        + ":" + l.getLineNumber());
                ut = new com.stavigilmonitoring.utility();
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
                ut = new com.stavigilmonitoring.utility();
                if (!ut.checkErrLogFile()) {

                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()) {
                    ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                            + ":" + l.getLineNumber() + "	" + e.getMessage()
                            + " " + Ldate);
                }
            }
            url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetCSNStatus_Android_new?Mobile="
                    + mobno;

            Log.e("csn status", "url : " + url);
            url = url.replaceAll(" ", "%20");
            try {
                responsemsg = ut.httpGet(url);
                Log.e("csn status", "resmsg : " + responsemsg);

                if (responsemsg.contains("<A>")) {
                    sop = "valid";
                    String columnName, columnValue;

                  //  sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusUser");
                   // sql.execSQL(ut.getConnectionStatusUser());
                    sql.delete("ConnectionStatusUser",null,null);

                    Cursor cur = sql.rawQuery(
                            "SELECT *   FROM ConnectionStatusUser", null);
                    ContentValues values1 = new ContentValues();
                    NodeList nl1 = ut.getnode(responsemsg, "Table1");
                    Log.e("sts main...", " fetch data : " + nl1.getLength());
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
                            else if (columnName.equalsIgnoreCase("STAVersion"))
                                ncolumnname = "J";
                            else if (columnName
                                    .equalsIgnoreCase("AscOrderServerTime"))
                                ncolumnname = "K";
                            else if (columnName
                                    .equalsIgnoreCase("LatestDowntimeReason"))
                                ncolumnname = "L";
                            else if (columnName.equalsIgnoreCase("Type"))
                                ncolumnname = "N";
                            else if (columnName
                                    .equalsIgnoreCase("SubNetworkCode"))
                                ncolumnname = "R";
                            columnValue = ut.getValue(e, ncolumnname);

                            if (columnName.equalsIgnoreCase("ServerTime")) {
                                try {
                                    Calendar cal = Calendar.getInstance();
                                    // SimpleDateFormat format = new
                                    // SimpleDateFormat("MM/dd/yyyy HH:mm:ss aa");

                                    SimpleDateFormat format = new SimpleDateFormat(
                                            "MM/dd/yyyy hh:mm:ss aa", Locale.ENGLISH);

                                    Date Startdate = format.parse(columnValue);
                                    Date Enddate = cal.getTime();
                                    long diff = Enddate.getTime()
                                            - Startdate.getTime();
                                    long diffSeconds = diff / 1000 % 60;
                                    long diffMinutes = diff / (60 * 1000) % 60;
                                    long diffHours = diff / (60 * 60 * 1000)
                                            % 24;
                                    long diffDays = diff
                                            / (24 * 60 * 60 * 1000);

                                    Log.e("getdetails", "sd : " + Startdate
                                            + " ed: " + Enddate + " d: "
                                            + diffDays + " h: " + diffHours
                                            + " m:" + diffMinutes);
                                    /*
                                     * Log.e("printdiff.........","diffDays: "+
                                     * diffDays);
                                     * Log.e("printdiff.........","diffHours: "
                                     * +diffHours);
                                     * Log.e("printdiff.........","diffMinutes: "
                                     * +diffMinutes);
                                     * Log.e("printdiff.........",
                                     * "diffSeconds: "+diffSeconds);
                                     */

                                    if (diffDays == 0 && diffHours == 0
                                            && diffMinutes <= 15) {

                                    } else {
                                        conn = "valid";
                                    }
                                } catch (Exception ex) {
                                }
                            }

                            values1.put(columnName, columnValue);

                            // Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
                        }

                        if (conn == "valid")
                            sql.insert("ConnectionStatusUser", null, values1);
                    }

                    cur.close();
					/*sql.close();
					db.close();*/

                } else {
                    sop = "invalid";
                    System.out
                            .println("--------- invalid for project list --- ");
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
                dff = new SimpleDateFormat("HH:mm:ss");
                Ldate = dff.format(new Date());

                StackTraceElement l = new Exception().getStackTrace()[0];
                System.out.println(l.getClassName() + "/" + l.getMethodName()
                        + ":" + l.getLineNumber());
                ut = new com.stavigilmonitoring.utility();
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
                ut = new com.stavigilmonitoring.utility();
                if (!ut.checkErrLogFile()) {

                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()) {
                    ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                            + ":" + l.getLineNumber() + "	" + e.getMessage()
                            + " " + Ldate);
                }
            }

            return sop;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            // pd.cancel();
            try {
                System.out
                        .println("..DownloadxmlsDataURL_new onpost.............value of sop"
                                + sop);
                if (sop.equals("valid")) {
                    updatelist();
                } else {
                    ut.showD(parent,"invalid");
                }
                // iv.clearAnimation();
                iv.setVisibility(View.VISIBLE);
                ((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
                        .setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
                dff = new SimpleDateFormat("HH:mm:ss");
                Ldate = dff.format(new Date());

                StackTraceElement l = new Exception().getStackTrace()[0];
                System.out.println(l.getClassName() + "/" + l.getMethodName()
                        + ":" + l.getLineNumber());
                ut = new com.stavigilmonitoring.utility();
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

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();


            iv.setVisibility(View.GONE);
            ((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
                    .setVisibility(View.VISIBLE);

        }
    }

    private ArrayList<ConnectionstatusHelper> GetDetail() {
        ArrayList<ConnectionstatusHelper> results = new ArrayList<ConnectionstatusHelper>();
        //DatabaseHandler db = new DatabaseHandler(this);
        SQLiteDatabase sql = db.getWritableDatabase();

        Cursor c = sql
                .rawQuery(
                        "SELECT DISTINCT s.InstallationId,ServerTime,Remarks,Last7DaysPerFormance, QuickHealStatus, STAVersion, LatestDowntimeReason,InstallationDesc FROM ConnectionStatusUser s "
                                + " INNER JOIN ConnectionStatusUser1 s1 ON s.InstallationId=s1.InstallationId where SubNetworkCode='"
                                + subType + "' ORDER BY ServerTime ", null);
        if (c.getCount() == 0) {
            ConnectionstatusHelper sr = new ConnectionstatusHelper();

            sr.setinstallationId("");
            //
            sr.settymdiff("");
            //
            sr.setreason("");
            results.add(sr);

            c.close();
			/*sql.close();
			db.close();*/

            return results;
        } else {
            c.moveToFirst();
            String[] sa = c.getColumnNames();
            // Log.e("getd","desc: "+c.getString(c.getColumnIndex("InstallationDesc"))+" servertime: "+c.getString(c.getColumnIndex("ServerTime")));
            int column = 0;
            do {

                ConnectionstatusHelper sr = new ConnectionstatusHelper();


                int column1 = c.getColumnIndex("ServerTime");
                // int column1 = c.getColumnIndex("EndTime");
                String[] tym = splitfromtym(c.getString(column1));
                sr.setservertime(c.getString(column1));
                // int column2 = c.getColumnIndex("AscOrderServerTime");

                // int f=printDiff(c.getString(column1));
                try {
                    Calendar cal = Calendar.getInstance();
                    // SimpleDateFormat format = new
                    // SimpleDateFormat("MM/dd/yyyy HH:mm:ss aa");

                    SimpleDateFormat format = new SimpleDateFormat(
                            "MM/dd/yyyy hh:mm:ss aa", Locale.ENGLISH);

                    Date Startdate = format.parse(c.getString(column1));
                    Date Enddate = cal.getTime();
                    long diff = Enddate.getTime() - Startdate.getTime();
                    long diffSeconds = diff / 1000 % 60;
                    long diffMinutes = diff / (60 * 1000) % 60;
                    long diffHours = diff / (60 * 60 * 1000) % 24;
                    long diffDays = diff / (24 * 60 * 60 * 1000);

                    if (!(Enddate.getTime() - Startdate.getTime() < 0)) {
                        if (diffDays == 0 && diffHours == 0
                                && diffMinutes <= 15) {

                        } else {
                            String s = sr.setinstallationId(c.getString(c
                                    .getColumnIndex("InstallationDesc")));
                            String diffstr = "";
                            if (diffDays == 0 && diffHours == 0)
                                diffstr = diffMinutes + "Min";
                            else if (diffDays == 0)
                                diffstr = diffHours + "hr";
                            else {
                                if (diffDays >= 32) {
                                    long yc = diffDays / 30;
                                    if (yc >= 12)
                                        diffstr = (yc / 12) + " Year";
                                    else
                                        diffstr = yc + " Month";
                                } else
                                    diffstr = diffDays + "days ";
                            }

                            sr.settymdiff(diffstr);
                            sr.setreason(c.getString(c
                                    .getColumnIndex("LatestDowntimeReason")));
                            Log.e("get det", " time : " + c.getString(column1));
                            results.add(sr);
                        }
                    }
                } catch (Exception ex) {
                    dff = new SimpleDateFormat("HH:mm:ss");
                    Ldate = dff.format(new Date());

                    StackTraceElement l = new Exception().getStackTrace()[0];
                    System.out.println(l.getClassName() + "/"
                            + l.getMethodName() + ":" + l.getLineNumber());
                    ut = new com.stavigilmonitoring.utility();
                    if (!ut.checkErrLogFile()) {

                        ut.ErrLogFile();
                    }
                    if (ut.checkErrLogFile()) {
                        ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
                                + ":" + l.getLineNumber() + "	"
                                + ex.getMessage() + " " + Ldate);
                    }

                }
            } while (c.moveToNext());

            c.close();
			/*sql.close();
			db.close();*/
        }
        Log.e("connection sts main", "cursor res : " + results.size());
        SimpleDateFormat dff = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.ENGLISH);
        try {
            for (int i = 0; i < results.size(); i++) {
                for (int j = i + 1; j < results.size(); j++) {
                    Date s1 = dff.parse(results.get(i).getservertime());
                    Date s2 = dff.parse(results.get(j).getservertime());
                    if (s1.compareTo(s2) > 0) {
                        ConnectionstatusHelper ci = results.get(i);
                        ConnectionstatusHelper cj = results.get(j);
                        results.remove(i);
                        results.add(i, cj);

                        results.remove(j);
                        results.add(j, ci);
                    }
                }
            }
        } catch (Exception ex) {
            dff = new SimpleDateFormat("HH:mm:ss");
            Ldate = dff.format(new Date());

            StackTraceElement l = new Exception().getStackTrace()[0];
            System.out.println(l.getClassName() + "/" + l.getMethodName() + ":"
                    + l.getLineNumber());
            ut = new com.stavigilmonitoring.utility();
            if (!ut.checkErrLogFile()) {

                ut.ErrLogFile();
            }
            if (ut.checkErrLogFile()) {
                ut.addErrLog(l.getClassName() + "/" + l.getMethodName() + ":"
                        + l.getLineNumber() + "	" + ex.getMessage() + " "
                        + Ldate);
            }

        }
        return results;
    }

    private String[] splitfromtym(String tym) {
        System.out.println("---value of tym differ...." + tym);
        String fromtimetw = "";
        final String dateStart = tym;
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.ENGLISH);
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

}
