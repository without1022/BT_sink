package com.meig.bluetooth.handler;

import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import com.android.bluetooth.pbapclient.PbapPCEClient;
import android.bluetooth.client.map.BluetoothMasClient;
import android.bluetooth.client.map.BluetoothMapMessage;
import android.bluetooth.client.map.BluetoothMapBmessage;

import com.android.bluetooth.pbapclient.BluetoothPbapClient;
import com.android.vcard.VCardEntry;
import static com.meig.bluetooth.handler.StaticMessage.*;

import android.annotation.NonNull;
import android.widget.Toast;

import com.meig.bluetooth.profilelistener.BluetoothAVRCPListener;

import com.meig.bluetooth.BluetoothProfileListener;
import com.meig.bluetooth.MainActivity;
import com.meig.bluetooth.utils.DateUtils;
import java.util.Set;
import java.util.HashSet;

import java.util.ArrayList;
import java.util.HashMap;

public class MainHandler extends Handler {
    private static final String TAG = "MainHandler";

    private PbapPCEClient mClient;
    private BluetoothMasClient masClient;
    public static final String BLUETOOTH_ADMIN_PERM =  android.Manifest.permission.BLUETOOTH_ADMIN;
    ArrayList<String> newMessage;
    private Handler mainToast;
	private Set<String> readedMsg = new HashSet<String>();
	private HashMap<String,String> phoneBookList = new HashMap<String,String>();

    public MainHandler(){
        super();
    }


