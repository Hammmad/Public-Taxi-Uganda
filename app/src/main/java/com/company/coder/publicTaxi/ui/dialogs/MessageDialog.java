package com.company.coder.publicTaxi.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by alisons on 04/04/2018.
 */

public class MessageDialog {

    private static AlertDialog messageDialog;

    public static void showDialogMessage(String title, String body, Context mContext) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    messageDialog.dismiss();
                } catch (Exception e) {
                    //
                }
            }
        });
        messageDialog = builder.create();
        messageDialog.show();
    }
}
