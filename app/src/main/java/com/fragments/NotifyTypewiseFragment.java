package com.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.stavigilmonitoring.R;
import com.stavigilmonitoring.STNotifyNetworksActivity;

public class NotifyTypewiseFragment extends Fragment {
    LinearLayout lay_advfirstplay, lay_advnotrun, lay_busann, lay_pconoff, lay_soundlevel, lay_tvstatus;

    @SuppressLint("ValidFragment")
    public NotifyTypewiseFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.tbuds_printed_bill);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //   parent = getActivity();

        View view = inflater.inflate(R.layout.fragment_typewise_notify, container, false);
        lay_advfirstplay = (LinearLayout)view.findViewById(R.id.lay_advfirstplay);
        lay_advnotrun = (LinearLayout)view.findViewById(R.id.lay_advnotrun);
        lay_busann = (LinearLayout)view.findViewById(R.id.lay_busann);
        lay_pconoff = (LinearLayout)view.findViewById(R.id.lay_pconoff);
        lay_soundlevel = (LinearLayout)view.findViewById(R.id.lay_soundlevel);
        lay_tvstatus = (LinearLayout)view.findViewById(R.id.lay_tvstatus);

        setListeners();

        return view;
    }

    public void setListeners(){
        lay_advfirstplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), STNotifyNetworksActivity.class);
                intent.putExtra("intentFrom","NotifyTypeWiseNotification");
                intent.putExtra("MsgType","ADVFPLAY");
                startActivity(intent);
            }
        });

        lay_advnotrun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), STNotifyNetworksActivity.class);
                intent.putExtra("intentFrom","NotifyTypeWiseNotification");
                intent.putExtra("MsgType","ADVNOTRUN");
                startActivity(intent);
            }
        });

        lay_busann.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), STNotifyNetworksActivity.class);
                intent.putExtra("intentFrom","NotifyTypeWiseNotification");
                intent.putExtra("MsgType","BUSANN");
                startActivity(intent);
            }
        });

        lay_pconoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), STNotifyNetworksActivity.class);
                intent.putExtra("intentFrom","NotifyTypeWiseNotification");
                intent.putExtra("MsgType","PCONOFF");
                startActivity(intent);
            }
        });

        lay_soundlevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), STNotifyNetworksActivity.class);
                intent.putExtra("intentFrom","NotifyTypeWiseNotification");
                intent.putExtra("MsgType","SL");
                startActivity(intent);
            }
        });

        lay_tvstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), STNotifyNetworksActivity.class);
                intent.putExtra("intentFrom","NotifyTypeWiseNotification");
                intent.putExtra("MsgType","TVSTAT");
                startActivity(intent);
            }
        });
    }
}
