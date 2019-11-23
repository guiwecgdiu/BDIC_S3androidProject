package com.example.myapplication.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;
import android.support.v7.app.AlertDialog;


public class SftpMenu extends Activity implements View.OnClickListener {

    /*
        Debug variable
    */
    private final String TAG = "SftpMenu";

    /*
        Data variable
        pathStack Keep current path

     */
    Stack<String> pathStack;
    String rootPath;
    private ArrayList<String> curPathFiles;
    private String downLoadPath;


    /*
        Sftp state variable
     */
    private boolean isConnect;
    private boolean isTransfer;
    private boolean isWaiting;

    /*
        Message handler and ui updator
        stfp entity
        Msg receiver of sftp entity
        Observer thread listen to the change of the data and state and notify ui handler
        Site entity
     */
    Handler uiHandler;
    private SFTPUtils sftp;
    private Handler sftpHandler;
    private Thread observerThread;
    private SiteInfo siteInfo;

   /*
        MSG type;
    */
    private final int MSG_CONNECT = 001; private final int MSG_DISCONNECT = 002;
    private final int MSG_LOADDIR = 003;  private final int MSG_WAIRTING = 004;
    private final int CDTODIR=1234;
    private final int DOWNLOAD=888;
    private final int DOWNLOAD_TOAST = 000;
    private final int SELECT_TOAST = 010;

