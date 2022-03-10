package com.services;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.database.DBInterface;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.stavigilmonitoring.DatabaseHandler;
import com.stavigilmonitoring.DmCstnwiseActivity;
import com.stavigilmonitoring.R;
import com.stavigilmonitoring.utility;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.stavigilmonitoring.utility.OpenConnection;

public class JobService_DMCertificate extends JobService {

    private static final String TAG1 = "MyJobService_DMCertificate";

    String mobno;
    String urlStringToken="", urlStringToken2="";
    private static final int NOTIFICATION = 1337;
    String responsemsg = "k";
    DatabaseHandler db;
    SQLiteDatabase sql;
//	private NotificationManager mNotificationManager;
//	private int notificationID = 100;

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

        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();

        db = new DatabaseHandler(this);
        sql = db.getWritableDatabase();

        Calendar cal = Calendar.getInstance();
        if (cal.HOUR <= 20 && cal.HOUR >= 6 && isnet())
            new UploadTS_new().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new UpdateDMCUsers().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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

    class UpdateDMCUsers extends AsyncTask<Void, Void, String> {
        utility ut;

        String exceptionString = "ok";
        String resposmsg = "m";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            String Url = "http://sta.vritti.co/imedia/STA_Announcement/DmCertificate.asmx/GetDMCertifcateUser?Mobile=" + mobno;
            ut= new utility();
            Log.e("DMCertificateUser", "url : " + Url);
            Log.e("Tag", " ******* WORKING ON DMCertificateUser *********");
            Url = Url.replaceAll(" ", "%20");

