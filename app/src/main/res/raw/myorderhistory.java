package com.vritti.orderbilling.customer;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.vritti.orderbilling.R;
import com.vritti.orderbilling.adapters.MyOrderHistoryAdapter;
import com.vritti.orderbilling.beans.OrderHistoryBean;
import com.vritti.orderbilling.data.KukadiVegetableData;
import com.vritti.orderbilling.data.KukadiVegetablesDatabaseConstants;
import com.vritti.orderbilling.database.DatabaseHelper;
import com.vritti.orderbilling.interfaces.CallbackInterface;
import com.vritti.orderbilling.utils.NetworkUtils;
import com.vritti.orderbilling.utils.StartSession;
import com.vritti.orderbilling.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

/**
 * Created by sharvari on 2/25/2016.
 */
public class MyOrderHistory extends AppCompatActivity {
    ListView listview_my_orders_history;
    private Context parent;
    OrderHistoryBean bean;
    private String json;
    ArrayList<OrderHistoryBean> arrayList;
    private DatabaseHelper databaseHelper;
    String Address, City, ConsigneeName, CustomerMasterId, ItemMasterId, Mobile, Qty, Rate, SODate, SOHeaderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        getSupportActionBar().setTitle("My Order History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
      //  getSupportActionBar().setIcon(R.drawable.logo);
        databaseHelper = new DatabaseHelper(this);
     /*   initViews();
        showCartItems();
        setListeners();*/
        parent = MyOrderHistory.this;
        listview_my_orders_history = (ListView) findViewById(R.id.listview_my_orders_history);
        arrayList = new ArrayList<OrderHistoryBean>();
        if (databaseHelper.getOrderHistoryCount() > 0) {
         getDataFromDatabase();

        } else {
            getDataFromServer();
          //  listview_my_orders_history.setAdapter(new MyOrderHistoryAdapter(MyOrderHistory.this, arrayList));
        }


    }


