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
 * @since 13.02.2017
 */
public class NewsVideo implements Parcelable {

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("prContentId")
    @Expose
    private int prContentId;
    @SerializedName("pRContentId")
    @Expose
    private int pRContentId;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrContentId() {
        return prContentId;
    }

    public void setPrContentId(int prContentId) {
        this.prContentId = prContentId;
    }

    public int getPRContentId() {
        return pRContentId;
    }

    public void setPRContentId(int pRContentId) {
        this.pRContentId = pRContentId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.created);
        dest.writeInt(this.id);
        dest.writeInt(this.prContentId);
        dest.writeInt(this.pRContentId);
    }

    public NewsVideo() {
    }

    protected NewsVideo(Parcel in) {
        this.url = in.readString();
        this.created = in.readString();
        this.id = in.readInt();
        this.prContentId = in.readInt();
        this.pRContentId = in.readInt();
    }

    public static final Parcelable.Creator<NewsVideo> CREATOR = new Parcelable.Creator<NewsVideo>() {
        @Override
        public NewsVideo createFromParcel(Parcel source) {
            return new NewsVideo(source);
        }

        @Override
        public NewsVideo[] newArray(int size) {
            return new NewsVideo[size];
        }
    };

    @Override
    public String toString() {
        return "NewsVideo{" +
                "url='" + url + '\'' +
                ", created='" + created + '\'' +
                ", id=" + id +
                ", prContentId=" + prContentId +
                ", pRContentId=" + pRContentId +
                '}';
    }
}
