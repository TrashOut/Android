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

package com.beoui.geocell.model;

import com.google.android.gms.maps.model.LatLng;

/**
 *
 * @author Alexandre Gellibert
 *
 */
public class BoundingBox {

    private LatLng northEast;
    private LatLng southWest;

    public BoundingBox(double north, double east, double south, double west) {
        double north_,south_;
        if(south > north) {
            south_ = north;
            north_ = south;
        } else {
            south_ = south;
            north_ = north;
        }

        // Don't swap east and west to allow disambiguation of
        // antimeridian crossing.

        northEast = new LatLng(north_, east);
        southWest = new LatLng(south_, west);
    }

    public double getNorth() {
        return northEast.latitude;
    }

    public double getSouth() {
        return southWest.latitude;
    }

    public double getWest() {
        return southWest.longitude;
    }

    public double getEast() {
        return northEast.longitude;
    }

    public LatLng getNorthEast() {
        return northEast;
    }

    public LatLng getSouthWest() {
        return southWest;
    }

    @Override
    public String toString() {
        return "BoundingBox{" +
                "southWest=" + southWest +
                ", northEast=" + northEast +
                '}';
    }
}
