package com.stavigilmonitoring;

import java.io.IOException;
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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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

import com.adapters.AlertStnReqMainAdapter;
import com.adapters.SoundlevelAdapter;
import com.beanclasses.SoundlevelBean;
import com.beanclasses.StateList;
import com.database.DBInterface;

public class AlertStnReqMain extends Activity {

	String Type;
	String mobno, link;
	AsyncTask depattask;
	static SimpleDateFormat dff;
	static String Ldate;
	String sop = "no";

	ArrayList<StateList> searchResults;
	SoundlevelBean soundlevelBean;

	static DownloadStation asyncfetch_csnstate;

	SoundlevelAdapter soundlevelAdapter;
	String resposmsg = "k";
	String ActivityName, ActivityId, actname;
	com.stavigilmonitoring.utility ut;
	private TextView txtdaterefresh;
	String daterestr;
	public String filter;
	AlertStnReqMainAdapter materialReqMainAdapter;
	// ArrayList<String> arrlist = new ArrayList<String>();
	TextView title;
	ImageView iv;
	private GridView lstcsn;
	String conn = "invalid";
	String finalDate;
	Context parent;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_material_detail_main);
		parent = AlertStnReqMain.this;
		lstcsn =  findViewById(R.id.materialreq_main);
		iv = (ImageView) findViewById(R.id.button_refresh_connection_main);
		title = (TextView) findViewById(R.id.materialreq);
		searchResults = new ArrayList<StateList>();

		Intent intent = getIntent();
		ut = new com.stavigilmonitoring.utility();
		Type = intent.getStringExtra("Type");
		title.setText(Type + "- Alert Station");

		db = new DatabaseHandler(parent);

		DBInterface dbi = new DBInterface(parent);
		mobno = dbi.GetPhno();

		if (asyncfetch_csnstate != null
				&& asyncfetch_csnstate.getStatus() == AsyncTask.Status.RUNNING) {
			Log.e("async", "running");
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}
		Log.e("Sound level", " dbval : " + dbvalue());

		if (dbvalue()) {
			updatelist();
		} else if (isnet()) {
			fetchdata();
		} else {
			try{
				showD("nonet");
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		((EditText) findViewById(R.id.edfitertext_search))
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

						filter = s.toString().trim();
						materialReqMainAdapter.filter((filter)
								.toLowerCase(Locale.getDefault()));

					}
				});
		lstcsn.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				SharedPreferences pref = getBaseContext().getSharedPreferences("AlertPref", Context.MODE_PRIVATE);
				Editor editor = pref.edit();
				editor.putString("alertmaterialreq", searchResults.get(position)
						.getStatioName().toString());
				editor.putString("alertmaterialreq_id", searchResults.get(position)
						.getInstallationId().toString());
				editor.commit();

				Intent intent = new Intent();
				intent.putExtra("StatioNameID", searchResults.get(position)
						.getInstallationId());
				intent.putExtra("StatioName", searchResults.get(position)
						.getStatioName());
				setResult(Common.AlertStn2, intent);
				finish();
			}
		});

		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isnet()) {
					asyncfetch_csnstate = null;
					asyncfetch_csnstate = new DownloadStation();
					asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					// fetchdata();
				} else {
					showD("nonet");
				}
			}
		});
	}

	public void FilterClick(View v) {
		if (((EditText) findViewById(R.id.edfitertext_search)).getVisibility() == View.VISIBLE) {
			((EditText) findViewById(R.id.edfitertext_search))
					.setVisibility(View.GONE);
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		} else if (((EditText) findViewById(R.id.edfitertext_search))
				.getVisibility() == View.GONE) {
			((EditText) findViewById(R.id.edfitertext_search))
					.setVisibility(View.VISIBLE);
			EditText textView = (EditText) findViewById(R.id.edfitertext_search);
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
		Cursor c2 = sqldb.rawQuery(
				"SELECT * FROM SoundLevel where StationName='"+params+"'", null);
		String stnname = "";

		if (c2.getCount() == 0) {
			c2.close();
			/*db.close();
			db1.close();*/

		} else {

			c2.moveToFirst();
			stnname = c2.getString(c2.getColumnIndex("StationName"));

			c2.moveToLast();

			c2.close();
			/*db.close();
			db1.close();*/

		}

		// MOMA --- mom attend request
		// System.out.println("----------  type --- " + type);

		Bundle dataBundle = new Bundle();
		dataBundle.putString("stnname", stnname);
		// dataBundle.putString("ActivityName", ActivityName);

		// finish();
		Intent myIntent = new Intent();
		myIntent.setClass(parent, AlrtCreateActivity.class);

		myIntent.putExtras(dataBundle);
		startActivity(myIntent);
		// finish();
		// System.out.println("------------- 1");

	}

	private boolean dbvalue() {

		try {
			//DatabaseHandler db1 = new DatabaseHandler(parent);
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery(
					"Select distinct InstallationDesc from ConnectionStatusFiltermob Where NetworkCode='"
							+ Type + "'", null);// SoundLevel_new

			System.out.println("----------  dbvalue screen cursor count -- "
					+ cursor.getCount());

			if (cursor != null && cursor.getCount() > 0) {

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
			asyncfetch_csnstate = new DownloadStation();
			asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			if (asyncfetch_csnstate.getStatus() == AsyncTask.Status.RUNNING) {
				Log.e("async", "running");
				iv.setVisibility(View.GONE);
				((ProgressBar) findViewById(R.id.progressBar1)).setVisibility(View.VISIBLE);
			}
		}

	}

	public class DownloadStation extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			String Url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMasterMobile?Mobile="+mobno;
			Log.e("All Station", "Url=" + Url);

			try {
				resposmsg = ut.httpGet(Url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
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

			if (resposmsg.contains("<InstalationId>")) {
				sop = "valid";
				//DatabaseHandler db = new DatabaseHandler(parent);
				SQLiteDatabase sql = db.getWritableDatabase();
				String columnName, columnValue;
				//sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusFiltermob");
				//sql.execSQL(ut.getConnectionStatusFiltermob());
				sql.delete("ConnectionStatusFiltermob",null,null);

				Cursor cur1 = sql.rawQuery(
						"SELECT * FROM ConnectionStatusFiltermob", null);
				cur1.getCount();
				ContentValues values2 = new ContentValues();
				NodeList nl2 = ut.getnode(resposmsg, "Table");

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
				/*sql.close();
				db.close();*/

			} else {
				sop = "invalid";
				System.out.println("--------- invalid for project list --- ");
			}
			return sop;

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				if (sop.equals("valid")) {
					updatelist();
				} else {
					ut.showD(parent, "invalid");
				}
				iv.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(R.id.progressBar1))
						.setVisibility(View.GONE);

			} catch (Exception e) {
				e.printStackTrace();
				// dff = new SimpleDateFormat("HH:mm:ss");
				// Ldate = dff.format(new Date());

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

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(AlertStnReqMain.this);
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
		Intent intent = new Intent(AlertStnReqMain.this,AlrtCreateActivity.class);
		startActivity(intent);		
		finish();
	}

	private boolean isnet() {
		Context context = this.parent;
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
		/*Cursor c = sql.rawQuery(
				"Select distinct StatioName,InstallationId from AllStation Where NetworkCode='"
						+ Type + "'", null);*/
		try{
			Cursor c = sql
					.rawQuery(
							"Select distinct InstallationDesc,InstalationId from ConnectionStatusFiltermob Where NetworkCode='"
									+ Type + "' Order by InstallationDesc",
							null);
			//,InstallationId
			if (c.getCount() > 0) {
				c.moveToFirst();
				do {

					String StationName = c
							.getString(c.getColumnIndex("InstallationDesc"));
					String	InstallationId = c.getString(c.getColumnIndex("InstalationId"));
					count = c.getCount();

					// Type = Type.replaceAll("0", "");
					// Type = Type.replaceAll("1", "");
					// if (!Type.trim().equalsIgnoreCase("")) {
					StateList sitem = new StateList();
					sitem.SetNetworkCode(Type);
					sitem.setStatioName(StationName);
					sitem.setInstallationId(InstallationId);
					sitem.Setcount(count);
					searchResults.add(sitem);

					// }
				} while (c.moveToNext());

			}

			materialReqMainAdapter = new AlertStnReqMainAdapter(parent, searchResults);
			lstcsn.setAdapter(materialReqMainAdapter);
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	/*class StateList {
		String Networkcode;
		String InstallationId;
		String StatioName;
		int count;

		public StateList() {
		}

		public String getStatioName() {
			return StatioName;
		}

		public void setStatioName(String statioName) {
			StatioName = statioName;
		}

		public void setInstallationId(String InstallationId) {
			this.InstallationId = InstallationId;
		}

		public String getInstallationId() {
			return InstallationId;
		}

		public void SetNetworkCode(String Networkcode) {
			this.Networkcode = Networkcode;
		}

		public String getNetworkcode() {
			return Networkcode;
		}

		public void Setcount(int count) {
			this.count = count;
		}

		public int Getcount() {
			return count;
		}
	}*/
}
