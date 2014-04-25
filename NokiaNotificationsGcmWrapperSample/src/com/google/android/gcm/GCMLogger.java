/**
 * Copyright 2012 Google Inc.
 * Copyright (c) 2014 Microsoft Mobile and/or its subsidiary(-ies).
 * 
 * See the license text file delivered with this project for more information.
 */

package com.google.android.gcm;

import android.util.Log;

/**
 * Custom logger.
 */
class GCMLogger {

    private final String mTag;
    // can't use class name on TAG since size is limited to 23 chars
    private final String mLogPrefix;

    GCMLogger(String tag, String logPrefix) {
        mTag = tag;
        mLogPrefix = logPrefix;
    }

    /**
     * Logs a message on logcat.
     *
     * @param priority logging priority
     * @param template message's template
     * @param args list of arguments
     */
    protected void log(int priority, String template, Object... args) {
        if (Log.isLoggable(mTag, priority)) {
            String message = String.format(template, args);
            Log.println(priority, mTag, mLogPrefix + message);
        }
    }
}
