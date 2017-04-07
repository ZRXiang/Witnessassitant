package com.example.phobes.witnessassitant.activity;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import com.example.phobes.witnessassitant.model.FileUpload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YLS on 2016/9/16.
 */
public class DeleteFile{


public Boolean deleteFile(String strFileName){
       //删除文件：
        File file = new File(strFileName);
            try {
                //设置属性:
                // 让文件可执行，可读，可写
                file.setExecutable(true,false);
                file.setReadable(true,false);
                file.setWritable(true,false);

                if (file.exists())
                {
                    if(file.delete())
                    {
                        return true;
                    }else
                    {
                        return false;
                    }
                }else
                {
                    return true;
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                return false;
            }

    }


}
