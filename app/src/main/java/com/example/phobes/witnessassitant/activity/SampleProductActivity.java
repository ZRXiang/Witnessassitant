package com.example.phobes.witnessassitant.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.service.CommService;
import com.example.phobes.witnessassitant.util.DateUtil;

import java.util.Date;

/**
 * Created by phobes on 2016/6/20.
 */
public class SampleProductActivity extends AppCompatActivity {
    public static final String ARG_ORG_ID = "orgId";
    public static final String ERROR_SCAN="scanError";
    public static final String ARG_NOTICE_ID = "noticeId";
    public static final String ARG_SAMPLE_ID = "sampleId";
    public static final String ARG_STRENGTH = "strength";
    public static final String ARG_POS = "pos";
    public static final String NOT_EXIST = "NOT_EXIST";
    private final int PRODUCT_REQ_CODE = 0;
    private String orgId;
    private String noticeId;
    private String sampleId;
    private String strength;
    private String pos;
    private String time;
    private String curing_way;
    private String use;
    private UploadWitness uploadWitness = null;
    public static final String UPLOADED = "UPLOADED";

    private int uploaded = 0;
    private String errorInf="服务器保存异常！！！";
    Toolbar toolbar;
    EditText edTime;
    Spinner curingSpinner;
    private EditText tvOrgId;
    private EditText tvSampleId;
    private EditText tvNoticeId;
    private EditText tvStrength;
    private EditText tvPos;
    private EditText edUse;
    private Button btQrScan;
    private Button btOk;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_product);
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
        curingSpinner = (Spinner) findViewById(R.id.curing_way_list);


        edTime = (EditText) findViewById(R.id.time_list);
        edUse = (EditText) findViewById(R.id.use_edit);
        findViewById(R.id.time).setVisibility(View.GONE);
        findViewById(R.id.use_label).setVisibility(View.GONE);
        edUse.setVisibility(View.GONE);
        edTime.setVisibility(View.GONE);
    }

    private void addEvents() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              // getBack(); yang
                onBackPressed();
            }
        });
        btQrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SampleProductActivity.this,CaptureActivity.class);
                intent.putExtra("operation","sampleProduct");
                startActivityForResult(intent,PRODUCT_REQ_CODE);
            }
        });

        curingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                curing_way = (String) curingSpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

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
    public void onBackPressed() {
        super.onBackPressed();
      //  getBack();   yang
    }
    private void getBack(){
        Intent intent = new Intent(SampleProductActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
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

    private void initViews() {
        if (toolbar != null) {
            toolbar.setTitle(SampleProductActivity.this.getResources().getString(R.string.sample_product_witness_button));
        }
        tvOrgId.setText(orgId);
        tvSampleId.setText(sampleId);
        tvNoticeId.setText(noticeId);
        tvPos.setText(pos);
        tvStrength.setText(strength);
        String[] mCuring_way = {"标准养护", "同条件养护", "蒸气养护", "28d同条件转标养", "56d同条件转标养", "同条件等效养护600°C", "同条件等效养护1200°C"};
        ArrayAdapter<String> curring_Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mCuring_way);
        //绑定 Adapter到控件
        curingSpinner.setAdapter(curring_Adapter);
    }

    private void attmpUpload() {
        tvOrgId.setError(null);
        tvNoticeId.setError(null);
        tvSampleId.setError(null);
        tvStrength.setError(null);
        tvPos.setError(null);
        boolean cancel = false;
        View focusView = null;
        use = edUse.getText().toString();
      /*  if (TextUtils.isEmpty(use)) {
            cancel = true;
            focusView = edUse;
            edUse.setError(getString(R.string.error_field_required));
        }
        if(TextUtils.isEmpty(pos)){
            tvPos.setError(getString(R.string.error_field_required));
            focusView=tvPos;
            cancel = true;
        }
        if(TextUtils.isEmpty(strength)){
            tvStrength.setError(getString(R.string.error_field_required));
            focusView=tvStrength;
            cancel = true;
        }*/
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
        }

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
        }else{
            Snackbar.make(tvStrength, "没有网络，请在网络通畅的时候进行制作见证", Snackbar.LENGTH_LONG)
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
                String result = CommData.dbWeb.productingWitness(CommData.username, DateUtil.DateTimeToString(new Date()), orgId, noticeId, sampleId,
                        use, curing_way, time);
                if (result.equals(NOT_EXIST)) {
                    return false;
                } else if (result.equals(UPLOADED)) {
                    uploaded = 1;
                    return false;
                } else if(result.equals("SUCCESS")){
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
