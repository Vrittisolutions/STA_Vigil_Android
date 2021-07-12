package com.stavigilmonitoring;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class SupporterEnquiry_New extends Activity {
    private Context parent;
    private ImageView mRefresh;
    private ProgressBar mProgressBar;
    LinearLayout supenq_stnwise, supenq_supwise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.stavigilmonitoring.R.layout.activity_supporter_enquiry__new);

        Initialize();

        SetListeners();
    }

    public void Initialize(){
        parent = SupporterEnquiry_New.this;

        mRefresh = (ImageView)findViewById(com.stavigilmonitoring.R.id.button_refresh_suppEnq);
        supenq_stnwise = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.supenq_stnwise);
        supenq_supwise = (LinearLayout)findViewById(com.stavigilmonitoring.R.id.supenq_supwise);
    }

    public void SetListeners(){

        supenq_stnwise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(parent,"Station wise clicked",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),SupporterEnquiryStatewise.class);
                intent.putExtra("SupEnqKey","supenq_STNWISE");
                startActivity(intent);
            }
        });

        supenq_supwise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(parent,"Supporter wise clicked",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),SupporterEnquiryStatewise.class);
                intent.putExtra("SupEnqKey","supenq_SUPWISE");
                startActivity(intent);
            }
        });
    }
}
