package com.example.phobes.witnessassitant.sitedata;

import com.example.phobes.witnessassitant.struct.StructField;

import java.io.Serializable;

/**
 * Created by aipsys on 2017/3/25.
 */

public class DeepthDataHeader   implements Serializable {
    @StructField(order = 0)
    public int nDepth;                // 测点深度
    @StructField(order = 1)
    public float fEllipsisTime;      // 声时
    @StructField(order = 2)
    public float fAmplitude;         //试验任务 （试验号）
    @StructField(order = 3)
    public float fPsd;               //Psd
    @StructField(order = 4)
    public float fI1;                //I1
}
