package com.example.phobes.witnessassitant.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.service.CommService;
import com.example.phobes.witnessassitant.util.DateUtil;

import java.util.Date;

/**
 * Created by phobes on 2016/6/21.
 */
public class OutRoomActivity extends AppCompatActivity {
    public static final String ARG_ORG_ID = "orgId";
    public static final String ARG_NOTICE_ID = "noticeId";
    public static final String ARG_SAMPLE_ID = "sampleId";
    public static final String ARG_STRENGTH = "strength";
    public static final String ARG_POS = "pos";
    public static final String NOT_EXIST = "NOT_EXIST";
    private final int OUT_ROOM_REQ_CODE = 3;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private String orgId;
    private String noticeId;
    private String sampleId;
    private String strength;
    private String pos;
    public static final String UPLOADED = "UPLOADED";
    private int uploaded = 0;
    private String errorInf="服务器保存异常！！！";

    private UploadWitness uploadWitness = null;
    Toolbar toolbar;
    private EditText tvOrgId;
    private EditText tvSampleId;
    private EditText tvNoticeId;
    private EditText tvStrength;
    private EditText tvPos;
    private Button btQrScan;
    private Button btOk;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inroom);
        findViewById();
        addEvents();
        initViews();
    }

    private void findViewById() {
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        tvOrgId = (EditText) findViewById(R.id.org_id);
        tvSampleId = (EditText) findViewById(R.id.sample_id);
        tvNoticeId = (EditText) findViewById(R.id.notice_id);
        tvStrength = (EditText) findViewById(R.id.strength);
        tvPos = (EditText) findViewById(R.id.pos);
        btQrScan = (Button) findViewById(R.id.qr_capture);
        btOk = (Button) findViewById(R.id.ok_button);
        findViewById(R.id.position_label).setVisibility(View.GONE);
        findViewById(R.id.position).setVisibility(View.GONE);


    }

    private void addEvents() {
        btQrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OutRoomActivity.this, CaptureActivity.class);
                intent.putExtra("operation", "outRoom");
                startActivityForResult(intent, OUT_ROOM_REQ_CODE);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                attmpUpload();
                upload();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       try {
           int errorTag = data.getIntExtra(SampleProductActivity.ERROR_SCAN, 0);
           if (errorTag == 1) {
               Snackbar.make(tvSampleId, "扫描出错，请手动输入", Snackbar.LENGTH_LONG).show();
               return;
           }
           orgId = data.getStringExtra(ARG_ORG_ID);
           noticeId = data.getStringExtra(ARG_NOTICE_ID);
           strength = data.getStringExtra(ARG_STRENGTH);
           pos = data.getStringExtra(ARG_POS);
           sampleId = data.getStringExtra(ARG_SAMPLE_ID);

       }catch (Exception e){
           e.printStackTrace();
       }
        initViews();
    }

    public void onBackPressed() {
        super.onBackPressed();
        //getBack();
    }
    private void openQrWrapper() {
        int hasOpenCameraPermission = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasOpenCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            if (hasOpenCameraPermission != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    showMessageOKCancel("你需要授权打开摄像头",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[] {Manifest.permission.CAMERA},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[] {Manifest.permission.CAMERA},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
//TODO
            }else {
                openQr();
            }
        }else {
            openQr();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    openQr();
                } else {
                    // Permission Denied
                    Snackbar.make(tvStrength, "拒绝打开摄像头", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private void openQr(){
        Intent intent = new Intent(OutRoomActivity.this,CaptureActivity.class);
       // intent.putExtra("operation","inRoom");  yang 160922
        intent.putExtra("operation","outRoom");
        startActivityForResult(intent,OUT_ROOM_REQ_CODE);
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(OutRoomActivity.this)
                .setMessage(message)
                .setPositiveButton("确定", okListener)
                .setNegativeButton("取消", null)
                .create()
                .show();
    }
    private void getBack() {
        Intent intent = new Intent(OutRoomActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initViews() {
        if (toolbar != null) {
            toolbar.setTitle(OutRoomActivity.this.getResources().getString(R.string.out_room_button));
        }
        tvOrgId.setText(orgId);
        tvSampleId.setText(sampleId);
        tvNoticeId.setText(noticeId);
        tvPos.setText(pos);
        tvStrength.setText(strength);

    }

    private void attmpUpload() {
        tvOrgId.setError(null);
        tvNoticeId.setError(null);
        tvSampleId.setError(null);
        tvStrength.setError(null);
        tvPos.setError(null);
        boolean cancel = false;
        View focusView = null;
      /*  if(TextUtils.isEmpty(pos)){
            tvPos.setError(getString(R.string.error_field_required));
            focusView=tvPos;
            cancel = true;
        }
        if(TextUtils.isEmpty(strength)){
            tvStrength.setError(getString(R.string.error_field_required));
            focusView=tvStrength;
            cancel = true;
        }
        if(TextUtils.isEmpty(noticeId)){
            tvNoticeId.setError(getString(R.string.error_field_required));
            focusView=tvNoticeId;
            cancel = true;
        }
        if(TextUtils.isEmpty(sampleId)){
            tvSampleId.setError(getString(R.string.error_field_required));
            focusView=tvSampleId;
            cancel = true;
        }
        if(TextUtils.isEmpty(orgId)){
            tvOrgId.setError(getString(R.string.error_field_required));
            focusView=tvOrgId;
            cancel = true;
        }*/
        if (cancel) {
            focusView.requestFocus();
        } else {
            upload();
        }
    }

    private void upload() {
        CommService commService = new CommService(this);
        if (commService.isNetConnected()) {
            uploadWitness = new UploadWitness(orgId, noticeId, sampleId);
            uploadWitness.execute((Void) null);
        } else {
            Snackbar.make(tvStrength, "没有网络，请在网络通畅的时候进行出库上报", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public class UploadWitness extends AsyncTask<Void, Void, Boolean> {
        private String orgId;
        private String sampleId;
        private String noticeId;

        public UploadWitness(String orgId, String noticeId, String sampleId) {
            this.orgId = orgId;
            this.noticeId = noticeId;
            this.sampleId = sampleId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String result = CommData.dbWeb.outRootWitness(CommData.username, DateUtil.DateTimeToString(new Date()), orgId, noticeId, sampleId);
                if (result.equals(NOT_EXIST)) {
                    return false;
                } else if (result.equals(UPLOADED)) {
                    uploaded = 1;
                    return false;
                }else if(result.equals("SUCCESS")){
                    return true;
                }else{
                    uploaded=2;
                    errorInf=result;
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            uploadWitness = null;
            String message;
            if (success) {
                message = "上传成功";
            } else {
                if (uploaded == 1) {
                    message = "已上传，无法再次上传";
                }else if(uploaded == 2){
                    message=errorInf;
                } else {
                    message = "上传失败,数据库没有该记录";
                }
            }
            Snackbar.make(tvStrength, message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }
}