    private void getDataFromDatabase() {
       // ArrayList<OrderHistoryBean> historyBeanArrayList = new ArrayList<OrderHistoryBean>();

        SQLiteDatabase sql = databaseHelper.getWritableDatabase();
        Cursor c = sql.rawQuery(
                "SELECT distinct SOHeaderId FROM " + KukadiVegetablesDatabaseConstants.TABLE_MY_ORDER_HISTORY + " WHERE Mobile ='"
                        + KukadiVegetableData.MOBILE + "' ORDER BY SOHeaderId  ",
                null);
        int ordercnt = 0;
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
//Address, City, ConsigneeName, CustomerMasterId, ItemMasterId, Mobile, Qty, Rate, SODate,
                String orderid = c.getString(c
                        .getColumnIndex("SOHeaderId"));
                ordercnt = ordercnt + 1;
                bean = new OrderHistoryBean();

                bean.setSOHeaderId(Integer.parseInt(orderid)); //String.valueOf(ordercnt)
                SQLiteDatabase sql1 = databaseHelper.getWritableDatabase();
                Cursor c1 = sql1.rawQuery(
                        "SELECT distinct SODate , Rate FROM " + KukadiVegetablesDatabaseConstants.TABLE_MY_ORDER_HISTORY + " WHERE SOHeaderId ='"
                                + orderid + "' ORDER BY SOHeaderId  ",
                        null);
                float amt = 0;
                if (c1.getCount() > 0) {
                    c1.moveToFirst();
                    do {
                        float amtofitem = c1.getFloat(c1.getColumnIndex("Rate"));
                        String o_date = c1.getString(c1.getColumnIndex("SODate"));
                        amt = amt + amtofitem;

                        bean.setSODate(o_date);
                        bean.setRate(String.valueOf(amt));



                    } while (c1.moveToNext());
                }
                arrayList.add(bean);

            } while (c.moveToNext());
        } else {

        }
        listview_my_orders_history.setAdapter(new MyOrderHistoryAdapter(MyOrderHistory.this, arrayList));

    }

    private void getDataFromServer() {
        if (NetworkUtils.isNetworkAvailable(parent)) {
            if ((KukadiVegetableData.SESSION_ID != null)
                    && (KukadiVegetableData.HANDLE != null)) {
                new GetMyOrderHistoryList().execute();
            } else {
                new StartSession(parent, new CallbackInterface() {

                    @Override
                    public void callMethod() {
                        new GetMyOrderHistoryList().execute();
                    }
                });
            }
        } else {
            Toast.makeText(parent, "No internet..", Toast.LENGTH_LONG).show();
        }
    }

    public class GetMyOrderHistoryList extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;
        String responseString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(parent);
            progressDialog.setMessage("Loading Order History");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                SoapObject request = new SoapObject(
                        KukadiVegetableData.NAMESPACE,
                        KukadiVegetableData.METHOD_ORDER_HISTORY);
                PropertyInfo propInfo = new PropertyInfo();
                propInfo.type = PropertyInfo.STRING_CLASS;
                // adding parameters

                request.addProperty("mobileno", KukadiVegetableData.MOBILE);
                request.addProperty("handler", KukadiVegetableData.HANDLE);
                request.addProperty("sessionid", KukadiVegetableData.SESSION_ID);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE androidHttpTransport = new HttpTransportSE(
                        KukadiVegetableData.URL);
                androidHttpTransport.call(
                        KukadiVegetableData.SOAP_ACTION_ORDER_HISTORY,
                        envelope);

                SoapObject response = (SoapObject) envelope.bodyIn;
                responseString = response.getProperty(0).toString();
            } catch (Exception e) {
                responseString = "error";
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (responseString.equalsIgnoreCase("Session Expired")) {
                if (NetworkUtils.isNetworkAvailable(parent)) {
                    new StartSession(parent, new CallbackInterface() {
                        @Override
                        public void callMethod() {
                            new GetMyOrderHistoryList().execute();
                        }
                    });
                }
            } else if (responseString.equalsIgnoreCase("error")) {
                Toast.makeText(parent, "Server Error..", Toast.LENGTH_LONG)
                        .show();
            } else {
                json = responseString;
                parseJson(json);
            }
        }
    }

    protected void parseJson(String json) {
        Utilities.clearTable(parent,
                KukadiVegetablesDatabaseConstants.TABLE_CATEGORY);
        arrayList.clear();
        //  categoryNameList.clear();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
               /* bean = new OrderHistoryBean();

                bean.setSOHeaderId(jsonArray.getJSONObject(i).getString(
                        "SOHeaderId"));
                bean.setSODate(jsonArray.getJSONObject(i).getString(
                        "SODate"));
                bean.setRate(jsonArray.getJSONObject(i).getString(
                        "Rate"));
                bean.setAddress(jsonArray.getJSONObject(i).getString(
                        "Address"));
                bean.setCity(jsonArray.getJSONObject(i).getString(
                        "City"));
                bean.setConsigneeName(jsonArray.getJSONObject(i).getString(
                        "ConsigneeName"));
                bean.setCustomerMasterId(jsonArray.getJSONObject(i).getString(
                        "CustomerMasterId"));
                bean.setItemMasterId(jsonArray.getJSONObject(i).getString(
                        "ItemMasterId"));
                bean.setMobile(jsonArray.getJSONObject(i).getString(
                        "Mobile"));
                bean.setQty(jsonArray.getJSONObject(i).getString(
                        "Qty"));
               *//* bean.setCategoryNameEnglish(jsonArray.getJSONObject(i)
                        .getString("ItmClsDesc"));
                bean.setCategoryNameHindi(null);
                bean.setCategoryNameMarathi(null);*//*
                arrayList.add(bean);*/
                databaseHelper.addOrderHistory(jsonArray.getJSONObject(i).getString(
                        "Address"), jsonArray.getJSONObject(i).getString(
                        "City"), jsonArray.getJSONObject(i).getString(
                        "ConsigneeName"), jsonArray.getJSONObject(i).getString(
                        "CustomerMasterId"), jsonArray.getJSONObject(i).getString(
                        "ItemMasterId"), jsonArray.getJSONObject(i).getString(
                        "Mobile"), jsonArray.getJSONObject(i).getString(
                        "Qty"), jsonArray.getJSONObject(i).getString(
                        "Rate"), jsonArray.getJSONObject(i).getString(
                        "SODate"), Integer.parseInt(jsonArray.getJSONObject(i).getString(
                        "SOHeaderId")));
               /* categoryNameList.add(jsonArray.getJSONObject(i).getString(
                        "ItmClsDesc"));*/
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // showCategories();
        getDataFromDatabase();


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


}
