package com.example.phobes.witnessassitant.util;

import com.example.phobes.witnessassitant.model.WitenessData;

/**
 * Created by phobes on 2016/6/10.
 */
public class ParaseWitness {
    //分割每行
    public static String[] spitDataRow(String sourceStr){
        return sourceStr.split("\\^");
    }
    //分割每列
    public static String[] splitDataColumn(String sourceStr){
        return sourceStr.split("@",16);
    }
    public static boolean isSampleWitness(String workflow){
        return isNeedWitness(workflow,0);
    }
    public static boolean isTestWitness(String workflow){
        return  isNeedWitness(workflow,1);
    }
    public  static boolean isNeedWitness(String workflow,int position){
        char tag = workflow.charAt(position);

        if(tag=='1'){
            return true;
        }else {
            return false;
        }
    }
    public static WitenessData toWitnessData(String witenessStr,String type){
        WitenessData witenessData = new WitenessData();
        String[]  columns= splitDataColumn(witenessStr);

        witenessData.setApply_from(Integer.parseInt(columns[1]));
        witenessData.setWitness_id(Integer.parseInt(columns[2]));
        witenessData.setSample_id(columns[3]);
        witenessData.setApply_time(columns[4]);
        witenessData.setObject_name(columns[5]);
        witenessData.setObject_id(Integer.parseInt(columns[6]));
        witenessData.setSample_org_id(columns[7]);
        witenessData.setSample_org_name(columns[8]);
        witenessData.setWitness_org_id(columns[9]);
        witenessData.setWitness_org_name(columns[10]);
        witenessData.setTest_items(columns[12]);
        if(!columns[11].equals(""))witenessData.setEntry_id(Integer.parseInt(columns[11]));
        if(type.equals("test")){
            witenessData.setWitnessType(3);
            witenessData.setUploadded(0);
        }else{
            witenessData.setWitnessType(1);
            witenessData.setUploadded(0);
        }
        return witenessData;
    }
    public static void main(String[] args){

//        String source = "@1@98@20160602-001@2016-06-02 15:10:51@钢筋原材试验@1000@101030101@中心试验室@中铁十四局@Testv7-8080@@@";
//        String[] columns = splitDataColumn(source);
//        for(int i = 0;i<columns.length;i++){
//            System.out.println(i+" : "+ columns[i]);
//        }
//        WitenessData witenessData = new WitenessData();
//        witenessData.setWitness_id(Integer.parseInt(columns[2]));
//        witenessData.setSample_id(columns[3]);
//        witenessData.setApply_time(columns[4]);
//        witenessData.setObject_name(columns[5]);
//        witenessData.setObject_id(Integer.parseInt(columns[6]));
//        witenessData.setSample_org_id(columns[7]);
//        witenessData.setSample_org_name(columns[8]);
//        witenessData.setWitness_org_id(columns[9]);
//        witenessData.setWitness_org_name(columns[10]);
        String workflow="111111";
        String workflow2="011111";
        System.out.println(isTestWitness(workflow));
        System.out.println(isTestWitness(workflow2));
        System.out.println(isSampleWitness(workflow));
        System.out.println(isSampleWitness(workflow2));


    }
}
