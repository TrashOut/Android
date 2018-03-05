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
import java.util.Date;
import java.util.HashMap;

import me.trashout.utils.DateTimeUtils;

public class TrashFilter {

    private ArrayList<Constants.TrashType> trashTypes = new ArrayList<>();
    private ArrayList<Constants.TrashSize> trashSizeList;
    private ArrayList<Constants.TrashStatus> trashStatusList = new ArrayList<>();
    private Accessibility accessibility;
    private Constants.LastUpdate lastUpdate;

    public ArrayList<Constants.TrashType> getTrashTypes() {
        return trashTypes;
    }

    public void setTrashTypes(ArrayList<Constants.TrashType> trashTypes) {
        this.trashTypes = trashTypes;
    }

    public ArrayList<Constants.TrashSize> getTrashSizeList() {
        return trashSizeList;
    }

    public void setTrashSizeList(ArrayList<Constants.TrashSize> trashSize) {
        this.trashSizeList = trashSize;
    }

    public ArrayList<Constants.TrashStatus> getTrashStatusList() {
        return trashStatusList;
    }

    public void setTrashStatusList(ArrayList<Constants.TrashStatus> trashStatusList) {
        this.trashStatusList = trashStatusList;
    }

    public Accessibility getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(Accessibility accessibility) {
        this.accessibility = accessibility;
    }

    public Constants.LastUpdate getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Constants.LastUpdate lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Generate filter querry map
     *
     * @return
     */
    public HashMap<String, String> generateFilterQuerryMap() {
        HashMap<String, String> filterQuerryMap = new HashMap<>();

        if (trashTypes != null && !trashTypes.isEmpty()) {
            ArrayList<String> trashtypeString = new ArrayList<>(trashTypes.size());
            for (Constants.TrashType trashType : trashTypes) {
                trashtypeString.add(trashType.getName());
            }

            filterQuerryMap.put("trashType", TextUtils.join(",", trashtypeString));
        }

        if (trashSizeList != null && !trashSizeList.isEmpty()) {

            ArrayList<String> trashSizeString = new ArrayList<>(trashSizeList.size());
            for (Constants.TrashSize trashSize : trashSizeList) {
                trashSizeString.add(trashSize.getName());
            }

            filterQuerryMap.put("trashSize", TextUtils.join(",", trashSizeString));
        }

        if (trashStatusList != null && !trashStatusList.isEmpty() && !(trashStatusList.contains(Constants.TrashStatus.UNKNOWN) && trashStatusList.contains(Constants.TrashStatus.CLEANED) && trashStatusList.contains(Constants.TrashStatus.STILL_HERE))) {
            ArrayList<String> trashStatusString = new ArrayList<>();
            Boolean updateNeeded = null;

            if (trashStatusList.contains(Constants.TrashStatus.STILL_HERE)) {
                if (!trashStatusList.contains(Constants.TrashStatus.CLEANED)) {
                    trashStatusString.add(Constants.TrashStatus.STILL_HERE.getName());
                    trashStatusString.add(Constants.TrashStatus.LESS.getName());
                    trashStatusString.add(Constants.TrashStatus.MORE.getName());
                }

                if (!trashStatusList.contains(Constants.TrashStatus.UNKNOWN)) {
                    updateNeeded = false;
                }
            } else if (trashStatusList.contains(Constants.TrashStatus.CLEANED)) {
                trashStatusString.add(Constants.TrashStatus.CLEANED.getName());
            }

            if (trashStatusList.contains(Constants.TrashStatus.UNKNOWN) && !trashStatusList.contains(Constants.TrashStatus.CLEANED) && !trashStatusList.contains(Constants.TrashStatus.STILL_HERE)) {
                updateNeeded = true;
            }

            if (!trashStatusString.isEmpty())
                filterQuerryMap.put("trashStatus", TextUtils.join(",", trashStatusString));

            if (updateNeeded != null)
                filterQuerryMap.put("updateNeeded", String.valueOf(updateNeeded));
        }

        if (accessibility != null && accessibility.isSomeAccessibilityValue()) {
            ArrayList<String> accessibilitiesString = new ArrayList<>();

            if (accessibility.isByCar())
                accessibilitiesString.add("byCar");
            if (accessibility.isInCave())
                accessibilitiesString.add("inCave");
            if (accessibility.isUnderWater())
                accessibilitiesString.add("underWater");
            if (accessibility.isNotForGeneralCleanup())
                accessibilitiesString.add("notForGeneralCleanup");

            filterQuerryMap.put("trashAccessibility", TextUtils.join(",", accessibilitiesString));
        }

        if (lastUpdate != null && !lastUpdate.equals(Constants.LastUpdate.NO_LIMIT)) {
            filterQuerryMap.put("timeBoundaryFrom", DateTimeUtils.getDateBefore(lastUpdate));
            filterQuerryMap.put("timeBoundaryTo", DateTimeUtils.TIMESTAMP_FORMAT.format(new Date()));
        }

        return filterQuerryMap;
    }

    @Override
    public String toString() {
        return "TrashFilter{" +
                "trashTypes=" + trashTypes +
                ", trashSizeList=" + trashSizeList +
                ", trashStatusList=" + trashStatusList +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
