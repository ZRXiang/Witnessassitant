package com.example.phobes.witnessassitant.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.example.phobes.witnessassitant.activity.MainActivity;
import com.example.phobes.witnessassitant.activity.MeasurePointActivity;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.EntryCheckData;
import com.example.phobes.witnessassitant.model.FileUpload;
import com.example.phobes.witnessassitant.model.PointMeta;
import com.example.phobes.witnessassitant.model.SiteTestData;
import com.example.phobes.witnessassitant.model.WitenessData;
import com.example.phobes.witnessassitant.service.CommService;
import com.example.phobes.witnessassitant.service.ListenerThread;
import com.example.phobes.witnessassitant.service.SiteFileUpload;
import com.example.phobes.witnessassitant.struct.TFState;
import com.example.phobes.witnessassitant.util.DatabaseHelper;
import com.example.phobes.witnessassitant.util.DividerItemDecoration;
import com.example.phobes.witnessassitant.util.ParaseData;
import com.example.phobes.witnessassitant.util.ParaseWitness;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by phobes on 2016/6/27.
 * Alter 2016/9/2
 */
public class SiteTestListFragment extends Fragment {
    View siteTestView = null;
    View rootView;
   // private UploadTask mupload = null;
    private String uploadResult;
    private DatabaseHelper dbHelper;
    private List<SiteTestData> siteTestDataList = new ArrayList<SiteTestData>();
    SiteTestItemAdapter siteTestItemAdapter;
    String s="";
    private boolean isRun=true;

