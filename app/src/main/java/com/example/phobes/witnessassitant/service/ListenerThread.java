package com.example.phobes.witnessassitant.service;

import com.example.phobes.witnessassitant.model.CommData;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by YLS on 2016-11-02.
 */
public class ListenerThread extends Thread {
    //监听端口号
    private static final int SERVER_PORT = 5005;
    Socket socket=null;
    ServerSocket serverSocket = null;// 声明一个ServerSocket对象
    public Boolean isRun=true;

    public void run() {
        while (isRun) {
            if(CommData.pointId==0){
                continue;
            }
            try {
                // 创建一个ServerSocket对象，并让这个Socket在5005端口监听
                serverSocket = new ServerSocket(SERVER_PORT);
                // 调用ServerSocket的accept()方法，接受客户端所发送的请求，
                // 如果客户端没有发送数据，那么该线程就停滞不继续
                 socket = serverSocket.accept();
                if (socket != null) {
                    SocketThread.setSocket(socket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

  public void close(){
      try {
          if(serverSocket!=null) {
              isRun=false;
              serverSocket.close();
          }
          if(socket!=null){
              socket.close();
          }
      } catch (IOException e) {
          e.printStackTrace();
      }
  }


}
