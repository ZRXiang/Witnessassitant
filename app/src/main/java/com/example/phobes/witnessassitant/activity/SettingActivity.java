package com.example.phobes.witnessassitant.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.util.Configuration;
import com.example.phobes.witnessassitant.util.Md5Utils;

/**
 * Created by phobes on 2016/6/12.
 */
public class SettingActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final  String SERVER_ADDRESS = "server_address";
    public static final  String TEST_HOST = "test_host";
    public static final  String TEST_PORT = "test_port";
    public static final  String VIDEO_IP = "video_ip";
    public static final  String VIDEO_USER = "video_user";
    public static final  String VIDEO_PASSWORD = "video_password";
    public static final  String TRAN_SAFE ="tran_safe";
    public static final  String DETAIL_LOG ="detail_log";
    public static final  String SQL_SERVER ="sql_server";

    private TextView tvMachineode;
    private  TextView tvAuthorCode;
    private  TextView tvServerAddress;
    private  TextView tvTestHost;
    private  TextView tvTestPort;
    private  CheckBox cbTranSafe;
    private  CheckBox cbDetailLog;
    private  CheckBox cbSQLServer;
    Toolbar toolbar;
    private  TextView tvVideoIp;
    private  TextView tvVideoUser;
    private  TextView tvVideoPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        findViewById();
        initView();
        addEvent();
    }
    private void findViewById(){
        toolbar = (Toolbar)findViewById(R.id.common_toolbar);
        tvServerAddress =(TextView)findViewById(R.id.server_address);
        tvTestHost =(TextView)findViewById(R.id.test_server);
        tvTestPort =(TextView)findViewById(R.id.test_port);
        tvMachineode = (TextView) findViewById(R.id.machine_code);
        tvAuthorCode = (TextView) findViewById(R.id.author_code);
        tvVideoIp= (TextView) findViewById(R.id.video_ip);
        tvVideoUser= (TextView) findViewById(R.id.video_user);
        tvVideoPassword= (TextView) findViewById(R.id.video_password);
        cbTranSafe = (CheckBox) findViewById(R.id.tran_safe);
        cbDetailLog =  (CheckBox) findViewById(R.id.detail_log);
        cbSQLServer =  (CheckBox) findViewById(R.id.sql_server);
    }
    private void initView(){
        if (toolbar != null) {
            toolbar.setTitle(SettingActivity.this.getResources().getString(R.string.button_setting));
        }
        String port=null;
        String host=null;
        String serverAd=null;
        String videoIp=null;
        String videoUser=null;
        String videoPassword=null;
        boolean bTranSafe,bDetailLog,bSQLServer;
        Configuration configuration = new Configuration(this);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        serverAd = settings.getString(SERVER_ADDRESS,null);
        host = settings.getString(TEST_HOST,null);
        port = settings.getString(TEST_PORT,null);
        videoIp= settings.getString(VIDEO_IP,null);
        videoUser= settings.getString(VIDEO_USER,null);
        videoPassword= settings.getString(VIDEO_PASSWORD,null);
        bTranSafe = settings.getBoolean(TRAN_SAFE,false);
        bDetailLog = settings.getBoolean(DETAIL_LOG,false);
        bSQLServer = settings.getBoolean(SQL_SERVER,false);

        if(serverAd==null){
            serverAd = configuration.getServerAddress();
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(SERVER_ADDRESS, serverAd);
            editor.commit();

        }
        CommData.serverAddress = serverAd;
        if(host==null){
            host = configuration.getTestHost();
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(TEST_HOST, host);
            editor.commit();
        }
        if(port==null){
            port = configuration.getTestPort();
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(TEST_PORT, port);
            editor.commit();
        }

        if(videoIp==null){
            videoIp = configuration.getVideoIp();
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(VIDEO_IP, videoIp);
            editor.commit();
        }

        tvServerAddress.setText(serverAd);
        tvTestHost.setText(host);
        tvTestPort.setText(port);
        tvVideoIp.setText(videoIp);
        tvVideoUser.setText(videoUser);
        tvVideoPassword.setText(videoPassword);
        cbTranSafe.setChecked(bTranSafe);
        cbDetailLog.setChecked(bDetailLog);
        cbSQLServer.setChecked(bSQLServer);

        TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
        String m_szImei = TelephonyMgr.getDeviceId();
        String m_szLongID = m_szImei + m_szWLANMAC;
        CommData.deviceId = Md5Utils.md5(m_szLongID);
//        CommData.deviceId=m_szImei;
        tvMachineode.setText(CommData.deviceId);
    }
    private void addEvent(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    /*
 去注册申请
 */
    public void redirectRegisterApply(View view) {
        //do something...
        Intent intent = new Intent();
        intent.setClass(SettingActivity.this, RegisterApplyActivity.class);
        startActivity(intent);
    }
    /*
去注册
*/
    public void redirectRegister(View view) {
        //do something...
        Intent intent = new Intent();
        intent.setClass(SettingActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
    public void save(View view){
        String testHost = tvTestHost.getText().toString();
        String testPort = tvTestPort.getText().toString();
        String serverAd = tvServerAddress.getText().toString();
        String videoIp = tvVideoIp.getText().toString();
        String videoUser=tvVideoUser.getText().toString();
        String videoPassword=tvVideoPassword.getText().toString();
        boolean bTranSafe = cbTranSafe.isChecked();
        boolean bDetailLog = cbDetailLog.isChecked();
        boolean bSQLServer = cbSQLServer.isChecked();

        Configuration.SERVER_ADDRESS = serverAd;
        CommData.serverAddress = serverAd;
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SERVER_ADDRESS,serverAd);
        editor.putString(TEST_HOST,testHost);
        editor.putString(TEST_PORT, testPort);
        editor.putString(VIDEO_IP,videoIp);
        editor.putString(VIDEO_USER,videoUser);
        editor.putString(VIDEO_PASSWORD,videoPassword);
        editor.putBoolean(TRAN_SAFE,bTranSafe);
        editor.putBoolean(DETAIL_LOG,bDetailLog);
        editor.putBoolean(SQL_SERVER,bSQLServer);

        editor.commit();
        Snackbar.make(tvTestPort, "保存成功", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
