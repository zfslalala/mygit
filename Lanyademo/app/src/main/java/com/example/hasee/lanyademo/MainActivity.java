package com.example.hasee.lanyademo;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BLESample";
    // 0：未连接 1：连接 2：已连接
    private static final int BLE_DISCONNECTED = 0;
    private static final int BLE_CONNECTING = 1;
    private static final int BLE_CONNECTED = 2;

    private BluetoothAdapter mBluetoothAdapter;
    private Toast mToast;
    private Button mBtScan;
    private boolean mIsScanStart = false;
    private BluetoothLeScanner mLeScanner;
    private ScanSettings mScanSettings;
    //实例化文本
    private TextView mBleTextView;
    //记录设备地址
    private String mBleAddress = "";
    private Button mConnButton;
    //搜寻服务
    private  Button mDiscoverButton;
    //连接状态
    private boolean mIsConnected = false;
    //通过特征值的UUID获取数据
    private BluetoothGattCharacteristic mSimpleKeyChar;

    //进行线程间通信
    private Handler mMainUIHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            int newState = msg.what;
            if(newState == BluetoothProfile.STATE_CONNECTED){
                //连接成功
                mIsConnected = true;
                showToast("连接成功");
                mConnButton.setText("断开");
            } else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                mIsConnected = false;
                showToast("断开连接");
                mConnButton.setText("连接");
            }
        }
    };


    //连接需要用到的类
    private BluetoothGatt mGatt;
    private BluetoothGattCallback mCallback = new BluetoothGattCallback() {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);

        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
        }

        @Override//连接回调
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            mMainUIHandler.sendEmptyMessage(newState);

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            //服务被发现
            Log.d(TAG,"++onServicesDiscovered++");
            //gatt.getService();获取服务
            discoverGattService(gatt.getServices());
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
//           被动接受通知/接受设备发送过来的数据
            byte[] data = characteristic.getValue();
            String value = "";
            for(int i = 0;i<data.length;i++){
                value += String.format("%02x",data[i]);
            }
            Message msg = new Message();
            msg.obj  = value;
            mMainUIHandler.sendMessage(msg);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };


    //发现service
    private void discoverGattService(List<BluetoothGattService> services){
        if(services == null)
            return;
        for(BluetoothGattService service:services){
            String uuid = service.getUuid().toString();
            Log.d(TAG,"Service uuid = "+uuid);
            //获取特征列表
            List<BluetoothGattCharacteristic> characteristics
                    = service.getCharacteristics();
            for(BluetoothGattCharacteristic characterisitc:characteristics){
                String char_uuid = service.getUuid().toString();
                Log.d(TAG,"Characteristic uuid = "+char_uuid);
                //在这里通过UUID判断 进行读和写
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if(mBluetoothAdapter != null){
            showToast("手机支持蓝牙功能");
        }else {
            finish();
        }
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            showToast("手机不支持蓝牙4.0");
            finish();
        } else {
            showToast("手机支持蓝牙4.0");
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            mScanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setReportDelay(3000).build();
        }

        mBtScan = (Button) findViewById(R.id.bt_scan);
        mBtScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mIsScanStart){
                    mBtScan.setText("停止扫描");
                    mIsScanStart = true;
                    scan(true);
                }else {
                    mBtScan.setText("开始扫描");
                    mIsScanStart = false;
                    scan(false);
                }
            }
        });

        mConnButton = (Button)findViewById(R.id.bt_conn);
        mBleTextView =(TextView) findViewById(R.id.tv_ble);
        mConnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mIsConnected){
                    connect();
                } else {
                    disconnect();
                }

            }
        });
        //搜寻服务
        mDiscoverButton = (Button)findViewById(R.id.bt_server);
        mDiscoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mGatt != null) {
                    mGatt.discoverServices();
                }
            }
        });

    }

    private  boolean connect(){
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mBleAddress);
        //获取地址进行连接
        mGatt = device.connectGatt(MainActivity.this,false,mCallback);
        if(mGatt != null){
            return true;
        } else {
            return false;
        }
    }

    private void showToast(String msg){
        mToast.setText(msg);
        mToast.show();
    }

    private  void disconnect(){
        if(mGatt != null){
            mGatt.disconnect();
        }
    }
    @TargetApi(23)
    private void scan(boolean enable){
        final ScanCallback scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                BluetoothDevice device = result.getDevice();

                //当扫描到时将设备显示到TextView
                mBleTextView.setText("name=" +device.getName() + "－address="+device.getAddress());
                mBleAddress = device.getAddress();
            }
        };
        if(enable){
            mLeScanner.startScan(scanCallback);
        } else {
            mLeScanner.stopScan(scanCallback);
        }
    }


    private void requestPermission() {
        if (PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
            //has permission, do operation directly
        } else {
            //do not have permission
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        0);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
}