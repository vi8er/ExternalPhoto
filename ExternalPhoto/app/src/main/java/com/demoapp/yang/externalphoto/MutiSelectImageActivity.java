package com.demoapp.yang.externalphoto;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by YANG on 2018/5/9.
 */

public class MutiSelectImageActivity extends AppCompatActivity implements View.OnClickListener{

    private Activity activity;
    private TextView tv_back,tv_ok;
    private RecyclerView rv_list;
    private ArrayList<ImageBean> imageList = new ArrayList<>();
    private ArrayList<ImageBean> tempList = new ArrayList<>();
    private ImageAdapter adapter;
    private MyHandler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutiselect_image);
        activity = this;
        handler = new MyHandler(activity);

        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(this);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(this);

        rv_list = (RecyclerView) findViewById(R.id.rv_list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(activity,3);
        rv_list.setLayoutManager(layoutManager);
        adapter = new ImageAdapter(activity);
        adapter.setData(imageList);
        rv_list.setAdapter(adapter);

        isGrantExternalRW(activity);
        activity.getLoaderManager().initLoader(0 , null ,loaderCallbacks);
    }

    /**
     * 6.0以上这俩权限要在用的地方手动申请
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:{
                finish();
                break;
            }
            case R.id.tv_ok:{
                getSelectImage();
                finish();
                break;
            }
        }
    }

    private void getSelectImage(){
        if (imageList.size() <= 0){
            return;
        }
        ArrayList<String> seletced = new ArrayList<>();
        for(ImageBean bean : imageList){
            if (bean.isSelect){
                seletced.add(bean.uri.getPath());
            }
        }
        Intent intent = new Intent();
        intent.putStringArrayListExtra("selectImages",seletced);
        setResult(RESULT_OK,intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        activity.getLoaderManager().destroyLoader(0);
    }

    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED
        };


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            CursorLoader cursorLoader = new CursorLoader(activity,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    IMAGE_PROJECTION,
                    MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpg", "image/jpeg", "image/png"},
                    IMAGE_PROJECTION[2] + " DESC");

            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null){
                if (data.getCount() > 0){
                    data.moveToFirst();
                    ImageBean bean;
                    File file;
                    do {
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long time = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        file = new File(path);
                        if (file.exists()){
                            bean = new ImageBean();
                            bean.time = time;
                            bean.name = name;
                            bean.uri = Uri.parse(path);
                            tempList.add(bean);
                        }
                    } while (data.moveToNext());
                    data = null;
                    if (tempList.size() > 0){
                        handler.sendEmptyMessage(GET_DATA_SUCCESS);
                    } else {
                        handler.sendEmptyMessage(GET_DATA_FAIL);
                    }
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

    };

    private static final int GET_DATA_SUCCESS = 4001;
    private static final int GET_DATA_FAIL = 4002;
    public class MyHandler extends Handler{
        WeakReference<Activity> weakReference;

        public MyHandler(Activity activity) {
            super();
            weakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET_DATA_SUCCESS:{
                    imageList.clear();
                    imageList.addAll(tempList);
                    tempList.clear();
                    adapter.notifyDataSetChanged();
                    break;
                }
                case GET_DATA_FAIL:{
                    Toast.makeText(activity,"相册中没有图片!",Toast.LENGTH_LONG);
                    break;
                }
            }
        }
    }
}
