package com.example.myapplication.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.FileUtils.RequestCode;
import com.example.myapplication.R;

public class MainMenu extends AppCompatActivity {
    final String TAG = "MainMenu";
    /**
     *  2019/11/6
    * @param gofolder: folderButton
    * @param goServer  serverButton
     */
    Button gofolder;
    Button goServer;

    /*
    * 2019/11/6
    * Set the click listenner
    * @param gofolder
    * @param goServer
     */
    private void initClickListenner(){

        gofolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMenu.this, FolderActivity.class);
                startActivity(i);
            }
        });

        goServer.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View v) {
                Intent i= new Intent(MainMenu.this, SftpMenu.class);
                startActivity(i);
            }
        });
    }

    /*
     * 2019/11/6
    * Init the components
    * @param goServer
    * @param goFolder
     */


    private void init(){
      goServer=findViewById(R.id.bServer_mainmenu);
      gofolder= findViewById(R.id.bFolder_mainmenu);


    }


    /** 2019/11/6 
     * The start of a lifecycle
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        powerPermission();
        init();
        initClickListenner();



     //   loadFilelistWithpermission();
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

//    protected void powerPermission(){
//        if(  ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
//                        !=PackageManager.PERMISSION_GRANTED){
//            Log.d(TAG,"Line 89: The readExternal is denied");
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                    1);
//        }else{
//            Log.d(TAG,"line95: The readExternal is granted");
//        }
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//
//        if (requestCode == 1) {
//            if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
//                Toast.makeText(this,"The read is setted",Toast.LENGTH_LONG);
//            }else {
//                //Permission denied
//                Toast.makeText(this,"The permission is denied",Toast.LENGTH_LONG);
//            }
//        }
//    }

    /**
     * For each devices with ssdk>23, it need a dynamically permission check
     * It can be five steps:
     * Check Platform - Not cluded in this codes
     * Check the permission - checkSelfPermission.....
     *If true do process, if No, Explain request to permission  - shouldShow......
     *          if the should.. be selected with 'don't show again' it return false, else it return true, and give permission disabled to devices
     * Request the permission - requestthepermission
     *           It will be called as a callback method by the should...its behaviour base on the user's select
     * Handle the requst
     */

    protected void  loadFilelistWithpermission(){
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
        {


        }else {
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this,"In my app, the permission is needed",Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RequestCode.REQUEST_WESTORAGE);
        }
    }


}
