package com.example.phobes.witnessassitant.sitedata;

//http://www.cnblogs.com/shaocm/p/3528346.html
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.service.LogWriter;
import com.example.phobes.witnessassitant.struct.JavaStruct;
import com.example.phobes.witnessassitant.struct.StructException;
import com.example.phobes.witnessassitant.struct.TFState;
import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;




public class TcpClientThread extends Thread {
    //通讯控制常量
    private static byte PARAMDESC = 1;
    private static byte WAVE_FILE = 2;
    private static byte LOG_DATA = 3;
    private static  byte START = 1;
    private static byte TRANSPORTING = 2;
    private static byte RE_SEND = 3;
    private static byte CONFIRM = 4;
    private static byte CANCEL = 5;
    private static byte PARTEND =6;
    private static byte TESTEND = 7;
    private static short HEADLEN = 31;

    private static short HIGH_STRAIN =   1501;
    private static short LOW_STRAIN  =   1501;
    private static short ULTRASONIC  =   1502;
    private static short K30         =   1502;
    private static short EVD         =   1502;
    private static short STATIC_LOAD =   1502;
    private static short SURFACE_WAVE=   1502;
    private static short BOLT_TEST   =   1502;
    private static short RADAR_SCAN  =   1502;

    private String ip = "10.0.0.113";
    private int port = 13000;
    private String TAG = "socket thread";
    private int timeout = 10000;

    public boolean isFinish = false;
    public int G_nPackIndex =99999999;
    public static Socket server = null;
    public String filename;
    public String shortFileName;
    DataOutputStream dsOut;
    DataInputStream dsIn;
    public boolean isRun = true;
    Handler hRead;
    Handler hWrite;
    Context ctx;
    private String TAG1 = "===Send===";
    SharedPreferences sp;
    private int nCountLength;
    private String mOrderId;

    private final String DEBUG="DEBUG";
    private final String ERROR="ERROR";
    private byte []aHeader = new byte[64];            //接收头缓冲区
    private byte []aData = new byte[5120];            //接收数据缓冲区
    private CommHeader ReadHeader = new CommHeader(); //报文头结构
    private ByteArrayOutputStream  bosParamDesc;      //数据流用于保存描述性数据
    private ByteArrayOutputStream  bosWaveOrFile;     //数据流用于保存文件或波形数据
    private String sMachineVendor,sMachineId,sPileId,sChannelId;       //厂家标识
    private int nObjectId;                           //试验分类
    private int nTaskId;

    public TcpClientThread(Handler hReceive, Handler hSend, Context context,String orderId) {
        hRead = hReceive;
        hWrite = hSend;
        ctx = context;
        mOrderId=orderId;


        filename=context.getFilesDir().getPath();  //手机内存目录
        LogWriter.log(DEBUG,"创建线程SocketThread");
    }


