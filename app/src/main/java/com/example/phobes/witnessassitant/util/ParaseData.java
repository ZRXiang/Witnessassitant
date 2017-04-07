package com.example.phobes.witnessassitant.util;

import android.util.Log;

import com.example.phobes.witnessassitant.model.EntryCheckData;
import com.example.phobes.witnessassitant.model.MixMachine;
import com.example.phobes.witnessassitant.model.MixStation;
import com.example.phobes.witnessassitant.model.MixTaskData;
import com.example.phobes.witnessassitant.model.MixTestData;
import com.example.phobes.witnessassitant.model.OptionPerson;
import com.example.phobes.witnessassitant.model.PointMeta;
import com.example.phobes.witnessassitant.model.ProblemBaseData;
import com.example.phobes.witnessassitant.model.RiskCat;
import com.example.phobes.witnessassitant.model.SiteTestData;
import com.example.phobes.witnessassitant.model.SiteTestItemData;
import com.example.phobes.witnessassitant.model.TransportData;
import com.example.phobes.witnessassitant.model.WatchProgress;
import com.example.phobes.witnessassitant.model.WitenessData;
import com.example.phobes.witnessassitant.model.WitenessDetail;


import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

/**
 * Created by phobes on 2016/6/1.
 */
public class ParaseData {
    //分割每行
    public static String[] spitDataRow(String sourceStr) {
        return sourceStr.split("\\^");
    }

    public static String[] splitDataColumn(String sourceStr) {
        return sourceStr.split("@",25);
    }

    public static WitenessData toWitnessData(String witenessStr) {
        WitenessData witenessData = new WitenessData();
        String[] columns = splitDataColumn(witenessStr);
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

        return witenessData;
    }
    
    public static List<WitenessData> toWitenessDatas(String sourceStr,String type) {
        List<WitenessData> witenessDatas = new ArrayList<WitenessData>();
        String[] rows = spitDataRow(sourceStr);
        for (String a : rows) {
            String[] columns = splitDataColumn(a);
            WitenessData witenessData = new WitenessData();
            try{
                witenessData.setWitness_id(Integer.parseInt(columns[1]));
                witenessData.setSample_id(columns[2]);
                witenessData.setApply_time(columns[3]);
                witenessData.setObject_name(columns[4]);
                witenessData.setObject_id(Integer.parseInt(columns[5]));
                witenessData.setSample_org_name(columns[6]);
                witenessData.setBatch_id(columns[8]);
                if(!columns[7].equals("") && columns[7]!=null) {
                    witenessData.setEntry_id(Integer.parseInt(columns[7]));
                }
            }catch (Exception e){
                e.printStackTrace();
                System.out.print("取样见证，加载异常任务列表："+a);
            }

            if(type.equals("test")){
               // witenessData.setWitnessType(0);
                //试验见证数据加载 witness为2
                witenessData.setWitnessType(2); // yang
            }else{
                //取样见证数据加载 witness为0
                witenessData.setWitnessType(0);
            }
            witenessDatas.add(witenessData);
        }
        return witenessDatas;
    }

    public static List<EntryCheckData> toEntryCheckTasks(String sourceStr) {
//        select entry_id, product_name,  batch_id, quantity, entry_date,object_id
        List<EntryCheckData> witenessDatas = new ArrayList<EntryCheckData>();
        String[] rows = spitDataRow(sourceStr);
        for (String a : rows) {
            try {
                String[] columns = splitDataColumn(a);
                EntryCheckData entryCheckDataTask = new EntryCheckData();
                entryCheckDataTask.setEntryId(Integer.parseInt(columns[0]));
                entryCheckDataTask.setObjectId(Integer.parseInt(columns[5]));
                entryCheckDataTask.setProductName(columns[1]);
                if (!columns[2].equals("")) {
                    entryCheckDataTask.setBatchId(columns[2]);
                }
                entryCheckDataTask.setEntryDate(columns[4]);
                if (!columns[3].equals("")) {
                    entryCheckDataTask.setQuantity(columns[3]);
                }
                entryCheckDataTask.setOutputDate(columns[6]);
                entryCheckDataTask.setStrength(columns[7]);
                entryCheckDataTask.setSampleSpec(columns[8]);
                witenessDatas.add(entryCheckDataTask);
            }catch (Exception e) {
                System.out.println(a);
            }
        }
        return witenessDatas;
    }

