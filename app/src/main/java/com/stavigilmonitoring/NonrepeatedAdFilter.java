package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.NonReportedStateAdapter;
import com.adapters.SoundAdapter;
import com.beanclasses.NonRepStateBean;
import com.database.DBInterface;
import com.stavigilmonitoring.SoundLevelStateFilter.Downloadxmlsound;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class NonrepeatedAdFilter extends Activity {

	private List<NonRepStateBean> searchResults;
	private ImageView mRefresh, btnadd;
	private GridView mListView;
	private ProgressBar mProgressBar;
	private LinearLayout mAllnet;
	private TextView mText, nonrepfilt;
	private TextView mAllCount;
	private TextView mAdvcnt;
	private com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	static SimpleDateFormat dff;
	static String Ldate;
	private String mobno, type;
	static Downloadxmlsound asyncfetch_csnstate;
	String responsemsg, Syncdate, sop, urlnet;
	int stncount = 0;
	private int scount = 0;//advcnt
	private int advcnt = 0;
	SoundAdapter adapter;
	static DownloadxmlsDataURL_new asyncfetch_nonstatefiltr;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.stavigilmonitoring.R.layout.nonreportedstatefilter);

		mRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_nonrep_filter);
		btnadd = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_alert_add);
		btnadd.setImageResource(com.stavigilmonitoring.R.drawable.work_assign);
		btnadd.setVisibility(View.VISIBLE);
		mListView = findViewById(com.stavigilmonitoring.R.id.listFiternonrep);
		mProgressBar = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.nonrepstFilterState);
		mAllnet = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.networksumnonrep);
		mText = (TextView) findViewById(com.stavigilmonitoring.R.id.tvsNamenonrep);
		nonrepfilt = (TextView)findViewById(com.stavigilmonitoring.R.id.nonrepfilt);
		mAllCount = (TextView) findViewById(com.stavigilmonitoring.R.id.tvsCntnonrep);//
		mAdvcnt = (TextView) findViewById(com.stavigilmonitoring.R.id.tvsAdcntnonrep);

		db = new DatabaseHandler(getBaseContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		searchResults = new ArrayList<NonRepStateBean>();
		mobno = dbi.GetPhno();
		dbi.Close();
		Intent intent = getIntent();
		type = intent.getStringExtra("Type");
		mText.setText(type + "-All");
		nonrepfilt.setText("Non Reported Stations - "+type);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

			if (dbSubnet()) {
			if (dbvalue()) {
				updatelist();
			} else if (ut.isnet(getApplicationContext())) {
				fetchdata();
			} else {
				ut.showD(NonrepeatedAdFilter.this, "nonet");
			}
		} else if (ut.isnet(NonrepeatedAdFilter.this)) {
			new DownloadSubnetnonrepfilter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			ut.showD(NonrepeatedAdFilter.this, "nonet");
		}

		mRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (dbSubnet()) {
					if (ut.isnet(getApplicationContext())) {
						fetchdata();
					} else {
						ut.showD(NonrepeatedAdFilter.this, "nonet");
					}
				} else if (ut.isnet(NonrepeatedAdFilter.this)) {
					//DatabaseHandler db = new DatabaseHandler(getBaseContext());
					SQLiteDatabase sql = db.getWritableDatabase();
					Cursor c = sql.rawQuery("DELETE FROM NonrepeatedAd", null);
					int ct = c.getCount();
					c.close();
					new DownloadSubnetnonrepfilter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					ut.showD(NonrepeatedAdFilter.this, "nonet");
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

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(getApplicationContext(), NonrepeatedAdMain.class);
				intent.putExtra("Type", type);
				intent.putExtra("SubType", searchResults.get(position).GetStateName());
				intent.putExtra("CallFrom","NonrepeatedAdFilter");
				//adapter.notifyDataSetChanged();
				startActivity(intent);

			}
		});

		mAllnet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						NonrepeatedAdMainAll.class);
				intent.putExtra("Type", type);
				intent.putExtra("CallFrom","NonrepeatedAdFilter");
				startActivity(intent);
			}
		});
	}

	private void updatelist() {
		searchResults.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor c3 = sql.rawQuery("SELECT DISTINCT a.SubNetworkCode FROM ConnectionStatusFilter a INNER JOIN NonrepeatedAd b ON a.InstalationId=b.StationMasterId where b.Type='"
									+ type + "' ORDER BY a.SubNetworkCode",null);
//SELECT DISTINCT a.SubNetworkCode FROM ConnectionStatusFilter a INNER JOIN NonrepeatedAd b ON a.InstalationId=b.StationMasterId where b.Type='"+ type + "'
		//Cursor c = sql.rawQuery("SELECT * FROM NonrepeatedAd WHERE Type='"+ Type + "' ORDER BY InstallationDesc", null);
		if (c3.getCount() > 0) {
			c3.moveToFirst();
			do {
				String Type = c3.getString(0);
				Cursor c1 = sql.rawQuery(
						"SELECT DISTINCT b.StationMasterId AS count FROM NonrepeatedAd b " +
								"INNER JOIN ConnectionStatusFilter a ON a.InstalationId=b.StationMasterId WHERE a.SubNetworkCode='"
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
					c1.close();
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

					/*Collections.sort(searchResults, new Comparator<NonRepStateBean>() {
						public int compare(NonRepStateBean o1, NonRepStateBean o2) {
							if (o1.getDdate() == null || o2.getDdate() == null)
								return 0;
							return o2.getDdate().compareTo(o1.getDdate());
						}
					});*/
				}
			} while (c3.moveToNext());
			c3.close();
		}
		scount = 0;
		for (int i = 0; i < searchResults.size(); i++){
			scount = scount + searchResults.get(i).GetSCount();
			advcnt = advcnt + searchResults.get(i).GetAdCnt();
			}
		mAllCount.setText("" + scount);
		mAdvcnt.setText("Non-Reported Ads : "+advcnt);
		mListView.setAdapter(new NonReportedStateAdapter(
				NonrepeatedAdFilter.this, searchResults));

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

		asyncfetch_nonstatefiltr = new DownloadxmlsDataURL_new();
		asyncfetch_nonstatefiltr.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@SuppressLint("LongLogTag")
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
					Log.e("non reported.....in back..","...count : " + nl.getLength());

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
					ut.showD(NonrepeatedAdFilter.this, "nodata");
				}
				mRefresh.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.GONE);
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
			mRefresh.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
		}
	}

	public class DownloadSubnetnonrepfilter extends
			AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mRefresh.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
		}

		@SuppressLint("LongLogTag")
		@Override
		protected String doInBackground(String... params) {

		//	DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			String columnName, columnValue;
			urlnet = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMaster";
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

					Log.e("ConnectionFilter data...",
							" fetch data : " + NL.getLength());
					for (int i = 0; i < NL.getLength(); i++) {
						Element e = (Element) NL.item(i);
						for (int j = 0; j < cur1.getColumnCount(); j++) {
							columnName = cur1.getColumnName(j);

							columnValue = ut.getValue(e, columnName);
							Log.e("ConnectionFilter data...", " fetch data : "
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
				mRefresh.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.GONE);
			} else if (ut.isnet(getApplicationContext())) {
				fetchdata();
			} else {
				ut.showD(NonrepeatedAdFilter.this, "nonet");
				mRefresh.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.GONE);
			}

		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent i = new Intent(getBaseContext(), NonReportedAdStatewise.class);
	//	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}
}
