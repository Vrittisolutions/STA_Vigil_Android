package com.stavigilmonitoring;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.fragments.NotifyTypewiseFragment;
import com.fragments.RecentNotificationsFragment;
import com.fragments.StationInvFragment;
import com.fragments.StationwiseNotifyFragment;

import java.util.ArrayList;
import java.util.List;

public class Inventory_WH_Stock_StationActivity extends AppCompatActivity {
    private Context parent;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    String Position;
    private String mType, mobno, InstallationID1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_inventory__wh__stock__station);

        init();

        Intent i = getIntent();
        mType = i.getStringExtra("Type");
        InstallationID1 = i.getStringExtra("InstallationId");
        mobno = i.getStringExtra("mobno");
        Log.e("Type", mType);
        //tvhead.setText("Station Inventory - " + mType);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        /*if(mobno.equalsIgnoreCase("9561068567")){
            tabLayout.getTabAt(0).getIcon().setAlpha(255);
            tabLayout.getTabAt(1).getIcon().setAlpha(128);
            tabLayout.getTabAt(2).getIcon().setAlpha(128);
            tabLayout.getTabAt(3).getIcon().setAlpha(128);
        }else {
            tabLayout.getTabAt(1).getIcon().setAlpha(255);
            tabLayout.getTabAt(2).getIcon().setAlpha(128);
        }*/

       /* viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        tabLayout.getTabAt(0).getIcon().setAlpha(255);
                        tabLayout.getTabAt(1).getIcon().setAlpha(128);
                        tabLayout.getTabAt(2).getIcon().setAlpha(128);
                        tabLayout.getTabAt(3).getIcon().setAlpha(128);
                        break;
                    case 1:
                        tabLayout.getTabAt(0).getIcon().setAlpha(128);
                        tabLayout.getTabAt(1).getIcon().setAlpha(255);
                        tabLayout.getTabAt(2).getIcon().setAlpha(128);
                        tabLayout.getTabAt(3).getIcon().setAlpha(128);
                        break;
                    case 2:
                        tabLayout.getTabAt(0).getIcon().setAlpha(128);
                        tabLayout.getTabAt(1).getIcon().setAlpha(128);
                        tabLayout.getTabAt(2).getIcon().setAlpha(255);
                        tabLayout.getTabAt(3).getIcon().setAlpha(128);
                        break;
                    case 3:
                        tabLayout.getTabAt(0).getIcon().setAlpha(128);
                        tabLayout.getTabAt(1).getIcon().setAlpha(128);
                        tabLayout.getTabAt(2).getIcon().setAlpha(128);
                        tabLayout.getTabAt(3).getIcon().setAlpha(255);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/
    }

    public void init(){
        parent = Inventory_WH_Stock_StationActivity.this;
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.station_100);
      /*  if(mobno.equalsIgnoreCase("9561068567")){
            tabLayout.getTabAt(0).setIcon(R.drawable.wh_100);
            tabLayout.getTabAt(1).setIcon(R.drawable.station_100);
            tabLayout.getTabAt(2).setIcon(R.drawable.stock_100);
            tabLayout.getTabAt(3).setIcon(R.drawable.scrap_100);
        }else {
            tabLayout.getTabAt(1).setIcon(R.drawable.station_100);
            tabLayout.getTabAt(2).setIcon(R.drawable.stock_100);
        }*/
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new StationInvFragment(), "Station Inventory");

       /* if(mobno.equalsIgnoreCase("9561068567")){
            adapter.addFragment(new StationInvFragment(), "Pune WH Inventory");
            adapter.addFragment(new StationInvFragment(), "Station Inventory");
            adapter.addFragment(new StationInvFragment(), "Employee Stock Inventory");
            adapter.addFragment(new StationInvFragment(), "Scrap Inventory");
        }else {
            adapter.addFragment(new StationInvFragment(), "Station Inventory");
            adapter.addFragment(new StationInvFragment(), "Employee Stock Inventory");
        }*/
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public String GetData(){
        return mType+","+InstallationID1;
    }

}
