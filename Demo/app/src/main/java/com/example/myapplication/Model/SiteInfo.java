package com.example.myapplication.Model;

import android.os.Bundle;

import java.io.Serializable;

public class SiteInfo implements Serializable {
    //public int mId=-1;public static String DID = "_id";
    public String mMooduleCode;public static String DBCODE = "moodule_code";
    public String mMooduleName;public static String DBNAME = "moodule_name";
    public String mChoice;public static String DBCHOICE = "first_letter";
    public String mDayofWeek;public static String DBDAYOFWEEK = "day_indecation";
    public String mStartTime;public static String DBSTARTTIME = "start_time";
    public String mLocation;public static String DBLOCATION = "location";
    public String mComment;public static String DBCOMMENT = "comments";
    public static String Serializable = "FULLINFO";


    public int mId=-1;public static String DID = "_id";
    public String mHost_ip; public static String DB_ip_host = "Ip_adress";
    public String mSite_Name;  public static String DB_Name_mSite = "Site_Name";
    public String mProtocol;  public static String DB_Protocol = "Protocol";
    public String mUsername; public static String DB_Username = "User_Name";
    public String mPassward; public static String DB_Passward = "Passward";


    public SiteInfo(){

    }
    public SiteInfo(String ip, String Site_Name, String username, String passward){

        mHost_ip=ip;
        mSite_Name=Site_Name;
        mProtocol = "SFTP";
        mUsername = username;
        mPassward = passward;
    }
    public SiteInfo(String code, String name, String choice, String dayOfweek, String startTime, String location, String comment){
        mMooduleCode = code;
        mMooduleName = name;
        mChoice = choice;
        mDayofWeek = dayOfweek;
        mStartTime = startTime;
        mLocation = location;
        mComment = comment;
    }



    @Override
    public String toString() {
        String record = "<A>"+mProtocol +"-" +mSite_Name + "("+mHost_ip+")"+"["+mUsername+"."+mPassward+"]";
        return record;
    }



    public void setId(int id){
        mId=id;
    }


}
