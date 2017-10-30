package com.servicesinaction.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by dankoni on 10/28/17.
 */

public class BoundedServiceBind extends Service{
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private boolean downloadComplete = false;
    private String filename ="demo.png";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public class LocalBinder extends Binder {
        public BoundedServiceBind getService() {
            //retunirng this service for clients
            return BoundedServiceBind.this;
        }
    }

    public void  startDownload(String url){
        new LongRunning().execute(url);
    }

    public boolean isFinished(){
        return downloadComplete;
    }

    public String getFilename(){
        return filename;
    }


    private class LongRunning extends AsyncTask<String, Void, String> {

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
            return urlString;//ne ovo negi dowloaded stream
        }

        @Override
        protected void onPostExecute(String s) {
            downloadComplete = true;
            super.onPostExecute(s);
        }
    }
}
