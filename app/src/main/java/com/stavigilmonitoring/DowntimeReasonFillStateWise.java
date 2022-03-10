package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.DowntimeReasonAdpt;
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

public class DowntimeReasonFillStateWise extends Activity {
	
	 List<StateList> searchResults;
	 ImageView iv;
	 String sop,responsemsg,mobno;
	 static DownloadxmlsDataURL_new asyncfetch_csnstate;
	 GridView lstcsn;
	 static SimpleDateFormat dff;
		static String Ldate;
		com.stavigilmonitoring.utility ut=new com.stavigilmonitoring.utility();
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.downtimestatewise);

		searchResults=new ArrayList<StateList>();
		lstcsn = findViewById(R.id.lstcsn);
		iv=(ImageView)findViewById(R.id.button_refresh_nonrepeated_main);

		db = new DatabaseHandler(getBaseContext());
		
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		
		if(asyncfetch_csnstate!=null && asyncfetch_csnstate.getStatus() == AsyncTask.Status.RUNNING)
		{
			Log.e("async","running");
			iv.setVisibility(View.GONE);
			((ProgressBar)findViewById(R.id.progressBar1)).setVisibility(View.VISIBLE);
		}
		Log.e("Downtime..."," dbval : "+dbvalue());

			if (dbvalue()) {
			updatelist();
		} 
			else if(isnet())
	      {
			fetchdata();
	}	
			else{
				showD("nonet");
			}
			
	
		
		lstcsn.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				Intent i=new Intent(DowntimeReasonFillStateWise.this,DowntimeReasonMain.class);
				i.putExtra("Type", searchResults.get(position).getNetworkcode());
				startActivity(i);			
			}
		});
		
		
		iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (isnet()) {
					asyncfetch_csnstate=null;
					asyncfetch_csnstate=new DownloadxmlsDataURL_new();
					asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					//fetchdata();
				} else {
					showD("nonet");
				}					
			}
		});		
	}
	private boolean dbvalue() {
		try{
		
	//	DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor cursor = sql.rawQuery("SELECT *   FROM AllStation", null);
		Cursor c=sql.rawQuery("SELECT * FROM DownTimeRason", null);
		if( (cursor != null && cursor.getCount() > 0)&&(c != null && c.getCount() > 0)) {
			cursor.moveToFirst();
			if(cursor.getColumnIndex("NetworkCode") < 0)
			{
				cursor.close();
				return false;
			}
			else
			{
			cursor.close();
			return true;
			}
		} else {
			cursor.close();
			return false;
		}
		}
		catch(Exception e){
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
		 //new DownloadxmlsDataURL_new().execute();	
	if(asyncfetch_csnstate == null)
	{
		iv.setVisibility(View.VISIBLE);
		((ProgressBar)findViewById(R.id.progressBar1)).setVisibility(View.GONE);
		
		Log.e("async","null");
		asyncfetch_csnstate=new DownloadxmlsDataURL_new();
		asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	else
	{
		if(asyncfetch_csnstate.getStatus() == AsyncTask.Status.RUNNING)
		{
			Log.e("async","running");
			iv.setVisibility(View.GONE);
			((ProgressBar)findViewById(R.id.progressBar1)).setVisibility(View.VISIBLE);
		}
	}
	
}
	
	public class DownloadxmlsDataURL_new extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			com.stavigilmonitoring.utility ut=new com.stavigilmonitoring.utility();
			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			String url="http://sta.vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetAllStation_Android";
			
			
			Log.e("csn status", "url : "+url);
			url = url.replaceAll(" ", "%20");
			try {				
				responsemsg = com.stavigilmonitoring.utility.httpGet(url);
				Log.e("csn status", "resmsg : "+responsemsg);
			
			
			if (responsemsg.contains("<NetworkCode>")) {
				sop = "valid";
				String columnName, columnValue;
				/*DatabaseHandler db = new DatabaseHandler(getBaseContext());					
				SQLiteDatabase sql = db.getWritableDatabase();*/
			
				//sql.execSQL("DROP TABLE IF EXISTS AllStation");
				//sql.execSQL(ut.getAllStation());
				sql.delete("AllStation",null,null);

				Cursor cur = sql.rawQuery("SELECT * FROM AllStation",
						null);				
				ContentValues values1 = new ContentValues();
				NodeList nl1 = ut.getnode(responsemsg, "Table1");
				//String msg = "";					
				//String columnName, columnValue;
				Log.e("All Station data..."," fetch data : "+ nl1.getLength());
				for (int i = 0; i < nl1.getLength(); i++) {
					Element e = (Element) nl1.item(i);						
					for (int j = 0; j < cur.getColumnCount(); j++) {
						columnName = cur.getColumnName(j);
						
						
						columnValue = ut.getValue(e, columnName);							
						values1.put(columnName, columnValue);	
						
						//Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
					}	
					sql.insert("AllStation", null, values1);						
				}

				cur.close();

				//url="http://sta.vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetCurrentlyStationDowntime_Android?Mobile="+mobno;
				url="http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetCurrentlyStationDowntime_Android?Mobile="+mobno;

				Log.e("csn status", "url : "+url);
				url = url.replaceAll(" ", "%20");
				try {				
					responsemsg = com.stavigilmonitoring.utility.httpGet(url);
					Log.e("csn status", "resmsg : "+responsemsg);
				} 
				catch(NullPointerException e)
				{
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
				catch (IOException e) {
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
				
				if (responsemsg.contains("<StationDownTimeID>")) {
					sop = "valid";
					
					//sql.execSQL("DROP TABLE IF EXISTS DownTimeRason");
					//sql.execSQL(ut.getDownTimeRason());
					sql.delete("DownTimeRason",null,null);

					Cursor cur1 = sql.rawQuery("SELECT * FROM DownTimeRason",
							null);				
					ContentValues values2 = new ContentValues();
					NodeList nl2 = ut.getnode(responsemsg, "Table1");
					
					Log.e("Downtime data..."," fetch data : "+ nl2.getLength());
					for (int i = 0; i < nl2.getLength(); i++) {
						Element e = (Element) nl2.item(i);						
						for (int j = 0; j < cur1.getColumnCount(); j++) {
							columnName = cur1.getColumnName(j);						
							
							columnValue = ut.getValue(e, columnName);							
							values2.put(columnName, columnValue);								
							
						}	
						sql.insert("DownTimeRason", null, values2);						
					}

					cur1.close();
				}

			} else {
				sop = "invalid";
				System.out
						.println("--------- invalid for project list --- ");
			}			
			
			
			} 
			catch(NullPointerException e)
			{
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
			catch (IOException e) {
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
			
			
			return sop;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try{
			if (sop.equals("valid")) {
				updatelist();
			} else {
				showD("invalid");
			}
			iv.setVisibility(View.VISIBLE);
			((ProgressBar)findViewById(R.id.progressBar1)).setVisibility(View.GONE);
		
			}catch(Exception e)
			{
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
			((ProgressBar)findViewById(R.id.progressBar1)).setVisibility(View.VISIBLE);
		}

	}
	
	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(DowntimeReasonFillStateWise.this);
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

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				myDialog.dismiss();
				// finish();

			}
		});

		myDialog.show();

	}
	public void onBackPressed() {
		super.onBackPressed();
        /*Intent i = new Intent(getBaseContext(), SelectMenu.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getBaseContext().startActivity(i);*/
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
		int count=0;
		Cursor c = sql.rawQuery("SELECT DISTINCT NetworkCode FROM AllStation",null);
		if (c.getCount() > 0) {
			c.moveToFirst();
			do
			{
				//int stncnt=0;
				String Type=c.getString(0);	
				
				Cursor c1 = sql.rawQuery("Select distinct InstallationName from DownTimeRason c1 inner join AllStation c2 on c1.InstallationId=c2.InstallationId where c2.NetworkCode='"+Type+"'", null);
				count=c1.getCount();
				/*if(c1.getCount() > 0)
				{
					c1.moveToFirst();
					do
					{		count=count+1;					
						int column1 = c1.getColumnIndex("NetworkCode");	
						//String[] tym = splitfromtym(c1.getString(column1));							
						
					}while(c1.moveToNext());
				}	*/
				
				Type=Type.replaceAll("0", "");
				Type=Type.replaceAll("1", "");
				if(!Type.trim().equalsIgnoreCase(""))
				{		
					StateList sitem=new StateList();
					sitem.SetNetworkCode(Type);
					sitem.Setcount(count);
					searchResults.add(sitem);
				}
			}while(c.moveToNext());			
		
	}			
	lstcsn.setAdapter(new DowntimeReasonAdpt(DowntimeReasonFillStateWise.this,searchResults));

	}

	/*class StateList
	{
		String Networkcode;
		String InstallationId;
		String StatioName;
		int count;
		public StateList() {}
		public void setInstallationId(String Networkcode){
			this.InstallationId=InstallationId;
		}
		public String getInstallationId()
		{
			return InstallationId;
		}
	
		public void SetNetworkCode(String Networkcode){
			this.Networkcode=Networkcode;
		}
		public String getNetworkcode()
		{
			return Networkcode;
		}
		public void Setcount(int count){
			this.count=count;
		}
		public int Getcount()
		{
			return count;
		}
	
	}*/

	

}
