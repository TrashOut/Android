
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

import java.util.Comparator;
import java.util.Date;

public class UpdateHistory implements Parcelable {

    @SerializedName("userId")
    @Expose
    private int userId;
    @SerializedName("activityId")
    @Expose
    private int activityId;
    @SerializedName("userInfo")
    @Expose
    private UserInfo userInfo;
    @SerializedName("anonymous")
    @Expose
    private Boolean anonymous;
    @SerializedName("updateTime")
    @Expose
    private Date updateTime;
    @SerializedName("changed")
    @Expose
    private Changed changed;

    public static UpdateHistory createLastUpdateHistoryFromTrash(Trash trash) {
        UpdateHistory updateHistory = new UpdateHistory();
        updateHistory.setChanged(Changed.createChangedFromTrash(trash));
        updateHistory.setUpdateTime(trash.getLastChangeDate());
        updateHistory.setUserInfo(trash.getUserInfo());
        updateHistory.setAnonymous(trash.isAnonymous());
        return updateHistory;
    }

    /**
     * @return The userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId The userId
     */
    public void setUserId(int userId) {
        this.userId = userId;
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
     * @return The anonymous
     */
    public Boolean isAnonymous() {
        return anonymous;
    }

    /**
     * @param anonymous The anonymous
     */
    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
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
     * @return The changed
     */
    public Changed getChanged() {
        return changed;
    }

    /**
     * @param changed The changed
     */
    public void setChanged(Changed changed) {
        this.changed = changed;
    }

    /**
     * @return Check if update contains images
     */
    public boolean isContainImages() {
        return changed != null && changed.getImages() != null && !changed.getImages().isEmpty();
    }

    public UpdateHistory() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.userId);
        dest.writeInt(this.activityId);
        dest.writeParcelable(this.userInfo, flags);
        dest.writeValue(this.anonymous);
        dest.writeLong(this.updateTime != null ? this.updateTime.getTime() : -1);
        dest.writeParcelable(this.changed, flags);
    }

    protected UpdateHistory(Parcel in) {
        this.userId = in.readInt();
        this.activityId = in.readInt();
        this.userInfo = in.readParcelable(UserInfo.class.getClassLoader());
        this.anonymous = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        long tmpUpdateTime = in.readLong();
        this.updateTime = tmpUpdateTime == -1 ? null : new Date(tmpUpdateTime);
        this.changed = in.readParcelable(Changed.class.getClassLoader());
    }

    public static final Creator<UpdateHistory> CREATOR = new Creator<UpdateHistory>() {
        @Override
        public UpdateHistory createFromParcel(Parcel source) {
            return new UpdateHistory(source);
        }

        @Override
        public UpdateHistory[] newArray(int size) {
            return new UpdateHistory[size];
        }
    };

    public static class Comparators {
        public static Comparator<UpdateHistory> SORT_BY_LAST_UPDATE_ASC = new Comparator<UpdateHistory>() {
            @Override
            public int compare(UpdateHistory updateHistory1, UpdateHistory updateHistory2) {

                if (updateHistory2 == null || updateHistory2.getUpdateTime() == null)
                    return 1;

                if (updateHistory1 == null || updateHistory1.getUpdateTime() == null)
                    return -1;

                int i = updateHistory1.getUpdateTime().getTime() == updateHistory2.getUpdateTime().getTime() ? 0 : updateHistory1.getUpdateTime().getTime() < updateHistory2.getUpdateTime().getTime() ? 1 : -1;

                if (i == 0) {
                    i = Math.random() < 0.5 ? 1 : -1;
                }
                return i;
            }
        };

        public static Comparator<UpdateHistory> SORT_BY_LAST_UPDATE_DESC = new Comparator<UpdateHistory>() {
            @Override
            public int compare(UpdateHistory updateHistory1, UpdateHistory updateHistory2) {

                if (updateHistory2 == null || updateHistory2.getUpdateTime() == null)
                    return -1;

                if (updateHistory1 == null || updateHistory1.getUpdateTime() == null)
                    return 1;

                int i = updateHistory1.getUpdateTime().getTime() == updateHistory2.getUpdateTime().getTime() ? 0 : updateHistory1.getUpdateTime().getTime() > updateHistory2.getUpdateTime().getTime() ? 1 : -1;

                if (i == 0) {
                    i = Math.random() < 0.5 ? 1 : -1;
                }
                return i;
            }
        };
    }

    @Override
    public String toString() {
        return "UpdateHistory{" +
                "userId=" + userId +
                ", activityId=" + activityId +
                ", userInfo=" + userInfo +
                ", anonymous=" + anonymous +
                ", updateTime=" + updateTime +
                ", changed=" + changed +
                '}';
    }
}
