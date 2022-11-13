package com.documentorworldke.android.fcm;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.documentorworldke.android.MainActivity;
import com.documentorworldke.android.R;
import com.documentorworldke.android.constants.Constants;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final int REQUEST_CODE = 0;
    private static final int FLAGS = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (remoteMessage.getData().size() > 0) {
            Timber.d("Message data payload: %s", remoteMessage.getData());
            Map<String, String> data = remoteMessage.getData();
            assert notification != null;
            try {
                sendNotification(notification,data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onNewToken(@NonNull String token) {
        Timber.d("Refreshed token: %s", token);
    }


    private void sendNotification(RemoteMessage.Notification notification, Map<String, String> data) throws IOException {
        Bitmap icon = BitmapFactory.decodeStream(new URL(Objects.requireNonNull(notification.getImageUrl()).toString()).openConnection().getInputStream());

        Intent buttonIntent = new Intent(getBaseContext(), NotificationReceiver.class);
        buttonIntent.putExtra("notificationId", 1);
        PendingIntent dismissIntent = PendingIntent.getBroadcast(getBaseContext(), REQUEST_CODE, buttonIntent, FLAGS);

        String messageType = data.get("type");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("objectID",messageType);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = Constants.CHANNEL_ID;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.icon)
                .setContentTitle(notification.getTitle())
                .setLargeIcon(icon)
                .setContentText(notification.getBody())
                .setAutoCancel(false)
                .setColor(Color.RED)
                .setLights(Color.RED,1000,300)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSound(defaultSoundUri)
                .addAction(android.R.drawable.ic_menu_view, "VIEW", pendingIntent)
                .addAction(android.R.drawable.ic_delete, "DISMISS", dismissIntent)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, Constants.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(Constants.CHANNEL_DESCRIPTION);
            channel.setShowBadge(true);
            channel.canShowBadge();
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100,200,300,400,500});
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(Constants.NOTIFICATION_ID, notificationBuilder.build());
    }
}
