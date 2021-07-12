package com.stavigilmonitoring;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adapters.DMCStateDetailsAdapter;
import com.beanclasses.StateDetailsList;
import com.database.DBInterface;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.gson.Gson;
import com.services.JobService_DMCertificate;

import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.stavigilmonitoring.utility.OpenPostConnectionNow;

/**
 * Created by Admin-3 on 11/13/2017.
 */

public class DmCSoNoStateStnDetails extends Activity {
    private static ReasonUpdateURL asynktask;
    ListView lstcsn;
    TextView header;
    Context parent;
    com.stavigilmonitoring.utility ut;
    DMCStateDetailsAdapter listAdapter;
    ImageView iv,btnfilter;
    String sop = "no";
    String resposmsg ="n";
    private ArrayList<String> NameList;
    String AssignToName, AssignToMob, Remark, ClipURLlist, SONumber, Reason, csdate="No info", cedate="No info";
    String sonum, station, subnetwork, installationId, activityId, mobno;
    private static DmCRefresh asynk_new;
    String[] parts;
    DatabaseHandler db;

    public static FirebaseJobDispatcher dispatcher ;
    public static Job myJob = null;
    boolean AppCommon = false;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static int IMG_RESULT = 200;
    private static final String IMAGE_DIRECTORY_NAME = "STA Vigil Images";// directory name to store captured images and videos
    private Uri fileUri; // file url to store image/video
    String encodedImage, image_encode="NA",Imagefilename, photoName;
    String mCurrentPhotoPath;
    EditText editTextfileName;

    List<StateDetailsList> searchResults;
    //private static ActivityUpdateURL asynk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //setContentView(com.stavigilmonitoring.R.layout.csnstatewise);
        setContentView(R.layout.dmc_details_activity);

        initView();

