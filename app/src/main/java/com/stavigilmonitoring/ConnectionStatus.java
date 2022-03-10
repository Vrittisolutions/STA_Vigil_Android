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

import com.adapters.CallListAdapter;
import com.adapters.ConnectionstatusAdapt;
import com.beanclasses.ConnectionstatusHelper;
import com.beanclasses.StationCall;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ConnectionStatus extends Activity {
	ListView workspacewisedetail;
	String responsesoap = "Added";
	String mobno, link;
	AsyncTask depattask, depattask1;
	// ExpandableListAdapter listAdapter;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop = "no";
	ArrayList<String> projectlist = new ArrayList<String>();
	String spparam = "Employee Reporting to Me";
	ImageView iv,btnadd,btntask;
	static SimpleDateFormat dff;
	static String Ldate;
	String responsemsg = "k";
	String ActivityName, ActivityId, actname;
	private TextView txtdate;
	private TextView txtdaterefresh;
	String daterestr;
	String z = "";
	Context parent;
	private ListView connectionstatus, CallList;
	HashMap<String, List<String>> listDataChild;
	List<String> listDataHeader;
	private String Stationname;
	private TextView stnnameconn;
	private LinearLayout contact;
	ArrayList<String> assignedlist = new ArrayList<String>();
	ArrayList<String> assignedlist1 = new ArrayList<String>();
	private Spinner rbfh;
	private String contactName;
	private String contactnum, subType;
	private LinearLayout contact1;
	private LinearLayout contact2;
	private String number1 = "";
	private LinearLayout contact3;
	private String frompage, Type;
	List<StationCall> lstCall = new ArrayList<StationCall>();
	DatabaseHandler db;

	View sheetview;
	static Button btmsheetedsationname;
	TextView txt_stn_name_btmsht;
	static TextView txtpass;
	int requestCode = 11;
	ProgressDialog progressdialogupdateserver;
	AsyncTask refreshasyncupdateserver;
	String reasonCode = "", reasonDesc = "";
	private String installationId;
	Button edt_btmsht_reasonname, btn_tvstatressave,btn_tvstatrescancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.connectionstatus);

        init();

		if (dbvalue()) {
			updatelist();
			updatelist1();
		} else if (isnet()) {

			fetchdata();
		} else {
			showD("nonet");
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

		btnadd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(parent,WorkAssign_AssignActivity.class);
				intent.putExtra("Activity", "ConnectionStatusStatewise");
				intent.putExtra("Type", Type);
				startActivity(intent);
			//	finish();
			}
		});

		btntask.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(parent,WorkAssignStation_ActivityDetails.class);
				intent.putExtra("Activity", "ConnectionStatus");
				intent.putExtra("Type", Type);
				intent.putExtra("SubType", subType);
				intent.putExtra("stnname", Stationname);
				intent.putExtra("frompage", frompage);
				startActivity(intent);
				finish();
			}
		});

		connectionstatus.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				/*Bundle dataBundle = new Bundle();
				dataBundle.putString("stnname", Stationname);
				dataBundle.putString("frompage", frompage);
				dataBundle.putString("SubType", subType);
				dataBundle.putString("Type", Type);
				Intent i = new Intent(parent,ConnectionStatusFillreason.class);
				i.putExtras(dataBundle);
				startActivity(i);
				finish();*/

				final BottomSheetDialog btmsheetdialog = new BottomSheetDialog(ConnectionStatus.this);
				sheetview = getLayoutInflater().inflate(R.layout.bottomsheet_tvstatusfillreason, null);
				btmsheetdialog.setContentView(sheetview);
				btmsheetdialog.show();
				btmsheetdialog.setCanceledOnTouchOutside(false);

				edt_btmsht_reasonname = sheetview.findViewById(R.id.edt_btmsht_reasonname);
				btn_tvstatressave = sheetview.findViewById(R.id.btn_tvstatressave);
				btn_tvstatrescancel = sheetview.findViewById(R.id.btn_tvstatrescancel);
				txt_stn_name_btmsht = sheetview.findViewById(R.id.txt_stn_name_btmsht);

				txt_stn_name_btmsht.setText("Fill Connection status reason of  "+ Stationname +" here...");

				edt_btmsht_reasonname.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						//get reasons list
						InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

						if(ConnectionStatus.this.getCurrentFocus() != null)
						{
							imm.hideSoftInputFromWindow(ConnectionStatus.this.getCurrentFocus().getWindowToken(), 0);
						}else {
							//Toast.makeText(SelectMenu.this,"Token is null",Toast.LENGTH_SHORT).show();
						}

						Intent intent = new Intent(ConnectionStatus.this,	ConnReasonList.class);
						startActivityForResult(intent, requestCode);
					}
				});

				btn_tvstatressave.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						//save status
						if ((reasonCode.length() > 0)) {
							if (net()) {
								progressdialogupdateserver = ProgressDialog.show(
										ConnectionStatus.this, "Update Reason.......",
										"Please Wait....", true, true, new OnCancelListener() {

											public void onCancel(DialogInterface dialog) {
												// TODO Auto-generated method stub
												if (refreshasyncupdateserver != null
														&& refreshasyncupdateserver.getStatus() != AsyncTask.Status.FINISHED) {
													refreshasyncupdateserver.cancel(true);
												}
											}
										});

								refreshasyncupdateserver = new Updatetoserver_connstat().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

							} else {
								showD("nonet");
							}

						} else {
							showD("empty");
						}

						btmsheetdialog.dismiss();
					}
				});

				btn_tvstatrescancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						btmsheetdialog.dismiss();
					}
				});



			}
		});

		CallList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				String num = lstCall.get(position).getnumber();
				if (num.contains("-"))
					num = num.replace("-", "");
				else
					num = "+91" + num;

				Intent callIntent = new Intent(Intent.ACTION_DIAL);
				callIntent.setData(Uri.parse("tel:" + num));
				startActivity(callIntent);
				finish();

			}
		});
	}

	private void init() {
		parent = ConnectionStatus.this;
		stnnameconn = (TextView) findViewById(R.id.tvconnectionsatusstnname);

		iv = (ImageView) findViewById(R.id.button_refresh_connection);
		btnadd = (ImageView) findViewById(R.id.button_alert_add);
		btnadd.setImageResource(R.drawable.work_assign);
		btnadd.setVisibility(View.VISIBLE);
		btntask = (ImageView) findViewById(R.id.button_viewtask);
		btntask.setVisibility(View.VISIBLE);
		connectionstatus = (ListView) findViewById(R.id.connectionstatusdetail);
		CallList =  findViewById(R.id.listCall);


		Bundle extras = getIntent().getExtras();
		Stationname = extras.getString("stnname");
		frompage = extras.getString("frompage");
		Type = extras.getString("Type");
		subType = extras.getString("SubType");
		stnnameconn.setText(Stationname);

		db = new DatabaseHandler(parent);

		DBInterface dbi = new DBInterface(parent);
		mobno = dbi.GetPhno();

		dbi.Close();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}

	public void updatelist1() {
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		String[] params = new String[1];
		params[0] = Stationname;
        try {


			Cursor c2 = sql
					.rawQuery(
							"SELECT s.InstallationId,ServerTime,Remarks,Last7DaysPerFormance, QuickHealStatus, STAVersion, LatestDowntimeReason,InstallationDesc ,UserName,MobileNo,SUP FROM ConnectionStatusUser s "
									+ " INNER JOIN ConnectionStatusUser1 s1 ON s.InstallationId=s1.InstallationId where s1.InstallationDesc=?",
							params);

			if (c2.getCount() <= 0) {
				c2.close();
				/*sql.close();
				db.close();*/
			} else {
				c2.moveToFirst();
				int column = 0;
				do {
					int columnContact = c2.getColumnIndex("UserName");
					contactName = c2.getString(columnContact);
					String[] arr = contactName.split("/");

					int columnnum = c2.getColumnIndex("MobileNo");
					contactnum = c2.getString(columnnum);
					String[] arr1 = contactnum.split("/");
					Log.e("NameNolist", contactName + "  :  " + contactnum);
					String stnno = c2.getString(c2.getColumnIndex("SUP"));
					Log.e("Station no", "Namelist: " + arr.length);
					Log.e("Station no", "Nolist : " + arr1.length);
					Log.e("Station no", "kavi : " + stnno);

					if (stnno.contains("/")) {
						lstCall.add(new StationCall("Station Number", stnno
								.substring(0, stnno.indexOf("/"))));
					} else {
						lstCall.add(new StationCall("Station Number", stnno));
					}

					Log.e("val", "kavi : " + lstCall.size() + "," + stnno);
					for (int p = 0; p < arr1.length; p++) {
						lstCall.add(new StationCall(arr[p], arr1[p]));
					}

					CallList.setAdapter(new CallListAdapter(ConnectionStatus.this, lstCall));

				} while (c2.moveToNext());

				c2.close();
				/*sql.close();
				db.close();*/
			}
		}catch (Exception e){
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

	private boolean isnet() {
		// TODO Auto-generated method stub
		Context context = ConnectionStatus.this;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	protected String getctime() {
		// TODO Auto-generated method stub

		Calendar c = Calendar.getInstance();
		SimpleDateFormat df3 = new SimpleDateFormat("HH:mm aa", Locale.ENGLISH);
		String formattedDate3 = df3.format(c.getTime());

		return formattedDate3;
	}

	private boolean dbvalue() {
		// TODO Auto-generated method stub
		//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor cursor = sql.rawQuery("SELECT *   FROM ConnectionStatusUser",
				null);

		System.out.println("----------  dbvalue screen cursor count -- "
				+ cursor.getCount());

		if (cursor != null && cursor.getCount() > 0) {

			// do your action
			// Fetch your data

			cursor.close();
			/*sql.close();
			db1.close();*/
			return true;

		} else {

			cursor.close();
			/*sql.close();
			db1.close();*/
			return false;
		}

	}

	private void updatelist() {
		final ArrayList<ConnectionstatusHelper> searchResults = GetDetail();
		connectionstatus.setAdapter(new ConnectionstatusAdapt(this, searchResults));

	}

	private ArrayList<ConnectionstatusHelper> GetDetail() {
		ArrayList<ConnectionstatusHelper> results = new ArrayList<ConnectionstatusHelper>();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		String[] params = new String[1];
		params[0] = Stationname;
		Cursor c2 = sql.rawQuery(
						"SELECT s.InstallationId,ServerTime,Remarks,Last7DaysPerFormance, QuickHealStatus, STAVersion, LatestDowntimeReason,InstallationDesc,UserName,MobileNo  FROM ConnectionStatusUser s "
								+ " INNER JOIN ConnectionStatusUser1 s1 ON s.InstallationId=s1.InstallationId where s1.InstallationDesc=?",
						params);

		if (c2.getCount() == 0) {
			ConnectionstatusHelper sr = new ConnectionstatusHelper();
			// sr.setcsId("");
			sr.setinstallationId("");
			sr.setservertime("");
			sr.setdateDay("");
			sr.setStartTime("");
			sr.setEndTime("");
			sr.settymdiff("");
			sr.setStartEnd("");
			sr.setRemarks("");
			sr.setreason("");
			sr.setpersonDetails("");
			sr.setpersonnumber("");
			results.add(sr);

			c2.close();
			/*sql.close();
			db.close();*/

			return results;
		} else {

			c2.moveToFirst();

			int column = 0;
			do {

				ConnectionstatusHelper sr = new ConnectionstatusHelper();
				sr.setinstallationId(c2.getString(c2
						.getColumnIndex("InstallationDesc")));

				int column1 = c2.getColumnIndex("ServerTime");
				String tf = c2.getString(column1);
				String tfdateday = c2.getString(column1);
				String tftym = c2.getString(column1);
				String[] tym = splitfromtym(tftym);
				String[] v11 = splitfrom(tf);
				String sv1 = tf.substring(tf.indexOf(" ") + 1);
				// String[] v2 = splittime(tf);
				String v2 = splittime(tf);
				String[] v2dd = splittimedateday(tfdateday);
				System.out.println("----value of v1" + sv1);
				sr.setStartTime(sv1); // /chn
				// sr.setservertime(v2[0]);
				sr.setservertime(v2);
				sr.setdateDay(v2dd[0]);
				sr.settymdiff(tym[0]);

				String dates = sv1; // ///chn

				sr.setStatus(c2.getString(c2.getColumnIndex("QuickHealStatus")));
				sr.setVersion(c2.getString(c2.getColumnIndex("STAVersion")));
				sr.setRemarks(c2.getString(c2.getColumnIndex("Remarks")));
				// sr.setreason(c2.getString(c2.getColumnIndex("LatestDowntimeReason")));
				int columnreason = c2.getColumnIndex("LatestDowntimeReason");
				String tfreason = c2.getString(columnreason);
				String finalreason = tfreason + " "
						+ "(Click to Update Reason)";
				sr.setreason(finalreason);
				results.add(sr);

			} while (c2.moveToNext());

			c2.close();
			/*sql.close();
			db.close();*/
		}
		return results;

	}

	private String calculatediff(String datedb) {
		System.out.println("date db......................" + datedb);
		// TODO Auto-generated method stub
		long diffInMillisec = 0;
		long diffInDays = 0;
		try {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

			Date datestop = sdf.parse(datedb);

			long diff = date.getTime() - datestop.getTime();

			diffInDays = diff / (24 * 60 * 60 * 1000);

			// System.out.println(" #####  calculatediff 1 " + diffInDays);

		} catch (Exception ex) {
			diffInDays = 0;
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
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm ", Locale.ENGLISH);
			System.out.println("---##### sdf 0 " + sdf);

			Date datestop = sdf.parse(datedb);
			System.out.println("---value of datestop...." + datestop);
			diff = date.getTime() - datestop.getTime();
		} catch (Exception ex) {
			diff = 0;
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
		String s = String.valueOf(diff);
		return s;

	}

	private String[] splitfrom(String tf) {
		System.out.println("---value of tf...." + tf);
		String fromtimetw = "";
		String k = tf.substring(9, tf.length() - 0);
		String[] v1 = { k };

		return v1;
	}

	private String[] splitfromtym(String tym) {
		// TODO Auto-generated method stub
		System.out.println("---value of tym differ...." + tym);
		String fromtimetw = "";

		final String dateStart = tym;
		// final String dateStop = "01/15/2012 10:31:48";
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
				} else {
					int i = Integer.parseInt(minutes);
					if (i >= 30) {
						// end

						diffTym = diffHours + " Hours " + diffMinutes
								+ " Minutes ";
					} else {
						diffTym = diffHours + " Hours ";
					}

				}

			} else {
				if (hours.equals("0")) {
					int i = Integer.parseInt(minutes);
					if (i >= 30) {
						// end

						// diffTym=diffDays + " Days " +
						// diffMinutes+" Minutes ";
						diffTym = diffDays + " Days ";
					} else {
						diffTym = diffDays + " Days ";
					}

				} else {

					int i = Integer.parseInt(minutes);
					if (i >= 30) {
						// end

						// diffTym=diffDays +
						// " Days "+diffHours+" Hours "+diffMinutes+" Minutes ";
						diffTym = diffDays + " Days ";
					} else {
						// diffTym=diffDays + " Days "+diffHours+" Hours ";
						diffTym = diffDays + " Days ";
					}

				}
			}

			System.out.print(diffDays + " days, ");
			System.out.print(diffHours + " hours, ");
			System.out.print(diffMinutes + " minutes, ");
			System.out.print(diffSeconds + " seconds.");

		} catch (Exception e) {
			diffTym = "";
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

	private String splittime(String tf) {

		long diffDays = 0;
		System.out.println("---value of tf for date...." + tf);
		String fromtimetw = "";

		String k = tf.substring(0, tf.length() - 11);

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
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
		String finalDate = timeFormat.format(myDate);

		final String dateStart = finalDate;
		DateFormat dateFormat1 = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
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
			long diff = d2.getTime() - d1.getTime();

			diffDays = diff / (24 * 60 * 60 * 1000);

		} catch (Exception e) {
			diffDays = 0;
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

	private String[] splittimedateday(String tf) {
		System.out.println("---value of tf for date...." + tf);
		String fromtimetw = "";

		String k = tf.substring(0, tf.length() - 11);

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
		SimpleDateFormat timeFormat = new SimpleDateFormat("EEE");
		String finalDate = timeFormat.format(myDate);

		String[] v2dd = { finalDate };

		return v2dd;
	}

	private void fetchdata() {
		new DownloadxmlsDataURL_new().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			try {
				String xx = "";

				String url = "http://sta.vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetAllStationWithUserName_Android?Mobile="
						+ mobno;

				Log.e("csn status", "url : " + url);
				url = url.replaceAll(" ", "%20");
				try {
					responsemsg = ut.httpGet(url);
					Log.e("csn status", "resmsg : " + responsemsg);
				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				//DatabaseHandler db = new DatabaseHandler(getBaseContext());
				SQLiteDatabase sql = db.getWritableDatabase();
				if (responsemsg.contains("<IId>")) {
					sop = "valid";

					//sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusUser1");
					//sql.execSQL(ut.getConnectionStatusUser1());
					sql.delete("ConnectionStatusUser1",null,null);

					Cursor c1 = sql.rawQuery(
							"SELECT *   FROM ConnectionStatusUser1", null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table1");
					String msg = "";
					String columnName, columnValue;
					Log.e("sts main...", " fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < c1.getColumnCount(); j++) {
							columnName = c1.getColumnName(j);

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
							Log.e("download stn : ", columnName + " : "
									+ columnValue);
							values1.put(columnName, columnValue);

						}
						sql.insert("ConnectionStatusUser1", null, values1);
					}

					c1.close();
					/*sql.close();
					db.close();*/

				}

				url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetCSNStatus_Android_new?Mobile="
						+ mobno;
				url = url.replaceAll(" ", "%20");
				try {
					responsemsg = ut.httpGet(url);

				} catch (NullPointerException e) {
					responsemsg = "Error";
					e.printStackTrace();
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
								+ e.getMessage() + " " + Ldate);
					}
				} catch (IOException e) {
					responsemsg = "Error";
					e.printStackTrace();
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
								+ e.getMessage() + " " + Ldate);
					}
				}

				if (responsemsg.contains("<A>")) {
					sop = "valid";
					//sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusUser");
					//sql.execSQL(ut.getConnectionStatusUser());
					sql.delete("ConnectionStatusUser",null,null);

					Cursor c = sql.rawQuery(
							"SELECT *   FROM ConnectionStatusUser", null);

					ContentValues values = new ContentValues();
					NodeList nl = ut.getnode(responsemsg, "Table1");
					String msg = "";
					String columnName, columnValue;
					for (int i = 0; i < nl.getLength(); i++) {
						String conn = "invalid";
						Element e = (Element) nl.item(i);

						for (int j = 0; j < c.getColumnCount(); j++) {
							columnName = c.getColumnName(j);

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
									conn = "invalid";
								}
							}

							values.put(columnName, columnValue);
						}

						if (conn == "valid")
							sql.insert("ConnectionStatusUser", null, values);
					}

					c.close();
					/*sql.close();
					db.close();*/

				} else {
					sop = "invalid";
					System.out
							.println("--------- invalid for project list --- ");
				}
			} catch (Exception e) {
				sop = "invalid";
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
			super.onPostExecute(result);
			try {
				System.out.println("...............value of sop" + sop);
				if (sop.equals("valid")) {

					updatelist();
					updatelist1();

				} else {

					showD("invalid");
				}
				iv.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(R.id.progressBar1))
						.setVisibility(View.GONE);
			} catch (Exception e) {
				sop = "invalid";
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

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);

		}

	}

	protected void showD(String string) {
		final Dialog myDialog = new Dialog(ConnectionStatus.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(R.layout.dialoginfosmall);
		myDialog.setCancelable(true);
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
			txt.setText("No Refresh data Available. Please check Internet connection...");
		}

		Button btn = (Button) myDialog
				.findViewById(R.id.gotobtndialoginfosmall);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				myDialog.dismiss();
			}
		});

		myDialog.show();

	}

	protected boolean net() {
		// TODO Auto-generated method stub
		Context context = parent;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Log.e("on back", "frompage : " + frompage);
		//finish();
		if (frompage == null) {
			Intent i = new Intent(ConnectionStatus.this,
					ConnectionStatusMain.class);
			i.putExtra("subType", subType);
			i.putExtra("Type", Type);
			startActivity(i);
			finish();
		} else if (frompage.equalsIgnoreCase("nonreport")) {
			Bundle dataBundle = new Bundle();
			dataBundle.putString("stnname", Stationname);
			Intent myIntent = new Intent();
			myIntent.setClass(parent, NonrepeatedAd.class);
			myIntent.putExtras(dataBundle);
			startActivity(myIntent);
		} else if (frompage.equalsIgnoreCase("connStatus")) {
			Intent i = new Intent(ConnectionStatus.this,
					ConnectionStatusMain.class);

			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.putExtra("Type", Type);
			i.putExtra("subType", subType);
			startActivity(i);
			finish();
		} else if (frompage.equalsIgnoreCase("connStatusAll")) {
			Intent i = new Intent(ConnectionStatus.this,ConnectionStatusMainAll.class);
			i.putExtra("Type", Type);
			i.putExtra("subType", subType);
			i.putExtra("CallFrom","ConnectionStatusStateFilter");
			startActivity(i);
			finish();
		}
	}

	class Updatetoserver_connstat extends AsyncTask<String, Void, String> {
		private String reasonCodeDesc = "";
		private String installationId = "";

		@Override
		protected String doInBackground(String... paramss) {
			try {
				//DatabaseHandler db1 = new DatabaseHandler(parent);
				SQLiteDatabase sqldb = db.getWritableDatabase();
				String[] params = new String[1];
				params[0] = reasonCode;

				Cursor c2 = sqldb
						.rawQuery(
								"SELECT * FROM DownTimeRasonFill where ReasonDescription=? ",
								params);

				reasonCodeDesc = "";
				if (c2.getCount() == 0) {
					c2.close();
					/*db.close();
					db1.close();*/
				} else {
					c2.moveToFirst();
					reasonCodeDesc = c2.getString(c2
							.getColumnIndex("ReasonCode"));
					System.out
							.println("...................reason code value is.............."
									+ reasonCodeDesc);
					c2.moveToLast();
					c2.close();
					/*db.close();
					db1.close();*/

				}
			} catch (Exception e) {
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
			try {

				//DatabaseHandler db2 = new DatabaseHandler(parent);
				SQLiteDatabase dbf = db.getWritableDatabase();
				String[] params = new String[1];
				params[0] = Stationname;
				Cursor cf = dbf
						.rawQuery(
								"SELECT * FROM ConnectionStatusUser1 where InstallationDesc=? ",
								params);
				installationId = "";
				if (cf.getCount() == 0) {
					cf.close();
					/*dbf.close();
					db2.close();*/
				} else {
					cf.moveToFirst();
					installationId = cf.getString(cf
							.getColumnIndex("InstallationId"));

					cf.moveToLast();
					cf.close();
					/*dbf.close();
					db2.close();*/

				}
			} catch (Exception e) {
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
			String xx = "";

			String url = "http://sta.vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/UpdateTemporaryReason_Android?InstallationId="
					+ installationId
					+ "&MobileNo="
					+ mobno
					+ "&ReasonCode="
					+ reasonCodeDesc + "&ReasonDesc=" + reasonCode;
			url = url.replaceAll(" ", "%20");

			try {
				responsemsg = ut.httpGet(url);
			} catch (NullPointerException e) {
				responsemsg = "wrong" + e.toString();
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
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			// progressdialogupdateserver.cancel();

			if (responsesoap.equals("Added")) {

				new DownloadxmlsDataURL_connstat().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


			} else {
				Toast.makeText(parent, "Server Error..",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public class DownloadxmlsDataURL_connstat extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			//DatabaseHandler Sql = new DatabaseHandler(parent);
			SQLiteDatabase sqldb = db.getWritableDatabase();

			String url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetCSNStatus_Android_new?Mobile="
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

			if (responsemsg.contains("<A>")) {
				sop = "valid";
				//sqldb.execSQL("DROP TABLE IF EXISTS ConnectionStatusUser");
				//sqldb.execSQL(ut.getConnectionStatusUser());
				sqldb.delete("ConnectionStatusUser",null,null);

				Cursor c = sqldb.rawQuery("SELECT *   FROM ConnectionStatusUser",
						null);

				ContentValues values = new ContentValues();
				NodeList nl = ut.getnode(responsemsg, "Table1");
				String msg = "";
				String columnName, columnValue;
				for (int i = 0; i < nl.getLength(); i++) {
					Element e = (Element) nl.item(i);

					for (int j = 0; j < c.getColumnCount(); j++) {
						columnName = c.getColumnName(j);

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
						else if (columnName
								.equalsIgnoreCase("Last7DaysPerFormance"))
							ncolumnname = "H";
						else if (columnName.equalsIgnoreCase("QuickHealStatus"))
							ncolumnname = "I";
						else if (columnName.equalsIgnoreCase("STAVersion"))
							ncolumnname = "J";
						else if (columnName
								.equalsIgnoreCase("AscOrderServerTime"))
							ncolumnname = "K";
						else if (columnName
								.equalsIgnoreCase("LatestDowntimeReason"))
							ncolumnname = "L";
						else if (columnName.equalsIgnoreCase("UserName"))
							ncolumnname = "M";
						else if (columnName.equalsIgnoreCase("Type"))
							ncolumnname = "N";
						else if (columnName.equalsIgnoreCase("SubHeadPH_No"))
							ncolumnname = "O";
						else if (columnName
								.equalsIgnoreCase("SupportAgencyName"))
							ncolumnname = "P";
						else if (columnName.equalsIgnoreCase("SubNetworkCode"))
							ncolumnname = "R";
						columnValue = ut.getValue(e, ncolumnname);
						values.put(columnName, columnValue);
					}
					sqldb.insert("ConnectionStatusUser", null, values);
				}

				c.close();
				//db.close();

			} else {
				sop = "invalid";
				System.out.println("--------- invalid for project list --- ");
			}
			return sop;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			progressdialogupdateserver.cancel();

			if (result.equalsIgnoreCase("valid")) {
				Toast.makeText(parent,
						"Reason Updated Successfully..!", Toast.LENGTH_LONG).show();
				finish();
			} else {

				Toast.makeText(parent,
						"Reason not Updated...Please try later ",
						Toast.LENGTH_LONG).show();

			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			reasonCode = data.getExtras().getString("ReasonDesc");

			edt_btmsht_reasonname.setText(reasonCode);
		}
	}
}
