package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.AlertStnReqStatewiseAdapter;
import com.beanclasses.StateList;
import com.database.DBInterface;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AlertStnReqStatewiseActivity extends Activity {
    String MobileNo = "";
    static SimpleDateFormat dff;
    static String Ldate = "";
    List<StateList> searchResults;
    ImageView iv;
    TextView alertHead;
    String sop = "", resposmsg = "", mobno = "";
    DownloadStation asyncfetch_csnstate;
    GridView lstcsn;
    com.stavigilmonitoring.utility ut;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_material_req_statewise);

        db = new DatabaseHandler(AlertStnReqStatewiseActivity.this);

        Intent intent = getIntent();
        MobileNo = intent.getStringExtra("mobileno");
        searchResults = new ArrayList<StateList>();

        alertHead = (TextView) findViewById(R.id.materialreq_name);
        alertHead.setText("Alert Station");
        lstcsn = findViewById(R.id.materialreq_statewise);
        iv = (ImageView) findViewById(R.id.button_refresh_list);

        DBInterface dbi = new DBInterface(AlertStnReqStatewiseActivity.this);
        mobno = dbi.GetPhno();

        if (asyncfetch_csnstate != null
                && asyncfetch_csnstate.getStatus() == AsyncTask.Status.RUNNING) {
            Log.e("async", "running");
            iv.setVisibility(View.GONE);
            ((ProgressBar) findViewById(R.id.progressBar1))
                    .setVisibility(View.VISIBLE);
        }

        if (dbvalue()) {
            updatelist();
        } else if (isnet()) {
            fetchdata();
        } else {
            showD("nonet");
        }

        lstcsn.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {/*******/
                String NetCode = searchResults.get(position).getNetworkcode();
				/*if (NetCode.equalsIgnoreCase("LMS")){
					Intent i = new Intent(MaterialReqStatewiseActivity.this,
							LMSNetworkCodeActivity.class);
					i.putExtra("Type", searchResults.get(position).getNetworkcode());
					startActivity(i);
					finish();
				}else{*/
                Intent i = new Intent(AlertStnReqStatewiseActivity.this, AlertStnReqMain.class);

                i.putExtra("Type", searchResults.get(position).getNetworkcode());
                startActivityForResult(i, Common.AlertStn2);
				/*startActivity(i);
				finish();*/
                //}

            }
        });

        iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isnet()) {
                    asyncfetch_csnstate = null;
                    asyncfetch_csnstate = new DownloadStation();
                    asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    // fetchdata();
                } else {
                    showD("nonet");
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Common.AlertStn2) {
            try{
                String MaterialStation = data.getStringExtra("StatioName");
                //ButtonStationNmae.setText(MaterialStation);
                String stationid = data.getStringExtra("StatioNameID");
                //ButtonMaterialName.setText(data.getStringExtra("MaterialName"));
                Intent intent = new Intent();
                intent.putExtra("StatioNameID", stationid);
                intent.putExtra("StatioName", MaterialStation);
                setResult(Common.AlertStn1, intent);
                finish();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private boolean dbvalue() {

        try {
            //DatabaseHandler db1 = new DatabaseHandler(AlertStnReqStatewiseActivity.this);
            SQLiteDatabase sql = db.getWritableDatabase();
            Cursor cursor = sql.rawQuery(
                    "SELECT DISTINCT NetworkCode FROM ConnectionStatusFiltermob", null);// SoundLevel_new

            System.out.println("----------  dbvalue screen cursor count -- " + cursor.getCount());

            if (cursor != null && cursor.getCount() > 0) {

                cursor.close();
                /*sql.close();
                db1.close();*/
                return true;

            } else {

                cursor.close();
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

    private void fetchdata() {
        // new DownloadxmlsDataURL_new().execute();
        if (asyncfetch_csnstate == null) {
            iv.setVisibility(View.VISIBLE);
            ((ProgressBar) findViewById(R.id.progressBar1))
                    .setVisibility(View.GONE);

            Log.e("async", "null");
            asyncfetch_csnstate = new DownloadStation();
            asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            if (asyncfetch_csnstate.getStatus() == AsyncTask.Status.RUNNING) {
                Log.e("async", "running");
                iv.setVisibility(View.GONE);
                ((ProgressBar) findViewById(R.id.progressBar1))
                        .setVisibility(View.VISIBLE);
            }
        }

    }

    public class DownloadStation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String Url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMasterMobile?Mobile=" + mobno;
            Log.e("All Station", "Url=" + Url);

            try {
                resposmsg = ut.httpGet(Url);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
                            + e.getMessage() + " " + Ldate);
                }
            }

            if (resposmsg.contains("<InstalationId>")) {
                sop = "valid";
                //DatabaseHandler db = new DatabaseHandler(AlertStnReqStatewiseActivity.this);
                SQLiteDatabase sql = db.getWritableDatabase();
                String columnName, columnValue;
               // sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusFiltermob");
                //sql.execSQL(ut.getConnectionStatusFiltermob());
                sql.delete("ConnectionStatusFiltermob",null,null);

                Cursor cur1 = sql.rawQuery(
                        "SELECT * FROM ConnectionStatusFiltermob", null);
                cur1.getCount();
                ContentValues values2 = new ContentValues();
                NodeList nl2 = ut.getnode(resposmsg, "Table");

                Log.e("All Station Data ", "get length : " + nl2.getLength());
                for (int i = 0; i < nl2.getLength(); i++) {
                    Log.e("All Station Data ", "length : " + nl2.getLength());
                    Element e = (Element) nl2.item(i);
                    for (int j = 0; j < cur1.getColumnCount(); j++) {
                        columnName = cur1.getColumnName(j);
                        columnValue = ut.getValue(e, columnName);
                        Log.e("All Station Data ", "column Name : "
                                + columnName);
                        Log.e("All Station Data ", "column value : "
                                + columnValue);

                        values2.put(columnName, columnValue);

                    }
                    sql.insert("ConnectionStatusFiltermob", null, values2);
                }

                cur1.close();
                /*sql.close();
                db.close();*/

            } else {
                sop = "invalid";
                System.out.println("--------- invalid for project list --- ");
            }
            return sop;

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            iv.setVisibility(View.GONE);
            ((ProgressBar) findViewById(R.id.progressBar1))
                    .setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                if (sop.equals("valid")) {
                    updatelist();
                } else {
                    ut.showD(AlertStnReqStatewiseActivity.this, "invalid");
                }
                iv.setVisibility(View.VISIBLE);
                ((ProgressBar) findViewById(R.id.progressBar1))
                        .setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
                // dff = new SimpleDateFormat("HH:mm:ss");
                // Ldate = dff.format(new Date());

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

    }

    protected void showD(String string) {
        // TODO Auto-generated method stub

        final Dialog myDialog = new Dialog(AlertStnReqStatewiseActivity.this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.dialoginfosmall);
        myDialog.setCancelable(true);
        // myDialog.getWindow().setGravity(Gravity.BOTTOM);

        TextView txt = (TextView) myDialog
                .findViewById(R.id.dialoginfogototextsmall);
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
                .findViewById(R.id.gotobtndialoginfosmall);
        btn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                myDialog.dismiss();
                // finish();

            }
        });

        myDialog.show();

    }

    @Override
    public void onBackPressed() {

        /*Intent i = new Intent(AlertStnReqStatewiseActivity.this, AlrtCreateActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AlertStnReqStatewiseActivity.this.startActivity(i);*/
        finish();

    }

    private boolean isnet() {
        Context context = AlertStnReqStatewiseActivity.this;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private void updatelist() {
        searchResults.clear();
       // DatabaseHandler db = new DatabaseHandler(this);
        SQLiteDatabase sql = db.getWritableDatabase();
        int count = 0;
        Cursor c = sql.rawQuery("SELECT DISTINCT NetworkCode FROM ConnectionStatusFiltermob",
                null);
		/*Cursor c = sql
				.rawQuery(
						"Select distinct InstallationDesc,InstalationId from ConnectionStatusFiltermob Order by InstallationDesc",
						null);*/
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {

                String Type = c.getString(0);

				/*Cursor c1 = sql.rawQuery(
						"Select distinct StatioName from AllStation Where NetworkCode='"
								+ Type + "'", null);
				count = c1.getCount();
				

				Type = Type.replaceAll("0", "");
				Type = Type.replaceAll("1", "");
				if (!Type.trim().equalsIgnoreCase("")) {*/
                StateList sitem = new StateList();
                sitem.SetNetworkCode(Type);
                //sitem.Setcount(count);
                searchResults.add(sitem);

                //}
            } while (c.moveToNext());

        }
        lstcsn.setAdapter(new AlertStnReqStatewiseAdapter(
                AlertStnReqStatewiseActivity.this, searchResults));

    }

}
