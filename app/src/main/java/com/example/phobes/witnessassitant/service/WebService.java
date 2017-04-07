package com.example.phobes.witnessassitant.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.EntryCheckData;
import com.example.phobes.witnessassitant.model.SendTestValue;
import com.example.phobes.witnessassitant.model.WitenessData;
import com.example.phobes.witnessassitant.util.Base64Util;
import com.example.phobes.witnessassitant.util.Configuration;
import com.example.phobes.witnessassitant.util.SOAPUtils;
import com.example.phobes.witnessassitant.util.SessionKeyUtil;
import com.example.phobes.witnessassitant.util.StreamUtils;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by phobes on 2016/6/8.
 */
public class WebService extends Service {
    private final Context mContext;
    private String result = null;
    private final String TABLE_NAME_WITNESS = "WitnessData";
    private final String TABLE_NAME_WITNESSTASK = "WitnessTask";
    private final String TABLE_NAME_WITNESS_DETAIL = "WitnessDetail";
    private final String TABLE_NAME_CONCRETE_SAMPLE = "ConcreteSample";
    private final String TABLE_NAME_ENTRY_CHECK = "EntryCheckData";
    private final String TABLE_NAME_TEST_TASK = "TestTask";
    private final String TABLE_NAME_SITE_TEST_ITEM = "site_test_item";
    private final String NOT_EXIST = "NOT_EXIST";
    private final String UPLOADED = "UPLOADED";
    private String address = "";
    private String mixStationName = "";

    private char aSourceCharList[] = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private char aTargetCharList[] = {'z', 'y', 'x', 'w', 'v', 'u', 't', 's', 'r', 'q', 'p', 'o', 'n', 'm', 'l', 'k', 'j', 'i', 'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a', 'Z', 'Y', 'X', 'W', 'V', 'U', 'T', 'S', 'R', 'Q', 'P', 'O', 'N', 'M', 'L', 'K', 'J', 'I', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};

    public WebService(Context context) {
        this.mContext = context;
    }

    private int BiSearch(char cValue) {
        int nStart, nEnd, nMiddle;               //瀹氫箟鏁扮粍寮€濮嬩笅鏍?鍜岀粨鏉熶笅鏍?    涓棿涓嬫爣
        nStart = 0;
        nEnd = 51;
        while (nStart <= nEnd) {
            nMiddle = (nStart + nEnd) / 2;
            if (aSourceCharList[nMiddle] == cValue)    //濡傛灉涓棿浣嶇疆鏁颁负璇ユ暟鍒欒繑鍥炶鏁颁綅缃紝閫€鍑哄惊鐜?
                return nMiddle;
            else if (aSourceCharList[nMiddle] > cValue)
                nEnd = nMiddle - 1;                         //涓棿浣嶇疆鏁板ぇ浜庤鏁帮紝鍒欏幓鎺夊悗涓€瀛愯〃 缁х画鏌ヨ
            else if (aSourceCharList[nMiddle] < cValue)
                nStart = nMiddle + 1;                     //涓棿浣嶇疆鏁板皬浜庤鏁帮紝鍒欏幓鎺夊墠涓€瀛愯〃  缁х画鏌ヨ
        }
        return -1;
    }

    private String EncryptSQL(String sSQL) {
        int I, nPos;
        String sSQLEx = "";
        if (CommData.bEncryptSQL) {
            for (I = 0; I < sSQL.length(); I++) {
                nPos = BiSearch(sSQL.charAt(I));
                if (nPos >= 0)
                    sSQLEx = sSQLEx + aTargetCharList[nPos];
                else
                    sSQLEx = sSQLEx + sSQL.charAt(I);
            }
            sSQLEx = sSQLEx.replaceAll("&OG;", "&lt;");
            sSQLEx = sSQLEx.replaceAll("&TG;", "&gt;");

            return sSQLEx;
        } else
            return sSQL;
    }

    public String uploadWitnessTask(WitenessData witenessData){
        String sql = "INSERT INTO " + TABLE_NAME_WITNESSTASK + "(witness_id,test_image,test_comment,test_latitude,test_longitude,test_time) VALUES(";
        sql+= witenessData.getWitness_id() +",'" + witenessData.getTest_image()+ "','"+witenessData.getTest_comment()+"',";
        sql+= witenessData.getTest_latitude()+","+witenessData.getTest_longitude()+",to_date('" + witenessData.getTest_time() + "','yyyy-mm-dd hh24:mi:ss'))";

        try {
            result = basicService("ExecuteSQL", getRequestBodyEx(sql));
            return result;
        } catch (Exception e) {
            LogWriter.log(CommData.ERROR, "取样见证,sql语句" + sql + " 异常：" + e);
            e.printStackTrace();
            return "";
        }
    }

    public String uploadWitness(WitenessData witenessData, String type) throws Exception {
        String sql = "update " + TABLE_NAME_WITNESS + " set ";

        if ("监理试验室".equals(CommData.orgType)) {

            if (type.equals("sample")) {
                LogWriter.log(CommData.INFO, "监理，取样见证，任务列表数据查询");
                sql = sql + "witness_image = '" + witenessData.getWitness_image() +
                        "', witness_longitude=" + witenessData.getWitness_longitude() +
                        ",witness_latitude=" + witenessData.getWitness_latitude() +
                        ",witness_person='" + CommData.username + "'";
                if (CommData.DBType == 1) {
                    sql += ",witness_time=to_date('" + witenessData.getWitness_time() + "','yyyy-mm-dd hh24:mi:ss')" +//orcle
                            ",\"COMMENT\"='" + witenessData.getComment() + "'";//oracle
                } else if (CommData.DBType == 2) {
                    sql += ",witness_time= '" + witenessData.getWitness_time() + "'" +//sqlServer
                            ",comment='" + witenessData.getComment() + "'"; //sqlServer
                }
            }
            if (type.equals("test")) {
                LogWriter.log(CommData.INFO, "监理，试验见证，任务列表数据查询");
                sql = sql + "test_image = '" + witenessData.getTest_image() +
                        "',test_latitude=" + witenessData.getTest_latitude() +
                        ",test_longitude=" + witenessData.getTest_longitude() +
                        ",test_comment='" + witenessData.getTest_comment() + "'";
                if (CommData.DBType == 1) {
                    sql += ",test_time=to_date('" + witenessData.getTest_time() + "','yyyy-mm-dd hh24:mi:ss')"; //oracle
                } else if (CommData.DBType == 2) {
                    sql += ",test_time='" + witenessData.getTest_time() + "'"; //sqlServer
                }
            }
            sql = sql + " where witness_id = " + witenessData.getWitness_id() + ";";
        } else {
            LogWriter.log(CommData.INFO, "实验员，取样见证，任务列表数据查询");
            sql = sql + " sample_image = '" + witenessData.getSample_image() +
                    "', sample_longitude=" + witenessData.getSample_longitude() +
                    " ,sample_latitude=" + witenessData.getSample_latitude() +
                    " ,sample_person='" + CommData.username + "'";
            if (CommData.DBType == 1) {
                sql += " ,sample_time=to_date('" + witenessData.getSample_time() + "','yyyy-mm-dd hh24:mi:ss')"; //oracle
            } else if (CommData.DBType == 2) {
                sql += " ,sample_time='" + witenessData.getSample_time() + "'";//sqlServer
            }

            sql += "  where witness_id = " + witenessData.getWitness_id();
        }
        try {
            if (!type.equals("test")) {
                result = basicService("ExecuteSQL", getRequestBodyEx(sql));
                return result;
            }else{
                return "true";
            }

        } catch (Exception e) {
            LogWriter.log(CommData.ERROR, "取样见证,sql语句" + sql + " 异常：" + e);
            e.printStackTrace();
            return "";
        }
    }

    public String login(String sUsername, String sUserPassword) throws Exception {
        getLoginRequestBody(sUsername, sUserPassword);
        return basicService("Login", getLoginRequestBody(sUsername, sUserPassword));
    }

    //获取职务
    public String getDuty(String duty) throws Exception {
        //   LogWriter.open(CommData.filePath).print(CommData.DEBUG);
        String sql;
        sql = "select duty from Personnel where user_name='" + duty + "'";
        return basicService("SQLDBEx", getRequestBody(sql));
    }


