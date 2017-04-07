package com.example.phobes.witnessassitant.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by phobes on 2016/6/1.
 */
public class SessionKeyUtil {
    public static String getSessionKey(String username,String orgId){
        Date date = new Date();
        String sdate = DateUtil.DateToString(date);
        String sessionkey = null;
        sessionkey = Md5Utils.md5(username+orgId+Md5Utils.md5(username+sdate));
        return sessionkey;
    }
    // test
    public static void main(String[] args) {
        System.out.println("Session——key:"+getSessionKey("qq@qq.com","101030101"));
    }
}
