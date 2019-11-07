package com.example.myapplication.presentation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;

import java.io.File;
import java.util.List;

public class FileAdaptor extends ArrayAdapter<File> {

    private int resourceId;
    /**
     *
     * @param context
     * @param resource    item.xml
     * @param objects    the file list given
     */
    public FileAdaptor(Context context, int resource,List<File> objects) {
        super(context, resource,objects);
        resourceId = resource;
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
     File f= getItem(position);
     View v = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
     ImageView iIcon = v.findViewById(R.id.iFileIcon);
     TextView iFileName = v.findViewById(R.id.iFileName);

     iIcon.setImageResource(R.mipmap.folder);
     iFileName.setText(f.getName());

    return v;
    }
}
