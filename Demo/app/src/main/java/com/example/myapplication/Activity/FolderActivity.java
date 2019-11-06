package com.example.myapplication.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.R;

import java.io.File;
import java.util.ArrayList;

public class FolderActivity extends AppCompatActivity {

    /**
     * The global variables
     * @para lFolderlist   listview to show current folder structure
     * @para tCurPath    Show the current directory path
     */

    TextView tCurPath;
    ListView lFolderlist;
    ArrayList<File> fileList;

    /**
     * Initiate the view component
     */
    private void init(){

        lFolderlist =findViewById(R.id.lFolderExplore_actFolder);
        tCurPath = findViewById(R.id.tCurrentDir_actFolder);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
    }


    /**
     * initiate list adaptor with folderlist
     * @param forderList
     * @param lFolderlist
     * @param forderlistAdaptor
     */


}
