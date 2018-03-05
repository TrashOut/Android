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

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import me.trashout.R;

public class PositionUtils {

    public static int[] ROUNDED_DISTANCE = {5, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000};


    public enum GeoCellProximityReal {
        KM_5000(1), KM_2000(2), KM_500(3), KM_128(4),
        KM_32(5), KM_8(6), KM_2(7), METERS_500(8), METERS_126(9),
        METERS_30(10), METERS_7(11), METERS_2(12), CENTIMETERS(13),
        MILIMETERS_50(14), MILIMETERS_20(15), MILIMETERS_5(16);

        private int code;

        private GeoCellProximityReal(int c) {
            code = c;
        }

        public int getValue() {
            return code;
        }
    }

    public static String getStaticMapUrl(Activity activity, double lat, double lng) {
        return activity.getString(R.string.staticMapUrlFormat, lat, lng, lat, lng, Utils.getMapApiKey(activity));
    }

    public static String getFormattedLocation(Context context, double latitude, double longitude) {

        if (PreferencesHandler.getUserPreferredLocationFormat(context) == 0) {
            return String.format(context.getString(R.string.position_formatter), latitude) + ", "
                    + String.format(context.getString(R.string.position_formatter), longitude);
        } else {
            return getFormattedLocationInDegree(context, latitude, longitude);
        }

    }

    private static String getFormattedLocationInDegree(Context context, double latitude, double longitude) {
        try {
            int latSeconds = (int) Math.round(latitude * 3600);
            int latDegrees = latSeconds / 3600;
            latSeconds = Math.abs(latSeconds % 3600);
            int latMinutes = latSeconds / 60;
            latSeconds %= 60;

            int longSeconds = (int) Math.round(longitude * 3600);
            int longDegrees = longSeconds / 3600;
            longSeconds = Math.abs(longSeconds % 3600);
            int longMinutes = longSeconds / 60;
            longSeconds %= 60;
            String latDegree = latDegrees >= 0 ? " N" : " S";
            String lonDegrees = longDegrees >= 0 ? " E" : " W";

            return Math.abs(latDegrees) + "°" + latMinutes + "'" + latSeconds
                    + "\"" + latDegree + " " + Math.abs(longDegrees) + "°" + longMinutes
                    + "'" + longSeconds + "\"" + lonDegrees;
        } catch (Exception e) {
            return String.format(context.getString(R.string.position_formatter), latitude) + ", "
                    + String.format(context.getString(R.string.position_formatter), longitude);
        }
    }

    public static String getRoundedFormattedDistance(Context context, int distance) {
        boolean imperialUnit = UnitLocale.getDefault() == UnitLocale.Imperial;
        if (imperialUnit) { // convert distance to yards
            distance = (int) (distance * 1.0936133);
        }

        int computeDistance = Math.abs(ROUNDED_DISTANCE[0] - distance);
        int idx = 0;
        for (int c = 1; c < ROUNDED_DISTANCE.length; c++) {
            int cdistance = Math.abs(ROUNDED_DISTANCE[c] - distance);
            if (cdistance < computeDistance) {
                idx = c;
                computeDistance = cdistance;
            }
        }

        computeDistance = ROUNDED_DISTANCE[idx];

        if (computeDistance > 10000) {
            return context.getString(imperialUnit ? R.string.distance_more_than_10mi : R.string.distance_more_than_10km);
        } else {
            return imperialUnit ? getFormattedDistanceInYard(context, computeDistance) : getFormattedDistanceInMeter(context, computeDistance);
        }
    }

    public static String getFormattedDistanceInMeter(Context context, int distanceInMeter) {
        if (distanceInMeter >= 1000) {
            distanceInMeter /= 1000;
            return String.format(context.getString(R.string.distance_km_formatter), distanceInMeter);
        } else {
            return String.format(context.getString(R.string.distance_m_formatter), distanceInMeter);
        }
    }

    public static String getFormattedDistanceInYard(Context context, int distanceInYard) {
        if (distanceInYard >= 1760) {
            distanceInYard /= 1760;
            return String.format(context.getString(R.string.distance_mile_formatter), distanceInYard);
        } else {
            return String.format(context.getString(R.string.distance_yard_formatter), distanceInYard);
        }
    }

    public static String getFormattedComputeDistance(Context context, LatLng location1, LatLng location2) {
        if (location1 == null || location2 == null)
            return "?m";
        int distance = (int) computeDistance(location1, location2);
        return getFormattedDistanceInMeter(context, distance);
    }

    public static double computeDistance(LatLng location1, LatLng location2) {
        if (location1 == null || location2 == null)
            return 0;

        return SphericalUtil.computeDistanceBetween(location1, location2);
    }

    public static LatLngBounds centerPointAndRadiusToBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }

    public static int convertMapZoomToServerZoom(int zoom) {
        switch (zoom) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return 4;
            case 6:
                return 5;
            case 7:
                return 6;
            case 8:
                return 7;
            case 9:
                return 8;
            case 10:
                return 9;
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
                return 13;
            default:
                return 4;
        }
    }

    public static int getResolutionForMapZoomLevel(int mapZoomLevel) {
        return getResolutionForZoomLevel(convertMapZoomToServerZoom(mapZoomLevel));
    }

    public static int getResolutionForZoomLevel(int zoomLevel) {
        int proximityOfGeocell = GeoCellProximityReal.KM_500.getValue();

        switch (zoomLevel) {
            case 1:
            case 2:
            case 3:
            case 4:
                proximityOfGeocell = GeoCellProximityReal.KM_2000.getValue();
                break;
            case 5:
            case 6:
                proximityOfGeocell = GeoCellProximityReal.KM_500.getValue();
                break;
            case 7:
            case 8:
                proximityOfGeocell = GeoCellProximityReal.KM_128.getValue();
                break;
            case 9:
            case 10:
                proximityOfGeocell = GeoCellProximityReal.KM_32.getValue();
                break;
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19: // do not use this one
            case 20:
            case 21:
                proximityOfGeocell = GeoCellProximityReal.KM_32.getValue();
            default:
                proximityOfGeocell = GeoCellProximityReal.KM_32.getValue();
                break;
        }

        return proximityOfGeocell;
    }

}
