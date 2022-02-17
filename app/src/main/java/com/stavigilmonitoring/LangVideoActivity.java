package com.stavigilmonitoring;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

public class LangVideoActivity extends Activity {
    Context parent;
    LinearLayout laymarathi,layhindi,layenglish,laypunjabi,laykannada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_lang_video);

        init();

        setListeners();
    }

    public void init(){
        parent = LangVideoActivity.this;
        laymarathi = findViewById(R.id.laymarathi);
        layhindi = findViewById(R.id.layhindi);
        layenglish = findViewById(R.id.layenglish);
        laypunjabi = findViewById(R.id.laypunjabi);
        laykannada = findViewById(R.id.laykannada);
    }

    public void setListeners(){

        laymarathi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LangVideoActivity.this, VideoListActivity.class);
                intent.putExtra("LangCode","Marathi");
                startActivity(intent);
            }
        });

        layhindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LangVideoActivity.this, VideoListActivity.class);
                intent.putExtra("LangCode","Hindi");
                startActivity(intent);
            }
        });

        layenglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LangVideoActivity.this, VideoListActivity.class);
                intent.putExtra("LangCode","English");
                startActivity(intent);
            }
        });

        laypunjabi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LangVideoActivity.this, VideoListActivity.class);
                intent.putExtra("LangCode","Punjabi");
                startActivity(intent);
            }
        });

        laykannada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LangVideoActivity.this, VideoListActivity.class);
                intent.putExtra("LangCode","Kannada");
                startActivity(intent);
            }
        });

    }
}
