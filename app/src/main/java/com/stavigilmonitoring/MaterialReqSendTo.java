package com.stavigilmonitoring;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.adapters.MaterialSendTOAdp;
import com.beanclasses.reporteeBean;

public class MaterialReqSendTo extends Activity {

	String MobileNo;
	static SimpleDateFormat dff;
	static String Ldate;
	List<reporteeBean> searchResults;
	ImageView iv;
	String sop, responsemsg, mobno;
	DownloadxmlsDataURL_new asyncfetch_csnstate;
	ListView lstcsn;
	com.stavigilmonitoring.utility ut;
	DatabaseHandler db1;
	TextView txtmaterialreq_name_sendto;
	String ReportingName, ReportingID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_req_sendto);
		Intent intent = getIntent();
		// intent.putExtra("mobileno", mobno);
		MobileNo = intent.getStringExtra("mobileno");
		searchResults = new ArrayList<reporteeBean>();
		lstcsn = (ListView) findViewById(R.id.materialreq_sendto);
		iv = (ImageView) findViewById(R.id.button_refresh_sendto);
		txtmaterialreq_name_sendto = (TextView) findViewById(R.id.txtmaterialreq_name_sendto);
		txtmaterialreq_name_sendto.setText("Material Request send to");
		db1 = new DatabaseHandler(getBaseContext());

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
					int position, long id) {

				Intent intent = new Intent();

				String cashiername = searchResults.get(position).getReporteeName();

				intent.putExtra("MaterialRqesendto",searchResults.get(position).getReporteeName());
				intent.putExtra("MaterialRqesendtoID",	searchResults.get(position).getReporteeID());
				setResult(Common.MaterialRqesendto, intent);
				finish();
			}
		});

		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isnet()) {
					asyncfetch_csnstate = null;
					asyncfetch_csnstate = new DownloadxmlsDataURL_new();
					asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					// fetchdata();
				} else {
					showD("nonet");
				}
			}
		});
	}

	private boolean dbvalue() {

		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db1.getWritableDatabase();
			Cursor cursor = sql.rawQuery("SELECT * FROM Reporting", null);

			System.out.println("----------  dbvalue screen cursor count -- "
					+ cursor.getCount());

			if (cursor != null && cursor.getCount() > 0) {

				cursor.close();
				return true;

			} else {

				cursor.close();
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
			asyncfetch_csnstate = new DownloadxmlsDataURL_new();
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

	public class DownloadxmlsDataURL_new extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db1.getWritableDatabase();
			String url;

			sop = "valid";
			String columnName, columnValue;
			Log.e("Tag", " ******* WORKING ON SYNCDATA *********");

			url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/getreportingTo?Mobileno="
					+ MobileNo;

			Log.e("material", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = ut.httpGet(url);
				NodeList NL = ut.getnode(responsemsg, "Table1");
				Log.e("material", "resmsg : " + responsemsg);
			
            	if (responsemsg.contains("<UserMasterid>")) {
					sop = "valid";
					sql.execSQL("DROP TABLE IF EXISTS Reporting");
					//sql.delete("Reporting",null,null);

	                sql.execSQL("Create table Reporting ( UserMasterid TEXT , Username TEXT, email TEXT)");//usermasterid,Username
	                Log.e("Tag", " ******* WORKING ON Material Reporting table *********");
					Cursor cur1 = sql.rawQuery("SELECT * FROM Reporting", null);
					ContentValues values2 = new ContentValues();
                    Log.e("WorkType data...", " fetch data : " + NL.getLength());
					for (int i = 0; i < NL.getLength(); i++) {
						Element e = (Element) NL.item(i);
						Log.e("Reporting data...", " fetch data : " + i);

						for (int j = 0; j < cur1.getColumnCount(); j++) {
							columnName = cur1.getColumnName(j);

							columnValue = ut.getValue(e, columnName);
							Log.e("Reporting data...", " fetch data : "
									+ columnValue);
							values2.put(columnName, columnValue);
						}
						long ad = sql.insert("Reporting", null, values2);
						Log.e("Tag", " ******* WORKING ON MAterial DATA inserted *********");
					}

					cur1.close();

				}
			} catch (NullPointerException e) {
				responsemsg = "Error";
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

			} catch (UnknownHostException e) {
				responsemsg = "Error";
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
				responsemsg = "Error";
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

			return responsemsg;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				if (responsemsg.equalsIgnoreCase("Error")) {
					showD("invalid");
				} else {

					// searchResults.add(responsemsg);
					updatelist();
				}
				iv.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(R.id.progressBar1))
						.setVisibility(View.GONE);

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
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}

	}

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(MaterialReqSendTo.this);
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
		// super.onBackPressed();
		Intent intent = new Intent();
		intent.putExtra("MaterialRqesendto",ReportingName);
		intent.putExtra("MaterialRqesendtoID",ReportingID);
		setResult(Common.MaterialRqesendto, intent);
		finish();
	}

	private boolean isnet() {
		Context context = this.getApplicationContext();
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
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db1.getWritableDatabase();
		int count = 0;
		Cursor c = sql.rawQuery("SELECT * FROM Reporting order by Username desc", null);

			if (c.getCount() > 0) {
			c.moveToFirst();
			do {
             reporteeBean bean = new reporteeBean();
				 ReportingName = c.getString(c.getColumnIndex("Username"));
				 ReportingID = c.getString(c.getColumnIndex("UserMasterid"));
				bean.setReporteeName(ReportingName);
				bean.setReporteeID(ReportingID);

				searchResults.add(bean);

				// }
			} while (c.moveToNext());

		}

		MaterialSendTOAdp mat = new MaterialSendTOAdp(getApplicationContext(), searchResults);

		lstcsn.setAdapter(mat);

	}

}

