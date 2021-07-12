package com.stavigilmonitoring;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapters.StnPerfSeven_DayValue_Adapter;
import com.adapters.StnPerformnce_DayValue_Adapter;
import com.database.DBInterface;

import java.util.ArrayList;

public class AllStnPerformanceList extends Activity {
    private Context parent;

    ListView listperformance;
    ImageView mRefresh;
    ProgressBar mprogress;
    DatabaseHandler db;
    SQLiteDatabase sqlDb;
    String resposmsg, sop,mobno;
    private utility ut;
    ArrayList<StationPerformance> arraylistStations;

    StnPerformnce_DayValue_Adapter adapter_1day;
    StnPerfSeven_DayValue_Adapter adapter_7days;

    String DayCnt, Network;
    LinearLayout layone, layseven, tstlay;
    TextView hdrnetwrks;
    HorizontalScrollView viewseven;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_all_stn_performance_list);

        init();

        setListeners();
    }

    public void init(){
        parent = AllStnPerformanceList.this;

        listperformance = (ListView)findViewById(R.id.listperformance);
        mRefresh = (ImageView) findViewById(com.stavigilmonitoring.R.id.imgbtnrfrsh);
        mprogress = (ProgressBar) findViewById(com.stavigilmonitoring.R.id.progressBar1);

        layone = (LinearLayout)findViewById(R.id.layone);
        layseven = (LinearLayout)findViewById(R.id.layseven);
        viewseven = (HorizontalScrollView)findViewById(R.id.viewseven);
        tstlay = (LinearLayout)findViewById(R.id.tstlay);

        hdrnetwrks = (TextView)findViewById(R.id.hdrnetwrks);

        Intent intent = getIntent();
        DayCnt = intent.getStringExtra("DayCnt");
        Network = intent.getStringExtra("Network");

        hdrnetwrks.setText("Station Performance - "+Network);

        if(DayCnt.equalsIgnoreCase("1")){
            layone.setVisibility(View.VISIBLE);
            viewseven.setVisibility(View.GONE);
        }else if(DayCnt.equalsIgnoreCase("7")) {
            layone.setVisibility(View.GONE);
            viewseven.setVisibility(View.VISIBLE);
        }

        ut = new utility();
        db = new DatabaseHandler(getBaseContext());
        sqlDb = db.getWritableDatabase();

        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
        dbi.Close();

        arraylistStations = new ArrayList<StationPerformance>();

    }

    public void setListeners(){
        tstlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllStnPerformanceList.this,
                        StationPerformanceDetails.class);
                intent.putExtra("Network",Network);
                startActivity(intent);
            }
        });
    }
}
