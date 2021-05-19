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

package me.trashout.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;

import me.trashout.model.CollectionPointFilter;
import me.trashout.model.TrashFilter;
import me.trashout.model.TrashHunterState;
import me.trashout.model.User;

public class PreferencesHandler {

    private static Gson GSON = new Gson();

    private static final String FIRST_RUN = "FIRST_RUN";
    private static final String FIREBASE_TOKEN = "FIREBASE_TOKEN";
    private static final String TRASH_FILTER_DATA = "TRASH_FILTER_DATA";
    private static final String COLLECTION_POINT_FILTER_DATA = "COLLECTION_POINT_FILTER_DATA";
    private static final String USER_DATA = "USER_DATA";
    private static final String LAST_LOCATION = "LAST_LOCATION";
    private static final String USER_LOCATION_FORMAT = "USER_LOCATION_FORMAT";
    private static final String TRASH_HUNTER_STATE = "TRASH_HUNTER_STATE";
    private static final String TUTORIAL_WAS_SHOWN = "TUTORIAL_WAS_SHOWN";
    private static final String DEVICE_LOCALE = "DEVICE_LOCALE";
    private static final String FCM_TOKEN_REGISTERED = "FCM_TOKEN_REGISTERED";

    /**
     * Setting first run
     *
     * @param context
     * @param firstRun
     */
    public static void setFirstRun(Context context, boolean firstRun) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(FIRST_RUN, firstRun).apply();
    }

    /**
     * Get if first run
     *
     * @param context
     * @return
     */
    public static boolean isFirstRun(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(FIRST_RUN, true);
    }


    /**
     * Save firebase token
     *
     * @param context
     * @param firebaseToken
     */
    public static void setFirebaseToken(Context context, String firebaseToken) {
        if (context == null) {
            return;
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(FIREBASE_TOKEN, firebaseToken).apply();
    }

    /**
     * Get Firebase token
     *
     * @param context
     * @return
     */
    @Nullable
    public static String getFirebaseToken(Context context) {
        if (context == null) {
            return null;
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(FIREBASE_TOKEN, "");
    }

    /**
     * Save trash filter
     *
     * @param context
     * @param trashFilter
     */
    public static void setTrashFilterData(Context context, TrashFilter trashFilter) {
        if (context == null) {
            return;
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(TRASH_FILTER_DATA, GSON.toJson(trashFilter, TrashFilter.class)).apply();
    }


    /**
     * Get trash filtr
     *
     * @param context
     * @return
     */
    @Nullable
    public static TrashFilter getTrashFilterData(Context context) {
        if (context == null) {
            return null;
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String trashFilterJson = pref.getString(TRASH_FILTER_DATA, "");
        if (TextUtils.isEmpty(trashFilterJson))
            return null;

        return GSON.fromJson(trashFilterJson, TrashFilter.class);

    }

    /**
     * Save collection point filter
     *
     * @param context
     * @param collectionPointFilter
     */
    public static void setCollectionPointFilterData(Context context, CollectionPointFilter collectionPointFilter) {
        if (context == null) {
            return;
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(COLLECTION_POINT_FILTER_DATA, GSON.toJson(collectionPointFilter, CollectionPointFilter.class)).apply();
    }


    /**
     * Get collection point filter
     *
     * @param context
     * @return
     */
    @Nullable
    public static CollectionPointFilter getCollectionPointFilterData(Context context) {
        if (context == null) {
            return null;
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String collectionPointFilterJson = pref.getString(COLLECTION_POINT_FILTER_DATA, "");
        if (TextUtils.isEmpty(collectionPointFilterJson))
            return null;

        return GSON.fromJson(collectionPointFilterJson, CollectionPointFilter.class);

    }


    /**
     * Save user
     *
     * @param context
     * @param user
     */
    public static void setUserData(Context context, User user) {
        if (context != null) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(USER_DATA, GSON.toJson(user, User.class)).apply();
        }
    }


    /**
     * Get user
     *
     * @param context
     * @return
     */
    @Nullable
    public static User getUserData(Context context) {
        if (context == null) {
            return null;
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String userJson = pref.getString(USER_DATA, "");
        if (TextUtils.isEmpty(userJson))
            return null;

        return GSON.fromJson(userJson, User.class);

    }

    /**
     * Setting user preferred location format - 0 - double, 1 - DMS
     *
     * @param context
     * @param locationFormat
     */
    public static void setUserPreferredLocationFormat(Context context, int locationFormat) {
        if (context == null) {
            return;
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(USER_LOCATION_FORMAT, locationFormat).apply();
    }

    /**
     * Get user preferred location format
     *
     * @param context
     * @return
     */
    public static int getUserPreferredLocationFormat(Context context) {
        if (context == null) {
            return 0;
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(USER_LOCATION_FORMAT, 0);
    }

    /**
     * Setting user's last known location
     *
     * @param context
     * @param lastLocation
     */
    public static void setUserLastLocation(Context context, LatLng lastLocation) {
        if (context == null || lastLocation == null) {
            return;
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(LAST_LOCATION, GSON.toJson(lastLocation, LatLng.class)).apply();
    }


    /**
     * Get user's last known location
     *
     * @param context
     * @return
     */
    @Nullable
    public static LatLng getUserLastLocation(Context context) {
        if (context == null) {
            return null;
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String lastLocationJson = pref.getString(LAST_LOCATION, "");
        if (TextUtils.isEmpty(lastLocationJson))
            return null;

        return GSON.fromJson(lastLocationJson, LatLng.class);
    }




    /**
     * Save Trash Hunter State
     *
     * @param context
     * @param trashHunterState
     */
    public static void setTrashHunterState(Context context, TrashHunterState trashHunterState) {
        if (context == null) {
            return;
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(TRASH_HUNTER_STATE, GSON.toJson(trashHunterState, TrashHunterState.class)).apply();
    }


    /**
     * Get Trash Hunter State
     *
     * @param context
     * @return
     */
    @Nullable
    public static TrashHunterState getTrashHunterState(Context context) {
        if (context == null) {
            return null;
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String trashHunterStateJson = pref.getString(TRASH_HUNTER_STATE, "");
        if (TextUtils.isEmpty(trashHunterStateJson))
            return null;

        return GSON.fromJson(trashHunterStateJson, TrashHunterState.class);

    }

    /**
     * Save Tutorial was shown State
     *
     * @param context
     * @param wasShown
     */
    public static void setTutorialWasShown(Context context, boolean wasShown) {
        if (context == null) {
            return;
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(TUTORIAL_WAS_SHOWN, wasShown).apply();
    }

    /**
     * Get Tutorial was shown State
     *
     * @param context
     * @return
     */
    public static boolean isTutorialWasShown(Context context) {
        if (context == null) {
            return false;
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(TUTORIAL_WAS_SHOWN, false);
    }

    /**
     * Save current device locale
     *
     * @param context
     * @param locale  ("en_US", "cs_CZ", "de_DE", "es_ES", "sk_SK")
     */
    public static void setDeviceLocale(Context context, String locale) {
        if (context == null) {
            return;
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(DEVICE_LOCALE, locale).apply();
    }

    /**
     * Get last saved device locale
     *
     * @param context
     * @return locale string representation ("en_US", "cs_CZ", "de_DE", "es_ES", "sk_SK")
     */
    @Nullable
    public static String getDeviceLocale(Context context) {
        if (context == null) {
            return null;
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(DEVICE_LOCALE, "");
    }

    /**
     * Save Tutorial was shown State
     *
     * @param context
     * @param isRegistered
     */
    public static void setFcmRegistered(Context context, boolean isRegistered) {
        if (context == null) {
            return;
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(FCM_TOKEN_REGISTERED, isRegistered).apply();
    }

    /**
     * Get Tutorial was shown State
     *
     * @param context
     * @return
     */
    public static boolean isFcmRegistered(Context context) {
        if (context == null) {
            return false;
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(FCM_TOKEN_REGISTERED, false);
    }
}
