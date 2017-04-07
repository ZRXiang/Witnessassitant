package com.example.phobes.witnessassitant.service;

//http://www.cnblogs.com/shaocm/p/3528346.html
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.struct.*;
import com.example.phobes.witnessassitant.struct.TFHeader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SocketThread extends Thread {
    private String ip = "10.0.0.113";
    private int port = 13000;
    private String TAG = "socket thread";
    private int timeout = 10000;

    public boolean isFinish = false;
    public int G_nPackIndex =99999999;
    public static Socket server = null;
    public String filename;
    public String shortFileName;
    DataOutputStream out;
    DataInputStream in;
    public boolean isRun = true;
    Handler inHandler;
    Handler outHandler;
    Context ctx;
    private String TAG1 = "===Send===";
    SharedPreferences sp;
    private int nCountLength;
    private String mOrderId;
    private final String DEBUG="DEBUG";
    private final String ERROR="ERROR";

    public SocketThread(Handler handlerin, Handler handlerout, Context context,String orderId) {
        inHandler = handlerin;
        outHandler = handlerout;
        ctx = context;
        mOrderId=orderId;
        //filename= context.getExternalFilesDir(null).getPath();//手机sdcard目录
        filename=context.getFilesDir().getPath();  //手机内存目录

        LogWriter.log(DEBUG,"创建线程SocketThread");

    }

    /**
     * 连接socket服务器
     */
   /* public void connect() {

        try {
            initdate();
            //Log.i(TAG, "连接中……");
            LogWriter.open(CommData.filePath).print(DEBUG,"SocketThread开始连接");
            client = new Socket(ip, port);
            try{
                client.setSoTimeout(timeout);// 设置阻塞时间
            }catch (IOException e){
               // SendMessage("连接服务器错误，请重试！", 0, (byte) 5, 100);
                e.printStackTrace();
            }
            //Log.i(TAG, "连接成功");
            LogWriter.open(CommData.filePath).print(DEBUG,"SocketThread连接成功");
            in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
           // Log.i(TAG, "输入输出流获取成功");
            LogWriter.open(CommData.filePath).print(DEBUG,"开始获取输入输出流");
        } catch (UnknownHostException e) {
           // Log.i(TAG, "连接错误UnknownHostException 重新获取");
            try {
                LogWriter.open(CommData.filePath).print(ERROR,"连接错误UnknownHostException 重新获取连接");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            connect();
        } catch (IOException e) {
            Log.i(TAG, "连接服务器io错误"+e);
            try {
                LogWriter.open(CommData.filePath).print(ERROR,"连接服务器io错误"+e.getMessage());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                if(client!=null){
                    client.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } catch (Exception e) {
           // SendMessage("连接服务器超时，请重试！", 0, (byte) 5, 100);
          //  Log.i(TAG, "连接服务器错误Exception" + e.getMessage());
            try {
                LogWriter.open(CommData.filePath).print(ERROR, "连接服务器错误Exception" + e.getMessage());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }*/

      public static void setSocket(Socket sktConn){
          server = sktConn;
      }

        public void setStream(){
        try {
            in = new DataInputStream(new BufferedInputStream(server.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SendMessage(String sMsg,int nLen,byte nState,int nCountLength){
        TFState st = new TFState();
        st.nState = nState;
        st.nLength = nLen;
        st.sMsg = sMsg;
        st.nCountLength=nCountLength;
        Message msg = outHandler.obtainMessage();
        msg.obj = st;
        msg.what = 1;
        outHandler.sendMessage(msg);// 结果返回给UI处理
    }

    public void sendResult(String msg){
        Message msgStr = outHandler.obtainMessage();
        msgStr.obj = msg;
        msgStr.what = 1;
        inHandler.sendMessage(msgStr);
    }


   /* public void initdate() {
        sp = ctx.getSharedPreferences("SP", ctx.MODE_PRIVATE);
        ip = sp.getString("ipstr", ip);
        port = Integer.parseInt(sp.getString("port", String.valueOf(port)));
        Log.i(TAG, "获取到ip端口:" + ip + ";" + port);
    }*/
    private  void isRead(byte[] buff,int offset,int size,DataInputStream is)
    {
        int readCount = 0; // 已经成功读取的字节的个数
        while (readCount < size)
        {
            try
            {
                readCount += is.read(buff, offset+readCount, size - readCount);
              //  System.out.println(readCount+"-------------------------------------is length="+is.available());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    /**
     * 实时接受数据
     */
    @Override
    public void run() {
        byte[] buff = new byte[1024];
        G_nPackIndex =99999999;
        shortFileName = "";
        byte[] confirm;
        FileOutputStream outStream = null;
        LogWriter.log(CommData.INFO,TAG+"开始运行");

        //Log.i(TAG, "线程socket开始运行");
       // close();//首先断开的意义在于 可能上次的连接还在缓存,导致这次连接异常
        //connect();//连接是个阻塞的过程,如果没连接上就阻塞了 (建议增加延时功能,友情提醒用户连接超时);
        //Log.i(TAG, "1.run开始");

        LogWriter.log(CommData.INFO,TAG+"1.run开始");

        TFHeader hd = new TFHeader();
        TFConfirm cf = new TFConfirm();
        TFData data = new TFData();
        isFinish = false;
        while (isRun) {
            try {
                if (server != null) {
                    setStream();//获取输入输出流
                    SendMessage("连接成功", 0, (byte) 0, 0);
                    // Log.i(TAG, "2.接收数据头");
                    LogWriter.log(CommData.INFO,TAG+"2.接收数据头开始");
                    //循环读取头
                    while(true) {
                        in.read(buff, 0, 1);
                        if (buff[0]==71){//G
                            in.read(buff, 1, 1);
                            if (buff[1]==90) {//Z
                                in.read(buff, 2, 1);
                                if (buff[2]==74) {//J
                                    in.read(buff, 3, 1);
                                    if (buff[3]==67) {//C
                                        in.read(buff,4,1);//Tag
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    LogWriter.log(CommData.INFO,TAG+"2.接收数据头成功");
                    //判断tag 1 参数包 2 数据包 4 结束报 5 确认包
                    if(buff[4]==1)//head tag
                    {
                        LogWriter.log(CommData.INFO,"接收参数包开始");
                        isRead(buff,5,16,in);
                        //判断当前的包头是否是要接收的数据包头
                       /* JavaStruct.unpack(hd, buff, ByteOrder.BIG_ENDIAN);
                        //cf.ReceivePackIndex = hd.PackIndex;
                        //cf.ReceivePackTag = hd.PackTag;
                        hd.PackIndex =IntLHToHL(hd.PackIndex);
                        hd.PackCount =IntLHToHL(hd.PackCount);
                        hd.ParamSize =IntLHToHL(hd.ParamSize);
                        hd.TestNo =IntLHToHL(hd.TestNo);
                        */
                        JavaStruct.unpack(hd, buff, ByteOrder.LITTLE_ENDIAN);
                        isRead(buff,21,512-21,in);
                        if (buff[511]!=70){//tail→F
                            continue;
                        }
                        if (hdCheckBitEx(buff)){
                            cf.ReceiveConfirmTag=1;
                        }else{
                            cf.ReceiveConfirmTag=0;
                        }
                        cf.ReceivePackIndex = hd.PackIndex;
                        cf.ReceivePackTag = hd.PackTag;
                        cf.PackHead=new byte[]{71,90,74,67};
                        cf.PackTag = 5;
                        cf.PackCount = 1;//转成低高位
                        cf.PackIndex = 1;//转成低高位
                        cf.PackTail = 70; //F
                        confirm = JavaStruct.pack(cf, ByteOrder.LITTLE_ENDIAN);
                        confirm= cfCheckBit(confirm);
                        out.write(confirm);
                        out.flush();
                        LogWriter.log(CommData.INFO,"接收参数包完成");
                        //重发时，应将outStream 缓存输出，并关闭，然后删除文件
                       /* if(outStream!=null) {
                            outStream.close();
                        }*/
                        //判断是否已经存在此文件(当前蓝牙连接超时重发,应先把文件给删除)
                        DeleteFile(shortFileName);
                        G_nPackIndex =99999999;//初始化接受包的序号
                        //System.out.println("out1 length="+out.size());
                    }
                    else if(buff[4]==2){ //data tag
                        LogWriter.log(CommData.INFO,"接收数据包开始");
                        isRead(buff, 5,12,in);//edit by wt
                       /* JavaStruct.unpack(data, buff, ByteOrder.BIG_ENDIAN);

                        data.PackIndex =IntLHToHL(data.PackIndex);
                        data.PackCount =IntLHToHL(data.PackCount);
                        data.ParamSize =IntLHToHL(data.ParamSize);
                        */
                        JavaStruct.unpack(data, buff, ByteOrder.LITTLE_ENDIAN);
                        nCountLength= data.PackCount;
                        isRead(buff, 17,512 - 17, in);
                        if (buff[511]!=70){//tail→F
                            continue;
                        }
                        if(data.PackIndex!=G_nPackIndex) {
                            if (hdCheckBitEx(buff)) {
                                if (G_nPackIndex == 99999999) {
                                    if (shortFileName == "") {
                                        shortFileName = UUID.randomUUID().toString() + ".prt";
                                        sendResult(shortFileName);
                                        shortFileName = filename + "/" + shortFileName;
                                        //filename += "/" + shortFileName;
                                        outStream = new FileOutputStream(shortFileName);
                                    }
                                    //outStream = new FileOutputStream(filename);
                                    //sendResult(shortFileName);
                                    //outStream = new FileOutputStream(shortFileName);
                                }
                                cf.ReceiveConfirmTag = 1;
                                outStream.write(buff, 17, data.ParamSize);
                                //  SendMessage("接收包号" + data.PackIndex, data.ParamSize, data.PackTag, nCountLength);
                                SendMessage("接收包号" + data.PackIndex, data.PackIndex, data.PackTag, nCountLength);
                                G_nPackIndex = data.PackIndex;
                            } else {
                                cf.ReceiveConfirmTag = 0;
                            }
                            cf.ReceivePackIndex = data.PackIndex;
                            cf.ReceivePackTag = data.PackTag;
                            cf.PackHead = new byte[]{71, 90, 74, 67};
                            cf.PackTag = 5;
                            //cf.PackCount = IntHLToLH(1);
                            //cf.PackIndex = IntHLToLH(1);
                            cf.PackCount = 1;
                            cf.PackIndex = 1;
                            cf.PackTail = 70; //F
                            //confirm = JavaStruct.pack(cf, ByteOrder.BIG_ENDIAN);
                            confirm = JavaStruct.pack(cf, ByteOrder.LITTLE_ENDIAN);
                            confirm = cfCheckBit(confirm);
                            out.write(confirm);
                            out.flush();
                            LogWriter.log(CommData.INFO,"接收第"+G_nPackIndex+"数据包完成");
                            //System.out.println("out2 length=" + out.size());
                        }
                    }
                    else if(buff[4]==4) {//finish tag
                        LogWriter.log(CommData.INFO,"接收结束包开始");
                        isRead(buff, 5, 12, in);//edit by wt
                        /*
                        JavaStruct.unpack(data, buff, ByteOrder.BIG_ENDIAN);

                        data.PackIndex = IntLHToHL(data.PackIndex);
                        data.PackCount = IntLHToHL(data.PackCount);
                        data.ParamSize = IntLHToHL(data.ParamSize);*/
                        JavaStruct.unpack(data, buff, ByteOrder.LITTLE_ENDIAN);
                        nCountLength = data.PackCount;
                        isRead(buff, 17, 512 - 17, in);
                        if (buff[511] != 70) {//tail→F
                            continue;
                        }
                        if (hdCheckBitEx(buff)) {
                            cf.ReceiveConfirmTag = 1;
                        } else {
                            cf.ReceiveConfirmTag = 0;
                        }
                        cf.ReceivePackIndex = data.PackIndex;
                        cf.ReceivePackTag = data.PackTag;
                        cf.PackHead = new byte[]{71, 90, 74, 67};
                        cf.PackTag = 5;
                        cf.PackCount = 1;
                        cf.PackIndex = 1;
                        cf.PackTail = 70; //F
                        confirm = JavaStruct.pack(cf, ByteOrder.LITTLE_ENDIAN);
                        confirm = cfCheckBit(confirm);
                        out.write(confirm);
                        out.flush();
                        LogWriter.log(CommData.INFO,"接收结束包完成");
                        // System.out.println("out4 length=" + out.size());
                        isFinish = true;
                    }
                    else
                    {
                        continue;
                    }
                    if (isFinish) {
                        if(outStream != null) {
                            outStream.flush();
                            outStream.close();
                        }
                        LogWriter.log(CommData.INFO,"蓝牙连接开始断开");
                        close();
                        LogWriter.log(CommData.INFO,"蓝牙连接断开成功");
                        SendMessage("接受完毕", nCountLength, (byte) 4, nCountLength);
                        LogWriter.log(CommData.INFO,TAG+"1.run 退出");
                        //Log.i(TAG, "1.run 退出");
                        break;
                    }
                } else {
                    Log.i(TAG, "没有可用连接");
                   // connect();
                }
            } catch (Exception e) {
                LogWriter.log(ERROR,"数据接收错误" + e.getMessage());
                e.printStackTrace();
                continue;
            }
        }
    }

    /*
    * 暂且不提取run()中提交的相同函数 (报异常)
    * */
    public void SendConfirmPacket( int ReceivePackIndex,byte ReceivePackTag,byte ReceiveConfirmTag){
        TFConfirm cf = new TFConfirm();
        cf.ReceiveConfirmTag = ReceiveConfirmTag;
        cf.ReceivePackTag =ReceivePackTag;
        cf.ReceivePackIndex = ReceivePackIndex;
        cf.PackHead = new byte[]{71, 90, 74, 67};
        cf.PackTag = 5;
        cf.PackCount = IntHLToLH(1);
        cf.PackIndex = IntHLToLH(1);
        cf.PackTail = 70; //F
        //confirm = JavaStruct.pack(cf, ByteOrder.BIG_ENDIAN);
        //confirm = cfCheckBit(confirm);
        //out.write(confirm);
        //out.flush();
    }
    /**
     * 关闭连接
     */
    public void close()
    {
        try
        {
            if (server != null)
            {
                Log.i(TAG, "close in");
                in.close();
                Log.i(TAG, "close out");
                out.close();
                Log.i(TAG, "close client");
                server.close();
            }
        }
        catch (Exception e)
        {
            LogWriter.log(ERROR,"关闭连接异常" + e.getMessage());
            e.printStackTrace();
        }
    }


    public  int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }

    public  Boolean hdCheckBit(byte[] buff,byte[] buff1 ){
        int hcb=0;
        for(int i=0;i<509;i++){
            hcb += byteToInt(buff[i]);
        }
        hcb += byteToInt( buff1[buff1.length-3]);
        if(((byte)(hcb%255))==buff1[buff1.length-2]) {
            return true;
        }else{
            return false;
        }
    }
    //整个包 校验是校验位 前的所有byte→int相加 对255求余
    public  Boolean hdCheckBitEx(byte[] buff){
        int hcb=0;
        for(int i=0;i<510;i++){
            hcb += byteToInt(buff[i]);
        }
        //hcb += byteToInt( buff[buff.length-3]);
        if(((byte)(hcb%255))==buff[510]) {
            return true;
        }else{
            return false;
        }
    }
    //确认包校验位
    public  byte[] cfCheckBit(byte[] buff){
        int sum=0;
        for(int i=0;i< buff.length-2;i++){
            sum+=byteToInt(buff[i]);
        }
        sum=sum%255;
        buff[20]=(byte)sum;
        return buff;
    }

    //整数 低高位 变成 高低位
    public int IntLHToHL(int temp){
        // 将每个字节取出来  
        byte byte4 = (byte) (temp & 0xff);
        byte byte3 = (byte) ((temp & 0xff00) >> 8);
        byte byte2 = (byte) ((temp & 0xff0000) >> 16);
        byte byte1 = (byte) ((temp & 0xff000000) >> 24);
        // 拼装成 正确的int  
        int realint = ((byte1 & 0xff)<<0)+((byte2& 0xff)<<8) + ((byte3 & 0xff)<< 16) +((byte4& 0xff)<<24);
        return realint;
    }
    //整数 高低位 变成 低高位
    public int IntHLToLH(int temp){
        // 将每个字节取出来  
        byte byte1 = (byte) (temp & 0xff);
        byte byte2 = (byte) ((temp & 0xff00) >> 8);
        byte byte3 = (byte) ((temp & 0xff0000) >> 16);
        byte byte4 = (byte) ((temp & 0xff000000) >> 24);
        // 拼装成 正确的int  
        int realint = ((byte1 & 0xff)<<0)+((byte2& 0xff)<<8) + ((byte3 & 0xff)<< 16) +((byte4& 0xff)<<24);
        return realint;
    }
    public void DeleteFile(String filename1){
        if (filename1 != "") {
            File file = new File(filename1);
            if (file.exists()) {
                file.delete();
            }
        }
    }

}