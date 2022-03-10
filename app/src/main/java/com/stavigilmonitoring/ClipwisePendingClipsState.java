package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

import com.adapters.pendingclipwisestatnadp;
import com.beanclasses.NonRepStateBean;
import com.database.DBInterface;

public class ClipwisePendingClipsState extends Activity {

	List<NonRepStateBean> searchResults;
	ImageView iv;
	static SimpleDateFormat dff;
	static String Ldate;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop, responsemsg, mobno;
	static DownloadxmlsDataURL_new asyncfetch_csnstate;
	GridView lstcsn;
	int icount = 0, nAdcnt = 0;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.pendingclipwisestate);

		searchResults = new ArrayList<NonRepStateBean>();
		lstcsn = findViewById(com.stavigilmonitoring.R.id.lstcsn);
		iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_tvstatus_main);

		db = new DatabaseHandler(getBaseContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		Log.e("Downtime...", " dbval : " + dbvalue());

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		/*
		 * if (dbSubnet()) { if (dbvalue()) { updatelist(); } else if
		 * (ut.isnet(getApplicationContext())) { fetchdata(); } else {
		 * ut.showD(getApplicationContext(), "nonet"); } } else if
		 * (ut.isnet(ClipwisePendingClipsState.this)) { new
		 * DownloadSubState().execute(); } else {
		 * ut.showD(getApplicationContext(), "nonet"); }
		 */

		if (dbvalue()) {
			updatelist();

		} else if (ut.isnet(getApplicationContext())) {
			fetchdata();

		} else {
			try{
				ut.showD(ClipwisePendingClipsState.this, "nonet");
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		lstcsn.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/*
				 * Intent i = new Intent(PendingClipsStateWise.this,
				 * PendingClipsMain.class); i.putExtra("Type",
				 * searchResults.get(position).GetStateName());
				 * startActivity(i);
				 */
			//	check(searchResults.get(position).GetStateName());
			//	Boolean value = subnetworkcheck(searchResults.get(position)
			//			.GetStateName());
			//	if (value) {
					Intent i = new Intent(ClipwisePendingClipsState.this, ClipwisePendingClips.class);
					i.putExtra("NetCode", searchResults.get(position).GetStateName());
				    startActivity(i);
					

			/*	} else {
					Intent i = new Intent(ClipwisePendingClipsState.this,
							PendingClipsMain.class);
					i.putExtra("Type", searchResults.get(position)
							.GetStateName());
					i.putExtra("SubType", searchResults.get(position)
							.GetStateName());
					startActivity(i);

				}*/
			}
		});

		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// PendingClips

				if (ut.isnet(ClipwisePendingClipsState.this)) {
					//DatabaseHandler db = new DatabaseHandler(getBaseContext());
					SQLiteDatabase sql = db.getWritableDatabase();
					Cursor c = sql.rawQuery("DELETE FROM PendingClips", null);
					int ct = c.getCount();
					c.close();
					/*db.close();
					sql.close();*/
					new DownloadSubState().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					try{
						ut.showD(ClipwisePendingClipsState.this, "nonet");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				/*
				 * if (isnet()) { asyncfetch_csnstate = null;
				 * asyncfetch_csnstate = new DownloadxmlsDataURL_new();
				 * asyncfetch_csnstate.execute(); // fetchdata(); } else {
				 * showD("nonet"); }
				 */
			}
		});

	}

	private void check(String type) {
		//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();

		ArrayList<String> mSubnetworklist = new ArrayList<String>();
		String qrery = "SELECT DISTINCT instalationid FROM PendingClips WHERE PendingClips.NetworkCode='"
				+ type + "'";// instalationid

		Cursor c = sql.rawQuery(qrery, null);
		int as = c.getCount();
		c.moveToFirst();
		do {
			int stncnt = 0;
			String inst = c.getString(c.getColumnIndex("instalationid"));

			Cursor cur = sql
					.rawQuery(
							"SELECT DISTINCT SubNetworkCode FROM ConnectionStatusFilter WHERE InstalationId='"
									+ inst + "'", null);
			int ct = cur.getCount();
			cur.moveToFirst();
			do {
				String sub = cur.getString(0);

				mSubnetworklist.add(sub);
			} while (cur.moveToNext());

			mSubnetworklist.clear();

		} while (c.moveToNext());
		c.close();
		/*db1.close();
		sql.close();*/
	}

	private boolean subnetworkcheck(String type) {
		//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();

		ArrayList<String> mSubnetworklist = new ArrayList<String>();
		try {

			String qrery = "SELECT DISTINCT ConnectionStatusFilter.SubNetworkCode FROM ConnectionStatusFilter INNER JOIN PendingClips ON ConnectionStatusFilter.InstalationId=PendingClips.instalationid WHERE PendingClips.NetworkCode='"
					+ type + "'";

			Cursor c = sql.rawQuery(qrery, null);
			c.getCount();

			if (c.getCount() == 0) {
				c.close();
				/*db1.close();
				sql.close();*/
				return false;

			} else if (c.getCount() > 0) {

				c.moveToFirst();
				do {

					int stncnt = 0;
					String Type = c.getString(0);

					mSubnetworklist.add(Type);

				} while (c.moveToNext());
				c.close();
				/*db1.close();
				sql.close();*/

			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		if (mSubnetworklist.contains(type) && mSubnetworklist.size() > 1) {
			return true;
		} else {
			// return false;

			if (mSubnetworklist.contains(type)) {
				return false;
			} else {
				return true;
			}

		}

	}

	private Boolean dbSubnet() {

		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();

			Cursor cursor = sql.rawQuery(
					"SELECT * FROM ConnectionStatusFilter", null);// SoundLevel_new

			System.out.println("----------  dbSubnet screen cursor count -- "
					+ cursor.getCount());

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
			// ut = new utility();
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

	private boolean dbvalue() {
		try {

			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();

			// Cursor cursor = sql.rawQuery("SELECT *   FROM AllStation", null);
			Cursor c = sql.rawQuery("SELECT * FROM PendingClips", null);
			if (c != null && c.getCount() > 0) {
				if (c.getColumnIndex("instalationid") < 0) {
					c.close();// InstalationId InstallationId
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
						sql.insert("PendingClips", null, values1);
						String s = "val";
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
						ut.showD(ClipwisePendingClipsState.this, "nodata");
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
				// ut = new utility();
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
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}

	}

	private void updatelist() {
		searchResults.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor c = sql
				.rawQuery(
						"SELECT DISTINCT NetworkCode FROM PendingClips ORDER BY NetworkCode",
						null);
		c.moveToFirst();
		int cnt = 0, Adcnt = 0;
		Log.e("Pending n/w count", "" + c.getCount());
		if (c.getCount() > 0) {
			do {
				String station = c.getString(c.getColumnIndex("NetworkCode"));
				Cursor c1 = sql.rawQuery(
						"SELECT DISTINCT InstallationDesc FROM PendingClips WHERE NetworkCode='"
								+ station + "'", null);

				Cursor c2 = sql.rawQuery(
						"SELECT DISTINCT  AdvertisementDesc FROM PendingClips WHERE NetworkCode='"
								+ station + "'", null);
				cnt = c1.getCount();
				Adcnt = c2.getCount();

				NonRepStateBean s = new NonRepStateBean();
				s.SetStateName(station);
				s.SetAdCnt(Adcnt);
				s.SetCount(cnt);
				searchResults.add(s);
				cnt = 0;
				Adcnt = 0;
			} while (c.moveToNext());
		}

		lstcsn.setAdapter(new pendingclipwisestatnadp(ClipwisePendingClipsState.this, searchResults));

	}

	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		SharedPreferences prefclips = getApplicationContext()
				.getSharedPreferences("MyPrefclips", Context.MODE_PRIVATE); // 0
																			// -
																			// for
																			// private
																			// mode
		Editor editorclips = prefclips.edit();

		for (int i = 0; i < searchResults.size(); i++) {
			icount = icount + searchResults.get(i).GetSCount();
			nAdcnt = nAdcnt + searchResults.get(i).GetAdCnt();
		}
		editorclips.putString("clips", "" + icount);

		editorclips.putString("ClipCnt", "" + nAdcnt);

		editorclips.commit();

		/*Intent i = new Intent(getBaseContext(), SelectMenu.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getBaseContext().startActivity(i);*/
		finish();

	}

	public class DownloadSubState extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {

			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			String columnName, columnValue;
			String urlnet = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMaster";
			urlnet = urlnet.replaceAll(" ", "%20");
			Log.e("installation for Subnet", "6th" + urlnet);

			try {
				responsemsg = ut.httpGet(urlnet);
				NodeList NL = ut.getnode(responsemsg, "Table");
				Log.e("SubnetCount", "len :" + NL.getLength());

				if (responsemsg.contains("<Table>")) {
					sop = "valid";
					//sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusFilter");
					//sql.execSQL(ut.getConnectionStatusFilter());
					sql.delete("ConnectionStatusFilter",null,null);

					Cursor cur1 = sql.rawQuery(
							"SELECT * FROM ConnectionStatusFilter", null);
					ContentValues values2 = new ContentValues();

					Log.e("ConnectionFilter data..",
							" fetch data : " + NL.getLength());
					for (int i = 0; i < NL.getLength(); i++) {
						Element e = (Element) NL.item(i);
						for (int j = 0; j < cur1.getColumnCount(); j++) {
							columnName = cur1.getColumnName(j);

							columnValue = ut.getValue(e, columnName);
							Log.e("ConnectionFilter data..", " fetch data : "
									+ columnValue);
							values2.put(columnName, columnValue);
							// SubnetString = "Valid";

						}
						long ad = sql.insert("ConnectionStatusFilter", null,
								values2);
					}

					cur1.close();
					/*sql.close();
					db.close();*/

				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();

				/*sql.close();
				db.close();*/
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (dbvalue()) {
				updatelist();
				iv.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
						.setVisibility(View.GONE);
			} else if (ut.isnet(getApplicationContext())) {
				fetchdata();
			} else {
				try{
					ut.showD(ClipwisePendingClipsState.this, "nonet");
					iv.setVisibility(View.VISIBLE);
					((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
							.setVisibility(View.GONE);
				}catch (Exception e){
					e.printStackTrace();
				}
			}

		}

	}

}
