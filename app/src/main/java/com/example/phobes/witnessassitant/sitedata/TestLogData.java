package com.example.phobes.witnessassitant.sitedata;

import com.example.phobes.witnessassitant.struct.StructField;

import java.io.Serializable;

/**
 * Created by aipsys on 2017/3/25.
 */

public class TestLogData   implements Serializable {
    @StructField(order = 0)
    public short nParamId;    // 参数编号
    @StructField(order = 1)
    public float fOldValue;                   // 旧值
    @StructField(order = 2)
    public float fNewValue;                   // 新值
    @StructField(order = 3)
    public byte []sModifyTime=new byte[20];  //修改时间 yyyy-MM-dd hh:mm:ss
    public byte []sContent=new byte[64];     //修改内容 文本
}
