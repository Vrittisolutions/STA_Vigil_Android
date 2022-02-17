package com.stavigilmonitoring;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.SoundlevelAdapter;
import com.beanclasses.ConnectionStatusBean;
import com.beanclasses.DowntimeHelper;
import com.beanclasses.SoundlevelBean;
import com.database.DBInterface;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
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

public class SoundLevelMainAll extends Activity {
	String Type;
	String mobno, link;
	AsyncTask depattask;

	String sop = "no";

	ArrayList<SoundlevelBean> soundlevelBeanslist;
	SoundlevelBean soundlevelBean;
	List<ConnectionStatusBean> connectionStatusBeanslist;
	ConnectionStatusBean connectionStatusBean;
	private static DownloadxmlsDataURL asyncfetch_csnstate;
	// static DownloadxmlsDataConnection asyncfetch_csn;
	SoundlevelAdapter soundlevelAdapter;
	String responsemsg = "k";
	String ActivityName, ActivityId, actname;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	private TextView txtdaterefresh;
	String daterestr;
	public String filter;
	// SoundLevelAdaptMain listAdapter;
	// ArrayList<String> arrlist = new ArrayList<String>();
	TextView title;
	ImageView iv, btnadd;
	private ListView sound;
	String conn = "invalid";
	String finalDate, subType;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.soundlevelmainlist);
		sound = (ListView) findViewById(com.stavigilmonitoring.R.id.soundlevelmainlist);
		iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_sound);
		title = (TextView) findViewById(com.stavigilmonitoring.R.id.title);
		btnadd = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_alert_add);
		btnadd.setImageResource(com.stavigilmonitoring.R.drawable.work_assign);
		btnadd.setVisibility(View.VISIBLE);
		soundlevelBeanslist = new ArrayList<SoundlevelBean>();
		connectionStatusBeanslist = new ArrayList<ConnectionStatusBean>();
		Intent intent = getIntent();
		ut = new utility();
		Type = intent.getStringExtra("Type");
		subType = intent.getStringExtra("SubType");
		title.setText(Type + "-Sound Level");

		db = new DatabaseHandler(getBaseContext());
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		// ut.getSoundLevel_new();
		if (asyncfetch_csnstate != null
				&& asyncfetch_csnstate.getStatus() == AsyncTask.Status.RUNNING) {
			Log.e("async", "running");
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}

		if (dbvalue()) {
			// fetchdata();
			getDetail();
		} else if (ut.isnet(getApplicationContext())) {

			fetchdata();
		} else {
			try{
				ut.showD(SoundLevelMainAll.this, "nonet");
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		dbi.Close();

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
				// TODO Auto-generated method stub

				if (ut.isnet(getApplicationContext())) {

					fetchdata();

				} else {
					try{
						ut.showD(SoundLevelMainAll.this,"nonet");
					}catch (Exception e){
						e.printStackTrace();
					}
				}

			}
		});

		((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
				.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {

						filter = s.toString();
						soundlevelAdapter
								.filter(((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
										.getText().toString().trim()
										.toLowerCase(Locale.getDefault()));
						// getDetail_filter();
						// soundlevelAdapter
						// .filter(((EditText) findViewById(R.id.edfitertext))
						// .getText().toString().trim()
						// .toLowerCase(Locale.getDefault()));
					}
				});
		sound.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {

			//	if (dbvalue()) {
					/*if (soundlevelBeanslist.get(position)
							.getLast5calibrationvalue().equalsIgnoreCase("No Recent Data Found")) {
						Toast.makeText(getBaseContext(),
								"No Recent Data Found...", Toast.LENGTH_LONG)
								.show();

					} else {*/

						Bundle dataBundle = new Bundle();
						dataBundle.putString("stnname", soundlevelBeanslist
								.get(position).getStationname());
						dataBundle.putString("SubType", subType);
						dataBundle.putString("Installation",soundlevelBeanslist
								.get(position).getInstalationid());
						Intent myIntent = new Intent();
						myIntent.setClass(getApplicationContext(),
								SoundLevel.class);

						myIntent.putExtras(dataBundle);
						startActivity(myIntent);

					}

			/*	} else {

					Toast.makeText(getBaseContext(),
							"No Information Present..", Toast.LENGTH_LONG)
							.show();
				}

			}*/
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

	public void editActivity(String s) {

		System.out.println("==========@#@# actid " + s);
		//DatabaseHandler db1 = new DatabaseHandler(this);
		SQLiteDatabase sqldb = db.getWritableDatabase();
		String[] params = new String[1];
		params[0] = s;
		Cursor c2 = sqldb.rawQuery("SELECT * FROM SoundLevel where StationName=? ", params);
		String stnname = "";

		if (c2.getCount() == 0) {
			c2.close();

		} else {

			c2.moveToFirst();
			stnname = c2.getString(c2.getColumnIndex("StationName"));

			c2.moveToLast();

			c2.close();

		}

		// MOMA --- mom attend request
		// System.out.println("----------  type --- " + type);

		Bundle dataBundle = new Bundle();
		dataBundle.putString("stnname", stnname);
		// dataBundle.putString("ActivityName", ActivityName);

		// finish();
		Intent myIntent = new Intent();
		myIntent.setClass(getApplicationContext(), SoundLevel.class);

		myIntent.putExtras(dataBundle);
		startActivity(myIntent);
		// finish();
		// System.out.println("------------- 1");

	}

	private boolean dbvalue() {
		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			// Cursor cursor1 = sql.rawQuery(
			// "SELECT * FROM ConnectionStatusUser where Type='" + Type
			// + "'", null);
			// if (cursor1 != null && cursor1.getCount() > 0) {
			Cursor cursor = sql.rawQuery(
					"Select * from SoundLevel_new s where s.NetworkCode='"
							+ Type + "'", null);

			System.out.println("----------  dbvalue screen cursor count -- "
					+ cursor.getCount());

			if (cursor != null && cursor.getCount() > 0) {

				// do your action
				// Fetch your data

				cursor.close();

				return true;

			} else {

				cursor.close();
				return false;
			}

		} catch (Exception e) {
			return false;
		}

	}

	public void getDetail() {

		soundlevelBeanslist.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql
				.rawQuery(
						"Select  DISTINCT s.InstallationDesc , s.CallibrationDate, s.AO , s.InstalationId ,s.ServerTime from SoundLevel_new s WHERE s.NetworkCode='"
								+ Type + "' ORDER BY s.InstallationDesc ", null); // s.ServerTime,
		Log.e("SoundLevel ", "" + c.getCount());
		if (c.getCount() == 0) {

			c.close();

			// return arrlist;
		} else {

			c.moveToFirst();

			int column = 0;
			do {

				int column1 = c.getColumnIndex("CallibrationDate");
				int column2 = c.getColumnIndex("ServerTime");
				String id = c.getString(c.getColumnIndex("InstalationId"));
				String s = c.getString(c.getColumnIndex("InstallationDesc"));
				String cbtime = c.getString(c
						.getColumnIndex("CallibrationDate"));

				String tf_calibration = c.getString(column1);
				String tf_connection = c.getString(column2);
				String[] v = splitDT(tf_calibration);
				String[] v1 = splitConnectionDT(tf_connection);
				String cstime = c.getString(c.getColumnIndex("ServerTime"));
				String cbvalue = c.getString(c.getColumnIndex("AO"));// 109-101-26-13-22-

				/*
				 * if (cbvalue.length() > 0 ) { cbvalue = cbvalue.substring(0,
				 * cbvalue.length()-1); }
				 */
				soundlevelBean = new SoundlevelBean();
				soundlevelBean.setInstalationid(id);
				soundlevelBean.setLastcalibrationtime(v[0]);
				soundlevelBean.setLast5calibrationvalue(cbvalue);
				soundlevelBean.setLastconnectiontime(v1[0]);
				soundlevelBean.setStationname(s);
				soundlevelBeanslist.add(soundlevelBean);
				Log.d("test", "sound bean list" + soundlevelBeanslist);

			} while (c.moveToNext());

			c.close();
		}
		soundlevelAdapter = new SoundlevelAdapter(getApplicationContext(),
				soundlevelBeanslist);

		sound.setAdapter(soundlevelAdapter);

	}

	private String[] splitDT(String tf) {
		// TODO Auto-generated method stub
		// Oct 13 2015 1:05PM
		if (!(tf.equals("") || tf == null)) {

			tf = tf.replace("T"," ");
			//tf= tf.substring(0,tf.indexOf("+"));

			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date myDate = null;
			try {
				myDate = dateFormat.parse(tf);
				System.out.println("..........value of my date after conv"
						+ myDate);

			} catch (ParseException e) {
				e.printStackTrace();
			}
			SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM HH:mm");
			finalDate = timeFormat.format(myDate);

		} else {
			finalDate = "";
		}

		String[] v = { finalDate };

		return v;

	}

	private String[] splitConnectionDT(String tf) {
		// TODO Auto-generated method stub
		// Oct 13 2015 1:05PM
		if (!(tf.equals("") || tf == null)) {
			// 2015-11-24 09:54:09
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date myDate = null;
			try {
				myDate = dateFormat.parse(tf);
				System.out.println("..........value of my date after conv"
						+ myDate);

			} catch (ParseException e) {
				e.printStackTrace();
			}
			SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM HH:mm");
			finalDate = timeFormat.format(myDate);

		} else {
			finalDate = "";
		}

		String[] v1 = { finalDate };

		return v1;

	}

	private ArrayList<DowntimeHelper> GetDetail() {
		ArrayList<DowntimeHelper> results = new ArrayList<DowntimeHelper>();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql.rawQuery("SELECT * FROM Downtime", null);
		if (c.getCount() == 0) {
			DowntimeHelper sr = new DowntimeHelper();
			sr.setstnname("");

			// sr.setStartEnd("");
			// sr.setRemarks("");

			results.add(sr);

			c.close();

			return results;
		} else {

			c.moveToFirst();

			int column = 0;
			do {

				DowntimeHelper sr = new DowntimeHelper();

				// column = c.getColumnIndex("UserName");

				sr.setstnname(c.getString(c.getColumnIndex("InstallationDesc")));
				// sr.setcurrent(c.getString(c.getColumnIndex("AddedDate")));

				results.add(sr);

			} while (c.moveToNext());

			c.close();
		}
		return results;

	}

	private String calculatediff(String datedb) {
		System.out.println("date db......................" + datedb);
		// TODO Auto-generated method stub
		long diffInMillisec = 0;
		long diffInDays = 0;
		try {
			// Create two calendars instances

			// System.out.println("---##### calculatediff 0 " + datedb);
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
			// System.out.println("---##### calculatediff 0 " +
			// sdf.format(date));

			Date datestop = sdf.parse(datedb);

			long diff = date.getTime() - datestop.getTime();

			diffInDays = diff / (24 * 60 * 60 * 1000);

			// System.out.println(" #####  calculatediff 1 " + diffInDays);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (diffInDays == 0) {
			return "Today";

		} else if (diffInDays == 1) {
			return "Yesterday";
		} else {
			return datedb;
		}

	}

	private String calculatedifftime(String datedb) {
		System.out.println("date db......................" + datedb);
		// TODO Auto-generated method stub
		long diffInMillisec = 0;
		long diffInDays = 0;
		long diff = 0;
		try {

			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm ");
			System.out.println("---##### sdf 0 " + sdf);

			Date datestop = sdf.parse(datedb);
			System.out.println("---value of datestop...." + datestop);
			diff = date.getTime() - datestop.getTime();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		String s = String.valueOf(diff);
		return s;

	}

	private String[] splitfrom(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf down...." + tf);
		String fromtimetw = "";
		String k = tf.substring(0, tf.length() - 11);
		System.out.println("---value of kdown..." + k);

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(k);
			System.out
					.println("..........value of my date after conv" + myDate);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM");
		String finalDate = timeFormat.format(myDate);

		System.out.println("time------" + fromtimetw);
		// String[] v1 = { finalDate };
		String[] v1 = { finalDate };

		return v1;
	}

	private String[] splittodate(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...." + tf);
		String fromtimetw = "";
		String k = tf.substring(0, tf.length() - 11);
		System.out.println("---value of k..." + k);
		// String m = k.replace("T", " From ");
		// System.out.println("---value of m..."+m);
		// String[] n = m.split(" From");
		//
		// // System.out.println("--------n[1]" + n[1].trim());
		//
		// try {
		// SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
		// SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm ");
		// Date date = parseFormat.parse(n[1] + " ");
		//
		// // System.out.println(parseFormat.format(date) + " = "
		// // + displayFormat.format(date));
		//
		// fromtimetw = displayFormat.format(date);
		//
		// } catch (Exception e) {
		//
		// }
		//
		// String v = n[0].trim();

		// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(k);
			System.out
					.println("..........value of my date after conv" + myDate);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM");
		String finalDate = timeFormat.format(myDate);

		System.out.println("time------" + fromtimetw);
		// String[] v1 = { finalDate };
		String[] v2 = { finalDate };

		return v2;
	}

	private String[] splitfromto(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...." + tf);
		String fromtimetw = "";
		String k = tf.substring(0, tf.length() - 9);
		System.out.println("---value of k..." + k);
		String m = k.replace("T", " From ");
		System.out.println("---value of m..." + m);
		String[] n = m.split(" From");

		// System.out.println("--------n[1]" + n[1].trim());

		try {
			SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
			SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm ");
			Date date = parseFormat.parse(n[1] + " ");

			// System.out.println(parseFormat.format(date) + " = "
			// + displayFormat.format(date));

			fromtimetw = displayFormat.format(date);

		} catch (Exception e) {

		}

		String v = n[0].trim();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(v);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM yyyy");
		String finalDate = timeFormat.format(myDate);

		System.out.println("--------------date-----" + finalDate);

		fromtimetw = m.substring(15, tf.length() - 8);

		System.out.println("time------" + fromtimetw);
		String[] vto = { finalDate };
		String[] v2 = { fromtimetw };

		return vto;
	}

	private String[] splitfromtotime(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...." + tf);
		String fromtimetw = "";
		String k = tf.substring(0, tf.length() - 9);
		System.out.println("---value of k..." + k);
		String m = k.replace("T", " From ");
		System.out.println("---value of m..." + m);
		String[] n = m.split(" From");

		// System.out.println("--------n[1]" + n[1].trim());

		try {
			SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
			SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm ");
			Date date = parseFormat.parse(n[1] + " ");

			// System.out.println(parseFormat.format(date) + " = "
			// + displayFormat.format(date));

			fromtimetw = displayFormat.format(date);

		} catch (Exception e) {

		}

		String v = n[0].trim();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(v);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM yyyy");
		String finalDate = timeFormat.format(myDate);

		System.out.println("--------------date-----" + finalDate);

		fromtimetw = m.substring(15, tf.length() - 8);

		System.out.println("time------" + fromtimetw);
		String[] vto = { finalDate };
		String[] vtotym = { fromtimetw };

		return vtotym;
	}

	private String[] splitfromtym(String tym) {
		// TODO Auto-generated method stub

		// System.out.println("---value of tf...." + tf);
		//
		// String k = tf.substring(tf.indexOf(" "));
		// System.out.println("---value of k for time..." + k);
		// // String k1 = k.replace("", newChar)
		//
		// String fromtimetw = k.substring(0, 5); // = "ab"
		// String[] v1 = { fromtimetw };
		// // String[] v2={ fromtimetw };
		//
		// return v1;

		System.out.println("---value of tym...." + tym);
		String fromtimetw = "";
		String k = tym.substring(0, tym.length() - 9);
		System.out.println("---value of kym..." + k);
		String m = k.replace("T", " ");
		System.out.println("---value of mym..." + m);

		final String dateStart = m;
		// final String dateStop = "01/15/2012 10:31:48";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		// System.out.println("date format of system......................"+dateFormat.format(date));
		final String dateStop = dateFormat.format(date);
		Date d1 = null;
		Date d2 = null;
		String diffTym = "";

		try {
			d1 = dateFormat.parse(dateStart);
			d2 = dateFormat.parse(dateStop);

			// in milliseconds
			long diff = d2.getTime() - d1.getTime();

			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);
			diffTym = diffDays + " Days " + diffHours + " Hours " + diffMinutes
					+ " Minutes " + diffSeconds + " Seconds";
			System.out.print(diffDays + " days, ");
			System.out.print(diffHours + " hours, ");
			System.out.print(diffMinutes + " minutes, ");
			System.out.print(diffSeconds + " seconds.");

		} catch (Exception e) {
			e.printStackTrace();
		}

		String[] s = { diffTym };
		return s;
	}

	private String[] splitfromtime(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...." + tf);
		String fromtimetw = "";
		String k = tf.substring(0, tf.length() - 9);
		System.out.println("---value of k..." + k);
		String m = k.replace("T", " From ");
		System.out.println("---value of m..." + m);
		String[] n = m.split(" From");

		// System.out.println("--------n[1]" + n[1].trim());

		try {
			SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
			SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm ");
			Date date = parseFormat.parse(n[1] + " ");

			// System.out.println(parseFormat.format(date) + " = "
			// + displayFormat.format(date));

			fromtimetw = displayFormat.format(date);

		} catch (Exception e) {

		}

		String v = n[0].trim();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(v);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM, yyyy");
		String finalDate = timeFormat.format(myDate);

		System.out.println("--------------date-----" + finalDate);

		fromtimetw = m.substring(15, tf.length() - 4);

		System.out.println("time------" + fromtimetw);
		String[] v1 = { finalDate };
		String[] v1time = { fromtimetw };

		return v1time;
	}

	private void fetchdata() {
		asyncfetch_csnstate = null;

		asyncfetch_csnstate = new DownloadxmlsDataURL();
		asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		// ////////////////////////////////////////////////////////////////////------------/////////////////////////////////
		/*
		 * if (asyncfetch_csnstate == null) { iv.setVisibility(View.VISIBLE);
		 * ((ProgressBar) findViewById(R.id.progressBar1))
		 * .setVisibility(View.GONE);
		 * 
		 * Log.e("async", "null"); asyncfetch_csnstate = new
		 * DownloadxmlsDataURL(); asyncfetch_csnstate.execute(); } else { if
		 * (asyncfetch_csnstate.getStatus() == AsyncTask.Status.RUNNING) {
		 * Log.e("async", "running"); iv.setVisibility(View.GONE);
		 * ((ProgressBar) findViewById(R.id.progressBar1))
		 * .setVisibility(View.VISIBLE); } }
		 */
		// ///////////////////////////////////////////////////////////////////////////////////////////////
	}

	public class DownloadxmlsDataURL extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			String xx = "";

			String url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetStationCallibrationNew?Mobile="
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

						sql.insert("SoundLevel_new", null, values);
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
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			// pd.cancel();
			try {
				System.out.println("...............value of sop" + sop);
				if (sop.equals("valid")) {

					getDetail();

				} else {
					try{
						ut.showD(SoundLevelMainAll.this, "invalid");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				iv.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
						.setVisibility(View.GONE);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	private void updaterefreshdate() {
		// TODO Auto-generated method stub

		Calendar c = Calendar.getInstance();

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedDate = df.format(c.getTime());

		System.out.println("------ curdaterefresh " + formattedDate);

		String[] aDate = { formattedDate };

		DBInterface db = new DBInterface(getBaseContext());
		db.SetDaterefresh(aDate);
		//db.Close();

		filldaterefresh();

	}

	/*
	 * protected void showD(String string) { // TODO Auto-generated method stub
	 * 
	 * final Dialog myDialog = new Dialog(SoundLevelMain.this);
	 * myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	 * myDialog.setContentView(R.layout.dialoginfosmall);
	 * myDialog.setCancelable(true); //
	 * myDialog.getWindow().setGravity(Gravity.BOTTOM);
	 * 
	 * TextView txt = (TextView) myDialog
	 * .findViewById(R.id.dialoginfogototextsmall); if (string.equals("empty"))
	 * { myDialog.setTitle("Error...");
	 * txt.setText("Please Fill required data.."); } else if
	 * (string.equals("nonet")) { myDialog.setTitle("Error..."); txt.setText(
	 * "No Internet Connection Found.Please Activate internet Connectin on Device.."
	 * ); } else if (string.equals("invalid")) { myDialog.setTitle(" ");
	 * txt.setText("No Refresh Data Available...."); }
	 * 
	 * Button btn = (Button) myDialog
	 * .findViewById(R.id.gotobtndialoginfosmall); btn.setOnClickListener(new
	 * OnClickListener() {
	 * 
	 * public void onClick(View v) { // TODO Auto-generated method stub
	 * 
	 * myDialog.dismiss(); // finish();
	 * 
	 * } });
	 * 
	 * myDialog.show();
	 * 
	 * }
	 */

	protected boolean net() {
		// TODO Auto-generated method stub
		Context context = this.getApplicationContext();
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	private void filldaterefresh() {
		// TODO Auto-generated method stub

		System.out.println("-------  filldateref " + daterestr);

		if (daterestr.equals("1")) {
			// txtdate.setVisibility(View.INVISIBLE);
			txtdaterefresh.setVisibility(View.INVISIBLE);
		} else {

			try {

				String olddate = getolddate();

				System.out.println("-------  olddate " + olddate);

				Calendar c = Calendar.getInstance();

				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String formattedDate = df.format(c.getTime());

				System.out.println("------ curdaterefresh " + formattedDate);
				String diff = getTimeDiff(olddate, formattedDate);
				System.out.println("----- ##### " + diff);

				if ((diff.contains("seconds ago"))
						|| (diff.contains("minutes ago"))) {
					// txtdate.setVisibility(View.INVISIBLE);
					txtdaterefresh.setVisibility(View.INVISIBLE);

				} else {
					System.out.println("----- ##### 2 " + diff);

					if (diff.equals("yesterday")) {
						String refdate = "1 day old data";
						// txtdate.setText(refdate);
					} else if (diff.contains("ago")) {

						String[] sar = diff.split(" ");
						String a = sar[0].toString();
						int i = Integer.parseInt(a);

						if (i > 8) {
							// txtdate.setText(" 1 day old data");
						} else {
							String ref[] = diff.split("ago");

							String refdate = ref[0].toString();
							System.out.println("--- #### refdate " + refdate);

							// txtdate.setText(refdate + "old data");
						}

					} else {
						// txtdate.setText(diff + "old data");
					}
				}

			} catch (Exception e) {

			}

		}

	}

	private String getTimeDiff(String time, String curTime)
			throws ParseException {
		DateFormat formatter;
		Date curDate;
		Date oldDate;
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		curDate = (Date) formatter.parse(curTime);
		oldDate = (Date) formatter.parse(time);
		long oldMillis = oldDate.getTime();
		long curMillis = curDate.getTime();
		// Log.d("CaseListAdapter", "Date-Milli:Now:"+curDate.toString()+":"
		// +curMillis +" old:"+oldDate.toString()+":" +oldMillis);
		CharSequence text = DateUtils.getRelativeTimeSpanString(oldMillis,
				curMillis, 0);
		return text.toString();
	}

	private String getolddate() {
		// TODO Auto-generated method stub

		DBInterface dbi = new DBInterface(getBaseContext());
		String dateref = dbi.GetDateRefresg();
		dbi.Close();
		return dateref;
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences preferences = getApplicationContext()
				.getSharedPreferences("SCROLLSOUND", Context.MODE_PRIVATE);
		int scroll = preferences.getInt("ScrollValueSound", 0);
		System.out
				.println(".............value of scroll at resume>>>>>>>>>>>>>>>>>>>"
						+ scroll);
		// connectionstatus.scrollTo(0, scroll);
		sound.smoothScrollToPosition(scroll);
	}

	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SharedPreferences preferences = getApplicationContext()
				.getSharedPreferences("SCROLLSOUND", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		int scroll = sound.getFirstVisiblePosition();
		// int scrollx = connectionstatus.getScrollX();
		System.out.println(".............value of scroll>>>>>>>>>>>>>>>>>>>"
				+ scroll);
		editor.putInt("ScrollValueSound", scroll);
		editor.commit();

		// finish();
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();
		/*
		 * Intent i = new Intent(getBaseContext(), SoundLevelStateWise.class);
		 * i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * getBaseContext().startActivity(i); finish();
		 */

	}

}
