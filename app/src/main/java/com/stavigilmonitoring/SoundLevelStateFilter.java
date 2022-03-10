package com.stavigilmonitoring;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.SoundAdapter;
import com.beanclasses.StateList;
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
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SoundLevelStateFilter extends Activity {

	private List<StateList> searchResults;
	private ImageView mRefresh, btnadd;
	private GridView mListView;
	private ProgressBar mProgressBar;
	private LinearLayout mAllnet;
	private TextView mText;
	private TextView mAllCount;
	private com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	static SimpleDateFormat dff;
	static String Ldate;
	private String mobno, type,conn;
	private static Downloadxmlsound asyncfetch_csnstate;
	String responsemsg, Syncdate, sop, urlnet;
	int scount = 0;
	SoundAdapter adapter;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.soundlevelstatefilter);

		mRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_sound_filter);
		mListView =  findViewById(com.stavigilmonitoring.R.id.listsoundFiterSate);
		mProgressBar = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.soundlevelFilterState);
		mAllnet = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.networksumsound);
		mText = (TextView) findViewById(com.stavigilmonitoring.R.id.tvName);
		mAllCount = (TextView) findViewById(com.stavigilmonitoring.R.id.tvCntc);
		btnadd = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_alert_add);
		btnadd.setImageResource(com.stavigilmonitoring.R.drawable.work_assign);
		btnadd.setVisibility(View.VISIBLE);

		db = new DatabaseHandler(getBaseContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		searchResults = new ArrayList<StateList>();
		mobno = dbi.GetPhno();
		dbi.Close();
		Intent intent = getIntent();
		type = intent.getStringExtra("Type");
		mText.setText(type+"-All");

		if (dbvalue()) {
			updatelist();
		} else if (ut.isnet(getApplicationContext())) {
			fetchdata();
		} else {
			ut.showD(SoundLevelStateFilter.this, "nonet");
		}
	

		/*if (dbSubnet()) {
			if (dbvalue()) {
				updatelist();
			} else if (ut.isnet(getApplicationContext())) {
				fetchdata();
			} else {
				ut.showD(getApplicationContext(), "nonet");
			}
		} else if (ut.isnet(SoundLevelStateFilter.this)) {
			new DownloadSubnet().execute();
		} else {
			ut.showD(getApplicationContext(), "nonet");
		}*/
		mAllnet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), SoundLevelMainAll.class);
				intent.putExtra("Type", type);
				startActivity(intent);

			}
		});
		btnadd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),WorkAssign_AssignActivity.class);
				intent.putExtra("Activity", "SoundLevelStateWise");
				intent.putExtra("Type", "");
				startActivity(intent);
				finish();
			}
		});
		mRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (ut.isnet(getApplicationContext())) {
					fetchdata();
				} else {
					ut.showD(SoundLevelStateFilter.this, "nonet");
				}
			
				/*if (dbSubnet()) {} else if (ut.isnet(SoundLevelStateFilter.this)) {
					DatabaseHandler db = new DatabaseHandler(getBaseContext());
					SQLiteDatabase sql = db.getWritableDatabase();
					Cursor c = sql.rawQuery("DELETE FROM SoundLevel", null);
					int ct = c.getCount();
					c.close();
					db.close();
					sql.close();
					new DownloadSubnet().execute();
				} else {
					ut.showD(getApplicationContext(), "nonet");
				}*/

			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(getApplicationContext(), SoundLevelMain.class);
				intent.putExtra("Type", type);
				intent.putExtra("SubType", searchResults.get(position).getNetworkcode());
			//	adapter.notifyDataSetChanged();
				startActivity(intent);

			}
		});

	}

	/*private Boolean dbSubnet() {

		try {
			DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db1.getWritableDatabase();
			Cursor cursor = sql.rawQuery(
					"SELECT * FROM ConnectionStatusFilter", null);// SoundLevel_new

			System.out.println("----------  dbSubnet screen cursor count -- "
					+ cursor.getCount());

			if (cursor != null && cursor.getCount() > 0) {

				cursor.close();
				sql.close();
				db1.close();
				return true;

			} else {

				cursor.close();
				sql.close();
				db1.close();
				return false;
			}
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

			return false;
		}
	}*/

	private void fetchdata() {
		// new DownloadxmlsDataURL_new().execute();

		asyncfetch_csnstate = new Downloadxmlsound();
		asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	public class Downloadxmlsound extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			String xx = "";
//http://192.168.1.108:70
			String url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetStationCallibrationNew?Mobile="
					+ mobno + "&NetworkCode=&InstallationId=";
			url = url.replaceAll(" ", "%20");

			System.out.println("============ internet reg url " + url);

			try {
				System.out.println("-------  activity url --- " + url);
				responsemsg = ut.httpGet(url);

				System.out.println("-------------  xx vale of non repeated-- "
						+ responsemsg);
				
				//DatabaseHandler db = new DatabaseHandler(getBaseContext());
				System.out.println("------------- 1-- ");
				SQLiteDatabase sql = db.getWritableDatabase();
				System.out.println("------------- 2-- ");
				//sql.execSQL("DROP TABLE IF EXISTS SoundLevel_new");
				System.out.println("------------- 3-- ");
				//sql.execSQL(ut.getSoundLevel_new());
				sql.delete("SoundLevel_new",null,null);

				System.out.println("------------- 4-- ");
				System.out.println("------------- 5-- ");

				// InstalationId
				if (responsemsg.contains("<InstalationId>")) {
					sop = "valid";
					

					Cursor c = sql.rawQuery("SELECT * FROM SoundLevel_new",
							null);
					System.out.println("------------- 6-- ");
					ContentValues values = new ContentValues();
					System.out.println("------------- 7-- ");
					NodeList nl = ut.getnode(responsemsg, "Table");
					Log.e("get SoundLevel_new node...", " fetch data : " + nl);
					String msg = "";
					String columnName, columnValue;
					for (int i = 0; i < nl.getLength(); i++) {
						Element e = (Element) nl.item(i);
						for (int j = 0; j < c.getColumnCount(); j++) {

							columnName = c.getColumnName(j);
							columnValue = ut.getValue(e, columnName);

							System.out.println("-------------column name"
									+ columnName);
							System.out.println("-------------column value"
									+ columnValue);
							// CallibrationDate=Oct 7 2015 7:05AM
							if (columnName.equalsIgnoreCase("CallibrationDate")) {
								try {
									Calendar cal = Calendar.getInstance();
									// "MM/dd/yyyy hh:mm:ss aa"
									SimpleDateFormat format = new SimpleDateFormat(
											"MMM dd yyyy hh:mm");

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
											&& diffMinutes <= 30) {

									} else {
										conn = "valid";
									}
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}

							values.put(columnName, columnValue);
							Log.d("test", "values :" + values);
						}

						long A = sql.insert("SoundLevel_new", null, values);
						Log.d("test", "SoundLevel_new " + values.size());
					}

					c.close();

				} else {
					sop = "invalid";
					System.out.println("--------- invalid for AD list --- ");
				}

			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();

				responsemsg = "wrong" + e.toString();
				System.out
						.println("--------- invalid for message type list --- "
								+ responsemsg);

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
					ut.showD(SoundLevelStateFilter.this, "invalid");
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
			mRefresh.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
		}

	}

	/*public class DownloadSubnet extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mRefresh.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {

			DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			String columnName, columnValue;
			urlnet = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMaster";
			urlnet = urlnet.replaceAll(" ", "%20");
			Log.e("installation for Subnet", "6th" + urlnet);

			try {
				responsemsg = ut.httpGet(urlnet);
				NodeList NL = ut.getnode(responsemsg, "Table");
				Log.e("SubnetCount", "len :" + NL.getLength());

				if (responsemsg.contains("<Table>")) {
					sop = "valid";
					sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusFilter");
					sql.execSQL(ut.getConnectionStatusFilter());
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
					sql.close();
					db.close();

				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();

				sql.close();
				db.close();
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
				ut.showD(getApplicationContext(), "nonet");
				mRefresh.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.GONE);
			}

		}
	}*/

	private String[] splitfrom(String tf) {
		// TODO Auto-generated method stub
		// 12/31/2015 7:05:00 AM---
		String time1, time2 = null;
		String time[];
		String k = "";
		// String str = "18/01/2013 5:00:00 pm";
		SimpleDateFormat input = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		Date dt;
		try {
			dt = input.parse(tf);

			SimpleDateFormat output = new SimpleDateFormat("MM/dd/yyyy HH:mm");// 2016/02/18
			String formattedDate = output.format(dt); // contains 18/01/2013
														// 17:00:00
														// String k = 01/14/2016
														// 07:05
														// formattedDate.substring(formattedDate.indexOf(" "));
			// fromtimetw = k.substring(0, 5); // = "ab"

			time = formattedDate.split(" ");
			time1 = time[0];
			time2 = time[1];

		} catch (ParseException e) {
			// TODO Auto-generated catch block
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

		String[] v1 = { time2 };

		return v1;
	}

	private String[] splittime(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf for date...." + tf);
		String fromtimetw = "";

		String k = tf.substring(0, tf.indexOf(" "));
		System.out.println("---value of k for date..." + k);

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(k);
			System.out
					.println("..........value of my date after conv" + myDate);

		} catch (ParseException e) {
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
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM");
		String finalDate = timeFormat.format(myDate);

		String[] v2 = { finalDate };

		return v2;
	}

	private void updatelist() {
		searchResults.clear();
		//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor cursor = sql
				.rawQuery(
						"SELECT DISTINCT SubNetworkCode FROM SoundLevel_new where NetworkCode='"
								+ type + "' ORDER BY SubNetworkCode", null);
		cursor.getCount();
	//	SELECT DISTINCT a.SubNetworkCode FROM ConnectionStatusFilter a INNER JOIN SoundLevel b ON a.InstalationId=b.InstallationId where b.NetworkCode='"
		//		+ type + "' ORDER BY a.SubNetworkCode

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {

				int stncnt = 0;
				String subType = cursor.getString(0);
				String que = "Select distinct InstallationDesc from SoundLevel_new  Where SubNetworkCode='"+ subType+"'";
				Cursor c1 = sql.rawQuery(que, null);

				stncnt = c1.getCount();

				if (c1.getCount() > 0) {
					c1.moveToFirst();
					do {
						ArrayList<String> asd = new ArrayList<String>();
						String subType1 = c1.getString(0);
						asd.add(subType1);
					} while (c1.moveToNext());
					c1.close();
				}

				//subType = subType.replaceAll("0", "");
				//subType = subType.replaceAll("1", "");
				if (!subType.trim().equalsIgnoreCase("")) {

					StateList sitem = new StateList();
					sitem.SetNetworkCode(subType);
					sitem.Setcount(stncnt);
					searchResults.add(sitem);

				}

			} while (cursor.moveToNext());
			cursor.close();

		}

		// searchResults.add();
		scount = 0;
		for (int i = 0; i < searchResults.size(); i++)
		scount = scount + searchResults.get(i).Getcount();
		mAllCount.setText(""+scount);
		adapter = new SoundAdapter(getApplicationContext(), searchResults);
		adapter.notifyDataSetChanged();
		mListView.setAdapter(adapter);
		

	}

	private boolean dbvalue() {

		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery("SELECT * FROM SoundLevel_new", null);// SoundLevel_new

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
			ut = new utility();
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(getApplicationContext(),
				SoundLevelStateWise.class);
		startActivity(intent);
	}
}
