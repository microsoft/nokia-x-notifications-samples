/**
 * Copyright (c) 2014 Nokia Corporation and/or its subsidiary(-ies).
 * See the license text file delivered with this project for more information.
 */

package com.nokia.example.nnasingleapksample;

import com.nokia.example.nnasingleapksample.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * A helper class for creating a simple error dialog.
 */
public class ErrorDialog
    extends AlertDialog.Builder
    implements OnClickListener
{
    /**
     * Constructor.
     * 
     * @param context The application context.
     * @param message The message in the dialog.
     */
    public ErrorDialog(Context context, String message) {
        super(context);
        setTitle(R.string.error);
        setMessage(message);
        setPositiveButton(R.string.ok, this);
    }

    /**
     * Constructor. For convenience.
     * 
     * @param context The application context.
     * @param message The message for the dialog as a resource ID.
     */
    public ErrorDialog(Context context, int message) {
        this(context, context.getString(message));
    }

    /**
     * @see android.content.DialogInterface.OnClickListener#onClick(
     * android.content.DialogInterface, int)
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        // Close the dialog
        dialog.dismiss();
    }
}
