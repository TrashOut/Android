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

import java.util.Date;

/**
 * @author Miroslav Cupalka
 * @package me.trashout.model
 * @since 03.02.2017
 */
public class UserActivity implements Parcelable {

    @SerializedName("type")
    @Expose
    private Constants.UserActivityType type;
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("created")
    @Expose
    private Date created;
    @SerializedName("userInfo")
    @Expose
    private UserInfo userInfo;
    @SerializedName("gps")
    @Expose
    private Gps gps;
    @SerializedName("activity")
    @Expose
    private Activity activity;

    public Constants.UserActivityType getType() {
        return type;
    }

    public void setType(Constants.UserActivityType type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Gps getGps() {
        return gps;
    }

    public void setGps(Gps gps) {
        this.gps = gps;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * @return The position
     */
    public LatLng getPosition() {
        if (getGps() == null)
            return null;
        return new LatLng(getGps().getLat(), getGps().getLng());
    }

    public UserActivity() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeString(this.action);
        dest.writeString(this.id);
        dest.writeLong(this.created != null ? this.created.getTime() : -1);
        dest.writeParcelable(this.userInfo, flags);
        dest.writeParcelable(this.gps, flags);
        dest.writeParcelable(this.activity, flags);
    }

    protected UserActivity(Parcel in) {
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : Constants.UserActivityType.values()[tmpType];
        this.action = in.readString();
        this.id = in.readString();
        long tmpCreated = in.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
        this.userInfo = in.readParcelable(UserInfo.class.getClassLoader());
        this.gps = in.readParcelable(Gps.class.getClassLoader());
        this.activity = in.readParcelable(Activity.class.getClassLoader());
    }

    public static final Creator<UserActivity> CREATOR = new Creator<UserActivity>() {
        @Override
        public UserActivity createFromParcel(Parcel source) {
            return new UserActivity(source);
        }

        @Override
        public UserActivity[] newArray(int size) {
            return new UserActivity[size];
        }
    };

    @Override
    public String toString() {
        return "UserActivity{" +
                "type=" + type +
                ", action='" + action + '\'' +
                ", id='" + id + '\'' +
                ", created=" + created +
                ", userInfo=" + userInfo +
                ", gps=" + gps +
                ", activity=" + activity +
                '}';
    }
}
