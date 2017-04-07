package com.example.phobes.witnessassitant.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.SiteTestItemData;
import com.example.phobes.witnessassitant.model.UserInfo;
import com.example.phobes.witnessassitant.service.CommService;
import com.example.phobes.witnessassitant.service.LogWriter;
import com.example.phobes.witnessassitant.service.SqliteService;
import com.example.phobes.witnessassitant.service.WebService;
import com.example.phobes.witnessassitant.util.Configuration;
import com.example.phobes.witnessassitant.util.DatabaseHelper;
import com.example.phobes.witnessassitant.util.DateUtil;
import com.example.phobes.witnessassitant.util.Md5Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by phobes on 2016/5/29.
 */
public class LoginActivity extends BaseActivity {
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private final int REQUEST_CODE_ASK_READ_PERMISSIONS = 456;
    private UserLoginTask mAuthTask = null;
    private GetUsername mGetUsername = null;
    private EditText mUseridView;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private Button mRedirectRegisterButton;
    private String userResult;
    private Button mSignInButton;
    private Button mQrButton;
    private String siteTestItem;
    //private GetSiteTestItem getSiteTestItem=null;
    private final String USER_FILE="userFile";
    private final String USER_ID="userId";
    private final String PASSWORD="password";
    private String mUserId;
    private String mPassword;

    private String userId;
    List<SiteTestItemData> siteTestItemDatas = new ArrayList<SiteTestItemData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        setContentView(R.layout.activity_login);
        findViewById();
        addEvent();
        //getPermission();
        if (Build.VERSION.SDK_INT >= 24) {
            try{
                getPermission();
            }catch (Exception e) {
                // WriteLog(CommData.DEBUG,"--上传失败");
            }
        }
        else if (Build.VERSION.SDK_INT >= 23) {
            // WriteLog(CommData.DEBUG,"--Android版本="+android.os.Build.VERSION.SDK_INT);
            if (LoginActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
                getPermission();
            }
        } else {
            getPermission();
        }

