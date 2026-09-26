package com.example.loanemi.Activities.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loanemi.R;

public class Util {
    public static boolean isUserAdClicked = false;
    @SuppressLint("ObsoleteSdkInt")
    public static void hide(Activity activity) {
        Window window = activity.getWindow();

        int systemBarColor;
        boolean isDarkMode =
                (activity.getResources().getConfiguration().uiMode
                        & Configuration.UI_MODE_NIGHT_MASK)
                        == Configuration.UI_MODE_NIGHT_YES;

        if (isDarkMode) {
            // DARK MODE
            systemBarColor = ContextCompat.getColor(activity, R.color.lightTransparent);
        } else {
            // LIGHT MODE
            systemBarColor = ContextCompat.getColor(activity, R.color.lightTransparent);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            window.setStatusBarColor(systemBarColor);

            window.setNavigationBarColor(systemBarColor);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (activity.getWindow() != null && activity.getWindow().getInsetsController() != null) {
                activity.getWindow().getInsetsController().hide(
                        WindowInsets.Type.navigationBars());

                activity.getWindow().getInsetsController().setSystemBarsBehavior(
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                );
            }

        } else {
            View decorView = activity.getWindow().getDecorView();

            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        }
    }

    public static void setupEdgeToEdge(Activity activity, int rootId) {
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
        View main = activity.findViewById(rootId);

        ViewCompat.setOnApplyWindowInsetsListener(main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public static boolean isInternetAvailable(Context context) {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) {
            return false;
        }

        NetworkCapabilities networkCapabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork);

        if (networkCapabilities == null) {
            return false;
        }

        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
    }
}
