package com.example.phobes.witnessassitant.model;

import com.example.phobes.witnessassitant.util.DataTypeConv;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by phobes on 2016/6/15.
 */
public class SendData {
    private String filename;
    private int sendType;
    public SendData(String filename, int sendType) {
        this.filename = filename;
        sendType = sendType;
    }

    public byte[] ConvertToPacket() throws Exception {

        Packet packetStart = new Packet();
        byte[] headTag = new byte[4];
        headTag=DataTypeConv.intToByteArray(0xAAAABBBB);
        byte[] headTypeBeg = new byte[4];
        headTypeBeg = DataTypeConv.intToByteArray(0x01);
        byte[] headTypeData = new byte[4];
        headTypeData = DataTypeConv.intToByteArray(0x02);
        byte[] headTypeRec = new byte[4];
        headTypeRec = DataTypeConv.intToByteArray(0x03);
        byte[] headTypeEnd = new byte[4];
        headTypeEnd = DataTypeConv.intToByteArray(0x04);
        FileInputStream fis = new FileInputStream(new File(filename));
//        FileInputStream fis = new FileInputStream(new File(filename));
        byte[] fileData = new byte[fis.available()];//新建一个字节数组
        fis.read(fileData);//将文件中的内容读取到字节数组中
        fis.close();
        byte[] headLength = new byte[4];
        headLength = DataTypeConv.intToByteArray(1152);
        byte[] body = new byte[1152];
        byte[] filenameByte = new byte[127];
        filenameByte = filename.getBytes();
        byte[] fileHead = new byte[1024];
        System.arraycopy(filenameByte,0,body,0,filenameByte.length);
        System.arraycopy(fileHead,0,body,filenameByte.length,fileHead.length);

                packetStart.setPacket(headTag,headTypeBeg,headLength,body);
        byte[] start = packetStart.getPacket();
        Packet packetData = new Packet();

        headLength = DataTypeConv.intToByteArray(fileData.length + 12);
        body = new byte[fileData.length];
        System.arraycopy(fileData,0,body,0,fileData.length);
        packetData.setPacket(headTag,headTypeData,headLength,body);
        byte[] transBody = packetData.getPacket();
        Packet packetEnd  = new Packet();
        headLength = DataTypeConv.intToByteArray(1152);
        packetEnd.setPacket(headTag,headTypeEnd,headLength);
        byte[] end = packetEnd.getPacket();
        byte[] total = new byte[start.length+transBody.length+end.length];
        System.arraycopy(start,0,total,0,start.length);
        System.arraycopy(transBody,0,total,start.length,transBody.length);
        System.arraycopy(end,0,total,start.length+transBody.length,end.length);
        return total;
    }
}
