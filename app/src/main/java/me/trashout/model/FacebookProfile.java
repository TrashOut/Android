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

import com.facebook.AccessToken;

public class FacebookProfile implements Parcelable {

    private String id;
    private String first_name;
    private String last_name;
    private String email;
    private PictureWrapper picture;

    private AccessToken accessToken;

    public String getId() {
        return id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPictureUrl() {
        return picture.data.url;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    class PictureWrapper implements Parcelable {
        PictureDataWrapper data;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.data, flags);
        }

        public PictureWrapper() {
        }

        private PictureWrapper(Parcel in) {
            this.data = in.readParcelable(PictureDataWrapper.class.getClassLoader());
        }

        public  final Creator<PictureWrapper> CREATOR = new Creator<PictureWrapper>() {
            public PictureWrapper createFromParcel(Parcel source) {
                return new PictureWrapper(source);
            }

            public PictureWrapper[] newArray(int size) {
                return new PictureWrapper[size];
            }
        };
    }

    class PictureDataWrapper implements Parcelable {
        String url;
        boolean is_silhouette;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.url);
            dest.writeByte(is_silhouette ? (byte) 1 : (byte) 0);
        }

        public PictureDataWrapper() {
        }

        private PictureDataWrapper(Parcel in) {
            this.url = in.readString();
            this.is_silhouette = in.readByte() != 0;
        }

        public  final Creator<PictureDataWrapper> CREATOR = new Creator<PictureDataWrapper>() {
            public PictureDataWrapper createFromParcel(Parcel source) {
                return new PictureDataWrapper(source);
            }

            public PictureDataWrapper[] newArray(int size) {
                return new PictureDataWrapper[size];
            }
        };
    }


    @Override
    public String toString() {
        return "FacebookProfile{" +
                "id='" + id + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                ", picture=" + picture.data.url +
                ", accessToken=" + accessToken +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.first_name);
        dest.writeString(this.last_name);
        dest.writeString(this.email);
        dest.writeParcelable(this.picture, flags);
        dest.writeParcelable(this.accessToken, 0);
    }

    public FacebookProfile() {
    }

    private FacebookProfile(Parcel in) {
        this.id = in.readString();
        this.first_name = in.readString();
        this.last_name = in.readString();
        this.email = in.readString();
        this.picture = in.readParcelable(PictureWrapper.class.getClassLoader());
        this.accessToken = in.readParcelable(AccessToken.class.getClassLoader());
    }

    public static final Parcelable.Creator<FacebookProfile> CREATOR = new Parcelable.Creator<FacebookProfile>() {
        public FacebookProfile createFromParcel(Parcel source) {
            return new FacebookProfile(source);
        }

        public FacebookProfile[] newArray(int size) {
            return new FacebookProfile[size];
        }
    };
}
