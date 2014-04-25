/**
 * Copyright (c) 2014 Microsoft Mobile and/or its subsidiary(-ies).
 * See the license text file delivered with this project for more information.
 */

package com.nokia.example.nnasingleapksample;

import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;
import com.nokia.example.nnasingleapksample.notifications.CommonIntentServiceImpl;

/**
 * IntentService responsible for handling messages from GCM.
 */
public class GCMIntentService extends GCMBaseIntentService {
    private static final String TAG = "NNASingleAPKSample/GCMIntentService";
    private CommonIntentServiceImpl mImplementation;

    /**
     * Constructor.
     */
    public GCMIntentService() {
        mImplementation = new CommonIntentServiceImpl(TAG);
    }

    /**
     * @see com.google.android.gcm.GCMBaseIntentService#getSenderIds(android.content.Context)
     */
    @Override
    protected String[] getSenderIds(Context context) {
        return mImplementation.getSenderIds(context);
    }

    /**
     * @see com.google.android.gcm.GCMBaseIntentService#onDeletedMessages(android.content.Context, int)
     */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        mImplementation.onDeletedMessages(context, total);
    }

    /**
     * @see com.google.android.gcm.GCMBaseIntentService#onRecoverableError(android.content.Context, java.lang.String)
     */
    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        mImplementation.onRecoverableError(context, errorId);
        return super.onRecoverableError(context, errorId);
    }

    /**
     * @see com.google.android.gcm.GCMBaseIntentService#onRegistered(android.content.Context, java.lang.String)
     */
    @Override
    protected void onRegistered(Context context, String registrationId) {
        mImplementation.onRegistered(context, registrationId);
    }

    /**
     * @see com.google.android.gcm.GCMBaseIntentService#onUnregistered(android.content.Context, java.lang.String)
     */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        mImplementation.onUnregistered(context, registrationId);
    }

    /**
     * @see com.google.android.gcm.GCMBaseIntentService#onMessage(android.content.Context, android.content.Intent)
     */
    @Override
    protected void onMessage(Context context, Intent intent) {
        mImplementation.onMessage(context, intent);
    }

    /**
     * @see com.google.android.gcm.GCMBaseIntentService#onError(android.content.Context, java.lang.String)
     */
    @Override
    public void onError(Context context, String errorId) {
        mImplementation.onError(context, errorId);
    }
}
