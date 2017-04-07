package com.example.phobes.witnessassitant.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.fragment.PointItemCellListFragment;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.PointItemData;
import com.example.phobes.witnessassitant.service.BlueToothThread;
import com.example.phobes.witnessassitant.service.SocketThread;
import com.example.phobes.witnessassitant.struct.TFState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YLS on 2016/9/9.
 */
public class PointItemActivity1 extends AppCompatActivity {
    Toolbar toolbar;
    private Button btnFileStart;
    private Button btnPointItemFinish;
    private Button btnMessageSet;
    private TextView tvSpace;
    PointItemCellListFragment pointItemCellListFragment;
    private String pTestName;
    private  int pSN;
    private  int pPointId;
    private  int pDataId;
    private String orderId;
    ProgressDialog mProgressDialog;
    Handler mhandler;
    Handler mhandlerSend;
    private String TAG = "===Client===";
    SocketThread socketThread=null;
    BlueToothThread blueToothThread=null;
    private int progress=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_item);
        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra("pBundle");
        pTestName=bundle.getString("pTestName");
        pSN=bundle.getInt("pSN");
        pPointId=bundle.getInt("pPointId");
        pDataId=bundle.getInt("pDataId");
        orderId=bundle.getString("orderId");
        initView();
        addEvent();

        mhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==1){
                    String fileName=String.valueOf(msg.obj);
                    CommData.dbSqlite.updateSiteTestDataAndFileName(pPointId,fileName);
                }
            }
        };

        mhandlerSend = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Log.i(TAG, "mhandlerSend接收到msg.what=" + msg.what);
                    String MSG =((TFState) msg.obj).sMsg;
                    int nSize=((TFState) msg.obj).nLength;
                   int state=((TFState) msg.obj).nState;
                    int nCountLength=((TFState) msg.obj).nCountLength;
                    if (msg.what == 1) {
                       // progress=nSize;
                        mProgressDialog.setProgress(nSize*100/nCountLength);
                        mProgressDialog.setMessage(MSG);
                        CommData.dbSqlite.updateSiteTestDataAndReceiveState(pPointId,1,nSize);

                        switch(state){
                            case 4:
                                CommData.dbSqlite.updateSiteTestDataAndReceiveState(pPointId,2,progress);
                                mProgressDialog.dismiss();
                                btnFileStart.setClickable(false);
                                btnFileStart.setBackgroundColor(getResources().getColor(R.color.white_grap));
                                break;
                            case 5:
                                mProgressDialog.dismiss();
                                stopBlueTooth();
                                stopSocket();
                                break;
                        }
                    } else {
                        Log.i(TAG, "接收错误");
                   System.out.println("接收错误");
                    }
                } catch (Exception ee) {
                    Log.i(TAG, "加载过程出现异常");
                    ee.printStackTrace();
                }
            }
        };

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "start onStart~~~");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "start onRestart~~~");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "start onResume~~~");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "start onPause~~~");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "start onStop~~~");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "start onDestroy~~~");
        try{
            stopBlueTooth();
            stopSocket();
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    private void addEvent(){

        btnFileStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommData.pointId=pPointId;

                int state= CommData.dbSqlite.querySiteTestDataReceiveState(pPointId);
                if(state==2){
                    Snackbar.make(btnFileStart, "文件已接收!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                SharedPreferences set=getSharedPreferences("SP",0);
                String s= set.getString("device_type","");
                if(s.equals("wifi")){
                    startSocket();
                }else if(s.equals("blueTooth")){
                    startBlueTooth();
                }else{
                    Snackbar.make(btnFileStart, "请设置通讯方式!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                mProgressDialog = new ProgressDialog(PointItemActivity1.this);
                mProgressDialog.setMessage("正在连接设备...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(true);
                mProgressDialog.show();

            }
        });

        btnPointItemFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSpace.requestFocus();

                int state= CommData.dbSqlite.querySiteTestDataReceiveState(pPointId);
                if(state==2){
                    List<PointItemData> pointItemDataList=new ArrayList<PointItemData>();
                    pointItemDataList=CommData.dbSqlite.querySiteTestDetailValue(pPointId);
                    for(PointItemData pointItemData:pointItemDataList){
                        if(pointItemData.getItemValue().equals("") || pointItemData.getItemValue()==null){
                            Snackbar.make(btnFileStart,pointItemData.getMetaName()+"不能为空", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            return;
                        }
                    }
                    CommData.dbSqlite.updateSiteTestDataAndPointState(pPointId,1);
                    finish();
                }else{
                    Snackbar.make(btnFileStart,"文件接受未完成！", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        btnMessageSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PointItemActivity1.this,SetMessageAcitivity.class);
                startActivity(intent);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                try{
                    stopBlueTooth();
                    stopSocket();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            onBackPressed();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        try{
            stopBlueTooth();
            stopSocket();
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onBackPressed();
    }

    public void initView(){
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        btnFileStart=(Button) findViewById(R.id.button_file_start);
        btnPointItemFinish=(Button)findViewById(R.id.button_point_item_finish);
        btnMessageSet=(Button)findViewById(R.id.button_message_set);
        tvSpace= (TextView) findViewById(R.id.tv_space);
        if(toolbar!=null){
            toolbar.setTitle(pTestName+"  测点"+pSN);
        }
        tvSpace.setFocusable(true);
        tvSpace.setFocusableInTouchMode(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        pointItemCellListFragment = new PointItemCellListFragment();
        pointItemCellListFragment.setmPointId(pPointId);
        pointItemCellListFragment.setmDataId(pDataId);
        fragmentTransaction.replace(R.id.measure_point_item,pointItemCellListFragment);
        fragmentTransaction.commit();
    }


    public void startSocket() {
        try{
            socketThread = new SocketThread(mhandler, mhandlerSend, PointItemActivity1.this,orderId);
            socketThread.start();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void stopSocket() {
        try {
            if(socketThread!=null) {
                socketThread.isRun = false;
                socketThread.close();
                socketThread = null;
                Log.i(TAG, "Socket已终止");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startBlueTooth(){
        try{
            BlueToothThread blueToothThread=new BlueToothThread(mhandler, mhandlerSend, PointItemActivity1.this,orderId);
            blueToothThread.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private  void stopBlueTooth(){
        try{
            if(blueToothThread!=null){
            blueToothThread.isRun=false;
            blueToothThread.close();
            blueToothThread=null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
