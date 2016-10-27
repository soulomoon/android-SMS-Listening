package com.example.soulomoon.hello;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 412;
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 401;
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 94;
    //to contain instance of MainActivity
    private static MainActivity ins;
    public boolean flag = false;
    public boolean locked;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static List<String> getAllSmsFromProvider(Context context) {
        Log.i("getAllSmsFromProvider", "begin");

        List<String> lstSms = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();

        Cursor c = cr.query(Telephony.Sms.Inbox.CONTENT_URI, // Official CONTENT_URI from docs
                            new String[]{Telephony.Sms.Inbox.BODY}, // Select body text
                            null,
                            null,
                            Telephony.Sms.Inbox.DEFAULT_SORT_ORDER); // Default sort order

        assert c != null;
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

    public static String sendSocket(String newText) {
//        for allowing thread unsafe
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
        int timeout = 5000;
        try {
            String ip = "10.0.2.2";
            Socket soc = new Socket();
            //set time out
//            InetAddress Addresses = InetAddress.getByName(ip);
//            boolean reach = Addresses.isReachable(timeout);
//            Log.i("Addresses of", Addresses.toString() + " reachable: " + reach);

            InetSocketAddress Address = new InetSocketAddress(ip, 6101);

            Log.d("", "trying to build connection");
            soc.connect(Address, timeout);
            Log.d("", "socket built");

            if (newText.equals("")){
                Log.d("", "socket test success");
                newText = "True";
            } else {
//            String toSend= "We are going to send this line";
                byte[] buffer = newText.getBytes("UTF-8");//Encoding in UTF-8 system

                DataOutputStream dos = new DataOutputStream(soc.getOutputStream());//gets the output Stream

                dos.write(buffer, 0, buffer.length);//writes complete buffer
                dos.writeBytes("\n");// A fancy new line
                dos.flush();//flush the data

                dos.close();

                Log.d("内容:", "socket sent");
            }
            soc.close();
            return newText;
        } catch (IOException e) {
            e.printStackTrace();
//            String hashCode = Integer.toString(e.hashCode());
            Log.d("", "Socket fails," + "timeout: " + Integer.toString(timeout));
//            Log.d("", "Socket fails," + "hashCode: " + hashCode);
            return "socket连接失败";
        }
    }

    /**
     * for returning the MainActivity running Instance
     *
     * @return instance of MainActivity
     */
    public static MainActivity getInstance() {
        return ins;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ins = this;

        getPermissionSmsINTERNET();

        Log.i("获取权限", "获取监听权限");
        setContentView(R.layout.activity_main);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        new TestSocketTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Called when the user clicks the Send button
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void sendMessage(View view) {
        sendSocketMessage();
    }

    public void sendSocketMessage() {
        new sendSocketTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String sendMessageToSocket() {
        List<String> text = getAllSmsFromProvider(this);

        String newText = text.get(0);
        Log.d("1", newText);

        Log.d("内容:", "给我发送socket");
        String result = sendSocket(newText);
        Log.d("内容:", "socket完成");


        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_INTERNET: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("内容:", "给我权限使用网络INTERNET");
                    //for new permission
                    getPermissionSmsREAD_SMS();
                } else {
                    Log.d("内容:", "获取权限失败INTERNET");
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_READ_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("内容:", "给我权限获取短信内容READ_SMS");
                    getPermissionSmsRECEIVE_SMS();
                } else {
                    Log.d("内容:", "获取权限失败READ_SMS");
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_RECEIVE_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("内容:", "给我权限监听手机短信接收RECEIVE_SMS");
                } else {
                    Log.d("内容:", "获取权限失败RECEIVE_SMS");
                }
            }
        }
    }

    public void getPermissionSmsREAD_SMS() {
        Log.d("", "getting permission begin2");
        ActivityCompat.requestPermissions(this,
                                          new String[]{Manifest.permission.READ_SMS},
                                          MY_PERMISSIONS_REQUEST_READ_SMS);
    }

    public void getPermissionSmsRECEIVE_SMS() {
        Log.d("", "getting permission begin2");
        ActivityCompat.requestPermissions(this,
                                          new String[]{Manifest.permission.RECEIVE_SMS},
                                          MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
    }

    public void getPermissionSmsINTERNET() {
        Log.d("", "getting permission begin");
        ActivityCompat.requestPermissions(this,
                                          new String[]{Manifest.permission.INTERNET},
                                          MY_PERMISSIONS_REQUEST_INTERNET);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private class TestSocketTask extends AsyncTask<Void, String, String> {
        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        public boolean isLocked(){
            return locked;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            locked = true;

            TextView button_send = (TextView) findViewById(R.id.button_send);
            String send = "Testing...";
            button_send.setText(send);
        }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        protected String doInBackground(Void... params) {

            while (true) {
                if (this.isLocked()){
                    String result = sendSocket("");
                    publishProgress(result);
//                    flag = true;
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while (!this.isLocked()) {
                    try {
                        flag = true;
                        Thread.sleep(120000);
                        flag = false;

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }


//            return sendMessageToSocket();
        }
        protected void onProgressUpdate(String... data) {
            TextView button_send = (TextView) findViewById(R.id.button_send);
            String send = "Testing...";
            button_send.setText(send);

            String result = data[0];
            Log.i("", "refreshing textView");
            TextView socket_message_value = (TextView) findViewById(R.id.socket_message_value);
            if (result.equals("True")) {
                this.isLocked();
                socket_message_value.setText("连接成功");
            } else {
                socket_message_value.setText("连接失败");
            }
        }

        /**
         * The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */
        protected void onPostExecute(String result) {
            TextView socket_message_value = (TextView) findViewById(R.id.socket_message_value);
            if (result.equals("失败")) {
                socket_message_value.setText(result);
            } else {
                socket_message_value.setText("成功");
            }
        }
    }


    private class sendSocketTask extends AsyncTask<Void, String, String> {
        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        public boolean isLocked(){
            return flag;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            locked = true;
            TextView button_send = (TextView) findViewById(R.id.button_send);
            String send = "SENDING...";
            button_send.setText(send);
        }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        protected String doInBackground(Void... params) {
            Log.i("", "Thread two");
//          locking the check
            locked = false;

            while (!this.isLocked()){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            String result = sendMessageToSocket();
            publishProgress(result);

            return result;
        }
        protected void onProgressUpdate(String... data) {
            String result = data[0];
            TextView edit_message = (TextView) findViewById(R.id.edit_message);
            edit_message.setText(result);

            TextView button_send = (TextView) findViewById(R.id.button_send);
            String send = "SEND";
            button_send.setText(send);
        }
        protected void onPostExecute(String result) {
            locked = true;
        }
    }
}
