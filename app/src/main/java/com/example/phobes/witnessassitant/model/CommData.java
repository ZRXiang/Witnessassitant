package com.example.phobes.witnessassitant.model;

import android.os.Environment;

import com.zbl.server.TCP.ServerThread;
import com.example.phobes.witnessassitant.service.SqliteService;
import com.example.phobes.witnessassitant.service.WebService;

/**
 * Created by phobes on 2016/6/7.
 */
public class CommData {
    public static String serverAddress;
    public static String name;      //登陆名 如:sj1-1
    public static String sUserId;   //用户ID
    public static String sLabId;    //机构ID
    public static String orgType;   //机构类型
    public static String deviceId;
    public static String email;
    public static String orgName;
    public static String username;  //姓名 如:张三
    public static String witnessType;
    public static String duty;
    public static String filePath;  //日志文件路径
    public static String DEBUG="debug";
    public static String ERROR="error";
    public static String INFO="info";
    public static int pointId=0;
    public static String sessionKey;
    public static ServerThread st =null;
    public static int DBType=2;     // oracle 1  sqlserver 2
    public static boolean bEncryptSQL = false;
    public static boolean bDetailLog = false;
    public static SqliteService dbSqlite;
    public static WebService dbWeb;
}
