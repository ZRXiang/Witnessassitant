package com.example.phobes.witnessassitant.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.activity.EntryCheckMainActivity;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.WitenessData;
import com.example.phobes.witnessassitant.util.Configuration;
import com.example.phobes.witnessassitant.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phobes on 2016/6/27.
 */
public class EntryCheckTaskFragment extends Fragment {

    GroupFragment groupFragment;
    private String result = null;
    private List<WitenessData> witenessLists = new ArrayList<WitenessData>();

    //cy
//    SharedPreferences sharedPreferences;
//    SharedPreferences.Editor editor;
    EntryCheckTaskListDetailFragment witenessTaskListFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        sharedPreferences = getContext().getSharedPreferences("nPage", 0);
//        editor = sharedPreferences.edit();
        View rootView = inflater.inflate(R.layout.witness_apply_list, container, false);
        FragmentManager fragmentManager = getFragmentManager();

        final FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        groupFragment = new GroupFragment();
        fragmentTransaction.replace(R.id.sampleApply_classify_frameLayout, groupFragment);
        DatabaseHelper database = new DatabaseHelper(getActivity(), Configuration.DB_NAME, Configuration.DB_VERSION);
        SQLiteDatabase db = null;
        db = database.getReadableDatabase();
        if (!CommData.dbSqlite.isExitTestObject()) {
            CommData.dbSqlite.insertTestObjects();
        }
        db.close();
        witenessTaskListFragment = new EntryCheckTaskListDetailFragment();
        fragmentTransaction.replace(R.id.sample_guild_list_frameLayout, witenessTaskListFragment);
        fragmentTransaction.commit();

        //bycy


//        textView = (TextView) rootView.findViewById(R.id.tv_nPage);


//        callBackFragmentValue = (CallBackFragmentValue) getActivity();


//        rootView.findViewById(R.id.btn_pageup).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                nPage -= 1;
//                if (nPage<1){nPage=1;}
//                textView.setText("第"+nPage+"页");
//                editor.putInt("npage",nPage);
//                editor.commit();
//                witenessTaskListFragment.setupRecyclerView();
//
//            }
//        });
//
//        rootView.findViewById(R.id.btn_pagedown).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                nPage += 1;
//
//                textView.setText("第"+nPage+"页");
//
//                editor.putInt("npage",nPage);
//                editor.commit();
//                witenessTaskListFragment.setupRecyclerView();
//            }
//        });


        return rootView;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((EntryCheckMainActivity)context).setHandler(mHandler);
    }



    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    nPage = (int) msg.obj;
                    witenessTaskListFragment.setupRecyclerView();
                    break;
            }
        }
    };
//    TextView textView;

//    CallBackFragmentValue callBackFragmentValue;
//
//    public interface CallBackFragmentValue {
//        public void sendPageValue(int nPage);
//    }

    int nPage = 1;

}