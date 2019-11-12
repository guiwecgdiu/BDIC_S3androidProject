package com.example.myapplication.Activity;

import android.app.Activity;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.SFTPUtilis.SFTPUtils;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.util.Stack;


public class SftpMenu extends Activity implements View.OnClickListener {

    Stack<String> pathStack;
    String rootPath;
    Handler mHandler;


    private final String TAG = "SftpMenu";
    private final int CONNECTING = 101;
    private final int DISCONNECT = 404;
    private final int WAITING = 000;
    private Button buttonUpLoad = null;
    private Button buttonDownLoad = null;
    private Button bConnect = null;
    private Button bDisconnect = null;
    private ListView lRemoteList;
    private Button buttonListdirectory = null;
    private Button buttonCurrentDir = null;
    private TextView tCurPath;


    private SFTPUtils sftp;
    private String currentPath;
    private Thread mThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sftpmain);
        makeHandler();
        init();
        startConnectCheckThread();
    }

    protected void makeHandler(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == CONNECTING){
                    updateUI();
                    updateData();
                }
                if(msg.what == DISCONNECT){
                    tCurPath.setText("Disconnect"+"["+sftp.isChannelConnected()+"]");
                }
                if(msg.what == 000){
                    tCurPath.setText("Waiting......");
                }
            }
        };
    }

    protected void updateUI(){
        //currentPath = sftp.currentRemotePath();
        tCurPath.setText(currentPath+"["+sftp.isChannelConnected()+"]");
    }
    protected void updateData(){
        currentPath = sftp.currentRemotePath();
    }


    protected void startConnectCheckThread()
    {
        mThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                //  sftp.disconnect();
                Message msg = new Message();
//                Bundle bundle = new Bundle();
//                    bundle.putString("currentPath", "[" +sftp.isConnected()+"]");
//                    msg.setData(bundle);
                        if(sftp.isChannelConnected() == true)
                        {
                            Log.d(TAG,"Current path is"+sftp.currentRemotePath());//important without pwd can't read
                            msg.what = CONNECTING;
                        mHandler.sendMessage(msg);
                        }else {
                        msg.what = DISCONNECT;
                        mHandler.sendMessage(msg);
                        }
                    }
                }
        });
        mThread.start();
    }

    protected  void initViews(){
        Log.d(TAG,"Init");
        //获取控件对象
        buttonUpLoad = (Button) findViewById(R.id.button_upload);
        buttonDownLoad = (Button) findViewById(R.id.button_download);
        bConnect = findViewById(R.id.connect);
        bDisconnect = findViewById(R.id.disconnect);
        tCurPath=findViewById(R.id.serverPath);
        lRemoteList=findViewById(R.id.lRemoteList);
//        buttonCurrentDir = findViewById(R.id.current_location);
//        buttonListdirectory = findViewById(R.id.check_remoteList);
    }
    public void initListener(){
        //设置控件对应相应函数
        buttonUpLoad.setOnClickListener(this);
        buttonDownLoad.setOnClickListener(this);
        //buttonListdirectory.setOnClickListener(this);
        bConnect.setOnClickListener(this);
        bDisconnect.setOnClickListener(this);
        //buttonCurrentDir.setOnClickListener(this);
        //bDisconnect.setOnClickListener(this);


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

                    case R.id.connect:{
                        sftp.connect();
                        Message conncting=Message.obtain();
                        conncting.what=WAITING;
                        mHandler.sendMessage(conncting);
                        Log.d(TAG,"Connect Start");
                       Log.d(TAG,sftp.currentRemotePath());
                       rootPath=sftp.currentRemotePath();//call it wil the connect please otherwise the currentpathwon't show

                    }
                    break;

                    case R.id.disconnect:{
                        sftp.disconnect();
                    }
                    break;

//                    case R.id.current_location: {
//                        sftp.connect();
//                        Log.d(TAG, "Connect Successful");
//                        Log.d(TAG, sftp.currentRemotePath());
//                        sftp.disconnect();
//                    }
//                    break;
//
//                    case R.id.check_remoteList: {
//                        sftp.connect();
//                        Log.d(TAG, "Connect Successful");
//                        Log.d(TAG, sftp.showChildNames(sftp.currentRemotePath()).toString());
//                        sftp.disconnect();
//                    }
//                    break;
                    default:
                        break;
                }
            }
        }.start();
    }

        protected String getPathString(){
            Stack<String> temp = new Stack<String>();
            temp.addAll(pathStack);
            String result= "";
            while (!temp.empty()){
                result=temp.pop()+result;
            }
            // Log.d(TAG,i+"+++");
            return result;
        }


    protected void init() {
        initData();
        initViews();
        initListener();
    }

    protected void initData(){
        //sftp = new SFTPUtils("SFTP服务器IP", "用户名", "密码");
       // sftp = new SFTPUtils("119.3.238.156", "siteadmin", "L1l2l3l4");
        sftp=new SFTPUtils("47.103.117.157","siteadmin","guiwecgdiu");
    }
    }
// Java 获取文件后缀
//    public static void main(String[] args) {
//        File file = new File("HelloWorld.java");
//        String fileName = file.getName();
//        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
//        System.out.println(suffix);
//    }


