package com.stavigilmonitoring;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapters.AdvStationsAdapter;
import com.beanclasses.AdvVideoDataBean;
import com.database.DBInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdvDtlsStationsList extends Activity {
    private Context parent;
    TextView hdrnetwrks;
    ListView lst_stns;
    ImageView mRefresh;
    ProgressBar mprogress;
    String Network, ClipNo, Stationname;
    ArrayList<AdvVideoDataBean> listStations;
    String sop;
    String responsemsg = "k";
    private String DateToStr;
    String mobno;
    utility ut;
    DatabaseHandler db;
    SQLiteDatabase sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_adv_first_play_rprt__stations_list);

        init();

        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();

        getDataFromTable();

        setListeners();
    }

    public void init(){
        parent = AdvDtlsStationsList.this;

        mRefresh = (ImageView) findViewById(R.id.imgbtnrfrsh);
        mRefresh.setVisibility(View.GONE);
        mprogress = (ProgressBar) findViewById(R.id.progressBar1);

        hdrnetwrks = (TextView)findViewById(R.id.hdrnetwrks);
        lst_stns = findViewById(R.id.lst1playrprtstn);

        listStations = new ArrayList<AdvVideoDataBean>();
        ut = new utility();
        db = new DatabaseHandler(this);
        sql = db.getWritableDatabase();

        Intent intent = getIntent();
        Network = intent.getStringExtra("Network");
        ClipNo = intent.getStringExtra("ClipNo");

        hdrnetwrks.setText("Stations List"+" - "+Network+" - "+ClipNo);
    }

    public void setListeners(){

    }

    public void getDataFromTable(){
        listStations.clear();

        String qryStns = "Select DISTINCT InstalationId,InstalationName,EffectiveDateTo,EffectiveDatefrom FROM AdvAudioClipDtls " +
                "WHERE AdvertisementCode='"+ClipNo+"' AND " + "NetworkCode='"+Network+"'";
        Cursor c2 = sql.rawQuery(qryStns,null);
        if(c2.getCount()>0){
            c2.moveToFirst();
            do{
                String instId = c2.getString(c2.getColumnIndex("InstalationId"));
                String instName = c2.getString(c2.getColumnIndex("InstalationName"));
                String EffectiveDatefrom = c2.getString(c2.getColumnIndex("EffectiveDatefrom"));
                String EffectiveDateTo = c2.getString(c2.getColumnIndex("EffectiveDateTo"));

                AdvVideoDataBean beanStn = new AdvVideoDataBean();
                beanStn.setInstalationId(instId);
                beanStn.setInstalationName(instName);
                beanStn.setEffectiveDatefrom(dateconvert(EffectiveDatefrom));
                beanStn.setEffectiveDateTo(dateconvert(EffectiveDateTo));

                listStations.add(beanStn);

            }while (c2.moveToNext());

            AdvStationsAdapter adapter = new AdvStationsAdapter(this,listStations);
            lst_stns.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    public String dateconvert(String Date_to_convert){

        String date = Date_to_convert.split("T")[0];

        SimpleDateFormat Format = new SimpleDateFormat("dd MMM yyyy");//Feb 23 2016 12:16PM
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        //SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        SimpleDateFormat toFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date d1 = null;

        try {
            d1 = format.parse(date);
            //DateToStr = toFormat.format(date);
            DateToStr = Format.format(d1);
            System.out.println(DateToStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateToStr;
    }

    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate) {

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {

        }

        return outputDate;

    }

    public String dateconvert_1(String Date_to_convert){ //5/31/2019 1:10:33 PM

        SimpleDateFormat Format = new SimpleDateFormat("dd MMM yyyy");//Feb 23 2016 12:16PM
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        //SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat toFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date d1 = null;

        try {
            //d1 = format.parse(DoAck);
            d1 = format.parse(Date_to_convert);
            //DateToStr = toFormat.format(date);
            DateToStr = Format.format(d1);
            System.out.println(DateToStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateToStr;
    }
}
