package com.example.phobes.witnessassitant.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
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
import java.util.Calendar;
import java.util.List;

/**
 * Created by YLS on 2016/9/17.
 */
public class ZJSiteFileUpload extends Thread {

    private List<FileUpload> fileUploadList;
    Context context;
    private  int packLength=16*1024;
    CommService commService;


    public ZJSiteFileUpload( Context context){
        this.context=context;
         commService=new CommService(context);
    }

    @Override
    public void run() {
        LogWriter.log(CommData.INFO,"--ZJSiteFileUpload开始运行");
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!commService.isNetConnected()){
               continue;
            }
            LogWriter.log(CommData.INFO,"--网络正常,开始获取未上传文件");
           try {
               fileUploadList = CommData.dbSqlite.getSiteTestFile();
               LogWriter.log(CommData.INFO,"--获取未上传文件个数："+fileUploadList.size());
           } catch (ParseException e) {
               LogWriter.log(CommData.ERROR,"获取本地数据失败！"+e.getMessage());
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
            LogWriter.log(CommData.INFO,"--开始读取文件:"+fileName);
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
                        LogWriter.log(CommData.DEBUG,"文件长度不足！");
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
                        LogWriter.log(CommData.ERROR,"文件上传异常："+e.getMessage());
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
                                LogWriter.log(CommData.DEBUG,"文件名"+fileName+"上传成功");
                                CommData.dbSqlite.updateSiteTestDataAndSendState(pointId, index, 2);
                            }else{
                                LogWriter.log(CommData.DEBUG,"文件名"+fileName+"上传失败");
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
                    return;
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
