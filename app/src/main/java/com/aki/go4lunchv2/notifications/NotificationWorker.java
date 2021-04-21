package com.aki.go4lunchv2.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.UI.MainActivity;

public class NotificationWorker extends Worker {

    public static final int NOTIFICATION_ID = 100;
    public static final String NOTIFICATION_TAG = "FIREBASETEST";
    public static Boolean notificationsEnabled = false;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //DEFINE WORK HERE (notification with restaurant info)

        String restaurantName = getInputData().getString("RESTAURANT_NAME");
        String formattedAddress = getInputData().getString("RESTAURANT_ADDRESS");
        String users = getInputData().getString("WORKMATES");


        if(users.isEmpty()){
            sendVisualNotification(getApplicationContext().getString(R.string.its_time_for_lunch) + restaurantName + " !",
                    getApplicationContext().getString(R.string.the_address_is) + formattedAddress,
                    getApplicationContext().getString(R.string.only_one));
        } else {
            sendVisualNotification(getApplicationContext().getString(R.string.its_time_for_lunch) + restaurantName + " !",
                    getApplicationContext().getString(R.string.the_address_is) + formattedAddress,
                    getApplicationContext().getString(R.string.youll_be_eating_with) + users);
        }
        return Result.success();
    }

    private void sendVisualNotification(String messageBody1, String messageBody2, String messageBody3) {
        // Creating an Intent that will be shown when user will click on the notification
        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // Creating a style for the notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(messageBody1);
        inboxStyle.addLine(messageBody2);
        inboxStyle.addLine(messageBody3);

        // Creating a channel (for android 8 and up)
        String channelId = "MAIN NOTIFICATIONS";

        // Building a notification Object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), channelId)
                        .setSmallIcon(R.drawable.ic_restaurant_marker)
                        .setContentTitle(getApplicationContext().getString(R.string.app_name))
                        .setContentText(getApplicationContext().getText(R.string.notification_title))
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);

        // Adding the notification to the notification manager and showing it
        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Main notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        // Showing notification
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }
}
