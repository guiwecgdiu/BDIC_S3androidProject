package com.example.myapplication.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.nfc.Tag;
import android.os.Environment;
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

import com.example.myapplication.Model.SiteInfo;
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
import android.support.v7.app.AlertDialog;


public class SftpMenu extends Activity implements View.OnClickListener {

    Stack<String> pathStack;
    String rootPath;
    Handler mHandler;


    private final String TAG = "SftpMenu";
    private final int CONNECTING = 101;
    private final int DISCONNECT = 404;
    private final int LOADDIR = 001;
    private final int WAITING = 000;
    private final int CDTODIR=1234;
    private final int DOWNLOAD=888;
    private final int DOWNLOADVERIFY = 002;
    private Button buttonUpLoad = null;
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
    private String downLoadPath;
    private SiteInfo siteInfo;
    private boolean Isconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sftpmain);
        makeHandler();
        generateSftp();
        init();
        startConnectCheckThread();
    }
    //UI handler receive data and update UI
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
                    buttonUpLoad.setVisibility(Button.GONE);
                    remoteAdaptor.notifyDataSetChanged();
                    Isconnect=false;
                    bConnect.setVisibility(Button.VISIBLE);
                }
                if(msg.what == WAITING){
                    tCurPath.setText("Waiting......");
                }
                if(msg.what == LOADDIR){
                    Log.d(TAG,"show dir");
                    remoteAdaptor.notifyDataSetChanged();
                    Toast.makeText(SftpMenu.this,"Cd to"+currentPath,Toast.LENGTH_LONG).show();
                    }
                if(msg.what == DOWNLOADVERIFY){
                    Log.d(TAG,"Here");
                    Toast.makeText(SftpMenu.this,msg.obj.toString(),Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    protected void generateSftp(){
        siteInfo = (SiteInfo)getIntent().getSerializableExtra("siteInfo");
        //sftp = new SFTPUtils("SFTP服务器IP", "用户名", "密码");
        //sftp = new SFTPUtils("119.3.238.156", "siteadmin", "L1l2l3l4");
        sftp = new SFTPUtils(siteInfo.mHost_ip, siteInfo.mUsername, siteInfo.mPassward);
        //sftp=new SFTPUtils("47.103.117.157","siteadmin","guiwecgdiu");

    }
    protected void closeUI(){
        curPathFiles.clear();
       remoteAdaptor.notifyDataSetChanged();
    }
    protected void updateUI(){
        tCurPath.setText(currentPath+"["+sftp.isChannelConnected()+"]");
        buttonUpLoad.setVisibility(Button.VISIBLE);
        bConnect.setVisibility(Button.GONE);


    }

    protected void updateData(){
        currentPath = sftp.currentRemotePath();
        Isconnect = true;
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
                    //This thread is a observer
                        //detect is connecting....
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
        buttonUpLoad.setVisibility(Button.GONE);
        bConnect = findViewById(R.id.connect);
        bDisconnect = findViewById(R.id.disconnect);
        tCurPath=findViewById(R.id.serverPath);
        lRemoteList=findViewById(R.id.lRemoteList);
//        buttonCurrentDir = findViewById(R.id.current_location);
//        buttonListdirectory = findViewById(R.id.check_remoteList);
        remoteAdaptor = new RemoteFileAdaptor(this,R.layout.item_type1,curPathFiles);
        lRemoteList.setAdapter(remoteAdaptor);
    }

    public void toParenTorChild(String item){
        pathStack.push("/"+item);
        Message message = subHandler.obtainMessage(CDTODIR,item);
        message.what=CDTODIR;
        message.sendToTarget();
    }
    public void initListener(){
        //设置控件对应相应函数
        buttonUpLoad.setOnClickListener(this);
        bConnect.setOnClickListener(this);
        bDisconnect.setOnClickListener(this);
        lRemoteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ListView l = (ListView)parent;
                String item =(String) l.getAdapter().getItem(position);
                toParenTorChild(item);
            }
        });
        lRemoteList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String item = (String)parent.getItemAtPosition(position);
                AlertDialog aldg;
                AlertDialog.Builder adBd=new AlertDialog.Builder(SftpMenu.this);
                adBd.setTitle("My Dialog");
                adBd.setMessage("Do you want to download this file");
                adBd.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                        Toast.makeText(SftpMenu.this,"Start DownLoad",Toast.LENGTH_LONG).show();
                        Message msg = Message.obtain(subHandler);
                        msg.what=DOWNLOAD;
                        Bundle b = new Bundle();
                        b.putString("fn",item);
                        msg.setData(b);
                        msg.sendToTarget();
                    }
                });
                adBd.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                aldg=adBd.create();
                aldg.show();

                return true;
            }
        });

    }

    protected void updateDir(){
        while (true) {
            if(sftp.listFiles(getPathString())!=null) {
                Message m = Message.obtain();
                m.what = LOADDIR;
                //update the current listview
                //Use LoadDIR MSG to update UI
                curPathFiles.clear();
                curPathFiles.addAll(showChildNames(sftp.listFiles(getPathString())));
                mHandler.sendMessage(m);
                break;
            }
        }
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
                        //cd Directory, trigger by click a remote file..go deeper
                        if(msg.what == CDTODIR)
                        {
                            Message conncting=Message.obtain();
                            conncting.what=WAITING;
                            mHandler.sendMessage(conncting);
                            Log.d(TAG,sftp.currentRemotePath());
                            // Log.d(TAG,sftp.showChildNames(sftp.currentRemotePath()).toString());
                            sftp.cdDeeper(getPathString());
                        //wait for a while until the list file is not false then load the files
                                updateDir();
                        }
                            if(msg.what==DOWNLOAD){
                                Log.d(TAG,"line276");
                                String str = msg.getData().getString("fn");
                                // Log.d(TAG,downLoadPath + "is received at 260");
                                boolean isSuccess = sftp.downloadFile(currentPath,str,downLoadPath,str);

                                Log.d(TAG,isSuccess+"line276");
                                Message downloadVerify;
                                if(isSuccess == true){
                                     downloadVerify = Message.obtain(mHandler);
                                     downloadVerify.obj="Download Successed :]";
                                 }else{
                                    downloadVerify = Message.obtain(mHandler);
                                    downloadVerify.obj="Download Failed, try again plz :[";
                                 }
                                    downloadVerify.what=DOWNLOADVERIFY;
                                downloadVerify.sendToTarget();
                                }
                    }
                };
                switch (v.getId()) {
                    case R.id.button_upload: {
                        //上传文件
                        Log.d(TAG, "上传文件");


                        sftp.uploadFile(currentPath, "wall.gif", downLoadPath, "wall.gif");
                        updateDir();

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
                               //Log.d(TAG,sftp.listFiles(currentPath).toString());
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
//        //sftp = new SFTPUtils("SFTP服务器IP", "用户名", "密码");
//        sftp = new SFTPUtils("119.3.238.156", "siteadmin", "L1l2l3l4");
//        //sftp=new SFTPUtils("47.103.117.157","siteadmin","guiwecgdiu");
        pathStack = new Stack<String>();
        curPathFiles = new ArrayList<String>();
        downLoadPath= Environment.getExternalStorageDirectory().toString()+"/lazyDocument"+"/Remote";
        Isconnect = false;
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

       // myLooper.quit();//
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    protected void exit(){
        if(Isconnect){
            toParenTorChild("..");
        }else{
            finish();
        }

    }
}
// Java 获取文件后缀
//    public static void main(String[] args) {
//        File file = new File("HelloWorld.java");
//        String fileName = file.getName();
//        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
//        System.out.println(suffix);
//    }


