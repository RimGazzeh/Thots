package io.geekgirl.thots.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import io.geekgirl.thots.R;

/**
 * Created by Rim Gazzah on 28/03/19
 */
public class Tools {

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(activity, activity.getString(R.string.info_message_error_network), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                DebugLog.d(serviceClass.getName() + " is running");
                return true;
            }
        }
        DebugLog.d(serviceClass.getName() + " not running");
        return false;
    }

    public static void showAlert(int message_id, final Activity activity, final Runnable runnable, boolean showCancel, boolean showTitle, int title) {
        //  Toast.makeText(activity, activity.getString(message_id), Toast.LENGTH_SHORT).show();
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, R.style.MyAlertDialogStyle);
        AppCompatDialog alertDialog = null;
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.dialog_info, null);

        LinearLayout linearLayout_container = dialoglayout.findViewById(R.id.linearLayout_container);
        TextView dialogMessage =  dialoglayout.findViewById(R.id.textView_message);

        TextView textView_title = dialoglayout.findViewById(R.id.textView_title);
        ImageView imageView_info = dialoglayout.findViewById(R.id.imageView_info);
        if (showTitle) {
            textView_title.setText(activity.getString(title));
            textView_title.setVisibility(View.VISIBLE);
            imageView_info.setVisibility(View.GONE);
            linearLayout_container.setGravity(Gravity.LEFT);
        }
        dialogMessage.setText(activity.getString(message_id));

        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton(activity.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (runnable != null) {
                            activity.runOnUiThread(runnable);
                        } else {
                            dialog.dismiss();
                        }

                    }
                });
        if (showCancel) {
            alertDialogBuilder.setNegativeButton(activity.getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
        }

        alertDialogBuilder.setView(dialoglayout);
        alertDialog = alertDialogBuilder.create();

        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.background_light);
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null
                    && activity.getCurrentFocus() != null
                    && activity.getCurrentFocus().getWindowToken() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity
                        .getCurrentFocus().getWindowToken(), 0);
            }
        }
    }
}
