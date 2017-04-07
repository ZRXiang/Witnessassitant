package com.example.phobes.witnessassitant.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by phobes on 2016/6/1.
 */
public class TestObject {
    /**
     * An array of sample (guild) items.
     */
    public static final List<TestObjectItem> ITEMS = new ArrayList<TestObjectItem>();

    /**
     * A map of sample (guild) items, by ID.
     */
    public static final Map<String, TestObjectItem> ITEM_MAP = new HashMap<String, TestObjectItem>();

    static {
        ITEMS.add(new TestObjectItem(10,	"钢筋及金属材料",		0,"11111"));
        ITEMS.add(new TestObjectItem(11,	"原材料",		0,"11111"));
        ITEMS.add(new TestObjectItem(12,	"混凝土",		0,"11111"));
        ITEMS.add(new TestObjectItem(13,	"土工",		0,"11111"));
        ITEMS.add(new TestObjectItem(14,	"其他试验",		0,"11111"));
        ITEMS.add(new TestObjectItem(15,	"现场检测",	0,"11111"));
        ITEMS.add(new TestObjectItem(1000,	"钢筋原材试验	",	10,"11111"));
        ITEMS.add(new TestObjectItem(1001,	"钢筋焊接接头试验	",	10,"11111"));
        ITEMS.add(new TestObjectItem(1002,	"钢筋机械连接接头试验 	",	10,"11111"));
        ITEMS.add(new TestObjectItem(1003,	"金属材料机械性能试验	",	10,"11111"));
        ITEMS.add(new TestObjectItem(1004,	"预应力混凝土用钢绞线试验	",	10,"11111"));
        ITEMS.add(new TestObjectItem(1006,	"锚杆抗拔试验	",	10,"11111"));
        ITEMS.add(new TestObjectItem(1005,	"预应力混凝土用钢丝试验	",	10,"11111"));
        ITEMS.add(new TestObjectItem(1007,	"金属洛氏硬度试验	",	10,"11111"));
        ITEMS.add(new TestObjectItem(1008,	"锚具锚固拉力极限总应变试验	",	10,"11111"));
        ITEMS.add(new TestObjectItem(1009,	"钢材化学分析试验",	10,"11111"));
        ITEMS.add(new TestObjectItem(1010,	"金相试验",	10,"11111"));
        ITEMS.add(new TestObjectItem(1100,	"细骨料	"	,11,"11111"));
        ITEMS.add(new TestObjectItem(1101,	"粗骨料	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1102,	"混凝土用粉煤灰试验	"	,11,"11111"));
        ITEMS.add(new TestObjectItem(1103,	"水泥试验	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1104,	"混凝土用磨细矿渣粉试验	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1105,	"减水剂试验(2009)	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1106,	"膨胀剂试验	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1107,	"引气剂试验	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1108,	"粉体速凝剂试验	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1109,	"液体速凝剂试验	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1110,	"混凝土外加剂匀质性试验	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1111,	"水质分析试验	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1112,	"石灰试验	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1113,	"粗骨料试验(2009)	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1114,	"支座灌浆砂浆试验(2009)	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1115,	"混凝土外加剂试验(2009)	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1117,	"支座自流平砂浆试验	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1118,	"岩石试验（一）	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1119,	"岩石试验（二）	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1120,	"梁体管道压浆剂试验	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1121,	"锚杆锚固剂试验	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1122,	"轻骨料试验	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1123,	"烧结普通砖试验	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1124,	"混凝土用骨料碱活性试验	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1125,	"混凝土外加剂试验	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1126,	"吸水式锚固包试验	",11,"11111"));
        ITEMS.add(new TestObjectItem(1127,	"沥青胶结材料试验	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1128,	"乳化沥青试验	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1129,	"水泥乳化沥青砂浆用干料试验",	11,"11111"));
        ITEMS.add(new TestObjectItem(1130,	"沥青试验	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1131,	"改性沥青试验	",	11,"11111"));
        ITEMS.add(new TestObjectItem(1132,	"点荷载试验",	11,"11111"));
        ITEMS.add(new TestObjectItem(1133,	"岩石薄片鉴定试验",	11,"11111"));
        ITEMS.add(new TestObjectItem(1200,	"混凝土抗压试验	",	12,"11111"));
        ITEMS.add(new TestObjectItem(1201,	"混凝土抗折强度试验	",	12,"11111"));
        ITEMS.add(new TestObjectItem(1202,	"混凝土抗渗试验	",	12,"11111"));
        ITEMS.add(new TestObjectItem(1203,	"混凝土静力受压弹性模量试验	",	12,"11111"));
        ITEMS.add(new TestObjectItem(1204,	"混凝土抗冻性能试验	",	12,"11111"));
        ITEMS.add(new TestObjectItem(1206,	"钻芯法混凝土强度试验	",	12,"11111"));
        ITEMS.add(new TestObjectItem(1205,	"混凝土电通量快速测定试验	",	12,"11111"));
        ITEMS.add(new TestObjectItem(1207,	"回弹法混凝土强度试验	"	,12,"11111"));
        ITEMS.add(new TestObjectItem(1208,	"混凝土抗裂性试验	",	12,"11111"));
        ITEMS.add(new TestObjectItem(1209,	"混凝土劈裂抗拉强度试验	",	12,"11111"));
        ITEMS.add(new TestObjectItem(1210,	"混凝土收缩试验	",	12,"11111"));
        ITEMS.add(new TestObjectItem(1213,	"混凝土结构实体钢筋保护层厚度检测	",	12,"11111"));
        ITEMS.add(new TestObjectItem(1214,	"梁体管道压浆检查试件强度试验	"	,12,"11111"));
        ITEMS.add(new TestObjectItem(1215,	"砂浆检查件抗压强度	"	,12,"11111"));
        ITEMS.add(new TestObjectItem(1216,	"硫磺锚固检测试验	"	,12,"11111"));
        ITEMS.add(new TestObjectItem(1217,	"硫磺砂浆配合比试验	"	,12,"11111"));
        ITEMS.add(new TestObjectItem(1218,	"后拔出法测定混凝土强度试验	"	,12,"11111"));
        ITEMS.add(new TestObjectItem(1219,	"预应力混凝土铁路桥简支梁静载弯曲试验	"	,12,"11111"));
        ITEMS.add(new TestObjectItem(1220,	"超声回弹测混凝土强度检测	",	12,"11111"));
        ITEMS.add(new TestObjectItem(1221,	"混凝土强度检验评定试验",	12,"11111"));
        ITEMS.add(new TestObjectItem(1222,	"混凝土配合比选定",	12,"11111"));
        ITEMS.add(new TestObjectItem(1223,	"砂浆配合比选定"	,12,"11111"));
        ITEMS.add(new TestObjectItem(1300,	"灰剂量及曲线试验	"	,13,"11111"));
        ITEMS.add(new TestObjectItem(1301,	"改良土无侧限抗压强度试验	"	,13,"11111"));
        ITEMS.add(new TestObjectItem(1302,	"级配碎石试验(2009)	"	,13,"11111"));
        ITEMS.add(new TestObjectItem(1303,	"土工试验报告二	"	,13,"11111"));
        ITEMS.add(new TestObjectItem(1304,	"填料压实质量试验-K30	"	,13,"11111"));
        ITEMS.add(new TestObjectItem(1305,	"填料压实质量试验-EVD	"	,13,"11111"));
        ITEMS.add(new TestObjectItem(1306,	"填料压实质量试验-灌砂法	"	,13,"11111"));
        ITEMS.add(new TestObjectItem(1307,	"标准击实试验(2009)	"	,13,"11111"));
        ITEMS.add(new TestObjectItem(1308,	"液塑限联合测定试验(2009)	",	13,"11111"));
        ITEMS.add(new TestObjectItem(1309,"	基床表层级配碎石试验	"	,13,"11111"));
        ITEMS.add(new TestObjectItem(1310,	"过渡段用级配碎石试验	"	,13,"11111"));
        ITEMS.add(new TestObjectItem(1311,	"土工化学分析综合试验	"	,13,"11111"));
        ITEMS.add(new TestObjectItem(1312,	"填料压实质量试验-灌水法	"	,13,"11111"));
        ITEMS.add(new TestObjectItem(1313,	"填料压实质量试验-环刀法	"	,13,"11111"));
        ITEMS.add(new TestObjectItem(1314,	"碎石道砟试验（一）	"	,13,"11111"));
        ITEMS.add(new TestObjectItem(1315,	"碎石道砟试验（二）	"	,13,"11111"));
        ITEMS.add(new TestObjectItem(1316,	"十字板剪切试验	"	,13,"11111"));
        ITEMS.add(new TestObjectItem(1317,	"承载比试验	"	,13,"11111"));
        ITEMS.add(new TestObjectItem(1318,	"基床表层级配砂砾试验	"	,13,"11111"));
        ITEMS.add(new TestObjectItem(1319,	"击实试验(开发中)",	13,"11111"));
        ITEMS.add(new TestObjectItem(1401,	"动力触探试验	"	,14,"11111"));
        ITEMS.add(new TestObjectItem(1402,	"静力触探试验	"	,14,"11111"));
        ITEMS.add(new TestObjectItem(1403,	"防水涂料试验	"	,14,"11111"));
        ITEMS.add(new TestObjectItem(1404,	"外委检测报告	"	,14,"11111"));
        ITEMS.add(new TestObjectItem(1405,"	软式透水管试验	"	,14,"11111"));
        ITEMS.add(new TestObjectItem(1407,	"防水材料试验	"	,14,"11111"));
        ITEMS.add(new TestObjectItem(1408,	"止水带试验	"	,14,"11111"));
        ITEMS.add(new TestObjectItem(1409,	"混凝土施工配料通知单"	,14,"101111"));
        ITEMS.add(new TestObjectItem(1410,	"砂浆施工配料通知单"	,14,"101111"));
        ITEMS.add(new TestObjectItem(1411,	"混凝土首盘鉴定记录表"	,14,"101111"));
        ITEMS.add(new TestObjectItem(1412,	"混凝土浇筑申请"	,14,"101111"));
        ITEMS.add(new TestObjectItem(1413,	"混凝土配合比调整单1"	,14,"00101"));
        ITEMS.add(new TestObjectItem(1414,	"混凝土配合比调整单2"	,14,"001111"));
        ITEMS.add(new TestObjectItem(1500,	"挡墙探地试验"	,15,"10001"));
        ITEMS.add(new TestObjectItem(1501,	"路基挡墙试验检测"	,15,"10001"));
        ITEMS.add(new TestObjectItem(1502,	"动态变形模量,基地系数试验"	,15,"10001"));
        ITEMS.add(new TestObjectItem(1503,	"岩溶路基注浆效果检测试验"	,15,"10001"));
        ITEMS.add(new TestObjectItem(1504,	"静载记录"	,15,"10001"));
        ITEMS.add(new TestObjectItem(1505,	"基桩完整性试验检测"	,15,"10001"));
        ITEMS.add(new TestObjectItem(1506,	"混凝土碳化试验检验"	,15,"10001"));
        ITEMS.add(new TestObjectItem(1507,	"基地承载力试验性能检验"	,15,"10001"));
        ITEMS.add(new TestObjectItem(1508,	"基桩承载力试验"	,15,"10001"));
        ITEMS.add(new TestObjectItem(1509,	"（支护）厚度及背后空洞试验检测 "	,15,"10001"));
        ITEMS.add(new TestObjectItem(1510,	"隧道基底岩溶地质探查原始记录表（地质雷达）",	15,"10001"));
        ITEMS.add(new TestObjectItem(1511,	"锚杆拉拔试验"	,15,"10001"));
    }
    public static void addItem(TestObjectItem item) {
        ITEMS.add(item);
    }


    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }
    public static class TestObjectItem {
        public final int object_id;
        public final String name;
        public final int group_id;
        public final String workflow;
        public TestObjectItem(int object_id, String name, int group_id,String workflow) {

            this.object_id = object_id;
            this.name = name;
            this.group_id = group_id;
            this.workflow = workflow;//采样，试验，
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
