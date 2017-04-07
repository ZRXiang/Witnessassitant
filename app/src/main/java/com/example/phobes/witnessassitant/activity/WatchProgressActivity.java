package com.example.phobes.witnessassitant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.fragment.MixTaskListFragment;
import com.example.phobes.witnessassitant.fragment.WatchProgressListFragment;

/**
 * Created by YLS on 2016/9/26.
 */
public class WatchProgressActivity extends AppCompatActivity {

    Toolbar toolbar;
    WatchProgressListFragment watchProgressListFragment;
    private String orgId;
    private String taskNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_progress);
        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra("bundle");
        orgId =bundle.getString("orgId");
        taskNumber =bundle.getString("applyNumber");
        initView();
        addEvent();
    }

    private void initView(){
        toolbar= (Toolbar) findViewById(R.id.common_toolbar);
        if(toolbar!=null){
            toolbar.setTitle("任务进度");
        }
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        watchProgressListFragment=new WatchProgressListFragment();
        watchProgressListFragment.setOrgId(orgId);
        watchProgressListFragment.setTaskNumber(taskNumber);
        fragmentTransaction.replace(R.id.watch_progress_list_frameLayout,watchProgressListFragment);
        fragmentTransaction.commit();
    }

    private void addEvent(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
