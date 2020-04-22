package com.meig.bluetooth.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.util.Log;

public class DateUtils {
    private static Date newDate = new Date();
	private static final String TAG = "DateUtils";
    private static void setNewDate(Date newDate){
        DateUtils.newDate = newDate;
    }

    public static boolean isNewDate(Date date){
        boolean ret =false;
		Log.e(TAG,"newDate:"+newDate+"date:"+date);
            ret=date.getTime()>newDate.getTime()?true:false;
        if(ret){
            setNewDate(date);
        }
        return ret;
    }
}
