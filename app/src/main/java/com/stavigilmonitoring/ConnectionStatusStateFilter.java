package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.CSNFilteradapter;
import com.beanclasses.StateListFiter;
import com.database.DBInterface;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectionStatusStateFilter extends Activity {
	ImageView mRefresh,btnadd;
	GridView mListView;
	AsyncTask mm;
	private LinearLayout mAllnet;
	private TextView mText;
	private TextView mAllCount;
	private com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	static SimpleDateFormat dff;
	static String Ldate;
	String responsemsg = "k";
	String sop;
	String type, typecount, mobno;
	ArrayList<StateListFiter> searchresult;
	DatabaseHandler db;
    private int scount= 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.connectionsatusstatefilter);

		mRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_connect_filter);
		btnadd = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_alert_add);
		btnadd.setImageResource(com.stavigilmonitoring.R.drawable.work_assign);
		btnadd.setVisibility(View.VISIBLE);

		mListView =  findViewById(com.stavigilmonitoring.R.id.listFiterSate);
		mAllnet = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.networksumconn);
		mText = (TextView) findViewById(com.stavigilmonitoring.R.id.tvNameconn);
		mAllCount = (TextView) findViewById(com.stavigilmonitoring.R.id.tvCntcon);

		db = new DatabaseHandler(this);

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		searchresult = new ArrayList<StateListFiter>();

		Intent intent = getIntent();
		type = intent.getStringExtra("Type");

		mText.setText(type + "-All");
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		mRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ut.isnet(getApplicationContext())) {

					mm = new mDownloadsubFilter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					try{
						ut.showD(ConnectionStatusStateFilter.this, "nonet");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
			}
		});
		btnadd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),WorkAssign_AssignActivity.class);
				intent.putExtra("Activity", "ConnectionStatusStatewise");
				intent.putExtra("Type", "");
				startActivity(intent);
			//	finish();
			}
		});

		mAllnet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), ConnectionStatusMainAll.class);
				intent.putExtra("Type", type);
				intent.putExtra("CallFrom","ConnectionStatusStateFilter");
				startActivity(intent);
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (stationpresent()) {
					Intent intent = new Intent(getApplicationContext(), ConnectionStatusMain.class);
					intent.putExtra("Type", type);
					intent.putExtra("subType", searchresult.get(position).GetStateName());
					startActivity(intent);
				} else {

					Toast.makeText(getBaseContext(), "No Station Present..",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		if (dbvalue()) {
			updatelist();
		} else if (ut.isnet(ConnectionStatusStateFilter.this)) {
			fetchdata();
		} else {
			try{
				ut.showD(ConnectionStatusStateFilter.this, "nonet");
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	protected boolean stationpresent() {
		// TODO Auto-generated method stub

		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql.rawQuery("SELECT * FROM ConnectionStatusUser", null);

		int count = c.getCount();

		c.close();
		/*sql.close();
		db.close();*/

		if (count == 0) {
			return false;
		} else {

			return true;
		}

	}

	private void updatelist() {
		searchresult.clear();

		SQLiteDatabase sql = db.getWritableDatabase();
		type = type.trim();
		Cursor c = sql.rawQuery(
				"SELECT DISTINCT SubNetworkCode FROM ConnectionStatusUser where Type='"
						+ type + "' ORDER BY SubNetworkCode", null);

		if (c.getCount() > 0) {
			c.moveToFirst();
			do {
				int stncnt = 0;
				String Type = c.getString(0);

				Cursor c1 = sql
						.rawQuery(
								"SELECT DISTINCT InstallationId,ServerTime ,STAVersion ,Type  FROM ConnectionStatusUser WHERE SubNetworkCode='"
										+ c.getString(0)
										+ "' ORDER BY Type Desc", null);
				if (c1.getCount() > 0) {
					c1.moveToFirst();
					do {
						int column1 = c1.getColumnIndex("ServerTime");
						try {
							Calendar cal = Calendar.getInstance();
							SimpleDateFormat format = new SimpleDateFormat(
									"MM/dd/yyyy hh:mm:ss aa", Locale.ENGLISH);
							String srt = c1.getString(column1);
							Date Startdate = format.parse(srt);
							Date Enddate = cal.getTime();
							long diff = Enddate.getTime() - Startdate.getTime();
							long diffSeconds = diff / 1000 % 60;
							long diffMinutes = diff / (60 * 1000) % 60;
							long diffHours = diff / (60 * 60 * 1000) % 24;
							long diffDays = diff / (24 * 60 * 60 * 1000);

							if (diffDays == 0 && diffHours == 0
									&& diffMinutes <= 15) {

							} else {
								stncnt = stncnt + 1;
							}
						} catch (Exception ex) {
							dff = new SimpleDateFormat("HH:mm:ss");
							Ldate = dff.format(new Date());

							StackTraceElement l = new Exception()
									.getStackTrace()[0];
							System.out.println(l.getClassName() + "/"
									+ l.getMethodName() + ":"
									+ l.getLineNumber());
							ut = new com.stavigilmonitoring.utility();
							if (!ut.checkErrLogFile()) {

								ut.ErrLogFile();
							}
							if (ut.checkErrLogFile()) {
								ut.addErrLog(l.getClassName() + "/"
										+ l.getMethodName() + ":"
										+ l.getLineNumber() + "	"
										+ ex.getMessage() + " " + Ldate);
							}
						}

					} while (c1.moveToNext());
				}
				if (!Type.trim().equalsIgnoreCase("")) {
					StateListFiter sitem = new StateListFiter();
					sitem.SetStateName(Type);
					sitem.SetCount(stncnt);
					searchresult.add(sitem);
				}
			} while (c.moveToNext());

		}

		c.close();

		//sql.close();
		scount = 0;
		for (int i = 0; i < searchresult.size(); i++)
			scount = scount + searchresult.get(i).GetSCount();
			mAllCount.setText(""+scount);
            mListView.setAdapter(new CSNFilteradapter(
					ConnectionStatusStateFilter.this, searchresult));

	}

	private boolean dbvalue() {
		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery(
					"SELECT *   FROM ConnectionStatusUser", null);
			if (cursor != null && cursor.getCount() > 0) {
				if (cursor.getColumnIndex("Type") < 0) {
					cursor.close();
					/*sql.close();
					db1.close();*/
					return false;
				} else {
					cursor.close();
					/*sql.close();
					db1.close();*/
					return true;
				}
			} else {
				cursor.close();
				/*sql.close();
				db1.close();*/

			}
		} catch (Exception e) {
			e.printStackTrace();
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

		}
		return false;
	}

	private void fetchdata() {
		// new DownloadxmlsDataURL_new().execute();
		if (mm == null) {
			mRefresh.setVisibility(View.VISIBLE);
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
					.setVisibility(View.GONE);

			Log.e("async", "null");
			mm = new mDownloadsubFilter();
			mm.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			if (mm.getStatus() == AsyncTask.Status.RUNNING) {
				Log.e("async", "running");
				mRefresh.setVisibility(View.GONE);
				((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
						.setVisibility(View.VISIBLE);
			}
		}

	}

	/*class StateListFiter {
		String StateName;
		int scount;

		public StateListFiter() {
		}

		public String GetStateName() {
			return StateName;
		}

		public void SetStateName(String s) {
			this.StateName = s;
		}

		public int GetSCount() {
			return scount;
		}

		public void SetCount(int s) {
			this.scount = s;
		}

	}*/

	public class mDownloadsubFilter extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mRefresh.setVisibility(View.GONE);
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.ConnectFilterState))
					.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			// utility ut = new utility();
			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();

			String url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetCSNStatus_Android_new?Mobile="
					+ mobno;

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			// SQLiteDatabase sql = db.getWritableDatabase();
			try {
				responsemsg = ut.httpGet(url);
				Log.e("csn status", "resmsg : " + responsemsg);

				if (responsemsg.contains("<A>")) {
					sop = "valid";
					String columnName, columnValue;

					//sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusUser");
					//sql.execSQL(ut.getConnectionStatusUser());
					sql.delete("ConnectionStatusUser",null,null);

					Cursor cur = sql.rawQuery(
							"SELECT *   FROM ConnectionStatusUser", null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table1");

					Log.e("sts main...", " fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						String conn = "invalid";
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							columnName = cur.getColumnName(j);

							String ncolumnname = "";
							if (columnName.equalsIgnoreCase("InstallationId"))
								ncolumnname = "A";
							else if (columnName.equalsIgnoreCase("ServerTime"))
								ncolumnname = "B";
							else if (columnName.equalsIgnoreCase("StartTime"))
								ncolumnname = "C";
							else if (columnName.equalsIgnoreCase("EndTime"))
								ncolumnname = "D";
							else if (columnName.equalsIgnoreCase("Remarks"))
								ncolumnname = "E";
							else if (columnName
									.equalsIgnoreCase("InstallationDesc"))
								ncolumnname = "F";
							else if (columnName.equalsIgnoreCase("TVStatus"))
								ncolumnname = "G";
							else if (columnName.equalsIgnoreCase("STAVersion"))
								ncolumnname = "J";
							else if (columnName
									.equalsIgnoreCase("AscOrderServerTime"))
								ncolumnname = "K";
							else if (columnName
									.equalsIgnoreCase("LatestDowntimeReason"))
								ncolumnname = "L";

							else if (columnName.equalsIgnoreCase("Type"))
								ncolumnname = "N";
							else if (columnName
									.equalsIgnoreCase("SubNetworkCode"))
								ncolumnname = "R";

							columnValue = ut.getValue(e, ncolumnname);

							if (columnName.equalsIgnoreCase("ServerTime")) {
								try {
									Calendar cal = Calendar.getInstance();

									SimpleDateFormat format = new SimpleDateFormat(
											"MM/dd/yyyy hh:mm:ss aa", Locale.ENGLISH);

									Date Startdate = format.parse(columnValue);
									Date Enddate = cal.getTime();
									long diff = Enddate.getTime()
											- Startdate.getTime();
									long diffSeconds = diff / 1000 % 60;
									long diffMinutes = diff / (60 * 1000) % 60;
									long diffHours = diff / (60 * 60 * 1000)
											% 24;
									long diffDays = diff
											/ (24 * 60 * 60 * 1000);

									Log.e("getdetails", "sd : " + Startdate
											+ " ed: " + Enddate + " d: "
											+ diffDays + " h: " + diffHours
											+ " m:" + diffMinutes);


									if (diffDays == 0 && diffHours == 0
											&& diffMinutes <= 15) {

									} else {
										conn = "valid";
									}
								} catch (Exception ex) {
									dff = new SimpleDateFormat("HH:mm:ss");
									Ldate = dff.format(new Date());

									StackTraceElement l = new Exception()
											.getStackTrace()[0];
									System.out.println(l.getClassName() + "/"
											+ l.getMethodName() + ":"
											+ l.getLineNumber());
									ut = new com.stavigilmonitoring.utility();
									if (!ut.checkErrLogFile()) {

										ut.ErrLogFile();
									}
									if (ut.checkErrLogFile()) {
										ut.addErrLog(l.getClassName() + "/"
												+ l.getMethodName() + ":"
												+ l.getLineNumber() + "	"
												+ ex.getMessage() + " " + Ldate);
									}

								}
							}

							values1.put(columnName, columnValue);

						}
						if (conn == "valid")
							sql.insert("ConnectionStatusUser", null, values1);
					}

					cur.close();
					/*sql.close();
					db.close();*/

				} else {
					sop = "invalid";
					System.out
							.println("--------- invalid for project list --- ");
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
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				System.out
						.println("..DownloadxmlsDataURL_new onpost.............value of sop"
								+ sop);
				if (sop.equals("valid")) {
					updatelist();
				} else {
					try{
						ut.showD(ConnectionStatusStateFilter.this, "invalid");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				mRefresh.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(com.stavigilmonitoring.R.id.ConnectFilterState))
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

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		/*
		 * Intent i = new Intent(ConnectionStatusStateFilter.this,
		 * ConnectionStatusStatewise.class);
		 * i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); startActivity(i);
		 * finish();
		 */
	}

}
