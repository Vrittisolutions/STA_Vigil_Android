package com.stavigilmonitoring;

import android.app.Activity;
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
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adapters.SendEmailPdfAdapter;
import com.beanclasses.STA_Visit_Questions;
import com.beanclasses.reporteeBean;
import com.database.DBInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.provider.CalendarContract.CalendarCache.URI;

public class SendEmailPDF extends Activity {
    private Context parent;
    ListView listview;
    LinearLayout llscroll;
    Button btnsendEmail;
    TextView txtnw;

    String JsonData, Network, StationName, Supporter, addedDt,mobno;
    JSONArray jsonMain;
    ArrayList<STA_Visit_Questions> tempList;
    ArrayList<String> emailList;
    SendEmailPdfAdapter emailPdfAdapter;

    DatabaseHandler dbhandler;
    String sop = "";

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_send_email_pdf);

        init();

        if (dbvalue()) {
            try{
                updateEmailIdList();
            }catch (Exception e){
                e.printStackTrace();
            }
        } else if (isnet()) {
            new DownloadxmlsDataURL_new().execute();
        } else {
            //showD("nonet");
        }

        //updateEmailIdList();

        parseJSON(jsonMain);

        btnsendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailToHOD();
            }
        });

    }

    public void init(){
        parent = SendEmailPDF.this;

        listview = (ListView)findViewById(R.id.listview);
        llscroll= (LinearLayout)findViewById(R.id.llscroll);
        btnsendEmail= (Button)findViewById(R.id.btnsendEmail);

        txtnw = (TextView)findViewById(R.id.txtnw);

        Intent intent = getIntent();
        Network = intent.getStringExtra("Network");
        StationName = intent.getStringExtra("StationName");
        Supporter = Common.UserName;

        try {
            jsonMain = new JSONArray(intent.getStringExtra("JsonData"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try{
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd_MMM_YYYY");
            addedDt = sdf.format(c.getTime());
        }catch (Exception e){
            e.printStackTrace();
        }

        txtnw.setText(Network +" - "+  StationName + " - " + Supporter + " - " + addedDt);

        tempList = new ArrayList<STA_Visit_Questions>();
        emailList = new ArrayList<String>();
      
        dbhandler = new DatabaseHandler(parent);

        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();
        
       // tempList = intent.getParcelableExtra("List");

        /*Bundle bundle = getIntent().getExtras();
       // JsonData = bundle.getString("JsonData");
        Network = bundle.getString("Network");
        StationName = bundle.getString("StationName");
        Supporter = bundle.getString("Supporter");
        ArrayList<STA_Visit_Questions> tempList = bundle.getParcelableArrayList("List");*/

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

    public void parseJSON(JSONArray jsonMain){
        String qus = "", ans = "";

        for(int i=0; i<jsonMain.length(); i++){
            try{
                JSONObject jsonObject = jsonMain.getJSONObject(i);
                qus = jsonObject.getString("QuesText");
                ans = jsonObject.getString("ResponseByCustomer");

                STA_Visit_Questions qusans = new STA_Visit_Questions();
                qusans.setQuestion(qus);
                qusans.setAnswer(ans);

                tempList.add(qusans);

            }catch (Exception e){
                e.printStackTrace();
            }
        }

         emailPdfAdapter = new SendEmailPdfAdapter(this, tempList);
         listview.setAdapter(emailPdfAdapter);
    }

    public void sendEmailToHOD(){
        Log.d("size"," "+llscroll.getWidth() +"  "+llscroll.getWidth());
         bitmap = loadBitmapFromView(llscroll, llscroll.getWidth(), llscroll.getHeight());
        //bitmap = loadBitmapFromView(llscroll, llscroll.getWidth(), llscroll.getHeight());
        String _pathToParse = createPdf();

        //	String[] a = FileHelper.saveTextFile("","Ichalkaranji","Chetana Salunkhe","MSRTC");

        String data1 = "Respected Sir, \n\t\t Please find attached PDF file of submitted station visit form for" +
                " Network - "+ Network +", Station - "+ StationName + "\n\n\t Thanks and Regards, \n\t"+Supporter + " \n\t Via STA Vigil";//message to send

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
            Toast.makeText(this,"Saved to file",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Error save file!!!",Toast.LENGTH_SHORT).show();
        }

        String[] eml = new String[emailList.size()];
        String[] email = new String[emailList.size()];
        try
        {
            // eml = {"chetana.salunkhe@vritti.co.in", "sanket.shende@vritti.co.in"};
            try{
                for(int i=0; i< emailList.size(); i++){
                    eml[i] =  emailList.get(i).toString();
                   // email = emailList.get(i).toString() + ",";

                }
            }catch (Exception e){
                e.printStackTrace();
            }

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
            this.startActivity(Intent.createChooser(emailIntent,"Sending email..."));
            finish();
        }
        catch (Throwable t) {
            Toast.makeText(this, "Request failed try again: " + t.toString(),Toast.LENGTH_LONG).show();
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

       /* Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MMM_YYYY");
        String addedDt = sdf.format(c.getTime());

        txtnw.setText(Network +" - "+  StationName + " - " + Supporter + " - " + addedDt);*/

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
                Toast.makeText(this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class DownloadxmlsDataURL_new extends
            AsyncTask<String, Void, String> {

        String responsemsg = "";

        @Override
        protected String doInBackground(String... params) {
            com.stavigilmonitoring.utility ut = new com.stavigilmonitoring.utility();
            //DatabaseHandler db = new DatabaseHandler(getBaseContext());
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
                    //sql.delete("Reporting",null,null);

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
            } catch (Exception e) {
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

                    updateEmailIdList();
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

        emailList.clear();
        
        SQLiteDatabase sql = dbhandler.getWritableDatabase();
        int count = 0;
        Cursor c = sql.rawQuery("SELECT email FROM Reporting", null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                reporteeBean bean = new reporteeBean();
               // ReporteeEmail = c.getString(c.getColumnIndex("email"));
                emailList.add(c.getString(c.getColumnIndex("email")));

            } while (c.moveToNext());
        }
    }
}
