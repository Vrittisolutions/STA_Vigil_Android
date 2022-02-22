package com.video_photo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.database.DBInterface;
import com.stavigilmonitoring.DatabaseHandler;
import com.stavigilmonitoring.R;
import com.stavigilmonitoring.utility;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;

/**
 * Created by Admin-3 on 11/8/2017.
 */

public class VideoPhotocategorizeActivity extends Activity {

    LinearLayout  dmcstnwise, dmcsonowise,dmcstnwise_video,dmcsonowise_video;
    private static DmCRefresh asynk_new;
    private ProgressBar mprogressBar;
    private ImageView btnrefresh;
    private utility ut;
    Context parent;
    String mobno;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_videophoto_category);

        initViews();
        setListeners();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       // Intent intent = new Intent(getApplicationContext(), DmCcategorizeActivity.class);
       // startActivity(intent);
        finish();
    }

    private void setListeners() {
        dmcstnwise_video.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), VideoPhotostnwiseActivity.class);
               // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //Intent.FLAG_ACTIVITY_CLEAR_TOP|
                startActivity(intent);
               // finish();
            }
        });

        dmcsonowise_video.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), VideoPhotosonowiseActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        btnrefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                 //btnrefresh.setVisibility(View.GONE);
               // mprogressBar.setVisibility(View.VISIBLE);
                /*Intent i = new Intent(parent, DmCertificateService.class);
                startService(i);*/
                fetchdata();
            }
        });
    }

    private void fetchdata(){
		/*if (asynk_new == null){*/
        /*btnrefresh.setVisibility(View.VISIBLE);
        mprogressBar.setVisibility(View.GONE);*/

        asynk_new = null;
        asynk_new = new DmCRefresh();
        asynk_new.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public class DmCRefresh extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        //String sumdata2;
        @Override
        protected String doInBackground(String... params) {
            String	responsemsg;

            String bb= "";
            utility ut = new utility();
            String urls = "http://vritti.co/imedia/STA_Announcement/DmCertificate.asmx/GetListOfPendingDMPhotoVideo?Mobile="
                    + mobno;
            urls = urls.replaceAll(" ", "%20");

            try {
                System.out.println("-------  activity url --- " + urls);
                responsemsg = ut.httpGet(urls);

                System.out.println("-------------  xx vale of non repeated-- "
                        + responsemsg);

               //DatabaseHandler db = new DatabaseHandler(getBaseContext());
                SQLiteDatabase sql = db.getWritableDatabase();

                //sql.execSQL("DROP TABLE IF EXISTS DmCertificateTable");
               //sql.execSQL(ut.getDmCertificateTable());
                sql.delete("VideoPhotoTable",null,null);

                Log.e("dm certificate", "resmsg : " + responsemsg);

                if (responsemsg.contains("<DMPhotoVideoHeaderId>")) {
                    //	sop = "valid";

                    String columnName, columnValue;
                    Cursor cur = sql.rawQuery("SELECT * FROM VideoPhotoTable", null);
                    ContentValues values1 = new ContentValues();
                    NodeList nl1 = ut.getnode(responsemsg, "Table1");

                    Cursor c = sql.rawQuery("SELECT * FROM VideoPhotoTable",null);
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

                            // Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
                        }
                        sql.insert("VideoPhotoTable",
                                null, values1);
                    }

                    cur.close();

                } else {
                    //	sop = "invalid";
                    System.out.println("--------- invalid for DmC list --- ");
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

                responsemsg = "wrong" + e.toString();
                System.out.println("--------- invalid for message type list --- "
                        + responsemsg);

            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(VideoPhotocategorizeActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            //Log.e("prgdlg", "Started");
            /*
            btnrefresh.setVisibility(View.GONE);
            mprogressBar.setVisibility(View.VISIBLE);*/
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try{
              //  String totalDMC =  DmCstnwiseActivity.dbvalueDMC(getApplicationContext());

              /*  SharedPreferences prefDMC = getApplicationContext()
                        .getSharedPreferences("PrefDMC", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorDMC = prefDMC.edit();

                editorDMC.putString("DMC",
                        String.valueOf(totalDMC));
                editorDMC.commit();

*/                progressDialog.dismiss();
                //Log.e("prgdlg", "Ended");
            }catch(Exception e){
                e.printStackTrace();
                StackTraceElement l = new Exception().getStackTrace()[0];

                ut =new utility();
                if(!ut.checkErrLogFile()){
                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()){
                    ut.addErrLog(l.getClassName()+"/"+l.getMethodName()+":"+l.getLineNumber()+"  "+e.getMessage());
                }
            }
        }

    }


    private void initViews() {
        parent = VideoPhotocategorizeActivity.this;
        dmcstnwise = (LinearLayout) findViewById(R.id.dmcstnwise);
        dmcsonowise = (LinearLayout) findViewById(R.id.dmcsonowise);
        dmcsonowise_video = (LinearLayout) findViewById(R.id.dmcsonowise_video);
        dmcstnwise_video = (LinearLayout) findViewById(R.id.dmcstnwise_video);
        btnrefresh = (ImageView) findViewById(R.id.button_refresh_alert);
        mprogressBar = (ProgressBar) findViewById(R.id.progressinvent1);

        db = new DatabaseHandler(getBaseContext());
        ut = new utility();

        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();
    }
}
