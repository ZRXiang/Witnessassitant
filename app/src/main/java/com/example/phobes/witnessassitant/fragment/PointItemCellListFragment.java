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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.MeasurePointData;
import com.example.phobes.witnessassitant.model.PointItemData;
import com.example.phobes.witnessassitant.util.DividerItemDecoration;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YLS on 2016/9/9.
 */
public class PointItemCellListFragment extends Fragment {
    View  pointItemView = null;
    View rootView;
    private List<PointItemData> pointItemDataList = new ArrayList<PointItemData>();
    PointItemCellAdapter pointItemCellAdapter;
    private int mPointId;
    private int mDataId;

    public int getmDataId() {
        return mDataId;
    }

    public void setmDataId(int mDataId) {
        this.mDataId = mDataId;
    }

    public int getmPointId() {
        return mPointId;
    }

    public void setmPointId(int mPointId) {
        this.mPointId = mPointId;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.point_item, container, false);
        pointItemView = rootView.findViewById(R.id.point_item_cell_list);
        assert pointItemView != null;

        setupRecyclerView((RecyclerView) pointItemView);
        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        try {
            pointItemDataList = CommData.dbSqlite.getSiteTestDetail(mPointId,mDataId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        pointItemCellAdapter = new PointItemCellAdapter(pointItemDataList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()) ;
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));
        recyclerView.setAdapter(pointItemCellAdapter);
    }

    public class PointItemCellAdapter
            extends RecyclerView.Adapter<PointItemCellAdapter.ViewHolder> {

        private List<PointItemData> mValues;

        public PointItemCellAdapter(List<PointItemData> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.point_item_cell, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mPointMetaName.setText(mValues.get(position).getMetaName());
            holder.mPointMetaValue.setText(mValues.get(position).getItemValue());
            holder.mPointMetaValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus) {
                        CommData.dbSqlite.updateSiteTestDetailData(holder.mItem.getPointId(), holder.mItem.getDataId(), holder.mItem.getMetaId(), holder.mPointMetaValue.getText().toString());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mPointMetaName;
            public final EditText mPointMetaValue;
            public PointItemData mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mPointMetaName = (TextView) view.findViewById(R.id.text_meta_name);
                mPointMetaValue=(EditText)view.findViewById(R.id.edit_meta_value);
            }

            @Override
            public String toString() {
                //  return super.toString() + " '" + mSampleIdView.getText() + "'";
                return super.toString();
            }
        }
    }
}
