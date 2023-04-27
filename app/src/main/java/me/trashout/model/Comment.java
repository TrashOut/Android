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

import java.util.Date;

public class Comment implements Parcelable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("created")
    @Expose
    private Date created;
    @SerializedName("userId")
    @Expose
    private long userId;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("organizationId")
    @Expose
    private long organizationId;
    @SerializedName("organization")
    @Expose
    private Organization organization;

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
     * @return The body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body The body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @return The created date
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created The created date
     */
    public void setCreated(Date created) {
        this.created = created;
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
     * @return The user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user The user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return The organization ID
     */
    public long getOrganizationId() {
        return organizationId;
    }

    /**
     * @param organizationId The organization ID
     */
    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
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


    public Comment() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.body);
        dest.writeLong(this.created != null ? this.created.getTime() : -1);
        dest.writeLong(this.userId);
        dest.writeParcelable(this.user, flags);
        dest.writeLong(this.organizationId);
        dest.writeParcelable(this.organization, flags);
    }

    protected Comment(Parcel in) {
        this.id = in.readInt();
        this.body = in.readString();
        long tmpCreated = in.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
        this.userId = in.readLong();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.organizationId = in.readLong();
        this.organization = in.readParcelable(Organization.class.getClassLoader());
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", body='" + body + '\'' +
                ", created=" + created +
                ", userId=" + userId +
                ", user=" + user +
                ", organizationId=" + organizationId +
                ", organization=" + organization +
                '}';
    }
}