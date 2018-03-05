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

package me.trashout.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.trashout.Configuration;
import me.trashout.R;
import me.trashout.activity.MainActivity;
import me.trashout.activity.base.BaseActivity;
import me.trashout.api.ApiServer;
import me.trashout.api.param.ApiGetTrashListParam;
import me.trashout.fragment.TrashListFragment;
import me.trashout.model.Constants;
import me.trashout.model.Trash;
import me.trashout.model.TrashHunterState;
import me.trashout.model.serialize.TrashSizeSerializer;
import me.trashout.model.serialize.TrashStatusSerializer;
import me.trashout.model.serialize.TrashTypeSerializer;
import me.trashout.utils.ApplicationLifecycleHandler;
import me.trashout.utils.PositionUtils;
import me.trashout.utils.PreferencesHandler;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Miroslav Cupalka
 * @package me.trashout.service
 * @since 01.03.2017
 */
public class TrashHunterService extends Service {

    private static final int NOTIFICATION_ID = 47787;

    public static final String BUNDLE_LAST_POSITION = "BUNDLE_LAST_POSITION";

    private LocationManager locationManager;
    private LocationListener locationListener;
    private TrashHunterState trashHunterState;

    private HashMap<Long, Trash> lastTrashList;
    private ApiServer mApiServer;

    private Handler stopSelfHandler = new Handler();

    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            LatLng lastPosition = intent.getParcelableExtra(BUNDLE_LAST_POSITION);
            Log.d("TrashHunter", "onStartCommand - lastPosition = " + lastPosition);

