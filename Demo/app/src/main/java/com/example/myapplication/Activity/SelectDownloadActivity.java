package com.example.myapplication.Activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.FileUtils.FileUtils;

public class SelectDownloadActivity extends FolderActivity {


    @Override
    public void operateFolder(String name) {
        if(FileUtils.fileType(name) == "folder") {
            Bundle b = new Bundle();
            Intent i = new Intent();
            b.putString("path",getPathString());
            b.putString("file",name);
            setResult(2,i.putExtras(b));
            finish();
        }
    }
}
