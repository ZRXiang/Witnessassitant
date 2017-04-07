package com.example.phobes.witnessassitant.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.service.CommService;
import com.example.phobes.witnessassitant.util.DateUtil;
import com.example.phobes.witnessassitant.util.ParaseData;

import java.util.Date;

/**
 * Created by YLS on 2016/9/25.
 */
public class TestConfirmActivity extends AppCompatActivity {

    Toolbar toolbar;
    private EditText applyNumber;
    private EditText temperature;
    private EditText slump;
    private EditText gasContent;
    private EditText productTime;
    private EditText detectionTime;
    private Button buttonOk;
    private Button buttonCancel;
    private Spinner detectionResult;
    private EditText detectionOpinion;
    private String option;
    private String taskNumber;
    private String orgId;
    private int year;
    private int month;
    private int day;
    private int hour;    // 0-23
    private int minute;
    private String result;
    private int id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_confirm);
        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra("bundle");
        option=bundle.getString("options");
        taskNumber=bundle.getString("applyNumber");
        orgId=bundle.getString("orgId");
        initView();
        getTestData();
        addEvent();
    }

    public void initView(){
        toolbar= (Toolbar) findViewById(R.id.common_toolbar);
        applyNumber= (EditText) findViewById(R.id.task_number);
        temperature= (EditText) findViewById(R.id.temperature);
        slump= (EditText) findViewById(R.id.slump);
        gasContent= (EditText) findViewById(R.id.gas_content);
        productTime= (EditText) findViewById(R.id.product_time);
        detectionTime= (EditText) findViewById(R.id.detection_time);
        buttonOk= (Button) findViewById(R.id.button_ok);
        buttonCancel= (Button) findViewById(R.id.button_cancel);
        detectionResult= (Spinner) findViewById(R.id.detection_result);
        detectionOpinion= (EditText) findViewById(R.id.detection_opinion);

        applyNumber.setText(taskNumber);
        if(toolbar!=null){
            if(option.equals("firstConfirm")){
                toolbar.setTitle("首盘确认");
            }else{
                toolbar.setTitle("现场确认");
            }
        }

        String[] mCuring_way = {"合格","不合格"};
        ArrayAdapter<String> curring_Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mCuring_way);
        //绑定 Adapter到控件
        detectionResult.setAdapter(curring_Adapter);

        applyNumber.setFocusable(false);
        temperature.setFocusable(false);
        slump.setFocusable(false);
        gasContent.setFocusable(false);
        productTime.setFocusable(false);
        detectionTime.setFocusable(false);

       /* Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow(); // 取得系统时间。
         year = t.year;
         month = t.month;
         day = t.monthDay;
         hour = t.hour;    // 0-23
         minute=t.minute;
        productDate.setText(year+"-"+month+"-"+day);
        productTime.setText(hour+":"+minute);
        detectionDate.setText(year+"-"+month+"-"+day);
        detectionTime.setText(hour+":"+minute);*/

    }

    private void getTestData(){
        CommService commService=new CommService(TestConfirmActivity.this);
        if(commService.isNetConnected()){
            GetDetectionData getDetectionData=new GetDetectionData();
            getDetectionData.execute((Void)null);
        }
    }

    public void addEvent(){
     /*
     *  点击事件注释
     * productDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(TestConfirmActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        productDate.setText(String.format("%d-%d-%d", year,monthOfYear+1,dayOfMonth));
                    }
                }, year, month, day).show();
            }
        });
        productTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new TimePickerDialog(TestConfirmActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        productTime.setText(String.format("%s:%s",TimeFormat(hourOfDay),TimeFormat(minute)));
                    }
                }, hour, minute, true).show();
            }
        });
        detectionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(TestConfirmActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        detectionDate.setText(String.format("%d-%d-%d", year,monthOfYear+1,dayOfMonth));
                    }
                }, year, month, day).show();

            }
        });
        detectionTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(TestConfirmActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        detectionTime.setText(String.format("%s:%s",TimeFormat(hourOfDay),TimeFormat(minute)));
                    }
                }, hour, minute, true).show();
            }
        });*/

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommService commService=new CommService(TestConfirmActivity.this);
                if(commService.isNetConnected()){
                    //   String option,String taskNumber,String envTemperature,String productionTime,checkTime,String slump,String gasContent
                   // String productionTime=productDate.getText().toString()+" "+productTime.getText().toString();
                   // String checkTime=detectionDate.getText().toString()+" "+detectionTime.getText().toString();
                   // SaveOrUpdateConfirm saveOrUpdateConfirm=new SaveOrUpdateConfirm(orgId,option,taskNumber,temperature.getText().toString(),productionTime,checkTime,
                    //   slump.getText().toString(),gasContent.getText().toString(),detectionResult.getSelectedItem().toString(),detectionOpinion.getText().toString());
                    SaveOrUpdateConfirm saveOrUpdateConfirm=new SaveOrUpdateConfirm(taskNumber,detectionResult.getSelectedItem().toString(),detectionOpinion.getText().toString(), DateUtil.DateTimeToString(new Date()));
                    saveOrUpdateConfirm.execute((Void)null);
                }else{
                    Snackbar.make(buttonOk,"网络连接异常",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                }

            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public String TimeFormat(int value){
        return value>=10?""+value:"0"+value;
    }


    public class SaveOrUpdateConfirm extends AsyncTask<Void,Void,Boolean> {

       /* private String orgId;
        private String envTemperature;
        private String productionTime;
        private String checkTime;
        private String slump;
        private String gasContent;*/
        private String taskNumber;
        private String detectionResult;
        private String detectionOpinion;
        private String confirmTime;
       /* public SaveOrUpdateConfirm(String orgId,String option,String taskNumber,String envTemperature,String productionTime,String checkTime,String slump,
                                     String gasContent,String detectionResult,String detectionOpinion){*/

        public SaveOrUpdateConfirm(String taskNumber,String detectionResult,String detectionOpinion,String confirmTime){
            /*this.orgId=orgId;
            this.envTemperature=envTemperature;
            this.productionTime=productionTime;
            this.checkTime=checkTime;
            this.slump=slump;
            this.gasContent=gasContent;*/
            this.taskNumber=taskNumber;
            this.detectionResult=detectionResult;
            this.detectionOpinion=detectionOpinion;
            this.confirmTime=confirmTime;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
           // String result=webService.saveOrUpdateTestConfirm(orgId,option,taskNumber,envTemperature,productionTime,checkTime,slump,gasContent,detectionResult,detectionOpinion);
            String result=CommData.dbWeb.updateTestConfirm(id,option,taskNumber,detectionResult,detectionOpinion,confirmTime);
            if(result.toLowerCase().equals("true")){
                return true;
            }else{
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success){
                buttonOk.setTextColor(0xFFD0EFC6);
                buttonOk.setEnabled(false);
                Snackbar.make(buttonOk,"上传成功",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
            }else{
                Snackbar.make(buttonOk,"数据保存失败",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
            }
        }
    }

    public class GetDetectionData extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if(option.equals("firstConfirm")){
                    result= CommData.dbWeb.getFirstDetectionData(taskNumber);
                }else{
                    result=CommData.dbWeb.getSiteDetectionData(taskNumber);
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            if(!result.equals("")){
                return true;
            }else{
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success){
               String[] strData= ParaseData.splitDataColumn(result);
                if(option.equals("firstConfirm")) {
                    id=Integer.parseInt(strData[0]);
                    temperature.setText(strData[3]);
                    slump.setText(strData[8]);
                    gasContent.setText(strData[9]);
                    productTime.setText(strData[6]);
                    detectionTime.setText(strData[7]);
                }else{
                    id=Integer.parseInt(strData[0]);
                    temperature.setText(strData[3]);
                    slump.setText(strData[8]);
                    gasContent.setText(strData[10]);
                    productTime.setText(strData[6]);
                    detectionTime.setText(strData[7]);
                }
            }else{
                Snackbar.make(buttonOk,"当前检测任务未完成，无法确认！",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                buttonOk.setTextColor(0xFFD0EFC6);
                buttonOk.setEnabled(false);
            }
        }
    }

}
