package com.example.phobes.witnessassitant.activity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.phobes.witnessassitant.model.CommData;

import org.apache.cordova.DroidGap;

/**
 * Created by YLS on 2016-11-13.
 */
public class TestVideoActivity extends DroidGap {
        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
        String videoIp= settings.getString("video_ip",null);
        String serverAd = settings.getString("server_address",null);
        String videoUser= settings.getString("video_user",null);
        String videoPassword= settings.getString("video_password",null);
            super.loadUrl("file:///android_asset/www/taskList.html?userName=" + CommData.username + "&orgId=" + CommData.sLabId +
                    "&sessionKey=" + CommData.sessionKey + "&userId=" + CommData.sUserId + "&name=" + CommData.name+ "&videoIp=" +videoIp+
                    "&serverAddress=" +serverAd+"&video_user="+videoUser+"&video_password="+videoPassword);

    }



//    private WebView mWebView;
//    private Handler mHandler = new Handler();
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_web);
//        mWebView= (WebView) findViewById(R.id.webLogin);
//
//        WebSettings webSettings = mWebView.getSettings();
//        webSettings.setSavePassword(true);
//        webSettings.setSaveFormData(true);
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setSupportZoom(false);
//
//        mWebView.setWebChromeClient(new MyWebChromeClient());
//
//       // mWebView.addJavascriptInterface(new DemoJavaScriptInterface(), "demo");
//
//        mWebView.loadUrl("http://192.168.0.188:9090/test/login.html");
//    }
//
//    final class DemoJavaScriptInterface {
//
//        DemoJavaScriptInterface() {
//        }
//
//        /**
//         * This is not called on the UI thread. Post a runnable to invoke
//         * loadUrl on the UI thread.
//         */
//      /* public void clickOnAndroid() {
//            mHandler.post(new Runnable() {
//                public void run() {
//                    mWebView.loadUrl("javascript:wave()");
//                }
//            });
//
//        }
//    }*/
//
//    /**
//     * Provides a hook for calling "alert" from javascript. Useful for
//     * debugging your javascript.
//     */
//  /*  final class MyWebChromeClient extends WebChromeClient {
//        @Override
//        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//            result.confirm();
//            return true;
//        }
//    }
//
//    public static void synCookies(Context context, String url) {
//        CookieSyncManager.createInstance(context);
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setAcceptCookie(true);
//        cookieManager.removeSessionCookie();//移除
//        cookieManager.setCookie(url, "{cookie:'123'}");//cookies是在HttpClient中获得的cookie
//        CookieSyncManager.getInstance().sync();
//    }

}
