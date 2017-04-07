package com.example.phobes.witnessassitant.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.activity.SampleGuildItemDetailActivity;
import com.example.phobes.witnessassitant.activity.TestObjectListActivity;
import com.example.phobes.witnessassitant.activity.WitenessApplyActivity;
import com.example.phobes.witnessassitant.activity.WitnessMainActivity;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.TestObject;
import com.example.phobes.witnessassitant.util.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phobes on 2016/6/3.
 */
public class TestObjectListFragment extends Fragment {
    View TestObjectrecyclerView = null;
    private List<TestObject.TestObjectItem> sampleGuildItems = new ArrayList<TestObject.TestObjectItem>();
    TestObjectItemAdapter sampleGuildItemAdapter;
    SearchView mSearchView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.sample_guild_item_list, container, false);
        TestObjectrecyclerView = rootview.findViewById(R.id.sample_guild_item_list);
        assert TestObjectrecyclerView != null;
        setupRecyclerView((RecyclerView) TestObjectrecyclerView);
        return rootview;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        if (!CommData.dbSqlite.isExitTestObject()) {
            CommData.dbSqlite.insertTestObjects();
        }
        sampleGuildItems = CommData.dbSqlite.getTestObjects();
        sampleGuildItemAdapter = new TestObjectItemAdapter(sampleGuildItems);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));
        recyclerView.setAdapter(sampleGuildItemAdapter);
    }

    public class TestObjectItemAdapter
            extends RecyclerView.Adapter<TestObjectItemAdapter.ViewHolder> {
        private List<TestObject.TestObjectItem> mValues;
        public TestObjectItemAdapter(List<TestObject.TestObjectItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sample_guild_item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(mValues.get(position).name);
            Log.e("after onclick", "after onclick");
            holder.mContentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() instanceof TestObjectListActivity) {
                        System.out.println("guild");
                        Context context = v.getContext();
                        Intent intent = new Intent(context, SampleGuildItemDetailActivity.class);
                        intent.putExtra(SampleGuildItemDetailFragment.ARG_ITEM_ID, holder.mItem.object_id);
                        context.startActivity(intent);
                    }
                    if (getActivity() instanceof WitnessMainActivity) {
                        System.out.println("witeness");
                        Context context = v.getContext();
                        Intent intent = new Intent(context, WitenessApplyActivity.class);
                        intent.putExtra(WitenessApplyDetailFragment.ARG_ITEM_ID, holder.mItem.object_id);
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public void updateList(List<TestObject.TestObjectItem> data) {
            mValues = data;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mContentView;
            public TestObject.TestObjectItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.sample_guild_item_content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    public void changeTestObjects(int id) {
        System.out.println(id);
        if (!CommData.dbSqlite.isExitTestObject()) {
            CommData.dbSqlite.insertTestObjects();
        }
        sampleGuildItems = CommData.dbSqlite.getTestObjects(id);
        if(sampleGuildItems.size()==0){
            Snackbar.make(TestObjectrecyclerView, "没有数据", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        sampleGuildItemAdapter.updateList(sampleGuildItems);
    }
    public void changeTestObjects(String  objectName) {

        if (!CommData.dbSqlite.isExitTestObject()) {
            CommData.dbSqlite.insertTestObjects();
        }
        sampleGuildItems = CommData.dbSqlite.getTestObjectsByName(objectName);
        sampleGuildItemAdapter.updateList(sampleGuildItems);
    }
}
