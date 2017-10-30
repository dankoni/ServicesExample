package com.servicesinaction.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by dankoni on 10/30/17.
 */

public class IntentServices extends IntentService {

    private URL url;
    private HttpURLConnection urlConnection;

    public IntentServices() {
        super("IntentServiceExample");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        FileOutputStream out = null;
        String filename ="demo.png";


        try {
            URL url = new URL(intent.getStringExtra("URL"));
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
            Intent resultIntent = new Intent("custom-event-name");
            LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
        }


    }



}
