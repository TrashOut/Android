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
import java.util.List;


public class User implements Parcelable {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("info")
    @Expose
    private String info;
    @SerializedName("birthdate")
    @Expose
    private Date birthdate;
    @SerializedName("created")
    @Expose
    private Date created;
    @SerializedName("active")
    @Expose
    private boolean active;
    @SerializedName("newsletter")
    @Expose
    private boolean newsletter;
    @SerializedName("image")
    @Expose
    private Image image;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("tokenFCM")
    @Expose
    private String tokenFCM;
    @SerializedName("facebookUrl")
    @Expose
    private String facebookUrl;
    @SerializedName("twitterUrl")
    @Expose
    private String twitterUrl;
    @SerializedName("googlePlusUrl")
    @Expose
    private String googlePlusUrl;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("points")
    @Expose
    private int points;
    @SerializedName("reviewed")
    @Expose
    private boolean reviewed;
    @SerializedName("userRoleId")
    @Expose
    private int userRoleId;
    @SerializedName("eventOrganizer")
    @Expose
    private boolean eventOrganizer;
    @SerializedName("volunteerCleanup")
    @Expose
    private boolean volunteerCleanup;
    @SerializedName("userRole")
    @Expose
    private UserRole userRole;
    @SerializedName("stats")
    @Expose
    private Stats stats;
    @SerializedName("organizations")
    @Expose
    private List<Organization> organizations = null;
    @SerializedName("badges")
    @Expose
    private List<Badge> badges = null;
    @SerializedName("areas")
    @Expose
    private List<Area> areas = null;


    /**
     * @return The id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return The firstName
     */
    public String getFirstName() {
        return (firstName != null ? firstName : "");
    }

    /**
     * @param firstName The firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return The lastName
     */
    public String getLastName() {
        return (lastName != null ? lastName : "");
    }

    /**
     * @param lastName The lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return The fullName
     */
    public String getFullName() {
        return (getFirstName() + " " + getLastName()).trim();
    }

    /**
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return The info
     */
    public String getInfo() {
        return info;
    }

    /**
     * @param info The info
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * @return The birthdate
     */
    public Date getBirthdate() {
        return birthdate;
    }

    /**
     * @param birthdate The birthdate
     */
    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    /**
     * @return The created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created The created
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * @return The active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active The active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return The newsletter
     */
    public boolean isNewsletter() {
        return newsletter;
    }

    /**
     * @param newsletter The newsletter
     */
    public void setNewsletter(boolean newsletter) {
        this.newsletter = newsletter;
    }

    /**
     * @return The image
     */
    public Image getImage() {
        return image;
    }

    /**
     * @param image The image
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * @return The uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param uid The uid
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return The tokenFCM
     */
    public String getTokenFCM() {
        return tokenFCM;
    }

    /**
     * @param tokenFCM The tokenFCM
     */
    public void setTokenFCM(String tokenFCM) {
        this.tokenFCM = tokenFCM;
    }

    /**
     * @return The facebookUrl
     */
    public String getFacebookUrl() {
        return facebookUrl;
    }

    /**
     * @param facebookUrl The facebookUrl
     */
    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    /**
     * @return The twitterUrl
     */
    public String getTwitterUrl() {
        return twitterUrl;
    }

    /**
     * @param twitterUrl The twitterUrl
     */
    public void setTwitterUrl(String twitterUrl) {
        this.twitterUrl = twitterUrl;
    }

    /**
     * @return The googlePlusUrl
     */
    public String getGooglePlusUrl() {
        return googlePlusUrl;
    }

    /**
     * @param googlePlusUrl The googlePlusUrl
     */
    public void setGooglePlusUrl(String googlePlusUrl) {
        this.googlePlusUrl = googlePlusUrl;
    }

    /**
     * @return The phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber The phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return The points
     */
    public int getPoints() {
        return points;
    }

    /**
     * @param points The points
     */
    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * @return The reviewed
     */
    public boolean isReviewed() {
        return reviewed;
    }

