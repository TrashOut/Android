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

package me.trashout.api.param;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.HashMap;
import java.util.List;

import me.trashout.Configuration;
import me.trashout.api.base.ApiBaseParam;
import me.trashout.model.Constants;
import me.trashout.model.TrashFilter;
import me.trashout.utils.PositionUtils;

public class ApiGetTrashListParam extends ApiBaseParam {

    private static final String BASE_ATTRIBUTES_NEEDED = "id,gpsShort,types,status,images,updateTime,created,updateNeeded,updateHistory";
    private static final String HOME_ATTRIBUTES_NEEDED = "id,gpsShort,images";
    private static final String MAP_ATTRIBUTES_NEEDED = "id,gpsShort,types,status,updateNeeded";
    private static final String TRASH_HUNTER_ATTRIBUTES_NEEDED = "id,gpsShort,status,updateNeeded,updateHistory";
    private static final String ATTRIBUTES_ORDER_BY = "gps";

    private String attributesNeeded;
    private LatLng userPosition;
    private TrashFilter trashFilter;
    private List<String> geocells;

    private int page = -1;
    private int limit = Configuration.LIST_LIMIT_SIZE;

    private int radius = -1;
    private boolean isTrashHunterOptions = false;

    public static ApiGetTrashListParam createHomeScreenTrashListParam(LatLng userPosition) {
        ApiGetTrashListParam apiGetTrashListParam = new ApiGetTrashListParam(userPosition);
        apiGetTrashListParam.setAttributesNeeded(HOME_ATTRIBUTES_NEEDED);
        apiGetTrashListParam.setPage(1);
        apiGetTrashListParam.setLimit(2);
        return apiGetTrashListParam;
    }

    public static ApiGetTrashListParam createEventMapTrashListParam(TrashFilter trashFilter, LatLng userPosition) {
        ApiGetTrashListParam apiGetTrashListParam = new ApiGetTrashListParam(trashFilter, userPosition);
        apiGetTrashListParam.setAttributesNeeded(MAP_ATTRIBUTES_NEEDED);
        apiGetTrashListParam.setLimit(200);
        return apiGetTrashListParam;
    }

    public static ApiGetTrashListParam createMapTrashListParam(TrashFilter trashFilter, List<String> geocells) {
        ApiGetTrashListParam apiGetTrashListParam = new ApiGetTrashListParam();
        apiGetTrashListParam.setTrashFilter(trashFilter);
        apiGetTrashListParam.setAttributesNeeded(MAP_ATTRIBUTES_NEEDED);
        apiGetTrashListParam.setGeocells(geocells);
        apiGetTrashListParam.setLimit(1000);
        return apiGetTrashListParam;
    }

    public static ApiGetTrashListParam createTrashHunterTrashListParam(LatLng userPosition, int radius, int page) {
        ApiGetTrashListParam apiGetTrashListParam = new ApiGetTrashListParam(userPosition);
        apiGetTrashListParam.setPage(page);
        apiGetTrashListParam.setRadius(radius);
        apiGetTrashListParam.setTrashHunterOptions(true);
        apiGetTrashListParam.setAttributesNeeded(BASE_ATTRIBUTES_NEEDED);
        return apiGetTrashListParam;
    }

    public ApiGetTrashListParam() {
        this.attributesNeeded = BASE_ATTRIBUTES_NEEDED;
    }

    public ApiGetTrashListParam(LatLng userPosition) {
        this.userPosition = userPosition;
        this.attributesNeeded = BASE_ATTRIBUTES_NEEDED;
    }

    public ApiGetTrashListParam(TrashFilter trashFilter, LatLng userPosition) {
        this.trashFilter = trashFilter;
        this.userPosition = userPosition;
        this.attributesNeeded = BASE_ATTRIBUTES_NEEDED;
    }

    public ApiGetTrashListParam(TrashFilter trashFilter, LatLng userPosition, int page) {
        this.trashFilter = trashFilter;
        this.userPosition = userPosition;
        this.attributesNeeded = BASE_ATTRIBUTES_NEEDED;
        this.page = page;
    }

    public void setAttributesNeeded(String attributesNeeded) {
        this.attributesNeeded = attributesNeeded;
    }

    public void setUserPosition(LatLng userPosition) {
        this.userPosition = userPosition;
    }

    public void setTrashFilter(TrashFilter trashFilter) {
        this.trashFilter = trashFilter;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getPage() {
        return page;
    }

    public List<String> getGeocells() {
        return geocells;
    }

    public void setGeocells(List<String> geocells) {
        this.geocells = geocells;
    }

    public boolean isTrashHunterOptions() {
        return isTrashHunterOptions;
    }

    public void setTrashHunterOptions(boolean trashHunterOptions) {
        isTrashHunterOptions = trashHunterOptions;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public HashMap<String, String> getTrashListOptions() {

        if (isTrashHunterOptions) {
            return getTrashHunterOptions(userPosition, radius, !TextUtils.isEmpty(attributesNeeded) ? attributesNeeded : TRASH_HUNTER_ATTRIBUTES_NEEDED);
        }

        HashMap<String, String> trashListOptions = new HashMap<>();

        if (!TextUtils.isEmpty(attributesNeeded)) {
            trashListOptions.put("attributesNeeded", attributesNeeded);
        }

        if (userPosition != null) {
            trashListOptions.put("userPosition", userPosition.latitude + "," + userPosition.longitude);
            trashListOptions.put("orderBy", ATTRIBUTES_ORDER_BY);
        }

        if (trashFilter != null) {
            trashListOptions.putAll(trashFilter.generateFilterQuerryMap());
        }
        if (limit > 0)
            trashListOptions.put("limit", String.valueOf(limit));

        if (page >= 0) {
            trashListOptions.put("page", String.valueOf(page));
        }

        if (geocells != null && !geocells.isEmpty()) {
            trashListOptions.put("geocells", TextUtils.join(",", geocells));
        }

        return trashListOptions;
    }

    public static HashMap<String, String> getTrashHunterOptions(LatLng userPosition, int radius) {
        return getTrashHunterOptions(userPosition, radius, TRASH_HUNTER_ATTRIBUTES_NEEDED);
    }

    public static HashMap<String, String> getTrashHunterOptions(LatLng userPosition, int radius, String attributesNeeded) {
        HashMap<String, String> trashListOptions = new HashMap<>();

        if (userPosition != null) {
            trashListOptions.put("userPosition", userPosition.latitude + "," + userPosition.longitude);
            trashListOptions.put("orderBy", ATTRIBUTES_ORDER_BY);

            if (radius >= 0) {
                LatLngBounds latLngBounds = PositionUtils.centerPointAndRadiusToBounds(userPosition, radius);
                trashListOptions.put("area", latLngBounds.northeast.latitude + "," + latLngBounds.southwest.longitude + "," + latLngBounds.southwest.latitude + "," + latLngBounds.northeast.longitude);
            }
        }

        trashListOptions.put("attributesNeeded", attributesNeeded);
        trashListOptions.put("trashStatus", Constants.TrashStatus.STILL_HERE.getName() + "," + Constants.TrashStatus.MORE.getName() + "," + Constants.TrashStatus.LESS.getName());

        return trashListOptions;
    }
}
