package com.example.phobes.witnessassitant.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.adpter.SiteTestFragmentPagerAdapter;
import com.example.phobes.witnessassitant.fragment.SiteTestHistory;
import com.example.phobes.witnessassitant.service.ListenerThread;
import com.example.phobes.witnessassitant.service.ZJSiteFileUpload;;

public class SiteTestMainActivity extends AppCompatActivity {

    private SiteTestFragmentPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    Toolbar toolbar;
    ListenerThread listenerThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_witeness_main);
        findViewById();
        addEvent();
        initView();
      //  listenerStart();

    }


    private void listenerStart(){
         listenerThread=new ListenerThread();
        listenerThread.start();
    }

    private void findViewById() {
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.witness_tabs);
     /*   viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

           @Override
            public void onPageScrollStateChanged(int state) {
              SiteTestHistory siteTestHistory=new  SiteTestHistory();
              siteTestHistory.updateSiteTestHistory();
            }
        });*/
    }

    private void addEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initView() {
        if (toolbar != null) {
            toolbar.setTitle("现场监测采集");
        }
        pagerAdapter = new SiteTestFragmentPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    @Override
    public void onBackPressed() {
        if(listenerThread!=null) {
            listenerThread.close();
        }

        super.onBackPressed();
    }
}
