
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

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gps implements Parcelable {

    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("long")
    @Expose
    private double lng;
    @SerializedName("accuracy")
    @Expose
    private int accuracy;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("area")
    @Expose
    private Area area;

    public static Gps createGPSFromLocation(Location location){
        if (location == null)
            return null;
        Gps gps = new Gps();
        gps.setLat(location.getLatitude());
        gps.setLng(location.getLongitude());
        if (location.hasAccuracy()){
            gps.setAccuracy((int) location.getAccuracy());
        }
        gps.setSource("gps"); // TODO Fused?
        return gps;
    }

    public static Gps createGPSFromLatLng(LatLng location){
        if (location == null)
            return null;
        Gps gps = new Gps();
        gps.setLat(location.latitude);
        gps.setLng(location.longitude);
        gps.setSource("gps"); // TODO Fused?
        return gps;
    }

    /**
     *
     * @return
     * The lat
     */
    public double getLat() {
        return lat;
    }

    /**
     *
     * @param lat
     * The lat
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     *
     * @return
     * The lng
     */
    public double getLng() {
        return lng;
    }

    /**
     *
     * @param lng
     * The lng
     */
    public void setLng(double lng) {
        this.lng = lng;
    }

    /**
     *
     * @return
     * The accuracy
     */
    public int getAccuracy() {
        return accuracy;
    }

    /**
     *
     * @param accuracy
     * The accuracy
     */
    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    /**
     *
     * @return
     * The source
     */
    public String getSource() {
        return source;
    }

    /**
     *
     * @param source
     * The source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     *
     * @return
     * The area
     */
    public Area getArea() {
        return area;
    }

    /**
     *
     * @param area
     * The area
     */
    public void setArea(Area area) {
        this.area = area;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeInt(this.accuracy);
        dest.writeString(this.source);
        dest.writeParcelable(this.area, flags);
    }

    public Gps() {
    }

    protected Gps(Parcel in) {
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.accuracy = in.readInt();
        this.source = in.readString();
        this.area = in.readParcelable(Area.class.getClassLoader());
    }

    public static final Parcelable.Creator<Gps> CREATOR = new Parcelable.Creator<Gps>() {
        @Override
        public Gps createFromParcel(Parcel source) {
            return new Gps(source);
        }

        @Override
        public Gps[] newArray(int size) {
            return new Gps[size];
        }
    };
}
