package com.meig.bluetooth;


import android.bluetooth.BluetoothUuid;
import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothUuid;
import android.bluetooth.SdpMasRecord;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayDeque;
import android.annotation.NonNull;
import android.accounts.Account;

import android.bluetooth.client.map.BluetoothMasClient;
import com.android.bluetooth.pbapclient.PbapPCEClient;
import com.android.bluetooth.pbapclient.BluetoothPbapClient;

import android.provider.Settings;
import com.meig.bluetooth.handler.StaticMessage;
import com.meig.bluetooth.handler.MainHandler;
import com.meig.bluetooth.profilelistener.BluetoothAVRCPListener;
import com.meig.bluetooth.profilelistener.BluetoothHFPProfileListener;

import android.accounts.AccountManager;

import java.util.ArrayList;
import java.util.Set;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.HashMap;

import android.graphics.drawable.Drawable;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.telephony.TelephonyManager;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothHeadsetClientCall;

public class MainActivity extends Activity {

    private static final String TAG="MainActivity";
	private MainHandler mainHandler;
	private HandlerThread handlerThread;
	
	private BluetoothPbapClient pbapClient;
	private PbapPCEClient mPbapClient;
	public static final String BLUETOOTH_PERM = android.Manifest.permission.BLUETOOTH;

	public static final String BLUETOOTH_ADMIN_PERM =
	              android.Manifest.permission.BLUETOOTH_ADMIN;

    private BluetoothAdapter m_bluetoothAdapter;
    private Set<BluetoothDevice> bluetoothDeviceSet ;
    private BluetoothProfileListener btProfileListener = new BluetoothProfileListener();
    private BluetoothHFPProfileListener btHFPProfileListener = new BluetoothHFPProfileListener();
    private BluetoothAVRCPListener avrcpListener = new BluetoothAVRCPListener();
    private BluetoothDevice bluetoothDevice;
	private BluetoothMasClient masClient;

	private Timer m_timer;
	private	Button read1;
	private	Button a2dp_sink;
	private	Button hfpclient;
	private TimerTask uiTimertask;
	private TimerTask checkMsg; 
	private HashMap<String,String> phoneBookList = new HashMap<String,String>();
	private Button start;

	private AccountManager mAccountManager;

	private Handler toastHandle = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case StaticMessage.WORK_GET_NEWMSG:{
                    // ArrayList<String> newMsg = (ArrayList<String>)msg.obj;
                    // for (String handle : newMsg) {
                    //     masClient.getMessage(handle,false);
                    // }
					String handle = (String) msg.obj;
					masClient.getMessage(handle,false);
                    break;
                }
                case StaticMessage.WORK_GET_NEWMSG_SUCCESS:{
                    toastutil((String)msg.obj);
                    break;
                }
				case StaticMessage.WORK_START_READMESSAGE:{
					checkMsg = new M_TimerTask();
					m_timer.schedule(checkMsg,0,1000);
					read1.setBackgroundColor(getColor(R.color.colorPrimaryDark));
					read1.setText("reading MESSAGE");
					// Message.obtain(mainHandler,StaticMessage.PROFILE_DISCONNECT_PBAP_CLIENT,bluetoothDevice).sendToTarget();
					// Message.obtain(mainHandler,StaticMessage.PROFILE_CONNECT_PBAP_CLIENT,getPriority(bluetoothDevice),0,bluetoothDevice).sendToTarget();
					break;
				}
				case StaticMessage.WORK_GET_PHONEBOOK_SUCCESS:{
				phoneBookList = (HashMap<String,String>)msg.obj;
				
				break;
				}
				case StaticMessage.UI_DISCONNECT_A2DP_SINK:{
					a2dp_sink.setBackgroundColor(getColor(R.color.colorAccent));
					a2dp_sink.setText("a2dp_sink disconnect");
					break;
				}
				case StaticMessage.UI_CONNECT_A2DP_SINK:{
					a2dp_sink.setBackgroundColor(getColor(R.color.colorPrimaryDark));
					a2dp_sink.setText("a2dp_sink connecting");
					break;
				}
				case StaticMessage.PROFILE_CONNECT_AVRCP_CONTROLLER:{
					String str = (String)msg.obj;
                    Toast.makeText(getApplicationContext(), str,Toast.LENGTH_SHORT).show();
					break;
				}
				case StaticMessage.UI_A2DP_SINK_PLAY:{
					start.setText("start");
					//Drawable top =getResources().getDrawable(android.R.drawable.ic_media_play);
					// start.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
					break;
				}
				case StaticMessage.UI_A2DP_SINK_PAUSE:{
					start.setText("stop");
					// Drawable top =getResources().getDrawable(android.R.drawable.ic_media_pause);
					// start.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);

				break;
				}
/*				case StaticMessage.UI_CONNECT_HFP_CLIENT:{
					hfpclient.setBackgroundColor(getColor(R.color.colorPrimaryDark));
					hfpclient.setText("hfp client connecting");
					break;
				}
				case StaticMessage.UI_DISCONNECT_HFP_CLIENT:{
					hfpclient.setBackgroundColor(getColor(R.color.colorAccent));
					hfpclient.setText("hfp client disconnect");
					break;
				}
*/
            }
        }
    };
	
	private String inbox = "telecom/msg/inbox";
	private ArrayDeque<String> mSetPathQueue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init Bluetooth
        {
            m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            // open bluetooth
            if (m_bluetoothAdapter != null) {
                if (!m_bluetoothAdapter.isEnabled()) {
                    m_bluetoothAdapter.enable();
                }
            }
        }

        // registerReceiver
        {
            bluetoothDeviceSet = m_bluetoothAdapter.getBondedDevices();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
            intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
            intentFilter.addAction(BluetoothHeadsetClient.ACTION_CALL_CHANGED);
            intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
			intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
            intentFilter.addAction(BluetoothDevice.ACTION_SDP_RECORD);
            //      intentFilter.addAction(BluetoothHeadsetClient.ACTION_CALL_CHANGED);
            registerReceiver(m_bt_revcevier, intentFilter);
        }

        mSetPathQueue = new ArrayDeque<String>(Arrays.asList(inbox.split("/")));

        // init work handler Thread
        {
            handlerThread = new HandlerThread("workHandler");
            handlerThread.start();
            mAccountManager = AccountManager.get(this);
            mainHandler = new MainHandler(handlerThread.getLooper());
            mainHandler.setMainToast(toastHandle);
            mPbapClient = new PbapPCEClient(getApplicationContext(),mainHandler);
            mPbapClient.start();
            mainHandler.setmClient(mPbapClient);

        }

		read1 = (Button)findViewById(R.id.read1);
		a2dp_sink= (Button)findViewById(R.id.a2dp_sink);
