package com.meig.bluetooth.profilelistener;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

public class BluetoothHFPProfileListener implements BluetoothProfile.ServiceListener {
    private static final String TAG = "BtHFPProfileListener";
    public BluetoothHeadsetClient bluetoothHeadsetClient;
    @Override
    public void onServiceConnected(int profile, BluetoothProfile bluetoothProfile) {
Log.d(TAG, "onServiceConnected: request successed");
        if(profile == BluetoothProfile.HEADSET_CLIENT){
            bluetoothHeadsetClient = (BluetoothHeadsetClient)bluetoothProfile;
            if(bluetoothHeadsetClient != null){
                Log.d(TAG, "onServiceConnected: bluetoothHeadsetClient init success");
            }else {
                Log.d(TAG, "onServiceConnected: bluetoothHeadsetClient init failed");
            }
        }
    }

    @Override
    public void onServiceDisconnected(int i) {

    }

     public void hfpConnect(BluetoothDevice device){
        Log.e(TAG, "bluetoothHeadsetClientConnect: "+(bluetoothHeadsetClient.connect(device)?"success":"failed") );
    }
   public void hfpDisConnect(BluetoothDevice device){
        Log.e(TAG, "bluetoothHeadsetClientDisConnect: "+(bluetoothHeadsetClient.disconnect(device)?"success":"failed") );
    }
}
