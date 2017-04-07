package com.example.phobes.witnessassitant.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.phobes.witnessassitant.activity.DeleteFile;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.EntryCheckData;
import com.example.phobes.witnessassitant.model.FileUpload;
import com.example.phobes.witnessassitant.model.MeasurePointData;
import com.example.phobes.witnessassitant.model.PointItemData;
import com.example.phobes.witnessassitant.model.PointMeta;
import com.example.phobes.witnessassitant.model.SendTestValue;
import com.example.phobes.witnessassitant.model.SiteTestData;
import com.example.phobes.witnessassitant.model.SiteTestItemData;
import com.example.phobes.witnessassitant.model.TestObject;
import com.example.phobes.witnessassitant.model.UserInfo;
import com.example.phobes.witnessassitant.model.WitenessData;
import com.example.phobes.witnessassitant.model.WitenessDetail;
import com.example.phobes.witnessassitant.util.Configuration;
import com.example.phobes.witnessassitant.util.DatabaseHelper;
import com.example.phobes.witnessassitant.util.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by phobes on 2016/6/6.
 */
public class SqliteService {

    public final String TABLE_NAME_WITNESSDATA = "WitnessData";
    public final String TABLE_NAME_WITNESSTASK = "WitnessTask";
    public final String TABLE_NAME_WITNESSDETAIL = "WitnessDetail";
    public final String TABLE_NAME_ENTRY_CHECK = "EntryCheckData";
    public final String TABLE_NAME_MOBILE_USER = "MobileUser";
    public final String TABLE_NAME_TEST_OBJECT = "TestObject";
    public static final String NOT_EXIT_USER_ID = "userId not exit";
    public final String TABLE_NAME_TEST_TASK="TestTask";
    public final String TABLE_NAME_SITE_TEST_DATA="site_test_data";
    public final String TABLE_NAME_SITE_TEST_ITEM="site_test_item";
    public final String TABLE_NAME_SITE_TEST_DETAIL ="site_test_detail";
    public final String TABLE_NAME_SITE_TESTDATA="SiteTestData";
    public final String TABLE_NAME_SITE_TESTDESC="SiteTestDesc";

    private SQLiteDatabase db = null;

    public SqliteService(Context context){
        DatabaseHelper databaseHelper = new DatabaseHelper(context, Configuration.DB_NAME, Configuration.DB_VERSION);
        db = databaseHelper.getReadableDatabase();
    }

    public boolean isExitTestObject() {
        boolean isExit = true;
        String sql = "SELECT * FROM TestObject";
        Cursor cursor = db.rawQuery(sql, null);
        int i = 0;
        while (cursor.moveToNext()) {
            i++;
        }
        if (i <= 1) {
            isExit = false;
        }
        cursor.close();
        return isExit;
    }

    public List<TestObject.TestObjectItem> getTestObjects() {
        return getTestObjects("select * from TestObject where object_id > 100");
    }

    public List<TestObject.TestObjectItem> getTestObjectsByName(String objectName) {
        return getTestObjects("select * from TestObject where object_name like '%" + objectName + "%'");
    }

