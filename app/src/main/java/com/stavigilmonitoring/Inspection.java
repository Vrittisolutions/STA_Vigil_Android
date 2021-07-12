package com.stavigilmonitoring;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.database.DBInterface;
import com.sendimage.ImageSelectionClaim;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Inspection extends Activity {
	private final String NAMESPACE = "vWorkbench";
	private String URL = "/webservice/ClaimWebService.asmx?wsdl";
	private final String SOAP_ACTION = "vWorkbench/CreateClaim";
	private final String METHOD_NAME = "CreateClaim";
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	// private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
	public static final int MEDIA_TYPE_IMAGE = 1;
	// public static final int MEDIA_TYPE_VIDEO = 2;
	private static final int PICK_FROM_GALLERY = 101;
	// directory name to store captured images and videos
	private static final String IMAGE_DIRECTORY_NAME = "Claim Folder";
	private Button btnCapturePicture;
	String responsesoap = "no";
	private Button btnAttachment;
	final String uploadFilePath = Environment.getExternalStorageDirectory()
			.getAbsolutePath().toString()
			+ "/my.jpg";
	Uri URI = null;
	// final String uploadFilePath =
	// Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/alarms.zip";
	Uri fileUri = Uri.parse(uploadFilePath);
	final String uploadFileName = "my.jpg";
	InputStream is = null;
	byte[] array;
	ListView workspacewisedetail;
	String mobno, link;
	ProgressDialog progressdialog, progressdialog3, progressdialog2;
	AsyncTask refreshasync, refreshasync2, refreshasync3;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String sop = "no";
	ArrayList<String> projectlist = new ArrayList<String>();
	String spparam = "Employee Reporting to Me";
	ImageView iv;
	static SimpleDateFormat dff;
	static String Ldate;
	String responsemsg = "k";
	String ActivityName, ActivityId, actname;
	private TextView txtdate;
	private TextView txtdaterefresh;
	String daterestr;
	private ListView connectionstatus;
	private ListView nonrepeated;
	private String stnnAme;
	private TextView downtimestnnsme;
	private Button barChart;
	private Button btnSave;
	private Button btncancel;
	private Button start;
	private Button stop;
	private Button play;
	private MediaRecorder myAudioRecorder;
	private String outputFile = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.inspection);
		btnCapturePicture = (Button) findViewById(R.id.btnCapturePicture);
		btnAttachment = (Button) findViewById(R.id.buttonAttachment);
		btnSave = (Button) findViewById(R.id.saveclaim);
		btncancel = (Button) findViewById(R.id.cancelclaim);
		// iv = (ImageView) findViewById(R.id.button_refresh_workspace);
		// txtdate = (TextView) findViewById(R.id.txtdaterefreshworkspace);
		// txtdaterefresh = (TextView)
		// findViewById(R.id.txtdaterefreshlinkworkspace);

		// actname = extras.getString("fromactivity");

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		// link = dbi.GetUrl();
		// daterestr = dbi.GetDateRefresg();
		dbi.Close();
		// System.out.print("Link value"+link);

		start = (Button) findViewById(R.id.button1);
		stop = (Button) findViewById(R.id.button2);
		play = (Button) findViewById(R.id.button3);

		stop.setEnabled(false);
		play.setEnabled(false);
		outputFile = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Pictures/Claim Folder/myrecording.3gp";

		myAudioRecorder = new MediaRecorder();
		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		myAudioRecorder.setOutputFile(outputFile);
		btnAttachment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// capture picture
				openGallery();
			}
		});
		if (!isDeviceSupportCamera()) {
			Toast.makeText(getApplicationContext(),
					"Sorry! Your device doesn't support camera",
					Toast.LENGTH_LONG).show();
			// will close the app if the device does't have camera
			finish();
		}
		btnCapturePicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// capture picture
				captureImage();
			}
		});

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// changeprojects();

				if (isnet()) {

					progressdialog3 = ProgressDialog.show(Inspection.this,
							"Sending Detals..", "Please Wait....", true, true,
							new OnCancelListener() {

								public void onCancel(DialogInterface dialog) {
									// TODO Auto-generated method stub
									if (refreshasync3 != null
											&& refreshasync3.getStatus() != AsyncTask.Status.FINISHED) {
										refreshasync3.cancel(true);
									}
								}
							});

					refreshasync3 = new Downloadxmls3().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}

				else {

					showD("nonet");

				}

			}
		});

		btncancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent myIntent = new Intent();
				myIntent.setClass(getApplicationContext(), SelectMenu.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity(myIntent);

				finish();
			}
		});

	}

	private void captureImage() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		URI = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, URI);

		// start the image capture Intent
		startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
		// previewCapturedImage();

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

	/**
	 * Previewing recorded video
	 */

	/**
	 * ------------ Helper Methods ----------------------
	 * */

	/**
	 * Creating file uri to store image/video
	 */
	public Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/**
	 * returning image / video
	 */
	private static File getOutputMediaFile(int type) {

		// External sdcard location
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
						+ IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		}
		// } else if (type == MEDIA_TYPE_VIDEO) {
		// mediaFile = new File(mediaStorageDir.getPath() + File.separator
		// + "VID_" + timeStamp + ".mp4");
		// }
		else {
			return null;
		}

		return mediaFile;
	}

	public void openGallery() {
		// Intent intent = new Intent();
		// intent.setType("image/*");
		// intent.setAction(Intent.ACTION_GET_CONTENT);
		// intent.putExtra("return-data", true);
		// startActivityForResult(
		// Intent.createChooser(intent, "Complete action using"),
		// PICK_FROM_GALLERY);

		Intent i = new Intent(getBaseContext(), ImageSelectionClaim.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getBaseContext().startActivity(i);
		finish();

		// previewCapturedImage();
	}

	public class Downloadxmls3 extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {

				try {
					// String urln = s + URL;
					// SoapObject request = new SoapObject(NAMESPACE,
					// METHOD_NAME);
					// System.out.println("--- 1");
					//
					// PropertyInfo propInfo = new PropertyInfo();
					// System.out.println("--- 2");
					// propInfo.type = PropertyInfo.STRING_CLASS;
					// // adding parameters
					// System.out.println("--- 3");
					// request.addProperty("Mobileno", mobno);
					// System.out.println("--- 4");
					// request.addProperty("ClaimMDate", datec);
					// request.addProperty("Purpose", purposeTravel);
					// request.addProperty("approver", approverid);
					// request.addProperty("ProjectId", userloginid);
					// request.addProperty("Remark", remarks);
					//
					// request.addProperty("TotalAmt", TotalAmoumt);
					// request.addProperty("AdvanceTaken", "0");
					// request.addProperty("Balance", "0");
					// request.addProperty("PaidAmount", "0");
					// request.addProperty("ActId", activityid);
					//
					// request.addProperty("Mode", spinnerworkspacemoj);
					// request.addProperty("ClaimDate", format2);
					// request.addProperty("AdvanceTaken", "0");
					// request.addProperty("From", fromPlace);
					// request.addProperty("To", toPlace);
					// request.addProperty("Exp1", travelClaim);
					// request.addProperty("Exp2", foodClaim);
					// request.addProperty("Exp3", lodgingClaim);
					// request.addProperty("Exp4", localClaim);
					// request.addProperty("Exp5", phoneClaim);
					// request.addProperty("Exp6", "0");
					//
					// System.out.println("--- 5");
					// SoapSerializationEnvelope envelope = new
					// SoapSerializationEnvelope(
					// SoapEnvelope.VER11);
					// System.out.println("--- 6");
					// envelope.dotNet = true;
					// System.out.println("--- 7");
					// envelope.setOutputSoapObject(request);
					// System.out.println("--- 8");
					// HttpTransportSE androidHttpTransport = new
					// HttpTransportSE(
					// urln);
					// System.out.println("--- 9");
					// androidHttpTransport.call(SOAP_ACTION, envelope);
					// System.out.println("--- 10");
					// SoapObject response = (SoapObject) envelope.bodyIn;
					// Log.e("Object response", response.toString());
					//
					// responsesoap = response.getProperty(0).toString();

				} catch (Exception e) {
					System.out.println("--------- illegal ----" + e.toString());
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

				String s = uploadFilePath;
				System.out.println("============  file path " + s);
				uploadnew(s);
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

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub

			super.onPostExecute(result);

			progressdialog3.cancel();

			if (responsesoap.equals("Done")) {
				// showD("done");

				Toast.makeText(getApplicationContext(),
						"Claim Applied Successfully..!", Toast.LENGTH_LONG)
						.show();

				// LogFile lf = new LogFile();
				// lf.writeFile("Claim Applied Successfully. vWork Detail - Claim Purpose - "
				// + purposeTravel);

				Intent myIntent = new Intent();
				myIntent.setClass(getApplicationContext(), SelectMenu.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity(myIntent);

				finish();

			} else {
				Toast.makeText(getApplicationContext(), "Server Error..",
						Toast.LENGTH_LONG).show();

				// LogFile lf = new LogFile();
				// lf.writeFile("Claim Application Failed. vWork Detail - Claim Purpose - "
				// + purposeTravel);
			}

		}
	}

	public void uploadnew(String path) {
		// TODO Auto-generated method stub

		System.out.println("===============  uploadnew " + path);
		//
		// try{
		// is = new FileInputStream(path);
		// if (path != null) {
		// try {
		// array=streamToBytes(is);
		// } finally {
		// is.close();
		// }
		//
		//
		// //convertbacktoimage();
		//
		//
		//
		//
		// }
		//
		// System.out.println("......image 1......");
		// File externalStorage = Environment.getExternalStorageDirectory();
		// System.out.println("......image 2......");
		// String strUri = uploadFilePath;
		// File file2 = new File(strUri);
		// if(file2.exists()==true)
		// {
		// System.out.println("File Exists");
		// }else
		// {
		// System.out.println("File not Exists");
		// }
		// System.out.println("......image 3......");
		// Bitmap bitmapOrg = BitmapFactory.decodeFile(strUri);
		// System.out.println("......image 4......");
		//
		// ByteArrayOutputStream bao=new ByteArrayOutputStream();
		// System.out.println("......image 5......");
		// double width=bitmapOrg.getWidth();
		// System.out.println("......image 6......");
		// double height=bitmapOrg.getHeight();
		// System.out.println("......image 7......");
		// double ratio=400/width;
		// System.out.println("......image 8......");
		// int newheight=(int)(ratio*height);
		// System.out.println("......image 9......");
		//
		// bitmapOrg=Bitmap.createScaledBitmap(bitmapOrg, 400, newheight, true);
		// System.out.println("......image 10......");
		//
		// bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 95, bao);
		// System.out.println("......image 11......");
		//
		// byte[] ba=bao.toByteArray();
		// System.out.println("......image 12......");
		//
		// String byteArrayStr= new String(Base64.encode(ba));
		//
		// System.out.println("====================  string of  array " +
		// byteArrayStr);
		//
		// final String NAMESPACE = "vWorkbench";
		// String URL =
		// "http://vritti.co/VrittiPortal/webservice/vwbservice.asmx?wsdl";
		// final String SOAP_ACTION = "vWorkbench/UploadFile_new";
		// final String METHOD_NAME = "UploadFile_new";
		// try{
		// SoapObject so=new SoapObject(NAMESPACE, METHOD_NAME);
		// System.out.println("==========1 =============");
		// so.addProperty("fdata",byteArrayStr );
		// so.addProperty("fileName", "my.jpg");
		// System.out.println("==========2 =============");
		// SoapSerializationEnvelope sse=new
		// SoapSerializationEnvelope(SoapEnvelope.VER11);
		// System.out.println("==========3 ============= "+ sse.toString());
		// new MarshalBase64().register(sse);
		// System.out.println("==========4 =============");
		// sse.dotNet=true;
		// sse.setOutputSoapObject(so);
		// System.out.println("==========5 =============");
		// HttpTransportSE htse=new HttpTransportSE(URL);
		// htse.call(SOAP_ACTION, sse);
		// System.out.println("==========6 =============");
		// SoapPrimitive response=(SoapPrimitive) sse.getResponse();
		//
		// System.out.println("==========7 =============");
		// String str=response.toString();
		// System.out.println("====================  Response "+str);
		// Toast.makeText(getApplicationContext(), str,
		// Toast.LENGTH_LONG).show();
		// } catch (Exception e) {
		// e.printStackTrace();
		// System.out.println("==========8 error  =============");
		//
		// }
		// }
		// catch(Exception e)
		// {
		//
		// }
		//
		//
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

	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(Inspection.this);
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
			txt.setText("No Data Available for Workspacewise Management.");
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
		Context context = this.getApplicationContext();
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	private void filldaterefresh() {
		// TODO Auto-generated method stub

		System.out.println("-------  filldateref " + daterestr);

		if (daterestr.equals("1")) {
			txtdate.setVisibility(View.INVISIBLE);
			txtdaterefresh.setVisibility(View.INVISIBLE);
		} else {

			try {

				String olddate = getolddate();

				System.out.println("-------  olddate " + olddate);

				Calendar c = Calendar.getInstance();

				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String formattedDate = df.format(c.getTime());

				System.out.println("------ curdaterefresh " + formattedDate);
				String diff = getTimeDiff(olddate, formattedDate);
				System.out.println("----- ##### " + diff);

				if ((diff.contains("seconds ago"))
						|| (diff.contains("minutes ago"))) {
					txtdate.setVisibility(View.INVISIBLE);
					txtdaterefresh.setVisibility(View.INVISIBLE);

				} else {
					System.out.println("----- ##### 2 " + diff);

					if (diff.equals("yesterday")) {
						String refdate = "1 day old data";
						txtdate.setText(refdate);
					} else if (diff.contains("ago")) {

						String[] sar = diff.split(" ");
						String a = sar[0].toString();
						int i = Integer.parseInt(a);

						if (i > 8) {
							txtdate.setText(" 1 day old data");
						} else {
							String ref[] = diff.split("ago");

							String refdate = ref[0].toString();
							System.out.println("--- #### refdate " + refdate);

							txtdate.setText(refdate + "old data");
						}

					} else {
						txtdate.setText(diff + "old data");
					}
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

		}

	}

	private String getTimeDiff(String time, String curTime)
			throws ParseException {
		DateFormat formatter;
		Date curDate;
		Date oldDate;
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		curDate = (Date) formatter.parse(curTime);
		oldDate = (Date) formatter.parse(time);
		long oldMillis = oldDate.getTime();
		long curMillis = curDate.getTime();
		// Log.d("CaseListAdapter", "Date-Milli:Now:"+curDate.toString()+":"
		// +curMillis +" old:"+oldDate.toString()+":" +oldMillis);
		CharSequence text = DateUtils.getRelativeTimeSpanString(oldMillis,
				curMillis, 0);
		return text.toString();
	}

	public void start(View view) {
		try {
			myAudioRecorder.prepare();
			myAudioRecorder.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		start.setEnabled(false);
		stop.setEnabled(true);
		Toast.makeText(getApplicationContext(), "Recording started",
				Toast.LENGTH_LONG).show();

	}

	public void stop(View view) {
		myAudioRecorder.stop();
		myAudioRecorder.release();
		myAudioRecorder = null;
		stop.setEnabled(false);
		play.setEnabled(true);
		Toast.makeText(getApplicationContext(), "Audio recorded successfully",
				Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; thi s adds items to the action bar if it is
		// present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void play(View view) throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {

		MediaPlayer m = new MediaPlayer();
		m.setDataSource(outputFile);
		m.prepare();
		m.start();
		Toast.makeText(getApplicationContext(), "Playing audio",
				Toast.LENGTH_LONG).show();

	}

	private String getolddate() {
		// TODO Auto-generated method stub

		DBInterface dbi = new DBInterface(getBaseContext());
		String dateref = dbi.GetDateRefresg();
		dbi.Close();
		return dateref;
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

		// Bundle dataBundle = new Bundle();
		// dataBundle.putString("ActivityId", ActivityId);
		// dataBundle.putString("ActivityName", ActivityName);
		Intent i = new Intent(getBaseContext(), SelectMenu.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// i.putExtras(dataBundle);
		getBaseContext().startActivity(i);
		finish();

	}

}
