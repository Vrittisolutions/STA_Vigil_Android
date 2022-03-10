package com.stavigilmonitoring;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.database.DBInterface;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.gson.Gson;
import com.services.GPSTracker;
import com.services.JobService_Test;
import com.services.JobService_WorkDone;
import com.services.WorkDoneService;
import com.stavigilmonitoring.BuildConfig;
import com.stavigilmonitoring.MaterialRequest.DownloadxmlsDataURL_new;
import com.stavigilmonitoring.R.id;
import com.stavigilmonitoring.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import static com.stavigilmonitoring.utility.OpenPostConnectionNow;

public class WorkDoneFillDetail extends Activity {
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static int IMG_RESULT = 200;
	private static final String IMAGE_DIRECTORY_NAME = "STA Vigil Images";
	private Uri fileUri; // file url to store image/video
	String encodedImage, image_encode="NA",Imagefilename, photoName;
	String mCurrentPhotoPath;
	EditText editTextfileName;

	private TextView mStation;
	GPSTracker gps;
	Context parent;
	// private Button mEmpName;
	private Button mSave;
	private Button mCancle;
	private ImageView mDown;// imageButton11
	private ImageView mUp;
	private ImageView mClrTimeSht;
	// private ImageView mdatePicmat;
	// private EditText msetDatemat;
	private EditText mSelectWork;
	String cityName = null;
	// private EditText mSetDate;
	private ProgressBar mProgress;
	// private ImageView mdatepic;
	private Button mCurrentDate;
	private ImageButton btnPhotoAttachmentcam, btnPhotoAttachmentgal;
	private EditText mTimesheet;//,editTextfileName;
	private LinearLayout mLinWork;
	private LinearLayout mLinMaterial;
	private EditText mMAterialtype, mWorktext, mMattxt;
	private com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	private String aAcNAme, aWorkDate, aWorkType, aWorkRemark, aMaterialType,
			aMaterialDate, aMateRemark, aInastallation, mobno, newDate;
	double latitude;
	double longitude;
	private String Station, Stninst, cvname, responsemsg, Ldate, finalDatework,
			finalDatemat, SerDate;
	SimpleDateFormat dff;
	private int mYear, mMonth, mDay, mHour, mMinute;
	private static UploadingData mUploadingData;
	String Location = "";
	String Activity, ActivityId;
	DatabaseHandler db;
	SQLiteDatabase sql;

