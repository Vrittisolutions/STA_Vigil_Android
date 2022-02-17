package com.stavigilmonitoring;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

public class StnPerformance_dayselection extends Activity {
    private Context parent;

    private LinearLayout SP_OneDay_Perform,SP_SevenDay_Perform;
    String Netwrk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_stn_performance_dayselection);

        init();

        setListeners();
    }

    public void init(){
        parent = StnPerformance_dayselection.this;

        SP_OneDay_Perform = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.one_day_prfrm);
        SP_SevenDay_Perform = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.seven_day_prfrm);

        Intent intent = getIntent();
        Netwrk = intent.getStringExtra("Network");

    }

    public void setListeners(){
        SP_OneDay_Perform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(parent,"One day clicked",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(StnPerformance_dayselection.this,
                        AllStnPerformanceList.class);
                intent.putExtra("DayCnt","1");
                intent.putExtra("Network",Netwrk);
                startActivity(intent);
            }
        });

        SP_SevenDay_Perform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(parent,"Seven day clicked",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(StnPerformance_dayselection.this,
                        AllStnPerformanceList.class);
                intent.putExtra("DayCnt","7");
                intent.putExtra("Network",Netwrk);
                startActivity(intent);
            }
        });
    }
}