            if (lastPosition != null) {
                try {
                    makeUseOfNewLocation(lastPosition);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

//        createNotification(1, PreferencesHandler.getTrashHunterState(getBaseContext()).getAreaSize());
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TrashHunter", "onCreate - this = " + this.toString());
        try {
            updateListener(true, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        setupLocationListener();
    }

    private void setupLocationListener() {

        trashHunterState = PreferencesHandler.getTrashHunterState(getBaseContext());
        Log.d("TrashHunter", "setupLocationListener: " + trashHunterState);

        if (trashHunterState != null && trashHunterState.isTrashHunterActive()) {

            stopSelfHandler.postDelayed(stopSelfRunable, trashHunterState.getTrashHunterRemainingTime());

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    try {
                        makeUseOfNewLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.d("TrashHunter", "onStatusChanged: provider = " + provider + ", status = " + status);
                }

                public void onProviderEnabled(String provider) {
                    Log.d("TrashHunter", "onProviderEnabled: " + provider);
                }

                public void onProviderDisabled(String provider) {
                    Log.d("TrashHunter", "onProviderDisabled: " + provider);
                }
            };


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, trashHunterState.getUpdateStatusTime(), 0, locationListener);
        } else {
            Toast.makeText(this, "Trash hunter not activate.", Toast.LENGTH_SHORT).show();
            this.stopSelf();
        }
    }


    private void makeUseOfNewLocation(LatLng location) throws IOException {
        Log.d("TrashHunter", "makeUseOfNewLocation: new location = " + location + " - this = " + this.toString());
        Log.d("TrashHunter", "makeUseOfNewLocation: " + PositionUtils.centerPointAndRadiusToBounds(location, trashHunterState.getAreaSize()));

        Call<List<Trash>> call = getApiServer().getTrashList(ApiGetTrashListParam.getTrashHunterOptions(location, trashHunterState.getAreaSize()));
        call.enqueue(new Callback<List<Trash>>() {
            @Override
            public void onResponse(Call<List<Trash>> call, Response<List<Trash>> response) {
                if (response.isSuccessful()) {

                    int trashChangeCount = 0;
                    if ((trashChangeCount = getTrashListChangeCount(response.body())) > 0 && !isApplicationOnTop()) {
                        createNotification(trashChangeCount, trashHunterState.getAreaSize());
                    }

                    try {
                        updateListener(false, response.body().size());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Trash>> call, Throwable t) {
            }
        });

    }

    private int getTrashListChangeCount(List<Trash> trashList) {

        if (lastTrashList == null) {
            lastTrashList = new HashMap<>();

            for (Trash trash : trashList) {
                lastTrashList.put(trash.getId(), trash);
            }

            return trashList.size();
        }

        HashMap<Long, Trash> newTrashList = new HashMap<>(trashList.size());

        int changeItemCount = 0;
        int addItemCount = 0;

        for (Trash trash : trashList) {
            Trash oldTrash = lastTrashList.get(trash.getId());

            if (oldTrash == null) {
                addItemCount++;
            } else if (!oldTrash.getStatus().equals(trash.getStatus())) {
                changeItemCount++;
            }

            newTrashList.put(trash.getId(), trash);
        }

        lastTrashList.clear();
        lastTrashList.putAll(newTrashList);

        Log.d("TrashHunter", "checkTrashListChange: changeItemCount = " + changeItemCount + ", addItemCount = " + addItemCount);
        return changeItemCount + addItemCount;
    }

    private void updateListener(final boolean changeSate, final Integer changeTrashCount) throws RemoteException {
        if (onTrashHunterChangeListeners != null && !onTrashHunterChangeListeners.isEmpty()) {
            if (changeSate) {
                for (ITrashHunterChangeListener onTrashHunterChangeListener : onTrashHunterChangeListeners) {
                    onTrashHunterChangeListener.onTrashHunterStateChange();
                }
            }

            if (changeTrashCount != null) {
                for (ITrashHunterChangeListener onTrashHunterChangeListener : onTrashHunterChangeListeners) {
                    onTrashHunterChangeListener.onTrashHunterTrashCountChange(changeTrashCount);
                }
            }
        }
    }

    private boolean isApplicationOnTop() {
         return ApplicationLifecycleHandler.isApplicationVisible();
    }

    private void createNotification(int trashCount, int trashHunterArea) {
        Log.d("TrashHunter", ".....createNotification..... trashHunterArea - " + trashHunterArea);

        Intent viewIntent = BaseActivity.generateIntent(this, TrashListFragment.class.getName(), TrashListFragment.generateBundle(true, trashHunterArea), MainActivity.class);

        viewIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        Context context = getBaseContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(getNotificationIcon())
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(String.format(getString(R.string.notification_new_trash_formatter), trashCount))
                .setAutoCancel(true)
                .setContentIntent(viewPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notif = mBuilder.build();
        notif.defaults |= Notification.DEFAULT_SOUND;
        notif.defaults |= Notification.DEFAULT_VIBRATE;

        mNotificationManager.notify(NOTIFICATION_ID, notif);
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_notification : R.mipmap.ic_launcher;
    }

    private Runnable stopSelfRunable = new Runnable() {
        @Override
        public void run() {
            Log.d("TrashHunter", "run: stopSelf");
            if (locationManager != null) {
                try {
                    locationManager.removeUpdates(locationListener);
                } catch (SecurityException e) {
                }
            }
            TrashHunterService.this.stopSelf();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TrashHunter", "onDestroy: ");

        if (locationManager != null) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.removeUpdates(locationListener);
            }
        }

        if (stopSelfHandler != null) {
            stopSelfHandler.removeCallbacks(stopSelfRunable);
        }
    }

    // Prepare api server
    private ApiServer getApiServer() {
        if (mApiServer == null) {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    .registerTypeAdapter(Constants.TrashSize.class, new TrashSizeSerializer())
                    .registerTypeAdapter(Constants.TrashStatus.class, new TrashStatusSerializer())
                    .registerTypeAdapter(Constants.TrashType.class, new TrashTypeSerializer())
                    .create();


            Retrofit ra = new Retrofit.Builder()
                    .baseUrl(Configuration.API_BASE_URL)
                    .client(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            mApiServer = ra.create(ApiServer.class);
        }
        return mApiServer;
    }

    private OkHttpClient getOkHttpClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(logging)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {

                        String firebaseToken = PreferencesHandler.getFirebaseToken(getBaseContext());

                        Request.Builder newRequestBuilder = chain.request().newBuilder()
                                .addHeader("Accept", "application/json");

                        if (!TextUtils.isEmpty(firebaseToken)) {
                            newRequestBuilder.addHeader("X-Token", firebaseToken);
                        }

                        return chain.proceed(newRequestBuilder.build());
                    }
                }).build();

        return okHttpClient;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return the interface
        return iRemoteServiceBinder;
    }


    private List<ITrashHunterChangeListener> onTrashHunterChangeListeners = new ArrayList<ITrashHunterChangeListener>();

    private final IRemoteService.Stub iRemoteServiceBinder = new IRemoteService.Stub() {

        @Override
        public void addOnTrashHunterChangeListener(ITrashHunterChangeListener listener) throws RemoteException {
            onTrashHunterChangeListeners.add(listener);
        }

        @Override
        public void removeOnTrashHunterChangeListener(ITrashHunterChangeListener listener) throws RemoteException {
            onTrashHunterChangeListeners.remove(listener);
        }
    };
}
