package com.stavigilmonitoring;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.database.DBInterface;
import com.stavigilmonitoring.MaterialReqAckList.DownloadxmlsDataURL;
import com.stavigilmonitoring.MaterialReqSendTo;
import com.stavigilmonitoring.MaterialRequest.CustomOnItemSelectedListener;
import com.stavigilmonitoring.MaterialRequest.DownloadxmlsDataURL_new;
import com.stavigilmonitoring.utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import static com.stavigilmonitoring.WorkAssign_AssignActivity.Year;
import static com.stavigilmonitoring.WorkAssign_AssignActivity.day;
import static com.stavigilmonitoring.WorkAssign_AssignActivity.month;

public class MaterialReqAckDetails extends Activity {
	Button buttonDatepicker, button_approve, button_return;
	int year, month, day;
	String datetostring, finalDate, spinnervalue, mobno;
	String[] sendDate;
	String responsemsg, orderHeaderId, requestdate, sendreqto, warranty, selected_date;
	String materialname, reason, qty, stationname, scraprepair;
	Button btnStationName, btnGetmaterialReason, btnmaterialRequestdate,
			txtScrap, btnedtMaterialName, btnMaterialedtReqTO, btnupdate,
			btnreturn, ButtonSeldate;
	EditText txtmaterialQty;
	Spinner spinneredtScrap;
	int pos;
	com.stavigilmonitoring.utility ut;
	static SimpleDateFormat dff;
	static String Ldate;
	String stationid, reportingid, reportingname;
	ImageView button_delete;
	String isrefresh = "false";

