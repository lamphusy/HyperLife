package com.hyperlife.model;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.hyperlife.R;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"notify_sleep")
                .setSmallIcon(R.drawable.sleeping_in_bed)
                .setContentTitle("It's time to go to sleep!")
                .setContentText("Good night! I love you!")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);


        NotificationManager notificationManagerCompat =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);;
        notificationManagerCompat.notify(200,builder.build());
    }
}
