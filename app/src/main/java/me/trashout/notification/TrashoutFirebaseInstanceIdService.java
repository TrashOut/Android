package me.trashout.notification;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import me.trashout.model.User;
import me.trashout.model.UserDevice;
import me.trashout.service.CreateDeviceService;
import me.trashout.service.DeleteDeviceService;
import me.trashout.utils.PreferencesHandler;
import me.trashout.utils.Utils;

public class TrashoutFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = TrashoutFirebaseInstanceIdService.class.getSimpleName();
    private static final int CREATE_USER_DEVICE_REQUEST_ID = 420;
    private static final int DELETE_USER_DEVICE_REQUEST_ID = 421;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        User currentUser = PreferencesHandler.getUserData(this);
        if (currentUser != null) {
            Log.d(TAG, "User logged in: " + currentUser);
            registerFcmToken(this);
        } else {
            Utils.resetFcmToken();
        }
    }

    public static void registerFcmToken(Context context) {
        if (PreferencesHandler.getUserData(context) != null) {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            UserDevice userDevice = new UserDevice(refreshedToken, Utils.getLocaleString(), Utils.getDeviceID(context));
            CreateDeviceService.startForRequest(context, CREATE_USER_DEVICE_REQUEST_ID, userDevice);
            subscribeToFirebaseTopics();
        }
    }

    public static void deleteFcmToken(Context context) {
        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        DeleteDeviceService.startForRequest(context, DELETE_USER_DEVICE_REQUEST_ID, fcmToken);
    }

    public static void subscribeToFirebaseTopics() {
        String locale = Utils.getLocaleString();
        FirebaseMessaging.getInstance().subscribeToTopic("marketing-" + locale);
        FirebaseMessaging.getInstance().subscribeToTopic("news-" + locale);
    }
}
