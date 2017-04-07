package com.example.phobes.witnessassitant.util;

import android.content.Context;
import android.graphics.Path;
import android.os.Environment;

import com.example.phobes.witnessassitant.R;

import java.io.File;
import java.io.InputStream;

/**
 * Created by phobes on 2016/6/1.
 */
public class Configuration {
    public static String SERVER_ADDRESS = "http://192.168.0.188:9094";
    public static String SERVICE_NAME = "/DatabaseWebservice.asmx";

    public static final String DB_PATH = "schema";
    public static final String DB_NAME = "witness.db";
    //    public static final int DB_VERSION = 2;
    public static final int DB_VERSION = 3;
    public static int oldVersion = -1;
    private static String test_server = "";
    private static String test_port = "";
    private Context mContext;

    public Configuration(Context context) {
        this.mContext = context;
    }

    public String getTestPort() {
        try {
            InputStream inputStream = mContext.getResources().openRawResource(R.raw.config);
            return SOAPUtils.parseSOAP(inputStream, "port");
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getTestHost() {
        try {
            InputStream inputStream = mContext.getResources().openRawResource(R.raw.config);
            return SOAPUtils.parseSOAP(inputStream, "host");
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getServerAddress() {
        try {
            InputStream inputStream = mContext.getResources().openRawResource(R.raw.config);
            return SOAPUtils.parseSOAP(inputStream, "server_address");
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getVideoIp() {
        try {
            InputStream inputStream = mContext.getResources().openRawResource(R.raw.config);
            return SOAPUtils.parseSOAP(inputStream, "video_ip");
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getWitnessContent() {
        try {
            InputStream inputStream = mContext.getResources().openRawResource(R.raw.config);
            return SOAPUtils.parseSOAP(inputStream, "witness_content");
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getTestContent() {
        try {
            InputStream inputStream = mContext.getResources().openRawResource(R.raw.config);
            return SOAPUtils.parseSOAP(inputStream, "test_content");
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
