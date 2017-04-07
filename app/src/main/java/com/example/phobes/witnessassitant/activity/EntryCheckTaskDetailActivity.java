package com.example.phobes.witnessassitant.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.fragment.EntryCheckTaskItemDetailFragment;

/**
 * Created by phobes on 2016/6/27.
 *
 */
public class EntryCheckTaskDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_check_task_item_detail);
        findViewById();
        addEvent();
        initView();
    }

    private void findViewById() {
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
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
            toolbar.setTitle("进场验收任务");
        }
        Bundle arguments = new Bundle();
        arguments.putInt(EntryCheckTaskItemDetailFragment.ARG_ENTRY_ID,
                getIntent().getIntExtra(EntryCheckTaskItemDetailFragment.ARG_ENTRY_ID, 0));
        EntryCheckTaskItemDetailFragment fragment = new EntryCheckTaskItemDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.entry_task_detail_container, fragment)
                .commit();
    }
}
