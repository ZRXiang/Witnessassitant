package com.example.phobes.witnessassitant.model;

import java.io.InputStream;

/**
 * Created by phobes on 2016/6/15.
 */
public class Packet {
    final int HEAD_LENGTH = 12;
    final int HEAD_TAG_LENGTH = 4;
    final int HEAD_TYPE_LENGTH = 4;
    final int HEAD_LENGTH_LENGTH = 4;
    final int FILE_NAME_LENGTH = 127;
    final int FILE_HEAD_LENGTH = 1024;
//    final int PACKET_TAG = 0xAAAABBBB;
//    final int TYPE_BEG = 0x01;
//    final int TYPE_DATA = 0x02;
//    final int TYPE_END = 0x03;
//    final int TYPE_RCV = 0x04;
    private byte[] headTag = new byte[HEAD_TAG_LENGTH];
    private byte[] headType=new byte[HEAD_TYPE_LENGTH];
    private byte[] headLength = new byte[HEAD_LENGTH_LENGTH];
    private  byte[] fileName = new byte[FILE_NAME_LENGTH];
    private  byte[] fileHead = new byte[FILE_HEAD_LENGTH];
    private byte[] packetBody;
    private byte[] packet;


    public byte[] getHeadLength() {
        return headLength;
    }

    public int setHeadLength(InputStream inputStream)throws Exception {
        return inputStream.read(headLength);
    }

    public int setHeadTag(InputStream inputStream) throws Exception{
        return inputStream.read(headTag);
    }

    public byte[] getHeadTag() {
        return headTag;
    }

    public byte[] getHeadType() {
        return headType;
    }

    public int setHeadType(InputStream inputStream) throws Exception {
        return inputStream.read(headType);
    }

    public byte[] getPacketBody() {
        return packetBody;
    }

    public int setPacketBody(InputStream inputStream) throws Exception {
        return inputStream.read(packetBody);
    }
    public void setPacket(byte[] headTag,byte[] headType,byte[] headLength){
        packet = new byte[headTag.length+headType.length+headLength.length];
        System.arraycopy(headTag,0,packet,0,headTag.length);
        System.arraycopy(headType,0,packet,headTag.length,headType.length);
        System.arraycopy(headLength,0,packet,headTag.length+headType.length,headLength.length);
    }
    public void setPacket(byte[] headTag,byte[] headType,byte[] headLength,byte[] body){
        packet = new byte[headTag.length+headType.length+headLength.length+body.length];
        System.arraycopy(headTag,0,packet,0,headTag.length);
        System.arraycopy(headType,0,packet,headTag.length,headType.length);
        System.arraycopy(headLength,0,packet,headTag.length+headType.length,headLength.length);
        System.arraycopy(body,0,packet,headTag.length+headType.length+headLength.length,body.length);
    }
    public byte[] getPacket(){
        return packet;
    }
}
