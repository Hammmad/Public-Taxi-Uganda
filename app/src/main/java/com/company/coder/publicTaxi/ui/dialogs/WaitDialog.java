package com.company.coder.publicTaxi.ui.dialogs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by alisons on 04/04/2018.
 */

public class WaitDialog {

    private static ProgressDialog waitDialog = null;

    public static void showWaitDialog(String message, Context mContext) {
        Activity activity = (Activity) mContext;
        closeWaitDialog();
        if (!activity.isFinishing()) {
            try {
                waitDialog = new ProgressDialog(mContext);
                waitDialog.setCancelable(false);
                waitDialog.setTitle(message);
                waitDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeWaitDialog() {
        try {
            waitDialog.dismiss();
            waitDialog = null;
        } catch (Exception e) {
            //
        }
    }

    public static boolean isShowingDialog() {
        return waitDialog != null && waitDialog.isShowing();
    }
}
