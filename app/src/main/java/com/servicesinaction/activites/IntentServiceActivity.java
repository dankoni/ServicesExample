package com.servicesinaction.activites;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.servicesinaction.R;
import com.servicesinaction.services.IntentServices;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class IntentServiceActivity extends AppCompatActivity {
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
                Intent serviceIntent = new Intent(IntentServiceActivity.this, IntentServices.class);
                serviceIntent.putExtra("URL", "http://www.freepngimg.com/download/emoji/1-2-wink-emoji-png.png");
                startService(serviceIntent);
                statusText.setText("Started");

            }
        });
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("custom-event-name"));
        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);
        super.onPause();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                FileInputStream fileInputStream = openFileInput("demo.png");
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inPreferredConfig = Bitmap.Config.ARGB_8888;
                imageView.setImageBitmap(BitmapFactory.decodeStream(fileInputStream));
                statusText.setText("Finished");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }

    };

}
