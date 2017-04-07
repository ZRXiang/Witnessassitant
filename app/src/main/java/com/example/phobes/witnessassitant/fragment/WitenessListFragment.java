package com.example.phobes.witnessassitant.fragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.activity.WitenessActivity;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.WitenessData;
import com.example.phobes.witnessassitant.service.CommService;
import com.example.phobes.witnessassitant.service.LogWriter;
import com.example.phobes.witnessassitant.util.DatabaseHelper;
import com.example.phobes.witnessassitant.util.ParaseWitness;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by phobes on 2016/6/7.
 */
public class WitenessListFragment extends Fragment {
    View witenessView = null;
    View rootView;
    private int uploadWitnessId = -1;
    private final int REQUEST_CODE_ASK_READ_PERMISSIONS = 123;
    private UploadTask mupload = null;
    private String uploadDataResult="";
    private boolean uploadResult;
    private DatabaseHelper dbHelper;
    private List<WitenessData> witenessLists = new ArrayList<WitenessData>();
    WitenessItemAdapter witenessItemAdapter;
    WitenessData witenessData = new WitenessData();
    private String type;
    private final int packLength=100*1024;
    ProgressDialog mProgressDialog;

    public void setType(String type) {
        this.type = type;
    }

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

        rootView = inflater.inflate(R.layout.witeness_item_list, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                witenessView = rootView.findViewById(R.id.witeness_item_list);
                assert witenessView != null;
                setupRecyclerView((RecyclerView) witenessView);
            }
        }, 1000);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        if(CommData.witnessType.equals("sampleWitness")){
                witenessLists = CommData.dbSqlite.getWiteness("downLoad","sample");//downLoad 为已下载的数据，显示在见证页面
        }
        else {
                witenessLists = CommData.dbSqlite.getTestWiteness();
        }
        System.out.println("list size:" + witenessLists.size());

        witenessItemAdapter = new WitenessItemAdapter(witenessLists);
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
                    .inflate(R.layout.witeness_item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            String workflow = CommData.dbSqlite.getWorkflow(holder.mItem.getObject_id());
            final boolean isNeedSample = ParaseWitness.isSampleWitness(workflow);
            boolean isNeedTest = ParaseWitness.isTestWitness(workflow);
            boolean isTested = CommData.dbSqlite.isTested(holder.mItem.getWitness_id());
            int nStage = CommData.dbSqlite.TestProcess( holder.mItem.getWitness_id());
            final boolean isSampleed = CommData.dbSqlite.isSampleed(holder.mItem.getWitness_id());
            if (CommData.witnessType.equals("sampleWitness")) {
                if (isNeedSample && !isSampleed) {
                    holder.bSampleWitness.setVisibility(View.VISIBLE);
                    holder.bTestWitness.setVisibility(View.GONE);
                    holder.bUpload.setVisibility(View.GONE);
                } else if (isNeedSample && isSampleed) {
                    holder.bSampleWitness.setVisibility(View.GONE);
                    holder.bTestWitness.setVisibility(View.GONE);
                    holder.bUpload.setVisibility(View.VISIBLE);
                }else if(!isNeedSample){
                    holder.bSampleWitness.setVisibility(View.VISIBLE);
                    holder.bTestWitness.setVisibility(View.GONE);
                    holder.bUpload.setVisibility(View.GONE);
                }
            } else if (CommData.witnessType.equals("testWitness")) {
                holder.bSampleWitness.setVisibility(View.GONE);
                if (nStage==0){
                    holder.bTestWitness.setVisibility(View.VISIBLE);
                    holder.bUpload.setVisibility(View.GONE);
                }
                else if(nStage==1){
                    holder.bTestWitness.setVisibility(View.VISIBLE);
                    holder.bUpload.setVisibility(View.GONE);
                }
                else if(nStage==2) {
                    holder.bTestWitness.setVisibility(View.VISIBLE);
                    holder.bUpload.setVisibility(View.VISIBLE);
                }
                else if(nStage==3) {
                    holder.bTestWitness.setVisibility(View.VISIBLE);
                    holder.bUpload.setVisibility(View.GONE);
                }
                else if(nStage==4) {
                    holder.bTestWitness.setVisibility(View.GONE);
                    holder.bUpload.setVisibility(View.VISIBLE);
                }
            }
            holder.mObjectName.setText(mValues.get(position).getObject_name());
            holder.mApplyTimeView.setText(mValues.get(position).getApply_time());
            holder.mSampleIdView.setText(mValues.get(position).getSample_id());
            holder.mOrgNameView.setText(mValues.get(position).getSample_org_name());
            holder.bSampleWitness.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, WitenessActivity.class);
                    intent.putExtra(WitenessFragment.ARG_ITEM_ID, holder.mItem.getWitness_id());
                    intent.putExtra(WitenessFragment.WITNESS_TYPE, "sample");
                    context.startActivity(intent);
                }
            });
            holder.bTestWitness.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  if(isNeedSample&&isSampleed){
                        Context context = v.getContext();
                        Intent intent = new Intent(context, WitenessActivity.class);
                        intent.putExtra(WitenessFragment.ARG_ITEM_ID, holder.mItem.getWitness_id());
                        intent.putExtra(WitenessFragment.WITNESS_TYPE, "test");
                        context.startActivity(intent);
                   /* }
                    else {
                        Snackbar.make(witenessView, "你需要先取样见证", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }*/

                }
            });
            holder.bUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //WriteLog(CommData.DEBUG,"--点击上传按钮");
                    CommService commService = new CommService(getActivity());
                    if (commService.isNetConnected()) {
                        mProgressDialog = new ProgressDialog(getContext());
                        mProgressDialog.setMessage("数据上传中...");
                        mProgressDialog.setIndeterminate(false);
                        mProgressDialog.setCanceledOnTouchOutside(false);
                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        mProgressDialog.setCancelable(true);
                        mProgressDialog.show();
                        uploadWrapper(holder.mItem.getWitness_id());
                    } else {
                        Snackbar.make(witenessView, "没有网络，无法上传", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });
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
            public final TextView mOrgNameView;
            public final TextView mApplyTimeView;
            public WitenessData mItem;
            public final Button bUpload;
            public final Button bSampleWitness;
            public final Button bTestWitness;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mObjectName = (TextView) view.findViewById(R.id.object_name);
                mSampleIdView = (TextView) view.findViewById(R.id.sample_id);
                mOrgNameView = (TextView) view.findViewById(R.id.org_name);
                mApplyTimeView = (TextView) view.findViewById(R.id.apply_time);
                bSampleWitness = (Button) view.findViewById(R.id.button_sample_witness);
                bTestWitness = (Button) view.findViewById(R.id.button_test_witness);
                bUpload = (Button) view.findViewById(R.id.button_upload);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mSampleIdView.getText() + "'";
            }
        }
    }

    private void uploadWrapper(int witnessId) {
        uploadWitnessId = witnessId;

        if (Build.VERSION.SDK_INT >= 24) {
            try{
               upload();
            }catch (Exception e) {
               // WriteLog(CommData.DEBUG,"--上传失败");
            }
        }
        else if (Build.VERSION.SDK_INT >= 23) {
           // WriteLog(CommData.DEBUG,"--Android版本="+android.os.Build.VERSION.SDK_INT);
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showMessageOKCancel("你需要授权读取sd卡",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_READ_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_READ_PERMISSIONS);
                return;

            } else {
                upload();
            }
        } else {
            upload();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_READ_PERMISSIONS:
                if ((permissions.length>0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission Granted
                    upload();
                } else {
                    // Permission Denied
                    Snackbar.make(rootView, "拒绝打开sd卡", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("确定", okListener)
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    private void upload() {
        if (uploadWitnessId != -1) {
            witenessData = CommData.dbSqlite.getWitenessWithPic(uploadWitnessId);
        }
        uploadWitnessId = -1;
        mupload = new UploadTask(witenessData);
        mupload.execute((Void) null);
    }

    public class UploadTask extends AsyncTask<Void, Void, Boolean> {
        private WitenessData inwitenessData;

        public UploadTask(WitenessData witenessData) {
            this.inwitenessData = witenessData;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            int nTotal=0, nSucc = 0;
            try {
               // File path =  getActivity().getExternalFilesDir(null);
                //File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                File Path = new File("/mnt/sdcard/tmp1/gtwise");
                if (!Path.exists()) {
                    LogWriter.log(CommData.DEBUG,"create dir ");
                    Path.mkdirs();
                }
                if(type.equals("sample")){
                    if(CommData.orgType.equals("监理试验室")){
                        uploadResult= UploadFile(Path, inwitenessData.getWitness_image());
                    }else{
                        uploadResult= UploadFile(Path, inwitenessData.getSample_image());
                    }
                }else{
                    int nFail = 0;
                    WitenessData wData = CommData.dbSqlite.getUnUploadedItem(inwitenessData.getWitness_id());
                    while (wData.getWitness_id()!=0) {
                        nTotal +=1;
                        uploadResult = UploadFile(Path, wData.getTest_image());
                        if (uploadResult) {
                            String sRes =  CommData.dbWeb.uploadWitnessTask(wData);
                            if (sRes.toLowerCase().equals("true")) {
                                if (CommData.dbSqlite.updateWitenessTask(wData))
                                    nSucc +=1;
                            }
                        }
                        else{
                            nFail += 1;
                            if(nFail>3) break;
                        }

                        wData = CommData.dbSqlite.getUnUploadedItem(inwitenessData.getWitness_id());
                    }
                    uploadResult = ((nSucc + nTotal == 0) || (nSucc!=0));
                }

               // isNeedSampleWitness = ParaseWitness.isSampleWreplaceitness(workflow);
               // isNeedTestWitness = ParaseWitness.isTestWitness(workflow);
                LogWriter.log(CommData.INFO,"upload data type "+type);
                if(uploadResult) {
                    uploadDataResult = CommData.dbWeb.uploadWitness(witenessData, type);
                }
                if (uploadResult && uploadDataResult.toLowerCase().equals("true") ) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                LogWriter.log(CommData.ERROR,"取样见证上传，异常："+e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                if(CommData.witnessType.equals("sampleWitness")){
                    CommData.dbSqlite.setUploadWiteness(inwitenessData,1);
                    onResume();
                    mProgressDialog.dismiss();
                    Snackbar.make(witenessView, "上传成功", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                else {
                    if (CommData.dbSqlite.TestProcess(inwitenessData.getWitness_id()) == 5)
                          CommData.dbSqlite.setUploadWiteness(inwitenessData,3);
                    onResume();
                    mProgressDialog.dismiss();
                    Snackbar.make(witenessView, "上传成功", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }else{
                mProgressDialog.dismiss();
                Snackbar.make(witenessView, "上传失败！", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
        @Override
        protected void onCancelled() {
            mupload = null;
        }
    }

    public boolean UploadFile(File path,String fileName) throws IOException {
        FileInputStream in=null;
        int nIndex = 0;
        int nFileSize=0;
        int nLeft=0;
        int nUploadLen=0;
        int nOffset = 0;
        int nTemp = 0;
        String s="";

        File file=new File(path,fileName);

        if (!file.exists()) {
            LogWriter.log(CommData.DEBUG,"--文件不存在！");
            return false;
        }

        try {
            in=new FileInputStream(file);
            nFileSize=in.available();
        } catch(IOException e){
            LogWriter.log(CommData.DEBUG,"--打开流失败");
        }

        nIndex=CommData.dbSqlite.getFileIndex(fileName, type);

        nLeft=nFileSize-nIndex*packLength;
        nOffset = nIndex * packLength;
        in.skip(nOffset);

        while (nLeft>0){
            if(nLeft>packLength)
                nUploadLen=packLength;
            else
                nUploadLen=nLeft;
            byte[] buff=new byte[nUploadLen];
            try {
                nTemp = in.read(buff);
            }catch (IOException e){
                LogWriter.log(CommData.ERROR, "--读取文件失败："+e.getMessage());
                in.close();
                return false;
            }
            try {
                    s = CommData.dbWeb.siteUploadFile1(buff, fileName, nIndex, packLength);
                    if (Integer.parseInt(s) > 0) {
                        nIndex++;
                        nLeft -= nUploadLen;
                        nOffset += nUploadLen;
                        LogWriter.log(CommData.INFO, "--当前包索引：" + nIndex + ";包长度：" + nUploadLen + "该包上传成功");
                        if (!CommData.dbSqlite.updateFileIndex(nIndex, fileName, type)) {
                            LogWriter.log(CommData.ERROR, "--更新数据库异常：" + fileName + ";类型：" + type );
                            in.close();
                            return false;
                        }

                    }
            } catch (Exception e) {
                    LogWriter.log(CommData.ERROR, "--文件上传异常：" + e.getMessage());
                    in.close();
                    return false;
            }
        }
       // file.delete();
        in.close();
        return true;
    }
}
