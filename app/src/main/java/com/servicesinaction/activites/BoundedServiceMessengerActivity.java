package com.servicesinaction.activites;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.servicesinaction.R;
import com.servicesinaction.services.BoundedServiceMessager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class BoundedServiceMessengerActivity extends AppCompatActivity {


    final Messenger mMessenger =  new Messenger(new IncomingHandler());
    Messenger mService = null;

    /** Flag indicating whether we have called bind on the service. */
    boolean mBound;


    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mService = new Messenger(service);
            mBound = true;

            try {
                Message msg = Message.obtain(null, BoundedServiceMessager.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            }
            catch (RemoteException e) {
                // In this case the service has crashed before we could even do anything with it
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            mBound = false;
        }
    };
    private TextView statusText;
    private ImageView imageView;

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
                    startDownload("http://www.freepngimg.com/download/emoji/1-2-wink-emoji-png.png");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to the service
        bindService(new Intent(this, BoundedServiceMessager.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    public void startDownload(String url) {
        if (!mBound) return;
        // Create and send a message to the service, using a supported 'what' value
        Message msg = Message.obtain(null, BoundedServiceMessager.MSG_START_DOWNLOAD, 0, 0);
        msg.obj = new String(url);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BoundedServiceMessager.MSG_FINISH_DOWNLOAD:
                    FileInputStream fileInputStream = null;
                    try {
                            fileInputStream = openFileInput(msg.getData().getString("filename"));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        BitmapFactory.Options option = new BitmapFactory.Options();
                        option.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        imageView.setImageBitmap(BitmapFactory.decodeStream(fileInputStream));
                        statusText.setText("Done !");
                        break;
                        default:
                            super.handleMessage(msg);
                    }
            }
        }
    }





