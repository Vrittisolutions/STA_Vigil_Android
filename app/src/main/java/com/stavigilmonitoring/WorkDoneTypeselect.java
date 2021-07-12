package com.stavigilmonitoring;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.WorkDoneTypeselectAdp;
import com.beanclasses.WorkTypeselect;
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
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

public class WorkDoneTypeselect extends Activity{

	private ImageView mRefreshTyp;
	private ProgressBar mProgress;
	private ListView mList;
	private com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	private String sop;
	private String dff, Ldate,mobno;
	private ArrayList<WorkTypeselect> searchResults;
	private static DownloadWorkType asynwork;
	WorkDoneTypeselectAdp doneTypeAdp;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		setContentView(com.stavigilmonitoring.R.layout.worktypeselect);
		searchResults = new ArrayList<WorkTypeselect>();
		mRefreshTyp = (ImageView) findViewById(com.stavigilmonitoring.R.id.btnrefreshworktype);
		mProgress = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBarworktyp);
		mList = (ListView) findViewById(com.stavigilmonitoring.R.id.lstcsn);

		db = new DatabaseHandler(getBaseContext());
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		if (dbvalue()) {
			updatelist();
		} else if (ut.isnet(getApplicationContext())) {
			fetchdata();
		} else {
			ut.showD(WorkDoneTypeselect.this, "nonet");
		}
		mRefreshTyp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (ut.isnet(getApplicationContext())) {
					fetchdata();
				} else {
					ut.showD(WorkDoneTypeselect.this, "nonet");
				}
			}
		});
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String message = searchResults.get(position).getWorktype();
				Intent intent = new Intent();
				intent.putExtra("WorkTypeData", message);
				intent.putExtra("worktypeid", searchResults.get(position).getWorkid());
				setResult(1, intent);
				finish();// finishing activity

			}
		});
	}

	private void fetchdata() {
		// new DownloadxmlsDataURL_new().execute();

		asynwork = new DownloadWorkType();
		asynwork.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	public class DownloadWorkType extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mRefreshTyp.setVisibility(View.GONE);
			mProgress.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {

			//DatabaseHandler db = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			String columnName, columnValue;
			String urlnet = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/getWorkType";

			urlnet = urlnet.replaceAll(" ", "%20");
			Log.e("work type", "6th" + urlnet);

			try {
				String responsemsg = ut.httpGet(urlnet);
				NodeList NL = ut.getnode(responsemsg, "Table1");
				Log.e("SubnetCount", "len :" + NL.getLength());

				if (responsemsg.contains("<WorkTypeId>")) {
					sop = "valid";
					//sql.execSQL("DROP TABLE IF EXISTS WorkType");
					//sql.execSQL(ut.getWorkType());
					sql.delete("WorkType",null,null);

					Cursor cur1 = sql.rawQuery("SELECT * FROM WorkType", null);
					ContentValues values2 = new ContentValues();

					Log.e("WorkType data...", " fetch data : " + NL.getLength());
					for (int i = 0; i < NL.getLength(); i++) {
						Element e = (Element) NL.item(i);
						Log.e("WorkType data...", " fetch data : " + i);
						for (int j = 0; j < cur1.getColumnCount(); j++) {
							columnName = cur1.getColumnName(j);

							columnValue = ut.getValue(e, columnName);
							Log.e("WorkType data...", " fetch data : "
									+ columnValue);
							values2.put(columnName, columnValue);
							// SubnetString = "Valid";

						}
						long ad = sql.insert("WorkType", null, values2);
					}

					cur1.close();
					sql.close();
					//db.close();

				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();

				sql.close();
				//db.close();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (dbvalue()) {
				updatelist();
				mRefreshTyp.setVisibility(View.VISIBLE);
				mProgress.setVisibility(View.GONE);
			}

		}

	}

	private void updatelist() {
		searchResults.clear();
		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		int count = 0;
		Cursor c = sql.rawQuery("SELECT WorkTypeName,WorkTypeId FROM WorkType order by WorkTypeName", null);
		if (c.getCount() > 0) {
			c.moveToFirst();
			do {

				String Type = c.getString(c.getColumnIndex("WorkTypeName"));
				String Type1 = c.getString(c.getColumnIndex("WorkTypeId"));
				WorkTypeselect bean = new WorkTypeselect();
				bean.setWorktype(Type);
				bean.setWorkid(Type);
				searchResults.add(bean);

			} while (c.moveToNext());

			c.close();
			//db.close();
			sql.close();

		}

		doneTypeAdp = new WorkDoneTypeselectAdp(WorkDoneTypeselect.this, searchResults);
		mList.setAdapter(doneTypeAdp);

	}

	private boolean dbvalue() {

		try {
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery("SELECT * FROM WorkType", null);// SoundLevel_new

			System.out.println("----------  dbvalue screen cursor count -- "
					+ cursor.getCount());

			if (cursor != null && cursor.getCount() > 0) {

				cursor.close();
				sql.close();
				//db1.close();
				return true;

			} else {

				cursor.close();
				sql.close();
				//db1.close();
				return false;
			}
		} catch (Exception e) {

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

			return false;
		}

	}

	/*class WorkTypeselect {
		String worktype;
		String workid;

		public String getWorktype() {
			return worktype;
		}

		public void setWorktype(String worktype) {
			this.worktype = worktype;
		}

		public String getWorkid() {
			return workid;
		}

		public void setWorkid(String workid) {
			this.workid = workid;
		}

	}*/

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra("WorkTypeData", "");
		intent.putExtra("worktypeid", "");
		setResult(1, intent);
		finish();
	}


}
