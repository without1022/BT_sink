package com.meig.bluetooth.profilelistener;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothAvrcpController;
import android.util.Log;
import android.bluetooth.BluetoothAvrcp;

import java.util.List;

public class BluetoothAVRCPListener implements BluetoothProfile.ServiceListener {
    private static final String TAG = "BluetoothAVRCPListener";
    public BluetoothAvrcpController bluetoothAvrcpController;

    public static final int PASSTHROUGH_ID_PLAY     = 0x44;    /* play */
    public static final int PASSTHROUGH_ID_STOP     = 0x45;    /* stop */
    public static final int PASSTHROUGH_ID_PAUSE    = 0x46;    /* pause */
    public static final int PASSTHROUGH_ID_FORWARD  = 0x4B;     /* forward */
    public static final int PASSTHROUGH_ID_BACKWARD    = 0x4C;     /* backward */


    @Override
    public void onServiceConnected(int profile, BluetoothProfile bluetoothProfile) {
        bluetoothAvrcpController = (BluetoothAvrcpController)bluetoothProfile;
        if(bluetoothAvrcpController != null){
            Log.d(TAG, "onServiceConnected: bluetoothAvrcpController init success");
        }else {
            Log.d(TAG, "onServiceConnected: bluetoothAvrcpController init failed");
        }
    }

    @Override
    public void onServiceDisconnected(int profile) {

    }

    public void stopmusic(){
        sendPassThroughCmd(PASSTHROUGH_ID_PAUSE);
    }

    public void startMusic(){
        sendPassThroughCmd(PASSTHROUGH_ID_PLAY);
    }

    public void nextMusic(){
        sendPassThroughCmd(PASSTHROUGH_ID_FORWARD);
    }

    public void backMusic(){
        sendPassThroughCmd(PASSTHROUGH_ID_BACKWARD);
    }



    private void sendPassThroughCmd(int keyCode){
        if(bluetoothAvrcpController==null){
            return;
        }
        List<BluetoothDevice> devices = bluetoothAvrcpController.getConnectedDevices();
        for(BluetoothDevice device : devices){
            Log.d(TAG, "send command to device: "+ keyCode + device.getName() + " " + device.getAddress());
            bluetoothAvrcpController.sendPassThroughCmd(device, keyCode, BluetoothAvrcp.PASSTHROUGH_STATE_PRESS);
            bluetoothAvrcpController.sendPassThroughCmd(device, keyCode, BluetoothAvrcp.PASSTHROUGH_STATE_RELEASE);
        }

    }
}
