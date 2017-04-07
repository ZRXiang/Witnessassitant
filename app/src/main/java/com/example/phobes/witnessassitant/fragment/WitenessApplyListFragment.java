package com.example.phobes.witnessassitant.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.WitenessData;
import com.example.phobes.witnessassitant.util.Configuration;
import com.example.phobes.witnessassitant.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phobes on 2016/6/7.
 */
public class WitenessApplyListFragment extends Fragment {
    GroupFragment groupFragment;
    private String result = null;
    private List<WitenessData> witenessLists = new ArrayList<WitenessData>();
    private String type;

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.witness_apply_list, container, false);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        groupFragment = new GroupFragment();
        fragmentTransaction.replace(R.id.sampleApply_classify_frameLayout, groupFragment);

        DatabaseHelper database = new DatabaseHelper(getActivity(), Configuration.DB_NAME,Configuration.DB_VERSION);
        SQLiteDatabase db = null;
        db = database.getReadableDatabase();
        if(!CommData.dbSqlite.isExitTestObject()){
            CommData.dbSqlite.insertTestObjects();
        }
        db.close();
        WitenessTaskListFragment witenessTaskListFragment = new WitenessTaskListFragment();
        witenessTaskListFragment.setType(type);
        fragmentTransaction.replace(R.id.sample_guild_list_frameLayout,witenessTaskListFragment);
        fragmentTransaction.commit();
        return rootView;
    }

}
