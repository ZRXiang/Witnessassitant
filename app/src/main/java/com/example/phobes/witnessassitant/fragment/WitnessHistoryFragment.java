package com.example.phobes.witnessassitant.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.WitenessData;
import com.example.phobes.witnessassitant.util.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phobes on 2016/6/7.
 */
public class WitnessHistoryFragment extends Fragment {
    View witenessView = null;
    View rootView;
    private String uploadResult;
    private List<WitenessData> witenessLists = new ArrayList<WitenessData>();
    WitenessItemAdapter witenessItemAdapter;
    private String type;

    public void setType(String type) {
        this.type = type;
    }



    WitenessData witenessData = new WitenessData();

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
        rootView = inflater.inflate(R.layout.history_item_list, container, false);
        witenessView = rootView.findViewById(R.id.history_witness_item_list);
        assert witenessView != null;

        setupRecyclerView((RecyclerView) witenessView);
        return rootView;
    }
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        if (!CommData.dbSqlite.isExitWiteness()) {
//            Snackbar.make(rootView, "没有数据，请先下载任务，再见证", Snackbar.LENGTH_SHORT)
//                    .setAction("Action", null).show();
        }
        witenessLists = CommData.dbSqlite.getWitnessHisory(type);
        witenessItemAdapter = new WitenessItemAdapter(witenessLists);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()) ;
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));
        recyclerView.setAdapter(witenessItemAdapter);
    }

    public class WitenessItemAdapter
            extends RecyclerView.Adapter<WitenessItemAdapter.ViewHolder> {

        private List<WitenessData> mValues;

        public WitenessItemAdapter(List<WitenessData> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.history_item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
           /* DatabaseHelper databaseHelper = new DatabaseHelper(getActivity(), Configuration.DB_NAME, Configuration.DB_VERSION);
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            SqliteService sqliteService = new SqliteService();
            String workflow = sqliteService.getWorkflow(db, holder.mItem.getObject_id());
            boolean isNeedSample = ParaseWitness.isSampleWitness(workflow);
            boolean isNeedTest = ParaseWitness.isTestWitness(workflow);
            boolean isTested = sqliteService.isTested(db,holder.mItem.getWitness_id());
            boolean isSampleed = sqliteService.isSampleed(db,holder.mItem.getWitness_id());

            db.close();*/
            holder.mObjectName.setText(mValues.get(position).getObject_name());
            holder.mSampleIdView.setText(mValues.get(position).getSample_id());
            holder.mBatchNum.setText(mValues.get(position).getBatch_id());
            if(CommData.orgType.equals("监理试验室")){
                if(type.equals("sample")) {
                    holder.mWitnessTimeView.setText(mValues.get(position).getWitness_time());
                }else{
                    holder.mWitnessTimeView.setText(mValues.get(position).getTest_time());
                }
            }else {
                holder.mWitnessTimeView.setText(mValues.get(position).getSample_time());
            }
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
            public final TextView mObjectName;
            public final TextView mSampleIdView;
            public final TextView mWitnessTimeView;
            public final TextView mBatchNum;
            public WitenessData mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mObjectName = (TextView) view.findViewById(R.id.object_name);
                mSampleIdView = (TextView) view.findViewById(R.id.sample_id);
                mWitnessTimeView = (TextView)view.findViewById(R.id.history_witness_time);
                mBatchNum=(TextView)view.findViewById(R.id.batch_num);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mSampleIdView.getText() + "'";
            }
        }
    }

}