    /**
     * @param reviewed The reviewed
     */
    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
    }

    /**
     * @return The userRoleId
     */
    public int getUserRoleId() {
        return userRoleId;
    }

    /**
     * @param userRoleId The userRoleId
     */
    public void setUserRoleId(int userRoleId) {
        this.userRoleId = userRoleId;
    }

    /**
     * @return The event Organizer
     */
    public boolean isEventOrganizer() {
        return eventOrganizer;
    }

    /**
     * @param eventOrganizer The event organizer
     */
    public void setEventOrganizer(boolean eventOrganizer) {
        this.eventOrganizer = eventOrganizer;
    }

    /**
     * @return The volunteer clueanup
     */
    public boolean isVolunteerCleanup() {
        return volunteerCleanup;
    }

    /**
     * @param volunteerCleanup The volunteer cleanup
     */
    public void setVolunteerCleanup(boolean volunteerCleanup) {
        this.volunteerCleanup = volunteerCleanup;
    }

    /**
     * @return The User role
     */
    public UserRole getUserRole() {
        return userRole;
    }

    /**
     * @param userRole the User role
     */
    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    /**
     * @return the Stats
     */
    public Stats getStats() {
        return stats;
    }

    /**
     * @param stats The Stats
     */
    public void setStats(Stats stats) {
        this.stats = stats;
    }

    /**
     * @return The Organizations
     */
    public List<Organization> getOrganizations() {
        return organizations;
    }

    /**
     * @param organizations the Organizations
     */
    public void setOrganizations(List<Organization> organizations) {
        this.organizations = organizations;
    }

    /**
     * @return the Badges
     */
    public List<Badge> getBadges() {
        return badges;
    }

    /**
     * @param badges the Badges
     */
    public void setBadges(List<Badge> badges) {
        this.badges = badges;
    }

    /**
     * @return the Areas
     */
    public List<Area> getAreas() {
        return areas;
    }

    /**
     * @param areas the areas
     */
    public void setAreas(List<Area> areas) {
        this.areas = areas;
    }

    public User() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.email);
        dest.writeString(this.info);
        dest.writeLong(this.birthdate != null ? this.birthdate.getTime() : -1);
        dest.writeLong(this.created != null ? this.created.getTime() : -1);
        dest.writeByte(this.active ? (byte) 1 : (byte) 0);
        dest.writeByte(this.newsletter ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.image, flags);
        dest.writeString(this.uid);
        dest.writeString(this.tokenFCM);
        dest.writeString(this.facebookUrl);
        dest.writeString(this.twitterUrl);
        dest.writeString(this.googlePlusUrl);
        dest.writeString(this.phoneNumber);
        dest.writeInt(this.points);
        dest.writeByte(this.reviewed ? (byte) 1 : (byte) 0);
        dest.writeInt(this.userRoleId);
        dest.writeByte(this.eventOrganizer ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.userRole, flags);
        dest.writeParcelable(this.stats, flags);
        dest.writeTypedList(this.organizations);
        dest.writeTypedList(this.badges);
        dest.writeTypedList(this.areas);
    }

    protected User(Parcel in) {
        this.id = in.readLong();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.email = in.readString();
        this.info = in.readString();
        long tmpBirthdate = in.readLong();
        this.birthdate = tmpBirthdate == -1 ? null : new Date(tmpBirthdate);
        long tmpCreated = in.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
        this.active = in.readByte() != 0;
        this.newsletter = in.readByte() != 0;
        this.image = in.readParcelable(Image.class.getClassLoader());
        this.uid = in.readString();
        this.tokenFCM = in.readString();
        this.facebookUrl = in.readString();
        this.twitterUrl = in.readString();
        this.googlePlusUrl = in.readString();
        this.phoneNumber = in.readString();
        this.points = in.readInt();
        this.reviewed = in.readByte() != 0;
        this.userRoleId = in.readInt();
        this.eventOrganizer = in.readByte() != 0;
        this.userRole = in.readParcelable(UserRole.class.getClassLoader());
        this.stats = in.readParcelable(Stats.class.getClassLoader());
        this.organizations = in.createTypedArrayList(Organization.CREATOR);
        this.badges = in.createTypedArrayList(Badge.CREATOR);
        this.areas = in.createTypedArrayList(Area.CREATOR);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", info='" + info + '\'' +
                ", birthdate=" + birthdate +
                ", created=" + created +
                ", active=" + active +
                ", newsletter=" + newsletter +
                ", image=" + image +
                ", uid='" + uid + '\'' +
                ", tokenFCM='" + tokenFCM + '\'' +
                ", facebookUrl='" + facebookUrl + '\'' +
                ", twitterUrl='" + twitterUrl + '\'' +
                ", googlePlusUrl='" + googlePlusUrl + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", points=" + points +
                ", reviewed=" + reviewed +
                ", userRoleId=" + userRoleId +
                ", eventOrganizer=" + eventOrganizer +
                ", userRole=" + userRole +
                ", stats=" + stats +
                ", organizations=" + organizations +
                ", badges=" + badges +
                ", areas=" + areas +
                '}';
    }
}
