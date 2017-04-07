package com.example.phobes.witnessassitant.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.util.CheckRegister;
import com.example.phobes.witnessassitant.util.DateUtil;
import com.example.phobes.witnessassitant.util.SessionKeyUtil;

import java.util.Date;

/**
 * Created by phobes on 2016/6/12.
 */
public class RegisterActivity extends AppCompatActivity {
    private Button regQueryButton;
    private Button registerButton;
    private TextView tvEmail;
    private TextView tvOrgName;
    private TextView tvDeviceId;
    private TextView tvRegisterCode;
    private  TextView tvRegCode;
    private RegQueryTask regQueryTask = null;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViewById();
       initView();
       addEvent();
    }
    private void findViewById(){
        toolbar = (Toolbar)findViewById(R.id.common_toolbar);
        tvEmail = (TextView) findViewById(R.id.apply_email);
        tvOrgName = (TextView) findViewById(R.id.edit_orginization_code);
        tvDeviceId = (TextView) findViewById(R.id.edit_device_id);
        tvRegisterCode =(TextView) findViewById(R.id.register_code);
        regQueryButton = (Button) findViewById(R.id.register_apply);
        registerButton = (Button) findViewById(R.id.action_register);
        tvRegCode = (TextView) findViewById(R.id.register_code);
    }
    private void initView(){
        if(CommData.deviceId!=null){
            tvDeviceId.setText(CommData.deviceId);
        }
        if(CommData.email!=null){
            tvEmail.setText(CommData.email);
        }
        if(CommData.orgName!=null){
            tvOrgName.setText(CommData.orgName);
        }
        if(toolbar!=null){
            toolbar.setTitle(RegisterActivity.this.getResources().getString(R.string.button_register));
        }
    }
    private void addEvent(){
        regQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerSearch();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    public void registerSearch(){
        String email = tvEmail.getText().toString();
        String OrgName = tvOrgName.getText().toString();
        String deviceId = tvDeviceId.getText().toString();
        regQueryTask = new RegQueryTask(email,deviceId,email+";" + OrgName,SessionKeyUtil.getSessionKey(email,OrgName));
        regQueryTask.execute((Void)null);
    }
    public void register(){
        ScrollView rootView = (ScrollView) findViewById(R.id.register_form);
        String message;
        String regCode =  tvRegCode.getText().toString();
        if(new CheckRegister().CheckRegistered(regCode,CommData.deviceId)){
           message = "注册成功";
            String PREFS_NAME = "MyPrefsFile";
            boolean mSilentMode = true;
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("silentMode", mSilentMode);

            // Commit the edits!
            editor.commit();
//            SharedPreferences settings2 = getSharedPreferences(PREFS_NAME, 0);
//            boolean silent = settings2.getBoolean("silentMode", false);

        }
        else {
            message = "注册失败";
        }
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
//        String regCode = tvRegCode.getText().toString();
//        boolean cancel = false;
//        View focusView = null;
//
//        // Check for a valid password, if the user entered one.
//        if (!TextUtils.isEmpty(regCode)) {
//            tvRegCode.setError(getString(R.string.error_field_required));
//            focusView = tvRegCode;
//            cancel = true;
//        }
//        if (cancel) {
//            // There was an error; don't attempt login and focus the first
//            // form field with an error.
//            focusView.requestFocus();
//        } else {
//            // Show a progress spinner, and kick off a background task to
//            // perform the user login attempt.
//        }
    }
    public class RegQueryTask extends AsyncTask<Void, Void, Boolean> {
        private String email;
        private String serialNumber;
        private String authorInfo;
        private String sessionKey;
        String regApplyResult;
        private RelativeLayout layoutRoot;
        public RegQueryTask(String email, String serialNumber, String authorInfo, String sessionKey){
            this.email = email;
            this.serialNumber = serialNumber;
            this.authorInfo = authorInfo;
            this.sessionKey = sessionKey;
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            Integer result = 0;
            try {

                regApplyResult = CommData.dbWeb.registerQuery(email,serialNumber,authorInfo,sessionKey);
                System.out.println("结果： "+ regApplyResult);
                if(regApplyResult.equals(" ")){
                    return  false;
                }
                else {
                    return true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            regQueryTask = null;
            String message = null;
            layoutRoot = (RelativeLayout) findViewById(R.id.register_layout);
           if(result){
               message = "查询成功";
               tvRegCode.setText(regApplyResult);
           }
            else {
               message = "查询失败";
           }
            Snackbar.make(layoutRoot, message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        @Override
        protected void onCancelled() {
            regQueryTask = null;
        }
    }
}
