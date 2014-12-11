package com.mediatek.labs.hotknotapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mediatek.hotknot.HotKnotAdapter;

public class ReceiveActivity extends Activity {

   // HotKnotAdapter mHotKnotAdapter;
    TextView mInfoText;
    Button mOKButton;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        mInfoText = (TextView) findViewById(R.id.tv_info);
        mOKButton =  (Button) findViewById(R.id.btn_OK);

        mOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bla
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an HotKnot message.
        if (HotKnotAdapter.ACTION_MESSAGE_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent.
        setIntent(intent);
    }
    /**
     * Parses the HotKnot message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        byte[] rawMsgs = intent.getByteArrayExtra(HotKnotAdapter.EXTRA_DATA);
        mInfoText.setText(new String(rawMsgs));
    }

}
