package me.trashout.offline;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.ArrayList;

import me.trashout.R;
import me.trashout.activity.MainActivity;
import me.trashout.activity.base.BaseActivity;
import me.trashout.fragment.DashboardFragment;
import me.trashout.model.Trash;
import me.trashout.notification.PushNotification;
import me.trashout.service.CreateTrashService;
import me.trashout.service.UpdateTrashService;

import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;

public class OfflineTrashManager {
    private Context context;
    private OfflineTrashList offlineTrashList = new OfflineTrashList();
    private SharedPreferences sharedPreferences;

    private static final int CREATE_TRASH_REQUEST_ID = 450;
    private static final int UPDATE_TRASH_REQUEST_ID = 451;
    private static final String PREFS_NAME = "Offline";
    private static final String PREFS_KEY = "TrashList";

    private static boolean running = false;

    public OfflineTrashManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String serializedDataFromPreference = sharedPreferences.getString(PREFS_KEY, null);

        if (serializedDataFromPreference != null) {
            this.offlineTrashList = OfflineTrashList.create(serializedDataFromPreference);
        }
    }

    public void add(Trash trash, ArrayList<Uri> photos, boolean update) {
        offlineTrashList.list.add(new OfflineTrash(trash, photos, update));
        save();
    }

    public OfflineTrash get() {
        return offlineTrashList.list.size() > 0 ? offlineTrashList.list.get(0) : null;
    }

    public void remove() {
        if (offlineTrashList.list.size() > 0) {
            offlineTrashList.list.remove(0);
            save();
        }
    }

    private void save() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREFS_KEY, offlineTrashList.serialize());
        editor.commit();

        setPlan();
    }

    private void setPlan() {
        Constraints constraints = new Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(OfflineTrashWorker.class)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueueUniqueWork("offlineTrash", ExistingWorkPolicy.REPLACE, workRequest);
    }

    private void notifyUploaded() {
        Bundle args = new Bundle();
        Intent intent = BaseActivity.generateIntent(context, DashboardFragment.class.getName(), args, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        PushNotification pushNotificationType = PushNotification.TRASH_OFFLINE_NOTIFICATION;

        Notification notification = new NotificationCompat.Builder(context, pushNotificationType.getNotificationChannel().getChannelId())
                .setContentTitle(context.getString(R.string.trash_create_notification_title))
                .setContentText(context.getString(R.string.trash_create_notification_text))
                .setSmallIcon(R.drawable.ic_trash_type_organic)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setGroup(pushNotificationType.getNotificationChannel().getChannelId())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(pushNotificationType.getNotificationChannel().getChannelId()) == null) {
                String channelTitle = context.getString(pushNotificationType.getNotificationChannel().getTitleRes());
                NotificationChannel notificationChannel = new NotificationChannel(pushNotificationType.getNotificationChannel().getChannelId(), channelTitle, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setShowBadge(true);
                notificationChannel.setLockscreenVisibility(VISIBILITY_PUBLIC);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        notificationManager.notify(pushNotificationType.getNotificationId(), notification);
    }

    public boolean process() {
        OfflineTrash offlineTrash = get();

        if (offlineTrash == null) {
            return false;
        }

        if (offlineTrash.isUpdate()) {
            UpdateTrashService.startForRequest(context, UPDATE_TRASH_REQUEST_ID, offlineTrash.getTrash().getId(), offlineTrash.getTrash(), offlineTrash.getPhotos());
        } else {
            CreateTrashService.startForRequest(context, CREATE_TRASH_REQUEST_ID, offlineTrash.getTrash(), offlineTrash.getPhotos());
        }

        remove();
        return true;
    }

    public void processAll() {
        if (OfflineTrashManager.running) {
            return;
        } else {
            OfflineTrashManager.running = true;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean notify = false;

                while (process()) {
                    notify = true;
                    try {
                        Thread.sleep(1000 * 30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (notify) {
                    notifyUploaded();
                }

                OfflineTrashManager.running = false;
            }
        }).start();
    }
}
