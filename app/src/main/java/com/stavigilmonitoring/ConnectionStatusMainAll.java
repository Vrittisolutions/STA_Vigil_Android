package com.stavigilmonitoring;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.ConnectionstatusAdaptMain;
import com.beanclasses.ConnectionstatusHelper;
import com.database.DBInterface;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ConnectionStatusMainAll extends Activity{

	private static final String Days = null;
	private static final String Hours = null;
	private static final String Minutes = null;
	private static final String Seconds = null;
	static DownloadxmlsDataURL_new asyncfetch;
	ProgressDialog pd;
	ListView workspacewisedetail;
	String mobno, link;
	AsyncTask depattask;
	// ExpandableListAdapter listAdapter;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop = "no";
	ArrayList<String> projectlist = new ArrayList<String>();
	String spparam = "Employee Reporting to Me";
	ImageView iv,btnadd;
	String countConn = "";
	String responsemsg = "k";
	String ActivityName, ActivityId, actname;
	private TextView txtdate;
	private TextView txtdaterefresh;
	String daterestr;
	private ListView connectionstatus;
	private ExpandableListView expListView;
	HashMap<String, List<String>> listDataChild;
	List<String> listDataHeader;
	private String stnnAME;
	private TextView csnstatus;
	public static final String MyPREFERENCES = "MyPrefs";
	public static final String Name = "csnStatus";
	SharedPreferences sharedpreferences;
	private int icount;
	static SimpleDateFormat dff;
	static String Ldate;
	private String scount, Type,subType,Flag, CallFrom;
	private Date date;
	Context parent;
	private String datestring = "";
	private SharedPreferences prefDate;
	ArrayList<ConnectionstatusHelper> searchResults;
	Animation anim;
	ConnectionstatusAdaptMain listAdapter;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.connectionstatusmain_dtls);
		parent = ConnectionStatusMainAll.this;
		Intent intent = getIntent();
		// Bundle extras = getIntent().getExtras();
		Type = intent.getStringExtra("Type");
		subType = intent.getStringExtra("subType");
		Flag = intent.getStringExtra("NoSubnetWork");
		CallFrom = intent.getStringExtra("CallFrom");

		((TextView) findViewById(com.stavigilmonitoring.R.id.onactivitynamereassign)).setText("Connection Status - "+Type );
		iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_connection_main);

		anim = AnimationUtils.loadAnimation(this, com.stavigilmonitoring.R.anim.rotation);
		// iv.setAnimation(anim);
		btnadd = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_alert_add);
		btnadd.setImageResource(com.stavigilmonitoring.R.drawable.work_assign);
		btnadd.setVisibility(View.VISIBLE);

		connectionstatus =  findViewById(com.stavigilmonitoring.R.id.connectionstatusdetailmain);

		db = new DatabaseHandler(getApplicationContext());

		DBInterface dbi = new DBInterface(parent);
		mobno = dbi.GetPhno();

		dbi.Close();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		if (asyncfetch != null
				&& asyncfetch.getStatus() == AsyncTask.Status.RUNNING) {
			Log.e("async", "running");
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}
		ut.getConnectionStatusUser1();
		ut.getConnectionStatusUser();

		if (dbvalue()) {
			updatelist();
			// prepareListData();
		} else if (ut.isnet(parent)) {

			fetchdata();
		} else {
			ut.showD(parent,"nonet");
		}

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

						listAdapter
								.filter(((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
										.getText().toString().trim()
										.toLowerCase(Locale.getDefault()));
					}
				});

		// } else {
		// //showD("nonet");
		// System.out.println("no internet available..........");
		// }

		System.out.println("----------total number of item in list"
				+ connectionstatus.getCount());
		//
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (ut.isnet(parent)) {
					asyncfetch = null;
					asyncfetch = new DownloadxmlsDataURL_new();
					asyncfetch.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					// fetchdata();
				} else {
					try{
						ut.showD(parent,"nonet");
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
				Intent intent = new Intent(parent,WorkAssign_AssignActivity.class);
				intent.putExtra("Activity", "ConnectionStatusStatewise");
				intent.putExtra("Type", "");
				startActivity(intent);
			//	finish();
			}
		});

		if(CallFrom.equalsIgnoreCase("SupporterListAll")){
			//Toast.makeText(parent,"Not clickable",Toast.LENGTH_SHORT).show();
			connectionstatus.setClickable(false);
		}else if(CallFrom.equalsIgnoreCase("ConnectionStatusStateFilter")) {
			connectionstatus.setClickable(true);
			connectionstatus.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> a, View v, int position,
										long id) {

					if (stationpresent()) {

						String s = searchResults.get(position).getinstallationId();
						Bundle dataBundle = new Bundle();
						dataBundle.putString("stnname", s);
						dataBundle.putString("frompage", "connStatusAll");
						dataBundle.putString("Type", Type);
						dataBundle.putString("SubType", subType);
						/*
						 * Intent myIntent = new Intent();
						 * myIntent.setClass(parent,
						 * ConnectionStatus.class);
						 *
						 *
						 * startActivity(myIntent); finish();
						 */
						Intent i = new Intent(parent, com.stavigilmonitoring.ConnectionStatus.class);
						i.putExtras(dataBundle);
						startActivity(i);
						finish();

						/*
						 * Object o = connectionstatus.getItemAtPosition(position);
						 * ConnectionstatusHelper fullObject =
						 * (ConnectionstatusHelper) o;
						 * editActivity(fullObject.getinstallationId());
						 */

					} else {

						Toast.makeText(parent, "No Station Present..",
								Toast.LENGTH_LONG).show();
					}

				}
			});

		}

		// filldaterefresh();
		//
		//

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

	public void editActivity(String StationName) {

		System.out.println("==========@#@# actid " + ActivityId);
		//DatabaseHandler db1 = new DatabaseHandler(this);
		SQLiteDatabase sqldb = db.getWritableDatabase();
		String[] params = new String[1];
		params[0] = StationName;
		Cursor c2 = sqldb.rawQuery(
				"SELECT * FROM ConnectionStatusUser where InstallationDesc=? ",
				params);
		String stnname = "";

		if (c2.getCount() == 0) {
			c2.close();
			/*db.close();
			db1.close();*/

		} else {

			c2.moveToFirst();
			stnname = c2.getString(c2.getColumnIndex("InstallationDesc"));

			c2.moveToLast();

			c2.close();
			/*db.close();
			db1.close();*/

		}

		Bundle dataBundle = new Bundle();
		dataBundle.putString("stnname", stnname);

		/*
		 * Intent myIntent = new Intent();
		 * myIntent.setClass(parent, ConnectionStatus.class);
		 * 
		 * 
		 * startActivity(myIntent); finish();
		 */
		Intent i = new Intent(parent, ConnectionStatus.class);
		i.putExtras(dataBundle);
		startActivity(i);

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

	private boolean dbvalue() {
		// TODO Auto-generated method stub
		try {
			//DatabaseHandler db1 = new DatabaseHandler(parent);
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor1 = sql.rawQuery(
					"SELECT * FROM ConnectionStatusUser1", null);
			if (cursor1 != null && cursor1.getCount() > 0) {
				Cursor cursor = sql.rawQuery(
						"SELECT * FROM ConnectionStatusUser", null);

				System.out
						.println("----------  dbvalue screen cursor count -- "
								+ cursor.getCount());

				if (cursor != null && cursor.getCount() > 0) {

					// do your action
					// Fetch your data

					cursor.close();
					/*sql.close();
					db1.close();*/
					cursor1.close();
					return true;

				} else {

					cursor.close();
					//sql.close();
					cursor1.close();
					//db1.close();
					return false;
				}

			} else {
				cursor1.close();
				/*sql.close();
				db1.close();*/
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

	private void updatelist() {
		// TODO Auto-generated method stub
		// startService(new Intent(getBaseContext(),SynchDtataCount.class));
		ut.getConnectionStatusUser1();
		ut.getConnectionStatusUser();

		searchResults = GetDetail();
		ConnectionstatusHelper sr = new ConnectionstatusHelper();
		listAdapter = null;
		listAdapter = new ConnectionstatusAdaptMain(this, searchResults, "");
		connectionstatus.setAdapter(listAdapter);
	}

	//
	private ArrayList<ConnectionstatusHelper> GetDetail() {
		ArrayList<ConnectionstatusHelper> results = new ArrayList<ConnectionstatusHelper>();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql
				.rawQuery(
						"SELECT DISTINCT s.InstallationId,ServerTime,Remarks,Last7DaysPerFormance, QuickHealStatus, STAVersion, LatestDowntimeReason,InstallationDesc FROM ConnectionStatusUser s "
								+ " INNER JOIN ConnectionStatusUser1 s1 ON s.InstallationId=s1.InstallationId where s.Type='"
								+ Type + "' ORDER BY ServerTime ", null);
		if (c.getCount() == 0) {
			ConnectionstatusHelper sr = new ConnectionstatusHelper();

			sr.setinstallationId("");
			//
			sr.settymdiff("");
			//
			sr.setreason("");
			results.add(sr);

			c.close();
			/*sql.close();
			db.close();*/

			return results;
		} else {
			c.moveToFirst();
			String[] sa = c.getColumnNames();
			// Log.e("getd","desc: "+c.getString(c.getColumnIndex("InstallationDesc"))+" servertime: "+c.getString(c.getColumnIndex("ServerTime")));
			int column = 0;
			do {

				ConnectionstatusHelper sr = new ConnectionstatusHelper();

				// column = c.getColumnIndex("UserName");
				/*
				 * int column1 = c.getColumnIndex("ServerTime"); String tf =
				 * c.getString(column1); String tftym = c.getString(column1);
				 * String[] tym = splitfromtym(tftym);
				 * 
				 * String[] v1 = splitfrom(tf); String[] v2 = splittime(tf);
				 * System.out.println("----value of v1"+v1);
				 * if(tym[0].equals("")) {
				 * 
				 * } else { String
				 * s=sr.setinstallationId(c.getString(c.getColumnIndex
				 * ("InstallationDesc"))); sr.settymdiff(tym[0]);
				 * sr.setreason(c.
				 * getString(c.getColumnIndex("LatestDowntimeReason")));
				 * results.add(sr); }
				 */

				int column1 = c.getColumnIndex("ServerTime");
				// int column1 = c.getColumnIndex("EndTime");
				String[] tym = splitfromtym(c.getString(column1));
				sr.setservertime(c.getString(column1));
				// int column2 = c.getColumnIndex("AscOrderServerTime");

				// int f=printDiff(c.getString(column1));
				try {
					Calendar cal = Calendar.getInstance();
					// SimpleDateFormat format = new
					// SimpleDateFormat("MM/dd/yyyy HH:mm:ss aa");

					SimpleDateFormat format = new SimpleDateFormat(
							"MM/dd/yyyy hh:mm:ss aa", Locale.ENGLISH);

					Date Startdate = format.parse(c.getString(column1));
					Date Enddate = cal.getTime();
					long diff = Enddate.getTime() - Startdate.getTime();
					long diffSeconds = diff / 1000 % 60;
					long diffMinutes = diff / (60 * 1000) % 60;
					long diffHours = diff / (60 * 60 * 1000) % 24;
					long diffDays = diff / (24 * 60 * 60 * 1000);

					// Log.e("getdetails","sd : "+Startdate+" ed: "+Enddate+" d: "+diffDays+" h: "+diffHours+" m:"+diffMinutes);
					/*
					 * Log.e("printdiff.........","diffDays: "+diffDays);
					 * Log.e("printdiff.........","diffHours: "+diffHours);
					 * Log.e("printdiff.........","diffMinutes: "+diffMinutes);
					 * Log.e("printdiff.........","diffSeconds: "+diffSeconds);
					 */
					if (!(Enddate.getTime() - Startdate.getTime() < 0)) {
						if (diffDays == 0 && diffHours == 0
								&& diffMinutes <= 15) {

						} else {
							String s = sr.setinstallationId(c.getString(c
									.getColumnIndex("InstallationDesc")));
							String diffstr = "";
							if (diffDays == 0 && diffHours == 0)
								diffstr = diffMinutes + "Min";
							else if (diffDays == 0)
								diffstr = diffHours + "hr";
							else {
								if (diffDays >= 32) {
									long yc = diffDays / 30;
									if (yc >= 12)
										diffstr = (yc / 12) + " Year";
									else
										diffstr = yc + " Month";
								} else
									diffstr = diffDays + "days ";
							}

							sr.settymdiff(diffstr);
							sr.setreason(c.getString(c
									.getColumnIndex("LatestDowntimeReason")));
							Log.e("get det", " time : " + c.getString(column1));
							results.add(sr);
						}
					}
				} catch (Exception ex) {
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
								+ ex.getMessage() + " " + Ldate);
					}

				}
			} while (c.moveToNext());

			c.close();
			/*sql.close();
			db.close();*/
		}
		Log.e("connection sts main", "cursor res : " + results.size());
		SimpleDateFormat dff = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.ENGLISH);
		try {
			for (int i = 0; i < results.size(); i++) {
				for (int j = i + 1; j < results.size(); j++) {
					Date s1 = dff.parse(results.get(i).getservertime());
					Date s2 = dff.parse(results.get(j).getservertime());
					if (s1.compareTo(s2) > 0) {
						ConnectionstatusHelper ci = results.get(i);
						ConnectionstatusHelper cj = results.get(j);
						results.remove(i);
						results.add(i, cj);

						results.remove(j);
						results.add(j, ci);
					}
				}
			}
		} catch (Exception ex) {
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
						+ l.getLineNumber() + "	" + ex.getMessage() + " "
						+ Ldate);
			}
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
						+ l.getLineNumber() + "	" + ex.getMessage() + " "
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

		} catch (Exception ex) {
			ex.printStackTrace();
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
						+ l.getLineNumber() + "	" + ex.getMessage() + " "
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

		System.out.println("time------" + k);
		String[] v1 = { k };
		// String[] v2={ fromtimetw };
		return v1;
	}

	private String[] splitfromtym(String tym) {
		System.out.println("---value of tym differ...." + tym);
		String fromtimetw = "";
		final String dateStart = tym;
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.ENGLISH);
		Date date = new Date();
		System.out.println("date format of system......................"
				+ dateFormat.format(date));
		System.out
				.println("date format of web tym......................" + tym);

		final String dateStop = dateFormat.format(date);
		Date d1 = null;
		Date d2 = null;
		String diffTym = "";

		try {
			d1 = dateFormat.parse(dateStart);
			d2 = dateFormat.parse(dateStop);
			System.out.println("d2......................" + d2);
			// in milliseconds
			long diff = d2.getTime() - d1.getTime();

			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);
			String days = String.valueOf(diffDays);
			String hours = String.valueOf(diffHours);
			String minutes = String.valueOf(diffMinutes);
			if (days.equals("0")) {
				if (hours.equals("0")) {
					// add code for minutes

					int i = Integer.parseInt(minutes);
					if (i >= 30) {
						// end
						diffTym = diffMinutes + " Minutes ";
					} else {
						diffTym = "";
					}
				}
			} else {
				diffTym = diffDays + " Days ";
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

		String[] s = { diffTym };
		return s;
	}

	private String[] splittime(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf for date...." + tf);
		String fromtimetw = "";

		String k = tf.substring(0, tf.length() - 11);
		System.out.println("---value of k for date..." + k);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
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
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
		String finalDate = timeFormat.format(myDate);

		System.out.println("----------final----date-----" + finalDate);
		//
		// fromtimetw=m.substring(15, tf.length() - 7);

		// System.out.println("time------"+fromtimetw);
		// String[] v1 = { finalDate };
		String[] v2 = { finalDate };

		return v2;
	}

	//

	private void fetchdata() {
		// TODO Auto-generated method stub

		/*
		 * pd = ProgressDialog.show(ConnectionStatusMain.this,
		 * "Fetching Data from Server..", "Please Wait....", true, true, new
		 * OnCancelListener() {
		 * 
		 * public void onCancel(DialogInterface dialog) { // TODO Auto-generated
		 * method stub if (depattask != null && depattask.getStatus() !=
		 * AsyncTask.Status.FINISHED) { depattask.cancel(true); } } });
		 */

		// depattask = new DownloadxmlsDataURL_new().execute();
		// new DownloadxmlsDataURL_new().execute();
		if (asyncfetch == null) {
			iv.setVisibility(View.VISIBLE);
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
					.setVisibility(View.GONE);

			Log.e("async", "null");
			asyncfetch = new DownloadxmlsDataURL_new();
			asyncfetch.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			if (asyncfetch.getStatus() == AsyncTask.Status.RUNNING) {
				Log.e("async", "running");
				iv.setVisibility(View.GONE);
				((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
						.setVisibility(View.VISIBLE);
			}
		}
	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String xx = "";

			String url = "http://sta.vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetAllStationWithUserName_Android?Mobile="
					+ mobno;
			//DatabaseHandler db = new DatabaseHandler(parent);
			SQLiteDatabase sql = db.getWritableDatabase();
			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = ut.httpGet(url);
				Log.e("csn status", "resmsg : " + responsemsg);

				if (responsemsg.contains("<IId>")) {
					sop = "valid";

					//sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusUser1");
					//sql.execSQL(ut.getConnectionStatusUser1());
					sql.delete("ConnectionStatusUser1",null,null);

					Cursor c = sql.rawQuery(
							"SELECT *   FROM ConnectionStatusUser1", null);
					ContentValues values = new ContentValues();
					NodeList nl = ut.getnode(responsemsg, "Table1");
					String msg = "";
					String columnName, columnValue;
					Log.e("sts main...", " fetch data : " + nl.getLength());
					for (int i = 0; i < nl.getLength(); i++) {
						Element e = (Element) nl.item(i);
						for (int j = 0; j < c.getColumnCount(); j++) {
							columnName = c.getColumnName(j);

							String ncolumnname = "";
							if (columnName.equalsIgnoreCase("InstallationId"))
								ncolumnname = "IId";
							else if (columnName
									.equalsIgnoreCase("InstallationDesc"))
								ncolumnname = "SN";
							else if (columnName.equalsIgnoreCase("UserName"))
								ncolumnname = "UN";
							else if (columnName.equalsIgnoreCase("MobileNo"))
								ncolumnname = "UN1";
							else if (columnName.equalsIgnoreCase("SUP"))
								ncolumnname = "SUP";

							columnValue = ut.getValue(e, ncolumnname);
							if(columnValue.contains(" /")){
								columnValue = columnValue.replaceAll(" /","/");
							}

							if (ncolumnname == "SUP"
									&& columnValue.contains(",")) {
								columnValue = columnValue.substring(0,
										columnValue.indexOf(","));
							}
							values.put(columnName, columnValue);
							// Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
						}
						sql.insert("ConnectionStatusUser1", null, values);
					}

					c.close();

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

			// url="http://sta.vritti.co/iMedia/STA_Vigile_AndroidService_Test/WdbIntMgmtNew.asmx/GetCSNStatus_Android_new?Mobile="+mobno;
			url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetCSNStatus_Android_new?Mobile="
					+ mobno;

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = ut.httpGet(url);
				Log.e("csn status", "resmsg : " + responsemsg);

				if (responsemsg.contains("<A>")) {
					sop = "valid";
					String columnName, columnValue;

					sql.delete("ConnectionStatusUser",null,null);

					Cursor cur = sql.rawQuery(
							"SELECT *   FROM ConnectionStatusUser", null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table1");
					// String msg = "";
					// String columnName, columnValue;
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
							// else if (columnName
							// .equalsIgnoreCase("Last7DaysPerFormance"))
							// ncolumnname = "H";
							// else if (columnName
							// .equalsIgnoreCase("QuickHealStatus"))
							// ncolumnname = "I";
							else if (columnName.equalsIgnoreCase("STAVersion"))
								ncolumnname = "J";
							else if (columnName
									.equalsIgnoreCase("AscOrderServerTime"))
								ncolumnname = "K";
							else if (columnName
									.equalsIgnoreCase("LatestDowntimeReason"))
								ncolumnname = "L";
							// else if (columnName.equalsIgnoreCase("UserName"))
							// ncolumnname = "M";
							else if (columnName.equalsIgnoreCase("Type"))
								ncolumnname = "N";
							else if (columnName
									.equalsIgnoreCase("SubNetworkCode"))
								ncolumnname = "R";
							// else if (columnName
							// .equalsIgnoreCase("SupportAgencyName"))
							// ncolumnname = "P";

							columnValue = ut.getValue(e, ncolumnname);

							if (columnName.equalsIgnoreCase("ServerTime")) {
								try {
									Calendar cal = Calendar.getInstance();
									// SimpleDateFormat format = new
									// SimpleDateFormat("MM/dd/yyyy HH:mm:ss aa");

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
									/*
									 * Log.e("printdiff.........","diffDays: "+
									 * diffDays);
									 * Log.e("printdiff.........","diffHours: "
									 * +diffHours);
									 * Log.e("printdiff.........","diffMinutes: "
									 * +diffMinutes);
									 * Log.e("printdiff.........",
									 * "diffSeconds: "+diffSeconds);
									 */

									if (diffDays == 0 && diffHours == 0
											&& diffMinutes <= 15) {

									} else {
										conn = "valid";
									}
								} catch (Exception ex) {
								}
							}

							values1.put(columnName, columnValue);

							// Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
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
			// pd.cancel();
			try {
				System.out
						.println("..DownloadxmlsDataURL_new onpost.............value of sop"
								+ sop);
				if (sop.equals("valid")) {
					updatelist();
				} else {
					ut.showD(parent,"invalid");
				}
				// iv.clearAnimation();
				iv.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
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
			// TODO Auto-generated method stub
			super.onPreExecute();
			/*
			 * pd=new ProgressDialog(ConnectionStatusMain.this);
			 * pd.setTitle("Please Wait....");
			 * pd.setMessage("Fetching Data from Server.."); pd.show();
			 */

			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
					.setVisibility(View.VISIBLE);

		}
	}

	public String getDateTime(String dateInString) {
		// 1/8/2015 4:07:05 PM
		SimpleDateFormat formatter = new SimpleDateFormat(
				"dd-MM-yyyy HH:mm:ss aa", Locale.ENGLISH);
		// String dateInString = "7-Jun-2013";
		Date sdate = new Date();
		try {
			sdate = formatter.parse(dateInString);
			System.out.println(date);

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
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		// Date date = new Date();
		return dateFormat.format(sdate);
	}

	public int printDiff(String Sdate) {
		int flag = 0;
		Log.e("printdiff.........", "-------------------------");
		try {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat(
					"MM/dd/yyyy HH:mm:ss aa", Locale.ENGLISH);

			Date Startdate = format.parse(Sdate);
			Date Enddate = cal.getTime();
			long diff = Enddate.getTime() - Startdate.getTime();
			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);
			Log.e("printdiff.........", "diffDays: " + diffDays);
			Log.e("printdiff.........", "diffHours: " + diffHours);
			Log.e("printdiff.........", "diffMinutes: " + diffMinutes);
			Log.e("printdiff.........", "diffSeconds: " + diffSeconds);

			if (diffDays == 0 && diffMinutes == 0 && diffHours <= 15) {
				flag = 1;
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
		return flag;
	}

	public void getDate1() {
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
		// get current date time with Date()
		date = new Date();
		datestring = dateFormat.format(date);
		System.out.println("value of date is......" + datestring);
		SharedPreferences prefDate = parent
				.getSharedPreferences("MyPrefDate", Context.MODE_PRIVATE); // 0
																			// -
																			// for
																			// private
																			// mode
		Editor editorDate = prefDate.edit();
		editorDate.putString("Dates", datestring);
		editorDate.commit();

	}

	private void prepareListData() {
		// TODO Auto-generated method stub

		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		int c1 = 0;
		// listDataHeader.add("TimeSheet Entry");

		List<String> top250 = new ArrayList<String>();
		List<String> nowshowing = new ArrayList<String>();

		ArrayList<ConnectionstatusHelper> results = new ArrayList<ConnectionstatusHelper>();

		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql.rawQuery("SELECT * FROM ConnectionStatusUser", null);
		//

		// System.out.println("------------ timeshet cursor count --- "
		// + c.getCount());

		if (c.getCount() == 0) {
			listDataHeader.add("No Station Available");

			c.close();
			/*sql.close();
			db.close();*/

		}

		else {

			// c.moveToFirst();

			c.moveToFirst();

			// int column = 0;
			do {

				ConnectionstatusHelper sr = new ConnectionstatusHelper();

				// column = c.getColumnIndex("UserName");

				// sr.setinstallationId(c.getString(c.getColumnIndex("InstallationDesc")));

				int columnStn = c.getColumnIndex("InstallationDesc");
				stnnAME = c.getString(columnStn);
				System.out.println("------------stnsame" + stnnAME);
				listDataHeader.add(stnnAME);

				// Latest
				// int column1 = c.getColumnIndex("ServerTime");
				// String tf = c.getString(column1);
				// String tftym = c.getString(column1);
				// String[] tym = splitfromtym(tftym);
				// String[] v1 = splitfrom(tf);
				// String[] v2 = splittime(tf);
				// System.out.println("----value of v1"+v1);
				// sr.setStartTime(v1 [0]);
				// sr.setservertime(v2[0]);
				// sr.settymdiff(tym[0]);
				// sr.setEndTime(c.getString(c.getColumnIndex("Last7DaysPerFormance")));
				// String dates=v1[0];
				// String time=v2[0];
				// endlatest
				// String diffdate = calculatediff(dates);
				// String difftime = calculatediff(time);
				// System.out.println("-----------diffdatetime............."+
				// tym);

				// sr.setEndTime(tym[0]);
				// sr.setStartEnd(difftime);
				sr.setRemarks(c.getString(c.getColumnIndex("Remarks")));
				// ip address code
				// int column3 = c.getColumnIndex("Remarks");
				// String tf3 = c.getString(column3);
				// String[] v3 = splitIp(tf3);
				// end
				// String q=c.getString(c.getColumnIndex("QuickHealStatus"));
				// String p= c.getString(c.getColumnIndex("STAVersion"));
				//
				// top250.add(p);
				// listDataChild.put(listDataHeader.get(0), top250);
			//	DatabaseHandler db1 = new DatabaseHandler(this);
				SQLiteDatabase db2 = db.getWritableDatabase();
				String[] params = new String[1];
				params[0] = stnnAME;
				Cursor c2 = db2
						.rawQuery(
								"SELECT * FROM ConnectionStatusUser where InstallationDesc=? ",
								params);
				String remarks = "";
				if (c2.getCount() == 0) {
					c2.close();
					/*db1.close();
					db2.close();*/

				} else {

					c2.moveToFirst();
					remarks = c2.getString(c2.getColumnIndex("STAVersion"));
					// remarks = c2.getString(c2.getColumnIndex("SourceType"));
					c2.moveToLast();

					c2.close();
					/*db1.close();
					db2.close();*/

				}

				// String p= c.getString(c.getColumnIndex("STAVersion"));

				top250.add(remarks);
				listDataChild.put(listDataHeader.get(c1), top250);
				++c1;

			} while (c.moveToNext());

			c.close();
			/*sql.close();
			db.close();*/
		}
		//

		// listDataChild.put(listDataHeader.get(c1), top250); // Header, Child
		// data

	}

	private void updaterefreshdate() {
		// TODO Auto-generated method stub

		Calendar c = Calendar.getInstance();

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		String formattedDate = df.format(c.getTime());

		System.out.println("------ curdaterefresh " + formattedDate);

		String[] aDate = { formattedDate };

		DBInterface db = new DBInterface(parent);
		db.SetDaterefresh(aDate);
		db.Close();

		filldaterefresh();

	}

	/*protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(ConnectionStatusMain.this);
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
			txt.setText("No Refresh Data Available. Please check internet connection....");
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
	}*/

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
						"yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
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
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
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

		DBInterface dbi = new DBInterface(parent);
		String dateref = dbi.GetDateRefresg();
		dbi.Close();
		return dateref;
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences preferences = parent
				.getSharedPreferences("SCROLL", Context.MODE_PRIVATE);
		int scroll = preferences.getInt("ScrollValue", 0);
		System.out
				.println(".............value of scroll at resume>>>>>>>>>>>>>>>>>>>"
						+ scroll);
		// connectionstatus.scrollTo(0, scroll);
		connectionstatus.smoothScrollToPosition(scroll);
	}

	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SharedPreferences preferences = parent
				.getSharedPreferences("SCROLL", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		int scroll = connectionstatus.getFirstVisiblePosition();
		// int scrollx = connectionstatus.getScrollX();
		System.out.println(".............value of scroll>>>>>>>>>>>>>>>>>>>"
				+ scroll);
		editor.putInt("ScrollValue", scroll);
		editor.commit();

		// finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		/*Intent i = new Intent(ConnectionStatusMain.this,
				ConnectionStatusStateFilter.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		i.putExtra("Type", Type);
		startActivity(i);*/
		finish();
	}

}