        if (dbvalueforspinner()) {
            updateCustomerSpinner();
        } else if (ut.isnet(DmCSoNoStateStnDetails.this)) {
            new UpdateDMCUsers().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            Toast.makeText(parent, "No internet connection found..",
                    Toast.LENGTH_LONG).show();
        }
        updatelist();
        setListener();
    }

    private void initView() {
        ut = new com.stavigilmonitoring.utility();
        NameList = new ArrayList<String>();
        parent = DmCSoNoStateStnDetails.this;
        iv = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_refresh_nonrepeated_main);
        btnfilter = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_filter);
        btnfilter.setVisibility(View.VISIBLE);
        lstcsn =  findViewById(R.id.lstcsn);
        header = (TextView) findViewById(com.stavigilmonitoring.R.id.header);
        header.setText("DM Certificate SO Number wise Details");
        searchResults = new ArrayList<StateDetailsList>();
        Intent intent = getIntent();
        station = intent.getStringExtra("Type");
        subnetwork = intent.getStringExtra("SubNetwork");
        sonum = intent.getStringExtra("Network");

        db = new DatabaseHandler(getBaseContext());

        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();


    }

    private void setListener() {
        ((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
                .addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        listAdapter
                                .filter_details(((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
                                        .getText().toString().trim()
                                        .toLowerCase(Locale.getDefault()));
                        listAdapter.notifyDataSetChanged();
                    }
                });
        lstcsn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                DBInterface dbi = new DBInterface(getApplicationContext());
                mobno = dbi.GetPhno();
                dbi.Close();
                installationId = searchResults.get(position).GetInstallationIdForStateDetailsList();
                activityId = searchResults.get(position).GetActivityId();
                String word = searchResults.get(position).GetSONumber();
                String words[] =word.split("/");
                photoName = words[1]+"_"+station;
                Log.e("show list", mobno +", "+ installationId +", "+ activityId);
                showNewPrompt();
            }
        });

        lstcsn.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                DBInterface dbi = new DBInterface(getApplicationContext());
                mobno = dbi.GetPhno();
                dbi.Close();

                installationId = searchResults.get(position).GetInstallationIdForStateDetailsList();
                SONumber = searchResults.get(position).GetSONumber();
                ClipURLlist = searchResults.get(position).GetAdvertisementPlayURL();
                activityId = searchResults.get(position).GetActivityId();
                String dates = searchResults.get(position).GetEffectiveDate();
                if(dates!=null && !(dates.equalsIgnoreCase("")) ){
                    String[] parts = dates.split("-");
                    csdate = parts[0].replaceAll("-", "");
                    cedate = parts[1].replaceAll("-", "");
                }
                Log.e("show list", mobno +", "+ installationId +", "+ activityId);
                setPrompt();
                return true;
            }
        });

        iv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                fetchdata();
            }
        });

    }

    private boolean dbvalueforspinner() {
        try {
           // DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
            SQLiteDatabase sql = db.getWritableDatabase();
            Cursor cursor = sql.rawQuery("SELECT * FROM DMCUsersTable", null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.close();	/*sql.close();	db1.close();*/
                return true;
            } else {
                cursor.close();	/*sql.close();	db1.close();*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void FilterClick(View v) {
        if (((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext)).getVisibility() == View.VISIBLE) {
            ((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
                    .setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } else if (((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext)).getVisibility() == View.GONE) {
            ((EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext))
                    .setVisibility(View.VISIBLE);
            EditText textView = (EditText) findViewById(com.stavigilmonitoring.R.id.edfitertext);
            textView.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
        }

    }

    protected void setPrompt() {
        // TODO Auto-generated method stub

        final Dialog myDialog = new Dialog(parent);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoglistview);
        myDialog.setCancelable(true);
        // myDialog.getWindow().setGravity(Gravity.BOTTOM);
        myDialog.setTitle("Clip Details");

        final TextView quest = (TextView) myDialog.findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall1);
        final TextView start = (TextView) myDialog.findViewById(com.stavigilmonitoring.R.id.dialoginfogototextdatestart);
        final TextView end = (TextView) myDialog.findViewById(com.stavigilmonitoring.R.id.dialoginfogototextdateend);
        final LinearLayout btnll = (LinearLayout) myDialog.findViewById(com.stavigilmonitoring.R.id.btnll);
        btnll.setVisibility(View.VISIBLE);
        final ListView cliplist = (ListView) myDialog.findViewById(com.stavigilmonitoring.R.id.cliplist);
        final TextView tvreason = (TextView) myDialog.findViewById(com.stavigilmonitoring.R.id.editTextNarration);
        //quest.setText("Clip Details");
        quest.setText(SONumber);
        start.setText(csdate);
        end.setText(cedate);

        Log.e("Clip Url", ClipURLlist);
        String[] clips;
        if (ClipURLlist.equalsIgnoreCase("") || ClipURLlist == null){
            clips = new String[1];
            clips[0] = "No Clips";
        } else {
            parts = ClipURLlist.split("\\*\\*");
            clips = new String[parts.length];

            for (int i = 0; i < parts.length; i++) {
                String mydata = parts[i];
                clips[i] = mydata.substring(mydata.lastIndexOf("/") + 1);
            }
        }

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(parent, com.stavigilmonitoring.R.layout.select_dialog_list_item, clips);
        cliplist.setAdapter(adapter1);

        cliplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent httpIntent = new Intent(Intent.ACTION_VIEW);
                httpIntent.setData(Uri.parse(parts[position]));
                startActivity(httpIntent);
            }
        });

        Button btnReason = (Button) myDialog.findViewById(com.stavigilmonitoring.R.id.yesbtndialogclip);
        btnReason.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Reason = tvreason.getText().toString();
                if(!(tvreason.getText().toString().equalsIgnoreCase(""))){
                    updateReason();
                }

                myDialog.dismiss();
                // finish();
            }
        });
        Button btnCancel = (Button) myDialog.findViewById(com.stavigilmonitoring.R.id.nobtndialogclip);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                myDialog.dismiss();
                // finish();
            }
        });
        myDialog.show();

    }

    protected void updateReason() {
        // TODO Auto-generated method stub
        asynktask = null;
        if (asynktask == null) {
            try{
                Log.e("asynk", "null");
                asynktask = new ReasonUpdateURL();
                asynktask.executeOnExecutor(asynktask.THREAD_POOL_EXECUTOR);
            }catch(Exception e){
                e.printStackTrace();
            }
        } else {
            if (asynktask.getStatus() == AsyncTask.Status.RUNNING) {
                Log.e("asynk", "running");
            }
        }
    }

    public class ReasonUpdateURL extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String responsemsg = "m";
        com.stavigilmonitoring.utility ut;


        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            ut = new com.stavigilmonitoring.utility();
           // DatabaseHandler db = new DatabaseHandler(parent);
            SQLiteDatabase sql = db.getWritableDatabase();
            //String url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/AlertApproveAndRejected?AlertId="
            String url = "http://vritti.co/imedia/STA_Announcement/DMcertificate.asmx/ReasonUpdate?Mobile="
                    +mobno
                    +"&ActivityId="
                    + activityId
                    + "&Remark="
                    + Reason;

            Log.e("ReassignedCertificate", "url : " + url);
            url = url.replaceAll(" ", "%20");
            try {
                System.out.println("-------  activity url --- " + url);
                responsemsg = ut.httpGet(url);

                System.out.println("-------------reassign-- " + responsemsg);
                Log.e("Reassign", responsemsg);



            } catch (NullPointerException e) {
                responsemsg = "Error";
                e.printStackTrace();
                /*dff = new SimpleDateFormat("HH:mm:ss");
                Ldate = dff.format(new Date());*/

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
                            + " " );
                }

            } catch (IOException e) {
                e.printStackTrace();

                responsemsg = "Error";/*
                dff = new SimpleDateFormat("HH:mm:ss");
                Ldate = dff.format(new Date());*/

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
                            + " " );
                }

            }

            return responsemsg;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            try {
                if (responsemsg.contains("error")) {
                    Log.e("Reason", responsemsg);
                    showD("Error");
                } else if (responsemsg.contains("OK")) {
                    Log.e("Reason", responsemsg);
                    showD("Data Saved");
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
                            + " " );
                }

            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(parent);
            progressDialog.setMessage("Processing...");
            progressDialog.show();
        }
    }

    private void fetchdata(){
        asynk_new = null;
        asynk_new = new DmCRefresh();
        asynk_new.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public class DmCRefresh extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        //String sumdata2;
        @Override
        protected String doInBackground(String... params) {
            String	responsemsg;

            String bb= "";
            com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
            String urls = "http://vritti.co/imedia/STA_Announcement/DmCertificate.asmx/GetListOfPendingDM?Mobile="
                    + mobno;
            urls = urls.replaceAll(" ", "%20");

            try {
                System.out.println("-------  activity url --- " + urls);
                responsemsg = ut.httpGet(urls);

                System.out.println("-------------  xx vale of non repeated-- "
                        + responsemsg);

               // DatabaseHandler db = new DatabaseHandler(getBaseContext());
                SQLiteDatabase sql = db.getWritableDatabase();

                //sql.execSQL("DROP TABLE IF EXISTS DmCertificateTable");
                //sql.execSQL(ut.getDmCertificateTable());
                sql.delete("DmCertificateTable",null,null);

                Log.e("dm certificate", "resmsg : " + responsemsg);

                if (responsemsg.contains("<DMHeaderId>")) {

                    String columnName, columnValue;
                    Cursor cur = sql.rawQuery("SELECT * FROM DmCertificateTable", null);
                    ContentValues values1 = new ContentValues();
                    NodeList nl1 = ut.getnode(responsemsg, "Table1");

                    Cursor c = sql.rawQuery("SELECT * FROM DmCertificateTable",null);
                    ContentValues values = new ContentValues();
                    NodeList nl = ut.getnode(responsemsg, "Table1");
                    Log.e("DmCertificate data...",
                            " fetch data : " + nl1.getLength());
                    for (int i = 0; i < nl1.getLength(); i++) {
                        Element e = (Element) nl1.item(i);
                        for (int j = 0; j < cur.getColumnCount(); j++) {
                            columnName = cur.getColumnName(j);

                            columnValue = ut.getValue(e, columnName);
                            values1.put(columnName, columnValue);
                        }
                        sql.insert("DmCertificateTable",
                                null, values1);
                    }

                    cur.close();

                } else {
                    //	sop = "invalid";
                    System.out.println("--------- invalid for DmC list --- ");
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

                responsemsg = "wrong" + e.toString();
                System.out.println("--------- invalid for message type list --- "
                        + responsemsg);

            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(parent);
            progressDialog.setMessage("Loading...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try{
                String totalDMC =  DmCstnwiseActivity.dbvalueDMC(getApplicationContext());
                CharSequence text = "DmCertificate : "+totalDMC;
                String z = String.valueOf(totalDMC);
                SharedPreferences prefDMC = getApplicationContext()
                        .getSharedPreferences("PrefDMC", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorDMC = prefDMC.edit();

                editorDMC.putString("DMC",
                        String.valueOf(totalDMC));
                editorDMC.commit();
                updatelist();
                progressDialog.dismiss();
                //Log.e("prgdlg", "Ended");
            }catch(Exception e){
                e.printStackTrace();
                StackTraceElement l = new Exception().getStackTrace()[0];

                ut =new com.stavigilmonitoring.utility();
                if(!ut.checkErrLogFile()){
                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()){
                    ut.addErrLog(l.getClassName()+"/"+l.getMethodName()+":"+l.getLineNumber()+"  "+e.getMessage());
                }
            }
        }

    }

    protected void showNewPrompt() {
        // TODO Auto-generated method stub
        final Dialog myDialog = new Dialog(parent);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(com.stavigilmonitoring.R.layout.dialoyesnowithremarkncamera);
        myDialog.setCancelable(true);
        // myDialog.getWindow().setGravity(Gravity.BOTTOM);
        myDialog.setTitle("Complete Activity");

        final TextView quest = (TextView) myDialog.findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall1);
        final LinearLayout btnll = (LinearLayout) myDialog.findViewById(com.stavigilmonitoring.R.id.btnll);
        btnll.setVisibility(View.VISIBLE);
        quest.setText(" Do you want to complete activity ?");
        final ImageButton btnPhotoAttachmentcam = (ImageButton) myDialog.findViewById(com.stavigilmonitoring.R.id.btncam);
        final ImageButton btnPhotoAttachmentgal = (ImageButton) myDialog.findViewById(com.stavigilmonitoring.R.id.btngallery);

        final AutoCompleteTextView editTextAssignTo = (AutoCompleteTextView) myDialog.findViewById(com.stavigilmonitoring.R.id.editTextAssignTo);
        final EditText editTextNarration = (EditText) myDialog.findViewById(com.stavigilmonitoring.R.id.editTextNarration);
        editTextfileName = (EditText) myDialog.findViewById(com.stavigilmonitoring.R.id.editTextpath);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(parent,
                android.R.layout.select_dialog_item, NameList);
        //adapter1.setDropDownViewResource(android.R.layout.simple_list_item_1);
        editTextAssignTo.setThreshold(1);
        editTextAssignTo.setAdapter(adapter1);

        editTextAssignTo.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                editTextAssignTo.showDropDown();
                return false;
            }
        });

        btnPhotoAttachmentcam.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                captureImage();
            }
        });

        btnPhotoAttachmentgal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                //captureImage();]
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMG_RESULT);
            }
        });

        Button btnyes = (Button) myDialog.findViewById(com.stavigilmonitoring.R.id.yesbtndialogremark);
        btnyes.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                AssignToName = editTextAssignTo.getText().toString();
                Remark = editTextNarration.getText().toString();
                if(editTextAssignTo.getText().toString().equalsIgnoreCase("")){
                    editTextAssignTo.setError("Please Select Name");
                    Toast.makeText(parent, "Incorrect Data", Toast.LENGTH_LONG).show();
                } else if(editTextNarration.getText().toString().equalsIgnoreCase("")){
                    editTextNarration.setError("Please Enter Remark");
                    Toast.makeText(parent, "Incorrect Data", Toast.LENGTH_LONG).show();
                } else {
                    getAcCode(AssignToName);
                    if (AssignToMob != null) {
                        sendactivityupdatetoserver();

                    }
                }
                myDialog.dismiss();
                // finish();
            }
        });

        Button btnno = (Button) myDialog
                .findViewById(com.stavigilmonitoring.R.id.nobtndialogremark);
        btnno.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                myDialog.dismiss();
                // finish();
            }
        });
        myDialog.show();
    }

    /*
* Capturing Camera Image will lauch camera app requrest image capture
*/
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
        File image = new File(storageDir+"/"+imageFileName+".jpg");
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

    protected void sendactivityupdatetoserver() {
        // TODO Auto-generated method stub
                String urlStringToken = "http://ktc.vritti.co/api/Values/Reassignattachpostdata?";
                new ActivityUpdateAPI().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,urlStringToken);
    }

    private void updatelist() {
        searchResults.clear();
       // DatabaseHandler db = new DatabaseHandler(this);
        SQLiteDatabase sql = db.getWritableDatabase();
        Cursor c = sql.rawQuery(
                "SELECT DMDesc, ActualStartDate, ActualEndDate, Status, InstallationId, ActivityId, GenrateFileName, AdvertisementPlayURL, SoNumber, EffectiveDate  FROM DmCertificateTable WHERE SoNumber='"
                        + sonum + "' and SubNetworkCode='"
                        + subnetwork + "' and StationName='"
                        + station
                        + "' ORDER BY ActualEndDate", null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                Log.e("DATA", "Yes Data present");
                StateDetailsList sitem = new StateDetailsList();
                sitem.SetDMDesc(c.getString(0));
                sitem.Setdmcstatus(c.getString(3));
                sitem.SetInstallationIdForStateDetailsList(c.getString(4));
                sitem.SetActivityId(c.getString(5));
                sitem.SetActualStartDate(ConvertDate(c.getString(1)));
                sitem.SetActualEndDate(ConvertDate(c.getString(2)));
                sitem.SetGenrateFileName(c.getString(6));
                sitem.SetAdvertisementPlayURL(c.getString(7));
                sitem.SetSONumber(c.getString(8));
                sitem.SetEffectiveDate(c.getString(9));
                searchResults.add(sitem);
                Log.e("DATA", "Yes Data added");

            } while (c.moveToNext());

        }

        listAdapter = new DMCStateDetailsAdapter(parent, searchResults);
        lstcsn.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();

    }

    private String ConvertDate(String amcExpireDt) {
        String result = null;
        // 2017-10-30T00:00:00+05:30
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+05:30", Locale.ENGLISH);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM,yyyy");
        try {
            Date date2 = dateFormat1.parse(amcExpireDt);
            result = dateFormat2.format(date2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
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
                Log.e("ActivityId", activityId);
                jsonObject.put("ActivityId", activityId);
                jsonObject.put("ReassignedBy", AssignToMob);
                jsonObject.put("Remark", Remark);
                jsonObject.put("attacheddata", image_encode);

                String param ;// = jsonObject.toString();
                param = new Gson().toJson(jsonObject);
                Log.e("URLPARAM",params[0]+"\n"+param);

                //Log.e("URL",params[0]);
                res = OpenPostConnectionNow(params[0],param);
                responsemsg = res.toString();

                /*String param = jsonObject.toString();

                Log.e("URL",params[0]);
                res = OpenPostConnection(params[0],jsonObject);
                responsemsg = res.toString();*/
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

    public class ActivityUpdateURL extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String responsemsg = "m";
        com.stavigilmonitoring.utility ut;


        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            ut = new com.stavigilmonitoring.utility();
           // DatabaseHandler db = new DatabaseHandler(parent);
            SQLiteDatabase sql = db.getWritableDatabase();
            //String url = "http://vritti.co/imedia/STA_Announcement/TimeTable.asmx/AlertApproveAndRejected?AlertId="
            String url = "http://vritti.co/imedia/STA_Announcement/DMcertificate.asmx/ReassignedCertificateNew?Mobile="
                    + mobno
                    + "&ActivityId="
                    + activityId
                    + "&ReassignedBy="
                    + AssignToMob
                    + "&Remark="
                    + Remark
                    + "&attacheddata="
                    + image_encode.substring(0,63);

            Log.e("ReassignedCertificate", "url : " + url);
            url = url.replaceAll(" ", "%20");
            try {
                System.out.println("-------  activity url --- " + url);
                responsemsg = ut.httpGet(url);

                System.out.println("-------------reassign-- " + responsemsg);
                Log.e("Reassign", responsemsg);

            } catch (NullPointerException e) {
                responsemsg = "Error";
                e.printStackTrace();
                //dff = new SimpleDateFormat("HH:mm:ss");
                //Ldate = dff.format(new Date());

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

            } catch (IOException e) {
                e.printStackTrace();

                responsemsg = "Error";
                //dff = new SimpleDateFormat("HH:mm:ss");
                //Ldate = dff.format(new Date());

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

            return responsemsg;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            try {
                if (responsemsg.contains("error")) {
                    Log.e("Reassign", responsemsg);
                    showD("Error");
                } else if (responsemsg.contains("Error")) {
                    Log.e("Reassign", responsemsg);
                    showD("Error");
                } else if (responsemsg.contains("OK")) {
                    Log.e("Reassign", responsemsg);
                    showD("Data Saved");
                } else {
                    Log.e("Reassign", responsemsg);
                    showD("Error");
                }
            } catch (Exception e) {
                e.printStackTrace();

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
                            + " ");
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(parent);
            progressDialog.setMessage("Processing...");
            progressDialog.show();
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
                .findViewById(com.stavigilmonitoring.R.id.dialoginfogototextsmall);
        if (string.equals("Data Saved")) {
            myDialog.setTitle(" ");
            txt.setText("Data Saved");
        } else if (string.equals("Error")) {
            myDialog.setTitle(" ");
            txt.setText("Server Error.. Please try after some time");
        }

        Button btn = (Button) myDialog
                .findViewById(com.stavigilmonitoring.R.id.gotobtndialoginfosmall);
        btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                myDialog.dismiss();
                if (txt.getText().equals("Data Saved")) {
                   /* Intent i = new Intent(parent, DmCertificateService.class);
                    startService(i);*/
                    setJobShedulder();
                    finish();
                }

            }
        });

        myDialog.show();

    }

    private void updateCustomerSpinner() {
        NameList.clear();
      //  DatabaseHandler db1 = new DatabaseHandler(parent);
        SQLiteDatabase sqldb = db.getWritableDatabase();

        Cursor cursor = sqldb.rawQuery(
                "Select UserName from DMCUsersTable order by UserName ASC", null);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                NameList.add(cursor.getString(0));
                //ItemCodeList.add(cursor.getString(1));
            } while (cursor.moveToNext());

            //custCode = ItemCodeList.get(0);
        }

    }

    private void getAcCode(String Name) {
        // TODO Auto-generated method stub
       // DatabaseHandler db1 = new DatabaseHandler(parent);
        SQLiteDatabase sqldb = db.getWritableDatabase();

        Cursor cursor = sqldb.rawQuery(
                "Select Mobile from DMCUsersTable where UserName = '"+AssignToName+"'", null);
        if (cursor.getCount()==0){
            Toast.makeText(parent, "Name not available", Toast.LENGTH_SHORT).show();
        }
        else if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            AssignToMob = cursor.getString(0);

        }
        return;

    }

    class UpdateDMCUsers extends AsyncTask<Void, Void, String> {

        ProgressDialog progressDialog;
        String exceptionString = "ok";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(parent);
            progressDialog.setMessage("Updating database...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            String Url = "http://vritti.co/imedia/STA_Announcement/DmCertificate.asmx/GetDMCertifcateUser?Mobile=" + mobno;

            Log.e("DMCertificateUser", "url : " + Url);
            Log.e("Tag", " ******* WORKING ON DMCertificateUser *********");
            Url = Url.replaceAll(" ", "%20");
            try {
                resposmsg = ut.httpGet(Url);
                Log.e("Response", resposmsg);
            } catch (IOException e) {
                sop = "ServerError";
                e.printStackTrace();

            }

            if (resposmsg.contains("Record are not Found...!")) {
                //sumdata2 = "0";
                sop = "nodata";
               // DatabaseHandler db = new DatabaseHandler(getBaseContext());
                SQLiteDatabase sql = db.getWritableDatabase();
                sql.execSQL("Delete from DMCUsersTable");
                //up

            } else if (resposmsg.contains("<UserId>")) {
                sop = "valid";
               // DatabaseHandler db = new DatabaseHandler(getBaseContext());
                SQLiteDatabase sql = db.getWritableDatabase();

                String columnName, columnValue;

               // sql.execSQL("DROP TABLE IF EXISTS DMCUsersTable");
               // sql.execSQL(ut.getDMCUsersTable());
                sql.delete("DMCUsersTable",null,null);

                Cursor cur1 = sql.rawQuery("SELECT * FROM DMCUsersTable", null);
                int count = cur1.getCount();
                ContentValues values2 = new ContentValues();
                NodeList nl2 = ut.getnode(resposmsg, "Table1");

                for (int i = 0; i < nl2.getLength(); i++) {
                    Element e = (Element) nl2.item(i);
                    for (int j = 0; j < cur1.getColumnCount(); j++) {
                        columnName = cur1.getColumnName(j);
                        columnValue = ut.getValue(e, columnName);

                        values2.put(columnName, columnValue);
                    }
                    sql.insert("DMCUsersTable", null, values2);
                }
                cur1.close();/*
                sql.close();
                db.close();*/

            } else {
                sop = "invalid";
            }

            return sop;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                if (sop == "valid") {
                   updateCustomerSpinner();
                }
                progressDialog.dismiss();

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void setJobShedulder() {

        // checkBatteryOptimized();
        if(myJob == null) {
            dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
                callJobDispacher_DMCertificate();

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
                    callJobDispacher_DMCertificate();

            }else {
                AppCommon = true;
                dispatcher.cancelAll();
                dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
                myJob = null;
                    callJobDispacher_DMCertificate();

            }
        }
    }

    private void callJobDispacher_DMCertificate() {
        myJob = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(JobService_DMCertificate.class)
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

                //File f = new File(URI.getPath().toString());
                //Imagefilename = f.getName();
                editTextfileName.setText(ImageDecode);

            }
        } catch (Exception e) {
            Toast.makeText(this, "Please try again", Toast.LENGTH_LONG)
                    .show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*Intent i = new Intent(parent, DmCSonoStnFilter.class);
        i.putExtra("Network", sonum);
        i.putExtra("Type", subnetwork);
        startActivity(i);*/
        finish();
    }

}