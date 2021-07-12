package com.stavigilmonitoring;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapters.StationPerforDetails_Adapter;

import java.util.ArrayList;

public class StationPerformanceDetails extends Activity {
    private Context parent;

    ListView listperformance;
    ImageView mRefresh;
    ProgressBar mprogress;
    DatabaseHandler db;
    SQLiteDatabase sqlDb;
    String resposmsg, sop,mobno;
    private utility ut;
    ArrayList<StationPerformance> arraylistStations;
    TextView hdrnetwrks;
    String Network;

    StationPerforDetails_Adapter adapter_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_station_performance_details);

        init();

        setListeners();
    }

    public void init(){
        parent = StationPerformanceDetails.this;

        listperformance = (ListView)findViewById(R.id.listperfdetails);
        mRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.imgbtnrfrsh);
        mprogress = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1);

        hdrnetwrks = (TextView)findViewById(R.id.hdrnetwrks);

        Intent intent = getIntent();
        Network = intent.getStringExtra("Network");

        hdrnetwrks.setText("Station Performance Details - "+ Network);

    }

    public void setListeners(){

    }
}
