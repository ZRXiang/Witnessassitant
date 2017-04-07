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
import android.widget.Toast;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.adpter.EntryCheckFragmentPagerAdapter;
import com.example.phobes.witnessassitant.fragment.EntryCheckTaskFragment;
import com.example.phobes.witnessassitant.fragment.EntryCheckTaskListDetailFragment;

public class EntryCheckMainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    private EntryCheckFragmentPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    Toolbar toolbar;
    //cy
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int nPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_witeness_main);
        sharedPreferences = getSharedPreferences("nPage", 0);

        editor = sharedPreferences.edit();
        editor.putInt("npage", 1);
        editor.commit();
        findViewById();
        addEvent();
        initView();


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
                if (position!=0){
                    gMenuItem.setVisible(false);
                    uMenuItem.setVisible(false);
                    dMenuItem.setVisible(false);
                }else{
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
            toolbar.setTitle("进场验收");
        }
        pagerAdapter = new EntryCheckFragmentPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    //cy

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        String msg = "";
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

        if (!msg.equals("")) {
            Toast.makeText(EntryCheckMainActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    //cy
    Handler mHandler;
    MenuItem gMenuItem = null;
    MenuItem uMenuItem = null;
    MenuItem dMenuItem = null;

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    public void sendmsg() {
        Message message = new Message();
        message.obj = nPage;
        message.what = 1;
        mHandler.sendMessage(message);
    }
//    @Override
//    public void sendMessageValue(int msgValue) {
//        EntryCheckTaskListDetailFragment fg2=new EntryCheckTaskListDetailFragment();
//        Bundle bundle=new Bundle();
//        bundle.putInt("mes",msgValue);
//        fg2.setArguments(bundle);
////        FragmentTransaction shiwu = getSupportFragmentManager().beginTransaction();
////        shiwu.replace(R.id.fg2,fg2);
////        shiwu.commit();
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuentrycheckmain, menu);
        gMenuItem = menu.findItem(R.id.action_page);
        uMenuItem = menu.findItem(R.id.action_page_up);
        dMenuItem = menu.findItem(R.id.action_page_down);
        return true;
    }
}
