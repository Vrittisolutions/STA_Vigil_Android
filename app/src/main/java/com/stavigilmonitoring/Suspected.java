package com.stavigilmonitoring;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import com.adapters.SuspectedAdapt;
import com.adapters.SuspectedAdapter_new;
import com.beanclasses.SuspectedHelper;
import com.database.DBInterface;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class Suspected extends Activity {
	private static final String Days = null;
	private static final String Hours = null;
	private static final String Minutes = null;
	private static final String Seconds = null;
	ProgressDialog pd;
	ListView workspacewisedetail;
	String mobno, link;
	AsyncTask depattask;
	static SimpleDateFormat dff;
	static String Ldate;
	//ExpandableListAdapter listAdapter;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop = "no";
	ArrayList<String> projectlist = new ArrayList<String>();
	String spparam = "Employee Reporting to Me";
	ImageView iv;

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
	private String Stationname,NetworkCode;
	private GridView suspectedDetails;
	private TextView susstnnsme;
	private Button historysus;
	DatabaseHandler db;
	ArrayList<SuspectedHelper> searchResults;
	ImageView btnfilter;
	Context parent;
	SuspectedAdapter_new listAdapter;
	String sonum;
	private EditText filtertext;
	private SuspectedAdapter_new adapterNew;
	private static Suspected.DownloadxmlsDataURL asynk_new;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.suspected);

		filtertext = (EditText)findViewById(R.id.edfitertext);

		filtertext.setVisibility(View.GONE);

		initView();
		setListener();


		//	historysus=(Button)findViewById(R.id.btnsushistory);
		susstnnsme=(TextView)findViewById(R.id.tvsuspectedstnname);
		Bundle extras = getIntent().getExtras();
		System.out.println("'''''''''''1...........");
		Stationname = extras.getString("stnname");
		NetworkCode = extras.getString("network");
		System.out.println("...........station name on bp"+Stationname);
		susstnnsme.setText(NetworkCode+" Station Spot %");

		iv=(ImageView)findViewById(R.id.button_refresh_suspected);
		suspectedDetails = findViewById(R.id.suspecteddetail);
		db = new DatabaseHandler(getApplicationContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();

		searchResults = new ArrayList<SuspectedHelper>();

		dbi.Close();

		fetchdata();


			/*if (dbvalue()) {
				updatelist();
			//	prepareListData();
			} else {
				fetchdata();
			}*/

		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (isnet()) {

					fetchdata();
				} else {
					showD("nonet");
				}


			}
		});

		suspectedDetails.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
									long id) {

				Object o = suspectedDetails.getItemAtPosition(position);
				SuspectedHelper fullObject = (SuspectedHelper) o;
				editActivity(searchResults.get(position).getStationName(),
						searchResults.get(position).getInstalationId(),
						/*searchResults.get(position).getTotalSpot(),*/
						searchResults.get(position).getAdvertisementName());

				/*if (dbvalue()) {
					Object o = suspectedDetails.getItemAtPosition(position);
					SuspectedHelper fullObject = (SuspectedHelper) o;
					editActivity(fullObject.getAdvertisementcode());

				} else {

					Toast.makeText(getBaseContext(), "No Station Present..",
							Toast.LENGTH_LONG).show();
				}*/

			}
		});
	}

	private void setListener() {
		filtertext.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start,
									  int before, int count) {

						/*adapterNew
								.filter_Station(filtertext
										.getText().toString().trim());*/

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start,
										  int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

				adapterNew.filter_details(filtertext.getText().toString().trim());
				//adapterNew.notifyDataSetChanged();

			}
		});

	}

	private void initView() {
		parent = Suspected.this;
		//iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_nonrepeated_main);

		//btnfilter = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_filter);
		findViewById(R.id.button_filter).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(filtertext.getVisibility()==View.GONE){
					filtertext.setVisibility(View.VISIBLE);
				}else{
					filtertext.setVisibility(View.GONE);
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

	public void editActivity(String stName,String instId,String Advertisementname) {

		// MOMA --- mom attend request
		// System.out.println("----------  type --- " + type);

		Bundle dataBundle = new Bundle();
		dataBundle.putString("Advertisementname", Advertisementname);
		dataBundle.putString("Stationname", stName);
		dataBundle.putString("StationId", instId);
		//dataBundle.putString("Total", Total);
		Intent myIntent = new Intent();
		myIntent.setClass(getApplicationContext(), SuspectedHistory.class);

		myIntent.putExtras(dataBundle);
		startActivity(myIntent);


	}
	private boolean dbvalue() {
		// TODO Auto-generated method stub
		//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor cursor = sql.rawQuery(
				"SELECT *   FROM Suspected ", null);

		System.out.println("----------  dbvalue screen cursor count -- "
				+ cursor.getCount());

		if (cursor != null && cursor.getCount() > 0) {

			// do your action
			// Fetch your data

			cursor.close();
			sql.close();
			//db1.close();
			return true;

		} else {

			cursor.close();
			sql.close();
			//db1.close();
			return false;
		}

	}
	@RequiresApi(api = Build.VERSION_CODES.N)
	private void updatelist() {
		// TODO Auto-generated method stub

		searchResults = GetDetail();

		/*List<SuspectedHelper> sortedUsers = searchResults.stream()
				.sorted(Comparator.comparing(SuspectedHelper::getSpotWisePercentage))
				.collect(Collectors.toList());*/



		for (int i = 0; i < searchResults.size(); i++) {

			// Inner nested loop pointing 1 index ahead
			for (int j = i + 1; j < searchResults.size(); j++) {

				// Checking elements
				String temp = "0";
				if (Integer.parseInt(searchResults.get(j).getSpotWisePercentage())
						< Integer.parseInt(searchResults.get(i).getSpotWisePercentage())) {

					// Swapping
					temp = searchResults.get(i).getSpotWisePercentage();
					searchResults.get(i).setSpotWisePercentage(searchResults.get(j).getSpotWisePercentage());
					searchResults.get(j).setSpotWisePercentage(temp);


				}
			}


		}

		adapterNew=new SuspectedAdapter_new(this, searchResults);

		//suspectedDetails.setAdapter(new SuspectedAdapt(this, searchResults));
		suspectedDetails.setAdapter(adapterNew);
	}

	private ArrayList<SuspectedHelper> GetDetail() {
		ArrayList<SuspectedHelper> results = new ArrayList<SuspectedHelper>();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		String[] params = new String[1];
		params[0] = Stationname;
		Cursor c2 = sql.rawQuery("SELECT * FROM Suspected ORDER BY SpotWisePercentage ASC", null);
		if (c2.getCount() == 0) {
			SuspectedHelper sr = new SuspectedHelper();
			//sr.setcsId("");
			sr.setAdvertisementcode("");
			sr.setAdvertisementName("");
			sr.setEffectiveDateFrom("");

			sr.setEffectiveDateTo("");
			sr.setDayRepeatitions("");
			sr.setActRepeatitions("");
			sr.setStationSpots("");
			sr.setTotalSpot("");
			sr.setSpotWisePercentage("");
			sr.setPercentage("");
			results.add(sr);

			c2.close();
			sql.close();
			////db1.close();

			return results;
		} else {

			c2.moveToFirst();

			int column = 0;
			do {

				SuspectedHelper sr = new SuspectedHelper();

				// column = c.getColumnIndex("UserName");
				sr.setStationName(c2.getString(c2.getColumnIndex("StationName")));
				sr.setAdvertisementName(c2.getString(c2.getColumnIndex("AdvertisementName")));
				//sr.setEffectiveDateFrom(c2.getString(c2.getColumnIndex("EffectiveDateFrom")));
				//sr.setEffectiveDateTo(c2.getString(c2.getColumnIndex("EffectiveDateTo")));
				sr.setActRepeatitions(c2.getString(c2.getColumnIndex("ActRept")));
				//sr.setStationSpots(c2.getString(c2.getColumnIndex("StationSpots")));
				sr.setTotalSpot(c2.getString(c2.getColumnIndex("TotalSpot")));
				sr.setSpotWisePercentage(c2.getString(c2.getColumnIndex("SpotWisePercentage")));
				sr.setPercentage(c2.getString(c2.getColumnIndex("SpotWisePercentage")));
				//int column1 = c2.getColumnIndex("SpotWisePercentage");
				//sr.setActRepeatitions(c2.getString(c2.getColumnIndex("DayRepeatitions")));
				//sr.setDayRepeatitions(c2.getString(c2.getColumnIndex("ActRept")));
				/*String tf = c2.getString(column1);
				String[] v1 = splitfrom(tf);
				sr.setSpotWisePercentage(v1[0]);
//				sr.setinstallationId(c.getString(c
//						.getColumnIndex("InstallationId")));
				int column2 = c2.getColumnIndex("Percentage");
				String tf2 = c2.getString(column2);
				String[] v2 = splitfrom2(tf2);
				sr.setPercentage(v2[0]);*/
				results.add(sr);

			} while (c2.moveToNext());

			c2.close();
			sql.close();
			////db1.close();
		}
		return results;

	}

	private String splittime(String tf) {
		// TODO Auto-generated method stub

		long diffDays=0;
		System.out.println("---value of tf for date...."+tf);
		String fromtimetw = "";

		String k = tf.substring(0, tf.length() - 11);
		System.out.println("---value of k for date..."+k);

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(k);
			System.out.println("..........value of my date after conv"+myDate);

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
		System.out.println("date format of system......................"+dateFormat1.format(date));
		System.out.println("date format of web tym......................"+date);
		final String dateStop =dateFormat1.format(date);

		Date d1 = null;
		Date d2 = null;
		String diffTym="";

		try {
			d1 = dateFormat1.parse(dateStart);
			d2 = dateFormat1.parse(dateStop);
			System.out.println("d2......................"+d2);
			//in milliseconds
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

	private String[] splitfromhr(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf...."+tf);
		String fromtimetw = "";
		System.out.println("---21111111111111111111...."+tf);
		String k = tf.substring(11, tf.length() - 0);
		System.out.println("---value of khr..."+k);

		String[] v1hr={  k };


		return v1hr;
	}

	private String[] splitfrom2(String tf2) {
		// TODO Auto-generated method stub
		String s= tf2 + " % ";
		String [] vs2={s};
		return vs2;

	}


	private String[] splitfrom(String tf) {
		// TODO Auto-generated method stub
		String s= tf + " % ";
		String [] vs={s};
		return vs;
	}

	private void fetchdata() {
		// TODO Auto-generated method stub

		pd = ProgressDialog.show(Suspected.this,
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

			try {

				String xx = "";

				String url="http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetSuspectedStation?Flg="+NetworkCode;

				url = url.replaceAll(" ", "%20");

				System.out.println("============ internet reg url " + url);

				try {
					System.out.println("-------  activity url --- " + url);
					responsemsg = ut.httpGet(url);

					System.out.println("-------------  xx vale-- "
							+ responsemsg);
				}
				catch(NullPointerException e)
				{
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
				catch (IOException e) {
					e.printStackTrace();

					responsemsg = "wrong" + e.toString();
					System.out
							.println("--------- invalid for message type list --- "+responsemsg);
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

//
				if (responsemsg.contains("<InstalationId>")) {
					sop = "valid";
					//DatabaseHandler db = new DatabaseHandler(getBaseContext());
					System.out.println("------------- 1-- ");
					SQLiteDatabase sql = db.getWritableDatabase();
					System.out.println("------------- 2-- ");
					//sql.execSQL("DROP TABLE IF EXISTS Suspected");
					System.out.println("------------- 3-- ");
					//sql.execSQL(ut.getSuspected());
					sql.delete("Suspected",null,null);

					System.out.println("------------- 4-- ");
					System.out.println("------------- 5-- ");

					Cursor c = sql.rawQuery("SELECT *   FROM Suspected",
							null);
					System.out.println("------------- 6-- ");
					ContentValues values = new ContentValues();
					System.out.println("------------- 7-- ");
					NodeList nl = ut.getnode(responsemsg, "TableResult");
					System.out.println("------------- 8-- ");
					String msg = "";
					System.out.println("------------- 9-- ");
					String columnName, columnValue;
					for (int i = 0; i < nl.getLength(); i++) {
						Element e = (Element) nl.item(i);
						System.out.println("------------- 10-- ");
						columnValue = ut.getValue(e, "StnNm");
						db.addSuspectedDetails(ut.getValue(e, "InstalationId"), ut.getValue(e, "StnNm"),
								ut.getValue(e, "TotalSpt"), ut.getValue(e, "ActSpot"),
								ut.getValue(e, "SpotPerc"),ut.getValue(e, "AdvtDesc"));

						/*for (int j = 0; j < c.getColumnCount(); j++) {
							System.out.println("------------- 11-- ");
							columnName = c.getColumnName(j);
							columnValue = ut.getValue(e, columnName);

							System.out.println("-------------column name"+ columnName);
							System.out.println("-------------column value"+ columnValue);

							values.put(columnName, columnValue);
						}*/

						//sql.insert("Suspected", null, values);

						System.out.println("---------------inserted into suspected status");
					}

					c.close();
					sql.close();
					////db1.close();

				} else {
					sop = "invalid";
					System.out
							.println("--------- invalid for project list --- ");
				}
			}catch (Exception e) {
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

			return sop;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try{
				pd.cancel();

				System.out.println("...............value of sop"+ sop);
				if (sop.equals("valid")) {

					updatelist();

				} else {

					showD("invalid");
				}

			}catch(Exception e)
			{
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


		}

	}




	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(Suspected.this);
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
			txt.setText("No Refresh Data Available. Please check Internet connection...");
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

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences preferences = getApplicationContext().getSharedPreferences("SCROLLSUSHIS", Context.MODE_PRIVATE);
		int scroll = preferences.getInt("ScrollValueSusHis", 0);
		System.out.println(".............value of scroll at resume>>>>>>>>>>>>>>>>>>>"+scroll);
		//  connectionstatus.scrollTo(0, scroll);
		suspectedDetails.smoothScrollToPosition(scroll);
	}

	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SharedPreferences preferences = getApplicationContext().getSharedPreferences("SCROLLSUSHIS", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		int scroll = suspectedDetails.getFirstVisiblePosition();
		//  int scrollx = connectionstatus.getScrollX();
		System.out.println(".............value of scroll>>>>>>>>>>>>>>>>>>>"+scroll);
		editor.putInt("ScrollValueSusHis", scroll);
		editor.commit();
		// finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		Bundle dataBundle = new Bundle();
		dataBundle.putString("stnname", Stationname);
		//dataBundle.putString("ActivityName", ActivityName);
		Intent i = new Intent(Suspected.this, SuspectedMain.class);
		//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtras(dataBundle);
		startActivity(i);
		//finish();

	}

}
