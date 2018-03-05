
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CollectionPoint implements Parcelable {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("activityId")
    @Expose
    private int activityId;
    @SerializedName("gps")
    @Expose
    private Gps gps;
    @SerializedName("size")
    @Expose
    private Constants.CollectionPointSize size;
    @SerializedName("types")
    @Expose
    private List<Constants.CollectionPointType> types = new ArrayList<>();
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("openingHours")
    @Expose
    private List<Map<String, List<OpeningHour>>> openingHours;
    @SerializedName("images")
    @Expose
    private List<Image> images = null;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("updateHistory")
    @Expose
    private List<UpdateHistory> updateHistory = new ArrayList<>();
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("updateNeeded")
    @Expose
    private boolean updateNeeded;
    @SerializedName("updateTime")
    @Expose
    private Date updateTime;

    /**
     * @return The id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return The activityId
     */
    public int getActivityId() {
        return activityId;
    }

    /**
     * @param activityId The activityId
     */
    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    /**
     * @return The gps
     */
    public Gps getGps() {
        return gps;
    }

    /**
     * @param gps The gps
     */
    public void setGps(Gps gps) {
        this.gps = gps;
    }

    /**
     * @return The size
     */
    public Constants.CollectionPointSize getSize() {
        return size;
    }

    /**
     * @param size The size
     */
    public void setSize(Constants.CollectionPointSize size) {
        this.size = size;
    }

    /**
     * @return The types
     */
    public List<Constants.CollectionPointType> getTypes() {
        return types;
    }

    /**
     * @param types The types
     */
    public void setTypes(List<Constants.CollectionPointType> types) {
        this.types = types;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The note
     */
    public String getNote() {
        return note;
    }

    /**
     * @param note The note
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * @return The phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone The phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

//    /**
//     * @return The openingHours
//     */
//    public String getOpeningHours() {
//        return openingHours;
//    }
//
//    /**
//     * @param openingHours The openingHours
//     */
//    public void setOpeningHours(String openingHours) {
//        this.openingHours = openingHours;
//    }

    /**
     * @return The images
     */
    public List<Image> getImages() {
        return images;
    }

    /**
     * @param images The images
     */
    public void setImages(List<Image> images) {
        this.images = images;
    }

    /**
     * @return The userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId The userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return The updateHistory
     */
    public List<UpdateHistory> getUpdateHistory() {
        return updateHistory;
    }

    /**
     * @param updateHistory The updateHistory
     */
    public void setUpdateHistory(List<UpdateHistory> updateHistory) {
        this.updateHistory = updateHistory;
    }

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return The updateNeeded
     */
    public boolean isUpdateNeeded() {
        return updateNeeded;
    }

    /**
     * @param updateNeeded The updateNeeded
     */
    public void setUpdateNeeded(boolean updateNeeded) {
        this.updateNeeded = updateNeeded;
    }

    /**
     * @return The updateTime
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime The updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<Map<String, List<OpeningHour>>> getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(List<Map<String, List<OpeningHour>>> openingHours) {
        this.openingHours = openingHours;
    }

    /**
     * @return The position
     */
    public LatLng getPosition() {
        if (getGps() == null)
            return null;
        return new LatLng(getGps().getLat(), getGps().getLng());
    }

    public CollectionPoint() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.activityId);
        dest.writeParcelable(this.gps, flags);
        dest.writeInt(this.size == null ? -1 : this.size.ordinal());
        dest.writeList(this.types);
        dest.writeString(this.name);
        dest.writeString(this.note);
        dest.writeString(this.phone);
        dest.writeString(this.email);
        dest.writeList(this.openingHours);
        dest.writeTypedList(this.images);
        dest.writeString(this.userId);
        dest.writeTypedList(this.updateHistory);
        dest.writeString(this.url);
        dest.writeByte(this.updateNeeded ? (byte) 1 : (byte) 0);
        dest.writeLong(this.updateTime != null ? this.updateTime.getTime() : -1);
    }

    protected CollectionPoint(Parcel in) {
        this.id = in.readLong();
        this.activityId = in.readInt();
        this.gps = in.readParcelable(Gps.class.getClassLoader());
        int tmpSize = in.readInt();
        this.size = tmpSize == -1 ? null : Constants.CollectionPointSize.values()[tmpSize];
        this.types = new ArrayList<Constants.CollectionPointType>();
        in.readList(this.types, Constants.CollectionPointType.class.getClassLoader());
        this.name = in.readString();
        this.note = in.readString();
        this.phone = in.readString();
        this.email = in.readString();
        this.openingHours = new ArrayList<>();
        //in.readList(this.openingHours, Map<String, List<OpeningHour>>.class.getClassLoader());
        this.images = in.createTypedArrayList(Image.CREATOR);
        this.userId = in.readString();
        this.updateHistory = in.createTypedArrayList(UpdateHistory.CREATOR);
        this.url = in.readString();
        this.updateNeeded = in.readByte() != 0;
        long tmpUpdateTime = in.readLong();
        this.updateTime = tmpUpdateTime == -1 ? null : new Date(tmpUpdateTime);
    }

    public static final Creator<CollectionPoint> CREATOR = new Creator<CollectionPoint>() {
        @Override
        public CollectionPoint createFromParcel(Parcel source) {
            return new CollectionPoint(source);
        }

        @Override
        public CollectionPoint[] newArray(int size) {
            return new CollectionPoint[size];
        }
    };

    @Override
    public String toString() {
        return "CollectionPoint{" +
                "id=" + id +
                ", activityId=" + activityId +
                ", gps=" + gps +
                ", size=" + size +
                ", types=" + types +
                ", name='" + name + '\'' +
                ", note='" + note + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
//                ", openingHours='" + openingHours + '\'' +
                ", images=" + images +
                ", userId='" + userId + '\'' +
                ", updateHistory=" + updateHistory +
                ", url='" + url + '\'' +
                ", updateNeeded=" + updateNeeded +
                ", updateTime=" + updateTime +
                '}';
    }
}
