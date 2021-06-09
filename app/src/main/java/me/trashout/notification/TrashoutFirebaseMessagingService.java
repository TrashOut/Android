
package me.trashout.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import me.trashout.R;
import me.trashout.activity.StartActivity;

import static androidx.core.app.NotificationCompat.DEFAULT_ALL;
import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;

public class TrashoutFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = TrashoutFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived: " + remoteMessage);

        PushNotification pushNotificationType = PushNotification.TRASH_DEFAULT_NOTIFICATION;
        String title = (remoteMessage.getNotification() != null && !TextUtils.isEmpty(remoteMessage.getNotification().getTitle())) ? remoteMessage.getNotification().getTitle() : getString(R.string.app_name);
        String body = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getBody() : "";

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
            Log.d(TAG, (entry.getKey() + "/" + entry.getValue()));

            if (entry.getKey().equals("extratype")) {
                Log.d(TAG, "This message has extratype, so I will be silent");
                return;
            }

            if (entry.getKey().equals("type")) {
                pushNotificationType = PushNotification.getNotificationType(entry.getValue());
            }

            if (entry.getKey().equals("titleLocKey")) {
                title = getString(getResources().getIdentifier(entry.getValue(), "string", getPackageName()));
            }

            if (entry.getKey().equals("title")) {
                title = entry.getValue();
            }

            if (entry.getKey().equals("bodyLocKey")) {
                body = getString(getResources().getIdentifier(entry.getValue(), "string", getPackageName()));
            }

            if (entry.getKey().equals("body")) {
                body = entry.getValue();
            }

            intent.putExtra(entry.getKey(), entry.getValue());
        }

        intent.setAction(TAG + System.currentTimeMillis());

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (pushNotificationType != null && notificationManager != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, pushNotificationType.getNotificationChannel().getChannelId())
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.ic_trash_type_organic)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setGroup(pushNotificationType.getNotificationChannel().getChannelId())
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(DEFAULT_ALL);

            NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(this, pushNotificationType.getNotificationChannel().getChannelId())
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.ic_trash_type_organic)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setGroup(pushNotificationType.getNotificationChannel().getChannelId())
                    .setGroupSummary(true)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(DEFAULT_ALL);

            notificationBuilder.setContentIntent(pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!notificationChannelExists(pushNotificationType.getNotificationChannel().getChannelId(), notificationManager)) {
                    createChannel(pushNotificationType.getNotificationChannel(), notificationManager, getApplicationContext());
                }
            }

            notificationManager.notify((int) remoteMessage.getSentTime(), notificationBuilder.build());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                notificationManager.notify(pushNotificationType.getNotificationId(), summaryBuilder.build());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createChannel(PushNotification.PushNotificationChannel channel, NotificationManager notificationManager, Context context) {
        String channelTitle = context.getString(channel.getTitleRes());
        NotificationChannel notificationChannel = new NotificationChannel(channel.getChannelId(), channelTitle, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setShowBadge(true);
        notificationChannel.setLockscreenVisibility(VISIBILITY_PUBLIC);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static boolean notificationChannelExists(String channelId, NotificationManager notificationManager) {
        return notificationManager.getNotificationChannel(channelId) != null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void initNotificationChannels(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        for (PushNotification.PushNotificationChannel channel : PushNotification.PushNotificationChannel.values()) {
            if (notificationManager != null && !notificationChannelExists(channel.getChannelId(), notificationManager)) {
                createChannel(channel, notificationManager, context);
            }
        }
    }
}
