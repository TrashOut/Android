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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ZoomPoint implements Parcelable, TrashMapItem {


    @SerializedName("geocell")
    @Expose
    private String geocell;
    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("long")
    @Expose
    private double lng;
    @SerializedName("counts")
    @Expose
    private Counts counts;

    public String getGeocell() {
        return geocell;
    }

    public void setGeocell(String geocell) {
        this.geocell = geocell;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLong() {
        return lng;
    }

    public void setLong(double lng) {
        this.lng = lng;
    }

    public Counts getCounts() {
        return counts;
    }

    public void setCounts(Counts counts) {
        this.counts = counts;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(this.lat, this.lng);
    }


    @Override
    public int getTotal() {
        return counts != null ? counts.getStillHere() + counts.getCleaned() + counts.getMore() + counts.getLess() : 0;
    }

    @Override
    public int getRemains() {
        return counts != null ? counts.getStillHere() + counts.getMore() + counts.getLess(): 0;
    }

    @Override
    public int getCleaned() {
        return counts != null ? counts.getCleaned() : 0;
    }

    @Override
    public int getUpdateNeeded() {
        return counts != null ? counts.getUpdateNeeded() : 0;
    }

    public ZoomPoint() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.geocell);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeParcelable(this.counts, flags);
    }

    protected ZoomPoint(Parcel in) {
        this.geocell = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.counts = in.readParcelable(Counts.class.getClassLoader());
    }

    public static final Creator<ZoomPoint> CREATOR = new Creator<ZoomPoint>() {
        @Override
        public ZoomPoint createFromParcel(Parcel source) {
            return new ZoomPoint(source);
        }

        @Override
        public ZoomPoint[] newArray(int size) {
            return new ZoomPoint[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ZoomPoint zoomPoint = (ZoomPoint) o;

        return geocell.equals(zoomPoint.geocell);
    }

    @Override
    public int hashCode() {
        return geocell.hashCode();
    }

    @Override
    public String toString() {
        return "ZoomPoint{" +
                "geocell='" + geocell + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", counts=" + counts +
                '}';
    }
}