    /*
        Widgets
     */
    private Button buttonUpLoad = null; private Button bConnect = null;
    private Button bDisconnect = null;
    private ListView lRemoteList; private TextView tCurPath;
    private RemoteFileAdaptor remoteAdaptor=null;
    private String currentPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sftpmain);
        validInternet();
        init();
    }

    protected void validInternet(){
        AlertDialog aldg;
        AlertDialog.Builder adBd=new AlertDialog.Builder(SftpMenu.this);
        adBd.setTitle("Internet Checker");
        adBd.setMessage("Do you have a valid internet connection?");
        adBd.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        adBd.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        aldg=adBd.create();
        aldg.show();
    }

    //UI handler receive data and update UI
    protected void UIUpdateHandler(){
        uiHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == MSG_CONNECT){

                        updateUI();
                        updateData();

                }
                if(msg.what == MSG_DISCONNECT){
                    tCurPath.setText("Disconnect"+"["+sftp.isChannelConnected()+"]");
                    currentPath=null;
                    curPathFiles.clear();
                    pathStack.clear();
                    buttonUpLoad.setVisibility(Button.GONE);
                    remoteAdaptor.notifyDataSetChanged();
                    bConnect.setVisibility(Button.VISIBLE);
                }

                if(msg.what == MSG_WAIRTING){
                    tCurPath.setText("Waiting......");
                }

                if(msg.what == MSG_LOADDIR){
                    Log.d(TAG,"show dir");
                    remoteAdaptor.notifyDataSetChanged();
                    Toast.makeText(SftpMenu.this,"Cd to"+currentPath,Toast.LENGTH_LONG).show();
                    }
                if(msg.what == DOWNLOAD_TOAST){
                    Log.d(TAG,"Here");
                    Toast.makeText(SftpMenu.this,msg.obj.toString(),Toast.LENGTH_LONG).show();
                }
                if(msg.what==SELECT_TOAST){
                    Toast.makeText(SftpMenu.this,msg.obj.toString(),Toast.LENGTH_LONG).show();
                }
            }
        };
    }
    /*
    Init Data
     */
    protected void generateSftp(){
        siteInfo = (SiteInfo)getIntent().getSerializableExtra("siteInfo");
        //sftp = new SFTPUtils("SFTP服务器IP", "用户名", "密码");
        //sftp = new SFTPUtils("119.3.238.156", "siteadmin", "L1l2l3l4");
        sftp = new SFTPUtils(siteInfo.mHost_ip, siteInfo.mUsername, siteInfo.mPassward);
        //sftp=new SFTPUtils("47.103.117.157","siteadmin","guiwecgdiu");

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
                cdViaItem(item);
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
                        onStartDownload();
                        Message msg = Message.obtain(sftpHandler);
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
    protected void updateUI(){
        tCurPath.setText(currentPath+"["+sftp.isChannelConnected()+"]");
        buttonUpLoad.setVisibility(Button.VISIBLE);
        bConnect.setVisibility(Button.GONE);


    }
    protected void updateData(){
        currentPath = sftp.currentRemotePath();
        isConnect = true;
    }
    protected void updateDir(){
        while (true) {
            if(sftp.listFiles(getPathString())!=null) {
                Message m = Message.obtain();
                m.what = MSG_LOADDIR;
                curPathFiles.clear();
                curPathFiles.addAll(showChildNames(sftp.listFiles(getPathString())));
                curPathFiles.remove(".");

                uiHandler.sendMessage(m);
                break;
            }else {

                Message msg = uiHandler.obtainMessage();
                msg.what=MSG_WAIRTING;
                msg.sendToTarget();
            }
        }
    }
    protected void ObserverThread() {
        observerThread = new Thread(new Runnable()
        {
            private String lastPath;
            @Override
            public void run()
            {

                while (true) {


                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //This thread is a observer
                    //detect is connecting....
                    if (sftp.isChannelConnected() == true) {
                        Log.d(TAG, "Current path is" + sftp.currentRemotePath());//important without pwd can't read
                        Message msg = Message.obtain();
                        msg.what = MSG_CONNECT;
                        uiHandler.sendMessage(msg);
                        isConnect = true;

                    } else {
                        Message msg = Message.obtain();
                        msg.what = MSG_DISCONNECT;
                        uiHandler.sendMessage(msg);
                        isConnect = false;
                    }


                    lastPath=getPathString();
                }
            }

        });
        observerThread.start();
    }
    private void cdViaItem(String item){
        pathStack.push("/"+item);
        Message message = sftpHandler.obtainMessage(CDTODIR,item);
        message.what=CDTODIR;
        message.sendToTarget();
    }
    public void backPath(){
        pathStack.pop();
        Message message = sftpHandler.obtainMessage(CDTODIR);
        message.what=CDTODIR;
        message.sendToTarget();
    }   //no more use




    Looper myLooper;
    /*
       The onclick contain the sftp entity
     */

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
                sftpHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        //cd Directory, trigger by click a remote file..go deeper

                        if(msg.what == CDTODIR)
                        {
                            Log.d(TAG,sftp.currentRemotePath());
                            sftp.cdDeeper(getPathString());
                        //wait for a while until the list file is not false then load the files
                                updateDir();
                        }
                        if(msg.what==DOWNLOAD){
                                Log.d(TAG,"line276");
                                String str = msg.getData().getString("fn");
                                boolean isSuccess = sftp.downloadFile(currentPath,str,downLoadPath,str);
                                Log.d(TAG,isSuccess+"line276");
                               onFinishDownload(isSuccess);
                                }
                        }
                };


                switch (v.getId()) {
                    case R.id.button_upload: {
                        //上传文件
                        Log.d(TAG, "上传文件");
                        String[] para =onstartUpload();
                        while(true){
                            if(para[0]=="null" || para[1] == "null"){

                            }else{
                                Message select=uiHandler.obtainMessage();
                                select.what=SELECT_TOAST;
                                select.obj="break";
                                select.sendToTarget();

                                break;
                            }
                        }
                        String source=para[0];
                        String filename=para[1];
                        Message select=uiHandler.obtainMessage();
                        select.what=SELECT_TOAST;
                        select.obj="Luckily"+filename +"are uploaded from source path:"+source;
                        select.sendToTarget();
                        sftp.uploadFile(currentPath, filename, downLoadPath, filename);

                        updateDir();
                        onFinishUpload();
                    }
                    break;

                    case R.id.connect:{
                        sftp.connect();
                        Message conncting=Message.obtain();
                        conncting.what= MSG_WAIRTING;
                        uiHandler.sendMessage(conncting);
                        Log.d(TAG,"Connect Start");
                       rootPath=sftp.currentRemotePath();
                            pathStack.push(rootPath);
                            updateDir();

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
    String [] onuploadParas={"null","null"};
    private String[] onstartUpload(){
        int requestCode = 9;
        Intent getFilename = new Intent(this,SelectActivity.class);

        startActivityForResult(getFilename,requestCode);

            return onuploadParas;
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==9 && resultCode==1){
            String file = data.getStringExtra("file");
            String path = data.getStringExtra("path");
            onuploadParas[1]=file;
            onuploadParas[0]=path;
        }
    }

    private void onFinishUpload(){ }
    protected void onStartDownload(){
        Toast.makeText(SftpMenu.this,"Start DownLoad",Toast.LENGTH_LONG).show();
    }
        private void onFinishDownload(boolean isSuccess){
            Message downloadVerify;
            if(isSuccess == true){
                downloadVerify = Message.obtain(uiHandler);
                downloadVerify.obj="Download Successed :]";
            }else{
                downloadVerify = Message.obtain(uiHandler);
                downloadVerify.obj="Download Failed, try again plz :[";
            }
            downloadVerify.what= DOWNLOAD_TOAST;
            downloadVerify.sendToTarget();
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
        initState();
        initData();
        initViews();
        initListener();
        initComponent();
    }
    protected void initComponent(){
        generateSftp();
        ObserverThread();
        UIUpdateHandler();
    }
    protected void initState(){
        this.isConnect=false;
        this.isTransfer=false;
        this.isWaiting=false;
    }
    protected void  initData(){
//        //sftp = new SFTPUtils("SFTP服务器IP", "用户名", "密码");
//        sftp = new SFTPUtils("119.3.238.156", "siteadmin", "L1l2l3l4");
//        //sftp=new SFTPUtils("47.103.117.157","siteadmin","guiwecgdiu");
        pathStack = new Stack<String>();
        curPathFiles = new ArrayList<String>();
        downLoadPath= Environment.getExternalStorageDirectory().toString()+"/lazyDocument"+"/Remote";

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
    public void onBackPressed() {
        exit();
    }
    protected void exit(){
        if(isConnect){
            if(curPathFiles.contains("..")) {
                cdViaItem("..");
               // pathStack.pop();
            }
        }else{
            finish();
        }

    }
}



