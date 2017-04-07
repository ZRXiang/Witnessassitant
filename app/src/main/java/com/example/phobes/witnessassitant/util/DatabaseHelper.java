package com.example.phobes.witnessassitant.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.service.LogWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context mContext;
    public DatabaseHelper(Context context, String databaseName, int version) {
        super(context, databaseName, null, version);
        mContext = context;
    }

    /**
     * 数据库第一次创建时调用
     * */
    @Override
    public void onCreate(SQLiteDatabase db) {
        LogWriter.log(CommData.INFO, "create database" );
        System.out.println("创建表1");
        executeAssetsSQL(db, "schema.sql"); //executeAssetsSQL方法用于向Assets文件夹对应的路径读取SQL语句然后执行创建操作
        System.out.println("创建表2");
    }

    /**
     * 数据库升级时调用
     * */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //数据库不升级
        if (newVersion <= oldVersion) {
            return;
        }
        Configuration.oldVersion = oldVersion;

        int changeCnt = newVersion - oldVersion;
        for (int i = 0; i < changeCnt; i++) {
            // 依次执行updatei_i+1文件      由1更新到2 [1-2]，2更新到3 [2-3]
            String schemaName = "update" + (oldVersion + i) + "_" + (oldVersion + i + 1) + ".sql";
            executeAssetsSQL(db, schemaName);
        }
    }

    /**
     * 读取数据库文件（.sql），并执行sql语句
     * */
    private void executeAssetsSQL(SQLiteDatabase db, String schemaName) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(mContext.getAssets()
                    .open(Configuration.DB_PATH + "/" + schemaName)));

            System.out.println("路径:"+Configuration.DB_PATH + "/" + schemaName);
            String line;
            String buffer = "";
            while ((line = in.readLine()) != null) {
                buffer += line;
                if (line.trim().endsWith(";")) {
                    db.execSQL(buffer.replace(";", ""));
                    buffer = "";
                }
            }
        } catch (IOException e) {
            Log.e("db-error", e.toString());
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                Log.e("db-error", e.toString());
            }
        }
    }
}