    public static EntryCheckData toEntryCheckTaskDetail(String sourceStr) {
        EntryCheckData entryCheckData = new EntryCheckData();
        String[] columns = splitDataColumn(sourceStr);
        //返回顺序 select entry_id, object_id, product_name, batch_id, output_date,quantity, sample_spec, strength, entry_date, report_id,"lab_person,lab_comment, lab_check_date,super_person, super_comment,super_check_date,sample_size,factory
        if(!columns[0].equals(""))entryCheckData.setEntryId(Integer.parseInt(columns[0]));
        if(!columns[1].equals(""))entryCheckData.setObjectId(Integer.parseInt(columns[1]));
        if(!columns[2].equals(""))entryCheckData.setProductName(columns[2]);
        if(!columns[3].equals(""))entryCheckData.setBatchId(columns[3]);
        if(!columns[4].equals(""))entryCheckData.setOutputDate(columns[4]);
        if(!columns[5].equals(""))entryCheckData.setQuantity(columns[5]);
        if(!columns[6].equals(""))entryCheckData.setSampleSpec(columns[6]);
        if(!columns[7].equals(""))entryCheckData.setStrength(columns[7]);
        if(!columns[8].equals(""))entryCheckData.setEntryDate(columns[8]);
        if(!columns[9].equals(""))entryCheckData.setReportId(columns[9]);
        if(!columns[10].equals(""))entryCheckData.setLabPerson(columns[10]);
        if(!columns[11].equals(""))entryCheckData.setLabComment(columns[11]);
        if(!columns[12].equals(""))entryCheckData.setLabCheckDate(columns[12]);
        if(!columns[13].equals(""))entryCheckData.setSuperPerson(columns[13]);
        if(!columns[14].equals(""))entryCheckData.setSuperComment(columns[14]);
        if(!columns[15].equals(""))entryCheckData.setSuperCheckDate(columns[15]);
        if(!columns[16].equals(""))entryCheckData.setSampleSize(columns[16]);
        if(!columns[17].equals(""))entryCheckData.setFactory(columns[17]);
        return entryCheckData;
    }

    public static List<WitenessDetail> DicttoWitenessDetail(String sourceStr) {
        List<WitenessDetail> witenessDetails = new ArrayList<WitenessDetail>();
        String[] rows = spitDataRow(sourceStr);
        for (String a : rows) {
            String[] columns = splitDataColumn(a);

            WitenessDetail witenessDetail = new WitenessDetail();
            witenessDetail.setMeta_id(Integer.parseInt(columns[1]));
            witenessDetail.setMeta_name(columns[2]);
            witenessDetails.add(witenessDetail);
        }
        return witenessDetails;
    }

    public static List<WitenessDetail> toWitenessDetail(String sourceStr) {
        List<WitenessDetail> witenessDetails = new ArrayList<WitenessDetail>();
        String[] rows = spitDataRow(sourceStr);
        for (String a : rows) {
            String[] columns = splitDataColumn(a);

            WitenessDetail witenessDetail = new WitenessDetail();
            witenessDetail.setWitness_id(Integer.parseInt(columns[0]));
            witenessDetail.setMeta_id(Integer.parseInt(columns[1]));
            witenessDetail.setValue(columns[2]);
            witenessDetail.setEdit_time(null);

            witenessDetails.add(witenessDetail);
        }
        return witenessDetails;
    }

