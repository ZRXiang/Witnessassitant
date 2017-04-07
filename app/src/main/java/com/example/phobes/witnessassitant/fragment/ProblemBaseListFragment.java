package com.example.phobes.witnessassitant.fragment;

import android.content.Intent;
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
import com.example.phobes.witnessassitant.activity.ProblemBaseDetailActivity;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.ProblemBaseData;
import com.example.phobes.witnessassitant.service.CommService;
import com.example.phobes.witnessassitant.util.DividerItemDecoration;
import com.example.phobes.witnessassitant.util.ParaseData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YLS on 2016-10-25.
 */
public class ProblemBaseListFragment extends Fragment{

    private int riskCatId=0;
    private View rootView;
    RecyclerView problemBaseListView;
    private String result;
    List<ProblemBaseData> problemBaseDataList=new ArrayList<ProblemBaseData>();
    GetProblemBaseData getProblemBaseData;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if(getArguments().containsKey("risk_cat_id")){
        if(getArguments().containsKey("risk_cat_id")){
            riskCatId=getArguments().getInt("risk_cat_id");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        rootView=inflater.inflate(R.layout.problem_base_list,container,false);
        problemBaseListView=(RecyclerView) rootView.findViewById(R.id.problem_base_item_list);
        setupRecyclerView();
        return rootView;
    }

    private void setupRecyclerView(){
        if(new CommService(getContext()).isNetConnected()){
            getProblemBaseData=new GetProblemBaseData();
            getProblemBaseData.execute((Void)null);
        }else{
            Snackbar.make(rootView,"网络连接异常！",Snackbar.LENGTH_LONG).show();
        }
    }

  /*  @Override
    public void onResume() {
        super.onResume();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                assert problemBaseListView != null;
                problemBaseListView=(RecyclerView) rootView.findViewById(R.id.problem_base_item_list);
                setupRecyclerView();
            }
        }, 100);

    }*/


    public class ProblemBaseAdapter extends RecyclerView.Adapter<ProblemBaseAdapter.ViewHolder>{

        private List<ProblemBaseData> mValues;

       public ProblemBaseAdapter(List<ProblemBaseData> items){
           this.mValues=items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.problem_base_item_list_content,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder( final  ViewHolder holder,  int position) {
            holder.mItem=mValues.get(position);
            holder.mLab.setText(mValues.get(position).getLab());
            holder.mTender.setText(mValues.get(position).getTender());
            holder.mCheckDate.setText(mValues.get(position).getCheckDate());
            holder.mRiskCatName.setText(mValues.get(position).getRiskCatName());
            holder.mChargePerson.setText(mValues.get(position).getChargePerson());
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getContext(), ProblemBaseDetailActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("checkDate",holder.mItem.getCheckDate());
                    bundle.putString("lab",holder.mItem.getLab());
                    bundle.putString("tender",holder.mItem.getTender());
                    bundle.putString("riskSource",holder.mItem.getRiskSoure());
                    bundle.putString("risks",holder.mItem.getCheckDate());
                    bundle.putString("riskCatName",holder.mItem.getRiskCatName());
                    bundle.putString("credit",holder.mItem.getCredit());
                    bundle.putString("chargeDepartment",holder.mItem.getChargeDepartment());
                    bundle.putString("chargePerson",holder.mItem.getChargePerson());
                    bundle.putString("improveDate",holder.mItem.getImproveDate());
                    bundle.putString("improveMethod",holder.mItem.getImproveMethod());
                    bundle.putString("deadLine",holder.mItem.getDeadLine());
                    bundle.putString("improvement",holder.mItem.getImprovement());
                    bundle.putString("superDepartment",holder.mItem.getSuperDepartment());
                    bundle.putString("supervisor",holder.mItem.getSupervisor());
                    bundle.putString("inspectResult",holder.mItem.getInspectResult());
                    bundle.putString("memo",holder.mItem.getMemo());
                    intent.putExtra("bundle",bundle);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final View mView;
            private final TextView mLab;
            private final TextView mTender;
            private final TextView mCheckDate;
            private final TextView mRiskCatName;
            private final TextView mChargePerson;
            private ProblemBaseData mItem;

            public ViewHolder(View view){
                super(view);
                mView=view;
                mLab= (TextView) view.findViewById(R.id.lab);
                mTender= (TextView) view.findViewById(R.id.tender);
                mCheckDate= (TextView) view.findViewById(R.id.check_date);
                mRiskCatName= (TextView) view.findViewById(R.id.risk_cat_name);
                mChargePerson= (TextView) view.findViewById(R.id.charge_person);
            }

        }
    }


    class GetProblemBaseData extends AsyncTask<Void,Void,Boolean>{


        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                result= CommData.dbWeb.getProblemBaseData(riskCatId);
                if(!result.equals("") && result!=null){
                    return true;
                }else{
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success) {
                problemBaseDataList = ParaseData.toProblemBaseData(result);
                ProblemBaseAdapter problemBaseAdapter = new ProblemBaseAdapter(problemBaseDataList);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                problemBaseListView.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));
                problemBaseListView.setAdapter(problemBaseAdapter);
            }else{
                Snackbar.make(rootView,"没有数据！",Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
