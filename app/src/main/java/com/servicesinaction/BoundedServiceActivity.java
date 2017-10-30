package com.servicesinaction;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.servicesinaction.services.BoundedServiceBind;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class BoundedServiceActivity extends AppCompatActivity {

    private boolean mBound;
    private BoundedServiceBind mService;
    private ImageView imageView;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_started_service);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView = (ImageView) findViewById(R.id.imageNet);
        statusText = (TextView) findViewById(R.id.textStatus);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBound) {
                    if(mService.isFinished()){
                        FileInputStream fileInputStream = null;
                        try {
                            fileInputStream = openFileInput(mService.getFilename());
                            BitmapFactory.Options option = new BitmapFactory.Options();
                            option.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            imageView.setImageBitmap(BitmapFactory.decodeStream(fileInputStream));
                            statusText.setText("Done !");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }else{
                        mService.startDownload("http://www.freepngimg.com/download/emoji/1-2-wink-emoji-png.png");
                        statusText.setText("Running");
                    }
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, BoundedServiceBind.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        mBound = false;
    }


    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BoundedServiceBind.LocalBinder binder = (BoundedServiceBind.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}
