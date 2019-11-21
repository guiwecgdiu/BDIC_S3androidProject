package com.example.myapplication.Activity;

import android.content.Intent;
import android.os.Bundle;

public class SelectActivity extends FolderActivity {

    @Override
    public void operateFile(String fileName, String pathName) {
        Bundle b = new Bundle();
        Intent i = new Intent();
        b.putString("path",pathName);
        b.putString("file",fileName);
        setResult(1,i.putExtras(b));
        finish();
    }

}
