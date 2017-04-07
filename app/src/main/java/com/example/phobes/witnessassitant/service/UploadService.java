package com.example.phobes.witnessassitant.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.FileUpload;
import com.example.phobes.witnessassitant.model.SendTestValue;
import com.example.phobes.witnessassitant.util.Configuration;
import com.example.phobes.witnessassitant.util.DatabaseHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by YLS on 2017-01-09.
 */
public class UploadService extends Service {

    private Timer timer;
    private TimerTask timerTask;
    private List<FileUpload> fileUploadList;
    private  int packLength=16*1024;
    CommService commService;

    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    @Override
    public void onCreate() {
        statTimer();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        stopTmier();
        super.onDestroy();
    }

    private final ServiceBinder serviceBinder =new ServiceBinder();
    public class ServiceBinder extends Binder{
        public UploadService getUploadService(){
            return UploadService.this;
        }
    }


    public void statTimer(){
        if(timer==null){
            timer=new Timer();
            timerTask=new TimerTask() {
                @Override
                public void run() {
                    LogWriter.log(CommData.DEBUG,"--ZJSiteFileUpload开始运行");
                    while (true) {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(!commService.isNetConnected()){
                            continue;
                        }
                        LogWriter.log(CommData.DEBUG,"--网络正常,开始获取未上传文件");
                        try {
                            fileUploadList = CommData.dbSqlite.getSiteTestFile();
                            LogWriter.log(CommData.DEBUG,"--获取未上传文件个数："+fileUploadList.size());

                        } catch (ParseException e) {
                            LogWriter.log(CommData.ERROR,"获取本地数据失败！");
                             return;
                        }

                        for (FileUpload fileUpload : fileUploadList) {
                            switch (fileUpload.getObjectId()){
                                case 1513:
                                    packLength=16*1024;
                                    break;
                                case 1512:
                                    packLength=128*1024;
                                    break;

                            }
                            sendFileData(fileUpload.getSendPosition(), fileUpload.getFileName(),fileUpload.getReceiveState()>0 ? true : false,fileUpload.getPointId());
                        }
                    }
                }
            };
            timer.schedule(timerTask,1000,10000);
        }
    }

    public void stopTmier(){
        if(timer!=null){
            timerTask.cancel();
            timer.cancel();
            timerTask=null;
            timer=null;
        }
    }

    public void sendFileData(int index,String fileName,Boolean bEnd,int pointId) {
        int nLength = 0;
        int fileLength = 0;
        int nLen = 0;
        RandomAccessFile fis = null;
        FileChannel fcin=null;
        FileLock flin=null;
        String s = "";
        try {
            File file=new File(fileName);
            if (!file.exists()) {
                return;
            }
            //给该文件加锁
            fis = new RandomAccessFile(file, "r");

            fileLength=(int)fis.length();
            LogWriter.log(CommData.INFO,"当前分包索引："+index+";文件当前总长度"+fileLength);
            nLength = (int)fis.length() - index * packLength;
            Log.i("","wj1总长度"+nLength+"--------------------------------------------------------@@");
            LogWriter.log(CommData.INFO,"文件当前未上传总长度"+nLength);

            while (nLength > 0) {
                if (nLength >= packLength) {
                    nLen = packLength;
                }else {
                    if (bEnd) {
                        nLen = nLength;
                    }else {
                        return;
                    }
                }
                byte[] buff = new byte[nLen];
                fis.seek(index * packLength);
                fis.read(buff, 0, nLen);
                try {
                    LogWriter.log(CommData.INFO,"文件名"+fileName+"当前分包索引：" + index + ";当前包大小 :" + buff.length);
                    String[] paths=  fileName.split("/");
                    s = CommData.dbWeb.siteUploadFile(buff, paths[paths.length-1], index, packLength);
                    Log.i("pack" + index + " size", ":" + buff.length + "------------------------@@");
                } catch (Exception e) {
                    LogWriter.log(CommData.ERROR,"文件名"+fileName+"当前分包索引：" + index + ";当前包大小 :" + buff.length);
                    e.printStackTrace();
                    return;
                }
                if (Integer.parseInt(s) > 0) {
                    LogWriter.log(CommData.INFO,"文件名"+fileName+"当前分包索引：" + index + ";当前包大小 :" + buff.length+";当前分包上传成功");
                    index++;
                    nLength-=nLen;
                    //更新本地数据库

                    if(bEnd && nLength<=0){
                        if(sendMeta()) {
                            LogWriter.log(CommData.INFO,"文件名"+fileName+"上传成功");
                            CommData.dbSqlite.updateSiteTestDataAndSendState( pointId, index, 2);
                        }else{
                            LogWriter.log(CommData.ERROR,"文件名"+fileName+"上传失败");
                        }
                    }else{
                        CommData.dbSqlite.updateSiteTestDataAndSendState(pointId, index, 1);
                    }
                } else {
                    LogWriter.log(CommData.ERROR,"文件名"+fileName+"当前分包索引：" + index + ";当前包大小 :" + buff.length+";当前文件上传失败，异常码："+s);
                    return;
                }
            }
        }catch (Exception e){
            LogWriter.log(CommData.ERROR,"文件名"+fileName+"当前分包索引：" + index + ";当前文件上传失败，异常码："+e.getMessage());
            return;
        }finally {
            try {
                fis.close();
             } catch (IOException e) {
                e.printStackTrace();
                LogWriter.log(CommData.ERROR,"释放文件资源失败！异常:"+e.getMessage());
            }
        }
    }


    private  Boolean sendMeta(){
        //发送测点属性
        List<SendTestValue> sendTestValueList = new ArrayList<SendTestValue>();
        try {
            sendTestValueList = CommData.dbSqlite.getTestValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            if(sendTestValueList.size()>0) {
                String result = CommData.dbWeb.insertTestValue(sendTestValueList);
                LogWriter.log(CommData.INFO,"上传文件名的返回值:"+result);
                if (result.toLowerCase().equals("true")) {
                    return true;
                } else {
                    return false;
                }
            }else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
