package me.trashout.notification;

import android.support.annotation.StringRes;

import me.trashout.R;

public enum PushNotification {
    TRASH_DETAIL_NOTIFICATION("trash", "trash_id", 1337, PushNotificationChannel.TRASH_NOTIFICATION_CHANNEL),
    TRASH_EVENT_NOTIFICATION("event", "event_id", 1338, PushNotificationChannel.EVENT_NOTIFICATION_CHANNEL),
    TRASH_NEWS_NOTIFICATION("news", "news_id", 1339, PushNotificationChannel.NEWS_NOTIFICATION_CHANNEL),
    TRASH_DEFAULT_NOTIFICATION("default", "", 0, PushNotificationChannel.DEFAULT_NOTIFICATION_CHANNEL);

    private final String type;
    private final String id;

    private final int notificationId;

    private final PushNotificationChannel notificationChannel;

    PushNotification(String type, String id, int notificationId, PushNotificationChannel notificationChannel) {
        this.type = type;
        this.id = id;
        this.notificationId = notificationId;
        this.notificationChannel = notificationChannel;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public PushNotificationChannel getNotificationChannel() {
        return notificationChannel;
    }

    public static PushNotification getNotificationType(String type) {
        for (PushNotification pushNotification : PushNotification.values()) {
            if (pushNotification.type.equals(type)) {
                return pushNotification;
            }
        }

        return TRASH_DEFAULT_NOTIFICATION;
    }

    public enum PushNotificationChannel {
        TRASH_NOTIFICATION_CHANNEL("TRASH_NOTIFICATION_CHANNEL", R.string.push_notification_channel_trash),
        EVENT_NOTIFICATION_CHANNEL("EVENT_NOTIFICATION_CHANNEL", R.string.push_notification_channel_event),
        NEWS_NOTIFICATION_CHANNEL("NEWS_NOTIFICATION_CHANNEL", R.string.push_notification_channel_news),
        DEFAULT_NOTIFICATION_CHANNEL("DEFAULT_NOTIFICATION_CHANNEL", R.string.push_notification_channel_default);

        private final String channelId;

        public String getChannelId() {
            return channelId;
        }

        public int getTitleRes() {
            return titleRes;
        }

        private final int titleRes;

        PushNotificationChannel(String channelId, @StringRes int titleRes) {
            this.channelId = channelId;
            this.titleRes = titleRes;
        }
    }
}