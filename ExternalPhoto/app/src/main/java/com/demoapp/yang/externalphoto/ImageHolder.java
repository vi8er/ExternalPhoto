package com.demoapp.yang.externalphoto;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

/**
 * Created by YANG on 2018/5/9.
 */

public class ImageHolder extends RecyclerView.ViewHolder {

    public ImageView iv_image;
    public CheckBox checkBox;

    public ImageHolder(View itemView) {
        super(itemView);
        iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
        checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        checkBox.setChecked(false);
    }

    public void changeStatus(boolean check){
        checkBox.setChecked(check);
    }

}
