
/*
 * TrashOut is an environmental project that teaches people how to recycle 
 * and showcases the worst way of handling waste - illegal dumping. All you need is a smart phone.
 *  
 *  
 * There are 10 types of programmers - those who are helping TrashOut and those who are not.
 * Clean up our code, so we can clean up our planet. 
 * Get in touch with us: help@trashout.ngo
 *  
 * Copyright 2017 TrashOut, n.f.
 *  
 * This file is part of the TrashOut project.
 *  
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * See the GNU General Public License for more details: <https://www.gnu.org/licenses/>.
 */

package me.trashout.service.base;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import me.trashout.Configuration;
import me.trashout.R;
import me.trashout.api.ApiServer;
import me.trashout.api.base.ApiBaseDataResult;
import me.trashout.api.base.ApiBaseRequest;
import me.trashout.api.base.ApiBaseUpdate;
import me.trashout.api.base.ApiResult;
import me.trashout.api.base.ApiUpdate;
import me.trashout.model.Constants;
import me.trashout.model.Image;
import me.trashout.model.serialize.CollectionPointSizeSerializer;
import me.trashout.model.serialize.CollectionPointTypeSerializer;
import me.trashout.model.serialize.TrashSizeSerializer;
import me.trashout.model.serialize.TrashStatusSerializer;
import me.trashout.model.serialize.TrashTypeSerializer;
import me.trashout.model.serialize.UserActivityTypeSerializer;
import me.trashout.notification.PushNotification;
import me.trashout.utils.DateTimeUtils;
import me.trashout.utils.PreferencesHandler;
import me.trashout.utils.Utils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class BaseService extends Service {
    protected final String TAG = this.getClass().getSimpleName();

    private static final int SOCKET_OPERATION_TIMEOUT = 60 * 1000;
    private static final int READ_CONNECTION_TIMEOUT = 30 * 1000;

    private static final String EXTRA_FOREGROUND = "foreground";

    private final IBinder mBinder = new LocalBinder();
    private static Handler mHandler;
    private ArrayList<UpdateServiceListener> updateServiceListeners = new ArrayList<>();
    private boolean syncInProgress;
    protected Thread workingThread;
    private boolean mStopRequested = false;
    protected static ApiServer mApiServer;
    private boolean isNotified;
    protected Intent intent;

    public static interface UpdateServiceListener {
        /**
         * @param apiResult result from services - include data. state, exception
         */
        public void onNewResult(ApiResult apiResult);

        /**
         * @param apiUpdate result from services - update data
         */
        public void onNewUpdate(ApiUpdate apiUpdate);
    }


    public interface OnImageUploadListener {
        void onComplete(UploadStatus status, ArrayList<Image> images);
    }

    public enum UploadStatus {
        SUCCESS, FAILED
    }


    protected void uploadImages(final StorageReference ref, final ArrayList<Uri> imagesToUpload, final int index,
                                final ArrayList<Image> images, final OnImageUploadListener onImageUploadListener) {

        final String uuid = UUID.randomUUID().toString();
        final StorageReference tempRef = ref.child(uuid);
        tempRef.putFile(imagesToUpload.get(index)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                final Image image = new Image();
                image.setCreated(DateTimeUtils.TIMESTAMP_FORMAT.format(new Date()));
                image.setFullStorageLocation(tempRef.toString());

                if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null && taskSnapshot.getMetadata().getReference().getDownloadUrl() != null) {
                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();
                            image.setFullDownloadUrl(downloadUrl);

                            try {
                                byte[] thumbnailByteArray = Utils.resizeBitmap(BaseService.this, imagesToUpload.get(index), 150);
                                final StorageReference tempRefThumbnail = ref.child(uuid + "_thumb");
                                tempRefThumbnail.putBytes(thumbnailByteArray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        image.setThumbStorageLocation(tempRefThumbnail.toString());

                                        if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null && taskSnapshot.getMetadata().getReference().getDownloadUrl() != null) {
                                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String downloadUrl = uri.toString();
                                                    image.setThumbDownloadUrl(downloadUrl);
                                                    images.add(image);
                                                    if (index != imagesToUpload.size() - 1) {
                                                        uploadImages(ref, imagesToUpload, index + 1, images, onImageUploadListener);
                                                    } else {
                                                        onImageUploadListener.onComplete(UploadStatus.SUCCESS, images);
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    onImageUploadListener.onComplete(UploadStatus.FAILED, null);
                                                }
                                            });
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        onImageUploadListener.onComplete(UploadStatus.FAILED, null);
                                    }
                                });
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            onImageUploadListener.onComplete(UploadStatus.FAILED, null);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onImageUploadListener.onComplete(UploadStatus.FAILED, null);
            }
        });
    }

    protected String getServiceTAGSuffix() {
        return "_" + this.getClass().getSimpleName();
    }

    public class LocalBinder extends Binder {
        public BaseService getService() {
            return BaseService.this;
        }
    }

    @Override
    public void onCreate() {
        if (mHandler == null)
            mHandler = new Handler();

        if (mApiServer == null) {
            Gson gson = new GsonBuilder()
//                    .serializeNulls()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    .registerTypeAdapter(Constants.TrashSize.class, new TrashSizeSerializer())
                    .registerTypeAdapter(Constants.TrashStatus.class, new TrashStatusSerializer())
                    .registerTypeAdapter(Constants.TrashType.class, new TrashTypeSerializer())
                    .registerTypeAdapter(Constants.CollectionPointSize.class, new CollectionPointSizeSerializer())
                    .registerTypeAdapter(Constants.CollectionPointType.class, new CollectionPointTypeSerializer())
                    .registerTypeAdapter(Constants.UserActivityType.class, new UserActivityTypeSerializer())
//                    .registerTypeAdapter(boolean.class, new BooleanSerializer())
                    .create();


            Retrofit ra = new Retrofit.Builder()
                    .baseUrl(Configuration.API_BASE_URL)
                    .client(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            mApiServer = ra.create(ApiServer.class);
        }
    }

    /**
     * Prepare OkHttpClient
     *
     * @return okHttpClient
     */
    private OkHttpClient getOkHttpClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(SOCKET_OPERATION_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(SOCKET_OPERATION_TIMEOUT, TimeUnit.MILLISECONDS)
                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(logging)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {

                        String firebaseToken = PreferencesHandler.getFirebaseToken(getBaseContext());

                        Request.Builder newRequestBuilder = chain.request().newBuilder().addHeader("Accept", "application/json");

                        if (!TextUtils.isEmpty(firebaseToken)) {
                            newRequestBuilder.addHeader("X-Token", firebaseToken);
                        }

                        return chain.proceed(newRequestBuilder.build());
                    }
                }).build();

        return okHttpClient;
    }

    /**
     * reseting apiserver
     */
    public static void resetApiServer() { // pokud napriklad po prihlaseni potrebuju zmenit token pro komunikaci...
        mApiServer = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Android O+ fix (part 2 of 2) (https://www.programmersought.com/article/152032662/)
        boolean foreground = intent.getBooleanExtra(EXTRA_FOREGROUND, false);
        if (foreground && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Context context = getBaseContext();

            PushNotification pushNotificationType = PushNotification.TRASH_OFFLINE_NOTIFICATION;
            Notification notification = new NotificationCompat.Builder(context, pushNotificationType.getNotificationChannel().getChannelId())
                    .setContentTitle(context.getString(R.string.app_name))
                    .setSmallIcon(R.drawable.ic_trash_type_organic)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setGroup(pushNotificationType.getNotificationChannel().getChannelId())
                    .build();

            startForeground(1, notification);
        }

        this.intent = intent;
        Log.i("Service" + getServiceTAGSuffix(), "onStartCommand");
        startCommunicationProcess();
        return START_STICKY;
    }


    /**
     * Start communication process
     */
    protected void startCommunicationProcess() {
        if (!isCommunicationInProgress()) {
            setCommunicationProgress(true);
            Log.i("Service" + getServiceTAGSuffix(), "startCommunicationProcess");

            workingThread = new Thread() {
                @Override
                public void run() {
                    doInBackground();
                    mStopRequested = false;
                    Log.i("Service" + getServiceTAGSuffix(), "finished");
                    setCommunicationProgress(false);
                    stopSelf();
                }
            };
            workingThread.start();
        } else {
            if (workingThread != null) {
                Log.i("Service" + getServiceTAGSuffix(), "before notify");

                synchronized (workingThread) {
                    isNotified = true;
                    workingThread.notifyAll();
                    Log.i("Service" + getServiceTAGSuffix(), "notified");
                }
            }
            Log.i("Service" + getServiceTAGSuffix(), "sync started");
        }

    }

    /**
     * Add communication request
     *
     * @param context        Context
     * @param clazz          Communication service class
     * @param apiBaseRequest Communication request
     * @param mRequestList   List of requests
     * @param foreground     Run foreground service? (for Android O+)
     */
    protected static synchronized void addRequest(Context context, Class<?> clazz, ApiBaseRequest apiBaseRequest, List<ApiBaseRequest> mRequestList, boolean foreground) {
        Log.d("BaseService", "startForRequest");
        if (context == null)
            return;

        if (mRequestList.contains(apiBaseRequest)) {
            mRequestList.remove(apiBaseRequest);
        }
        mRequestList.add(apiBaseRequest);

        // Android O+ fix (part 1 of 2) (https://www.programmersought.com/article/152032662/)
        if (foreground && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(context, clazz);
            intent.putExtra(EXTRA_FOREGROUND, true);
            context.startForegroundService(intent);
        } else {
            context.startService(new Intent(context, clazz));
        }
    }

    /**
     * Add communication request
     *
     * @param context        Context
     * @param clazz          Communication service class
     * @param apiBaseRequest Communication request
     * @param mRequestList   List of requests
     */
    protected static synchronized void addRequest(Context context, Class<?> clazz, ApiBaseRequest apiBaseRequest, List<ApiBaseRequest> mRequestList) {
        addRequest(context, clazz, apiBaseRequest, mRequestList, false);
    }

    protected abstract List<ApiBaseRequest> getRequestList();

    protected abstract void requestProcess(ApiBaseRequest apiBaseRequest);

    /**
     * Background worker
     */
    protected synchronized void doInBackground() {
        Log.v(TAG, "doInBackground - class = " + this.getClass().getSimpleName());
        boolean anyPending;
        do {
            List<ApiBaseRequest> pendingRequests = new ArrayList<>();
            synchronized (BaseService.class) {
                for (ApiBaseRequest request : getRequestList()) {
                    pendingRequests.add(request);
                }
            }
            anyPending = pendingRequests.size() > 0;

            for (ApiBaseRequest request : pendingRequests) {
                try {
                    if (request.getStatus() == ApiBaseRequest.Status.NEW) {
                        requestProcess(request);
                    } else if (request.getStatus() == ApiBaseRequest.Status.DONE || request.getStatus() == ApiBaseRequest.Status.ERROR) {
                        synchronized (BaseService.class) {
                            getRequestList().remove(request);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    notifyResultListener(0, null, null, e);
                    anyPending = false;//so it won't end up in an infinite loop
                }
            }
//            if (anyPending) {//only notify listener if new data were requested
//                notifyResultListener(null);
//            }
            try {
                Log.v(TAG, "waiting");
                synchronized (workingThread) {
                    workingThread.wait(3000);
                }
            } catch (InterruptedException e) {//Does NOT occure after notify or notifyAll is called!!!
                Log.e(TAG, "BUNDLE - Interrupted exception");
                e.printStackTrace();
                anyPending = true;// interrupted, so assume new data arrived
            }
            Log.v(TAG, "waited");

            if (!anyPending) {
                anyPending = getRequestList().size() > 0;
            }
        } while (hasBeenNotified() || (anyPending && !isStopRequested()));
    }

    /**
     * Get if process hes been notified
     *
     * @return isNotified
     */
    protected boolean hasBeenNotified() {
        boolean rtrn = isNotified;
        isNotified = false;
        Log.v("hasBeenNotified", rtrn + "");
        return rtrn;
    }


    /**
     * Get state if communication is in progress
     *
     * @return syncInProgress
     */
    public boolean isCommunicationInProgress() {
        synchronized (BaseService.class) {
            return syncInProgress;
        }
    }

    /**
     * Set communication progress
     *
     * @param isInProgress
     */
    protected void setCommunicationProgress(boolean isInProgress) {
        synchronized (BaseService.class) {
            syncInProgress = isInProgress;
        }
    }


    /**
     * @param requestId requestId
     * @param result    Object result
     * @param response  Info about response
     * @param e         Exception if error occured
     */
    protected void notifyResultListener(final int requestId, final ApiBaseDataResult result, final Response response, final Throwable e) {
        notifyResultListener(requestId, null, result, response, e);
    }

    /**
     * @param requestId requestId
     * @param result    Object result
     * @param response  Info about response
     * @param e         Exception if error occured
     */
    protected void notifyResultListener(final int requestId, final ApiBaseRequest request, final ApiBaseDataResult result, final Response response, final Throwable e) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (updateServiceListeners != null && updateServiceListeners.size() > 0) {
                    ApiResult apiResult = new ApiResult(requestId, request, result, response, e);
                    for (UpdateServiceListener updateServiceListener : updateServiceListeners) {
                        updateServiceListener.onNewResult(apiResult);
                    }
                }
            }
        });
    }

    /**
     * @param requestId     request id
     * @param apiBaseUpdate Object update
     */
    protected void notifyUpdateListener(final int requestId, final ApiBaseUpdate apiBaseUpdate) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (updateServiceListeners != null && updateServiceListeners.size() > 0) {
                    ApiUpdate apiUpdate = new ApiUpdate(requestId, apiBaseUpdate);
                    for (UpdateServiceListener updateServiceListener : updateServiceListeners) {
                        updateServiceListener.onNewUpdate(apiUpdate);
                    }
                }
            }
        });
    }

    /**
     * Add update listener
     *
     * @param updateServiceListener
     */
    public void addUpdateListener(UpdateServiceListener updateServiceListener) {
        synchronized (BaseService.class) {
            Log.d("BaseServices", "addListenr - " + updateServiceListener.toString());
            updateServiceListeners.add(updateServiceListener);
        }
    }

    /**
     * Remove update listener
     *
     * @param updateServiceListener
     */
    public void removeUpdateListener(UpdateServiceListener updateServiceListener) {
        synchronized (BaseService.class) {
            updateServiceListeners.remove(updateServiceListener);
        }
    }

    /**
     * @return
     */
    protected boolean isStopRequested() {
        return mStopRequested;
    }

    @Override
    public void onDestroy() {
        Log.i("Service" + getServiceTAGSuffix(), "onDestroy");
        super.onDestroy();
        mStopRequested = true;
    }
}
