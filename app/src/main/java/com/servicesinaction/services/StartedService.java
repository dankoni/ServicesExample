package com.servicesinaction.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by dankoni on 10/28/17.
 */

public class StartedService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new LongRunning().execute(intent.getStringExtra("URL"));

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    private class LongRunning extends AsyncTask<String, Void, String> {

        private URL url;
        private String filename ="demo.png";
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
            return urlString;//ne ovo negi dowloaded stream
        }

        @Override
        protected void onPostExecute(String s) {
                Intent intent = new Intent("custom-event-name");
                intent.putExtra("filename", filename);
                LocalBroadcastManager.getInstance(StartedService.this).sendBroadcast(intent);
            super.onPostExecute(s);
            stopSelf();
        }
    }
}
