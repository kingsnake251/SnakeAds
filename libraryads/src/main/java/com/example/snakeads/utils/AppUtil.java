package com.example.snakeads.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;

import com.example.snakeads.dialog.LoadingAdsDialog;

public class AppUtil {

    public static int layoutLoadingAds = 0;
    public static int layoutLoadingResumeAds = 0;
    public static Boolean BUILD_DEBUG = true;

    public static float currentTotalRevenue001Ad = 0;

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static LoadingAdsDialog dialogLoadingAds;

    public static void showDialogLoadingAds(Activity activity) {
        dialogLoadingAds = new LoadingAdsDialog(activity);
        dialogLoadingAds.show();
    }

    public static void dismissDialogLoadingAds() {
        if (dialogLoadingAds != null && dialogLoadingAds.isShowing()) {
            dialogLoadingAds.dismiss();
            dialogLoadingAds = null;
        }
    }
}
