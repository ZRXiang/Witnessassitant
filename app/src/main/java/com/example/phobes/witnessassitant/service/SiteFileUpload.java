package com.example.phobes.witnessassitant.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.FileUpload;
import com.example.phobes.witnessassitant.model.PointMeta;
import com.example.phobes.witnessassitant.model.SendTestValue;
import com.example.phobes.witnessassitant.struct.TFState;
import com.example.phobes.witnessassitant.util.Configuration;
import com.example.phobes.witnessassitant.util.DatabaseHelper;
import com.example.phobes.witnessassitant.util.ParaseData;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YLS on 2016/9/17.
 */
public class SiteFileUpload extends Thread {

    private int dataId;
    private int indexId;
    private String orgId;
    private List<FileUpload> fileUploadList;
    Handler outHandler;
    Context context;
    private Boolean sendFile=true;
    private Boolean isRun=true;
    private final int packLength=100000;
    int uploadCount=0;

    public SiteFileUpload(Handler handlerout, Context context, int dataId,int indexId,String orgId){
        this.outHandler=handlerout;
        this.context=context;
        this.dataId=dataId;
        this.indexId=indexId;
        this.orgId=orgId;
    }

    public void SendMessage(String sMsg){
        Message msg = outHandler.obtainMessage();
        msg.obj = sMsg;
        msg.what = 1;
        outHandler.sendMessage(msg);// 结果返回给UI处理
    }

    @Override
    public void run() {
        while (isRun) {
        try {
            CommData.dbSqlite.updateTaskStateAndBlock(dataId,indexId);
            fileUploadList = CommData.dbSqlite.getSiteTestFile();//, dataId,indexId
        } catch (ParseException e) {
            isRun=false;
            sendFile=false;
            e.printStackTrace();
            return;
        }

        for (FileUpload fileUpload : fileUploadList) {
            InputStream in = null;
            int nLength = 0;
            if (fileUpload.getSendState() == 0) {
                try {
                    in = new FileInputStream(context.getFilesDir().getPath() + "/" + fileUpload.getFileName());
                   // in = new FileInputStream(context.getExternalFilesDir(null).getPath() + "/" + fileUpload.getFileName());
                    nLength = in.available();
                    Log.i("inf","wj总长度"+nLength+"--------------------------------------------------------@@");
                    sendFileData(nLength, in, fileUpload.getSendPosition(), fileUpload.getFileName());
                } catch (FileNotFoundException e) {
                    isRun=false;
                    sendFile=false;
                    e.printStackTrace();
                    return;
                } catch (IOException e) {
                    isRun=false;
                    sendFile=false;
                    e.printStackTrace();
                    return;
                }
            } else if (fileUpload.getSendState() == 1) {
                try {
                    in = new FileInputStream(context.getFilesDir().getPath() + "/" + fileUpload.getFileName());
                    nLength = in.available() - fileUpload.getSendPosition() * packLength ;
                    Log.i("","wj1总长度"+nLength+"--------------------------------------------------------@@");
                    byte[] b = new byte[packLength];
                    for(int i=0;i<fileUpload.getSendPosition();i++){
                        in.read(b,0 , packLength);
                    }
                    sendFileData(nLength, in, fileUpload.getSendPosition(), fileUpload.getFileName());
                } catch (FileNotFoundException e) {
                    isRun=false;
                    sendFile=false;
                    e.printStackTrace();
                    return;
                } catch (IOException e) {
                    isRun=false;
                    sendFile=false;
                    e.printStackTrace();
                    return;
                }
            }
        }
        //发送测点属性
        List<SendTestValue> sendTestValueList = new ArrayList<SendTestValue>();

        if (sendFile) {
            try {
                if(sendTestValueList.size()>0) {
                    String result = CommData.dbWeb.insertTestValue(sendTestValueList);
                    result = result.toLowerCase();
                    if (result.toLowerCase().equals("true")) {
                        //meta上传成功
                        for(SendTestValue sendTestValue:sendTestValueList){
                            if(sendTestValue.getPointId()!=0){
                                CommData.dbSqlite.updatePointSendState(sendTestValue.getPointId());
                                    Boolean b= CommData.dbSqlite.updateTaskState(dataId,indexId);
                            }
                        }
                        SendMessage("success");
                        isRun = false;
                        sendFile=false;
                        return;
                    } else {

                              CommData.dbSqlite.updateTaskStateAndBlock(dataId,indexId);
                               SendMessage("fail");
                           isRun=false;
                           sendFile=false;
                           return;
                    }
                }else{
                    isRun=false;
                    sendFile=false;
                    return;
                }
            } catch (Exception e) {
                isRun=false;
                sendFile=false;
                e.printStackTrace();
                return;
            }
        }
            isRun=false;
            sendFile=false;
            break;
    }

    }


