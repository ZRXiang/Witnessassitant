package com.example.phobes.witnessassitant.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.adpter.WitnessFragmentPagerAdapter;

/**
 * Created by phobes on 2016/6/7.
 */
public class WitnessMainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    String operation;
    String type;

    private WitnessFragmentPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_witeness_main);
        operation = getIntent().getStringExtra("operation");
        type = getIntent().getStringExtra("type");
        findViewById();
        addEvent();
        initView();
        sharedPreferences = getSharedPreferences("nPage", 0);
        editor = sharedPreferences.edit();
        editor.putInt("npage", 1);
        editor.commit();
    }

    private void findViewById() {
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.witness_tabs);
    }

    private void addEvent() {
        setSupportActionBar(toolbar);
//        if (sharedPreferences.getBoolean("visible", true)) {
//
//
//        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setOnMenuItemClickListener(this);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position != 0) {
                    gMenuItem.setVisible(false);
                    uMenuItem.setVisible(false);
                    dMenuItem.setVisible(false);
                } else {
                    gMenuItem.setVisible(true);
                    uMenuItem.setVisible(true);
                    dMenuItem.setVisible(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initView() {
        if (toolbar != null) {
            if (operation.equals("sampleWitness")) {
                toolbar.setTitle("取样见证");
            } else if (operation.equals("testWitness")) {
                toolbar.setTitle("试验见证");
            }
        }
        pagerAdapter = new WitnessFragmentPagerAdapter(getSupportFragmentManager(), this);
        pagerAdapter.setType(type);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    Handler mHandler;

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    public void sendmsg() {
        Message message = new Message();
        message.obj = nPage;
        message.what = 1;
        mHandler.sendMessage(message);
    }

    int nPage = 1;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    MenuItem gMenuItem = null;
    MenuItem uMenuItem = null;
    MenuItem dMenuItem = null;


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_page_up:
                if (sharedPreferences.getBoolean("bHasDate", true)) {

                    nPage -= 1;
                    if (nPage < 1) {
                        nPage = 1;
                    }
                    gMenuItem.setTitle("第" + nPage + "页");
                    sendmsg();

                }
                editor.putBoolean("bHasDate", true);
                editor.putInt("npage", nPage);
                editor.commit();
                break;
            case R.id.action_page_down:
                if (sharedPreferences.getBoolean("bHasDate", true)) {

                    nPage += 1;
                    gMenuItem.setTitle("第" + nPage + "页");
                    sendmsg();
                }
                editor.putInt("npage", nPage);
                editor.commit();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuentrycheckmain, menu);
        gMenuItem = menu.findItem(R.id.action_page);
        uMenuItem = menu.findItem(R.id.action_page_up);
        dMenuItem = menu.findItem(R.id.action_page_down);
        return true;
    }

}

