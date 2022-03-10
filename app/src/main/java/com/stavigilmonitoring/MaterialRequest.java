package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beanclasses.reporteeBean;
import com.database.DBInterface;
import com.services.WakeLocker;

import static com.stavigilmonitoring.WorkAssign_AssignActivity.Year;
import static com.stavigilmonitoring.WorkAssign_AssignActivity.day;
import static com.stavigilmonitoring.WorkAssign_AssignActivity.month;

public class MaterialRequest extends Activity {
	Context parent;
	Button ButtonStationNmae;
	Button ButtonMaterialName;
	Button ButtonMaterialReqTO;
	Button ButtonReason, ButtonSeldate;
	Spinner spinner;
	EditText editTextRepair, edittextQty;
	Button btnsave, btnreturn;
	String mobno;
	static SimpleDateFormat dff;
	com.stavigilmonitoring.utility ut;
	static String Ldate;
	String responsemsg = "k";
	String sop = "no";
	static String stationid, stationname, materialname,MaterialID;
	static String reportingid, reportingname;
	static String Quantity, ScrapRepair, reason;
	DownloadxmlsDataURL_new asyncfetch_csnstate;
	public static String scraprepair, flag;
	private static int NOTIFICATION_ID = 1;

	private RadioGroup radioGroup;
	private RadioButton radioButton;
	String Sel_RadBtnVal;
	String trnselectDate, SelectedDate;
	String Reporting_managerName, Reporting_managerID;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.materailreqapply);

		parent = MaterialRequest.this;
		btnsave = (Button) findViewById(R.id.button_save);
		btnreturn = (Button) findViewById(R.id.button_return);
		ButtonMaterialName = (Button) findViewById(R.id.btnMaterialName);
		ButtonStationNmae = (Button) findViewById(R.id.btnStationNmae);
		ButtonMaterialReqTO = (Button) findViewById(R.id.btnMaterialReqTO);
		ButtonReason = (Button) findViewById(R.id.ButtonReason);
		ButtonSeldate = (Button)findViewById(R.id.ButtonSeldate);
		ButtonSeldate.setVisibility(View.VISIBLE);
		edittextQty = (EditText) findViewById(R.id.editTextQty);
		// editTextRepair = (EditText) findViewById(R.id.editTextRepair);
		spinner = (Spinner) findViewById(R.id.spinnerScrap);

		radioGroup = (RadioGroup)findViewById(R.id.radiogroup);
		radioGroup.setVisibility(View.VISIBLE);

		db = new DatabaseHandler(getApplicationContext());
		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();

		registerReceiver(mHandleMessageReceiver, new IntentFilter(Config.DISPLAY_MESSAGE_ACTION));
		SharedPreferences prefmaterial = getApplicationContext().getSharedPreferences("Material", Context.MODE_PRIVATE);
		Editor editorMaterial = prefmaterial.edit();
		//String MaterialStation = prefmaterial.getString("materialreq", "");
		//stationid=prefmaterial.getString("materialreq_id", "");
		//materialreq_id
		//ButtonStationNmae.setText(MaterialStation);

		ButtonStationNmae.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MaterialRequest.this, MaterialReqStatewiseActivity.class);
				intent.putExtra("mobileno", mobno);
				startActivityForResult(intent, Common.MaterialStn1);
				//startActivity(intent);
				//finish();
			}
		});

		ButtonMaterialName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MaterialRequest.this, MaterialListActivity.class);
				startActivityForResult(intent, Common.MaterialName);
			}
		});

		ButtonReason.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MaterialRequest.this,
						MaterialReason.class);
				startActivityForResult(intent, Common.MaterialReason);
			}
		});

		ButtonSeldate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				Date date = new Date();
				final Calendar c = Calendar.getInstance();

				Year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DAY_OF_MONTH);

				// Launch Date Picker Dialog
				DatePickerDialog datePickerDialog = new DatePickerDialog(parent,
						new DatePickerDialog.OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker datePicker, int year,
												  int monthOfYear, int dayOfMonth) {
								// Display Selected date in textbox

								ButtonSeldate.setText(dayOfMonth + "-"
										+ (monthOfYear + 1) + "-" + year);
								trnselectDate = year + "-" + (monthOfYear + 1)
										+ "-" + dayOfMonth+ " 00:00:00.000";

								String seldate = trnselectDate;
								SelectedDate = (dayOfMonth + "-"
										+ (monthOfYear + 1) + "-" + year);

                              /*  if (year>=Year){
                                    if((year==Year)&&(monthOfYear>=month)){
                                        if((monthOfYear==month)&&(dayOfMonth>=day)){
                                            btn_selectdate.setText(dayOfMonth + "-"
                                                    + (monthOfYear + 1) + "-" + year);
                                            trnselectDate = year + "-" + (monthOfYear + 1)
                                                    + "-" + dayOfMonth+ " 00:00:00.000";
                                        }*//*else if((monthOfYear==month)&&(dayOfMonth<day)){
                                            btn_selectdate.setText(day + "-"
                                                    + (month + 1) + "-" + Year);
                                            trnselectDate = Year + "-" + (month + 1)
                                                    + "-" + day+ " 00:00:00.000";
                                            //2018-01-15 16:43:40.440
                                            Toast.makeText(getApplicationContext(),
                                                    "Past date is not accepted",Toast.LENGTH_SHORT).show();
                                        }*//*else if(monthOfYear>month){
                                            btn_selectdate.setText(dayOfMonth + "-"
                                                    + (monthOfYear + 1) + "-" + year);
                                            trnselectDate = year + "-" + (monthOfYear + 1)
                                                    + "-" + dayOfMonth+ " 00:00:00.000";
                                        }
                                    }else if(year>Year){
                                        btn_selectdate.setText(dayOfMonth + "-"
                                                + (monthOfYear + 1) + "-" + year);
                                        trnselectDate = year + "-" + (monthOfYear + 1)
                                                + "-" + dayOfMonth+ " 00:00:00.000";
                                    }*//*else if((year==Year)&&(monthOfYear<month)){
                                        btn_selectdate.setText(day + "-"
                                                + (month + 1) + "-" + Year);
                                        trnselectDate = Year + "-" + (month + 1)
                                                + "-" + day+ " 00:00:00.000";
                                        Toast.makeText(getApplicationContext(),"Past date is not accepted",Toast.LENGTH_SHORT).show();
                                    }*//*
                                }else {
                                    btn_selectdate.setText(day + "-"
                                            + (month + 1) + "-" + Year);
                                    trnselectDate = Year + "-" + (month + 1)
                                            + "-" + day+ " 00:00:00.000";
                                    Toast.makeText(getApplicationContext(),"Past date is not accepted",Toast.LENGTH_SHORT).show();
                                }*/

							}
						}, Year, month, day);
				datePickerDialog.show();
			}
		});

		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				// get selected radio button from radioGroup
				int selectedId = radioGroup.getCheckedRadioButtonId();
				// find the radiobutton by returned id
				radioButton = (RadioButton) findViewById(selectedId);

				String radvalue = ((RadioButton)findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
				Sel_RadBtnVal = radvalue;

				//Toast.makeText(getApplicationContext(), "selected button - "+Sel_RadBtnVal, Toast.LENGTH_SHORT).show();
			}
		});


		ButtonMaterialReqTO.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//getReporting_ManagerName();

				Intent intent = new Intent(MaterialRequest.this,
						MaterialReqSendTo.class);
				intent.putExtra("mobileno", mobno);
				startActivityForResult(intent, Common.MaterialRqesendto);
			}
		});

		spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

		btnreturn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MaterialRequest.this.finish();
			}
		});

		btnsave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (isvalid()) {

					/*reportingid = getReportingId(ButtonMaterialReqTO.getText()
							.toString().trim());
					SharedPreferences pref = getApplicationContext()
							.getSharedPreferences("MyReport", Context.MODE_PRIVATE);
					Editor editor = pref.edit();
					editor.putString("reportingID", reportingid);
					editor.commit();*/
					stationname = ButtonStationNmae.getText().toString().trim();
					materialname = ButtonMaterialName.getText().toString().trim();
					reportingname = ButtonMaterialReqTO.getText().toString().trim();
					Quantity = edittextQty.getText().toString().trim();
					// ScrapRepair = editTextRepair.getText().toString().trim();
					reason = ButtonReason.getText().toString().trim();
					if (isnet()) {
						asyncfetch_csnstate = new DownloadxmlsDataURL_new();
						asyncfetch_csnstate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

					} else {
						showD("nonet");
					}
				}

				// ButtonStationNmae.setText("");
				// ButtonMaterialName.setText("");
				// ButtonMaterialReqTO.setText("");
				// edittextQty.setText("");
				// editTextRepair.setText("");
				// ButtonReason.setText("");/;.
			}
		});
	}

	public void getReporting_ManagerName(){

		//DatabaseHandler db = new DatabaseHandler(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		int count = 0;
		Cursor c = sql.rawQuery("SELECT * FROM Reporting WHERE Username='Sachin Khedekar'", null);

		if (c.getCount() > 0) {
			c.moveToFirst();
			do {
				reporteeBean bean = new reporteeBean();
				Reporting_managerName = c.getString(c
						.getColumnIndex("Username"));
				Reporting_managerID = c.getString(c
						.getColumnIndex("usermasterid"));

				/*bean.setReporteeName(Reporting_managerName);
				bean.setReporteeID(Reporting_managerID);*/

			} while (c.moveToNext());
		}
	}

	protected boolean isvalid() {
		// TODO Auto-generated method stub
		if (!(ButtonStationNmae.getText().toString().length() > 0)) {
			Toast.makeText(MaterialRequest.this, "Please Select station name",
					Toast.LENGTH_LONG).show();
			return false;
		} else if (!(ButtonMaterialName.getText().toString().length() > 0)) {
			Toast.makeText(MaterialRequest.this, "Please Select Material name",
					Toast.LENGTH_LONG).show();
			return false;
		} else if (!(ButtonMaterialReqTO.getText().toString().length() > 0)) {
			Toast.makeText(MaterialRequest.this,
					"Please Select Material request send to", Toast.LENGTH_LONG)
					.show();
			return false;
		} else if (!(ButtonReason.getText().toString().length() > 0)) {
			Toast.makeText(MaterialRequest.this, "Please Select reason ",
					Toast.LENGTH_LONG).show();
			return false;
		} else if (!(edittextQty.getText().toString().length() > 0)) {
			Toast.makeText(MaterialRequest.this, "Please Select Quantity",
					Toast.LENGTH_LONG).show();
			return false;
		} else if (!(scraprepair.length() > 0)) {
			Toast.makeText(MaterialRequest.this,
					"Please Select scrap or repair", Toast.LENGTH_LONG).show();
			return false;
		} 
		/*else if (ButtonStationNmae.getText().toString().length() > 0) {
			stationid = getStationId(ButtonStationNmae.getText().toString()
					.trim());
		}*/

		return true;
	}

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();

			String url;
			Reporting_managerName = "Sachin Khedekar";
			Reporting_managerID = "26";

			/*url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/bookOrder?mobileno="
					+ mobno
					+ "&stationame="
					+ stationname
					+ "&stationmasterid="
					+ stationid
					+"&itemmasterid="
					+ MaterialID
					+ "&materialname="
					+ materialname
					+ "&reason="
					+ reason
					+ "&scraprepair="
					+ scraprepair
					+ "&reporteename="
					+ reportingname
					+ "&reportingid="
					+ reportingid
					+ "&qty="
					+ Quantity
					+"&remarks=Remark";*/

				/*'Sachin Khedekar'*/

			 url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/bookOrder?mobileno=" + mobno
					+ "&stationame="+ stationname + "&stationmasterid="+ stationid	+"&itemmasterid="+ MaterialID
					+ "&materialname="+ materialname + "&reason="+ reason + "&scraprepair="+ scraprepair
					+ "&reporteename="+ reportingname + "&reportingid="+ reportingid + "&qty="+ Quantity
					+ "&remarks=Remark"+ "&warranty="+ Sel_RadBtnVal +"&reporting_managrname="+ Reporting_managerName+
					 "&reporting_managrid="+Reporting_managerID+"&sele_date="+ SelectedDate;

			Log.e("material ", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				System.out.println("-------  activity url --- " + url);
				responsemsg = ut.httpGet(url);

				System.out.println("-------------  xx vale-- " + responsemsg);

				responsemsg = responsemsg.toString().replaceAll("^\"|\"$", "");

				/*responsemsg = responsemsg.substring(responsemsg.indexOf(">") + 1);
				responsemsg = responsemsg.substring(responsemsg.indexOf(">") + 1);
				responsemsg = responsemsg.substring(0, responsemsg.indexOf("<"));*/

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
					((ProgressBar) findViewById(R.id.progressBar1))
							.setVisibility(View.GONE);
					// Toast.makeText(MaterialRequest.this,
					// "Server Error...Please try after some time",
					// Toast.LENGTH_LONG).show();
				} else {
					//updateNotification(true);
				//	showD("Done");
					 Toast.makeText(MaterialRequest.this, "Material request send successfully", Toast.LENGTH_LONG).show();
					((ProgressBar) findViewById(R.id.progressBar1))
							.setVisibility(View.GONE);
					// ButtonMaterialName.setText("");
					// ButtonMaterialReqTO.setText("");
					// edittextQty.setText("");
					// editTextRepair.setText("");
					// ButtonReason.setText("");
					MaterialRequest.this.finish();
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
			((ProgressBar) findViewById(R.id.progressBar1))
					.setVisibility(View.VISIBLE);
		}
	}

	private void updateNotification(Boolean status) {
		Context mContext = getApplicationContext();
		//
		// String ns = Context.NOTIFICATION_SERVICE;
		// NotificationManager mNotificationManager = (NotificationManager)
		// getSystemService(ns);
		//
		// if (status) {
		// int icon = R.drawable.sta_logo;
		CharSequence tickerText = "STA";
		long when = System.currentTimeMillis();
		//
		// Notification notification = new Notification(icon, tickerText, when);
		//
		Context context = getApplicationContext();
		CharSequence contentTitle = "Material Request Acknowledment";

		String txt = " Material request send successfully ";
		txt += " of ";
		txt += stationname + " for " + materialname;
		CharSequence contentText = txt;
		//
		Intent notificationIntent = new Intent(this, MaterialRequest.class);
		// PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
		// notificationIntent, 0);
		//
		// notification.setLatestEventInfo(context, contentTitle, contentText,
		// contentIntent);
		// mNotificationManager.notify(NOTIFICATION_ID, notification);
		// NOTIFICATION_ID++;
		// } else {
		// mNotificationManager.cancel(NOTIFICATION_ID);
		// }

		int icon = R.drawable.sta_logo;

		int mNotificationId = 001;

		PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
				0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				mContext);
		Notification notification = mBuilder
				.setSmallIcon(icon)
				.setTicker(contentTitle)
				.setWhen(0)
				.setAutoCancel(true)
				.setContentTitle(tickerText)
				.setStyle(
						new NotificationCompat.BigTextStyle()
								.bigText(contentText))
				.setContentIntent(resultPendingIntent)

				.setLargeIcon(
						BitmapFactory.decodeResource(mContext.getResources(),
								icon)).setContentText(contentText).build();
		notification.defaults |= Notification.DEFAULT_SOUND;
		// .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
		NotificationManager notificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(mNotificationId, notification);
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

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(MaterialRequest.this);
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
		} else if (string.equals("Error")) {
			myDialog.setTitle(" ");
			txt.setText("Server Error.. Please try after some time");
		} else if (string.equals("Done")) {
			myDialog.setTitle(" ");
			txt.setText("Material request send successfully");
		}

		Button btn = (Button) myDialog
				.findViewById(R.id.gotobtndialoginfosmall);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				myDialog.dismiss();
				// finish();

			}
		});

		myDialog.show();

	}

	private String getStationId(String stationname) {
		String id = null;
		try {
			//DatabaseHandler db1 = new DatabaseHandler(MaterialRequest.this);
			SQLiteDatabase sqldb = db.getWritableDatabase();

			Cursor cursor = sqldb.rawQuery(
							"SELECT DISTINCT InstallationId FROM AllStation where StatioName=? ",
							new String[] { stationname });

			cursor.moveToFirst();
			do {
				id = cursor.getString(cursor.getColumnIndex("InstallationId"));
			} while (cursor.moveToNext());

			cursor.close();
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

		}
		return id;
	}

	private String getReportingId(String reportingname) {
		String id = null;
		try {
			//DatabaseHandler db1 = new DatabaseHandler(MaterialRequest.this);
			SQLiteDatabase sqldb = db.getWritableDatabase();

			Cursor cursor = sqldb.rawQuery(
							"SELECT DISTINCT ReportingId FROM Reporting where ReportingName=? ",
							new String[] { reportingname });

			cursor.moveToFirst();
			do {
				id = cursor.getString(cursor.getColumnIndex("ReportingId"));
			} while (cursor.moveToNext());

			cursor.close();
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

		}
		return id;
	}

	public class CustomOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			// Toast.makeText(parent.getContext(),
			// "OnItemSelectedListener : " + pos, Toast.LENGTH_SHORT)
			// .show();
			scraprepair = parent.getItemAtPosition(pos).toString();
			if (pos == 0) {
				flag = "";
			} else if (pos == 1) {
				flag = "";
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(
					Config.EXTRA_MESSAGE);
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());

			/**
			 * Take appropriate action on this message depending upon your app
			 * requirement For now i am just displaying it on the screen
			 * */

			// Showing received message
			// lblMessage.append(newMessage + "\n");
			Toast.makeText(getApplicationContext(),
					"New Message: " + newMessage, Toast.LENGTH_LONG).show();

			// Releasing wake lock
			WakeLocker.release();
		}
	};

	@Override
	protected void onDestroy() {
		// Cancel AsyncTask

		try {
			// Unregister Broadcast Receiver
			unregisterReceiver(mHandleMessageReceiver);

			// Clear internal resources.

		} catch (Exception e) {
			Log.e("UnRegister Receiver", "> " + e.getMessage());
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
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		try{
			if (requestCode == Common.MaterialStn1) {
				String MaterialStation = data.getStringExtra("StatioName");
				ButtonStationNmae.setText(MaterialStation);
				stationid=data.getStringExtra("StatioNameID");
				//ButtonMaterialName.setText(data.getStringExtra("MaterialName"));
			}
			// MaterialReason
			else if (requestCode == Common.MaterialName) {
				MaterialID = data.getStringExtra("MaterialID");
				ButtonMaterialName.setText(data.getStringExtra("MaterialName"));
			}
			// MaterialReason
			else if (requestCode == Common.MaterialReason) {
				ButtonReason.setText(data.getStringExtra("MaterialReason"));
			} else if (requestCode == Common.MaterialRqesendto) {
				ButtonMaterialReqTO.setText(data.getStringExtra("MaterialRqesendto"));
				reportingid = data.getStringExtra("MaterialRqesendtoID");
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		/*Intent intent = new Intent(getApplicationContext(), SelectMaterialReqType.class);
		startActivity(intent);*/
		finish();

	}

}