    public MainHandler(Looper looper){
        super(looper);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        switch (msg.what){
            case PROFILE_CONNECT_A2DP_SINK:{
//                ((BluetoothProfileListener)msg.obj).a2dpSinkConnect();
                break;
            }
            case PROFILE_DISCONNECT_A2DP_SINK:{
                //((BluetoothProfileListener)msg.obj).a2dpSinkDisConnect();
                break;
            }
            case PROFILE_CONNECT_AVRCP_CONTROLLER:{
				String str = (String)msg.obj;
				mainToast.obtainMessage(PROFILE_CONNECT_AVRCP_CONTROLLER,str).sendToTarget();
                break;
            }
            case PROFILE_DISCONNECT_AVRCP_CONTROLLER:{
                break;
            }
            case PROFILE_CONNECT_HFP_CLIENT:{
                break;
            }
            case PROFILE_DISCONNECT_HFP_CLIENT:{
                break;
            }
            case PROFILE_CONNECT_PBAP_CLIENT:{
				Log.e(TAG,"pbapclient start");
                BluetoothDevice dev = (BluetoothDevice) msg.obj;
                if(msg.arg1 >= BluetoothProfile.PRIORITY_ON) {
                    connect(dev,msg.arg1);
                }
                break;
            }
            case PROFILE_DISCONNECT_PBAP_CLIENT:{
                BluetoothDevice dev = (BluetoothDevice) msg.obj;
                disconnect(dev);
                break;
            }
			case CONTROLLER_A2DP_STOP:{
                BluetoothAVRCPListener bluetoothAVRCPListener = (BluetoothAVRCPListener)msg.obj;
                bluetoothAVRCPListener.stopmusic();
                break;
            }
            case CONTROLLER_A2DP_START:{
                BluetoothAVRCPListener bluetoothAVRCPListener = (BluetoothAVRCPListener)msg.obj;
				Log.e(TAG,"Start the music");
                bluetoothAVRCPListener.startMusic();
                break;
            }
            case CONTROLLER_A2DP_NEXT:{
                BluetoothAVRCPListener bluetoothAVRCPListener = (BluetoothAVRCPListener)msg.obj;
                bluetoothAVRCPListener.nextMusic();
                break;
            }
            case CONTROLLER_A2DP_BACK:{
                BluetoothAVRCPListener bluetoothAVRCPListener = (BluetoothAVRCPListener)msg.obj;
                bluetoothAVRCPListener.backMusic();
                break;
            }
			case WORK_MASCLIENT_SETPATH:{
				masClient.setFolderDown((String)msg.obj);
				break;
			}
			case WORK_MASCLIENT_CONNECT:{
				masClient.connect();
				break;
			}
			case WORK_MASCLIENT_SETPATHROOT:{
				masClient.setFolderRoot();	
				break;
			}
			case BluetoothMasClient.EVENT_CONNECT:{
				Log.e(TAG,"BluetoothMasClient "+msg.obj);
				this.obtainMessage(WORK_MASCLIENT_SETPATHROOT).sendToTarget();
				this.obtainMessage(WORK_MASCLIENT_SETPATH,"telecom").sendToTarget();
				this.obtainMessage(WORK_MASCLIENT_SETPATH,"msg").sendToTarget();
				mainToast.obtainMessage(WORK_START_READMESSAGE).sendToTarget();

				Log.e(TAG,"BluetoothMasClient create success");
				break;
			}
			case BluetoothMasClient.EVENT_GET_MESSAGES_LISTING:{
				ArrayList<BluetoothMapMessage> getMessagesListing= (ArrayList< BluetoothMapMessage >) msg.obj;
				Log.e(TAG,getMessagesListing.toString());
				if(getMessagesListing!=null){
					Log.e(TAG,"Message:listing size = "+getMessagesListing.size());
				}
				if(getMessagesListing.size()>0){
					Log.e(TAG,"Message:"+getMessagesListing.get(0).toString());
				}
				newMessage = new ArrayList<String>();
				for(int i=getMessagesListing.size()-1;i>=0;i--){
					if(DateUtils.isNewDate(getMessagesListing.get(i).getDateTime())){
						String handle = getMessagesListing.get(i).getHandle();
						if(!readedMsg.contains(handle)){
						newMessage.add(handle);
						readedMsg.add(handle);
						}
					}
				}	
				Log.e(TAG,newMessage.toString());
				while(newMessage.size()>0){
				    mainToast.obtainMessage(WORK_GET_NEWMSG,newMessage.remove(0)).sendToTarget();
                }
				break;
			}
			case BluetoothMasClient.EVENT_GET_MESSAGES_LISTING_SIZE:{
				int a = (int)msg.obj;
				int b = msg.arg1;
				int c = msg.arg2;
				Log.e(TAG,"Message:"+a+" b:"+b+" c:"+c);
	
				break;
			}
			case BluetoothMasClient.EVENT_SET_PATH:{
				String str = (String)msg.obj;
				Log.e(TAG,"PATH="+str);
				break;
			}
			case BluetoothMasClient.EVENT_GET_FOLDER_LISTING:{
				ArrayList<String> strs = (ArrayList<String>)msg.obj;
				Log.e(TAG,strs.toString());
				break;
			}
			case BluetoothMasClient.EVENT_GET_MESSAGE:{
				BluetoothMapBmessage bmsg = (BluetoothMapBmessage)msg.obj;
				Log.e(TAG,bmsg.toString());
                mainToast.obtainMessage(WORK_GET_NEWMSG_SUCCESS,bmsg.getBodyContent()).sendToTarget();
                break;

			}
			case BluetoothPbapClient.EVENT_PULL_PHONE_BOOK_DONE:{
			ArrayList<VCardEntry> cardList = (ArrayList<VCardEntry>)msg.obj;
			Log.e(TAG,"enties:"+cardList);
			for(VCardEntry cardEntry : cardList){
				Log.e(TAG,"PhoneNumber:"+cardEntry.getPhoneList());
				if(cardEntry.getPhoneList() != null  && cardEntry.getPhoneList().size()>0){
				String number = cardEntry.getPhoneList().get(0).getNumber().replace("-","");
				 phoneBookList.put(number,cardEntry.getNameData().getGiven());
				}
				//TODO return phonebookList to MainActivity 
				Log.e(TAG,"phonebookList:"+phoneBookList);
				mainToast.obtainMessage(WORK_GET_PHONEBOOK_SUCCESS,phoneBookList).sendToTarget();
			}
			break;
			}

        }
    }


    public void setmClient(PbapPCEClient client){
        mClient = client;
    }

    public void setMasClient(BluetoothMasClient client){
        masClient = client;
    }


    boolean disconnect(BluetoothDevice device) {
		if(mClient==null){
			return false;
		}
	Log.e(TAG,"PBapPCE disconnect");
        mClient.disconnect(device);
        return true;
    }

    public boolean connect(BluetoothDevice device,int arg1) {
        Log.d(TAG,"Received request to ConnectPBAPPhonebook " + device.getAddress());
        int connectionState = mClient.getConnectionState();
        if (connectionState == BluetoothProfile.STATE_CONNECTED ||
                connectionState == BluetoothProfile.STATE_CONNECTING) {
            return false;
        }
        if (arg1>BluetoothProfile.PRIORITY_OFF) {
            mClient.connect(device);
            return true;
        }
        return false;
    }

    public void setMainToast(Handler mainToast) {
        this.mainToast = mainToast;
    }
}

