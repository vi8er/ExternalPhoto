package com.demoapp.yang.externalphoto;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by YANG on 2018/5/9.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageHolder> {

    private Activity activity;
    private ArrayList<ImageBean> list = new ArrayList<>();

    public ImageAdapter(Activity activity) {
        super();
        this.activity = activity;
    }

    public void setData(ArrayList<ImageBean> data){
        this.list = data;
        notifyDataSetChanged();
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_image,null);
        ImageHolder holder = new ImageHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ImageHolder holder, int position) {
        final ImageBean bean  = list.get(position);
        holder.iv_image.setImageURI(bean.uri);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bean.isSelect = !bean.isSelect;
                holder.changeStatus(bean.isSelect);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
