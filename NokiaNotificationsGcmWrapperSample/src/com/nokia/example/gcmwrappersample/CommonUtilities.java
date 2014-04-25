/**
 * Copyright (c) 2014 Microsoft Mobile and/or its subsidiary(-ies).
 * See the license text file delivered with this project for more information.
 */

package com.nokia.example.gcmwrappersample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Helper class providing methods and constants common to other classes.
 */
public final class CommonUtilities {
    private static final String TAG = "GCMWrapperSample/CommonUtilities";
    
    // Intent used to display a message on the screen
    public static final String DISPLAY_MESSAGE_ACTION = "com.nokia.pushnotifications.gcmdemo.DISPLAY_MESSAGE";
    
    // Intent used to trigger handling of push messages in the application  
    public static final String PROCESS_MESSAGES_ACTION = "com.nokia.pushnotifications.gcmdemo.PROCESS_MESSAGES";
    
    // Intent's extra that contains the message to be displayed
    public static final String EXTRA_MESSAGE = "message";
    
    // Shared preferences key for storing the sender ID
    private static final String PREFS_KEY_SENDER_ID = "sender_id";
    
    public static final String DEFAULT_SENDER_ID = "anna-example";

    /** 
     * @param context The application context.
     * @return The stored sender ID or the default value if not available.
     */
    public static String getSenderId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PREFS_KEY_SENDER_ID, DEFAULT_SENDER_ID);
    }

    /**
     * Stores the given sender ID.
     *  
     * @param context The application context.
     * @param id The sender ID to store.
     * @return True if successful, false otherwise.
     */
    public static boolean setSenderId(Context context, String id) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.edit().putString(PREFS_KEY_SENDER_ID, id).commit();
    }

    /**
     * Pops the latest message from the messages and returns it.
     * 
     * @param context The application context.
     * @return The latest message.
     */
    public static String popLastMessage(Context context) {
        SharedPreferences sp =
                context.getSharedPreferences("messages", Context.MODE_PRIVATE);
        String last = sp.getString("last", null);
        sp.edit().putString("last", null).commit();
        return last;
    }

    /**
     * Clears all messages.
     * 
     * @param context The application context.
     * @return The number of messages cleared.
     */
    public static int clearMessages(Context context) {
        SharedPreferences sp =
                context.getSharedPreferences("messages", Context.MODE_PRIVATE);
        int messages = sp.getInt("messages", 0);
        sp.edit().putInt("messages", 0).commit();
        return messages;
    }

    /**
     * Stores the given message.
     * 
     * @param context The application context.
     * @param message The message to store.
     * @return The number of total message including the stored one.
     */
    public static int storeMessage(Context context, String message) {
        SharedPreferences sp =
                context.getSharedPreferences("messages", Context.MODE_PRIVATE);
        int messages = sp.getInt("messages", 0) + 1;
        sp.edit().putInt("messages", messages).putString("last", message).commit();
        return messages;
    }

    /**
     * Notifies UI to display a message. This method is defined in the common
     * helper because it's used both by the UI and the background service.
     *
     * @param context The application context.
     * @param message The message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Log.i(TAG, "displayMessage: "+message);
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }

    /**
     * Sends a request to the Activity instance to process the messages.
     * 
     * @param context The application context.
     */
    public static void processMessages(Context context) {
        Log.i(TAG, "processMessages");
        Intent intent = new Intent(PROCESS_MESSAGES_ACTION);
        context.sendBroadcast(intent);
    }
}
