package com.meig.bluetooth.handler;

public final class StaticMessage {
    /**
     * CONNETC/DISCONNECT DEVICE WITH PROFILE
     * +- 100001_100100
     */
    public static final int PROFILE_CONNECT_A2DP_SINK= 100001;
    public static final int PROFILE_DISCONNECT_A2DP_SINK = -100001;

    public static final int PROFILE_CONNECT_AVRCP_CONTROLLER = 100002;
    public static final int PROFILE_DISCONNECT_AVRCP_CONTROLLER = -100002;

    public static final int PROFILE_CONNECT_HFP_CLIENT = 100003;
    public static final int PROFILE_DISCONNECT_HFP_CLIENT = -100003;

    public static final int PROFILE_CONNECT_PBAP_CLIENT = 100004;
    public static final int PROFILE_DISCONNECT_PBAP_CLIENT = -100004;



	public static final int CONTROLLER_A2DP_STOP = 100101;
    public static final int CONTROLLER_A2DP_START = 100102;
    public static final int CONTROLLER_A2DP_NEXT = 100103;
    public static final int CONTROLLER_A2DP_BACK = 100104;


    public static final int WORK_GET_NEWMSG=100201;
    public static final int WORK_GET_NEWMSG_SUCCESS=100202;
    public static final int WORK_MASCLIENT_SETPATH=100203;
    public static final int WORK_MASCLIENT_SETPATHROOT=100204;
 	public static final int WORK_MASCLIENT_CONNECT=100205;

    public static final int WORK_START_READMESSAGE  =100206;
    public static final int WORK_GET_PHONEBOOK_SUCCESS=100207;



    public static final int UI_DISCONNECT_MASCLIENT=100301;
    public static final int UI_CONNECT_MASCLIENT=100302;
    public static final int UI_DISCONNECT_A2DP_SINK=100303;
    public static final int UI_CONNECT_A2DP_SINK=100304;
    public static final int UI_DISCONNECT_HFP_CLIENT=100305;
    public static final int UI_CONNECT_HFP_CLIENT=100306;
    public static final int UI_READED_PHONE_DONE=100307;
    public static final int UI_PBAP_CLIENT_DISCONNECT=100308;
    public static final int UI_A2DP_SINK_PLAY=100309;
    public static final int UI_A2DP_SINK_PAUSE=100310;
	
	
	
}
