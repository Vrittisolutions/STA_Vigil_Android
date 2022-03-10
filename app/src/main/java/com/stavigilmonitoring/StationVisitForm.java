package com.stavigilmonitoring;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adapters.StaVisitForm_Adapter;
import com.beanclasses.STA_Visit_Questions;
import com.beanclasses.reporteeBean;
import com.database.DBInterface;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.provider.CalendarContract.CalendarCache.URI;
import static com.helper.Util.setListViewHeightBasedOnChildren;

public class StationVisitForm extends Activity {
    com.stavigilmonitoring.utility ut;
    Context parent;
    String MyFlag = "no flag",mobileUserName;
    LinearLayout llscroll;
    ListView lstcsn;
    TextView header;
    ImageView iv,btnfilter,btnadd;
    String mobno, ActivityID;
    Button btnsave, btncancel;
    private static Download_StaVisitQuns asynk_new;
    String sop, resposmsg;

    ArrayList<STA_Visit_Questions> searchResults;
    ArrayList<STA_Visit_Questions> reporteeEmailList;
    String StationName, network, subnetwork, IssuedToName, Activity=null, SubType1, Type1, frompage1;
    String mobi, ReassignBy, activityId, Remark, ReassignTo, worktype ="";
    Button testbtn_stavisit;
    ImageView imgcamera;

    StaVisitForm_Adapter staVisitForm_adapter;
    String responsemsg = "k";
    DatabaseHandler dbhandler;
    String PKQuesId, QuesText, ResponseType, ValueMin, ValueMax, SelectionText, QuesCode;
    JSONObject jsonMain;
    JSONArray temp_jsonArray1;

    ArrayList<STA_Visit_Questions> tempListToSend;

