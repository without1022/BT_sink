package com.meig.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

public class BluetoothProfileListener implements BluetoothProfile.ServiceListener {
    private static final String TAG = "BTProfileListener";
//    public BluetoothHeadsetClient bluetoothHeadsetClient;
    public BluetoothA2dp bluetoothA2dp;
    public volatile BluetoothA2dpSink bluetoothA2dpSink;
    public BluetoothHeadsetClient bluetoothHeadsetClient;
    @Override
    public void onServiceConnected(int profile, BluetoothProfile bluetoothProfile) {
        if(profile == BluetoothProfile.A2DP){
            bluetoothA2dp = (BluetoothA2dp)bluetoothProfile;
            if(bluetoothA2dp != null){
                Log.d(TAG, "onServiceConnected: a2dp init success");
            }else {
                Log.d(TAG, "onServiceConnected: a2dp init failed");
            }
        }else if(profile == BluetoothProfile.A2DP_SINK){ //a2dp_sink
            bluetoothA2dpSink = (BluetoothA2dpSink)bluetoothProfile;
            if(bluetoothA2dpSink != null){
                Log.d(TAG, "onServiceConnected: a2dp_sink init success");
            }else {
                Log.d(TAG, "onServiceConnected: a2dp_sink init failed");
            }
        }else if(profile == BluetoothProfile.HEADSET_CLIENT){
            bluetoothHeadsetClient = (BluetoothHeadsetClient)bluetoothProfile;
            if(bluetoothHeadsetClient != null){
                Log.d(TAG, "onServiceConnected: bluetoothHeadsetClient init success");
            }else {
                Log.d(TAG, "onServiceConnected: bluetoothHeadsetClient init failed");
            }
        }
    }

    @Override
    public void onServiceDisconnected(int prifile) {

    }

    public void a2dpConnect(BluetoothDevice device){
        if(device ==null){
            Log.e(TAG, "a2dpConnect: device is a null object" );
            return;
        }
        if(bluetoothA2dp==null){
            Log.e(TAG, "a2dpConnect: not get the a2dp profile proxy object" );
            return;
        }
        try{
            Class.forName("android.bluetooth.BluetoothA2dp").getMethod("connect",BluetoothDevice.class).invoke(bluetoothA2dp,device);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void a2dpDisConnect(){

    }

    public void a2dpSinkConnect(BluetoothDevice device){
        Log.e(TAG, "a2dpSinkConnect: "+(bluetoothA2dpSink.connect(device)?"success":"failed") );
    }

	 public void a2dpSinkDisConnect(BluetoothDevice device){
        Log.e(TAG, "a2dpSinkDisConnect: "+(bluetoothA2dpSink.disconnect(device)?"success":"failed") );
    }


    public void hfpConnect(BluetoothDevice device){
        Log.e(TAG, "a2dpSinkConnect: "+(bluetoothHeadsetClient.connect(device)?"success":"failed") );
    }
}
