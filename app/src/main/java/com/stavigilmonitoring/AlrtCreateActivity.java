package com.stavigilmonitoring;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.database.DBInterface;
import com.services.GPSTracker;
import com.stavigilmonitoring.AlrtListActivity.AlrtListURL;
import com.stavigilmonitoring.utility;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static com.stavigilmonitoring.utility.OpenConnection;

public class AlrtCreateActivity extends Activity {

	private TextView tvhead;
	String urlStringToken="", urlStringToken2="";
	private Button BtnSave, BtnRtn, BtnItemName;
	private ImageView BtnDelete;
	private EditText EdtSTN, EdtDesc;
	int flag = 0;
	Context parent;
	//static String Ldate;
	String edtSTN, edtDesc, mobno, installationid, mType;
	DownloadxmlsDataURL_new asyncfetch_csnstate;
	AlrtListURL asynk;
	DownloadnetWork_New async_new;
	static SimpleDateFormat dff;
	com.stavigilmonitoring.utility ut;
	private ImageView btnRefresh;
	private ProgressBar mprogressBar;
	static String Ldate;
	String responsemsg = "k", MaterialStation, stationid, sop,resposmsg;
	DatabaseHandler db;
	GPSTracker gps;
	double sup_latitude, sup_longitude, stn_latitude, stn_longitude;
	Spinner spinner_alert;
	String Alert_Type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.alertcreatealert);

		parent = AlrtCreateActivity.this;
		SharedPreferences prefmaterial = parent.getSharedPreferences("AlertPref", Context.MODE_PRIVATE);
		Editor editorMaterial = prefmaterial.edit();
		//String MaterialStation = prefmaterial.getString("alertmaterialreq", "");
		//stationid=prefmaterial.getString("alertmaterialreq_id", "");
		initView();

		EdtSTN.setText(MaterialStation);
		setListeners();
		
	}

	private void setListeners() {
		// TODO Auto-generated method stub
		EdtSTN.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AlrtCreateActivity.this,
						AlertStnReqStatewiseActivity.class);
				intent.putExtra("mobileno", mobno);
				startActivityForResult(intent, Common.AlertStn1);
				/*startActivity(intent);
				finish();*/
			}
		});
		
		BtnRtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AlrtCreateActivity.this,AlrtListActivity.class);
				startActivity(intent);
				AlrtCreateActivity.this.finish();				
			}
		});
		
		BtnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isvalid()) {

					//test whether GPS is on or not
					CheckGPSAvailable();
					//get station location and compare both locations if both get matched then open station visit form
				//	new Download_StationLocation().execute();

					createALert();
				}
			}

		});

		spinner_alert.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Alert_Type=parent.getSelectedItem().toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == Common.AlertStn1) {
			try{
				String MaterialStation = data.getStringExtra("StatioName");
				EdtSTN.setText(MaterialStation);
				stationid=data.getStringExtra("StatioNameID");
				mType = MaterialStation;
				installationid = stationid;
				//ButtonMaterialName.setText(data.getStringExtra("MaterialName"));
			}catch (Exception e){
				e.printStackTrace();
			}
		}
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
	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(AlrtCreateActivity.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoginfosmall);
		myDialog.setCancelable(true);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM); Data is already exist....!

		TextView txt = (TextView) myDialog
				.findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall);
		if (string.equals("empty")) {
			myDialog.setTitle("Error...");
			txt.setText("Please Fill required data..");
		} else if (string.equals("Data is already exist....!")) {
				myDialog.setTitle("Error...");
				txt.setText("Data is already exist for this Station....!");
		} else if (string.equals("nonet")) {
			myDialog.setTitle("Error...");
			txt.setText("No Internet Connection Found.Please Activate internet Connectin on Device..");
		} else if (string.equals("invalid")) {
			myDialog.setTitle(" ");
			txt.setText("No Refresh Data Available.Please check internet connection...");
		} else if (string.equals("Error")) {
			myDialog.setTitle(" ");
			txt.setText("Server Error.. Please try after some time");
		} else if (string.equals("Done")) {
			myDialog.setTitle(" ");
			txt.setText("Alert send successfully");
		}

		Button btn = (Button) myDialog
				.findViewById(com.stavigilmonitoring.R.id.gotobtndialoginfosmall);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				myDialog.dismiss();
				if (flag == 5){
				Intent intent = new Intent(AlrtCreateActivity.this,AlrtListActivity.class);
				startActivity(intent);
				AlrtCreateActivity.this.finish();
				}
				// finish();

			}
		});

		myDialog.show();

	}
	protected boolean isvalid() {
		// TODO Auto-generated method stub
		if (!(EdtSTN.getText().toString().length() > 0)) {
			Toast.makeText(AlrtCreateActivity.this, "Please Enter Station Name",
					Toast.LENGTH_LONG).show();
			return false;
		} else if (!(EdtDesc.getText().toString().length() > 0)) {
			Toast.makeText(AlrtCreateActivity.this, "Please Enter Alert Description.",
					Toast.LENGTH_LONG).show();
			return false;
		} else{
			return true;
			}		
	}
	private void initView() {
		// TODO Auto-generated method stub
		tvhead = (TextView) findViewById(com.stavigilmonitoring.R.id.stationInvent);
		btnRefresh =(ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_invent);
		mprogressBar = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressinvent);
		//BtnItemName = (Button) findViewById(R.id.editTextItemName);
		BtnSave = (Button) findViewById(com.stavigilmonitoring.R.id.button_save);
		EdtSTN = (EditText) findViewById(com.stavigilmonitoring.R.id.editTextSTNName);
		EdtDesc = (EditText) findViewById(com.stavigilmonitoring.R.id.editTextAlertDesc);
		BtnRtn = (Button) findViewById(com.stavigilmonitoring.R.id.button_return);
		BtnDelete = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_invent_delete);
		spinner_alert = (Spinner) findViewById(R.id.spinner_alert);
		BtnDelete.setVisibility(View.GONE);
		Intent intent = getIntent();

		db = new DatabaseHandler(AlrtCreateActivity.this);
		ut = new utility();
		//mType = MaterialStation;
		//installationid = stationid;
		//EdtSTN.setClickable(false);
	}
	
	public class DownloadxmlsDataURL_new extends AsyncTask<String, Void, String> {
		ProgressDialog progressDialog;
		String url;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			 btnRefresh.setVisibility(View.GONE);
			
					mprogressBar.setVisibility(View.VISIBLE);
					flag= 0;

					progressDialog = new ProgressDialog(AlrtCreateActivity.this);
					progressDialog.setMessage("Processing...");
					progressDialog.setCanceledOnTouchOutside(false);
					progressDialog.setCancelable(false);
					progressDialog.show();
					
		}
		@Override
		protected String doInBackground(String... params) {
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();

			//currDate = system.get

			try {
				url ="http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/AlertInsert?"+"AddedBy="
						+ mobno
						+ "&AddedDt="
						+ ""
						+ "&StationName="
						+ edtSTN
						+ "&InstallationId="
						+ installationid
						+ "&AlertDesc="
						+ URLEncoder.encode(edtDesc,"UTF-8")
						+ "&AlertType="
						+ Alert_Type;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}


			Log.e("material ", "url : " + url);
				url = url.replaceAll(" ", "%20");
			try {
				System.out.println("-------  activity url --- " + url);
				responsemsg = ut.httpGet(url);

				System.out.println("-------------  xx vale-- " + responsemsg);
				Log.e("Response",responsemsg);

				/*responsemsg = responsemsg
						.substring(responsemsg.indexOf(">") + 1);
				responsemsg = responsemsg
						.substring(responsemsg.indexOf(">") + 1);
				responsemsg = responsemsg
						.substring(0, responsemsg.indexOf("<"));
				Log.e("Nantar response",responsemsg);*/

			} catch (NullPointerException e) {
				responsemsg = "Error";
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

				responsemsg = "Error";
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

			return responsemsg;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				Log.e("Post madhe responsemsg", responsemsg);
				
				if (responsemsg.equals("Error")) {
					showD("Error");
					mprogressBar
							.setVisibility(View.GONE);
					// Toast.makeText(MaterialRequest.this,
					// "Server Error...Please try after some time",
					// Toast.LENGTH_LONG).show();
				} else if (responsemsg.equals("Not saved")) {
					showD("Error");
					mprogressBar.setVisibility(View.GONE);
				} else if (responsemsg.contains("Data is already exist....!")) {
					
					flag=2;
					async_new = null;
					async_new = new DownloadnetWork_New();
					async_new.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					//showD("Data is already exist....!");
					mprogressBar.setVisibility(View.GONE);
				} else if (responsemsg.contains("Data Saved")) {
					// updateNotification(true);
					flag = 1;
					async_new = null;
					async_new = new DownloadnetWork_New();
					async_new.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

					/*try {
						//Common.TOKEN = registerGCM();
						//Common.UserName = GetUserName();
						*//*JSONObject jsonObject = new JSONObject();
						jsonObject.put("Pkg_name", "com.stavigilmonitoring");
						jsonObject.put("Mobile", strmobileno);
						jsonObject.put("UserName", Common.UserName);
						jsonObject.put("Device_Id", Common.TOKEN);
						paramToken=jsonObject.toString();*//*
						urlStringToken = "http://ccs.ekatm.com" //AdatSoftData.URL
								+ "/api/AddNotification?"// AdatSoftData.METHOD_SAVE_DATA
								+	"sessionId=" + "vigil"// AdatSoftData.SESSION_ID
								+ "&handler=" + "0" //AdatSoftData.HANDLE
								+ "&pkg_name=com.stavigilmonitoring"
								+ "&ToMobile=" + mobno
								+ "&message=" + "New Alert is created for "+ edtSTN
								+ "&FromMobile=" + mobno;
						urlStringToken2 = "http://ccs.ekatm.com" //AdatSoftData.URL
								+ "/api/AddNotification?"// AdatSoftData.METHOD_SAVE_DATA
								+	"sessionId=" + "vigil"// AdatSoftData.SESSION_ID
								+ "&handler=" + "0" ;//AdatSoftData.HANDLE
								*//*+ "&pkg_name=com.stavigilmonitoring"
								+ "&ToMobile=" + mobno
								+ "&message=" + "New Alert is created for "+ edtSTN
								+ "&FromMobile=" + mobno;*//*
						new NotificationCreateAPI().execute(urlStringToken, null, null, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
*/
					//showD("Done");
					
					// Toast.makeText(MaterialRequest.this,   Data is already exist....!
					// "Material request send successfully",
					// Toast.LENGTH_LONG).show();
					mprogressBar.setVisibility(View.GONE);
					
					
					// ButtonMaterialName.setText("");
					// ButtonMaterialReqTO.setText("");
					// edittextQty.setText("");
					// editTextRepair.setText("");
					// ButtonReason.setText("");
				}
				progressDialog.dismiss();
				
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
	
	public class DownloadnetWork_New extends AsyncTask<String, Void, String>{
		ProgressDialog progressDialog;
		String sumdata2;

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String Url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/AlertCount?Mobile="+mobno;

			Log.e("Alert Count", "url : " + Url);
			Url = Url.replaceAll(" ", "%20");
			try {
				resposmsg = ut.httpGet(Url);
				Log.e("Response", resposmsg);
					
			if(resposmsg.contains("<InstalationId>")){
				sop = "valid";
				ut = new com.stavigilmonitoring.utility();
				//DatabaseHandler db = new DatabaseHandler(AlrtCreateActivity.this);
				SQLiteDatabase sql = db.getWritableDatabase();
				String columnName, columnValue;
				//sql.execSQL("DROP TABLE IF EXISTS AlrtCountTable");
				Log.e("AlrtCountTable", "DROP DONE");
				//sql.execSQL(ut.getAlrtCountTable());
				Log.e("AlrtCountTable", "CREATE DONE");
				sql.delete("AlrtCountTable",null,null);

				Cursor cur1 = sql.rawQuery("SELECT * FROM AlrtCountTable", null);
				int count = cur1.getCount();
				ContentValues values2 = new ContentValues();
				NodeList nl2 = ut.getnode(resposmsg, "TableResult");
				
				for(int i = 0; i < nl2.getLength(); i++){
					Element e = (Element) nl2.item(i);
					for (int j=0; j<cur1.getColumnCount(); j++){
						columnName = cur1.getColumnName(j);
						columnValue = ut.getValue(e, columnName);
						
						values2.put(columnName, columnValue);
					}
					sql.insert("AlrtCountTable", null, values2);
				}
				cur1.close();
				/*sql.close();
				db.close();*/
				
			}else{
				sop = "invalid";
			}
			
			}catch(Exception e){
				sop = "ServerError";
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
				
			
			return sop;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(AlrtCreateActivity.this);
			progressDialog.setMessage("Loading...");
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
			progressDialog.show();
			//Log.e("prgdlg", "Started");
			//btnrefresh.setVisibility(View.GONE);
			mprogressBar.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDialog.dismiss();
			try{
				if(sop.equals("valid")){
					//updatelist2();
					
					asynk = null;
					asynk = new AlrtListURL();
					asynk.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					if (flag ==1){
						flag =3;
						//showD("Done");
					}else if (flag ==2){
						flag = 4;
						//showD("Data is already exist....!");
					}
					updateAlertCount();
				} else if(sop.equals("nodata")){
					
					//updatelist2();
					//updateAlertCount();
					sumdata2 ="0";
					SharedPreferences prefalertcount = parent
							.getSharedPreferences("Prefalertcount", Context.MODE_PRIVATE);
					Editor editoralertcount = prefalertcount.edit();
					// String TVsound = prefsound.getString("TVSound", "");
					editoralertcount.putString("AlertCount", sumdata2);
					editoralertcount.commit();
					ut.showD(AlrtCreateActivity.this, "nodata");
					/*async = null;
					async = new AlrtListURL();
					async.execute();*/
				} else {
					ut.showD(AlrtCreateActivity.this, "invalid");
				}
				//btnrefresh.setVisibility(View.VISIBLE);
				mprogressBar.setVisibility(View.GONE);
				
				//Log.e("prgdlg", "Ended");
				}catch(Exception e){
					e.printStackTrace();
					StackTraceElement l = new Exception().getStackTrace()[0];
					
					ut =new com.stavigilmonitoring.utility();
					if(!ut.checkErrLogFile()){
						ut.ErrLogFile();
					} 
					if (ut.checkErrLogFile()){
						ut.addErrLog(l.getClassName()+"/"+l.getMethodName()+":"+l.getLineNumber()+"  "+e.getMessage()+ " "/*+ LDate*/);
					}
			}
		}
		
	}
	public void updateAlertCount() {
		// searchResults.clear();
		//searchResults.clear();
		//DatabaseHandler db = new DatabaseHandler(AlrtCreateActivity.this);
		SQLiteDatabase sql = db.getWritableDatabase();
		int count = 0;
		String sumdata2 = "0";
		Cursor c = sql.rawQuery(
				"SELECT SUM(CAST(cnt AS INT)) as sumdata FROM AlrtCountTable", null);//c.getCount()
				
				if (c.moveToFirst()){
					   do{
					      sumdata2 = c.getString(c.getColumnIndex("sumdata"));
					      // do what ever you want here
					   }while(c.moveToNext());
					
		
			SharedPreferences prefalertcount = parent
					.getSharedPreferences("Prefalertcount", Context.MODE_PRIVATE);
			Editor editoralertcount = prefalertcount.edit();
			// String TVsound = prefsound.getString("TVSound", "");
			editoralertcount.putString("AlertCount", sumdata2);
			editoralertcount.commit();
			//Log.e("get details.....", "---kk add STn : " + totalstation);
			//alertcounts.setText(String.valueOf(sumdata2));
		}

		//sql.close();
		c.close();

	}

	class NotificationCreateAPI extends AsyncTask<String, Void, String> {
		Object res;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			/*progressDialog = new ProgressDialog(ConfirmOTPActivity.this);
			progressDialog.setMessage("Processing...");
			progressDialog.show();*/
		}
		@Override
		protected String doInBackground(String... params) {

       /* responsemsg = "";
        inwid = "";
        inwtab = "";*/
			try {
				res = OpenConnection(params[0]);
				responsemsg = res.toString();
			} catch (NullPointerException e) {
				responsemsg = "error";
				e.printStackTrace();
			} catch (Exception e) {
				responsemsg = "error";
				e.printStackTrace();
			}
			return responsemsg;
		}
		@Override
		protected void onPostExecute(String result) {
			//String table = "";
			if (result.contains("error")) {
				Toast.makeText(AlrtCreateActivity.this, "error!", Toast.LENGTH_LONG).show();
			} else if (result.contains("Success")) {
				try{
					res = OpenConnection(urlStringToken2);
					responsemsg = res.toString();
				} catch (NullPointerException e) {
					responsemsg = "error";
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
					responsemsg = "error";
					e.printStackTrace();
				}

				//Toast.makeText(getBaseContext(), "Token Added Successfully..", Toast.LENGTH_LONG).show();
			}
		}
	}
		
	public class AlrtListURL extends AsyncTask<String, Void, String>{
		ProgressDialog progressDialog;

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
			//DatabaseHandler db = new DatabaseHandler(AlrtCreateActivity.this);
			SQLiteDatabase sql = db.getWritableDatabase();
			String url = "http://sta.vritti.co/imedia/STA_Announcement/TimeTable.asmx/AlertGet?InstallationId="
			+""
			+"&AddedBy="
			+ mobno;

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = ut.httpGet(url);
				//sql.execSQL("DROP TABLE IF EXISTS AlrtListTable");
				//sql.execSQL(ut.getAlrtListTable());
				sql.delete("AlrtListTable",null,null);

				Log.e("csn status", "resmsg : " + responsemsg);

				if (responsemsg.contains("<AlertId>")) {
					sop = "valid";
					String columnName, columnValue;

					Cursor cur = sql.rawQuery("SELECT * FROM AlrtListTable",null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "TableResult");
					Log.e("AlrtListTable data...",
							" fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							columnName = cur.getColumnName(j);


							columnValue = ut.getValue(e, columnName);
							values1.put(columnName, columnValue);

							 Log.e("Downloadxmls..on back."," count i: "+i+"  j:"+j);
						}
						sql.insert("AlrtListTable",
								null, values1);
					}
					cur.close();
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
				dff = new SimpleDateFormat("HH:mm:ss");
				Ldate = dff.format(new Date());
				StackTraceElement l = new Exception().getStackTrace()[0];
				System.out.println(l.getClassName() + "/" + l.getMethodName() + ":" + l.getLineNumber());
				ut = new com.stavigilmonitoring.utility();
				if (!ut.checkErrLogFile()) {
					ut.ErrLogFile();
				}
				if (ut.checkErrLogFile()) {
					ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
							+ ":" + l.getLineNumber() + "	" + e.getMessage() + " " + Ldate);
				}

			} catch (IOException e) {
				e.printStackTrace();
				dff = new SimpleDateFormat("HH:mm:ss");
				Ldate = dff.format(new Date());
				StackTraceElement l = new Exception().getStackTrace()[0];
				System.out.println(l.getClassName() + "/" + l.getMethodName() + ":" + l.getLineNumber());
				ut = new com.stavigilmonitoring.utility();
				if (!ut.checkErrLogFile()) {
					ut.ErrLogFile();
				}
				if (ut.checkErrLogFile()) {
					ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
							+ ":" + l.getLineNumber() + "	" + e.getMessage() + " " + Ldate);
				}
			}
			return sop;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				if (sop == "valid") {
					progressDialog.dismiss();
					if (flag ==3){
						flag =5;
						showD("Done");
					}else if (flag ==4){
						flag = 5;
						showD("Data is already exist....!");
					}
					
					//updatelist();
					/*asynk = null;
					asynk = new DownloadnetWork();
					asynk.execute();*/
				} else {
					progressDialog.dismiss();
					//ut.showD(parent,"nodata");
				}
				/*btnrefresh.setVisibility(View.VISIBLE);
				mprogressBar
						.setVisibility(View.GONE);*/

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
			progressDialog = new ProgressDialog(AlrtCreateActivity.this);
			progressDialog.setMessage("Loading...");
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
			progressDialog.show();
			/*btnrefresh.setVisibility(View.GONE);
			mprogressBar.setVisibility(View.VISIBLE);*/
		}		
	}

	private void createALert() {
		//ItemName = BtnItemName.getText().toString().trim();
		edtSTN = EdtSTN.getText().toString().trim();
		edtDesc = EdtDesc.getText().toString().trim();
		DBInterface dbi = new DBInterface(AlrtCreateActivity.this);
		mobno = dbi.GetPhno();


		if (isnet()) {
			asyncfetch_csnstate = new DownloadxmlsDataURL_new();
			asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		} else {
			showD("no net");
		}
		//Intent intent = new Intent();
		//setResult(Common.InvtAddEdit, intent);
		//AlrtCreateActivity.this.finish();
	}

	public void CheckGPSAvailable(){
		gps = new GPSTracker(this);

		// check if GPS enabled
		if(gps.canGetLocation()){

			sup_latitude = gps.getLatitude();
			sup_longitude = gps.getLongitude();

			// \n is for new line
			// Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "+ sup_latitude + "\nLong: " + sup_longitude, Toast.LENGTH_LONG).show();

		}else{
			gps.showSettingsAlert();
		}
	}

	public class Download_StationLocation extends AsyncTask<String, Void, String>{
		ProgressDialog progressDialog;
		String sumdata2;

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			String Url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/Getlatlantofstation?Installationid="+installationid;
			Url = Url.replaceAll(" ", "%20");
			try {
				resposmsg = ut.httpGet(Url);
				Log.e("Response", resposmsg);

				if(resposmsg.contains("<TableResult>")){
					sop = "valid";

					NodeList nl1 = ut.getnode(resposmsg, "TableResult");

					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						stn_latitude = Double.parseDouble(ut.getValue(e,"Latitude"));
						stn_longitude = Double.parseDouble(ut.getValue(e,"Longitude"));
					}
				}else{
					sop = "invalid";
				}

			}catch(IOException e){
				sop = "ServerError";
				e.printStackTrace();
			}

			return sop;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(parent);
			progressDialog.show();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try{
				if(sop.equals("valid")){
					Toast.makeText(parent,"Station location available \n "+stn_latitude + "\n" +stn_longitude,
							Toast.LENGTH_SHORT).show();

					if(checkDistance()){
						Toast.makeText(parent,"Both locations get matched",Toast.LENGTH_SHORT).show();

						createALert();

					}else {
						Toast.makeText(parent,"You can not create alert! Locations are not getting matched",Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(parent,"Station location not available", Toast.LENGTH_SHORT).show();
					createALert();
				}

				progressDialog.dismiss();

			}catch(Exception e){
				e.printStackTrace();
				StackTraceElement l = new Exception().getStackTrace()[0];

				ut =new utility();
				if(!ut.checkErrLogFile()){
					ut.ErrLogFile();
				}
				if (ut.checkErrLogFile()){
					ut.addErrLog(l.getClassName()+"/"+l.getMethodName()+":"+l.getLineNumber()+"  "+e.getMessage());
				}
			}
		}

		private boolean checkDistance(){
            /*float radius =  100F;
            float[] results = new float[3];
            Location.distanceBetween(sup_latitude, sup_longitude, stn_latitude, stn_longitude, results);
          //  return (results[0] <= radius);*/

			Location location_supp = new Location("point A");

			/*******************For testing purpose***************************************/
            /*tesp purpose shirdi station
            sup_latitude = 19.7632908;
            sup_longitude = 74.477115;*/

			location_supp.setLatitude(sup_latitude);
			location_supp.setLongitude(sup_longitude);

			Location location_stn = new Location("point B");

			/*******************For testing purpose***************************************/
            //test purpopse swarget location
           /* stn_latitude = 18.499980072143828;
            stn_longitude = 73.85901030153036;*/

			/*******************For testing purpose***************************************/

			location_stn.setLatitude(stn_latitude);
			location_stn.setLongitude(stn_longitude);

			float distance = location_supp.distanceTo(location_stn);

			if(distance < 150F){
				Toast.makeText(parent,"Distance is matched within 150mtr",Toast.LENGTH_SHORT).show();
				return true;
			}else {
				if(distance < 250F){
					Toast.makeText(parent,"Distance is matched within 250mtr",Toast.LENGTH_SHORT).show();
					return true;
				}else {
					if(distance < 350F){
						Toast.makeText(parent,"Distance is matched within 350mtr",Toast.LENGTH_SHORT).show();
						return true;
					}else {
						if(distance < 500F){
							Toast.makeText(parent,"Distance is matched within 500mtr",Toast.LENGTH_SHORT).show();
							return true;
						}else {
							Toast.makeText(parent,"Distances are not getting matched",Toast.LENGTH_SHORT).show();
							return false;
						}
						//return false;
					}
					//return false;
				}
				//return false;
			}
		}
	}

}