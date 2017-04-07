package com.example.phobes.witnessassitant.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.service.CommService;
import com.example.phobes.witnessassitant.util.DateUtil;

import java.util.Date;
import java.util.zip.Inflater;

/**
 * Created by YLS on 2016/9/25.
 */
public class MixTaskDetailActivity extends AppCompatActivity {

    private final String SUP_PERMISSION = "监理试验室";
    private final String MIX_PERMISSION = "拌和站";
    private final String MIX_DUTY="站长";
    private final String SITE_DUTY="施工队长";
    private final String WA_PERMISSION = "工区试验室";
    private final String CENTER_PERMISSION = "中心试验室";
    private final String GROUP_PERMISSION = "试验室组";
    Toolbar toolbar;
    private TextView firstDetection;
    private TextView siteDetection;
    private TextView abolish;
    private TextView watchProgress;
    private TextView hearTask;
    private TextView firstConfirm;
    private TextView siteConfirm;
    private TextView editTask;
    private String orgId;
    private String applyNumber;
    private String projectName;
    private String inflictionPosition;
    private String intensityLevel;
    private String planVolume;
    private String mixStationName;
    private String predictStartTime;
    private String applicant;
    private String applyTime;
    private int taskState;
    private String planSlump;
    private String destination;
   // private View dialog;
    private String reason;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mix_task_detail);
        Intent intent=getIntent();
        Bundle bundle= intent.getBundleExtra("bundle");
        orgId= bundle.getString("orgId");
        applyNumber= bundle.getString("applyNumber");
        projectName= bundle.getString("projectName");
        inflictionPosition= bundle.getString("inflictionPosition");
        intensityLevel= bundle.getString("intensityLevel");
        planVolume= bundle.getString("planVolume");
        mixStationName= bundle.getString("mixStationName");
        predictStartTime= bundle.getString("predictStartTime");
        applicant= bundle.getString("applicant");
        applyTime= bundle.getString("applyTime");
        planSlump=bundle.getString("planSlump");
        destination=bundle.getString("destination");
        taskState=bundle.getInt("taskState",0);
        initView();
        addEvent();
        isShow();
    }
    public void initView(){
      toolbar= (Toolbar) findViewById(R.id.common_toolbar);
      firstDetection= (TextView) findViewById(R.id.first_detection);
      siteDetection= (TextView) findViewById(R.id.site_detection);
        abolish= (TextView) findViewById(R.id.abolish);
        watchProgress= (TextView) findViewById(R.id.watch_progress);
        hearTask= (TextView) findViewById(R.id.hear_task);
        firstConfirm= (TextView) findViewById(R.id.first_confirm);
        siteConfirm= (TextView) findViewById(R.id.site_confirm);
        editTask= (TextView) findViewById(R.id.edit_task);
       // abolishReason= (EditText) findViewById(R.id.abolish_reason);
        if(toolbar!=null){
            toolbar.setTitle("任务详情");
        }

    }

    public void addEvent(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        firstDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MixTaskDetailActivity.this,TestDetectionActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("options","firstDetection");
                bundle.putString("applyNumber",applyNumber);
                bundle.putString("orgId",orgId);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });

        siteDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MixTaskDetailActivity.this,TestDetectionActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("options","siteDetection");
                bundle.putString("applyNumber",applyNumber);
                bundle.putString("orgId",orgId);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });

        abolish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et = new EditText(MixTaskDetailActivity.this);
                et.setHint("废除理由");
                new AlertDialog.Builder(MixTaskDetailActivity.this).setTitle("你确定将 任务单号:"+applyNumber+" 废除吗？").setIcon(
                         android.R.drawable.ic_dialog_info).setView(et).
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                     reason=  et.getText().toString();
                        CommService commService=new CommService(MixTaskDetailActivity.this);
                        if(commService.isNetConnected()){
                            if(reason.equals("")){
                                Snackbar.make(firstDetection,"废除理由不能为空！",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                                return;
                            }
                            AbolishTask abolishTask=new AbolishTask(applyNumber,reason);
                            abolishTask.execute((Void)null);
                        }else{
                            Snackbar.make(firstDetection,"网络连接异常",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                        }
                    }
                })
                 .setNegativeButton("取消", null).show();
            }
        });

        watchProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MixTaskDetailActivity.this,WatchProgressActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("applyNumber",applyNumber);
                bundle.putString("orgId",orgId);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });


        hearTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(MixTaskDetailActivity.this, HearTaskActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("applyNumber", applyNumber);
                    bundle.putString("orgId", orgId);
                    bundle.putString("projectName", projectName);
                    bundle.putString("inflictionPosition", inflictionPosition);
                    bundle.putString("intensityLevel", intensityLevel);
                    bundle.putString("planVolume", planVolume);
                    bundle.putString("mixStationName", mixStationName);
                    bundle.putString("predictStartTime", predictStartTime);
                    bundle.putString("applicant", applicant);
                    bundle.putString("applyTime", applyTime);
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);

            }
        });

        firstConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(MixTaskDetailActivity.this, TestConfirmActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("options", "firstConfirm");
                    bundle.putString("applyNumber", applyNumber);
                    bundle.putString("orgId", orgId);
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);

            }
        });

        siteConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(MixTaskDetailActivity.this, TestConfirmActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("options", "siteConfirm");
                    bundle.putString("applyNumber", applyNumber);
                    bundle.putString("orgId", orgId);
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);

            }
        });

        editTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(MixTaskDetailActivity.this, NewTaskActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("option","editTask");
                    bundle.putString("applyNumber", applyNumber);
                    bundle.putString("orgId", orgId);
                    bundle.putString("projectName", projectName);
                    bundle.putString("inflictionPosition", inflictionPosition);
                    bundle.putString("intensityLevel", intensityLevel);
                    bundle.putString("planVolume", planVolume);
                    bundle.putString("mixStationName", mixStationName);
                    bundle.putString("predictStartTime", predictStartTime);
                    bundle.putString("applicant", applicant);
                    bundle.putString("applyTime", applyTime);
                    bundle.putString("planSlump",planSlump);
                    bundle.putString("destination",destination);
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);

            }
        });

    }

    public void isShow(){
        switch (taskState){
            case 0:
                if(CommData.orgType.equals(MIX_PERMISSION) && CommData.duty.equals(SITE_DUTY)){ //
                    firstDetection.setVisibility(View.GONE);
                    siteDetection.setVisibility(View.GONE);
                    firstConfirm.setVisibility(View.GONE);
                    siteConfirm.setVisibility(View.GONE);
                    hearTask.setVisibility(View.GONE);
                    abolish.setVisibility(View.GONE);
                    watchProgress.setVisibility(View.GONE);
                   // editTask.setVisibility(View.GONE);
                }else{
                    firstDetection.setVisibility(View.GONE);
                    siteDetection.setVisibility(View.GONE);
                    abolish.setVisibility(View.GONE);
                    editTask.setVisibility(View.GONE);
                    hearTask.setVisibility(View.GONE);
                    firstConfirm.setVisibility(View.GONE);
                    siteConfirm.setVisibility(View.GONE);
                    watchProgress.setVisibility(View.GONE);
                }
                break;
            case 1:
                    if(CommData.orgType.equals(MIX_PERMISSION) && CommData.duty.equals(MIX_DUTY)){
                        firstDetection.setVisibility(View.GONE);
                        siteDetection.setVisibility(View.GONE);
                        firstConfirm.setVisibility(View.GONE);
                        siteConfirm.setVisibility(View.GONE);
                        editTask.setVisibility(View.GONE);
                    }else{
                        firstDetection.setVisibility(View.GONE);
                        siteDetection.setVisibility(View.GONE);
                        hearTask.setVisibility(View.GONE);
                        editTask.setVisibility(View.GONE);
                        firstConfirm.setVisibility(View.GONE);
                        siteConfirm.setVisibility(View.GONE);
                    }
                break;
            case 2:
                firstDetection.setVisibility(View.GONE);
                siteDetection.setVisibility(View.GONE);
                hearTask.setVisibility(View.GONE);
                editTask.setVisibility(View.GONE);
                firstConfirm.setVisibility(View.GONE);
                siteConfirm.setVisibility(View.GONE);
                abolish.setVisibility(View.GONE);
                break;
            case 3:
                firstDetection.setVisibility(View.GONE);
                siteDetection.setVisibility(View.GONE);
                hearTask.setVisibility(View.GONE);
                editTask.setVisibility(View.GONE);
                firstConfirm.setVisibility(View.GONE);
                siteConfirm.setVisibility(View.GONE);
                abolish.setVisibility(View.GONE);
                break;
            case 4:
                if(CommData.orgType.equals(WA_PERMISSION) || CommData.orgType.equals(CENTER_PERMISSION) || CommData.orgType.equals(GROUP_PERMISSION)){
                    siteDetection.setVisibility(View.GONE);
                    hearTask.setVisibility(View.GONE);
                    abolish.setVisibility(View.GONE);
                    editTask.setVisibility(View.GONE);
                    firstConfirm.setVisibility(View.GONE);
                    siteConfirm.setVisibility(View.GONE);
                }else if(CommData.orgType.equals(SUP_PERMISSION)){
                    firstDetection.setVisibility(View.GONE);
                    siteDetection.setVisibility(View.GONE);
                    abolish.setVisibility(View.GONE);
                    editTask.setVisibility(View.GONE);
                    hearTask.setVisibility(View.GONE);
                    siteConfirm.setVisibility(View.GONE);
                }else{
                    firstDetection.setVisibility(View.GONE);
                    siteDetection.setVisibility(View.GONE);
                    hearTask.setVisibility(View.GONE);
                    editTask.setVisibility(View.GONE);
                    firstConfirm.setVisibility(View.GONE);
                    siteConfirm.setVisibility(View.GONE);
                    abolish.setVisibility(View.GONE);
                }
                break;
            case 5:
                if(CommData.orgType.equals(WA_PERMISSION) || CommData.orgType.equals(CENTER_PERMISSION) || CommData.orgType.equals(GROUP_PERMISSION)) {
                    firstDetection.setVisibility(View.GONE);
                    hearTask.setVisibility(View.GONE);
                    abolish.setVisibility(View.GONE);
                    editTask.setVisibility(View.GONE);
                    firstConfirm.setVisibility(View.GONE);
                    siteConfirm.setVisibility(View.GONE);
                }else if(CommData.orgType.equals(SUP_PERMISSION)){
                    firstDetection.setVisibility(View.GONE);
                    siteDetection.setVisibility(View.GONE);
                    abolish.setVisibility(View.GONE);
                    editTask.setVisibility(View.GONE);
                    hearTask.setVisibility(View.GONE);
                    firstConfirm.setVisibility(View.GONE);
                }else{
                    firstDetection.setVisibility(View.GONE);
                    siteDetection.setVisibility(View.GONE);
                    hearTask.setVisibility(View.GONE);
                    editTask.setVisibility(View.GONE);
                    firstConfirm.setVisibility(View.GONE);
                    siteConfirm.setVisibility(View.GONE);
                    abolish.setVisibility(View.GONE);
                }
                break;
            case 6:
                firstDetection.setVisibility(View.GONE);
                siteDetection.setVisibility(View.GONE);
                abolish.setVisibility(View.GONE);
                editTask.setVisibility(View.GONE);
                hearTask.setVisibility(View.GONE);
                firstConfirm.setVisibility(View.GONE);
                siteConfirm.setVisibility(View.GONE);
                break;
            case 7:
                firstDetection.setVisibility(View.GONE);
                siteDetection.setVisibility(View.GONE);
                abolish.setVisibility(View.GONE);
                editTask.setVisibility(View.GONE);
                hearTask.setVisibility(View.GONE);
                firstConfirm.setVisibility(View.GONE);
                siteConfirm.setVisibility(View.GONE);
                break;
            default:
                firstDetection.setVisibility(View.GONE);
                siteDetection.setVisibility(View.GONE);
                abolish.setVisibility(View.GONE);
                editTask.setVisibility(View.GONE);
                hearTask.setVisibility(View.GONE);
                firstConfirm.setVisibility(View.GONE);
                siteConfirm.setVisibility(View.GONE);
                watchProgress.setVisibility(View.GONE);
                break;
        }
    }

    public class AbolishTask extends AsyncTask<Void,Void,Boolean> {

        private String abolishReason;
        private String taskNumber;
        public AbolishTask(String taskNumber,String abolishReason){
            this.taskNumber=taskNumber;
            this.abolishReason=abolishReason;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String result= null;
            try {
                result = CommData.dbWeb.abolishTask(taskNumber,abolishReason, DateUtil.DateTimeToString(new Date()));
                if(result.toLowerCase().equals("1")){
                    return true;
                }else{
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success){
                Snackbar.make(firstDetection,"废除任务成功",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
            }else{
                Snackbar.make(firstDetection,"废除任务失败，服务器更新失败",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
            }
        }
    }

    private boolean checkPermission(String permission) {
        if (CommData.orgType.equals(permission)) {
            return true;
        } else {
            Snackbar.make(firstDetection, "你没有权限执行操作", Snackbar.LENGTH_SHORT).show();
            return false;
        }
    }
}
