package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.MaterialReqStatewiseAdapter;
import com.beanclasses.StateList;
import com.database.DBInterface;

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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MaterialReqStatewiseActivity extends Activity {
	String MobileNo;
	static SimpleDateFormat dff;
	static String Ldate;
	List<StateList> searchResults;
	ImageView iv;
	String sop, responsemsg, mobno;
	DownloadxmlsDataURL_new asyncfetch_csnstate;
	GridView lstcsn;
	com.stavigilmonitoring.utility ut;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_material_req_statewise);
		Intent intent = getIntent();

		MobileNo = intent.getStringExtra("mobileno");
		searchResults = new ArrayList<StateList>();
		lstcsn = findViewById(R.id.materialreq_statewise);
		iv = (ImageView) findViewById(R.id.button_refresh_list);

		db = new DatabaseHandler(getBaseContext());

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();

		if (asyncfetch_csnstate != null
				&& asyncfetch_csnstate.getStatus() == AsyncTask.Status.RUNNING) {
			Log.e("async", "running");
			iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}
		// Log.e("Sound level"," dbval : "+dbvalue());

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

		lstcsn.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {/*******/
				String NetCode = searchResults.get(position).getNetworkcode();
				/*if (NetCode.equalsIgnoreCase("LMS")){
					Intent i = new Intent(MaterialReqStatewiseActivity.this,
							LMSNetworkCodeActivity.class);
					i.putExtra("Type", searchResults.get(position).getNetworkcode());
					startActivity(i);
					finish();
				}else{*/
				Intent i = new Intent(MaterialReqStatewiseActivity.this, MaterialReqMain.class);

				i.putExtra("Type", searchResults.get(position).getNetworkcode());
				startActivityForResult(i, Common.MaterialStn2);
				//startActivity(i);
				//finish();
				//}
				
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
					try{
						showD("nonet");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
			}
		});
	}

	private boolean dbvalue() {

		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery(
					"SELECT DISTINCT NetworkCode FROM AllStation", null);// SoundLevel_new

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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		try{
			if (requestCode == Common.MaterialStn2) {
				String MaterialStation = data.getStringExtra("StatioName");
				//ButtonStationNmae.setText(MaterialStation);
				String stationid=data.getStringExtra("StatioNameID");
				//ButtonMaterialName.setText(data.getStringExtra("MaterialName"));
				Intent intent = new Intent();

				intent.putExtra("StatioNameID", stationid);
				intent.putExtra("StatioName", MaterialStation);
				setResult(Common.MaterialStn1, intent);
				finish();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();

			sop = "valid";
			String columnName, columnValue;
			String url = "http://vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetAllStation_Android";

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = com.stavigilmonitoring.utility.httpGet(url);
				Log.e("csn status", "resmsg : " + responsemsg);
			} catch (NullPointerException e) {
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

			} catch (IOException e) {
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

			if (responsemsg.contains("<NetworkCode>")) {

				sop = "valid";

				//sql.execSQL("DROP TABLE IF EXISTS AllStation");
				//sql.execSQL(ut.getAllStation());
				sql.delete("AllStation",null,null);

				Cursor cur = sql.rawQuery("SELECT * FROM AllStation", null);
				ContentValues values1 = new ContentValues();
				NodeList nl1 = ut.getnode(responsemsg, "Table1");
				// String msg = "";
				// String columnName, columnValue;
				Log.e("All Station data...", " fetch data : " + nl1.getLength());
				for (int i = 0; i < nl1.getLength(); i++) {
					Element e = (Element) nl1.item(i);
					for (int j = 0; j < cur.getColumnCount(); j++) {
						columnName = cur.getColumnName(j);

						columnValue = ut.getValue(e, columnName);
						values1.put(columnName, columnValue);

						
					}
					sql.insert("AllStation", null, values1);
				}
			}

			else {
				sop = "invalid";
				System.out.println("--------- invalid for project list --- ");
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
					try{
						showD("invalid");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				iv.setVisibility(View.VISIBLE);
				((ProgressBar) findViewById(R.id.progressBar1))
						.setVisibility(View.GONE);

			} catch (Exception e) {
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

		final Dialog myDialog = new Dialog(MaterialReqStatewiseActivity.this);
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
		Cursor c = sql.rawQuery("SELECT DISTINCT NetworkCode FROM AllStation",
				null);
		if (c.getCount() > 0) {
			c.moveToFirst();
			do {

				String Type = c.getString(0);

				Cursor c1 = sql.rawQuery(
						"Select distinct StatioName from AllStation Where NetworkCode='"
								+ Type + "'", null);
				count = c1.getCount();
				

				Type = Type.replaceAll("0", "");
				Type = Type.replaceAll("1", "");
				if (!Type.trim().equalsIgnoreCase("")) {
					StateList sitem = new StateList();
					sitem.SetNetworkCode(Type);
					sitem.Setcount(count);
					searchResults.add(sitem);

				}
			} while (c.moveToNext());

		}
		lstcsn.setAdapter(new MaterialReqStatewiseAdapter(
				MaterialReqStatewiseActivity.this, searchResults));
	}

	@Override
	public void onBackPressed() {
		/*Intent i = new Intent(getBaseContext(), MaterialRequest.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getBaseContext().startActivity(i);*/
		finish();

	}

}
