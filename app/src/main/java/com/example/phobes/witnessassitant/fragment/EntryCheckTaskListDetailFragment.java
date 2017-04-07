package com.example.phobes.witnessassitant.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.activity.EntryCheckTaskDetailActivity;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.EntryCheckData;
import com.example.phobes.witnessassitant.service.CommService;
import com.example.phobes.witnessassitant.service.LogWriter;
import com.example.phobes.witnessassitant.util.DividerItemDecoration;
import com.example.phobes.witnessassitant.util.ParaseData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phobes on 2016/6/27.
 */
public class EntryCheckTaskListDetailFragment extends Fragment {
    String result = null;
    private View rootView;
    private GetEntryCheckTasks mGetEntrysTask = null;
    private List<EntryCheckData> entryCheckTasks = new ArrayList<EntryCheckData>();
    EntryCheckTaskItemAdapter entryCheckTaskItemAdapter = null;
    RecyclerView tasksView;
    ProgressDialog mProgressDialog;
    int nPage = 1;
    SharedPreferences sharedPreferences;

    //    SharedPreferences.Editor editor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.entry_check_task_list, container, false);
        tasksView = (RecyclerView) rootView.findViewById(R.id.entry_check_task_list_detail);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("数据加载中...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        // by cy
        sharedPreferences = getContext().getSharedPreferences("nPage", 0);
        editor = sharedPreferences.edit();
//        editor = sharedPreferences.edit();
//        editor.putInt("npage",1);
//        Bundle bundle = getArguments();
//        if (bundle !=null){
//            nPage = bundle.getInt("mes");
//        }
        return rootView;
    }

    public void setupRecyclerView() {
//        // by cy
//        Bundle bundle = getArguments();
//        if (bundle !=null){
//            nPage = bundle.getInt("mes");
//        }
        int n = sharedPreferences.getInt("npage", 1);
        nPage = n;
        CommService commService = new CommService(getActivity());
        if (commService.isNetConnected()) {

            mGetEntrysTask = new GetEntryCheckTasks();
            mGetEntrysTask.execute((Void) null);
        } else {
            loadLocalTask();
            mProgressDialog.dismiss();
        }
    }


    private void loadLocalTask() {
        entryCheckTasks = CommData.dbSqlite.getEntryCheckDatas();
        entryCheckTaskItemAdapter = new EntryCheckTaskItemAdapter(entryCheckTasks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        tasksView.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));
        tasksView.setAdapter(entryCheckTaskItemAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("entryCheck Resume: 1save");
        setupRecyclerView();
    }
//
//    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public class GetEntryCheckTasks extends AsyncTask<Void, Void, Boolean> {
        GetEntryCheckTasks() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            int nObjectId = -1;
            nObjectId = sharedPreferences.getInt("nObjectId",nObjectId);
            try {

                if (nObjectId == -1) {

                    result = CommData.dbWeb.loadEntryCheckList(nPage);
                } else {
                    result = CommData.dbWeb.changeEntryCheckList(nObjectId, nPage);
                }
                // Log.d("result", result);
                if (result == null || result.equals("")) {
                    return false;
                } else {
                    return true;
                }
            } catch (Exception e) {
                mProgressDialog.dismiss();
                //System.out.println(e.getMessage());
                LogWriter.log(CommData.ERROR, "加载进场验收任务列表，异常：" + e.getMessage());
                return false;
            } finally {
                //mGetEntrysTask = null;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //mGetEntrysTask = null;
            if (success) { editor.putBoolean("bHasDate",true);
                editor.commit();
                entryCheckTasks = ParaseData.toEntryCheckTasks(result);

                entryCheckTaskItemAdapter = new EntryCheckTaskItemAdapter(entryCheckTasks);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                tasksView.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));
                tasksView.setAdapter(entryCheckTaskItemAdapter);
                mProgressDialog.dismiss();

            } else {
                //cy
                editor.putBoolean("bHasDate",false);
                editor.commit();
//                editor.putInt("npage",(sharedPreferences.getInt("npage",1)-1));
                Snackbar.make(rootView, "没有数据", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

        @Override
        protected void onCancelled() {
            mGetEntrysTask = null;
        }
    }

    public class EntryCheckTaskItemAdapter
            extends RecyclerView.Adapter<EntryCheckTaskItemAdapter.ViewHolder> {

        private List<EntryCheckData> mValues;

        public EntryCheckTaskItemAdapter(List<EntryCheckData> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.entry_check_task_list_content, parent, false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mProductNameView.setText(mValues.get(position).getProductName());
            holder.mBatchIdView.setText(mValues.get(position).getBatchId());
            String objectName = CommData.dbSqlite.getObjectName(mValues.get(position).getObjectId());
            holder.mObjectNameView.setText(objectName);

            holder.mQuantityView.setText(mValues.get(position).getQuantity());

            holder.mEntryDateView.setText(mValues.get(position).getEntryDate());
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public void updateList(List<EntryCheckData> data) {
            mValues = data;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mProductNameView;
            public final TextView mBatchIdView;
            public final TextView mObjectNameView;
            public final TextView mQuantityView;
            public final TextView mEntryDateView;
            public EntryCheckData mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, EntryCheckTaskDetailActivity.class);
                        intent.putExtra(EntryCheckTaskItemDetailFragment.ARG_ENTRY_ID, mItem.getEntryId());
                        context.startActivity(intent);
                    }
                });
                mProductNameView = (TextView) view.findViewById(R.id.product_name);
                mBatchIdView = (TextView) view.findViewById(R.id.batch_id);
                mQuantityView = (TextView) view.findViewById(R.id.quantity);
                mObjectNameView = (TextView) view.findViewById(R.id.object_name_value);
                mEntryDateView = (TextView) view.findViewById(R.id.entry_date);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mProductNameView.getText() + "'";
            }
        }
    }


    public void changeTaskByObjId(int groupId) {


        if (groupId == -1) {
            mGetEntrysTask = new GetEntryCheckTasks();
            mGetEntrysTask.execute((Void) null);
        } else {

            ChangeEntryCheckTasks entryCheckTasks = new ChangeEntryCheckTasks(groupId);
            entryCheckTasks.execute((Void) null);
        }
    }

    public class ChangeEntryCheckTasks extends AsyncTask<Void, Void, Boolean> {
        private int objectId;

        public ChangeEntryCheckTasks(int objectId) {
            this.objectId = objectId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                result = CommData.dbWeb.changeEntryCheckList(objectId, nPage);
                // Log.d("result", result);
                if (result == null || result.equals("")) {
                    return false;
                } else {
                    return true;
                }
            } catch (Exception e) {
                mProgressDialog.dismiss();
                //System.out.println(e.getMessage());
                return false;
            } finally {
                //mGetEntrysTask = null;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //mGetEntrysTask = null;
            if (success) {
                entryCheckTasks = ParaseData.toEntryCheckTasks(result);
                entryCheckTaskItemAdapter.updateList(entryCheckTasks);
            } else {
                if (entryCheckTasks.size() > 0) {
                    entryCheckTasks.clear();
                    entryCheckTaskItemAdapter.updateList(entryCheckTasks);
                }
                Snackbar.make(rootView, "该分组没有数据", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

        @Override
        protected void onCancelled() {
            mGetEntrysTask = null;
        }
    }
}