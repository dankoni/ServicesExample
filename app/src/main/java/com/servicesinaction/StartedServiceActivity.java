package com.servicesinaction;

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

import com.servicesinaction.services.StartedService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class StartedServiceActivity extends AppCompatActivity {

    ImageView imageView;
    TextView statusText;

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
                Intent serviceIntent = new Intent(StartedServiceActivity.this, StartedService.class);
                serviceIntent.putExtra("URL","http://www.freepngimg.com/download/emoji/1-2-wink-emoji-png.png");
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
            String message = intent.getStringExtra("filename");
            try {
                FileInputStream fileInputStream = openFileInput(message);
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inPreferredConfig = Bitmap.Config.ARGB_8888;
                imageView.setImageBitmap(BitmapFactory.decodeStream(fileInputStream));
                statusText.setText("Finished");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


           // mImg.setImageBitmap(BitmapFactory.decodeFile(message));
// mImg.setImageBitmap(BitmapFactory.decodeFile(path, option));
// mImg.setImageDrawable(Drawable.createFromPath(path));
           // mImg.setVisibility(View.VISIBLE);
           // mText.setText(path);
        }

    };
}
