package com.example.myapplication.presentation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class RemoteFileAdaptor extends ArrayAdapter<String> {
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
        imageView.setImageResource(R.mipmap.folder);
        textView.setText(fileName);

        return v;
    }
}
