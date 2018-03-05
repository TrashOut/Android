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
public class NewsImage implements Parcelable {

    @SerializedName("main")
    @Expose
    private boolean main;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("prContentId")
    @Expose
    private int prContentId;
    @SerializedName("imageId")
    @Expose
    private int imageId;
    @SerializedName("image")
    @Expose
    private Image image;

    public boolean isMain() {
        return main;
    }

    public void setMain(boolean main) {
        this.main = main;
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

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.main ? (byte) 1 : (byte) 0);
        dest.writeInt(this.id);
        dest.writeInt(this.prContentId);
        dest.writeInt(this.imageId);
        dest.writeParcelable(this.image, flags);
    }

    public NewsImage() {
    }

    protected NewsImage(Parcel in) {
        this.main = in.readByte() != 0;
        this.id = in.readInt();
        this.prContentId = in.readInt();
        this.imageId = in.readInt();
        this.image = in.readParcelable(Image.class.getClassLoader());
    }

    public static final Parcelable.Creator<NewsImage> CREATOR = new Parcelable.Creator<NewsImage>() {
        @Override
        public NewsImage createFromParcel(Parcel source) {
            return new NewsImage(source);
        }

        @Override
        public NewsImage[] newArray(int size) {
            return new NewsImage[size];
        }
    };

    @Override
    public String toString() {
        return "NewsImage{" +
                "main=" + main +
                ", id=" + id +
                ", prContentId=" + prContentId +
                ", imageId=" + imageId +
                ", image=" + image +
                '}';
    }
}

