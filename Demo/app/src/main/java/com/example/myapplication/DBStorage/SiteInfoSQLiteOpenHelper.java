package com.example.myapplication.DBStorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.myapplication.Model.SiteInfo;

import java.util.ArrayList;

public class SiteInfoSQLiteOpenHelper extends SQLiteOpenHelper {

    //重写Oncreate和OnUpgrade的方法，OnCreate只在第一次打开数据库时执行，onUpgrade在数据库版本更新时执行
    //封装 保证数据库安全的必要方法，
    //  1.获取单例对象，避免App运行时数据库只被打开一次
    //  2打开数据库连接，有getReadableDatabase的读连接，和getWritable的写连接
    //  3关闭数据库连接，使用close关闭数据库
    //增加对表记录的增加，删除，修改，查询的操作方法，主要是通过游标实现

    //TAG is usded for debugging
    private static final String TAG = "SiteInfoSQLiteOpenHelper";
    private static final int DB_Version =1;
    private static String DB_NAME = "siteinfo.db";
    private static final String TABLE_NAME = "site_info";
    private SQLiteDatabase mDB = null;
    private static SiteInfoSQLiteOpenHelper mHelper = null;

    private SiteInfoSQLiteOpenHelper(Context context){
        super(context,DB_NAME,null,DB_Version);
      //  mDB = context.openOrCreateDatabase(context.getFilesDir()+"/"+DB_Name,Context.MODE_PRIVATE,null);
    }
    private SiteInfoSQLiteOpenHelper(Context context, int version){
        super(context,DB_NAME,null,version);
       // mDB = context.openOrCreateDatabase(context.getFilesDir()+"/"+DB_Name,Context.MODE_PRIVATE,null);
    }
    //Access the SingleClass Objects
    //  1.获取单例对象，避免App运行时数据库只被打开一次
    public static SiteInfoSQLiteOpenHelper getInstance(Context context, int version){
        if(version >0 && mHelper == null){
            mHelper = new SiteInfoSQLiteOpenHelper(context,version);
        }else if(mHelper == null){
            mHelper = new SiteInfoSQLiteOpenHelper(context);
        }
        return mHelper;
    }
    //10/25 99page 0daosshangxian CHECKED AT 10/26
    //  2打开数据库连接，有getReadableDatabase的读连接，和getWritable的写连接
    //...
    public SQLiteDatabase openReadlink() {
        if (mDB == null && mDB.isOpen() != true) {
            mDB = mHelper.getReadableDatabase();
        }
        return mDB;
    }

    public SQLiteDatabase openWriteLink(){
        if(mDB == null){
            mDB = mHelper.getWritableDatabase();
        }
        return mDB;
    }
    //...


    //  3关闭数据库连接，使用close关闭数据库
    //...
    public void closeLink(){
        if(mDB!=null && mDB.isOpen() == true){
            mDB.close();
            mDB = null;
        }
    }
    //...

    public String getDBName(){
        if(mHelper!=null){
            return mHelper.getDatabaseName();
        }else{
            return DB_NAME;
        }
    }
    //..

    //重写Oncreate和OnUpgrade的方法，OnCreate只在第一次打开数据库时执行
    // onUpgrade在数据库版本更新时执行
    //Execute when db is opened for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,"onCreate");
        String create_sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +"("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + SiteInfo.DB_ip_host+" VARCHAR NOT NULL,"+ SiteInfo.DB_Name_mSite+" VARCHAR NOT NULL,"+ SiteInfo.DB_Protocol+" VARCHAR NOT NULL,"
                + SiteInfo.DB_Username+" VARCHAR NOT NULL," + SiteInfo.DB_Passward+" VARCHAR NOT NULL"
               +");";
        Log.d(TAG,"create_sql:"+create_sql);
        db.execSQL(create_sql);
    }

    //Execute whe the version changed
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }


    //10/26 -- current

    //增加对表记录的增加，删除，修改，查询的操作方法，主要是通过游标实现
    public int delete(String condition){
        int count = mDB.delete(TABLE_NAME,condition,null);
        return count;
    }

    public int deleteAll(){
        int count = mDB.delete(TABLE_NAME,"1=1",null);
        return count;
    }

    public long insert(SiteInfo info) {
        ArrayList<SiteInfo> infos = new ArrayList<SiteInfo>();
        infos.add(info);
        return  insert(infos);
    }

    public long insert(ArrayList<SiteInfo> infos) {
        long result = -1;
        for (int i = 0; i < infos.size(); i++) {
            SiteInfo info = infos.get(i);
            ContentValues cv = new ContentValues();
            cv.put(SiteInfo.DB_ip_host, info.mHost_ip);
            cv.put(SiteInfo.DB_Name_mSite, info.mSite_Name);
            cv.put(SiteInfo.DB_Protocol, info.mProtocol);
            cv.put(SiteInfo.DB_Username, info.mUsername);
            cv.put(SiteInfo.DB_Passward, info.mPassward);
            result = mDB.insert(TABLE_NAME," ", cv);
            if (result == -1) {
                return result;

            }
        }
        return result;
    }

    public int update(SiteInfo info, String condition){

        ContentValues cv = new ContentValues();
        cv.put(SiteInfo.DB_Protocol,info.mProtocol);
        cv.put(SiteInfo.DB_Username,info.mUsername);
        cv.put(SiteInfo.DB_Passward,info.mPassward);
        cv.put(SiteInfo.DB_ip_host,info.mHost_ip);
        cv.put(SiteInfo.DB_Name_mSite,info.mSite_Name);
        int count = mDB.update(TABLE_NAME,cv,condition,null);

        return count;

    }

    public ArrayList<SiteInfo> query(String condition){


        String sql = String.format("select _id,%s,%s,%s,%s,%s from " +
                "%s where %s", SiteInfo.DB_ip_host, SiteInfo.DB_Name_mSite, SiteInfo.DB_Protocol, SiteInfo.DB_Username, SiteInfo.DB_Passward, TABLE_NAME,condition);
        Log.d(TAG,"query sql: "+sql);
        ArrayList<SiteInfo> infoArray = new ArrayList<SiteInfo>();
        Cursor cursor = mDB.rawQuery(sql,null);
        if(cursor.moveToFirst()){
            for(;;cursor.moveToNext()){

                SiteInfo info = new SiteInfo();
                info.mId =cursor.getInt(0);
                info.mHost_ip = cursor.getString(1);
                info.mSite_Name = cursor.getString(2);
                info.mProtocol = cursor.getString(3);
                info.mUsername = cursor.getString(4);
                info.mPassward = cursor.getString(5);
                infoArray.add(info);
                if(cursor.isLast()==true){
                    break;
                }
            }

        }
        cursor.close();

        Log.e(TAG,infoArray.toString());
        return infoArray;
    }

    public SiteInfo queryByIp(String ip){
        SiteInfo info = null;
        ArrayList<SiteInfo> infoArray = query(String.format("%s = '%s'", SiteInfo.DB_ip_host,ip));
        if(infoArray.size() >0 ){
            info = infoArray.get(0);
        }
        return info;
    }

    public int deleteSiteInfo(SiteInfo i){
            int id = i.mId;
            if(id!=-1) {
                mHelper.delete(String.format("%s='%s'", SiteInfo.DID, id));
                return id;
            }
            else {
                Log.d(TAG,"The courseifo has no id");
                return -1;
            }
    }

    public ArrayList<SiteInfo> queryAll(){
        ArrayList<SiteInfo> infoArray = query("1=1");
        return infoArray;
    }



}
