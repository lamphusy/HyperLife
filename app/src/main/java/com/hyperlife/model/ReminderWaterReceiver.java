package com.hyperlife.model;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.hyperlife.R;

import java.time.LocalDate;
import java.util.Calendar;

public class ReminderWaterReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"notify_drink")
                .setSmallIcon(R.drawable.water)
                .setContentTitle("Take a break!")
                .setContentText("Drink a cup of water")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);

        NotificationManager notificationManagerCompat =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManagerCompat.notify(100,builder.build());

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 50);
        c.set(Calendar.SECOND,00);

        if(System.currentTimeMillis() > c.getTimeInMillis()){
            notificationManagerCompat.cancel(100);
        }
    }
}
