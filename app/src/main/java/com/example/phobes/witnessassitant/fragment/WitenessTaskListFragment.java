package com.example.phobes.witnessassitant.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.activity.WitenessApplyActivity;
import com.example.phobes.witnessassitant.activity.WitnessMainActivity;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.WitenessData;
import com.example.phobes.witnessassitant.service.CommService;
import com.example.phobes.witnessassitant.service.LogWriter;
import com.example.phobes.witnessassitant.util.DividerItemDecoration;
import com.example.phobes.witnessassitant.util.ParaseData;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by phobes on 2016/6/3.
 */
public class WitenessTaskListFragment extends Fragment {
    String result = null;
    private View rootView;
    private GetWitenessDatas mGetWitenessDetailTask = null;
    private List<WitenessData> witenessDatas = new ArrayList<WitenessData>();
    WitenessDataItemAdapter witenessTaskItemAdapter;
    RecyclerView tasksView;
    private String type;
    private ProgressDialog mProgressDialog;
    View witenessView = null;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPreferences = getContext().getSharedPreferences("nPage", 0);
        editor = sharedPreferences.edit();
        rootView = inflater.inflate(R.layout.witeness_apply_item_list, container, false);
        tasksView = (RecyclerView) rootView.findViewById(R.id.witeness_apply_item_list);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("数据加载中...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        return rootView;
    }

    private void setupRecyclerView() {
        // mProgressDialog.show();
        int n = sharedPreferences.getInt("npage", 1);
        nPage = n;
        CommService commService = new CommService(getActivity());
        if (commService.isNetConnected()) {

            mGetWitenessDetailTask = new GetWitenessDatas();
            mGetWitenessDetailTask.execute((Void) null);
        } else {
            loadLocalTask();
            mProgressDialog.dismiss();
        }
    }

    private void loadLocalTask() {
        witenessDatas = CommData.dbSqlite.getWiteness("loadData", type);//loadData 为未下载的数据，显示在下载任务页面
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        tasksView.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));
        witenessTaskItemAdapter = new WitenessDataItemAdapter(witenessDatas);
        tasksView.setAdapter(witenessTaskItemAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        //setupRecyclerView();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("witnessData Resume: 1");
                witenessView = rootView.findViewById(R.id.witeness_item_list);
                assert witenessView != null;
                setupRecyclerView();
            }
        }, 1000);
    }

    public class GetWitenessDatas extends AsyncTask<Void, Void, Boolean> {//加载见证数据

        GetWitenessDatas() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            int g = sharedPreferences.getInt("nObjectId", -1);
            if (g == -1) {
                try {
                    result = CommData.dbWeb.loadApplyList(type, nPage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    result = CommData.dbWeb.changeApplyList(type, g, nPage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {

                Log.d("result", result);
                if (result == null || result.equals("")) {
                    return false;
                } else {
                    return true;
                }
            } catch (Exception e) {
                LogWriter.log(CommData.ERROR, "加载见证任务列表，异常：" + e.getMessage());
                return false;
            } finally {
                //mGetWitenessDetailTask = null;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // mGetWitenessDetailTask = null;
            if (success) {
                witenessDatas = ParaseData.toWitenessDatas(result, type);
            /*    DatabaseHelper dbHelper = new DatabaseHelper(getActivity(), Configuration.DB_NAME, Configuration.DB_VERSION);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                SqliteService sqliteService = new SqliteService();
                sqliteService.saveWitenessList(db, witenessDatas);
                db.close();*/
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                tasksView.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));
                witenessTaskItemAdapter = new WitenessDataItemAdapter(witenessDatas);
                tasksView.setAdapter(witenessTaskItemAdapter);
                editor.putBoolean("bHasDate",true);
                editor.commit();
            } else {
                //mProgressDialog.dismiss();
                editor.putBoolean("bHasDate",false);
                editor.commit();
//                editor.putInt("npage",(sharedPreferences.getInt("npage",1)-1));
                Snackbar.make(rootView, "没有数据", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            mProgressDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            mProgressDialog.dismiss();
            //mGetWitenessDetailTask = null;
        }
    }

    public class WitenessDataItemAdapter
            extends RecyclerView.Adapter<WitenessDataItemAdapter.ViewHolder> {

        private List<WitenessData> mValues;

        public WitenessDataItemAdapter(List<WitenessData> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.witeness_apply_item_list_content, parent, false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mObjectNameView.setText(mValues.get(position).getObject_name());
            holder.mSampleIdView.setText(mValues.get(position).getBatch_id());
            holder.mTimeView.setText(mValues.get(position).getApply_time());
            holder.mOrgNameView.setText(mValues.get(position).getSample_org_name());
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public void updateList(List<WitenessData> data) {
            mValues = data;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mObjectNameView;
            public final TextView mSampleIdView;
            public final TextView mTimeView;
            public final TextView mOrgNameView;
            public WitenessData mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, WitenessApplyActivity.class);
                        intent.putExtra(WitenessApplyDetailFragment.ARG_ITEM_ID, mItem.getObject_id());
                        intent.putExtra(WitenessApplyDetailFragment.ARG_WITNESS_ID, mItem.getWitness_id());
                        intent.putExtra("type", type);
                        context.startActivity(intent);
                    }
                });
                mObjectNameView = (TextView) view.findViewById(R.id.object_name);
                mSampleIdView = (TextView) view.findViewById(R.id.sample_id);
                mOrgNameView = (TextView) view.findViewById(R.id.org_name);
                mTimeView = (TextView) view.findViewById(R.id.apply_time);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mObjectNameView.getText() + "'";
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((WitnessMainActivity) context).setHandler(mHandler);
    }


    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    nPage = (int) msg.obj;
                    setupRecyclerView();
                    break;
            }
        }
    };
    int nPage = 1;


    public void changeTaskByObjId(int groupId) {
        if (groupId == -1) {
            mGetWitenessDetailTask = new GetWitenessDatas();
            mGetWitenessDetailTask.execute((Void) null);
        } else {
            ChangeWitenessDatas changeWitenessDatas = new ChangeWitenessDatas(groupId);
            changeWitenessDatas.execute((Void) null);
        }
    }

    public class ChangeWitenessDatas extends AsyncTask<Void, Void, Boolean> {

        private int groupId;

        ChangeWitenessDatas(int groupId) {
            this.groupId = groupId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // Snackbar.make(rootView, "Step2", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                result = CommData.dbWeb.changeApplyList(type, groupId, nPage);
                Log.d("result", result);
                if (result == null || result.equals("")) {
                    return false;
                } else {
                    return true;
                }
            } catch (Exception e) {
                LogWriter.log(CommData.ERROR, "加载见证任务列表，异常：" + e.getMessage());
                return false;
            } finally {
                mGetWitenessDetailTask = null;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // mGetWitenessDetailTask = null;
            if (success) {
                witenessDatas = ParaseData.toWitenessDatas(result, type);
                witenessTaskItemAdapter.updateList(witenessDatas);
            } else {
                //mProgressDialog.dismiss();
                if (witenessDatas.size() > 0) {
                    witenessDatas.clear();
                    witenessTaskItemAdapter.updateList(witenessDatas);
                }
                Snackbar.make(rootView, "该分组没有数据", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            mProgressDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            mProgressDialog.dismiss();
            mGetWitenessDetailTask = null;

        }
    }
}
