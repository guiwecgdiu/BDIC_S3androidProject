package com.example.myapplication.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
        init();
        initClickListenner();
    }
}
