package com.example.phobes.witnessassitant.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.fragment.WitenessApplyDetailFragment;
import com.example.phobes.witnessassitant.fragment.WitenessFragment;

/**
 * Created by phobes on 2016/6/3.
 */
public class WitenessActivity extends AppCompatActivity {
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_witeness);
        findViewById();
        initView();
        addEvent();
        int witeness_id=getIntent().getIntExtra(WitenessApplyDetailFragment.ARG_ITEM_ID,0);
        String witness_type = getIntent().getStringExtra(WitenessFragment.WITNESS_TYPE);
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putInt(WitenessFragment.ARG_ITEM_ID, getIntent().getIntExtra(WitenessFragment.ARG_ITEM_ID,0));
            arguments.putString(WitenessFragment.WITNESS_TYPE,getIntent().getStringExtra(WitenessFragment.WITNESS_TYPE));
            WitenessFragment fragment = new WitenessFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.witeness_container, fragment)
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
            toolbar.setTitle("见证");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuentrycheckmain, menu);
        return true;
    }

}