    public String loadApplyList(String type, int nPage) throws Exception {  //查询见证列表数据
        String sql = null;
        if (CommData.orgType.equals("监理试验室")) {
            if (type.equals("sample")) {
                sql = "SELECT a.apply_from,a.witness_id,a.Sample_id,a.apply_time,b.object_name,a.object_id,d.org_name||'-'||c.org_name as org_name,a.entry_id,i.batch_id ";

                sql = sql + " FROM  WitnessData a LEFT JOIN TestObject b ON a.object_id= b.object_id INNER JOIN Organization c on a.sample_org_id=c.org_id "
                        + " LEFT JOIN Organization d ON d.org_id= c.parent_id  "
                        + " LEFT JOIN EntryCheckData i on i.entry_id=a.entry_id  "//yang 2016/10/18
                        + " where  a.witness_org_id = '" + CommData.sLabId + "'"//(a.witness_person ='" + CommData.sUserId + "' OR
                        + " and (a.witness_time IS NULL) and (i.super_comment='验收合格'  or i.super_comment IS NULL) "//
                        + " ORDER BY a.apply_time DESC";
            } else {
                sql = "SELECT a.apply_from,a.witness_id,a.Sample_id,a.apply_time,b.object_name,a.object_id,d.org_name||'-'||c.org_name as org_name,a.entry_id,i.batch_id ";
                sql = sql + " FROM WitnessData a LEFT JOIN TestObject b ON a.object_id= b.object_id INNER JOIN Organization c on a.sample_org_id=c.org_id "
                        + " LEFT JOIN Organization d ON d.org_id= c.parent_id  "
                        + " LEFT JOIN EntryCheckData i on i.entry_id=a.entry_id LEFT JOIN TestData j on a.data_id=j.data_id "//yang 2016/10/20
                        + " where a.witness_org_id = '" + CommData.sLabId + "' and ( a.witness_time IS NOT NULL and a.test_time IS NULL) "
                        + "  and a.data_id IS NOT NULL "
                        + " ORDER BY a.apply_time DESC";//
            }
        } else {
            sql = "SELECT a.apply_from,a.witness_id,a.Sample_id,a.apply_time,b.object_name,a.object_id,c.org_name,d.org_name  parent_name,a.sample_person  name,f.org_name  w_org_name,g.org_name  w_parent_name,a.witness_person  w_name,a.entry_id,i.batch_id  ";
            sql += " FROM WitnessData a LEFT JOIN TestObject b ON a.object_id= b.object_id INNER JOIN Organization c on a.sample_org_id=c.org_id LEFT JOIN Organization d on c.parent_id=d.org_id  " +
                    " LEFT JOIN Organization f on a.witness_org_id=f.org_id LEFT JOIN Organization g on f.parent_id=g.org_id  LEFT JOIN EntryCheckData i on i.entry_id=a.entry_id " +
                    " WHERE a.sample_org_id='" + CommData.sLabId + "' AND  (a.sample_time IS NULL) and (i.super_comment='验收合格' or i.super_comment IS NULL) and a.witness_org_id IS NOT NULL ORDER BY a.apply_time DESC";
        }
        if (CommData.DBType == 1)
            sql = "select * from (select a.*,ROWNUM rn from (" + sql + ") a ) t where t.rn &gt; " + (nPage - 1) * 10 + " and " + " t.rn &lt; " + ((nPage) * 10+1);
        else
            sql = "select * from (select a.*,Row_number() over (order by entry_date desc) as rn from (" + sql + ") a ) t where t.rn &gt; " + (nPage - 1) * 10 + " and " + " t.rn &lt; " + ((nPage) * 10+1);

        return basicService("SQLDBEx", getRequestBody(sql));
    }

    public String changeApplyList(String type, int groupId, int nPage) throws Exception {//yang 2016-12-23
        String sql = null;
        if (CommData.orgType.equals("监理试验室")) {
            if (type.equals("sample")) {
                sql = "SELECT a.apply_from,a.witness_id,a.Sample_id,a.apply_time,b.object_name,a.object_id,d.org_name||'-'||c.org_name as org_name,a.entry_id,i.batch_id ";
                sql = sql + " FROM WitnessData a LEFT JOIN TestObject b ON a.object_id= b.object_id INNER JOIN Organization c on a.sample_org_id=c.org_id  "
                        + " LEFT JOIN Organization d ON d.org_id= c.parent_id  "
                        + " LEFT JOIN EntryCheckData i on i.entry_id=a.entry_id  "//yang 2016/10/18
                        + " where a.witness_org_id = '" + CommData.sLabId + "'"
                        + " and (a.witness_time IS NULL) and (i.super_comment='验收合格'  or i.super_comment IS NULL) and a.object_id LIKE '" + groupId + "%'"
                        + " ORDER BY a.apply_time DESC";
            } else {
                sql = "SELECT a.apply_from,a.witness_id,a.Sample_id,a.apply_time,b.object_name,a.object_id,d.org_name||'-'||c.org_name as org_name,a.entry_id,i.batch_id ";
                sql = sql + " FROM WitnessData a LEFT JOIN TestObject b ON a.object_id= b.object_id INNER JOIN Organization c on a.sample_org_id=c.org_id "
                        + " LEFT JOIN Organization d ON d.org_id= c.parent_id  "
                        + "  LEFT JOIN EntryCheckData i on i.entry_id=a.entry_id LEFT JOIN TestData j on a.data_id=j.data_id "//yang 2016/10/20
                        + " where  a.witness_org_id = '" + CommData.sLabId + "' and ( a.witness_time IS NOT NULL and a.test_time IS NULL) "
                        + " and a.data_id IS NOT NULL and a.object_id LIKE '" + groupId + "%' "
                        + " ORDER BY a.apply_time DESC";
            }
        } else {
            sql = "SELECT a.apply_from,a.witness_id,a.Sample_id,a.apply_time,b.object_name,a.object_id,c.org_name,a.entry_id,i.batch_id ";
            sql += " FROM WitnessData a LEFT JOIN TestObject b ON a.object_id= b.object_id INNER JOIN Organization c on a.sample_org_id=c.org_id LEFT JOIN Organization d on c.parent_id=d.org_id  " +
                    " LEFT JOIN Organization f on a.witness_org_id=f.org_id LEFT JOIN Organization g on f.parent_id=g.org_id  LEFT JOIN EntryCheckData i on i.entry_id=a.entry_id " +
                    " WHERE a.sample_org_id='" + CommData.sLabId + "' AND (a.sample_time IS NULL) and (i.super_comment='验收合格' or i.super_comment IS NULL) and a.object_id LIKE '" + groupId + "%' ORDER BY a.apply_time DESC";
        }
        if (CommData.DBType == 1)
            sql = "select * from (select a.*,ROWNUM rn from (" + sql + ") a ) t where t.rn &gt; " + (nPage - 1) * 10 + " and " + " t.rn &lt; " + ((nPage) * 10+1);
        else
            sql = "select * from (select a.*,Row_number() over (order by entry_date desc) as rn from (" + sql + ") a ) t where t.rn &gt; " + (nPage - 1) * 10 + " and " + " t.rn &lt; " + ((nPage) * 10+1);

        return basicService("SQLDBEx", getRequestBody(sql));
    }

    public String deleteWitness(int witnessId, int entryId) throws Exception {
        String sql = "delete FROM " + TABLE_NAME_WITNESS + " where entry_id=" + witnessId + " or entry_id=" + entryId;
        basicService("SQLDBEx", getRequestBody(sql));
        sql = "delete FROM " + TABLE_NAME_WITNESS_DETAIL + " where witness_id=" + witnessId;
        return basicService("SQLDBEx", getRequestBody(sql));
    }


    //cy1

    /**
     * @param nPage
     * @return
     */

