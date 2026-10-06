package com.loanemi.calculator.emi.remote;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.loanemi.calculator.emi.MainActivity;
import com.loanemi.calculator.emi.R;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        try {

            String title = "";
            String body = "";

            if (remoteMessage.getNotification() != null) {

                title = remoteMessage.getNotification().getTitle();
                body = remoteMessage.getNotification().getBody();

            }

            if (!remoteMessage.getData().isEmpty()) {

                if (remoteMessage.getData().containsKey("title")) {
                    title = remoteMessage.getData().get("title");
                }

                if (remoteMessage.getData().containsKey("body")) {
                    body = remoteMessage.getData().get("body");
                }

            }

            if (body != null && title != null && !title.isEmpty() && !body.isEmpty()) {

                showNotification(title, body);

            }

        } catch (Exception e) {
            // Exception
        }
    }

    private void showNotification(String title, String message) {

        try {

            int notificationId = (int) System.currentTimeMillis();

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this,
                    notificationId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            String channelId = "loan_emi_fcm_channel";

            NotificationManager manager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (manager == null) {
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "LoanEMI Notification",
                        NotificationManager.IMPORTANCE_HIGH
                );

                channel.setDescription("LoanEMI App Notifications");

                manager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.ic_noti)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setContentIntent(pendingIntent);

            manager.notify(notificationId, builder.build());

        } catch (Exception e) {
            // Exception
        }
    }
}
