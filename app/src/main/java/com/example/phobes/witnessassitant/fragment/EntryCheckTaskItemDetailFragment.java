package com.example.phobes.witnessassitant.fragment;

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
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.EntryCheckData;
import com.example.phobes.witnessassitant.util.DividerItemDecoration;
import com.example.phobes.witnessassitant.util.ParaseData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phobes on 2016/6/27.
 */
public class EntryCheckTaskItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private GetEntryCheckTaskDetail mGetStringTask = null;
    public String sWitnessResult = null;
    public static final String ARG_ENTRY_ID = "entry_id";

    public int entryId = 0;
    public String entryCheckResult;
    public View rootView;
    private FloatingActionButton bDownload;
    private EntryCheckData entryCheckData;
    RecyclerView mRecycleView;
    TaskDetailItemAdapter taskDetailItemAdapter;


    public EntryCheckTaskItemDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ENTRY_ID)) {
            entryId = getArguments().getInt(ARG_ENTRY_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.entry_check_item_detail, container, false);
        findViewById();
        addEvent();

        if (CommData.dbSqlite.isExitEntryCheck(entryId)) {
            entryCheckData = CommData.dbSqlite.getEntryCheckTaskDetail(entryId);
            showData();
            bDownload.setVisibility(View.GONE);
        } else {
            mGetStringTask = new GetEntryCheckTaskDetail(entryId);
            mGetStringTask.execute((Void) null);
        }

        return rootView;
    }

    private void findViewById() {
        mRecycleView = (RecyclerView) rootView.findViewById(R.id.witness_apply_item_detail);
        bDownload = (FloatingActionButton) getActivity().findViewById(R.id.entry_check_download);
    }
    private void addEvent(){
        bDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadEntryCheck();
            }
        });
    }

    public class GetEntryCheckTaskDetail extends AsyncTask<Void, Void, Boolean> {
        private final int entryId;

        GetEntryCheckTaskDetail(int entryId) {
            this.entryId = entryId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                entryCheckResult = CommData.dbWeb.loadEntryCheck(entryId);
                Log.i("entry check result", entryCheckResult);
                return true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mGetStringTask = null;
            if (success) {
                try {
                    entryCheckData = ParaseData.toEntryCheckTaskDetail(entryCheckResult);
                    showData();
                } catch (Exception e) {
                    Snackbar.make(bDownload, "没有细节", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            } else {
                Snackbar.make(bDownload, "无法从服务器得到数据", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

        @Override
        protected void onCancelled() {
            mGetStringTask = null;
        }
    }

    private void showData() {
        List<String> taskDetails = new ArrayList<String>();
        taskDetails.add("对应试验");
        taskDetails.add("产品名称");
        taskDetails.add("生产厂家");    //added by WQS  2017/02/19
        taskDetails.add("批量号");
        taskDetails.add("出厂日期");
        taskDetails.add("材料数量");
        taskDetails.add("规格型号");
        taskDetails.add("规格尺寸");    //added by WQS  2017/02/19
        taskDetails.add("强度等级");
        taskDetails.add("进场日期");
        taskDetails.add("出厂报告编号");
        taskDetails.add("试验室验收人");
        taskDetails.add("试验室意见");
        taskDetails.add("试验室验收日期");
        taskDetails.add("监理验收人");
        taskDetails.add("监理意见");
        taskDetails.add("监理验收日期");
        taskDetailItemAdapter = new TaskDetailItemAdapter(taskDetails);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecycleView.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));
        mRecycleView.setAdapter(taskDetailItemAdapter);
    }

    public class TaskDetailItemAdapter
            extends RecyclerView.Adapter<TaskDetailItemAdapter.ViewHolder> {
        private List<String> mValues;

        public TaskDetailItemAdapter(List<String> items) {
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
            holder.metaNameView.setText(holder.mItem);
            String value = null;
            switch (position) {
                case 0: {
                    value = CommData.dbSqlite.getObjectName(entryCheckData.getObjectId());
                }
                break;
                case 1:
                    value = entryCheckData.getProductName();
                    break;
                case 2:
                    value = entryCheckData.getFactory();
                    break;
                case 3:
                    value = entryCheckData.getBatchId();
                    break;
                case 4:
                    value = entryCheckData.getOutputDate();
                    break;
                case 5:
                    value =entryCheckData.getQuantity();
                    if(value==null || value.equals("null")){
                        value="";
                    }
                    break;
                case 6:
                    value = entryCheckData.getSampleSpec();
                    break;
                case 7:
                    value = entryCheckData.getSampleSize();
                    break;
                case 8:
                    value = entryCheckData.getStrength();
                    break;
                case 9:
                    value = entryCheckData.getEntryDate();
                    break;
                case 10:
                    value = entryCheckData.getReportId();
                    break;
                case 11:
                    value = entryCheckData.getLabPerson();
                    break;
                case 12:
                    value = entryCheckData.getLabComment();
                    break;
                case 13:
                    value =entryCheckData.getLabCheckDate();
                    break;
                case 14:
                    value = entryCheckData.getSuperPerson();
                    break;
                case 15:
                    value = entryCheckData.getSuperComment();
                    break;
                case 16:
                    value = entryCheckData.getSuperCheckDate();
                    break;
            }
            holder.metaValueView.setText(value);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView metaNameView;
            public final TextView metaValueView;
            public String mItem;

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

    private void downloadEntryCheck() {
        boolean bResult = CommData.dbSqlite.saveEntryCheck(entryCheckData);
        if (bResult)
          Snackbar.make(bDownload, "下载成功", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        else
          Snackbar.make(bDownload, "下载保存失败", Snackbar.LENGTH_LONG).setAction("Action", null).show();

    }
}
