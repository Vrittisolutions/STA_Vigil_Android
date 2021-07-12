package com.stavigilmonitoring;

import com.stavigilmonitoring.MaterialRequest;
import com.stavigilmonitoring.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class SelectMaterialReqType extends Activity {
	Button btnmaterialrequest, btnMyOrder, btnMaterialPending,btnMaterialPendingDispatches, btnMaterialReceivedConfirmation,
            btnMaterialDispatchedbutnotReceived,btnMaterialRejected;
	ImageView imgnotdisp,imgpending;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activityselectmaterailtreqype);

		btnmaterialrequest = (Button) findViewById(R.id.btnMaterialRequest);
		btnMyOrder=(Button) findViewById(R.id.btnMyOrder);
		btnMaterialPendingDispatches = (Button) findViewById(R.id.btnMaterialPendingDispatches);
		btnMaterialPending = (Button) findViewById(R.id.btnMaterialPending);
		btnMaterialReceivedConfirmation=(Button)findViewById(R.id.btnMaterialReceivedConfirmation);
		btnMaterialDispatchedbutnotReceived=(Button)findViewById(R.id.btnMaterialDispatchedbutnotReceived);
		btnMaterialRejected=(Button)findViewById(R.id.btnMaterialRejected);
		imgpending = findViewById(R.id.imgpending);
		Animation hrtbeat = AnimationUtils.loadAnimation(this, R.anim.heartbeat);
		imgpending.startAnimation(hrtbeat);
		imgnotdisp = findViewById(R.id.imgnotdisp);
		imgnotdisp.startAnimation(hrtbeat);

		btnmaterialrequest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),	MaterialRequest.class);
				startActivity(intent);
				finish();
			}
		});

		btnMyOrder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), MaterialReqAckList.class);
				startActivity(intent);
				finish();
			}
		});

		btnMaterialPendingDispatches.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), MaterialDeliveredListActivity.class);
				startActivity(intent);
				finish();
				
			}
		});

		btnMaterialPending.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), MaterialReqPendingList.class);
				startActivity(intent);
				finish();
				
			}
		});

		btnMaterialReceivedConfirmation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), MaterialReceivedConfirmation.class);
				startActivity(intent);
				finish();
				
			}
		});
		btnMaterialDispatchedbutnotReceived.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), MaterialDispatchedbutnotReceived.class);
				startActivity(intent);
				finish();

			}
		});
		btnMaterialRejected.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), MaterialRejectedOrders.class);
				startActivity(intent);
				finish();
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
}