//		hfpclient= (Button)findViewById(R.id.hfpclient);
		m_timer = new Timer();
		initButton();
    }

	/**
	*init BUTTON	
	*/
	public void initButton(){
		
        Button next = (Button)findViewById(R.id.next);
		start = (Button)findViewById(R.id.start);
        Button bcak = (Button)findViewById(R.id.back);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				if(btProfileListener.bluetoothA2dpSink == null){
				toastutil("a2dp_sink not connect");
				return;
				}
				
				if(btProfileListener.bluetoothA2dpSink.isA2dpPlaying(bluetoothDevice)) {
                    Toast.makeText(getApplicationContext(), "stop the music", Toast.LENGTH_SHORT).show();
					Message.obtain(mainHandler,StaticMessage.CONTROLLER_A2DP_STOP,avrcpListener).sendToTarget();
					toastHandle.obtainMessage(StaticMessage.UI_A2DP_SINK_PLAY).sendToTarget();
					// start.setText("start");
					// Drawable top = getResources().getDrawable(android.R.drawable.ic_media_play);
					// start.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
				}else{
					Message.obtain(mainHandler,StaticMessage.CONTROLLER_A2DP_START,avrcpListener).sendToTarget();
					toastHandle.obtainMessage(StaticMessage.UI_A2DP_SINK_PAUSE).sendToTarget();
					// start.setText("stop");
					// Drawable top = getResources().getDrawable(android.R.drawable.ic_media_pause);
					// start.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
					// Log.e(TAG,"start the pbapClient");

				}
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //avrcpListener.nextMusic();
				if(btProfileListener.bluetoothA2dpSink == null){
				toastutil("a2dp_sink not connect");
				return;
				}
				Message.obtain(mainHandler,StaticMessage.CONTROLLER_A2DP_NEXT,avrcpListener).sendToTarget();
                Toast.makeText(getApplicationContext(), "next the music", Toast.LENGTH_SHORT).show();
            }
        });
        bcak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //avrcpListener.backMusic();
				if(btProfileListener.bluetoothA2dpSink == null){
				toastutil("a2dp_sink not connect");
				return;
				}
				Message.obtain(mainHandler,StaticMessage.CONTROLLER_A2DP_BACK,avrcpListener).sendToTarget();
                Toast.makeText(getApplicationContext(), "back the music", Toast.LENGTH_SHORT).show();
            }
        });






	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(m_bt_revcevier);
    }

    private BroadcastReceiver m_bt_revcevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case BluetoothDevice.ACTION_ACL_CONNECTED: {
                    //检测到新设备连接时
                    BluetoothDevice dev = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    bluetoothDeviceSet.add(dev);

                    Log.d(TAG, "onReceive: bluetooth:" + dev.getName());
                    bluetoothDevice = dev;
                    if(dev!=null){
                        m_bluetoothAdapter.getProfileProxy(getApplicationContext(),btProfileListener,BluetoothProfile.A2DP_SINK);
                        m_bluetoothAdapter.getProfileProxy(getApplicationContext(),btHFPProfileListener,BluetoothProfile.HEADSET_CLIENT);
                        m_bluetoothAdapter.getProfileProxy(getApplicationContext(),avrcpListener,BluetoothProfile.AVRCP_CONTROLLER);
                    }
					Handler handle = (Handler)mainHandler;
					pbapClient = new BluetoothPbapClient(dev,new Account(dev.getAddress(),getApplicationContext().getString(R.string.pbap_account_type)),mainHandler);
				
					pbapClient.pullPhoneBook(BluetoothPbapClient.PB_PATH,0,BluetoothPbapClient.VCARD_TYPE_21,0,0);
					
					Toast.makeText(getApplicationContext(),"pbapClientStart",Toast.LENGTH_SHORT).show();
					dev.sdpSearch(BluetoothUuid.MAS);
			
                    if(btProfileListener.bluetoothA2dpSink!=null){
                        btProfileListener.a2dpSinkConnect(dev);
                    }
                    if(btHFPProfileListener.bluetoothHeadsetClient!=null){
                        btHFPProfileListener.hfpConnect(dev);
                    }
						
					uiTimertask = new UI_TimerTask();
					m_timer.schedule(uiTimertask,0,50);
                    break;
                }
				case BluetoothDevice.ACTION_SDP_RECORD: {
                	Toast.makeText(MainActivity.this, "recoed message", Toast.LENGTH_SHORT).show();
					
					BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    ParcelUuid uuid = intent.getParcelableExtra(BluetoothDevice.EXTRA_UUID);
                    if(uuid.equals(BluetoothUuid.MAS)){
                        SdpMasRecord record = intent.getParcelableExtra(BluetoothDevice.EXTRA_SDP_RECORD);
                        masClient = new BluetoothMasClient(device,record,mainHandler);
                    }
					if(masClient!=null){
                	Toast.makeText(MainActivity.this,"connect MASClient success", Toast.LENGTH_SHORT).show();
						mainHandler.setMasClient(masClient);						
						mainHandler.obtainMessage(StaticMessage.WORK_MASCLIENT_CONNECT).sendToTarget();
//						masClient.getMessagesListing("telecom/msg/inbox",10);
					}
					break;
				}
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:{
                    Toast.makeText(MainActivity.this,"device is disconnect",Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onReceive: device is disconnect" );
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                 	disconnect(device);
					phoneBookList =null;
					 Message.obtain(mainHandler,StaticMessage.PROFILE_DISCONNECT_PBAP_CLIENT,bluetoothDevice).sendToTarget();  
					synchronized(BluetoothProfileListener.class){
						btProfileListener.bluetoothA2dpSink = null;
					}
					if(uiTimertask !=null){	
						uiTimertask.cancel();
						uiTimertask = null;
					}
					if(checkMsg!=null){
						checkMsg.cancel();
						checkMsg = null;
					}
					read1.setBackgroundColor(getColor(R.color.colorAccent));
					read1.setText("read disconnect");
                  break;
                }
				// case Intent.ACTION_NEW_OUTGOING_CALL:{
				// 	Toast.makeText(MainActivity.this,"call the number "+10086,Toast.LENGTH_SHORT).show();
					// break;
				// }
				case BluetoothHeadsetClient.ACTION_CALL_CHANGED:{
					try{
					Object state = intent.getParcelableExtra(BluetoothHeadsetClient.EXTRA_CALL);
                    BluetoothHeadsetClientCall btHFPCall = (BluetoothHeadsetClientCall)state;
                    // Toast.makeText(getApplicationContext(),String.valueOf(btHFPCall.getState()),Toast.LENGTH_SHORT).show();
					if(btHFPCall.getState()==BluetoothHeadsetClientCall.CALL_STATE_INCOMING){
						String number = String.valueOf(btHFPCall.getNumber());
						if(phoneBookList != null && phoneBookList.containsKey(number)){
                    		Toast.makeText(getApplicationContext(),phoneBookList.get(number),Toast.LENGTH_SHORT).show();
						}
						else{

                    		Toast.makeText(getApplicationContext(),number,Toast.LENGTH_SHORT).show();

						}
					Log.e(TAG,"get the number:"+String.valueOf(btHFPCall.getNumber()));
						

					}
					}catch(Exception e){
					e.printStackTrace();
					Log.e(TAG,"phoneException");
					}
					break;
				}
                case BluetoothDevice.ACTION_PAIRING_REQUEST: 
                {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    int pin = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY, 0);
                    int pv = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, 0);
                    Log.d(TAG, TAG + ".BluetoothReceiver onReceive request:" + device.getAddress() + "  pin:" + pin
                            + " pair:" + pv + " name:" + device.getName());
                    byte[] pincode = null;
                    try {
                        pincode = ("" + pin).getBytes("UTF-8");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (pincode == null) {
                        Log.e(TAG, TAG + ".BluetoothReceiver onReceive Bad pin code:" + pin);
                        break;
                    }
                    boolean ret = false;
                    ret = device.setPin(pincode);
                    Log.d(TAG, TAG + ".BluetoothReceiver onReceive setPin ret:" + ret);
                    ret = device.setPairingConfirmation(true);
                    Log.d(TAG, TAG + ".BluetoothReceiver onReceive setPairingConfirmation ret:" + ret);
                    break;
                }
				case TelephonyManager.ACTION_PHONE_STATE_CHANGED:{
				TelephonyManager mTelephonyManger = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
				Log.e(TAG,"get the phone manger");
				toastutil("get the phone manager");
				int state = mTelephonyManger.getCallState();
				if(state == TelephonyManager.CALL_STATE_RINGING){
				toastutil("get the phone");
				}
				break;
				}
            }
        }
    };



	boolean disconnect(BluetoothDevice device) {
        enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM,
                "Need BLUETOOTH ADMIN permission");
        mPbapClient.disconnect(device);
        return true;
    }



    public int getPriority(BluetoothDevice device) {
        enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM,
                "Need BLUETOOTH_ADMIN permission");
        int priority = Settings.Global.getInt(getContentResolver(),
                Settings.Global.getBluetoothPbapClientPriorityKey(device.getAddress()),
                BluetoothProfile.PRIORITY_UNDEFINED);
        return priority;
    }


    public void callPhone(String number){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+number));
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public  void toastutil(String str){
        Toast.makeText(MainActivity.this,str,Toast.LENGTH_SHORT).show();
    }




	private class M_TimerTask extends TimerTask{
        @Override
        public void run() {
            BluetoothMasClient.MessagesFilter filter = new BluetoothMasClient.MessagesFilter();
            masClient.getMessagesListing("inbox",10,filter,0,10,0);
		}
    }
	
	private class UI_TimerTask extends TimerTask{
		@Override
        public void run() {
			synchronized(BluetoothProfileListener.class){
			if(btProfileListener.bluetoothA2dpSink!=null){
				if(btProfileListener.bluetoothA2dpSink.getConnectionState(bluetoothDevice)==BluetoothProfile.STATE_DISCONNECTED){
					//a2dp_sink disconnect

					Message.obtain(toastHandle,StaticMessage.UI_DISCONNECT_A2DP_SINK).sendToTarget();
				
				}else{
					Message.obtain(toastHandle,StaticMessage.UI_CONNECT_A2DP_SINK).sendToTarget();

				}
				if(btProfileListener.bluetoothA2dpSink.isA2dpPlaying(bluetoothDevice)) {
					Message.obtain(toastHandle,StaticMessage.UI_A2DP_SINK_PAUSE).sendToTarget();
				
				}else{
					Message.obtain(toastHandle,StaticMessage.UI_A2DP_SINK_PLAY).sendToTarget();
				
				}
			}
			}

/*			if(btProfileListener.bluetoothHeadsetClient!=null){
				if(btProfileListener.bluetoothHeadsetClient.getConnectionState(bluetoothDevice)==BluetoothProfile.STATE_DISCONNECTED){
					//hfpclient disconnect

					Message.obtain(toastHandle,StaticMessage.UI_DISCONNECT_HFP_CLIENT).sendToTarget();
				
				}else{
					Message.obtain(toastHandle,StaticMessage.UI_CONNECT_HFP_CLIENT).sendToTarget();

				}
			}
			*/
		}
	}

}
