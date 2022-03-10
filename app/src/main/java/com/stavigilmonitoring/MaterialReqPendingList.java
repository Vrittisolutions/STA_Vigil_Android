package com.stavigilmonitoring;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.adapters.MaterialReqAckAdapter;
import com.beanclasses.MaterialReqAckBean;
import com.database.DBInterface;

public class MaterialReqPendingList extends Activity {
	ArrayList<MaterialReqAckBean> materialPendingBeanslist;
	MaterialReqAckBean materialPendingBean;
	String Type;
	String mobno, link;
	AsyncTask depattask;
	static SimpleDateFormat dff;
	static String Ldate;
	String sop = "no";

	static DownloadxmlsDataURL asyncfetch_delivered;

	MaterialReqAckAdapter materialPendingAdapter;
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
	String finalDate;
	ImageView search_pendingmaterial;
	EditText edt_pendmaterial;
	DatabaseHandler db;
	String sendername;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.activity_materialpending);

		listPending = (ListView) findViewById(com.stavigilmonitoring.R.id.materialreq_pending);
		iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_pending);
		title = (TextView) findViewById(com.stavigilmonitoring.R.id.materialreq_namepending);
		materialPendingBeanslist = new ArrayList<MaterialReqAckBean>();
		search_pendingmaterial = (ImageView)findViewById(com.stavigilmonitoring.R.id.button_filter_pendmaterial);
		edt_pendmaterial = (EditText)findViewById(com.stavigilmonitoring.R.id.edfiter_pendmaterial);

		ut = new com.stavigilmonitoring.utility();

		title.setText("Material Pending List");
		db = new DatabaseHandler(getBaseContext());
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();

		Intent intent = getIntent();
		if (intent.hasExtra("isrefreshed")) {
			if (intent.getStringExtra("isrefreshed").equalsIgnoreCase("true")) {
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

					// Bundle dataBundle = new Bundle();
					// dataBundle.putString("orderHeaderId",
					// materialPendingBeanslist.get(position)
					// .getPkmaterialid());
					// dataBundle.putString("materialname",
					// materialPendingBeanslist.get(position)
					// .getMaterialname());
					// dataBundle.putString("reason", materialPendingBeanslist
					// .get(position).getReason());
					//
					// dataBundle.putString("qty",
					// materialPendingBeanslist.get(position).getQty());
					// dataBundle.putString("stationname",
					// materialPendingBeanslist.get(position)
					// .getStationname());
					// dataBundle.putString("scraprepair",
					// materialPendingBeanslist.get(position)
					// .getScraprepair());
					//
					// dataBundle.putString("addedtdt", materialPendingBeanslist
					// .get(position).getAddedtdt());
					// dataBundle.putBoolean("notification", false);

					Intent myIntent = new Intent();
					myIntent.setClass(getApplicationContext(), MaterialReqPendingDetails.class);

					myIntent.putExtra("orderHeaderId", materialPendingBeanslist
							.get(position).getPkmaterialid());

					myIntent.putExtra("materialname", materialPendingBeanslist
							.get(position).getMaterialname());
					myIntent.putExtra("reason",
							materialPendingBeanslist.get(position).getReason());

					myIntent.putExtra("qty",
							materialPendingBeanslist.get(position).getQty());

					myIntent.putExtra("stationname", materialPendingBeanslist
							.get(position).getStationname());

					myIntent.putExtra("scraprepair", materialPendingBeanslist
							.get(position).getScraprepair());

					myIntent.putExtra("addedtdt",
							materialPendingBeanslist.get(position).getAddedtdt());
					myIntent.putExtra("addedby",
							materialPendingBeanslist.get(position).getAddedby());

					// myIntent.putExtra("notification", false);
					startActivity(myIntent);

				} else {

					Toast.makeText(getBaseContext(),
							"No Information Present..", Toast.LENGTH_LONG)
							.show();
				}

			}
		});

		search_pendingmaterial.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (edt_pendmaterial.getVisibility() == View.VISIBLE) {
					edt_pendmaterial.setVisibility(View.GONE);
					InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
				} else if ((edt_pendmaterial).getVisibility() == View.GONE) {
					edt_pendmaterial.setVisibility(View.VISIBLE);
					EditText textView = edt_pendmaterial;
					textView.requestFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
				}
			}
		});

		edt_pendmaterial.addTextChangedListener(new TextWatcher() {

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
				materialPendingAdapter.filter((edt_pendmaterial)
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

	// pkmaterialid TEXT,senderMobNo TEXT, materialname TEXT, reason TEXT, qty
	// TEXT, stationname TEXT,stationmasterid TEXT, reason1 TEXT," +
	// " scraprepair TEXT, reporteename TEXT, reportingid TEXT,"
	// + " qty1 TEXT, addedtdt TEXT, addedby TEXT, statusflag TEXT,rejectedorder
	// TEXT ,dispatchedorder TEXT
	public void getDetail() {
		materialPendingBeanslist.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		Cursor c = sql
				.rawQuery(
						"Select  distinct pkmaterialid , senderMobNo , materialname , reason  , qty , stationname , addedtdt , scraprepair , statusflag, rejectedorder, dispatchedorder, addedby from PendingRequests",
						null);
		Log.e("PendingRequests ", "" + c.getCount());
		if (c.getCount() == 0) {

			materialPendingBean = new MaterialReqAckBean();
			materialPendingBean.setPkmaterialid("");
			materialPendingBean.setAddedtdt("");
			materialPendingBean.setQty("");
			materialPendingBean.setMaterialname("");
			materialPendingBean.setReason("");
			materialPendingBean.setScraprepair("");
			materialPendingBean.setStationname("");
			materialPendingBean.setStatusflag("");
			materialPendingBean.setRejectedorder("");
			materialPendingBean.setDispatchedorder("");
			materialPendingBean.setAddedby("");
			materialPendingBeanslist.add(materialPendingBean);

			c.close();

			// return arrlist;
		} else {

			c.moveToFirst();

			int column = 0;
			do {

				/*String senderMobNo = c.getString(c.getColumnIndex("senderMobNo"));
				Cursor c1 = sql.rawQuery("select addedby from PendingRequests where senderMobNo="+senderMobNo,
						null);
				if(c1.getCount()>0){
					c1.moveToFirst();
					do{
						sendername = c1.getString(c1.getColumnIndex("addedby"));
					}while (c1.moveToNext());
				}*/

				int column1 = c.getColumnIndex("addedtdt");
				String tf_calibration = c.getString(column1);
				String[] v = splitDT(tf_calibration);
				String materialname = c.getString(c.getColumnIndex("materialname"));
				String stationname = c.getString(c.getColumnIndex("stationname"));
				String pkmaterialid = c.getString(c.getColumnIndex("pkmaterialid"));
				String reason = c.getString(c.getColumnIndex("reason"));
				String statusflag = c.getString(c.getColumnIndex("statusflag"));
				String scraprepair = c.getString(c.getColumnIndex("scraprepair"));
				String qty = c.getString(c.getColumnIndex("qty"));
				String rejectedorder = c.getString(c.getColumnIndex("rejectedorder"));
				String dispatchedorder = c.getString(c.getColumnIndex("dispatchedorder"));
				String addedby = c.getString(c.getColumnIndex("addedby"));

				materialPendingBean = new MaterialReqAckBean();
				materialPendingBean.setPkmaterialid(pkmaterialid);
				materialPendingBean.setAddedtdt(v[0]);
				materialPendingBean.setQty(qty);
				materialPendingBean.setMaterialname(materialname);
				materialPendingBean.setReason(reason);
				materialPendingBean.setScraprepair(scraprepair);
				materialPendingBean.setStationname(stationname);
				materialPendingBean.setStatusflag(statusflag);
				materialPendingBean.setRejectedorder(rejectedorder);
				materialPendingBean.setDispatchedorder(dispatchedorder);
				materialPendingBean.setAddedby(addedby);
				materialPendingBeanslist.add(materialPendingBean);

			} while (c.moveToNext());
			c.close();
		}
		setadapter();
	}
	
	private void setadapter(){
		materialPendingAdapter = new MaterialReqAckAdapter(getApplicationContext(), materialPendingBeanslist);
		listPending.setAdapter(materialPendingAdapter);
	}

	private boolean dbvalue() {
		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();

			Cursor cursor = sql
					.rawQuery(
							"Select distinct materialname, stationname from PendingRequests ",
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

		try {
			asyncfetch_delivered = new DownloadxmlsDataURL();
			asyncfetch_delivered.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} catch (Exception e) {
			showD("noresponse");
		}
	}

	public class DownloadxmlsDataURL extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			String xx = "";
			try {
				String url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/getPendingRequests?Mobileno=" + mobno;
				url = url.replaceAll(" ", "%20");

				System.out.println("============ internet reg url " + url);

				System.out.println("-------  activity url --- " + url);
				responsemsg = ut.httpGet(url);

				System.out.println("-------------  xx vale of non repeated-- "+ responsemsg);
				
				//DatabaseHandler db = new DatabaseHandler(getBaseContext());
				System.out.println("------------- 1-- ");
				SQLiteDatabase sql = db.getWritableDatabase();
				System.out.println("------------- 2-- ");
			//	sql.execSQL("DROP TABLE IF EXISTS PendingRequests");
				System.out.println("------------- 3-- ");
			//	sql.execSQL(ut.getPendingRequests());
				sql.delete("PendingRequests",null,null);

				System.out.println("------------- 4-- ");
				System.out.println("------------- 5-- ");

				Cursor c = sql.rawQuery("SELECT * FROM PendingRequests",
						null);
				System.out.println("------------- 6-- ");
				ContentValues values = new ContentValues();
				System.out.println("------------- 7-- ");

				// InstalationId
				if (responsemsg.contains("<pkmaterialid>")) {
					sop = "valid";
					
					NodeList nl = ut.getnode(responsemsg, "Table1");
					Log.e("get PendingRequests", " fetch data : " + nl);
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

						sql.insert("PendingRequests", null, values);
						Log.d("test", "PendingRequests " + values.size());
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
					materialPendingBeanslist.clear();
					setadapter();
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

		final Dialog myDialog = new Dialog(MaterialReqPendingList.this);
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
			txt.setText("No data available");
		}// noresponse
		else if (string.equals("nodata")) {
			myDialog.setTitle(" ");
			txt.setText("No response from server");
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
		/*MaterialReqPendingList.this.finish();
		Intent intent = new Intent(MaterialReqPendingList.this,SelectMaterialReqType.class);
		startActivity(intent);*/
		finish();
	}

}
