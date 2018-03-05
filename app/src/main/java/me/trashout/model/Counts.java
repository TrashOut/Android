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
 * @since 23.12.2016
 */
public class Counts implements Parcelable {

    @SerializedName("stillHere")
    @Expose
    private int stillHere;
    @SerializedName("cleaned")
    @Expose
    private int cleaned;
    @SerializedName("less")
    @Expose
    private int less;
    @SerializedName("more")
    @Expose
    private int more;
    @SerializedName("updateNeeded")
    @Expose
    private int updateNeeded;

    public int getStillHere() {
        return stillHere;
    }

    public void setStillHere(int stillHere) {
        this.stillHere = stillHere;
    }

    public int getCleaned() {
        return cleaned;
    }

    public void setCleaned(int cleaned) {
        this.cleaned = cleaned;
    }

    public int getLess() {
        return less;
    }

    public void setLess(int less) {
        this.less = less;
    }

    public int getMore() {
        return more;
    }

    public void setMore(int more) {
        this.more = more;
    }

    public int getUpdateNeeded() {
        return updateNeeded;
    }

    public void setUpdateNeeded(int updateNeeded) {
        this.updateNeeded = updateNeeded;
    }

    public Counts() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.stillHere);
        dest.writeInt(this.cleaned);
        dest.writeInt(this.less);
        dest.writeInt(this.more);
        dest.writeInt(this.updateNeeded);
    }

    protected Counts(Parcel in) {
        this.stillHere = in.readInt();
        this.cleaned = in.readInt();
        this.less = in.readInt();
        this.more = in.readInt();
        this.updateNeeded = in.readInt();
    }

    public static final Creator<Counts> CREATOR = new Creator<Counts>() {
        @Override
        public Counts createFromParcel(Parcel source) {
            return new Counts(source);
        }

        @Override
        public Counts[] newArray(int size) {
            return new Counts[size];
        }
    };

    @Override
    public String toString() {
        return "Counts{" +
                "stillHere=" + stillHere +
                ", cleaned=" + cleaned +
                ", less=" + less +
                ", more=" + more +
                ", updateNeeded=" + updateNeeded +
                '}';
    }
}