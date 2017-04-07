package com.example.phobes.witnessassitant.struct;

/**
 * Created by Administrator on 16-9-7.
 */
@StructClass
public class TFConfirm {
   /* @StructField(order = 0)
    public byte PackTag;                    //包标识：1开始包，2数据包，3日志包，4结束包，5确认包
    @StructField(order = 1)
    public byte ReceivePackTag;            //接收到的包标识：1开始包，2数据包，3日志包，4结束包
    @StructField(order = 2)
    public byte ReceivePackIndex;         //接收到的当前包索号（从1开始）
    @StructField(order = 3)
    public int ReceiveConfirmTag;        	//接收到的当前包状态：0错误，1正确
   @StructField(order = 4)
    public byte CheckBit;  */                 //奇偶校验位


    //--------------yang 2016/10/08-----------------
    @StructField(order = 0)
    public byte[] PackHead;                      //包头
    @StructField(order = 1)
    public byte PackTag;                     //包标识：1开始包，2数据包，3日志包，4结束包，5确认包
    @StructField(order = 2)
    public int PackCount;                  // 包数量
    @StructField(order = 3)
    public int PackIndex;                   //当前包序号
    @StructField(order = 4)
    public byte ReceivePackTag;            //接收到的包标识：1开始包，2数据包，3日志包，4结束包
    @StructField(order = 5)
    public int ReceivePackIndex;         //接收到的当前包索号（从1开始）
    @StructField(order = 6)
    public byte ReceiveConfirmTag;        	//接收到的当前包状态：0错误，1正确
    @StructField(order = 7)
    public byte Memo;                   //备用
    @StructField(order = 8)
    public byte CheckBit;                   //奇偶校验位
    @StructField(order = 9)
    public byte PackTail;                   //包尾
}
