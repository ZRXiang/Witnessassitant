package com.example.phobes.witnessassitant.struct;

import java.io.Serializable;
/**
 * Created by Administrator on 16-9-7.
 */
@StructClass
public class TFHeader  implements Serializable {
   /* @StructField(order = 0)
    public int TestNo;      //试件号（试验号）
    @StructField(order = 1)
    public byte PackTag;                    //包标识：1开始包，2数据包，3日志包，4结束包，5确认包
    @StructField(order = 2)
    public byte PackCount;                  //开始包数量（如果参数部分超过496字节需要分解成多个开始包）
    @StructField(order = 3)
    public byte PackIndex;                  //当前开始包索号（从1开始）
    @StructField(order = 4)
    public int PackSize ;                   //本包总长度
    @StructField(order = 5)
    public int ParamSize;                   //本包参数设定部分数据长度
    @StructField(order = 6)
    public byte CheckBit;                   //奇偶校验位
   /* @StructField(order = 7)
    public byte[] Data;  */               //参数部分数据（厂商自己组织，含检测开始前设定参数）

    //----------yang  2016/10/08----------------

    @StructField(order = 0)
    public byte[] PackHead =new byte[4];                    //包头
    @StructField(order = 1)
    public byte PackTag;                    //包标识：1开始包，2数据包，3日志包，4结束包，5确认包
    @StructField(order = 2)
    public int TestNo;                       //试件号（试验号）

    @StructField(order = 3)
    public int  PackCount;                  //开始包数量（如果参数部分超过496字节需要分解成多个开始包）
    @StructField(order = 4)
    public int PackIndex;                  //当前开始包索号（从1开始）

    @StructField(order = 5)
    public int ParamSize;                   //本包参数设定部分数据长度
   /* @StructField(order = 5)
    public int PackSize ;                   //本包总长度
     @StructField(order = 7)
    public byte Memo;                    //备用
    @StructField(order =8)
    public byte CheckBit;                    //奇偶校验位
    @StructField(order =9)
    public byte PackTail;                    //奇偶校验位
    */
}