    public static void main(String[] args) {
 /*       String result="1101@120001@拟用混凝土强度等级@单选@文本@1031^1101@100001@样品编号@@文本@0^1101@100002@委托单位@@文本@-1^1101@100003@工程名称@@文本@-1^1101@100004@施工部位@@文本@-1^1101@100005@取样地点@@文本@-1^1101@100007@产地厂名@@文本@-1^1101@100009@委托人@@文本@0^1101@100011@取样人@@文本@0^1101@100012@取样日期@@日期@-1^1101@100013@代表数量@@数值@-1^1101@100014@试验项目@多选@文本@1058^1101@100015@样品数量@@数值@0^1101@100016@委托单位负责人@@文本@0^1101@100017@取样见证人@@文本@0^1101@100018@试样描述@@文本@-1^1101@100020@委托单位电话@@文本@0^1101@100021@取样见证人电话@@文本@0^1101@100025@产品批号@@文本@-1^1101@100047@工程标段@@文本@1001^1101@100048@代表数量单位@单选@文本@1111^1101@140147@粒级规格@@文本@0^1101@14014701@粒级规格试样1@单选@文本@1054^1101@14014702@粒级规格试样2@单选@文本@1054^1101@14014703@粒级规格试样3@单选@文本@1054^1101@140148@粒级比例@@数值@-1^1101@120292@强度等级数值或梁体设计强度@@数值@-1^1101@120288@粗骨料种类@单选@文本@1048^1101@120289@粗骨料规格@单选@文本@1054^1101@120291@岩石种类@单选@文本@1055^1101@120294@是否干湿交替或冻融破坏环境@单选@文本@1057^1101@120293@拟用结构类型@单选@文本@1056^1101@120284@出厂日期@@日期@";
        String[] resultarr = spitDataRow(result);
        for (String a:resultarr){
            System.out.println(a);
            String[] columnarr = splitDataColumn(a);
            for(String b:columnarr){
                System.out.println(b);
            }
        }*/
//        String source = "@1@98@20160602-001@2016-06-02 15:10:51@钢筋原材试验@1000@101030101@中心试验室@中铁十四局@Testv7-8080@@@";
        String source = "1@1407@@@1900-01-01 00:00:00@@@@1900-01-01 00:00:00@@@@@@@@";

        String[] columns = splitDataColumn(source);
        EntryCheckData entryCheckData = new EntryCheckData();
        if(!columns[0].equals(""))entryCheckData.setEntryId(Integer.parseInt(columns[0]));
        if(!columns[1].equals(""))entryCheckData.setObjectId(Integer.parseInt(columns[1]));
        if(!columns[2].equals(""))entryCheckData.setProductName(columns[2]);
        if(!columns[3].equals(""))entryCheckData.setBatchId(columns[3]);
        if(!columns[4].equals(""))entryCheckData.setOutputDate(columns[4]);
        if(!columns[5].equals(""))entryCheckData.setQuantity(columns[5]);
        if(!columns[6].equals(""))entryCheckData.setSampleSpec(columns[6]);
        if(!columns[7].equals(""))entryCheckData.setStrength(columns[7]);
        if(!columns[8].equals(""))entryCheckData.setEntryDate(columns[8]);
        if(!columns[9].equals(""))entryCheckData.setReportId(columns[9]);
        if(!columns[10].equals(""))entryCheckData.setLabComment(columns[10]);
        if(!columns[11].equals(""))entryCheckData.setLabCheckDate(columns[11]);
        if(!columns[12].equals(""))entryCheckData.setSuperComment(columns[12]);
        if(!columns[13].equals(""))entryCheckData.setSuperCheckDate(columns[13]);
    }

    //现场检测
    public static List<SiteTestData> toSiteDataTasks(String sourceStr) {
        List<SiteTestData> SiteTestDatas = new ArrayList<SiteTestData>();
        String[] rows = spitDataRow(sourceStr);
        for (String a : rows) {
            try {
                String[] columns = splitDataColumn(a);
                SiteTestData siteTestData = new SiteTestData();
                if(!columns[0].equals(""))siteTestData.setDataId(Integer.parseInt(columns[0]));
                if(!columns[1].equals(""))siteTestData.setIndexId(Integer.parseInt(columns[1]));
                if(!columns[2].equals(""))siteTestData.setOrgId(columns[2]);
                if(!columns[3].equals(""))siteTestData.setOrderId(columns[3]);
                if(!columns[4].equals(""))siteTestData.setObjectId(Integer.parseInt(columns[4]));
                if(!columns[5].equals(""))siteTestData.setMetaId(Integer.parseInt(columns[5]));
                if(!columns[6].equals(""))siteTestData.setMetaName(columns[6]);
                if(!columns[7].equals(""))siteTestData.setTestName(columns[7]);
                if(!columns[8].equals(""))siteTestData.setSampleId(columns[8]);
                if(!columns[9].equals(""))siteTestData.setTestCount(Integer.parseInt(columns[9]));
                if(!columns[10].equals(""))siteTestData.setStartNo(Integer.parseInt(columns[10]));
                if(!columns[11].equals(""))siteTestData.setSampleSpec(columns[11]);
                if(!columns[12].equals(""))siteTestData.setSize(columns[12]);
                if(!columns[13].equals(""))siteTestData.setOriginalGauge(Float.parseFloat(columns[13]));
                if(!columns[14].equals(""))siteTestData.setProductDate(columns[14]);
                if(!columns[15].equals(""))siteTestData.setExpectedDate(columns[15]);
                if(!columns[16].equals(""))siteTestData.setAge(Integer.parseInt(columns[16]));
                if(!columns[17].equals(""))siteTestData.setDownloadDate(columns[17]);
                if(!columns[18].equals(""))siteTestData.setTestDate(columns[18]);
                if(!columns[19].equals(""))siteTestData.setTester(columns[19]);
                if(!columns[20].equals(""))siteTestData.setTransferTime(columns[20]);
                if(!columns[21].equals(""))siteTestData.setDownloadCount(Integer.parseInt(columns[21]));
                if(!columns[22].equals(""))siteTestData.setTestCategory(Integer.parseInt(columns[22]));
                if(!columns[23].equals(""))siteTestData.setGroups(Integer.parseInt(columns[23]));
                if(!columns[24].equals(""))siteTestData.setOrderDate(columns[24]);
                SiteTestDatas.add(siteTestData);
            }catch (Exception e) {
                System.out.println(a);
            }
        }
        return SiteTestDatas;
    }

