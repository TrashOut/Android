
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

import java.util.ArrayList;
import java.util.List;

public class Changed implements Parcelable {

    @SerializedName("types")
    @Expose
    private List<Constants.TrashType> types = new ArrayList<Constants.TrashType>();
    @SerializedName("images")
    @Expose
    private List<Image> images = new ArrayList<Image>();
    @SerializedName("status")
    @Expose
    private Constants.TrashStatus status;
    @SerializedName("note")
    @Expose
    private String note;

    public static Changed createChangedFromTrash(Trash trash) {
        Changed changed = new Changed();
        changed.setImages(trash.getImages());
        changed.setNote(trash.getNote());
        changed.setStatus(trash.getStatus());
        return changed;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.types);
        dest.writeTypedList(this.images);
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
        dest.writeString(this.note);
    }

    public Changed() {
    }

    protected Changed(Parcel in) {
        this.types = new ArrayList<Constants.TrashType>();
        in.readList(this.types, Constants.TrashType.class.getClassLoader());
        this.images = in.createTypedArrayList(Image.CREATOR);
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : Constants.TrashStatus.values()[tmpStatus];
        this.note = in.readString();
    }

    public static final Creator<Changed> CREATOR = new Creator<Changed>() {
        @Override
        public Changed createFromParcel(Parcel source) {
            return new Changed(source);
        }

        @Override
        public Changed[] newArray(int size) {
            return new Changed[size];
        }
    };

    @Override
    public String toString() {
        return "Changed{" +
                "types=" + types +
                ", images=" + images +
                ", status=" + status +
                ", note='" + note + '\'' +
                '}';
    }
}
