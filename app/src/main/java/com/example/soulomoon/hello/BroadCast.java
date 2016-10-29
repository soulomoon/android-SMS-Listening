package com.example.soulomoon.hello;

/*
  BroadCast Created by soulomoon on 2016/10/19.
 */


import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class BroadCast extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getLatestMsg(final Context context) {
        Log.i("getLatestMsg", "begin");

        final List<String> lstSms = new ArrayList<>();
        final ContentResolver cr = context.getContentResolver();

        final Cursor query = cr.query(Telephony.Sms.Inbox.CONTENT_URI, // Official CONTENT_URI from docs
                new String[]{Telephony.TextBasedSmsColumns.BODY}, // Select body text
                null,
                null,
                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER); // Default sort order

        assert query != null;
        final int totalSMS = query.getCount();

        if (query.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                lstSms.add(query.getString(0));
                query.moveToNext();
            }
        } else {
            lstSms.add("You have no SMS in Inbox");
        }
        query.close();

        return lstSms.get(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(final Context context, final Intent intent) {
        SystemClock.sleep(1000L);

        Log.d("broadcast", "我操居然接收到广播了");


        context.sendBroadcast(new Intent("SMS is recieved"));
    }
}


//        final Thread thread1 = new Thread(new Runnable()
//        {
//            @Override
//            public void run() {
//                final String msg = getLatestMsg(context);
//                SocketSender.sendSocket(msg); //get instance and send socket
//                System.out.println("blah");
//            }
//        });
//        thread1.start();
//        try {
//            thread1.join();
//        } catch (final InterruptedException e) {
//            e.printStackTrace();
//        }
