package com.example.phobes.witnessassitant.struct;

import java.io.Serializable;

/**
 * Created by Administrator on 16-9-7.
 */
@StructClass
public class TFTail implements Serializable {

    @StructField(order = 0)
    public int PackHead;                      //包头
    @StructField(order = 1)
    public byte PackTag;                    //包标识：1开始包，2数据包，3日志包，4结束包，5确认包
    @StructField(order = 2)
    public int PackCount;                  //结束包数量（如果参数部分超过496字节需要分解成多个开始包）
    @StructField(order = 3)
    public int PackIndex;                   //当前包序号
    @StructField(order = 4)
    public int PackSize;                   //本包总长度
   /* @StructField(order = 5)
    public byte[] PackData;                //包数据
    @StructField(order = 6)
    public byte Memo;                   //备用
    @StructField(order = 7)
    public byte CheckBit;                   //奇偶校验位
    @StructField(order = 8)
    public byte PackTail;   */                //包尾
}
