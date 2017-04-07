package com.example.phobes.witnessassitant.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.WitenessData;
import com.example.phobes.witnessassitant.service.GPSService;
import com.example.phobes.witnessassitant.service.LogWriter;
import com.example.phobes.witnessassitant.util.Configuration;
import com.example.phobes.witnessassitant.util.DatabaseHelper;
import com.example.phobes.witnessassitant.util.DateUtil;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Created by phobes on 2016/6/3.
 */
public class WitenessFragment extends Fragment {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int VIDEO = 3;
    final private int REQUEST_CODE_ASK_PHOTO_PERMISSIONS = 123;
    final private int REQUEST_CODE_ASK_GPS_PERMISSIONS = 321;
    final private int REQUEST_CODE_ASK_READ_EXTERNAL_STORAGE = 124;

    private FloatingActionButton btTakePhoto;
    private FloatingActionButton vSave;
    private FloatingActionButton btLocation;
    private ImageView showImage;

    private Uri imageUri; //图片路径
    private String filename = null; //图片名称
    public static final String ARG_ITEM_ID = "witeness";
    public static final String WITNESS_TYPE = "witness_type";

    private EditText mOrgNameView;
    private TextView vComment;
    private EditText mPositonView;
    private EditText mCommentView;
    private TextView mLableComment;
    private TextView tvObjectName;
    private TextView tvOrgNameView;
    private TextView tvOrgIdView;
    private View rootView;
    String sComment;
    private int witnessId;
    private double latitude;
    private double longitude;
    private String witnessType;
    private WebView webView;
    private int witnessContent;
    private int testContent;
    Spinner spinnerTestItems;
    private WitenessData mWiteness;


    public WitenessFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            witnessId = getArguments().getInt(ARG_ITEM_ID);
            Log.i("witnessId ", witnessId + "");
            Activity activity = this.getActivity();

            mWiteness = CommData.dbSqlite.getWiteness( witnessId);

