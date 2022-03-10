package com.stavigilmonitoring;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.adapters.AlertsItemListAdapter;
import com.adapters.AlrtStatewiseAdptr;
import com.beanclasses.AlertsItemBean;
import com.beanclasses.AlrtStateList;
import com.database.DBInterface;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkAssignSupporterNameListActivity extends Activity {

	private TextView tvhead,myCount;
	private EditText SearchFilterText;
	private ImageView btnfilter, btnrefresh, btnaddItem2;
	static SimpleDateFormat dff;
	static String LDate;
	LinearLayout MyActivitylayout;
	int myactcnt;

	//DMCStateAdapter listAdapter;
	AlrtStatewiseAdptr listAdapter;
	
	String sop = "no";
	private static DownloadnetWork asynk;
	private ListView mList;

	List<DmCstnwiseActivity.StateList> searchResults;
	private ArrayList<AlrtStateList> mSearchList;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	static String Ldate;
	private String mobno;
	private GridView invtlist;
	private static DownloadnetWork_New asynk_new;
	//private ProgressBar mprogressBar;
	String mType, installationid; 
	String responsemsg = "k", resposmsg ="n";
	Bundle dataBundle = new Bundle();
	
	ArrayList<AlertsItemBean> alertsItemBeanlist;
	AlertsItemBean alertsItemBean;	
	AlertsItemListAdapter alertsItemListAdapter;
	DatabaseHandler db;

	String Station, network;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.workassignsupplist);
		
		initViews();

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		if (dbvalue()) {
			GetMyActivitiesCount();
			//myCount.setText(myactcnt);
			updatelist2();
		} else if (ut.isnet(getApplicationContext())) {
			fetchdata();
		}else {
				ut.showD(this, "nonet");
		}

		SetListeners();
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

	private void GetMyActivitiesCount() {
		try{
		//	DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			int stncnt = 0,overduecnt=0;
			Cursor cursor = sql.rawQuery("Select ActivityId,ActualEndDate from WorkAssignedTable where IssuedUserName like '"+Common.UserName+"'"+ " Order by ActualEndDate", null);
			Log.e("TAG TAG TAG", "dBVALUE :  FALSE "+cursor.getCount());
				myactcnt = cursor.getCount();
			if (cursor.getCount()>0){
				cursor.moveToFirst();
				do{
					String column1 = cursor.getString(cursor.getColumnIndex("ActualEndDate"));
					if (checkOverdue(column1)){
						overduecnt = overduecnt + 1;
					}
					stncnt = stncnt + 1;

				}while(cursor.moveToNext());
			}
			myCount.setText(String.valueOf(overduecnt)+ "/" +String.valueOf(stncnt));
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
	}

	private boolean dbvalue(){
		try{
		//	DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery("Select * from WorkAssignedTable", null);
			cursor.moveToFirst();
			if (cursor != null && cursor.getCount()>0){
				if(cursor.getColumnIndex("ActivityId")<0){
					//cursor.close();
					//sql.close();
					//db1.close();
					return false;
				} else {
					//cursor.close();
					//sql.close();
					//db1.close();
					return true;
				}
			}else{
				cursor.close();
				sql.close();
				//db1.close();
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
		return false;
	}

	private void SetListeners() {

		((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
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

						listAdapter
								.filter_DMDesc(((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
										.getText().toString().trim()
										.toLowerCase(Locale.getDefault()));
					}
				});

        btnaddItem2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),WorkAssign_AssignActivity.class);
				intent.putExtra("Activity", "WorkAssignSupporterNameListActivity");
				intent.putExtra("Type", "");
				startActivity(intent);
				finish();
			}
		});
        
        btnrefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fetchdata();
			}
		});

		MyActivitylayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),WorkAssignSupporter_ActivityDetails.class);
				intent.putExtra("Type", Common.UserName);
				//intent.putExtra("Network", );
				startActivity(intent);
				finish();
			}
		});
        
        invtlist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Log.e("Check List item Click", mSearchList.get(position).getNetworkcode() );
				Intent intent = new Intent(getApplicationContext(),WorkAssignSupporter_ActivityDetails.class);
				intent.putExtra("Type", mSearchList.get(position).getNetworkcode());
				intent.putExtra("Network", mSearchList.get(position).getNetworkcode());
				intent.putExtra("StationName", mSearchList.get(position).getStatioName());

				startActivity(intent);
				finish();
			}
		});
		
	}
	
	private void fetchdata(){
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
						"Select distinct IssuedUserName from WorkAssignedTable where IssuedUserName NOT Like  '"+Common.UserName+"'"+ " Order by IssuedUserName",// " Order by ActualEndDate",
						null);
		if (c.getCount()>0){
			c.moveToFirst();
			do{
				int count = 0;
				int stncnt = 0,overduecnt=0;
				String StationName = c.getString(c.getColumnIndex("IssuedUserName"));
				Station = StationName;
				//network = c.getString(c.getColumnIndex("NetworkCode"));

				Cursor c1 = sql.rawQuery("select ActivityId,ActualEndDate from WorkAssignedTable where IssuedUserName Like '"+StationName+"'"+ " Order by IssuedUserName", null);
				count = c1.getCount();
				if (c1.getCount()>0){
					c1.moveToFirst();
					int total = 0;
					do{
						String column1 = c1.getString(c1.getColumnIndex("ActualEndDate"));
						if (checkOverdue(column1)){
							overduecnt = overduecnt + 1;
						}
						stncnt = stncnt + 1;

					}while(c1.moveToNext());
					count = total;
				}
				AlrtStateList sitem = new AlrtStateList();
				sitem.Setact("Work");
				sitem.SetNetworkCode(StationName);
				sitem.Setcount(stncnt);
				sitem.SetOverdueCnt(overduecnt);
				mSearchList.add(sitem);
			}while(c.moveToNext());
		}

		listAdapter = new AlrtStatewiseAdptr(WorkAssignSupporterNameListActivity.this, mSearchList);
		//listAdapter.notifyDataSetChanged();
		invtlist.setAdapter(listAdapter);
	}

	private boolean checkOverdue(String amcExpireDt) {
		String[] parts = amcExpireDt.split("T");
		amcExpireDt = parts[0];
		boolean result = false;
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date2 = dateFormat2.parse(amcExpireDt);
			Date date = new Date();
			String res = dateFormat2.format(date);
			date = dateFormat2.parse(res);
			if (date2.equals(date)){
				result = false;
			} else if (date.after(date2)){
				result = true;
			} else if (date2.after(date)){
				result = false;
			}
		}catch( Exception e){
			e.printStackTrace();
		}
		return result;
	}

	private void initViews() {
		tvhead = (TextView) findViewById(com.stavigilmonitoring.R.id.tvalertlist);
		tvhead.setText("Supporter List");
		MyActivitylayout = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.MyActivitylayout);
		btnaddItem2= (ImageView) findViewById(com.stavigilmonitoring.R.id.button_alert_add);
		btnaddItem2.setImageResource(com.stavigilmonitoring.R.drawable.work_assign);
		btnrefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_alert);
		btnfilter = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_filter);
		btnfilter.setVisibility(View.VISIBLE);
		invtlist =  findViewById(com.stavigilmonitoring.R.id.listAlertitems);
		myCount = (TextView) findViewById(com.stavigilmonitoring.R.id.tvsmyCntc);
		mSearchList = new ArrayList<AlrtStateList>();
		ut = new com.stavigilmonitoring.utility();

		db = new DatabaseHandler(getBaseContext());
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		/*Intent i = new Intent(WorkAssignSupporterNameListActivity.this, WorkAssigncategorizeActivity.class);
		startActivity(i);*/
	}

	
	public class DownloadnetWork_New extends AsyncTask<String, Void, String>{
		ProgressDialog progressDialog;
		String sumdata2;

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String Url = "http://sta.vritti.co/imedia/STA_Announcement/DmCertificate.asmx/GetWorkAssignList?Mobile="
					+ mobno;
			Url = Url.replaceAll(" ", "%20");
			try {
				resposmsg = ut.httpGet(Url);
				Log.e("Response", resposmsg);

			}catch(IOException e){
				sop = "ServerError";
				e.printStackTrace();
			}
			if(resposmsg.contains("<DMHeaderId>")){
				sop = "valid";
			//	DatabaseHandler db = new DatabaseHandler(getBaseContext());
				SQLiteDatabase sql = db.getWritableDatabase();
				String columnName, columnValue;
				//sql.execSQL("DROP TABLE IF EXISTS WorkAssignedTable");
				//sql.execSQL(ut.getWorkAssignList());
				sql.delete("WorkAssignedTable",null,null);

				Cursor cur = sql.rawQuery("SELECT * FROM WorkAssignedTable", null);
				ContentValues values1 = new ContentValues();
				NodeList nl1 = ut.getnode(resposmsg, "Table1");
				Log.e("WorkAssignedTable data",
						" fetch data : " + nl1.getLength());
				for (int i = 0; i < nl1.getLength(); i++) {
					Element e = (Element) nl1.item(i);
					for (int j = 0; j < cur.getColumnCount(); j++) {
						columnName = cur.getColumnName(j);
						columnValue = ut.getValue(e, columnName);
						values1.put(columnName, columnValue);
					}
					sql.insert("WorkAssignedTable",null, values1);
				}

			//	cur.close();
			//	sql.close();
				//db.close();
				
			}else{
				sop = "invalid";
			}
				
			
			return sop;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(WorkAssignSupporterNameListActivity.this);
			progressDialog.setMessage("Loading...");
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try{
				progressDialog.dismiss();
				if(sop.equals("valid")){
					Log.e("Tag", " ******* WORKING ON ALERTCOUNT *********");
					updatelist2();
					GetMyActivitiesCount();
					//myCount.setText(myactcnt);
				} else if(sop.equals("nodata")){
					updatelist2();
					GetMyActivitiesCount();
					//myCount.setText(myactcnt);
				} else {
					ut.showD(WorkAssignSupporterNameListActivity.this, "invalid");
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

	public void updatelist() {
		// TODO Auto-generated method stub
		//DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		alertsItemBeanlist = new ArrayList<AlertsItemBean>();
		alertsItemBeanlist.clear();
		
		Cursor c = sql.rawQuery("Select * from AlrtListTable where InstallationId = "+installationid+" order by CAST(AlertId AS INT) desc", null);
		int count = c.getCount();
		if (count== 0){
			c.close();
			sql.close();
			//db.close();
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
				alertsItemBeanlist.add(alertsItemBean);				
			}while(c.moveToNext());
			c.close();
			sql.close();
			//db.close();
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
			//	DatabaseHandler db = new DatabaseHandler(getBaseContext());
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
				sql.close();
				//db.close();

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
			progressDialog = new ProgressDialog(WorkAssignSupporterNameListActivity.this);
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
					ut.showD(WorkAssignSupporterNameListActivity.this, "invalid");
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
