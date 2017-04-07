package com.example.phobes.witnessassitant.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by phobes on 2016/6/7.
 */
public class CommService extends Service {
    /**
     * 检测网络是否连接
     *
     * @return
     */
    private final Context mContext;
    public CommService(Context context) {
        this.mContext = context;
    }
    public boolean isNetConnected() {

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni !=null){
            if(ni.isAvailable())
                if (ni.isConnected())
                   return true;
        }
/*        if (cm != null) {
            NetworkInfo[] infos = cm.getAllNetworkInfo();
            if (infos != null) {
                for (NetworkInfo ni : infos) {
                    if (ni.isConnected()) {
                        return true;
                    }
                }
            }
        }*/
        return false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