	public static FirebaseJobDispatcher dispatcher ;
	public static Job myJob = null;
	boolean AppCommon = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.workdonestationdetail);

		mStation = (TextView) findViewById(id.workdoneStation);
		mDown = (ImageView) findViewById(id.radio_work);
		mUp = (ImageView) findViewById(id.radioMaterial);
		mClrTimeSht = (ImageView) findViewById(id.imageButton11);
		// msetDatemat = (EditText) findViewById(R.id.editTextmat);
		mLinWork = (LinearLayout) findViewById(id.linWork);
		mLinMaterial = (LinearLayout) findViewById(id.linMAterial);

		// mdatepic = (ImageView) findViewById(R.id.imageButton1);
		// mdatePicmat = (ImageView) findViewById(R.id.imageButton1mat);
		// mSetDate = (EditText) findViewById(R.id.editText);
		mWorktext = (EditText) findViewById(id.txtText1);
		mTimesheet = (EditText) findViewById(id.editTexts);
		mMattxt = (EditText) findViewById(id.txtText1mat);
		mSave = (Button) findViewById(id.worksave);// mtimesheet
		mCurrentDate = (Button) findViewById(id.datebtn);
		mMAterialtype = (EditText) findViewById(id.txtTextmat);
		mSelectWork = (EditText) findViewById(id.txtTextwork);
		mCancle = (Button) findViewById(id.workcancle);
		mProgress = (ProgressBar) findViewById(id.progressBarwrkStn);
		btnPhotoAttachmentcam = (ImageButton) findViewById(id.btncam);
		btnPhotoAttachmentgal = (ImageButton) findViewById(id.btngallery);
		editTextfileName = (EditText) findViewById(id.editTextpath);
		parent = WorkDoneFillDetail.this;
		Intent i = getIntent();
		Station = i.getStringExtra("stnname");
		Stninst = i.getStringExtra("stninst");

		db = new DatabaseHandler(getBaseContext());
		sql = db.getWritableDatabase();

		DBInterface dbi = new DBInterface(getApplicationContext());
		mobno = dbi.GetPhno();
		mStation.append(Station);

		SharedPreferences prefTV1 = getApplicationContext()
				.getSharedPreferences("cvNamepref", Context.MODE_PRIVATE);
		Editor editorTV1 = prefTV1.edit();
		cvname = prefTV1.getString("cvName", "");
		// mEmpName.setText(cvname);

		Date currentDate = new Date();
		newDate = splittime(currentDate);
		mCurrentDate.setText(newDate);
		SerDate = splittime(newDate);
		mTimesheet.setVisibility(View.GONE);
		mClrTimeSht.setVisibility(View.GONE);
		gps = new GPSTracker(WorkDoneFillDetail.this);

		if (gps.canGetLocation()) {
			latitude = gps.getLatitude();
			longitude = gps.getLongitude();

			// LocationAddress locationAddress = new LocationAddress();
			getAddressFromLocation(latitude, longitude, getApplicationContext());
		} else {

			gps.showSettingsAlert();
			// WorkDoneFillDetail.this.finish();

			// button1.setText("NOW PROCEED...");
		}

		mTimesheet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WorkDoneFillDetail.this,
						WorkDoneType.class);
				startActivityForResult(intent, 2);

			}
		});

		mClrTimeSht.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mTimesheet.setText("");

			}
		});

		mCancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WorkDoneFillDetail.this.finish();

			}
		});

		btnPhotoAttachmentcam.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// capture picture
				String Sendingdate = SerDate.replace("-","");
				Sendingdate = Sendingdate.replace(":","");
				Sendingdate = Sendingdate.replace(" ","_");
				photoName = "WD_"+Station+"_"+Sendingdate+".jpg";
				captureImage();
			}
		});

		btnPhotoAttachmentgal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// capture picture
				//captureImage();]
				String Sendingdate = SerDate.replace("-","");
				Sendingdate = Sendingdate.replace(":","");
				Sendingdate = Sendingdate.replace(" ","_");
				photoName = "WD_"+Station+"_"+Sendingdate+".jpg";
				Intent intent = new Intent(Intent.ACTION_PICK,
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(intent, IMG_RESULT);
			}
		});
	
		mSelectWork.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(WorkDoneFillDetail.this,
						WorkDoneTypeselect.class);
				startActivityForResult(intent, 1);
			}
		});
		mMAterialtype.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WorkDoneFillDetail.this,
						WorkDonematerialType.class);
				startActivityForResult(intent, 3);
			}
		});
		mSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isvalid()) {

					if (mLinMaterial.getVisibility() == View.GONE
							&& mLinWork.getVisibility() == View.VISIBLE) {
						aWorkType = mSelectWork.getText().toString();
						if (aWorkType.equalsIgnoreCase("") || aWorkType == null) {
							aWorkType = "0";

						}
						aWorkRemark = mWorktext.getText().toString();
						if (aWorkRemark.equalsIgnoreCase("")
								|| aWorkRemark == null) {
							aWorkRemark = "0";

						}
						aMaterialType = "0";
						aMaterialDate = "0";
						aMateRemark = "0";
						if (!image_encode.equals("NA")) {
							sendactivityupdatetoserver();
						}

						//SaveData();

					} else if (mLinMaterial.getVisibility() == View.VISIBLE
							&& mLinWork.getVisibility() == View.GONE) {
						aWorkType = "0";
						aWorkDate = "0";
						aWorkRemark = "0";
						aMaterialType = mMAterialtype.getText().toString(); // aMaterialDate
						if (aMaterialType.equalsIgnoreCase("")
								|| aMaterialType == null) {
							aMaterialType = "0";

						}
						aMateRemark = mMattxt.getText().toString();
						if (aMateRemark.equalsIgnoreCase("")
								|| aMateRemark == null) {
							aMateRemark = "0";

						}
						SaveData();
					} else if (mLinMaterial.getVisibility() == View.VISIBLE
							&& mLinWork.getVisibility() == View.VISIBLE) {
						aWorkType = mSelectWork.getText().toString(); // aWorkDate
						if (aWorkType.equalsIgnoreCase("") || aWorkType == null) {
							aWorkType = "0";

						} // =

						aWorkRemark = mWorktext.getText().toString();
						if (aWorkRemark.equalsIgnoreCase("")
								|| aWorkRemark == null) {
							aWorkRemark = "0";

						}
						aMaterialType = mMAterialtype.getText().toString(); // aMaterialDate
						if (aMaterialType.equalsIgnoreCase("")
								|| aMaterialType == null) {
							aMaterialType = "0";

						}

						aMateRemark = mMattxt.getText().toString();
						if (aMateRemark.equalsIgnoreCase("")
								|| aMateRemark == null) {
							aMateRemark = "0";

						}
						if (!image_encode.equals("NA")) {
							sendactivityupdatetoserver();
						}
					//	SaveData();
					} else if (mLinMaterial.getVisibility() == View.GONE
							&& mLinWork.getVisibility() == View.GONE) {

					}

				} else {
					/*
					 * Toast.makeText(WorkDoneFillDetail.this, "false",
					 * Toast.LENGTH_LONG).show();
					 */
				}
			}
		});
	}

	protected void sendactivityupdatetoserver() {
		// TODO Auto-generated method stub
		String urlStringToken = "http://ktc.vritti.co/api/Values/SaveAlertandWorkdoneImage?";
		new ActivityUpdateAPI().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,urlStringToken);
	}

	private void captureImage() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			// File file = new File(AppGlobal.URI_CAPTURED_IMAGE.getPath());
			try {
				fileUri = FileProvider.getUriForFile(parent,BuildConfig.APPLICATION_ID + ".provider", createImageFile());
			}catch (IOException ex) {               return;            }
		}  else        {
			try {
				fileUri = Uri.fromFile(createImageFile());
			}catch (IOException ex) {                return;            }
		}
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		// start the image capture Intent
		startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		//String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String Sendingdate = SerDate.replace("-","");
		Sendingdate = Sendingdate.replace(":","");
		Sendingdate = Sendingdate.replace(" ","_");
		photoName = "WD_"+Station+"_"+Sendingdate+".jpg";
		String imageFileName = photoName;
		File storageDir = new File(Environment.getExternalStorageDirectory(), IMAGE_DIRECTORY_NAME);
		if (!storageDir.exists()){  // Checks that Directory/Folder Doesn't Exists!
			boolean result = storageDir.mkdir();
			if(result){ Toast.makeText(parent, "New Folder created!",Toast.LENGTH_SHORT).show();}
		}
		File image = new File(storageDir+"/"+imageFileName/*+".jpg"*/);
		image. createNewFile();

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		return image;
	}

	/*
	 * Display image from a path to ImageView
	 */
	private void previewCapturedImage() {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();// bitmap factory
			options.inSampleSize = 2;// downsizing image as it throws OutOfMemory Exception for larger images
			Uri imageUri = Uri.parse(mCurrentPhotoPath);
			final Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath(),options);
			image_encode = getStringImage(bitmap);
			File f = new File(imageUri.getPath().toString());
			Imagefilename = f.getName();
			editTextfileName.setText(Imagefilename);
			//editTextfileName.setText(photoName);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public String getStringImage(Bitmap bmp) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] imageBytes = baos.toByteArray();
		encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
		return encodedImage;
	}

	private String getAddressFromLocation(final double latitude1,
			final double longitude1, final Context context) {

		// if (isnetStn()) {

		Thread thread = new Thread() {
			@Override
			public void run() {

				// String cityName = null;
				String Statename = null;
				String Addline = null;
				String B;
				String c;
				Locale d;
				String e;
				String f;

				try {

					Geocoder gcd = new Geocoder(context, Locale.getDefault());
					List<Address> addresses = gcd.getFromLocation(latitude1,
							longitude1, 1);
					if (addresses != null && addresses.size() > 0) {
						System.out.println(addresses.get(0).getLocality());
						Statename = addresses.get(0).getAdminArea();
						cityName = addresses.get(0).getLocality();
						// Statename=addresses.get(0).getCountryName();
						Addline = addresses.get(0).getAddressLine(0);

						c = addresses.get(0).getCountryCode();
						d = addresses.get(0).getLocale();
						e = addresses.get(0).getSubLocality();
						f = addresses.get(0).getPremises();
						Location = e + " " + cityName + " " + Statename + " "
								+ c;

					}

				} catch (IOException e1) {
					Log.e("LocationAddress", "Unable connect to Geocoder", e1);
					// showD("Geocoder");
					Location = "";

				}
			}
		};
		thread.start();
		return "";
	}

	private void SaveData() {
		if(!image_encode.equals("NA")){
			aWorkRemark = aWorkRemark+"/WDIMG/"+photoName;
		}

		AddToLocal(aWorkType, aWorkRemark, aMaterialType, Station, Stninst,
				mobno, SerDate, aMateRemark, ActivityId, Location, Activity,
				latitude, longitude, "No");

		if (ut.isnet(getApplicationContext())) {

			mUploadingData = new UploadingData(); mUploadingData.execute();

			//for test purpose commented
			/*if ("android.intent.action.BOOT_COMPLETED".equals(getApplicationContext())) {
				setJobShedulder("WorkDoneservice");
			}else {
				setJobShedulder("WorkDoneservice");
			}*/

		//	startService(new Intent(WorkDoneFillDetail.this,WorkDoneService.class));
			/*Intent activityIntent = new Intent(WorkDoneFillDetail.this, WorkDoneService.class);
			if (Build.VERSION.SDK_INT >= 26*//*Build.VERSION_CODES.O*//*) {
				this.startForegroundService(activityIntent);
			} else {
				this.startService(activityIntent);
			}*/

		} else {
			try{
				ut.showD(WorkDoneFillDetail.this, "nonet");
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	private void AddToLocal(String aWorkType2, String aWorkRemark2,
			String aMaterialType2, String station2, String Stninst,
			String mobno2, String serDate2, String aMateRemark2,
			String activityId2, String location2, String activity2,
			double latitude2, double longitude2, String flag) {
		// TODO Auto-generated method stub
		String d = Double.toString(longitude2);
		String S = Double.toString(latitude2);
		//DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
		//SQLiteDatabase sql = db.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("WorkType", aWorkType2);
		values.put("WorkRemark", aWorkRemark2);
		values.put("MatType", aMaterialType2);
		values.put("Station", station2);
		values.put("StationInstal", Stninst);
		values.put("Mob", mobno2);
		values.put("Currentdate", serDate2);
		values.put("MatRemark", aMateRemark2);
		values.put("ActivityID", activityId2);
		values.put("MYLocation", location2);
		values.put("ActName", activity2);
		values.put("Latitude", latitude2);
		values.put("Longitude", longitude2);
		values.put("isUpload", flag);

		// Inserting Row
		Long data = sql.insert("WorkDonetable", null, values);
		Log.d("test", " values " + values);
		sql.close();
		//db1.close();

		//Toast.makeText(WorkDoneFillDetail.this, "Data Saved Successfully....",	Toast.LENGTH_LONG).show();
		mSelectWork.setText(""); // aWorkDat
		mWorktext.setText("");
		;
		mMAterialtype.setText("");
		; // aMaterialDate
		mMattxt.setText("");
		mTimesheet.setText("");

	}

	private void toast() {
		Toast.makeText(WorkDoneFillDetail.this, "Please Fill required Data...",
				Toast.LENGTH_LONG).show();

	}

	private String splittime(String tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf for date...." + tf);
		// String fromtimetw = "";

		/*
		 * String k = tf.substring(0, tf.length() - 11);
		 * System.out.println("---value of k for date..." + k);
		 */

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");// 30-4-2016
		Date myDate = null;
		try {
			myDate = dateFormat.parse(tf);
			System.out
					.println("..........value of my date after conv" + myDate);

		} catch (ParseException e) {
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
		}
		SimpleDateFormat timeFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");// 2016-04-30
		String finalDate = timeFormat.format(myDate);

		System.out.println("----------final----date-----" + finalDate);

		String[] v2 = { finalDate };

		return finalDate;
	}

	private String splittime(Date tf) {
		// TODO Auto-generated method stub
		System.out.println("---value of tf for date...." + tf);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");// 30-4-2016
		Date myDate = null;
		String myDates = dateFormat.format(tf);
		System.out.println("..........value of my date after conv" + myDate);

		String[] v2 = { myDates };

		return myDates;
	}

	protected boolean isvalid() {
		// TODO Auto-generated method stub

		if (mLinMaterial.getVisibility() == View.GONE
				&& mLinWork.getVisibility() == View.VISIBLE) {
			if (!(mSelectWork.getText().toString().length() > 0)) {
				toast();
				return false;
			} else {
				return true;
			}

		} else if (mLinMaterial.getVisibility() == View.VISIBLE
				&& mLinWork.getVisibility() == View.GONE) {
			if (!(mMAterialtype.getText().toString().length() > 0)) {
				toast();
				return false;
			} else {
				return true;
			}
		} else if (mLinMaterial.getVisibility() == View.VISIBLE
				&& mLinWork.getVisibility() == View.VISIBLE) {
			int work = mSelectWork.getText().length();
			int workmat = mMAterialtype.getText().length();
			if ((mSelectWork.getText().length() == 0)
					|| mMAterialtype.getText().length() == 0) {
				if (mSelectWork.getText().length() == 0
						&& mMAterialtype.getText().length() == 0) {
					toast();
					return false;
				} else {
					if (!(mSelectWork.getText().length() == 0)) {

						return true;
					} else if (!(mMAterialtype.getText().length() == 0)) {
						return true;
					}

				}

			} else {
				if (!(mMAterialtype.getText().toString().length() > 0)) {
					toast();
					return false;
				} else if (!(mSelectWork.getText().toString().length() > 0)) {
					toast();
					return false;
				} else {
					return true;
				}

			}
		} else if (mLinMaterial.getVisibility() == View.GONE
				&& mLinWork.getVisibility() == View.GONE) {
			toast();
			return false;
		}

		return true;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		try {
		if (requestCode == 2) {
			Activity = data.getStringExtra("WorkTypeData");
			ActivityId = data.getStringExtra("worktypeid");
			// Station=data.getStringExtra("MESSAGE");
			mTimesheet.setText(Activity);
		} else if (requestCode == 3) {
			String message = data.getStringExtra("MaterialName");
			mMAterialtype.setText(message);
		} else if (requestCode == 1) {

			String message = data.getStringExtra("WorkTypeData");
			String workid = data.getStringExtra("worktypeid");
			mSelectWork.setText(message);

		}else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {  // if the result is capturing Image
				if (resultCode == RESULT_OK) {
					// successfully captured the image
					// display it in image view
					previewCapturedImage();

				} else if (resultCode == RESULT_CANCELED) {
					// user cancelled Image capture
					Toast.makeText(parent,
							"User cancelled image capture", Toast.LENGTH_SHORT)
							.show();
				} else {
					// failed to capture image
					Toast.makeText(parent,"Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
				}
			} else if (requestCode == IMG_RESULT && resultCode == RESULT_OK
					&& null != data) {
				Uri URI = data.getData();
				String[] FILE = { MediaStore.Images.Media.DATA };


				Cursor cursor = getContentResolver().query(URI,
						FILE, null, null, null);

				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(FILE[0]);
				String ImageDecode = cursor.getString(columnIndex);
				cursor.close();
				BitmapFactory.Options options = new BitmapFactory.Options();// bitmap factory
				options.inSampleSize = 2;

				//imageViewLoad.setImageBitmap(BitmapFactory.decodeFile(ImageDecode));
				final Bitmap bitmap = BitmapFactory.decodeFile(ImageDecode,options);
				image_encode = getStringImage(bitmap);

				//File f = new File(URI.getPath().toString());
				//Imagefilename = f.getName();
				editTextfileName.setText(photoName);

			}
		} catch (Exception e) {
			Toast.makeText(this, "Please try again", Toast.LENGTH_LONG)
					.show();
		}

	}

	public void onRadioButtonWork(View view) {
		// Is the button now checked?
		// boolean checked = ((ImageView) view).i;

		if (mLinWork.getVisibility() == View.VISIBLE) {
			mLinWork.setVisibility(View.GONE);
			// mSetDate.setText("");
			mSelectWork.setText("");
			mDown.setImageResource(com.stavigilmonitoring.R.drawable.workdown);
		} else if (mLinWork.getVisibility() == View.GONE) {
			mLinWork.setVisibility(View.VISIBLE);
			mDown.setImageResource(com.stavigilmonitoring.R.drawable.workup);
			// mDown.setBackgroundResource(R.drawable.workup);
		}

	}

	public void onRadioButtonMaterial(View view) {
		// Is the button now checked?
		/*
		 * boolean checked = ((RadioButton) view).isChecked();
		 * 
		 * if (checked) {
		 */
		if (mLinMaterial.getVisibility() == View.VISIBLE) {
			mLinMaterial.setVisibility(View.GONE);
			// msetDatemat.setText("");
			mMAterialtype.setText("");
			mUp.setImageResource(com.stavigilmonitoring.R.drawable.workdown);

		} else if (mLinMaterial.getVisibility() == View.GONE) {
			mLinMaterial.setVisibility(View.VISIBLE);
			mUp.setImageResource(com.stavigilmonitoring.R.drawable.workup);

		}
	}

	class ActivityUpdateAPI extends AsyncTask<String, Void, String> {
		Object res;
		String responsemsg = "m";
		ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(parent);
			progressDialog.setMessage("Processing...");
			progressDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {

			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("Mobile", mobno);
				jsonObject.put("LoginId", Common.UserLogin);
				jsonObject.put("ProjectName", "STAVigil");
				jsonObject.put("ModuleName", "WorkDone");
				jsonObject.put("FileName", photoName);
				jsonObject.put("AttachedData", image_encode);
				String param ;// = jsonObject.toString();
				param = new Gson().toJson(jsonObject);
				Log.e("URLPARAM",params[0]+"\n"+param);

				res = OpenPostConnectionNow(params[0],param);
				responsemsg = res.toString();
				responsemsg.replace("\"","");
				Log.e("URL res",responsemsg);
			} catch (NullPointerException e) {
				responsemsg = "error";
				e.printStackTrace();
			} catch (Exception e) {
				responsemsg = "error";
				e.printStackTrace();
			}
			return responsemsg;
		}
		@Override
		protected void onPostExecute(String result) {
			//String table = "";
			responsemsg = result;
			progressDialog.dismiss();
			try {
				if (responsemsg.contains("error")) {
					Log.e("Reassign", responsemsg);
					showD("Error");
				} else if (responsemsg.contains("Error")) {
					Log.e("Reassign", responsemsg);
					showD("Error");
				} else if (responsemsg.contains("Y")) {
					Log.e("Reassign", responsemsg);
					showD("Data Saved");

					SaveData();

				} else {
					Log.e("Reassign", responsemsg);
					showD("Error");
				}
			} catch (Exception e) {
				e.printStackTrace();

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
							+ " ");
				}
			}
		}
	}

	public class UploadingData extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			String url;

			url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/BookWorkType?WorkType="
					+ aWorkType
					+ "&Remarks="
					+ aWorkRemark
					+ "&MaterialName="
					+ aMaterialType
					+ "&StationName="
					+ Station
					+ "&InstallationId="
					+ Stninst
					+ "&Mobileno="
					+ mobno
					+ "&currentDate="
					+ SerDate
					+ "&remarksMaterial="
					+ aMateRemark
					+ "&ActivityId="
					+ ActivityId // ActivityId,Location,Activity,latitude,longitude
					+ "&currentLocation="
					+ Location
					+ "&ActivityName="
					+ Activity
					+ "&latitude="
					+ latitude
					+ "&longitude="
					+ longitude + "";

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

			} catch (Exception e) {
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
				String asd = result;
				if (asd.equalsIgnoreCase("Work Done Inserted")) {
					Toast.makeText(WorkDoneFillDetail.this,
							"Data Saved successfully...", Toast.LENGTH_LONG)
							.show();

					/*ContentValues contentValues = new ContentValues();
					contentValues.put("isUpload", "Yes");
					sql.update("WorkDonetable", contentValues, "WkId=?",
							new String[] { Integer.toString(wkid) });

					sql.delete("WorkDonetable", "WkId=?",
							new String[] { Integer.toString(wkid) });*/


					mProgress.setVisibility(View.GONE);
					WorkDoneFillDetail.this.finish();

				} else {
					try{
						ut.showD(WorkDoneFillDetail.this, "Error");
						mProgress.setVisibility(View.GONE);
					}catch (Exception e){
						e.printStackTrace();
					}
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
			mProgress.setVisibility(View.VISIBLE);
		}

	}

	protected void showD(final String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(parent);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoginfosmall);
		myDialog.setCancelable(true);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);

		final TextView txt = (TextView) myDialog
				.findViewById(id.dialoginfogototextsmall);
		if (string.equals("Data Saved")) {
			myDialog.setTitle(" ");
			txt.setText("Data Saved");
		} else if (string.equals("Error")) {
			myDialog.setTitle(" ");
			txt.setText("Server Error.. Please try after some time");
		}

		Button btn = (Button) myDialog
				.findViewById(id.gotobtndialoginfosmall);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				myDialog.dismiss();
				if (txt.getText().equals("Data Saved")) {
					mLinWork.setVisibility(View.GONE);
					editTextfileName.setText("");
					mSelectWork.setText("");
					mWorktext.setText("");
				}

			}
		});

		myDialog.show();

	}

	private void setJobShedulder( String key) {

		// checkBatteryOptimized();
		if(myJob == null) {
			dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
			callJobDispacher_workdone();
		}
		else{
			/*if(!AppCommon.getInstance(this).isServiceIsStart()){
				dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
				callJobDispacher();
			}else {
				dispatcher.cancelAll();
				dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
				myJob = null;
				callJobDispacher();
			}*/

			if(AppCommon){
				dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
				callJobDispacher_workdone();

			}else {
				AppCommon = true;
				dispatcher.cancelAll();
				dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
				myJob = null;
				callJobDispacher_workdone();

			}
		}
	}

	private void callJobDispacher_workdone() {
		myJob = dispatcher.newJobBuilder()
				// the JobService that will be called
				.setService(JobService_WorkDone.class)
				// uniquely identifies the job
				.setTag("test")
				// one-off job
				.setRecurring(true)
				// don't persist past a device reboot
				.setLifetime(Lifetime.FOREVER)

				// start between 0 and 60 seconds from now
				.setTrigger(Trigger.executionWindow(180, 240))
				// don't overwrite an existing job with the same tag
				.setReplaceCurrent(true)
				// retry with exponential backoff
				.setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
				// constraints that need to be satisfied for the job to run
				.setConstraints(

						// only run on an unmetered network
						Constraint.ON_ANY_NETWORK,
						// only run when the device is charging
						Constraint.DEVICE_IDLE
				)
				.build();

		dispatcher.mustSchedule(myJob);
		//AppCommon.getInstance(this).setServiceStarted(true);
	}
}
