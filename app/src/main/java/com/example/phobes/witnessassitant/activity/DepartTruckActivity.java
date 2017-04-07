package com.example.phobes.witnessassitant.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.TransportData;
import com.example.phobes.witnessassitant.service.CommService;
import com.example.phobes.witnessassitant.util.DateUtil;
import com.example.phobes.witnessassitant.util.Md5Utils;
import com.example.phobes.witnessassitant.util.ParaseData;

import java.util.Date;

/**
 * Created by YLS on 2016/10/9.
 */
public class DepartTruckActivity extends AppCompatActivity {

    public static final String ARG_REQ_CODE = "qrName";
    private int DEPART_TRUCK_REQ_CODE=5;
    private EditText etProjectName;
    private EditText etDepartTime;
    private EditText etDevice;
    private EditText etNoticeId;
    private EditText etPosition;
    private EditText etSlump;
    private EditText etStrength;
    private EditText etTruckId;
    private EditText etPlanVolume;
    private EditText etThisVolume;
    private EditText etAddCar;
    private EditText etAddVolume;
    private EditText etMemo;
    private EditText etDepartPerson;
    private EditText etDriver;
    private Spinner  spConfirmResult;
    private Button btnConfirm;
    private Button btnCancel;
    private String result;
    private Toolbar toolbar;
    public final int FIRST=10;
    public final int SECOND=20;
    private int transportId;
    int hasOpenCameraPermission = 0;
    final private int REQUEST_CODE_ASK_PERMISSIONS=123;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depart_truck);
        Intent intent = new Intent(DepartTruckActivity.this, CaptureActivity.class);
        intent.putExtra("operation", "departTruck");

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
            }else {
                startActivityForResult(intent, DEPART_TRUCK_REQ_CODE);
            }
        }
        startActivityForResult(intent, DEPART_TRUCK_REQ_CODE);
        findView();
        addEvent();
    }

    private void findView(){
        etProjectName= (EditText) findViewById(R.id.projectName);
        etDepartTime= (EditText) findViewById(R.id.departTime);
        etDevice= (EditText) findViewById(R.id.device);
        etNoticeId= (EditText) findViewById(R.id.noticeId);
        etPosition= (EditText) findViewById(R.id.position);
        etSlump= (EditText) findViewById(R.id.slump);
        etStrength= (EditText) findViewById(R.id.strength);
        etTruckId= (EditText) findViewById(R.id.truckId);
        etPlanVolume= (EditText) findViewById(R.id.planVolume);
        etThisVolume= (EditText) findViewById(R.id.thisVolume);
        etAddCar= (EditText) findViewById(R.id.addCar);
        etAddVolume= (EditText) findViewById(R.id.addVolume);
        etMemo= (EditText) findViewById(R.id.memo);
        etDepartPerson= (EditText) findViewById(R.id.departPerson);
        etDriver= (EditText) findViewById(R.id.driver);
        spConfirmResult= (Spinner) findViewById(R.id.confirmResult);
        btnConfirm= (Button) findViewById(R.id.confirm);
        btnCancel= (Button) findViewById(R.id.cancel);
        toolbar=(Toolbar)findViewById(R.id.common_toolbar);
        toolbar.setTitle("现场签收");
    }

    private void addEvent(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

         btnConfirm.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(new CommService(DepartTruckActivity.this).isNetConnected()) {
                     EditText[] editTexts=new EditText[]{etProjectName,etDepartTime,etNoticeId,etTruckId,etDriver};
                    if(isNull(editTexts)){
                        return;
                    }

                     int confirm;
                     if (spConfirmResult.getSelectedItem().toString().equals("签收")) {
                         confirm = 1;
                     } else {
                         confirm = 2;
                     }
                     SiteConfirm siteConfirm = new SiteConfirm(confirm, DateUtil.DateTimeToString(new Date()),transportId);
                     siteConfirm.execute((Void)null);
                 }else{
                     Snackbar.make(spConfirmResult,"网络连接异常！",Snackbar.LENGTH_LONG).show();
                 }
             }
         });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String[] confirmResult={"签收","不签收"};
        ArrayAdapter<String> sp_data=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,confirmResult);
        spConfirmResult.setAdapter(sp_data);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        String result="";
        try{
            result=data.getStringExtra("strResult");
        }catch (Exception e){
            return;
        }

          String[] stringArray=result.split("\\^");
        if(stringArray.length!=4){
            btnConfirm.setClickable(false);
            btnConfirm.setBackgroundColor(getResources().getColor(R.color.white_grap));
            Snackbar.make(spConfirmResult,"扫描条码不正确！",Snackbar.LENGTH_LONG).show();
            return;
        }
        encrypt(result,FIRST,SECOND);
        CommService commService=new CommService(DepartTruckActivity.this);
        if(commService.isNetConnected()) {
            transportId= Integer.parseInt(stringArray[0]);
            TransportInf transportInf = new TransportInf(Integer.parseInt(stringArray[0]), stringArray[1], stringArray[2], stringArray[3]);
            transportInf.execute((Void) null);
        }
        else{
            Snackbar.make(etAddCar,"网络连接异常！",Snackbar.LENGTH_SHORT).show();
        }
    }

    class TransportInf extends AsyncTask<Void,Void,Boolean>{

        private int id;
        private String orgId;
        private String taskId;
        private String noticeId;

        TransportInf(int id, String orgId,String taskId,String noticeId){
            this.id=id;
            this.orgId=orgId;
            this.taskId=taskId;
            this.noticeId=noticeId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                result=CommData.dbWeb.getTransportInf(id,orgId,taskId,noticeId);
                if(result!=null && !result.equals("")){
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
                try {
                    TransportData transportInf = ParaseData.transportInfData(result);
                    etProjectName.setText(transportInf.getProjectName());
                    etDepartTime.setText(transportInf.getDepartTime());
                    etDevice.setText(transportInf.getDevice());
                    etNoticeId.setText(transportInf.getNoticeId());
                    etPosition.setText(transportInf.getPosition());
                    etSlump.setText(transportInf.getSlump());
                    etStrength.setText(transportInf.getStrength());
                    etTruckId.setText(transportInf.getTruckId());
                    etPlanVolume.setText(transportInf.getPlanVolume());
                    etThisVolume.setText(transportInf.getThisVolume());
                    etAddCar.setText(transportInf.getAddCar());
                    etAddVolume.setText(transportInf.getAddVolume());
                    etMemo.setText(transportInf.getMemo());
                    etDepartPerson.setText(transportInf.getDepartPerson());
                    etDriver.setText(transportInf.getDriver());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                btnConfirm.setClickable(false);
                btnConfirm.setBackgroundColor(getResources().getColor(R.color.white_grap));
                Snackbar.make(etDriver,"数据库没有该记录！！！",Snackbar.LENGTH_LONG).show();
            }

        }
    }

    class SiteConfirm extends AsyncTask<Void,Void,Boolean>{

        private int confirmResult;
        private String arriveTime;
        private int transportId;
        SiteConfirm(int confirmResult,String arriveTime,int transportId){
          this.confirmResult=confirmResult;
            this.arriveTime=arriveTime;
            this.transportId=transportId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
          String result= CommData.dbWeb.SiteSign(confirmResult,arriveTime,transportId);
            if(result.toLowerCase().equals("true")){
                return true;
            }else{
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //super.onPostExecute(aBoolean);
           if(success){
               Snackbar.make(etAddCar,"上传成功",Snackbar.LENGTH_SHORT).show();
               btnConfirm.setClickable(false);
               btnConfirm.setBackgroundColor(getResources().getColor(R.color.white_grap));
           } else{
               Snackbar.make(etAddCar,"服务器保存失败！",Snackbar.LENGTH_SHORT).show();
           }

        }
    }

    private String encrypt(String sourceStr,int firstOff,int secondOff){
        String[] stringArray=sourceStr.split("\\^");
        String s="";
        for(int i=0;i<stringArray.length-1;i++){
            s+=stringArray[i];
        }
       s= Md5Utils.md5(s);
        return s.substring(firstOff-1,firstOff)+s.substring(secondOff-1,secondOff);
    }

    private boolean isNull(EditText[] view) {
        for(int i=0;i<view.length;i++) {
            if (view[i].getText().toString().equals("")) {
                view[i].setError(getString(R.string.error_field_required));
                view[i].requestFocus();
                return true;
            }
        }
        return false;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(DepartTruckActivity.this)
                .setMessage(message)
                .setPositiveButton("确定", okListener)
                .setNegativeButton("取消", null)
                .create()
                .show();
    }
}