        LogWriter.log(CommData.INFO,"--欢迎再次登陆见证助手");
    }

    public void getPermission(){
        try {
            File tPathSon,file;
            Log.v("EagleTag", Environment.getExternalStorageState());
            File tPath = new File("/mnt/sdcard/tmp1");
            if (tPath.canRead())
                Log.v("EagleTag", "very bad");
            if (tPath.canWrite())
                Log.v("EagleTag", "very good");
            tPathSon = new File("/mnt/sdcard/tmp1/gtwise");
            //FileProvider.getUriForFile(LoginActivity.this, "com.example.phobes.witnessassitant", tPathSon);
            if (!tPathSon.exists()) {
                //按照指定的路径创建文件夹
                tPathSon.mkdirs();
            }
             file = new File(tPathSon+"/"+ DateUtil.DateToString(new Date())+".log");
           // FileProvider.getUriForFile(LoginActivity.this, "com.example.phobes.witnessassitant", file);
            if (!file.exists()) {
                file.createNewFile();
            }
            File delFile = new File(tPathSon+"/"+ DateUtil.DateToString(new Date(new Date().getTime()-3 * 24 * 60 * 60 * 1000))+".log");
            if(delFile.exists()){
                delFile.delete();
            }

            CommData.filePath=file.getPath();
        } catch(Exception e)
        {
            Log.v("EagleTag","file　create　error");
            String s=e.getMessage();
        }

    }


    private void initData() {
        Configuration configuration = new Configuration(this);
        SharedPreferences settings = getSharedPreferences(SettingActivity.PREFS_NAME, 0);
        String serverAd = settings.getString(SettingActivity.SERVER_ADDRESS, null);
        boolean bTranSafe = settings.getBoolean(SettingActivity.TRAN_SAFE,false);
        boolean bDetailLog = settings.getBoolean(SettingActivity.DETAIL_LOG,false);
        boolean bSQLServer = settings.getBoolean(SettingActivity.SQL_SERVER,false);
        if (serverAd == null) {
            serverAd = configuration.getServerAddress();
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(SettingActivity.SERVER_ADDRESS, serverAd);
            editor.commit();
        }
        CommData.serverAddress = serverAd;
        CommData.bEncryptSQL = bTranSafe;
        CommData.bDetailLog = bDetailLog;
        CommData.dbSqlite = new SqliteService(this);
        CommData.dbWeb = new WebService(this);

        if (bSQLServer)
            CommData.DBType = 2;
        else
            CommData.DBType = 1;
    }

    private void findViewById() {
        mUseridView = (EditText) findViewById(R.id.edit_user_id);
        mUsernameView = (EditText) findViewById(R.id.edit_user_name);
        mPasswordView = (EditText) findViewById(R.id.edit_password);
        mRedirectRegisterButton = (Button) findViewById(R.id.go_register_button);
        mProgressView = findViewById(R.id.login_progress);
        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mQrButton = (Button) findViewById(R.id.qr_capture);

        SharedPreferences settings = getSharedPreferences(USER_FILE, 0);
        mUserId = settings.getString(USER_ID,null);
        mPassword = settings.getString(PASSWORD,null);
        if(mUserId!=null && mPassword!=null){

            mUseridView.setText(mUserId);
            mPasswordView.setText(mPassword);
        }
    }

    private void addEvent() {
        mSignInButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            attemptLogin();
                        }
        });
        mUseridView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            userId = mUseridView.getText().toString();
                            if (TextUtils.isEmpty(userId)) {
                                return;
                            } else {
                                CommService commService = new CommService(getApplicationContext());
                                if (commService.isNetConnected()) {
                        mGetUsername = new GetUsername(userId);
                        mGetUsername.execute((Void) null);
                    }else {
                        localGetName(userId);
                    }
                }
            }
        });
        mQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectCaptureWrapper();
            }
        });
    }

    /**
     * 进行登录
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mUseridView.setError(null);
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String userid = mUseridView.getText().toString();
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(userid)) {
            mUseridView.setError(getString(R.string.error_field_required));
            focusView = mUseridView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            CommService commService = new CommService(this);
            if (commService.isNetConnected()) {
                showProgress(true);
                mAuthTask = new UserLoginTask(userid, password);
                mAuthTask.execute((Void) null);
               // downLoadSiteTestItemData();//下载site_test_item表的数据
            } else {
                localLogin(userid, password);
            }
        }
    }

    private void localLogin(String userId, String password) {
        if (CommData.dbSqlite.checkUser(userId, Md5Utils.md5(userId + password))) {
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);

            startActivity(intent);
        } else {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            mPasswordView.requestFocus();
            showProgress(false);
        }
    }
    private void localGetName(String userId){
        String username=CommData.dbSqlite.getUserName(userId);
        if(username.equals(CommData.dbSqlite.NOT_EXIT_USER_ID)){
            mUseridView.setError("用户编号不存在");
        }else {
            mUsernameView.setText(username);
        }
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public class GetUsername extends AsyncTask<Void, Void, Boolean> {

        private final String mUserId;
        private String username;

        GetUsername(String userId) {
            mUserId = userId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                username = CommData.dbWeb.getUsernameById(mUserId);
                return true;
            } catch (InterruptedException e) {

                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mGetUsername = null;
            if (success) {
                mUsernameView.setText(username);
            } else {
                mUseridView.setError("用户编号不存在");
            }
        }

        @Override
        protected void onCancelled() {
            mGetUsername = null;
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                userResult = CommData.dbWeb.login(mUsername, Md5Utils.md5(mUsername + mPassword));
                String duty=CommData.dbWeb.getDuty(mUsername);
                Log.d("结果", userResult);
                String[] userarray = new String[10];
                userarray = userResult.split(";");
                if (userarray[0].equals("")) {
                    return false;
                } else {
                    UserInfo user = new UserInfo();
                    user.setOrgId(Integer.parseInt(userarray[1]));
                    user.setName(userarray[0]);
                    user.setOrgName(userarray[2]);
                    user.setOrgType(userarray[6]);
                    user.setPassword(Md5Utils.md5(mUsername + mPassword));
                    user.setPersonId(Integer.parseInt(userarray[4]));
                    user.setUsername(userarray[5]);
                    user.setDuty(duty);
                    CommData.sUserId = String.valueOf(user.getPersonId());
                    CommData.sLabId = String.valueOf(user.getOrgId());
                    CommData.orgName=user.getOrgName();
                    CommData.orgType = user.getOrgType();
                    CommData.name = user.getName();
                    CommData.username = user.getUsername();
                    CommData.duty=user.getDuty();
                    CommData.sessionKey=userarray[3];
                    DatabaseHelper databaseHelper = new DatabaseHelper(LoginActivity.this, Configuration.DB_NAME, Configuration.DB_VERSION);
                    SQLiteDatabase db = databaseHelper.getReadableDatabase();
                    if (!CommData.dbSqlite.isExitUser(user)) {
                        CommData.dbSqlite.saveUser(user);
                    }
                    db.close();
                }
                return true;
            } catch (InterruptedException e) {
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (success) {
                SharedPreferences settings = getSharedPreferences(USER_FILE, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(USER_ID, mUsername);
                editor.putString(PASSWORD, mPassword);
                editor.commit();
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                showProgress(false);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
                showProgress(false);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    public void redirectSetting(View view) {
        //do something...
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    public void redirectCapture() {

        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, CaptureActivity.class);
        intent.putExtra("operation", "orderTicket");
        startActivity(intent);
        LoginActivity.this.finish();
    }
    private void redirectCaptureWrapper() {
        int hasOpenCameraPermission = 0;
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
//TODO
            }else {
                redirectCapture();
            }
        }else {
            redirectCapture();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    redirectCapture();
                } else {
                    // Permission Denied
                    Snackbar.make(mPasswordView, "拒绝打开摄像头", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;
            case REQUEST_CODE_ASK_READ_PERMISSIONS:
                if ((permissions.length>0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission Granted
                    getPermission();
                } else {
                    // Permission Denied
                    Snackbar.make(mPasswordView, "拒绝打开sd卡", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage(message)
                .setPositiveButton("确定", okListener)
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    @Override
    protected void onShowKeyboard(int keyboardHeight) {
        // do things when keyboard is shown
        mRedirectRegisterButton.setVisibility(View.GONE);
    }

    @Override
    protected void onHideKeyboard() {
        // do things when keyboard is hidden
        mRedirectRegisterButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed(); 	不要调用父类的方法
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    /*public class GetSiteTestItem extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            webService=new WebService(LoginActivity.this);
            siteTestItem=webService.getSiteTestItemData();
            if(siteTestItem.equals("")){
                siteTestItem=webService.getSiteTestItemData();
            }
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
                DatabaseHelper dbHelper = new DatabaseHelper(LoginActivity.this, Configuration.DB_NAME,Configuration.DB_VERSION);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                SqliteService sqliteService = new SqliteService();
                try {
                    sqliteService.saveSiteTestItemData(db,siteTestItemDatas);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.i("数据保存到本地时异常","siteTestItemDatas");
                }
                db.close();
            }
        }

        @Override
        protected void onCancelled() {
            getSiteTestItem=null;
        }
    }

    private void downLoadSiteTestItemData(){
        DatabaseHelper dbHelper = new DatabaseHelper(LoginActivity.this, Configuration.DB_NAME,Configuration.DB_VERSION);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SqliteService sqliteService = new SqliteService();
        //if(sqliteService.getSiteTestItem(db)<1) {
        getSiteTestItem=new GetSiteTestItem();
        getSiteTestItem.execute((Void) null);
       // }
        db.close();
    }
     */
}
