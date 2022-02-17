package com.services;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.stavigilmonitoring.DatabaseHandler;
import com.stavigilmonitoring.utility;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JobService_WorkDone extends JobService {
    private static final String TAG1 = "MyJobService";
    String Worktype,WrkRemrk,MatType,SattionName,installation,mob,date,MatRmrk,ActID,Loc,ActName;
    Double Lat,Long;
    utility ut;
    String responsemsg;
    SimpleDateFormat dff;
    String Ldate,isUpload;
    int wkid;
    DatabaseHandler db;
    SQLiteDatabase sql;

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.e("Back Timer : ","JobServiceStarted");

        init();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.d(TAG1, "Job cancelled!");
        return false;
    }

    public void init(){

        db = new DatabaseHandler(getBaseContext());
        sql = db.getWritableDatabase();

        getRowFromDatabase();
    }

    private void getRowFromDatabase() {
        // TODO Auto-generated method stub
        ut = new utility();
        //DatabaseHandler db = new DatabaseHandler(getBaseContext());
        //SQLiteDatabase sql = db.getWritableDatabase();
        Cursor c = sql.rawQuery("SELECT * FROM WorkDonetable WHERE isUpload='No'",null);
        int count_ = c.getCount();
        if (c.getCount() == 0) {
            System.out.println("======= c= 0  fetchall ");

            c.close();

            stopSelf();
        }else{
            c.moveToFirst();

            //Worktype,WrkRemrk,MatType,SattionName,installation,mob,date,MatRmrk,ActID,Loc,ActName,Lat,Long
            wkid = c.getInt(c.getColumnIndex("WkId"));
            Worktype = c.getString(c.getColumnIndex("WorkType"));
            WrkRemrk = c.getString(c.getColumnIndex("WorkRemark"));
            MatType = c.getString(c.getColumnIndex("MatType"));
            SattionName = c.getString(c.getColumnIndex("Station"));
            installation = c.getString(c.getColumnIndex("StationInstal"));
            mob = c.getString(c.getColumnIndex("Mob"));
            date = c.getString(c.getColumnIndex("Currentdate"));
            MatRmrk = c.getString(c.getColumnIndex("MatRemark"));
            ActID = c.getString(c.getColumnIndex("ActivityID"));
            Loc = c.getString(c.getColumnIndex("MYLocation"));
            ActName = c.getString(c.getColumnIndex("ActName"));
            isUpload = c.getString(c.getColumnIndex("isUpload"));
            Lat = c.getDouble(c.getColumnIndex("Latitude"));
            Long = c.getDouble(c.getColumnIndex("Longitude"));
            if (ut.isnet(getApplicationContext())) {
                new UploadingData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                stopSelf();
            }
            c.close();
        }
    }

    public class UploadingData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String url;

            url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/BookWorkType?WorkType="
                    + Worktype
                    + "&Remarks="
                    + WrkRemrk
                    + "&MaterialName="
                    + MatType
                    + "&StationName="
                    + SattionName
                    + "&InstallationId="
                    + installation
                    + "&Mobileno="
                    + mob
                    + "&currentDate="
                    + date
                    + "&remarksMaterial="
                    + MatRmrk
                    + "&ActivityId="
                    + ActID // ActivityId,Location,Activity,latitude,longitude
                    + "&currentLocation="
                    + Loc
                    + "&ActivityName="
                    + ActName
                    + "&latitude="
                    + Lat
                    + "&longitude="
                    + Long + "";

            Log.e("material ", "url : " + url);
            url = url.replaceAll(" ", "%20");
            try {
                System.out.println("-------  activity url --- " + url);
                responsemsg = ut.httpGet(url);

                System.out.println("-------------  xx vale-- " + responsemsg);

                responsemsg = responsemsg
                        .substring(responsemsg.indexOf(">") + 1);
                responsemsg = responsemsg
                        .substring(responsemsg.indexOf(">") + 1);
                responsemsg = responsemsg
                        .substring(0, responsemsg.indexOf("<"));

            } catch (NullPointerException e) {
                responsemsg = "Error";
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

                responsemsg = "Error";
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

            } catch (Exception e) {
                e.printStackTrace();

                responsemsg = "Error";
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

            return responsemsg;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                String asd = result;
                if (asd.equalsIgnoreCase("Work Done Inserted")) {
				/*Toast.makeText(WorkDoneService.this,
						"Data Saved successfully...", Toast.LENGTH_LONG)
						.show();*/
                    //DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
                    //SQLiteDatabase sql = db.getWritableDatabase();

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("isUpload", "Yes");
                    sql.update("WorkDonetable", contentValues, "WkId=?",
                            new String[] { Integer.toString(wkid) });

                    sql.delete("WorkDonetable", "WkId=?",
                            new String[] { Integer.toString(wkid) });

                    // uploadDatasheetAttachment();
                    // uploadDatasheetPhotos();

                    getRowFromDatabase();
                } else {
				/*Toast.makeText(getApplicationContext(), "Server Error..",
						Toast.LENGTH_LONG).show();*/
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // iv.setVisibility(View.GONE);
            //mProgress.setVisibility(View.VISIBLE);
        }
    }

}
