package com.stavigilmonitoring;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.adapters.NonrepeatedAdAdapt;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.beanclasses.NonrepeatedAdHelper;
import com.database.DBInterface;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class NonrepeatedAd extends Activity {
	// ProgressDialog pd;
	ListView workspacewisedetail;
	String mobno, link;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop = "no";
	ArrayList<String> projectlist = new ArrayList<String>();
	static SimpleDateFormat dff;
	static String Ldate;
	ImageView iv,btnadd;

	String responsemsg = "k";
	String ActivityName, ActivityId, actname;
	private TextView txtdate;
	private TextView txtdaterefresh;
	String daterestr;
	private ListView connectionstatus;
	private ListView nonrepeated;
	private String Stationname;
	private TextView nonreprtedstnname;
	private TextView servertime;
	private TextView dateday;
	String flag = "";
	private TextView starttime;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.nonrepeatedad);

		Bundle extras = getIntent().getExtras();
		Stationname = extras.getString("stnname");
		nonreprtedstnname = (TextView) findViewById(R.id.tvnionreportedstnname);
		servertime = (TextView) findViewById(R.id.tvservertimenonreported);
		dateday = (TextView) findViewById(R.id.tvdatedaynonreported);
		starttime = (TextView) findViewById(R.id.tvstarttimenonreported);

		nonreprtedstnname.setText(Stationname);

		((LinearLayout) findViewById(R.id.llStationName)).setTag(Stationname);
		iv = (ImageView) findViewById(R.id.button_refresh_nonrepeated);
		nonrepeated = (ListView) findViewById(R.id.nonrepeatedad);
		btnadd = (ImageView) findViewById(R.id.button_alert_add);
		btnadd.setImageResource(R.drawable.work_assign);
		btnadd.setVisibility(View.VISIBLE);

		db = new DatabaseHandler(getApplicationContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		if (dbvalue()) {
			updateLink();
			updatelist();
		} else {

			fetchdata();
		}

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

				if (isnet()) {

					fetchdata();
				} else {
					showD("nonet");
				}
			}
		});
	}

	/*public void StationClick(View v) {
		*//*
		 * String Installationnm = ((LinearLayout)
		 * findViewById(R.id.llStationName)) .getTag().toString(); Bundle
		 * dataBundle = new Bundle(); dataBundle.putString("stnname",
		 * Installationnm); dataBundle.putString("frompage", "nonreport");
		 * Intent i = new Intent(getApplicationContext(),
		 * ConnectionStatus.class); i.putExtras(dataBundle); startActivity(i);
		 *//*

	}*/

	private boolean isnet() {
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

	public void updateLink() {
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		String[] params = new String[1];
		params[0] = Stationname;
		Cursor c = sql
				.rawQuery(
						"SELECT * FROM NonrepeatedAd  where InstallationDesc=?",
						params);
		if (c.getCount() == 0) {

			servertime.setText("");
			starttime.setText("");
			dateday.setText("");
		} else {
			c.moveToFirst();

			int column = 0;
			do {

				int column1 = c.getColumnIndex("LastServerTime");
				String tf = c.getString(column1);
				String tfdateday = c.getString(column1);
				String tftym = c.getString(column1);
				String v2 = splittime1(tf);

				String[] v1 = splitfrom1(c.getString(c
						.getColumnIndex("LastServerTime")));
				String[] v2dd = splittimedateday(c.getString(c
						.getColumnIndex("LastServerTime")));

				servertime.setText(splittime1(c.getString(c
						.getColumnIndex("LastServerTime"))));
				dateday.setText(v2dd[0]);
				starttime.setText(v1[0]);

				if (!c.getString(c.getColumnIndex("LA")).trim()
						.equalsIgnoreCase("")) {

					String[] v1LA = splitfrom1(c.getString(c
							.getColumnIndex("LA")));
					String[] v2ddLA = splittimedateday(c.getString(c
							.getColumnIndex("LA")));

					((TextView) findViewById(R.id.tvstarttimeLA))
							.setText(v1LA[0]);
					((TextView) findViewById(R.id.tvservertimeLA))
							.setText(splittime1(c.getString(c
									.getColumnIndex("LA"))));
					((TextView) findViewById(R.id.tvdatedayLA))
							.setText(v2ddLA[0]);
				} else {
					((TextView) findViewById(R.id.tvstarttimeLA)).setText("");
					((TextView) findViewById(R.id.tvservertimeLA)).setText("");
					((TextView) findViewById(R.id.tvdatedayLA)).setText("");
				}

				if (!c.getString(c.getColumnIndex("LBR")).trim()
						.equalsIgnoreCase("")) {
					String[] v1LBR = splitfrom1(c.getString(c
							.getColumnIndex("LBR")));
					String[] v2ddLBR = splittimedateday(c.getString(c
							.getColumnIndex("LBR")));

					((TextView) findViewById(R.id.tvstarttimeLBR))
							.setText(v1LBR[0]);
					((TextView) findViewById(R.id.tvservertimeLBR))
							.setText(splittime1(c.getString(c
									.getColumnIndex("LBR"))));
					((TextView) findViewById(R.id.tvdatedayLBR))
							.setText(v2ddLBR[0]);
				} else {
					((TextView) findViewById(R.id.tvstarttimeLBR)).setText("");
					((TextView) findViewById(R.id.tvservertimeLBR)).setText("");
					((TextView) findViewById(R.id.tvdatedayLBR)).setText("");
				}

				// ((TextView)findViewById(R.id.tvLA)).setText("Last Adv.  : "+c.getString(c.getColumnIndex("LA")));
				// ((TextView)findViewById(R.id.tvLB)).setText("Last BusAnnouncement      : "+c.getString(c.getColumnIndex("LB")));
				// ((TextView)findViewById(R.id.tvLBR)).setText("Last Ann. : "+c.getString(c.getColumnIndex("LBR")));
				Log.e("non rep Ad...........", "servertime: " + v2);
				Log.e("non rep Ad...........", "dateday: " + v2dd[0]);
				Log.e("non rep Ad...........", "starttime: " + v1[0]);
			} while (c.moveToNext());

			c.close();
		}

	}

	private String[] splittimedateday(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf for date...." + tf);
		String fromtimetw = "";

		String k = tf.substring(0, tf.indexOf(" "));
		System.out.println("---value of k for date..." + k);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
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
		SimpleDateFormat timeFormat = new SimpleDateFormat("EEE");
		String finalDate = timeFormat.format(myDate);

		String[] v2dd = { finalDate };

		return v2dd;
	}

	private String[] splitfrom1(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...." + tf);
		String fromtimetw = "";
		String k = tf.substring(tf.indexOf(" "));
		System.out.println("---value of k for time..." + k);

		System.out.println("time------" + fromtimetw);
		String[] v1 = { k };
		// String[] v2={ fromtimetw };

		return v1;
	}

	private String splittime1(String tf) {
		// TODO Auto-generated method stub

		long diffDays = 0;
		System.out.println("---value of tf for date...." + tf);
		String fromtimetw = "";
		String k = tf.substring(0, tf.indexOf(" "));

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
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
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM, yyyy");
		String finalDate = timeFormat.format(myDate);
		System.out.println("..............final date of update link>>>>>"
				+ finalDate);

		final String dateStart = finalDate;
		DateFormat dateFormat1 = new SimpleDateFormat("dd MMM, yyyy");
		Date date = new Date();
		System.out.println("date format of system......................"
				+ dateFormat1.format(date));
		System.out
				.println("<<<<<<<<<<<<<<<<date format startdate......................"
						+ dateStart);
		System.out.println("date format of web tym......................"
				+ date);
		final String dateStop = dateFormat1.format(date);
		System.out
				.println("<<<<<<<<<<<<<<<<date format dateStop......................"
						+ dateStop);
		Date d1 = null;
		Date d2 = null;
		String diffTym = "";

		try {
			d1 = dateFormat1.parse(dateStart);
			d2 = dateFormat1.parse(dateStop);
			System.out.println("d2......................" + d2);
			// in milliseconds
			long diff = d2.getTime() - d1.getTime();

			diffDays = diff / (24 * 60 * 60 * 1000);

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

		if (diffDays == 0) {
			return "Today";

		} else if (diffDays == 1) {
			return "Yesterday";
		} else {
			return finalDate;
		}

	}

	private boolean dbvalue() {
		// TODO Auto-generated method stub
		//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor cursor = sql.rawQuery("SELECT *   FROM NonrepeatedAd", null);

		System.out.println("----------  dbvalue screen cursor count -- "
				+ cursor.getCount());

		if (cursor != null && cursor.getCount() > 0) {

			cursor.close();
			return true;

		} else {

			cursor.close();
			return false;
		}

	}

	private void updatelist() {
		final ArrayList<NonrepeatedAdHelper> searchResults = GetDetail();
		nonrepeated.setAdapter(new NonrepeatedAdAdapt(this, searchResults));
	}

	private ArrayList<NonrepeatedAdHelper> GetDetail() {
		ArrayList<NonrepeatedAdHelper> results = new ArrayList<NonrepeatedAdHelper>();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		String[] params = new String[1];
		params[0] = Stationname;
		Cursor c = sql
				.rawQuery(
						"SELECT * FROM NonrepeatedAd  where InstallationDesc=? ORDER BY AdvertisementCode DESC",
						params);
		if (c.getCount() == 0) {
			NonrepeatedAdHelper sr = new NonrepeatedAdHelper();
			sr.setadvcode("");
			sr.setadvName("");
			sr.setinstallationname("");

			sr.setdateFrom("");
			sr.setdateTo("");
			sr.settimefrom("");
			sr.settimeto("");
			sr.setfirstdate("");
			sr.setfirsttime("");
			sr.setlastdate("");
			sr.setlasttime("");
			sr.setmasterrecord("");
			sr.setdetailrecord("");
			sr.setclipmaster("");
			results.add(sr);

			c.close();

			return results;
		} else {

			c.moveToFirst();

			int column = 0;
			do {

				NonrepeatedAdHelper sr = new NonrepeatedAdHelper();

				sr.setadvcode(c.getString(c.getColumnIndex("InstallationDesc")));

				sr.setadvName(c.getString(c.getColumnIndex("AdvertisementCode")));
				sr.setinstallationname(c.getString(c
						.getColumnIndex("AdvertisementDesc")));
				int column1 = c.getColumnIndex("EffectiveDateFrom");
				int column2 = c.getColumnIndex("EffectiveDateTo");
				String tf = c.getString(column1);
				String tfhr = c.getString(column1);
				String tfto = c.getString(column2);
				String tftohr = c.getString(column2);
				String tftym = c.getString(column1);
				String tftotym = c.getString(column2);
				String[] tym = splitfromtym(tftym);
				String[] v1 = splitfrom(tf);
				String[] v1hr = splitfromhr(tfhr);
				String[] v2 = splittodate(tfto);
				String[] v2hr = splittodatehr(tftohr);
				int column3 = c.getColumnIndex("FirstReportingDate");
				int column4 = c.getColumnIndex("LatestAddeDate");
				String fd = c.getString(column3);
				String fdhr = c.getString(column3);

				try {
					String datestr = fd.substring(0, tf.lastIndexOf(" ") + 1);
					int a = fd.indexOf(" ");
					// 08-Sep-2015 03:15:49 PM
					// Oct 15 2015 4:41PM
					DateFormat formatter;
					Date date;
					formatter = new SimpleDateFormat("MMM dd yyyy");
					date = (Date) formatter.parse(datestr);
					Log.e("TAG", "******************");
					Log.e("TAG", datestr.toString());
					Log.e("TAG", "MMM dd yyyy");
					SimpleDateFormat dateformatyyyyMMdd = new SimpleDateFormat(
							"MM/dd/yyyy");

					fd = dateformatyyyyMMdd.format(date);
					Log.e("TAG", "******************");
					// + fd.substring(fd.indexOf(" "));
					//fdhr = fd;
				} catch (Exception e) {
				}

				String ld = c.getString(column4);
				String ldhr = c.getString(column4); // May 20 2016 10:10AM
				// ......kk
				if (fd.equals("")) {
					sr.setfirstdate("No info");

				} else {
					
					Log.e("TAG", "FD : " + fd.toString());
					Log.e("TAG", "MM/dd/yyyy");
					Log.e("TAG", "******************");
					String[] vfd = splitfromdate(fd);
					sr.setfirstdate(vfd[0]);
				}
				if (fdhr.equals("")) {
					sr.setfirsttime("No info");

				} else {

					String[] vfhr = splitfromfhr(fdhr);
					sr.setfirsttime(vfhr[0]);
				}
				if (ld.equals("")) {
					sr.setlastdate("No info");

				} else {
					String[] lfd = splitfrommmm(ld);
					sr.setlastdate(lfd[0]);
				}
				if (ldhr.equals("")) {
					sr.setlasttime("No info");

				} else {

					String[] llhr = splitfromlasthr(ldhr);
					sr.setlasttime(llhr[0]);
				}
				sr.setCSR(c.getString(c.getColumnIndex("CSR")));
				sr.setdateFrom(v1[0]);
				sr.settimefrom(v1hr[0]);
				sr.setdateTo(v2[0]);
				sr.settimeto(v2hr[0]);
				sr.setmasterrecord(c.getString(c
						.getColumnIndex("IsmasterRecordDownloaded")));
				sr.setdetailrecord(c.getString(c
						.getColumnIndex("IsDetailRecordDownloaded")));
				sr.setclipmaster(c.getString(c
						.getColumnIndex("IsClipMasterRecordDownloaded")));
				results.add(sr);

			} while (c.moveToNext());

			c.close();
		}
		return results;

	}

	private String[] splitfrommmm(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...." + tf);
		String fromtimetw = "";

		String k = tf.substring(0, tf.length() - 8);// Jul 10 2016 6:01PM
		System.out.println("---value of k..." + k);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy");
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
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM, yyyy");
		String finalDate = timeFormat.format(myDate);

		System.out.println("time------" + fromtimetw);
		// String[] v1 = { finalDate };
		String[] v1 = { finalDate };

		return v1;
	}

	private String[] splitfrom(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...." + tf);
		String fromtimetw = "";

		String k = tf.substring(0, tf.length() - 9);
		System.out.println("---value of k..." + k);
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
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM, yyyy",Locale.ENGLISH);
		String finalDate = timeFormat.format(myDate);

		System.out.println("time------" + fromtimetw);
		// String[] v1 = { finalDate };
		String[] v1 = { finalDate };

		return v1;
	}

	private String[] splitfromdate(String tf) {
		// TODO Auto-generated method stub
		// tf 09/01/2015 04:15:08 PM
		System.out.println("---value of tffor f date...." + tf);
		String fromtimetw = "";
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy",Locale.ENGLISH);
		Date myDate = null;
		try {
			myDate = dateFormat.parse(tf);
			
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
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM, yyyy",Locale.getDefault());
		String finalDate;
		
		finalDate = timeFormat.format(myDate);
		
		Log.e("TAG", "******************");
		Log.e("TAG", myDate.toString());
		Log.e("TAG", "dd MMM, yyyy");
		Log.e("TAG", "******************");
		
		System.out.println("time------" + fromtimetw);

		String[] vfd = { finalDate };
		return vfd;
		//return vfd;
	}

	private String[] splitfromlastdate(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf for last date...." + tf);
		String fromtimetw = "";
		tf.length();
		String k = tf.substring(0, tf.length() - 9);
		System.out.println("---value of k for last date..." + k);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
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
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM, yyyy");
		String finalDate = timeFormat.format(myDate);

		System.out.println("time------" + fromtimetw);
		// String[] v1 = { finalDate };
		String[] lfd = { finalDate };

		return lfd;
	}

	private String[] splitfromhr(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...." + tf);
		String fromtimetw = "";
		System.out.println("---21111111111111111111...." + tf);
		String k = tf.substring(11, tf.length() - 0);
		System.out.println("---value of khr..." + k);
		String[] v1hr = { k };

		return v1hr;
	}

	private String[] splitfromfhr(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf for firsttime...." + tf);
		String fromtimetw = "";
		System.out.println("---21111111111111111111...." + tf);
		String k = tf.substring(12, tf.length() - 0);
		System.out.println("---value of k for first time hr..." + k);

		String[] vfhr = { k };

		return vfhr;
	}

	private String[] splitfromlasthr(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf for last time...." + tf);
		String fromtimetw = "";
		System.out.println("---21111111111111111111...." + tf);
		String k = tf.substring(11, tf.length() - 0);
		System.out.println("---value of k for last hr..." + k);

		String[] llhr = { k };

		return llhr;
	}

	private String[] splittodate(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...." + tf);
		String fromtimetw = "";
		String k = tf.substring(0, tf.length() - 9);

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
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM, yyyy");
		String finalDate = timeFormat.format(myDate);

		System.out.println("time------" + fromtimetw);
		// String[] v1 = { finalDate };
		String[] v2 = { finalDate };

		return v2;
	}

	private String[] splittodatehr(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf to...." + tf);
		String fromtimetw = "";
		String k = tf.substring(11, tf.length() - 0);
		System.out.println("---value of ktohr..." + k);

		String[] v2hr = { k };

		return v2hr;
	}

	private String[] splitfromtym(String tym) {
		// TODO Auto-generated method stub
		System.out.println("---value of tym...." + tym);
		String fromtimetw = "";
		String k = tym.substring(0, tym.length() - 9);
		System.out.println("---value of kym..." + k);
		String m = k.replace("T", " ");
		System.out.println("---value of mym..." + m);// 09/14/2015
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		String diffTym = null;
		try {
			Date datestop = sdf.parse(m);

			// final String dateStart = m;
			// final String dateStop = "01/15/2012 10:31:48";
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			final String dateStart = dateFormat.format(datestop);

			Date date = new Date();
			// System.out.println("date format of system......................"+dateFormat.format(date));
			final String dateStop = dateFormat.format(date);
			Date d1 = null;
			Date d2 = null;

			try {
				d1 = dateFormat.parse(dateStart);
				d2 = dateFormat.parse(dateStop);

				// in milliseconds
				long diff = d2.getTime() - d1.getTime();

				long diffSeconds = diff / 1000 % 60;
				long diffMinutes = diff / (60 * 1000) % 60;
				long diffHours = diff / (60 * 60 * 1000) % 24;
				long diffDays = diff / (24 * 60 * 60 * 1000);
				diffTym = diffDays + " Days " + diffHours + " Hours "
						+ diffMinutes + " Minutes " + diffSeconds + " Seconds";
				System.out.print(diffDays + " days, ");
				System.out.print(diffHours + " hours, ");
				System.out.print(diffMinutes + " minutes, ");
				System.out.print(diffSeconds + " seconds.");

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
		String[] s = { diffTym };
		return s;
	}

	private void fetchdata() {
		// TODO Auto-generated method stub

		new DownloadxmlsDataURL_new().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			String url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetNonReportedAdvt_Android_new?Mobile="
					+ mobno;

			url = url.replaceAll(" ", "%20");

			try {
				responsemsg = ut.httpGet(url);
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

			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			//sql.execSQL("DROP TABLE IF EXISTS NonrepeatedAd");
			//sql.execSQL(ut.getNonrepeatedAd());
			sql.delete("NonrepeatedAd",null,null);

			Cursor c = sql.rawQuery("SELECT *  FROM NonrepeatedAd", null);
			if (responsemsg.contains("<A>")) {
				sop = "valid";

				ContentValues values = new ContentValues();
				NodeList nl = ut.getnode(responsemsg, "Table1");
				String msg = "";
				String columnName, columnValue;
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
						else if (columnName.equalsIgnoreCase("EffectiveDateTo"))
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
						else if (columnName.equalsIgnoreCase("LastServerTime"))
							ncolumnname = "M";
						else if (columnName
								.equalsIgnoreCase("FirstReportingDate")) // FirstReportingDate
							ncolumnname = "N";
						else if (columnName.equalsIgnoreCase("LatestAddeDate"))
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

						/*
						 * if(ncolumnname=="E") { String currentTimeaft="";
						 * String datestr=columnValue.substring(0,
						 * columnValue.indexOf(" ")); int
						 * a=columnValue.indexOf(" "); String
						 * timestr=columnValue.substring(a);
						 * Log.e("recoed time ", timestr);
						 * 
						 * DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
						 * String now = df.format(new Date());
						 * 
						 * Date date,date1,time,time1; // SimpleDateFormat
						 * dateformatyyyyMMdd = new
						 * SimpleDateFormat("MM/dd/yyyy"); Calendar calendar2 =
						 * Calendar.getInstance(); SimpleDateFormat formatter2 =
						 * new SimpleDateFormat("hh:mm:ss");
						 * 
						 * String currentTime =
						 * formatter2.format(calendar2.getTime());
						 * 
						 * Date datetime; try {
						 * 
						 * 
						 * datetime = formatter2.parse(currentTime);
						 * 
						 * calendar2.setTime(datetime);
						 * calendar2.add(Calendar.HOUR, 1); currentTimeaft =
						 * formatter2.format(calendar2.getTime());
						 * 
						 * date= (Date)df.parse(datestr); date1=
						 * (Date)df.parse(now); time=formatter2.parse(timestr);
						 * time1=formatter2.parse(currentTimeaft);
						 * 
						 * long diff = time.getTime() - time1.getTime(); long
						 * diffHours = diff / (60 * 60 * 1000) % 24;
						 * 
						 * 
						 * 
						 * 
						 * 
						 * if(date.before(date1)) { flag="Valid";
						 * Log.e("Date is Before", ""+date); } else
						 * if(date.equals(date1)) {
						 * Log.e("Record is of same day", ""+date);
						 * Log.e("current time diiference ", ""+diffHours); if(
						 * diffHours<=0){
						 * 
						 * 
						 * 
						 * flag="Valid"; } } else{ flag="Invalid"; }
						 * 
						 * 
						 * } catch (ParseException e2) { // TODO Auto-generated
						 * catch block e2.printStackTrace(); }
						 * 
						 * }
						 */

						values.put(columnName, columnValue);
					}

					sql.insert("NonrepeatedAd", null, values);
					Log.d("test", "data :" + values);
				}

				c.close();

			} else {
				sop = "invalid";
				System.out.println("--------- invalid for AD list --- ");

			}

			return sop;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// pd.cancel();
			if (sop.equals("valid")) {
				updateLink();
				updatelist();
			} else {
				showD("nodata");
			}
			iv.setVisibility(View.VISIBLE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.GONE);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);
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
		db.Close();

		filldaterefresh();

	}

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(NonrepeatedAd.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(R.layout.dialoginfosmall);
		myDialog.setCancelable(true);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);

		TextView txt = (TextView) myDialog
				.findViewById(R.id.dialoginfogototextsmall);
		if (string.equals("empty")) {
			myDialog.setTitle("Error...");
			txt.setText("Please Fill required data..");
		} else if (string.equals("nonet")) {
			myDialog.setTitle("Error...");
			txt.setText("No Internet Connection Found.Please Activate internet Connectin on Device..");
		} else if (string.equals("invalid")) {
			myDialog.setTitle(" ");
			txt.setText("No Refresh Data Available. Please check Internet Connection");
		} else if (string.equals("nodata")) {
			myDialog.setTitle(" ");
			txt.setText("No data available");
		}
		Button btn = (Button) myDialog
				.findViewById(R.id.gotobtndialoginfosmall);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				myDialog.dismiss();
				// finish();

			}
		});

		myDialog.show();

	}

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
			txtdate.setVisibility(View.INVISIBLE);
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
					txtdate.setVisibility(View.INVISIBLE);
					txtdaterefresh.setVisibility(View.INVISIBLE);

				} else {
					System.out.println("----- ##### 2 " + diff);

					if (diff.equals("yesterday")) {
						String refdate = "1 day old data";
						txtdate.setText(refdate);
					} else if (diff.contains("ago")) {

						String[] sar = diff.split(" ");
						String a = sar[0].toString();
						int i = Integer.parseInt(a);

						if (i > 8) {
							txtdate.setText(" 1 day old data");
						} else {
							String ref[] = diff.split("ago");

							String refdate = ref[0].toString();
							System.out.println("--- #### refdate " + refdate);

							txtdate.setText(refdate + "old data");
						}

					} else {
						txtdate.setText(diff + "old data");
					}
				}

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
		 * Intent i = new Intent(NonrepeatedAd.this, NonrepeatedAdMain.class);
		 * 
		 * i.putExtras(dataBundle); startActivity(i);
		 */

	}

}
