package com.example.phobes.witnessassitant.fragment;

import android.database.sqlite.SQLiteDatabase;
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
import com.example.phobes.witnessassitant.model.EntryCheckData;
import com.example.phobes.witnessassitant.util.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phobes on 2016/6/27.
 */
public class EntryCheckHistory extends Fragment {
    View witenessView = null;
    View rootView;
    private List<EntryCheckData> entryCheckDataLists = new ArrayList<EntryCheckData>();
    WitenessItemAdapter witenessItemAdapter;

    EntryCheckData entryCheckData = new EntryCheckData();

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
        entryCheckDataLists = CommData.dbSqlite.getEntryCheckHisory();
        witenessItemAdapter = new WitenessItemAdapter(entryCheckDataLists);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()) ;
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));
        recyclerView.setAdapter(witenessItemAdapter);
    }

    public class WitenessItemAdapter
            extends RecyclerView.Adapter<WitenessItemAdapter.ViewHolder> {

        private List<EntryCheckData> mValues;

        public WitenessItemAdapter(List<EntryCheckData> items) {
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
            String objectName = CommData.dbSqlite.getObjectName(mValues.get(position).getObjectId());
            holder.mObjectName.setText(objectName);
            holder.mSampleIdView.setText(mValues.get(position).getProductName());
            holder.mBatchNum.setText(mValues.get(position).getBatchId());
            if(CommData.orgType.equals("监理试验室")){
                holder.mWitnessTimeView.setText(mValues.get(position).getSuperCheckDate());
            }else {
                holder.mWitnessTimeView.setText(mValues.get(position).getLabCheckDate());
            }
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
            public final TextView mObjectName;
            public final TextView mSampleIdView;
            public final TextView mWitnessTimeView;
            public final TextView mBatchNum;
            public EntryCheckData mItem;

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
