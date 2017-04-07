package com.example.phobes.witnessassitant.service;

/**
 * Created by YLS on 2016-10-21.
 */
import android.Manifest;
import android.content.pm.PackageManager;

import com.example.phobes.witnessassitant.model.CommData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogWriter {

    private static LogWriter mLogWriter;

    private static String mPath;

    private static Writer mWriter;

    private static SimpleDateFormat df;

    public LogWriter(){

    }
    private LogWriter(String file_path) {
        this.mPath = file_path;
        this.mWriter = null;
    }

   public static LogWriter open(String file_path) throws IOException {
        File file = new File(file_path);
        String sDir = file.getAbsolutePath();

        file = new File(sDir);
        if (!file.exists()) {
            try {
                //按照指定的路径创建文件夹
                file.mkdirs();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
       if (mLogWriter == null) {
            mLogWriter = new LogWriter(file_path);
        }

        mWriter = new BufferedWriter(new FileWriter(mPath,true), 2048);
        df = new SimpleDateFormat("[yy-MM-dd hh:mm:ss]: ");
         return mLogWriter;
       // return new LogWriter();
    }

     /*public static LogWriter open(String file_path) throws IOException {
         return new LogWriter();
     }*/

    public void close() throws IOException {
        mWriter.close();
    }

    public void print(String inf,String log) throws IOException {
        mWriter.write(df.format(new Date()));
        mWriter.write(inf+" : ");
        mWriter.write(log);
        mWriter.write("\n");
        mWriter.flush();
    }
    public static void log(String sInf,String sLog) {
        try {
            if (!CommData.bDetailLog) {
                if (sInf != "error")
                    return;
            }
           open(CommData.filePath).print(sInf, sLog);
        }catch (IOException e)
        {

        }
    }
}
