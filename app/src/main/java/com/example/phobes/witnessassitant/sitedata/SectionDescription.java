package com.example.phobes.witnessassitant.sitedata;

import com.example.phobes.witnessassitant.struct.StructField;

import java.io.Serializable;

/**
 * Created by aipsys on 2017/3/25.
 */

public class SectionDescription   implements Serializable {
    @StructField(order = 0)
    public float fDistance;           // 跨距
    @StructField(order = 1)
    public float fFrequency;          // 主频
    @StructField(order = 2)
    public float fAvgSpeed;          //平均声速
    @StructField(order = 3)
    public float fCriticalValue;    //声速异常判定值（临界值）
    @StructField(order = 4)
    public float fStdDev;            //声速标准差
    @StructField(order = 5)
    public float fCoeVar;            //离散系数
    @StructField(order = 6)
    public float fUniformGrade;      //均匀性等级
    @StructField(order = 7)
    public byte cClass;              //类别
    @StructField(order = 8)
    public float fMinSpeed;          //波速最小值
    @StructField(order = 9)
    public float fAvgAmp;             //平均波幅
    @StructField(order = 10)
    public float fCriticalAmp;       //波幅临界值
    @StructField(order = 11)
    public float fMinAmp;             //波幅最小值
}