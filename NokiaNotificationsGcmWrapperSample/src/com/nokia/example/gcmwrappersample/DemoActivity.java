/**
 * Copyright (c) 2014 Microsoft Mobile and/or its subsidiary(-ies).
 * See the license text file delivered with this project for more information.
 */

package com.nokia.example.gcmwrappersample;

import java.lang.reflect.Field;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.nokia.example.gcmwrappersample.R;

/**
 * Main UI for the demo app.
 */
public class DemoActivity extends Activity {
    private static final String TAG = "GCMWrapperSample/Main";
    private TextView mDisplay;
    private AsyncTask<Void, Void, Void> mRegisterTask;

    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Force menu to be displayed in ActionBar
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        }
        catch (Exception ex) {
            // Ignore
        }
        
        Intent startIntent = getIntent();
        Log.i(TAG, "startIntent: " + startIntent);
        
        if (startIntent != null) {
            String message = startIntent.getStringExtra("message");
            Log.i(TAG, "v: " + message);
        }
        
        // Make sure the device has the proper dependencies
        GCMRegistrar.checkDevice(this);
        
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready
        GCMRegistrar.checkManifest(this);
        
        setContentView(R.layout.main);
        mDisplay = (TextView) findViewById(R.id.display);
        
        displayMessage("Stored sender ID: " + CommonUtilities.getSenderId(this));
        
        final String regId = GCMRegistrar.getRegistrationId(this);
        
        if (regId == null || regId.isEmpty()) {
            Log.i(TAG, "No registration ID stored.");
            displayMessage("No registration ID stored. Please register (from menu).");
            
            /*
             * Uncomment the following to automatically register the
             * application on startup.
             */
            //GCMRegistrar.register(this, CommonUtilities.DEFAULT_SENDER_ID);
        }
        else {
            Log.i(TAG, "Found stored registration ID: " + regId);
            displayMessage("The app has a stored registration ID: " + regId);
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
            ClipData clip = ClipData.newPlainText("RID", regId);
            clipboard.setPrimaryClip(clip);
            displayMessage("No register call is needed. Registration ID was copied to the clipboard.");
        }
    }

    /**
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CommonUtilities.DISPLAY_MESSAGE_ACTION);
        filter.addAction(CommonUtilities.PROCESS_MESSAGES_ACTION);
        registerReceiver(mMessageHandler, filter);
        displayMessage(" --- Resumed --- ");
        processMessages();
    }

    /**
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mMessageHandler);
    }

    /**
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        
        GCMRegistrar.onDestroy(this);
        super.onDestroy();
    }

    /**
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*
             * Typically, an application registers automatically, so options below
             * are disabled. Uncomment them if you want to manually register or
             * unregister the device (you will also need to uncomment the equivalent
             * options on options_menu.xml).
             */
            case R.id.options_register:
                String id = CommonUtilities.getSenderId(this);
                displayMessage("Trying to register \"" + id + "\"...");
                GCMRegistrar.register(this, id);
                return true;
            case R.id.options_unregister:
                displayMessage("Unregistering...");
                GCMRegistrar.unregister(this);
                return true;
            case R.id.options_set_app_id:
                PromptDialog dialog = new PromptDialog(this, "title", "Enter sender ID") {
                    @Override
                    public boolean onOkClicked(String input) {
                        CommonUtilities.setSenderId(DemoActivity.this, input);
                        displayMessage("Sender ID \"" + input + "\" stored.");
                        return true;
                    }
                };
                
                dialog.show();
                return true;
            case R.id.options_clear:
                mDisplay.setText(null);
                return true;
            case R.id.options_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        } // switch (item.getItemId())
    }

    /**
     * Displays the given message.
     * 
     * @param message The message to display.
     */
    private void displayMessage(String message) {
        mDisplay.append(message + "\n");
    }

    /**
     * Displays the last message, if one exists, and the number of new messages.
     */
    private void processMessages() {
        final int numOfNewMessages = CommonUtilities.clearMessages(this);
        final String message = CommonUtilities.popLastMessage(this);
        StringBuilder sb = new StringBuilder();
        
        if (message != null) {
            sb.append("Last message: \"").append(message).append("\" ");
        }
        
        sb.append("[").append(numOfNewMessages).append(" new message(s)]");
        displayMessage(sb.toString());
        
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(0);
    }

    /**
     * This class receives the messages from the notifications service and
     * provides the content for the UI.
     */
    private final BroadcastReceiver mMessageHandler = new BroadcastReceiver() {
        /**
         * @see android.content.BroadcastReceiver#onReceive(
         * android.content.Context, android.content.Intent)
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "BroadcastReceiver.onReceive(): " + intent);
            String action = intent.getAction();
            
            if (CommonUtilities.PROCESS_MESSAGES_ACTION.equals(action)) {
                processMessages();
            }
            else if (CommonUtilities.DISPLAY_MESSAGE_ACTION.equals(action)) {
                String message = intent.getStringExtra(CommonUtilities.EXTRA_MESSAGE);
                displayMessage(message);
            }
        }
    };
}