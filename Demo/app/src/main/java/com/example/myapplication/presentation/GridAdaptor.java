package com.example.myapplication.presentation;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.Model.SiteInfo;
import com.example.myapplication.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class GridAdaptor extends BaseAdapter {
    private ArrayList<File> mFileList;
    private LayoutInflater mInflater;
    private Context mContext;

    public GridAdaptor(Context context, ArrayList<File> folderList){
        mContext = context;
        mFileList = folderList;
        mInflater=LayoutInflater.from(mContext);
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return mFileList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        convertView=mInflater.inflate(R.layout.item_grid,null);
        TextView folder_name = convertView.findViewById(R.id.folderName);
        ImageView imgview=convertView.findViewById(R.id.folderIcon);
        ImageView vertical_three = convertView.findViewById(R.id.vert_three);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(400,105);
        params.setMargins(400,60,0,0);
      //  vertical_three.setLayoutParams(new LinearLayout.LayoutParams(105, 85));
      //  vertical_three.setLayoutParams();
        vertical_three.setLayoutParams(params);
        Random random=new Random();
        int color=Color.argb(255,random.nextInt(256),random.nextInt(256),random.nextInt(256));

        imgview.setColorFilter(color);

        convertView.setLayoutParams(new GridView.LayoutParams(480, 210));

       // GridView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
        File f= (File)getItem(position);;
        folder_name.setText(f.getName());//xxxxxxxxxxxxxx
        return convertView;



    }


}
