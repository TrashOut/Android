
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

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import me.trashout.R;

public class Accessibility implements Parcelable {

    @SerializedName("byCar")
    @Expose
    private boolean byCar;
    @SerializedName("inCave")
    @Expose
    private boolean inCave;
    @SerializedName("underWater")
    @Expose
    private boolean underWater;
    @SerializedName("notForGeneralCleanup")
    @Expose
    private boolean notForGeneralCleanup;

    /**
     * 
     * @return
     *     The byCar
     */
    public boolean isByCar() {
        return byCar;
    }

    /**
     * 
     * @param byCar
     *     The byCar
     */
    public void setByCar(boolean byCar) {
        this.byCar = byCar;
    }

    /**
     * 
     * @return
     *     The inCave
     */
    public boolean isInCave() {
        return inCave;
    }

    /**
     * 
     * @param inCave
     *     The inCave
     */
    public void setInCave(boolean inCave) {
        this.inCave = inCave;
    }

    /**
     * 
     * @return
     *     The underWater
     */
    public boolean isUnderWater() {
        return underWater;
    }

    /**
     * 
     * @param underWater
     *     The underWater
     */
    public void setUnderWater(boolean underWater) {
        this.underWater = underWater;
    }

    /**
     * 
     * @return
     *     The notForGeneralCleanup
     */
    public boolean isNotForGeneralCleanup() {
        return notForGeneralCleanup;
    }

    /**
     * 
     * @param notForGeneralCleanup
     *     The notForGeneralCleanup
     */
    public void setNotForGeneralCleanup(boolean notForGeneralCleanup) {
        this.notForGeneralCleanup = notForGeneralCleanup;
    }


    public String getAccessibilityString(Context context){
        ArrayList <String> accessibilityStringList = new ArrayList<>();

        if (byCar){
            accessibilityStringList.add(context.getString(R.string.trash_accessibility_byCar));
        }
        if (inCave){
            accessibilityStringList.add(context.getString(R.string.trash_accessibility_inCave));
        }
        if (underWater){
            accessibilityStringList.add(context.getString(R.string.trash_accessibility_underWater));
        }
        if (notForGeneralCleanup){
            accessibilityStringList.add(context.getString(R.string.trash_accessibility_notForGeneralCleanup));
        }
        return TextUtils.join(",",accessibilityStringList);
    }

    public boolean isSomeAccessibilityValue(){
        return byCar || inCave || underWater || notForGeneralCleanup;
    }

    public Accessibility() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.byCar ? (byte) 1 : (byte) 0);
        dest.writeByte(this.inCave ? (byte) 1 : (byte) 0);
        dest.writeByte(this.underWater ? (byte) 1 : (byte) 0);
        dest.writeByte(this.notForGeneralCleanup ? (byte) 1 : (byte) 0);
    }

    protected Accessibility(Parcel in) {
        this.byCar = in.readByte() != 0;
        this.inCave = in.readByte() != 0;
        this.underWater = in.readByte() != 0;
        this.notForGeneralCleanup = in.readByte() != 0;
    }

    public static final Creator<Accessibility> CREATOR = new Creator<Accessibility>() {
        @Override
        public Accessibility createFromParcel(Parcel source) {
            return new Accessibility(source);
        }

        @Override
        public Accessibility[] newArray(int size) {
            return new Accessibility[size];
        }
    };

    @Override
    public String toString() {
        return "Accessibility{" +
                "byCar=" + byCar +
                ", inCave=" + inCave +
                ", underWater=" + underWater +
                ", notForGeneralCleanup=" + notForGeneralCleanup +
                '}';
    }
}
