package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.StationEnquiryAdptr;
import com.beanclasses.StateList;
import com.database.DBInterface;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class StationInventoryStatewise extends Activity{
	private ImageView mRefresh;
	private ProgressBar mProgress;
	private GridView mList;
	private ArrayList<StateList> mSearchList;
	private com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	static SimpleDateFormat dff;
	static String LDate;
	String resposmsg, sop, mobno;
	private static DownloadnetWork asynk;
	DatabaseHandler db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.inventstatewise);
		
		//Find View By IDs
		mRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_Stn_Invent);
		mProgress = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBarstninvt);
		mList =  findViewById(com.stavigilmonitoring.R.id.lststninvent);
		mSearchList = new ArrayList<StateList>();

		db = new DatabaseHandler(getApplicationContext());
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();
		
		mRefresh.setOnClickListener(new  OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if( ut.isnet(getApplicationContext())){
					asynk = null;
					asynk = new DownloadnetWork();
					asynk.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					//fetchdata();
				} else {
					try{
						ut.showD(StationInventoryStatewise.this, "nonet");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
			}
		});
		
		mList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Log.e("Check List item Click", mSearchList.get(position).getNetworkcode() );
				Intent intent = new Intent(getApplicationContext(),StationInventoryStnListAll.class);
				intent.putExtra("Type", mSearchList.get(position).getNetworkcode());
				startActivity(intent);
			}
			
		});
		
		if (dbvalue()){
			updatelist();
		} else if (ut.isnet(getApplicationContext())){
			fetchdata();
		} else {
			try{
				ut.showD(StationInventoryStatewise.this, "nonet");
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		/*Intent i = new Intent(StationInventoryStatewise.this,
				SelectMenu.class);
		startActivity(i);*/
		finish();
	}
	
	private void updatelist(){
		mSearchList.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor c = sql
				.rawQuery(
						"Select distinct NetworkCode from ConnectionStatusFiltermob Order by NetworkCode",
						null);
		if (c.getCount()>0){
			c.moveToFirst();
			do{
				int count = 0;
				String StationName = c.getString(c.getColumnIndex("NetworkCode"));
				Cursor c1 = sql.rawQuery("select distinct InstallationDesc from ConnectionStatusFiltermob where NetworkCode='"+StationName+"'", null);
				count = c1.getCount();
				
				StateList sitem = new StateList();
				sitem.SetNetworkCode(StationName);
				sitem.Setcount(count);
				mSearchList.add(sitem);
			}while(c.moveToNext());
		}
		
		StationEnquiryAdptr adp = new StationEnquiryAdptr(StationInventoryStatewise.this, mSearchList,"Default");
		adp.notifyDataSetChanged();
		mList.setAdapter(adp);
	}
	
	private void fetchdata(){
		if (asynk == null){
			mRefresh.setVisibility(View.VISIBLE);
			mProgress.setVisibility(View.GONE);
			 
			asynk = new DownloadnetWork();
			asynk.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			if (asynk.getStatus() == AsyncTask.Status.RUNNING){
				Log.e("ASYNC", "running");
				mRefresh.setVisibility(View.GONE);
				mProgress.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private boolean dbvalue(){
		try{
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery("SELECT NetworkCode FROM ConnectionStatusFiltermob", null);
			if (cursor != null && cursor.getCount()>0){
				if(cursor.getColumnIndex("NetworkCode")<0){
					cursor.close();
					return false;
				} else {
					cursor.close();
					return true;
				}
			}else{
				cursor.close();
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
	
	public class DownloadnetWork extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String Url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMasterMobile?Mobile="+mobno;
			Log.e("All Station", "Url=" + Url);
			
			try{
				resposmsg = ut.httpGet(Url);
			}catch(IOException e){
				e.printStackTrace();
			}
			
			if(resposmsg.contains("<InstalationId>")){
				sop = "valid";
				//DatabaseHandler db = new DatabaseHandler(getBaseContext());
				SQLiteDatabase sql = db.getWritableDatabase();
				String columnName, columnValue;
				//sql.execSQL("DROP TABLE IF EXISTS ConnectionStatusFiltermob");
				//sql.execSQL(ut.getConnectionStatusFiltermob());
				sql.delete("ConnectionStatusFiltermob",null,null);

				Cursor cur1 = sql.rawQuery("SELECT * FROM ConnectionStatusFiltermob", null);
				cur1.getCount();
				ContentValues values2 = new ContentValues();
				NodeList nl2 = ut.getnode(resposmsg, "Table");
				
				for(int i = 0; i < nl2.getLength(); i++){
					Element e = (Element) nl2.item(i);
					for (int j=0; j<cur1.getColumnCount(); j++){
						columnName = cur1.getColumnName(j);
						columnValue = ut.getValue(e, columnName);
						
						values2.put(columnName, columnValue);
					}
					sql.insert("ConnectionStatusFiltermob", null, values2);
				}
				cur1.close();
				
			}else{
				sop = "invalid";
			}
				
			
			return sop;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mRefresh.setVisibility(View.GONE);
			mProgress.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try{
				if(sop.equals("valid")){
					updatelist();
				} else {
					try{
						ut.showD(StationInventoryStatewise.this, "invalid");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				mRefresh.setVisibility(View.VISIBLE);
				mProgress.setVisibility(View.GONE);				
				}catch(Exception e){
					e.printStackTrace();
					StackTraceElement l = new Exception().getStackTrace()[0];
					
					ut =new utility();
					if(!ut.checkErrLogFile()){
						ut.ErrLogFile();
					} 
					if (ut.checkErrLogFile()){
						ut.addErrLog(l.getClassName()+"/"+l.getMethodName()+":"+l.getLineNumber()+"  "+e.getMessage()+ " "+ LDate);
					}
			}
		}
		
	}

}

