/**
 * Copyright 2012 Google Inc.
 * Copyright (c) 2014 Nokia Corporation and/or its subsidiary(-ies).
 * 
 * See the license text file delivered with this project for more information.
 */

package com.nokia.example.gcmwrappersample;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.nokia.example.gcmwrappersample.R;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {
    private static final String TAG = "GCMWrapperSample/GCMIntentService";

    /**
     * Constructor.
     */
    public GCMIntentService() {
    }

    @Override
    protected String[] getSenderIds(Context context) {
        return new String[] { CommonUtilities.getSenderId(this) };
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered with ID \"" + registrationId + "\"");
        CommonUtilities.displayMessage(context, getString(R.string.gcm_registered, registrationId));
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("RID", registrationId);
        clipboard.setPrimaryClip(clip);
        CommonUtilities.displayMessage(context, "Registration ID was copied to the clipboard.");
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        CommonUtilities.displayMessage(context, getString(R.string.gcm_unregistered));
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message. Extras: " + intent.getExtras());
        String message = intentExtrasToString(intent.getExtras());
        int messages = CommonUtilities.storeMessage(context, message);
        
        String text = (messages > 1)
                ? getString(R.string.gcm_message_many, messages)
                : getString(R.string.gcm_message);
        
        generateNotification(context, message, text, message, 0);
        CommonUtilities.processMessages(context);
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        generateNotification(context, message, message, null, 1);
        CommonUtilities.displayMessage(context, message);
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        CommonUtilities.displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        Log.i(TAG, "Received recoverable error: " + errorId);
        CommonUtilities.displayMessage(context, getString(R.string.gcm_recoverable_error, errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Extracts the key-value pairs from the given Intent extras and returns
     * them in a string.
     * 
     * @param extras The Intent extras as Bundle.
     * @return The extracted data in a string.
     */
    private String intentExtrasToString(Bundle extras) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        
        for (String key : extras.keySet()) {
            sb.append(sb.length() <= 2 ? "" : ", ");
            sb.append(key).append("=").append(extras.get(key));
        }
        
        sb.append(" }");
        return sb.toString();
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private void generateNotification(Context context,
                                      String message,
                                      String text,
                                      String info,
                                      int id)
    {
        int icon = R.drawable.ic_stat_gcm;
        long when = System.currentTimeMillis();
        Intent notificationIntent = new Intent(context, DemoActivity.class);
        notificationIntent.putExtra("message", message);
        
        // Set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        
        Notification notification =
                new Notification.Builder(context)
                    .setSmallIcon(icon).setTicker(text).setWhen(when)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(text).setContentInfo(info)
                    .setContentIntent(intent).build();
        
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        ((NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE)).notify(id, notification);
    }
}
