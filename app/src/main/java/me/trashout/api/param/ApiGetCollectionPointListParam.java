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

import me.trashout.api.base.ApiBaseParam;
import me.trashout.model.CollectionPointFilter;
import me.trashout.model.Constants;

public class ApiGetCollectionPointListParam extends ApiBaseParam {

    private static final String BASE_ATTRIBUTES_NEEDED = "id,gpsShort,types,name,size";
    private static final String HOME_ATTRIBUTES_NEEDED = "id,gpsShort,size,name";
    private static final String ATTRIBUTES_ORDER_BY = "gps";

    private String attributesNeeded;
    private LatLng userPosition;
    private CollectionPointFilter collectionPointFilter;

    private int page = -1;
    private int limit = 20;

    public static ApiGetCollectionPointListParam createHomeScreenCollectionPointListParam(LatLng userPosition) {
        ApiGetCollectionPointListParam apiGetCollectionPointListParam = new ApiGetCollectionPointListParam(userPosition);
        apiGetCollectionPointListParam.setAttributesNeeded(HOME_ATTRIBUTES_NEEDED);
        apiGetCollectionPointListParam.setPage(1);
        apiGetCollectionPointListParam.setLimit(1);
        return apiGetCollectionPointListParam;
    }

    public ApiGetCollectionPointListParam() {
        this.attributesNeeded = BASE_ATTRIBUTES_NEEDED;
    }

    public ApiGetCollectionPointListParam(LatLng userPosition) {
        this.userPosition = userPosition;
    }

    public ApiGetCollectionPointListParam(CollectionPointFilter collectionPointFilter) {
        this.collectionPointFilter = collectionPointFilter;
        this.attributesNeeded = BASE_ATTRIBUTES_NEEDED;
    }

    public ApiGetCollectionPointListParam(CollectionPointFilter collectionPointFilter, LatLng userPosition, int page) {
        this.collectionPointFilter = collectionPointFilter;
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

    public void setCollectionPointFilter(CollectionPointFilter collectionPointFilter) {
        this.collectionPointFilter = collectionPointFilter;
    }

    public void setCollectionPointSize(Constants.CollectionPointSize collectionPointSize) {
        if (this.collectionPointFilter == null)
            this.collectionPointFilter = new CollectionPointFilter(collectionPointSize);
        else
            this.collectionPointFilter.setCollectionPointSize(collectionPointSize);
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

    public HashMap<String, String> getCollectionPointListOptions() {
        HashMap<String, String> collectionPointListOptions = new HashMap<>();

        if (!TextUtils.isEmpty(attributesNeeded)) {
            collectionPointListOptions.put("attributesNeeded", attributesNeeded);
        }

        if (userPosition != null) {
            collectionPointListOptions.put("userPosition", userPosition.latitude + "," + userPosition.longitude);
            collectionPointListOptions.put("orderBy", ATTRIBUTES_ORDER_BY);
        }

        if (collectionPointFilter != null) {
            collectionPointListOptions.putAll(collectionPointFilter.generateFilterQuerryMap());
        }

        collectionPointListOptions.put("limit", String.valueOf(limit));

        if (page >= 0) {
            collectionPointListOptions.put("page", String.valueOf(page));
        }

        return collectionPointListOptions;
    }
}
