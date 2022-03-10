package com.stavigilmonitoring;

import java.io.IOException;
import java.text.DateFormat;
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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

public class SoundLevelStateWise extends Activity {

	List<StateList> searchResults;
	ImageView iv,btnadd;
	String sop, responsemsg, mobno;
	static DownloadxmlsDataURL_new asyncfetch_csnstate;
	GridView lstcsn;
	String InstallationId = "";
	String enddate, startdate, Syncdate, urlnet,conn;
	DateFormat dateFormat;
	Date date;
	static SimpleDateFormat dff;
	static String Ldate;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	SoundAdapter adapter;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.soundlevelstatewise);

		searchResults = new ArrayList<StateList>();
		lstcsn = findViewById(R.id.lstcsn);
		iv = (ImageView) findViewById(R.id.btnrefreshsoundlevel);
		date = new Date();
		dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		btnadd = (ImageView) findViewById(R.id.button_alert_add);
		btnadd.setImageResource(R.drawable.work_assign);
		btnadd.setVisibility(View.VISIBLE);
		Syncdate = dateFormat.format(date);

		db = new DatabaseHandler(getBaseContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();

		if (asyncfetch_csnstate != null
				&& asyncfetch_csnstate.getStatus() == AsyncTask.Status.RUNNING) {
			Log.e("async", "running");
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}
		Log.e("Sound level_new", " dbval : " + dbvalue());

		/*
		 * DatabaseHandler db = new DatabaseHandler(this); SQLiteDatabase sql =
		 * db.getWritableDatabase(); Cursor c =
		 * sql.rawQuery("DELETE FROM ConnectionStatusFilter", null); int ct =
		 * c.getCount(); c.close(); db.close(); sql.close();
		 */

	
			if (dbvalue()) {
				updatelist();
			} else if (ut.isnet(getApplicationContext())) {
				fetchdata();
			} else {
				try{
					ut.showD(SoundLevelStateWise.this, "nonet");
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		
		lstcsn.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//adapter.notifyDataSetChanged();
				Boolean value = subnetworkcheck(searchResults.get(position)
						.getNetworkcode());
				if (value) {
					Intent i = new Intent(SoundLevelStateWise.this, SoundLevelStateFilter.class);
					i.putExtra("Type", searchResults.get(position).getNetworkcode());
					i.putExtra("Count", searchResults.get(position).Getcount());
					startActivity(i);
					finish();

				} else {
					Intent i = new Intent(SoundLevelStateWise.this,
							SoundLevelMain.class);
					i.putExtra("Type", searchResults.get(position)
							.getNetworkcode());
					i.putExtra("SubType", searchResults.get(position)
							.getNetworkcode());
					startActivity(i);

				}
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

		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				 if (ut.isnet(getApplicationContext())) {
					fetchdata();
				} else {
				 	try{
						ut.showD(SoundLevelStateWise.this, "nonet");
					}catch (Exception e){
				 		e.printStackTrace();
					}
				}
				
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
							"SELECT DISTINCT SubNetworkCode FROM SoundLevel_new WHERE NetworkCode='"
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

		asyncfetch_csnstate = new DownloadxmlsDataURL_new();
		asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			String xx = "";
//http://192.168.1.108:70
			String url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetStationCallibrationNew?Mobile="
					+ mobno + "&NetworkCode="+xx+"&InstallationId="+xx;
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
			//	sql.execSQL("DROP TABLE IF EXISTS SoundLevel_new");
				System.out.println("------------- 3-- ");
			//	sql.execSQL(ut.getSoundLevel_new());
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
					//Log.e("get SoundLevel_new node...", " fetch data : " + nl);
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
					try{
						ut.showD(SoundLevelStateWise.this, "invalid");
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

	public void onBackPressed() {
		// super.onBackPressed();
		// Intent i = new Intent(getBaseContext(), SelectMenu.class);
		// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// getBaseContext().startActivity(i);
		// finish();

		super.onBackPressed();
		int scount = 0;
		for (int i = 0; i < searchResults.size(); i++)
			scount = scount + searchResults.get(i).Getcount();

		try {
			SharedPreferences pref = getApplicationContext()
					.getSharedPreferences("PrefSound", Context.MODE_PRIVATE); // 0
																				// -
																				// for
																				// private
			Editor editor = pref.edit();
			editor.putString("TVSound", scount + "");
			editor.commit();

			// SharedPreferences prefsound = getApplicationContext()
			// .getSharedPreferences("PrefSound", Context.MODE_PRIVATE);
			// Editor editorsound = prefsound.edit();
			// String TVsound = prefsound.getString("TVSound", "");
			// // editorsound.putString("TVSound",
			// String.valueOf(totalstation));
			// editorsound.commit();

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
		startActivity(i);*/
		finish();

	}

	private void updatelist() {
		searchResults.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		int count = 0;
		Cursor c = sql.rawQuery("SELECT DISTINCT NetworkCode FROM SoundLevel_new ORDER BY NetworkCode",
				null);
		if (c.getCount() > 0) {
			c.moveToFirst();
			do {

				String Type = c.getString(0);

				Cursor c1 = sql.rawQuery(
						"Select distinct InstallationDesc from SoundLevel_new  Where NetworkCode='"
								+ Type + "' ORDER BY NetworkCode Desc", null);
				count = c1.getCount();
				/*
				 * if(c1.getCount() > 0) { c1.moveToFirst(); do { count=count+1;
				 * int column1 = c1.getColumnIndex("NetworkCode"); //String[]
				 * tym = splitfromtym(c1.getString(column1));
				 * 
				 * }while(c1.moveToNext()); }
				 */

				//Type = Type.replaceAll("0", "");
				//Type = Type.replaceAll("1", "");
				if (!Type.trim().equalsIgnoreCase("")) {
					StateList sitem = new StateList();
					sitem.SetNetworkCode(Type);
					sitem.Setcount(count);
					searchResults.add(sitem);

				}
			} while (c.moveToNext());

			c.close();

		}
		adapter = new SoundAdapter(SoundLevelStateWise.this, searchResults);
		adapter.notifyDataSetChanged();
		lstcsn.setAdapter(adapter);

	}

	/*
	 * class StateList { String Networkcode; String InstallationId; String
	 * StatioName; int count;
	 * 
	 * public StateList() { }
	 * 
	 * public void setInstallationId(String Networkcode) { this.InstallationId =
	 * InstallationId; }
	 * 
	 * public String getInstallationId() { return InstallationId; }
	 * 
	 * public void SetNetworkCode(String Networkcode) { this.Networkcode =
	 * Networkcode; }
	 * 
	 * public String getNetworkcode() { return Networkcode; }
	 * 
	 * public void Setcount(int count) { this.count = count; }
	 * 
	 * public int Getcount() { return count; }
	 * 
	 * }
	 */

}
