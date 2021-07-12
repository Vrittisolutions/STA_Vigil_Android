package com.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.stavigilmonitoring.MaterialDeliveredListActivity;
import com.stavigilmonitoring.MaterialDispatchedbutnotReceived;
import com.stavigilmonitoring.MaterialReceivedConfirmation;
import com.stavigilmonitoring.MaterialRejectedOrders;
import com.stavigilmonitoring.MaterialReqAckList;
import com.stavigilmonitoring.MaterialReqPendingList;
import com.stavigilmonitoring.MaterialRequest;
import com.stavigilmonitoring.MyDispatchOrders;
import com.stavigilmonitoring.R;

public class DispatchMaterialFragment extends Fragment {
    Button btnmydisporders,btnmatdispatches,btnscrappedmaterials,btnmaterialrepaired,btndispconfirmation, btndispnotrcvd;
    ImageView imgnotdisp,imgpending;

    public DispatchMaterialFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dispatch_material, container, false);

        btnmatdispatches = view.findViewById(R.id.btnmatdispatches);
        btnmydisporders = view.findViewById(R.id.btnmydisporders);
        btnscrappedmaterials = view.findViewById(R.id.btnscrappedmaterials);
        btnmaterialrepaired= view.findViewById(R.id.btnmaterialrepaired);
        btndispconfirmation = view.findViewById(R.id.btndispconfirmation);
        btndispnotrcvd = view.findViewById(R.id.btndispnotrcvd);
        imgpending = view.findViewById(R.id.imgpending);
        Animation hrtbeat = AnimationUtils.loadAnimation(getContext(), R.anim.heartbeat);
        imgpending.startAnimation(hrtbeat);
        imgnotdisp = view.findViewById(R.id.imgnotdisp);
        imgnotdisp.startAnimation(hrtbeat);

        setListeners();

        return  view;
    }

    public void setListeners(){
        btnmydisporders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyDispatchOrders.class);
                startActivity(intent);
            }
        });

    }

}
