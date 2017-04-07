package com.example.phobes.witnessassitant.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.phobes.witnessassitant.activity.MixTaskDetailActivity;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.MixTaskData;
import com.example.phobes.witnessassitant.service.CommService;
import com.example.phobes.witnessassitant.util.DividerItemDecoration;
import com.example.phobes.witnessassitant.util.ParaseData;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by YLS on 2016/9/25.
 */
public class MixTaskListFragment extends Fragment {
        View rootView;
        RecyclerView mixTaskListView;
        MixTaskListAdapter mixTaskListAdapter;
       private List<MixTaskData> mixTaskDataList=new ArrayList<MixTaskData>();
       private String result=null;
       GetMixTask mGetMixTask;
      private final String MIX_PERMISSION = "拌和站";
      private final String SUP_PERMISSION = "监理试验室";

    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.mix_task_item_list,container,false);
        mixTaskListView=(RecyclerView) rootView.findViewById(R.id.mix_task_item_list);
        setupRecyclerView();
        return rootView;
    }

    private void setupRecyclerView(){

        CommService commService = new CommService(getActivity());
        if (commService.isNetConnected()) {
            mGetMixTask = new GetMixTask();
            mGetMixTask.execute((Void) null);
        } else {
            Snackbar.make(rootView, "网络连接异常！", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                assert mixTaskListView != null;
                mixTaskListView=(RecyclerView) rootView.findViewById(R.id.mix_task_item_list);
                setupRecyclerView();
            }
        }, 100);

    }

    public class GetMixTask extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if(checkPermission(SUP_PERMISSION)) {
                    result = CommData.dbWeb.loadMixTask();
                }else if(checkPermission(MIX_PERMISSION)){
                    result = CommData.dbWeb.loadMixTaskAndOrgId();
                }else{
                    result = CommData.dbWeb.loadMixTaskAndTestRoom();
                }
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
                mixTaskDataList = ParaseData.toMixTask(result);
                mixTaskListAdapter=new MixTaskListAdapter(mixTaskDataList);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()) ;
                mixTaskListView.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));
                mixTaskListView.setAdapter(mixTaskListAdapter);
            }else {
                Snackbar.make(rootView, "没有数据", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

        @Override
        protected void onCancelled() {
            mGetMixTask=null;
        }
    }



    public class MixTaskListAdapter extends RecyclerView.Adapter<MixTaskListAdapter.ViewHolder>{

        private List<MixTaskData> mValues;
        public MixTaskListAdapter(List<MixTaskData> items){
                    mValues=items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mix_task_item_list_content,parent,false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem=mValues.get(position);
            holder.mTaskNumber.setText(mValues.get(position).getTaskId());
            holder.mInflictionPosition.setText(mValues.get(position).getPosition());
            holder.mApplyDate.setText(mValues.get(position).getApplicationTime());
            holder.mAllocation.setText(String.valueOf(mValues.get(position).getVolume())+"(m³)");
            holder.mApplyPerson.setText(mValues.get(position).getApplicant());
            holder.mIntensity.setText(mValues.get(position).getStrength());
            switch (mValues.get(position).getState()){
                case 0:
                    holder.mTaskState.setText("未下发");
                    break;
                case 1:
                    holder.mTaskState.setText("未受理");
                    break;
                case 2:
                    holder.mTaskState.setText("已受理");
                    break;
                case 3:
                    holder.mTaskState.setText("配比通知单下发");
                    break;
                case 4:
                    holder.mTaskState.setText("首盘检测");
                    break;
                case 5:
                    holder.mTaskState.setText("现场检测");
                    break;
                case 6:
                    holder.mTaskState.setText("已完成");
                    break;
                case 7:
                    holder.mTaskState.setText("已废除");
                    break;
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.mView.setBackgroundResource(R.color.baby_blue);
                    Intent intent=new Intent(getContext(), MixTaskDetailActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("option","editTask");
                    bundle.putString("orgId",holder.mItem.getOrgId());
                    bundle.putString("applyNumber",holder.mItem.getTaskId());
                    bundle.putString("projectName",holder.mItem.getProjectName());
                    bundle.putString("inflictionPosition",holder.mItem.getPosition());
                    bundle.putString("intensityLevel",holder.mItem.getStrength());
                    bundle.putString("planVolume",String.valueOf(holder.mItem.getVolume()));
                    bundle.putString("mixStationName",holder.mItem.getStationName());
                    bundle.putString("predictStartTime",holder.mItem.getBeginTime());
                    bundle.putString("applicant",holder.mItem.getApplicant());
                    bundle.putString("applyTime",holder.mItem.getApplicationTime());
                    bundle.putInt("taskState",holder.mItem.getState());
                    bundle.putString("planSlump",holder.mItem.getSlump());
                    bundle.putString("destination",holder.mItem.getDestination());
                    intent.putExtra("bundle",bundle);
                    startActivity(intent);

                }
            });

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            private final View mView;
            private final TextView mTaskNumber;
            private final TextView mInflictionPosition;
            private final TextView mTaskState;
            private final TextView mApplyDate;
            private final TextView mAllocation;
            private final TextView mApplyPerson;
            private final TextView mIntensity;
            public MixTaskData mItem;

            public ViewHolder(View view){
                super(view);
                mView=view;
                mTaskNumber= (TextView) view.findViewById(R.id.task_number);
                mInflictionPosition= (TextView) view.findViewById(R.id.infliction_position);
                mTaskState= (TextView) view.findViewById(R.id.task_state);
                mApplyDate= (TextView) view.findViewById(R.id.apply_date);
                mAllocation= (TextView) view.findViewById(R.id.allocation);
                mApplyPerson= (TextView) view.findViewById(R.id.apply_person);
                mIntensity= (TextView) view.findViewById(R.id.intensity);
            }
            @Override
            public String toString() {
                return super.toString();
            }
        }
    }

    private boolean checkPermission(String permission) {
        if (CommData.orgType.equals(permission)) {
            return true;
        } else {
            return false;
        }
    }
}
