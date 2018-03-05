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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Miroslav Cupalka
 * @package me.trashout.model
 * @since 17.02.2017
 */
public class Activity implements Parcelable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("images")
    @Expose
    private List<Image> images = null;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("status")
    @Expose
    private Constants.TrashStatus status;
    @SerializedName("cleanedByMe")
    @Expose
    private boolean cleanedByMe;
    @SerializedName("anonymous")
    @Expose
    private boolean anonymous;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Constants.TrashStatus getStatus() {
        return status;
    }

    public void setStatus(Constants.TrashStatus status) {
        this.status = status;
    }

    public boolean isCleanedByMe() {
        return cleanedByMe;
    }

    public void setCleanedByMe(boolean cleanedByMe) {
        this.cleanedByMe = cleanedByMe;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }


    public Activity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeTypedList(this.images);
        dest.writeString(this.note);
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
        dest.writeByte(this.cleanedByMe ? (byte) 1 : (byte) 0);
        dest.writeByte(this.anonymous ? (byte) 1 : (byte) 0);
    }

    protected Activity(Parcel in) {
        this.id = in.readInt();
        this.images = in.createTypedArrayList(Image.CREATOR);
        this.note = in.readString();
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : Constants.TrashStatus.values()[tmpStatus];
        this.cleanedByMe = in.readByte() != 0;
        this.anonymous = in.readByte() != 0;
    }

    public static final Creator<Activity> CREATOR = new Creator<Activity>() {
        @Override
        public Activity createFromParcel(Parcel source) {
            return new Activity(source);
        }

        @Override
        public Activity[] newArray(int size) {
            return new Activity[size];
        }
    };

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", images=" + images +
                ", note='" + note + '\'' +
                ", status=" + status +
                ", cleanedByMe=" + cleanedByMe +
                ", anonymous=" + anonymous +
                '}';
    }
}