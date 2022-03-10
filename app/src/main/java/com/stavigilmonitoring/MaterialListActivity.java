package com.stavigilmonitoring;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.SelectDeliveryListAdapter;
import com.beanclasses.MaterialListBean;

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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MaterialListActivity extends Activity {

	String MobileNo;
	static SimpleDateFormat dff;
	static String Ldate;
	ArrayList<MaterialListBean> searchResults;
	ImageView iv;
	String sop, responsemsg, mobno;
	DownloadxmlsDataURL_new asyncfetch_csnstate;
	GridView lstcsn;
	com.stavigilmonitoring.utility ut;
	TextView materialreqlist_name;
	SelectDeliveryListAdapter selectDeliveryListAdapter;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_material_list);

		Intent intent = getIntent();
		searchResults = new ArrayList<MaterialListBean>();
		lstcsn = findViewById(R.id.materialreq_list);
		iv = (ImageView) findViewById(R.id.button_refresh_materiallist);
		materialreqlist_name = (TextView) findViewById(R.id.materialreqlist_name);
		materialreqlist_name.setText("Material List");

		db = new DatabaseHandler(getApplicationContext());

		if (asyncfetch_csnstate != null
				&& asyncfetch_csnstate.getStatus() == AsyncTask.Status.RUNNING) {
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

		lstcsn.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent intent = new Intent();
				String cashiername = lstcsn.getItemAtPosition(position).toString();
				intent.putExtra("MaterialName", searchResults.get(position).getItemdesc());
				intent.putExtra("MaterialID",searchResults.get(position).getItemmasterid());
				setResult(Common.MaterialName, intent);
				finish();
			}
		});

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

						selectDeliveryListAdapter
								.filter(((EditText) findViewById(R.id.edfitertext))
										.getText().toString().trim()
										.toLowerCase(Locale.getDefault()));
						// getDetail_filter();
						// soundlevelAdapter
						// .filter(((EditText) findViewById(R.id.edfitertext))
						// .getText().toString().trim()
						// .toLowerCase(Locale.getDefault()));
					}
				});
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isnet()) {
					asyncfetch_csnstate = null;
					asyncfetch_csnstate = new DownloadxmlsDataURL_new();
					asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					// fetchdata();
				} else {
					showD("nonet");
				}
			}
		});
	}

	public void FilterClick(View v) {
		if (((EditText) findViewById(R.id.edfitertext)).getVisibility() == View.VISIBLE) {
			((EditText) findViewById(R.id.edfitertext))
					.setVisibility(View.GONE);
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		} else if (((EditText) findViewById(R.id.edfitertext)).getVisibility() == View.GONE) {
			((EditText) findViewById(R.id.edfitertext))
					.setVisibility(View.VISIBLE);
			EditText textView = (EditText) findViewById(R.id.edfitertext);
			textView.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
		}

	}

	public void editActivity(String s) {

		System.out.println("==========@#@# actid " + s);
		//DatabaseHandler db1 = new DatabaseHandler(this);
		SQLiteDatabase sqldb = db.getWritableDatabase();
		String[] params = new String[1];
		params[0] = s;
		Cursor c2 = sqldb.rawQuery("SELECT * FROM MaterialReqList where itemdesc=? ", params);
		String itemdesc = "";

		if (c2.getCount() == 0) {
			c2.close();

		} else {

			c2.moveToFirst();
			itemdesc = c2.getString(c2.getColumnIndex("itemdesc"));

			c2.moveToLast();

			c2.close();

		}

		// MOMA --- mom attend request
		// System.out.println("----------  type --- " + type);

		// Bundle dataBundle = new Bundle();
		// dataBundle.putString("stnname", stnname);
		// // dataBundle.putString("ActivityName", ActivityName);
		//
		// // finish();
		// Intent myIntent = new Intent();
		// myIntent.setClass(getApplicationContext(), SoundLevel.class);
		//
		// myIntent.putExtras(dataBundle);
		// startActivity(myIntent);

		Intent intent = new Intent();

		intent.putExtra("MaterialName", itemdesc);
		setResult(Common.MaterialName, intent);
		finish();

		// finish();
		// System.out.println("------------- 1");

	}

	private boolean dbvalue() {

		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery("SELECT * FROM MaterialReqList", null);

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

	private void fetchdata() {
		// new DownloadxmlsDataURL_new().execute();
		if (asyncfetch_csnstate == null) {
			iv.setVisibility(View.VISIBLE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.GONE);

			Log.e("async", "null");
			asyncfetch_csnstate = new DownloadxmlsDataURL_new();
			asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			if (asyncfetch_csnstate.getStatus() == AsyncTask.Status.RUNNING) {
				Log.e("async", "running");
				iv.setVisibility(View.GONE);
				((ProgressBar) findViewById(R.id.progressBar1))
						.setVisibility(View.VISIBLE);
			}
		}
	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			String url;

			sop = "valid";
			String columnName, columnValue;

			url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/getmateriallist";

			Log.e("material", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = ut.httpGet(url);
				Log.e("material", "resmsg : " + responsemsg);

				if (responsemsg.contains("<NewDataSet>")) {
					sop = "valid";

					//sql.execSQL("DROP TABLE IF EXISTS MaterialReqList");
					//sql.execSQL(ut.getMaterialReqList());
					sql.delete("MaterialReqList",null,null);

					Cursor cur1 = sql.rawQuery("SELECT * FROM MaterialReqList",
							null);
					ContentValues values2 = new ContentValues();
					NodeList nl2 = ut.getnode(responsemsg, "Table1");

					Log.e("calliberation data...",
							" fetch data : " + nl2.getLength());
					for (int i = 0; i < nl2.getLength(); i++) {
						Element e = (Element) nl2.item(i);
						for (int j = 0; j < cur1.getColumnCount(); j++) {
							columnName = cur1.getColumnName(j);
							columnValue = ut.getValue(e, columnName);
							values2.put(columnName, columnValue);
						}
						sql.insert("MaterialReqList", null, values2);
						Log.d("test", "MaterialReqList " + values2.size());
					}
					cur1.close();
				}

				else {
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

			} catch (UnknownHostException e) {
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
			super.onPostExecute(result);
			try {
				if (sop.equals("valid")) {
					updatelist();
				} else {
					showD("invalid");
				}
				iv.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(R.id.progressBar1))
						.setVisibility(View.GONE);

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

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}

	}

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(MaterialListActivity.this);
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

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();

		intent.putExtra("MaterialName", "");
		setResult(Common.MaterialName, intent);
		finish();
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

	private void updatelist() {
		searchResults.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		int count = 0;
		Cursor c = sql.rawQuery("SELECT * FROM MaterialReqList", null);
		if (c.getCount() > 0) {
			c.moveToFirst();
			do {

				String itemmasterid = c.getString(c
						.getColumnIndex("itemmasterid"));
				String itemdesc = c.getString(c.getColumnIndex("itemdesc"));

				MaterialListBean materialListBean = new MaterialListBean();
				materialListBean.setItemmasterid(itemmasterid);
				materialListBean.setItemdesc(itemdesc);
				searchResults.add(materialListBean);

			} while (c.moveToNext());

		}

		selectDeliveryListAdapter = new SelectDeliveryListAdapter(MaterialListActivity.this, searchResults);
		lstcsn.setAdapter(selectDeliveryListAdapter);

	}

}
