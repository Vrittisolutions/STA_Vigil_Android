package com.stavigilmonitoring;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.database.DBInterface;
import com.stavigilmonitoring.utility;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class MaterialDeliveryListDetails extends Activity {
	String responsemsg, orderHeaderId, requestdate, apprvCategory;
	Spinner spinnertypeofcurier;
	EditText editTextDocateNo;
	TextView txtapprcatogery;
	Button btnDatepicker, btn_return, btn_save;
	ImageButton btncamera, btnfolder;
	ImageView imgMaterial;
	int year, month, day;
	String datetostring, finalDate, detailid, mobno,reportingid;
	String[] sendDate;
	String docketno, mode, fileBase64Code, fileName;
	static Uri url;
	com.stavigilmonitoring.utility ut;
	static SimpleDateFormat dff;
	static String Ldate;
	public static int REQUEST_CODE_GET_FILE_PATH = 1;
	public static int REQUEST_CODE_GET_CAMERA_FILE_PATH = 1888;
	TextView txttitle;
	String materialname, reason, qty, stationname, scraprepair;
	public static String NAMESPACE = "http://tempuri.org/";
	public static String METHOD = "senddispatchedorder";
	public static String SOAP_ACTION = "http://tempuri.org/senddispatchedorder";
	String isrefresh = "false";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.materialreqdeliverydetails);
		spinnertypeofcurier = (Spinner) findViewById(com.stavigilmonitoring.R.id.spinnertypeofcurier);
		txtapprcatogery = (TextView)findViewById(R.id.txtapprcatogery);
		editTextDocateNo = (EditText) findViewById(com.stavigilmonitoring.R.id.editTextDocateNo);
		btn_return = (Button) findViewById(com.stavigilmonitoring.R.id.btn_return);
		btn_save = (Button) findViewById(com.stavigilmonitoring.R.id.btn_save);
		btncamera = (ImageButton) findViewById(com.stavigilmonitoring.R.id.btncamera);
		btnfolder = (ImageButton) findViewById(com.stavigilmonitoring.R.id.btnfolder);
		btnDatepicker = (Button) findViewById(com.stavigilmonitoring.R.id.btnDatepicker);
		imgMaterial = (ImageView) findViewById(com.stavigilmonitoring.R.id.imgMaterial);
		txttitle = (TextView) findViewById(com.stavigilmonitoring.R.id.txttitle);

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
          dbi.Close();
		
		SharedPreferences preflmsconn = getApplicationContext()
				.getSharedPreferences("MyReport", Context.MODE_PRIVATE);
		Editor editorlmsConne = preflmsconn.edit();
	    reportingid = preflmsconn.getString("reportingID", "");

		Intent intent = getIntent();
		orderHeaderId = intent.getStringExtra("orderHeaderId");
		scraprepair = intent.getStringExtra("scraprepair");
		stationname = intent.getStringExtra("stationname");
		qty = intent.getStringExtra("qty");
		reason = intent.getStringExtra("reason");
		materialname = intent.getStringExtra("materialname");
		requestdate = intent.getStringExtra("addedtdt");
		apprvCategory = intent.getStringExtra("ApproveCategory");

		txttitle.setText(materialname + " for " + stationname);
		txtapprcatogery.setText(apprvCategory);

		UUID uuid = UUID.randomUUID();
		detailid = uuid.toString();
		if (!isDeviceSupportCamera()) {
			Toast.makeText(getApplicationContext(),
					"Sorry! Your device doesn't support camera",
					Toast.LENGTH_LONG).show();
			// will close the app if the device does't have camera
			finish();
		}
		spinnertypeofcurier
				.setOnItemSelectedListener(new CustomOnItemSelectedListener());

		btncamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent,
						REQUEST_CODE_GET_CAMERA_FILE_PATH);
			}
		});
		btnfolder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");

				intent.setAction(Intent.ACTION_GET_CONTENT);

				intent = Intent.createChooser(intent, "Choose a file");

				MaterialDeliveryListDetails.this.startActivityForResult(intent,
						REQUEST_CODE_GET_FILE_PATH);
			}
		});
		btnDatepicker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Date date = new Date();
				final Calendar c = Calendar.getInstance();

				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DAY_OF_MONTH);

				// Launch Date Picker Dialog
				DatePickerDialog datePickerDialog = new DatePickerDialog(
						MaterialDeliveryListDetails.this,
						new DatePickerDialog.OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker datePicker,
									int year, int monthOfYear, int dayOfMonth) {

								btnDatepicker.setText(dayOfMonth + "-"
										+ (monthOfYear + 1) + "-" + year);
								datetostring = dayOfMonth + "-"
										+ (monthOfYear + 1) + "-" + year;
							}

						}, year, month, day);
				datePickerDialog.show();
			}
		});

		btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				docketno = editTextDocateNo.getText().toString();
				try {
					if (datetostring == null && datetostring.equals("")
							&& docketno == null && docketno.equals("")) {
						showD("empty");

					} else {

						sendDate = splitDT(datetostring);
						if (isnet()) {
							DownloadxmlsDataURL_new dataURL_new = new DownloadxmlsDataURL_new();
							dataURL_new.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						} else {
							showD("nonet");
						}
					}
				} catch (Exception e) {
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

					showD("empty");
				}


			}
		});

		btn_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(getApplicationContext(),	MaterialDeliveredListActivity.class);
				intent.putExtra("isrefresh", isrefresh);
				startActivity(intent);
				MaterialDeliveryListDetails.this.finish();
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
		protected String doInBackground(String... paramss) {

			try {
				String Url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx";
				SoapObject request = new SoapObject(NAMESPACE, METHOD);
				PropertyInfo propInfo = new PropertyInfo();
				propInfo.type = PropertyInfo.STRING_CLASS;
				request.addProperty("reportingid", reportingid);
				request.addProperty("mobileno", mobno);
				request.addProperty("couriermode", mode);
				request.addProperty("docketNo", docketno);
				request.addProperty("DateCourier", sendDate[0]);
				request.addProperty("imageName", fileName);
				request.addProperty("imagebase64", fileBase64Code);
				request.addProperty("materialOrderId", orderHeaderId);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.dotNet = true;
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(Url);
				androidHttpTransport.call(SOAP_ACTION, envelope);
				SoapObject response = (SoapObject) envelope.bodyIn;
				responsemsg = response.getProperty(0).toString();
				Log.d("Object response ", response.toString());
				SoapPrimitive res = (SoapPrimitive) envelope.getResponse();
				Log.d("response", res.toString());
				// responseSoap = res.toString();

			} catch (Exception e) {
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
			}
			return null;
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
					if (responsemsg.equals("NO")) {
						showD("NO");
					} else {
						Toast.makeText(
								MaterialDeliveryListDetails.this,	"Material delivered successfully "
										+ responsemsg, Toast.LENGTH_LONG)
								.show();
						btnDatepicker.setText("Select Date");
						editTextDocateNo.setText("");
						imgMaterial.setVisibility(View.GONE);

						// DatabaseHandler db1 = new DatabaseHandler(
						// getApplicationContext());
						// SQLiteDatabase db = db1.getWritableDatabase();
						//
						// ContentValues contentValues = new ContentValues();
						// contentValues.put("statusflag", "2");
						//
						// db.update("DeliveredRequests", contentValues,
						// "pkmaterialid=?",
						// new String[] { orderHeaderId });
						//
						// db.delete("DeliveredRequests", "pkmaterialid=?",
						// new String[] { orderHeaderId });
						isrefresh = "true";

						Intent intent = new Intent(getApplicationContext(),	MaterialDeliveredListActivity.class);
						intent.putExtra("isrefresh", isrefresh);
						startActivity(intent);
						MaterialDeliveryListDetails.this.finish();

					}
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

	private boolean isDeviceSupportCamera() {
		if (getApplicationContext().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(MaterialDeliveryListDetails.this);
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
		} else if (string.equals("NO")) {
			myDialog.setTitle(" ");
			txt.setText("Request not send...Please try after some time.");
		}// notdone
		else if (string.equals("notdone")) {
			myDialog.setTitle(" ");
			txt.setText("Request not send...Please try after some time.");
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

	public class CustomOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {

			mode = parent.getItemAtPosition(pos).toString();

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_GET_FILE_PATH
				&& resultCode == Activity.RESULT_OK) {
			if (data.getData() != null) {

				url = data.getData();

				InputStream stream;
				try {
					stream = MaterialDeliveryListDetails.this
							.getContentResolver().openInputStream(
									data.getData());
					Bitmap bitmap = BitmapFactory.decodeStream(stream);
					stream.close();
					final int maxSize = 500;
					int outWidth;
					int outHeight;
					int inWidth = bitmap.getWidth();
					int inHeight = bitmap.getHeight();
					if (inWidth > inHeight) {
						outWidth = maxSize;
						outHeight = (inHeight * maxSize) / inWidth;
					} else {
						outHeight = maxSize;
						outWidth = (inWidth * maxSize) / inHeight;
					}
					bitmap = Bitmap.createScaledBitmap(bitmap, outWidth,
							outHeight, true);
					ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
					// photo = Bitmap.createScaledBitmap(photo, 300, 300, true);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream1);
					byte[] byte_arr = stream1.toByteArray();
					fileBase64Code = Base64.encodeToString(byte_arr,
							Base64.NO_WRAP);

					LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(
							(outWidth / 2), (outHeight / 2));
					imgMaterial.setLayoutParams(parms);

					imgMaterial.setImageBitmap(bitmap);
					Log.d("test", "fileBase64Code " + fileBase64Code);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block

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
					// TODO Auto-generated catch block
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

				}

				fileName = detailid + ".jpeg";

			} else {
				Toast.makeText(MaterialDeliveryListDetails.this,
						"Nothing Selected.", Toast.LENGTH_LONG).show();
			}
		} else if (requestCode == REQUEST_CODE_GET_CAMERA_FILE_PATH
				&& resultCode == Activity.RESULT_OK) {

			try {

				fileName = detailid + ".jpeg";
				Bitmap photo = (Bitmap) data.getExtras().get("data");

				final int maxSize = 500;
				int outWidth;
				int outHeight;
				int inWidth = photo.getWidth();
				int inHeight = photo.getHeight();
				if (inWidth > inHeight) {
					outWidth = maxSize;
					outHeight = (inHeight * maxSize) / inWidth;
				} else {
					outHeight = maxSize;
					outWidth = (inWidth * maxSize) / inHeight;
				}
				photo = Bitmap.createScaledBitmap(photo, outWidth, outHeight,
						true);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();

				photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				byte[] byte_arr = stream.toByteArray();
				fileBase64Code = Base64
						.encodeToString(byte_arr, Base64.DEFAULT);
				Log.d("test", "fileBase64Code" + fileBase64Code);
				LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(
						(outWidth / 2), (outHeight / 2));
				imgMaterial.setLayoutParams(parms);
				imgMaterial.setImageBitmap(photo);
			} catch (NullPointerException e) {
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

		else if (resultCode == RESULT_CANCELED) {
			url = null;
			Toast.makeText(MaterialDeliveryListDetails.this, "Cancelled!",
					Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(getApplicationContext(),	MaterialDeliveredListActivity.class);
		intent.putExtra("isrefresh", isrefresh);
		startActivity(intent);
		MaterialDeliveryListDetails.this.finish();

	}
}
