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

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import me.trashout.R;

public class Area implements Parcelable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("centerLat")
    @Expose
    private double centerLat;
    @SerializedName("centerlong")
    @Expose
    private double centerlong;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("continent")
    @Expose
    private String continent;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("aa1")
    @Expose
    private String aa1;
    @SerializedName("aa2")
    @Expose
    private String aa2;
    @SerializedName("aa3")
    @Expose
    private String aa3;
    @SerializedName("locality")
    @Expose
    private String locality;
    @SerializedName("subLocality")
    @Expose
    private String subLocality;
    @SerializedName("street")
    @Expose
    private String street;
    @SerializedName("zip")
    @Expose
    private String zip;
    @SerializedName("zoomLevel")
    @Expose
    private int zoomLevel;
    @SerializedName("aliasId")
    @Expose
    private long aliasId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getCenterLat() {
        return centerLat;
    }

    public void setCenterLat(double centerLat) {
        this.centerLat = centerLat;
    }

    public double getCenterlong() {
        return centerlong;
    }

    public void setCenterlong(double centerlong) {
        this.centerlong = centerlong;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAa1() {
        return aa1;
    }

    public void setAa1(String aa1) {
        this.aa1 = aa1;
    }

    public String getAa2() {
        return aa2;
    }

    public void setAa2(String aa2) {
        this.aa2 = aa2;
    }

    public String getAa3() {
        return aa3;
    }

    public void setAa3(String aa3) {
        this.aa3 = aa3;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getSubLocality() {
        return subLocality;
    }

    public void setSubLocality(String subLocality) {
        this.subLocality = subLocality;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public int getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    public long getAliasId() {
        return aliasId;
    }

    public void setAliasId(long aliasId) {
        this.aliasId = aliasId;
    }

    /**
     * Get best address from returning fields
     *
     * @return
     */
    public String getFormatedLocation() {

        if (!TextUtils.isEmpty(this.street)) {
            return this.street + (!TextUtils.isEmpty(this.subLocality) ? ", " + this.subLocality : (!TextUtils.isEmpty(this.locality) ? ", " + this.locality : ""));
        } else if (!TextUtils.isEmpty(this.subLocality)) {
            return this.subLocality;
        } else if (!TextUtils.isEmpty(this.locality)) {
            return this.locality;
        } else if (!TextUtils.isEmpty(this.aa3)) {
            return this.aa3;
        } else if (!TextUtils.isEmpty(this.aa2)) {
            return this.aa2;
        } else if (!TextUtils.isEmpty(this.aa1)) {
            return this.aa1;
        } else if (!TextUtils.isEmpty(this.country)) {
            return this.country;
        } else if (!TextUtils.isEmpty(this.continent)) {
            return this.continent;
        }
        return "";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeDouble(this.centerLat);
        dest.writeDouble(this.centerlong);
        dest.writeString(this.type);
        dest.writeString(this.continent);
        dest.writeString(this.country);
        dest.writeString(this.aa1);
        dest.writeString(this.aa2);
        dest.writeString(this.aa3);
        dest.writeString(this.locality);
        dest.writeString(this.subLocality);
        dest.writeString(this.street);
        dest.writeString(this.zip);
        dest.writeInt(this.zoomLevel);
        dest.writeLong(this.aliasId);
    }

    public Area() {
    }

    protected Area(Parcel in) {
        this.id = in.readInt();
        this.centerLat = in.readDouble();
        this.centerlong = in.readDouble();
        this.type = in.readString();
        this.continent = in.readString();
        this.country = in.readString();
        this.aa1 = in.readString();
        this.aa2 = in.readString();
        this.aa3 = in.readString();
        this.locality = in.readString();
        this.subLocality = in.readString();
        this.street = in.readString();
        this.zip = in.readString();
        this.zoomLevel = in.readInt();
        this.aliasId = in.readLong();
    }

    public static final Parcelable.Creator<Area> CREATOR = new Parcelable.Creator<Area>() {
        @Override
        public Area createFromParcel(Parcel source) {
            return new Area(source);
        }

        @Override
        public Area[] newArray(int size) {
            return new Area[size];
        }
    };

    @Override
    public String toString() {
        return "Area{" +
                "id=" + id +
                ", centerLat=" + centerLat +
                ", centerlong=" + centerlong +
                ", type='" + type + '\'' +
                ", continent='" + continent + '\'' +
                ", country='" + country + '\'' +
                ", aa1='" + aa1 + '\'' +
                ", aa2='" + aa2 + '\'' +
                ", aa3='" + aa3 + '\'' +
                ", locality='" + locality + '\'' +
                ", subLocality='" + subLocality + '\'' +
                ", street='" + street + '\'' +
                ", zip='" + zip + '\'' +
                ", zoomLevel=" + zoomLevel +
                ", aliasId=" + aliasId +
                '}';
    }

    public static Area createWorldwideArea(Context context) {
        Area worldWideArea = new Area();
        worldWideArea.setCountry(context.getString(R.string.global_worldwide));
        return worldWideArea;
    }
}
