package com.example.phobes.witnessassitant.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.service.BlueToothThread;
import com.example.phobes.witnessassitant.util.ClsUtils;
import com.example.phobes.witnessassitant.util.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by YLS on 2016/9/11.
 */
public class  SetMessageAcitivity extends AppCompatActivity {

    public static final String SET_MESSAGE = "SP";
    public static final String WIFI = "wifi";
    public static final String BLUETOOTH = "blueTooth";
    public static final String IP_ADDRESS = "ipstr";
    public static final String PORT = "port";
    public static final String MAC_ADDRESS = "mac_address";
    public static final String DEVICE_TYPE = "device_type";
    private static final int REQUEST_ENABLE_BT = 2;
    private RadioButton rbWifi;
    private RadioButton rbBlueTooth;
    private EditText etIPAddress;
    private EditText etPort;
    private ListView pairedDevices;
    private ListView newDevices;
    private TextView titleNewDevices;
    private Toolbar toolbar;
    private Button btnSaveSet;
    private Button btnCancelSet;
    private Button btnScan;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private BluetoothAdapter mBtAdapter;
    private  String macAddress="";
    private View layoutBluetooth;
    private String address;
    private WifiManager wifiManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_message_activity);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        initView();
        addEvent();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(SetMessageAcitivity.this)
                .setMessage(message)
                .setPositiveButton("确定", okListener)
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 321:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    setWifiApEnabled(true);
                } else {
                    // Permission Denied
                    Snackbar.make(btnScan, "拒绝打开WIFI热点", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initView(){
        toolbar= (Toolbar) findViewById(R.id.common_toolbar);
        rbWifi= (RadioButton) findViewById(R.id.rb_wifi);
        rbBlueTooth= (RadioButton) findViewById(R.id.rb_bluetooth);
        etIPAddress= (EditText) findViewById(R.id.et_ip_address);
        etPort= (EditText) findViewById(R.id.et_port);
        pairedDevices= (ListView) findViewById(R.id.paired_devices);
        newDevices= (ListView) findViewById(R.id.new_devices);
        titleNewDevices= (TextView) findViewById(R.id.title_new_devices);
        btnSaveSet= (Button) findViewById(R.id.button_save_set);
        btnCancelSet= (Button) findViewById(R.id.button_cancel_set);
        btnScan= (Button) findViewById(R.id.button_scan);
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,R.layout.device_name_list);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name_list);
        layoutBluetooth=findViewById(R.id.layout_bluetooth);
        etPort.setVisibility(View.GONE);
        etIPAddress.setVisibility(View.GONE);
        if(toolbar!=null){
            toolbar.setTitle("设置通信");
        }
        pairedDevices.setAdapter(mPairedDevicesArrayAdapter);
        pairedDevices.setOnItemClickListener(mDeviceClickListener);

        // Find and set up the ListView for newly discovered devices
        newDevices.setAdapter(mNewDevicesArrayAdapter);
        newDevices.setOnItemClickListener(mDeviceClickListener);

        SharedPreferences sp = SetMessageAcitivity.this.getSharedPreferences("SP", SetMessageAcitivity.this.MODE_PRIVATE);
        String deviceType= sp.getString(DEVICE_TYPE, "");
        if(deviceType.equals(WIFI)) {
            layoutBluetooth.setVisibility(View.GONE);
            rbWifi.setChecked(true);
            rbBlueTooth.setChecked(false);
           // etIPAddress.setText(sp.getString("ipstr", ""));
           // etPort.setText(sp.getString("port", ""));
        }else if(deviceType.equals(BLUETOOTH)){
            if (mBtAdapter == null) {
                Snackbar.make(rbBlueTooth, "不支持蓝牙设备", Snackbar.LENGTH_SHORT).show();
            }
            if (!mBtAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
            findViewById(R.id.layout_ip_address).setVisibility(View.GONE);
            findViewById(R.id.layout_port).setVisibility(View.GONE);
            layoutBluetooth.setVisibility(View.VISIBLE);
            rbWifi.setChecked(false);
            rbBlueTooth.setChecked(true);
            address = sp.getString("mac_address", "");
            getPairedDevices();
        }else{
            rbWifi.setChecked(false);
            rbBlueTooth.setChecked(false);
        }
       /* if(rbWifi.isChecked()){
            wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
           // wifiManager.setWifiEnabled(false);
            setWifiApEnabled(true);
        }*/
    }

    private void addEvent(){
        rbWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutBluetooth.setVisibility(View.GONE);
                findViewById(R.id.layout_ip_address).setVisibility(View.VISIBLE);
                findViewById(R.id.layout_port).setVisibility(View.VISIBLE);
                rbWifi.setChecked(true);
                rbBlueTooth.setChecked(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (SetMessageAcitivity.this.checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED
                            || SetMessageAcitivity.this.checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_WIFI_STATE)) {
                            showMessageOKCancel("你需要授予WIFI权限",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE},
                                                        321);
                                            }
                                        }
                                    });
                            return;
                        }
                        requestPermissions(new String[]{Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE},
                                321);
                        return;
                    }
                }
                wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
               // wifiManager.setWifiEnabled(false);
                setWifiApEnabled(true);

            }
        });
        rbBlueTooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBtAdapter == null) {
                    Snackbar.make(rbBlueTooth, "不支持蓝牙设备", Snackbar.LENGTH_SHORT).show();
                }
                if (!mBtAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                }
                layoutBluetooth.setVisibility(View.VISIBLE);
                findViewById(R.id.layout_ip_address).setVisibility(View.GONE);
                findViewById(R.id.layout_port).setVisibility(View.GONE);
                rbWifi.setChecked(false);
                rbBlueTooth.setChecked(true);
                getPairedDevices();

            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnSaveSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(SET_MESSAGE, 0);
                SharedPreferences.Editor editor = settings.edit();
            if(rbWifi.isChecked()){
               /* String ipAddress = etIPAddress.getText().toString();
                String port = etPort.getText().toString();
                if(ipAddress.equals("")){
                    Snackbar.make(rbWifi, "IP地址不能为空", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                if(port.equals("")){
                    Snackbar.make(rbWifi, "端口号不能为空", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }*/
                editor.putString(DEVICE_TYPE,WIFI);
                //editor.putString(IP_ADDRESS,ipAddress);
                //editor.putString(PORT, port);
                editor.commit();
                Snackbar.make(rbWifi, "保存成功", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }else{
                if(macAddress.equals("")){
                    Snackbar.make(rbBlueTooth, "请选择一个蓝牙设备", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                editor.putString(DEVICE_TYPE,BLUETOOTH);
                editor.putString(MAC_ADDRESS, macAddress);
                editor.commit();

                Snackbar.make(rbBlueTooth, "保存成功", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            }
        });
        btnCancelSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });

    }

           @Override
        protected void onDestroy() {
            super.onDestroy();

            // Make sure we're not doing discovery anymore
            if (mBtAdapter != null) {
                mBtAdapter.cancelDiscovery();
            }

        // Unregister broadcast listeners
               try{
                   if(mReceiver!=null){
                       this.unregisterReceiver(mReceiver);
                   }

               }catch (Exception e){
                   e.printStackTrace();
               }


    }

    private  void getPairedDevices(){
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter


        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            mPairedDevicesArrayAdapter.clear();
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }
    }

    private void doDiscovery() {

        // Turn on sub-title for new devices
        titleNewDevices.setVisibility(View.VISIBLE);
        titleNewDevices.setText("正在搜索中...");
        newDevices.setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }



    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            macAddress = info.substring(info.length() - 17);
            BluetoothDevice btDevice = mBtAdapter.getRemoteDevice(macAddress);
            setPin(btDevice.getClass(),btDevice,"1234");//yang
            Boolean returnValue = false;
            if (btDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                //利用反射方法调用BluetoothDevice.createBond(BluetoothDevice remoteDevice);
                Method createBondMethod = null;

                try {
                    createBondMethod = BluetoothDevice.class.getMethod("createBond");
                    Log.d("BlueToothTestActivity", "开始配对");
                    try {
                        returnValue = (Boolean) createBondMethod.invoke(btDevice);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                   // mPairedDevicesArrayAdapter.notifyDataSetChanged();
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                titleNewDevices.setVisibility(View.GONE);
               /* if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }*/
                /*Snackbar.make(rbWifi, "扫描蓝牙设备结束", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        }
    };

    @SuppressWarnings("unchecked")
    static public boolean setPin(Class btClass, BluetoothDevice btDevice,
                                 String str)
    {
        try
        {
            Method removeBondMethod = btClass.getDeclaredMethod("setPin",
                    new Class[]
                            {byte[].class});
            Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice,
                    new Object[]
                            {str.getBytes()});
            Log.d("returnValue", "setPin is success " +btDevice.getAddress()+ returnValue.booleanValue());
        }
        catch (SecurityException e)
        {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;

    }


    // wifi热点开关
    public boolean setWifiApEnabled(boolean enabled) {
        if (enabled) { // disable WiFi in any case
            //wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
            wifiManager.setWifiEnabled(false);
        }
        try {
            //热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();
            //配置热点的名称(可以在名字后面加点随机数什么的)
            //apConfig.SSID = "Gtwise"+(int)(Math.random()*100);
            //配置热点的密码
           // apConfig.preSharedKey="";
            //通过反射调用设置热点
            Method method = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            //返回热点打开状态
             method.invoke(wifiManager, apConfig, enabled);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
