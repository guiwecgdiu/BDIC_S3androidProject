package com.example.myapplication.Activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.FileUtils.FileUtils;
import com.example.myapplication.FileUtils.RequestCode;
import com.example.myapplication.R;
import com.example.myapplication.presentation.FileAdaptor;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Stack;

public class FolderActivity extends AppCompatActivity {
    final String TAG ="FolderActivitys";
    int i;
    /**
     * The global variables
     * @para lFolderlist   listview to show current folder structure
     * @para tCurPath    Textview to show the current directory path
     */

    TextView tCurPath;
    ListView lFolderlist;
    FileAdaptor fad;
    ArrayList<File> fileList;
    String rootPath;

    static String remotePath;
    static String localPath;

    Stack<String> curPathStack;

    /**
     * Initiate the view component
     * getExternalPubdir() | getExternalDir() is decrepated in api 29
     * DIRECTORY_DOCUMENTS
     * Standard directory in which to place documents that have been created by the user.
     */
    private void init(){

        //Init the data layer

        fileList=new ArrayList<File>();
        curPathStack = new Stack<String>();
        //rootPath = getFilesDir().getAbsolutePath();
      //  rootPath = Environment.getExternalStorageDirectory().toString();

            rootPath = loadLocalFolder().toString();
            Log.d(TAG, rootPath);
        curPathStack.push(rootPath);


        //read the file[] of external directory to a file array - in comment
        //now I use the internal directory version
        Log.d(TAG, "Sucessful read external storage with length = "+Environment.getExternalStorageDirectory());

        String folder =getIntent().getStringExtra("MainIntent");
        if(folder!=null){
                curPathStack.push("/"+folder);
        }
        lFolderlist =findViewById(R.id.lFolderExplore);
        tCurPath = findViewById(R.id.tCurrentDir);
        tCurPath.setText(getPathString());
        fad = new FileAdaptor(this,R.layout.item_type1,fileList);
        showChange(getPathString());
        initAdaptor();
        initListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        powerPermission();
        init();

    }

    protected File loadLocalFolder(){
        File file = new File(Environment.getExternalStorageDirectory().toString()+"/lazyDocument");
        return file;
    }


    /*
     * initiate list adaptor with folderlist
     * @param forderList array
     * @param lFolderlist v
     * @param forderlistAdaptor a
     */
    protected void initAdaptor(){
        lFolderlist.setAdapter(fad);
    }

    /*
        *init list item onclick listener
     */
    protected void initListener(){
        lFolderlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView item =(ListView) parent;
                File itemf = (File) parent.getAdapter().getItem(position);
                String name = itemf.getName();
                Log.d(TAG,name+"is clcked");
                operateFolder(name);
            }
        });
    }
    public void operateFolder(String name){
        if(FileUtils.fileType(name) == "folder") {
            curPathStack.push("/" + name);
            showChange(getPathString());
        }else{

            //Toast.makeText(FolderActivity.this,"It doesn't has children folder",Toast.LENGTH_LONG).show();
            operateFile(name,getPathString());
        }
    }


    public void operateFile(String fileName,String pathName){
        Toast.makeText(FolderActivity.this,"File:"+fileName + " locate: under "+pathName,Toast.LENGTH_LONG).show();
    }





    protected void showChange(String path){
        Log.d(TAG,path);
        File [] files = new File(path).listFiles();

            fileList.clear();
            for (int i = 0; i < files.length; i++) {
                fileList.add(files[i]);
            }


        fad.notifyDataSetChanged();
        tCurPath.setText(getPathString());
    }

    protected String getPathString(){
        Stack<String> temp = new Stack<String>();
        temp.addAll(curPathStack);
        String result= "";
        i=0;
        while (!temp.empty()){
            result=temp.pop()+result;
            i++;
        }
       // Log.d(TAG,i+"+++");
        return result;
    }






    protected void powerPermission(){
        if(  ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
                !=PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,"Line 89: The readExternal is denied");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }else{
            Log.d(TAG,"line95: The readExternal is granted");
        }

        if(  ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                !=PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,"Line 89: The writeExternal is denied");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    2);
        }else{
            Log.d(TAG,"line95: The writeExternal is granted");
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (requestCode == 1) {
            if(grantResults.length>0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"The read is setted",Toast.LENGTH_LONG);
            }else {
                //Permission denied
                Toast.makeText(this,"The permission is denied",Toast.LENGTH_LONG);
            }
        }

        if (requestCode == 2) {
            if(grantResults.length>0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"The write is setted",Toast.LENGTH_LONG);
            }else {
                //Permission denied
                Toast.makeText(this,"The permission is denied",Toast.LENGTH_LONG);
            }
        }
    }
}





