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

import com.adapters.SuspectedHistoryAdapt;
import com.beanclasses.SuspectedHelper;
import com.database.DBInterface;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SuspectedHistory extends Activity {
	private static final String Days = null;
	private static final String Hours = null;
	private static final String Minutes = null;
	private static final String Seconds = null;
	ProgressDialog pd;
	ListView workspacewisedetail;
	String mobno, link;
	AsyncTask depattask;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop = "no";
	ArrayList<String> projectlist = new ArrayList<String>();
	String spparam = "Employee Reporting to Me";
	ImageView iv;
	static SimpleDateFormat dff;
	static String Ldate;
	String responsemsg = "k";
	String ActivityName, ActivityId, actname;
	private TextView txtdate;
	private TextView txtdaterefresh;
	String daterestr;
	private ListView connectionstatus;
	private ListView nonrepeated;
	private String stnnAme;
	private TextView downtimestnnsme;
	private ListView suspectedHistory;
	private TextView susstnname;
	private String advName;
	private String advCode;
	private String Total="";
	private TextView setadvcode;
	private TextView setadvname;
	DatabaseHandler db;
	ArrayList<SuspectedHelper> searchResults;
	String[] advnames;
	String[] Total_Count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.suspectedhistory);
		susstnname = (TextView) findViewById(R.id.tvsuspectedhistorystnname);
		setadvcode = (TextView) findViewById(R.id.tvsushisadvcode);
		setadvname = (TextView) findViewById(R.id.tvsushisadvname);
		Bundle extras = getIntent().getExtras();
		advName = extras.getString("Advertisementname");
		stnnAme = extras.getString("Stationname");
		System.out.println("................stnname on sh" + stnnAme);
		advCode = extras.getString("advCodes");
		Total = extras.getString("Total");
		susstnname.setText(stnnAme+" Advertisements");
		iv = (ImageView) findViewById(R.id.button_refresh_suspectedhistory);
		suspectedHistory = (ListView) findViewById(R.id.suspectehistoryddetail);

		db = new DatabaseHandler(getBaseContext());

		searchResults = new ArrayList<SuspectedHelper>();

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();

		advnames = advName.split(",");
		//Total_Count = Total.split(",");

		for(int i=0; i<advnames.length;i++){
			SuspectedHelper sus = new SuspectedHelper();
			sus.setAdvertisementName(advnames[i]);
			//sus.setTotalSpot(Total_Count[i]);

			searchResults.add(sus);
		}

		suspectedHistory.setAdapter(new SuspectedHistoryAdapt(this, searchResults));
	}

	private void updatelist() {
		// TODO Auto-generated method stub

		searchResults = GetDetail();

		suspectedHistory.setAdapter(new SuspectedHistoryAdapt(this, searchResults));
	}

	private ArrayList<SuspectedHelper> GetDetail() {
		ArrayList<SuspectedHelper> results = new ArrayList<SuspectedHelper>();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		String[] params = { advCode, stnnAme };
		// String[] params = new String[1];
		// params[0] = advCode;
		// System.out.println("adv code..........."+advCode);
		// String[] params1 = new String[1];
		// params1[0] = stnnAme;
		System.out.println("adv name..........." + stnnAme);
		//Cursor c = sql.rawQuery("SELECT * FROM SuspectedHistory where AdvertisementCode= ? AND InstallationDesc= ?", null);
		Cursor c = sql.rawQuery("SELECT * FROM Suspected where InstalationId='"+""+"'", null);
		if (c.getCount() == 0) {
			SuspectedHelper sr = new SuspectedHelper();
			// sr.setstnname("");
			sr.setdates("");
			sr.setdateday("");
			sr.settimes("");

			// sr.setStartEnd("");
			// sr.setRemarks("");

			results.add(sr);

			c.close();
			sql.close();
			//db.close();

			return results;
		} else {

			c.moveToFirst();

			int column = 0;
			do {

				SuspectedHelper sr = new SuspectedHelper();

				// column = c.getColumnIndex("UserName");

				// sr.setstnname(c.getString(c.getColumnIndex("InstallationDesc")));
				// sr.setcurrent(c.getString(c.getColumnIndex("AddedDate")));
				int column1 = c.getColumnIndex("ScheduleTime");
				String tf = c.getString(column1);
				String tfday = c.getString(column1);
				String tftym = c.getString(column1);
				String v1 = splittime(tf);
				String[] v1day = splitfromday(tfday);
				String[] v2tym = splitfrom(tftym);
				sr.setdateday(v1day[0]);
				sr.setdates(v1);
				// sr.setdateDay(v1day[0]);
				sr.settimes(v2tym[0]);
				// int column2 = c.getColumnIndex("StationDownTime");
				// String tfsd = c.getString(column2);
				// String[] v2 = splitfromsd(tfsd);
				// sr.setlastseven(v2[0]);
				// sr.setlastseven(c.getString(c.getColumnIndex("StationDownTime")));

				// int column2 = c.getColumnIndex("StationDownTime");
				// String tfy = c.getString(column2);
				// String[] v2 = splitfromtohr(tfy);
				// //sr.setlastseven(v2[0]);
				results.add(sr);

			} while (c.moveToNext());

			c.close();
			sql.close();
			//db.close();
		}
		return results;

	}

	private String[] splitfrom(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...." + tf);
		String fromtimetw = "";
		String k = tf.substring(9, tf.length() - 0);
		System.out.println("---value of k for time..." + k);
		// String m = k.replace("T", " From ");
		// System.out.println("---value of m..."+m);
		// String[] n = m.split(" From");

		// System.out.println("--------n[1]" + n[1].trim());
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
		// Date myDate = null;
		// try {
		// myDate = dateFormat.parse(v);
		//
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }
		//
		// SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM yyyy");
		// String finalDate = timeFormat.format(myDate);
		//
		// System.out.println("--------------date-----"+finalDate);
		//
		// fromtimetw=m.substring(15, tf.length() - 8);

		System.out.println("time------" + fromtimetw);
		String[] v1 = { k };
		// String[] v2={ fromtimetw };

		return v1;
	}

	private String[] splitfromtohr(String tfy) {
		// TODO Auto-generated method stub
		String newv = "";
		String newvv = "";
		String kfy = tfy;
		if (kfy.contains(".")) {
			newv = kfy.replace(".", ":");

			if (newv.startsWith(":")) {
				newvv = "00" + newv;
			}

		}
		String s = "00:48";
		SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm ");
		SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm ");
		String finalv = parseFormat.format(s);
		System.out.println("..................finav" + finalv);

		return null;
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

		if (diffInDays == 0) {
			return "Today";

		} else if (diffInDays == 1) {
			return "Yesterday";
		} else {
			return datedb;
		}

	}

	private String splittime(String tf) {
		// TODO Auto-generated method stub

		long diffDays = 0;
		System.out.println("---value of tf for date...." + tf);
		String fromtimetw = "";

		String k = tf.substring(0, tf.length() - 11);
		System.out.println("---value of k for date..." + k);
		// String m = k.replace("T", " From ");
		// System.out.println("---value of m..."+m);
		// String[] n = k;
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
		// //
		// STRING V = K[0].TRIM();
		//
		// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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

		final String dateStart = finalDate;
		DateFormat dateFormat1 = new SimpleDateFormat("dd MMM, yyyy");
		Date date = new Date();
		System.out.println("date format of system......................"
				+ dateFormat1.format(date));
		System.out.println("date format of web tym......................"
				+ date);
		final String dateStop = dateFormat1.format(date);

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

	private String[] splitfromday(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf down...." + tf);
		String fromtimetw = "";
		String k = tf.substring(0, tf.length() - 11);
		System.out.println("---value of kdown..." + k);
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
		SimpleDateFormat timeFormat = new SimpleDateFormat("EEE");
		String finalDate = timeFormat.format(myDate);

		System.out.println("time day------" + finalDate);
		// String[] v1 = { finalDate };
		String[] v1day = { finalDate };

		return v1day;
	}

	private String[] splitfromsd(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tfsd down...." + tf);
		String s = tf + "Hours";
		System.out.println("---value of tfsd down1...." + s);
		String[] v2 = { s };
		return v2;
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

		String v = n[0].trim();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(v);

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

		String v = n[0].trim();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(v);

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

		String v = n[0].trim();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(v);

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

		System.out.println("--------------date-----" + finalDate);

		fromtimetw = m.substring(15, tf.length() - 4);

		System.out.println("time------" + fromtimetw);
		String[] v1 = { finalDate };
		String[] v1time = { fromtimetw };

		return v1time;
	}

	private void fetchdata() {
		// TODO Auto-generated method stub

		pd = ProgressDialog.show(SuspectedHistory.this,
				"Fetching Data from Server..", "Please Wait....", true, true,
				new OnCancelListener() {

					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						if (depattask != null
								&& depattask.getStatus() != AsyncTask.Status.FINISHED) {
							depattask.cancel(true);
						}
					}
				});

		depattask = new DownloadxmlsDataURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	public class DownloadxmlsDataURL extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			// try {
			// DatabaseHandler db = new DatabaseHandler(getBaseContext());
			// SQLiteDatabase sql = db.getWritableDatabase();
			//
			String xx = "";

			// String url;

			// http://sta.vritti.co/vrittiportal/webservice/DepartmentwiseActWebService.asmx/FillOldGrid?Mobileno=9922708394&DeptId=Report&DeptName=Employee%20Reporting%20to%20Me
			// url =
			// "http://intranet.vritti.co/VWBTest/webservice/ActivityWebService.asmx/WorkspaceWiseActivities?"
			// + "Mobileno=" + mobno;
			// String
			// url="http://sta.vritti.co/imedia/WdbIntMgmtNew.asmx/GetNonReportedAdvt_Android?UserName="+link+"&Mobile="+mobno;
			String url = "http://sta.vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetDataFromAdvertisementCode_Android?Mobile="
					+ mobno + "&AdvertisementCode=" + advCode;
			url = url.replaceAll(" ", "%20");

			System.out.println("============ internet reg url " + url);

			try {
				System.out.println("-------  activity url --- " + url);
				responsemsg = ut.httpGet(url);

				System.out.println("-------------  xx vale of non repeated-- "
						+ responsemsg);

				//
				if (responsemsg.contains("<ScheduleTime>")) {
					sop = "valid";
					//DatabaseHandler db = new DatabaseHandler(getBaseContext());
					System.out.println("------------- 1-- ");
					SQLiteDatabase sql = db.getWritableDatabase();
					System.out.println("------------- 2-- ");
					//sql.execSQL("DROP TABLE IF EXISTS SuspectedHistory");
					System.out.println("------------- 3-- ");
					//sql.execSQL(ut.getSuspectedHistory());
					sql.delete("SuspectedHistory",null,null);

					System.out.println("------------- 4-- ");
					System.out.println("------------- 5-- ");

					Cursor c = sql.rawQuery("SELECT *   FROM SuspectedHistory",
							null);
					System.out.println("------------- 6-- ");
					ContentValues values = new ContentValues();
					System.out.println("------------- 7-- ");
					NodeList nl = ut.getnode(responsemsg, "Table1");
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

							values.put(columnName, columnValue);

						}

						sql.insert("SuspectedHistory", null, values);

					}
					System.out.println("insetr data");
					c.close();
					sql.close();
					//db.close();

				} else {
					sop = "invalid";
					System.out.println("--------- invalid for AD list --- ");
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

				responsemsg = "wrong" + e.toString();
				System.out
						.println("--------- invalid for message type list --- "
								+ responsemsg);
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
				pd.cancel();
				System.out.println("...............value of sop" + sop);
				if (sop.equals("valid")) {

					updatelist();

				} else {

					showD("invalid");
				}

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
		//db.close();

		filldaterefresh();

	}

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(SuspectedHistory.this);
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
			txt.setText("No Data Available for Suspected History...");
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

		Bundle dataBundle = new Bundle();
		// dataBundle.putString("Advertisementname", advName);
		System.out.println("............on bp of sh" + stnnAme);
		dataBundle.putString("stnname", stnnAme);
		// dataBundle.putString("advCodes", advCode);
		Intent i = new Intent(SuspectedHistory.this, Suspected.class);
		// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtras(dataBundle);
		startActivity(i);
		// finish();

	}

}
