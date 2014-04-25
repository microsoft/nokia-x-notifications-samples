/**
 * Copyright (c) 2014 Microsoft Mobile and/or its subsidiary(-ies).
 * See the license text file delivered with this project for more information.
 */

package com.nokia.example.nnasingleapksample.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.nokia.example.nnasingleapksample.DemoActivity;
import com.nokia.example.nnasingleapksample.R;

/**
 * Provides the common implementation for the intent service regardless if it's
 * based on GCM or NNA.
 */
public class CommonIntentServiceImpl {
    private final String mTag;

    /**
     * Constructor.
     * 
     * @param tag The identification tag for logging.
     */
    public CommonIntentServiceImpl(final String tag) {
        mTag = tag;
        Log.i(mTag, "Service created.");
    }

    public String[] getSenderIds(Context context) {
        return new String[] {
            CommonUtilities.getSenderId(context)
        };
    }

    public void onRegistered(Context context, String registrationId) {
        Log.i(mTag, "Device registered with ID \"" + registrationId + "\"");
        CommonUtilities.displayMessage(context, context.getString(R.string.push_registered, registrationId));
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("RID", registrationId);
        clipboard.setPrimaryClip(clip);
        CommonUtilities.displayMessage(context, "Registration ID was copied to the clipboard.");
    }

    public void onUnregistered(Context context, String registrationId) {
        Log.i(mTag, "Device unregistered");
        CommonUtilities.displayMessage(context, context.getString(R.string.push_unregistered));
    }

    public void onMessage(Context context, Intent intent) {
        Log.i(mTag, "Received message. Extras: " + intent.getExtras());
        String message = intentExtrasToString(intent.getExtras());
        int messages = CommonUtilities.storeMessage(context, message);
        
        String text = messages > 1
                ? context.getString(R.string.push_message_many, messages)
                : context.getString(R.string.push_message);
        
        generateNotification(context, null, text, message, 0);
        CommonUtilities.processMessages(context);
    }

    public void onDeletedMessages(Context context, int total) {
        Log.i(mTag, "Received deleted messages notification");
        String message = context.getString(R.string.push_deleted, total);
        generateNotification(context, message, message, null, 1);
        CommonUtilities.displayMessage(context, message);
    }

    public void onError(Context context, String errorId) {
        Log.i(mTag, "Received error: " + errorId);
        CommonUtilities.displayMessage(context,
                context.getString(R.string.push_error, errorId));
    }

    public void onRecoverableError(Context context, String errorId) {
        Log.i(mTag, "Received recoverable error: " + errorId);
        CommonUtilities.displayMessage(context,
                context.getString(R.string.push_recoverable_error, errorId));
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
        
        if (message != null) {
            notificationIntent.putExtra("message", message);
        }
        
        // Set intent so it does not start a new activity
        notificationIntent.setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
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
        
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
            .notify(id, notification);
    }
}
