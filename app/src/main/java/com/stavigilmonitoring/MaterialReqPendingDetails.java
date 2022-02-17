package com.stavigilmonitoring;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.stavigilmonitoring.utility;

public class MaterialReqPendingDetails extends Activity {
	Button buttonDatepicker, button_approve, button_reject;
	private RadioGroup rdgroup;
	private RadioButton radioButton;
	RadioButton rdbtnrepair, rdbtnnew;
	String Sel_RadBtnVal, matApproveCategory;
	int year, month, day;
	String datetostring, finalDate;
	String[] sendDate;
	String responsemsg, orderHeaderId, requestdate;
	String materialname, reason, qty, stationname, scraprepair, addedby;
	TextView txtStationName, txtGetmaterialReason, txtmaterialRequestdate,
			txtScrap, txtmaterialQty, txtreqmaterial, txtsender, txtsendername;
	String isrefreshed = "false";
	String message;
	boolean b;
	String Requestfor, qnty, by, from, OrderHeaderId, Reason;
	static SimpleDateFormat dff;
	static String Ldate;
	com.stavigilmonitoring.utility ut;
	String reportingid;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.materialdelivery);

		txtreqmaterial = (TextView) findViewById(com.stavigilmonitoring.R.id.txtreqmaterial);
		txtStationName = (TextView) findViewById(com.stavigilmonitoring.R.id.txtStationName);
		txtGetmaterialReason = (TextView) findViewById(com.stavigilmonitoring.R.id.txtGetmaterialReason);
		txtScrap = (TextView) findViewById(com.stavigilmonitoring.R.id.txtScrap);
		txtmaterialQty = (TextView) findViewById(com.stavigilmonitoring.R.id.txtmaterialQty);
		txtsender = (TextView)findViewById(R.id.txtsender);
		txtsendername = (TextView)findViewById(R.id.txtsendername);

		// "Request for : " + materialname + " , qty : " + qty + ", by : " +
		// sendername + ", from : " + stationame + ", ScrapRepair : "
		// +scraprepair+ "," +
		// " OrderHeaderId : "+pkordermain + ", Reason : "+reason

		Intent intent = getIntent();
		message = intent.getExtras().getString("message");
		
	//	*****************getShared prefrence **********
		
		SharedPreferences preflmsconn = getApplicationContext()
				.getSharedPreferences("MyReport", Context.MODE_PRIVATE);
		Editor editorlmsConne = preflmsconn.edit();
	    reportingid = preflmsconn.getString("reportingID", "");
		db = new DatabaseHandler(getApplicationContext());
		
		if (message != null) {
			Log.d("test", "if");

			String[] parts = message.split(",");
			Requestfor = parts[0]; // 004
			qnty = parts[1];
			by = parts[2];
			from = parts[3];
			scraprepair = parts[4];
			OrderHeaderId = parts[5];
			Reason = parts[6];

			String[] parts1 = Requestfor.split(":");
			String a1 = parts1[0];
			materialname = parts1[1];

			String[] parts2 = qnty.split(":");
			String a2 = parts2[0];
			qty = parts2[1];

			String[] parts3 = by.split(":");
			String a3 = parts3[0];
			String b3 = parts3[1];

			String[] parts4 = from.split(":");
			String a4 = parts4[0];
			stationname = parts4[1];

			String[] parts5 = scraprepair.split(":");
			String a5 = parts5[0];
			scraprepair = parts5[1];

			String[] parts6 = OrderHeaderId.split(":");
			String a6 = parts6[0];
			orderHeaderId = parts6[1];

			String[] parts7 = Reason.split(":");
			String a7 = parts7[0];
			reason = parts7[1];

			// txtmaterialRequestdate.setText(requestdate);
			txtreqmaterial.setText(materialname);
			txtmaterialQty.setText(qty);
			txtStationName.setText(stationname);
			txtGetmaterialReason.setText(reason);
			txtScrap.setText(scraprepair);

		} else {

			Log.d("test", "else");
			orderHeaderId = intent.getStringExtra("orderHeaderId");
			scraprepair = intent.getStringExtra("scraprepair");
			stationname = intent.getStringExtra("stationname");
			qty = intent.getStringExtra("qty");
			reason = intent.getStringExtra("reason");
			materialname = intent.getStringExtra("materialname");
			addedby = intent.getStringExtra("addedby");

			// requestdate = intent.getStringExtra("addedtdt");
			// txtmaterialRequestdate.setText(requestdate);
			txtreqmaterial.setText(materialname);
			txtmaterialQty.setText(qty);
			txtStationName.setText(stationname);
			txtGetmaterialReason.setText(reason);
			txtScrap.setText(scraprepair);
			txtsendername.setText(addedby);

		}

		// txtGetmaterialReason.setText(reason);
		// txtStationName.setText(stationname);
		// // txtmaterialRequestdate.setText(requestdate);
		// txtScrap.setText(scraprepair);
		// txtmaterialQty.setText(qty);
		// txtreqmaterial.setText(materialname);

		buttonDatepicker = (Button) findViewById(com.stavigilmonitoring.R.id.buttonDatepicker);
		button_approve = (Button) findViewById(com.stavigilmonitoring.R.id.button_approve);
		button_reject = (Button) findViewById(com.stavigilmonitoring.R.id.button_return);
		rdgroup = (RadioGroup)findViewById(R.id.rdgroup);
		rdbtnrepair = (RadioButton)findViewById(R.id.rdbtnrepair);
		rdbtnnew = (RadioButton)findViewById(R.id.rdbtnnew);

		buttonDatepicker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Date date = new Date();
				final Calendar c = Calendar.getInstance();

				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DAY_OF_MONTH);

				// Launch Date Picker Dialog
				DatePickerDialog datePickerDialog = new DatePickerDialog(
						MaterialReqPendingDetails.this,
						new DatePickerDialog.OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker datePicker,
									int year, int monthOfYear, int dayOfMonth) {

								buttonDatepicker.setText(dayOfMonth + "-"
										+ (monthOfYear + 1) + "-" + year);
								datetostring = dayOfMonth + "-"
										+ (monthOfYear + 1) + "-" + year;
							}

						}, year, month, day + 3);
				datePickerDialog.show();
			}
		});

		rdgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				// get selected radio button from radioGroup
				int selectedId = rdgroup.getCheckedRadioButtonId();

				// find the radiobutton by returned id
				radioButton = (RadioButton) findViewById(selectedId);

				String radvalue = ((RadioButton)findViewById(rdgroup.getCheckedRadioButtonId())).getText().toString();
				Sel_RadBtnVal = radvalue;

				//Toast.makeText(getApplicationContext(), "selected button - "+Sel_RadBtnVal, Toast.LENGTH_SHORT).show();
			}
		});

		button_approve.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				try {
					if (isnet()) {
						if ((datetostring == null && datetostring.equals(""))) {
							showD("empty");
						} else {
							sendDate = splitDT(datetostring);
							matApproveCategory = Sel_RadBtnVal;
							DownloadxmlsDataURL_new dataURL_new = new DownloadxmlsDataURL_new();
							dataURL_new.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						}
					}
				} catch (Exception e) {
					showD("empty");
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
			}
		});

		button_reject.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isnet()) {
					DownloadxmlsDataURL dataURL = new DownloadxmlsDataURL();
					dataURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
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

	private String[] splitDT(String tf) {
		// TODO Auto-generated method stub
		// Oct 13 2015 1:05PM
		if (!(tf.equals("") || tf == null)) {

			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date myDate = null;
			try {
				myDate = dateFormat.parse(tf);
				System.out.println("..........value of my date after conv"
						+ myDate);

			} catch (ParseException e) {
				e.printStackTrace();
			}
			SimpleDateFormat timeFormat = new SimpleDateFormat("MM/dd/yyyy");
			finalDate = timeFormat.format(myDate);

		} else {
			finalDate = "";
		}

		String[] v = { finalDate };

		return v;

	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();

			String url;

			url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/ackRequest?orderHeaderId="+ orderHeaderId
					+"&reportingid="+reportingid+"&ApproveCategory="+matApproveCategory;

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
					((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1)).setVisibility(View.GONE);

				} else {

					Toast.makeText(MaterialReqPendingDetails.this,
							"Material request approved successfully", Toast.LENGTH_LONG).show();
					isrefreshed = "true";
					//DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
					SQLiteDatabase sqldb = db.getWritableDatabase();
					//
					// ContentValues contentValues = new ContentValues();
					// contentValues.put("statusflag", "1");
					//
					// db.update("PendingRequests", contentValues,
					// "pkmaterialid=?", new String[] { orderHeaderId });
					//
					sqldb.delete("PendingRequests", "pkmaterialid=?",
							new String[] { orderHeaderId });

					Intent intent = new Intent(MaterialReqPendingDetails.this,
							MaterialReqPendingList.class);
					intent.putExtra("isrefreshed", isrefreshed);
					startActivity(intent);
					MaterialReqPendingDetails.this.finish();

					((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1)).setVisibility(View.GONE);

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
			// iv.setVisibility(View.GONE);
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}

	}

	public class DownloadxmlsDataURL extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();

			String url;

			url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/RejectOrder?orderHeaderId="

				+ orderHeaderId+"&reportingid="+reportingid;

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
					((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
							.setVisibility(View.GONE);

				} else {

					Toast.makeText(MaterialReqPendingDetails.this,
							"Material request rejected successfully",
							Toast.LENGTH_LONG).show();
					isrefreshed = "true";
					//DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
					SQLiteDatabase sqldb = db.getWritableDatabase();
					//
					// ContentValues contentValues = new ContentValues();
					// contentValues.put("rejectedorder", "1");
					// db.update("PendingRequests", contentValues,
					// "pkmaterialid=?", new String[] { orderHeaderId });
					//
					sqldb.delete("PendingRequests", "pkmaterialid=?",
							new String[] { orderHeaderId });

					Intent intent = new Intent(MaterialReqPendingDetails.this,
							MaterialReqPendingList.class);
					intent.putExtra("isrefreshed", isrefreshed);
					startActivity(intent);
					MaterialReqPendingDetails.this.finish();

					((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
							.setVisibility(View.GONE);

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
			((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}

	}

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(MaterialReqPendingDetails.this);
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
		} else if (string.equals("nodata")) {
			myDialog.setTitle(" ");
			txt.setText("No data available");
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

		Intent intent = new Intent(MaterialReqPendingDetails.this,
				MaterialReqPendingList.class);
		intent.putExtra("isrefreshed", isrefreshed);
		startActivity(intent);
		MaterialReqPendingDetails.this.finish();

		// Intent intent=new Intent(getApplicationContext(),
		// MaterialReqAckList.class);
		// intent.putExtra("isrefresh", isrefresh);
		// startActivity(intent);
		// MaterialReqAckDetails.this.finish();
	}
}
