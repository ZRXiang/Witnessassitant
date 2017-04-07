package com.example.phobes.witnessassitant.util;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.phobes.witnessassitant.model.SendData;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientThread implements Runnable {
    private Socket s;
    // 定义向UI线程发送消息的Handler对象
    Handler handler;
    String host;
    public String result;
    int port;
    // 定义接收UI线程的Handler对象
    public Handler revHandler;
    // 该线程处理Socket所对用的输入输出流
    BufferedReader br = null;
    OutputStream os = null;

    public ClientThread(Handler handler, String host, int port) {
        this.handler = handler;
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try {
//            s = new Socket(host, port);
            s = new Socket("192.168.0.7", 7777);
            if (s != null) {
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                out.writeUTF(result);
                out.flush();
                out.close();
                s.close();
            }
        } catch (SocketTimeoutException e) {
            Message msg = new Message();
            msg.what = 0x123;
            msg.obj = "网络连接超时！";
            Log.i("连接超时", "109");
        } catch (IOException io) {
            io.printStackTrace();
        }

    }

}