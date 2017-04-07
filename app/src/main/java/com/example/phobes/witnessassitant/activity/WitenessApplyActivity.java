package com.example.phobes.witnessassitant.activity;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.fragment.WitenessApplyDetailFragment;

/**
 * Created by phobes on 2016/6/7.
 */
public class WitenessApplyActivity extends AppCompatActivity {
    private int object_id = 0;

    private int witness_id = 0;

    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_witeness_apply_detail);
        object_id = getIntent().getIntExtra(WitenessApplyDetailFragment.ARG_ITEM_ID, 0);
        witness_id = getIntent().getIntExtra(WitenessApplyDetailFragment.ARG_WITNESS_ID, 0);
        findViewById();
        addEvent();
        initView();

        Bundle arguments = new Bundle();
        arguments.putInt(WitenessApplyDetailFragment.ARG_ITEM_ID,
                getIntent().getIntExtra(WitenessApplyDetailFragment.ARG_ITEM_ID, 0));
        arguments.putInt(WitenessApplyDetailFragment.ARG_WITNESS_ID,
                getIntent().getIntExtra(WitenessApplyDetailFragment.ARG_WITNESS_ID, 0));
        arguments.putString("type", getIntent().getStringExtra("type"));
        WitenessApplyDetailFragment fragment = new WitenessApplyDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.witeness_apply_detail_container, fragment)
                .commit();

    }

    private void findViewById() {
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
    }

    private void addEvent() {
//        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
//        toolbar.setOnMenuItemClickListener(this);
    }

    private void initView() {
        if (toolbar != null) {
            toolbar.setTitle("见证任务");
        }
    }




//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menuentrycheckmain, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onMenuItemClick(MenuItem item) {
//        return false;
//    }
}
