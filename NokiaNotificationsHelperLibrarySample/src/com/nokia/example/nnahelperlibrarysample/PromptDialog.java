/**
 * Copyright (c) 2014 Nokia Corporation and/or its subsidiary(-ies).
 * See the license text file delivered with this project for more information.
 */

package com.nokia.example.nnahelperlibrarysample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;

import com.nokia.example.nnahelperlibrarysample.R;

/**
 * A helper class for creating and controlling a dialog with a text editor.
 */
public abstract class PromptDialog
    extends AlertDialog.Builder
    implements OnClickListener
{
    private final EditText mInput;

    /**
     * Constructor.
     * 
     * @param context The application context.
     * @param title The title of the dialog.
     * @param message The message in the dialog.
     */
    public PromptDialog(Context context, String title, String message) {
        super(context);
        setTitle(title);
        setMessage(message);
        mInput = new EditText(context);
        setView(mInput);
        setPositiveButton(R.string.ok, this);
        setNegativeButton(R.string.cancel, this);
    }

    /**
     * Constructor. For convenience.
     * 
     * @param context The application context.
     * @param title The resource ID of the dialog title.
     * @param message The resource ID of the message.
     */
    public PromptDialog(Context context, int title, int message) {
        this(context, context.getString(title), context.getString(message));
    }

    /**
     * @see android.content.DialogInterface.OnClickListener#onClick(
     * android.content.DialogInterface, int)
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            if (onOkClicked(mInput.getText().toString())) {
                dialog.dismiss();
            }
        }
        else {
            // Cancel was tapped
            dialog.dismiss();
        }
    }

    /**
     * Called when OK is tapped.
     * 
     * @param input The current content in the text edit field.
     * @return True if the dialog should be closed, false otherwise.
     */
    abstract protected boolean onOkClicked(String input);
}
