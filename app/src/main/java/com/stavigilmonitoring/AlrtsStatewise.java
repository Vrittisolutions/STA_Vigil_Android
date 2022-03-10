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
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AlrtsStatewise extends Activity {
	private ImageView mRefresh;
	private ProgressBar mProgress;
	private ListView mList;
	private ArrayList<AlrtStateList> mSearchList;
	private com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	static SimpleDateFormat dff;
	static String LDate;
	static String Ldate;
	TextView heading;
	String resposmsg, responsemsg, sop, mobno;
	private static DownloadnetWork asynk;
	private static DownloadnetWork_New asynk_new;
	ArrayList<AlertsItemBean> alertsItemBeanlist;
	AlertsItemBean alertsItemBean;	
	AlertsItemListAdapter alertsItemListAdapter;
	DatabaseHandler db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.alertstatewise);
		
		
		//Find View By IDs
		heading = (TextView) findViewById(com.stavigilmonitoring.R.id.onactivitynamereassign);
		heading.setText("Alert List");
		mRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_Stn_Invent);
		mProgress = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBarstninvt);
		mList = (ListView) findViewById(com.stavigilmonitoring.R.id.lststninvent);
		mSearchList = new ArrayList<AlrtStateList>();

		db = new DatabaseHandler(AlrtsStatewise.this);
		
		DBInterface dbi = new DBInterface(AlrtsStatewise.this);
		mobno = dbi.GetPhno();
		dbi.Close();
		
		mRefresh.setOnClickListener(new  OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if( ut.isnet(AlrtsStatewise.this)){
					asynk_new = null;
					asynk_new = new DownloadnetWork_New();
					asynk_new.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					//fetchdata();
				} else {
					try{
						ut.showD(AlrtsStatewise.this, "nonet");
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
				
				Log.e("Checking item Click", mSearchList.get(position).getNetworkcode() );
				Intent intent = new Intent(AlrtsStatewise.this,AlrtsStnListAll.class);
				intent.putExtra("Type", mSearchList.get(position).getNetworkcode());
				intent.putExtra("Activity","AlrtsStatewise");
				startActivity(intent);
				finish();
			}
			
		});
		
		if (dbvalue()){
			/*asynk_new = null;
			asynk_new = new DownloadnetWork_New();
			asynk_new.execute();*/
			updatelist2();
		} else if (ut.isnet(AlrtsStatewise.this)){
			/*asynk_new = null;
			asynk_new = new DownloadnetWork_New();
			asynk_new.execute();*/
			fetchdata();
		} else {
			try{
				ut.showD(AlrtsStatewise.this, "nonet");
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
				Intent i = new Intent(AlrtsStatewise.this,
				SelectMenu.class);
		//i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
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
				
				AlrtStateList sitem = new AlrtStateList();
				sitem.Setact("Alert");
				sitem.SetNetworkCode(StationName);
				sitem.Setcount(count);
				mSearchList.add(sitem);
			}while(c.moveToNext());
		}
		
		AlrtStatewiseAdptr adp = new AlrtStatewiseAdptr(AlrtsStatewise.this, mSearchList);
		adp.notifyDataSetChanged();
		mList.setAdapter(adp);
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
				Cursor c1 = sql.rawQuery("select cnt, InstalationId from AlrtCountTable where NetworkCode='"+StationName+"'", null);
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
		
		AlrtStatewiseAdptr adp = new AlrtStatewiseAdptr(AlrtsStatewise.this, mSearchList);
		adp.notifyDataSetChanged();
		mList.setAdapter(adp);
	}
	
	private void fetchdata(){
		if (asynk_new == null){
			mRefresh.setVisibility(View.VISIBLE);
			mProgress.setVisibility(View.GONE);
			
			asynk_new = null;
			asynk_new = new DownloadnetWork_New();
			asynk_new.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			 
			/*asynk = new DownloadnetWork();
			asynk.execute();*/
		} else {
			if (asynk_new.getStatus() == AsyncTask.Status.RUNNING){
				Log.e("ASYNC", "running");
				mRefresh.setVisibility(View.GONE);
				mProgress.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private boolean dbvalue(){
		try{
			//DatabaseHandler db1 = new DatabaseHandler(AlrtsStatewise.this);
			SQLiteDatabase sql = db.getWritableDatabase();
			//Cursor cursor = sql.rawQuery("SELECT NetworkCode FROM ConnectionStatusFiltermob", null);
			Cursor cursor = sql.rawQuery("Select distinct NetworkCode from AlrtCountTable where cnt != '0' Order by NetworkCode", null);
			cursor.moveToFirst();
			if (cursor != null && cursor.getCount()>0){
				if(cursor.getColumnIndex("NetworkCode")<0){
					cursor.close();
					/*sql.close();
					db1.close();*/
					return false;
				} else {
					cursor.close();
					/*sql.close();
					db1.close();*/
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
		return false;
	}
	
	public class DownloadnetWork extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String Url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetInstallationiMasterMobile?Mobile="+mobno;
			Log.e("All Station", "Url=" + Url);
			
			try{
				resposmsg = ut.httpGet(Url);
			}catch(IOException e){
				e.printStackTrace();
			}
			
			if(resposmsg.contains("<InstalationId>")){
				sop = "valid";
				//DatabaseHandler db = new DatabaseHandler(AlrtsStatewise.this);
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
				/*sql.close();
				db.close();*/
				
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
						ut.showD(AlrtsStatewise.this, "invalid");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				mRefresh.setVisibility(View.VISIBLE);
				mProgress.setVisibility(View.GONE);				
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
	

	public class DownloadnetWork_New extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String Url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/AlertCount";

			Log.e("Alert Count", "url : " + Url);
			Url = Url.replaceAll(" ", "%20");
			try {

				resposmsg = ut.httpGet(Url);
			}catch(IOException e){
				e.printStackTrace();
			}
			
			if(resposmsg.contains("<InstalationId>")){
				sop = "valid";
				//DatabaseHandler db = new DatabaseHandler(AlrtsStatewise.this);
				SQLiteDatabase sql = db.getWritableDatabase();
				String columnName, columnValue;

				//sql.execSQL("DROP TABLE IF EXISTS AlrtCountTable");
				//sql.execSQL(ut.getAlrtCountTable());
				sql.delete("AlrtCountTable",null,null);

				Cursor cur1 = sql.rawQuery("SELECT * FROM AlrtCountTable", null);
				cur1.getCount();
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
					updatelist2();
					updateAlertCount();
				} else {
					try{
						ut.showD(AlrtsStatewise.this, "invalid");
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
	
	public void updateAlertCount() {
		// searchResults.clear();
		//searchResults.clear();
		//DatabaseHandler db = new DatabaseHandler(AlrtsStatewise.this);
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
					
		
			SharedPreferences prefalertcount = AlrtsStatewise.this
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
	
	
}
