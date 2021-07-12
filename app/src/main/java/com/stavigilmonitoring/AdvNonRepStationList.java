package com.stavigilmonitoring;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.AdvNonrepeatedStationwiseAdapt;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AdvNonRepStationList extends Activity {

	// ProgressDialog pd;
	ListView workspacewisedetail;
	String mobno, link;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop = "no";
	ArrayList<String> projectlist = new ArrayList<String>();
	static SimpleDateFormat dff;
	static String Ldate;
	ImageView iv;

	String responsemsg = "k";
	String ActivityName, ActivityId, actname;
	private TextView txtdate;
	private TextView txtdaterefresh;
	String daterestr;
	private ListView nonrep;
	private String Stationname, code, AdvNAme;
	private TextView nonreprtedstnname, addname;
	private TextView servertime;
	private TextView dateday;
	String flag = "";
	private TextView starttime;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.nonrepstationwisedata);

		Intent extras = getIntent();
		code = extras.getStringExtra("AdvCode");
		AdvNAme = extras.getStringExtra("AdvName");

		addname = (TextView) findViewById(com.stavigilmonitoring.R.id.netcodess);
		addname.setText(code + "  " + AdvNAme);
		iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_nonrepeated);
		nonrep = (ListView) findViewById(com.stavigilmonitoring.R.id.nonstn);

		db = new DatabaseHandler(getBaseContext());

       DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		if (dbvalue()) {

			updatelist();
		} else {

			fetchdata();
		}
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
//			sql.close();
//			db1.close();
			return true;

		} else {

			cursor.close();
			/*sql.close();
			db1.close();*/
			return false;
		}

	}

	private void updatelist() {
		final ArrayList<NonrepeatedAdHelper> searchResults = GetDetail();
		nonrep.setAdapter(new AdvNonrepeatedStationwiseAdapt(this,
				searchResults));
	}

	private ArrayList<NonrepeatedAdHelper> GetDetail() {
		ArrayList<NonrepeatedAdHelper> results = new ArrayList<NonrepeatedAdHelper>();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		String[] params = new String[1];
		params[0] = code;
		/*
		 * Cursor c = sql .rawQuery(
		 * "SELECT * FROM NonrepeatedAd  where InstallationDesc=? ORDER BY AdvertisementCode"
		 * , params);
		 */

		Cursor c = sql
				.rawQuery(
						"SELECT * FROM NonrepeatedAd  where AdvertisementCode=? ORDER BY InstallationDesc asc",
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
			/*sql.close();
			db.close();*/

			return results;
		} else {

			c.moveToFirst();

			int column = 0;
			do {

				NonrepeatedAdHelper sr = new NonrepeatedAdHelper();

				sr.setadvcode(c.getString(c.getColumnIndex("InstallationDesc")));
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
					String datestr = fd.substring(0, tf.lastIndexOf(" ") + 1);//08/23/2016 16:07:02
					int a = fd.indexOf(" ");
					// 08-Sep-2015 03:15:49 PM
					// Oct 15 2015 4:41PM
					DateFormat formatter;
					Date date;
					formatter = new SimpleDateFormat("MMM dd yyyy");
					date = (Date) formatter.parse(datestr);
					SimpleDateFormat dateformatyyyyMMdd = new SimpleDateFormat(
							"MM/dd/yyyy");

					fd = dateformatyyyyMMdd.format(date);
					// + fd.substring(fd.indexOf(" "));
					fdhr = fd;
				} catch (Exception e) {
				}

				String ld = c.getString(column4);
				String ldhr = c.getString(column4); // May 20 2016 10:10AM
				// ......kk
				if (fd.equals("")) {
					sr.setfirstdate("No info");

				} else {
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
//			sql.close();
//			db.close();
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
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM, yyyy");
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

		// String k = tf.substring(0, tf.length() - 9);
		// System.out.println("---value of k for f date..." + k);
		// Oct 18 201
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
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
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM, yyyy");
		String finalDate = timeFormat.format(myDate);

		System.out.println("time------" + fromtimetw);

		String[] vfd = { finalDate };

		return vfd;
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
		String k = tf.substring(10, tf.length() - 0);
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
						values.put(columnName, columnValue);
					}

					sql.insert("NonrepeatedAd", null, values);
					Log.d("test", "data :" + values);
				}

				c.close();
				/*sql.close();
				db.close();*/

			} else {
				sop = "invalid";
				System.out.println("--------- invalid for AD list --- ");
				/*sql.close();
				db.close();*/
			}

			return sop;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// pd.cancel();
			if (sop.equals("valid")) {

				updatelist();
			} else {
				showD("nodata");
			}
			iv.setVisibility(View.VISIBLE);
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
					.setVisibility(View.GONE);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
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
		final Dialog myDialog = new Dialog(AdvNonRepStationList.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoginfosmall);
		myDialog.setCancelable(true);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);

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
			txt.setText("No Refresh Data Available. Please check Internet Connection");
		} else if (string.equals("nodata")) {
			myDialog.setTitle(" ");
			txt.setText("No data available");
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

}
