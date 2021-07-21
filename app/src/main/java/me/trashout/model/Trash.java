
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

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.trashout.R;


public class Trash implements Parcelable, TrashMapItem {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("activityId")
    @Expose
    private int activityId;
    @SerializedName("images")
    @Expose
    private List<Image> images = new ArrayList<Image>();
    @SerializedName("gps")
    @Expose
    private Gps gps;
    @SerializedName("size")
    @Expose
    private Constants.TrashSize size;
    @SerializedName("types")
    @Expose
    private List<Constants.TrashType> types = new ArrayList<Constants.TrashType>();
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("userInfo")
    @Expose
    private UserInfo userInfo;
    @SerializedName("organization")
    @Expose
    private Organization organization;
    @SerializedName("organizationId")
    @Expose
    private int organizationId;
    @SerializedName("anonymous")
    @Expose
    private boolean anonymous;
    @SerializedName("accessibility")
    @Expose
    private Accessibility accessibility;
    @SerializedName("status")
    @Expose
    private Constants.TrashStatus status;
    @SerializedName("cleanedByMe")
    @Expose
    private boolean cleanedByMe;
    @SerializedName("created")
    @Expose
    private Date created;
    @SerializedName("updateTime")
    @Expose
    private Date updateTime;
    @SerializedName("updateHistory")
    @Expose
    private List<UpdateHistory> updateHistory = new ArrayList<UpdateHistory>();
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("events")
    @Expose
    private List<Event> events = new ArrayList<Event>();
    @SerializedName("updateNeeded")
    @Expose
    private int updateNeeded;
    @SerializedName("userId")
    @Expose
    private long userId;

    public static Trash createCleanedUpdateTrash(long trashId, Gps gps, Constants.TrashSize size, List<Constants.TrashType> types, Accessibility accessibility,  boolean cleanedByMe, String note, boolean anonymous, long userId, int organizationId) {
        Trash updatedTrash = new Trash();
        updatedTrash.setId(trashId);
        updatedTrash.setGps(gps);
        updatedTrash.setStatus(Constants.TrashStatus.CLEANED);
        updatedTrash.setSize(size);
        updatedTrash.setTypes(types);
        updatedTrash.setAccessibility(accessibility);
        updatedTrash.setCleanedByMe(cleanedByMe);
        updatedTrash.setNote(note);
        updatedTrash.setAnonymous(anonymous);
        updatedTrash.setUserId(userId);
        if (organizationId > 0) {
            updatedTrash.setOrganizationId(organizationId);
        }
        return updatedTrash;
    }

    public static Trash createStillHereUpdateTrash(long trashId, Gps gps, Constants.TrashStatus status, String note, Constants.TrashSize size, List<Constants.TrashType> types, Accessibility accessibility, boolean anonymous, long userId, int organizationId) {
        Trash updatedTrash = new Trash();
        updatedTrash.setId(trashId);
        updatedTrash.setGps(gps);
        updatedTrash.setStatus(status);
        updatedTrash.setNote(note);
        updatedTrash.setSize(size);
        updatedTrash.setTypes(types);
        updatedTrash.setAccessibility(accessibility);
        updatedTrash.setAnonymous(anonymous);
        updatedTrash.setUserId(userId);
        if (organizationId > 0) {
            updatedTrash.setOrganizationId(organizationId);
        }
        return updatedTrash;
    }

    public static Trash createUpdateTrash(long trashId, Gps gps, String note, Constants.TrashSize size, List<Constants.TrashType> types, Accessibility accessibility, boolean anonymous, long userId, int organizationId) {
        Trash updatedTrash = new Trash();
        updatedTrash.setId(trashId);
        updatedTrash.setGps(gps);
        updatedTrash.setNote(note);
        updatedTrash.setSize(size);
        updatedTrash.setTypes(types);
        updatedTrash.setAccessibility(accessibility);
        updatedTrash.setAnonymous(anonymous);
        updatedTrash.setUserId(userId);
        if (organizationId > 0) {
            updatedTrash.setOrganizationId(organizationId);
        }
        return updatedTrash;
    }

    public static Trash createNewTrash(Gps gps, String note, Constants.TrashSize size, List<Constants.TrashType> types, Accessibility accessibility, boolean anonymous, long userId, int organizationId) {
        Trash newTrash = new Trash();
        newTrash.setGps(gps);
        newTrash.setNote(note);
        newTrash.setSize(size);
        newTrash.setStatus(Constants.TrashStatus.STILL_HERE);
        newTrash.setTypes(types);
        newTrash.setAccessibility(accessibility);
        newTrash.setAnonymous(anonymous);
        newTrash.setUserId(userId);
        if (organizationId > 0) {
            newTrash.setOrganizationId(organizationId);
        }
        return newTrash;
    }

    public Trash() {
    }


    public long getId() {
        return id;
    }

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
    public Constants.TrashSize getSize() {
        return size;
    }

    /**
     * @param size The size
     */
    public void setSize(Constants.TrashSize size) {
        this.size = size;
    }

    /**
     * @return The types
     */
    public List<Constants.TrashType> getTypes() {
        return types;
    }

    /**
     * @param types The types
     */
    public void setTypes(List<Constants.TrashType> types) {
        this.types = types;
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
     * @return The userInfo
     */
    public UserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * @param userInfo The userInfo
     */
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * @return The organization
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * @param organization The organization
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }


    /**
     * @return The organization ID
     */
    public int getOrganizationId() {
        return organizationId;
    }

    /**
     * @param organizationId The organization ID
     */
    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * @return The anonymous
     */
    public boolean isAnonymous() {
        return anonymous;
    }

