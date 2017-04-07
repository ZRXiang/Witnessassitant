package com.example.phobes.witnessassitant.sitedata;

import com.example.phobes.witnessassitant.struct.StructField;

import java.io.Serializable;

/**
 * Created by aipsys on 2017/3/25.
 */

public class K30Data   implements Serializable {
    @StructField(order = 0)
    public byte []sSampleTime=new byte[20];    // 测试时间
    @StructField(order = 1)
    public byte cLoadDirect;                   // 荷载方向  0:卸载  1 加载
    @StructField(order = 2)
    public byte cGrade;                        //加载级别
    @StructField(order = 3)
    public short nTimeElapse;                 //采样累计时间  单位  分钟
    @StructField(order = 4)
    public float fLoad;                       //实际荷载
    @StructField(order = 5)
    public float fPressure;                  //实测油压
    @StructField(order = 6)
    public float []aShift=new float[16];     //实测位移
}