    public static List<SiteTestItemData> toSiteTestItemData(String sourceStr) {
        List<SiteTestItemData> siteTestItemDatas = new ArrayList<SiteTestItemData>();
        String[] rows = spitDataRow(sourceStr);
        for (String a : rows) {
            try {
                String[] columns = splitDataColumn(a);
                SiteTestItemData siteTestItemData = new SiteTestItemData();
                if(!columns[0].equals(""))siteTestItemData.setObjectId(Integer.parseInt(columns[0]));
                if(!columns[1].equals(""))siteTestItemData.setMetaId(Integer.parseInt(columns[1]));
                if(!columns[2].equals(""))siteTestItemData.setMetaName(columns[2].toString());
                if(!columns[3].equals(""))siteTestItemData.setMetaType(Integer.parseInt(columns[3]));

                siteTestItemDatas.add(siteTestItemData);
            }catch (Exception e) {
                System.out.println(a);
            }
        }
        return siteTestItemDatas;
    }

    //拌合站
    public static List<MixTaskData> toMixTask(String sourceStr) {
        List<MixTaskData> mixTaskDatas = new ArrayList<MixTaskData>();
        String[] rows = spitDataRow(sourceStr);
        for (String a : rows) {
            try {
            //  select org_id,task_id,prj_name,position,strength,volume,station_name,begin_time,applicant,application_time,state,slump_set,destination from m_p_task ORDER BY application_time DESC
                String[] columns = splitDataColumn(a);
                MixTaskData mixTaskData = new MixTaskData();
                mixTaskData.setOrgId(columns[0]);
                mixTaskData.setTaskId(columns[1]);
                mixTaskData.setProjectName(columns[2]);
                mixTaskData.setPosition(columns[3]);
                mixTaskData.setStrength(columns[4]);
                mixTaskData.setVolume(Float.parseFloat(columns[5]));
                mixTaskData.setStationName(columns[6]);
                mixTaskData.setBeginTime(columns[7]);
                mixTaskData.setApplicant(columns[8]);
                mixTaskData.setApplicationTime(columns[9]);
                mixTaskData.setState(Integer.parseInt(columns[10]));
                mixTaskData.setSlump(columns[11]);
                mixTaskData.setDestination(columns[12]);
                mixTaskDatas.add(mixTaskData);
            }catch (Exception e) {
                System.out.println(a);
            }
        }
        return mixTaskDatas;
    }

    public static List<WatchProgress> toWatchProgress(String sourceStr) {
        List<WatchProgress> watchProgressList = new ArrayList<WatchProgress>();
        String[] rows = spitDataRow(sourceStr);
        for (String a : rows) {
            try {
                String[] columns = splitDataColumn(a);
                WatchProgress watchProgress = new WatchProgress();
                watchProgress.setProgressState(columns[0]);
                watchProgressList.add(watchProgress);
            }catch (Exception e) {
                System.out.println(a);
            }
        }
        return watchProgressList;
    }

    public static List<MixStation> toMixStation(String sourceStr) {
        List<MixStation> mixStationList = new ArrayList<MixStation>();
        String[] rows = spitDataRow(sourceStr);
        for (String a : rows) {
            try {
                String[] columns = splitDataColumn(a);
                MixStation mixStation = new MixStation();
                mixStation.setOrgId(columns[0]);
                mixStation.setMixStation(columns[1]);
                mixStationList.add(mixStation);
            }catch (Exception e) {
                System.out.println(a);
            }
        }
        return mixStationList;
    }

    public static List<MixMachine> toMixMachine(String sourceStr) {
        List<MixMachine> mixMachineList = new ArrayList<MixMachine>();
        String[] rows = spitDataRow(sourceStr);
        for (String a : rows) {
            try {
                String[] columns = splitDataColumn(a);
                MixMachine mixMachine = new MixMachine();
                mixMachine.setDeviceId(columns[0]);
                mixMachine.setDeviceName(columns[1]);
                mixMachineList.add(mixMachine);
            }catch (Exception e) {
                System.out.println(a);
            }
        }
        return mixMachineList;
    }

