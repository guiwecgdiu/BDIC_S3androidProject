package com.example.myapplication;

import android.app.Activity;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.SFTPUtilis.SFTPUtils;
import com.jcraft.jsch.SftpException;

import java.io.File;


public class MainActivity extends Activity implements View.OnClickListener {
    private final String TAG = "MainActivity";
    private Button buttonUpLoad = null;
    private Button buttonDownLoad = null;
    private Button buttonConnect = null;
    private Button buttonDisconnect = null;
    private Button buttonListdirectory = null;
    private Button buttonCurrentDir = null;
    private SFTPUtils sftp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sftpmain);
        init();
    }

    public void init() {
        //获取控件对象
        buttonUpLoad = (Button) findViewById(R.id.button_upload);
        buttonDownLoad = (Button) findViewById(R.id.button_download);
        buttonConnect = findViewById(R.id.connect);
        buttonDisconnect = findViewById(R.id.disconnect);
        buttonCurrentDir =findViewById(R.id.current_location);
        buttonListdirectory = findViewById(R.id.check_remoteList);
        //设置控件对应相应函数
        buttonUpLoad.setOnClickListener(this);
        buttonDownLoad.setOnClickListener(this);
        buttonListdirectory.setOnClickListener(this);
        buttonConnect.setOnClickListener(this);
        buttonDisconnect.setOnClickListener(this);
        buttonCurrentDir.setOnClickListener(this);
        buttonDisconnect.setOnClickListener(this);
        //sftp = new SFTPUtils("SFTP服务器IP", "用户名", "密码");
        sftp = new SFTPUtils("119.3.238.156", "siteadmin", "L1l2l3l4");
    }

    @Override
    public void onClick(final View v) {
            // TODO Auto-generated method stub
            new Thread() {
                @Override
                public void run() {
                    //这里写入子线程需要做的工作

                    switch (v.getId()) {
                        case R.id.button_upload: {
                            //上传文件
                            Log.d(TAG, "上传文件");
                            String localPath = "sdcard/xml/";
                            String remotePath = "test";
                            sftp.connect();
                            Log.d(TAG, "连接成功");
                            sftp.uploadFile(remotePath, "APPInfo.xml", localPath, "APPInfo.xml");
                            Log.d(TAG, "上传成功");
                            sftp.disconnect();
                            Log.d(TAG, "断开连接");
                        }
                        break;

                        case R.id.button_download: {
                            //下载文件
                            Log.d(TAG, "下载文件");
                            String localPath = "sdcard/download/";
                            String remotePath = "test";
                            sftp.connect();
                            Log.d(TAG, "连接成功");
                            sftp.downloadFile(remotePath, "APPInfo.xml", localPath, "APPInfo.xml");
                            Log.d(TAG, "下载成功");
                            sftp.disconnect();
                            Log.d(TAG, "断开连接");

                        }
                        break;

                        case R.id.current_location:{
                            sftp.connect();
                            Log.d(TAG, "Connect Successful");
                            Log.d(TAG,sftp.currentRemotePath());
                            sftp.disconnect();
                        }
                        break;

                        case R.id.check_remoteList:{
                            sftp.connect();
                            Log.d(TAG,"Connect Successful");
                            Log.d(TAG,sftp.showChildNames(sftp.currentRemotePath()).toString());
                            sftp.disconnect();
                        }
                        break;
                        default:
                            break;
                    }
                }
            }.start();
    }
// Java 获取文件后缀
//    public static void main(String[] args) {
//        File file = new File("HelloWorld.java");
//        String fileName = file.getName();
//        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
//        System.out.println(suffix);
//    }


}