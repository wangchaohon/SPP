package com.example.spp2;

import static android.bluetooth.BluetoothDevice.BOND_BONDED;
import static android.bluetooth.BluetoothDevice.BOND_BONDING;
import static android.bluetooth.BluetoothDevice.BOND_NONE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    BlueToothController blueToothController = new BlueToothController();
    public ArrayList<String> requestList = new ArrayList<>();
    public static MainActivity instance = null;
    public ArrayList<String> arrayList = new ArrayList<>();
    public ArrayList<String> deviceName = new ArrayList<>();
    private IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    public ArrayAdapter adapter1;
    Set<BluetoothDevice> deviceList = null;
    BluetoothSocket bluetoothSocket = null;
    UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CharSequence content = ((TextView) view).getText();
                String con = content.toString();
                Log.e("setOnItemClickListener", "con:" + content.toString());
                String[] conArray = con.split("\n");
                Log.e("setOnItemClickListener", "conArray[0]:" + conArray[0] + "conArray[1]" + conArray[1] + "conArray[2]" + conArray[2]);
                String rightStr = conArray[1].substring(9, conArray[1].length());//获取蓝牙地址
                Log.e("setOnItemClickListener", "rightStr" + rightStr);
                BluetoothDevice device = blueToothController.mAdapter.getRemoteDevice(rightStr);//根据地址找到相应的设备
                try {
                    if (device.getBondState() == BOND_NONE)//未配对的进行配对，否则取消配对
                    {
                        blueToothController.mAdapter.cancelDiscovery();//获取到设备状态后就取消搜索了
                        deviceName.remove(con);
                        device.createBond();
                        con = "Name :" + device.getName() + "\n" + "Address :" + device.getAddress() + "\n" + "State :已配对" + "\n";
                        deviceName.add(con);
                        adapter1.notifyDataSetChanged();
                    } else {
                        //Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
                        blueToothController.mAdapter.cancelDiscovery();//获取到设备状态后就取消搜索了
                        Method method = BluetoothDevice.class.getMethod("removeBond");
                        method.invoke(device);
                        deviceName.remove(con);
                        con = "Name :" + device.getName() + "\n" + "Address :" + device.getAddress() + "\n" + "State :未配对" + "\n";
                        deviceName.add(con);
                        adapter1.notifyDataSetChanged();
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        //判断是否设备支持蓝牙
        if (!blueToothController.isSupportBlueTooth()) {
            Toast.makeText(getApplicationContext(), "设备不支持蓝牙，3s后退出app", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    instance.finish();
                    System.exit(0);
                }
            }, 3000);
        }
        //获取并检查手机蓝牙权限
        GetPermission();
        if (!CheckPermision()) {
            Toast.makeText(getApplicationContext(), "设备不支持蓝牙，5s后退出app", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    instance.finish();
                    System.exit(0);
                }
            }, 5000);
        }
        if (!blueToothController.getBlueToothStatus()) {
            blueToothController.turnOnBlueTooth(1);
        }
        adapter1 = new ArrayAdapter(instance, R.layout.bluetooth_list_item, deviceName);//数组适配器
        //注册广播和过滤器
        foundFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        foundFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothReceiver, foundFilter);
        listView.setAdapter(adapter1);
    }

    //重写button点击事件
    class MyClickListener implements View.OnClickListener {


        boolean isConnected = false;
        TextView textView = (TextView)findViewById(R.id.TextView0);
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button0:
                    arrayList.clear();
                    deviceName.clear();
                    blueToothController.findDevice();
                    break;
                case R.id.button1:
                    if (ActivityCompat.checkSelfPermission(instance, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        Toast.makeText(getApplicationContext(), "无权限操作", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    deviceList = blueToothController.mAdapter.getBondedDevices();
                    for (BluetoothDevice device : deviceList)
                    {
                        try {
                            Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                            isConnectedMethod.setAccessible(true);
                            isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                            if(isConnected)
                            {
                                bluetoothSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);
                                bluetoothSocket.connect();
                                if(bluetoothSocket.isConnected())
                                {
                                    Toast.makeText(getApplicationContext(), "spp连接成功", Toast.LENGTH_SHORT).show();
                                    textView.setBackgroundColor(Color.parseColor("#4CAF50"));
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(), "spp连接失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
            }
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
        public boolean getBlueToothStatus(){//获取蓝牙状态
            // 断言,为了避免mAdapter为null导致return出错
            assert (mAdapter != null);
            // 蓝牙状态
            return mAdapter.isEnabled();
        }
        //打开蓝牙
        public void turnOnBlueTooth( int requestCode){
            try{
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivityForResult(intent, requestCode);
            }catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        public boolean findDevice(){
            assert(mAdapter!=null);
            try{
                return mAdapter.startDiscovery();//需要打开定位
            }catch (SecurityException e) {
                e.printStackTrace();
            }
            return false;
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
    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();//Intent要完成的动作
            if (BluetoothDevice.ACTION_FOUND.equals(action))//如果要完成的动作是发现设备操作
            {
                String s;
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                /*
                * 系统发现新的蓝牙设备了之后，会通过广播把这个设备的信息发送出来。所以我们要通过截获 Action 为BluetoothDevice.ACTION_FOUND的 Intent，并得到设备信息
                * */
                try{
                    if(device.getBondState()==BOND_NONE)
                    {
                        s = "Name :" + device.getName() + "\n" + "Address :" + device.getAddress() + "\n" + "State :未配对" + "\n";
                    }
                    else if(device.getBondState()==BOND_BONDING)
                    {
                        s = "Name :" + device.getName() + "\n" + "Address :" + device.getAddress() + "\n" + "State :配对中" + "\n";
                    }
                    else
                    {
                        s = "Name :" + device.getName() + "\n" + "Address :" + device.getAddress() + "\n" + "State :已配对" + "\n";
                    }
                    if(!deviceName.contains(s))
                    {
                        deviceName.add(s);
                        arrayList.add(device.getAddress());
                        adapter1.notifyDataSetChanged();
                    }
                }catch (SecurityException e) {
                    e.printStackTrace();
                }

            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))//搜索完成
            {
                Toast.makeText(getApplicationContext(), "搜索完成", Toast.LENGTH_SHORT).show();
                //unregisterReceiver(this);
            }else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                Toast.makeText(getApplicationContext(), "开始搜索", Toast.LENGTH_SHORT).show();
            }
        }
    };
}