package com.example.phobes.witnessassitant.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.FileUpload;
import com.example.phobes.witnessassitant.model.SendTestValue;
import com.example.phobes.witnessassitant.util.Configuration;
import com.example.phobes.witnessassitant.util.DatabaseHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YLS on 2016/9/17.
 */
public class ZJSiteFileUpload1 extends Thread {

    private int dataId;
    private int indexId;
    private String orgId;
    private List<FileUpload> fileUploadList;
    Handler outHandler;
    Context context;
    private Boolean sendFile=true;
    private final int packLength=16*1024;
    private final int fileLengthMax=10*1024*1024;
    int uploadCount=0;
    private int fileSize=0;


    public ZJSiteFileUpload1(Context context){
        this.context=context;
    }

    @Override
    public void run() {
        CommService commService=new CommService(context);
        try {
            LogWriter.open(CommData.filePath).print(CommData.DEBUG,"--ZJSiteFileUpload开始运行");
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!commService.isNetConnected()){
               continue;
            }
            try {
                LogWriter.open(CommData.filePath).print(CommData.DEBUG,"--网络正常,开始获取未上传文件");
            } catch (IOException e) {
                e.printStackTrace();
            }
        try {
           // sqliteService.updateTaskStateAndBlock(db, dataId,indexId);
            fileUploadList = CommData.dbSqlite.getSiteTestFile();
        } catch (ParseException e) {
           // isRun=false;
            //sendFile=false;
            //e.printStackTrace();
            try {
                LogWriter.open(CommData.filePath).print(CommData.ERROR,"获取本地数据失败！");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
        for (FileUpload fileUpload : fileUploadList) {
            int nLength = 0;
            RandomAccessFile fis = null;
            FileChannel fcin=null;
            FileLock flin=null;
            try {
                LogWriter.open(CommData.filePath).print(CommData.DEBUG,"--开始读取文件:"+fileUpload.getFileName());
            } catch (IOException e) {
                e.printStackTrace();
            }
            // if (fileUpload.getSendState() <2 ) {
                try {
                    File file=new File(fileUpload.getFileName());
                    //给该文件加锁
                    try {
                        LogWriter.open(CommData.filePath).print(CommData.DEBUG,"--开始读取文件:"+fileUpload.getFileName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                     fis = new RandomAccessFile(file, "r");
                     fcin=fis.getChannel();
                    while(true){
                        try {
                            flin = fcin.tryLock(0,Long.MAX_VALUE,true);
                            if(flin!=null){
                                break;
                            } else{
                                Thread.sleep(1000);
                                LogWriter.log(CommData.DEBUG,"--有其他线程正在操作该文件，当前线程休眠1000毫秒");
                            }
                        } catch (Exception e) {
                            LogWriter.log(CommData.INFO,"--有其他线程正在操作该文件，当前线程休眠1000毫秒; 异常:"+e.getMessage());
                            try {
                                sleep(1000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                    fileSize=(int)fis.length();
                    LogWriter.log(CommData.DEBUG,"当前文件："+fileUpload.getFileName()+";文件当前总长度"+fileSize);
                    nLength = (int)fis.length() - fileUpload.getSendPosition() * packLength;
                    Log.i("","wj1总长度"+nLength+"--------------------------------------------------------@@");
                    LogWriter.log(CommData.DEBUG,"文件当前未上传总长度"+nLength);
                    if(nLength>fileLengthMax){
                        nLength=fileLengthMax;
                    }
                    byte[] tmpBuff=new byte[nLength];
                    fis.seek(fileUpload.getSendPosition() * packLength);
                    fis.readFully(tmpBuff);//,fileUpload.getSendPosition() * packLength,tmpBuff.length
                    String[] paths=  fileUpload.getFileName().split("/");
                    if (null != flin && flin.isValid()) {
                        try {
                            flin.release();
                            flin = null;
                            fcin.close();
                            fcin = null;
                        } catch (IOException e) {
                                LogWriter.log(CommData.ERROR,"释放文件资源失败！异常:"+e.getMessage());
                        }
                    }
                    sendFileData(nLength, tmpBuff, fileUpload.getSendPosition(), paths[paths.length-1],fileUpload.getReceiveState(),fileUpload.getPointId());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }finally {
                    if (null != flin && flin.isValid()) {
                        try {
                            flin.release();
                            flin = null;
                            fcin.close();
                            fcin = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                            LogWriter.log(CommData.ERROR,"释放文件资源失败！异常:"+e.getMessage());
                        }
                    }
                }
        }

    }

    }


    public void sendFileData(int length,byte[] mBuff,int index,String fileName,int recevieState,int pointId) {
        //int last = 0;
        String s = "";
        byte[] buff1=null;
       // byte[] buff = new byte[length];
        int len=length;
        if(len<packLength && recevieState==0){  //当前未上传文件小于一个包且未接收完
            LogWriter.log(CommData.DEBUG,"获取的文件长度不足");
            return;
        }else if(len<packLength && recevieState==1){//当前未上传文件小于一个包且已接收完
             buff1 = new byte[len];
             System.arraycopy(mBuff,0,buff1,0,len);
            WebService webService = new WebService(context);
            try {
                LogWriter.log(CommData.INFO,"文件名"+fileName+";当前分包索引：" + index + " 当前包大小 :" + buff1.length);
                s = webService.siteUploadFile(buff1, fileName, index,packLength);
                Log.i("pack" + index + " size", ":" + buff1.length + "--------------------------------------------------------@@");
            } catch (Exception e) {
                    LogWriter.log(CommData.ERROR,"文件名"+fileName+"当前分包索引：" + index + ";当前包大小 :" + buff1.length+";当前文件上传失败，异常码："+e.getMessage());
            }
            if (s!=null && Integer.parseInt(s) > 0) {
                    LogWriter.log(CommData.DEBUG,"文件名"+fileName+"当前分包索引：" + index + ";当前包大小 :" + buff1.length+";当前包上传成功");
                index++;
                //更新本地数据库

                if(fileSize<=index*packLength) {
                    CommData.dbSqlite.updateSiteTestDataAndSendState1(pointId, index, 2);
                    LogWriter.log(CommData.INFO,"文件名"+fileName+"当前分包索引：" + index + ";当前包大小 :" + buff1.length+";当前文件上传成功");

                    boolean b =sendMeta();//上传文件名
                    if(b){
                            LogWriter.log(CommData.INFO,"文件名"+fileName+"上传成功");
                    }else{
                            LogWriter.log(CommData.ERROR,"文件名"+fileName+"上传失败");
                    }
                }
            } else {
                //文件发送失败
                LogWriter.log(CommData.ERROR,"文件名"+fileName+"当前分包索引：" + index + ";当前包大小 :" + buff1.length+";当前文件上传失败，异常码："+s);
                return;
            }
        }else if(len>=packLength && recevieState==0 ){//当前未上传文件大于于一个包且未接收完
            int count=len/packLength;
            for(int i=0;i<count;i++){
                buff1 = new byte[packLength];
                System.arraycopy(mBuff,i*packLength,buff1,0,packLength);
                WebService webService = new WebService(context);
                try {
                    LogWriter.log(CommData.INFO,"文件名"+fileName+";当前分包索引：" + index + " 当前包大小 :" + buff1.length);
                    s = webService.siteUploadFile(buff1, fileName, index,packLength);
                    Log.i("pack" + index + " size", ":" + buff1.length + "--------------------------------------------------------@@");
                } catch (Exception e) {
                        LogWriter.log(CommData.ERROR,"文件名"+fileName+"当前分包索引：" + index + ";当前包大小 :" + buff1.length+";当前文件上传失败，异常："+e.getMessage());
                }

                if (s!=null && Integer.parseInt(s) > 0) {
                    LogWriter.log(CommData.INFO,"文件名"+fileName+"当前分包索引：" + index + ";当前包大小 :" + buff1.length+";当前包上传成功");
                    index++;
                    //更新本地数据库
                    CommData.dbSqlite.updateSiteTestDataAndSendState1(pointId, index, 1);
                } else {
                    //文件发送失败
                        LogWriter.log(CommData.ERROR,"文件名"+fileName+"当前分包索引：" + index + ";当前包大小 :" + buff1.length+";当前文件上传失败，异常码："+s);
                    return;
                }
            }

        }else{//当前未上传文件大于于一个包且已接收完
            int last=0;
            int count=len/packLength;
            if(len%packLength>0){
                 last=len%packLength;
                count++;
            }
            for(int i=0;i<count;i++) {
                if (last > 0) {
                    if (i < count - 1) {
                        buff1 = new byte[packLength];
                        System.arraycopy(mBuff, i * packLength, buff1, 0, packLength);
                        WebService webService = new WebService(context);
                        try {
                            LogWriter.log(CommData.INFO,"文件名"+fileName+";当前分包索引：" + index + " 当前包大小 :" + buff1.length);
                            s = webService.siteUploadFile(buff1, fileName, index, packLength);
                            Log.i("pack" + index + " size", ":" + buff1.length + "--------------------------------------------------------@@");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (s!=null && Integer.parseInt(s) > 0) {
                            LogWriter.log(CommData.INFO,"文件名"+fileName+"当前分包索引：" + index + ";当前包大小 :" + buff1.length+";当前包上传成功");
                            index++;
                            //更新本地数据库

                            CommData.dbSqlite.updateSiteTestDataAndSendState1( pointId, index, 1);
                        } else {
                            //文件发送失败
                             LogWriter.log(CommData.ERROR,"文件名"+fileName+"当前分包索引：" + index + ";当前包大小 :" + buff1.length+";当前文件上传失败，异常码："+s);
                             return;
                        }
                    } else {
                        buff1 = new byte[last];
                        System.arraycopy(mBuff, i * packLength, buff1, 0, last);
                        WebService webService = new WebService(context);
                        try {
                            LogWriter.log(CommData.INFO,"文件名"+fileName+";当前分包索引：" + index + " 当前包大小 :" + buff1.length);
                            s = webService.siteUploadFile(buff1, fileName, index, packLength);
                            Log.i("pack" + index + " size", ":" + buff1.length + "--------------------------------------------------------@@");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (s!=null && Integer.parseInt(s) > 0) {
                            LogWriter.log(CommData.INFO,"文件名"+fileName+"当前分包索引：" + index + ";当前包大小 :" + buff1.length+";当前包上传成功");
                            index++;
                            //更新本地数据库
                            if(fileSize<=index*packLength) {
                                CommData.dbSqlite.updateSiteTestDataAndSendState1(pointId, index, 2);
                                LogWriter.log(CommData.INFO,"文件名"+fileName+"当前分包索引：" + index + ";当前包大小 :" + buff1.length+";当前文件上传成功");
                                boolean b =sendMeta();//上传文件名
                                if(b){
                                        LogWriter.log(CommData.INFO,"文件名"+fileName+"上传成功");
                                }else{
                                        LogWriter.log(CommData.ERROR,"文件名"+fileName+"上传失败");
                                }
                            }
                        } else {
                            //文件发送失败
                            LogWriter.log(CommData.ERROR,"文件名"+fileName+"当前分包索引：" + index + ";当前包大小 :" + buff1.length+";当前文件上传失败，异常码："+s);
                            return;
                        }
                    }
                }else{
                    buff1 = new byte[packLength];
                    System.arraycopy(mBuff, i * packLength, buff1, 0, packLength);
                    WebService webService = new WebService(context);
                    try {
                        LogWriter.log(CommData.INFO,"文件名"+fileName+";当前分包索引：" + index + " 当前包大小 :" + buff1.length);
                        s = webService.siteUploadFile(buff1, fileName, index, packLength);
                        Log.i("pack" + index + " size", ":" + buff1.length + "--------------------------------------------------------@@");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (s!=null && Integer.parseInt(s) > 0) {
                        LogWriter.log(CommData.INFO,"文件名"+fileName+"当前分包索引：" + index + ";当前包大小 :" + buff1.length+";当前包上传成功");
                        index++;
                        //更新本地数据库
                        if(i<count-1) {
                            CommData.dbSqlite.updateSiteTestDataAndSendState1(pointId, index, 1);
                        }else{
                            if(fileSize<=index*packLength) {
                                CommData.dbSqlite.updateSiteTestDataAndSendState1(pointId, index, 2);
                               // sendMeta();//上传文件名
                                LogWriter.log(CommData.INFO,"文件名"+fileName+"当前分包索引：" + index + ";当前包大小 :" + buff1.length+";当前文件上传成功");
                                boolean b =sendMeta();//上传文件名
                                if(b){
                                    LogWriter.log(CommData.INFO,"文件名"+fileName+"上传成功");
                                }else{
                                        LogWriter.log(CommData.ERROR,"文件名"+fileName+"上传失败");
                                }
                            }
                        }
                    } else {
                        LogWriter.log(CommData.ERROR,"文件名"+fileName+"当前分包索引：" + index + ";当前包大小 :" + buff1.length+";当前文件上传失败，异常码："+s);
                        //文件发送失败
                        return;
                    }
                }
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
                WebService webService = new WebService(context);
                try {
                    if(sendTestValueList.size()>0) {
                        String result = webService.insertTestValue(sendTestValueList);
                       // result = result.toLowerCase();
                        if (result.toLowerCase().equals("true")) {
                            //meta上传成功
                            /*for(SendTestValue sendTestValue:sendTestValueList){
                                if(sendTestValue.getPointId()!=0){
                                    SQLiteDatabase db2 = databaseHelper.getWritableDatabase();
                                    sqliteService.updatePointSendState(db2,sendTestValue.getPointId());
                                   // Boolean b= sqliteService.updateTaskState(db2, dataId,indexId);
                                    db2.close();
                                }
                            }*/
                            return true;
                        } else {
                           /*SQLiteDatabase db3 = databaseHelper.getWritableDatabase();
                            sqliteService.updateTaskStateAndBlock(db3, dataId,indexId);*/
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
