package com.stavigilmonitoring;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.GetPassAdapt;
import com.beanclasses.StateList;

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
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GetpassList extends Activity {
	GridView list;
	ImageView iv;
	TextView tvpwd;
	com.stavigilmonitoring.utility ut;
	List<StateList> searchResults = new ArrayList<StateList>();
	GetPassAdapt listAdapter;
	static SimpleDateFormat dff;
	static String Ldate;
	String InstallationId;
	DatabaseHandler db;
	String mobno;
	String sop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.getpasswordlist);

		Intent intent = getIntent();
		mobno = intent.getStringExtra("mobno");

		iv = (ImageView) findViewById(R.id.filter);
		list = findViewById(R.id.getpasslist);

		ut = new com.stavigilmonitoring.utility();
		db = new DatabaseHandler(getBaseContext());

		if (getdbvalue()) {
			updatelist();
		} else if (isnet()) {
			fetchdata();
		} else {
			showD("nonet");
		}

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent i = new Intent();
				/*
				 * String station=(String) (list.getItemAtPosition(position));
				 * InstallationId=sitem.GetInstallationId();
				 */
				String station = searchResults.get(position).getStatioName();
				InstallationId = searchResults.get(position).getInstallationId();
				i.putExtra("station", station);
				i.putExtra("InstallationId", InstallationId);
				setResult(Activity.RESULT_OK, i);
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
						// listAdapter=new
						// DowntimeAdaptMain(DowntimeMain.this,arrlist);
						String txt = ((EditText) findViewById(R.id.edfitertext))
								.getText().toString().trim()
								.toLowerCase(Locale.getDefault());
						Log.e("search txt", txt);
						listAdapter.filter(txt);
					}
				});

	}

	private boolean isnet() {
		// TODO Auto-generated method stub
		Context context = this.getApplicationContext();
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(GetpassList.this);
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
			txt.setText("No Refresh data Available. Please check internet connection...");
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

	private boolean getdbvalue() {
		try {
			// TODO Auto-generated method stub
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor1 = sql.rawQuery("SELECT * FROM  AllStation", null);
			if (cursor1 != null && cursor1.getCount() > 0) {


				cursor1.close();
				return true;

			} else {

				//sql.close();
				cursor1.close();
				//db1.close();
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
		getDetail();
	}

	private void fetchdata() {
		GetStation stations = new GetStation();
		stations.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		// GetStationPassword1 g1= new GetStationPassword1();
		// g1.execute();

	}

	public void getDetail() {
		// searchResults.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();

		//Cursor c1 = sql.rawQuery("select * from AllStation", null);

		Cursor c1 = sql.rawQuery("Select * from ConnectionStatusFiltermob ORDER BY InstallationDesc ASC",null);
		Log.e("Downtime Stn clmn cnt", "" + c1.getCount());
		if (c1.getCount() > 0) {
			c1.moveToFirst();
			do {
				String Station = c1.getString(c1.getColumnIndex("InstallationDesc"));
				String InsatallationId = c1.getString(c1.getColumnIndex("InstalationId"));

				if (!InsatallationId.trim().equalsIgnoreCase("")) {
					StateList sitem = new StateList();
					sitem.setStatioName(Station);
					sitem.setInstallationId(InsatallationId);
					searchResults.add(sitem);
				}
			} while (c1.moveToNext());
		}

		listAdapter = new GetPassAdapt(GetpassList.this, searchResults);
		list.setAdapter(listAdapter);
	}

	public void FilterClick(View v) {
		if (((EditText) findViewById(R.id.edfitertext)).getVisibility() == View.VISIBLE) {
			((EditText) findViewById(R.id.edfitertext)).setVisibility(View.GONE);
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(/*getCurrentFocus().getWindowToken()*/this.getWindow().getDecorView().getRootView().getWindowToken(), 0);
		} else if (((EditText) findViewById(R.id.edfitertext)).getVisibility() == View.GONE) {
			((EditText) findViewById(R.id.edfitertext)).setVisibility(View.VISIBLE);
			EditText textView = (EditText) findViewById(R.id.edfitertext);
			textView.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
		}
	}

	public class GetStation extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String result1 = "Invalid";
			// Log.e("GetStationPassword",""+params[0]);
			String result = "";
			//String url = "http://sta.vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetAllStation_Android";
			String url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMasterMobile?Mobile="+mobno;

			url = url.replaceAll(" ", "%20");
			try {
				System.out.println("-------  activity url --- " + url);
				result = ut.httpGet(url);

				/*if (result.contains("<InstallationId>")) {

					//DatabaseHandler db = new DatabaseHandler(getBaseContext());
					System.out.println("------------- 1-- ");
					SQLiteDatabase sql = db.getWritableDatabase();
					//sql.execSQL("DROP TABLE IF EXISTS AllStation");
					//sql.execSQL(ut.getAllStation());
					sql.delete("AllStation",null,null);

					Cursor c = sql.rawQuery("SELECT *   FROM AllStation", null);
					ContentValues values = new ContentValues();
					NodeList nl = ut.getnode(result, "Table1");
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
							result1 = "valid";

						}
						sql.insert("AllStation", null, values);
					}
					c.close();
				}*/

				if (result.contains("<InstalationId>")) {
					sop = "valid";
					//  DatabaseHandler db = new DatabaseHandler(getBaseContext());
					SQLiteDatabase sql = db.getWritableDatabase();
					String columnName, columnValue;
					// sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusFiltermob");
					// sql.execSQL(ut.getConnectionStatusFiltermob());
					sql.delete("ConnectionStatusFiltermob",null,null);

					Cursor cur1 = sql.rawQuery(
							"SELECT * FROM ConnectionStatusFiltermob", null);
					cur1.getCount();
					ContentValues values2 = new ContentValues();
					NodeList nl2 = ut.getnode(result, "Table");

					Log.e("All Station Data ", "get length : " + nl2.getLength());
					for (int i = 0; i < nl2.getLength(); i++) {
						Log.e("All Station Data ", "length : " + nl2.getLength());
						Element e = (Element) nl2.item(i);
						for (int j = 0; j < cur1.getColumnCount(); j++) {
							columnName = cur1.getColumnName(j);
							columnValue = ut.getValue(e, columnName);
							Log.e("All Station Data ", "column Name : "
									+ columnName);
							Log.e("All Station Data ", "column value : "
									+ columnValue);

							values2.put(columnName, columnValue);

						}
						sql.insert("ConnectionStatusFiltermob", null, values2);
					}

					cur1.close();

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
			return result1;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				updatelist();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

		}
	}

	/*class StatList {
		String StationName, InstallationId;

		public void SetStationName(String StationName) {
			this.StationName = StationName;
		}

		public void SetInstallationId(String InstallationId) {
			this.InstallationId = InstallationId;
		}

		public String GetInstallationId() {
			return this.InstallationId;
		}

		public String GetStationName() {
			return this.StationName;
		}

	}*/

}
