package com.example.phobes.witnessassitant.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.fragment.MeasurePointListFragment;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.PointMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YLS on 2016/9/4.
 */
public class MeasurePointActivity extends AppCompatActivity {


    Toolbar toolbar;
    private Button btnAddPoint;
    private Button btnFinish;
    MeasurePointListFragment measurePointListFragment;
    private  int objectId;
    private  int dataId;
    private  int indexId;
    private String orderId;
    private String testName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_point);
        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra("bundle");
        objectId=bundle.getInt("objectId");
        dataId=bundle.getInt("dataId");
        indexId=bundle.getInt("indexId");
        testName=bundle.getString("testName");
        orderId=bundle.getString("orderId");
        initView();
        addEvent();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        measurePointListFragment.update();
    }

    private void addPoint(){
        List<PointMeta> pointMetas= CommData.dbSqlite.getPointNum(dataId);
        for(int i=1;i<=pointMetas.size();i++){
            int mate=100135*100+i;
            for(int j=0;j<pointMetas.size();j++){
                if(mate==pointMetas.get(j).getMetaId()){
                    measurePointListFragment.add(objectId,dataId,indexId,pointMetas.get(j).getMetaValue());
                    continue;
                }
            }
        }
    }

    private void addEvent(){
        btnAddPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPoint();
                //measurePointListFragment.add(objectId,dataId,indexId);
            }
        });
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(!measurePointListFragment.isCount()) {
                 if(CommData.dbSqlite.getPointState(dataId, indexId)) {
                     if(CommData.dbSqlite.updateTaskStateAndTwo(dataId,indexId)){
                         finish();
                     }
                 }else{
                     Snackbar.make(btnFinish,"请完成测点后保存！", Snackbar.LENGTH_LONG)
                             .setAction("Action", null).show();
                 }
              }else{
                  Snackbar.make(btnFinish,"请完成测点后保存！", Snackbar.LENGTH_LONG)
                          .setAction("Action", null).show();
              }
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void initView(){
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        btnAddPoint=(Button) findViewById(R.id.button_add_point);
        btnFinish=(Button)findViewById(R.id.button_finish);
        if(toolbar!=null){
            toolbar.setTitle(testName+"  桩号");
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        measurePointListFragment = new MeasurePointListFragment();
        measurePointListFragment.setmObjectId(objectId);
        measurePointListFragment.setmDataId(dataId);
        measurePointListFragment.setmIndexId(indexId);
        measurePointListFragment.setmTestName(testName);
        measurePointListFragment.setmOrederId(orderId);
        fragmentTransaction.replace(R.id.measure_point_list_frameLayout,measurePointListFragment);
        fragmentTransaction.commit();
    }

}
