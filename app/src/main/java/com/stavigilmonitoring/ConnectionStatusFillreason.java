package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.database.DBInterface;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectionStatusFillreason extends Activity {

	ProgressDialog pd, progressdialogupdateserver, pd1;
	ListView workspacewisedetail;
	String responsesoap = "Added";
	String mobno, link;
	AsyncTask depattask, refreshasyncupdateserver, depattask1;
	// ExpandableListAdapter listAdapter;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop = "no";
	ArrayList<String> projectlist = new ArrayList<String>();
	String spparam = "Employee Reporting to Me";
	ImageView iv;
	String reasonCodeDesc;
	String responsemsg = "k";
	String ActivityName, ActivityId, actname;
	String daterestr;
	String z = "";
	Context parent;
	static SimpleDateFormat dff;
	static String Ldate;
	String reasonCode = "";
	int requestCode;
	HashMap<String, List<String>> listDataChild;
	List<String> listDataHeader;
	private String Stationname, frompage, type, subtype;
	private TextView stnnameconn;
	ArrayList<String> assignedlist = new ArrayList<String>();
	ArrayList<String> assignedlist1 = new ArrayList<String>();
	private Spinner rbfh;
	private Button btnsave;
	private Button btncancelreason;
	EditText ED;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialogchangecsnreason);
		parent = ConnectionStatusFillreason.this;

		stnnameconn = (TextView) findViewById(R.id.tvconnectionsatusstnnamefillreason);
		Bundle extras = getIntent().getExtras();
		Stationname = extras.getString("stnname");
		frompage = extras.getString("frompage");
		type = extras.getString("Type");
		subtype = extras.getString("SubType");
		requestCode = 111;
		// rbfh = (Spinner)findViewById(R.id.spinnerreasonfillcsn1);
		stnnameconn.setText("Fill Reason :" + Stationname);
		btnsave = (Button) findViewById(R.id.btncsneasonsave1);
		btncancelreason = (Button) findViewById(R.id.btncsnncancel1);
		ED = (EditText) findViewById(R.id.edsationname);

		db = new DatabaseHandler(parent);

		DBInterface dbi = new DBInterface(parent);
		mobno = dbi.GetPhno();
		dbi.Close();

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		/*
		 * if (dbvalue1()) { updatespinner1(); } else { fetchdata1(); }
		 */

		btnsave.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// Log.e("ReasonDesc", reasonCodeDesc);
				// reasonCode = rbfh.getSelectedItem().toString();
				if ((reasonCode.length() > 0)) {
					if (net()) {
						updateReason();

					} else {
						showD("nonet");
					}

				} else {
					showD("empty");
				}

			}
		});

		btncancelreason.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Bundle dataBundle = new Bundle();
				dataBundle.putString("stnname", Stationname);
				dataBundle.putString("frompage", frompage);
				dataBundle.putString("Type", type);
				dataBundle.putString("SubType", subtype);
				Intent i = new Intent(parent, com.stavigilmonitoring.ConnectionStatus.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtras(dataBundle);
				parent.startActivity(i);
				finish();

			}
		});

	}

	public void updateReason() {

		progressdialogupdateserver = ProgressDialog.show(
				ConnectionStatusFillreason.this, "Update Reason.......",
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
		private String reasonCodeDesc = "";
		private String installationId = "";

		@Override
		protected String doInBackground(String... paramss) {
			try {
				//DatabaseHandler db1 = new DatabaseHandler(parent);
				SQLiteDatabase sqldb = db.getWritableDatabase();
				String[] params = new String[1];
				params[0] = reasonCode;

				Cursor c2 = sqldb
						.rawQuery(
								"SELECT * FROM DownTimeRasonFill where ReasonDescription=? ",
								params);

				reasonCodeDesc = "";
				if (c2.getCount() == 0) {
					c2.close();
					/*db.close();
					db1.close();*/
				} else {
					c2.moveToFirst();
					reasonCodeDesc = c2.getString(c2
							.getColumnIndex("ReasonCode"));
					System.out
							.println("...................reason code value is.............."
									+ reasonCodeDesc);
					c2.moveToLast();
					c2.close();
					/*db.close();
					db1.close();*/

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

				//DatabaseHandler db2 = new DatabaseHandler(parent);
				SQLiteDatabase dbf = db.getWritableDatabase();
				String[] params = new String[1];
				params[0] = Stationname;
				Cursor cf = dbf
						.rawQuery(
								"SELECT * FROM ConnectionStatusUser1 where InstallationDesc=? ",
								params);
				installationId = "";
				if (cf.getCount() == 0) {
					cf.close();
					/*dbf.close();
					db2.close();*/
				} else {
					cf.moveToFirst();
					installationId = cf.getString(cf
							.getColumnIndex("InstallationId"));

					cf.moveToLast();
					cf.close();
					/*dbf.close();
					db2.close();*/

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
			String xx = "";

			String url = "http://vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/UpdateTemporaryReason_Android?InstallationId="
					+ installationId
					+ "&MobileNo="
					+ mobno
					+ "&ReasonCode="
					+ reasonCodeDesc + "&ReasonDesc=" + reasonCode;
			url = url.replaceAll(" ", "%20");

			try {
				responsemsg = ut.httpGet(url);
			} catch (NullPointerException e) {
				responsemsg = "wrong" + e.toString();
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
				responsemsg = "wrong" + e.toString();
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
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			// progressdialogupdateserver.cancel();

			if (responsesoap.equals("Added")) {

				new DownloadxmlsDataURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


			} else {
				Toast.makeText(parent, "Server Error..",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public void StationList(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		Intent intent = new Intent(ConnectionStatusFillreason.this,	ConnReasonList.class);
		startActivityForResult(intent, requestCode);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			reasonCode = data.getExtras().getString("ReasonDesc");
			Bundle dataBundle = new Bundle();
			dataBundle.putString("stnname", Stationname);
			dataBundle.putString("frompage", frompage);
			dataBundle.putString("SubType", subtype);
			dataBundle.putString("Type", type);

			ED.setText(reasonCode);
		}
	}

	protected void showmultimobdialog(final String telno) {

		final Dialog myDialog = new Dialog(ConnectionStatusFillreason.this);
		myDialog.setContentView(R.layout.dialogmultino);
		myDialog.setCancelable(true);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);
		myDialog.setTitle("Multiple Nos found ");

		final TextView txtfirstno = (TextView) myDialog
				.findViewById(R.id.txtfirstno);
		final TextView txtsecondno = (TextView) myDialog
				.findViewById(R.id.txtsecondno);
		final TextView txtthiredno = (TextView) myDialog
				.findViewById(R.id.txtthiredno);
		final TextView txtthireddis = (TextView) myDialog
				.findViewById(R.id.txtthireddis);

		Button btnyes = (Button) myDialog
				.findViewById(R.id.btnyesmultinodialog);
		Button btnno = (Button) myDialog.findViewById(R.id.btnnomultinodialog);

		String[] arr = telno.split("/");

		if (arr.length == 3) {
			txtfirstno.setText(arr[0].toString());
			txtsecondno.setText(arr[1].toString());
			txtthiredno.setText(arr[2].toString());
		} else {
			txtfirstno.setText(arr[0].toString());
			txtsecondno.setText(arr[1].toString());
			txtthiredno.setVisibility(View.INVISIBLE);
			txtthireddis.setVisibility(View.INVISIBLE);

		}

		txtfirstno.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String[] arr = telno.split("/");

				if (arr.length == 3) {

					if (txtsecondno.getCurrentTextColor() == Color.GREEN) {
						txtsecondno.setTextColor(Color.BLACK);
					}

					if (txtthiredno.getCurrentTextColor() == Color.GREEN) {
						txtthiredno.setTextColor(Color.BLACK);
					}

				} else {
					if (txtsecondno.getCurrentTextColor() == Color.GREEN) {
						txtsecondno.setTextColor(Color.BLACK);
					}
				}

				txtfirstno.setTextColor(Color.GREEN);

			}
		});

		txtsecondno.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String[] arr = telno.split("/");

				if (arr.length == 3) {

					if (txtfirstno.getCurrentTextColor() == Color.GREEN) {
						txtfirstno.setTextColor(Color.BLACK);
					}

					if (txtthiredno.getCurrentTextColor() == Color.GREEN) {
						txtthiredno.setTextColor(Color.BLACK);
					}

				} else {
					if (txtfirstno.getCurrentTextColor() == Color.GREEN) {
						txtfirstno.setTextColor(Color.BLACK);
					}
				}

				txtsecondno.setTextColor(Color.GREEN);

			}
		});

		txtthiredno.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (txtfirstno.getCurrentTextColor() == Color.GREEN) {
					txtfirstno.setTextColor(Color.BLACK);
				}

				if (txtsecondno.getCurrentTextColor() == Color.GREEN) {
					txtsecondno.setTextColor(Color.BLACK);
				}

				txtthiredno.setTextColor(Color.GREEN);

			}
		});

		btnyes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (txtfirstno.getCurrentTextColor() == Color.GREEN) {
					// Toast.makeText(parent,
					// txtfirstno.getText().toString(),
					// Toast.LENGTH_LONG).show();

					String telno = txtfirstno.getText().toString();
					String ctime = getctime();

					SharedPreferences pref = getSharedPreferences("prefCall",
							MODE_PRIVATE);
					SharedPreferences.Editor editor = pref.edit();

					editor.putString("callstarttime", ctime);

					editor.commit();
					myDialog.dismiss();

					Intent callIntent = new Intent(Intent.ACTION_DIAL);
					callIntent.setData(Uri.parse("tel:" + telno));
					startActivity(callIntent);
					finish();

				} else if (txtsecondno.getCurrentTextColor() == Color.GREEN) {
					// Toast.makeText(parent,
					// txtsecondno.getText().toString(),
					// Toast.LENGTH_LONG).show();

					String telno = txtsecondno.getText().toString();
					String ctime = getctime();

					SharedPreferences pref = getSharedPreferences("prefCall",
							MODE_PRIVATE);
					SharedPreferences.Editor editor = pref.edit();

					editor.putString("callstarttime", ctime);

					editor.commit();
					myDialog.dismiss();

					Intent callIntent = new Intent(Intent.ACTION_DIAL);
					callIntent.setData(Uri.parse("tel:" + telno));
					startActivity(callIntent);
					finish();

				} else if (txtthiredno.getCurrentTextColor() == Color.GREEN)

				{
					// Toast.makeText(parent,
					// txtthiredno.getText().toString(),
					// Toast.LENGTH_LONG).show();

					String telno = txtthiredno.getText().toString();
					String ctime = getctime();

					SharedPreferences pref = getSharedPreferences("prefCall",
							MODE_PRIVATE);
					SharedPreferences.Editor editor = pref.edit();

					editor.putString("callstarttime", ctime);

					editor.commit();
					myDialog.dismiss();

					Intent callIntent = new Intent(Intent.ACTION_DIAL);
					callIntent.setData(Uri.parse("tel:" + telno));
					startActivity(callIntent);
					finish();

				}

				myDialog.dismiss();
			}
		});

		btnno.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				myDialog.dismiss();
			}
		});

		myDialog.show();

	}

	protected String getctime() {
		// TODO Auto-generated method stub

		Calendar c = Calendar.getInstance();
		SimpleDateFormat df3 = new SimpleDateFormat("HH:mm aa", Locale.ENGLISH);
		String formattedDate3 = df3.format(c.getTime());

		return formattedDate3;
	}

	protected void showmultiteldialog(final String telno) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(ConnectionStatusFillreason.this);
		myDialog.setContentView(R.layout.dialogmultino);
		myDialog.setCancelable(true);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);
		myDialog.setTitle("Multiple Nos found ");

		final TextView txtfirstno = (TextView) myDialog
				.findViewById(R.id.txtfirstno);
		final TextView txtsecondno = (TextView) myDialog
				.findViewById(R.id.txtsecondno);
		final TextView txtthiredno = (TextView) myDialog
				.findViewById(R.id.txtthiredno);
		final TextView txtthireddis = (TextView) myDialog
				.findViewById(R.id.txtthireddis);

		Button btnyes = (Button) myDialog
				.findViewById(R.id.btnyesmultinodialog);
		Button btnno = (Button) myDialog.findViewById(R.id.btnnomultinodialog);

		String[] arr = telno.split("/");

		if (arr.length == 3) {
			txtfirstno.setText(arr[0].toString());
			txtsecondno.setText(arr[1].toString());
			txtthiredno.setText(arr[2].toString());
		} else {
			txtfirstno.setText(arr[0].toString());
			txtsecondno.setText(arr[1].toString());
			txtthiredno.setVisibility(View.INVISIBLE);
			txtthireddis.setVisibility(View.INVISIBLE);

		}

		txtfirstno.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String[] arr = telno.split("/");

				if (arr.length == 3) {

					if (txtsecondno.getCurrentTextColor() == Color.GREEN) {
						txtsecondno.setTextColor(Color.BLACK);
					}

					if (txtthiredno.getCurrentTextColor() == Color.GREEN) {
						txtthiredno.setTextColor(Color.BLACK);
					}

				} else {
					if (txtsecondno.getCurrentTextColor() == Color.GREEN) {
						txtsecondno.setTextColor(Color.BLACK);
					}
				}

				txtfirstno.setTextColor(Color.GREEN);

			}
		});

		txtsecondno.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String[] arr = telno.split("/");

				if (arr.length == 3) {

					if (txtfirstno.getCurrentTextColor() == Color.GREEN) {
						txtfirstno.setTextColor(Color.BLACK);
					}

					if (txtthiredno.getCurrentTextColor() == Color.GREEN) {
						txtthiredno.setTextColor(Color.BLACK);
					}

				} else {
					if (txtfirstno.getCurrentTextColor() == Color.GREEN) {
						txtfirstno.setTextColor(Color.BLACK);
					}
				}

				txtsecondno.setTextColor(Color.GREEN);

			}
		});

		txtthiredno.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (txtfirstno.getCurrentTextColor() == Color.GREEN) {
					txtfirstno.setTextColor(Color.BLACK);
				}

				if (txtsecondno.getCurrentTextColor() == Color.GREEN) {
					txtsecondno.setTextColor(Color.BLACK);
				}

				txtthiredno.setTextColor(Color.GREEN);

			}
		});

		btnyes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (txtfirstno.getCurrentTextColor() == Color.GREEN) {
					Toast.makeText(parent,
							txtfirstno.getText().toString(), Toast.LENGTH_LONG)
							.show();
				} else if (txtsecondno.getCurrentTextColor() == Color.GREEN) {
					Toast.makeText(parent,
							txtsecondno.getText().toString(), Toast.LENGTH_LONG)
							.show();
				} else if (txtthiredno.getCurrentTextColor() == Color.GREEN)

				{
					Toast.makeText(parent,
							txtthiredno.getText().toString(), Toast.LENGTH_LONG)
							.show();
				}

				myDialog.dismiss();
			}
		});

		btnno.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				myDialog.dismiss();
			}
		});

		myDialog.show();
	}

	/*
	 * private boolean dbvalue1() { DatabaseHandler db1 = new
	 * DatabaseHandler(parent); SQLiteDatabase sql =
	 * db1.getWritableDatabase(); Cursor cursor =
	 * sql.rawQuery("SELECT *   FROM DownTimeRasonFill", null); if (cursor !=
	 * null && cursor.getCount() > 0) { cursor.close(); sql.close();
	 * db1.close(); return true;
	 * 
	 * } else { cursor.close(); sql.close(); db1.close(); return false; }
	 * 
	 * }
	 */

	/*
	 * private void fetchdata1() {
	 * 
	 * pd1 = ProgressDialog.show(ConnectionStatusFillreason.this,
	 * "Fetching Data from Server..", "Please Wait....", true, true, new
	 * OnCancelListener() {
	 * 
	 * public void onCancel(DialogInterface dialog) { // TODO Auto-generated
	 * method stub if (depattask1 != null && depattask1.getStatus() !=
	 * AsyncTask.Status.FINISHED) { depattask1.cancel(true); } } });
	 * pd1.setCancelable(false); depattask1 = new
	 * DownloadxmlsDataURL1().execute(); } public class DownloadxmlsDataURL1
	 * extends AsyncTask<String, Void, String> {
	 * 
	 * @Override protected String doInBackground(String... params) { try {
	 * String xx = ""; String url=
	 * "http://vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetReasonDownTime_Android?Mobile="
	 * +mobno; url = url.replaceAll(" ", "%20"); try {
	 * System.out.println("-------  activity url --- " + url); responsemsg =
	 * ut.httpGet(url);
	 * 
	 * } catch (IOException e) { e.printStackTrace(); responsemsg = "wrong" +
	 * e.toString(); }
	 * 
	 * if (responsemsg.contains("<ReasonCode>")) { sop = "valid";
	 * DatabaseHandler db = new DatabaseHandler(parent);
	 * //System.out.println("------------- 1-- "); SQLiteDatabase sql =
	 * db.getWritableDatabase(); //System.out.println("------------- 2-- ");
	 * sql.execSQL("DROP TABLE IF EXISTS DownTimeRasonFill");
	 * //System.out.println("------------- 3-- ");
	 * sql.execSQL(ut.getDownTimeRasonFill());
	 * //System.out.println("------------- 4-- ");
	 * //System.out.println("------------- 5-- ");
	 * 
	 * Cursor c = sql.rawQuery("SELECT *   FROM DownTimeRasonFill", null);
	 * //System.out.println("------------- 6-- "); ContentValues values = new
	 * ContentValues(); //System.out.println("------------- 7-- "); NodeList nl
	 * = ut.getnode(responsemsg, "Table"); String msg = ""; String columnName,
	 * columnValue; for (int i = 0; i < nl.getLength(); i++) { Element e =
	 * (Element) nl.item(i); for (int j = 0; j < c.getColumnCount(); j++) {
	 * columnName = c.getColumnName(j); columnValue = ut.getValue(e,
	 * columnName); values.put(columnName, columnValue); }
	 * sql.insert("DownTimeRasonFill", null, values);
	 * 
	 * }
	 * 
	 * c.close(); sql.close(); db.close();
	 * 
	 * } else { sop = "invalid"; } }catch(Exception e) {
	 * 
	 * } return sop; }
	 * 
	 * @Override protected void onPostExecute(String result) {
	 * super.onPostExecute(result); pd1.cancel();
	 * 
	 * System.out.println("...............value of sop"+ sop); if
	 * (sop.equals("valid")) { updatespinner1(); } else {
	 * 
	 * showD("invalid"); } }
	 * 
	 * }
	 * 
	 * 
	 * private void updatespinner1() { assignedlist1.clear();
	 * assignedlist1.add("Select Reason");
	 * 
	 * DatabaseHandler db1 = new DatabaseHandler(this); SQLiteDatabase db =
	 * db1.getWritableDatabase();
	 * 
	 * Cursor c2 = db.query("DownTimeRasonFill", new String[] {
	 * "ReasonDescription" }, null, null, null, null, null); if (c2.getCount()
	 * <= 0) { assignedlist1.add("No Reason Added"); c2.close(); db.close();
	 * db1.close(); } else { c2.moveToFirst(); do {
	 * assignedlist1.add(c2.getString(0)); } while (c2.moveToNext());
	 * 
	 * c2.close(); db.close(); db1.close(); } Collections.sort(assignedlist1,
	 * String.CASE_INSENSITIVE_ORDER); String[] items1 =
	 * assignedlist1.toArray(new String[assignedlist1.size()]);
	 * 
	 * MySpinnerAdapter customDept = new
	 * MySpinnerAdapter(ConnectionStatusFillreason.this,
	 * R.layout.view_spinner_item,assignedlist1 ); /* ArrayAdapter<String>
	 * adapter1 = new ArrayAdapter<String>(this,
	 * android.R.layout.simple_spinner_item, items1);
	 * rbfh.setAdapter(customDept); }
	 * 
	 * private static class MySpinnerAdapter extends ArrayAdapter<String> { //
	 * Initialise custom font, for example: Typeface font =
	 * Typeface.createFromAsset(getContext().getAssets(),"font/BOOKOS.TTF");
	 * Context con;
	 * 
	 * private MySpinnerAdapter(Context context, int resource, List<String>
	 * items) { super(context, resource, items); con=context; }
	 * 
	 * // Affects default (closed) state of the spinner
	 * 
	 * @Override public View getView(int position, View convertView, ViewGroup
	 * parent) { TextView view = (TextView) super.getView(position, convertView,
	 * parent); view.setTypeface(font); view.setPadding(3, 20, 3, 20);
	 * //view.setBackgroundColor(con.getResources().getColor(R.color.txtc));
	 * 
	 * return view; }
	 * 
	 * // Affects opened state of the spinner
	 * 
	 * @Override public View getDropDownView(int position, View convertView,
	 * ViewGroup parent) { TextView view = (TextView)
	 * super.getDropDownView(position, convertView, parent);
	 * view.setTypeface(font); return view; } }
	 */
	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(ConnectionStatusFillreason.this);
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
			txt.setText("No Refresh data Available. Please check Internet connection...");
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

	protected boolean net() {
		// TODO Auto-generated method stub
		Context context = parent;
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		// finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		Bundle dataBundle = new Bundle();
		dataBundle.putString("stnname", Stationname);
		dataBundle.putString("frompage", frompage);
		dataBundle.putString("Type", type);
		dataBundle.putString("SubType", subtype);
		Intent i = new Intent(ConnectionStatusFillreason.this,
				com.stavigilmonitoring.ConnectionStatus.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtras(dataBundle);
		parent.startActivity(i);
		finish();

	}

	public class DownloadxmlsDataURL extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			//DatabaseHandler Sql = new DatabaseHandler(parent);
			SQLiteDatabase sqldb = db.getWritableDatabase();

			String url = "http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetCSNStatus_Android_new?Mobile="
					+ mobno;
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = ut.httpGet(url);

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

			if (responsemsg.contains("<A>")) {
				sop = "valid";
				//sqldb.execSQL("DROP TABLE IF EXISTS ConnectionStatusUser");
				//sqldb.execSQL(ut.getConnectionStatusUser());
				sqldb.delete("ConnectionStatusUser",null,null);

				Cursor c = sqldb.rawQuery("SELECT *   FROM ConnectionStatusUser",
						null);

				ContentValues values = new ContentValues();
				NodeList nl = ut.getnode(responsemsg, "Table1");
				String msg = "";
				String columnName, columnValue;
				for (int i = 0; i < nl.getLength(); i++) {
					Element e = (Element) nl.item(i);

					for (int j = 0; j < c.getColumnCount(); j++) {
						columnName = c.getColumnName(j);

						String ncolumnname = "";
						if (columnName.equalsIgnoreCase("InstallationId"))
							ncolumnname = "A";
						else if (columnName.equalsIgnoreCase("ServerTime"))
							ncolumnname = "B";
						else if (columnName.equalsIgnoreCase("StartTime"))
							ncolumnname = "C";
						else if (columnName.equalsIgnoreCase("EndTime"))
							ncolumnname = "D";
						else if (columnName.equalsIgnoreCase("Remarks"))
							ncolumnname = "E";
						else if (columnName
								.equalsIgnoreCase("InstallationDesc"))
							ncolumnname = "F";
						else if (columnName.equalsIgnoreCase("TVStatus"))
							ncolumnname = "G";
						else if (columnName
								.equalsIgnoreCase("Last7DaysPerFormance"))
							ncolumnname = "H";
						else if (columnName.equalsIgnoreCase("QuickHealStatus"))
							ncolumnname = "I";
						else if (columnName.equalsIgnoreCase("STAVersion"))
							ncolumnname = "J";
						else if (columnName
								.equalsIgnoreCase("AscOrderServerTime"))
							ncolumnname = "K";
						else if (columnName
								.equalsIgnoreCase("LatestDowntimeReason"))
							ncolumnname = "L";
						else if (columnName.equalsIgnoreCase("UserName"))
							ncolumnname = "M";
						else if (columnName.equalsIgnoreCase("Type"))
							ncolumnname = "N";
						else if (columnName.equalsIgnoreCase("SubHeadPH_No"))
							ncolumnname = "O";
						else if (columnName
								.equalsIgnoreCase("SupportAgencyName"))
							ncolumnname = "P";
						else if (columnName.equalsIgnoreCase("SubNetworkCode"))
							ncolumnname = "R";
						columnValue = ut.getValue(e, ncolumnname);
						values.put(columnName, columnValue);
					}
					sqldb.insert("ConnectionStatusUser", null, values);
				}

				c.close();
				//db.close();

			} else {
				sop = "invalid";
				System.out.println("--------- invalid for project list --- ");
			}
			return sop;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			progressdialogupdateserver.cancel();

			if (result.equalsIgnoreCase("valid")) {
				Toast.makeText(parent,
						"Reason Updated Successfully..!", Toast.LENGTH_LONG)
						.show();

				Bundle dataBundle = new Bundle();
				dataBundle.putString("stnname", Stationname);
				dataBundle.putString("frompage", frompage);
				dataBundle.putString("Type", type);
				dataBundle.putString("SubType", subtype);
				Intent i = new Intent(parent, ConnectionStatus.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtras(dataBundle);
				parent.startActivity(i);
				finish();
			} else {

				Toast.makeText(parent,
						"Reason not Updated...Please try later ",
						Toast.LENGTH_LONG).show();

			}
		}
	}

}
