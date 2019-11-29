package com.example.myapplication.presentation;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.FileUtils.FileUtils;
import com.example.myapplication.R;

import java.io.File;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class RemoteFileAdaptor extends ArrayAdapter<String> {
    String TAG="temp";
   int resourceId;

    public RemoteFileAdaptor(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String fileName = getItem(position);
        View v = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        ImageView imageView = v.findViewById(R.id.iFileIcon);
        TextView textView = v.findViewById(R.id.iFileName);
        Log.d(TAG,fileType(fileName));

        imageView.setImageResource(FileUtils.getFileIcon(fileName));
        textView.setText(fileName);

        return v;
    }
    private String fileType(String fileName) {
        if (fileName.lastIndexOf(".") != -1 ) {
            String suffix = fileName.substring(fileName.lastIndexOf(".")).substring(1);
            return suffix;
        } else{
            return "folder";}
    }

}