    public static List<OptionPerson> toOptionPerson(String sourceStr) {
        List<OptionPerson> optionPersonList = new ArrayList<OptionPerson>();
        String[] rows = spitDataRow(sourceStr);
        for (String a : rows) {
            try {
                String[] columns = splitDataColumn(a);
                OptionPerson optionPerson = new OptionPerson();
                optionPerson.setName(columns[0]);
                optionPersonList.add(optionPerson);
            }catch (Exception e) {
                System.out.println(a);
            }
        }
        return optionPersonList;
    }

    public static TransportData transportInfData(String sourceStr){
        String[] columns=splitDataColumn(sourceStr);
        TransportData transportInf=new TransportData();
        transportInf.setProjectName(columns[0]);
        transportInf.setDepartTime(columns[1]);
        transportInf.setDevice(columns[2]);
        transportInf.setNoticeId(columns[3]);
        transportInf.setPosition(columns[4]);
        transportInf.setSlump(columns[5]);
        transportInf.setStrength(columns[6]);
        transportInf.setTruckId(columns[7]);
        transportInf.setPlanVolume(columns[8]);
        transportInf.setThisVolume(columns[9]);
        transportInf.setAddCar(columns[10]);
        transportInf.setAddVolume(columns[11]);
       transportInf.setMemo(columns[12]);
        transportInf.setDepartPerson(columns[13]);
        transportInf.setDriver(columns[14]);
        return transportInf;
    }

    public static List<ProblemBaseData> toProblemBaseData(String sourceStr) {
        List<ProblemBaseData> problemBaseDataList = new ArrayList<ProblemBaseData>();
        String[] rows = spitDataRow(sourceStr);
        for (String a : rows) {
            try {
                String[] columns = splitDataColumn(a);
                ProblemBaseData problemBaseData = new ProblemBaseData();
                problemBaseData.setCheckDate(columns[0]);
                problemBaseData.setLab(columns[1]);
                problemBaseData.setTender(columns[2]);
                problemBaseData.setRiskSoure(columns[3]);
                problemBaseData.setRisks(columns[4]);
                problemBaseData.setRiskCatName(columns[5]);
                problemBaseData.setCredit(columns[6]);
                problemBaseData.setChargeDepartment(columns[7]);
                problemBaseData.setChargePerson(columns[8]);
                problemBaseData.setImproveDate(columns[9]);
                problemBaseData.setImproveMethod(columns[10]);
                problemBaseData.setDeadLine(columns[11]);
                problemBaseData.setImprovement(columns[12]);
                problemBaseData.setSuperDepartment(columns[13]);
                problemBaseData.setSupervisor(columns[14]);
                problemBaseData.setInspectResult(columns[15]);
                problemBaseData.setMemo(columns[16]);

                problemBaseDataList.add(problemBaseData);
            }catch (Exception e) {
                System.out.println(a);
            }
        }
        return problemBaseDataList;
    }

    public static List<RiskCat> toRiskCat(String sourceStr) {
        List<RiskCat> riskCatList = new ArrayList<RiskCat>();
        RiskCat rc = new RiskCat();
        rc.setRiskCatId(0);
        rc.setRiskCatName("全部分类");
        riskCatList.add(rc);
        String[] rows = spitDataRow(sourceStr);
        for (String a : rows) {
            try {
                String[] columns = splitDataColumn(a);
                RiskCat riskCat = new RiskCat();
                riskCat.setRiskCatId(Integer.parseInt(columns[0]));
                riskCat.setRiskCatName(columns[1]);
                riskCatList.add(riskCat);
            }catch (Exception e) {
                System.out.println(a);
            }
        }
        return riskCatList;
    }

    //分割测点meta
    public static List<PointMeta> toPointMeta(String sourceStr) {
        List<PointMeta> pointMetaList = new ArrayList<PointMeta>();
        String[] rows = spitDataRow(sourceStr);
        for (String a : rows) {
            try {
                String[] columns = splitDataColumn(a);
                PointMeta pointMeta = new PointMeta();
                pointMeta.setDataId(Integer.parseInt(columns[0]));
                pointMeta.setMetaId(Integer.parseInt(columns[1]));
                pointMeta.setMetaValue(columns[2]);
                pointMetaList.add(pointMeta);
            }catch (Exception e) {
                System.out.println(a);
            }
        }
        return pointMetaList;
    }
}
