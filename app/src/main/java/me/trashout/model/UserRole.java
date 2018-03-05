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

/**
 * @author Miroslav Cupalka
 * @package me.trashout.model
 * @since 04.02.2017
 */
public class UserRole implements Parcelable {

    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("id")
    @Expose
    private int id;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeString(this.description);
        dest.writeInt(this.id);
    }

    public UserRole() {
    }

    protected UserRole(Parcel in) {
        this.code = in.readString();
        this.description = in.readString();
        this.id = in.readInt();
    }

    public static final Parcelable.Creator<UserRole> CREATOR = new Parcelable.Creator<UserRole>() {
        @Override
        public UserRole createFromParcel(Parcel source) {
            return new UserRole(source);
        }

        @Override
        public UserRole[] newArray(int size) {
            return new UserRole[size];
        }
    };

    @Override
    public String toString() {
        return "UserRole{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                '}';
    }
}
