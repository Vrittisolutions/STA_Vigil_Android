package com.stavigilmonitoring;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import com.adapters.DispMaterialAdapter;

import java.util.ArrayList;

public class MyDispatchOrders extends AppCompatActivity {
    private Context parent;
    ListView material_dispatch;

    ArrayList<String> listDisp;
    DispMaterialAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_dispatch_orders);

        init();

        setListeners();

    }

    public void init(){
        parent = MyDispatchOrders.this;

        material_dispatch = findViewById(R.id.material_dispatch);

        listDisp = new ArrayList<String>();
        adapter = new DispMaterialAdapter(MyDispatchOrders.this,listDisp);
        material_dispatch.setAdapter(adapter);

    }

    public void setListeners(){

    }
}
