package com.vritti.orderbilling.customer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import com.vritti.orderbilling.R;
import com.vritti.orderbilling.data.*;
import com.vritti.orderbilling.database.*;
import com.vritti.orderbilling.beans.*;
import com.vritti.orderbilling.utils.*;

import java.util.ArrayList;

public class MyCartActivity extends AppCompatActivity {
	private Context parent;
	private SearchView searchView;
	private ImageView imageviewMenu;
	private TextView textviewTotalAmount, textviewHeading;
	private LinearLayout containerLayout;
	private Button placeOrderButton;
	// private double totalAmount = 0;
	RelativeLayout cartIconLayout;
	DatabaseHelper databaseHelper;
	private String mActivityTitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cart);
		/*android.support.v7.app.ActionBar actionBar=getSupportActionBar();
		actionBar.setTitle("My Cart");
		getSupportActionBar().show();*/
		getSupportActionBar().setTitle("My Cart");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
	//	getSupportActionBar().setIcon(R.drawable.logo);

		initViews();
		showCartItems();
		setListeners();
	}

	private void initViews() {
		parent = MyCartActivity.this;
		textviewTotalAmount = (TextView) findViewById(R.id.textview_cart_total_amount);
		//textviewHeading = (TextView) findViewById(R.id.textview_topbar_heading);
		//textviewHeading.setText(KukadiVegetablesStringConstants.MY_CART);
		//imageviewMenu = (ImageView) findViewById(R.id.imageview_topbar_overflow);
		/*searchView = (SearchView) findViewById(R.id.searchview_topbar);
		searchView.setVisibility(View.GONE);*/

		placeOrderButton = (Button) findViewById(R.id.button_my_cart_proceed);

		containerLayout = (LinearLayout) findViewById(R.id.linearlayout_cart_container);

		/*cartIconLayout = (RelativeLayout) findViewById(R.id.relative_cart);
		cartIconLayout.setVisibility(View.GONE);*/

		if (KukadiVegetableData.myCartList.size() == 0) {
			onBackPressed();
		}
	}

	private void setListeners() {
		/*imageviewMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utilities.showListDialog(parent, imageviewMenu);
			}
		});*/
		placeOrderButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveOrder();
			}
		});
	}

	private void saveOrder() {
		databaseHelper = new DatabaseHelper(parent);
		databaseHelper.addMyOrdersSummary(Utilities.getCurrentDateTime(),
				Double.toString(KukadiVegetableData.totalAmount), null, null,
				null);

		SQLiteDatabase sql = databaseHelper.getWritableDatabase();

		Cursor cursor = sql.rawQuery(
				"SELECT MAX(OrderId) FROM MyOrdersSummary", null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			KukadiVegetableData.currentOrderId = cursor.getInt(0);

		} else {

		}

		for (int i = 0; i < KukadiVegetableData.myCartList.size(); i++) {
			databaseHelper.addMyOrders(KukadiVegetableData.myCartList.get(i)
					.getProductId(), KukadiVegetableData.currentOrderId,
					KukadiVegetableData.myCartList.get(i).getProductName(),
					Double.toString(KukadiVegetableData.myCartList.get(i)
							.getProductRate()), KukadiVegetableData.myCartList
							.get(i).getProductQuantity());
		}
		KukadiVegetableData.myCartList = new ArrayList<MyCartBean>();
		Toast.makeText(parent, "Order Saved Successfully", Toast.LENGTH_LONG)
				.show();
		/*if ((KukadiVegetableData.selectedAgencyId != null)
				&& (KukadiVegetableData.selectedAgencyName != null)) {*/
			startActivity(new Intent(parent, CheckoutActivity.class));
		/*} else {
			startActivityForResult(new Intent(parent,
							CheckoutActivity.class),
					KukadiVegetableData.RESULT_AGENCY_SELECTION);
		}*/
	}

	private void showCartItems() {
		if(KukadiVegetableData.myCartList.size()>0) {
			for (int i = 0; i < KukadiVegetableData.myCartList.size(); i++) {
				addView(i);
			}
		}
		else{

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ((requestCode == KukadiVegetableData.RESULT_AGENCY_SELECTION)
				&& (data != null)) {
			if (data.getBooleanExtra(KukadiVegetableData.IS_AGENCY, false))
				startActivity(new Intent(parent, OrderSummaryActivity.class));
		}
	}

	private void addView(int i) {
		LayoutInflater layoutInflater = (LayoutInflater) parent
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View baseView = layoutInflater.inflate(R.layout.custom_cart_row,
				null);
		TextView textviewProductName = (TextView) baseView
				.findViewById(R.id.textview_cart_product_name);
	/*	TextView textviewProductRate = (TextView) baseView
				.findViewById(R.id.textview_cart_product_rate);*/
		final TextView textviewProductAmount = (TextView) baseView
				.findViewById(R.id.textview_cart_product_amount);
		ImageView imageviewProductImage = (ImageView) baseView
				.findViewById(R.id.imageview_cart_product_image);
		ImageView imageviewRemoveProduct = (ImageView) baseView
				.findViewById(R.id.imageview_cart_remove);

		final EditText edittextQuantity = (EditText) baseView
				.findViewById(R.id.edittext_cart_product_qty);
		edittextQuantity.setTag(containerLayout.getChildCount());
		edittextQuantity.setText(Integer
				.toString(KukadiVegetableData.myCartList.get(i)
						.getProductQuantity()));
		edittextQuantity.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				int pos = Integer
						.parseInt(edittextQuantity.getTag().toString());
				if (!((s.toString().trim() == "") || (s.toString() == null) || (s
						.toString().length() == 0))) {
					KukadiVegetableData.myCartList.get(pos).setProductQuantity(
							Integer.parseInt(s.toString()));
					KukadiVegetableData.myCartList.get(pos).setProductAmount(
							Integer.parseInt(s.toString())
									* (KukadiVegetableData.myCartList.get(pos)
											.getProductRate()));
				} else {
					KukadiVegetableData.myCartList.get(pos).setProductQuantity(
							Integer.parseInt("0"));
					KukadiVegetableData.myCartList.get(pos).setProductAmount(
							Double.parseDouble("0"));
				}
				textviewProductAmount.setText(Double
						.toString(KukadiVegetableData.myCartList.get(pos)
								.getProductAmount()));

				showTotal();
			}
		});

		textviewProductName.setText(KukadiVegetableData.myCartList.get(i)
				.getProductName());
		/*textviewProductRate.setText(Double
				.toString(KukadiVegetableData.myCartList.get(i)
						.getProductRate()));*/
		textviewProductAmount.setText(Double
				.toString(KukadiVegetableData.myCartList.get(i)
						.getProductAmount()));

		Picasso.with(parent)
				.load(KukadiVegetableData.myCartList.get(i)
						.getProductImageLink())
				.placeholder(R.drawable.ic_vegetable)
				.error(R.drawable.ic_vegetable).into(imageviewProductImage);

		// imageviewProductImage.setImageDrawable(parent.getResources()
		// .getDrawable(R.drawable.ic_vegetable));
		imageviewRemoveProduct.setTag(containerLayout.getChildCount());

		imageviewRemoveProduct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {

				new AlertDialog.Builder(parent)
						.setTitle(KukadiVegetablesStringConstants.APP_TITLE)
						.setMessage(
								KukadiVegetablesStringConstants.MSG_MY_CART_REMOVE)
						.setIcon(R.drawable.delete)
						.setPositiveButton(android.R.string.yes,
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int whichButton) {

										int index = Integer.parseInt(v.getTag()
												.toString().trim());
										((LinearLayout) baseView.getParent())
												.removeView(baseView);
										KukadiVegetableData.myCartList
												.remove(index);

										if (KukadiVegetableData.myCartList
												.size() == 0) {
											// KukadiVegetableData.myCartList =
											// new ArrayList<MyCartBean>();
											Toast.makeText(
													parent,
													KukadiVegetablesStringConstants.MSG_NO_ITEM_IN_CART,
													Toast.LENGTH_LONG).show();
											onBackPressed();
										}
										for (int i = 0; i < containerLayout
												.getChildCount(); i++) {

											View view = containerLayout
													.getChildAt(i);
											ImageView iv = (ImageView) view
													.findViewById(R.id.imageview_cart_remove);
											EditText edittextQty = (EditText) view
													.findViewById(R.id.edittext_cart_product_qty);
											iv.setTag(i);
											edittextQty.setTag(i);
										}
										showTotal();
									}
								}).setNegativeButton(android.R.string.no, null)
						.show();
			}
		});
		showTotal();
		containerLayout.addView(baseView);
	}

	private void showTotal() {
		KukadiVegetableData.totalAmount = 0;
		for (int i = 0; i < KukadiVegetableData.myCartList.size(); i++) {
			KukadiVegetableData.totalAmount += KukadiVegetableData.myCartList
					.get(i).getProductAmount();
		}
		textviewTotalAmount.setText("Amount Rs. "
				+ KukadiVegetableData.totalAmount);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();


		switch (id) {
			case R.id.search:
				break;
			case R.id.notification:
				break;
			case android.R.id.home:
				finish();
				return true;
			default:
				break;
		}


		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onBackPressed() {
		finish();
	}
}