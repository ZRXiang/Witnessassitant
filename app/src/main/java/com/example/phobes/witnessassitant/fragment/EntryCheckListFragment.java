package com.example.phobes.witnessassitant.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.activity.EntryCheckActivity;
import com.example.phobes.witnessassitant.activity.LoginActivity;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.EntryCheckData;
import com.example.phobes.witnessassitant.service.LogWriter;
import com.example.phobes.witnessassitant.util.DatabaseHelper;
import com.example.phobes.witnessassitant.util.DividerItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by phobes on 2016/6/27.
 */
public class EntryCheckListFragment extends Fragment {
    View witenessView = null;
    View rootView;
    private UploadTask mupload = null;
    private String uploadResult;
    private DatabaseHelper dbHelper;
    private List<EntryCheckData> entryCheckDataList = new ArrayList<EntryCheckData>();
    EntryCheckItemAdapter witenessItemAdapter;
    ProgressDialog mProgressDialog;

    EntryCheckData mEntryCheckData = new EntryCheckData();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.entry_check_list, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("entryCheck Resume: 1");
                witenessView = rootView.findViewById(R.id.entry_check_item_list);
                assert witenessView != null;
                setupRecyclerView((RecyclerView) witenessView);
            }
        }, 1000);

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        if (!CommData.dbSqlite.isExitEntryCheck()) {
           /* Snackbar.make(rootView, "没有数据，请先下载任务，再见证", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();*/
        }
        entryCheckDataList = CommData.dbSqlite.getEntryCheckDatas();
        System.out.println("list size:" + entryCheckDataList.size());
        witenessItemAdapter = new EntryCheckItemAdapter(entryCheckDataList);
        recyclerView.setAdapter(witenessItemAdapter);
    }

    public class EntryCheckItemAdapter
            extends RecyclerView.Adapter<EntryCheckItemAdapter.ViewHolder> {

        private List<EntryCheckData> mValues;

        public EntryCheckItemAdapter(List<EntryCheckData> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.entry_check_list_content, parent, false);
            return new ViewHolder(view);
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
            boolean isChecked = CommData.dbSqlite.isEntryCheckDone(mValues.get(position).getEntryId());

            if (!isChecked) {
                holder.btCheck.setVisibility(View.VISIBLE);
                holder.bUpload.setVisibility(View.GONE);
            } else {
                holder.btCheck.setVisibility(View.GONE);
                holder.bUpload.setVisibility(View.VISIBLE);
            }
            holder.btCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, EntryCheckActivity.class);
//                    intent.putExtra(EntryCheckFragment.ARG_ITEM_ID, holder.mItem);
                    intent.putExtra(EntryCheckFragment.ARG_ENTRY_ID, holder.mItem.getEntryId());
                    context.startActivity(intent);
                }
            });
            holder.bUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEntryCheckData = CommData.dbSqlite.getEntryCheckWithComment(holder.mItem.getEntryId());
                    mProgressDialog = new ProgressDialog(getContext());
                    mProgressDialog.setMessage("正在上传中...");
                    mProgressDialog.setIndeterminate(false);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mProgressDialog.setCancelable(true);
                    mProgressDialog.show();
                    mupload = new UploadTask(mEntryCheckData);
                    mupload.execute((Void) null);
                }
            });
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
            public final Button bUpload;
            public final Button btCheck;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mProductNameView = (TextView) view.findViewById(R.id.product_name);
                mBatchIdView = (TextView) view.findViewById(R.id.batch_id);
                mQuantityView = (TextView) view.findViewById(R.id.quantity);
                mObjectNameView = (TextView) view.findViewById(R.id.object_name_entry);
                mEntryDateView = (TextView) view.findViewById(R.id.entry_date);
                btCheck = (Button) view.findViewById(R.id.button_entry_check);
                bUpload = (Button) view.findViewById(R.id.button_upload);
            }
        }
    }

    public class UploadTask extends AsyncTask<Void, Void, Boolean> {
        private EntryCheckData inmEntryCheckData;

        public UploadTask(EntryCheckData mEntryCheckData) {
            this.inmEntryCheckData = mEntryCheckData;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String comment = null;
                String witness = null;
                if (CommData.orgType.equals("监理试验室")) {
                    comment = inmEntryCheckData.getSuperComment();
                }else{
                    comment = inmEntryCheckData.getLabComment();
                    witness = inmEntryCheckData.getWitness();
                }
                uploadResult = CommData.dbWeb.uploadEntryCheck(inmEntryCheckData);
                if(uploadResult.toLowerCase().equals("true")) {
                    if(!CommData.orgType.equals("监理试验室")){
                        if (comment != null && (comment.equals("验收不合格") || witness.equals("不见证")) ) {
                            int witnessId = CommData.dbSqlite.getWitnessIdByEntryId(inmEntryCheckData.getEntryId());
                            CommData.dbSqlite.deleteWitness(witnessId);
                            CommData.dbWeb.deleteWitness(witnessId,inmEntryCheckData.getEntryId());
                        }
                    } else {
                        if (comment != null && comment.equals("验收不合格") && CommData.orgType.equals("监理试验室")) {
                            int witnessId = CommData.dbSqlite.getWitnessIdByEntryId(inmEntryCheckData.getEntryId());
                            CommData.dbSqlite.deleteWitness(witnessId);
                        }
                    }
                    return true;
                }else{
                    LogWriter.log(CommData.ERROR,"EntryCheckListFragment ，返回值："+uploadResult);
                    return false;
                }
            } catch (Exception e) {
                    LogWriter.log(CommData.ERROR,"EntryCheckListFragment ，异常："+e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mupload = null;
            if (success) {
                CommData.dbSqlite.setUploadEntryCheck(mEntryCheckData);
                mProgressDialog.dismiss();
                Snackbar.make(witenessView, "上传成功", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                setupRecyclerView((RecyclerView) witenessView);
            }else{
                mProgressDialog.dismiss();
                Snackbar.make(witenessView, "上传失败，请重新上传！", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

        @Override
        protected void onCancelled() {
            mupload = null;
        }
    }
}
