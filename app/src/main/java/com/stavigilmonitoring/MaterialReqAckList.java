package com.stavigilmonitoring;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.adapters.ExpandableListAdapter_new;
import com.adapters.MaterialReqAckAdapter;
import com.beanclasses.MaterialReqAckBean;
import com.database.DBInterface;

public class MaterialReqAckList extends Activity {
	ArrayList<MaterialReqAckBean> materialmyorderlist;
	MaterialReqAckBean materialmyorderBean;
	String Type;
	String mobno, link;
	AsyncTask depattask;
	String sop = "no";
	static DownloadxmlsDataURL asyncfetch_delivered;

	MaterialReqAckAdapter materialAdapter;
	String responsemsg = "k";
	String ActivityName, ActivityId, actname;
	com.stavigilmonitoring.utility ut;
	private TextView txtdaterefresh;
	String daterestr;
	public String filter;

	TextView title;
	ImageView iv;
	private ListView listPending;
	String conn = "invalid";
	static SimpleDateFormat dff;
	static String Ldate;
	String finalDate;
	ImageView btn_filter_order;
	EditText searchorder;
	DatabaseHandler db;

	ExpandableListView expndblorderlist;
	ExpandableListAdapter_new expadapter;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.activity_myorder);

		//setContentView(R.layout.expndble_myorder_list);
		listPending = (ListView) findViewById(com.stavigilmonitoring.R.id.materialreq_myorderlist);
		listPending.setVisibility(View.VISIBLE);

		expndblorderlist = (ExpandableListView)findViewById(R.id.expndblorderlist);
		expndblorderlist.setVisibility(View.GONE);

		iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_myorder);
		title = (TextView) findViewById(com.stavigilmonitoring.R.id.materialreq_myorder);
		btn_filter_order = (ImageView)findViewById(com.stavigilmonitoring.R.id.button_filter_myord);
		searchorder = (EditText) findViewById(com.stavigilmonitoring.R.id.edfiter_ordtext);
		materialmyorderlist = new ArrayList<MaterialReqAckBean>();


		ut = new com.stavigilmonitoring.utility();

		title.setText("My Orders");
		db = new DatabaseHandler(getBaseContext());
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();

		Intent intent = getIntent();
		if (intent.hasExtra("isrefresh")) {
			if (intent.getStringExtra("isrefresh").equalsIgnoreCase("true")) {
				if (isnet()) {
					fetchdata();
				} else {
					showD("nonet");
				}
			}
		}

		if (asyncfetch_delivered != null
				&& asyncfetch_delivered.getStatus() == AsyncTask.Status.RUNNING) {
			Log.e("async", "running");
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}

		if (dbvalue()) {
			getDetail();
		} else if (isnet()) {

			fetchdata();
		} else {
			showD("nonet");
		}

		dbi.Close();

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

		listPending.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {

				if (dbvalue()) {

					Bundle dataBundle = new Bundle();
					dataBundle.putString("orderHeaderId", materialmyorderlist
							.get(position).getPkmaterialid());
					dataBundle.putString("materialname", materialmyorderlist
							.get(position).getMaterialname());
					dataBundle.putString("reason",materialmyorderlist.get(position).getReason());
					dataBundle.putString("qty",materialmyorderlist.get(position).getQty());
					dataBundle.putString("stationname", materialmyorderlist
							.get(position).getStationname());
					dataBundle.putString("scraprepair", materialmyorderlist
							.get(position).getScraprepair());
					dataBundle.putString("sendreqto",materialmyorderlist.
							get(position).getReporteename());
					dataBundle.putString("Warranty",materialmyorderlist.
							get(position).getWarranty());
					dataBundle.putString("SelectedDt",materialmyorderlist.
							get(position).getSelectedDt());

					Intent myIntent = new Intent();
					myIntent.setClass(getApplicationContext(), MaterialReqAckDetails.class);
					myIntent.putExtras(dataBundle);
					startActivity(myIntent);

				} else {
					Toast.makeText(getBaseContext(),
							"No Information Present..", Toast.LENGTH_LONG)
							.show();
				}
			}
		});

		btn_filter_order.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (searchorder.getVisibility() == View.VISIBLE) {
					searchorder.setVisibility(View.GONE);
					InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
				} else if ((searchorder).getVisibility() == View.GONE) {
					searchorder.setVisibility(View.VISIBLE);
					EditText textView = searchorder;
					textView.requestFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
				}
			}
		});

		searchorder.addTextChangedListener(new TextWatcher() {

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
						materialAdapter.filter((searchorder)
										.getText().toString().trim()
										.toLowerCase(Locale.getDefault()));
						// getDetail_filter();
						// soundlevelAdapter
						// .filter(((EditText) findViewById(R.id.edfitertext))
						// .getText().toString().trim()
						// .toLowerCase(Locale.getDefault()));
					}
				});

	}

	public void getDetail() {
		materialmyorderlist.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		/*Cursor c = sql.rawQuery(
						"Select  distinct pkmaterialid , senderMobNo ,materialname , reason , qty , stationname , stationmasterid , scraprepair ,reporteename , reportingid , addedtdt , statusflag , Warranty , SelectedDt from Myorders ORDER BY cast(pkmaterialid as int) DESC",
						null);*/
		Cursor c = sql.rawQuery(
				"Select  distinct pkmaterialid , senderMobNo ,materialname , reason , qty , stationname , stationmasterid , " +
						"scraprepair ,reporteename , reportingid , addedtdt , statusflag, Warranty, SelectedDt  from Myorders " +
						"ORDER BY cast(pkmaterialid as int) DESC",
				null);

		Log.e("Myorders ", "" + c.getCount());
		if (c.getCount() == 0) {
			
			materialmyorderBean = new MaterialReqAckBean();
			materialmyorderBean.setPkmaterialid("");
			materialmyorderBean.setSenderMobNo("");
			materialmyorderBean.setMaterialname("");
			materialmyorderBean.setReason("");
			materialmyorderBean.setQty("");
			materialmyorderBean.setScraprepair("");
			materialmyorderBean.setStationname("");
			materialmyorderBean.setReporteename("");
			materialmyorderBean.setAddedtdt("");
			materialmyorderBean.setStatusflag("");
			materialmyorderBean.setWarranty("");
			materialmyorderBean.setSelectedDt("");
			materialmyorderlist.add(materialmyorderBean);

			c.close();

			// return arrlist;
		} else {

			c.moveToFirst();

			int column = 0;
			do {

				// pkmaterialid , senderMobNo ,materialname , reason , qty ,
				// stationname ,
				// stationmasterid , scraprepair ,
				// "reporteename , reportingid ,"
				// + " qty1 TEXT, addedtdt TEXT, addedby TEXT, statusflag
				// materialmyorderlist.clear();
				String materialname = c.getString(c.getColumnIndex("materialname"));
				String senderMobNo = c.getString(c.getColumnIndex("senderMobNo"));
				String stationname = c.getString(c.getColumnIndex("stationname"));
				String pkmaterialid = c.getString(c.getColumnIndex("pkmaterialid"));
				String reason = c.getString(c.getColumnIndex("reason"));
				String statusflag = c.getString(c.getColumnIndex("statusflag"));

				String scraprepair = c.getString(c.getColumnIndex("scraprepair"));
				String qty = c.getString(c.getColumnIndex("qty"));
				String addedtdt = c.getString(c.getColumnIndex("addedtdt"));

				String[] v1 = splitfrom(addedtdt);
				String reporteename = c.getString(c.getColumnIndex("reporteename"));

				String Warranty = c.getString(c.getColumnIndex("Warranty"));
				String SelectedDt = c.getString(c.getColumnIndex("SelectedDt"));

				materialmyorderBean = new MaterialReqAckBean();
				materialmyorderBean.setPkmaterialid(pkmaterialid);
				materialmyorderBean.setSenderMobNo(senderMobNo);
				materialmyorderBean.setMaterialname(materialname);
				materialmyorderBean.setReason(reason);
				materialmyorderBean.setQty(qty);
				materialmyorderBean.setScraprepair(scraprepair);
				materialmyorderBean.setStationname(stationname);
				materialmyorderBean.setReporteename(reporteename);
				materialmyorderBean.setAddedtdt(v1[0]);
				materialmyorderBean.setStatusflag(statusflag);
				materialmyorderBean.setWarranty(Warranty);
				materialmyorderBean.setSelectedDt(SelectedDt);
				materialmyorderlist.add(materialmyorderBean);

			} while (c.moveToNext());

			c.close();
		}
		
		SetAdapter();
	}

	private void prepareListData() {
		materialmyorderlist.clear();
		SQLiteDatabase sql = db.getWritableDatabase();

		String addeddate, addeddate_Year = null, YEAR;
		boolean YEAR2;

		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();

		//Adding listheader data
		Cursor c = sql.rawQuery("Select DISTINCT addedtdt from Myorders ORDER BY cast(pkmaterialid as int) DESC",
				null);
		Log.e("Myorders ", "" + c.getCount());
		if(c.getCount() > 0){
			c.moveToFirst();
			do{
				addeddate = c.getString(c.getColumnIndex("addedtdt"));
				String[] yr = addeddate.split(" ");
				String data = yr[0];

				String[] years =  data.split("/");
				YEAR = years[2];

				if(listDataHeader.contains(YEAR)){
					//do not add same year in list
					addeddate_Year = YEAR;
				}else {
					listDataHeader.add(YEAR);
				}
				//listDataHeader.add(YEAR);

			}while (c.moveToNext());

		}else {

		}

		// Adding child data
		List<String> top250 = new ArrayList<String>();
		top250.add("The Shawshank Redemption");
		top250.add("The Godfather");
		top250.add("The Godfather: Part II");
		top250.add("Pulp Fiction");
		top250.add("The Good, the Bad and the Ugly");
		top250.add("The Dark Knight");
		top250.add("12 Angry Men");

		List<String> nowShowing = new ArrayList<String>();
		nowShowing.add("The Conjuring");
		nowShowing.add("Despicable Me 2");
		nowShowing.add("Turbo");
		nowShowing.add("Grown Ups 2");
		nowShowing.add("Red 2");
		nowShowing.add("The Wolverine");

		List<String> comingSoon = new ArrayList<String>();
		comingSoon.add("2 Guns");
		comingSoon.add("The Smurfs 2");
		comingSoon.add("The Spectacular Now");
		comingSoon.add("The Canyons");
		comingSoon.add("Europa Report");

		/*for(int i = 0; i<=listDataHeader.size();i++){
			String year = listDataHeader.get(i);

			listDataChild.put(listDataHeader.get(i), top250); // Header, Child data
			listDataChild.put(listDataHeader.get(1), nowShowing);
			listDataChild.put(listDataHeader.get(2), comingSoon);
			listDataChild.put(listDataHeader.get(3), comingSoon);
		}*/

		listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
		listDataChild.put(listDataHeader.get(1), nowShowing);
		listDataChild.put(listDataHeader.get(2), comingSoon);
		listDataChild.put(listDataHeader.get(3), comingSoon);
	}

	private void  SetAdapter(){
		materialAdapter = new MaterialReqAckAdapter(getApplicationContext(), materialmyorderlist);
		listPending.setAdapter(materialAdapter);

		/*prepareListData();
		expadapter = new ExpandableListAdapter_new(MaterialReqAckList.this, listDataHeader, listDataChild);

		// setting list adapter
		expndblorderlist.setAdapter(expadapter);*/
	}

	private String[] splitfrom(String tf) {
		// TODO Auto-generated method stub
		// 12/31/2015 7:05:00 AM---
		String fromtimetw = null;
		String formattedDate = " ";
		// 12/14/2015 12:55:49 PM
		SimpleDateFormat input = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		Date dt;
		try {
			dt = input.parse(tf);

			SimpleDateFormat output = new SimpleDateFormat("dd MMM HH:mm");
			formattedDate = output.format(dt); // contains 18/01/2013
												// 17:00:00
												// String k =
												// formattedDate.substring(formattedDate.indexOf(" "));
			// fromtimetw = k.substring(0, 5); // = "ab"

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String[] v1 = { formattedDate };

		return v1;
	}

	private boolean dbvalue() {
		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();

			Cursor cursor = sql
					.rawQuery(
							"Select distinct pkmaterialid , senderMobNo ,materialname , reason , qty , stationname , stationmasterid , scraprepair ,reporteename , reportingid , statusflag from Myorders ",
							null);

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

	private void fetchdata() {
		asyncfetch_delivered = new DownloadxmlsDataURL();
		asyncfetch_delivered.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		// if (asyncfetch_delivered == null) {
		// iv.setVisibility(View.VISIBLE);
		// ((ProgressBar) findViewById(R.id.progressBar1))
		// .setVisibility(View.GONE);
		//
		// Log.e("async", "null");
		// asyncfetch_delivered = new DownloadxmlsDataURL();
		// asyncfetch_delivered.execute();
		// } else {
		// if (asyncfetch_delivered.getStatus() == AsyncTask.Status.RUNNING) {
		// Log.e("async", "running");
		// iv.setVisibility(View.GONE);
		// ((ProgressBar) findViewById(R.id.progressBar1))
		// .setVisibility(View.VISIBLE);
		// }
		// }
	}

	public class DownloadxmlsDataURL extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {

			String xx = "";

			String url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/Myorders?sendMobileNo=" +mobno;
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
				//sql.execSQL("DROP TABLE IF EXISTS Myorders");
				System.out.println("------------- 3-- ");
				//sql.execSQL(ut.getMyorders());
				sql.delete("Myorders",null,null);

				System.out.println("------------- 4-- ");
				System.out.println("------------- 5-- ");

				Cursor c = sql.rawQuery("SELECT * FROM Myorders", null);
				System.out.println("------------- 6-- ");
				ContentValues values = new ContentValues();
				System.out.println("------------- 7-- ");

				// InstalationId
				if (responsemsg.contains("<pkmaterialid>")) {
					sop = "valid";
					
					NodeList nl = ut.getnode(responsemsg, "Table1");
					Log.e("get Myorders node...", " fetch data : " + nl);
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
							Log.d("test", "values :" + values);
						}

						sql.insert("Myorders", null, values);
						Log.d("test", "Myorders " + values.size());
					}

					c.close();

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
			// pd.cancel();
			try {
				System.out.println("...............value of sop" + sop);
				if (sop.equals("valid")) {

					getDetail();

				} else {
					materialmyorderlist.clear();
					SetAdapter();
					showD("nodata");
				}
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

	}

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(MaterialReqAckList.this);
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
			txt.setText("No Refresh Data Available. Please check internet connection...");
		} else if (string.equals("nodata")) {
			myDialog.setTitle(" ");
			txt.setText("No Data Available.");
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

	private String[] splitDT(String tf) {
		// TODO Auto-generated method stub
		// Oct 13 2015 1:05PM
		if (!(tf.equals("") || tf == null)) {
			// 11/27/2015 3:37:07 PM
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"MM/dd/yyyy hh:mm:ss a");
			Date myDate = null;
			try {
				myDate = dateFormat.parse(tf);
				System.out.println("..........value of my date after conv"
						+ myDate);

			} catch (ParseException e) {
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
			SimpleDateFormat timeFormat = new SimpleDateFormat(
					"MM/dd/yyyy hh:mm");
			finalDate = timeFormat.format(myDate);

		} else {
			finalDate = "";
		}

		String[] v = { finalDate };

		return v;

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//MaterialReqAckList.this.finish();
		/*Intent intent = new Intent(MaterialReqAckList.this,SelectMaterialReqType.class);
		startActivity(intent);*/
		finish();

	}

}
