package com.example.phobes.witnessassitant.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.fragment.GroupFragment;
import com.example.phobes.witnessassitant.fragment.TestObjectListFragment;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.TestObject;

import java.util.ArrayList;
import java.util.List;

public class TestObjectListActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener{


    private TestObjectListFragment sampleGuildListFragment=null;
    public TestObject.TestObjectItem mItem;
    private List<TestObject.TestObjectItem> sampleGuildItems = new ArrayList<TestObject.TestObjectItem>();

    private ListView listView;
    private ListView samplyClassifyListView;
    private Toolbar toolbar;
    private SearchView mSearchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_guild_list);
        findViewById();
        initView();
        addEvent();
    }
    private void findViewById(){
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
         //mSearchView= (SearchView)findViewById(R.id.test_object_search);
    }
    private void addEvent(){
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
//        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//
//                doSearch(newText);
//                return true;
//            }
//        });
        toolbar.setOnMenuItemClickListener(this);
    }
    private void initView(){
        if (toolbar != null) {
            toolbar.setTitle(TestObjectListActivity.this.getResources().getString(R.string.sample_guild));
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        GroupFragment groupFragment = new GroupFragment();
        fragmentTransaction.replace(R.id.sampleApply_classify_frameLayout, groupFragment);


        if (!CommData.dbSqlite.isExitTestObject()) {
            CommData.dbSqlite.insertTestObjects();
        }
        sampleGuildItems = CommData.dbSqlite.getTestObjects();

        TestObjectListFragment sampleGuildListFragment = new TestObjectListFragment();
        fragmentTransaction.replace(R.id.sample_guild_list_frameLayout,sampleGuildListFragment);
        fragmentTransaction.commit();
    }
    public void doSearch(String searchStr){
        TestObjectListFragment fragment = (TestObjectListFragment) getSupportFragmentManager().findFragmentById(R.id.sample_guild_list_frameLayout);
        fragment.changeTestObjects(searchStr);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuentrycheckmain, menu);
        return true;
    }
}