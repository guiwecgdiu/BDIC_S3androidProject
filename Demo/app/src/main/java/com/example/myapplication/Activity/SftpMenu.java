package com.example.myapplication.Activity;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.SFTPUtilis.SFTPUtils;
import com.example.myapplication.presentation.RemoteFileAdaptor;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.Vector;


public class SftpMenu extends Activity implements View.OnClickListener {

    Stack<String> pathStack;
    String rootPath;
    Handler mHandler;


    private final String TAG = "SftpMenu";
    private final int CONNECTING = 101;
    private final int DISCONNECT = 404;
    private final int LOADDIR = 001;
    private final int WAITING = 000;
    private Button buttonUpLoad = null;
    private Button buttonDownLoad = null;
    private Button bConnect = null;
    private Button bDisconnect = null;
    private ListView lRemoteList;
    private Button buttonListdirectory = null;
    private Button buttonCurrentDir = null;
    private TextView tCurPath;
    private RemoteFileAdaptor remoteAdaptor=null;
    private ArrayList<String> curPathFiles;


    private SFTPUtils sftp;
    private String currentPath;
    private Thread mThread;
    private Handler subHandler;
    private Looper myLooper;

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
                    curPathFiles.clear();
                    pathStack.clear();
                    remoteAdaptor.notifyDataSetChanged();
                }
                if(msg.what == WAITING){
                    tCurPath.setText("Waiting......");
                }
                if(msg.what == LOADDIR){
                    Log.d(TAG,"show dir");
                    remoteAdaptor.notifyDataSetChanged();
                    }
            }
        };
    }
    protected void closeUI(){
        curPathFiles.clear();
       remoteAdaptor.notifyDataSetChanged();
    }
    protected void updateUI(){
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

                        if(sftp.isChannelConnected() == true)
                        {
                            Log.d(TAG,"Current path is"+sftp.currentRemotePath());//important without pwd can't read
                            Message msg =Message.obtain();
                            msg.what = CONNECTING;
                        mHandler.sendMessage(msg);
                        }else {
                            Message msg =Message.obtain();
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
        remoteAdaptor = new RemoteFileAdaptor(this,R.layout.item_type1,curPathFiles);
        lRemoteList.setAdapter(remoteAdaptor);
    }
    public void initListener(){
        //设置控件对应相应函数
        buttonUpLoad.setOnClickListener(this);
        buttonDownLoad.setOnClickListener(this);
        bConnect.setOnClickListener(this);
        bDisconnect.setOnClickListener(this);
        lRemoteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ListView l = (ListView)parent;
                String item =(String) l.getAdapter().getItem(position);
                pathStack.push("/"+item);
                Message message = subHandler.obtainMessage(2002,item);
                message.what=1234;
                message.sendToTarget();
            }
        });

    }

    @Override
    public void onClick(final View v) {
        // TODO Auto-generated method stub
        new Thread() {

            @Override
            public void run() {
                //这里写入子线程需要做的工作
                //Those code for trans msg from the main act to the sub act
                Looper.prepare();
                myLooper=Looper.myLooper();
                //...
                subHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
//                        pathStack.push(msg.obj.toString());
//                        Message m =mHandler.obtainMessage();
//                        m.what=LOADDIR;
//                        sftp.cdDeeper(getPathString());
//
//                        sftp.listFiles(sftp.currentRemotePath());
//                        mHandler.sendMessage(m);
                        if(msg.what == 1234){
                        Message conncting=Message.obtain();
                        conncting.what=WAITING;
                        mHandler.sendMessage(conncting);
                        Log.d(TAG,"Connect Start");
                        Log.d(TAG,sftp.currentRemotePath());
                        // Log.d(TAG,sftp.showChildNames(sftp.currentRemotePath()).toString());
                        Log.d(TAG,"at 214");
                       sftp.cdDeeper(getPathString());

                        //wait for a while and then load the files
                        while (true) {
                            if(sftp.listFiles(getPathString())!=null) {
                                Message m = Message.obtain();
                                m.what = LOADDIR;
                              //  Log.d(TAG, "Line 238" +sftp.listFiles(currentPath).toString());
                                curPathFiles.clear();
                               curPathFiles.addAll(showChildNames(sftp.listFiles(getPathString())));
                                mHandler.sendMessage(m);
                                break;
                            }}
                        Toast.makeText(SftpMenu.this,msg.obj.toString()+" is clicked",Toast.LENGTH_LONG).show();}
                    }
                };
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
                      // Log.d(TAG,sftp.showChildNames(sftp.currentRemotePath()).toString());
                       rootPath=sftp.currentRemotePath();
                       pathStack.push(rootPath);

                        //wait for a while and then load the files
                       while (true) {
                           if(sftp.listFiles(currentPath)!=null) {
                               Message msg = Message.obtain();
                               msg.what = LOADDIR;
                               Log.d(TAG, "Line 238" +sftp.listFiles(currentPath).toString());
                               curPathFiles.addAll(showChildNames(sftp.listFiles(currentPath)));
                               mHandler.sendMessage(msg);
                               break;
                           }
                       }
                       //call it wil the connect please otherwise the currentpathwon't show


                    }
                    break;

                    case R.id.disconnect:{
                        sftp.disconnect();

                    }
                    break;
                    default:
                        break;
                }
                Looper.loop();
                Log.d(TAG,"END OF LOOPER");
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

    protected void cd(String directory){
        sftp.cdDeeper(directory);
    }

    protected void init() {
        initData();
        initViews();
        initListener();
    }

    protected void initData(){
        //sftp = new SFTPUtils("SFTP服务器IP", "用户名", "密码");
        sftp = new SFTPUtils("119.3.238.156", "siteadmin", "L1l2l3l4");
        //sftp=new SFTPUtils("47.103.117.157","siteadmin","guiwecgdiu");
        pathStack = new Stack<String>();
        curPathFiles = new ArrayList<String>();
    }

    public ArrayList<String> showChildNames(Vector v) {
        ArrayList<String> arrs = new ArrayList<String>();

        Enumeration<ChannelSftp.LsEntry> elements = v.elements();
        while (elements.hasMoreElements()){
            ChannelSftp.LsEntry ls = elements.nextElement();
            arrs.add(ls.getFilename());
        }
        return arrs;
    }

    @Override
    protected void onDestroy() {

        myLooper.quit();//
        super.onDestroy();
    }
}
// Java 获取文件后缀
//    public static void main(String[] args) {
//        File file = new File("HelloWorld.java");
//        String fileName = file.getName();
//        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
//        System.out.println(suffix);
//    }


