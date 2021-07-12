package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.database.DBInterface;
import com.stavigilmonitoring.utility;

import android.app.Activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MaterialReceivedConfirmationDetails extends Activity {

	String datetostring, finalDate;
	String[] sendDate;
	String responsemsg,mobno;
	String materialname, reason, reporteename, pkdispatchid, fkorderid, mode,
			docketno, date, mobileno, stationname;
	Button btn_received, btn_returnact;
	TextView txtmaterialname, txtdocketno, txtcouriermode, txtmaterialQty,
			txtmaterialdispatchdate, txtreqstation, txtstatnname;
	String isrefresh = "false";
	static SimpleDateFormat dff;
	static String Ldate;
	com.stavigilmonitoring.utility ut;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.activity_receivedconfirmationdetails);

		Intent intent = getIntent();
		// materialname, reason, reporteename, pkdispatchid, fkorderid,
		// mode,docketno, date, mobileno

		pkdispatchid = intent.getStringExtra("pkdispatchid");
		fkorderid = intent.getStringExtra("fkorderid");
		materialname = intent.getStringExtra("materialname");
		reason = intent.getStringExtra("reason");
		reason = intent.getStringExtra("reason");
		reporteename = intent.getStringExtra("reporteename");
		mode = intent.getStringExtra("mode");
		docketno = intent.getStringExtra("docketno");
		date = intent.getStringExtra("date");
		mobileno = intent.getStringExtra("mobileno");
		stationname = intent.getStringExtra("stationname");

		txtmaterialname = (TextView) findViewById(com.stavigilmonitoring.R.id.txtmaterialname);
		txtdocketno = (TextView) findViewById(com.stavigilmonitoring.R.id.txtdocketno);
		txtcouriermode = (TextView) findViewById(com.stavigilmonitoring.R.id.txtcouriermode);
		txtmaterialQty = (TextView) findViewById(com.stavigilmonitoring.R.id.txtmaterialsendby);
		txtmaterialdispatchdate = (TextView) findViewById(com.stavigilmonitoring.R.id.txtmaterialdispatchdate);
		txtstatnname = (TextView)findViewById(R.id.txtstatnname);

		btn_received = (Button) findViewById(com.stavigilmonitoring.R.id.btn_received);
		btn_returnact = (Button) findViewById(com.stavigilmonitoring.R.id.btn_returnact);

		txtcouriermode.setText(mode);
		txtdocketno.setText(docketno);
		txtmaterialdispatchdate.setText(date);
		txtmaterialname.setText(materialname);
		txtmaterialQty.setText(reporteename);
		txtstatnname.setText(stationname);

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		dbi.Close();

		btn_received.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isnet()) {
					DownloadxmlsDataURL asyncfetch_delivered = new DownloadxmlsDataURL();
					asyncfetch_delivered.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					showD("invalid");
				}
			}
		});

		btn_returnact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						MaterialReceivedConfirmation.class);
				intent.putExtra("isrefresh", isrefresh);
				startActivity(intent);
				MaterialReceivedConfirmationDetails.this.finish();

			}
		});
	}

	private boolean isnet() {
		// TODO Auto-generated method stub
		Context context = this.getApplicationContext();
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public class DownloadxmlsDataURL extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();

			String url;

			url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/ReceivedDispatch?orderHeaderId="

					+ fkorderid+"&mobileno="+mobno;

			Log.e("material ", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				System.out.println("-------  activity url --- " + url);
				responsemsg = ut.httpGet(url);

				System.out.println("-------------  xx vale-- " + responsemsg);

				responsemsg = responsemsg
						.substring(responsemsg.indexOf(">") + 1);
				responsemsg = responsemsg
						.substring(responsemsg.indexOf(">") + 1);
				responsemsg = responsemsg
						.substring(0, responsemsg.indexOf("<"));

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
				if (responsemsg.equals("Error")) {
					showD("Error");
					((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar__1))
							.setVisibility(View.GONE);

				} else {
					isrefresh = "true";
					Toast.makeText(MaterialReceivedConfirmationDetails.this,
							"Material request received confirm successfully",
							Toast.LENGTH_LONG).show();

					// DatabaseHandler db1 = new DatabaseHandler(
					// getApplicationContext());
					// SQLiteDatabase db = db1.getWritableDatabase();
					//
					// ContentValues contentValues = new ContentValues();

					// db.update("ReceivedConfirmation", contentValues,
					// "fkorderid=?", new String[] { fkorderid });
					//
					// db.delete("ReceivedConfirmation", "fkorderid=?",
					// new String[] { fkorderid });

					((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar__1))
							.setVisibility(View.GONE);

                    Intent intent = new Intent(getApplicationContext(),	MaterialReceivedConfirmation.class);
                    intent.putExtra("isrefresh", isrefresh);
                    startActivity(intent);
                    MaterialReceivedConfirmationDetails.this.finish();

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
			// iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar__1))
					.setVisibility(View.VISIBLE);
		}

	}

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(
				MaterialReceivedConfirmationDetails.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoginfosmall);
		myDialog.setCancelable(true);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);

		TextView txt = (TextView) myDialog
				.findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall);
		if (string.equals("empty")) {
			myDialog.setTitle("Error...");
			txt.setText("Please Fill required data..");
		} else if (string.equals("nonet")) {
			myDialog.setTitle("Error...");
			txt.setText("No Internet Connection Found.Please Activate internet Connectin on Device..");
		} else if (string.equals("invalid")) {
			myDialog.setTitle(" ");
			txt.setText("No Refresh Data Available.Please check internet connection...");
		} else if (string.equals("Error")) {
			myDialog.setTitle(" ");
			txt.setText("Server Error.. Please try after some time");
		}

		Button btn = (Button) myDialog
				.findViewById(com.stavigilmonitoring.R.id.gotobtndialoginfosmall);
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
		super.onBackPressed();
		Intent intent = new Intent(getApplicationContext(),	MaterialReceivedConfirmation.class);
		intent.putExtra("isrefresh", isrefresh);
		startActivity(intent);
		MaterialReceivedConfirmationDetails.this.finish();

	}
}