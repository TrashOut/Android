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

package me.trashout.model;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author Miroslav Cupalka
 * @package me.trashout.model
 * @since 08.12.2016
 */
public class CollectionPointFilter {

    private ArrayList<Constants.CollectionPointType> collectionPointTypes = new ArrayList<>();
    private Constants.CollectionPointSize collectionPointSize;

    public CollectionPointFilter(ArrayList<Constants.CollectionPointType> collectionPointTypes, Constants.CollectionPointSize collectionPointSize) {
        this.collectionPointTypes = collectionPointTypes;
        this.collectionPointSize = collectionPointSize;
    }

    public CollectionPointFilter() {
    }

    public CollectionPointFilter(Constants.CollectionPointSize collectionPointSize) {
        this.collectionPointSize = collectionPointSize;
    }

    public ArrayList<Constants.CollectionPointType> getCollectionPointTypes() {
        return collectionPointTypes;
    }

    public void setCollectionPointTypes(ArrayList<Constants.CollectionPointType> collectionPointTypes) {
        this.collectionPointTypes = collectionPointTypes;
    }

    public Constants.CollectionPointSize getCollectionPointSize() {
        return collectionPointSize;
    }

    public void setCollectionPointSize(Constants.CollectionPointSize collectionPointSize) {
        this.collectionPointSize = collectionPointSize;
    }

    /**
     * Generate filter querry map
     *
     * @return
     */
    public HashMap<String, String> generateFilterQuerryMap() {
        HashMap<String, String> filterQuerryMap = new HashMap<>();

        if (collectionPointTypes != null && !collectionPointTypes.isEmpty()) {
            ArrayList<String> collectionPointTypesString = new ArrayList<>(collectionPointTypes.size());
            for (Constants.CollectionPointType collectionPointType : collectionPointTypes) {
                collectionPointTypesString.add(collectionPointType.getName());
            }

            filterQuerryMap.put("collectionPointType", TextUtils.join(",", collectionPointTypesString));
        }

        if (collectionPointSize != null && !collectionPointSize.equals(Constants.CollectionPointSize.ALL)) {
            filterQuerryMap.put("collectionPointSize", collectionPointSize.getName());
        }

        return filterQuerryMap;
    }
}
