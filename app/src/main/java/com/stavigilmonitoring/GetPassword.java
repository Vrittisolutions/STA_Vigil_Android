package com.stavigilmonitoring;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.stavigilmonitoring.R;
import com.stavigilmonitoring.utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class GetPassword extends Activity {
	static SimpleDateFormat dff;
	static String Ldate;
	String[] StationName;
	String[] StationId;
	Button btngetPassword;
	Spinner spnr;
	TextView tvpwd;
	com.stavigilmonitoring.utility ut;
	ProgressDialog p;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.getpassword);

		ut = new com.stavigilmonitoring.utility();

		new GetAllStations().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


		btngetPassword = (Button) findViewById(R.id.btngetpwd);
		spnr = (Spinner) findViewById(R.id.spinnerpwdstnlist);
		tvpwd = (TextView) findViewById(R.id.getpwd);
		btngetPassword.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new GetStationPassword().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,StationId[spnr
						.getSelectedItemPosition()]);
			}
		});
	}

	public class GetAllStations extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			String result = "";
			String url = "http://vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetAllStation_Android";

			url = url.replaceAll(" ", "%20");
			try {
				System.out.println("-------  activity url --- " + url);
				result = ut.httpGet(url);
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

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				Log.e("get all pwd", " res : " + result);

				NodeList n1 = ut.getnode(result, "Table1");
				Log.e("get all pwd", " length : " + n1.getLength());
				StationName = new String[n1.getLength()];
				StationId = new String[n1.getLength()];
				Log.e("get all pwd", " StationName : " + StationName);
				for (int i = 0; i < n1.getLength(); i++) {
					Element ele = (Element) n1.item(i);
					Log.e("GetAllPassword",
							" " + ut.getValue(ele, "StatioName"));
					StationName[i] = ut.getValue(ele, "StatioName");
					StationId[i] = ut.getValue(ele, "InstallationId");
				}
				ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
						GetPassword.this, android.R.layout.simple_spinner_item,
						StationName);
				adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spnr.setAdapter(adapter1);
				p.cancel();
			} catch (Exception e) {
				Log.e("error", "  msg" + e.getMessage());
				p.cancel();
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
			// TODO Auto-generated method stub
			super.onPreExecute();
			p = new ProgressDialog(GetPassword.this);
			p.setMessage("Loading");
			p.show();
		}
	}

	public class GetStationPassword extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			Log.e("GetStationPassword", "" + params[0]);
			String result = "";
			String url = "http://vritti.co/iMedia/STA_Android_Webservice/WdbIntMgmtNew.asmx/GetStationPassword_Android?&Station="
					+ params[0];

			url = url.replaceAll(" ", "%20");
			try {
				System.out.println("-------  activity url --- " + url);
				result = ut.httpGet(url);
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

			} catch (Exception e) {
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

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			NodeList n1 = ut.getnode(result, "Table1");
			Element ele = (Element) n1.item(0);
			tvpwd.setText(ut.getValue(ele, "Password"));
			p.dismiss();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			p.show();
		}
	}
}
