package com.example.soulomoon.hello;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by fwy99 on 10/29/2016.
 */

enum SocketSender {
    ;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String sendSocket(final String newText) {
        Log.i("", "sendSocket start");
        String result = null;
        final int timeout = 5000;
        try (Socket soc = new Socket()) {
            final InetSocketAddress address = new InetSocketAddress("10.0.0.3", 6101);
            soc.connect(address, timeout);
            result = writToSocket(newText, soc);
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
//            String hashCode = Integer.toString(e.hashCode());
            Log.i("", String.format("Socket fails, timeout: %s", Integer.toString(timeout)));
        }

        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String writToSocket(final String newText, final Socket soc) {
        String result;
        if (newText.isEmpty()) {
            Log.d("", "socket test success");
            result = "True";
        } else {
            try (DataOutputStream dos = new DataOutputStream(soc.getOutputStream())) {
                final byte[] buffer = newText.getBytes("UTF-8");//Encoding in UTF-8 system
                dos.write(buffer, 0, buffer.length);//writes complete buffer
                dos.writeBytes("\n");// A fancy new line
                dos.flush();//flush the data
                result = newText;
            } catch (final IOException e) {
                e.printStackTrace();
                result = "false";
            }
            Log.d("content:", "socket sent");
        }
        return result;
    }
}
