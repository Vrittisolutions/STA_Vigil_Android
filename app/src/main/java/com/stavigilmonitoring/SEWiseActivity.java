package com.stavigilmonitoring;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

public class SEWiseActivity extends AppCompatActivity {
    private Context parent;
    LinearLayout lay_assign,lay_history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sewise);

        init();

        setListeners();
    }

    public void init(){
        parent = SEWiseActivity.this;

        lay_assign = findViewById(R.id.lay_assign);
        lay_history = findViewById(R.id.lay_history);
    }

    public void setListeners(){
        lay_assign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent,SEAssignActivity.class);
                startActivity(intent);
            }
        });

        lay_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent,SEActivitiesHistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
