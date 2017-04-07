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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.service.CommService;

/**
 * Created by YLS on 2016/9/25.
 */
public class TestDetectionActivity extends AppCompatActivity {

    Toolbar toolbar;
    private EditText applyNumber;
    private EditText temperature;
    private EditText slump;
    private EditText gasContent;
    private Button productDate;
    private Button productTime;
    private Button detectionDate;
    private Button detectionTime;
    private Button buttonOk;
    private Button buttonCancel;
    private String option;
    private String taskNumber;
    private String orgId;
    private int year;
    private int month;
    private int day;
    private int hour;    // 0-23
    private int minute;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_detection);
        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra("bundle");
        option=bundle.getString("options");
        taskNumber=bundle.getString("applyNumber");
        orgId=bundle.getString("orgId");
        initView();
        addEvent();
    }

    public void initView(){
        toolbar= (Toolbar) findViewById(R.id.common_toolbar);
        applyNumber= (EditText) findViewById(R.id.apply_number);
        temperature= (EditText) findViewById(R.id.temperature);
        slump= (EditText) findViewById(R.id.slump);
        gasContent= (EditText) findViewById(R.id.gas_content);
        productDate= (Button) findViewById(R.id.product_date);
        productTime= (Button) findViewById(R.id.product_time);
        detectionDate= (Button) findViewById(R.id.detection_date);
        detectionTime= (Button) findViewById(R.id.detection_time);
        buttonOk= (Button) findViewById(R.id.button_ok);
        buttonCancel= (Button) findViewById(R.id.button_cancel);

        applyNumber.setText(taskNumber);
        if(toolbar!=null){
            if(option.equals("firstDetection")){
                toolbar.setTitle("首盘检测");
            }else{
                toolbar.setTitle("现场检测");
            }
        }

        Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow(); // 取得系统时间。
         year = t.year;
         month = t.month+1;
         day = t.monthDay;
         hour = t.hour;    // 0-23
         minute=t.minute;
        productDate.setText(year+"-"+month+"-"+day);
        productTime.setText(TimeFormat(hour)+":"+TimeFormat(minute));
        detectionDate.setText(year+"-"+month+"-"+day);
        detectionTime.setText(TimeFormat(hour)+":"+TimeFormat(minute));
        applyNumber.setFocusable(false);
    }
    public void addEvent(){
        productDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(TestDetectionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        productDate.setText(String.format("%d-%d-%d", year,monthOfYear+1,dayOfMonth));
                    }
                }, year, month-1, day).show();
            }
        });
        productTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new TimePickerDialog(TestDetectionActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

                new DatePickerDialog(TestDetectionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                     detectionDate.setText(String.format("%d-%d-%d", year,monthOfYear+1,dayOfMonth));
                    }
                }, year, month-1, day).show();

            }
        });
        detectionTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new TimePickerDialog(TestDetectionActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        detectionTime.setText(String.format("%s:%s",TimeFormat(hourOfDay),TimeFormat(minute)));
                    }
                }, hour, minute, true).show();
            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(temperature.getText().toString())){
                    temperature.setError(getString(R.string.error_field_required));
                    temperature.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(slump.getText().toString())){
                    slump.setError(getString(R.string.error_field_required));
                    slump.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(gasContent.getText().toString())){
                    gasContent.setError(getString(R.string.error_field_required));
                    gasContent.requestFocus();
                    return;
                }

                CommService commService=new CommService(TestDetectionActivity.this);
                if(commService.isNetConnected()){
                 //   String option,String taskNumber,String envTemperature,String productionTime,checkTime,String slump,String gasContent
                    String productionTime=productDate.getText().toString()+" "+productTime.getText().toString();
                    String checkTime=detectionDate.getText().toString()+" "+detectionTime.getText().toString();
                    SaveOrUpdateDetection saveOrUpdateDetection=new SaveOrUpdateDetection(orgId,option,taskNumber,temperature.getText().toString(),productionTime,checkTime,
                            slump.getText().toString(),gasContent.getText().toString());
                    saveOrUpdateDetection.execute((Void)null);
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

    public class SaveOrUpdateDetection extends AsyncTask<Void,Void,String>{

        private String orgId;
        private String option;
        private String taskNumber;
        private String envTemperature;
        private String productionTime;
        private String checkTime;
        private String slump;
        private String gasContent;
      public SaveOrUpdateDetection(String orgId,String option,String taskNumber,String envTemperature,String productionTime,String checkTime,String slump,String gasContent){
          this.orgId=orgId;
          this.option=option;
          this.taskNumber=taskNumber;
          this.envTemperature=envTemperature;
          this.productionTime=productionTime;
          this.checkTime=checkTime;
          this.slump=slump;
          this.gasContent=gasContent;
      }

        @Override
        protected String doInBackground(Void... params) {
            String result= CommData.dbWeb.saveOrUpdateTestDetection(orgId,option,taskNumber,envTemperature,productionTime,checkTime,slump,gasContent);
            if(result.toLowerCase().equals("true")){
                return "true";
            }else if(result.equals("isNull")){
                return "isNull";
            } else{
                return "false";
            }
        }

        @Override
        protected void onPostExecute(final String success) {
            if(success.equals("true")){
                buttonOk.setTextColor(0xFFD0EFC6);
                buttonOk.setEnabled(false);
                Snackbar.make(buttonOk,"上传成功",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
            }else if(success.equals("isNull")){
                buttonOk.setTextColor(0xFFD0EFC6);
                buttonOk.setEnabled(false);
                Snackbar.make(buttonOk,"当前任务还无需检测，请等待...",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
            }else{
                Snackbar.make(buttonOk,"数据保存失败",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
            }
        }
    }
}