            witnessType = getArguments().getString(WITNESS_TYPE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.witeness, container, false);
        if (savedInstanceState == null) {

        }
        findViewById();
        initView();
        addEvent();
        gpsLocationWrapper();
        return rootView;
    }

    private void findViewById() {
        btTakePhoto = (FloatingActionButton) getActivity().findViewById(R.id.take_photo);
        btLocation = (FloatingActionButton) getActivity().findViewById(R.id.get_position);
        vSave = (FloatingActionButton) getActivity().findViewById(R.id.save);
        showImage = (ImageView) rootView.findViewById(R.id.witeness_sample_image);
        tvOrgIdView = (TextView) rootView.findViewById(R.id.org_id);
        tvOrgNameView = (TextView) rootView.findViewById(R.id.org_name);
        tvObjectName = (TextView) rootView.findViewById(R.id.object_name);
        mOrgNameView = (EditText) rootView.findViewById(R.id.org_name);
        mCommentView = (EditText) rootView.findViewById(R.id.edit_comment);
        mLableComment = (TextView) rootView.findViewById(R.id.label_comment);
        mPositonView = (EditText) rootView.findViewById(R.id.position);
        webView = (WebView) rootView.findViewById(R.id.witeness_map);
        spinnerTestItems = (Spinner)rootView.findViewById(R.id.test_items);
    }

    private void initView() {
//        tvOrgIdView.setText(CommData.sLabId);
        tvOrgNameView.setText(CommData.orgName);
        tvObjectName.setText(mWiteness.getObject_name());
        if (!CommData.orgType.equals("监理试验室")) {
            mLableComment.setVisibility(View.GONE);
            mCommentView.setVisibility(View.GONE);
        }
        String sTestItems = mWiteness.getTest_items();
        String[] TestItemsList = sTestItems.split(";");
        if (TestItemsList.length ==0)
            TestItemsList = mWiteness.getObject_name().split(";");

        TestItemsList = CommData.dbSqlite.getWitnessedItem(mWiteness.getWitness_id(),TestItemsList);
        ArrayAdapter<String> TestItemAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, TestItemsList);
        spinnerTestItems.setAdapter(TestItemAdapter);

    }

    private void addEvent() {
        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsLocationWrapper();
            }
        });
        vSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSave(v);
            }
        });
        btTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Configuration configuration=new Configuration(getContext());
                //witnessContent  0录像    1拍照    2拍照或录像
                testContent=Integer.parseInt(configuration.getTestContent());
                witnessContent =Integer.parseInt(configuration.getWitnessContent());
                    if(witnessType.equals("sample") && witnessContent==2){
                        showListDialog();
                    }else if(!witnessType.equals("sample") && testContent==2){//是否使用
                        showListDialog();
                    }else{
                        takePhotoWrapper();
                    }
            }
        });
    }

    private void takePhoto() {
        //图片名称 时间命名
        //SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        //Date date = new Date(System.currentTimeMillis());
        if (CommData.orgType.equals("监理试验室")) {
            if(witnessType.equals("sample")){
                if(witnessContent==0){
                    filename = "JLQY-"+ witnessId + ".mp4";
                }else{
                    filename = "JLQY-"+ witnessId+ ".jpg";//
                }
            }else{
                if(testContent==0){
                    filename = "JLSY-"+ witnessId + "-"+ System.currentTimeMillis()+ ".mp4";
                }else{
                    filename = "JLSY-"+ witnessId+ "-" + System.currentTimeMillis()+ ".jpg";//
                }
            }
        }else{
            if(witnessType.equals("sample")) {
                if(witnessContent==0) {
                    filename = "SYYQY-" + witnessId + ".mp4";
                }else{
                    filename = "SYYQY-" + witnessId+ ".jpg";
                }
            }
        }
        //创建File对象用于存储拍照的图片 SD卡根目录
        //File outputImage = new File(Environment.getExternalStorageDirectory(),"test.jpg");
        //存储至DCIM文件夹
        //File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
       // File path =  getActivity().getExternalFilesDir(null);
        File Path = new File("/mnt/sdcard/tmp1/gtwise");

        if (!Path.exists()) {
            //按照指定的路径创建文件夹
            Path.mkdirs();
        }
                File outputImage = new File(Path, filename);
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将File对象转换为Uri并启动照相程序
        imageUri = Uri.fromFile(outputImage);
        if(witnessContent==0){
          // MediaStore.EXTRA_OUTPUT：设置媒体文件的保存路径。
          // MediaStore.EXTRA_VIDEO_QUALITY：设置视频录制的质量，0为低质量，1为高质量。
          // MediaStore.EXTRA_DURATION_LIMIT：设置视频最大允许录制的时长，单位为毫秒。
          // MediaStore.EXTRA_SIZE_LIMIT：指定视频最大允许的尺寸，单位为byte。
            Intent intent = new Intent();
            intent.setAction("android.media.action.VIDEO_CAPTURE");//录像
            intent.addCategory("android.intent.category.DEFAULT");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //设置视频路径
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); //设置视频录制的质量 0低质量  1高质量
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30000); //设置视频录制的时间
            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 1024*512);
            startActivityForResult(intent, VIDEO);
        }else if((witnessContent==1 && witnessType.equals("sample")) || !witnessType.equals("sample")){
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE"); //照相
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //指定图片输出地址
            startActivityForResult(intent, TAKE_PHOTO); //启动照相
        }


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                Intent intent = new Intent("com.android.camera.action.CROP"); //剪裁
                intent.setDataAndType(imageUri, "image/*");
                intent.putExtra("scale", true);
                //设置宽高比例
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                //设置裁剪图片宽高
                intent.putExtra("outputX", 400);
                intent.putExtra("outputY", 400);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                Toast.makeText(getActivity(), "剪裁图片", Toast.LENGTH_SHORT).show();
                //广播刷新相册
                Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intentBc.setData(imageUri);
                getActivity().sendBroadcast(intentBc);
                startActivityForResult(intent, CROP_PHOTO); //设置裁剪参数显示图片至ImageView
                break;
            case CROP_PHOTO:
                try {
                    //图片解析成Bitmap对象
                    requestPermissions(new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    File sd = Environment.getExternalStorageDirectory();
                    boolean can_read = sd.canRead();
                    boolean can_write = sd.canWrite();
                    System.out.println(can_write);
                    File path =  getActivity().getExternalFilesDir(null);
                    File f = new File(path, filename);
                    if (f.exists()) {
//                        Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                        InputStream in = new FileInputStream(f);
                        Bitmap bitmap = BitmapFactory.decodeStream(in);
                        in.close();
                        if (bitmap != null)
                            showImage.setImageBitmap(bitmap); //将剪裁后照片显示出来
//                    Toast.makeText(getActivity(), imageUri.toString(), Toast.LENGTH_SHORT).show();
                    }

                } catch (FileNotFoundException e) {
                    LogWriter.log(CommData.ERROR,"拍照 ，异常："+e.getMessage());
                    e.printStackTrace();
                }
                catch (IOException e){

                    e.printStackTrace();
                }
                break;
            case VIDEO:
                //图片解析成Bitmap对象
                requestPermissions(new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                File sd = Environment.getExternalStorageDirectory();
                boolean can_read = sd.canRead();
                boolean can_write = sd.canWrite();
                System.out.println(can_write);
                File path =  getActivity().getExternalFilesDir(null);
                File f = new File(path, filename);
                if (f.exists()) {
//                        Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                    showImage.setImageBitmap(getVideoThumbnail(f.getPath(), 60, 60,
                            MediaStore.Images.Thumbnails.MICRO_KIND));//显示视频缩略图

//                    Toast.makeText(getActivity(), imageUri.toString(), Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }
                }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * @param videoPath 视频的路径
     * @param width 指定输出视频缩略图的宽度
     * @param height 指定输出视频缩略图的高度度
     * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    private Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                     int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
       // System.out.println("w"+bitmap.getWidth());
        //System.out.println("h"+bitmap.getHeight());
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }


    private void gpsLocationWrapper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showMessageOKCancel("你需要授权打开GPS",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                                REQUEST_CODE_ASK_GPS_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_GPS_PERMISSIONS);
                return;
