package com.example.phobes.witnessassitant.fragment;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.activity.PointItemActivity;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.MeasurePointData;
import com.example.phobes.witnessassitant.model.PointMeta;
import com.example.phobes.witnessassitant.util.DividerItemDecoration;
import com.example.phobes.witnessassitant.util.ParaseData;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YLS on 2016/9/4.
 */
public class MeasurePointListFragment extends Fragment {
    View measurePointView = null;
    View rootView;
    private List<MeasurePointData> measurePointDataLists = new ArrayList<MeasurePointData>();

    private int mObjectId;
    private int mDataId;
    private int mIndexId;
    private  String mTestName;
    private String mOrederId;
    private String result;

    MeasurePointItemAdapter measurePointItemAdapter;

    public String getmTestName() {
        return mTestName;
    }

    public void setmTestName(String mTestName) {
        this.mTestName = mTestName;
    }

    public int getmIndexId() {
        return mIndexId;
    }

    public void setmIndexId(int mIndexId) {
        this.mIndexId = mIndexId;
    }

    public int getmObjectId() {
        return mObjectId;
    }

    public void setmObjectId(int mObjectId) {
        this.mObjectId = mObjectId;
    }

    public int getmDataId() {
        return mDataId;
    }

    public void setmDataId(int mDataId) {
        this.mDataId = mDataId;
    }

    public String getmOrederId() {
        return mOrederId;
    }

    public void setmOrederId(String mOrederId) {
        this.mOrederId = mOrederId;
    }


    /*public void addPoint(){
        DatabaseHelper dbHelper = new DatabaseHelper(getContext(), Configuration.DB_NAME,Configuration.DB_VERSION);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SqliteService sqliteService = new SqliteService();
        List<PointMeta> pointMetas= sqliteService.getPointNum(db,mDataId);
        for(int i=1;i<=pointMetas.size();i++){
            int mate=100135*100+i;
            for(int j=0;j<pointMetas.size();j++){
                if(mate==pointMetas.get(j).getMetaId()){
                    measurePointItemAdapter.addItem(mObjectId,mDataId,mIndexId,pointMetas.get(j).getMetaValue());
                    continue;
                }
            }
        }
    }*/

    public  void add(int objectId,int dataId,int indexId,String pointName){
         measurePointItemAdapter.addItem(objectId,dataId,indexId,pointName);
    }

    public void update(){

        measurePointDataLists = CommData.dbSqlite.getSiteTestAndMeasurePointData(mDataId,mIndexId);
        measurePointItemAdapter.updateList(measurePointDataLists);
    }
    public Boolean isCount(){
        return measurePointItemAdapter.isCount();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.measure_point_item_list, container, false);
        measurePointView = rootView.findViewById(R.id.measure_point_item_list);
        assert measurePointView != null;
        setupRecyclerView((RecyclerView) measurePointView);
        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        measurePointDataLists = CommData.dbSqlite.getSiteTestAndMeasurePointData(mDataId,mIndexId);
        measurePointItemAdapter = new MeasurePointItemAdapter(measurePointDataLists);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()) ;
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));
        recyclerView.setAdapter(measurePointItemAdapter);
    }

    public class MeasurePointItemAdapter
            extends RecyclerView.Adapter<MeasurePointItemAdapter.ViewHolder> {

        private List<MeasurePointData> mValues;

        public MeasurePointItemAdapter(List<MeasurePointData> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.measure_point_item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mMeasurePointName.setText("桩号"+mValues.get(position).getPointName());
            if(mValues.get(position).getMeasurePointStatus()==0){
                holder.mMeasurePointStatus.setText("未完成");
            }else if(mValues.get(position).getMeasurePointStatus()==1){
                holder.mMeasurePointStatus.setText("已完成");
            }else if(mValues.get(position).getMeasurePointStatus()==2){
                holder.mMeasurePointStatus.setText("已上传");
            }
            holder.mMeasurePointStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( holder.mItem.getMeasurePointStatus()==0) {
                        Intent intent = new Intent(getActivity(), PointItemActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("pTestName", getmTestName());
                        bundle.putInt("pSN", holder.mItem.getSN());
                        bundle.putInt("pPointId", holder.mItem.getPointId());
                        bundle.putInt("pDataId", holder.mItem.getDataId());
                        bundle.putString("orderId", getmOrederId());
                        intent.putExtra("pBundle", bundle);
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public void updateList(List<MeasurePointData> data) {
            mValues = data;
            notifyDataSetChanged();
        }

        public Boolean isCount(){
            int testCount=CommData.dbSqlite.getTestCount(mDataId,mIndexId);
            if(testCount==getItemCount()){
                return false;
            }else{
                return true;
            }
        }

        public Boolean isHas(String pointName){
            boolean isHas=CommData.dbSqlite.isPointName(mDataId,pointName);
            if(isHas){
                return true;
            }else{
                return false;
            }
        }

        public void addItem(int objectId,int dataId,int indexId,String pointName){
            if(isHas(pointName)){
                return;
            }
            MeasurePointData data=new MeasurePointData();
            data.setDataId(dataId);
            data.setIndexId(indexId);
            data.setSN(getItemCount()+1);
            data.setPointName(pointName);
            data.setMeasurePointStatus(0);
            data.setReceiveState(0);
            try {
              int result= (int) CommData.dbSqlite.saveSiteTestData(data);
                if(result>0) {
                    data.setPointId(result);
                    CommData.dbSqlite.setSiteTestDetail(objectId,result,dataId,data.getSN());
                    mValues.add(data);
                    notifyDataSetChanged();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mMeasurePointName;
            public final Button mMeasurePointStatus;
            public MeasurePointData mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mMeasurePointName = (TextView) view.findViewById(R.id.measure_point_name);
                mMeasurePointStatus=(Button)view.findViewById(R.id.button_point_status);
            }

            @Override
            public String toString() {
                //  return super.toString() + " '" + mSampleIdView.getText() + "'";
                return super.toString();
            }
        }
    }


}
