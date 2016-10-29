package com.example.soulomoon.hello;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import java.util.logging.Level;
import java.util.logging.Logger;


public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 412;
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 401;
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 94;
    private static final Logger LOGGER = Logger.getLogger(MainActivity.class.getName());
    private final Object m_monitorObj = new Object();
    private final BroadcastReceiver m_broadcastReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(final Context context, final Intent intent) {
            // internet lost alert dialog method call from here...
            final String text = BroadCast.getLatestMsg(context);
            sendSocketMessage(text);
        }
    };
    private boolean m_socketSentLocked = true;
    private boolean m_testLocked = false;
    private boolean m_check = true;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient m_googleApiClient;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public static Action getIndexApiAction() {
        final Thing object = new Thing.Builder()
                .setName("Main Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermissionSmsINTERNET();

        Log.i("获取权限", "获取监听权限");
        setContentView(R.layout.activity_main);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        m_googleApiClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        new TestSocketTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        registerReceiver(m_broadcastReceiver, new IntentFilter("SMS is recieved"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(m_broadcastReceiver);
        m_check = false;
    }

    /**
     * Called when the user clicks the Send button
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void sendMessage(final View view) {
        final TextView editMessage = (TextView) findViewById(R.id.edit_message);
        final String text = editMessage.getText().toString();
        sendSocketMessage(text);
    }

    public void sendSocketMessage(final String text) {
        new SendSocketTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, text);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_INTERNET) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.d("内容:", "给我权限使用网络INTERNET");
                //for new permission
                getPermissionSmsREADSMS();
            } else {
                Log.d("内容:", "获取权限失败INTERNET");
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_READ_SMS) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.d("内容:", "给我权限获取短信内容READ_SMS");
                getPermissionSmsRECEIVESMS();
            } else {
                Log.d("内容:", "获取权限失败READ_SMS");
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_RECEIVE_SMS) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.d("内容:", "给我权限监听手机短信接收RECEIVE_SMS");
            } else {
                Log.d("内容:", "获取权限失败RECEIVE_SMS");
            }
        }
    }

    public void getPermissionSmsREADSMS() {
        Log.d("", "getting permission begin2");
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_SMS},
                MY_PERMISSIONS_REQUEST_READ_SMS);
    }

    public void getPermissionSmsRECEIVESMS() {
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

    @Override
    public void onStart() {
        super.onStart();
//        m_check = true;
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        m_googleApiClient.connect();
        AppIndex.AppIndexApi.start(m_googleApiClient, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
//        m_check = false;
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(m_googleApiClient, getIndexApiAction());
        m_googleApiClient.disconnect();
    }


    private class TestSocketTask extends AsyncTask<Void, String, Void> {
        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            m_testLocked = true;

            final TextView buttonSend = (TextView) findViewById(R.id.button_send);
            final String send = "Testing...";
            buttonSend.setText(send);
        }

        @Nullable
        @Override
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        protected Void doInBackground(final Void... params) {

            while (m_check) {
                final String result = SocketSender.sendSocket("");
                publishProgress(result);
                synchronized (m_monitorObj) {
                    if (!m_testLocked) {
                        try {
                            m_monitorObj.wait(3000L);
                        } catch (final InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    while (m_testLocked) {
                        try {
                            m_socketSentLocked = false;
                            m_monitorObj.notifyAll();
                            m_monitorObj.wait(10000L);
                        } catch (final InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(final String... values) {
            final TextView buttonSend = (TextView) findViewById(R.id.button_send);
            final String send = "Testing...";
            buttonSend.setText(send);

            final String result = values[0];
            Log.i("", "refreshing textView");
            final TextView socketMessageValue = (TextView) findViewById(R.id.socket_message_value);
            if ("True".equals(result)) {
                socketMessageValue.setText("连接成功");
            } else {
                socketMessageValue.setText("连接失败");
            }
        }
    }

    private class SendSocketTask extends AsyncTask<String, String, String> {
        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
//        private boolean isLocked(){
//            return m_socketSentLocked;
//        }
        @Override
        protected final void onPreExecute() {
            super.onPreExecute();
            final TextView buttonSend = (TextView) findViewById(R.id.button_send);
            final String send = "SENDING...";
            buttonSend.setText(send);
        }

        @Override
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        protected final String doInBackground(final String... params) {
            Log.i("", "Thread two");
            synchronized (m_monitorObj) {
                m_testLocked = true;
                while (m_socketSentLocked) {
                    try {
                        m_monitorObj.wait(10000L);
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            final String text = params[0];
            final String result = SocketSender.sendSocket(text);
            publishProgress(result);
            synchronized (m_monitorObj) {
//              lock itself again
                m_socketSentLocked = true;
//              to let TestSocketTask run
                m_testLocked = false;
//                m_monitorObj.notifyAll();
            }
            return result;
        }

        @Override
        protected final void onProgressUpdate(final String... values) {
            final TextView buttonSend = (TextView) findViewById(R.id.button_send);
            final String send = "SEND";
            buttonSend.setText(send);
        }

        @Override
        protected final void onPostExecute(final String result) {
            LOGGER.log(Level.INFO, result);
        }

    }
}
