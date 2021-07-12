package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.TvStatusAdpt;
import com.beanclasses.TvStatusStateBean;
import com.database.DBInterface;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TvStatusStateFilter extends Activity {
	private List<TvStatusStateBean> searchResults;
	private ImageView mRefresh;
	private GridView mListView;
	private LinearLayout mAllnet;
	private TextView mText;
	private TextView mAllCount;
	private ProgressBar mProgressBar;
	private com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	static SimpleDateFormat dff;
	static String Ldate;
	private String mobno, Type;
	private int scount = 0;
	private int totcount = 0;
	static DownloadxmlsDataURL_new asyncfetch_csnstate;
	String responsemsg, Syncdate, sop, urlnet;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.tvstatusstatefilter);

		mListView =  findViewById(com.stavigilmonitoring.R.id.listFitertv);
		searchResults = new ArrayList<TvStatusStateBean>();
		mRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_tv_filter);
		mProgressBar = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.tvFilterState);
		mAllnet = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.networksumtv);
		mText = (TextView) findViewById(com.stavigilmonitoring.R.id.tvNametv);
		mAllCount = (TextView) findViewById(com.stavigilmonitoring.R.id.tvCnttv);
		Bundle extras = getIntent().getExtras();
		Type = extras.getString("Type");
		mText.setText(Type + "-All");

		db = new DatabaseHandler(getBaseContext());
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();
		if (dbSubnet()) {
			if (dbvalue()) {
				updatelist();
			} else if (ut.isnet(getApplicationContext())) {
				fetchdata();
			} else {
				ut.showD(TvStatusStateFilter.this, "nonet");
			}
		} else if (ut.isnet(TvStatusStateFilter.this)) {
			new DownloadSubStatetvfilter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			ut.showD(TvStatusStateFilter.this, "nonet");
		}
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), TvStatusMain.class);
				intent.putExtra("Type", Type);
				intent.putExtra("SubType", searchResults.get(position)	.getNetworkcode());
				intent.putExtra("CallFrom","TVStatusStateFilter");
				// adapter.notifyDataSetChanged();
				startActivity(intent);

			}
		});

		mAllnet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),	TvStatusMainAll.class);
				intent.putExtra("Type", Type);
				intent.putExtra("CallFrom","TVStatusStateFilter");
				startActivity(intent);

			}
		});
		mRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (ut.isnet(TvStatusStateFilter.this)) {
					//DatabaseHandler db = new DatabaseHandler(getBaseContext());
					SQLiteDatabase sql = db.getWritableDatabase();
					Cursor c = sql.rawQuery("DELETE FROM PendingClips", null);
					int ct = c.getCount();
					c.close();
					//db.close();
					sql.close();
					new DownloadSubStatetvfilter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					ut.showD(TvStatusStateFilter.this, "nonet");
				}

			}
		});

	}

	private void updatelist() {
		searchResults.clear();
		// arrlist.clear();
		int cnt = 0;

	//	DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c1 = sql
				.rawQuery(
						"SELECT DISTINCT ConnectionStatusFilter.SubNetworkCode FROM ConnectionStatusFilter INNER JOIN TvStatus ON ConnectionStatusFilter.InstalationId=TvStatus.InstallationId WHERE TvStatus.Type='"
								+ Type
								+ "' ORDER BY ConnectionStatusFilter.SubNetworkCode",
						null);
		int Scnt = 0;
		c1.moveToFirst();
		do {
			Log.e("Table count", "" + c1.getCount());
			String subType = c1.getString(c1.getColumnIndex("SubNetworkCode"));
			// String Type = c1.getString(c1.getColumnIndex("N"));

			// Select distinct c2.StatioName,c1.TVStatus from TvStatus c1 inner
			// join AllStation c2 on c1.InstallationId=c2.InstallationId where
			// c2.NetworkCode='"
			// + Type + "'
			Cursor c = sql
					.rawQuery(
							"Select distinct c1.InstallationId,c1.TVStatus,c1.flg from TvStatus c1  inner join AllStation c2  on c1.InstallationId=c2.InstallationId INNER JOIN ConnectionStatusFilter c3 ON c2.InstallationId=c3.InstalationId WHERE c3.SubNetworkCode='"
									+ subType + "'", null);
			// Select distinct c1.TVStatus from TvStatus c1 inner join
			// ConnectionStatusFilter c2 on c1.InstallationId=c2.InstalationId
			// where c2.SubNetworkCode='"
			// + subType + "'

			if (c.getCount() == 0) {
				c.close();
			} else {
				TvStatusStateBean sitem = new TvStatusStateBean();

				c.moveToFirst();

				int column = 0;
				do {

					int i = 0;
					int s = c.getInt(c.getColumnIndex("flg"));
					String s1 = c.getString(c.getColumnIndex("TVStatus"));

					for (char d : s1.toCharArray()) {
						if (d == '0') {
							cnt++;
						}
					}

					if (!subType.trim().equalsIgnoreCase("") && s != 0) { // if
																			// (!subType.trim().equalsIgnoreCase("")
																			// &&
																			// cnt
																			// !=
																			// 8)
																			// {

						column++;
						sitem.Setcount(cnt);
						int a = sitem.Getcount();
						if (a != 0) {
							Scnt++;
						}
						sitem.SetNetworkCode(subType);
						sitem.SetScount(Scnt);
					}

					cnt = 0;

				} while (c.moveToNext());
				if (column != 0) {
					sitem.SettotalStation(column);
					searchResults.add(sitem);
				}
				/*
				 * sitem.SettotalStation(column); searchResults.add(sitem);
				 */
				Log.e("TV count", "" + column);
				Scnt = 0;

			}
		} while (c1.moveToNext());
		c1.close();

		sql.close();
		//db.close();
		for (int i = 0; i < searchResults.size(); i++) {
			scount = scount + searchResults.get(i).GetScount();
			totcount = totcount + searchResults.get(i).GettotalStation();
		}
		mAllCount.setText("" + scount + "/" + totcount);
		mListView.setAdapter(new TvStatusAdpt(TvStatusStateFilter.this, searchResults));

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
				sql.close();
				//db1.close();
				return true;

			} else {

				cursor.close();
				sql.close();
				//db1.close();
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
			Cursor c = sql.rawQuery("SELECT * FROM TvStatus", null);
			if (c != null && c.getCount() > 0) {
				if (c.getColumnIndex("TVStatus") < 0) {
					c.close();
					sql.close();
					//db1.close();
					return false;
				} else {
					c.close();
					sql.close();
					//db1.close();
					return true;
				}
			} else {
				c.close();
				sql.close();
				//db1.close();
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
			mRefresh.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);

			Log.e("async", "null");
			asyncfetch_csnstate = new DownloadxmlsDataURL_new();
			asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			if (asyncfetch_csnstate.getStatus() == AsyncTask.Status.RUNNING) {
				Log.e("async", "running");
				mRefresh.setVisibility(View.GONE);
				mProgressBar.setVisibility(View.VISIBLE);
			}
		}

	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			try {
				//DatabaseHandler db = new DatabaseHandler(getBaseContext());
				SQLiteDatabase sql = db.getWritableDatabase();

				String url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/GetTVStatus_Android_new?Mobile="
						+ mobno;

				Log.e("csn status", "url : " + url);
				url = url.replaceAll(" ", "%20");
				String columnName, columnValue;
				try {
					responsemsg = ut.httpGet(url);
					Log.e("csn status", "resmsg : " + responsemsg);

					if (responsemsg.contains("<A>")) {

					//	sql.execSQL("DROP TABLE IF EXISTS TvStatus");
						//sql.execSQL(ut.getTvStatus());
						sql.delete("TvStatus",null,null);

						Cursor cur = sql.rawQuery("SELECT *   FROM TvStatus",
								null);
						ContentValues values1 = new ContentValues();
						NodeList nl1 = ut.getnode(responsemsg, "Table1");
						// String msg = "";
						// String columnName, columnValue;
						Log.e("sts main...", " fetch data : " + nl1.getLength());
						for (int i = 0; i < nl1.getLength(); i++) {
							Element e = (Element) nl1.item(i);
							for (int j = 0; j < cur.getColumnCount(); j++) {
								columnName = cur.getColumnName(j);

								String ncolumnname = "";
								if (columnName
										.equalsIgnoreCase("InstallationId"))
									ncolumnname = "A";
								else if (columnName
										.equalsIgnoreCase("TVStatusReason"))
									ncolumnname = "G";
								else if (columnName
										.equalsIgnoreCase("TVStatus"))
									ncolumnname = "J";
								else if (columnName.equalsIgnoreCase("Type"))
									ncolumnname = "P";
								else
									ncolumnname = columnName;
								columnValue = ut.getValue(e, ncolumnname);
								values1.put(columnName, columnValue);

								Log.e("DownloadxmlsDataURL_new...on back....",
										" count i: " + i + "  j:" + j);
							}
							sql.insert("TvStatus", null, values1);
						}

						cur.close();
						sql.close();
						//db.close();

					} else {
						System.out
								.println("--------- invalid for project list --- ");
					}

				}

				catch (NullPointerException e) {
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

				} catch (IOException e) {
					e.printStackTrace();
					dff = new SimpleDateFormat("HH:mm:ss");
					Ldate = dff.format(new Date());

					StackTraceElement l = new Exception().getStackTrace()[0];
					System.out.println(l.getClassName() + "/"
							+ l.getMethodName() + ":" + l.getLineNumber());
					if (!ut.checkErrLogFile()) {

						ut.ErrLogFile();
					}
					if (ut.checkErrLogFile()) {
						ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
								+ ":" + l.getLineNumber() + "	"
								+ e.getMessage() + " " + Ldate);
					}

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
					ut.showD(TvStatusStateFilter.this, "nonet");
				}
				mRefresh.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.GONE);

			} catch (NullPointerException e) {
				e.printStackTrace();
				dff = new SimpleDateFormat("HH:mm:ss");
				Ldate = dff.format(new Date());

				StackTraceElement l = new Exception().getStackTrace()[0];
				System.out.println(l.getClassName() + "/" + l.getMethodName()
						+ ":" + l.getLineNumber());

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
			mRefresh.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
		}

	}

	public class DownloadSubStatetvfilter extends
			AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mRefresh.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {

		//	DatabaseHandler db = new DatabaseHandler(getBaseContext());
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

					Log.e("ConnectionFilter data.",
							" fetch data : " + NL.getLength());
					for (int i = 0; i < NL.getLength(); i++) {
						Element e = (Element) NL.item(i);
						for (int j = 0; j < cur1.getColumnCount(); j++) {
							columnName = cur1.getColumnName(j);

							columnValue = ut.getValue(e, columnName);
							Log.e("ConnectionFilter da.", " fetch data : "
									+ columnValue);
							values2.put(columnName, columnValue);
							// SubnetString = "Valid";

						}
						long ad = sql.insert("ConnectionStatusFilter", null,
								values2);
					}

					cur1.close();
					sql.close();
					//db.close();

				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();

				sql.close();
				//db.close();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (dbvalue()) {
				mRefresh.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.GONE);
			} else if (ut.isnet(getApplicationContext())) {
				fetchdata();
			} else {
				ut.showD(TvStatusStateFilter.this, "nonet");
				mRefresh.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.GONE);
			}

		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		Intent i = new Intent(getBaseContext(), TvStatusStateWise.class);
		// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}
}
