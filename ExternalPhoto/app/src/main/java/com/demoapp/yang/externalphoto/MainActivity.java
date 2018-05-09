package com.demoapp.yang.externalphoto;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView tv_skip,tv_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_skip = (TextView) findViewById(R.id.tv_skip);
        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this,MutiSelectImageActivity.class),1001);
            }
        });
        tv_data = (TextView) findViewById(R.id.tv_data);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == 1001) {
                ArrayList<String> list = data.getStringArrayListExtra("selectImages");
                if (list != null && list.size() > 0) {
                    String str = "";
                    for (String s : list) {
                        str += s;
                    }
                    tv_data.setText(str);
                }
            }
        }
    }
}
