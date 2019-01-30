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
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Image implements Parcelable {

    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("fullStorageLocation")
    @Expose
    private String fullStorageLocation;
    @SerializedName("fullDownloadUrl")
    @Expose
    private String fullDownloadUrl;
    @SerializedName("thumbDownloadUrl")
    @Expose
    private String thumbDownloadUrl;
    @SerializedName("thumbStorageLocation")
    @Expose
    private String thumbStorageLocation;
    @SerializedName("thumbRetinaStorageLocation")
    @Expose
    private String thumbRetinaStorageLocation;
    @SerializedName("thumbRetinaDownloadUrl")
    @Expose
    private String thumbRetinaDownloadUrl;
    @SerializedName("id")
    private int id;
    @SerializedName("isMain")
    @Expose
    private boolean isMain;

    /**
     * @return The created
     */
    public String getCreated() {
        return created;
    }

    /**
     * @param created The created
     */
    public void setCreated(String created) {
        this.created = created;
    }

    /**
     * @return The fullStorageLocation
     */
    public String getFullStorageLocation() {
        return fullStorageLocation;
    }

    /**
     * @param fullStorageLocation The fullStorageLocation
     */
    public void setFullStorageLocation(String fullStorageLocation) {
        this.fullStorageLocation = fullStorageLocation;
    }

    /**
     * @return The fullDownloadUrl
     */
    public String getFullDownloadUrl() {
        return fullDownloadUrl;
    }

    /**
     * @param fullDownloadUrl The fullDownloadUrl
     */
    public void setFullDownloadUrl(String fullDownloadUrl) {
        this.fullDownloadUrl = fullDownloadUrl;
    }

    /**
     * @return The thumbDownloadUrl
     */
    public String getThumbDownloadUrl() {
        return thumbDownloadUrl;
    }

    /**
     * @param thumbDownloadUrl The thumbDownloadUrl
     */
    public void setThumbDownloadUrl(String thumbDownloadUrl) {
        this.thumbDownloadUrl = thumbDownloadUrl;
    }

    /**
     * @return The thumbStorageLocation
     */
    public String getThumbStorageLocation() {
        return thumbStorageLocation;
    }

    /**
     * @param thumbStorageLocation The thumbStorageLocation
     */
    public void setThumbStorageLocation(String thumbStorageLocation) {
        this.thumbStorageLocation = thumbStorageLocation;
    }

    /**
     * @return The thumbRetinaStorageLocation
     */
    public String getThumbRetinaStorageLocation() {
        return thumbRetinaStorageLocation;
    }

    /**
     * @param thumbRetinaStorageLocation The thumbRetinaStorageLocation
     */
    public void setThumbRetinaStorageLocation(String thumbRetinaStorageLocation) {
        this.thumbRetinaStorageLocation = thumbRetinaStorageLocation;
    }

    /**
     * @return The thumbRetinaDownloadUrl
     */
    public String getThumbRetinaDownloadUrl() {
        return thumbRetinaDownloadUrl;
    }

    /**
     * @param thumbRetinaDownloadUrl The thumbRetinaDownloadUrl
     */
    public void setThumbRetinaDownloadUrl(String thumbRetinaDownloadUrl) {
        this.thumbRetinaDownloadUrl = thumbRetinaDownloadUrl;
    }

    /**
     * @return The id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the smallest image
     *
     * @return
     */
    public String getSmallestImage() {
        return (!TextUtils.isEmpty(this.thumbStorageLocation) && this.thumbStorageLocation.length() > 2)? this.thumbStorageLocation : this.getFullStorageLocation();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.created);
        dest.writeString(this.fullStorageLocation);
        dest.writeString(this.fullDownloadUrl);
        dest.writeString(this.thumbDownloadUrl);
        dest.writeString(this.thumbStorageLocation);
        dest.writeString(this.thumbRetinaStorageLocation);
        dest.writeString(this.thumbRetinaDownloadUrl);
        dest.writeByte((byte) (isMain ? 1 : 0));
        dest.writeInt(this.id);
    }

    public Image() {
    }

    protected Image(Parcel in) {
        this.created = in.readString();
        this.fullStorageLocation = in.readString();
        this.fullDownloadUrl = in.readString();
        this.thumbDownloadUrl = in.readString();
        this.thumbStorageLocation = in.readString();
        this.thumbRetinaStorageLocation = in.readString();
        this.thumbRetinaDownloadUrl = in.readString();
        this.isMain = in.readByte() != 0;
        this.id = in.readInt();
    }

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    @Override
    public String toString() {
        return "Image{" +
                "created='" + created + '\'' +
                ", fullStorageLocation='" + fullStorageLocation + '\'' +
                ", fullDownloadUrl='" + fullDownloadUrl + '\'' +
                ", thumbDownloadUrl='" + thumbDownloadUrl + '\'' +
                ", thumbStorageLocation='" + thumbStorageLocation + '\'' +
                ", thumbRetinaStorageLocation='" + thumbRetinaStorageLocation + '\'' +
                ", thumbRetinaDownloadUrl='" + thumbRetinaDownloadUrl + '\'' +
                ", isMain='" + isMain + '\'' +
                ", id=" + id +
                '}';
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }
}