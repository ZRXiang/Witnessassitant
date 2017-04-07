package com.example.phobes.witnessassitant.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.fragment.SampleGuildItemDetailFragment;


public class SampleGuildItemDetailActivity extends AppCompatActivity {
    private int sample_guild_id = 0;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_guild_detail);
        findViewById();
        initView();
        addEvent();
        sample_guild_id = getIntent().getIntExtra(SampleGuildItemDetailFragment.ARG_ITEM_ID, 0);
        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putInt(SampleGuildItemDetailFragment.ARG_ITEM_ID,
                    getIntent().getIntExtra(SampleGuildItemDetailFragment.ARG_ITEM_ID, 0));
            SampleGuildItemDetailFragment fragment = new SampleGuildItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.sample_guild_item_detail_container, fragment)
                    .commit();
        }
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
            toolbar.setTitle(SampleGuildItemDetailActivity.this.getResources().getString(R.string.sample_guild));
        }
    }


}