    public void setSocket(Socket sktConn){
        try {
            server = sktConn;
            dsIn = new DataInputStream(new BufferedInputStream(server.getInputStream()));
            dsOut = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));
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
        Message msg = hWrite.obtainMessage();
        msg.obj = st;
        msg.what = 1;
        hWrite.sendMessage(msg);// 结果返回给UI处理
    }

    public void sendResult(String msg){
        Message msgStr = hWrite.obtainMessage();
        msgStr.obj = msg;
        msgStr.what = 1;
        hRead.sendMessage(msgStr);
    }

    private  int ReadSocket(byte[] buffer,int size)
    {
        int nReadTotal = 0; // 已经成功读取的字节的个数
        int nTemp;
        while (nReadTotal < size)
        {
            try
            {
                nTemp = dsIn.read(buffer, nReadTotal, size - nReadTotal);
                if (nTemp == -1)
                    break;
                nReadTotal += nTemp;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                nReadTotal  = -1;
            }
        }
        return nReadTotal;
    }

    private boolean unpack(Object o,byte[] buffer, ByteOrder order) {
        try {
            JavaStruct.unpack(o, buffer, order);
            return true;
        }   catch (StructException e)  {
            e.printStackTrace();
        }
        return false;
    }

    private byte[] pack(Object o, ByteOrder order) {
        byte[] aTemp;
        try {
            aTemp = JavaStruct.pack(o, order);
            return aTemp;
        }   catch (StructException e)  {
            e.printStackTrace();
        }
        return null;
    }

    private  int ReadPacket()
    {
        int nReadLen = 0; // 已经成功读取的字节的个数
        int nTemp;
        if (ReadSocket(aHeader,HEADLEN) == HEADLEN)
        {
            if (unpack(ReadHeader, aHeader, ByteOrder.LITTLE_ENDIAN))
            {
                nReadLen = ReadSocket(aData,ReadHeader.nLength);
                if (nReadLen == ReadHeader.nLength)
                    return nReadLen;
            }
        }
        return -1;
    }

    private void Sleep(long nTime)
    {
        try {
            Thread.sleep(nTime);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private boolean RequestLostData(int nPacketId)
    {
        CommHeader WriteHeader = new CommHeader();
        WriteHeader.OrderId = ReadHeader.OrderId;
        WriteHeader.sObjectId = ReadHeader.sObjectId;
        WriteHeader.nPacketId = nPacketId;
        WriteHeader.cDataCategory = ReadHeader.cDataCategory;
        WriteHeader.cStage = RE_SEND;
        WriteHeader.nLength = 0;

        byte[] aSend = pack(WriteHeader, ByteOrder.LITTLE_ENDIAN);
        if (aSend != null){
            try {
                dsOut.write(aSend);
                return true;
            }catch (IOException e)
            {
                    e.printStackTrace();
                   return false;
            }
        }
       return false;
    }
    private boolean DataConfirm()
    {
        CommHeader WriteHeader = new CommHeader();
        WriteHeader.OrderId = ReadHeader.OrderId;
        WriteHeader.sObjectId = ReadHeader.sObjectId;
        WriteHeader.nPacketId = ReadHeader.nPacketId;
        WriteHeader.cDataCategory = ReadHeader.cDataCategory;
        WriteHeader.cStage = CONFIRM;
        WriteHeader.nLength = 0;
        byte[] aSend = pack(WriteHeader, ByteOrder.LITTLE_ENDIAN);
        if (aSend != null){
            try {
                dsOut.write(aSend);
                return true;
            }catch (IOException e)
            {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * 实时接受数据
     */
    @Override
    public void run() {
        int nDataLen;
        int OrderId;
        String sObjectId;
        int nLastChannelId=-1,nCurrChannelId=-1,nLastPacketId=-1,nCurrPacketId=-1;
        bosParamDesc = new ByteArrayOutputStream();
        bosWaveOrFile = new ByteArrayOutputStream();
        FileOutputStream outStream = null;
        while (isRun) {
            if (server != null) {     //如果连接尚未建立， 先行空转
                Sleep(1000);
                continue;
            }
            nDataLen = ReadPacket();  //接收一个完整报文
            if (nDataLen != -1) {
               if ( ReadHeader.nPacketId == 1)
               {
                   nLastChannelId = ReadHeader.cChannelId;
                   nLastPacketId = ReadHeader.nPacketId;
                   sObjectId = new String(ReadHeader.sObjectId);
               }
               else {
                   if (ReadHeader.nPacketId != nLastPacketId+1) {
                       if (!RequestLostData(nLastPacketId+1))
                            LogWriter.log(ERROR,"请求重传失败");
                       continue;
                   }
               }
                if (ReadHeader.cDataCategory == PARAMDESC)
                {
                    bosParamDesc.write(aData,0,nDataLen);
                    if ((ReadHeader.cStage == PARTEND) || (ReadHeader.cStage == TESTEND))
                        ParamDescProcess(bosParamDesc);
                }
                else  if (ReadHeader.cDataCategory == WAVE_FILE)
                {
                    bosParamDesc.write(aData,0,nDataLen);
                    if ((ReadHeader.cStage == PARTEND) || (ReadHeader.cStage == TESTEND))
                        WaveFileDescProcess(bosWaveOrFile);
                }
                else if (ReadHeader.cDataCategory == LOG_DATA)
                {
                    if ((ReadHeader.cStage == PARTEND) || (ReadHeader.cStage == TESTEND))
                        LogProcess(aData,nDataLen);
                }
                if (!DataConfirm())
                    LogWriter.log(ERROR,"数据确认发送失败");
                if (ReadHeader.cStage == TESTEND )
                {
                    close();
                    break;
                }
            }
        }
    }

    private boolean ParamDescProcess(ByteArrayOutputStream bsParam)
    {
        byte[]cParam = bsParam.toByteArray();
        String sParam = cParam.toString();
        try {
            JSONObject jsonObject = new JSONObject(sParam);
            sMachineVendor = jsonObject.getString("设备商标识");
            sMachineId = jsonObject.getString("测试仪编号");
            if ((nObjectId == HIGH_STRAIN) || (nObjectId == LOW_STRAIN))
            {
                sPileId = jsonObject.getString("试桩编号");
                sChannelId = jsonObject.getString("当前锤数");
            }
            else if ((nObjectId == K30) || (nObjectId == STATIC_LOAD) || (nObjectId == SURFACE_WAVE) || (nObjectId == BOLT_TEST) || (nObjectId == EVD))
            {
                sPileId = jsonObject.getString("试桩编号");
            }
            bsParam.reset();
            return CommData.dbSqlite.SaveParamDescData(nTaskId,sPileId,sMachineVendor,sMachineId,sParam);

        } catch (JSONException e) {
               e.printStackTrace();
            return false;
        }
    }


    private boolean  WaveFileDescProcess(ByteArrayOutputStream bsData)
    {
        byte[] cWave = ZLibCompress(bsData.toByteArray());
        bsData.reset();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());   //获取当前时间       
        String strDate = formatter.format(curDate);
        return CommData.dbSqlite.SaveSiteTestData(nTaskId,sPileId,sChannelId,strDate,cWave);
    }

    private boolean LogProcess(byte[] aData,int nLength)
    {
        byte[] aLog = new byte[nLength];
        System.arraycopy(aData,0,aLog,0,nLength);
        return CommData.dbSqlite.SaveSiteTestLog(nTaskId,sPileId,sChannelId,aLog);
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
                dsIn.close();
                dsOut.close();
                server.close();
            }
        }
        catch (Exception e)
        {
            LogWriter.log(ERROR,"关闭连接异常" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void DeleteFile(String filename1){
        if (filename1 != "") {
            File file = new File(filename1);
            if (file.exists()) {
                file.delete();
            }
        }
    }
    private byte[] ZLibCompress(byte [] aData)
    {
        int nInputLen;
        int nOutputLen;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ZOutputStream zOut = new ZOutputStream(out, JZlib.Z_BEST_COMPRESSION);
            ObjectOutputStream objOut = new ObjectOutputStream(zOut);
            objOut.writeObject(aData);
            zOut.close();
            byte [] aOut = out.toByteArray();
            return aOut;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

}
/*
    public String decompressData(String encdata) {
         try {
             ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
             InflaterOutputStream zos = new InflaterOutputStream(bos); 
             zos.write(convertFromBase64(encdata)); 
             zos.close(); 
             return new String(bos.toByteArray()); 
             } catch (Exception ex) {
             ex.printStackTrace(); 
             return "UNZIP_ERR"; 
             }
         }

         //压缩
         public String compressData(String data) {
         try {
         ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
         DeflaterOutputStream zos = new DeflaterOutputStream(bos); 
         zos.write(data.getBytes()); 
         zos.close(); 
         return new String(convertToBase64(bos.toByteArray())); 
         } catch (Exception ex) {
         ex.printStackTrace(); 
         return "ZIP_ERR"; 
         }
         }
        */