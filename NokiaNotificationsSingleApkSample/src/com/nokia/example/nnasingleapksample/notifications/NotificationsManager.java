/**
 * Copyright (c) 2014 Microsoft Mobile and/or its subsidiary(-ies).
 * See the license text file delivered with this project for more information.
 */

package com.nokia.example.nnasingleapksample.notifications;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.nokia.push.PushRegistrar;

/**
 * This class provides a common interface for registering the app to both the
 * GCM and NNA services by wrapping the method calls of both GCMRegistrar and
 * PushRegistrar classes.
 */
public class NotificationsManager {
    private static final String TAG = "NNASingleAPKSample/NotificationsManager";

    public enum SupportedNotificationServices {
        None,
        NokiaPushNotifications,
        GCM
    };

    private static NotificationsManager mInstance;
    private Context mContext;
    private SupportedNotificationServices mSupportedService;

    /**
     * Provides the singleton instance of this class.
     * 
     * @param context The application context.
     * @return The singleton instance of this class.
     * @throws NullPointerException If the context is null.
     */
    public static NotificationsManager getInstance(Context context)
        throws NullPointerException, IllegalArgumentException
    {
        if (context == null) {
            throw new NullPointerException("Context is null!");
        }
        
        if (mInstance == null) {
            mInstance = new NotificationsManager(context);
        }
        else if (context != mInstance.mContext) {
            Log.w(TAG, "getInstance(): The given context does not match the previous one, switching.");
            mInstance.mContext = context;
        }
        
        return mInstance;
    }

    /**
     * Constructor.
     * 
     * @param context The application context.
     * @throws NullPointerException If the context is null.
     */
    private NotificationsManager(Context context)
    {
        mContext = context;
        
        // Check the device for supported notifications service
        try {
            PushRegistrar.checkDevice(mContext);
            mSupportedService = SupportedNotificationServices.NokiaPushNotifications;
            Log.i(TAG, "Supported service: Nokia Push Notifications");
        }
        catch (UnsupportedOperationException e1) {
            try {
                GCMRegistrar.checkDevice(mContext);
                mSupportedService = SupportedNotificationServices.GCM;
                Log.i(TAG, "Supported service: GCM");
            }
            catch (UnsupportedOperationException e2) {
                mSupportedService = SupportedNotificationServices.None;
                Log.w(TAG, "No supported notifications services!");
            }
        }
    }

    /** 
     * @return The supported notifications service. This is resolved during the
     * class construction.
     */
    public SupportedNotificationServices getSupportedService() {
        return mSupportedService;
    }

    /**
     * Makes sure that the application manifest is properly set. While
     * developing app you can disregard this method.
     */
    public void checkManifest() {
        if (mSupportedService == SupportedNotificationServices.NokiaPushNotifications) {
            PushRegistrar.checkManifest(mContext);
        }
        else if (mSupportedService == SupportedNotificationServices.GCM) {
            GCMRegistrar.checkManifest(mContext);
        }
    }

    /** 
     * @return The notification/registration ID if available, null otherwise.
     */
    public String getRegistrationId() {
        String id = null;
        
        if (mSupportedService == SupportedNotificationServices.NokiaPushNotifications) {
            id = PushRegistrar.getRegistrationId(mContext);
        }
        else if (mSupportedService == SupportedNotificationServices.GCM) {
            id = GCMRegistrar.getRegistrationId(mContext);
        }
        
        return id;
    }

    /**
     * Registers the application to the service using the stored service/sender
     * ID.
     */
    public void register() {
        final String id = CommonUtilities.getSenderId(mContext);
        Log.i(TAG, "register(): ID: " + id);
        
        if (mSupportedService == SupportedNotificationServices.NokiaPushNotifications) {
            PushRegistrar.register(mContext, id);
        }
        else if (mSupportedService == SupportedNotificationServices.GCM) {
            GCMRegistrar.register(mContext, id);
        }
    }

    /**
     * Unregisters the application.
     */
    public void unregister() {
        Log.i(TAG, "unregister()");
        
        if (mSupportedService == SupportedNotificationServices.NokiaPushNotifications) {
            PushRegistrar.unregister(mContext);
        }
        else if (mSupportedService == SupportedNotificationServices.GCM) {
            GCMRegistrar.unregister(mContext);
        }
    }

    /**
     * Executes the necessary operations when the application is terminated.
     */
    public void onDestroy() {
        if (mSupportedService == SupportedNotificationServices.NokiaPushNotifications) {
            PushRegistrar.onDestroy(mContext);
        }
        else if (mSupportedService == SupportedNotificationServices.GCM) {
            GCMRegistrar.onDestroy(mContext);
        }
    }
}
