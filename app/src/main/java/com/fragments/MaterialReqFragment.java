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
import com.stavigilmonitoring.R;

public class MaterialReqFragment extends Fragment {
    Button btnmaterialrequest, btnMyOrder, btnMaterialPending,btnMaterialPendingDispatches, btnMaterialReceivedConfirmation,
            btnMaterialDispatchedbutnotReceived,btnMaterialRejected;
    ImageView imgnotdisp,imgpending;

    public MaterialReqFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_material_req, container, false);

        btnmaterialrequest = view.findViewById(R.id.btnMaterialRequest);
        btnMyOrder = view.findViewById(R.id.btnMyOrder);
        btnMaterialPendingDispatches = view.findViewById(R.id.btnMaterialPendingDispatches);
        btnMaterialPending = view.findViewById(R.id.btnMaterialPending);
        btnMaterialReceivedConfirmation = view.findViewById(R.id.btnMaterialReceivedConfirmation);
        btnMaterialDispatchedbutnotReceived = view.findViewById(R.id.btnMaterialDispatchedbutnotReceived);
        btnMaterialRejected = view.findViewById(R.id.btnMaterialRejected);
        imgpending = view.findViewById(R.id.imgpending);
        Animation hrtbeat = AnimationUtils.loadAnimation(getContext(), R.anim.heartbeat);
        imgpending.startAnimation(hrtbeat);
        imgnotdisp = view.findViewById(R.id.imgnotdisp);
        imgnotdisp.startAnimation(hrtbeat);

        setListeners();

        return  view;
    }

    public void setListeners(){
        btnmaterialrequest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getContext(),	MaterialRequest.class);
                startActivity(intent);
                //getActivity().finish();
            }
        });

        btnMyOrder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getContext(), MaterialReqAckList.class);
                startActivity(intent);
                //getActivity().finish();
            }
        });

        btnMaterialPendingDispatches.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getContext(), MaterialDeliveredListActivity.class);
                startActivity(intent);
                //getActivity().finish();
            }
        });

        btnMaterialPending.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getContext(), MaterialReqPendingList.class);
                startActivity(intent);
                //getActivity().finish();
            }
        });

        btnMaterialReceivedConfirmation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getContext(), MaterialReceivedConfirmation.class);
                startActivity(intent);
                //getActivity().finish();
            }
        });

        btnMaterialDispatchedbutnotReceived.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getContext(), MaterialDispatchedbutnotReceived.class);
                startActivity(intent);
                //getActivity().finish();
            }
        });

        btnMaterialRejected.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getContext(), MaterialRejectedOrders.class);
                startActivity(intent);
                //getActivity().finish();
            }
        });
    }

}