	private RadioGroup radioGroup;
	private RadioButton radioButton, radio_undr_warr, radio_not_in_warr;
	String Sel_RadBtnVal;
	String trnselectDate, SelectedDate;
	DatabaseHandler db;
	String Reporting_managerName, Reporting_managerID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.activity_myoredrdetails);

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		Intent intent = getIntent();

		orderHeaderId = intent.getStringExtra("orderHeaderId");
		scraprepair = intent.getStringExtra("scraprepair");
		stationname = intent.getStringExtra("stationname");
		qty = intent.getStringExtra("qty");
		reason = intent.getStringExtra("reason");
		materialname = intent.getStringExtra("materialname");
		sendreqto = intent.getStringExtra("sendreqto");
		warranty = intent.getStringExtra("Warranty");
		selected_date = intent.getStringExtra("SelectedDt");

		btnStationName = (Button) findViewById(com.stavigilmonitoring.R.id.btnedtStationNmae);
		btnGetmaterialReason = (Button) findViewById(com.stavigilmonitoring.R.id.ButtonedtReason);
		btnupdate = (Button) findViewById(com.stavigilmonitoring.R.id.button_update);
		btnreturn = (Button) findViewById(com.stavigilmonitoring.R.id.button_returns);
		txtmaterialQty = (EditText) findViewById(com.stavigilmonitoring.R.id.editedtTextQty);
		spinneredtScrap = (Spinner) findViewById(com.stavigilmonitoring.R.id.spinneredtScrap);
		btnedtMaterialName = (Button) findViewById(com.stavigilmonitoring.R.id.btnedtMaterialName);
		btnMaterialedtReqTO = (Button) findViewById(com.stavigilmonitoring.R.id.btnMaterialedtReqTO);
		button_delete = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_delete);

		radioGroup = (RadioGroup)findViewById(com.stavigilmonitoring.R.id.radiogroup);
		radioGroup.setVisibility(View.VISIBLE);

		radio_undr_warr = (RadioButton)findViewById(R.id.radio_undr_warr);
		radio_not_in_warr = (RadioButton)findViewById(R.id.radio_not_in_warr);

		btnGetmaterialReason.setText(reason);
		btnStationName.setText(stationname);
		btnedtMaterialName.setText(materialname);

		db = new DatabaseHandler(MaterialReqAckDetails.this);

		if (scraprepair.equals("Scrap")) {
			pos = 0;
		} else if (scraprepair.equals("Repair")) {
			pos = 1;
		} else {
			pos = 0;
		}

		spinneredtScrap.setSelection(pos);
		txtmaterialQty.setText(qty);
		btnMaterialedtReqTO.setText(sendreqto);
		button_approve = (Button) findViewById(com.stavigilmonitoring.R.id.button_approve);
		button_return = (Button) findViewById(com.stavigilmonitoring.R.id.button_return);
		ButtonSeldate = (Button)findViewById(com.stavigilmonitoring.R.id.ButtonSeldate);
		ButtonSeldate.setVisibility(View.VISIBLE);
		ButtonSeldate.setText(selected_date);

		if(warranty.equalsIgnoreCase("Under Warranty")){
			//radiobutton1.setChecked(true);
			radio_undr_warr.setChecked(true);
		}else if(warranty.equalsIgnoreCase("Not In Warranty")){
			//radiobutton2.setChecked(true);
			radio_not_in_warr.setChecked(true);
		}

		SharedPreferences prefmaterial = getApplicationContext()
				.getSharedPreferences("Material", Context.MODE_PRIVATE);
		Editor editorMaterial = prefmaterial.edit();
		String MaterialStation = prefmaterial.getString("materialreq", "");

		btnStationName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MaterialReqAckDetails.this,
						MaterialReqStatewiseActivity.class);
				intent.putExtra("mobileno", mobno);
				startActivity(intent);
				finish();

			}
		});
		btnedtMaterialName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MaterialReqAckDetails.this,
						MaterialListActivity.class);
				startActivityForResult(intent, Common.MaterialName);

			}
		});
		btnGetmaterialReason.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MaterialReqAckDetails.this,
						MaterialReason.class);
				startActivityForResult(intent, Common.MaterialReason);
			}
		});

		btnMaterialedtReqTO.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MaterialReqAckDetails.this,
						MaterialReqSendTo.class);
				intent.putExtra("mobileno", mobno);
				startActivityForResult(intent, Common.MaterialRqesendto);
			}
		});

		spinneredtScrap.setOnItemSelectedListener(new CustomOnItemSelectedListener());

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
				DatePickerDialog datePickerDialog = new DatePickerDialog(MaterialReqAckDetails.this,
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
				warranty = Sel_RadBtnVal;

				//Toast.makeText(getApplicationContext(), "selected button - "+Sel_RadBtnVal, Toast.LENGTH_SHORT).show();
			}
		});

		btnupdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (isvalid()) {

					reportingid = getReportingId(btnMaterialedtReqTO.getText().toString().trim());
					stationname = btnStationName.getText().toString().trim();
					materialname = btnedtMaterialName.getText().toString().trim();

					reportingname = btnMaterialedtReqTO.getText().toString().trim();
					qty = txtmaterialQty.getText().toString().trim();
					// ScrapRepair = editTextRepair.getText().toString().trim();
					reason = btnGetmaterialReason.getText().toString().trim();
					if (isnet()) {

						DownloadxmlsDataURL_new asyncfetch_csnstate = new DownloadxmlsDataURL_new();
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
				// ButtonReason.setText("");
			}
		});

		button_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder1 = new AlertDialog.Builder(
						MaterialReqAckDetails.this);
				builder1.setMessage("Do you want to delete material request ?");
				builder1.setCancelable(true);

				builder1.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								if (isnet()) {

									DownloadxmlsDataURL asyncfetch = new DownloadxmlsDataURL();
									asyncfetch.execute();
								} else {
									showD("nonet");
								}
								// MaterialReqAckDetails.this.finish();
							}
						});

				builder1.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

				AlertDialog alert11 = builder1.create();
				alert11.show();
			}
		});

		btnreturn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						MaterialReqAckList.class);
				intent.putExtra("isrefresh", isrefresh);
				startActivity(intent);
				MaterialReqAckDetails.this.finish();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == Common.MaterialName) {
			btnedtMaterialName.setText(data.getStringExtra("MaterialName"));
		}
		// MaterialReason
		else if (requestCode == Common.MaterialReason) {
			btnGetmaterialReason.setText(data.getStringExtra("MaterialReason"));
		} else if (requestCode == Common.MaterialRqesendto) {
			btnMaterialedtReqTO.setText(data.getStringExtra("MaterialRqesendto"));
		}
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

	public class CustomOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			// Toast.makeText(parent.getContext(),
			// "OnItemSelectedListener : " + pos, Toast.LENGTH_SHORT)
			// .show();
			spinnervalue = parent.getItemAtPosition(pos).toString();
			if (pos == 0) {

			} else if (pos == 1) {

			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}
	}

	public class DownloadxmlsDataURL extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();

			String url;
			try {
				url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/deleteOrder?pkorderid="

						+ orderHeaderId;

				Log.e("material ", "url : " + url);
				url = url.replaceAll(" ", "%20");

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

					Toast.makeText(MaterialReqAckDetails.this,
							"Material request delete successfully",
							Toast.LENGTH_LONG).show();
					isrefresh = "true";
					// DatabaseHandler db1 = new DatabaseHandler(
					// getApplicationContext());
					// SQLiteDatabase db = db1.getWritableDatabase();

					// ContentValues contentValues = new ContentValues();
					// contentValues.put("statusflag", "1");
					// db.update("Myorders", contentValues,
					// "pkmaterialid=?", new String[] { orderHeaderId });

					// db.delete("Myorders", "pkmaterialid=?",
					// new String[] { orderHeaderId });

					Intent intent = new Intent(getApplicationContext(),
							MaterialReqAckList.class);
					intent.putExtra("isrefresh", isrefresh);
					startActivity(intent);
					MaterialReqAckDetails.this.finish();

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

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(MaterialReqAckDetails.this);
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

	protected boolean isvalid() {
		// TODO Auto-generated method stub
		if (!(btnStationName.getText().toString().length() > 0)) {
			Toast.makeText(MaterialReqAckDetails.this,
					"Please Select station name", Toast.LENGTH_LONG).show();
			return false;
		} else if (!(btnedtMaterialName.getText().toString().length() > 0)) {
			Toast.makeText(MaterialReqAckDetails.this,
					"Please Select Material name", Toast.LENGTH_LONG).show();
			return false;
		} else if (!(btnMaterialedtReqTO.getText().toString().length() > 0)) {
			Toast.makeText(MaterialReqAckDetails.this,
					"Please Select Material request send to", Toast.LENGTH_LONG)
					.show();
			return false;
		} else if (!(btnGetmaterialReason.getText().toString().length() > 0)) {
			Toast.makeText(MaterialReqAckDetails.this, "Please Select reason ",
					Toast.LENGTH_LONG).show();
			return false;
		} else if (!(txtmaterialQty.getText().toString().length() > 0)) {
			Toast.makeText(MaterialReqAckDetails.this,
					"Please Select Quantity", Toast.LENGTH_LONG).show();
			return false;
		} else if (!(scraprepair.length() > 0)) {
			Toast.makeText(MaterialReqAckDetails.this,
					"Please Select scrap or repair", Toast.LENGTH_LONG).show();
			return false;
		} else if (btnStationName.getText().toString().length() > 0) {
			stationid = getStationId(btnStationName.getText().toString().trim());
		}

		return true;
	}

	private String getStationId(String stationname) {
		String id = null;
		try {
			//DatabaseHandler db1 = new DatabaseHandler(MaterialReqAckDetails.this);
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
			//DatabaseHandler db1 = new DatabaseHandler(MaterialReqAckDetails.this);
			SQLiteDatabase sqldb = db.getWritableDatabase();

			Cursor cursor = sqldb.rawQuery(
							"SELECT DISTINCT UserMasterid FROM Reporting where Username=? ",
							new String[] { reportingname });

			cursor.moveToFirst();
			do {
				id = cursor.getString(cursor.getColumnIndex("UserMasterid"));
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

	public class DownloadxmlsDataURL_new extends
			AsyncTask<String, Void, String> {

		@SuppressLint("WrongThread")
		@Override
		protected String doInBackground(String... params) {
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();

			String url;
			Reporting_managerName = "Sachin Khedekar";
			Reporting_managerID = "26";
			SelectedDate = ButtonSeldate.getText().toString();

			/*url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/editOrder?pkorderid="
					+ orderHeaderId
					+ "&mobileno="
					+ mobno
					+ "&stationame="
					+ stationname
					+ "&stationmasterid="
					+ stationid
					+ "&materialname="
					+ materialname
					+ "&reason="
					+ reason
					+ "&scraprepair="
					+ scraprepair
					+ "&reporteename="+ btnMaterialedtReqTO.getText().toString()
					+ "&reportingid=" + reportingid + "&qty=" + qty;*/

			url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/editOrder?pkorderid="
					+ orderHeaderId+ "&mobileno="+ mobno+ "&stationame="+ stationname
					+ "&stationmasterid="+ stationid+ "&materialname="+ materialname
					+ "&reason="+ reason+ "&scraprepair="+ scraprepair
					+ "&reporteename="+ btnMaterialedtReqTO.getText().toString()
					+ "&reportingid=" + reportingid + "&qty=" + qty +"&warranty="+ warranty +"&reporting_managername="+ Reporting_managerName+
					"&reporting_managerid="+Reporting_managerID+"&sele_date="+ SelectedDate;

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
					Toast.makeText(MaterialReqAckDetails.this,
							"Server Error...Please try after some time",
							Toast.LENGTH_LONG).show();
				} else {
					// updateNotification(true);
					Toast.makeText(MaterialReqAckDetails.this,"Material request update successfully",
							Toast.LENGTH_LONG).show();
					((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
							.setVisibility(View.GONE);
					// DatabaseHandler db1 = new DatabaseHandler(
					// getApplicationContext());
					// SQLiteDatabase db = db1.getWritableDatabase();
					//
					// ContentValues contentValues = new ContentValues();
					//
					// // contentValues.put("pkorderid", orderHeaderId);
					// contentValues.put("senderMobNo", mobno);
					// contentValues.put("stationname", stationname);
					// contentValues.put("stationmasterid", stationid);
					// contentValues.put("materialname", materialname);
					// contentValues.put("reason", reason);
					// contentValues.put("scraprepair", scraprepair);
					// contentValues.put("reporteename", reportingname);
					// contentValues.put("reportingid", reportingid);
					// contentValues.put("qty", qty);
					// db.update("Myorders", contentValues, "pkmaterialid=?",
					// new String[] { orderHeaderId });
					isrefresh = "true";

					Intent intent = new Intent(getApplicationContext(),
							MaterialReqAckList.class);
					intent.putExtra("isrefresh", isrefresh);
					startActivity(intent);
					MaterialReqAckDetails.this.finish();

					// db.delete("Myorders", "pkmaterialid=?",
					// new String[] { orderHeaderId });
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

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		Intent intent = new Intent(getApplicationContext(),
				MaterialReqAckList.class);
		intent.putExtra("isrefresh", isrefresh);
		startActivity(intent);
		MaterialReqAckDetails.this.finish();

	}
}