    private Uri fileUri;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final String IMAGE_DIRECTORY_NAME = "STA Vigil Images";
    String SerDate, newDate, photoName, Station, mCurrentPhotoPath,ReportingName, ReportingID, ReporteeEmail;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.stavigilmonitoring.R.layout.activity_station_visit_form);

        initView();

        if (dbhandler.get_Sta_Visit_form_data() > 0) {
            updatelist();   // retrieve data from database
        } else {
            fetchdata();
        }

        setlistener();
    }

    public void initView(){
        ut = new com.stavigilmonitoring.utility();
        //NameList = new ArrayList<String>();
        parent = StationVisitForm.this;
        iv = (ImageView) findViewById(R.id.button_refresh_nonrepeated_main);

        /*btnadd = (ImageView) findViewById(R.id.button_alert_add);
        btnadd.setImageResource(R.drawable.work_assign);
        btnadd.setVisibility(View.GONE);*/

        llscroll = (LinearLayout)findViewById(R.id.llscroll);
        btnfilter = (ImageView) findViewById(com.stavigilmonitoring.R.id.button_filter);
        btnfilter.setVisibility(View.GONE);

        lstcsn = (ListView) findViewById(com.stavigilmonitoring.R.id.lstcsn);
        setListViewHeightBasedOnChildren(lstcsn);
        header = (TextView) findViewById(com.stavigilmonitoring.R.id.header);
        btnsave = (Button)findViewById(com.stavigilmonitoring.R.id.btnsave_stavisit);
        btncancel = (Button)findViewById(com.stavigilmonitoring.R.id.btncancel_stavisit);
        imgcamera = (ImageView)findViewById(R.id.btncamera);
        imgcamera.setVisibility(View.GONE);
        mobileUserName = Common.UserName;

        searchResults = new ArrayList<STA_Visit_Questions>();
        reporteeEmailList = new ArrayList<STA_Visit_Questions>();
        tempListToSend = new ArrayList<STA_Visit_Questions>();

        dbhandler = new DatabaseHandler(getBaseContext());

        /*Intent intent = getIntent();
        Activity = intent.getStringExtra("Activity");

        if(Activity.equals("WorkAssignStationListActivity")){
            StationName = intent.getStringExtra("Type");
        }else if(Activity.equals("ConnectionStatus")){
            StationName = intent.getStringExtra("stnname");
            SubType1 = intent.getStringExtra("SubType");
            Type1 = intent.getStringExtra("Type");
            frompage1 = intent.getStringExtra("frompage");
        }
        header.setText("Activity Details - "+StationName );*/

        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();

        Intent intent = getIntent();
        ActivityID = intent.getStringExtra("activityId");
        StationName = intent.getStringExtra("StationName");
        network = intent.getStringExtra("Network");

        Date currentDate = new Date();
        newDate = splittime(currentDate);
        SerDate = splittime(newDate);

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

    private boolean dbvalue() {

        try {
            //DatabaseHandler db1 = new DatabaseHandler(getBaseContext());
            SQLiteDatabase sql = dbhandler.getWritableDatabase();
            Cursor cursor = sql.rawQuery("SELECT * FROM Reporting", null);

            System.out.println("----------  dbvalue screen cursor count -- "
                    + cursor.getCount());

            if (cursor != null && cursor.getCount() > 0) {

                cursor.close();
                return true;

            } else {

                cursor.close();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        }

    }

    public void setlistener(){
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send data to server
               getQueAnsdata();

               /* if (FileHelper.saveTextFile("test file createion",StationName)){
                    Toast.makeText(parent,"Saved to file",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(parent,"Error save file!!!",Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // capture picture
                String Sendingdate = SerDate.replace("-","");
                Sendingdate = Sendingdate.replace(":","");
                Sendingdate = Sendingdate.replace(" ","_");
                //photoName = "WD_"+Station+"_"+Sendingdate+".jpg";
                photoName = "STVForm"+Station+"_"+Sendingdate+".jpg";
                captureImage();
            }
        });

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchdata();
            }
        });
    }

    private void fetchdata(){
        asynk_new = null;
        asynk_new = new Download_StaVisitQuns();
        asynk_new.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public class Download_StaVisitQuns extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String sumdata2;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            //String Url = "http://sta.vritti.co/imedia/STA_Announcement/DmCertificate.asmx/GetWorkAssignList?Mobile="
            String Url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/GetQuestions";
            Url = Url.replaceAll(" ", "");
            try {
                responsemsg = ut.httpGet(Url);
                Log.e("Response", responsemsg);

            }catch(IOException e){
                sop = "ServerError";
                e.printStackTrace();
            }

            if(responsemsg.contains("<PKQuesId>")){
                sop = "valid";

               // DatabaseHandler db = new DatabaseHandler(getBaseContext());
                SQLiteDatabase sql = dbhandler.getWritableDatabase();

                NodeList nl1 = ut.getnode(responsemsg, "TableResult");

                for (int i = 0; i < nl1.getLength(); i++) {

                    Element e = (Element) nl1.item(i);

                    PKQuesId = ut.getValue(e,"PKQuesId");
                    QuesText = ut.getValue(e,"QuesText");
                    ResponseType = ut.getValue(e,"ResponseType");
                    SelectionText = ut.getValue(e,"SelectionText");
                    ValueMin = ut.getValue(e,"ValueMin");
                    ValueMax = ut.getValue(e,"ValueMax");
                    QuesCode = ut.getValue(e,"QuesCode");

                    STA_Visit_Questions sv_quees_ans = new STA_Visit_Questions();
                    sv_quees_ans.setQuesID(PKQuesId);
                    sv_quees_ans.setQuestion(QuesText);
                    sv_quees_ans.setResponseType(ResponseType);
                    sv_quees_ans.setSelectionText(SelectionText);
                    sv_quees_ans.setValueMax(ValueMax);
                    sv_quees_ans.setValuemin(ValueMin);
                    sv_quees_ans.setQuesCode(QuesCode);
                    searchResults.add(sv_quees_ans);

                    dbhandler.add_sta_visit_form_data(PKQuesId,QuesText,ResponseType,SelectionText,ValueMin,ValueMax,QuesCode);
                }

            }else{
                sop = "invalid";
            }
            return sop;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            iv.setVisibility(View.GONE);
            progressDialog = new ProgressDialog(parent);
            ((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
                    .setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try{
                if(sop.equals("valid")){
                    Log.e("Tag", " ******* STA Visit Form*********");
                    staVisitForm_adapter = new StaVisitForm_Adapter(getApplicationContext(), searchResults);
                    lstcsn.setAdapter(staVisitForm_adapter);

                } else if(sop.equals("nodata")){

                }

                iv.setVisibility(View.VISIBLE);
                ((ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1))
                        .setVisibility(View.GONE);

            }catch(Exception e){
                e.printStackTrace();
                StackTraceElement l = new Exception().getStackTrace()[0];

                ut =new utility();
                if(!ut.checkErrLogFile()){
                    ut.ErrLogFile();
                }
                if (ut.checkErrLogFile()){
                    ut.addErrLog(l.getClassName()+"/"+l.getMethodName()+":"+l.getLineNumber()+"  "+e.getMessage());
                }
            }
        }
    }

    private void updatelist() {
        searchResults.clear();
        //DatabaseHandler db = new DatabaseHandler(this);
        SQLiteDatabase sql = dbhandler.getWritableDatabase();
        Cursor c = sql.rawQuery("SELECT * FROM sta_visit_form GROUP BY QuesCode", null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                Log.e("DATA", "Yes Data present");

                PKQuesId = c.getString(c.getColumnIndex("PKQuesId")) ;
                QuesText = c.getString(c.getColumnIndex("QuesText"));
                ResponseType = c.getString(c.getColumnIndex("ResponseType"));
                SelectionText = c.getString(c.getColumnIndex("SelectionText"));
                ValueMin = c.getString(c.getColumnIndex("ValueMin"));
                ValueMax = c.getString(c.getColumnIndex("ValueMax"));
                QuesCode = c.getString(c.getColumnIndex("QuesCode"));

                STA_Visit_Questions sv_quees_ans = new STA_Visit_Questions();
                sv_quees_ans.setQuesID(PKQuesId);
                sv_quees_ans.setQuestion(QuesText);
                sv_quees_ans.setResponseType(ResponseType);
                sv_quees_ans.setSelectionText(SelectionText);
                sv_quees_ans.setValueMax(ValueMax);
                sv_quees_ans.setValuemin(ValueMin);
                sv_quees_ans.setQuesCode(QuesCode);
                searchResults.add(sv_quees_ans);
               // searchResults.add(sitem);
                Log.e("DATA", "Yes Data added");

                /*Sort by Aplhabatically*/
               /* Collections.sort(searchResults, new Comparator<STA_Visit_Questions>() {
                    @Override
                    public int compare(STA_Visit_Questions lhs, STA_Visit_Questions rhs) {
                        return lhs.getQuesCode().compareTo(rhs.getQuesCode());
                    }
                });*/

            } while (c.moveToNext());
        }

        staVisitForm_adapter = new StaVisitForm_Adapter(parent, searchResults);
        lstcsn.setAdapter(staVisitForm_adapter);
    }

    private void getQueAnsdata(){

        int i=0;
        String QuestionID, Question, Answer;

        if(searchResults.size() > 0){

            JSONArray jsonArray1 = new JSONArray();
            temp_jsonArray1 = new JSONArray();
            STA_Visit_Questions sta_ques = searchResults.get(i);

            for(i = 0; i < searchResults.size(); i++ ){

                QuestionID = searchResults.get(i).getQuesID();
                Question = searchResults.get(i).getQuestion();
                Answer = searchResults.get(i).getANSWER();

                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("FKQuesId",QuestionID);
                   // jsonObject.put("QuesText",Question);
                   // jsonObject.put("SelectionText",Answer);
                    jsonObject.put("ResponseByCustomer",Answer);
                   // jsonObject.put("AddedBy",mobileUserName);

                    jsonArray1.put(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject temp_jsonObject = new JSONObject();

                try {
                    temp_jsonObject.put("FKQuesId",QuestionID);
                    temp_jsonObject.put("QuesText",Question);
                   // temp_jsonObject.put("SelectionText",Answer);
                    temp_jsonObject.put("ResponseByCustomer",Answer);
                    // jsonObject.put("AddedBy",mobileUserName);

                    temp_jsonArray1.put(temp_jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            String listtosend = jsonArray1.toString();
            Log.e("count",listtosend);

            jsonMain = new JSONObject();
            try {
                jsonMain.put("jsonString", jsonArray1);
                jsonMain.put("MobileNo", mobno);
                jsonMain.put("ActivityId", ActivityID);
               // Toast.makeText(parent,"Data Stored in jsonmain Successfully",Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //call API to send data to server
             SubmitForm(String.valueOf(jsonMain));

        }else {
            Toast.makeText(parent,"Please answer the questions",Toast.LENGTH_SHORT).show();
        }
    }

    protected void SubmitForm(String jsonObject) {
        // TODO Auto-generated method stub
        String urlStringToken = "http://nwkrtc.vritti.co/api/Values/SaveQuestions?";
        new SendFormToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, urlStringToken, jsonObject);
    }

    public class SendFormToServer extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        Object res;
        String responsemsg = "m";
        com.stavigilmonitoring.utility ut;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            ut = new com.stavigilmonitoring.utility();

            String url = params[0];
            String json_Object = new Gson().toJson(params[1]);

            Log.e("SubmitForm", "url : " + url);
            url = url.replaceAll(" ", "%20");
            try {
                System.out.println("-------  activity url --- " + url);
                res = ut.OpenPostConnectionNow(url,params[1]);
                responsemsg = res.toString();

                System.out.println("-------------SubmitForm-- " + responsemsg);
                Log.e("SubmitForm", responsemsg);

            } catch (NullPointerException e) {
                responsemsg = "Error";
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
                responsemsg = "Error";
            }

            return responsemsg;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            try{
                if (responsemsg.contains("true")) {
                    Log.e("True", responsemsg);
                    Toast.makeText(parent,"Data saved successfully",Toast.LENGTH_SHORT).show();
                    //send mail to respected persons

                    Intent intent = new Intent(StationVisitForm.this, SendEmailPDF.class);
                    try{
                        intent.putExtra("JsonData",temp_jsonArray1.toString());
                        intent.putExtra("Network",network);
                        intent.putExtra("StationName",StationName);
                        intent.putExtra("Supporter","");
                        //  intent.putExtra("List", (Parcelable) searchResults);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    startActivity(intent);

                    //sendEmailToHOD();
                    finish();
                } else if (responsemsg.contains("false")) {
                    Log.e("False", responsemsg);
                    Toast.makeText(parent,"Data not saved",Toast.LENGTH_SHORT).show();
                }else  if (responsemsg.contains("Error")) {
                    Log.e("Error", responsemsg);
                    Toast.makeText(parent,"Data not saved",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(parent);
            progressDialog.setMessage("Processing...");
            progressDialog.show();
            MyFlag = "no flag";
        }
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
        photoName = "STVForm"+Station+"_"+Sendingdate+".jpg";

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

    private String splittime(String tf) {
        // TODO Auto-generated method stub
        System.out.println("---value of tf for date...." + tf);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");// 30-4-2016
        Date myDate = null;
        try {
            myDate = dateFormat.parse(tf);
            System.out
                    .println("..........value of my date after conv" + myDate);

        } catch (ParseException e) {
            e.printStackTrace();
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

    public class DownloadxmlsDataURL_new extends
            AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
            SQLiteDatabase sql = dbhandler.getWritableDatabase();
            String url;

            sop = "valid";
            String columnName, columnValue;
            Log.e("Tag", " ******* WORKING ON SYNCDATA *********");

            url = "http://sta.vritti.co/iMedia/STA_Announcement/TimeTable.asmx/getreportingTo?Mobileno="
                    + mobno;

            Log.e("material", "url : " + url);
            url = url.replaceAll(" ", "%20");
            try {
                responsemsg = ut.httpGet(url);
                NodeList NL = ut.getnode(responsemsg, "Table1");
                Log.e("material", "resmsg : " + responsemsg);

                if (responsemsg.contains("<UserMasterid>")) {
                    sop = "valid";
                    sql.execSQL("DROP TABLE IF EXISTS Reporting");

                    sql.execSQL("Create table Reporting ( UserMasterid TEXT , Username TEXT, email TEXT)");//usermasterid,Username
                    Log.e("Tag", " ******* WORKING ON Material Reporting table *********");
                    Cursor cur1 = sql.rawQuery("SELECT * FROM Reporting", null);
                    ContentValues values2 = new ContentValues();
                    Log.e("WorkType data...", " fetch data : " + NL.getLength());
                    for (int i = 0; i < NL.getLength(); i++) {
                        Element e = (Element) NL.item(i);
                        Log.e("Reporting data...", " fetch data : " + i);

                        for (int j = 0; j < cur1.getColumnCount(); j++) {
                            columnName = cur1.getColumnName(j);

                            columnValue = ut.getValue(e, columnName);
                            Log.e("Reporting data...", " fetch data : "
                                    + columnValue);
                            values2.put(columnName, columnValue);
                        }
                        long ad = sql.insert("Reporting", null, values2);
                        Log.e("Tag", " ******* WORKING ON MAterial DATA inserted *********");
                    }

                    cur1.close();

                }
            } catch (NullPointerException e) {
                responsemsg = "Error";
                e.printStackTrace();

            } catch (UnknownHostException e) {
                responsemsg = "Error";
                e.printStackTrace();

            } catch (IOException e) {
                responsemsg = "Error";
                e.printStackTrace();

            }

            return responsemsg;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (responsemsg.equalsIgnoreCase("Error")) {
                    //showD("invalid");
                } else {
                    updatelist();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
    }

    private void updateEmailIdList() {

        reporteeEmailList.clear();
        //DatabaseHandler db = new DatabaseHandler(this);
        SQLiteDatabase sql = dbhandler.getWritableDatabase();
        int count = 0;
        Cursor c = sql.rawQuery("SELECT Username,UserMasterid,email FROM Reporting order by Username desc", null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                reporteeBean bean = new reporteeBean();
                ReportingName = c.getString(c.getColumnIndex("Username"));
                ReportingID = c.getString(c.getColumnIndex("UserMasterid"));
                ReporteeEmail = c.getString(c.getColumnIndex("email"));

                bean.setReporteeName(ReportingName);
                bean.setReporteeID(ReportingID);
                bean.setReporteeEmail(ReporteeEmail);

                //reporteeEmailList.add(bean);

            } while (c.moveToNext());
        }
    }

    public void sendEmailToHOD(){
        Log.d("size"," "+llscroll.getWidth() +"  "+llscroll.getWidth());
       // bitmap = loadBitmapFromView(llscroll, llscroll.getWidth(), llscroll.getHeight());
        bitmap = loadBitmapFromView(lstcsn, lstcsn.getWidth(), lstcsn.getHeight());
        String _pathToParse = createPdf();

        //	String[] a = FileHelper.saveTextFile("","Ichalkaranji","Chetana Salunkhe","MSRTC");

        String data1 = "Respected Sir, \n\t\t Please find attached PDF file of submitted station visit form for" +
                " Network - "+ network +", Station - "+ StationName + "\n\n\t Thanks and Regards, \n\t"+" Chetana salunkhe" + " \n\t Via STA Vigil";//message to send

        String file = Environment.getExternalStorageDirectory().getAbsolutePath();
        file = file.replace("///","//");

        //File filelocation = new File(file, "/StationVisitForms/"+ _pathToParse);
        Uri path =  FileProvider.getUriForFile(getParent(),
                getPackageName() + ".provider",
                new File(file, "/StationVisitForms/"+ _pathToParse));

				/*Uri path1 =  FileProvider.getUriForFile(getParent(),
						getPackageName() + ".provider",
						new File(file, "/StationVisitForms/"+ a[1]));*/

        if (data1 != ""){
            Toast.makeText(StationVisitForm.this,"Saved to file",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(StationVisitForm.this,"Error save file!!!",Toast.LENGTH_SHORT).show();
        }

        try
        {
            String[] eml = {"chetana.salunkhe@vritti.co.in", "sanket.shende@vritti.co.in"};

            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            //emailIntent.setType("plain/text");
            emailIntent.setType("application/pdf");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, eml);
            //emailIntent.putExtra(Intent.EXTRA_STREAM, path);
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Station Visit Form");
            if (URI != null) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, path);
            }
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, data1);
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            StationVisitForm.this.startActivity(Intent.createChooser(emailIntent,"Sending email..."));
        }
        catch (Throwable t) {
            Toast.makeText(StationVisitForm.this, "Request failed try again: " + t.toString(),Toast.LENGTH_LONG).show();
        }
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    private String createPdf(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //  Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        int convertHighet = (int) hight, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        PdfDocument document = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            document = new PdfDocument();
        }
        PdfDocument.PageInfo pageInfo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
        }
        PdfDocument.Page page = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            page = document.startPage(pageInfo);
        }

        Canvas canvas = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            canvas = page.getCanvas();
        }

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);

        paint.setTextSize(15);
        paint.setTextScaleX(50);
        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            document.finishPage(page);
        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MMM_YYYY");
        String addedDt = sdf.format(c.getTime());

        // write the document content
        final String fileName = addedDt+"_stvisit.pdf";
        String targetPdf = Environment.getExternalStorageDirectory().getAbsolutePath() +"/StationVisitForms/"+StationName+"_"+fileName;
        String _fileName = StationName+"_"+fileName;
        File filePath;
        filePath = new File(targetPdf);
        try {

            if (!filePath.exists()) {
                filePath.getParentFile().mkdirs();
                filePath.createNewFile();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //FileOutputStream fileOutputStream = new FileOutputStream(filePath,true);
                document.writeTo(new FileOutputStream(filePath));
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            document.close();
        }
        Toast.makeText(this, "PDF of Scroll is created!!!", Toast.LENGTH_SHORT).show();

        return _fileName;

        // openGeneratedPDF();
    }

    private void openGeneratedPDF(){
        File file = new File("/sdcard/pdffromScroll.pdf");

        if (file.exists())
        {
            Intent intent=new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try
            {
                startActivity(intent);
            }
            catch(ActivityNotFoundException e)
            {
                Toast.makeText(StationVisitForm.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }
}
