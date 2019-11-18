package com.example.myapplication.presentation;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.Model.SiteInfo;
import com.example.myapplication.R;

import java.util.ArrayList;

public class GridAdaptor extends BaseAdapter {
    private ArrayList<SiteInfo> mFileList = new ArrayList<SiteInfo>();
    private LayoutInflater mInflater;
    private Context mContext;
    private int mResource;

    public GridAdaptor(Context context, ArrayList<SiteInfo> siteInfoList,int resource){
        mContext = context;
        mFileList = siteInfoList;
        mResource = resource;
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

            convertView=mInflater.inflate(mResource,null);
            TextView hostName = convertView.findViewById(R.id.grid_hostname);
            TextView ipAddress = convertView.findViewById(R.id.grid_ip_address);
            hostName.setBackgroundColor(Color.BLUE);
            SiteInfo siteinfo = (SiteInfo)getItem(position);
            hostName.setText(siteinfo.mSite_Name);
            hostName.setBackgroundResource(R.mipmap.folder);
            ipAddress.setText(siteinfo.mHost_ip);

        return convertView;
    }


}