//TODO
            } else {
                getLocation();
            }
        } else {
            getLocation();
        }
    }

    private void takePhotoWrapper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    showMessageOKCancel("你需要授权打开摄像头",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                    {
                                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PHOTO_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PHOTO_PERMISSIONS);
                return;

            }
            else
            {
                takePhoto();
            }
        } else {
            takePhoto();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PHOTO_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    takePhoto();
                } else {
                    // Permission Denied
                    Snackbar.make(btTakePhoto, "拒绝打开摄像头", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;
            case REQUEST_CODE_ASK_GPS_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    takePhoto();
                } else {
                    // Permission Denied
                    Snackbar.make(btTakePhoto, "拒绝打开GPS", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;
            case REQUEST_CODE_ASK_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Snackbar.make(btTakePhoto, "已经授权", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    // Permission Denied
                    Snackbar.make(btTakePhoto, "拒绝授权", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void sdReadWrapper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ( getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showMessageOKCancel("你需要授权打开SDCard 读权限",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                    {
                                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_READ_EXTERNAL_STORAGE);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_READ_EXTERNAL_STORAGE);
                return;

            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("确定", okListener)
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    private void attemptSave(View v) {
        mPositonView.setError(null);
        boolean cancel = false;
        View focusView = null;
        String sPosition = mPositonView.getText().toString();
        if (CommData.orgType.equals("监理试验室")) {
            sComment = mCommentView.getText().toString();
            String sItem = (String)spinnerTestItems.getSelectedItem();
            mCommentView.setError(null);
            // Check for a valid email address.
            //if (TextUtils.isEmpty(sComment)|| TextUtils.isEmpty(sItem) ) {
            if (TextUtils.isEmpty(sComment)) {
                mCommentView.setError(getString(R.string.error_field_required));
                focusView = mCommentView;
                cancel = true;
            }
        }
        if (TextUtils.isEmpty(sPosition) || sPosition.equals("")) {
            mPositonView.setError(getString(R.string.error_field_required));
            focusView = mPositonView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            if (filename == null) {
                Snackbar.make(v, "请先拍照再保存", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else {
                save(v);
            }
        }
    }

    private void save(View v) {
        boolean bResult=false;
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity(), Configuration.DB_NAME, Configuration.DB_VERSION);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        if (witnessType.equals("sample") && !CommData.orgType.equals("监理试验室")) {
            mWiteness.setSample_time(DateUtil.DateTimeToString(new Date()));
            mWiteness.setSample_latitude(latitude);
            mWiteness.setSample_longitude(longitude);
            mWiteness.setSample_image(filename);
            mWiteness.setWitnessType(1);
            bResult = CommData.dbSqlite.saveWiteness(mWiteness);
        } else if (witnessType.equals("sample") && CommData.orgType.equals("监理试验室")) {
            mWiteness.setWitness_latitude(latitude);
            mWiteness.setWitness_longitude(longitude);
            mWiteness.setWitness_time(DateUtil.DateTimeToString(new Date()));
            mWiteness.setComment(sComment);
            mWiteness.setWitness_image(filename);
            mWiteness.setWitnessType(1);
            bResult = CommData.dbSqlite.saveWiteness(mWiteness);
        } else if (witnessType.equals("test") && CommData.orgType.equals("监理试验室")) {
            mWiteness.setTest_latitude(latitude);
            mWiteness.setTest_longitude(longitude);
            mWiteness.setTest_time(DateUtil.DateTimeToString(new Date()));
            mWiteness.setTest_comment(sComment);
            mWiteness.setTest_image(filename);
            mWiteness.setTest_item((String) spinnerTestItems.getSelectedItem());
            mWiteness.setWitnessType(3);
            bResult = CommData.dbSqlite.SaveTestWitness(mWiteness);
        }
        db.close();
        if (bResult) {
            Snackbar.make(v, "保存成功", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            vSave.hide();
        }else{
            Snackbar.make(v, "保存失败", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }

    public class GetBaiduXYTask extends AsyncTask<Void, Void, Boolean> {
        String netResult = null;
        private double longitude;
        private double latitude;

        GetBaiduXYTask(double longitude, double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String path = "http://api.map.baidu.com/geoconv/v1/?coords=" + longitude + "," + latitude + "&ak=hZMTUyNGwbTjX8N66pCk2O8b&output=json";
                HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
                conn.setConnectTimeout(90000);
                if (conn.getResponseCode() == 200) {
                    netResult = getLine(conn.getInputStream());
                }
                JSONTokener jsonParser = new JSONTokener(netResult);
                JSONObject baiduResult = (JSONObject) jsonParser.nextValue();
                // 接下来的就是JSON对象的操作了
                JSONObject xy = new JSONObject(baiduResult.getJSONArray("result").get(0).toString());
                longitude = (double) xy.get("x");
                latitude = (double) xy.get("y");
                return true;
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                String sURL = "http://api.map.baidu.com/staticimage?width=400&height=300&zoom=16&markers=" + longitude + "," + latitude;
                webView.loadUrl(sURL);
            } else {
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

    private String getLine(InputStream inputStream) throws Exception {
        InputStreamReader isReader = new InputStreamReader(inputStream);
        char[] data = new char[100];
        if (isReader.read(data) == -1) {
            isReader.close();
            return null;
        }
        isReader.close();
        return String.valueOf(data);
    }

    private void getLocation() {
        GPSService mGPSService = new GPSService(getActivity());
        mGPSService.getLocation();

        if (mGPSService.isLocationAvailable == false) {
            Toast.makeText(getActivity(), "定位失败，请重新定位", Toast.LENGTH_SHORT).show();
            latitude = 0.0;
            longitude = 0.0;
            mPositonView.setText("x:" + latitude + ",\ny:" + longitude);
            return;
        } else {
            latitude = mGPSService.getLatitude();
            longitude = mGPSService.getLongitude();
            mPositonView.setText("x:" + latitude + ",\ny:" + longitude);
            GetBaiduXYTask getBaiduXYTask = new GetBaiduXYTask(longitude, latitude);
            getBaiduXYTask.execute((Void) null);
        }
        // make sure you close the gps after using it. Save user's battery power
        mGPSService.closeGPS();
    }

    private void showListDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("请选择见证方式:");
        /**
         * 1、public Builder setItems(int itemsId, final OnClickListener listener)
         * itemsId表示字符串数组的资源ID，该资源指定的数组会显示在列表中。
         * 2、public Builder setItems(CharSequence[] items, final OnClickListener listener)
         * items表示用于显示在列表中的字符串数组
         */
         String[] provinces = new String[] { "视频(在室外使用)", "拍照(在室内使用)"};
        builder.setItems(provinces, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                 switch (which){
                     case 0:
                         testContent=0;
                         witnessContent=0;
                         takePhotoWrapper();
                         break;
                     case 1:
                         testContent=1;
                         witnessContent=1;
                         takePhotoWrapper();
                         break;
                 }
            }
        });
        builder.create().show();
    }

}