package com.example.phobes.witnessassitant.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.TestObject;
import com.example.phobes.witnessassitant.model.WatchProgress;
import com.example.phobes.witnessassitant.service.CommService;
import com.example.phobes.witnessassitant.util.DividerItemDecoration;
import com.example.phobes.witnessassitant.util.ParaseData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YLS on 2016/9/26.
 */
public class WatchProgressListFragment extends Fragment {

    List<WatchProgress> watchProgressList=new ArrayList<WatchProgress>();
    private  String result;
    watchProgressAdapter watchProgressAdapter;
    View rootView;
    RecyclerView watchProgressListView;
    GetWatchProgress  mGetWatchProgress;
    private String orgId;
    private String taskNumber;

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(String taskNumber) {
        this.taskNumber = taskNumber;
    }



    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.watch_progress_list,container,false);
        watchProgressListView=(RecyclerView) rootView.findViewById(R.id.watch_progress_list);
        setupRecyclerView();
        return rootView;
    }

    private void setupRecyclerView() {

        CommService commService = new CommService(getActivity());
        if (commService.isNetConnected()) {
            mGetWatchProgress = new GetWatchProgress();
            mGetWatchProgress.execute((Void) null);
        } else {
            Snackbar.make(rootView, "网络连接异常！", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }


    public class GetWatchProgress extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                result= CommData.dbWeb.getProgressState(orgId,taskNumber);
                if (result == null || result.equals("")) {
                    return false;
                } else {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success){
                watchProgressList = ParaseData.toWatchProgress(result);
                watchProgressAdapter=new watchProgressAdapter(watchProgressList);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()) ;
                watchProgressListView.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));
                watchProgressListView.setAdapter(watchProgressAdapter);
            }else {
                Snackbar.make(rootView, "没有数据", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

        @Override
        protected void onCancelled() {
            mGetWatchProgress=null;
        }
    }


    public class watchProgressAdapter extends RecyclerView.Adapter<watchProgressAdapter.ViewHolder>{

        private List<WatchProgress> mValues;

        public watchProgressAdapter(List<WatchProgress> items){
            mValues=items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.watch_progress_list_content,parent,false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mItem=mValues.get(position);
            holder.mProgressState.setText(mValues.get(position).getProgressState());
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final View mView;
            private final TextView mProgressState;
            public WatchProgress mItem;

            public ViewHolder(View view){
                super(view);
                mView=view;
                mProgressState= (TextView) view.findViewById(R.id.progress_state);
            }

            @Override
            public String toString() {
                return super.toString();
            }
        }
    }
}
