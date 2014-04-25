/**
 * Copyright (c) 2014 Microsoft Mobile and/or its subsidiary(-ies).
 * See the license text file delivered with this project for more information.
 */

package com.nokia.example.nnasingleapksample;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.ListActivity;
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
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.nokia.example.nnasingleapksample.notifications.CommonUtilities;
import com.nokia.example.nnasingleapksample.notifications.NotificationsManager;
import com.nokia.example.nnasingleapksample.R;

/**
 * Main UI for the demo app.
 */
public class DemoActivity extends ListActivity {
    private static final String TAG = "NNASingleAPKSample/Main";
    private NotificationsManager mNotifications;
    private ArrayList<HashMap<String,String>> mList;
    private AsyncTask<Void, Void, Void> mRegisterTask;

    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
        
        // Force menu to be displayed in ActionBar
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        }
        catch (Exception ex) {
        }
        
        Intent startIntent = getIntent();
        Log.i(TAG, "startIntent: " + startIntent);
        
        String message = null;
        
        if (startIntent != null) {
            message = startIntent.getStringExtra("message");
            Log.i(TAG, "v: " + message);
        }
        
        constructList();
        
        mNotifications = NotificationsManager.getInstance(this);
        
        if (mNotifications.getSupportedService() ==
                NotificationsManager.SupportedNotificationServices.None)
        {
            ErrorDialog dialog = new ErrorDialog(this, R.string.notifications_not_supported);
            dialog.show();
        }
        
        mNotifications.checkManifest();
        final String senderId = CommonUtilities.getSenderId(this);
        displayMessage(senderId.isEmpty() ?
                "No sender ID stored." : "Stored sender ID: " + senderId);
        
        final String regId = mNotifications.getRegistrationId();
        
        if (regId == null || regId.isEmpty()) {
            Log.i(TAG, "No registration ID stored.");
            displayMessage("No registration ID stored. Please register (from menu).");
        }
        else {
            Log.i(TAG, "Found stored registration ID: " + regId);
            displayMessage("The app has a stored registration ID: " + regId);
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
            ClipData clip = ClipData.newPlainText("RID", regId);
            clipboard.setPrimaryClip(clip);
            displayMessage("No register call is needed. Registration ID was copied to the clipboard.");
        }
        
        if (message != null) {
            displayMessage(message);
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
        displayMessage("Resumed.");
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
        
        mNotifications.onDestroy();
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
            case R.id.options_register:
                final String id = CommonUtilities.getSenderId(this);
                displayMessage("Trying to register \"" + id + "\"...");
                mNotifications.register();
                return true;
            case R.id.options_unregister:
                displayMessage("Unregistering...");
                mNotifications.unregister();
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
                mList.clear();
                onContentChanged();
                return true;
            case R.id.options_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        } // switch (item.getItemId())
    }

    /**
     * Constructs the list array and the adapter for the list view.
     */
    private void constructList() {
        mList =  new ArrayList<HashMap<String,String>>();
        
        ListAdapter adapter =
                new SimpleAdapter(this, mList,
                                  R.layout.log_list_item,
                                  new String[] { "timestamp", "message" },
                                  new int[] { R.id.timestamp, R.id.message });
        setListAdapter(adapter);
    }

    /**
     * Displays the given message.
     * 
     * @param message The message to display.
     */
    private void displayMessage(String message) {
        HashMap<String,String> logItem = new HashMap<String,String>();
        logItem.put("timestamp", new Date().toString());
        logItem.put("message",  message);
        mList.add(0, logItem);
        onContentChanged();
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