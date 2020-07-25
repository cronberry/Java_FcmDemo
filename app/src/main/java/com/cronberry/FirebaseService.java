package com.cronberry;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class FirebaseService extends FirebaseMessagingService {

    private Integer HTML_FLAG = 2;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendOreoPushNotification(remoteMessage);
        } else {
            sendNotification(remoteMessage);
        }
    }


    @SuppressLint("LongLogTag")
    private void sendNotification(RemoteMessage remoteMessage) {
        if (!isAppIsInBackground(getApplicationContext())) {
            //foreground app

            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            if (remoteMessage.getData().containsKey("actionURL")) {
                resultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(remoteMessage.getData().get("actionURL")));
            } else {
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    0 /* Request code */, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                    getApplicationContext(),
                    CHANNEL_ID
            );
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.notification)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setNumber(10)
                    .setTicker("Cronberry")
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(pendingIntent)
                    .setContentInfo("Info");
            if (null != remoteMessage.getNotification().getImageUrl()) {
                Bitmap bitmapFromURL = getBitmapFromURL(remoteMessage.getNotification().getImageUrl());
                notificationBuilder.setLargeIcon(bitmapFromURL);
                Bitmap biy = null;
                notificationBuilder.setStyle(
                        new NotificationCompat.BigPictureStyle()
                                .bigPicture(bitmapFromURL)
                                .bigLargeIcon(null)
                );
            } else if (remoteMessage.getData().containsKey("icon")) {
                Bitmap bitmapFromURL = getBitmapFromURL(remoteMessage.getNotification().getImageUrl());
                notificationBuilder.setLargeIcon(bitmapFromURL);
                if (remoteMessage.getData().get("message").contains("</")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        notificationBuilder.setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(Html.fromHtml(remoteMessage.getData().get("message"), HTML_FLAG))
                        );
                    } else {
                        Log.d("cronberry", "Mesage without html");
                        notificationBuilder.setStyle(
                                new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("message"))
                        );
                    }
                } else {
                    Log.d("cronberry", "Mesage wihtout html");
                    notificationBuilder.setStyle(
                            new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("message"))
                    );
                }
            }
            notificationManager.notify(1, notificationBuilder.build());
        } else {
            Map<String, String> data = remoteMessage.getData();
            String title = data.get("title");
            String body = data.get("body");
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            if (remoteMessage.getData().containsKey("actionURL")) {
                resultIntent =
                        new Intent(Intent.ACTION_VIEW, Uri.parse(remoteMessage.getData().get("actionURL")));
            } else {
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    0 /* Request code */, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                    getApplicationContext(),
                    CHANNEL_ID
            );
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.notification)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setContentIntent(pendingIntent)
                    .setNumber(10)
                    .setTicker("Cronberry")
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentInfo("Info");

            if (null != remoteMessage.getNotification().getImageUrl()) {
                Bitmap bitmapFromURL = getBitmapFromURL(remoteMessage.getNotification().getImageUrl());
                notificationBuilder.setLargeIcon(bitmapFromURL);
                Bitmap biy = null;
                notificationBuilder.setStyle(
                        new NotificationCompat.BigPictureStyle()
                                .bigPicture(bitmapFromURL)
                                .bigLargeIcon(null)
                );
            } else if (remoteMessage.getData().containsKey("icon")) {
                Bitmap bitmapFromURL = getBitmapFromURL(remoteMessage.getNotification().getImageUrl());
                notificationBuilder.setLargeIcon(bitmapFromURL);

                if (remoteMessage.getData().get("message").toString().contains("</")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Log.d("cronberry", "Mesage from html");
                        notificationBuilder.setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(Html.fromHtml(remoteMessage.getData().get("message"), HTML_FLAG))
                        );
                    } else {
                        notificationBuilder.setStyle(
                                new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("message"))
                        );
                    }
                } else {
                    Log.d("cronberry", "Mesage from html");
                    notificationBuilder.setStyle(
                            new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("message"))
                    );
                }
            }
            notificationManager.notify(1, notificationBuilder.build());
        }
    }

    @SuppressLint("NewApi")
    private void sendOreoPushNotification(RemoteMessage remoteMessage) {
        if (!isAppIsInBackground(getApplicationContext())) {
            //foreground app
            Log.d("cronberry", "backound ot");
            String title = "Hello " + ((null != remoteMessage.getNotification()) ? remoteMessage.getNotification().getTitle() : "user");
            String body = (null != remoteMessage.getNotification()) ? remoteMessage.getNotification().getBody() : "Arnish";
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            if (remoteMessage.getData().containsKey("actionURL")) {
                resultIntent =
                        new Intent(Intent.ACTION_VIEW, Uri.parse(remoteMessage.getData().get("actionURL")));
            } else {
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    0 /* Request code */, resultIntent,
                    0
            );
            Uri defaultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            OreoNotification oreoNotification = new OreoNotification(this);
            Notification.Builder builder = oreoNotification.getOreoNotification(
                    title,
                    body,
                    pendingIntent,
                    defaultsound,
                    java.lang.String.valueOf(R.drawable.ic_launcher_background)
            );
            int i = 0;
            if (null != remoteMessage.getNotification() && null != remoteMessage.getNotification().getImageUrl()) {
                Bitmap bitmapFromURL = getBitmapFromURL(remoteMessage.getNotification().getImageUrl());
                builder.setLargeIcon(bitmapFromURL);
                builder.setStyle(new Notification.BigPictureStyle()
                        .bigPicture(bitmapFromURL)
                        .bigLargeIcon((Bitmap) null));
            } else if (remoteMessage.getData().containsKey("icon")) {
//                val bitmapFromURL = getBitmapFromURL(remoteMessage.notification!!.imageUrl)
//                builder.setLargeIcon(bitmapFromURL)

                if (remoteMessage.getData().get("message").toString().contains("</")) {
                    Log.d("cronberry", "Mesage from html");
                    builder.setStyle(new Notification.BigTextStyle()
                            .bigText(Html.fromHtml(remoteMessage.getData().get("message"), HTML_FLAG)));
                } else {
                    Log.d("cronberry", "Mesage wihtout html");
                    builder.setStyle(
                            new Notification.BigTextStyle().bigText(remoteMessage.getData().get("message")));
                }

            }
            oreoNotification.getManager().notify(i, builder.build());
        } else {

            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            if (remoteMessage.getData().containsKey("actionURL")) {
                resultIntent =
                        new Intent(Intent.ACTION_VIEW, Uri.parse(remoteMessage.getData().get("actionURL")));
            } else {
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    0 /* Request code */, resultIntent,
                    0
            );

            Uri defaultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            OreoNotification oreoNotification = new OreoNotification(this);
            Notification.Builder builder = oreoNotification.getOreoNotification(
                    title,
                    body,
                    pendingIntent,
                    defaultsound,
                    java.lang.String.valueOf(R.drawable.ic_launcher_background)
            );
            int i = 0;
            if (remoteMessage.getData().containsKey("imageUrl")) {
                Bitmap bitmapFromURL = getBitmapFromURL(remoteMessage.getNotification().getImageUrl());
                builder.setLargeIcon(bitmapFromURL);
                builder.setStyle(new Notification.BigPictureStyle()
                        .bigPicture(bitmapFromURL)
                        .bigLargeIcon((Bitmap) null));
            } else if (remoteMessage.getData().containsKey("icon")) {
                Bitmap bitmapFromURL = getBitmapFromURL(remoteMessage.getNotification().getImageUrl());
                builder.setLargeIcon(bitmapFromURL);
                if (remoteMessage.getData().get("message").toString().contains("</")) {
                    builder.setStyle(new Notification.BigTextStyle()
                            .bigText(Html.fromHtml(remoteMessage.getData().get("message"), HTML_FLAG)));
                } else {
                    Log.d("cronberry", "Mesage without html");
                    builder.setStyle(
                            new Notification.BigTextStyle().bigText(remoteMessage.getData().get("message")));
                }

            }
            oreoNotification.getManager().notify(i, builder.build());
        }
    }

    private Bitmap getBitmapFromURL(Uri strURL) {
        try {
            URL url = new URL(strURL.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private final String CHANNEL_ID = "Cronberry";

    private Boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess == context.getPackageName()) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName() == context.getPackageName()) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }

}
