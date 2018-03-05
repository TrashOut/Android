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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Event implements Parcelable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("gps")
    @Expose
    private Gps gps;
    @SerializedName("geographicArea")
    @Expose
    private GeographicArea geographicArea;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("start")
    @Expose
    private Date start;
    @SerializedName("duration")
    @Expose
    private int duration;
    @SerializedName("bring")
    @Expose
    private String bring;
    @SerializedName("have")
    @Expose
    private String have;
    @SerializedName("childFriendly")
    @Expose
    private boolean childFriendly;
    @SerializedName("contact")
    @Expose
    private Contact contact;
    @SerializedName("trashPoints")
    @Expose
    private List<TrashPoint> trashPoints = new ArrayList<>();
    @SerializedName("collectionPointIds")
    @Expose
    private List<Integer> collectionPointIds = new ArrayList<>();
    @SerializedName("users")
    @Expose
    private List<User> users = new ArrayList<>();
    @SerializedName("userId")
    @Expose
    private long userId;
    @SerializedName("trashPointIds")
    @Expose
    private List<Long> trashPointIds = new ArrayList<>();

    public static Event createNewEvent(String name, Gps gps, String description, Date start, int duration, String bring, String have, Contact contact, List<Long> trashPointIds, long userId) {
        Event event = new Event();
        event.setName(name);
        event.setGps(gps);
        event.setDescription(description);
        event.setStart(start);
        if (duration > 0)
            event.setDuration(duration);
        event.setBring(bring);
        event.setHave(have);
        event.setContact(contact);
        event.setTrashPointIds(trashPointIds);
        event.setUserId(userId);
        return event;
    }

    public static Event updateNewEvent(String name, Gps gps, String description, String bring, String have, Contact contact, List<Long> trashPointIds, long userId) {
        Event event = new Event();
        event.setName(name);
        event.setGps(gps);
        event.setDescription(description);
        event.setBring(bring);
        event.setHave(have);
        event.setContact(contact);
        event.setTrashPointIds(trashPointIds);
        event.setUserId(userId);
        return event;
    }

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
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The gps
     */
    public Gps getGps() {
        return gps;
    }

    /**
     * @param gps The gps
     */
    public void setGps(Gps gps) {
        this.gps = gps;
    }

    /**
     * @return The geographicArea
     */
    public GeographicArea getGeographicArea() {
        return geographicArea;
    }

    /**
     * @param geographicArea The geographicArea
     */
    public void setGeographicArea(GeographicArea geographicArea) {
        this.geographicArea = geographicArea;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The start
     */
    public Date getStart() {
        return start;
    }

    /**
     * @param start The start
     */
    public void setStart(Date start) {
        this.start = start;
    }

    /**
     * @return The duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * @param duration The duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * @return The bring
     */
    public String getBring() {
        return bring;
    }

    /**
     * @param bring The bring
     */
    public void setBring(String bring) {
        this.bring = bring;
    }

    /**
     * @return The have
     */
    public String getHave() {
        return have;
    }

    /**
     * @param have The have
     */
    public void setHave(String have) {
        this.have = have;
    }

    /**
     * @return The childFriendly
     */
    public boolean isChildFriendly() {
        return childFriendly;
    }

    /**
     * @param childFriendly The childFriendly
     */
    public void setChildFriendly(boolean childFriendly) {
        this.childFriendly = childFriendly;
    }

    /**
     * @return The contact
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * @param contact The contact
     */
    public void setContact(Contact contact) {
        this.contact = contact;
    }

    /**
     * @return The trashPoints
     */
    public List<TrashPoint> getTrashPoints() {
        return trashPoints;
    }

    /**
     * @param trashPoints The trashPointIds
     */
    public void setTrashPoints(List<TrashPoint> trashPoints) {
        this.trashPoints = trashPoints;
    }

    /**
     * @return The collectionPointIds
     */
    public List<Integer> getCollectionPointIds() {
        return collectionPointIds;
    }

    /**
     * @param collectionPointIds The collectionPointIds
     */
    public void setCollectionPointIds(List<Integer> collectionPointIds) {
        this.collectionPointIds = collectionPointIds;
    }

    /**
     * @return The userId
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * @param users The userId
     */
    public void setUsers(List<User> users) {
        this.users = users;
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
     * @return The trashPointIds
     */
    public List<Long> getTrashPointIds() {
        return trashPointIds;
    }

    /**
     * @param trashPointIds The collectionPointIds
     */
    public void setTrashPointIds(List<Long> trashPointIds) {
        this.trashPointIds = trashPointIds;
    }

    public Event() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeParcelable(this.gps, flags);
        dest.writeParcelable(this.geographicArea, flags);
        dest.writeString(this.description);
        dest.writeLong(this.start != null ? this.start.getTime() : -1);
        dest.writeInt(this.duration);
        dest.writeString(this.bring);
        dest.writeString(this.have);
        dest.writeByte(this.childFriendly ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.contact, flags);
        dest.writeTypedList(this.trashPoints);
        dest.writeList(this.collectionPointIds);
        dest.writeTypedList(this.users);
        dest.writeLong(this.userId);
        dest.writeList(this.trashPointIds);
    }

    protected Event(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.gps = in.readParcelable(Gps.class.getClassLoader());
        this.geographicArea = in.readParcelable(GeographicArea.class.getClassLoader());
        this.description = in.readString();
        long tmpStart = in.readLong();
        this.start = tmpStart == -1 ? null : new Date(tmpStart);
        this.duration = in.readInt();
        this.bring = in.readString();
        this.have = in.readString();
        this.childFriendly = in.readByte() != 0;
        this.contact = in.readParcelable(Contact.class.getClassLoader());
        this.trashPoints = in.createTypedArrayList(TrashPoint.CREATOR);
        this.collectionPointIds = new ArrayList<Integer>();
        in.readList(this.collectionPointIds, Integer.class.getClassLoader());
        this.users = in.createTypedArrayList(User.CREATOR);
        this.userId = in.readLong();
        this.trashPointIds = new ArrayList<Long>();
        in.readList(this.trashPointIds, Long.class.getClassLoader());
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gps=" + gps +
                ", geographicArea=" + geographicArea +
                ", description='" + description + '\'' +
                ", start=" + start +
                ", duration=" + duration +
                ", bring='" + bring + '\'' +
                ", have='" + have + '\'' +
                ", childFriendly=" + childFriendly +
                ", contact=" + contact +
                ", trashPoints=" + trashPoints +
                ", collectionPointIds=" + collectionPointIds +
                ", users=" + users +
                ", userId=" + userId +
                ", trashPointIds=" + trashPointIds +
                '}';
    }
}