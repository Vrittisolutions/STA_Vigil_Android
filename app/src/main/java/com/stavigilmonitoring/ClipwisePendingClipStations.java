package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.ClipPendingClipStationAdp;
import com.adapters.PendingClipsMainAdapt;
import com.beanclasses.StateList;
import com.database.DBInterface;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ClipwisePendingClipStations extends Activity {
	List<StateList> searchResults;
	ImageView iv;
	String sop, responsemsg, mobno, Adv, AdvCode;
	static DownloadxmlsDataURL_new asyncfetch_csnstate;
	ListView lstcsn;
	String Type;
	PendingClipsMainAdapt ClipsAdapt;
	String finalDate, Subtype;
	static SimpleDateFormat dff;
	static String Ldate;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pendingclipsstatewise_list);

		searchResults = new ArrayList<StateList>();
		lstcsn = (ListView) findViewById(com.stavigilmonitoring.R.id.lstcsn);
		iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_tvstatus_main);
		Intent extras = getIntent();
		Adv = extras.getStringExtra("AdvName");
		AdvCode = extras.getStringExtra("AdvCode");
		TextView tvStation = (TextView) findViewById(com.stavigilmonitoring.R.id.tvpendingclipstitle);
		tvStation.setText(Adv);

		db = new DatabaseHandler(getBaseContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		if (dbvalue()) {
			updatelist();
		} else if (ut.isnet(getApplicationContext())) {
			fetchdata();
		} else {
			try{
				ut.showD(ClipwisePendingClipStations.this, "nonet");
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		lstcsn.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
			}
		});

		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ut.isnet(getApplicationContext())) {
					asyncfetch_csnstate = null;
					asyncfetch_csnstate = new DownloadxmlsDataURL_new();
					asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					try{
						ut.showD(ClipwisePendingClipStations.this,"nonet");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
			}
		});

	}

	public void FilterClick(View v) {
		if (((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext)).getVisibility() == View.VISIBLE) {
			((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
					.setVisibility(View.GONE);
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		} else if (((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext)).getVisibility() == View.GONE) {
			((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
					.setVisibility(View.VISIBLE);
			EditText textView = (EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext);
			textView.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
		}

	}

	private boolean dbvalue() {
		try {

			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			// Cursor cursor = sql.rawQuery("SELECT *   FROM AllStation", null);
			Cursor c = sql.rawQuery("SELECT * FROM PendingClips", null);
			if (c != null && c.getCount() > 0) {
				if (c.getColumnIndex("instalationid") < 0) {
					c.close();
					/*sql.close();
					db1.close();*/
					return false;
				} else {
					c.close();
					/*sql.close();
					db1.close();*/
					return true;
				}
			} else {
				c.close();
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

	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		/*
		 * Intent i = new Intent(getBaseContext(), PendingClipsStateWise.class);
		 * i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * getBaseContext().startActivity(i); finish();
		 */

	}

	private void fetchdata() {
		// new DownloadxmlsDataURL_new().execute();
		asyncfetch_csnstate = null;
		if (asyncfetch_csnstate == null) {
			iv.setVisibility(View.VISIBLE);
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
					.setVisibility(View.GONE);

			Log.e("async", "null");
			asyncfetch_csnstate = new DownloadxmlsDataURL_new();
			asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			if (asyncfetch_csnstate.getStatus() == AsyncTask.Status.RUNNING) {
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
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			String url = "http://sta.vritti.co/imedia/STA_Announcement/TimeTable.asmx/GetListOfPendingDownloadingAdvertisment?Mobile="
					+ mobno + "&NetworkCode='ksrtc'";

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = com.stavigilmonitoring.utility.httpGet(url);
				Log.e("csn status", "resmsg : " + responsemsg);
				//sql.execSQL("DROP TABLE IF EXISTS PendingClips");
				//sql.execSQL(ut.getPendingClips());
				sql.delete("PendingClips",null,null);

				if (responsemsg.contains("<instalationid>")) {
					sop = "valid";
					String columnName, columnValue;
					Cursor cur = sql.rawQuery("SELECT * FROM PendingClips",
							null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table1");
					Log.e("All Station data...",
							" fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							columnName = cur.getColumnName(j);

							columnValue = ut.getValue(e, columnName);
							values1.put(columnName, columnValue);

							// Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
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
			super.onPostExecute(result);
			try {
				if (sop == "valid") {
					updatelist();
				} else {
					try{
						ut.showD(ClipwisePendingClipStations.this, "nonet");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				iv.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
						.setVisibility(View.GONE);

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
			super.onPreExecute();
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}
	}

	private void updatelist() {
		searchResults.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		String AdsDsce, AdsCode, AddedDate, Status,clrDate;
		String AddeddateSplit,clrDatesplit,Stationname,StationId;
		/*Cursor c1 = sql.rawQuery(
				"SELECT * FROM PendingClips WHERE AdvertisementDesc='" + Adv
						+ "' ORDER BY FileName", null);*/

		Cursor c1 = sql.rawQuery(
				"SELECT * FROM PendingClips WHERE FileName='" + AdvCode
						+ "' ORDER BY FileName", null);
		c1.moveToFirst();
		do {
			int Count = c1.getCount();
			Stationname = c1.getString(c1.getColumnIndex("InstallationDesc"));
			StationId = c1.getString(c1.getColumnIndex("instalationid"));
		/*	AdsDsce = c1.getString(c1.getColumnIndex("AdvertisementDesc"));
			AdsCode = c1.getString(c1.getColumnIndex("FileName"));
			int index = AdsCode.indexOf(".");
			AdsCode = AdsCode.substring(0, index);*/
			AddedDate = c1.getString(c1.getColumnIndex("AddedDT"));
			AddeddateSplit = Split(AddedDate);
			clrDate = c1.getString(c1.getColumnIndex("CLR"));
			clrDatesplit = Split(clrDate);
			Status = c1.getString(c1.getColumnIndex("IsTransfer"));
            StateList s = new StateList();
			s.SetAdsCode(StationId);
			s.SetAdsDesc(Stationname);
			s.SetAddedDate(AddeddateSplit);
            s.setClrDate(clrDatesplit);
			s.SetStatus(Status);
			searchResults.add(s);
			Count = 0;

		} while (c1.moveToNext());

		lstcsn.setAdapter(new ClipPendingClipStationAdp(ClipwisePendingClipStations.this, searchResults));

	}
	public String Split(String AddedDate){
		try {
			String datestr = AddedDate.substring(0, AddedDate.indexOf(" "));
			int a = AddedDate.indexOf(" ");

			// DateFormat formatter;
			String date;
			Date date1;
			SimpleDateFormat dateformatyyyyMMdd = new SimpleDateFormat(
					"MM/dd/yyyy");
			date1 = dateformatyyyyMMdd.parse(datestr);

			SimpleDateFormat formatter = new SimpleDateFormat(
					"dd MMM yyyy");
			date = formatter.format(date1);

			AddedDate = date + " "
					+ AddedDate.substring(AddedDate.indexOf(" "));
			// fdhr=fd;
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
		return AddedDate;
		
	}
}