    SiteTestData mSiteTestData = new SiteTestData();
    Handler mhandlerSend;
    private Runnable runnable;
    private String result;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mhandlerSend = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (msg.what == 1) {
                       siteTestDataList = CommData.dbSqlite.getTestTaskDatas();
                       try {
                           siteTestItemAdapter.updateList(siteTestDataList);
                       }catch (Exception e){

                           e.printStackTrace();
                       }

                }
            }
        };
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.site_test_list, container, false);  //yang  site_test_list
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
                siteTestView = rootView.findViewById(R.id.site_test_item_list);  //yang   site_test_item_list
                assert siteTestView != null;
                setupRecyclerView((RecyclerView) siteTestView);
            }
        }, 1000);

    }

    @Override
    public void onDestroy() {
        mhandlerSend.removeCallbacks(runnable);
        super.onDestroy();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private void setupRecyclerView(RecyclerView recyclerView) {
       try {

        siteTestDataList = CommData.dbSqlite.getTestTaskDatas();
        System.out.println("list size:" + siteTestDataList.size());
        if(siteTestDataList.size()==0){
            Snackbar.make(rootView, "没有数据！", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
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
                    .inflate(R.layout.site_test_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mTestName.setText(mValues.get(position).getTestName());
            holder.mTestDate.setText(mValues.get(position).getOrderDate());
            holder.mOrderId.setText(mValues.get(position).getOrderId());
            if(mValues.get(position).getTaskStatus()==0){
                holder.mSiteTest.setText("下载");
                holder.mTestSend.setVisibility(View.INVISIBLE);
            }else if(mValues.get(position).getTaskStatus()==1){
                holder.mSiteTest.setText("去做任务");
            }else if(mValues.get(position).getTaskStatus()==2){
                holder.mSiteTest.setText("点击上传");
                holder.mTestSend.setVisibility(View.INVISIBLE);
            }else if(mValues.get(position).getTaskStatus()==3){
                holder.mSiteTest.setText("点击续传");
                holder.mTestSend.setVisibility(View.INVISIBLE);
            }
            holder.mSiteTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch(holder.mItem.getTaskStatus()){
                        case 0:
                             AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("确认下载 "+holder.mItem.getTestName()+" 吗？");
                            builder.setTitle("提示");
                            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                new  Thread(){
                                        @Override
                                        public void run() {
                                            while (isRun) {
                                                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                                String date = sDateFormat.format(new java.util.Date());
                                                try {
                                                    s = CommData.dbWeb.updateTeatTaskStatus(holder.mItem.getDataId(), holder.mItem.getIndexId(), date, 1);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                if (s.toLowerCase().equals("true")) {
                                                    if (CommData.dbSqlite.updateTestTaskStatus(String.valueOf(holder.mItem.getDataId()), String.valueOf(holder.mItem.getIndexId()))) {
                                                        Message msg = mhandlerSend.obtainMessage();
                                                        msg.what = 1;
                                                        mhandlerSend.sendMessage(msg);
                                                        isRun=false;
                                                        if(!isRun){
                                                            String mateIds= CommData.dbSqlite.getPointAndMetaId(holder.mItem.getObjectId());
                                                            GetPointMeta getPointMeta=new GetPointMeta(holder.mItem.getDataId(),mateIds);
                                                            getPointMeta.execute((Void)null);
                                                        }

                                                        Snackbar.make(rootView, "下载任务成功", Snackbar.LENGTH_SHORT)
                                                                .setAction("Action", null).show();
                                                    } else {
                                                        Snackbar.make(rootView, "下载任务失败，请重试！", Snackbar.LENGTH_LONG)
                                                                .setAction("Action", null).show();
                                                    }
                                                } else {
                                                    Snackbar.make(rootView, "服务器任务下载失败！", Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                }
                                            }
                                        }
                                   }.start();
                                  //  mhandlerSend.post(runnable);
                                    dialog.dismiss();
                                }
                            });
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                            CommService commService = new CommService(getActivity());
                            if(!commService.isNetConnected()){
                                Snackbar.make(siteTestView, "没有网络，无法下载", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                return;
                            }
                            builder.create().show();
                           /* if(!isRun){
                                DatabaseHelper dbHelper = new DatabaseHelper(getActivity(), Configuration.DB_NAME,Configuration.DB_VERSION);
                                SqliteService sqliteService = new SqliteService();
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                String mateIds= sqliteService.getPointAndMetaId(db,holder.mItem.getObjectId());
                                GetPointMeta getPointMeta=new GetPointMeta(holder.mItem.getDataId(),mateIds);
                                getPointMeta.execute((Void)null);
                            }*/

                           /* //测试使用--------------------------------------------
                            Bundle bundle=new Bundle();
                            bundle.putInt("objectId",holder.mItem.getObjectId());
                            bundle.putInt("dataId",holder.mItem.getDataId());
                            bundle.putInt("indexId",holder.mItem.getIndexId());
                            bundle.putString("testName",holder.mItem.getTestName());
                            bundle.putString("orderId",holder.mItem.getOrderId());
                            Intent intent=new Intent(getContext(),MeasurePointActivity.class);
                            intent.putExtra("bundle",bundle);
                            startActivity(intent);
                            //测试使用---------------------------------------------*/


                            break;
                        case 1:
                          /* new Thread(){  //用于还原任务状态
                                @Override
                                public void run() {
                                    try {
                                        new WebService(getActivity()).isOldTestTaskStatus(holder.mItem.getDataId(), holder.mItem.getIndexId());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();*/
                            Bundle bundle=new Bundle();
                            bundle.putInt("objectId",holder.mItem.getObjectId());
                            bundle.putInt("dataId",holder.mItem.getDataId());
                            bundle.putInt("indexId",holder.mItem.getIndexId());
                            bundle.putString("testName",holder.mItem.getTestName());
                            bundle.putString("orderId",holder.mItem.getOrderId());
                            Intent intent=new Intent(getContext(),MeasurePointActivity.class);
                            intent.putExtra("bundle",bundle);
                            startActivity(intent);
                            break;
                        case 2:
                            CommService commService2 = new CommService(getActivity());
                            if(!commService2.isNetConnected()){
                                Snackbar.make(siteTestView, "没有网络，无法上传", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                return;
                            }
                            SiteFileUpload siteFileUpload=new SiteFileUpload(mhandlerSend,getContext(),holder.mItem.getDataId(),holder.mItem.getIndexId(),holder.mItem.getOrgId());
                            siteFileUpload.start();
                            break;
                        case 3:
                            CommService commService3 = new CommService(getActivity());
                            if(!commService3.isNetConnected()){
                                Snackbar.make(siteTestView, "没有网络，无法上传", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                return;
                            }
                            SiteFileUpload siteFileUpload1=new SiteFileUpload(mhandlerSend,getContext(),holder.mItem.getDataId(),holder.mItem.getIndexId(),holder.mItem.getOrgId());
                            siteFileUpload1.start();
                            break;
                    }
                }
            });
           holder.mTestSend.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                   builder.setMessage("确认强制上传 "+holder.mItem.getTestName()+" 吗？");
                   builder.setTitle("提示");
                   builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           SiteFileUpload siteFileUpload1=new  SiteFileUpload(mhandlerSend,getContext(),holder.mItem.getDataId(),holder.mItem.getIndexId(),holder.mItem.getOrgId());
                           siteFileUpload1.start();
                           dialog.dismiss();
                       }
                   });
                   builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {

                           dialog.dismiss();
                       }
                   });
                   CommService commService = new CommService(getActivity());
                   if(!commService.isNetConnected()){
                       Snackbar.make(siteTestView, "没有网络，无法上传", Snackbar.LENGTH_LONG)
                               .setAction("Action", null).show();
                       return;
                   }
                   builder.create().show();
               }
           });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public void updateList(List<SiteTestData> data) {
            mValues = data;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mTestName;
            public final TextView mTestDate;
            public final TextView mOrderId;
            public SiteTestData mItem;
            public final Button mSiteTest;
            public final Button mTestSend;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTestName=(TextView) view.findViewById(R.id.test_name);
                mTestDate=(TextView)view.findViewById(R.id.test_date);
                mSiteTest = (Button) view.findViewById(R.id.button_site_test);
                mTestSend= (Button) view.findViewById(R.id.button_test_send);
                mOrderId= (TextView) view.findViewById(R.id.orderId);
                mTestSend.setVisibility(View.GONE);
            }

        }
    }

    public class GetPointMeta extends AsyncTask<Void,Void,Boolean>{

        private int dataId;
        private String metaIds;

        public GetPointMeta(int dataId,String metaIds){
            this.dataId=dataId;
            this.metaIds=metaIds;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            result=CommData.dbWeb.getPointMeta(dataId,metaIds);
            if(result.equals("")){
                return false;
            }else{
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean succuss) {
            if(succuss){
                List<PointMeta> pointMetas= ParaseData.toPointMeta(result);
                CommData.dbSqlite.insertSiteTestDetail(pointMetas);
            }
        }
    }

}