    public String getWorkflow(int objectId) {
        String workflow = null;
        String sql = "select workflow from TestObject where object_id=" + objectId;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            workflow = cursor.getString(0);
        }
        cursor.close();
        return workflow;
    }

    public List<TestObject.TestObjectItem> getTestObjects(int groupId) {
        if (groupId == -1) {
            return getTestObjects();
        }
        return getTestObjects("select * from TestObject" + " where group_id=" + groupId);
    }

    public List<TestObject.TestObjectItem> getTestObjects(String sql) {
        List<TestObject.TestObjectItem> items = new ArrayList<TestObject.TestObjectItem>();
        //查询获得游标
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            items.add(new TestObject.TestObjectItem(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3)));
        }
        cursor.close();
        return items;
    }

    public void insertTestObjects() {
        String insertsql = "INSERT INTO TestObject (object_id, object_name,group_id,workflow) VALUES (?, ?,?,?)";
        SQLiteStatement stmt = db.compileStatement(insertsql);

        for (int i = 0; i < TestObject.ITEMS.size(); i++) {
            stmt.bindLong(1, TestObject.ITEMS.get(i).object_id);
            stmt.bindString(2, TestObject.ITEMS.get(i).name);
            stmt.bindLong(3, TestObject.ITEMS.get(i).group_id);
            stmt.bindString(4, TestObject.ITEMS.get(i).workflow);
            stmt.execute();
            stmt.clearBindings();
        }
    }

    public boolean checkUser(String userId, String password) {
        boolean isExit = true;
        String sql = "select org_id, person_id,user_name,name,org_name,org_type,password,duty" +
                " from " + TABLE_NAME_MOBILE_USER + " where name='" + userId + "' and password= '" + password + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) {
            isExit = false;
        } else {
            while (cursor.moveToNext()) {
                CommData.sLabId = cursor.getString(0);
                CommData.username = cursor.getString(2);
                CommData.name = cursor.getString(3);
                CommData.orgName = cursor.getString(4);
                CommData.orgType = cursor.getString(5);
                CommData.duty=cursor.getString(6);
            }
        }
        cursor.close();
        return isExit;
    }

    public String getUserName(String userId) {
        String username = NOT_EXIT_USER_ID;
        String sql = "select user_name from " + TABLE_NAME_MOBILE_USER + " where name='" + userId + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) {
            return NOT_EXIT_USER_ID;
        } else {
            while (cursor.moveToNext()) {
                username = cursor.getString(0);
            }
        }
        cursor.close();
        return username;
    }

    public boolean isExitUser(UserInfo user) {
        boolean isExit = true;
        String sql = "select org_id, person_id,user_name,name,org_name,org_type,password" +
                " from " + TABLE_NAME_MOBILE_USER + " where person_id=" + user.getPersonId();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) {
            isExit = false;
        }
        cursor.close();
        return isExit;
    }

    /*
    *用户登录信息保存
     */
    public void saveUser(UserInfo user) {

        String insertsql = "INSERT INTO " + TABLE_NAME_MOBILE_USER +
                " (org_id, person_id,user_name,name,org_name,org_type,password,duty) VALUES (?,?,?,?,?,?,?,?)";
        SQLiteStatement stmt = db.compileStatement(insertsql);
        stmt.bindLong(1, user.getOrgId());
        stmt.bindLong(2, user.getPersonId());
        stmt.bindString(3, user.getUsername());
        stmt.bindString(4, user.getName());
        stmt.bindString(5, user.getOrgName());
        stmt.bindString(6, user.getOrgType());
        stmt.bindString(7, user.getPassword());
        stmt.bindString(8, user.getDuty());
        stmt.execute();
        stmt.clearBindings();
    }

    public boolean isExitWiteness() {
        boolean isExit = true;
        String sql = "SELECT * FROM WitnessData";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) {
            isExit = false;
        }
        cursor.close();
        return isExit;
    }

    public void createWiteness(WitenessData witeness) {
        String insertsql = "INSERT INTO WitnessData (witness_id, object_id,sample_id) VALUES (?,?,?)";

        SQLiteStatement stmt = db.compileStatement(insertsql);
        stmt.bindLong(1, witeness.getWitness_id());
        stmt.bindLong(2, witeness.getObject_id());
        stmt.bindString(3, witeness.getSample_id());
        stmt.execute();
        stmt.clearBindings();

    }

    public void saveWitenessList(List<WitenessData> witenessDatas) {
        boolean isTrue=true;
        String querySql="select witness_id from WitnessData";
        Cursor cursor1 = db.rawQuery(querySql, null);
        while (cursor1.moveToNext()){
                isTrue=false;
                for(int i=0;i<witenessDatas.size();i++){
                    if(cursor1.getInt(0)==witenessDatas.get(i).getWitness_id()){
                        isTrue=true;
                        break;
                    }
                }
                if(!isTrue) {
                    LogWriter.log(CommData.INFO, "saveWitnessList");
                    String[] args = {String.valueOf(cursor1.getInt(0)),"0"};
                    if(db.delete(TABLE_NAME_WITNESSDATA, "witness_id=? and uploaded=?", args)>0) {
                        db.delete(TABLE_NAME_WITNESSDETAIL, "witness_id=?", new String[]{args[0]});
                    }
                }
        }
        for (int i = 0; i < witenessDatas.size(); i++) {
            if (!isExitWiteness(witenessDatas.get(i))) {
                //insertWiteness(db, witenessDatas.get(i));
            }else{  //yang  witness_type   为0是取样见证  为2是试验见证 为3
               if(witenessDatas.get(i).getWitnessType()==2) {
                   String sql = "select witness_type from WitnessData where witness_id = " + witenessDatas.get(i).getWitness_id();
                   Cursor cursor = db.rawQuery(sql, null);
                   while (cursor.moveToNext()) {
                       if (cursor.getInt(0) != 3) {
                           ContentValues cv = new ContentValues();
                           cv.put("witness_type", 2);
                           String[] args = {String.valueOf(witenessDatas.get(i).getWitness_id())};
                           LogWriter.log(CommData.INFO, "saveWitness1");
                           db.update(TABLE_NAME_WITNESSDATA, cv, "witness_id=?", args);
                       }
                   }
               }
            }
        }
    }

    public boolean saveWiteness(WitenessData witenessData) {
        if (isExitWiteness(witenessData)) {
            return updateWiteness(witenessData);
        } else {
            return insertWiteness(witenessData);
        }
    }

    public void saveEntryCheckList( List<EntryCheckData> entryCheckDatas) {
        for (int i = 0; i < entryCheckDatas.size(); i++) {
            if (!isExitEntryCheck(entryCheckDatas.get(i))) {
                insertEntryCheck(entryCheckDatas.get(i));
            }
        }
    }

    public boolean saveEntryCheck( EntryCheckData entryCheck) {
        if (isExitEntryCheck(entryCheck)) {
            return updateEntryCheck(entryCheck);
        } else {
            return insertEntryCheck(entryCheck);
        }
    }

    public boolean SaveTestWitness(WitenessData witenessData) {
        ContentValues cv = new ContentValues();
        cv.put("witness_id", witenessData.getWitness_id());
        cv.put("test_item", witenessData.getTest_item());
        cv.put("test_image", witenessData.getTest_image());
        cv.put("test_comment", witenessData.getTest_comment());
        cv.put("test_latitude", witenessData.getTest_latitude());
        cv.put("test_longitude", witenessData.getTest_longitude());
        cv.put("test_time", witenessData.getTest_time() );
        return (db.insert(TABLE_NAME_WITNESSTASK, null, cv) !=-1);
    }

    public boolean insertWiteness(WitenessData witenessData) {
        ContentValues cv = new ContentValues();

        cv.put("witness_id", witenessData.getWitness_id());
        cv.put("object_id", witenessData.getObject_id());
        cv.put("sample_id", witenessData.getSample_id());
        cv.put("entry_id", witenessData.getEntry_id());
        cv.put("sample_person", witenessData.getSample_person());
        cv.put("sample_person_name", witenessData.getSample_person_name());
        cv.put("sample_org_id", witenessData.getSample_org_id());
        cv.put("sample_org_name", witenessData.getSample_org_name());
        cv.put("sample_longitude", witenessData.getSample_longitude());
        cv.put("sample_latitude", witenessData.getSample_latitude());
        cv.put("sample_image", witenessData.getSample_image());
        cv.put("sample_time", witenessData.getSample_time());
        cv.put("witness_person", witenessData.getWitness_person());

        cv.put("witness_person_name", witenessData.getWitness_person_name());
        cv.put("witness_org_id", witenessData.getWitness_org_id());
        cv.put("witness_org_name", witenessData.getWitness_org_name());
        cv.put("witness_longitude", witenessData.getWitness_longitude());
        cv.put("witness_latitude", witenessData.getWitness_latitude());
        cv.put("witness_image", witenessData.getWitness_image());
        cv.put("witness_time", witenessData.getWitness_time());
        cv.put("apply_time", witenessData.getApply_time());
        cv.put("data_id", witenessData.getData_id());
        cv.put("object_name", witenessData.getObject_name());
        cv.put("uploaded", witenessData.getUploaded());
        cv.put("apply_from", witenessData.getApply_from());
        cv.put("comment", witenessData.getComment());
        cv.put("witness_type",witenessData.getWitnessType());
        cv.put("batch_id",witenessData.getBatch_id());
        cv.put("test_items",witenessData.getTest_items());
        cv.put("items_count",witenessData.getTestItems());

        String[] args = {String.valueOf(witenessData.getWitness_id())};
        return (db.insert(TABLE_NAME_WITNESSDATA, null, cv) !=-1);
    }

    public boolean updateWitenessTask(WitenessData witenessData) {
        ContentValues cv = new ContentValues();

        cv.put("upload_time", DateUtil.DateTimeToString(new Date()));

        String[] args = {String.valueOf(witenessData.getWitness_id()),witenessData.getTest_item()};
        LogWriter.log(CommData.ERROR, "updateWiteness");
        return (db.update(TABLE_NAME_WITNESSTASK, cv, "witness_id=? and test_item=?", args) != -1);
    }

    public boolean updateWiteness(WitenessData witenessData) {
        ContentValues cv = new ContentValues();

        cv.put("sample_longitude", witenessData.getSample_longitude());
        cv.put("sample_latitude", witenessData.getSample_latitude());
        cv.put("sample_image", witenessData.getSample_image());
        cv.put("sample_time", witenessData.getSample_time());
        cv.put("witness_longitude", witenessData.getWitness_longitude());
        cv.put("witness_latitude", witenessData.getWitness_latitude());
        cv.put("witness_image", witenessData.getWitness_image());
        cv.put("witness_time", witenessData.getWitness_time());

        cv.put("test_longitude", witenessData.getTest_longitude());
        cv.put("test_latitude", witenessData.getTest_latitude());
        cv.put("test_image", witenessData.getTest_image());
        cv.put("test_time", witenessData.getTest_time());
        cv.put("test_comment", witenessData.getTest_comment());

        cv.put("object_name", witenessData.getObject_name());
        cv.put("uploaded", witenessData.getUploaded());
        cv.put("apply_from", witenessData.getApply_from());
        cv.put("comment", witenessData.getComment());
        cv.put("witness_type",witenessData.getWitnessType());
        cv.put("test_items",witenessData.getTest_items());
        String[] args = {String.valueOf(witenessData.getWitness_id())};
        LogWriter.log(CommData.ERROR, "updateWiteness" );
        return (db.update(TABLE_NAME_WITNESSDATA, cv, "witness_id=?", args)!=-1);
      /*  if(i!=0){
            String sql = "select witness_type from " + TABLE_NAME_WITNESSDATA + " where witness_id=" + witenessData.getWitness_id();
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                int l = cursor.getInt(0);
                if (l == 3)
                    break;
            }
        }*/
    }

    public void deleteWitness(int witnessId) {
        String[] args = {String.valueOf(witnessId)};
        LogWriter.log(CommData.INFO, "deleteWitness：" +witnessId);
        if(db.delete(TABLE_NAME_WITNESSDETAIL, "witness_id=?", args)>0) {
            db.delete(TABLE_NAME_WITNESSDATA, "witness_id=?", args);
        }
    }

    public String[] getWitnessedItem(int WitnessId,String[] sItems) {
        int i = 0,nPos;
        String[] sNewItems;
        String sTemp;
        List<String> strList = new ArrayList<String>();

        for (i = 0; i < sItems.length;i++)
            strList.add(sItems[i]);
        //查询保存到本地的item信息
        String sql = "select test_item from " + TABLE_NAME_WITNESSTASK + " where witness_id=" + WitnessId;
        Cursor cursor = db.rawQuery(sql, null);
        //int nRows = cursor.getCount();
        while (cursor.moveToNext()) {
            //查询保存的item下标为0的字符串，从数据库中和查询出的两相对比，有责删除，无责添加
            sTemp = cursor.getString(0);
            //index(stem)如果获取到返回当前的下标，如果所查询的内容不在集合里返回-1
            nPos = strList.indexOf(sTemp);
            if (nPos >= 0)
                strList.remove(nPos);
        }
        cursor.close();
        sItems = new String[strList.size()];
        for (i = 0; i < strList.size();i++)
            sItems[i] = strList.get(i);
        return sItems;
    }

    public int getWitnessIdByEntryId(int entryId) {
        int witnessId = -1;
        String sql = "select witness_id from " + TABLE_NAME_WITNESSDATA + " where entry_id=" + entryId;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            witnessId = cursor.getInt(0);
            break;
        }
        cursor.close();
        return witnessId;
    }

    public boolean insertEntryCheck(EntryCheckData entryCheckData) {
        ContentValues cv = new ContentValues();
        cv.put("entry_id", entryCheckData.getEntryId());
        cv.put("org_id", entryCheckData.getOrgId());
        cv.put("object_id", entryCheckData.getObjectId());
        cv.put("batch_id", entryCheckData.getBatchId());
        cv.put("output_date", entryCheckData.getOutputDate());
        cv.put("quantity", entryCheckData.getQuantity());//if (entryCheckData.getQuantity() != 0)
        cv.put("strength", entryCheckData.getStrength());
        cv.put("sample_spec", entryCheckData.getSampleSpec());
        cv.put("sample_size", entryCheckData.getSampleSize());
        cv.put("factory", entryCheckData.getFactory());
        cv.put("product_name", entryCheckData.getProductName());
        cv.put("entry_date", entryCheckData.getEntryDate());
        cv.put("entrust_id", entryCheckData.getEntrustId());
        cv.put("report_id", entryCheckData.getReportId());
        cv.put("accepted", entryCheckData.getAccepted());
        cv.put("lab_comment", entryCheckData.getLabComment());
        cv.put("lab_check_date", entryCheckData.getLabCheckDate());
        cv.put("super_comment", entryCheckData.getSuperComment());
        cv.put("super_check_date", entryCheckData.getSuperCheckDate());

        if (db.insert(TABLE_NAME_ENTRY_CHECK, null, cv) == -1) {
            Log.i("db error", "insert");
            return false;
        }
        return true;
    }


    public boolean updateEntryCheck(EntryCheckData entryCheckData) {
        ContentValues cv = new ContentValues();
        if (CommData.orgType.equals("监理试验室")) {
            cv.put("accepted", entryCheckData.getAccepted());
            cv.put("super_person", CommData.username);
            cv.put("super_comment", entryCheckData.getSuperComment());
            cv.put("super_check_date", entryCheckData.getSuperCheckDate());
        } else {
            cv.put("witness", entryCheckData.getWitness());
            cv.put("lab_person", CommData.username);
            cv.put("lab_comment", entryCheckData.getLabComment());
            cv.put("lab_check_date", entryCheckData.getLabCheckDate());
        }
        String[] args = {String.valueOf(entryCheckData.getEntryId())};
        if(db.update(TABLE_NAME_ENTRY_CHECK, cv, "entry_id=?", args)==-1) {
            return false;
        }
        return true;
    }

    public boolean isExitWiteness(int witnessId) {
        boolean isExit = true;
        String sql = "select * from WitnessData where witness_id = " + witnessId;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) {
            isExit = false;
        }
        cursor.close();
        return isExit;
    }

    public boolean isExitWiteness(WitenessData witenessData) {
        boolean isExit = true;
        String sql = "select * from WitnessData where witness_id = " + witenessData.getWitness_id();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) {
            isExit = false;
        }
        cursor.close();
        return isExit;
    }

    public boolean isExitEntryCheck(int entryId) {
        boolean isExit = true;
        String sql = "select entry_id from " + TABLE_NAME_ENTRY_CHECK +
                " where entry_id = " + entryId;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) {
            isExit = false;
        }
        cursor.close();
        return isExit;
    }

    public boolean isExitEntryCheck() {
        boolean isExit = true;
        String sql = "select entry_id from " + TABLE_NAME_ENTRY_CHECK;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) {
            isExit = false;
        }
        cursor.close();
        return isExit;
    }

    public boolean isExitEntryCheck(EntryCheckData entryCheckData) {
        boolean isExit = true;
        String sql = "select entry_id from " + TABLE_NAME_ENTRY_CHECK +
                " where entry_id = " + entryCheckData.getEntryId();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) {
            isExit = false;
        }
        cursor.close();
        return isExit;
    }

    public WitenessData getUnUploadedItem(int witnessId) {
        WitenessData witenessData = new WitenessData();
        String sSql;

         sSql = "SELECT witness_id,test_image,test_longitude,test_latitude,test_comment,test_time,test_item " +
                    " FROM "+TABLE_NAME_WITNESSTASK+"  where  upload_time IS NULL and witness_id = " + witnessId;
        Cursor cursor=null;
        try{
            cursor = db.rawQuery(sSql, null);
        }catch (Exception e){
            e.printStackTrace();
        }
        witenessData.setWitness_id(0);
        if (cursor.moveToNext()) {
                witenessData.setWitness_id(witnessId);
                witenessData.setTest_image(cursor.getString(1));
                witenessData.setTest_longitude(cursor.getDouble(2));
                witenessData.setTest_latitude(cursor.getDouble(3));
                witenessData.setTest_comment(cursor.getString(4));
                witenessData.setTest_time(cursor.getString(5));
                witenessData.setTest_item(cursor.getString(6));
        }
        cursor.close();
        return witenessData;
    }

    public WitenessData getWiteness(int witnessId) {
        WitenessData witenessData = new WitenessData();
        String sSql;
        if (CommData.orgType.equals("监理试验室")) {
            sSql = "SELECT witness_id,object_id, object_name,witness_image,witness_longitude,witness_latitude,comment,witness_time," +
                    "test_image,test_longitude,test_latitude,test_comment,test_time,test_items FROM WitnessData  where  witness_time IS NULL and uploaded = 0 and witness_id = " + witnessId;
        } else {
            sSql = "SELECT witness_id,object_id,object_name,sample_image,sample_longitude,sample_latitude,sample_time FROM WitnessData  where  sample_time IS NULL and uploaded = 0 and witness_id=" + witnessId;
        }
        Cursor cursor=null;
        try{
             cursor = db.rawQuery(sSql, null);
        }catch (Exception e){
            e.printStackTrace();
        }

        while (cursor.moveToNext()) {
            witenessData.setWitness_id(cursor.getInt(0));
            witenessData.setObject_id(cursor.getInt(1));
            witenessData.setObject_name(cursor.getString(2));
            if (CommData.orgType.equals("监理试验室")) {
                witenessData.setWitness_image(cursor.getString(3));
                witenessData.setWitness_longitude(cursor.getDouble(4));
                witenessData.setWitness_latitude(cursor.getDouble(5));
                witenessData.setComment(cursor.getString(6));
                witenessData.setWitness_time(cursor.getString(6));
                witenessData.setTest_image(cursor.getString(8));
                witenessData.setTest_longitude(cursor.getDouble(9));
                witenessData.setTest_latitude(cursor.getDouble(10));
                witenessData.setTest_comment(cursor.getString(11));
                witenessData.setTest_time(cursor.getString(12));
                witenessData.setTest_items(cursor.getString(13));
            } else {
                witenessData.setSample_image(cursor.getString(3));
                witenessData.setSample_longitude(cursor.getDouble(4));
                witenessData.setSample_latitude(cursor.getDouble(5));
                witenessData.setSample_time(cursor.getString(6));
            }
        }
        cursor.close();
        return witenessData;
    }

    public WitenessData getWitenessWithPic(int witnessId) {
        WitenessData witenessData = new WitenessData();
        String sSql;
        if (CommData.orgType.equals("监理试验室")) {
//            sSql = "SELECT * FROM WitnessData  where (witness_time <>" + "" + "and witness_time IS NOT NULL) and uploaded = 0";
            sSql = "SELECT witness_id,object_id, object_name,witness_image,witness_longitude,witness_latitude,comment,witness_time," +
                    "test_image,test_longitude,test_latitude,test_comment,test_time ,witness_image,test_items FROM WitnessData  where  uploaded = 0 and witness_id = " + witnessId;
        } else {
            sSql = "SELECT witness_id,object_id,object_name,sample_image,sample_longitude,sample_latitude,sample_time, sample_image FROM WitnessData  where   uploaded = 0 and witness_id=" + witnessId;
        }
        Cursor cursor = db.rawQuery(sSql, null);
        while (cursor.moveToNext()) {
            witenessData.setWitness_id(cursor.getInt(0));
            witenessData.setObject_id(cursor.getInt(1));
            witenessData.setObject_name(cursor.getString(2));
            if (CommData.orgType.equals("监理试验室")) {
                witenessData.setWitness_image(cursor.getString(3));
                witenessData.setWitness_longitude(cursor.getDouble(4));
                witenessData.setWitness_latitude(cursor.getDouble(5));
                witenessData.setComment(cursor.getString(6));
                witenessData.setWitness_time(cursor.getString(7));
                witenessData.setTest_image(cursor.getString(8));
                witenessData.setTest_longitude(cursor.getDouble(9));
                witenessData.setTest_latitude(cursor.getDouble(10));
                witenessData.setTest_comment(cursor.getString(11));
                witenessData.setTest_time(cursor.getString(12));
                witenessData.setWitness_image(cursor.getString(13));
                witenessData.setTest_items(cursor.getString(14));
            } else {
                witenessData.setSample_image(cursor.getString(3));
                witenessData.setSample_longitude(cursor.getDouble(4));
                witenessData.setSample_latitude(cursor.getDouble(5));
                witenessData.setSample_time(cursor.getString(6));
                witenessData.setWitness_image(cursor.getString(7));
            }
        }
        cursor.close();
        return witenessData;
    }

    public boolean saveWitnessDetails(List<WitenessDetail> witenessDetails) {
        String[] args = {String.valueOf( witenessDetails.get(0).getWitness_id())};
        db.delete(TABLE_NAME_WITNESSDETAIL, "witness_id=?", args); //yang  add 2016/10/18
        for (int i = 0; i < witenessDetails.size(); i++) {
            ContentValues cv = new ContentValues();
            cv.put("witness_id", witenessDetails.get(i).getWitness_id());
            cv.put("meta_id", witenessDetails.get(i).getMeta_id());
            cv.put("value", witenessDetails.get(i).getValue());
//            cv.put("edit_time", DateUtil.DateToString(witenessDetails.get(i).getEdit_time()));
            cv.put("meta_name", witenessDetails.get(i).getMeta_name());
            db.insert(TABLE_NAME_WITNESSDETAIL, null, cv);
        }
        return true;
    }

    public boolean isExitWitenessDetail(int witnessId) {
        String sSql;//yang eidt
        if( CommData.witnessType.equals("testWitness")){
             sSql = "Select  * from " + TABLE_NAME_WITNESSDETAIL
                    + " a INNER JOIN WitnessData b  on a.witness_id=b.witness_id  where a.witness_id=" + witnessId
             +" and b.witness_type=3";
        }else{
            sSql = "Select  * from " + TABLE_NAME_WITNESSDETAIL
                    + " a INNER JOIN WitnessData b  on a.witness_id=b.witness_id  where a.witness_id=" + witnessId
                    +" and b.witness_type=1";
        }
        try {
                Cursor cursor = db.rawQuery(sSql, null);
                if (cursor.getCount() == 0) {
                    cursor.close();
                    return false;
                } else {
                    cursor.close();
                    return true;
                }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }



    }

    public List<WitenessDetail> getWitnessDetails(int witnessId) {
        List<WitenessDetail> witenessDetails = new ArrayList<WitenessDetail>();
        String sSql = "Select  witness_id, meta_id, meta_name, value from " + TABLE_NAME_WITNESSDETAIL+" where witness_id="+witnessId;
        Cursor cursor = db.rawQuery(sSql, null);
        while (cursor.moveToNext()) {
            WitenessDetail witenessDetail = new WitenessDetail();
            witenessDetail.setWitness_id(cursor.getInt(0));
            witenessDetail.setMeta_id(cursor.getInt(1));
            witenessDetail.setMeta_name(cursor.getString(2));
            witenessDetail.setValue(cursor.getString(3));
            witenessDetails.add(witenessDetail);
        }
        cursor.close();
        return witenessDetails;
    }

    public List<WitenessData> getWiteness(String isDown,String type) {
        List<WitenessData> witenessDatas = new ArrayList<WitenessData>();
        String sSql;
        //yang  根据不同角色，需要不同限制条件--------
        if(isDown.equals("downLoad")) {
                sSql = "Select  witness_id,sample_id, object_name, apply_time,sample_org_name,object_id from WitnessData where uploaded=0 and witness_type=1";
        }else{
            if(type.equals("sample")) {
                sSql = "Select  witness_id,sample_id, object_name, apply_time,sample_org_name,object_id from WitnessData where uploaded=0 and witness_type=1";
            }else{
                sSql = "Select  witness_id,sample_id, object_name, apply_time,sample_org_name,object_id from WitnessData where uploaded=0 and witness_type=3";
            }
        }
        Cursor cursor = db.rawQuery(sSql, null);

        while (cursor.moveToNext()) {
            WitenessData witenessData = new WitenessData();
            witenessData.setWitness_id(cursor.getInt(0));
            witenessData.setSample_id(cursor.getString(1));
            witenessData.setObject_name(cursor.getString(2));
            witenessData.setApply_time(cursor.getString(3));
            witenessData.setSample_org_name(cursor.getString(4));
            witenessData.setObject_id(cursor.getInt(5));
            witenessDatas.add(witenessData);
        }
        cursor.close();
        return witenessDatas;
    }

    public List<WitenessData> getTestWiteness() {
        List<WitenessData> witenessDatas = new ArrayList<WitenessData>();
        String sSql;
       // sSql = "Select  witness_id,sample_id, object_name, apply_time,sample_org_name,object_id from WitnessData where uploaded != 0 and uploaded !=3 ";
        sSql = "Select  witness_id,sample_id, object_name, apply_time,sample_org_name,object_id from WitnessData where uploaded=0 and witness_type=3";
       // sSql = "Select  witness_type from WitnessData where uploaded=0 ";

        Cursor cursor = db.rawQuery(sSql, null);

        while (cursor.moveToNext()) {
            try{
                WitenessData witenessData = new WitenessData();
                witenessData.setWitness_id(cursor.getInt(0));
                witenessData.setSample_id(cursor.getString(1));
                witenessData.setObject_name(cursor.getString(2));
                witenessData.setApply_time(cursor.getString(3));
                witenessData.setSample_org_name(cursor.getString(4));
                witenessData.setObject_id(cursor.getInt(5));
                witenessDatas.add(witenessData);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        cursor.close();
        return witenessDatas;
    }

    public List<WitenessData> getWitenessByGroupId(int groupId,String type) {
        List<WitenessData> witenessDatas = new ArrayList<WitenessData>();
        String sSql;
        if(type.equals("test")) {
            sSql = "Select  witness_id,sample_id, object_name, apply_time,sample_org_name,object_id from WitnessData " +
                    "where uploaded=0 and witness_type=3 and object_id in (select object_id from " + TABLE_NAME_TEST_OBJECT + " where group_id=" + groupId + ")";
        }else{
            sSql = "Select  witness_id,sample_id, object_name, apply_time,sample_org_name,object_id from WitnessData " +
                    "where uploaded=0 and witness_type=1 and object_id in (select object_id from " + TABLE_NAME_TEST_OBJECT + " where group_id=" + groupId + ")";
        }
            Cursor cursor = db.rawQuery(sSql, null);

        while (cursor.moveToNext()) {
            WitenessData witenessData = new WitenessData();
            witenessData.setWitness_id(cursor.getInt(0));
            witenessData.setSample_id(cursor.getString(1));
            witenessData.setObject_name(cursor.getString(2));
            witenessData.setApply_time(cursor.getString(3));
            witenessData.setSample_org_name(cursor.getString(4));
            witenessData.setObject_id(cursor.getInt(5));
            witenessDatas.add(witenessData);
        }
        cursor.close();
        return witenessDatas;
    }

    public List<WitenessData> getWitnessHisory(String type) {
        List<WitenessData> witenessDatas = new ArrayList<WitenessData>();
        String sSql="";
        if (CommData.orgType.equals("监理试验室")) {
            if(type.equals("sample")){
                sSql = "SELECT witness_id,sample_id, object_id, object_name, witness_time,batch_id FROM WitnessData  where  uploaded = 1 ORDER BY witness_time ASC";
            }else{
                sSql = "SELECT witness_id,sample_id, object_id, object_name, test_time,batch_id FROM WitnessData  where  uploaded = 3 ORDER BY test_time ASC";
            }

        } else {
            sSql = "SELECT witness_id,sample_id, object_id, object_name, sample_time,batch_id  FROM WitnessData  where  uploaded = 1 ORDER BY sample_time ASC";
        }
        Cursor cursor = db.rawQuery(sSql, null);
        while (cursor.moveToNext()) {
            WitenessData witenessData = new WitenessData();
            witenessData.setWitness_id(cursor.getInt(0));
            witenessData.setSample_id(cursor.getString(1));
            witenessData.setObject_id(cursor.getInt(2));
            witenessData.setObject_name(cursor.getString(3));
            witenessData.setBatch_id(cursor.getString(5));
            if (CommData.orgType.equals("监理试验室")) {
                if(type.equals("sample")) {
                    witenessData.setWitness_time(cursor.getString(4));
                }else{
                    witenessData.setTest_time(cursor.getString(4));
                }
            } else {
                witenessData.setSample_time(cursor.getString(4));
            }
            witenessDatas.add(witenessData);
        }
        cursor.close();
        return witenessDatas;
    }

    public List<EntryCheckData> getEntryCheckHisory() {
        List<EntryCheckData> entryCheckDataArrayLists = new ArrayList<EntryCheckData>();
        String sSql;
        if (CommData.orgType.equals("监理试验室")) {
            sSql = "SELECT entry_id,object_id, product_name,super_comment,super_check_date witness_time FROM " + TABLE_NAME_ENTRY_CHECK +
                    "  where    uploaded = 1 ORDER BY super_check_date ASC";
        } else {
            sSql = "SELECT entry_id,object_id, product_name,lab_comment,lab_check_date  FROM " + TABLE_NAME_ENTRY_CHECK +
                    "  where  uploaded = 1 ORDER BY lab_check_date ASC";
        }
        Cursor cursor = db.rawQuery(sSql, null);
        while (cursor.moveToNext()) {
            EntryCheckData entryCheckData = new EntryCheckData();
            entryCheckData.setEntryId(cursor.getInt(0));
            entryCheckData.setObjectId(cursor.getInt(1));
            entryCheckData.setProductName(cursor.getString(2));
            if (CommData.orgType.equals("监理试验室")) {
                entryCheckData.setSuperComment(cursor.getString(3));
                entryCheckData.setSuperCheckDate(cursor.getString(4));
            } else {
                entryCheckData.setLabComment(cursor.getString(3));
                entryCheckData.setLabCheckDate(cursor.getString(4));
            }
            entryCheckDataArrayLists.add(entryCheckData);
        }
        cursor.close();
        return entryCheckDataArrayLists;
    }

    public List<EntryCheckData> getEntryCheckDatas(int groupId) {
        List<EntryCheckData> entryCheckDataArrayList = new ArrayList<EntryCheckData>();
        String sql;
        sql = "select entry_id, object_id,product_name,  batch_id, quantity, entry_date"
                + " FROM " + TABLE_NAME_ENTRY_CHECK +
                " where uploaded=0 and object_id in (select object_id from " + TABLE_NAME_TEST_OBJECT + " where group_id=" + groupId + ")";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            EntryCheckData entryCheckDataTask = new EntryCheckData();
            entryCheckDataTask.setEntryId(cursor.getInt(0));
            entryCheckDataTask.setObjectId(cursor.getInt(1));
            entryCheckDataTask.setProductName(cursor.getString(2));
            entryCheckDataTask.setBatchId(cursor.getString(3));
            if(cursor.getString(4)!=null)entryCheckDataTask.setQuantity(cursor.getString(4));
            entryCheckDataTask.setEntryDate(cursor.getString(5));
            entryCheckDataArrayList.add(entryCheckDataTask);
        }
        cursor.close();
        return entryCheckDataArrayList;
    }

    public List<EntryCheckData> getEntryCheckDatas() {
        List<EntryCheckData> entryCheckDataArrayList = new ArrayList<EntryCheckData>();
        String sql;
        sql = "select entry_id, object_id,product_name,  batch_id, quantity, entry_date"
                + " FROM " + TABLE_NAME_ENTRY_CHECK + " where uploaded=0  ORDER BY entry_date DESC";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            EntryCheckData entryCheckDataTask = new EntryCheckData();
            entryCheckDataTask.setEntryId(cursor.getInt(0));
            entryCheckDataTask.setObjectId(cursor.getInt(1));
            entryCheckDataTask.setProductName(cursor.getString(2));
            entryCheckDataTask.setBatchId(cursor.getString(3));
            if(cursor.getString(4)!=null)entryCheckDataTask.setQuantity(cursor.getString(4));
            entryCheckDataTask.setEntryDate(cursor.getString(5));
            entryCheckDataArrayList.add(entryCheckDataTask);
        }
        cursor.close();
        return entryCheckDataArrayList;
    }

    public EntryCheckData getEntryCheckTaskDetail(int entryId) {
        EntryCheckData entryCheckData = new EntryCheckData();
        String sql = "select  object_id,product_name,output_date, batch_id, quantity," +
                " entry_date,strength,sample_spec,lab_comment,lab_check_date,super_comment,super_check_date,sample_size from "
                + TABLE_NAME_ENTRY_CHECK + " where entry_id=" + entryId;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            entryCheckData.setObjectId(cursor.getInt(0));
            entryCheckData.setProductName(cursor.getString(1));
            entryCheckData.setOutputDate(cursor.getString(2));
            entryCheckData.setBatchId(cursor.getString(3));
            if (cursor.getString(4) != null)
                entryCheckData.setQuantity(cursor.getString(4));
            entryCheckData.setEntryDate(cursor.getString(5));
            entryCheckData.setStrength(cursor.getString(6));
            entryCheckData.setSampleSpec(cursor.getString(7));
            entryCheckData.setLabComment(cursor.getString(8));
            entryCheckData.setLabCheckDate(cursor.getString(9));
            entryCheckData.setSuperComment(cursor.getString(10));
            entryCheckData.setSuperCheckDate(cursor.getString(11));
            entryCheckData.setSampleSize(cursor.getString(12));
        }
        cursor.close();
        return entryCheckData;
    }

    public EntryCheckData getEntryCheck(int entryId) {
        EntryCheckData entryCheckData = new EntryCheckData();
        String sql = "select  object_id,product_name,output_date, batch_id, quantity," +
                " entry_date,strength,sample_spec,sample_size from "
                + TABLE_NAME_ENTRY_CHECK + " where entry_id=" + entryId + " and uploaded=0";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            entryCheckData.setObjectId(cursor.getInt(0));
            entryCheckData.setProductName(cursor.getString(1));
            entryCheckData.setOutputDate(cursor.getString(2));
            entryCheckData.setBatchId(cursor.getString(3));
            if (cursor.getString(4) != null)
                entryCheckData.setQuantity(cursor.getString(4));
            entryCheckData.setEntryDate(cursor.getString(5));
            entryCheckData.setStrength(cursor.getString(6));
            entryCheckData.setSampleSpec(cursor.getString(7));
            entryCheckData.setSampleSize(cursor.getString(8));
        }
        cursor.close();
        return entryCheckData;
    }

    public EntryCheckData getEntryCheckWithComment(int entryId) {
        EntryCheckData entryCheckData = new EntryCheckData();
        String sql = "select  entry_id, lab_comment,lab_check_date," +
                " super_comment, super_check_date,accepted,witness from "
                + TABLE_NAME_ENTRY_CHECK + " where entry_id=" + entryId + " and uploaded=0";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            entryCheckData.setEntryId(cursor.getInt(0));
            entryCheckData.setLabComment(cursor.getString(1));
            entryCheckData.setLabCheckDate(cursor.getString(2));
            entryCheckData.setSuperComment(cursor.getString(3));
            entryCheckData.setSuperCheckDate(cursor.getString(4));
            entryCheckData.setAccepted(cursor.getInt(5));
            entryCheckData.setWitness(cursor.getString(6));
        }
        cursor.close();
        return entryCheckData;
    }

    public String getObjectName(int object_id) {
        String object_name = null;
        String sql = "select object_name from TestObject where object_id = " + object_id;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            object_name = cursor.getString(0);
        }
        cursor.close();
        return object_name;
    }

    public void setUploadWiteness(WitenessData witenessData, int uploadValue) {
        ContentValues cv = new ContentValues();
        cv.put("uploaded", uploadValue);
        String whereClause = "witness_id=?";
        String[] whereArgs = {String.valueOf(witenessData.getWitness_id())};
        db.update(TABLE_NAME_WITNESSDATA, cv, whereClause, whereArgs);
        LogWriter.log(CommData.INFO, "setUploadWiteness");
    }

    public void setUploadEntryCheck(EntryCheckData entryCheckData) {
        ContentValues cv = new ContentValues();
        cv.put("uploaded", 1);
        String whereClause = "entry_id=?";
        String[] whereArgs = {String.valueOf(entryCheckData.getEntryId())};
        db.update(TABLE_NAME_ENTRY_CHECK, cv, whereClause, whereArgs);
    }

    public boolean isSampleed(int witnessId) {
        String sql;
        if ("监理试验室".equals(CommData.orgType)) {
            sql = "select witness_time from " + TABLE_NAME_WITNESSDATA + " where witness_id=" + witnessId + " and witness_time is not NULL";
        } else {
            sql = "select sample_time from " + TABLE_NAME_WITNESSDATA + " where witness_id=" + witnessId + " and sample_time is not NULL";
        }
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() != 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public int  TestProcess(int witnessId){
        int nTotalItems=0,nFinishedItems=0,nUploaded = 0;
        String sql = "SELECT items_count FROM "+ TABLE_NAME_WITNESSDATA+" WHERE witness_id="+witnessId;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            nTotalItems = cursor.getInt(0);
        }
        sql = "SELECT count(*) FROM "+TABLE_NAME_WITNESSTASK+" WHERE witness_id="+witnessId;
        cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            nFinishedItems = cursor.getInt(0);
        }
        sql = "SELECT count(*) FROM "+TABLE_NAME_WITNESSTASK+" WHERE witness_id="+witnessId + " AND upload_time is not NULL";
        cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            nUploaded = cursor.getInt(0);
        }

        if (nFinishedItems == 0)
            return 1;
        else if ((nFinishedItems>0) && (nFinishedItems<nTotalItems))
        {
            if (nUploaded < nFinishedItems)
                return 2;
            else
                return 3;
        }
        else if (nFinishedItems==nTotalItems)
           if (nUploaded < nTotalItems)
              return 4;
           else
              return 5;
        return 4;
    }
    public boolean isTested(int witnessId) {
        String sql = "select test_time from " + TABLE_NAME_WITNESSDATA + " where witness_id=" + witnessId + " and test_time is not NULL";
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.getCount() != 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public boolean isEntryCheckDone(int entryId) {
        String sql;
        if("监理试验室".equals(CommData.orgType)){
             sql = "select entry_id from " + TABLE_NAME_ENTRY_CHECK + " where entry_id=" + entryId + " and  super_check_date IS NOT NULL";
        }else{
             sql = "select entry_id from " + TABLE_NAME_ENTRY_CHECK + " where entry_id=" + entryId + " and lab_check_date IS NOT NULL";
        }

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() != 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public void deleteOverdueHistory(String path) {
        deleteOverWitness(path);
        deleteOverEntryCheckData();
    }

    public void deleteOverWitness(String path) {
        DeleteFile deleteFile=new DeleteFile();
        String sql = "select witness_id,test_image from " + TABLE_NAME_WITNESSDATA;
        if (CommData.orgType.equals("监理试验室")) {
            sql = sql + " where test_time < datetime('now','-10 days') and test_time is not NULL";
        } else {
            sql = sql + " where sample_time < datetime('now','-10 days') and sample_time is not NULL";
        }
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            LogWriter.log(CommData.INFO, "deleteOverWitness");
            //deleteWitness(db, cursor.getInt(0));
            boolean IsTrue=true;
            if(!deleteFile.deleteFile(path+cursor.getString(1))){
                IsTrue=false;
            }
            String[] args = {String.valueOf( cursor.getInt(0))};
            if(db.delete(TABLE_NAME_WITNESSDETAIL, "witness_id=?", args)<=0){
                IsTrue=false;
            }
            if(IsTrue) {
                db.delete(TABLE_NAME_WITNESSDATA, "witness_id=?", args);
            }
        }
    }
    public void deleteOverEntryCheckData(){
        String sql = "select entry_id from " + TABLE_NAME_ENTRY_CHECK;
        if (CommData.orgType.equals("监理试验室")) {
            sql = sql + " where super_check_date < datetime('now','-10 days') and super_check_date is not NULL";
        } else {
            sql = sql + " where lab_check_date < datetime('now','-10 days') and lab_check_date is not NULL";
        }
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            deleteEntryCheck(cursor.getInt(0));
        }
    }
    private void deleteEntryCheck(int entryId){
        String[] whereArgs={String.valueOf(entryId)};
        db.delete(TABLE_NAME_ENTRY_CHECK,"entry_id=?",whereArgs);
    }

    public int getFileIndex(String fileName,String type) {
        String sql="";
        int index=0;
        if(type.equals("sample")){
           if(CommData.orgType.equals("监理试验室")){
              sql="select witness_index from " + TABLE_NAME_WITNESSDATA + " where witness_image='" + fileName+"'";
           }else{
               sql="select sample_index from " + TABLE_NAME_WITNESSDATA + " where sample_image='" + fileName+"'";
           }
        }else{
            sql="select test_index from " + TABLE_NAME_WITNESSTASK + " where test_image='" + fileName+"'";
        }
        Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            index= cursor.getInt(0);
        }
        return index;
    }

    public Boolean updateFileIndex(int index,String fileName,String type) {
        ContentValues cv = new ContentValues();
        String whereClause="";
        if(type.equals("sample")){
            if(CommData.orgType.equals("监理试验室")){
                cv.put("witness_index", index);
                 whereClause = "witness_image=?";
            }else{
                cv.put("sample_index", index);
                whereClause = "sample_image=?";

            }
        }else{
            cv.put("test_index", index);
            whereClause = "test_image=?";
        }

        String[] whereArgs = {String.valueOf(fileName)};
        if (type.equals("sample")){
            if(db.update(TABLE_NAME_WITNESSDATA, cv, whereClause, whereArgs)>0){
                LogWriter.log(CommData.INFO, "updateFileIndex");
                return true;
            }else{
                return false;
            }
        }
        else {
            if(db.update(TABLE_NAME_WITNESSTASK, cv, whereClause, whereArgs)>0){
                LogWriter.log(CommData.INFO, "updateFileIndex");
                return true;
            }else{
                return false;
            }

        }

    }

    //现场检测
    public List<SiteTestData> getTestTaskDatas() {
        List<SiteTestData> siteTestDataArrayList = new ArrayList<SiteTestData>();
        String sql;
        sql = "select test_name,order_date,task_status,data_id,index_id,object_id,order_id"
                + " FROM " + TABLE_NAME_TEST_TASK+ " where task_status<4" ;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            SiteTestData siteTestData = new SiteTestData();
            siteTestData.setTestName(cursor.getString(0));
            siteTestData.setOrderDate(cursor.getString(1));
            siteTestData.setTaskStatus(cursor.getInt(2));
            siteTestData.setDataId(cursor.getInt(3));
            siteTestData.setIndexId(cursor.getInt(4));
            siteTestData.setObjectId(cursor.getInt(5));
            siteTestData.setOrderId(cursor.getString(6));
            siteTestDataArrayList.add(siteTestData);
        }
        cursor.close();
        return siteTestDataArrayList;
    }

    public List<SiteTestData> getTestTaskHistory() {
        List<SiteTestData> siteTestDataArrayList = new ArrayList<SiteTestData>();
        String sql;
        sql = "select test_name,order_date,task_status,data_id,index_id,object_id,order_id"
                + " FROM " + TABLE_NAME_TEST_TASK+ " where task_status=4" ;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            SiteTestData siteTestData = new SiteTestData();
            siteTestData.setTestName(cursor.getString(0));
            siteTestData.setOrderDate(cursor.getString(1));
            siteTestData.setTaskStatus(cursor.getInt(2));
            siteTestData.setDataId(cursor.getInt(3));
            siteTestData.setIndexId(cursor.getInt(4));
            siteTestData.setObjectId(cursor.getInt(5));
            siteTestData.setOrderId(cursor.getString(6));
            siteTestDataArrayList.add(siteTestData);
        }
        cursor.close();
        return siteTestDataArrayList;
    }



    public void saveTestTask( List<SiteTestData> siteTestDatas) throws ParseException {
            for (int i = 0; i < siteTestDatas.size(); i++) {
                ContentValues cv = new ContentValues();
                cv.put("data_id", siteTestDatas.get(i).getDataId());
                cv.put("index_id", siteTestDatas.get(i).getIndexId());
                cv.put("org_id", siteTestDatas.get(i).getOrgId());
                cv.put("order_id", siteTestDatas.get(i).getOrderId());
                cv.put("object_id", siteTestDatas.get(i).getObjectId());
                cv.put("meta_id", siteTestDatas.get(i).getMetaId());
                cv.put("meta_name", siteTestDatas.get(i).getMetaName());
                cv.put("test_name", siteTestDatas.get(i).getTestName());
                cv.put("sample_id", siteTestDatas.get(i).getSampleId());
                cv.put("test_count", siteTestDatas.get(i).getTestCount());
                cv.put("start_no", siteTestDatas.get(i).getStartNo());
                cv.put("sample_spec", siteTestDatas.get(i).getSampleSpec());
                cv.put("size", siteTestDatas.get(i).getSize());
                cv.put("original_gauge", siteTestDatas.get(i).getOriginalGauge());
                cv.put("product_date", siteTestDatas.get(i).getProductDate());
                cv.put("expected_date", siteTestDatas.get(i).getExpectedDate());
                cv.put("age", siteTestDatas.get(i).getAge());
                cv.put("download_date", siteTestDatas.get(i).getDownloadDate());
                cv.put("test_date", siteTestDatas.get(i).getTestDate());
                cv.put("tester", siteTestDatas.get(i).getTester());
                cv.put("transfer_time", siteTestDatas.get(i).getTransferTime());
                cv.put("download_count", siteTestDatas.get(i).getDownloadCount());
                cv.put("test_category", siteTestDatas.get(i).getTestCategory());
                cv.put("groups", siteTestDatas.get(i).getGroups());
                cv.put("order_date", siteTestDatas.get(i).getOrderDate());
                cv.put("task_status", 0);

               if(db.insert(TABLE_NAME_TEST_TASK, null, cv)==-1) {
                   Log.i("error saveTestTask","数据保存到本地错误");
               }
            }
    }

    public void deleteTestTask(){
        db.delete(TABLE_NAME_TEST_TASK,"download_date is null AND download_count=?",new String[]{"0"});
    }

    public Boolean updateTestTaskStatus(String dataId,String indexId){
        SimpleDateFormat  sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = sDateFormat.format(new Date());
        ContentValues cv = new ContentValues();
        cv.put("download_date", date);
        cv.put("download_count", 1);
        cv.put("task_status", 1);
        String whereClause = "data_id=? and index_id=?";
        String[] whereArgs = {dataId,indexId};
        int i=db.update(TABLE_NAME_TEST_TASK, cv, whereClause, whereArgs);
        if(i>0) {
            return true;
        }else{
            return false;
        }
    }

    public long saveSiteTestData( MeasurePointData measurePointData) throws ParseException {
        ContentValues cv = new ContentValues();
        cv.put("data_id", measurePointData.getDataId());
        cv.put("index_id", measurePointData.getIndexId());
        cv.put("SN", measurePointData.getSN());
        cv.put("receive_state", measurePointData.getReceiveState());
        cv.put("point_status", measurePointData.getMeasurePointStatus());
        cv.put("point_name", measurePointData.getPointName());
        long i=db.insert(TABLE_NAME_SITE_TEST_DATA, null, cv);
        if(i==-1) {
            Log.i("error saveTestTask","数据保存到本地错误");
            return i;
        }else{
            return i;
        }
    }

    public List<MeasurePointData> getSiteTestAndMeasurePointData(int dataId,int indexId){
        List<MeasurePointData> measurePointDatasList=new ArrayList<MeasurePointData>();
        String sql;
        sql="select *  FROM "+ TABLE_NAME_SITE_TEST_DATA+ " where data_id="+dataId+" and index_id="+indexId;
        Cursor cursor=db.rawQuery(sql,null);
        while (cursor.moveToNext()){
            MeasurePointData measurePointData=new MeasurePointData();
            measurePointData.setPointId(cursor.getInt(0));
            measurePointData.setDataId(cursor.getInt(1));
            measurePointData.setIndexId(cursor.getInt(2));
            measurePointData.setSN(cursor.getInt(3));
            measurePointData.setReceiveState(cursor.getInt(4));
            measurePointData.setReceivePos(cursor.getInt(5));
            measurePointData.setFileName(cursor.getString(8));
            measurePointData.setMeasurePointStatus(cursor.getInt(9));
            measurePointData.setPointName(cursor.getString(10));

            measurePointDatasList.add(measurePointData);
        }
        cursor.close();
        return measurePointDatasList;
    }

   /* public int getSiteTestItem(SQLiteDatabase db){
        String sql;
        sql="select  *  FROM "+ TABLE_NAME_SITE_TEST_ITEM;
        Cursor cursor=db.rawQuery(sql,null);
      // int i=cursor.getCount();
        return cursor.getCount();
    }*/

    public void saveSiteTestItemData(List<SiteTestItemData> siteTestItemDatas) throws ParseException {

        for (int i = 0; i < siteTestItemDatas.size(); i++) {
            if(isnull(siteTestItemDatas.get(i).getObjectId(),siteTestItemDatas.get(i).getMetaId())) {
                ContentValues cv = new ContentValues();
                cv.put("object_id", siteTestItemDatas.get(i).getObjectId());
                cv.put("meta_id", siteTestItemDatas.get(i).getMetaId());
                cv.put("meta_name", siteTestItemDatas.get(i).getMetaName());
                cv.put("meta_type", siteTestItemDatas.get(i).getMetaType());
                long result = db.insert(TABLE_NAME_SITE_TEST_ITEM, null, cv);
                if (result == -1) {
                    Log.i("error saveTestTask", "数据保存到本地错误");
                }
            }
        }
    }
    private Boolean isnull(int objectId,int metaId){
        String sql;
        sql="select * from "+TABLE_NAME_SITE_TEST_ITEM+" where object_id="+objectId+" and meta_id="+metaId;
        Cursor cursor=db.rawQuery(sql,null);
        if(cursor.getCount()>0){
            return false;
        }else{
            return true;
        }
    }


    public void setSiteTestDetail( int objectId,int pointId,int dataId,int SN) throws ParseException{
          String sql;
        sql="select meta_id,meta_name,meta_type from "+TABLE_NAME_SITE_TEST_ITEM+" where object_id="+objectId;
        Cursor cursor=db.rawQuery(sql,null);
        while (cursor.moveToNext()){
            ContentValues cv=new ContentValues();
            if(isPointData(dataId,cursor.getInt(0)*100+SN)){
                cv.put("point_id",pointId);
                cv.put("meta_name",cursor.getString(1));
                cv.put("meta_type", cursor.getInt(2));
                int i= db.update(TABLE_NAME_SITE_TEST_DETAIL,cv,"data_id=? and meta_id=?",new String[]{String.valueOf(dataId),String.valueOf(cursor.getInt(0)*100+SN)});
                if(i==-1){
                    Log.i("setSiteTestDetail", "数据更新到本地错误");
                }
            }else {
                cv.put("point_id", pointId);
                cv.put("data_id", dataId);
                cv.put("meta_id", (cursor.getInt(0) * 100 + SN));
                cv.put("meta_name", cursor.getString(1));
                cv.put("meta_type", cursor.getInt(2));
                if (db.insert(TABLE_NAME_SITE_TEST_DETAIL, null, cv) == -1) {
                    Log.i("setSiteTestDetail", "数据保存到本地错误");
                }
            }
        }
    }

    private Boolean isPointData(int dataId,int metaId){
        String sql;
        sql = "select * from "+TABLE_NAME_SITE_TEST_DETAIL+"  where data_id="+dataId+" and meta_id="+metaId;
        Cursor cursor=db.rawQuery(sql,null);
        if(cursor.getCount()>0){
            return true;
        }else{
            return false;
        }
    }

    public void insertSiteTestDetail( List<PointMeta> PointMetas){
        for(int i=0;i<PointMetas.size();i++){
            PointMeta pointMeta=PointMetas.get(i);
            if(pointMeta.getMetaValue().equals("") || pointMeta.getMetaValue()==null){
                continue;
            }
            ContentValues cv=new ContentValues();
            cv.put("data_id",pointMeta.getDataId());
            cv.put("meta_id",pointMeta.getMetaId());
            cv.put("value",pointMeta.getMetaValue());
            cv.put("upload_status",1);
            long isSuccess= db.insert(TABLE_NAME_SITE_TEST_DETAIL,null,cv);
            if(isSuccess<=0){
                Log.i("error","更新数据失败！updateSiteTestDetailData");
            }
        }

    }


    //获取本测点的所有metaId
    public String getPointAndMetaId(int objectId){
        String sql;
        String metaIds="";
        String result="";
        sql="select meta_id from "+TABLE_NAME_SITE_TEST_ITEM+" where object_id="+objectId;
        Cursor cursor=db.rawQuery(sql,null);
        while (cursor.moveToNext()){
            metaIds+=cursor.getInt(0)+",";
        }
        String[] array=metaIds.split(",");
        for(int i=0;i<array.length;i++){
            result+=array[i];
            if(i!=array.length-1){
                result+=",";
            }
        }
        return result;
    }

    public List<PointItemData> getSiteTestDetail(int pointId,int dataId) throws ParseException{
        List<PointItemData> pointItemDataList=new ArrayList<PointItemData>();
        String sql;
        sql="select * from "+TABLE_NAME_SITE_TEST_DETAIL+" where point_id="+pointId+" and data_id="+dataId;
        Cursor cursor=db.rawQuery(sql,null);
        while (cursor.moveToNext()){
            PointItemData pointItemData=new PointItemData();
            pointItemData.setPointId(cursor.getInt(0));
            pointItemData.setDataId(cursor.getInt(1));
            pointItemData.setMetaId(cursor.getInt(2));
            pointItemData.setMetaName(cursor.getString(3));
            pointItemData.setMetaType(cursor.getInt(4));
            pointItemData.setItemValue(cursor.getString(5));
            pointItemDataList.add(pointItemData);
        }
        return pointItemDataList;
    }

    public void updateSiteTestDetailData(int pointId,int dataId,int metaId,String value){
        ContentValues cv=new ContentValues();
        cv.put("value",value);
        int i= db.update(TABLE_NAME_SITE_TEST_DETAIL,cv,"point_id=? and data_id=? and meta_id=?",new String[]{String.valueOf(pointId),String.valueOf(dataId),String.valueOf(metaId)});
        if(i<=0){
          Log.i("error","保存数据失败！updateSiteTestDetailData");
        }
    }

    //获取桩基文件的路径
    public void updateSiteTestDataAndFileName(int pointId,String path){
        ContentValues cv=new ContentValues();
        cv.put("file_name",path);
        int i= db.update(TABLE_NAME_SITE_TEST_DATA,cv,"point_id=?",new String[]{String.valueOf(pointId)});
        String sql="select * from "+TABLE_NAME_SITE_TEST_DATA+" where file_name IS NOT NULL ";
        Cursor cursor=db.rawQuery(sql,null);
        int i1=cursor.getCount();
        if(i<=0){
            Log.i("error","更新数据失败！updateSiteTestDataAndFileName");
        }
    }

    public void updateSiteTestDataAndReceiveState(int pointId,int receiveState,int receivePos){
        ContentValues cv=new ContentValues();
        cv.put("receive_state",receiveState);
        //cv.put("receive_pos",receivePos);
        int i= db.update(TABLE_NAME_SITE_TEST_DATA,cv,"point_id=?",new String[]{String.valueOf(pointId)});
        if(i<=0){
            Log.i("error","更新数据失败！updateSiteTestDataAndReceiveState");
        }
    }

    public int querySiteTestDataReceiveState(int pointId){
          String sql;
        sql="select receive_state from "+TABLE_NAME_SITE_TEST_DATA+" where point_id="+pointId;
        Cursor cursor= db.rawQuery(sql,null);
        while (cursor.moveToNext()){
            return cursor.getInt(0);
        }
        return 0;
    }

    public int querySiteTestDataSendState(int pointId){
        String sql;
        sql="select send_state from "+TABLE_NAME_SITE_TEST_DATA+" where point_id="+pointId;
        Cursor cursor= db.rawQuery(sql,null);
        while (cursor.moveToNext()){
            return cursor.getInt(0);
        }
        return 0;
    }

    public List<PointItemData> querySiteTestDetailValue(int pointId){
        List<PointItemData> pointItemDataList=new ArrayList<PointItemData>();
        String sql;
        sql="select meta_name,value from "+TABLE_NAME_SITE_TEST_DETAIL+" where point_id="+pointId;
        Cursor cursor= db.rawQuery(sql,null);
        while (cursor.moveToNext()){
            PointItemData pointItemData=new PointItemData();
            pointItemData.setMetaName(cursor.getString(0));
            pointItemData.setItemValue(cursor.getString(1));
            pointItemDataList.add(pointItemData);
    }
        return  pointItemDataList;
    }

    public void updateSiteTestDataAndPointState(int pointId,int pointStatus){
        ContentValues cv=new ContentValues();
        cv.put("point_status",pointStatus);
        int i= db.update(TABLE_NAME_SITE_TEST_DATA,cv,"point_id=?",new String[]{String.valueOf(pointId)});
        if(i<=0){
            Log.i("error","更新数据失败！updateSiteTestDataAndPointState");
        }
    }

   // public List<FileUpload> getSiteTestFile(SQLiteDatabase db, int dataId,int indexId) throws ParseException{
   public List<FileUpload> getSiteTestFile() throws ParseException{
        List<FileUpload> fileUploadList=new ArrayList<FileUpload>();
        String sql;
        sql="select a.send_state,a.send_pos,a.file_name,a.receive_state,a.point_id,b.object_id from site_test_data a inner join TestTask b on a.data_id=b.data_id where  a.file_name IS NOT NULL and a.send_state<2";//and and receive_state=1
        Cursor cursor=db.rawQuery(sql,null);
        while (cursor.moveToNext()){
            FileUpload fileUpload=new FileUpload();
            if(cursor.getInt(0)<2) {
                fileUpload.setSendState(cursor.getInt(0));
                fileUpload.setSendPosition(cursor.getInt(1));
                fileUpload.setFileName(cursor.getString(2));
                fileUpload.setReceiveState(cursor.getInt(3));
                fileUpload.setPointId(cursor.getInt(4));
                fileUpload.setObjectId(cursor.getInt(5));
                fileUploadList.add(fileUpload);
            }
        }
        return fileUploadList;
    }

    public void updateSiteTestDataAndSendState(int pointId,int sendPos,int sendState){
        ContentValues cv=new ContentValues();
        cv.put("send_state",sendState);
        cv.put("send_pos",sendPos);
        int i= db.update(TABLE_NAME_SITE_TEST_DATA,cv,"point_id=?",new String[]{String.valueOf(pointId)});
        if(i<=0){
            Log.i("error","更新数据失败！updateSiteTestDataAndSendState");
        }
    }

    public void updateSiteTestDataAndSendState1(int pointId,int sendPos,int sendState){
        ContentValues cv=new ContentValues();
        cv.put("send_state",sendState);
        cv.put("send_pos",sendPos);
        int i= db.update(TABLE_NAME_SITE_TEST_DATA,cv,"point_id=?",new String[]{String.valueOf(pointId)});
        if(i<=0){
            Log.i("error","更新数据失败！updateSiteTestDataAndSendState");
        }
    }

    public List<SendTestValue> getTestValue() throws ParseException{
        List<SendTestValue> sendTestValueList=new ArrayList<SendTestValue>();
        String sql;
        Cursor cursorOne=null;
        Cursor cursorTwo=null;
        db.beginTransaction();
        try{
            sql="select b.meta_id,a.file_name,a.SN,a.point_id,a.data_id  from site_test_data a INNER JOIN TestTask b on a.data_id=b.data_id where a.file_name IS NOT NULL ";//where  a.send_state=2
            cursorOne=db.rawQuery(sql,null);
            while (cursorOne.moveToNext()){
                SendTestValue sendTestValueOne=new SendTestValue();
                int mateId=cursorOne.getInt(0)*100+cursorOne.getInt(2);
                sendTestValueOne.setMateId(mateId);
                String[] paths=  cursorOne.getString(1).split("/");
                sendTestValueOne.setValue(paths[paths.length-1]);
                sendTestValueOne.setPointId(cursorOne.getInt(3));
                sendTestValueOne.setDataId(cursorOne.getInt(4));
                sendTestValueList.add(sendTestValueOne);
                //先屏蔽
                /*sql="select meta_id,value,upload_status from "+TABLE_NAME_SITE_TEST_DETAIL+" where upload_status != 1 and point_id="+cursorOne.getInt(3);
                cursorTwo=db.rawQuery(sql,null);
                while (cursorTwo.moveToNext()){
                    SendTestValue sendTestValueTwo=new SendTestValue();
                    sendTestValueTwo.setMateId(cursorTwo.getInt(0));
                    sendTestValueTwo.setValue(cursorTwo.getString(1));
                    sendTestValueTwo.setUploadStatus(cursorTwo.getInt(2));
                    sendTestValueTwo.setDataId(cursorOne.getInt(4));
                    sendTestValueList.add(sendTestValueTwo);
                }*/
            }
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.endTransaction();
        return sendTestValueList;
    }


    public int getTestCount(int dataId,int indexId){
        String sql;
        sql="select groups from TestTask  where data_id="+dataId+" and index_id="+indexId;
        Cursor cursor=db.rawQuery(sql,null);
       while (cursor.moveToNext()){
           return cursor.getInt(0);
       }
        return 0;
    }

    public boolean isPointName(int dataId,String pointName){
        String sql;

        sql="select * from site_test_data  where data_id="+dataId+" and point_name='"+pointName+"'";
        Cursor cursor=db.rawQuery(sql,null);
       if(cursor.getCount()>0){
           return  true;
       }else{
           return false;
       }
    }

    public boolean updateTaskState(int dataId,int indexId){

        ContentValues cv=new ContentValues();
        cv.put("task_status",4);
        int i= db.update(TABLE_NAME_TEST_TASK,cv,"data_id=? and index_id=?",new String[]{String.valueOf(dataId),String.valueOf(indexId)});
        if(i<=0){
            Log.i("error","更新数据失败！updateTaskState");
            return false;
        }
        return true;
    }

    public boolean updatePointSendState(int pointId){

        ContentValues cv=new ContentValues();
        cv.put("send_state",3);
        int i= db.update(TABLE_NAME_SITE_TEST_DATA,cv,"point_id=?",new String[]{String.valueOf(pointId)});
        if(i<=0){
            Log.i("error","更新数据失败！updateTaskState");
            return false;
        }
        return true;
    }

    public boolean updateTaskStateAndBlock(int dataId,int indexId){

        ContentValues cv=new ContentValues();
        cv.put("task_status",3);
        int i= db.update(TABLE_NAME_TEST_TASK,cv,"data_id=? and index_id=?",new String[]{String.valueOf(dataId),String.valueOf(indexId)});
        if(i<=0){
            Log.i("error","更新数据失败！updateTaskStateAndBlock");
            return false;
        }
        return true;
    }


    /*public void deleteOverTestTaskData(SQLiteDatabase db){
        String sql = "select data_id,index_id from " + TABLE_NAME_TEST_TASK+ " where download_date < datetime('now','-10 days') and task_status=4";

        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            deleteSiteTestData(db, cursor.getInt(0),cursor.getInt(1));
            String[] whereArgs={String.valueOf(cursor.getInt(0)),String.valueOf(cursor.getInt(1))};
            db.delete(TABLE_NAME_SITE_TEST_DATA,"data_id=? and index_id=? ",whereArgs);
        }

    }*/


    public Boolean getPointState(int dataId,int indexId){
        String sql;
        sql="select point_status from "+TABLE_NAME_SITE_TEST_DATA+"  where data_id="+dataId+" and index_id="+indexId;
        Cursor cursor=db.rawQuery(sql,null);
        while (cursor.moveToNext()){
           if(cursor.getInt(0)==0){
               return false;
           }
        }
        return true;
    }

    public Boolean updateTaskStateAndTwo(int dataId,int indexId){
        ContentValues cv=new ContentValues();
        cv.put("task_status",2);
        int i= db.update(TABLE_NAME_TEST_TASK,cv,"data_id=? and index_id=?",new String[]{String.valueOf(dataId),String.valueOf(indexId)});
        if(i<=0){
            Log.i("error","更新数据失败！updateTaskStateAndTwo");
            return false;
        }
        return true;

    }


    public void deleteOverFileAndData(String path){
        DeleteFile deleteFile=new DeleteFile();
        boolean taskIsTrue=true;
        String sql = "select data_id,index_id from " + TABLE_NAME_TEST_TASK+ " where download_date < datetime('now','-30 days') and task_status=4";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()){
            sql="select file_name,point_id from "+TABLE_NAME_SITE_TEST_DATA+"  where data_id="+cursor.getInt(0)+" and index_id="+cursor.getInt(1);
            Cursor cursor1 = db.rawQuery(sql, null);
            while (cursor1.moveToNext()){
                boolean pointIsTrue=true;
               if(!deleteFile.deleteFile(path+cursor1.getString(0))){
                   pointIsTrue=false;
                   taskIsTrue=false;
                }
                if(db.delete(TABLE_NAME_SITE_TEST_DETAIL,"point_id=? ",new String[]{String.valueOf(cursor1.getInt(1))})<=0){
                    pointIsTrue=false;
                    taskIsTrue=false;
                }
                if(pointIsTrue){
                   if(db.delete(TABLE_NAME_SITE_TEST_DATA,"point_id=? ",new String[]{String.valueOf(cursor1.getInt(1))})<=0) {
                       taskIsTrue=false;
                   }
                }
            }
            if(taskIsTrue){
                db.delete(TABLE_NAME_TEST_TASK, "data_id=? and index_id=? ", new String[]{String.valueOf(cursor.getInt(0)),String.valueOf(cursor.getInt(1))});
            }
        }
    }

    public List<PointMeta> getPointNum(int dataId){
        List<PointMeta> pointMetas=new ArrayList<>();
        String sql="select meta_id,value from "+TABLE_NAME_SITE_TEST_DETAIL+" where meta_id/100 = 100135 and data_id="+dataId;//where  a.send_state=2
        Cursor cursorOne=db.rawQuery(sql,null);
        while (cursorOne.moveToNext()) {
            PointMeta pointMeta = new PointMeta();
            pointMeta.setMetaId(cursorOne.getInt(0));
            pointMeta.setMetaValue(cursorOne.getString(1));
            pointMetas.add(pointMeta);
        }
        return pointMetas;
    }

    public boolean SaveParamDescData(int nDataId,String sPileNo,String sMachineVendor,String sMachineId,String sParam){

        ContentValues cv = new ContentValues();
        cv.put("data_id", nDataId);
        cv.put("test_id", sPileNo);
        cv.put("vendor_id", sMachineVendor);
        cv.put("device_id", sMachineId);
        cv.put("desc_info", sParam);
        String[] args = {String.valueOf(nDataId),sPileNo};
        String sql = "select * from " + TABLE_NAME_SITE_TESTDESC+ " where nDataId='+ nDataId +' and test_id='"+sPileNo+"'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            return (db.update(TABLE_NAME_SITE_TESTDESC, cv, "data_id=? AND pile_id=?", args) != -1);
        }
        else {
            return (db.insert(TABLE_NAME_SITE_TESTDESC, null, cv)!=-1);
        }
    }
    public boolean SaveSiteTestData(int nDataId,String sPileNo,String sChannelId, String tTestTime,byte []cData){

        ContentValues cv = new ContentValues();
        cv.put("data_id", nDataId);
        cv.put("test_id", sPileNo);
        cv.put("channel_id", sChannelId);
        cv.put("test_time", tTestTime);
        cv.put("wave_data", cData);
        String[] args = {String.valueOf(nDataId),sPileNo,sChannelId};
        String sql = "select * from " + TABLE_NAME_SITE_TESTDESC+ " where nDataId='+ nDataId +' and test_id='"+sPileNo+"' and channel_id='"+sChannelId+"'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            return (db.update(TABLE_NAME_SITE_TESTDESC, cv, "data_id=? AND pile_id=? and channel_id=?", args) != -1);
        }
        else {
            return (db.insert(TABLE_NAME_SITE_TESTDESC, null, cv)!=-1);
        }
    }
    public boolean SaveSiteTestLog(int nDataId,String sPileNo,String sChannelId,byte []cLog)
    {
        ContentValues cv = new ContentValues();
        cv.put("data_id", nDataId);
        cv.put("test_id", sPileNo);
        cv.put("channel_id", sChannelId);
        cv.put("wave_data", cLog);
        return (db.insert(TABLE_NAME_SITE_TESTDESC, null, cv)!=-1);
    }

}
