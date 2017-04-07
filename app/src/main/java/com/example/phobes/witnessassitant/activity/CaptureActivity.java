package com.example.phobes.witnessassitant.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtr.zbar.build.ZBarDecoder;
import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.util.CameraManager;
import com.example.phobes.witnessassitant.util.CameraPreview;

import java.io.IOException;
import java.lang.reflect.Field;

public class CaptureActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private CameraManager mCameraManager;

    private TextView scanResult;
    private FrameLayout scanPreview;
    private Button scanRestart;
    private RelativeLayout scanContainer;
    private RelativeLayout scanCropView;
    private ImageView scanLine;

    private Rect mCropRect = null;
    private boolean barcodeScanned = false;
    private boolean previewing = true;
    private String operation;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scan);
        operation = getIntent().getStringExtra("operation");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        findViewById();
        addEvents();
        initViews();
    }

    private void findViewById() {
        scanPreview = (FrameLayout) findViewById(R.id.capture_preview);
        scanResult = (TextView) findViewById(R.id.capture_scan_result);
        scanRestart = (Button) findViewById(R.id.capture_restart_scan);
        scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
        scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
    }

    private void addEvents() {
        scanRestart.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (barcodeScanned) {
                    barcodeScanned = false;
                    scanResult.setText("扫描...");
                    mCamera.setPreviewCallback(previewCb);
                    mCamera.startPreview();
                    previewing = true;
                    mCamera.autoFocus(autoFocusCB);
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*
        *   yang  160922 注释代码
       Intent intent = new Intent();
        if (operation.equals("sampleProduct")) {
           intent.setClass(CaptureActivity.this,SampleProductActivity.class);
        } else if (operation.equals("sampleModel")) {
            intent.setClass(CaptureActivity.this,SampleModelActivity.class);
        } else if (operation.equals("inRoom")) {
            intent.setClass(CaptureActivity.this,InRoomActivity.class);
        } else if (operation.equals("outRoom")) {
            intent.setClass(CaptureActivity.this,OutRoomActivity.class);
        } else if (operation.equals("orderTicket")) {
            intent.setClass(CaptureActivity.this,OrderTicketActivity.class);
        }
        startActivity(intent);
        finish();*/
    }
    private void initViews() {
        autoFocusHandler = new Handler();
        mCameraManager = new CameraManager(this);
        try {
            mCameraManager.openDriver();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera = mCameraManager.getCamera();
        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        scanPreview.addView(mPreview);

        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.85f);
        animation.setDuration(3000);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.REVERSE);
        scanLine.startAnimation(animation);
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }
    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Size size = camera.getParameters().getPreviewSize();

            // 这里需要将获取的data翻转一下，因为相机默认拿的的横屏的数据
            byte[] rotatedData = new byte[data.length];
            for (int y = 0; y < size.height; y++) {
                for (int x = 0; x < size.width; x++)
                    rotatedData[x * size.height + size.height - y - 1] = data[x + y * size.width];
            }

            // 宽高也要调整
            int tmp = size.width;
            size.width = size.height;
            size.height = tmp;

            initCrop();
            ZBarDecoder zBarDecoder = new ZBarDecoder();
            String result = zBarDecoder.decodeCrop(rotatedData, size.width, size.height, mCropRect.left, mCropRect.top, mCropRect.width(), mCropRect.height());

            if (!TextUtils.isEmpty(result)) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                showResult(result);
                barcodeScanned = true;
            }
        }
    };

    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int cameraWidth = mCameraManager.getCameraResolution().y;
        int cameraHeight = mCameraManager.getCameraResolution().x;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void showResult(String result) {
        Intent intent = new Intent();
        if (operation.equals("sampleProduct")) {
            String[] arrayResult = spitResult(result);
            try {
                if(arrayResult.length < 5) {
                    intent.putExtra(SampleProductActivity.ERROR_SCAN,1);
                    return;
                }
                    String orgId = arrayResult[0];
                    String noticeId = arrayResult[1];
                    String strength = arrayResult[2];
                    String pos = arrayResult[3];
                    String sampleId = arrayResult[4];
                    intent.putExtra(SampleProductActivity.ARG_ORG_ID, orgId);
                    intent.putExtra(SampleProductActivity.ARG_NOTICE_ID, noticeId);
                    intent.putExtra(SampleProductActivity.ARG_SAMPLE_ID, sampleId);
                    intent.putExtra(SampleProductActivity.ARG_STRENGTH, strength);
                    intent.putExtra(SampleProductActivity.ARG_POS, pos);

            } catch (Exception e) {
                Log.e("error",e.getMessage());
                intent.putExtra(SampleProductActivity.ERROR_SCAN,1);
            }
            finally {
                CaptureActivity.this.setResult(RESULT_OK, intent);
                CaptureActivity.this.finish();
            }

        } else if (operation.equals("sampleModel")) {
            try {
                String[] arrayResult = spitResult(result);
                if(arrayResult.length < 6) {
                    intent.putExtra(SampleProductActivity.ERROR_SCAN,1);
                    return;
                }
                String orgId = arrayResult[0];
                String noticeId = arrayResult[1];
                String strength = arrayResult[2];
                String pos = arrayResult[3];
                String sampleId = arrayResult[4];
                intent.putExtra(SampleProductActivity.ARG_ORG_ID, orgId);
                intent.putExtra(SampleProductActivity.ARG_NOTICE_ID, noticeId);
                intent.putExtra(SampleProductActivity.ARG_SAMPLE_ID, sampleId);
                intent.putExtra(SampleProductActivity.ARG_STRENGTH, strength);
                intent.putExtra(SampleProductActivity.ARG_POS, pos);
            } catch (Exception e) {
                Log.e("error",e.getMessage());
                intent.putExtra(SampleProductActivity.ERROR_SCAN,1);
            }
            finally {
                CaptureActivity.this.setResult(RESULT_OK, intent);
                CaptureActivity.this.finish();
            }

        } else if (operation.equals("inRoom")) {
            try {
                String[] arrayResult = spitResult(result);
                if(arrayResult.length < 5) {
                    intent.putExtra(SampleProductActivity.ERROR_SCAN,1);
                    return;
                }
                String orgId = arrayResult[0];
                String noticeId = arrayResult[1];
                String strength = arrayResult[2];
                String pos = arrayResult[3];
                String sampleId = arrayResult[4];
                intent.putExtra(SampleProductActivity.ARG_ORG_ID, orgId);
                intent.putExtra(SampleProductActivity.ARG_NOTICE_ID, noticeId);
                intent.putExtra(SampleProductActivity.ARG_SAMPLE_ID, sampleId);
                intent.putExtra(SampleProductActivity.ARG_STRENGTH, strength);
                intent.putExtra(SampleProductActivity.ARG_POS, pos);
            } catch (Exception e) {
                e.getMessage();
                intent.putExtra(SampleProductActivity.ERROR_SCAN,1);
            }
            finally {
                CaptureActivity.this.setResult(RESULT_OK, intent);
                CaptureActivity.this.finish();
            }

        } else if (operation.equals("outRoom")) {
            try {
                String[] arrayResult = spitResult(result);
                if(arrayResult.length < 5) {
                    intent.putExtra(SampleProductActivity.ERROR_SCAN,1);
                    return;
                }
                String orgId = arrayResult[0];
                String noticeId = arrayResult[1];
                String strength = arrayResult[2];
                String pos = arrayResult[3];
                String sampleId = arrayResult[4];
                intent.putExtra(SampleProductActivity.ARG_ORG_ID, orgId);
                intent.putExtra(SampleProductActivity.ARG_NOTICE_ID, noticeId);
                intent.putExtra(SampleProductActivity.ARG_SAMPLE_ID, sampleId);
                intent.putExtra(SampleProductActivity.ARG_STRENGTH, strength);
                intent.putExtra(SampleProductActivity.ARG_POS, pos);
            } catch (Exception e) {
                intent.putExtra(SampleProductActivity.ERROR_SCAN,1);
            }finally {
                CaptureActivity.this.setResult(RESULT_OK, intent);
                //关闭Activity
                CaptureActivity.this.finish();
            }

        } else if (operation.equals("orderTicket")) {
            intent = new Intent(CaptureActivity.this, OrderTicketActivity.class);
            intent.putExtra(OrderTicketActivity.ARG_QR_CONTENT, result);
            Log.i("qr capture:",result);
            startActivity(intent);
            CaptureActivity.this.finish();
        }else if (operation.equals("departTruck")) {
                intent.putExtra("strResult", result);

                CaptureActivity.this.setResult(RESULT_OK, intent);
                //关闭Activity
                CaptureActivity.this.finish();

        }
    }

    private String[] spitResult(String result) {
        String[] r = result.split("\\^");
        if (r.length < 2)
           r = result.split("~");
        return r;
    }
}
