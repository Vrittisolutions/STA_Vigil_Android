package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.database.DBInterface;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TVStatusfillReason extends Activity {
	int requestCode = 11;
	String reasonCode = "", reasonDesc = "";
	EditText ED;
	ProgressDialog progressdialogupdateserver;
	AsyncTask refreshasyncupdateserver;
	String responsesoap = "Added";
	String Stationname, mobno;
	String responsemsg = "k", sop = "";
	String result = "", Type = "";
	static SimpleDateFormat dff;
	static String Ldate;
	String StationName;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tvstatusfillreason);

		Intent i = getIntent();
		StationName = i.getStringExtra("StationName");
		Type = i.getStringExtra("Type");
		ED = (EditText) findViewById(R.id.edsationname);

		db = new DatabaseHandler(getBaseContext());
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();

		Button btn = (Button) findViewById(R.id.btncsnncancel1);

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				/*Intent i = new Intent(TVStatusfillReason.this,
						TvStatusMain.class);
				i.putExtra("Type", Type);
				startActivity(i);*/
				finish();
			}
		});
	}

	public void StationList(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		Intent intent = new Intent(TVStatusfillReason.this,
				TvStatusReasonList.class);
		intent.putExtra("StationName", StationName);
        startActivityForResult(intent, requestCode);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			reasonDesc = data.getExtras().getString("ReasonDesc");

			ED.setText(reasonDesc);
		}
	}

	public class DownloadxmlsDataURL extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String url = "http://sta.vritti.co/imedia/STA_Announcement/TimeTable.asmx/GetTVStatus_Android_new?Mobile="
					+ mobno;

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = ut.httpGet(url);
				Log.e("csn status", "resmsg : " + responsemsg);

				if (responsemsg.contains("<A>")) {
					sop = "valid";
					String columnName, columnValue;
					//DatabaseHandler db = new DatabaseHandler(getBaseContext());
					SQLiteDatabase sql = db.getWritableDatabase();

					//sql.execSQL("DROP TABLE IF EXISTS TvStatus");
					//sql.execSQL(ut.getTvStatus());
					sql.delete("TvStatus",null,null);

					Cursor cur = sql.rawQuery("SELECT *   FROM TvStatus", null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "Table1");
					// String msg = "";
					// String columnName, columnValue;
					Log.e("sts main...", " fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							columnName = cur.getColumnName(j);

							String ncolumnname = "";
							if (columnName.equalsIgnoreCase("InstallationId"))
								ncolumnname = "A";
							else if (columnName
									.equalsIgnoreCase("TVStatusReason"))
								ncolumnname = "G";
							else if (columnName.equalsIgnoreCase("TVStatus"))
								ncolumnname = "J";
							else if (columnName.equalsIgnoreCase("Type"))
								ncolumnname = "P";

							columnValue = ut.getValue(e, ncolumnname);
							values1.put(columnName, columnValue);

							// Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
						}
						sql.insert("TvStatus", null, values1);
					}

					cur.close();
					sql.close();
					//db.close();

				} else {
					System.out
							.println("--------- invalid for project list --- ");
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

			progressdialogupdateserver.cancel();

			if (result.equalsIgnoreCase("valid")) {
				Toast.makeText(getApplicationContext(),
						"Reason Updated Successfully..!", Toast.LENGTH_LONG)
						.show();

				/*Bundle dataBundle = new Bundle();
				dataBundle.putString("Type", Type);
				Intent i = new Intent(getBaseContext(), TvStatusMain.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtras(dataBundle);
				getBaseContext().startActivity(i);*/
				finish();

			} else {

			}
		}
	}

	public void updateReason(View v) {

		progressdialogupdateserver = ProgressDialog.show(
				TVStatusfillReason.this, "Update Reason.......",
				"Please Wait....", true, true, new OnCancelListener() {

					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						if (refreshasyncupdateserver != null
								&& refreshasyncupdateserver.getStatus() != AsyncTask.Status.FINISHED) {
							refreshasyncupdateserver.cancel(true);
						}
					}
				});

		refreshasyncupdateserver = new Updatetoserver().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


	}

	class Updatetoserver extends AsyncTask<String, Void, String> {

		private String installationId = "";

		@Override
		protected String doInBackground(String... paramss) {
			try {
				//DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
				SQLiteDatabase sqldb = db.getWritableDatabase();

				Cursor c2 = sqldb.rawQuery(
						"SELECT * FROM TVStatusReason where ReasonDescription='"
								+ reasonDesc + "' ", null);

				reasonCode = "";
				if (c2.getCount() == 0) {
					c2.close();
					//db.close();
					//db1.close();
				} else {
					c2.moveToFirst();
					reasonCode = c2.getString(c2.getColumnIndex("ReasonCode"));
					System.out
							.println("...................reason code value is.............."
									+ reasonCode);
					c2.moveToLast();
					c2.close();
					//db.close();
					//db1.close();

				}
			} catch (Exception e) {
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
			try {

				//DatabaseHandler db2 = new DatabaseHandler(getApplicationContext());
				SQLiteDatabase dbf = db.getWritableDatabase();

				Cursor cf = dbf.rawQuery(
						"SELECT * FROM ConnectionStatusUser1 where InstallationDesc='"
								+ StationName + "' ", null);
				installationId = "";
				if (cf.getCount() == 0) {

				} else {
					cf.moveToFirst();
					installationId = cf.getString(cf
							.getColumnIndex("InstallationId"));

					cf.moveToLast();


				}
			} catch (Exception e) {
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
			Log.e("Details", "" + installationId + "" + reasonCode + ""
					+ reasonDesc + "" + mobno);
			if (ut.isnet(getApplicationContext())) {
				String url = "http://sta.vritti.co/imedia/STA_Announcement/TimeTable.asmx/UpdateTVStatusReason_Android?InstallationId="
						+ installationId
						+ "&MobileNo="
						+ mobno
						+ "&ReasonCode="
						+ reasonCode
						+ "&ReasonDesc="
						+ reasonDesc;
				url = url.replaceAll(" ", "%20");
				try {
					responsemsg = ut.httpGet(url);
				} catch (NullPointerException e) {
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

				} catch (IOException e) {
					e.printStackTrace();
					responsemsg = "wrong" + e.toString();
					dff = new SimpleDateFormat("HH:mm:ss");
					Ldate = dff.format(new Date());

					StackTraceElement l = new Exception().getStackTrace()[0];
					System.out.println(l.getClassName() + "/"
							+ l.getMethodName() + ":" + l.getLineNumber());
					ut = new utility();
					if (!ut.checkErrLogFile()) {

						ut.ErrLogFile();
					}
					if (ut.checkErrLogFile()) {
						ut.addErrLog(l.getClassName() + "/" + l.getMethodName()
								+ ":" + l.getLineNumber() + "	"
								+ e.getMessage() + " " + Ldate);
					}

				}
			} else {
				try {
					ut.showD(TVStatusfillReason.this,"nonet");
				}catch (Exception e){
					e.printStackTrace();
				}
			}

			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (responsemsg.contains("Added")) {
				/*
				 * Toast.makeText(getApplicationContext(),
				 * "Reason Update Successfully...", Toast.LENGTH_LONG).show();
				 */
				new DownloadxmlsDataURL().executeOnExecutor(THREAD_POOL_EXECUTOR);

				/*
				 * Toast.makeText(getApplicationContext(),
				 * "Reason Updated Successfully..!", Toast.LENGTH_LONG) .show();
				 * 
				 * Bundle dataBundle = new Bundle();
				 * dataBundle.putString("stnname", Stationname);
				 * dataBundle.putString("frompage", frompage); Intent i = new
				 * Intent(getBaseContext(), ConnectionStatus.class);
				 * i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				 * i.putExtras(dataBundle); getBaseContext().startActivity(i);
				 * finish();
				 */

			} else {
				Toast.makeText(getApplicationContext(), "Server Error..",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
}