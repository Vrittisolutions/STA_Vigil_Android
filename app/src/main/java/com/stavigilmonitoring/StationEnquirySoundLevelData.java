package com.stavigilmonitoring;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.StationEnquirySoundDataAdp;
import com.beanclasses.SoundLevelBeanSort;
import com.database.DBInterface;
import com.helper.Util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StationEnquirySoundLevelData extends Activity {
	private static final String Days = null;
	private static final String Hours = null;
	private static final String Minutes = null;
	private static final String Seconds = null;
	ProgressDialog pd;
	ListView workspacewisedetail;
	String mobno, link;
	AsyncTask depattask;
	AsyncTask depattaskND;
	AsyncTask Loadasync;
	String InstallationID;
	String Syncloaddate;
	// ExpandableListAdapter listAdapter;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop = "no";
	static SimpleDateFormat dff;
	static String Ldate;
	ArrayList<String> projectlist = new ArrayList<String>();
	String spparam = "Employee Reporting to Me";
	ImageView iv;
	// SoundLevelHelper sr;
	int FlagLoad = 0;

	List<SoundLevelBeanSort> soundLevelHelperslist;
	String responsemsg = "k";
	String type;
	String ActivityName, ActivityId, actname;
	private TextView txtdate;
	private TextView txtdaterefresh;
	String daterestr;
	private ListView connectionstatus;
	private ExpandableListView expListView;
	HashMap<String, List<String>> listDataChild;
	List<String> listDataHeader;
	private String stnnAME;
	private String Stationname, InstallationId;
	// private ListView suspectedDetails;
	private ListView soundleveldetails;
	private TextView soundlevelstnname;
	Button txtload;
	ImageView mStandCal;
	StationEnquirySoundDataAdp soundLevelAdapt;
	String enddate, startdate, Syncdate, subtype;
	DateFormat dateFormat;
	
	Date todate1;
	LinearLayout layouttxt;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(com.stavigilmonitoring.R.layout.soundlevel);
		soundlevelstnname = (TextView) findViewById(com.stavigilmonitoring.R.id.tvsoundlevelssstnname);

		Bundle extras = getIntent().getExtras();
		Stationname = extras.getString("stnname");
		type = extras.getString("type");
		InstallationId = extras.getString("Installation");//Installation
        ut.getSoundLevel();

		// extras.getString("InstallationId")
		soundlevelstnname.setText(Stationname);
		txtload = (Button) findViewById(com.stavigilmonitoring.R.id.txtload_earlierdata);
		iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_soundlevel);
		mStandCal = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_calibrationStnd);
		soundleveldetails = (ListView) findViewById(com.stavigilmonitoring.R.id.lv_soundlevel);
		layouttxt = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.layouttxt);
		soundLevelHelperslist = new ArrayList<SoundLevelBeanSort>();

		db = new DatabaseHandler(getBaseContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		layouttxt.setVisibility(View.INVISIBLE);
		dbi.Close();

		if (dbvalue()) {

			GetDetail();

		} else if (ut.isnet(getApplicationContext())) {

			fetchdata();

		} else {
			showD("nonet");
		}
		if (soundLevelHelperslist.size() > 0) {
			layouttxt.setVisibility(View.VISIBLE);
		} else {
			layouttxt.setVisibility(View.INVISIBLE);
		}

		txtload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fetchdata_load();
			}
		});

		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (ut.isnet(getApplicationContext())) {

					fetchdata();
				} else {
					showD("nonet");
				}

			}
		});
		mStandCal.setVisibility(View.GONE);
		/*
		 * mStandCal.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub Intent intent = new Intent(SoundLevel.this,
		 * SoundLevelCalibrationStandard.class); intent.putExtra("Stationname",
		 * Stationname); intent.putExtra("InstallationID", InstallationID);
		 * 
		 * startActivity(intent); } });
		 */
	}

	private boolean dbvalue() {
		try {
			// TODO Auto-generated method stub
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery(
					"SELECT * FROM SoundLevel where InstallationId='"
							+ InstallationID + "'", null);
			// where StationName='"
			// + Stationname + "'
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

	/*
	 * void getMindate() {
	 * 
	 * try {
	 * 
	 * DatabaseHandler db = new DatabaseHandler(getBaseContext());
	 * SQLiteDatabase sql = db.getWritableDatabase(); ArrayList<String> ls = new
	 * ArrayList<String>();
	 * 
	 * Cursor c2 = sql.rawQuery(
	 * "SELECT MIN(CallibrationDate) FROM SoundLevel WHERE StationName='" +
	 * Stationname + "'", null);
	 * 
	 * Log.e("Count", c2.getCount() + "");
	 * 
	 * if (c2.moveToFirst()) { do { //
	 * ls.add(networkodelist.setNetworkCode(cursor.getString(0)));
	 * ls.add(c2.getString(0));
	 * 
	 * } while (c2.moveToNext()); } String calibrationDate = ls.get(0);
	 * 
	 * splitcalibration(calibrationDate);
	 * 
	 * c2.close(); db.close(); ls.clear();
	 * 
	 * Log.e("calibrationDate", "" + calibrationDate); } catch (Exception e) {
	 * // TODO: handle exception e.printStackTrace(); }
	 * 
	 * }
	 */

	/*
	 * public void getMindateND() {
	 * 
	 * try {
	 * 
	 * DatabaseHandler db = new DatabaseHandler(getBaseContext());
	 * SQLiteDatabase sql = db.getWritableDatabase(); ArrayList<String> ls = new
	 * ArrayList<String>();
	 * 
	 * Cursor c2 = sql.rawQuery(
	 * "SELECT MIN(CallibrationDate) FROM SoundLevelND WHERE StationName='" +
	 * Stationname + "'", null);
	 * 
	 * Log.e("Count", c2.getCount() + "");
	 * 
	 * if (c2.moveToFirst()) { do { //
	 * ls.add(networkodelist.setNetworkCode(cursor.getString(0)));
	 * ls.add(c2.getString(0));
	 * 
	 * } while (c2.moveToNext()); } String calibrationDate = ls.get(0);
	 * 
	 * splitcalibration(calibrationDate);
	 * 
	 * c2.close(); db.close(); ls.clear();
	 * 
	 * Log.e("calibrationDate", "" + calibrationDate); } catch (Exception e) {
	 * // TODO: handle exception e.printStackTrace(); }
	 * 
	 * }
	 */

	/*public void GetInstallation() {
		try {

		    DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			ArrayList<String> ls = new ArrayList<String>();

			Cursor c2 = sql.rawQuery(
					"SELECT InstallationId FROM SoundLevel WHERE StationName='"
							+ Stationname + "'", null);

			Log.e("Count", c2.getCount() + "");

			if (c2.moveToFirst()) {
				do {
					// ls.add(networkodelist.setNetworkCode(cursor.getString(0)));
					ls.add(c2.getString(0));

				} while (c2.moveToNext());
			}
			InstallationID = ls.get(0);

			c2.close();
			// db.close();
			ls.clear();

			Log.e("InstallationID ", "" + InstallationID);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}*/

	public void GetDetail() {
		try {
			soundLevelHelperslist.clear();
			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
            SQLiteDatabase sql = db.getWritableDatabase();
			Cursor c2 = sql
					.rawQuery(
							"SELECT * FROM SoundLevel WHERE StationName='"
									+ Stationname
									+ "' ORDER BY ScheduletimeDate DESC,ScheduletimeTime DESC",
							null); // ",

			Log.e("SoundLevel ", "" + c2.getCount());
			Log.e("SoundLevel ", "" + c2.getColumnCount());
			if (c2.getCount() == 0) {
				SoundLevelBeanSort beanSort = new SoundLevelBeanSort();
				beanSort.setScheduleDate("");

				beanSort.setScheduleTime("");

				beanSort.setStandard("");
				beanSort.setActual("");
				beanSort.setPercentage("");

				c2.close();

			} else {
				FlagLoad = 1;

				c2.moveToFirst();

				int column = 0;
				do {

					SoundLevelBeanSort beanSort = new SoundLevelBeanSort();

					// int column1 = c2.getColumnIndex("ScheduleTime");
					String schTime = c2.getString(c2
							.getColumnIndex("ScheduletimeDate"));
					String schDate = c2.getString(c2
							.getColumnIndex("ScheduletimeTime"));
					schTime = split(schTime);
					beanSort.setScheduleDate(schTime);
					beanSort.setScheduleTime(schDate);

					beanSort.setStandard(c2.getString(c2
							.getColumnIndex("Standard")));
					beanSort.setActual(c2.getString(c2.getColumnIndex("Actual")));
					beanSort.setPercentage(c2.getString(c2
							.getColumnIndex("Percentage")));

					soundLevelHelperslist.add(beanSort);
					Log.d("test", "list size :" + soundLevelHelperslist);
					// }
				} while (c2.moveToNext());

				c2.close();
			}
		/*	layouttxt.setVisibility(View.VISIBLE);
			soundLevelAdapt = new StationEnquirySoundDataAdp(this,
					soundLevelHelperslist);
			soundleveldetails.setAdapter(soundLevelAdapt);
			Util.setListViewHeightBasedOnChildren(soundleveldetails);*/
			
			
			layouttxt.setVisibility(View.VISIBLE);
			
			soundLevelAdapt = new StationEnquirySoundDataAdp(this,
					soundLevelHelperslist);
			int currentPosition = soundleveldetails.getFirstVisiblePosition();
			soundleveldetails.setSelectionFromTop(currentPosition + 1, 0);
			soundleveldetails.setAdapter(soundLevelAdapt);
			 Util.setListViewHeightBasedOnChildren(soundleveldetails);

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

	}

	private String split(String schTime) {
		// TODO Auto-generated method stub
		Date input = null;
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
		try {
			input = date.parse(schTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat date1 = new SimpleDateFormat("dd MMM");

		return date1.format(input);

	}

	/*public void GetDetail_load() {
		try {
			soundLevelHelperslist.clear();

			DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();

			Cursor c2 = sql
					.rawQuery(
							"SELECT * FROM SoundLevel WHERE StationName='"
									+ Stationname
									+ "' ORDER BY ScheduletimeDate DESC,ScheduletimeTime DESC",
							null); // ",

			Log.e("SoundLevel ", "" + c2.getCount());
			Log.e("SoundLevel ", "" + c2.getColumnCount());
			if (c2.getCount() == 0) {
				SoundLevelBeanSort beanSort = new SoundLevelBeanSort();
				beanSort.setScheduleDate("");

				beanSort.setScheduleTime("");

				beanSort.setStandard("");
				beanSort.setActual("");
				beanSort.setPercentage("");

				c2.close();
				sql.close();
				db.close();

			} else {

				c2.moveToFirst();

				int column = 0;
				do {

					SoundLevelBeanSort beanSort = new SoundLevelBeanSort();

					// int column1 = c2.getColumnIndex("ScheduleTime");
					String schTime = c2.getString(c2
							.getColumnIndex("ScheduletimeDate"));
					String schDate = c2.getString(c2
							.getColumnIndex("ScheduletimeTime"));

					beanSort.setScheduleDate(schTime);
					beanSort.setScheduleTime(schDate);

					beanSort.setStandard(c2.getString(c2
							.getColumnIndex("Standard")));
					beanSort.setActual(c2.getString(c2.getColumnIndex("Actual")));
					beanSort.setPercentage(c2.getString(c2
							.getColumnIndex("Percentage")));

					soundLevelHelperslist.add(beanSort);
					Log.d("test", "list size :" + soundLevelHelperslist);
					// }
				} while (c2.moveToNext());

				c2.close();
				sql.close();
				// db.close();
			}
			layouttxt.setVisibility(View.VISIBLE);
			int currentPosition = soundleveldetails.getFirstVisiblePosition();
			soundLevelAdapt = new StationEnquirySoundDataAdp(this,
					soundLevelHelperslist);
			soundleveldetails.setSelectionFromTop(currentPosition + 1, 0);
			// soundleveldetails.setAdapter(soundLevelAdapt);
			// Util.setListViewHeightBasedOnChildren(soundleveldetails);

			
			 * // Appending new data to menuItems ArrayList adapter = new
			 * ListViewAdapter( AndroidListViewWithLoadMoreButtonActivity.this,
			 * menuItems);
			 * 
			 * // Setting new scroll position
			 * lv.setSelectionFromTop(currentPosition + 1, 0);
			 

		} catch (Exception e) {
			e.printStackTrace();
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
	}*/

	private String[] splitfrom2(String tf2) {
		// TODO Auto-generated method stub
		String s = tf2 + " % ";
		String[] vs2 = { s };
		return vs2;

	}

	private String splitcalibration(String tf) {
		// TODO Auto-generated method stub
		// 12/31/2015 7:05:00 AM---
		String time1, time2 = null;
		String time[];
		String k = "";
		// String str = "18/01/2013 5:00:00 pm";2/17/2016 4:05:22 PM
		SimpleDateFormat input = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		Date dt;
		try {
			dt = input.parse(tf);

			SimpleDateFormat output = new SimpleDateFormat("yyyy/MM/dd");// 2016/02/18
			Syncloaddate = output.format(dt); // contains 18/01/2013
												// 17:00:00
												// String k = 01/14/2016
												// 07:05
												// formattedDate.substring(formattedDate.indexOf(" "));

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

		return Syncloaddate;
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
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
		String finalDate = timeFormat.format(myDate);

		String[] v2 = { finalDate };

		return v2;
	}

	private void fetchdata() {
		// TODO Auto-generated method stub
		//DatabaseHandler db = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		//sql.execSQL("DROP TABLE IF EXISTS SoundLevel");
		//sql.execSQL(ut.getSoundLevel());
		sql.delete("SoundLevel",null,null);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		String Syncdate = dateFormat.format(date);
		depattask = new DownloadxmlsDataURL().execute(Syncdate);

	}

	private void fetchdata_load() {
		// TODO Auto-generated method stub
		String Syncloaddate = getMindate();
		depattask = new DownloadxmlsDataURL().execute(Syncloaddate);

	}

	private String getMindate() {
		String calibrationDate = null;
		try {

			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			ArrayList<String> ls = new ArrayList<String>();
			
			Cursor c2 = sql.rawQuery(
					"SELECT MIN(CallibrationDate) FROM SoundLevel WHERE StationName='"
							+ Stationname + "'", null);

			Log.e("Count", c2.getCount() + "");

			if (c2.moveToFirst()) {
				do {
					// ls.add(networkodelist.setNetworkCode(cursor.getString(0)));
					ls.add(c2.getString(0));

				} while (c2.moveToNext());
			}
			calibrationDate = ls.get(0);

			c2.close();
			// db.close();
			ls.clear();
			Log.e("calibrationDate", "" + calibrationDate);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return splitcalibration(calibrationDate);
	}

	private void GetDetailND() {
		// TODO Auto-generated method stub

		try {
			soundLevelHelperslist.clear();
			//DatabaseHandler db = new DatabaseHandler(getBaseContext());

			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor c2 = sql
					.rawQuery(
							"SELECT * FROM SoundLevelND WHERE StationName='"
									+ Stationname
									+ "' ORDER BY ScheduletimeDate DESC,ScheduletimeTime DESC",
							null); // ",

			Log.e("SoundLevel ", "" + c2.getCount());
			Log.e("SoundLevel ", "" + c2.getColumnCount());
			if (c2.getCount() == 0) {
				FlagLoad = 0;
				SoundLevelBeanSort beanSort = new SoundLevelBeanSort();
				beanSort.setScheduleDate("");

				beanSort.setScheduleTime("");

				beanSort.setStandard("");
				beanSort.setActual("");
				beanSort.setPercentage("");

				c2.close();
				sql.close();
				// db.close();
				// Syncdate="";
				// depattask = new DownloadxmlsDataURLND().execute();

			} else {
				FlagLoad = 2;
				c2.moveToFirst();

				int column = 0;
				do {

					SoundLevelBeanSort beanSort = new SoundLevelBeanSort();

					// int column1 = c2.getColumnIndex("ScheduleTime");
					String schTime = c2.getString(c2
							.getColumnIndex("ScheduletimeDate"));
					String schDate = c2.getString(c2
							.getColumnIndex("ScheduletimeTime"));

					beanSort.setScheduleDate(schTime);
					beanSort.setScheduleTime(schDate);

					beanSort.setStandard(c2.getString(c2
							.getColumnIndex("Standard")));
					beanSort.setActual(c2.getString(c2.getColumnIndex("Actual")));
					beanSort.setPercentage(c2.getString(c2
							.getColumnIndex("Percentage")));

					soundLevelHelperslist.add(beanSort);
					Log.d("test", "list size :" + soundLevelHelperslist);
					// }
				} while (c2.moveToNext());

				c2.close();
				sql.close();
				// db.close();
			}
			layouttxt.setVisibility(View.VISIBLE);
			soundLevelAdapt = new StationEnquirySoundDataAdp(
					getApplicationContext(), soundLevelHelperslist);
			// new StationEnquirySoundDataAdp(this, soundLevelHelperslist)
			soundleveldetails.setAdapter(soundLevelAdapt);
			Util.setListViewHeightBasedOnChildren(soundleveldetails);

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

	}

	public class DownloadxmlsDataURL extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			String xx = params[0];
			// InstallationId
			try {
				String url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetStationCallibration_Load?InstallationId="
						+ InstallationId						
						+ "&SyncDate="
						+ xx;
				url = url.replaceAll(" ", "%20");
				// date = new Date();
				System.out.println("============ internet reg url " + url);

				System.out.println("-------  activity url --- " + url);

				responsemsg = ut.httpGet(url);

				System.out.println("-------------  xx vale of non repeated-- "
						+ responsemsg);
				//DatabaseHandler db = new DatabaseHandler(getBaseContext());
                SQLiteDatabase sql = db.getWritableDatabase();
				

				if (responsemsg.contains("<Table1>")) {

					NodeList nl = ut.getnode(responsemsg, "Table1");
					sop = "valid";
					for (int i = 0; i < nl.getLength(); i++) {

						// Log.e(tag, msg);
						Element e = (Element) nl.item(i);
						String network = ut.getValue(e, "StationMasterId");
						String DateTime = ut.getValue(e, "ScheduleTime");
						/*
						 * String time =
						 * DateTime.substring(DateTime.indexOf(" ")); String
						 * date = DateTime.substring(0, DateTime.indexOf(" "));
						 */

						String[] v1 = splitfrom(DateTime);
						String[] v2 = splittime(DateTime);
						String Scheduletime_date = v2[0];
						String Scheduletime_time = v1[0];

						SoundLevelBeanSort beanSort = new SoundLevelBeanSort(
								ut.getValue(e, "InstallationId"), ut.getValue(
										e, "StationName"), ut.getValue(e,
										"CallibrationDate"), Scheduletime_time,
								ut.getValue(e, "AO"),
								ut.getValue(e, "Standard"), ut.getValue(e,
										"Actual"),
								ut.getValue(e, "Percentage"), ut.getValue(e,
										"NetworkCode"), ut.getValue(e,
										"ServerTime"), ut.getValue(e, "Rank"),
								Scheduletime_date);
						db.addSoundLevel(beanSort);

					}

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				sop = "invalid";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				sop = "invalid";
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
			txtload.setVisibility(View.INVISIBLE);

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {

				System.out.println("...............value of sop" + sop);
				if (sop.equals("valid")) {

					GetDetail();

				} else {

					showD("invalid");
				}

				iv.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
						.setVisibility(View.GONE);

				if (txtload.getVisibility() == View.INVISIBLE) {
					txtload.setVisibility(View.VISIBLE);
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

	}

	/*
	 * public class DownloadXML_load extends AsyncTask<String, Void, String> {
	 * 
	 * @Override protected void onPreExecute() { // TODO Auto-generated method
	 * stub super.onPreExecute(); iv.setVisibility(View.GONE); ((ProgressBar)
	 * findViewById(R.id.progressBar1)) .setVisibility(View.VISIBLE);
	 * GetInstallation();
	 * 
	 * if (FlagLoad == 1) { getMindate(); } else if (FlagLoad == 2) {
	 * getMindateND(); } }
	 * 
	 * @Override protected String doInBackground(String... params) { // TODO
	 * Auto-generated method stub try { String url =
	 * "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetStationCallibration_Load?InstallationId="
	 * + InstallationID + "&SyncDate=" + Syncloaddate; url = url.replaceAll(" ",
	 * "%20"); // date = new Date();
	 * System.out.println("============ internet reg url " + url);
	 * 
	 * System.out.println("-------  activity url --- " + url);
	 * 
	 * responsemsg = ut.httpGet(url); if (responsemsg.contains("<Table1>")) {
	 * 
	 * NodeList nl = ut.getnode(responsemsg, "Table1"); sop = "valid"; for (int
	 * i = 0; i < nl.getLength(); i++) {
	 * 
	 * // Log.e(tag, msg); Element e = (Element) nl.item(i); String network =
	 * ut.getValue(e, "StationMasterId"); String DateTime = ut.getValue(e,
	 * "ScheduleTime");
	 * 
	 * String time = DateTime.substring(DateTime.indexOf(" ")); String date =
	 * DateTime.substring(0, DateTime.indexOf(" "));
	 * 
	 * 
	 * String[] v1 = splitfrom(DateTime); String[] v2 = splittime(DateTime);
	 * String Scheduletime_date = v2[0]; String Scheduletime_time = v1[0];
	 * 
	 * SoundLevelBeanSort beanSort = new SoundLevelBeanSort( ut.getValue(e,
	 * "InstallationId"), ut.getValue( e, "StationName"), ut.getValue(e,
	 * "CallibrationDate"), Scheduletime_time, ut.getValue(e, "AO"),
	 * ut.getValue(e, "Standard"), ut.getValue(e, "Actual"), ut.getValue(e,
	 * "Percentage"), ut.getValue(e, "NetworkCode"), ut.getValue(e,
	 * "ServerTime"), ut.getValue(e, "Rank"), Scheduletime_date);
	 * db.addSoundLevel(beanSort);
	 * 
	 * }
	 * 
	 * }
	 * 
	 * } catch (IOException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); sop = "invalid"; }
	 * 
	 * return sop; }
	 * 
	 * @Override protected void onPostExecute(String result) { // TODO
	 * Auto-generated method stub super.onPostExecute(result);
	 * 
	 * try {
	 * 
	 * System.out.println("...............value of sop" + sop); if
	 * (sop.equals("valid")) {
	 * 
	 * GetDetail_load(); GetInstallation(); getMindate();
	 * 
	 * } else {
	 * 
	 * showD("invalid"); }
	 * 
	 * iv.setVisibility(View.VISIBLE); ((ProgressBar)
	 * findViewById(R.id.progressBar1)) .setVisibility(View.GONE);
	 * 
	 * } catch (Exception e) { e.printStackTrace(); dff = new
	 * SimpleDateFormat("HH:mm:ss"); Ldate = dff.format(new Date());
	 * 
	 * StackTraceElement l = new Exception().getStackTrace()[0];
	 * System.out.println(l.getClassName() + "/" + l.getMethodName() + ":" +
	 * l.getLineNumber()); ut = new utility(); if (!ut.checkErrLogFile()) {
	 * 
	 * ut.ErrLogFile(); } if (ut.checkErrLogFile()) {
	 * ut.addErrLog(l.getClassName() + "/" + l.getMethodName() + ":" +
	 * l.getLineNumber() + "	" + e.getMessage() + " " + Ldate); }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * }
	 */

	/*
	 * public class DownloadxmlsDataURLND extends AsyncTask<String, Void,
	 * String> {
	 * 
	 * @Override protected String doInBackground(String... params) { // TODO
	 * Auto-generated method stub
	 * 
	 * String xx = ""; // InstallationId try { String url =
	 * "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetStationCallibration_Android?Mobile="
	 * + mobno + "&SyncDate=" + xx + "&InstallationId=" + InstallationId + "";
	 * url = url.replaceAll(" ", "%20"); // date = new Date();
	 * System.out.println("============ internet reg url " + url);
	 * 
	 * System.out.println("-------  activity url --- " + url);
	 * 
	 * responsemsg = ut.httpGet(url);
	 * 
	 * System.out.println("-------------  xx vale of non repeated-- " +
	 * responsemsg); SQLiteDatabase sql = db.getWritableDatabase();
	 * sql.execSQL("DROP TABLE IF EXISTS SoundLevelND");
	 * sql.execSQL(ut.getSoundLevelND());
	 * 
	 * if (responsemsg.contains("<Table1>")) {
	 * 
	 * NodeList nl = ut.getnode(responsemsg, "Table1"); sop = "valid"; for (int
	 * i = 0; i < nl.getLength(); i++) {
	 * 
	 * // Log.e(tag, msg); Element e = (Element) nl.item(i); String network =
	 * ut.getValue(e, "StationMasterId"); String DateTime = ut.getValue(e,
	 * "ScheduleTime");
	 * 
	 * String time = DateTime.substring(DateTime.indexOf(" ")); String date =
	 * DateTime.substring(0, DateTime.indexOf(" "));
	 * 
	 * 
	 * String[] v1 = splitfrom(DateTime); String[] v2 = splittime(DateTime);
	 * String Scheduletime_date = v2[0]; String Scheduletime_time = v1[0];
	 * 
	 * SoundLevelBeanSort beanSort = new SoundLevelBeanSort( ut.getValue(e,
	 * "InstallationId"), ut.getValue( e, "StationName"), ut.getValue(e,
	 * "CallibrationDate"), Scheduletime_time, ut.getValue(e, "AO"),
	 * ut.getValue(e, "Standard"), ut.getValue(e, "Actual"), ut.getValue(e,
	 * "Percentage"), ut.getValue(e, "NetworkCode"), ut.getValue(e,
	 * "ServerTime"), ut.getValue(e, "Rank"), Scheduletime_date);
	 * db.addSoundLevelND(beanSort);
	 * 
	 * } // c.close(); sql.close(); // db.close(); }
	 * 
	 * } catch (IOException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); sop = "invalid"; }
	 * 
	 * return sop; }
	 * 
	 * @Override protected void onPreExecute() { // TODO Auto-generated method
	 * stub super.onPreExecute();
	 * 
	 * iv.setVisibility(View.GONE); ((ProgressBar)
	 * findViewById(R.id.progressBar1)) .setVisibility(View.VISIBLE);
	 * txtload.setVisibility(View.GONE);
	 * 
	 * }
	 * 
	 * @Override protected void onPostExecute(String result) { // TODO
	 * Auto-generated method stub super.onPostExecute(result); try {
	 * 
	 * System.out.println("...............value of sop" + sop); if
	 * (sop.equals("valid")) {
	 * 
	 * GetDetailND();
	 * 
	 * } else {
	 * 
	 * showD("invalid"); }
	 * 
	 * iv.setVisibility(View.VISIBLE); ((ProgressBar)
	 * findViewById(R.id.progressBar1)) .setVisibility(View.GONE);
	 * 
	 * if (txtload.getVisibility() == View.INVISIBLE) {
	 * txtload.setVisibility(View.VISIBLE); }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); dff = new
	 * SimpleDateFormat("HH:mm:ss"); Ldate = dff.format(new Date());
	 * 
	 * StackTraceElement l = new Exception().getStackTrace()[0];
	 * System.out.println(l.getClassName() + "/" + l.getMethodName() + ":" +
	 * l.getLineNumber()); ut = new utility(); if (!ut.checkErrLogFile()) {
	 * 
	 * ut.ErrLogFile(); } if (ut.checkErrLogFile()) {
	 * ut.addErrLog(l.getClassName() + "/" + l.getMethodName() + ":" +
	 * l.getLineNumber() + "	" + e.getMessage() + " " + Ldate); }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * 
	 * 
	 * }
	 */

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(StationEnquirySoundLevelData.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoginfosmall);
		myDialog.setCancelable(true);

		TextView txt = (TextView) myDialog
				.findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall);
		if (string.equals("empty")) {
			myDialog.setTitle("Error...");
			txt.setText("Please Fill required data..");
		} else if (string.equals("nonet")) {
			myDialog.setTitle("Error...");
			txt.setText("No Internet Connection Found.Please Activate internet Connectin on Device..");
		} else if (string.equals("invalid")) {
			myDialog.setTitle(" ");
			txt.setText("No Refresh Data Available.....");
		}

		Button btn = (Button) myDialog
				.findViewById(com.stavigilmonitoring.R.id.gotobtndialoginfosmall);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				myDialog.dismiss();
				// finish();

			}
		});

		myDialog.show();

	}

	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		// finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		/*
		 * Bundle dataBundle = new Bundle(); dataBundle.putString("stnname",
		 * Stationname);
		 * 
		 * Intent i = new Intent(SoundLevel.this, SoundLevelMain.class);
		 * 
		 * startActivity(i);
		 */

	}
}
