package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.AlertsItemListAdapter;
import com.adapters.AlrtStatewiseAdptr;
import com.beanclasses.AlertsItemBean;
import com.beanclasses.AlrtStateList;
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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AlrtListActivity extends Activity {

	private TextView tvhead;
	private EditText SearchFilterText;
	private Button btnaddItem;
	private ImageView btnfilter, btnrefresh, button_alert_add;
	static SimpleDateFormat dff;
	static String LDate;
	
	String sop = "no";
	private static DownloadnetWork asynk;
	private static AlrtListURL async;
	private ArrayList<AlrtStateList> mSearchList;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	static String Ldate;
	private String mobno;
	private GridView invtlist;
	private static DownloadnetWork_New asynk_new;
	private ProgressBar mprogressBar;
	String mType, installationid; 
	String responsemsg = "k", resposmsg ="n";
	Bundle dataBundle = new Bundle();
	
	ArrayList<AlertsItemBean> alertsItemBeanlist;
	AlertsItemBean alertsItemBean;
	AlertsItemListAdapter alertsItemListAdapter;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.alertlist);
		
		initViews();
		/*if (async != null
				&& async.getStatus() == AsyncTask.Status.RUNNING) {
			Log.e("async", "running");
			btnrefresh.setVisibility(View.GONE);
			mprogressBar.setVisibility(View.VISIBLE);
		}*/
		SetListeners();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		if (dbvalue()) {

			if (dbvalue2()){
				updatelist2();
			}else{//
				async = null;
				async = new AlrtListURL();
				async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			updatelist2();
			
		} else if (ut.isnet(AlrtListActivity.this)) {

			fetchdata();
			} 
		else {
			try{
				ut.showD(AlrtListActivity.this, "nonet");
			}catch (Exception e){
				e.printStackTrace();
			}
			}	
	}
	
	private boolean dbvalue(){
		try{
			//DatabaseHandler db1 = new DatabaseHandler(AlrtListActivity.this);
			SQLiteDatabase sql = db.getWritableDatabase();
			//Cursor cursor = sql.rawQuery("SELECT NetworkCode FROM ConnectionStatusFiltermob", null);
			Cursor cursor = sql.rawQuery("Select distinct NetworkCode from AlrtCountTable where NOT(cnt = '0') Order by NetworkCode", null);
			Log.e("TAG TAG TAG", "dBVALUE :  FALSE "+cursor.getCount());
			cursor.moveToFirst();
			if (cursor != null && cursor.getCount()>0){
				if(cursor.getColumnIndex("NetworkCode")<0){
					cursor.close();
					/*sql.close();
					db1.close();*/
					Log.e("TAG TAG TAG", "dBVALUE :  FALSE");
					return false;
				} else {
					cursor.close();
					/*sql.close();
					db1.close();*/
					Log.e("TAG TAG TAG", "dBVALUE :  TRUE");
					return true;
				}
			}else{
				cursor.close();
				/*sql.close();
				db1.close();*/
			}
		}catch(Exception e){
			e.printStackTrace();
			dff = new SimpleDateFormat("HH:mm:ss");
			LDate = dff.format(new Date());
			
			StackTraceElement l = new Exception().getStackTrace()[0];
			ut = new com.stavigilmonitoring.utility();
			if(!ut.checkErrLogFile()){
				ut.ErrLogFile();
			}
			if(ut.checkErrLogFile()){
				ut.addErrLog(l.getClassName()+"/"+l.getMethodName()+":"+l.getLineNumber()+" "+e.getMessage()+" "+LDate);
			}
		}
		Log.e("TAG TAG TAG", "dBVALUE :  FALSE");
		return false;
	}
	
	private boolean dbvalue2(){
		try{
			//DatabaseHandler db1 = new DatabaseHandler(AlrtListActivity.this);
			SQLiteDatabase sql = db.getWritableDatabase();
			//Cursor cursor = sql.rawQuery("SELECT NetworkCode FROM ConnectionStatusFiltermob", null);
			Cursor cursor = sql.rawQuery("Select * from AlrtListTable", null);
			Log.e("TAG TAG TAG", "dBVALUE2 :  TRUE  "+cursor.getCount());
			cursor.moveToFirst();
			if (cursor != null && cursor.getCount()>0)/*{
				if(cursor.getColumnIndex("NetworkCode")<0){
					cursor.close();
					sql.close();
					db1.close();
					return false;
				} else*/ {
					cursor.close();
					/*sql.close();
					db1.close();*/
					Log.e("TAG TAG TAG", "dBVALUE2 :  TRUE");
					return true;
				//}
			}else{
				cursor.close();
				/*sql.close();
				db1.close();*/
			}
		}catch(Exception e){
			e.printStackTrace();
			dff = new SimpleDateFormat("HH:mm:ss");
			LDate = dff.format(new Date());
			
			StackTraceElement l = new Exception().getStackTrace()[0];
			ut = new com.stavigilmonitoring.utility();
			if(!ut.checkErrLogFile()){
				ut.ErrLogFile();
			}
			if(ut.checkErrLogFile()){
				ut.addErrLog(l.getClassName()+"/"+l.getMethodName()+":"+l.getLineNumber()+" "+e.getMessage()+" "+LDate);
			}
		}
		Log.e("TAG TAG TAG", "dBVALUE2 :  FALSE");
		return false;
	}
	
	
	protected void AlertDetails(Bundle dataBundle2) {
		// TODO Auto-generated method stub
		/*Intent intent = new Intent(AlrtListActivity.this,StationInventoryAddEditItems.class);
		startActivity(intent);	*/	
		Intent intent = new Intent(AlrtListActivity.this,
				AlrtDetailsWithCommentsActivity.class);
		intent.putExtras(dataBundle2);
		startActivity(intent);
	}
		
	private void SetListeners() {
		
		btnaddItem.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent intent = new Intent(AlrtListActivity.this,AlrtsStatewise.class);
				Intent intent = new Intent(AlrtListActivity.this,AlrtCreateActivity.class);
				//Intent intent = new Intent(AlrtListActivity.this,AlrtListActivity.class);
				/*intent.putExtra("Type", mType);
				intent.putExtra("InstallationId",installationid);
				*/startActivity(intent);
				finish();
			}
		});

		button_alert_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent intent = new Intent(AlrtListActivity.this, AlrtsStatewise.class);
				Intent intent = new Intent(AlrtListActivity.this,AlrtCreateActivity.class);
				//Intent intent = new Intent(AlrtListActivity.this,AlrtListActivity.class);
				/*intent.putExtra("Type", mType);
				intent.putExtra("InstallationId",installationid);
				*/startActivity(intent);	
				finish();
				
			}
		});
        
        btnrefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btnrefresh.setVisibility(View.GONE);
				mprogressBar.setVisibility(View.VISIBLE);
				fetchdata();
			}
		});
        
        /*invtlist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,	long id) {
				dataBundle.putString("AlertId", alertsItemBeanlist.get(position).getAlertId());
				dataBundle.putString("AlertDesc", alertsItemBeanlist.get(position).getAlertDesc());
				dataBundle.putString("StationName", alertsItemBeanlist.get(position).getStationName());
				dataBundle.putString("CreatedBy",alertsItemBeanlist.get(position).getAddedBy());
				dataBundle.putString("CreatedDt",alertsItemBeanlist.get(position).getAddedDt());
				dataBundle.putString("InstallationId", alertsItemBeanlist.get(position).getInstallationId());	
				AlertDetails(dataBundle);	`
			}
		});*/
        
        invtlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				
				Log.e("Check List item Click", mSearchList.get(position).getNetworkcode() );
				Intent intent = new Intent(AlrtListActivity.this,AlrtsStnListAll.class);
				intent.putExtra("Type", mSearchList.get(position).getNetworkcode());
				intent.putExtra("intentfrom", "0");
				intent.putExtra("Activity","AlrtListActivity");
				startActivity(intent);
				//finish();
			}
			
		});
	}
	
	private void fetchdata(){
		/*if (asynk_new == null){*/
			btnrefresh.setVisibility(View.VISIBLE);
			mprogressBar.setVisibility(View.GONE);
			
			asynk_new = null;
			asynk_new = new DownloadnetWork_New();
			asynk_new.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void updatelist2(){
		mSearchList.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor c = sql
				.rawQuery(
						"Select distinct NetworkCode from AlrtCountTable where cnt != '0'"
						+ " Order by NetworkCode",
						null);
		if (c.getCount()>0){
			c.moveToFirst();
			do{
				int count = 0;
				String StationName = c.getString(c.getColumnIndex("NetworkCode"));
				Cursor c1 = sql.rawQuery("select cnt, InstalationId from AlrtCountTable where cnt != '0' and NetworkCode='"+StationName+"'", null);
				count = c1.getCount();
				if (c1.getCount()>0){
					c1.moveToFirst();
					int total = 0;
					do{						
						String alcnt = c1.getString(c1.getColumnIndex("cnt"));
						total = total + Integer.parseInt(alcnt);
					}while(c1.moveToNext());
					count = total;
				}
				AlrtStateList sitem = new AlrtStateList();
				sitem.Setact("Alert");
				sitem.SetNetworkCode(StationName);
				sitem.Setcount(count);
				//sitem.Setcount(total);
				mSearchList.add(sitem);
			}while(c.moveToNext());
		}
		
		Log.e("count list",mSearchList.toString());
		AlrtStatewiseAdptr adp = new AlrtStatewiseAdptr(AlrtListActivity.this, mSearchList);
		adp.notifyDataSetChanged();
		invtlist.setAdapter(adp);
	}
	

	private void initViews() {
		tvhead = (TextView) findViewById(com.stavigilmonitoring.R.id.tvalertlist);
		tvhead.setText("Alert List");

		//Common.listMessages = null;
		btnaddItem = (Button) findViewById(com.stavigilmonitoring.R.id.txtCreateAlert);
		btnaddItem.setVisibility(View.GONE);
		button_alert_add= (ImageView) findViewById(com.stavigilmonitoring.R.id.button_alert_add);
		btnrefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_alert);
		invtlist = findViewById(com.stavigilmonitoring.R.id.listAlertitems);
		mprogressBar = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressinvent1);
		mSearchList = new ArrayList<AlrtStateList>();

		Intent intent = getIntent();
		/*mType = intent.getStringExtra("Type");
		installationid = intent.getStringExtra("InstallationId");*/
		//alertsItemBeanlist = new ArrayList<AlertsItemBean>();
		ut = new com.stavigilmonitoring.utility();

		db = new DatabaseHandler(AlrtListActivity.this);

		DBInterface dbi = new DBInterface(AlrtListActivity.this);
		mobno = dbi.GetPhno();
	}
			
	@Override
	public void onBackPressed() {
		super.onBackPressed();
				//Intent i = new Intent(AlrtListActivity.this, SelectMenu.class);
		//i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		//startActivity(i);
		finish();
	}
	
	public class DownloadnetWork_New extends AsyncTask<String, Void, String>{
		ProgressDialog progressDialog;
		String sumdata2;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(AlrtListActivity.this);
			progressDialog.setMessage("Loading...");
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
			progressDialog.show();
			//Log.e("prgdlg", "Started");
			btnrefresh.setVisibility(View.GONE);
			mprogressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String Url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/AlertCount?Mobile="+mobno;

			Log.e("Alert Count", "url : " + Url);
			Log.e("Tag", " ******* WORKING ON ALertCOUNT *********");
			Url = Url.replaceAll(" ", "%20");
			try {
				resposmsg = ut.httpGet(Url);
				Log.e("Response", resposmsg);
			}catch(IOException e){
				sop = "ServerError";
				e.printStackTrace();
				
			}
			
			if (resposmsg.contains("Record are not Found...!")){
				sumdata2 = "0";
				sop = "nodata";
				//DatabaseHandler db = new DatabaseHandler(AlrtListActivity.this);
				SQLiteDatabase sql = db.getWritableDatabase();
				sql.execSQL("Delete from AlrtCountTable");
				sql.execSQL("Delete from AlrtListTable");
				//up
				
			}
			else if(resposmsg.contains("<InstalationId>")){
				sop = "valid";
				//DatabaseHandler db = new DatabaseHandler(AlrtListActivity.this);
				SQLiteDatabase sql = db.getWritableDatabase();
				String columnName, columnValue;

				//sql.execSQL("DROP TABLE IF EXISTS AlrtCountTable");
				//sql.execSQL(ut.getAlrtCountTable());
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
				
			
			return sop;
		}

		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			btnrefresh.setVisibility(View.VISIBLE);
			mprogressBar.setVisibility(View.GONE);
			progressDialog.dismiss();
			try{
				if(sop.equals("valid")){
					Log.e("Tag", " ******* WORKING ON ALERTCOUNT *********");
					updatelist2();
					updateAlertCount();
					async = null;
					async = new AlrtListURL();
					async.executeOnExecutor(THREAD_POOL_EXECUTOR);
				}
				else if(sop.equals("nodata")){
					
					updatelist2();
					//updateAlertCount();
					sumdata2 ="0";
					SharedPreferences prefalertcount = AlrtListActivity.this
							.getSharedPreferences("Prefalertcount", Context.MODE_PRIVATE);
					Editor editoralertcount = prefalertcount.edit();
					editoralertcount.putString("AlertCount", sumdata2);
					editoralertcount.commit();
					try{
						ut.showD(AlrtListActivity.this, "nodata");
					}catch (Exception e){
						e.printStackTrace();
					}
				} else {
					try{
						ut.showD(AlrtListActivity.this, "invalid");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				}catch(Exception e){
					e.printStackTrace();
					StackTraceElement l = new Exception().getStackTrace()[0];
					
					ut =new com.stavigilmonitoring.utility();
					if(!ut.checkErrLogFile()){
						ut.ErrLogFile();
					} 
					if (ut.checkErrLogFile()){
						ut.addErrLog(l.getClassName()+"/"+l.getMethodName()+":"+l.getLineNumber()+"  "+e.getMessage()+ " "+ LDate);
					}
			}
		}
		
	}
	
	public void updateAlertCount() {
		//DatabaseHandler db = new DatabaseHandler(AlrtListActivity.this);
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
					
		
			SharedPreferences prefalertcount = AlrtListActivity.this
					.getSharedPreferences("Prefalertcount", Context.MODE_PRIVATE);
			Editor editoralertcount = prefalertcount.edit();
			editoralertcount.putString("AlertCount", sumdata2);
			editoralertcount.commit();
		}

		//sql.close();
		c.close();

	}
	
	public class AlrtListURL extends AsyncTask<String, Void, String>{
		ProgressDialog progressDialog;

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
			Log.e("Tag", " ******* WORKING ON AlertGET *********");
			//DatabaseHandler db = new DatabaseHandler(AlrtListActivity.this);
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

				if (responsemsg.contains("<AlertId>")) {
					sop = "valid";
					String columnName, columnValue;
					
					Cursor cur = sql.rawQuery("SELECT * FROM AlrtListTable",null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "TableResult");
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							columnName = cur.getColumnName(j);
							columnValue = ut.getValue(e, columnName);
							values1.put(columnName, columnValue);

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
				if (sop == "valid") {
					Log.e("Tag", " ******* WORKING ON ALERTGET *********");
					updatelist();
					/*asynk = null;
					asynk = new DownloadnetWork();
					asynk.execute();*/
				} else {
					try{
						ut.showD(AlrtListActivity.this,"nodata");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				btnrefresh.setVisibility(View.VISIBLE);
				mprogressBar
						.setVisibility(View.GONE);
				progressDialog.dismiss();

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
			super.onPreExecute();
			progressDialog = new ProgressDialog(AlrtListActivity.this);
			progressDialog.setMessage("Loading...");
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
			progressDialog.show();
			btnrefresh.setVisibility(View.GONE);
			mprogressBar.setVisibility(View.VISIBLE);
		}		
	}

	public void updatelist() {
		// TODO Auto-generated method stub
		//DatabaseHandler db = new DatabaseHandler(AlrtListActivity.this);
		SQLiteDatabase sql = db.getWritableDatabase();
		alertsItemBeanlist = new ArrayList<AlertsItemBean>();
		alertsItemBeanlist.clear();
		
		Cursor c = sql.rawQuery("Select * from AlrtListTable where InstallationId = "+installationid+" order by CAST(AlertId AS INT) desc", null);
		int count = c.getCount();
		if (count== 0){
			c.close();
			/*sql.close();
			db.close();*/
		}else{
			c.moveToFirst();
			int column = 0;
			do{
				String AlertId  = c.getString( c.getColumnIndex("AlertId") );
				String AlertDesc  = c.getString( c.getColumnIndex("AlertDesc") );
				String InstallationId  = c.getString( c.getColumnIndex("InstallationId") );
				String StationName  = c.getString( c.getColumnIndex("StationName") );
				String AddedBy  = c.getString( c.getColumnIndex("AddedBy") );
				String AddedDt  = c.getString( c.getColumnIndex("AddedDt") );
				String ConfirmBy  = c.getString( c.getColumnIndex("ConfirmBy") );
				String ConfirmDT  = c.getString( c.getColumnIndex("ConfirmDt") );
				String ResolveBy  = c.getString( c.getColumnIndex("ResolveBy") );
				String ResolveDT  = c.getString( c.getColumnIndex("ResolveDt") );
				String ModifiedBy  = c.getString( c.getColumnIndex("ModifiedBy") );
				String ModifiedDT  = c.getString( c.getColumnIndex("ModifiedDt") );
				String RejectedBy  = c.getString( c.getColumnIndex("RejectedBy") );
				String RejectedDT  = c.getString( c.getColumnIndex("RejectedDt") );
				String SupporterName  = c.getString( c.getColumnIndex("SupporterName") );
				String networkCode  = c.getString( c.getColumnIndex("NetworkCode") );
				String AlertType  = c.getString( c.getColumnIndex("AlertType") );
				alertsItemBean = new AlertsItemBean();
				alertsItemBean.setAlertId(AlertId);;
				alertsItemBean.setAlertDesc(AlertDesc);
				alertsItemBean.setInstallationId(InstallationId);
				alertsItemBean.setStationName(StationName);
				alertsItemBean.setAddedBy(AddedBy);
				alertsItemBean.setAddedDt(AddedDt);
				alertsItemBean.setConfirmBy(ConfirmBy);
				alertsItemBean.setConfirmDt(ConfirmDT);
				alertsItemBean.setResolveBy(ResolveBy);
				alertsItemBean.setResolveDt(ResolveDT);
				alertsItemBean.setModifiedBy(ModifiedBy);
				alertsItemBean.setModifiedDt(ModifiedDT);
				alertsItemBean.setRejectedBy(RejectedBy);
				alertsItemBean.setRejectedDt(RejectedDT);
				alertsItemBean.setSupporterName(SupporterName);
				alertsItemBean.setAlertType(AlertType);
				//alertsItemBean.setNetworkCode(networkCode);
				alertsItemBeanlist.add(alertsItemBean);
			}while(c.moveToNext());
			c.close();
			/*sql.close();
			db.close();*/
		}
	}
	
	public class DownloadnetWork extends AsyncTask<String, Void, String> {
		String sumdata2 = "1";
		ProgressDialog progressDialog;

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			String Url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMasterMobile?Mobile="+mobno;
			Log.e("All Station", "Url=" + Url);

			try {
				resposmsg = ut.httpGet(Url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			 if (resposmsg.contains("<InstalationId>")) {
				sop = "valid";
				//DatabaseHandler db = new DatabaseHandler(AlrtListActivity.this);
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

				//Log.e("All Station Data ", "get length : " + nl2.getLength());
				for (int i = 0; i < nl2.getLength(); i++) {
					//Log.e("All Station Data ", "length : " + nl2.getLength());
					Element e = (Element) nl2.item(i);
					for (int j = 0; j < cur1.getColumnCount(); j++) {
						columnName = cur1.getColumnName(j);
						columnValue = ut.getValue(e, columnName);
						/*Log.e("All Station Data ", "column Name : "
								+ columnName);
						Log.e("All Station Data ", "column value : "
								+ columnValue);*/

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
			progressDialog = new ProgressDialog(AlrtListActivity.this);
			progressDialog.setMessage("Loading...");
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
			progressDialog.show();
			//mRefresh.setVisibility(View.GONE);
			//mprogress.setVisibility(View.VISIBLE);

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				if (sop.equals("valid")) {
					updatelist();
				} else if (sop.equals("nodata")) {
					updatelist();
				} else {
					try{
						ut.showD(AlrtListActivity.this, "invalid");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				//mRefresh.setVisibility(View.VISIBLE);
				//mprogress.setVisibility(View.GONE);
				progressDialog.dismiss();

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

	
}
