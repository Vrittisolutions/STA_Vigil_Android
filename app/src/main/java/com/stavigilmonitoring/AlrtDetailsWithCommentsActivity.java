package com.stavigilmonitoring;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adapters.AlertsCommItemListAdapter;
import com.beanclasses.AlertsCommItemBean;
import com.database.DBInterface;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import dmax.dialog.SpotsDialog;

import static com.stavigilmonitoring.utility.OpenConnection;
import static com.stavigilmonitoring.utility.OpenPostConnectionNow;

public class AlrtDetailsWithCommentsActivity extends Activity{
	String mType,AlertId,AlertDesc,StationName,CreatedBy,CreatedDt,InstallationId,isResolved,AlertByMobNo,SupporterName;
	com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
	String urlStringToken="", urlStringToken2="";
	String mobno,UserName;
	String cmntAlertId, cmntDesc, cmntBy;
	LinearLayout LLayoutAddComment,Color_Layout,Listlayout;
	LinearLayout ResolvebtnLayout, ResolvebtnLayout2;
	TextView tvhead, txtalertdesc, txtCreatedByvalue, txtCreatedDtvalue;
	ImageView btnaddItem,attachalertimg;
	public String  ResolveBy = null, ResolveDt= null, ConfirmBy = null, ConfirmDt =null, RejectedBy =null;
	Button button_save,button_return,button_resolve, button_accept, button_reject;
	EditText editTextCommentBy,	editTextCommentDesc ;
	ListView invtlist;
	private ImageView btnRefresh;
	private ProgressBar mprogressBar;
	private static AcceptNRejectURL asynk;
	private static CommunicationInsertURL asyncaddcmnt;
	String responsemsg = "m", sop;
	static SimpleDateFormat dff;
	static String Ldate;
	Context parent;
	private static CommunicationGetURL async;
	private static AlrtListURL asyncAlrt;
	ArrayList<AlertsCommItemBean> alertsCommItemBeanlist;
	AlertsCommItemBean alertsCommItemBean;	
	AlertsCommItemListAdapter alertsCommItemListAdapter;

	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static int IMG_RESULT = 200;
	private static final String IMAGE_DIRECTORY_NAME = "STA Vigil Images";// directory name to store captured images and videos
	private Uri fileUri;
	String encodedImage, image_encode="NA",Imagefilename, photoName,SerDate;
	String mCurrentPhotoPath;
	EditText editTextfileName;
	DatabaseHandler db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.stavigilmonitoring.R.layout.alertdetailwithcomment);

		parent = AlrtDetailsWithCommentsActivity.this;
		
		initViews();
		initonvisible();
		SetListeners();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		if (async != null && async.getStatus() == AsyncTask.Status.RUNNING) {
			Log.e("async", "running");
			//btnRefresh.setVisibility(View.GONE);
			//mprogressBar.setVisibility(View.VISIBLE);
		}
		if (asynk != null
				&& asynk.getStatus() == AsyncTask.Status.RUNNING) {
			Log.e("asynk", "running");
			//btnRefresh.setVisibility(View.GONE);
			//mprogressBar.setVisibility(View.VISIBLE);
		}
		if (asyncaddcmnt != null
				&& asyncaddcmnt.getStatus() == AsyncTask.Status.RUNNING) {
			Log.e("asyncaddcmnt", "running");
			//btnRefresh.setVisibility(View.GONE);
			//mprogressBar.setVisibility(View.VISIBLE);
		}
		if (dbvalue()) {
			updatelist();
		} else if (ut.isnet(AlrtDetailsWithCommentsActivity.this)) {
			fetchdataforlist();
			} 
		else {
			try{
				ut.showD(AlrtDetailsWithCommentsActivity.this, "nonet");
			}catch (Exception e){
				e.printStackTrace();
			}
			}
	}
	
	public String GetUserName() {
		String UserName = "";
		//DatabaseHandler db = new DatabaseHandler(AlrtDetailsWithCommentsActivity.this);
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor c = sql.rawQuery("SELECT UserName FROM UserNameTable", null);
		if (c.getCount() > 0) {
			c.moveToFirst();
			UserName = c.getString(0).trim();
		}
		return UserName;
	}
	
	private boolean dbvalue() {
		try {
			//DatabaseHandler db1 = new DatabaseHandler(AlrtDetailsWithCommentsActivity.this);
			SQLiteDatabase sql = db.getWritableDatabase();
			Cursor cursor = sql.rawQuery(
					"SELECT *   FROM CommunicationTable where AlertId ='"+AlertId+"'", null);
			if (cursor != null && cursor.getCount() > 0) {
					cursor.close();
					/*sql.close();
					db1.close();*/
					return true;
				//}
			} else {
				cursor.close();
				/*sql.close();
				db1.close();*/

			}
		} catch (Exception e) {
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
		return false;
	}

	private String gettodaydate() {
		String result= null;
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			//LocalDate localDate = LocalDate.now();
			Date date2 = new Date();
			result = dateFormat1.format(date2);
		}catch( Exception e){
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
		return result;
	}
	
	private void SetListeners() {
		// TODO Auto-generated method stub
		btnaddItem.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LLayoutAddComment.setVisibility(View.VISIBLE);
				initonvisible();

			}			
		});

		attachalertimg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				SerDate = gettodaydate();
				String Sendingdate = SerDate.replace("-","");
				Sendingdate = Sendingdate.replace(":","");
				Sendingdate = Sendingdate.replace(" ","_");
				photoName = "ALERT_"+AlertId+"_"+Common.UserName+"_"+Sendingdate+".jpg";
				ShowShareImageDialog();
			}
		});
		
		button_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				cmntAlertId = AlertId;
				cmntDesc = editTextCommentDesc.getText().toString();
				cmntBy = editTextCommentBy.getText().toString();

				if(cmntAlertId!=null&&(cmntDesc!=null||cmntDesc!="")&&cmntBy!=null){

					if(cmntBy.length()==10){
						//btnRefresh.setVisibility(View.GONE);
						//mprogressBar.setVisibility(View.VISIBLE);
						if (!image_encode.equals("NA")) {
							sendactivityupdatetoserver();
							cmntDesc = cmntDesc+"/ALERTIMG/"+photoName;
							//cmntBy = null;
						}
						LLayoutAddComment.setVisibility(View.GONE);
						fetchdataforadd();
					}else{
						editTextCommentBy.setError("enter valid mobile no");
					}
				}else{
					Toast.makeText(AlrtDetailsWithCommentsActivity.this, "Please enter Values", Toast.LENGTH_LONG).show();
				}				
			}			
		});
		
		btnRefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//btnRefresh.setVisibility(View.GONE);
				//mprogressBar.setVisibility(View.VISIBLE);

				asyncAlrt = new AlrtListURL();
				asyncAlrt.executeOnExecutor(asyncAlrt.THREAD_POOL_EXECUTOR);
				fetchdataforlist();
			}
		});
		
		button_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LLayoutAddComment.setVisibility(View.GONE);
				cmntAlertId=null;
				cmntDesc = null;
				cmntBy = null;				
			}			
		});
		
       /*button_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LLayoutAddComment.setVisibility(View.GONE);
				cmntAlertId=null;
				cmntDesc = null;
				cmntBy = null;				
			}			
		});*/
       
       button_resolve.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPrompt("Resolve");

			}			
		});
       
       button_accept.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPrompt("Confirm");
			}			
		});
       
       button_reject.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPrompt("Reject");
			}			
		});
	}

	private void ShowShareImageDialog() {
		final Dialog myDialog = new Dialog(AlrtDetailsWithCommentsActivity.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(com.stavigilmonitoring.R.layout.dialogimgshare);
		myDialog.setCancelable(true);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);
		myDialog.setTitle("Share Image");

		final ImageView btnPhotoAttachmentcam = (ImageView) myDialog.findViewById(com.stavigilmonitoring.R.id.btncam);

		final ImageView btnPhotoAttachmentgal = (ImageView) myDialog.findViewById(com.stavigilmonitoring.R.id.btngallery);
		btnPhotoAttachmentcam.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// capture picture
				captureImage();
				myDialog.dismiss();
			}
		});

		btnPhotoAttachmentgal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// capture picture
				//captureImage();]
				Intent intent = new Intent(Intent.ACTION_PICK,
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(intent, IMG_RESULT);
				myDialog.dismiss();
			}
		});


		/*final TextView quest = (TextView) myDialog.findViewById(R.id.dialoginfogototextsmall1);
		quest.setText(" Do you want to complete activity ?");*/

		myDialog.show();
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
		String imageFileName = photoName;
		File storageDir = new File(Environment.getExternalStorageDirectory(), IMAGE_DIRECTORY_NAME);
		if (!storageDir.exists()){  // Checks that Directory/Folder Doesn't Exists!
			boolean result = storageDir.mkdir();
			if(result){ Toast.makeText(parent, "New Folder created!",Toast.LENGTH_SHORT).show();}
		}
		File image = new File(storageDir+"/"+imageFileName);
		image.createNewFile();
		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		return image;
	}

	/**
	 * Receiving activity result method will be called after closing the camera
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {


		try {

			// if the result is capturing Image
			if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
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
				if (image_encode.equals("NA")) {
					attachalertimg.setImageResource(com.stavigilmonitoring.R.drawable.attachimg_gray);
				}else{
					attachalertimg.setImageResource(com.stavigilmonitoring.R.drawable.attachimg_green);
				}

				//File f = new File(URI.getPath().toString());
				//Imagefilename = f.getName();
				//editTextCommentDesc.setText("");

			}
		} catch (Exception e) {
			Toast.makeText(this, "Please try again", Toast.LENGTH_LONG)
					.show();
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
			if (image_encode.equals("NA")) {
				attachalertimg.setImageResource(com.stavigilmonitoring.R.drawable.attachimg_gray);
			}else{
				attachalertimg.setImageResource(com.stavigilmonitoring.R.drawable.attachimg_green);
			}
			File f = new File(imageUri.getPath().toString());
			Imagefilename = f.getName();
			//editTextCommentDesc.setText(Imagefilename);
			//editTextfileName.setText(photoName);
		} catch (NullPointerException e) {
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
	}

	public String getStringImage(Bitmap bmp) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] imageBytes = baos.toByteArray();
		encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
		return encodedImage;
	}

	protected void sendactivityupdatetoserver() {
		// TODO Auto-generated method stub
		String urlStringToken = "http://ktc.vritti.co/api/Values/SaveAlertandWorkdoneImage?";
		new ActivityUpdateAPI().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,urlStringToken);
	}


	protected void fetchdataforResolve() {
		// TODO Auto-generated method stub
				asynk = null;
				if (asynk == null) {
					try{
						//btnRefresh.setVisibility(View.VISIBLE);
						//mprogressBar.setVisibility(View.GONE);
					Log.e("asynk", "null");
					asynk = new AcceptNRejectURL();
					asynk.executeOnExecutor(asynk.THREAD_POOL_EXECUTOR);
					}catch(Exception e){
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
				} else {
					if (asynk.getStatus() == AsyncTask.Status.RUNNING) {
						Log.e("asynk", "running");
						//btnRefresh.setVisibility(View.GONE);
						//mprogressBar.setVisibility(View.VISIBLE);
					}
				}		
	}

	protected void fetchdataforadd() {
		// TODO Auto-generated method stub
		asyncaddcmnt = null;
		if (asyncaddcmnt == null) {
			try{

			Log.e("asyncaddcmnt", "null");
			asyncaddcmnt = new CommunicationInsertURL();
			asyncaddcmnt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}catch(Exception e){
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
		} else {
			if (asyncaddcmnt.getStatus() == AsyncTask.Status.RUNNING) {
				Log.e("asyncaddcmnt", "running");
				//btnRefresh.setVisibility(View.GONE);
				//mprogressBar.setVisibility(View.VISIBLE);
			}
		}
	}

	protected void initonvisible() {
		// TODO Auto-generated method stub
		editTextCommentBy = (EditText) findViewById(com.stavigilmonitoring.R.id.editTextCommentBy);
		editTextCommentBy.setText(mobno);
		attachalertimg = (ImageView) findViewById(com.stavigilmonitoring.R.id.attachalertimg);
		if (image_encode.equals("NA")) {
			attachalertimg.setImageResource(com.stavigilmonitoring.R.drawable.attachimg_gray);
		}else{
			attachalertimg.setImageResource(com.stavigilmonitoring.R.drawable.attachimg_green);
		}
		editTextCommentDesc = (EditText) findViewById(com.stavigilmonitoring.R.id.editTextCommentDesc);
		button_save = (Button) findViewById(com.stavigilmonitoring.R.id.button_save);
		button_return = (Button) findViewById(com.stavigilmonitoring.R.id.button_return);
		
		button_resolve = (Button) findViewById(com.stavigilmonitoring.R.id.txtResolve);
		button_accept = (Button) findViewById(com.stavigilmonitoring.R.id.txtAccept);
		button_reject = (Button) findViewById(com.stavigilmonitoring.R.id.txtReject);
		checkvisibility();		
	}

	public void checkvisibility() {
		// TODO Auto-generated method stub
		try{

		if( isResolved.equals(null)||isResolved.equalsIgnoreCase("")&& (AlertByMobNo.equalsIgnoreCase(mobno))){
			ResolvebtnLayout.setVisibility(View.GONE);
			ResolvebtnLayout2.setVisibility(View.GONE);
			button_resolve.setVisibility(View.GONE);
			button_accept.setVisibility(View.GONE);
			button_reject.setVisibility(View.GONE);
		}else if( isResolved.equals(null)||isResolved.equalsIgnoreCase("")&& !(AlertByMobNo.equalsIgnoreCase(mobno))){
			ResolvebtnLayout.setVisibility(View.VISIBLE);
			ResolvebtnLayout2.setVisibility(View.GONE);
			button_resolve.setVisibility(View.VISIBLE);
			button_accept.setVisibility(View.GONE);
			button_reject.setVisibility(View.GONE);
		}else if( !(isResolved.equals(null)||isResolved.equalsIgnoreCase(""))&& AlertByMobNo.equalsIgnoreCase(mobno)){
			//Color_Layout.setBackgroundColor(getResources().getColor(R.color.resolvedalert));
			ResolvebtnLayout.setVisibility(View.GONE);
			ResolvebtnLayout2.setVisibility(View.VISIBLE);
			button_resolve.setVisibility(View.GONE);
			button_accept.setVisibility(View.VISIBLE);
			button_reject.setVisibility(View.VISIBLE);
		}
		else if( !(isResolved.equals(null)||isResolved.equalsIgnoreCase(""))&& !(AlertByMobNo.equalsIgnoreCase(mobno))){
			ResolvebtnLayout.setVisibility(View.GONE);
			ResolvebtnLayout2.setVisibility(View.GONE);
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)Listlayout.getLayoutParams();
			params.setMargins(0, 10, 0, 0);
			Listlayout.setLayoutParams(params);
			//Listlayout.setLay();
			button_resolve.setVisibility(View.GONE);
			button_resolve.setText("Alert Resolved");
			button_resolve.setEnabled(false);
			button_resolve.setClickable(false);
			button_accept.setVisibility(View.GONE);
			button_reject.setVisibility(View.GONE);
		}
		}catch (NullPointerException e){
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//Intent intent = new Intent(parent,AlrtsStnListAll.class);
		//intent.putExtra("Type", mType);
		//intent.putExtra("intentfrom","1");
		//startActivity(intent);
		finish();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Bundle extras = intent.getExtras();
		//Common.listMessages = null;
		if (extras != null) {
			AlertId = extras.getString("AlertId");
			InstallationId = extras.getString("InstallationId");
			AlertDesc = extras.getString("AlertDesc");
			StationName = extras.getString("StationName");
			CreatedBy = extras.getString("CreatedBy");
			CreatedDt = extras.getString("CreatedDt");
			isResolved =  extras.getString("isResolved");
			AlertByMobNo  =  extras.getString("AlertByMobNo");
			SupporterName = extras.getString("SupporterName");
			fetchdataforlist();
		}
	}

	private void initViews() {
		//onNewIntent(getIntent());
		db = new DatabaseHandler(getApplicationContext());
		Bundle extras = getIntent().getExtras();
		//Common.listMessages = null;
		if (extras != null) {
			AlertId = extras.getString("AlertId");
			InstallationId = extras.getString("InstallationId");
			AlertDesc = extras.getString("AlertDesc");
			StationName = extras.getString("StationName");
			CreatedBy = extras.getString("CreatedBy");
			CreatedDt = extras.getString("CreatedDt");
			isResolved =  extras.getString("isResolved");
			AlertByMobNo  =  extras.getString("AlertByMobNo");
			SupporterName = extras.getString("SupporterName");
			fetchdataforlist();
		}else {
			Toast.makeText(getApplicationContext(),"Extras are null",Toast.LENGTH_SHORT).show();
		}

		//Bundle extras = getIntent().getExtras();

		mType = extras.getString("Type");
		AlertId = extras.getString("AlertId");
		AlertDesc = extras.getString("AlertDesc");
		StationName = extras.getString("StationName");
		CreatedBy = extras.getString("CreatedBy");
		CreatedDt = extras.getString("CreatedDt");
		InstallationId = extras.getString("InstallationId");
		isResolved =  extras.getString("isResolved");
		AlertByMobNo  =  extras.getString("AlertByMobNo");
		SupporterName = extras.getString("SupporterName");
		
		tvhead = (TextView) findViewById(com.stavigilmonitoring.R.id.stationAlert);
		tvhead.setText(StationName + " Alert");
		txtalertdesc = (TextView) findViewById(com.stavigilmonitoring.R.id.txtalertdesc);
		txtCreatedByvalue = (TextView) findViewById(com.stavigilmonitoring.R.id.txtrequestvalue);
		txtCreatedDtvalue = (TextView) findViewById(com.stavigilmonitoring.R.id.txtoverduevalue);
		Color_Layout = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.color_layout);
		LLayoutAddComment = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.LLayoutAddComment);
		Listlayout = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.listlayout);
		ResolvebtnLayout = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.resolvebtnlayout);
		ResolvebtnLayout2 = (LinearLayout) findViewById(com.stavigilmonitoring.R.id.resolvebtnlayout2);
		btnRefresh =(ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_invent);
		mprogressBar = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressinvent);

		setdata();		
		
		btnaddItem= (ImageView) findViewById(com.stavigilmonitoring.R.id.button_alert_add);
		
		invtlist = (ListView) findViewById(com.stavigilmonitoring.R.id.getpasslist);
		ut = new com.stavigilmonitoring.utility();

		//DatabaseHandler db = new DatabaseHandler(AlrtDetailsWithCommentsActivity.this);
		DBInterface dbi = new DBInterface(AlrtDetailsWithCommentsActivity.this);
		mobno = dbi.GetPhno();
		Common.UserName = GetUserName();
	}
	
	public void setdata() {
		// TODO Auto-generated method stub
		txtalertdesc.setText(AlertDesc);
		txtCreatedByvalue.setText(CreatedBy);
		txtCreatedDtvalue.setText(CreatedDt);
	}

	protected void showPrompt(String str) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(AlrtDetailsWithCommentsActivity.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoyesno);
		myDialog.setCancelable(true);
		// myDialog.getWindow().setGravity(Gravity.BOTTOM);
		myDialog.setTitle("Complete Activity");

		final TextView quest = (TextView) myDialog.findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall1);
		quest.setText(" Do you want to complete activity ?");

		if (str.equals("Resolve")){
			quest.setText(" Do you want to Resolve ?");
			ResolveBy = mobno;
			ConfirmBy = "0";
			RejectedBy = "0";
			//fetchdataforResolve();
		}else if (str.equals("Confirm")){
			quest.setText(" Do you want to Accept Resolution ?");
			ResolveBy = "0";
			ConfirmBy = mobno;
			RejectedBy = "0";
			//fetchdataforResolve();
		}else if (str.equals("Reject")){
			quest.setText(" Do you want to Reject Resolution ?");
			ResolveBy = "0";
			ConfirmBy = "0";
			RejectedBy = mobno;
			//fetchdataforResolve();
		}



		Button btnyes = (Button) myDialog
				.findViewById(com.stavigilmonitoring.R.id.yesbtndialog);
		btnyes.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				fetchdataforResolve();
				myDialog.dismiss();
				// finish();
			}
		});

		Button btnno = (Button) myDialog
				.findViewById(com.stavigilmonitoring.R.id.nobtndialog);
		btnno.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				myDialog.dismiss();
				// finish();

			}
		});

		myDialog.show();

	}
/*

	protected void setPrompt(String str) {
		LayoutInflater li = LayoutInflater.from(AlrtDetailsWithCommentsActivity.this);
		View promptsView = li.inflate(R.layout.dialoyesno, null);			

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AlrtDetailsWithCommentsActivity.this);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);
		
		final TextView editTextNarration = (TextView) promptsView.findViewById(R.id.dialoginfogototextsmall1);
		final LinearLayout btnll = (LinearLayout) promptsView.findViewById(R.id.btnll);
		btnll.setVisibility(View.GONE);
		if (str.equals("Resolve")){
		editTextNarration.setText(" Do you want to Resolve ?");	
		ResolveBy = mobno;
    	ConfirmBy = "0";
    	RejectedBy = "0";
    	//fetchdataforResolve();
		}else if (str.equals("Confirm")){
			editTextNarration.setText(" Do you want to Accept Resolution ?");
			ResolveBy = "0";
           	ConfirmBy = mobno;
           	RejectedBy = "0";
           	//fetchdataforResolve();
		}else if (str.equals("Reject")){
			editTextNarration.setText(" Do you want to Reject Resolution ?");
			ResolveBy = "0";
           	ConfirmBy = "0";
           	RejectedBy = mobno;
           	//fetchdataforResolve();
		}
		
		
		// set dialog message
					alertDialogBuilder
						.setCancelable(false)
						.setPositiveButton("Yes",
						  new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog,int id) {
							// get user input and set it to result
						    	
						    	fetchdataforResolve();
						    }
						  })
						.setNegativeButton("No",
						  new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog,int id) {
							dialog.cancel();
						    }
						  });

					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();

					// show it
					alertDialog.show();		
	}
*/

	
	protected void showD(String string) {
		// TODO Auto-generated method stub

		final Dialog myDialog = new Dialog(AlrtDetailsWithCommentsActivity.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoginfosmall);
		myDialog.setCancelable(true);
		 myDialog.getWindow().setGravity(Gravity.BOTTOM);

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
		} else if (string.equals("Done")) {
			myDialog.setTitle(" ");
			txt.setText("Comment send successfully");
		}else if (string.equals("Data Saved")) {
			myDialog.setTitle(" ");
			txt.setText("Data Saved");
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
				jsonObject.put("AttachedData", image_encode);
				jsonObject.put("Mobile", mobno);
				jsonObject.put("LoginId", Common.UserLogin);
				jsonObject.put("ProjectName", "STAVigil");
				jsonObject.put("ModuleName", "Alert");
				jsonObject.put("FileName", photoName/*.replace(" ","#")*/);

				String param ;// = jsonObject.toString();
				param = new Gson().toJson(jsonObject);
				Log.e("URLPARAM",params[0]+"\n"+param);

				res = OpenPostConnectionNow(params[0],param);
				responsemsg = res.toString();
				Log.e("URL res",responsemsg);

				/*String param ;
				param = new Gson().toJson(jsonObject);
				res = OpenPostConnectionNow(params[0],String.valueOf(jsonObject));
				//res = OpenPostConnection(params[0],jsonObject);
				responsemsg = res.toString();
				Log.e("URL res",responsemsg);*/
			} catch (NullPointerException e) {
				responsemsg = "error";
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
			} catch (Exception e) {
				responsemsg = "error";
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
					image_encode ="NA";
					if (image_encode.equals("NA")) {
						attachalertimg.setImageResource(com.stavigilmonitoring.R.drawable.attachimg_gray);
					}else{
						attachalertimg.setImageResource(com.stavigilmonitoring.R.drawable.attachimg_green);
					}
					showD("Data Saved");
					updatelist();

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
	
	public class CommunicationInsertURL extends AsyncTask<String, Void, String>{
		ProgressDialog progressDialog;
		SpotsDialog SPdialog2;

		@Override
		protected String doInBackground(String... params) {
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();

			String url;
			//currDate = system.get

			url ="http://vritti.co/iMedia/STA_Announcement/TimeTable.asmx/CommunicationInsert?"
					+"AlertId="
					+ cmntAlertId
					+ "&CommentDescription="
					+ cmntDesc
					+ "&AddedBy="
					+ cmntBy
					;
			
			
				Log.e("Comment ", "url : " + url);
				url = url.replaceAll(" ", "%20");
			try {
				System.out.println("-------  activity url --- " + url);
				responsemsg = ut.httpGet(url);

				//System.out.println("-------------  xx vale-- " + responsemsg);

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

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				if (responsemsg.equals("Error")) {
					showD("Error");
				} else if (responsemsg.equals("Not saved")) {
					showD("Error");
				} else {
					//showD("Done");
					urlStringToken = "http://punbus.vritti.co/api/Values/AddNotification?"// AdatSoftData.METHOD_SAVE_DATA
							+	"Message=Vrittitest : "+Common.UserName+" commented " +cmntDesc
							+ "&PkgName=com.stavigilmonitoring"
							+ "&FromMobile=" + mobno
							+ "&ToMobile=" + mobno;
					/*urlStringToken2 = "http://punbus.vritti.co/api/Values/SendNotification?"// AdatSoftData.METHOD_SAVE_DATA
							+	"sessionId=" + "vigil"// AdatSoftData.SESSION_ID
							+ "&handler=" + "0" ;//AdatSoftData.HANDLE*/

					//new NotificationCreateAPI().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,urlStringToken);
					LLayoutAddComment.setVisibility(View.GONE);
					cmntAlertId=null;
					cmntDesc = null;
					cmntBy = null;
					editTextCommentDesc.setText("");
					editTextCommentBy.setText("");


						fetchdataforlist();
				}
				//progressDialog.dismiss();
				SPdialog2.dismiss();
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
			//btnRefresh.setVisibility(View.GONE);
			//mprogressBar.setVisibility(View.VISIBLE);
			SPdialog2 = new SpotsDialog(AlrtDetailsWithCommentsActivity.this);//, R.style.Custom
			SPdialog2.show();

			/*progressDialog = new ProgressDialog(AlrtDetailsWithCommentsActivity.this);
			progressDialog.setMessage("Processing...");
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
			progressDialog.show();*/
		}		
	}

	public void fetchdataforlist() {
		// TODO Auto-generated method stub
		async = null;
		if (async == null) {
			try{
			//btnRefresh.setVisibility(View.VISIBLE);
			//mprogressBar.setVisibility(View.GONE);

			Log.e("async", "null");
			async = new CommunicationGetURL();
			async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}catch(Exception e){
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
		} else {
			if (async.getStatus() == AsyncTask.Status.RUNNING) {
				Log.e("async", "running");
				//btnRefresh.setVisibility(View.GONE);
				//mprogressBar.setVisibility(View.VISIBLE);
			}
		}
	}

		class NotificationCreateAPI extends AsyncTask<String, Void, String> {
			Object res;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			/*progressDialog = new ProgressDialog(ConfirmOTPActivity.this);
			progressDialog.setMessage("Processing...");
			progressDialog.show();*/
			}
			@Override
			protected String doInBackground(String... params) {

       /* responsemsg = "";
        inwid = "";
        inwtab = "";*/
				try {
					Log.e("CmentURL",params[0]);
					res = OpenConnection(params[0]);
					responsemsg = res.toString();
					Log.e("CmentURL resp",responsemsg);
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
				if (result.contains("error")) {
					Toast.makeText(AlrtDetailsWithCommentsActivity.this, "error!", Toast.LENGTH_LONG).show();
				} else  /*if (result.contains("Success")) {*/ /*if (result.contains(" "))*/ {
					try{
						/*Log.e("CmentURL",urlStringToken2);
						res = OpenConnection(urlStringToken2);
						responsemsg = res.toString();
						Log.e("CmentURL resp",responsemsg);*/
					} catch (NullPointerException e) {
						responsemsg = "error";
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
					} catch (Exception e) {
						responsemsg = "error";
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

					//Toast.makeText(parent, "Token Added Successfully..", Toast.LENGTH_LONG).show();
				}
			}
		}

		public class CommunicationGetURL extends AsyncTask<String, Void, String>{
		ProgressDialog progressDialog;
		SpotsDialog SPdialog;

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
			//DatabaseHandler db = new DatabaseHandler(AlrtDetailsWithCommentsActivity.this);
			SQLiteDatabase sql = db.getWritableDatabase();
			//String url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/CommunicationGet?AlertId="
			String url = "http://vritti.co/STAVigilURL/TimeTable.asmx/CommunicationGet?AlertId="
			+AlertId
			+"&InstallationId="
			+InstallationId;

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = ut.httpGet(url);
				//sql.execSQL("DROP TABLE IF EXISTS CommunicationTable");
			//sql.execSQL(ut.getCommunicationTable()); 	//remove comment if crash not ressolved

				sql.execSQL("DELETE FROM " + "CommunicationTable" + " WHERE AlertId= '"+AlertId+"'");
				//"DELETE FROM " + "CommunicationTable" + " WHERE AlertId= '"+AlertId+"'"
				
				Log.e("csn status", "resmsg : " + responsemsg);

				if (responsemsg.contains("<CommunicationId>")) {
					sop = "valid";
					String columnName, columnValue;
									
					Cursor cur = sql.rawQuery("SELECT * FROM CommunicationTable",
							null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "TableResult");
					Log.e("CommunicationTable data",
							" fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							columnName = cur.getColumnName(j);

							columnValue = ut.getValue(e, columnName);
							values1.put(columnName, columnValue);

							// Log.e("DownloadxmlsDataURL_new...on back...."," count i: "+i+"  j:"+j);
						}
						sql.insert("CommunicationTable",
								null, values1);
					}

					//cur.close();

				}
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
			return sop;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			SPdialog.dismiss();
			try {
				if (sop == "valid") {
					callAllMethods();

				} else {
					//ut.showD(parent,"nodata");
				}
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
			SPdialog = new SpotsDialog(AlrtDetailsWithCommentsActivity.this);//, R.style.Custom
			//SPdialog.setMessage("Processing...");
			//SPdialog.setCanceledOnTouchOutside(false);
			SPdialog.setCancelable(false);
			SPdialog.show();
			/*progressDialog = new ProgressDialog(AlrtDetailsWithCommentsActivity.this);
			progressDialog.setMessage("Loading...");
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
			progressDialog.show();*/
		}		
	}

	public void updatelist() {
		// TODO Auto-generated method stub
		//DatabaseHandler db = new DatabaseHandler(AlrtDetailsWithCommentsActivity.this);
		SQLiteDatabase sql = db.getWritableDatabase();
		alertsCommItemBeanlist = new ArrayList<AlertsCommItemBean>();
		alertsCommItemBeanlist.clear();
		
		Cursor c = sql.rawQuery("Select * from CommunicationTable where AlertId ='"+AlertId+"' order by CAST"
				//+ "(CmdAddedDt as datetime)"
				+ "(CommunicationId AS INT) "
				+ "desc", null);
		if (c.getCount()== 0){
			c.close();
			/*sql.close();
			db.close();*/
		}else{
			c.moveToFirst();
			int column = 0;
			do{
				String AlertId  = c.getString( c.getColumnIndex("AlertId") );
				String AlertDesc  = c.getString( c.getColumnIndex("AlertDesc") );
				String InstallationId  = c.getString( c.getColumnIndex("InstallationId") );
				String StationName  = c.getString( c.getColumnIndex("StationName") );
				String CommunicationId  = c.getString( c.getColumnIndex("CommunicationId") );
				Log.e("CommunicationId",CommunicationId);
				String CommentDescription  = c.getString( c.getColumnIndex("CommentDescription") );
				String AddedByOfAlert  = c.getString( c.getColumnIndex("AddedByOfAlert") );
				String AlertAddedDt  = c.getString( c.getColumnIndex("AlertAddedDt") );
				String ResolveBy  = c.getString( c.getColumnIndex("ResolveBy") );
				String AlertResolveDt  = c.getString( c.getColumnIndex("AlertResolveDt") );
				String cmdAddedBy  = c.getString( c.getColumnIndex("cmdAddedBy") );
				String cmdAddedDt  = c.getString( c.getColumnIndex("CmdAddedDt") );
				String Status = c.getString(c.getColumnIndex("status"));

				if(Status==null||Status.equals("")){
					Status="0";
				}
				Log.e("Status",Status);
				
				//String cmdAddedDt  = c.getString( c.getColumnIndex("AlertAddedDt") );
				
				alertsCommItemBean = new AlertsCommItemBean();
				alertsCommItemBean.setAlertId(AlertId);;
				alertsCommItemBean.setAlertDesc(AlertDesc);
				alertsCommItemBean.setInstallationId(InstallationId);
				alertsCommItemBean.setStationName(StationName);
				alertsCommItemBean.setCommunicationId(CommunicationId);
				alertsCommItemBean.setCommentDescription(CommentDescription);
				alertsCommItemBean.setAddedByOfAlert(AddedByOfAlert);
				alertsCommItemBean.setAlertAddedDt(AlertAddedDt);
				alertsCommItemBean.setResolveBy(ResolveBy);
				alertsCommItemBean.setAlertResolveDt(AlertResolveDt);
				alertsCommItemBean.setcmdAddedBy(cmdAddedBy);
				alertsCommItemBean.setcmdAddedDt(cmdAddedDt);
				alertsCommItemBean.setAlertByMobNo(AlertByMobNo);
				alertsCommItemBean.setUsermobno(mobno);
				alertsCommItemBean.setStatus(Status);
				
				alertsCommItemBeanlist.add(alertsCommItemBean);	
				//Log.e("Bean",alertsCommItemBeanList.toString());
			}while(c.moveToNext());
			c.close();
			/*sql.close();
			db.close();*/
		}
		
		alertsCommItemListAdapter = new AlertsCommItemListAdapter(AlrtDetailsWithCommentsActivity.this,
				alertsCommItemBeanlist);
		invtlist.setAdapter(alertsCommItemListAdapter);
		alertsCommItemListAdapter.notifyDataSetChanged();
		
	}
	
	public void updatelist3() {
		// TODO Auto-generated method stub
		//DatabaseHandler db = new DatabaseHandler(AlrtDetailsWithCommentsActivity.this);
		SQLiteDatabase sql = db.getWritableDatabase();
		/*alertsItemBeanlist = new ArrayList<AlertsItemBean>();
		alertsItemBeanlist.clear();*/
		
		Cursor c = sql.rawQuery("Select * from AlrtListTable where InstallationId = "+InstallationId+" and ConfirmBy='' order by CAST(AlertId AS INT) desc", null);
		if (c.getCount()== 0){
			c.close();
			/*sql.close();
			db.close();*/
		}else{
			c.moveToFirst();
			int column = 0;
			do{
				
				AlertId  = c.getString( c.getColumnIndex("AlertId") );
				AlertDesc  = c.getString( c.getColumnIndex("AlertDesc") );
				InstallationId  = c.getString( c.getColumnIndex("InstallationId") );
				StationName  = c.getString( c.getColumnIndex("StationName") );
				CreatedBy  = c.getString( c.getColumnIndex("AddedBy") );
				CreatedDt  = c.getString( c.getColumnIndex("AddedDt") );
				/*String ConfirmBy  = c.getString( c.getColumnIndex("ConfirmBy") );
				String ConfirmDT  = c.getString( c.getColumnIndex("ConfirmDt") );*/
				isResolved = c.getString( c.getColumnIndex("ResolveBy") );
				/*String ResolveBy  = c.getString( c.getColumnIndex("ResolveBy") );
				String ResolveDT  = c.getString( c.getColumnIndex("ResolveDt") );
				String ModifiedBy  = c.getString( c.getColumnIndex("ModifiedBy") );
				String ModifiedDT  = c.getString( c.getColumnIndex("ModifiedDt") );
				String RejectedBy  = c.getString( c.getColumnIndex("RejectedBy") );
				String RejectedDT  = c.getString( c.getColumnIndex("RejectedDt") );*/
				AlertByMobNo = c.getString(c.getColumnIndex("Mobile"));
						
			}while(c.moveToNext());
			c.close();
			/*sql.close();
			db.close();*/
		}
	}
	
	
	public class AcceptNRejectURL extends AsyncTask<String, Void, String>{
		ProgressDialog progressDialog;

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
			//DatabaseHandler db = new DatabaseHandler(AlrtDetailsWithCommentsActivity.this);
			SQLiteDatabase sql = db.getWritableDatabase();
			//String url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/AlertApproveAndRejected?AlertId="
					String url = "http://vritti.co/STAVigilURL/TimeTable.asmx/AlertApproveAndRejected?AlertId="
			+AlertId
			+"&ResolveBy="
			+ ResolveBy
			+"&ConfirmBy="
			+ ConfirmBy
			+"&InstallationId="
			+ InstallationId
			+"&RejectedBy="
			+ RejectedBy;

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
		progressDialog.dismiss();
		try {
			if (responsemsg.equals("Error")) {
				showD("Error");
			} else if (responsemsg.equals("Not saved")) {
				showD("Error");
			} else{					
				//showD("Data Saved");
				//alrtDetailsWithCommentsActivity.CommunicationGetURL();
				asyncAlrt = new AlrtListURL();
				asyncAlrt.executeOnExecutor(asyncAlrt.THREAD_POOL_EXECUTOR);
				fetchdataforlist();
				/*Intent intent = new Intent(parent,AlrtsStnListAll.class);
				intent.putExtra("Type", mType);
				intent.putExtra("intentfrom","1");
				startActivity(intent);
				finish();*/
				
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
			progressDialog = new ProgressDialog(AlrtDetailsWithCommentsActivity.this);
			progressDialog.setMessage("Processing...");
			//progressDialog.setCanceledOnTouchOutside(false);
			//progressDialog.setCancelable(false);
			progressDialog.show();
			/*btnrefresh.setVisibility(View.GONE);
			mprogressBar.setVisibility(View.VISIBLE);*/
		}		
	}

	public class AlrtListURL extends AsyncTask<String, Void, String>{
		ProgressDialog progressDialog;

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
			//DatabaseHandler db = new DatabaseHandler(parent);
			SQLiteDatabase sql = db.getWritableDatabase();
			String url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/AlertGet?InstallationId="
			+""
			+"&AddedBy="
			+ mobno;

			Log.e("csn status", "url : " + url);
			url = url.replaceAll(" ", "%20");
			try {
				responsemsg = ut.httpGet(url);
				//sql.execSQL("DROP TABLE IF EXISTS AlrtListTable");
				//sql.execSQL(ut.getAlrtListTable());
				sql.delete("AlrtListTable",null,null);

				Log.e("csn status", "resmsg : " + responsemsg);

				if (responsemsg.contains("<AlertId>")) {
					sop = "valid";
					String columnName, columnValue;
					Cursor cur = sql.rawQuery("SELECT * FROM AlrtListTable",null);
					ContentValues values1 = new ContentValues();
					NodeList nl1 = ut.getnode(responsemsg, "TableResult");
					Log.e("AlrtListTable data",
							" fetch data : " + nl1.getLength());
					for (int i = 0; i < nl1.getLength(); i++) {
						Element e = (Element) nl1.item(i);
						for (int j = 0; j < cur.getColumnCount(); j++) {
							columnName = cur.getColumnName(j);
							columnValue = ut.getValue(e, columnName);
							values1.put(columnName, columnValue);
						}
						sql.insert("AlrtListTable",	null, values1);
					}

					cur.close();

				}
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
			return sop;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				if (sop == "valid") {
					//updatelist2();
                    callAllMethods();
				} else {
					try{
						ut.showD(parent,"nodata");
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				/*ivRefresh.setVisibility(View.VISIBLE);
				mProgressBar
						.setVisibility(View.GONE);*/
				progressDialog.dismiss();

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
			/*ivRefresh.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);*/
			progressDialog = new ProgressDialog(AlrtDetailsWithCommentsActivity.this);
			progressDialog.setMessage("Processing...");
			//progressDialog.setCanceledOnTouchOutside(false);
			//progressDialog.setCancelable(false);
			progressDialog.show();
		}		
	}

	public void toggle_icon_received_status(final String Url) {
		// TODO Auto-generated method stub
		final Dialog myDialog = new Dialog(AlrtDetailsWithCommentsActivity.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(com.stavigilmonitoring.R.layout.img_layout);
		myDialog.setCancelable(true);
		myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialogInterface) {
				//nothing;
			}
		});

		final ImageView imageView  = (ImageView) myDialog.findViewById(com.stavigilmonitoring.R.id.imagedisp);
		Picasso.with(AlrtDetailsWithCommentsActivity.this).load(Url).placeholder(com.stavigilmonitoring.R.drawable.progressanimation)
				//.error(R.drawable.no_image)
				.into(imageView,new com.squareup.picasso.Callback(){
					@Override
					public void onSuccess() {					}
					@Override
					public void onError() {					}
				});
		myDialog.show();
	}

    private void callAllMethods() {
        updatelist3();
        setdata();
        checkvisibility();
        updatelist();
    }

}
