package com.example.phobes.witnessassitant.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.activity.MeasurePointActivity;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.EntryCheckData;
import com.example.phobes.witnessassitant.model.SiteTestData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by phobes on 2016/6/27.
 */
public class SiteTestHistory extends Fragment {

    View siteTestView = null;
    View rootView;
    private String uploadResult;
    private List<SiteTestData> siteTestDataList = new ArrayList<SiteTestData>();
    SiteTestItemAdapter siteTestItemAdapter;
    String s="";

    SiteTestData mSiteTestData = new SiteTestData();
    Handler mhandlerSend;




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

        rootView = inflater.inflate(R.layout.site_test_history_list, container, false);  //yang  site_test_list
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("SitTestHistory Resume: 1");
                siteTestView = rootView.findViewById(R.id.site_test_history);  //yang   site_test_item_list
                assert siteTestView != null;
                setupRecyclerView((RecyclerView) siteTestView);
            }
        }, 1000);

    }

   /* public void updateSiteTestHistory(){
        DatabaseHelper database = new DatabaseHelper(getActivity(), Configuration.DB_NAME, Configuration.DB_VERSION);
        SQLiteDatabase dbTest = database.getReadableDatabase();
        sqliteService = new SqliteService();
        siteTestDataList = sqliteService.getTestTaskHistory(dbTest);
        dbTest.close();
        siteTestItemAdapter.updateList(siteTestDataList);
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
       try {
           siteTestDataList = CommData.dbSqlite.getTestTaskHistory();

           siteTestItemAdapter = new SiteTestItemAdapter(siteTestDataList);
           recyclerView.setAdapter(siteTestItemAdapter);
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    public class SiteTestItemAdapter
            extends RecyclerView.Adapter<SiteTestItemAdapter.ViewHolder> {

        private List<SiteTestData> mValues;

        public SiteTestItemAdapter(List<SiteTestData> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.site_test_history_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mTestName.setText(mValues.get(position).getTestName());
            holder.mTestDate.setText(mValues.get(position).getOrderDate());

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

       public void updateList(List<SiteTestData> dataList){
           mValues=dataList;
           notifyDataSetChanged();
       }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mTestName;
            public final TextView mTestDate;
            public SiteTestData mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTestName=(TextView) view.findViewById(R.id.test_history_name);
                mTestDate=(TextView)view.findViewById(R.id.test_history_date);
            }

        }
    }



}
