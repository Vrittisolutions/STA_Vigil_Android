package com.stavigilmonitoring;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DownTimeAnalysis extends Activity {
    private Context parent;
    private LinearLayout DT_ThirtyDays_Instance,DT_SevenDays_Instance;
    private TextView txtthirtydays, txtsevendays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.stavigilmonitoring.R.layout.activity_down_time_analysis);

        parent = DownTimeAnalysis.this;

        init();

        setListener();
    }

    public void init(){
        DT_ThirtyDays_Instance = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.dt_thirty_days_layout);
        DT_SevenDays_Instance = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.dt_seven_days_layout);
        //txtthirtydays = (TextView)findViewById(R.id.txtsevendays);
        //txtsevendays = (TextView)findViewById(R.id.txtsevendays);
    }

    public void setListener(){

        DT_ThirtyDays_Instance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(parent,"30 days instance clicked display network codes",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(parent, DTA_Networks.class);
                intent.putExtra("DaysInstKey", "30");
                startActivity(intent);
            }
        });

        DT_SevenDays_Instance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(parent,"7 days instance clicked",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(parent, DTA_Networks.class);
                intent.putExtra("DaysInstKey","7");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*Intent i = new Intent(DownTimeAnalysis.this, SelectMenu.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);*/
        finish();
    }
}