    public void sendFileData(int length,InputStream in,int index,String fileName){
        int last=0;
        byte[] buff=new byte[packLength];

        int packCount=length/packLength;
        if(length%packLength>0){
            packCount++;
            last=length%packLength;
        }
        for(int i=0;i<packCount;i++){
            if(last>0){
                if((packCount-i)<=1){
                    try {
                        String s="";
                        byte[] buff1=new byte[last];
                        in.read(buff1,0,last);
                        try {
                             s = CommData.dbWeb.siteUploadFile(buff1,fileName,index,packLength);
                            Log.i("pack"+index+" size",":"+buff1.length+"--------------------------------------------------------@@");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(Integer.parseInt(s)>0){
                            index++;
                            //更新本地数据库
                            CommData.dbSqlite.updateSiteTestDataAndSendState(dataId,index,2);
                        }else{
                            //文件发送失败
                            isRun=false;
                            sendFile=false;
                           // SendMessage(fileName+" 发送失败");
                            return;
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        String s="";
                        in.read(buff,0,packLength);
                        try {
                            s = CommData.dbWeb.siteUploadFile(buff,fileName,index,packLength);
                            Log.i("pack"+index+" size",":"+buff.length+"--------------------------------------------------------@@");
                        } catch (Exception e) {
                            isRun=false;
                            sendFile=false;
                            e.printStackTrace();
                            return;
                        }
                        if(Integer.parseInt(s)>0){
                            index++;
                            //更新本地数据库
                            CommData.dbSqlite.updateSiteTestDataAndSendState(dataId,index,1);
                        }else{
                            //文件发送失败
                            isRun=false;
                            sendFile=false;
                          //  SendMessage(fileName+" 发送失败");
                           return;
                        }
                    } catch (IOException e) {
                        isRun=false;
                        sendFile=false;
                        e.printStackTrace();
                    }
                }
            }else{
                try {
                    String s="";
                    in.read(buff,0,packLength);
                    try {
                        s = CommData.dbWeb.siteUploadFile(buff,fileName,index,packLength);
                    } catch (Exception e) {
                        isRun=false;
                        sendFile=false;
                        e.printStackTrace();
                        return;
                    }
                    if(Integer.parseInt(s)>0){
                        index++;
                        //更新本地数据库
                        CommData.dbSqlite.updateSiteTestDataAndSendState(dataId,index,1);
                        Log.i("pack"+index+" size",":"+buff.length+"--------------------------------------------------------@@");
                    }else{
                        //文件发送失败
                        isRun=false;
                        sendFile=false;
                        SendMessage(fileName+" 发送失败");
                        return;
                    }
                } catch (IOException e) {
                    isRun=false;
                    sendFile=false;
                    e.printStackTrace();
                }
            }
        }
        if(index==packCount){
            //文件发送成功
            //更新本地数据库
            CommData.dbSqlite.updateSiteTestDataAndSendState(dataId,index,2);
            SendMessage(fileName+" 发送成功");
        }
    }
}
