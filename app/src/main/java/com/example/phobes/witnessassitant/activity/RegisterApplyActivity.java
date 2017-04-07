package com.example.phobes.witnessassitant.activity;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.util.DateUtil;
import com.example.phobes.witnessassitant.util.SessionKeyUtil;

import java.util.Date;

public class RegisterApplyActivity extends AppCompatActivity {
    private Button regApply;
    private RegApplyTask regApplyTask= null;
    private TextView tvUsername;
    private TextView tvOrgId;
    private TextView tvEmail;
    private TextView tvTelephone;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_apply);
        findViewById();
        initView();
        addEvent();
        regApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerApply();
            }
        });
    }
    private void findViewById(){
        toolbar = (Toolbar)findViewById(R.id.common_toolbar);
        regApply = (Button)findViewById(R.id.action_register_apply);
        tvUsername = (TextView)findViewById(R.id.edit_apply_user_name);
        tvOrgId = (TextView)findViewById(R.id.edit_orginization_code);
        tvEmail = (TextView)findViewById(R.id.apply_email);
        tvTelephone =(TextView)findViewById(R.id.edit_telephone);
    }
    private void initView(){
        if (toolbar != null) {
            toolbar.setTitle(RegisterApplyActivity.this.getResources().getString(R.string.action_register_apply));
        }
    }
    private void addEvent(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    public void registerApply(){
        String username = tvUsername.getText().toString();
        String orgId = tvOrgId.getText().toString();
        String email = tvEmail.getText().toString();
        String telephone = tvTelephone.getText().toString();
        regApplyTask = new RegApplyTask(username,orgId,email,telephone);
        regApplyTask.execute((Void)null);
    }
    public class RegApplyTask extends AsyncTask<Void, Void, Integer> {
        private String username;
        private String orgId;
        private String email;
        private String telephoneNumber;
        private RelativeLayout layoutRoot;
        public RegApplyTask(String username, String orgId, String email, String telephoneNumber){
            this.username  = username;
            this.orgId = orgId;
            this.email = email;
            this.telephoneNumber = telephoneNumber;
        }
        @Override
        protected Integer doInBackground(Void... params) {
            Integer result = 0;
            try {
                String autorInfo = email + ";" + orgId;
                String sessionKey = SessionKeyUtil.getSessionKey(email,orgId);
                String regApplyResult = CommData.dbWeb.registerApply(
                        username,orgId,email,telephoneNumber,CommData.deviceId, DateUtil.DateToString(new Date()),"", "3" , autorInfo,sessionKey
                        );
                System.out.println("结果： "+ regApplyResult);
              result = Integer.valueOf(regApplyResult);

            } catch (InterruptedException e) {

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(final Integer result) {
            regApplyTask = null;
            String message = null;
            layoutRoot = (RelativeLayout) findViewById(R.id.register_apply_layout);
            if(result.equals(1)){
                message = "申请成功";
                Log.i("register apply success:", "1");
                CommData.email = email;
                CommData.orgName = orgId;
            }
            else if(result.equals(-10)){
                message = "已经提交过申请，不要重复提交！";
                Log.i("register faild:","-10");
            }
            else if(result.equals(-5)){
                message = "申请失败";
                Log.i("register apply faild:","-5");
            }
            Snackbar.make(layoutRoot, message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        @Override
        protected void onCancelled() {
            regApplyTask = null;
        }
    }

}
