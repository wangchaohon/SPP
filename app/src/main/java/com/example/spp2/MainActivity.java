package com.example.spp2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //绑定按钮
        Button bt0 = (Button) findViewById(R.id.button0);
        Button bt1 = (Button) findViewById(R.id.button1);
        Button bt2 = (Button) findViewById(R.id.button2);
        Button bt3 = (Button) findViewById(R.id.button3);
        Button bt4 = (Button) findViewById(R.id.button4);
        //新建一个监视器类对象
        MyClickListener mcl = new MyClickListener();
        //button注册监视器
        bt0.setOnClickListener(mcl);
        bt1.setOnClickListener(mcl);
        bt2.setOnClickListener(mcl);
        bt3.setOnClickListener(mcl);
        bt4.setOnClickListener(mcl);

        ListView listView = (ListView) findViewById(R.id.ListView0);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
            }
        });
    }

    class MyClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

        }
    }
}