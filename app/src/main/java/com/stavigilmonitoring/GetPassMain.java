package com.stavigilmonitoring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.database.DBInterface;
import com.stavigilmonitoring.GetpassList;
import com.stavigilmonitoring.R;
import com.stavigilmonitoring.GetpassList.GetStation;
import com.stavigilmonitoring.utility;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GetPassMain extends Activity {
	private Context parent;
	static EditText edname;
	Button btnpass;
	static TextView txtpass;
	ArrayList<String> searchResults;
	int requestCode = 111;
	static com.stavigilmonitoring.utility ut;
	String pass, id;
	GetStationPassword password;
	static SimpleDateFormat dff;

	static String Ldate;
	static ProgressDialog pd;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.getpassmain);

		parent = GetPassMain.this;

		edname = (EditText) findViewById(R.id.edsationname);
		btnpass = (Button) findViewById(R.id.btngetpwd);
		txtpass = (TextView) findViewById(R.id.getpwd);
		searchResults = new ArrayList<String>();

		ut = new com.stavigilmonitoring.utility();
		db = new DatabaseHandler(parent);

	}

	private boolean getdbvalue() {
		try {
			// TODO Auto-generated method stub
			//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor1 = sql.rawQuery("SELECT * FROM  Password", null);
			if (cursor1 != null && cursor1.getCount() > 0) {

				//cursor1.close();
				return true;

			} else {

				//sql.close();
				//cursor1.close();
				//db1.close();
				return false;
			}
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

			return false;
		}

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

	public void GetPassword(View v) {
		txtpass.setText("");
		String station = edname.getText().toString();
		if (isnet()) {
			if (!station.equalsIgnoreCase("")) {

				password = new GetStationPassword();
				password.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

			} else {
				Toast.makeText(this, "select station name", Toast.LENGTH_LONG)
						.show();
			}
		} else {
			showD("nonet");
		}
	}

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(parent);
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
			txt.setText("No Refresh Data Available. Please check internet connection....");
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

	public void StationList(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		Intent intent = new Intent(GetPassMain.this, GetpassList.class);
		startActivityForResult(intent, requestCode);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			pass = data.getExtras().getString("station");
			id = data.getExtras().getString("InstallationId");

			edname.setText(pass);
			edname.setTag(id);
		}
	}

	public class GetStationPassword extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			DBInterface dbi = new DBInterface(parent);
			String mobno = dbi.GetPhno();

			String result = "";
			String url = "http://sta.vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetStationPassword_Android?Mobile="+
					mobno+"&Station="+ edname.getTag().toString();

			url = url.replaceAll(" ", "%20");

			try {
				System.out.println("-------  activity url --- " + url);
				result = ut.httpGet(url);
			} catch (NullPointerException e) {
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

			} catch (Exception e) {
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

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			try {
				NodeList n1 = ut.getnode(result, "Table1");
				Element ele = (Element) n1.item(0);
				txtpass.setText(ut.getValue(ele, "Password"));
				pd.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			//pd = new ProgressDialog(parent);
			/* pd.setTitle("Please Wait.."); */
			//pd.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			//pd.setMessage("Loading...");
			//pd.setCancelable(false);
			//pd.show();

		}
	}

	public void onBackPressed() {
		super.onBackPressed();
		/*Intent i = new Intent(getBaseContext(), SelectMenu.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getBaseContext().startActivity(i);*/
		finish();
	}
}
