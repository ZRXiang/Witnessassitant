package com.example.phobes.witnessassitant.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.MixMachine;
import com.example.phobes.witnessassitant.model.OptionPerson;
import com.example.phobes.witnessassitant.service.CommService;
import com.example.phobes.witnessassitant.util.DateUtil;
import com.example.phobes.witnessassitant.util.ParaseData;

import java.util.Date;
import java.util.List;

/**
 * Created by YLS on 2016/9/25.
 */
public class HearTaskActivity extends AppCompatActivity {

    Toolbar toolbar;
    private TextView taskNumber;
    private TextView projectName;
    private TextView inflictionPosition;
    private TextView intensityLevel;
    private TextView planAllocation;
    private TextView mixStationName;
    private TextView predictStartTime;
    private TextView applyPerson;
    private TextView applyTime;
    private Spinner optionParson;
    private EditText allocationNum;
    private Spinner mixMachine;
    private Button btnOk;
    private Button btnCancel;
    private static String orgId;
    List<MixMachine> mixMachineList;
    List<OptionPerson> optionPersonList;
    String mixMachineResult;
    String optionPersonResult;
    Boolean isNll=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hear_task);
        initView();
        addEvent();
        getSpinnerData();
    }

    public void initView(){
        toolbar= (Toolbar) findViewById(R.id.common_toolbar);
        taskNumber= (TextView) findViewById(R.id.task_number);
         projectName= (TextView) findViewById(R.id.project_name);
         inflictionPosition= (TextView) findViewById(R.id.infliction_position);
         intensityLevel= (TextView) findViewById(R.id.intensity_level);
         planAllocation= (TextView) findViewById(R.id.plan_allocation);
         mixStationName= (TextView) findViewById(R.id.mix_name);
         predictStartTime= (TextView) findViewById(R.id.predict_start_time);
         applyPerson= (TextView) findViewById(R.id.apply_person);
         applyTime= (TextView) findViewById(R.id.apply_time);
         optionParson= (Spinner) findViewById(R.id.option_parson);
         allocationNum= (EditText) findViewById(R.id.allocation_num);
         mixMachine= (Spinner) findViewById(R.id.mix_machine);
         btnOk= (Button) findViewById(R.id.button_ok);
         btnCancel= (Button) findViewById(R.id.button_cancel);

         if(toolbar!=null){
            toolbar.setTitle("受理任务");
        }

        Intent intent=getIntent();
        Bundle bundle= intent.getBundleExtra("bundle");
        orgId= bundle.getString("orgId");
        taskNumber.setText(bundle.getString("applyNumber"));
        projectName.setText(bundle.getString("projectName"));
        inflictionPosition.setText(bundle.getString("inflictionPosition"));
        intensityLevel.setText(bundle.getString("intensityLevel"));
        planAllocation.setText( bundle.getString("planVolume"));
        mixStationName.setText( bundle.getString("mixStationName"));
        predictStartTime.setText(bundle.getString("predictStartTime"));
        applyPerson.setText(bundle.getString("applicant"));
        applyTime.setText(bundle.getString("applyTime"));
    }

    public void addEvent(){
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(allocationNum.getText())){
                    allocationNum.setError(getString(R.string.error_field_required));
                    allocationNum.requestFocus();
                    return;
                }
                float volume=Float.parseFloat(allocationNum.getText().toString());
                float planVolume=Float.parseFloat(planAllocation.getText().toString());
                if(volume!=planVolume){
                    allocationNum.setError("分配方量与设计方量不匹配！");
                    return;
                }
                CommService commService=new CommService(HearTaskActivity.this);
                if(commService.isNetConnected()){
                    SaveHearTask saveHearTask=new SaveHearTask(taskNumber.getText().toString(),planAllocation.getText().toString(),
                            optionParson.getSelectedItem().toString(),((MixMachine)mixMachine.getSelectedItem()).getDeviceId(), DateUtil.DateTimeToString(new Date()));
                    saveHearTask.execute((Void)null);
                }else{
                    Snackbar.make(btnOk, "网络连接异常！", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void getSpinnerData(){
        CommService commService=new CommService(HearTaskActivity.this);
        if(commService.isNetConnected()){
            GetMixMachine getMixMachine=new GetMixMachine();
            getMixMachine.execute((Void)null);
            GetOptionPerson getOptionPerson=new GetOptionPerson();
            getOptionPerson.execute((Void) null);
        }else{
            Snackbar.make(btnOk,"网络连接异常！",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
        }
    }

    public class SaveHearTask extends AsyncTask<Void,Void,Boolean>{

            private String taskNumber;
            private String planVolume;
            private String optionParson;
            private String mixMachine;
            private String time;
        public SaveHearTask(String taskNumber,String planVolume,String optionParson,String mixMachine,String time){
            this.taskNumber=taskNumber;
            this.planVolume=planVolume;
            this.optionParson=optionParson;
            this.mixMachine=mixMachine;
            this.time=time;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String result=CommData.dbWeb.saveHearTask(taskNumber,orgId,planVolume,optionParson,mixMachine,time);
                if(result.toLowerCase().equals("1")){
                    return true;
                }
                /*else if(result.equals("isNull")){
                    isNll=true;
                    return false;
                } */
                else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success ) {
          if(success){
              btnOk.setTextColor(0xFFD0EFC6);
              btnOk.setEnabled(false);
              Snackbar.make(btnOk,"操作成功",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
          }else{
             /* if(isNll){
                  Snackbar.make(btnOk,"当前任务已受理，无法再次受理！",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                  isNll=false;
              }else {*/
                  Snackbar.make(btnOk, "数据保存失败", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
              //}
          }
        }

        @Override
        protected void onCancelled() {

        }
    }

    public class GetMixMachine extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                mixMachineResult=CommData.dbWeb.getMixMachine(orgId);
                if(!mixMachineResult.equals("")){
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
                mixMachineList= ParaseData.toMixMachine(mixMachineResult);
                ArrayAdapter<MixMachine> curring_Adapter= new ArrayAdapter<MixMachine>(HearTaskActivity.this, android.R.layout.simple_list_item_1, mixMachineList);
                //绑定 Adapter到控件
                mixMachine.setAdapter(curring_Adapter);
            }else{
                Snackbar.make(btnOk, "拌合机没有数据！", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        }

        @Override
        protected void onCancelled() {
            mixMachineResult=null;
        }
    }

    public class GetOptionPerson extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                optionPersonResult= CommData.dbWeb.getOptionPerson(orgId);
                if(!optionPersonResult.equals("")){
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
                optionPersonList= ParaseData.toOptionPerson(optionPersonResult);
                ArrayAdapter<OptionPerson> curring_Adapter= new ArrayAdapter<OptionPerson>(HearTaskActivity.this, android.R.layout.simple_list_item_1, optionPersonList);
                //绑定 Adapter到控件
                optionParson.setAdapter(curring_Adapter);
            }else{
                Snackbar.make(btnOk, "操作工没有数据！", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        }

        @Override
        protected void onCancelled() {

        }
    }
}
