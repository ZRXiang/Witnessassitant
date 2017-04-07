package com.example.phobes.witnessassitant.sitedata;

import com.example.phobes.witnessassitant.struct.StructClass;
import com.example.phobes.witnessassitant.struct.StructField;

import java.io.Serializable;

/**
 * Created by aipsys on 2017/3/25.
 */


public class CommHeader  implements Serializable {
        @StructField(order = 0)
        public byte cDataCategory;                // 数据传输分类  0x01: 参数描述数据   0x02: 波形或文件数据  0x03: 日志数据
        @StructField(order = 1)
        public byte cStage;                        // 数据传输阶段：0x01: 启动，0x02 传输中，0x03 重传，0x04 确认，0x05 取消  0x06 结束
        @StructField(order = 2)
        public int OrderId;                        //试验任务 （试验号）
        @StructField(order = 3)
        public byte []sObjectId = new byte[16];   //测试物  桩号或测点标识
        @StructField(order = 4)
        public byte cChannelId;                    //通道号（从1开始）  如剖面号，
        @StructField(order = 5)
        public int nPacketId;                      //通讯包序号，
        @StructField(order = 6)
        public short nCrc;                         //数据包校验字，
        @StructField(order = 7)
        public short nLength;                        //数据长度
}


