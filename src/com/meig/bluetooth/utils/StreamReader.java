package com.meig.bluetooth.utils;

import android.bluetooth.BluetoothDevice;
//import android.bluetooth.pbapclient.BluetoothPbapClient;
import com.android.bluetooth.pbapclient.BluetoothPbapClient;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
//import com.android.backup.vcard.VCardEntry;
import com.android.vcard.VCardEntry;
import com.meig.bluetooth.R;
import android.annotation.NonNull;

import java.util.List;
import java.util.logging.LogRecord;

public class StreamReader extends Thread {
    private static final String TAG = "StreamReader";
    private BluetoothPbapClient btclient;
    public StreamReader(BluetoothDevice device){
//		mAccount = new Account(device.getAddress(),mContext.getString(R.string.pbap_account_type));
//pbap_account_type	
//        btclient = new BluetoothPbapClient(device,mHandler);
    }
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
           switch (msg.what){
               case BluetoothPbapClient.EVENT_PULL_PHONE_BOOK_DONE:{
                   if(msg.obj!=null){
                        btclient.disconnect();
                       List<VCardEntry> vCardEntries = (List<VCardEntry>)msg.obj;
                       for(VCardEntry vCardEntry : vCardEntries){
                          // Log.d(TAG, "handleMessage: name:"+vCardEntry.getFullName());
                           Log.d(TAG, "handleMessage: phone:"+vCardEntry.getPhoneList().get(0).toString());
                       }
                   }
                   break;
               }
           }
        }
    };
    @Override
    public void run() {
        btclient.connect();
        btclient.pullPhoneBook(BluetoothPbapClient.PB_PATH);
    }
}
