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

import com.adapters.NonrepeatedAdAdaptMain;
import com.beanclasses.NonrepeatedAdHelper;
import com.beanclasses.NonreportedList;
import com.database.DBInterface;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NonrepeatedAdMain extends Activity {
	NonrepeatedAdAdaptMain listAdapter;
	ListView workspacewisedetail;
	String mobno, link;
	// ArrayList<String>arrlist=new ArrayList<String>();
	List<NonreportedList> lst_nonrpt = new ArrayList<NonreportedList>();
	AsyncTask depattask;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop = "no";
	ArrayList<String> projectlist = new ArrayList<String>();
	String spparam = "Employee Reporting to Me";
	ImageView iv, btnadd;
	static SimpleDateFormat dff;
	static String Ldate;
	static DownloadxmlsDataURL_new asyncfetch_non;
	String responsemsg = "k";
	String ActivityName, ActivityId, actname;
	private TextView txtdate;
	private TextView txtdaterefresh;
	String daterestr;
    private ListView nonrepeated;
    private String st,Type;
    private String cou;
    private String sv;
	String z = "";
    private String z1, subType, CallFrom;
	private int nonreportadCount = 0;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.nonrepeatedadmain);

		iv = (ImageView) findViewById(R.id.button_refresh_nonrepeated_main);
		btnadd = (ImageView) findViewById(R.id.button_alert_add);
		btnadd.setImageResource(R.drawable.work_assign);
		btnadd.setVisibility(View.VISIBLE);
		nonrepeated = (ListView) findViewById(R.id.nonrepeatedadmain);

		try{
			Bundle extras = getIntent().getExtras();

			Type = extras.getString("Type");
			subType = extras.getString("SubType");
			CallFrom = extras.getString("CallFrom");
		}catch (Exception e){
			e.printStackTrace();
		}

		((TextView) findViewById(R.id.onactivitynamereassign)).setText("Non Reported Advertisements - "+ subType);

		db = new DatabaseHandler(getApplicationContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		if (asyncfetch_non != null
				&& asyncfetch_non.getStatus() == AsyncTask.Status.RUNNING) {
			Log.e("async", "running");
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}

		if (dbvalue()) {
			updatelist();
		} else if (isnet()) {
			fetchdata();
		} else {
			showD("nonet");
		}

		((EditText) findViewById(R.id.edfitertext))
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

						listAdapter
								.filter(((EditText) findViewById(R.id.edfitertext))
										.getText().toString().trim()
										.toLowerCase(Locale.getDefault()));
					}
				});

		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (isnet()) {
					asyncfetch_non = null;
					asyncfetch_non = new DownloadxmlsDataURL_new();
					asyncfetch_non.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

					// fetchdata();
				} else {
					showD("nonet");
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

		try{
			if(CallFrom.equalsIgnoreCase("SupporterList")){
				nonrepeated.setClickable(false);
			}else if(CallFrom.equalsIgnoreCase("NonrepeatedAdFilter")){
				nonrepeated.setClickable(true);
				nonrepeated.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> a, View v, int position,
											long id) {
						if (stationpresent()) {

							NonreportedList item = lst_nonrpt.get(position);
							Bundle dataBundle = new Bundle();
							dataBundle.putString("stnname", item.getInstallationDesc());
							Intent myIntent = new Intent();
							myIntent.setClass(getApplicationContext(), com.stavigilmonitoring.NonrepeatedAd.class);
							myIntent.putExtras(dataBundle);
							startActivity(myIntent);

						} else {
							Toast.makeText(getBaseContext(),
									"No Advertisement Present..", Toast.LENGTH_LONG)
									.show();
						}
					}
				});
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private boolean isnet() {
		Context context = this.getApplicationContext();
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public void editActivity(String StationName) {
		int i = 0;
		for (i = 1; i <= StationName.length(); i++) {
			char k = StationName.charAt(i - 1);
			sv = String.valueOf(k);
			System.out.println("...................value of sv is" + sv);
			if (sv.equals("\n")) {
				break;
			} else {
				z = z + sv;
				z1 = z;
			}
		}
		//DatabaseHandler db1 = new DatabaseHandler(this);
		SQLiteDatabase sqldb = db.getWritableDatabase();
		String[] params = new String[1];
		params[0] = z1;
		Cursor c2 = sqldb.rawQuery(
						"SELECT * FROM NonrepeatedAd where InstallationDesc='"+params+"'",null);
		String stnname = "";

		if (c2.getCount() == 0) {
			c2.close();
		} else {
			c2.moveToFirst();
			stnname = c2.getString(c2.getColumnIndex("InstallationDesc"));
			c2.moveToLast();
			c2.close();
		}

		Bundle dataBundle = new Bundle();
		dataBundle.putString("stnname", stnname);
		Intent myIntent = new Intent();
		myIntent.setClass(getApplicationContext(), NonrepeatedAd.class);

		myIntent.putExtras(dataBundle);
		startActivity(myIntent);

	}

	protected boolean stationpresent() {
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql.rawQuery("SELECT * FROM NonrepeatedAd", null);

		int count = c.getCount();

		c.close();
		if (count == 0) {
			return false;
		} else {
			return true;
		}

	}

	private boolean dbvalue() {
		try {
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

	public void FilterClick(View v) {
		if (((EditText) findViewById(R.id.edfitertext)).getVisibility() == View.VISIBLE) {
			((EditText) findViewById(R.id.edfitertext))
					.setVisibility(View.GONE);
		} else if (((EditText) findViewById(R.id.edfitertext)).getVisibility() == View.GONE) {
			((EditText) findViewById(R.id.edfitertext))
					.setVisibility(View.VISIBLE);
			EditText textView = (EditText) findViewById(R.id.edfitertext);
			textView.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
		}

	}

	private void updatelist() {
		final List<NonreportedList> searchResults = getDetail();

		Log.e("non reported.......", "count : " + searchResults.size());
		// listAdapter=null;
		if(searchResults.isEmpty()){
			//show message
			showD("nodata");
		}else {
			listAdapter = new NonrepeatedAdAdaptMain(this, searchResults);
			nonrepeated.setAdapter(listAdapter);
		}

	}

	private static class MySpinnerAdapter extends ArrayAdapter<String> {
		// Initialise custom font, for example:
		Typeface font = Typeface.createFromAsset(getContext().getAssets(),
				"font/BOOKOS.TTF");

		private MySpinnerAdapter(Context context, int resource,
				List<String> items) {
			super(context, resource, items);
		}

		// Affects default (closed) state of the spinner
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView view = (TextView) super.getView(position, convertView,
					parent);
			view.setTypeface(font);
			return view;
		}

		// Affects opened state of the spinner
		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			TextView view = (TextView) super.getDropDownView(position,
					convertView, parent);
			view.setTypeface(font);
			return view;
		}
	}

	private List<NonreportedList> getDetail() {

		lst_nonrpt.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		// SELECT DISTINCT InstallationDesc, InstallationCount FROM
		// NonrepeatedAd
		/*Cursor c = sql
				.rawQuery(
						"SELECT * FROM NonrepeatedAd a INNER JOIN ConnectionStatusFilter b ON a.StationMasterId=b.InstalationId WHERE b.SubNetworkCode='"
								+ subType + "' ORDER BY a.InstallationDesc",
						null);*/

		Cursor c = sql
				.rawQuery(
						"SELECT * FROM NonrepeatedAd a INNER JOIN ConnectionStatusFilter b ON a.StationMasterId=b.InstalationId WHERE b.SubNetworkCode='"
								+ subType + "' ORDER BY a.LastServerTime",
						null);

		if (c.getCount() == 0) {
			lst_nonrpt.clear();
			c.close();

			return lst_nonrpt;
		} else {
			Log.e("get details.....", "cursor stn count : " + c.getCount());
			c.moveToFirst();

			int column = 0;
			do {
				st = c.getString(c.getColumnIndex("InstallationDesc"));
				cou = c.getString(c.getColumnIndex("InstallationCount"));
				//String adds = st + "\n" + cou;

				if (lst_nonrpt.size() == 0) {
					Log.e("k ins cnt",
							" "
									+ c.getString(c
											.getColumnIndex("InstallationCount")));

					lst_nonrpt.add(new NonreportedList(c.getString(c
							.getColumnIndex("InstallationDesc")), c.getString(c
							.getColumnIndex("InstallationCount")), c
							.getString(c.getColumnIndex("LastServerTime"))));

					if (!c.getString(c.getColumnIndex("InstallationCount"))
							.trim().equalsIgnoreCase(""))
						nonreportadCount = nonreportadCount
								+ Integer.parseInt(c.getString(c
										.getColumnIndex("InstallationCount")));

					Log.e("get details.....",
							"add STn : "
									+ c.getString(c
											.getColumnIndex("InstallationDesc")));
				} else {
					int dflag = 0;
					for (int j = 0; j < lst_nonrpt.size(); j++) {
						if (lst_nonrpt
								.get(j)
								.getInstallationDesc()
								.equalsIgnoreCase(
										c.getString(c
												.getColumnIndex("InstallationDesc")))) {
							dflag = 1;
							break;
						}
					}
					if (dflag == 0) {
						lst_nonrpt
								.add(new NonreportedList(
										c.getString(c
												.getColumnIndex("InstallationDesc")),
										c.getString(c
												.getColumnIndex("InstallationCount")),
										c.getString(c
												.getColumnIndex("LastServerTime"))));
						Log.e("get details.....",
								"add STn : "
										+ c.getString(c
												.getColumnIndex("InstallationDesc")));

						nonreportadCount = nonreportadCount
								+ Integer.parseInt(c.getString(c
										.getColumnIndex("InstallationCount")));
					}
				}

			} while (c.moveToNext());

			c.close();
		}

		Log.e("get details.....", "lst stn count : " + lst_nonrpt.size());

		return lst_nonrpt;
	}

	private ArrayList<NonrepeatedAdHelper> GetDetail() {
		ArrayList<NonrepeatedAdHelper> results = new ArrayList<NonrepeatedAdHelper>();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql.rawQuery("SELECT * FROM NonrepeatedAd", null);
		if (c.getCount() == 0) {
			NonrepeatedAdHelper sr = new NonrepeatedAdHelper();
			sr.setadvcode("");
			sr.setadvName("");
			results.add(sr);
			c.close();

			return results;
		} else {

			c.moveToFirst();

			int column = 0;
			do {

				NonrepeatedAdHelper sr = new NonrepeatedAdHelper();
				String s = sr.setadvcode(c.getString(c
						.getColumnIndex("InstallationDesc")));

				String n1 = findCount(s);

			} while (c.moveToNext());

			c.close();
		}
		return results;

	}

	private String findCount(String s) {
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		String[] params = new String[1];
		params[0] = s;
		Cursor c2 = sql
				.rawQuery(
						"SELECT * FROM NonrepeatedAd where InstallationDesc='"+params+"'",null);
		int count = c2.getCount();

		String n = String.valueOf(count);
		c2.close();
		return n;
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

	private String calculatedifftime(String datedb) {
		System.out.println("date db......................" + datedb);
		// TODO Auto-generated method stub
		long diffInMillisec = 0;
		long diffInDays = 0;
		long diff = 0;
		try {
			// Create two calendars instances

			// System.out.println("---##### calculatediff 0 " + datedb);
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm ");
			System.out.println("---##### sdf 0 " + sdf);

			Date datestop = sdf.parse(datedb);
			System.out.println("---value of datestop...." + datestop);
			diff = date.getTime() - datestop.getTime();

			// diffInDays = diff / (24 * 60 * 60 * 1000);

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

		// if (diffInDays == 0) {
		// return "Today";
		//
		// } else if (diffInDays == 1) {
		// return "Yesterday";
		// } else {
		// return datedb;
		// }
		String s = String.valueOf(diff);
		return s;

	}

	private String[] splitfrom(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...." + tf);
		String fromtimetw = "";
		String k = tf.substring(tf.indexOf(" "));
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
		String[] v1 = { finalDate };

		return v1;
	}

	private String[] splittodate(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...." + tf);
		String fromtimetw = "";
		String k = tf.substring(0, tf.indexOf(" "));
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
		asyncfetch_non = new DownloadxmlsDataURL_new();
		asyncfetch_non.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String xx = "";

			// String
			// url="http://vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetNonReportedAdvt_Android_new?Mobile="+mobno;
			String url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetNonReportedAdvt_Android_new?Mobile="
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

					Cursor c = sql.rawQuery("SELECT *  FROM NonrepeatedAd",
							null);

					ContentValues values = new ContentValues();
					NodeList nl = ut.getnode(responsemsg, "Table1");
					String msg = "";
					String columnName, columnValue;
					Log.e("non reported..",
							"...count : " + nl.getLength());

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
						Log.d("test", "data NonrepeatedAd:" + values);
					}

					c.close();

				} else if (responsemsg.contains("<NewDataSet/>")) {
					//DatabaseHandler db = new DatabaseHandler(getBaseContext());
					SQLiteDatabase sql = db.getWritableDatabase();
					//sql.execSQL("DROP TABLE IF EXISTS NonrepeatedAd");
					//sql.execSQL(ut.getNonrepeatedAd());
					sql.delete("NonrepeatedAd",null,null);

					sop = "valid";
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
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				if (sop.equals("valid")) {
					updatelist();
				} else {
					showD("nodata");
				}
				iv.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(R.id.progressBar1))
						.setVisibility(View.GONE);
			} catch (Exception e) {
				e.printStackTrace();
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

		final Dialog myDialog = new Dialog(NonrepeatedAdMain.this);
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
			txt.setText("No Refresh Data Available.Please check internet connection...");
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

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences preferences = getApplicationContext()
				.getSharedPreferences("SCROLLNON", Context.MODE_PRIVATE);
		int scroll = preferences.getInt("ScrollValueNon", 0);
		System.out
				.println(".............value of scroll at resume>>>>>>>>>>>>>>>>>>>"
						+ scroll);
		// connectionstatus.scrollTo(0, scroll);
		nonrepeated.smoothScrollToPosition(scroll);
	}

	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SharedPreferences preferences = getApplicationContext()
				.getSharedPreferences("SCROLLNON", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		int scroll = nonrepeated.getFirstVisiblePosition();
		// int scrollx = connectionstatus.getScrollX();
		System.out.println(".............value of scroll>>>>>>>>>>>>>>>>>>>"
				+ scroll);
		editor.putInt("ScrollValueNon", scroll);
		editor.commit();
		// finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
	}

}
