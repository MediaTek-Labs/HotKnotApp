package com.mediatek.labs.hotknotapp;

import android.app.Activity;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.mediatek.hotknot.*;


public class MainActivity extends Activity  implements HotKnotAdapter.OnHotKnotCompleteCallback {

    private TextView mInfoText;
    private EditText mMessageText;
    private Button mSendMessageButton;
    private Button mSendFileButton;
    private HotKnotAdapter mHotKnotAdapter;

    private static final int MENU_SHOW_SYSTEM_SETTINGS = 0;
    private static final String MIMETYPE = "application/com.mediatek.labs.hotknotapp";
    private static final int READ_REQUEST_CODE = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSendMessageButton = (Button) findViewById(R.id.btn_send_message);
        mSendFileButton  = (Button) findViewById(R.id.btn_send_file);
        mInfoText = (TextView) findViewById(R.id.tv_info);
        mMessageText = (EditText)  findViewById(R.id.et_message);

        mHotKnotAdapter = HotKnotAdapter.getDefaultAdapter(this);

        if(!mHotKnotAdapter.isEnabled()){
            mInfoText.setText(R.string.info);
        }


        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendHotknotMessageData();
            }
        });


        mSendFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendHotKnotFileData();
            }
        });

    }


    private void sendHotknotMessageData() {
        String messageString = mMessageText.getText().toString();
        byte[] payload = messageString.getBytes();
        HotKnotMessage message = new HotKnotMessage(MIMETYPE, payload);
        mHotKnotAdapter.setHotKnotMessage(message, this);
        mHotKnotAdapter.setOnHotKnotCompleteCallback(this, this);
    }


    public void sendHotKnotFileData() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a file (as opposed to a list
        // of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Search for all documents available via installed storage providers
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // A URI to the selected document will be contained in the return intent. Pull that uri using "resultData.getData()"
            Uri uri = null;

            if (resultData != null) {
                uri = resultData.getData();
                Uri[] uris = {uri};

                //now we have the uri, send it via HotKnot
                mHotKnotAdapter.setHotKnotBeamUrisCallback(null, this);
                // Register new file (URI) for sending.
                mHotKnotAdapter.setHotKnotBeamUris(uris, this);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        if (mHotKnotAdapter != null) {
            menu.add(Menu.NONE, MENU_SHOW_SYSTEM_SETTINGS, 0, R.string.hotknot_menu_description)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_SHOW_SYSTEM_SETTINGS) {
            showHotKnotSystemSettings();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showHotKnotSystemSettings() {
        // Start the HotKnot settings activity to enable HotKnot.
        Intent intent = new Intent(HotKnotAdapter.ACTION_HOTKNOT_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    //OnHotKnotComplete has two possible reasons, 0 is for success and 1 is for failure
    public void onHotKnotComplete(int reason) {
        // A handler is needed to send messages to the activity when this
        // callback occurs, because it happens from a binder thread.
        mHandler.obtainMessage(reason).sendToTarget();
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 0:
                    Toast.makeText(getApplicationContext(), "Message sent successfully!", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "Failed to send message!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };




}
