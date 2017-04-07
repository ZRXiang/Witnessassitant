package com.example.phobes.witnessassitant.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;


import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.fragment.WitenessListFragment;

public class WitenessListActivity extends AppCompatActivity {
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_witeness_list);
        findViewById();
        addEvent();
        initView();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        WitenessListFragment fragment = new WitenessListFragment();
        fragmentTransaction.replace(R.id.witeness_item_list,fragment);
        fragmentTransaction.commit();
    }
    private void findViewById(){
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
    }
    private void addEvent(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void initView(){
        if (toolbar != null) {
            toolbar.setTitle("见证列表");
        }
    }
}

