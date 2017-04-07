package com.example.phobes.witnessassitant.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.fragment.EntryCheckFragment;

/**
 * Created by phobes on 2016/6/27.
 *
 */
public class EntryCheckActivity extends AppCompatActivity {
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_check);
        findViewById();
        initView();
        addEvent();
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putInt(EntryCheckFragment.ARG_ENTRY_ID,
                    getIntent().getIntExtra(EntryCheckFragment.ARG_ENTRY_ID,0));
            EntryCheckFragment fragment = new EntryCheckFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.entry_check_container, fragment)
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
            toolbar.setTitle("进场验收");
        }
    }
}