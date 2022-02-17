package com.stavigilmonitoring;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.database.DBInterface;
import com.fragments.DispatchMaterialFragment;
import com.fragments.MaterialReqFragment;
import com.fragments.ScrapFragment;
import com.fragments.StationInvFragment;
import com.fragments.WarehouseFragment;

import java.util.ArrayList;
import java.util.List;

public class NewMaterialRequirementActivity extends AppCompatActivity {
    private Context parent;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    String Position;
    private String mType, mobno, InstallationID1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_material_requirement);

        init();

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void init(){
        parent = NewMaterialRequirementActivity.this;

        DBInterface dbi = new DBInterface(getApplicationContext());
        mobno = dbi.GetPhno();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MaterialReqFragment(), "Requested Materials");

       /* if(mobno.equalsIgnoreCase("9561068567")){
            adapter.addFragment(new MaterialReqFragment(), "Requested Materials");
            //adapter.addFragment(new DispatchMaterialFragment(), "Dispatched Materials");
            //adapter.addFragment(new WarehouseFragment(), "Warehouse Materials");
            //adapter.addFragment(new ScrapFragment(), "Scrap Materials");
        }else {
            //adapter.addFragment(new MaterialReqFragment(), "Requested Materials");
            //adapter.addFragment(new DispatchMaterialFragment(), "Dispatched Materials");
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

}
