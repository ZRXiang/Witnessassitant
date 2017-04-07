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
import com.example.phobes.witnessassitant.model.MixStation;
import com.example.phobes.witnessassitant.service.CommService;
import com.example.phobes.witnessassitant.util.DateUtil;
import com.example.phobes.witnessassitant.util.ParaseData;

import java.util.Date;
import java.util.List;

/**
 * Created by YLS on 2016/9/25.
 */
public class NewTaskActivity extends AppCompatActivity {

    Toolbar toolbar;
    private EditText applyNumber;
    private EditText projectName;
    private EditText inflictionPosition;
    private Spinner mixStation;
    private EditText mixStationName;
    private EditText planVolume;
    private Spinner intensityLevel;
    private EditText planSlump;
    private EditText supplyPoint;
    private Button predictStartDate;
    private Button predictStartTime;
    private  Button btnOk;
    private Button btnSave;
    private int year;
    private int month;
    private int day;
    private int hour;    // 0-23
    private int minute;
    List<MixStation> mixStationList;
    private String sOption;
    private String sOrgId;
    private String sApplyNumber;
    private String sProjectName;
    private String sInflictionPosition;
    private String sIntensityLevel;
    private String sPlanVolume;
    private String sMixStationName;
    private String sPredictStartTime;
    private int sTaskState;
    private String sPlanSlump;
    private String sDestination;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);
        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra("bundle");
        sOption=bundle.getString("option");
        sIntensityLevel=bundle.getString("intensityLevel");
        sOrgId=bundle.getString("orgId");
        initView();
        addEvent();
        getMixStation();

        if(bundle.getString("option").equals("editTask")){
            if(toolbar!=null) {
                toolbar.setTitle("编辑任务");
            }
            applyNumber.setFocusable(false);
            applyNumber.setText(bundle.getString("applyNumber"));
            projectName.setText(bundle.getString("projectName"));
            inflictionPosition.setText(bundle.getString("inflictionPosition"));
            mixStationName.setText(bundle.getString("mixStationName"));
            planVolume.setText(bundle.getString("planVolume"));
            planSlump.setText(bundle.getString("planSlump"));
            supplyPoint.setText(bundle.getString("destination"));
            sPredictStartTime=bundle.getString("predictStartTime");
            String[] dateTime=sPredictStartTime.split("\\s+");//1个或多个空格
            predictStartDate.setText(dateTime[0]);
            predictStartTime.setText(dateTime[1]);
        }else{
            if(toolbar!=null) {
                toolbar.setTitle("新建任务");
            }
        }
    }

    private void getMixStation(){
        GetMixStation getMixStation=new GetMixStation();
        getMixStation.execute((Void)null);
    }


    private void initView(){
        toolbar= (Toolbar) findViewById(R.id.common_toolbar);
        applyNumber= (EditText) findViewById(R.id.apply_number);
        projectName= (EditText) findViewById(R.id.project_name);
        inflictionPosition= (EditText) findViewById(R.id.infliction_position);
        mixStationName= (EditText) findViewById(R.id.mix_station_name);
        planVolume= (EditText) findViewById(R.id.plan_allocation);
        planSlump= (EditText) findViewById(R.id.plan_slump);
        supplyPoint= (EditText) findViewById(R.id.supply_point);
        mixStation= (Spinner) findViewById(R.id.mix_station);
        intensityLevel= (Spinner) findViewById(R.id.intensity_level);
        predictStartDate= (Button) findViewById(R.id.predict_start_date);
        predictStartTime= (Button) findViewById(R.id.predict_start_time);
        btnOk= (Button) findViewById(R.id.button_ok);
        btnSave= (Button) findViewById(R.id.button_save);


        String[] mCuring_way = {"C10", "C20", "C25", "C30", "C40","C45", "C50", "C60","C65","C7.5"};
        ArrayAdapter<String> curring_Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mCuring_way);
        //绑定 Adapter到控件
        intensityLevel.setAdapter(curring_Adapter);
        if(sOption.equals("editTask")) {
            for (int i = 0; i < mCuring_way.length; i++) {
                if (sIntensityLevel.equals(mCuring_way[i])) {
                    intensityLevel.setSelection(i);
                }
            }
        }

        Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow(); // 取得系统时间。
         year = t.year;
         month = t.month+1;
         day = t.monthDay;
         hour = t.hour;    // 0-23
         minute=t.minute;
        predictStartDate.setText(year+"-"+month+"-"+day);
        predictStartTime.setText(TimeFormat(hour)+":"+TimeFormat(minute));
    }
    private  void addEvent(){

        predictStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(NewTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        predictStartDate.setText(String.format("%d-%d-%d", year,monthOfYear+1,dayOfMonth));
                    }
                }, year, month-1, day).show();
            }
        });

     predictStartTime.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             new TimePickerDialog(NewTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                 @Override
                 public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                     predictStartTime.setText(String.format("%s:%s",TimeFormat(hourOfDay),TimeFormat(minute)));
                 }
             }, hour, minute, true).show();
         }
     });

      btnOk.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              btnOk.setEnabled(false);
              btnSave.setEnabled(false);
              addMixTask(1);
          }
      });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnOk.setEnabled(false);
                btnSave.setEnabled(false);
                addMixTask(0);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        applyNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(applyNumber.getText().toString().equals("")){
                    return;
                }
                if(sOption.equals("newTask")) {
                    IsExitTaskId isExitTaskId = new IsExitTaskId(applyNumber.getText().toString());
                    isExitTaskId.execute((Void) null);
                }
            }
        });
    }
    private void addMixTask(int state){
        if(TextUtils.isEmpty(applyNumber.getText().toString())){
            applyNumber.setError(getString(R.string.error_field_required));
            applyNumber.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(projectName.getText().toString())){
            projectName.setError(getString(R.string.error_field_required));
            projectName.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(inflictionPosition.getText().toString())){
            inflictionPosition.setError(getString(R.string.error_field_required));
            inflictionPosition.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(mixStationName.getText().toString())){
            mixStationName.setError(getString(R.string.error_field_required));
            mixStationName.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(planVolume.getText().toString())){
            planVolume.setError(getString(R.string.error_field_required));
            planVolume.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(planSlump.getText().toString())){
            planSlump.setError(getString(R.string.error_field_required));
            planSlump.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(supplyPoint.getText().toString())){
            supplyPoint.setError(getString(R.string.error_field_required));
            supplyPoint.requestFocus();
            return;
        }

        CommService commService=new CommService(NewTaskActivity.this);
        if(commService.isNetConnected()){
            String time=predictStartDate.getText().toString()+" "+predictStartTime.getText().toString();

            SaveMixTask saveMixTask=new SaveMixTask(applyNumber.getText().toString(),projectName.getText().toString(),inflictionPosition.getText().toString(),
                    ((MixStation) mixStation.getSelectedItem()).getOrgId(),mixStationName.getText().toString(),planVolume.getText().toString(),intensityLevel.getSelectedItem().toString(),
                    planSlump.getText().toString(),supplyPoint.getText().toString(),time,DateUtil.DateToString(new Date()),state);
            saveMixTask.execute((Void)null);
        }else{
            Snackbar.make(applyNumber, "网络连接异常！", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
    }


    public String TimeFormat(int value){
        return value>=10?""+value:"0"+value;
    }

    public class SaveMixTask extends AsyncTask<Void,Void,Boolean>{
        private String applyNumber;
        private String projectName;
        private String inflictionPosition;
        private String mixStation;
        private String mixStationName;
        private String planVolume;
        private String intensityLevel;
        private String planSlump;
        private String supplyPoint;
        private String predictStartTime;
        private String applyTime;
        private int mstate;

        public SaveMixTask(String applyNumber,String projectName,String inflictionPosition,String mixStation,String mixStationName,String planVolume,
                           String intensityLevel,String planSlump,String supplyPoint,String predictStartTime,String applyTime,int state){

            this.applyNumber=applyNumber;
            this.projectName=projectName;
            this.inflictionPosition=inflictionPosition;
            this.mixStation=mixStation;
            this.mixStationName=mixStationName;
            this.planVolume=planVolume;
            this.intensityLevel=intensityLevel;
            this.planSlump=planSlump;
            this.supplyPoint=supplyPoint;
            this.applyNumber=applyNumber;
            this.predictStartTime=predictStartTime;
            this.applyTime=applyTime;
            this.mstate=state;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String result;
                if(sOption.equals("newTask")) {
                    result = CommData.dbWeb.saveMixTask(applyNumber, projectName, inflictionPosition, mixStation, mixStationName, planVolume,
                            intensityLevel, planSlump, supplyPoint, predictStartTime, applyTime,mstate);
                }else{
                    result = CommData.dbWeb.updateMixTask(applyNumber, projectName, inflictionPosition, mixStation, mixStationName, planVolume,
                            intensityLevel, planSlump, supplyPoint, predictStartTime, applyTime,mstate);
                }
                if(result.toLowerCase().equals("1") || result.toLowerCase().equals("true")){
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
                    if(mstate==1) {
                        btnOk.setTextColor(0xFFD0EFC6);
                        btnOk.setEnabled(false);
                    }else{
                        btnSave.setTextColor(0xFFD0EFC6);
                        btnSave.setEnabled(false);
                    }
                    Snackbar.make(btnOk, "上传成功", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }else{
                    btnOk.setEnabled(true);
                    btnSave.setEnabled(true);
                    Snackbar.make(btnOk, "服务器保存失败！", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
        }

        @Override
        protected void onCancelled() {

        }
    }

    public class GetMixStation extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String result=CommData.dbWeb.getMixStation();
                if(!result.equals("")){
                    mixStationList= ParaseData.toMixStation(result);
                    for(int i=0;i<mixStationList.size();i++){
                        String s=CommData.dbWeb.getMixStationParentName(mixStationList.get(i).getOrgId());
                        mixStationList.get(i).setMixStation(s+mixStationList.get(i).getMixStation());
                    }
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
                ArrayAdapter<MixStation> curring_Adapter1 = new ArrayAdapter<MixStation>(NewTaskActivity.this, android.R.layout.simple_list_item_1, mixStationList);
                //绑定 Adapter到控件
                mixStation.setAdapter(curring_Adapter1);
                if(sOption.equals("editTask")) {
                    for (int i = 0; i < mixStationList.size(); i++) {
                        if (sOrgId.equals(mixStationList.get(i).getOrgId())) {
                            mixStation.setSelection(i);
                        }
                    }
                }

            }else{
                Snackbar.make(btnOk, "拌合站没有数据！", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        }

        @Override
        protected void onCancelled() {

        }
    }

    public class IsExitTaskId extends AsyncTask<Void,Void,Boolean>{
        private String taskId;
       public IsExitTaskId(String taskId){
            this.taskId=taskId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String result= CommData.dbWeb.getTaskId(taskId);
                if(!result.equals("")){
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
                applyNumber.setError(getString(R.string.is_exit_task));
                applyNumber.requestFocus();
                btnOk.setEnabled(false);
                btnSave.setEnabled(false);
            }else{
                    btnOk.setEnabled(true);
                    btnSave.setEnabled(true);
            }
        }

        @Override
        protected void onCancelled() {

        }
    }


}
