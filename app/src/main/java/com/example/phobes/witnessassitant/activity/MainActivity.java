package com.example.phobes.witnessassitant.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.SiteTestData;
import com.example.phobes.witnessassitant.model.SiteTestItemData;
import com.example.phobes.witnessassitant.service.CommService;
import com.example.phobes.witnessassitant.service.UploadService;
import com.example.phobes.witnessassitant.service.ZJSiteFileUpload;
import com.example.phobes.witnessassitant.util.ParaseData;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ServiceConnection{
    private final String SUP_PERMISSION = "监理试验室";
    private final String WA_PERMISSION = "工区试验室";
    private final String CENTER_PERMISSION = "中心试验室";
    private final String MIX_PERMISSION = "拌和站";
    private final String THIRD_PERMISSION = "第三方试验室";
    private final String GROUP_PERMISSION  = "试验室组";
    private final int ENTRY_CHECK_TAG = 0;
    private final int SAMPLE_GUILD_TAG = 1;
    private final int SAMPLE_WITNESS_TAG = 2;
    private final int TEST_WITNESS_TAG = 3;
    private final int SAMPLE_PRODUCT_TAG = 4;
    private final int MODLE_SPLIT_TAG = 5;
    private final int IN_ROOM_TAG = 6;
    private final int OUT_ROOM_TAG = 7;
    private final int SITE_TEST_TAG = 8;
    private final int MIX_TASK_TAG=9;
    private final int DEPART_TRUCK=10;
    private GetSiteTestItem getSiteTestItem=null;
    private String siteTestItem;
    private final int REQUEST_CODE_ASK_READ_PERMISSIONS=456;
    List<SiteTestItemData> siteTestItemDatas = new ArrayList<SiteTestItemData>();

    private List<SiteTestData> siteTestDatas = new ArrayList<SiteTestData>();
    private String siteTestResult;
    GetTestTask mGetTestTask;
    GridView gridview;
    Toolbar toolbar;
    List<Map<String, Object>> data_list;
    ZJSiteFileUpload zjSiteFileUpload;
    private Intent serviceIntent;
    private UploadService service=null;

    /*private Integer[] mThumbIds = {
            R.drawable.check, R.drawable.guild,
            R.drawable.sample, R.drawable.test,
            R.drawable.product,R.drawable.split,
            R.drawable.inroom, R.drawable.out,
            R.drawable.sitetest,R.drawable.define_location,
            R.drawable.car
    };*/
//cy
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById();
        addEvent();
        initView();
        deleteOverHistory();
        if (CommData.orgType.equals(THIRD_PERMISSION)) {
            if(new CommService(MainActivity.this).isNetConnected()){
                getTestTask();
                downLoadSiteTestItemData();
            }
            if (Build.VERSION.SDK_INT >= 24) {
                try{
                    ZjSiteFileUpload();  //线程
                    //uploadService();  //服务
                }catch (Exception e) {
                    // WriteLog(CommData.DEBUG,"--上传失败");
                }
            }
            else if (Build.VERSION.SDK_INT >= 23) {
                // WriteLog(CommData.DEBUG,"--Android版本="+android.os.Build.VERSION.SDK_INT);
                if (MainActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
                    ZjSiteFileUpload();  //线程
                    //uploadService();  //服务
                }
            } else {
                ZjSiteFileUpload();  //线程
                //uploadService();  //服务
            }
        }

    }

    private void uploadService(){
        serviceIntent = new Intent(this, UploadService.class);
        startService(serviceIntent);
        bindService(serviceIntent,MainActivity.this, Context.BIND_AUTO_CREATE);
    }

    private  void ZjSiteFileUpload(){

            zjSiteFileUpload = new ZJSiteFileUpload(MainActivity.this);
            zjSiteFileUpload.start();

    }

    private void getTestTask(){
        mGetTestTask = new GetTestTask();
        mGetTestTask.execute((Void) null);
    }

    private void downLoadSiteTestItemData(){
        getSiteTestItem=new GetSiteTestItem();
        getSiteTestItem.execute((Void) null);
    }

    private void deleteOverHistory(){
        CommData.dbSqlite.deleteOverdueHistory(getBaseContext().getExternalFilesDir(null).getPath()+"/");
        CommData.dbSqlite.deleteOverFileAndData(getBaseContext().getFilesDir().getPath()+"/");;
    }

    private void findViewById() {
        gridview = (GridView) findViewById(R.id.main_gridview);
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
    }

    private void addEvent() {
        toolbar.setTitle(getResources().getString(R.string.app_name));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (CommData.orgType.equals(WA_PERMISSION) || CommData.orgType.equals(CENTER_PERMISSION) || CommData.orgType.equals(GROUP_PERMISSION)) {
                    switch (position) {
                        case 0: {
                            Intent intent = new Intent(MainActivity.this, EntryCheckMainActivity.class);
                            startActivity(intent);
                        }
                        break;

                        case 1: {
                            Intent intent = new Intent(MainActivity.this, TestObjectListActivity.class);
                            startActivity(intent);
                        }
                        break;
                        case 2: {
                            Intent intent = new Intent(MainActivity.this, WitnessMainActivity.class);
                            CommData.witnessType = "sampleWitness";
                            intent.putExtra("operation", "sampleWitness");
                            intent.putExtra("type", "sample");
                            startActivity(intent);
                        }
                        break;
                        case 3: {
                                Intent intent = new Intent(MainActivity.this, SampleProductActivity.class);
                                intent.putExtra("operation", "sampleProduct");
                                startActivity(intent);
                        }
                        break;

                        case 4: {
                            if (checkPermission(WA_PERMISSION) || checkPermission(CENTER_PERMISSION) || CommData.orgType.equals(GROUP_PERMISSION)) {
                                Intent intent = new Intent(MainActivity.this, InRoomActivity.class);
                                startActivity(intent);
                            }
                        }
                        break;
                        case 5: {
                            SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
                            String videoIp= settings.getString("video_ip",null);
                            String serverAd = settings.getString("server_address",null);
                            String videoUser= settings.getString("video_user",null);
                            String videoPassword= settings.getString("video_password",null);
                            if(videoIp!=null && serverAd!=null && videoUser!=null && videoPassword!=null) {
                                Intent intent = new Intent(MainActivity.this, TestVideoActivity.class);//TestVideoActivity
                                startActivity(intent);
                            }else{
                                if(videoIp==null) {
                                    Snackbar.make(gridview, "录像机地址不能为空", Snackbar.LENGTH_SHORT).show();
                                    return;
                                }
                                if(videoUser==null) {
                                    Snackbar.make(gridview, "摄像头账号不能为空", Snackbar.LENGTH_SHORT).show();
                                    return;
                                }
                                if(videoPassword==null) {
                                    Snackbar.make(gridview, "摄像头密码不能为空", Snackbar.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }
                        break;
                        case 6: {
                            Intent intent = new Intent(MainActivity.this, MixTaskActivity.class);
                            startActivity(intent);
                        }
                        break;
                    }
                }else if (CommData.orgType.equals(SUP_PERMISSION)) {
                    switch (position) {
                        case 0: {
                            Intent intent = new Intent(MainActivity.this, EntryCheckMainActivity.class);
                            startActivity(intent);
                        }
                        break;
                        case 1: {
                            Intent intent = new Intent(MainActivity.this, WitnessMainActivity.class);
                            CommData.witnessType = "sampleWitness";
                            intent.putExtra("operation", "sampleWitness");
                            intent.putExtra("type", "sample");
                            startActivity(intent);

                        }
                        break;
                        case 2: {
                                Intent intent = new Intent(MainActivity.this, WitnessMainActivity.class);
                                CommData.witnessType = "testWitness";
                                intent.putExtra("operation", "testWitness");
                                intent.putExtra("type", "test");
                                startActivity(intent);
                        }
                        break;
                        case 3: {
                                Intent intent = new Intent(MainActivity.this, SampleModelActivity.class);
                                startActivity(intent);
                        }
                        break;
                        case 4: {
                                Intent intent = new Intent(MainActivity.this, OutRoomActivity.class);
                                startActivity(intent);
                        }
                        break;
                        /*case 5: {
                                Intent intent = new Intent(MainActivity.this, MixTaskActivity.class);
                                startActivity(intent);
                        }
                        break;*/
                        case 5: {
                            SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
                            String videoIp= settings.getString("video_ip",null);
                            String serverAd = settings.getString("server_address",null);
                            String videoUser= settings.getString("video_user",null);
                            String videoPassword= settings.getString("video_password",null);
                            if(videoIp!=null && serverAd!=null && videoUser!=null && videoPassword!=null) {
                                Intent intent = new Intent(MainActivity.this, TestVideoActivity.class);//TestVideoActivity
                                startActivity(intent);
                            }else{
                                if(videoIp==null) {
                                    Snackbar.make(gridview, "录像机地址不能为空", Snackbar.LENGTH_SHORT).show();
                                    return;
                                }
                                if(videoUser==null) {
                                    Snackbar.make(gridview, "摄像头账号不能为空", Snackbar.LENGTH_SHORT).show();
                                    return;
                                }
                                if(videoPassword==null) {
                                    Snackbar.make(gridview, "摄像头密码不能为空", Snackbar.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }
                        break;
                        case 6: {
                            Intent intent = new Intent(MainActivity.this, ProblemBaseActivity.class);
                            startActivity(intent);
                        }
                    }
                }else if (CommData.orgType.equals(THIRD_PERMISSION)) {
                    switch (position) {
                        case 0: {
                                Intent intent = new Intent(MainActivity.this, SiteTestMainActivity.class);
                                startActivity(intent);
                            }
                        break;
                    }
                }else if (CommData.orgType.equals(MIX_PERMISSION)) {
                    switch (position) {
                        case 0: {
                                Intent intent = new Intent(MainActivity.this, MixTaskActivity.class);
                                startActivity(intent);
                        }
                        break;
                        case 1: {
                            Intent intent = new Intent(MainActivity.this, DepartTruckActivity.class);
                            startActivity(intent);
                        }
                        break;
                        case 2: {
                            Intent intent = new Intent(MainActivity.this, ProblemBaseActivity.class);
                            startActivity(intent);
                        }
                        break;
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                Bundle b = data.getExtras(); //data为B中回传的Intent
                String str = b.getString("str1");//str即为回传的值
                break;
            default:
                break;
        }
    }

    protected void initView() {

        data_list = new ArrayList<Map<String, Object>>();

        //获取数据
        getData();
        //新建适配器
        String[] from = {"image", "text"};
        int[] to = {R.id.main_function_image_view, R.id.main_function_text_view};
        SimpleAdapter sim_adapter = new SimpleAdapter(this, data_list, R.layout.baseadapter_provider, from, to);
        //配置适配器
        gridview.setAdapter(sim_adapter);
    }

    private boolean checkPermission(String permission) {
        if (CommData.orgType.equals(permission)) {
            return true;
        } else {
            Snackbar.make(gridview, "你没有权限执行操作", Snackbar.LENGTH_SHORT).show();
            return false;
        }
    }

    public List<Map<String, Object>> getData() {
          if (CommData.orgType.equals(WA_PERMISSION) || CommData.orgType.equals(CENTER_PERMISSION) || CommData.orgType.equals(GROUP_PERMISSION)) {
            String[] mFunctionTexts = {
                    MainActivity.this.getResources().getString(R.string.entry_check_button),
                    MainActivity.this.getResources().getString(R.string.sample_guild),
                    MainActivity.this.getResources().getString(R.string.sample_witness_button),
                    MainActivity.this.getResources().getString(R.string.sample_product_witness_button),
                    MainActivity.this.getResources().getString(R.string.in_room_button),
                   MainActivity.this.getResources().getString(R.string.video)
                   // , MainActivity.this.getResources().getString(R.string.mix_task)
            };
            Integer[] mThumbIds = {
                    R.drawable.check, R.drawable.guild,
                    R.drawable.sample, R.drawable.product,
                    R.drawable.inroom
                    ,R.drawable.video
                   // ,R.drawable.define_location
            };

            //cion和iconName的长度是相同的，这里任选其一都可以
            for (int i = 0; i < mThumbIds.length; i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("image", mThumbIds[i]);
                map.put("text", mFunctionTexts[i]);
                data_list.add(map);
            }
        }else if (CommData.orgType.equals(SUP_PERMISSION)) {
            String[] mFunctionTexts = {
                    MainActivity.this.getResources().getString(R.string.entry_check_button),
                    MainActivity.this.getResources().getString(R.string.sample_witness_button),
                    MainActivity.this.getResources().getString(R.string.test_witness_button),
                    MainActivity.this.getResources().getString(R.string.sample_split_model_button),
                    MainActivity.this.getResources().getString(R.string.out_room_button),
                    MainActivity.this.getResources().getString(R.string.video),
                    MainActivity.this.getResources().getString(R.string.problem_base),
                   // ,MainActivity.this.getResources().getString(R.string.mix_task)
            };
            Integer[] mThumbIds = {
                    R.drawable.check, R.drawable.sample,
                    R.drawable.test, R.drawable.split,
                    R.drawable.out,R.drawable.video,
                    R.mipmap.ic_launcher
                   // , R.drawable.define_location
            };

            //cion和iconName的长度是相同的，这里任选其一都可以
            for (int i = 0; i < mThumbIds.length; i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("image", mThumbIds[i]);
                map.put("text", mFunctionTexts[i]);
                data_list.add(map);
            }
        }else if (CommData.orgType.equals(THIRD_PERMISSION)) {
            String[] mFunctionTexts = {
                    "现场监测采集"
            };
            Integer[] mThumbIds = {
                    R.drawable.sitetest
            };

            //cion和iconName的长度是相同的，这里任选其一都可以
            for (int i = 0; i < mThumbIds.length; i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("image", mThumbIds[i]);
                map.put("text", mFunctionTexts[i]);
                data_list.add(map);
            }
        }
       /* else if (CommData.orgType.equals(MIX_PERMISSION)) {
            String[] mFunctionTexts = {
                    MainActivity.this.getResources().getString(R.string.mix_task),
                    MainActivity.this.getResources().getString(R.string.car),
                    MainActivity.this.getResources().getString(R.string.problem_base)
            };
            Integer[] mThumbIds = {
                    R.drawable.define_location, R.drawable.car,
                    R.drawable.problem_base
            };

            //cion和iconName的长度是相同的，这里任选其一都可以
            for (int i = 0; i < mThumbIds.length; i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("image", mThumbIds[i]);
                map.put("text", mFunctionTexts[i]);
                data_list.add(map);
            }
        }*/

            return data_list;
        }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((UploadService.ServiceBinder)binder).getUploadService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }


    public class  GetTestTask extends AsyncTask<Void,Void,Boolean> {


        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                siteTestResult = CommData.dbWeb.loadSiteTest();
                Log.i("entry check result", siteTestResult);

                return true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success) {
                siteTestDatas = ParaseData.toSiteDataTasks(siteTestResult);
                CommData.dbSqlite.deleteTestTask();
               try {
                   CommData.dbSqlite.saveTestTask(siteTestDatas);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else{
                CommData.dbSqlite.deleteTestTask();
            }
        }

        @Override
        protected void onCancelled() {
            mGetTestTask = null;
        }
    }

    public class GetSiteTestItem extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            siteTestItem=CommData.dbWeb.getSiteTestItemData();
            if(siteTestItem.equals("")){
                return false;
            }else{
                return true;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success){
                siteTestItemDatas= ParaseData.toSiteTestItemData(siteTestItem);
                try {
                    CommData.dbSqlite.saveSiteTestItemData(siteTestItemDatas);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.i("数据保存到本地时异常","siteTestItemDatas");
                }
            }
        }

        @Override
        protected void onCancelled() {
            getSiteTestItem=null;
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("确定", okListener)
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_READ_PERMISSIONS:
                if ((permissions.length>0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission Granted
                    ZjSiteFileUpload();  //线程
                    //uploadService();  //服务
                } else {
                    // Permission Denied
                    Snackbar.make(toolbar, "拒绝打开sd卡", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}  