    /**
     * @param anonymous The anonymous
     */
    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    /**
     * @return The accessibility
     */
    public Accessibility getAccessibility() {
        return accessibility;
    }

    /**
     * @param accessibility The accessibility
     */
    public void setAccessibility(Accessibility accessibility) {
        this.accessibility = accessibility;
    }

    /**
     * @return The status
     */
    public Constants.TrashStatus getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(Constants.TrashStatus status) {
        this.status = status;
    }

    /**
     * @return The cleanedByMe
     */
    public boolean isCleanedByMe() {
        return cleanedByMe;
    }

    /**
     * @param cleanedByMe The cleanedByMe
     */
    public void setCleanedByMe(boolean cleanedByMe) {
        this.cleanedByMe = cleanedByMe;
    }

    /**
     * @return The created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created The created
     */
    public void setCreated(Date created) {
        this.created = created;
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


    /**
     * @return The last change date
     */
    public Date getLastChangeDate() {
        return this.updateTime != null ? this.updateTime : this.created;
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
     * @return The events
     */
    public List<Event> getEvents() {
        return events;
    }

    /**
     * @param events The events
     */
    public void setEvents(List<Event> events) {
        this.events = events;
    }

    /**
     * @return The updateNeeded
     */

    @Override
    public int getUpdateNeeded() {
        return updateNeeded;
    }

    /**
     * @param updateNeeded The updateNeeded
     */
    public void setUpdateNeeded(int updateNeeded) {
        this.updateNeeded = updateNeeded;
    }

    /**
     * @return Is update needed
     */
    public boolean isUpdateNeeded() {
        return updateNeeded == 1;
    }

    /**
     * @return The userId
     */
    public long getUserId() {
        return userId;
    }

    /**
     * @param userId The userId
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /**
     * Get Last change Name or Annonymous
     *
     * @return
     */
    public String getLastChangeName(Context context) {
        if (anonymous) {
            return context.getString(R.string.trash_anonymous);
        } else if (userInfo != null) {
            return userInfo.getFullName(context);
        } else {
            return context.getString(R.string.global_unknow);
        }
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(getGps().getLat(), getGps().getLng());
    }

    @Override
    public int getTotal() {
        return 1;
    }

    @Override
    public int getRemains() {
        return (Constants.TrashStatus.CLEANED.equals(status) ? 0 : 1);
    }

    @Override
    public int getCleaned() {
        return (Constants.TrashStatus.CLEANED.equals(status) ? 1 : 0);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.activityId);
        dest.writeTypedList(this.images);
        dest.writeParcelable(this.gps, flags);
        dest.writeInt(this.size == null ? -1 : this.size.ordinal());
        dest.writeList(this.types);
        dest.writeString(this.note);
        dest.writeParcelable(this.userInfo, flags);
        dest.writeByte(this.anonymous ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.accessibility, flags);
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
        dest.writeByte(this.cleanedByMe ? (byte) 1 : (byte) 0);
        dest.writeLong(this.created != null ? this.created.getTime() : -1);
        dest.writeLong(this.updateTime != null ? this.updateTime.getTime() : -1);
        dest.writeTypedList(this.updateHistory);
        dest.writeString(this.url);
        dest.writeTypedList(this.events);
        dest.writeInt(this.updateNeeded);
        dest.writeLong(this.userId);
    }

    protected Trash(Parcel in) {
        this.id = in.readLong();
        this.activityId = in.readInt();
        this.images = in.createTypedArrayList(Image.CREATOR);
        this.gps = in.readParcelable(Gps.class.getClassLoader());
        int tmpSize = in.readInt();
        this.size = tmpSize == -1 ? null : Constants.TrashSize.values()[tmpSize];
        this.types = new ArrayList<Constants.TrashType>();
        in.readList(this.types, Constants.TrashType.class.getClassLoader());
        this.note = in.readString();
        this.userInfo = in.readParcelable(UserInfo.class.getClassLoader());
        this.anonymous = in.readByte() != 0;
        this.accessibility = in.readParcelable(Accessibility.class.getClassLoader());
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : Constants.TrashStatus.values()[tmpStatus];
        this.cleanedByMe = in.readByte() != 0;
        long tmpCreated = in.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
        long tmpUpdateTime = in.readLong();
        this.updateTime = tmpUpdateTime == -1 ? null : new Date(tmpUpdateTime);
        this.updateHistory = in.createTypedArrayList(UpdateHistory.CREATOR);
        this.url = in.readString();
        this.events = in.createTypedArrayList(Event.CREATOR);
        this.updateNeeded = in.readInt();
        this.userId = in.readLong();
    }

    public static final Creator<Trash> CREATOR = new Creator<Trash>() {
        @Override
        public Trash createFromParcel(Parcel source) {
            return new Trash(source);
        }

        @Override
        public Trash[] newArray(int size) {
            return new Trash[size];
        }
    };

    @Override
    public String toString() {
        return "Trash{" +
                "id=" + id +
                ", activityId=" + activityId +
                ", images=" + images +
                ", gps=" + gps +
                ", size=" + size +
                ", types=" + types +
                ", note='" + note + '\'' +
                ", userInfo=" + userInfo +
                ", organization=" + organization +
                ", organizationId=" + organizationId +
                ", anonymous=" + anonymous +
                ", accessibility=" + accessibility +
                ", status=" + status +
                ", cleanedByMe=" + cleanedByMe +
                ", created=" + created +
                ", updateTime=" + updateTime +
                ", updateHistory=" + updateHistory +
                ", url='" + url + '\'' +
                ", events=" + events +
                ", updateNeeded=" + updateNeeded +
                ", userId=" + userId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trash trash = (Trash) o;

        return id == trash.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
