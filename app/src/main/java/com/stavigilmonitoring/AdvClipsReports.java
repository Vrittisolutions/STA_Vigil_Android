package com.stavigilmonitoring;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

public class AdvClipsReports extends Activity {
    private Context parent;
    LinearLayout frstplayrprt, rprtdtls, stationdaycnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_adv_clips_reports);

        init();

        SetListeners();

    }

    public void init(){
        parent = AdvClipsReports.this;

        frstplayrprt = (LinearLayout)findViewById(R.id.frstplayrprt);
        rprtdtls = (LinearLayout)findViewById(R.id.rprtdtls);
        stationdaycnt = (LinearLayout)findViewById(R.id.stationdaycnt);

    }

    public void SetListeners(){

        frstplayrprt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AdvClipsReports.this, AdvFirstPlayReport_NetworkActivity.class);
                intent.putExtra("CallFrom","FirstPlayReportTab");
                startActivity(intent);

            }
        });

        rprtdtls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AdvClipsReports.this, AdvFirstPlayReport_NetworkActivity.class);
                intent.putExtra("CallFrom","TimingDetailsReportTab");
                startActivity(intent);

            }
        });

        stationdaycnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AdvClipsReports.this, AdvFirstPlayReport_NetworkActivity.class);
                intent.putExtra("CallFrom","DayCountStationwiseTab");
                startActivity(intent);

            }
        });

    }
}
