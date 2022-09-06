package com.onoma.go4lunch.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.onoma.go4lunch.R;
import com.onoma.go4lunch.model.Restaurant;

public class BroadcastManager extends BroadcastReceiver {
    private final String DEFAULT_CHANNEL_ID = "0";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getBundleExtra("bundle");
        Restaurant restaurant = (Restaurant) bundle.getSerializable("restaurant");
        String names = bundle.getString("names");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, DEFAULT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_local_dining_24)
                .setContentTitle(context.getString(R.string.notification_content_title))
                .setContentText(context.getString(R.string.notification_content_description, restaurant.getName(), restaurant.getAdress(), names))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(context.getString(R.string.notification_content_description, restaurant.getName(), restaurant.getAdress(), names)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(100, builder.build());
    }
}
