package com.example.phobes.witnessassitant.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.activity.MainActivity;
import com.example.phobes.witnessassitant.activity.SampleGuildItemDetailActivity;
import com.example.phobes.witnessassitant.activity.TestObjectListActivity;
import com.example.phobes.witnessassitant.activity.WitenessApplyActivity;
import com.example.phobes.witnessassitant.activity.WitnessMainActivity;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.TestObject;
import com.example.phobes.witnessassitant.model.UserInfo;
import com.example.phobes.witnessassitant.model.WitenessData;
import com.example.phobes.witnessassitant.model.WitenessDetail;
import com.example.phobes.witnessassitant.util.DividerItemDecoration;
import com.example.phobes.witnessassitant.util.Md5Utils;
import com.example.phobes.witnessassitant.util.ParaseData;
import com.example.phobes.witnessassitant.util.ParaseWitness;

import java.util.List;

/**
 * Created by phobes on 2016/6/3.
 */
public class WitenessApplyDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private GetWitenessDetailTask mGetWitenessDetailTask = null;
    private DownloadWitness mDownloadWitness = null;
    public String sWitnessResult = null;
    public static final String ARG_ITEM_ID = "sample_guild_item_id";
    public static final String ARG_WITNESS_ID = "witness_id";

    public int object_id = 0;
    public int witness_id = 0;
    private String type="";
    public String witnessDetailResult;
    public String TestDictResult;
    TaskDetailItemAdapter taskDetailItemAdapter;
    public View rootView;
    private FloatingActionButton bDownload;
    private List<WitenessDetail> fianlwitenessDetails;
    private RecyclerView mRecyclerView;
    public WitenessApplyDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            object_id = getArguments().getInt(ARG_ITEM_ID);
            if (getArguments().containsKey(ARG_WITNESS_ID)) {
                witness_id = getArguments().getInt(ARG_WITNESS_ID);
            }
            if(getArguments().containsKey("type")){
                type=getArguments().getString("type");
            }
//            Activity activity = this.getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.witeness_apply_detail, container, false);
        findViewById();

        if (CommData.dbSqlite.isExitWitenessDetail(witness_id)) {
            fianlwitenessDetails = CommData.dbSqlite.getWitnessDetails(witness_id);
            showData();
            bDownload.setVisibility(View.GONE);
        } else {
            mGetWitenessDetailTask = new GetWitenessDetailTask(object_id);
            mGetWitenessDetailTask.execute((Void) null);

            bDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDownloadWitness = new DownloadWitness(witness_id);
                    mDownloadWitness.execute((Void) null);
                }
            });
        }
        return rootView;
    }

    private void findViewById(){
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.witness_apply_item_detail);
        bDownload = (FloatingActionButton) getActivity().findViewById(R.id.download);
    }

    public class GetWitenessDetailTask extends AsyncTask<Void, Void, Boolean> {
        private final int objectId;

        GetWitenessDetailTask(int objectId) {
            this.objectId = objectId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if (object_id != 0) {
                try {
                    TestDictResult = CommData.dbWeb.loadTestObjectDict(String.valueOf(objectId));
                    witnessDetailResult = CommData.dbWeb.getWitenessDetail(witness_id);
                    return true;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return false;
                }
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mGetWitenessDetailTask = null;
            if (success) {
                try{
                    List<WitenessDetail> testObjectDict = ParaseData.DicttoWitenessDetail(TestDictResult);
                    List<WitenessDetail> witnessDetails = ParaseData.toWitenessDetail(witnessDetailResult);
                    Log.i("dict Details ; ", TestDictResult);
                    Log.i("witeness Details ; ", witnessDetailResult);
                    for (int i = 0; i < testObjectDict.size(); i++) {

                        for (int j = 0; j < witnessDetails.size(); j++) {
                            try{
                                testObjectDict.get(i).setWitness_id(witnessDetails.get(j).getWitness_id());
                                if (testObjectDict.get(i).getMeta_id() == witnessDetails.get(j).getMeta_id()) {

                                    testObjectDict.get(i).setValue(witnessDetails.get(j).getValue());
                                    break;
                                }
                            }catch (Exception e){
                                 e.printStackTrace();
                            }
                        }

                    }
                    fianlwitenessDetails = testObjectDict;
                    showData();
                }
               catch (Exception e){
                   bDownload.setVisibility(View.GONE);
                   Snackbar.make(bDownload, "没有见证细节", Snackbar.LENGTH_LONG)
                           .setAction("Action", null).show();
               }

            } else {
                Snackbar.make(bDownload, "无法从服务器得到数据", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

        @Override
        protected void onCancelled() {
            mGetWitenessDetailTask = null;
        }
    }

    public class DownloadWitness extends AsyncTask<Void, Void, Boolean> {

        private final int witnessId;


        DownloadWitness(int witnessId) {
            this.witnessId = witnessId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if (witness_id != 0) {
                try {
                    sWitnessResult = CommData.dbWeb.getWitenessData(witness_id);
                    Log.d("result", sWitnessResult);
                    if (sWitnessResult == null) {
                        System.out.println("null point");
                        return false;
                    } else {
                        return true;
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return false;
                }
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            boolean bResult = false;
            mGetWitenessDetailTask = null;
            if (success) {
                WitenessData witenessData = ParaseWitness.toWitnessData(sWitnessResult,type);
                String []sItems = witenessData.getTest_items().split(";",20);
                if (sItems.length == 0) {
                    witenessData.setTestItems(1);
                    witenessData.setTest_items(witenessData.getObject_name());
                }
                else
                    witenessData.setTestItems(sItems.length);
                bResult = CommData.dbSqlite.saveWiteness(witenessData);
                bResult = (bResult && CommData.dbSqlite.saveWitnessDetails( fianlwitenessDetails));
            }
            if (bResult)
                Snackbar.make(rootView, "下载成功", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            else
                Snackbar.make(rootView, "下载失败", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

        @Override
        protected void onCancelled() {
            mGetWitenessDetailTask = null;
        }
    }

    private void showData(){
        taskDetailItemAdapter= new TaskDetailItemAdapter(fianlwitenessDetails);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));
        mRecyclerView.setAdapter(taskDetailItemAdapter);

    }

    public class TaskDetailItemAdapter
            extends RecyclerView.Adapter<TaskDetailItemAdapter.ViewHolder> {
        private List<WitenessDetail> mValues;
        public TaskDetailItemAdapter( List<WitenessDetail> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.witness_apply_task_detail_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.metaNameView.setText(mValues.get(position).getMeta_name());
            holder.metaValueView.setText(mValues.get(position).getValue());
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView metaNameView;
            public final TextView metaValueView;
            public WitenessDetail mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                metaNameView = (TextView) view.findViewById(R.id.meta_name);
                metaValueView = (TextView) view.findViewById(R.id.meta_value);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + metaValueView.getText() + "'";
            }
        }
    }
}