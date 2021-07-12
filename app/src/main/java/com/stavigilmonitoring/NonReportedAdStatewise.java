package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.NonReportedStateAdapter;
import com.beanclasses.NonRepStateBean;
import com.database.DBInterface;

import android.app.Activity;
import android.content.ContentValues;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class NonReportedAdStatewise extends Activity {
	List<NonRepStateBean> searchResults;
	ImageView iv,btnadd;
	int icount = 0, nAdcnt = 0;
	GridView nonrepeated;
	static SimpleDateFormat dff;
	static String Ldate;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String mobno = "", responsemsg, sop;
	static DownloadxmlsDataURL_new asyncfetch_nonstate;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.nonreportedadstatewise);

		searchResults = new ArrayList<NonRepStateBean>();
		iv = (ImageView) findViewById(R.id.button_refresh_nonrepeated_main);
		btnadd = (ImageView) findViewById(R.id.button_alert_add);
		btnadd.setImageResource(R.drawable.work_assign);
		btnadd.setVisibility(View.VISIBLE);
		nonrepeated = findViewById(R.id.nonrepeatedadmain);

		db = new DatabaseHandler(getBaseContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		if (asyncfetch_nonstate != null
				&& asyncfetch_nonstate.getStatus() == AsyncTask.Status.RUNNING) {
			Log.e("async", "running");
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}

		if (dbSubnet()) {
			if (dbvalue()) {
				updatelist();
			} else if (ut.isnet(getApplicationContext())) {
				fetchdata();
			} else {
				try{
					ut.showD(this, "nonet");
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		} else if (ut.isnet(NonReportedAdStatewise.this)) {
			new DownloadSubnetnonrep().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			try{
				ut.showD(this, "nonet");
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		/*
		 * if (dbvalue()) { updatelist(); } else if (isnet()) { fetchdata(); }
		 * else { showD("nonet"); }
		 */
		nonrepeated.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/*
				 * Intent i = new Intent(NonReportedAdStatewise.this,
				 * NonrepeatedAdMain.class); i.putExtra("Type",
				 * searchResults.get(position).GetStateName());
				 * startActivity(i);
				 */

				Boolean value = subnetworkcheck(searchResults.get(position)
						.GetStateName());
				if (value) {
					Intent i = new Intent(NonReportedAdStatewise.this, NonrepeatedAdFilter.class);
					i.putExtra("Type", searchResults.get(position).GetStateName());
					i.putExtra("Count", searchResults.get(position).GetSCount());
					startActivity(i);
					finish();

				} else {
					Intent i = new Intent(NonReportedAdStatewise.this, NonrepeatedAdMain.class);
					i.putExtra("Type", searchResults.get(position)
							.GetStateName());
					i.putExtra("SubType", searchResults.get(position)
							.GetStateName());
					startActivity(i);

				}
			}
		});
		btnadd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),WorkAssign_AssignActivity.class);
				intent.putExtra("Activity", "NonReportedAdStatewise");
				intent.putExtra("Type", "");
				startActivity(intent);
				finish();
			}
		});
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (ut.isnet(NonReportedAdStatewise.this)) {
					//DatabaseHandler db = new DatabaseHandler(getBaseContext());
					SQLiteDatabase sql = db.getWritableDatabase();
					Cursor c = sql.rawQuery("DELETE FROM NonrepeatedAd", null);
					int ct = c.getCount();
					c.close();
					new DownloadSubnetnonrep().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					try{
						ut.showD(NonReportedAdStatewise.this, "nonet");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				/*
				 * if (isnet()) { asyncfetch_nonstate = null;
				 * asyncfetch_nonstate = new DownloadxmlsDataURL_new();
				 * asyncfetch_nonstate.execute(); // fetchdata(); } else {
				 * showD("nonet"); }
				 */
			}
		});
	}

	

	private boolean subnetworkcheck(String type) {
		//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		ArrayList<String> mSubnetworklist = new ArrayList<String>();
		try {
			Cursor c = sql
					.rawQuery(
							"SELECT DISTINCT a.SubNetworkCode FROM ConnectionStatusFilter a INNER JOIN NonrepeatedAd b ON a.InstalationId=b.StationMasterId where b.Type='"
									+ type + "'", null);

			if (c.getCount() == 0) {
				c.close();
				return false;

			} else if (c.getCount() > 0) {

				c.moveToFirst();
				do {

					int stncnt = 0;
					String Type = c.getString(0);

					mSubnetworklist.add(Type);

				} while (c.moveToNext());
				c.close();
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

	private void updatelist() {
		searchResults.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor c3 = sql.rawQuery("SELECT DISTINCT Type FROM NonrepeatedAd ORDER BY Type",
				null);
		if (c3.getCount() > 0) {
			c3.moveToFirst();
			do {
				String Type = c3.getString(0);
				Cursor c1 = sql.rawQuery(
						"SELECT DISTINCT StationMasterId AS count FROM NonrepeatedAd WHERE Type='"
								+ c3.getString(0) + "'", null);

				int stncnt = c1.getCount();
				int adcnt = 0;
				if (c1.getCount() > 0) {
					c1.moveToFirst();
					do {
						Cursor c2 = sql.rawQuery(
								"SELECT * FROM NonrepeatedAd WHERE StationMasterId='"
										+ c1.getString(0) + "'", null);
						if (c2.getCount() > 0)
							adcnt = adcnt + c2.getCount();
					} while (c1.moveToNext());
				}

				Log.e("lstdata", "type: " + Type + " :stncnt:" + stncnt
						+ " :adcnt" + adcnt);
				NonRepStateBean sitem = new NonRepStateBean();
				//Type = Type.replaceAll("0", "");
				//Type = Type.replaceAll("1", "");
				if (!Type.trim().equalsIgnoreCase("")) {
					sitem.SetStateName(Type);
					sitem.SetCount(stncnt);
					sitem.SetAdCnt(adcnt);
					searchResults.add(sitem);
				}
			} while (c3.moveToNext());
		}

		nonrepeated.setAdapter(new NonReportedStateAdapter(NonReportedAdStatewise.this, searchResults));

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
		//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor cursor = sql.rawQuery("SELECT *   FROM NonrepeatedAd", null);
		try {
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

		asyncfetch_nonstate = new DownloadxmlsDataURL_new();
		asyncfetch_nonstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
			String xx = "";

			// String
			// url="http://vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetNonReportedAdvt_Android_new?Mobile="+mobno;
			String url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/GetNonReportedAdvt_Android_new?Mobile="
					+ mobno;
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = ut.httpGet(url);

				if (responsemsg.contains("<A>")) {
					sop = "valid";
					//DatabaseHandler db = new DatabaseHandler(getBaseContext());

					SQLiteDatabase sql = db.getWritableDatabase();

					//sql.execSQL("DROP TABLE IF EXISTS NonrepeatedAd");
					//sql.execSQL(ut.getNonrepeatedAd());
					sql.delete("NonrepeatedAd",null,null);

					Cursor c = sql
							.rawQuery("SELECT * FROM NonrepeatedAd", null);

					ContentValues values = new ContentValues();
					NodeList nl = ut.getnode(responsemsg, "Table1");
					String msg = "";
					String columnName, columnValue;
					Log.e("non reported.in back..",
							"...count : " + nl.getLength());

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
					}

					c.close();

				} else {
					sop = "invalid";
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
				if (sop.equals("valid")) {
					updatelist();
				} else {
					try{
						ut.showD(NonReportedAdStatewise.this, "nodata");
					}catch (Exception e){
						e.printStackTrace();
					}
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
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		try {

			SharedPreferences pref = getApplicationContext()
					.getSharedPreferences("MyPrefnon", MODE_PRIVATE); // 0 - for
																		// private
																		// mode
			Editor editor = pref.edit();
			System.out.println("----------total number of item in list"
					+ nonrepeated.getCount());
			/*
			 * icount=nonrepeated.getCount(); scountnon=String.valueOf(icount);
			 */

			//nAdcnt = 0;

			for (int i = 0; i < searchResults.size(); i++) {
				icount = icount + searchResults.get(i).GetSCount();
				nAdcnt = nAdcnt + searchResults.get(i).GetAdCnt();
			}
			editor.putString("nonreportedStatus", icount + "");
			editor.putString("advCount", nAdcnt + "");
			editor.commit();
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

		}

		/*Intent i = new Intent(getBaseContext(), SelectMenu.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getBaseContext().startActivity(i);*/
		finish();
	}

	public class DownloadSubnetnonrep extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {

			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			String columnName, columnValue;
			String urlnet = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMaster";
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

				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();

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
				((ProgressBar) findViewById(R.id.progressBar1))
						.setVisibility(View.GONE);
			} else if (ut.isnet(getApplicationContext())) {
				fetchdata();
			} else {
				try{
					ut.showD(NonReportedAdStatewise.this, "nonet");
					iv.setVisibility(View.VISIBLE);
					((ProgressBar) findViewById(R.id.progressBar1))
							.setVisibility(View.GONE);
				}catch (Exception e){
					e.printStackTrace();
				}

			}

		}

	}
}
