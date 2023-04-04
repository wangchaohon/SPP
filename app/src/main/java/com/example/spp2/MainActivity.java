package com.example.spp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    BlueToothController blueToothController = new BlueToothController();
    public ArrayList<String> requestList = new ArrayList<>();
    public static MainActivity instance = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
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
        //重写列表点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        //判断是否设备支持蓝牙
        if(!blueToothController.isSupportBlueTooth())
        {
            Toast.makeText(getApplicationContext(), "设备不支持蓝牙，3s后退出app", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run() {
                    instance.finish();
                    System.exit(0);
                }
            },3000);
        }
        //获取并检查手机蓝牙权限
        GetPermission();
        if(!CheckPermision())
        {
            Toast.makeText(getApplicationContext(), "设备不支持蓝牙，5s后退出app", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run() {
                    instance.finish();
                    System.exit(0);
                }
            },5000);
        }
        
    }
    //重写button点击事件
    class MyClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

        }
    }
    //定义一个蓝牙控制器类
    public class BlueToothController{
        private BluetoothAdapter mAdapter;//定义一个蓝牙适配器
        //添加构造函数
        public BlueToothController(){
            mAdapter = mAdapter.getDefaultAdapter();
        }
        //判断是否支持蓝牙
        public boolean isSupportBlueTooth(){
            if(mAdapter != null){//不为空则支持蓝牙
                return true;
            }
            else{
                return false;
            }
        }
    }
    public void GetPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestList.add(Manifest.permission.BLUETOOTH_SCAN);
            requestList.add(Manifest.permission.BLUETOOTH_ADVERTISE);
            requestList.add(Manifest.permission.BLUETOOTH_CONNECT);
            requestList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            requestList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            requestList.add(Manifest.permission.BLUETOOTH);
            requestList.add(Manifest.permission.BLUETOOTH_ADMIN);
        }
        if(requestList.size() != 0){
            //Toast.makeText(getApplicationContext(), "requestList ", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, requestList.toArray(new String[0]), 1);
        }
    }

    public boolean CheckPermision()
    {
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)) {//待做：之后根据这个更改下检查权限的问题
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getApplicationContext(), "BLUETOOTH_SCAN 无权限操作", Toast.LENGTH_SHORT).show();
            return false;
        }
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED))
        {
            Toast.makeText(getApplicationContext(), "BLUETOOTH_ADVERTISE 无权限操作", Toast.LENGTH_SHORT).show();
            return false;
        }
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED))
        {
            Toast.makeText(getApplicationContext(), "BLUETOOTH_CONNECT 无权限操作", Toast.LENGTH_SHORT).show();
            return false;
        }
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED))
        {
            Toast.makeText(getApplicationContext(), "ACCESS_FINE_LOCATION 无权限操作", Toast.LENGTH_SHORT).show();
            return false;
        }
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED))
        {
            Toast.makeText(getApplicationContext(), "ACCESS_COARSE_LOCATION 无权限操作", Toast.LENGTH_SHORT).show();
            return false;
        }
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED))
        {
            Toast.makeText(getApplicationContext(), "BLUETOOTH 无权限操作", Toast.LENGTH_SHORT).show();
            return false;
        }
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED))
        {
            Toast.makeText(getApplicationContext(), "BLUETOOTH_ADMIN 无权限操作", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}