            try {
                resposmsg = ut.httpGet(Url);
                Log.e("Response", resposmsg);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (resposmsg.contains("Record are not Found...!")) {
                //DatabaseHandler db = new DatabaseHandler(getBaseContext());
                //SQLiteDatabase sql = db.getWritableDatabase();
                //sql.execSQL("Delete from DMCUsersTable");
                sql.delete("DMCUsersTable",null,null);

            } else if (resposmsg.contains("<UserId>")) {

                //DatabaseHandler db = new DatabaseHandler(getBaseContext());
                //sql = db.getWritableDatabase();
                //sql.beginTransaction();

                try {

                    String columnName, columnValue;
                    //sql.execSQL("DROP TABLE IF EXISTS DMCUsersTable");
                    sql.delete("DMCUsersTable",null,null);
                    //sql.execSQL(ut.getDMCUsersTable());
                    Cursor cur1 = sql.rawQuery("SELECT * FROM DMCUsersTable", null);
                    int count = cur1.getCount();
                    ContentValues values2 = new ContentValues();
                    NodeList nl2 = ut.getnode(resposmsg, "Table1");

                    for (int i = 0; i < nl2.getLength(); i++) {
                        Element e = (Element) nl2.item(i);
                        for (int j = 0; j < cur1.getColumnCount(); j++) {
                            columnName = cur1.getColumnName(j);
                            columnValue = ut.getValue(e, columnName);

                            values2.put(columnName, columnValue);
                        }
                        sql.insert("DMCUsersTable", null, values2);
                    }
                    //cur1.close();
				/*sql.close();
				db.close();*/

                    //sql.setTransactionSuccessful();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    //sql.endTransaction();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
        }
    }

    public class UploadTS_new extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String	responsemsg;

            String bb= "";
            utility ut = new utility();
            String urls = "http://sta.vritti.co/imedia/STA_Announcement/DmCertificate.asmx/GetListOfPendingDM?Mobile="
                    + mobno;
            urls = urls.replaceAll(" ", "%20");

            //DatabaseHandler db = new DatabaseHandler(getBaseContext());
            //SQLiteDatabase sql = db.getWritableDatabase();
            //sql = db.getWritableDatabase();
            //sql.beginTransaction();

            try {
                System.out.println("-------  activity url -------" + urls);
                responsemsg = ut.httpGet(urls);

                System.out.println("-------------  xx vale of non repeated-- "
                        + responsemsg);

						/*DatabaseHandler db = new DatabaseHandler(getBaseContext());
						SQLiteDatabase sql = db.getWritableDatabase();*/

                //sql.execSQL("DROP TABLE IF EXISTS DmCertificateTable");
                sql.delete("DmCertificateTable",null,null);
                //sql.execSQL(ut.getDmCertificateTable());

                Log.e("dm certificate", "resmsg : " + responsemsg);

                if (responsemsg.contains("<DMHeaderId>")) {

                    String columnName, columnValue;
                    Cursor cur = sql.rawQuery("SELECT * FROM DmCertificateTable", null);
                    ContentValues values1 = new ContentValues();
                    NodeList nl1 = ut.getnode(responsemsg, "Table1");

                    Cursor c = sql.rawQuery("SELECT * FROM DmCertificateTable",null);
                    ContentValues values = new ContentValues();
                    NodeList nl = ut.getnode(responsemsg, "Table1");
                    Log.e("DmCertificate data...",
                            " fetch data : " + nl1.getLength());

                    for (int i = 0; i < nl1.getLength(); i++) {
                        Element e = (Element) nl1.item(i);
                        for (int j = 0; j < cur.getColumnCount(); j++) {
                            columnName = cur.getColumnName(j);

                            columnValue = ut.getValue(e, columnName);
                            values1.put(columnName, columnValue);
                        }
                        sql.insert("DmCertificateTable",
                                null, values1);
                    }

                    //cur.close();

                } else {
                    System.out.println("--------- invalid for DmC list --- ");
                }

                //sql.setTransactionSuccessful();

            } catch (NullPointerException e) {
                e.printStackTrace();
                //db.close();
            } catch (IOException e) {
                e.printStackTrace();

                responsemsg = "wrong" + e.toString();
                System.out.println("--------- invalid for message type list --- "
                        + responsemsg);
                //db.close();
            }finally {
                //sql.endTransaction();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                CharSequence title = "STA";
                int icon = R.drawable.sta_logo;
                long time = System.currentTimeMillis();
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                String hours = String.valueOf(hour);
                int s1 = 6;
                int s2 = 22;

                String totalDMC = DmCstnwiseActivity.dbvalueDMC(getApplicationContext());
                CharSequence text = "DmCertificate : "+totalDMC;
                String z = String.valueOf(totalDMC);
                SharedPreferences prefDMC = getApplicationContext()
                        .getSharedPreferences("PrefDMC", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorDMC = prefDMC.edit();

                editorDMC.putString("DMC",
                        String.valueOf(totalDMC));
                editorDMC.commit();
                Log.e("get details.....", "---kk add STn : " +"WIP");// + totalSoundlevel);
                // soundcunt.setText(String.valueOf(totalstation));
                if (isnet()) {

                    if ((hour > s1) && (hour < s2)) {

                        try {
                            //Common.TOKEN = registerGCM();
                            //Common.UserName = GetUserName();

                            urlStringToken = "http://punbus.vritti.co/api/Values/AddNotification?"// AdatSoftData.METHOD_SAVE_DATA
                                    +	"Message=" +text
                                    + "&PkgName=com.stavigilmonitoring"
                                    + "&FromMobile=" + mobno
                                    + "&ToMobile=" + mobno;
                            urlStringToken2 = "http://punbus.vritti.co/api/Values/SendNotification?"// AdatSoftData.METHOD_SAVE_DATA
                                    +	"sessionId=" + "vigil"// AdatSoftData.SESSION_ID
                                    + "&handler=" + "0" ;//AdatSoftData.HANDLE

                            //new NotificationCreateAPI().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,urlStringToken);
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
                SimpleDateFormat dff = new SimpleDateFormat("HH:mm:ss");
                String Ldate = dff.format(new Date());

                StackTraceElement l = new Exception().getStackTrace()[0];
                System.out.println(l.getClassName() + "/" + l.getMethodName()
                        + ":" + l.getLineNumber());
                utility	ut = new utility();
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
                        Log.e("URL",urlStringToken2);
                        res = OpenConnection(urlStringToken2);
                        responsemsg = res.toString();
                        Log.e("URL resp",responsemsg);
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
    }
}
