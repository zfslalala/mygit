package com.example.hasee.lanyademo.task;

/**
 * Created by hasee on 2018/9/22.
 */

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class BlueReceiveTask extends Thread {
    private static final String TAG = "BlueReceiveTask";
    private BluetoothSocket mSocket;
    private Handler mHandler;

    public BlueReceiveTask(BluetoothSocket socket, Handler handler) {
        mSocket = socket;
        mHandler = handler;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;
        while (true) {
            try {
                bytes = mSocket.getInputStream().read(buffer);
                mHandler.obtainMessage(0, bytes, -1, buffer).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

}
