package com.example.myapplication.presentation;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.Model.SiteInfo;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class SiteInfoAdapter extends RecyclerView.Adapter<SiteInfoAdapter.ViewHolder> {

    private  List<SiteInfo> mSiteInfoList;
    private OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public SiteInfo getItem(int postion){
        return mSiteInfoList.get(postion);
    }
    public interface OnItemClickListener
    {
        //子条目单机事件
        void onItemClick(View view, int position);
        //子条目长按事件
        void onItemLongClick(View view,int position);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        SiteInfo siteinfo = mSiteInfoList.get(position);
        holder.mUsername.setText(siteinfo.getUsername());
        holder.mIpaddress.setText(siteinfo.getmHost_ip());
        holder.mSiteName.setText(siteinfo.getName());

        if (mOnItemClickListener != null)
        {
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //返回对应view的信息
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView,pos);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView,pos);
                    return false;
                }
            });
        }




    }
    ViewGroup viewParent;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.siteinfo_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        viewParent=parent;
        return holder;
    }



    @Override
    public int getItemCount() {
        return mSiteInfoList.size();
    }

    public  SiteInfoAdapter (List<SiteInfo> siteInfosList){
       mSiteInfoList=siteInfosList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView mIpaddress;
        TextView mSiteName;
        TextView mUsername;

        public ViewHolder (View view)
        {
            super(view);
            mIpaddress=view.findViewById(R.id.item_siteIp);
            mSiteName = view.findViewById(R.id.item_siteName);
            mUsername=view.findViewById(R.id.item_username);
        }

    }
}




