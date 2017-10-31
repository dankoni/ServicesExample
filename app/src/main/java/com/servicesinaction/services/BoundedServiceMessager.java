package com.servicesinaction.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by dankoni on 10/28/17.
 */

public class BoundedServiceMessager extends Service{
    /** Command to the service to display a message */
    public static final int MSG_START_DOWNLOAD = 1;
    public static final int MSG_FINISH_DOWNLOAD = 2;
    public static final int MSG_REGISTER_CLIENT = 3;
    ArrayList<Messenger> mClients = new ArrayList<Messenger>();

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mMessenger.getBinder();
    }



    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_DOWNLOAD:
                    String url = (String) msg.obj;
                    new LongRunning().execute(url);
                    break;
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                default:
                    super.handleMessage(msg);
            }
        }
    }


    private void sendMessageToUI() {
        for (int i=mClients.size()-1; i>=0; i--) {
            try {

                //Send data as a String
                Bundle b = new Bundle();
                b.putString("filename", "demo.png");
                Message msg = Message.obtain(null, MSG_FINISH_DOWNLOAD);
                msg.setData(b);
                mClients.get(i).send(msg);

            }
            catch (RemoteException e) {
                // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
                mClients.remove(i);
            }
        }
    }




    private class LongRunning extends AsyncTask<String, Void, String> {

        private String filename="demo.png";
        private URL url;
        private HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... params) {

            FileOutputStream out = null;
            String urlString = params[0];

            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream input = urlConnection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                out =  openFileOutput(filename, Context.MODE_PRIVATE);

                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);


            } catch (MalformedURLException e) {
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return urlString;
        }

        @Override
        protected void onPostExecute(String s) {
            sendMessageToUI();
            super.onPostExecute(s);
        }
    }
}
