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

import java.util.HashMap;
import java.util.List;

import me.trashout.Configuration;
import me.trashout.api.base.ApiBaseParam;
import me.trashout.model.TrashFilter;

public class ApiGetZoomPointListParam extends ApiBaseParam {


    private static final String MAP_ATTRIBUTES_NEEDED = "id,gpsShort,types,status";
    private static final String ATTRIBUTES_ORDER_BY = "gps";

    private String attributesNeeded;
    private LatLng userPosition;
    private TrashFilter trashFilter;
    private List<String> geocells;
    private int zoomLevel;

    private int page = -1;
    private int limit = Configuration.LIST_LIMIT_SIZE;

    public static ApiGetZoomPointListParam createMapZoomPointListParam(TrashFilter trashFilter , List<String> geocells, int zoomzoomLevel) {
        ApiGetZoomPointListParam apiGetTrashListParam = new ApiGetZoomPointListParam();
        apiGetTrashListParam.setAttributesNeeded(MAP_ATTRIBUTES_NEEDED);
        apiGetTrashListParam.setTrashFilter(trashFilter);
        apiGetTrashListParam.setGeocells(geocells);
        apiGetTrashListParam.setZoomLevel(zoomzoomLevel);
        apiGetTrashListParam.setLimit(150);
        return apiGetTrashListParam;
    }

    public ApiGetZoomPointListParam() {
        this.attributesNeeded = MAP_ATTRIBUTES_NEEDED;
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

    public int getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    public HashMap<String, String> getZoomPointListOptions() {
        HashMap<String, String> zoomPointListOptions = new HashMap<>();

        if (!TextUtils.isEmpty(attributesNeeded)) {
            zoomPointListOptions.put("attributesNeeded", attributesNeeded);
        }

        if (userPosition != null) {
            zoomPointListOptions.put("userPosition", userPosition.latitude + "," + userPosition.longitude);
            zoomPointListOptions.put("orderBy", ATTRIBUTES_ORDER_BY);
        }

        if (trashFilter != null) {
            zoomPointListOptions.putAll(trashFilter.generateFilterQuerryMap());
        }

        if (limit > 0)
            zoomPointListOptions.put("limit", String.valueOf(limit));

        if (page >= 0) {
            zoomPointListOptions.put("page", String.valueOf(page));
        }

        if (geocells != null && !geocells.isEmpty()){
            zoomPointListOptions.put("geocells", TextUtils.join(",", geocells));
        }

        if (zoomLevel >= 0) {
            zoomPointListOptions.put("zoomLevel", String.valueOf(zoomLevel));
        }

        return zoomPointListOptions;
    }
}
