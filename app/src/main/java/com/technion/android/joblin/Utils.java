package com.technion.android.joblin;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Utils {
    private static final String TAG = "Utils";

        public static Point getDisplaySize(WindowManager windowManager){
            try {
                if(Build.VERSION.SDK_INT > 16) {
                    Display display = windowManager.getDefaultDisplay();
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    display.getMetrics(displayMetrics);
                    return new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
                }else{
                    return new Point(0, 0);
                }
            }catch (Exception e){
                e.printStackTrace();
                return new Point(0, 0);
            }
        }
        public static int dpToPx(int dp) {
            return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
        }
    public static void matchPopUp(Context context, String type)
    {
        new SweetAlertDialog(context)
                .setTitleText("It's a match!")
                .setContentText("Now you can contact the " + type + ".")
                .setConfirmText("Great!")
                .show();
    }

    public static void noMoreSwipesPopUp(Context context)
    {
        new SweetAlertDialog(context)
                .setTitleText("No more swipes!")
                .setContentText("Try again tommorrow!")
                .setConfirmText("OK")
                .show();
    }

    public static void errorPopUp(Context context, String error)
    {
        new SweetAlertDialog(context,SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(String.format("Something went wrong: %s",error))
                .show();
    }

}