    public String loadEntryCheckList(int nPage) { //yang 2016/10/10  oracle
        String sql = null;
        sql = "select entry_id, product_name,batch_id, quantity, entry_date,object_id,output_date,strength,sample_spec,sample_size,factory FROM " + TABLE_NAME_ENTRY_CHECK;
        if (CommData.orgType.equals("监理试验室")) {
            sql = sql + " where org_id in ( SELECT org_id FROM Organization WHERE parent_id in (" +
                    "select org_id FROM Organization WHERE supervisor_id='" + CommData.sLabId + "')) " +
                    " and (super_check_date IS NULL or super_check_date='') AND entry_date IS NOT NULL";
        } else {
            sql = sql + " where ( org_id in (select org_id FROM Organization " +//
                    "WHERE supervisor_id='" + CommData.sLabId + "' )  )  and (lab_check_date IS NULL or lab_check_date='') AND entry_date IS NOT NULL ";//AND org_type='物资部'
        }
        sql = sql + "  ORDER BY entry_date DESC";// ROWNUM < 50 在 HTML ,XML等解释性语言中不能使用小于号（<）和大于号（>），这是因为浏览器会误认为它们是标签
        if (CommData.DBType == 1)
           sql = "select * from (select a.*,ROWNUM rn from (" + sql + ") a ) t where t.rn &gt; " + (nPage - 1) * 10 + " and " + " t.rn &lt; " + ((nPage) * 10+1);
        else
           sql = "select * from (select a.*,Row_number() over (order by entry_date desc) as rn from (" + sql + ") a ) t where t.rn &gt; " + (nPage - 1) * 10 + " and " + " t.rn &lt; " + ((nPage) * 10+1);
        try {
            return basicService("SQLDBEx", getRequestBody(sql));
        } catch (Exception e) {
            LogWriter.log(CommData.ERROR, "loadEntryCheckList()，异常：" + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    public String changeEntryCheckList(int objectId, int nPage) { //yang 2016/12/23
        String sql = "";
        sql = "select entry_id, product_name,batch_id, quantity, entry_date,object_id,output_date,strength,sample_spec,sample_size,factory FROM " + TABLE_NAME_ENTRY_CHECK;
        if (CommData.orgType.equals("监理试验室")) {
            sql = sql + " where org_id in ( SELECT org_id FROM Organization WHERE parent_id in (" +
                    "select org_id FROM Organization WHERE supervisor_id='" + CommData.sLabId + "')) " +
                    " and (super_check_date IS NULL or super_check_date='') AND entry_date IS NOT NULL and object_id LIKE '" + objectId + "%'";
        } else {
            sql = sql + " where ( org_id in (select org_id FROM Organization " +
                    "WHERE supervisor_id='" + CommData.sLabId + "' )  )  and (lab_check_date IS NULL or lab_check_date='') AND entry_date IS NOT NULL and object_id LIKE '" + objectId + "%'";
        }
        sql = sql + "  ORDER BY entry_date DESC"; // ROWNUM < 50 在 HTML ,XML等解释性语言中不能使用小于号（<）和大于号（>），这是因为浏览器会误认为它们是标签

        if (CommData.DBType == 1)
            sql = "select * from (select a.*,ROWNUM rn from (" + sql + ") a ) t where t.rn &gt; " + (nPage - 1) * 10 + " and " + " t.rn &lt; " +((nPage) * 10+1);
        else
            sql = "select * from (select a.*,Row_number() over (order by entry_date desc) as rn from (" + sql + ") a ) t where t.rn &gt; " + (nPage - 1) * 10 + " and " + " t.rn &lt; " + ((nPage) * 10+1);

        try {
            return basicService("SQLDBEx", getRequestBody(sql));
        } catch (Exception e) {
            LogWriter.log(CommData.ERROR, "loadEntryCheckList()，异常：" + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    public String loadEntryCheck(int entryId) {
        String sql = "select entry_id, object_id, product_name, batch_id, output_date,quantity, sample_spec, strength, entry_date, report_id," +
                "lab_person,lab_comment, lab_check_date,super_person, super_comment,super_check_date,sample_size,factory "
                + " FROM " + TABLE_NAME_ENTRY_CHECK + " where entry_id=" + entryId;
        try {
            result = basicService("SQLDBEx", getRequestBody(sql));
            if (result.equals("")) {
                LogWriter.log(CommData.ERROR, "loadEntryCheck(int entryId)，sql语句" + sql);
            }
            return result;
        } catch (Exception e) {
            LogWriter.log(CommData.ERROR, "loadEntryCheck(int entryId)， 异常：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public String uploadEntryCheck(EntryCheckData entryCheckData) {
        String sql = null;
        if (CommData.orgType.equals("监理试验室")) {
            sql = "update " + TABLE_NAME_ENTRY_CHECK + " set super_comment='" + entryCheckData.getSuperComment() +
                    "', super_check_date='" + entryCheckData.getSuperCheckDate() +
                    "', super_person='" + CommData.username + "'";
        } else {
            int is_witness = 0;
            if (entryCheckData.getWitness().equals("不见证")) {
                is_witness = 0;
            } else {
                is_witness = 1;
            }
            sql = "update " + TABLE_NAME_ENTRY_CHECK + " set lab_comment='" + entryCheckData.getLabComment() +
                    "', lab_check_date='" + entryCheckData.getLabCheckDate() +
                    "', lab_person='" + CommData.username + "',is_witness=" + is_witness;
        }
        sql = sql + " where entry_id = " + entryCheckData.getEntryId();
        try {
            result = basicService("ExecuteSQL", getRequestBodyEx(sql));
            return result;
        } catch (Exception e) {
            LogWriter.log(CommData.ERROR, "uploadEntryCheck(EntryCheckData entryCheckData)方法，sql语句" + sql + " 异常：" + e.getMessage());
        }
        return result;
    }

    public String getWitenessData(int witenessId) {
        String sql = "SELECT a.witness_org_id,a.apply_from,a.witness_id,a.Sample_id,a.apply_time,b.object_name,a.object_id,c.org_id,c.org_name,d.org_name as parent_name,a.sample_person as name,a.entry_id,i.value as test_items,f.org_name as w_org_name "
                + " FROM WitnessData a LEFT JOIN TestObject b ON a.object_id= b.object_id INNER JOIN Organization c on a.sample_org_id=c.org_id LEFT JOIN Organization d on c.parent_id=d.org_id "
                + " LEFT JOIN Organization f on a.witness_org_id=f.org_id LEFT JOIN Organization g on f.parent_id=g.org_id LEFT JOIN TestDetail i on i.data_id=a.data_id and i.meta_id = 100014 "
                + " where a.witness_id =" + witenessId;
        try {
            result = basicService("SQLDBEx", getRequestBody(sql));
            if (result.equals("")) {
                LogWriter.log(CommData.ERROR, "getWitenessData(int witenessId)方法，sql语句" + sql);
            }
            return result;
        } catch (Exception e) {
            LogWriter.log(CommData.ERROR, "getWitenessData(int witenessId)方法，异常：" + e.getMessage());
            return "";
        }
    }

    public String getWitenessDetail(int witenessId) {
        String sql = "SELECT * from WitnessDetail where witness_id = " + witenessId;
        try {
            return basicService("SQLDBEx", getRequestBody(sql));
        } catch (Exception e) {
            LogWriter.log(CommData.ERROR, "getWitenessDetail(int witenessId)方法，异常：" + e.getMessage());
            return "";
        }
    }

    public String loadTestObjectDict(String sObjectId) {
        String sql = "SELECT a.object_id,a.meta_id,b.meta_name,b.input_type,b.data_type,b.group_id FROM WitnessItem a LEFT JOIN TestDict b on a.object_id=b.object_id and a.meta_id=b.meta_id WHERE b.in_order = 1 and a.object_id=" + sObjectId;
        try {
            return basicService("SQLDBEx", getRequestBody(sql));
        } catch (Exception e) {
            LogWriter.log(CommData.ERROR, "loadTestObjectDict(String sObjectId)方法，异常：" + e.getMessage());
            return "";
        }
    }

    private byte[] getLoginRequestBody(String sUserName, String sUserPassword) throws Exception {
        String soap = readLoginSoap();
        soap = soap.replace("$sUserName", sUserName);
        soap = soap.replace("$sUserPassword", sUserPassword);
        return soap.getBytes();
    }

    private byte[] getRequestBody(String sSql) throws Exception {
        String authInfo = CommData.name + ";" + CommData.sLabId;
        String soap = readSoap();
        sSql = EncryptSQL(sSql);
        soap = soap.replace("$Sql", sSql);
        soap = soap.replace("$authInfo", authInfo);
        soap = soap.replace("$sessionKey", SessionKeyUtil.getSessionKey(CommData.name, CommData.sLabId));
        return soap.getBytes();
    }

    private byte[] getRequestBodyEx(String sSql) throws Exception {
        String authInfo = CommData.name + ";" + CommData.sLabId;
        String soap = readSoapEx();
        sSql = EncryptSQL(sSql);
        soap = soap.replace("$Sql", sSql);
        soap = soap.replace("$authInfo", authInfo);
        soap = soap.replace("$sessionKey", SessionKeyUtil.getSessionKey(CommData.name, CommData.sLabId));
        return soap.getBytes();
    }

    //mix task abolish soap
    private byte[] getAbolishRequestBody(String abolishReason, String taskId, String orgId, String userName) throws Exception {
        String authInfo = CommData.name + ";" + CommData.sLabId;
        String soap = readAbolishSoap();
        soap = soap.replace("$sAbolish_reason", abolishReason);
        soap = soap.replace("$sTaskId", taskId);
        soap = soap.replace("$sOrgId", orgId);
        soap = soap.replace("$sUserName", userName);
        soap = soap.replace("$sAuthInfo", authInfo);
        soap = soap.replace("$sSessionKey", SessionKeyUtil.getSessionKey(CommData.name, CommData.sLabId));
        return soap.getBytes();
    }

    //mix task flow state(addTask and hearTask)  soap
    private byte[] getFlowStateRequestBody(String orgId, String taskId, String sSql, String type, String projectName,
                                           String objectId, String strength, String orderDate, String orderPerson, String position, String stationName) throws Exception {
        //type 1表示新建任务  2表示受理任务
        String authInfo = CommData.name + ";" + CommData.sLabId;
        String soap = readFlowStateSoap();
        soap = soap.replace("$sOrgId", orgId);
        soap = soap.replace("$sTaskId", taskId);
        soap = soap.replace("$sSQL", sSql);
        soap = soap.replace("$nType", type);
        soap = soap.replace("$sProjectName", projectName);
        soap = soap.replace("$sObjectId", objectId);
        soap = soap.replace("$sStrength", strength);
        soap = soap.replace("$sOrderDate", orderDate);
        soap = soap.replace("$sOrderPerson", orderPerson);
        soap = soap.replace("$sPosition", position);
        soap = soap.replace("$sStation", stationName);
        soap = soap.replace("$sAuthInfo", authInfo);
        String s = SessionKeyUtil.getSessionKey(CommData.name, CommData.sLabId);
        soap = soap.replace("$sSessionKey", SessionKeyUtil.getSessionKey(CommData.name, CommData.sLabId));
        return soap.getBytes();
    }

    private String readLoginSoap() {
        InputStream inStream = null;
        byte[] data = null;
        try {
            inStream = mContext.getResources().openRawResource(R.raw.soap_login);
            data = StreamUtils.read(inStream);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new String(data);
    }

    private String readUploadFileSoap() {
        InputStream inStream = null;
        byte[] data = null;
        try {
            inStream = mContext.getResources().openRawResource(R.raw.soap_upload_file);
            data = StreamUtils.read(inStream);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new String(data);
    }

    private String readSoap() throws Exception {
        byte[] data;
        InputStream inStream = mContext.getResources().openRawResource(R.raw.soap);
        data = StreamUtils.read(inStream);
        Log.i("request body", data.toString());
        return new String(data);
    }

    private String readSoapEx() throws Exception {
        byte[] data;
        InputStream inStream = mContext.getResources().openRawResource(R.raw.soap_ex);
        data = StreamUtils.read(inStream);
        Log.i("request body", data.toString());
        return new String(data);
    }

    private String readAbolishSoap() throws Exception {
        byte[] data;
        InputStream inStream = mContext.getResources().openRawResource(R.raw.soap_abolish_task);
        data = StreamUtils.read(inStream);
        Log.i("request body", data.toString());
        return new String(data);
    }

    private String readFlowStateSoap() throws Exception {
        byte[] data;
        InputStream inStream = mContext.getResources().openRawResource(R.raw.soap_flow_state);
        data = StreamUtils.read(inStream);
        Log.i("request body", data.toString());
        return new String(data);
    }

    public String basicService(String operation, byte[] requestbody) throws Exception {
        address = "http://" + CommData.serverAddress + Configuration.SERVICE_NAME;
        String path = address + "?op=" + operation; //API地址也就是webservice的访问地址
        HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
        conn.setConnectTimeout(90000);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
//        conn.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("Content-Length", String.valueOf(requestbody.length));
        conn.getOutputStream().write(requestbody);

        if (conn.getResponseCode() == 200) {
            if (operation.equals("Login")) {
                result = SOAPUtils.parseSOAP(conn.getInputStream(), "LoginResult");
                return result;
            } else if (operation.equals("SQLDBEx")) {
                result = SOAPUtils.parseSOAP(conn.getInputStream(), "SQLDBExResult");
                return result;
            } else if (operation.equals("UploadFile")) {
                result = SOAPUtils.parseSOAP(conn.getInputStream(), "UploadFileResult");
                return result;
            } else if (operation.equals("RegApply")) {
                result = SOAPUtils.parseSOAP(conn.getInputStream(), "RegApplyResult");
                return result;
            } else if (operation.equals("RegQuery")) {
                result = SOAPUtils.parseSOAP(conn.getInputStream(), "RegQueryResult");
                return result;
            } else if (operation.equals("GetNameByUserId")) {
                return SOAPUtils.parseSOAP(conn.getInputStream(), "GetNameByUserIdResult");
            } else if (operation.equals("ExecuteSQL")) {
                result = SOAPUtils.parseSOAP(conn.getInputStream(), "ExecuteSQLResult");
                return result;
            } else if (operation.equals("UploadFileEx1")) {
                result = SOAPUtils.parseSOAP(conn.getInputStream(), "UploadFileEx1Result");
                return result;
            } else if (operation.equals("SampleLog")) {
                result = SOAPUtils.parseSOAP(conn.getInputStream(), "SampleLogResult");
                return result;
            } else if (operation.equals("SetFlowState")) {
                result = SOAPUtils.parseSOAP(conn.getInputStream(), "SetFlowStateResult");
                return result;
            } else if (operation.equals("AbolishTask")) {
                result = SOAPUtils.parseSOAP(conn.getInputStream(), "AbolishTaskResult");
                return result;
            }
        }
        return "";
    }

    public String uploadFile(File filepath, String filename) throws Exception {
        return basicService("UploadFile", getUploadRequestBody(filepath, filename));
    }

    public byte[] getUploadRequestBody(File file, String filename) throws Exception {
        String authInfo = CommData.name + ";" + CommData.sLabId;

        File uploadfile = new File(file, filename);
        String soap = readUploadFileSoap();
        soap = soap.replace("$base64Binary", Base64Util.encodeFileToBase64Binary(uploadfile));
        soap = soap.replace("$filename", filename);
        soap = soap.replace("$authInfo", authInfo);
        soap = soap.replace("$sessionKey", SessionKeyUtil.getSessionKey(CommData.name, CommData.sLabId));
        return soap.getBytes();
    }

    public String registerApply(String username, String orgId, String Email, String mobilePhone, String serialNumber,
                                String applyTime, String applyUser, String productType, String authorInfo, String sessionKey) throws Exception {
        return basicService("RegApply", getRegApplyRequestBody(username, orgId, Email, mobilePhone, serialNumber,
                applyTime, applyUser, productType, authorInfo, sessionKey));
    }

    public byte[] getRegApplyRequestBody(String username, String orgId, String Email, String mobilePhone, String serialNumber,
                                         String applyTime, String applyUser, String productType, String authorInfo, String sessionKey) throws Exception {
        String soap = readRegApplySoap();
        soap = soap.replace("$sUername", username);
        soap = soap.replace("$sOrgId", orgId);
        soap = soap.replace("$sMobilePhone", mobilePhone);
        soap = soap.replace("$sEmail", Email);
        soap = soap.replace("$sSerialNumber", serialNumber);
        soap = soap.replace("$sApplyTime", applyTime);
        soap = soap.replace("$sApplyUser", applyUser);
        soap = soap.replace("$nProductType", productType);
        soap = soap.replace("$sAuthInfo", authorInfo);
        soap = soap.replace("$sSessionKey", sessionKey);
        return soap.getBytes();
    }

    public byte[] getUploadSampleLogRequestBody(String orgId, String sampleId, String personName, String position,
                                                int operation) throws Exception {
        String authInfo = CommData.name + ";" + CommData.sLabId;
        String soap = readSampleLogSoap();
        soap = soap.replace("$org_id", orgId);
        soap = soap.replace("$sample_id", sampleId);
        soap = soap.replace("$person_name", personName);
        soap = soap.replace("$position", position);
        soap = soap.replace("$operation", String.valueOf(operation));
        soap = soap.replace("$sAuthInfo", authInfo);
        soap = soap.replace("$sSessionKey", SessionKeyUtil.getSessionKey(CommData.name, CommData.sLabId));
        return soap.getBytes();
    }


    private String readRegApplySoap() throws Exception {
        byte[] data;
        InputStream inStream = mContext.getResources().openRawResource(R.raw.soap_reg_apply);
        data = StreamUtils.read(inStream);
        return new String(data);
    }

    private String readSampleLogSoap() throws Exception {
        byte[] data;
        InputStream inStream = mContext.getResources().openRawResource(R.raw.soap_sample_log);
        data = StreamUtils.read(inStream);
        return new String(data);
    }

    public String registerQuery(String email, String serialNumber,
                                String authorInfo, String sessionKey) throws Exception {
        return basicService("RegQuery", getRegQueryRequestBody(email, serialNumber,
                authorInfo, sessionKey));
    }

    public byte[] getRegQueryRequestBody(String email, String serialNumber, String authorInfo, String sessionKey) throws Exception {
        String soap = readRegQuerySoap();
        soap = soap.replace("$email", email);
        soap = soap.replace("$sSerialNumber", serialNumber);
        soap = soap.replace("$sAuthInfo", authorInfo);
        soap = soap.replace("$sSessionKey", sessionKey);
        return soap.getBytes();
    }

    private String readRegQuerySoap() throws Exception {
        byte[] data;
        InputStream inStream = mContext.getResources().openRawResource(R.raw.soap_reg_query);
        data = StreamUtils.read(inStream);
        return new String(data);
    }

    /*
    * 根据用户id获取用户名
    *
    * */
    public String getUsernameById(String userId) throws Exception {
        return basicService("GetNameByUserId", getUsernameRequestBody(userId));
    }

    public byte[] getUsernameRequestBody(String userId) {
        String soap = readGetUsernameSoap();
        soap = soap.replace("$userId", userId);
        return soap.getBytes();
    }

    public String readGetUsernameSoap() {
        InputStream inStream = null;
        byte[] data = null;
        try {
            inStream = mContext.getResources().openRawResource(R.raw.soap_getname);
            data = StreamUtils.read(inStream);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new String(data);
    }

    private boolean isExitConcreteSample(String orgId, String noticeId, String sampleId) {
        String sql = "select * from ConcreteSample a INNER JOIN ConcreteBatch b ON a.org_id=b.org_id and a.data_id=b.data_id and a.batch_id=b.batch_id  " +
                "where a.org_id='" + orgId + "' and b.notice_id='" + noticeId + "' and a.sample_id='" + sampleId + "'";
        String result = null;
        try {
            result = basicService("SQLDBEx", getRequestBody(sql));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result.equals("")) {
            LogWriter.log(CommData.ERROR, "isExitConcreteSample(String orgId, String noticeId, String sampleId)方法，sql语句" + sql);
            return false;
        } else {
            return true;
        }
    }

    /*
    * @param curing_way 养护方式
    * @param use 用途
    * @param time 龄期
    * */
    public String productingWitness(String personName, String date, String orgId, String noticeId, String sampleId
            , String use, String curing_way, String time) throws Exception {
        if (isExitConcreteSample(orgId, noticeId, sampleId)) {
            if (isExitSampleProduct(orgId, noticeId, sampleId)) {
                return UPLOADED;
            } else {
                /*String sql = "update " + TABLE_NAME_CONCRETE_SAMPLE + " set witness_person='" + person + "', witness_date='" + date
                        + "', age =" + time + " ,maintain_mode='" + curing_way + "',construction_site='" + use
                        + "' where org_id='" + orgId + "' and notice_id='" + noticeId + "' and sample_id='" + sampleId + "'";*/
                return basicService("SampleLog", getUploadSampleLogRequestBody(orgId, sampleId, personName, "", 1));
            }
        } else return NOT_EXIST;

    }

    private boolean isExitSampleProduct(String orgId, String noticeId, String sampleId) throws Exception {
        String sql = "select * from ConcreteSample a INNER JOIN ConcreteBatch b ON a.org_id=b.org_id and a.data_id=b.data_id and a.batch_id=b.batch_id  " +
                "where a.org_id='" + orgId + "' and b.notice_id='" + noticeId + "' and a.sample_id='" + sampleId + "' and a.witness_person IS NOT NULL and a.witness_date IS NOT NULL";
        result = basicService("SQLDBEx", getRequestBody(sql));
        if (result.equals("")) {
            LogWriter.log(CommData.ERROR, "isExitSampleProduct方法，sql语句" + sql);
            return false;
        } else {
            return true;
        }
    }

    private boolean isExitSampleModle(String orgId, String noticeId, String sampleId) throws Exception {
        String sql = "select * from ConcreteSample a INNER JOIN ConcreteBatch b ON a.org_id=b.org_id and a.data_id=b.data_id and a.batch_id=b.batch_id  " +
                "where a.org_id='" + orgId + "' and b.notice_id='" + noticeId + "' and a.sample_id='" + sampleId + "' and a.remove_person IS NOT NULL and a.remove_date IS NOT NULL";
        String result = basicService("SQLDBEx", getRequestBody(sql));
        if (result.equals("")) {
            LogWriter.log(CommData.ERROR, "isExitSampleModle方法，sql语句" + sql);
            return false;
        } else {
            return true;
        }
    }

    private boolean isExitInRoom(String orgId, String noticeId, String sampleId) throws Exception {
        String sql = "select * from ConcreteSample a INNER JOIN ConcreteBatch b ON a.org_id=b.org_id and a.data_id=b.data_id and a.batch_id=b.batch_id  " +
                "where a.org_id='" + orgId + "' and b.notice_id='" + noticeId + "' and a.sample_id='" + sampleId + "' and a.input_person IS NOT NULL and a.input_date IS NOT NULL";
        String result = basicService("SQLDBEx", getRequestBody(sql));
        if (result.equals("")) {
            LogWriter.log(CommData.ERROR, "isExitInRoom方法，sql语句" + sql);
            return false;
        } else {
            return true;
        }
    }

    private boolean isExitOutRoom(String orgId, String noticeId, String sampleId) throws Exception {
        String sql = "select * from ConcreteSample a INNER JOIN ConcreteBatch b ON a.org_id=b.org_id and a.data_id=b.data_id and a.batch_id=b.batch_id  " +
                "where a.org_id='" + orgId + "' and b.notice_id='" + noticeId + "' and a.sample_id='" + sampleId + "' and a.output_person IS NOT NULL and a.output_date IS NOT NULL";
        String result = basicService("SQLDBEx", getRequestBody(sql));
        if (result.equals("")) {
            LogWriter.log(CommData.ERROR, "isExitOutRoom方法，sql语句" + sql);
            return false;
        } else {
            return true;
        }
    }

    public String inRoomWitness(String personName, String date, String orgId, String noticeId, String sampleId, String position) throws Exception {
        if (isExitConcreteSample(orgId, noticeId, sampleId)) {
            if (isExitInRoom(orgId, noticeId, sampleId)) {
                return UPLOADED;
            } else {
                /*String sql = "update " + TABLE_NAME_CONCRETE_SAMPLE + " set input_person='" + person + "', input_date='" + date
                        + "' where org_id='" + orgId + "' and notice_id='" + noticeId + "' and sample_id='" + sampleId + "'";*/
                return basicService("SampleLog", getUploadSampleLogRequestBody(orgId, sampleId, personName, position, 3));
            }
        } else return NOT_EXIST;
    }

    public String outRootWitness(String personName, String date, String orgId, String noticeId, String sampleId) throws Exception {
        if (isExitConcreteSample(orgId, noticeId, sampleId)) {
            if (isExitOutRoom(orgId, noticeId, sampleId)) {
                return UPLOADED;
            } else {
                /*String sql = "update " + TABLE_NAME_CONCRETE_SAMPLE + " set output_person='" + person + "', output_date='" + date
                        + "' where org_id='" + orgId + "' and notice_id='" + noticeId + "' and sample_id='" + sampleId + "'";*/
                return basicService("SampleLog", getUploadSampleLogRequestBody(orgId, sampleId, personName, "", 4));
            }
        } else return NOT_EXIST;
    }

    public String sampleModel(String personName, String date, String orgId, String noticeId, String sampleId) throws Exception {
        if (isExitConcreteSample(orgId, noticeId, sampleId)) {
            if (isExitSampleModle(orgId, noticeId, sampleId)) {
                return UPLOADED;
            } else {
                /*String sql = "update " + TABLE_NAME_CONCRETE_SAMPLE + " set test_person='" + person + "', test_date='" + date
                        + "' where org_id='" + orgId + "' and notice_id='" + noticeId + "' and sample_id='" + sampleId + "'";*/
                return basicService("SampleLog", getUploadSampleLogRequestBody(orgId, sampleId, personName, "", 2));
            }
        } else return NOT_EXIST;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
    *获取现场检测任务
     */
    public String loadSiteTest() {
        String sql = "SELECT a.data_id,a.index_id,a.org_id,a.order_id,a.object_id,a.meta_id,a.meta_name,a.test_name,a.sample_id,a.test_count,a.start_no,a.sample_spec,a.\"SIZE\",a.original_gauge,a.product_date," +
                "a.expected_date,a.age,a.download_date,a.test_date,a.tester,a.transfer_time,a.download_count,a.test_category,a.groups,b.order_date " +
                " FROM TestTask a INNER JOIN TestData b ON a.data_id=b.data_id  WHERE (a.download_count is null or a.download_count=0)  and a.download_date is null and a.org_id ='" + CommData.sLabId + "' and a.object_id BETWEEN 1500 AND 1600";

        try {
            LogWriter.log(CommData.INFO, "loadSiteTest()方法，sql语句" + sql);
            result = basicService("SQLDBEx", getRequestBody(sql));
            if (result.equals("")) {
                LogWriter.log(CommData.ERROR, "loadSiteTest()方法，sql语句" + sql);
            }
            return result;
        } catch (Exception e) {
            LogWriter.log(CommData.ERROR, "SQL异常，sql语句" + sql);
            e.printStackTrace();
            return result;
        }
    }

    public String updateTeatTaskStatus(int dataId, int indexId, String downLoadDate, int downLoadCount) throws Exception {
        if (isTestTaskStatus(dataId, indexId)) {
            String sql = "update " + TABLE_NAME_TEST_TASK + " set download_date='" + downLoadDate + "', download_count=" + downLoadCount
                    + " where data_id=" + dataId + " and index_id=" + indexId;
            String result = basicService("ExecuteSQL", getRequestBodyEx(sql));
            return result;
        } else {
            return "";
        }
    }


    public boolean isTestTaskStatus(int dataId, int indexId) throws Exception {
        String sql = "select test_name from  " + TABLE_NAME_TEST_TASK + " where data_id=" + dataId + " and index_id=" + indexId + " and download_count=0 and download_date IS NULL";

        result = basicService("SQLDBEx", getRequestBody(sql));
        if (result.equals("") || result == null) {
            LogWriter.log(CommData.ERROR, "isTestTaskStatus方法，sql语句" + sql);
            return false;
        } else {
            return true;
        }
    }

    public void isOldTestTaskStatus(int dataId, int indexId) throws Exception {//测试用，测试时，用于还原测试任务状态
        String sql = "update " + TABLE_NAME_TEST_TASK + " set download_date = null, download_count=0 where data_id=" + dataId + " and index_id=" + indexId;
        String result = basicService("ExecuteSQL", getRequestBodyEx(sql));
        if (result.equals("")) {

        }
    }

    public String getSiteTestItemData() {
        String sql;
        sql = "select * from  " + TABLE_NAME_SITE_TEST_ITEM;
        try {
            result = basicService("SQLDBEx", getRequestBody(sql));
            if (result.equals("")) {
                LogWriter.log(CommData.ERROR, "getSiteTestItemData()方法，sql语句" + sql);
            }
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String siteUploadFile1(byte[] buff, String filename, int index, int len) throws Exception {

        return basicService("UploadFileEx1", getSiteUploadRequestBody(buff, filename, index, len, 5));
    }

    public String siteUploadFile(byte[] buff, String filename, int index, int len) throws Exception {

        return basicService("UploadFileEx1", getSiteUploadRequestBody(buff, filename, index, len, 4));
    }

    public byte[] getSiteUploadRequestBody(byte[] buff, String filename, int index, int len, int fileCategory) throws Exception {
        String authInfo = CommData.name + ";" + CommData.sLabId;
        String soap = readSiteUploadFileSoap();
        String s = Base64.encodeToString(buff, Base64.DEFAULT);
        soap = soap.replace("$base64Binary", Base64.encodeToString(buff, Base64.DEFAULT));
        soap = soap.replace("$partNum", String.valueOf(index));
        soap = soap.replace("$len", String.valueOf(len));
        soap = soap.replace("$filename", filename);
        soap = soap.replace("$FileCategory", String.valueOf(fileCategory));
        soap = soap.replace("$authInfo", authInfo);
        soap = soap.replace("$sessionKey", SessionKeyUtil.getSessionKey(CommData.name, CommData.sLabId));
        return soap.getBytes();
    }

    private String readSiteUploadFileSoap() {
        InputStream inStream = null;
        byte[] data = null;
        try {
            inStream = mContext.getResources().openRawResource(R.raw.soap_site_upload_file);
            data = StreamUtils.read(inStream);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new String(data);
    }


    public String insertTestValue(List<SendTestValue> sendTestValueList) throws Exception {
        String sql = "";
        for (int i = 0; i < sendTestValueList.size(); i++) {
            if (sendTestValueList.get(i).getUploadStatus() != 1) {
                String sql1 = "select * from TestDetail where meta_id=" + sendTestValueList.get(i).getMateId() + "and data_id=" + sendTestValueList.get(i).getDataId();
                result = "";
                result = basicService("SQLDBEx", getRequestBody(sql1));
                if (result.length() > 0) {
                    continue;
                }
                sql += "INSERT INTO TestDetail (data_id, meta_id,value) VALUES  (" + sendTestValueList.get(i).getDataId() + "," + sendTestValueList.get(i).getMateId() + ",'" + sendTestValueList.get(i).getValue() + "');";
            }
        }
        if (sql.equals("")) {
            return "";
        }
        String result = basicService("ExecuteSQL", getRequestBodyEx(sql));
        return result;
    }

    //获取测点的meta
    public String getPointMeta(int dataId, String metaIds) {
        String sql;
        sql = "select data_id,meta_id,value from TestDetail where data_id=" + dataId + " and FLOOR(meta_id/100) in (" + metaIds + ")";
        try {
            result = "";
            result = basicService("SQLDBEx", getRequestBody(sql));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    //拌合站
    //加载拌合任务
    public String loadMixTask() throws Exception {
        String sql;

        sql = "select top 50 org_id,task_id,prj_name,position,strength,volume,station_name,begin_time,applicant,application_time,state,slump_set,destination from m_p_task where org_id in(select org_id from Organization where entrust_Lab in(select org_id from Organization where parent_id in(select org_id  from Organization where supervisor_id='" + CommData.sLabId + "'))) ORDER BY application_time DESC";
        return basicService("SQLDBEx", getRequestBody(sql));
    }

    public String loadMixTaskAndOrgId() throws Exception {
        String sql;
        sql = "select top 50 org_id,task_id,prj_name,position,strength,volume,station_name,begin_time,applicant,application_time,state,slump_set,destination from m_p_task where org_id='" + CommData.sLabId + "' ORDER BY application_time DESC";
        return basicService("SQLDBEx", getRequestBody(sql));
    }

    public String loadMixTaskAndTestRoom() throws Exception {
        String sql;
        sql = "select top 50 org_id,task_id,prj_name,position,strength,volume,station_name,begin_time,applicant,application_time,state,slump_set,destination from m_p_task where org_id in(select org_id from Organization where entrust_Lab='" + CommData.sLabId + "') ORDER BY application_time DESC";
        return basicService("SQLDBEx", getRequestBody(sql));
    }


    public String getProgressState(String orgId, String taskNumber) throws Exception {
        String sql;
        sql = "select action_name,id from t_flow_state  where org_id='" + orgId + "' and task_id='" + taskNumber + "' ORDER BY id ASC";
        return basicService("SQLDBEx", getRequestBody(sql));
    }

    //新建拌合任务
    public String saveMixTask(String applyNumber, String projectName, String inflictionPosition, String mixStation, String mixStationName, String planVolume,
                              String intensityLevel, String planSlump, String supplyPoint, String predictStartTime, String applyTime, int state) throws Exception {
        String sql;
       /* public static int GetInsertTaskSQL(
                string taskId,//任务单号
                string projectName,//工程名称
                string objectId, //试验编号  1409
                string strength,/度等级
        string orderDate,//委托日期
        string orderPerson,//委托人
        string position, //施工位置
        string station, //拌和站名称
        string stationId //拌和站id org_id
        )*/ //state
        if (state == 1) {
            sql = "INSERT INTO m_p_task (org_id,task_id,prj_name,position,strength,volume,station_name,begin_time,applicant,application_time,slump_set,destination) VALUES " +
                    "('" + mixStation + "','" + applyNumber + "','" + projectName + "','" + inflictionPosition + "','" + intensityLevel + "','" + planVolume + "','" + mixStationName + "','" + predictStartTime + "','" + CommData.username + "','" + applyTime + "','" + planSlump + "','" + supplyPoint + "')";
            String result = basicService("SetFlowState", getFlowStateRequestBody(CommData.sLabId, applyNumber, sql, "1", projectName, "1409", intensityLevel, applyTime, CommData.username, inflictionPosition, mixStationName));
            return result;
        } else {
            sql = "INSERT INTO m_p_task (org_id,task_id,prj_name,position,strength,volume,station_name,begin_time,applicant,application_time,slump_set,destination,state) VALUES " +
                    "('" + mixStation + "','" + applyNumber + "','" + projectName + "','" + inflictionPosition + "','" + intensityLevel + "','" + planVolume + "','" + mixStationName + "','" + predictStartTime + "','" + CommData.username + "','" + applyTime + "','" + planSlump + "','" + supplyPoint + "','0')";
            String result = basicService("ExecuteSQL", getRequestBodyEx(sql));
            return result;
        }
    }

    public String updateMixTask(String applyNumber, String projectName, String inflictionPosition, String mixStation, String mixStationName, String planVolume,
                                String intensityLevel, String planSlump, String supplyPoint, String predictStartTime, String applyTime, int state) throws Exception {
        String sql;
        sql = "UPDATE m_p_task SET org_id='" + mixStation + "',prj_name='" + projectName + "',position='" + inflictionPosition + "',strength='" + intensityLevel + "',volume='" + planVolume +
                "',station_name='" + mixStationName + "',begin_time='" + predictStartTime + "',applicant='" + CommData.username + "',application_time='" + applyTime + "',slump_set='" + planSlump + "',destination='" + supplyPoint + "'" +
                " where task_id='" + applyNumber + "'";
        if (state == 1) {
            String result = basicService("SetFlowState", getFlowStateRequestBody(CommData.sLabId, applyNumber, sql, "1", projectName, "1409", intensityLevel, applyTime, CommData.username, inflictionPosition, mixStationName));
            return result;
        } else {
            String result = basicService("ExecuteSQL", getRequestBodyEx(sql));
            return result;
        }
    }

    //受理任务
    public String saveHearTask(String taskId, String orgId, String planVolume, String optionParson, String mixMachine, String time) throws Exception {
        String sql;
        sql = "select * from m_p_task  where  task_id='" + taskId + "' and accept_time is null";
        String isNull = basicService("SQLDBEx", getRequestBody(sql));
        if (!isNull.equals("")) {
           /* sql = "UPDATE m_p_task SET accept_time='" + time + "',accept_person='" + CommData.username + "',state=2" +
                    " where task_id='" + taskNumber + "';";
            String result = basicService("ExecuteSQL", getRequestBodyEx(sql));
            if (result.toLowerCase().equals("true")) {
                sql = "INSERT INTO t_assign_volume (task_id,org_id,assigned_volume,operator,machine_id,accept_time,accept_person,assign_type) VALUES " +
                        " ('" + taskNumber + "','" + orgId + "','" + planVolume + "','" + optionParson + "','" + mixMachine + "','" + time + "','" + CommData.username + "','1');";
                result = basicService("ExecuteSQL", getRequestBodyEx(sql));
                return result;
            } else {
                return "";
            }*/
            sql = "UPDATE m_p_task SET accept_time='" + time + "',accept_person='" + CommData.username +
                    "'  where task_id='" + taskId + "';";
            sql += "INSERT INTO t_assign_volume (task_id,org_id,assigned_volume,operator,machine_id,accept_time,accept_person,assign_type) VALUES " +
                    " ('" + taskId + "','" + orgId + "','" + planVolume + "','" + optionParson + "','" + mixMachine + "','" + time + "','" + CommData.username + "','1')";
            result = basicService("SetFlowState", getFlowStateRequestBody(CommData.sLabId, taskId, sql, "2", "", "", "", "", "", "", ""));
            return result;
        } else {
            return "false";
        }
    }


    public String saveOrUpdateTestDetection(String orgId, String option, String taskNumber, String envTemperature,
                                            String productionTime, String checkTime, String slump, String gasContent) {
        String sql;
        if (option.equals("firstDetection")) {
            try {
                String id = getFirstDetection(taskNumber);
                if (id != null && !id.equals("")) {
                   /* sql="INSERT INTO t_first_check (org_id,task_id,env_temperature,production_time,check_time,slump,air_content,check_person) VALUES "+
                    " ('"+orgId+"','"+taskNumber+"','"+envTemperature+"','"+productionTime+"','"+checkTime+"','"+slump+"','"+gasContent+"','"+CommData.username+"')";
                    String result= basicService("ExecuteSQL", getRequestBodyEx(sql));
                    return result;
                }else{*/

                    sql = "UPDATE t_first_check SET env_temperature='" + envTemperature + "',production_time='" + productionTime + "',check_time='" + checkTime + "',slump='" + slump + "',air_content='" + gasContent + "',check_person='" + CommData.username + "'" +
                            " where task_id='" + taskNumber + "' and id=" + id;
                    String result = basicService("ExecuteSQL", getRequestBodyEx(sql));
                    return result;
                } else {
                    return "isNull";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }

        } else {
            try {
                String id = getSiteDetection(taskNumber);
                if (!id.equals("") && id != null) {
                  /*  sql="INSERT INTO t_site_check (org_id,task_id,env_temperature,production_time,check_time,slump,air_content,check_person) VALUES "+
                            " ('"+orgId+"','"+taskNumber+"','"+envTemperature+"','"+productionTime+"','"+checkTime+"','"+slump+"','"+gasContent+"','"+CommData.username+"')";
                    String result= basicService("ExecuteSQL", getRequestBodyEx(sql));
                    return result;
                 }else{*/

                    sql = "UPDATE t_site_check SET env_temperature='" + envTemperature + "',production_time='" + productionTime + "',check_time='" + checkTime + "',slump='" + slump + "',air_content='" + gasContent + "',check_person='" + CommData.username + "'" +
                            " where task_id='" + taskNumber + "' and id=" + Integer.parseInt(id);
                    String result = basicService("ExecuteSQL", getRequestBodyEx(sql));
                    return result;
                } else {
                    return "isNull";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    private String getFirstDetection(String taskNumber) throws Exception {
        String sql;
        sql = "select id from t_first_check where task_id='" + taskNumber + "' and check_person is null";
        return basicService("SQLDBEx", getRequestBody(sql));
        /*if(!s.equals("") && s!=null) {
            return true;
        }else{
            return false;
        }*/
    }

    public String getFirstDetectionData(String taskNumber) throws Exception {
        String sql;
        sql = "select * from t_first_check where check_person is not null and (super_confirm is null or super_confirm ='') and id=(select max(id) from t_first_check where task_id='" + taskNumber + "')";
        return basicService("SQLDBEx", getRequestBody(sql));
    }


    private String getSiteDetection(String taskNumber) throws Exception {
        String sql;
        sql = "select id from t_site_check where task_id='" + taskNumber + "' and check_person is null";
        return basicService("SQLDBEx", getRequestBody(sql));
       /* if(!s.equals("") && s!=null) {
            return true;
        }else{
            return false;
        }*/
    }


    public String getSiteDetectionData(String taskId) throws Exception {
        String sql;
        sql = "select * from t_site_check where check_person is not null and (super_confirm is null or super_confirm ='') and  id=(select max(id) from t_site_check where task_id='" + taskId + "')";
        return basicService("SQLDBEx", getRequestBody(sql));

    }

   /*
    *old TestConfirm save or update
    *
    * public String saveOrUpdateTestConfirm(String orgId,String option,String taskNumber,String envTemperature,
                                            String productionTime,String checkTime,String slump,String gasContent,String checkResult,String checkOpinion){
        String sql;
        if(option.equals("firstConfirm")){
            try {
                if(getFirstDetection(taskNumber)){
                    sql="INSERT INTO t_first_check (org_id,task_id,env_temperature,production_time,check_time,slump,air_content,check_result,check_comment,check_person) VALUES "+
                            " ('"+orgId+"','"+taskNumber+"','"+envTemperature+"','"+productionTime+"','"+checkTime+"','"+slump+"','"+gasContent+"','"+checkResult+"','"+checkOpinion+"','"+CommData.username+"')";
                    String result= basicService("ExecuteSQL", getRequestBodyEx(sql));
                    return result;
                }else{
                    sql="UPDATE t_first_check SET env_temperature='"+envTemperature+"',production_time='"+productionTime+"',check_time='"+checkTime+"',slump='"+slump+"',air_content='"+gasContent+"',check_result='"+checkResult+"',check_comment='"+checkOpinion+"',check_person='"+CommData.username+"'"+
                            " where task_id='"+taskNumber+"'";
                    String result= basicService("ExecuteSQL", getRequestBodyEx(sql));
                    return result;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }

        }else{
            try {
                if(getSiteDetection(taskNumber)){
                    sql="INSERT INTO t_site_check (org_id,task_id,env_temperature,production_time,check_time,slump,air_content,check_result,check_comment,check_person) VALUES "+
                            " ('"+orgId+"','"+taskNumber+"','"+envTemperature+"','"+productionTime+"','"+checkTime+"','"+slump+"','"+gasContent+"','"+checkResult+"','"+checkOpinion+"','"+CommData.username+"')";
                    String result= basicService("ExecuteSQL", getRequestBodyEx(sql));
                    return result;
                }else{
                    sql="UPDATE t_site_check SET env_temperature='"+envTemperature+"',production_time='"+productionTime+"',check_time='"+checkTime+"',slump='"+slump+"',air_content='"+gasContent+"',check_result='"+checkResult+"',check_comment='"+checkOpinion+"',check_person='"+CommData.username+"'"+
                    " where task_id='"+taskNumber+"'";
                    String result= basicService("ExecuteSQL", getRequestBodyEx(sql));
                    return result;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
    }*/


    public String updateTestConfirm(int id, String option, String taskId, String checkResult, String checkOpinion, String confirmTime) {
        String sql;
        if (option.equals("firstConfirm")) {
            try {

                sql = "UPDATE t_first_check SET check_result='" + checkResult + "',check_comment='" + checkOpinion + "',super_confirm='" + CommData.username + "',confirm_time='" + confirmTime + "'" +
                        " where task_id='" + taskId + "' and id=" + id;
                String result = basicService("ExecuteSQL", getRequestBodyEx(sql));
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        } else {
            try {
                sql = "UPDATE t_site_check SET check_result='" + checkResult + "',check_comment='" + checkOpinion + "',super_confirm='" + CommData.username + "',confirm_time='" + confirmTime + "'" +
                        " where task_id='" + taskId + "'";
                String result = basicService("ExecuteSQL", getRequestBodyEx(sql));
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
    }


    public String abolishTask(String taskId, String abolishReason, String abolishTime) throws Exception {
       /* String sql;
        sql="UPDATE m_p_task SET abolish_reason='"+abolishReason+"',abolish_time='"+abolishTime+"',abolish_person='"+CommData.username+"',state=7 "+
                "  where task_id='"+taskNumber+"'";**/
        String result = basicService("AbolishTask", getAbolishRequestBody(abolishReason, taskId, CommData.sLabId, CommData.username));
        return result;


    }

    public String getMixStation() throws Exception {
        // String s=CommData.sLabId.substring(0,CommData.sLabId.length()-2);
        // if(CommData.sLabId.length()==9) {
        String sql;
        sql = "select org_id,org_name from Organization where org_id='" + CommData.sLabId + "'";
        return basicService("SQLDBEx", getRequestBody(sql));
     /*   }else{
            return "";
        }*/
    }


    public String getMixStationParentName(String orgId) throws Exception {
        String s = orgId;
        s = orgId.substring(0, s.length() - 2);
        if (s.length() > 1) {
            String sql;
            sql = "select org_name from Organization where org_id='" + s + "'";
            mixStationName = getMixStationParentName(s);
            return mixStationName + basicService("SQLDBEx", getRequestBody(sql));
        } else {
            return "";
        }
    }

    public String getMixMachine(String orgId) throws Exception {

        String sql;
        sql = "select device_id,name from Device where org_id='" + orgId + "' and device_type=4";
        return basicService("SQLDBEx", getRequestBody(sql));
    }

    public String getOptionPerson(String orgId) throws Exception {

        String sql;
        sql = "select name from Personnel where org_id='" + orgId + "' and duty='操作手'";
        return basicService("SQLDBEx", getRequestBody(sql));
    }

    public String getTaskId(String taskId) throws Exception {
        String sql;
        sql = "select * from m_p_task  where  task_id='" + taskId + "' and accept_time is null";
        String result = basicService("SQLDBEx", getRequestBody(sql));
        return result;
    }

    //现场签收
    public String getTransportInf(int id, String orgId, String taskId, String noticeId) throws Exception {
        String sql;
        sql = "SELECT a.project_name,a.depart_time,b.name,a.notice_id,a.position,a.slump,a.strength,a.truck_id," +
                "a.plan_volume,a.volume,a.total_car_count,a.sum_volume,a.memo,a.operator1,a.driver " +
                " FROM t_transport a INNER JOIN  Device b ON a.device_id=b.device_id " +
                " WHERE a.id=" + id + " and a.org_id='" + orgId + "' and a.task_id='" + taskId + "' and a.notice_id='" + noticeId + "' and a.confirm_person IS NULL";
        String result = basicService("SQLDBEx", getRequestBody(sql));
        return result;
    }

    public String SiteSign(int confirmResult, String arriveTime, int id) {
        String sql;
        sql = "UPDATE t_transport SET confirm_result=" + confirmResult + ", arrive_time='" + arriveTime + "',confirm_person='" + CommData.username + "'" +
                " WHERE id=" + id;
        String result = null;
        try {
            result = basicService("ExecuteSQL", getRequestBodyEx(sql));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //问题库
    public String getProblemBaseData(int riskCatId) throws Exception {
        String sql;
        sql = "SELECT TOP 50 a.check_date,(SELECT org_name FROM Organization WHERE org_id='" + CommData.sLabId.substring(0, 7) + "') as lab," +
                "(SELECT org_name FROM Organization WHERE org_id='" + CommData.sLabId.substring(0, 5) + "') as tender,a.risk_source,a.risks, " +
                "b.risk_cat_name,a.credit,a.charge_department,a.charge_person,a.improve_date,a.improve_method,a.deadline,a.improvement," +
                "a.super_department,a.supervisor,a.inspect_result,a.memo " +
                " FROM Risks a INNER JOIN RiskCategory b ON a.risk_cat_id=b.risk_cat_id WHERE  a.org_id='" + CommData.sLabId + "'";
        if (riskCatId != 0) {
            sql += "  and a.risk_cat_id=" + riskCatId;
        }
        return basicService("SQLDBEx", getRequestBody(sql));
    }

    public String getRiskCat() throws Exception {
        String sql;
        sql = "SELECT risk_cat_id,risk_cat_name FROM RiskCategory";
        return basicService("SQLDBEx", getRequestBody(sql));
    }
}
