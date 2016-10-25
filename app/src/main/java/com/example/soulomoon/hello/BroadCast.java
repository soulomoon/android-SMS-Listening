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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {

        SystemClock.sleep(1000);

        MainActivity.getInstance().sendSocketMessage(); //get instance and send socket

        Log.d("broadcast", "我操居然接收到广播了");
    }
}
