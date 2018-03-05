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
 * @since 04.02.2017
 */
public class Organization implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("organizationTypeId")
    @Expose
    private int organizationTypeId;
    @SerializedName("gpsId")
    @Expose
    private int gpsId;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("mailSubject")
    @Expose
    private String mailSubject;
    @SerializedName("mailBody")
    @Expose
    private String mailBody;
    @SerializedName("contactEmail")
    @Expose
    private String contactEmail;
    @SerializedName("contactPhone")
    @Expose
    private String contactPhone;
    @SerializedName("contactTwitter")
    @Expose
    private String contactTwitter;
    @SerializedName("contactFacebook")
    @Expose
    private String contactFacebook;
    @SerializedName("contactUrl")
    @Expose
    private String contactUrl;
    @SerializedName("imageId")
    @Expose
    private int imageId;
    @SerializedName("parentId")
    @Expose
    private int parentId;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("organizationId")
    @Expose
    private Integer organizationId;
    @SerializedName("organizationRoleId")
    @Expose
    private Integer organizationRoleId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOrganizationTypeId() {
        return organizationTypeId;
    }

    public void setOrganizationTypeId(int organizationTypeId) {
        this.organizationTypeId = organizationTypeId;
    }

    public int getGpsId() {
        return gpsId;
    }

    public void setGpsId(int gpsId) {
        this.gpsId = gpsId;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getMailBody() {
        return mailBody;
    }

    public void setMailBody(String mailBody) {
        this.mailBody = mailBody;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactTwitter() {
        return contactTwitter;
    }

    public void setContactTwitter(String contactTwitter) {
        this.contactTwitter = contactTwitter;
    }

    public String getContactFacebook() {
        return contactFacebook;
    }

    public void setContactFacebook(String contactFacebook) {
        this.contactFacebook = contactFacebook;
    }

    public String getContactUrl() {
        return contactUrl;
    }

    public void setContactUrl(String contactUrl) {
        this.contactUrl = contactUrl;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getOrganizationRoleId() {
        return organizationRoleId;
    }

    public void setOrganizationRoleId(Integer organizationRoleId) {
        this.organizationRoleId = organizationRoleId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeInt(this.organizationTypeId);
        dest.writeInt(this.gpsId);
        dest.writeString(this.created);
        dest.writeString(this.mailSubject);
        dest.writeString(this.mailBody);
        dest.writeString(this.contactEmail);
        dest.writeString(this.contactPhone);
        dest.writeString(this.contactTwitter);
        dest.writeString(this.contactFacebook);
        dest.writeString(this.contactUrl);
        dest.writeInt(this.imageId);
        dest.writeInt(this.parentId);
        dest.writeInt(this.id);
        dest.writeInt(this.organizationId);
        dest.writeInt(this.organizationRoleId);
    }

    public Organization() {
    }

    protected Organization(Parcel in) {
        this.name = in.readString();
        this.description = in.readString();
        this.organizationTypeId = in.readInt();
        this.gpsId = in.readInt();
        this.created = in.readString();
        this.mailSubject = in.readString();
        this.mailBody = in.readString();
        this.contactEmail = in.readString();
        this.contactPhone = in.readString();
        this.contactTwitter = in.readString();
        this.contactFacebook = in.readString();
        this.contactUrl = in.readString();
        this.imageId = in.readInt();
        this.parentId = in.readInt();
        this.id = in.readInt();
        this.organizationId = in.readInt();
        this.organizationRoleId = in.readInt();
    }

    public static final Parcelable.Creator<Organization> CREATOR = new Parcelable.Creator<Organization>() {
        @Override
        public Organization createFromParcel(Parcel source) {
            return new Organization(source);
        }

        @Override
        public Organization[] newArray(int size) {
            return new Organization[size];
        }
    };

    @Override
    public String toString() {
        return "Organization{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", organizationRoleId='" + organizationRoleId + '\'' +
                ", description='" + description + '\'' +
                ", organizationTypeId=" + organizationTypeId +
                ", gpsId=" + gpsId +
                ", created='" + created + '\'' +
                ", mailSubject='" + mailSubject + '\'' +
                ", mailBody='" + mailBody + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", contactTwitter='" + contactTwitter + '\'' +
                ", contactFacebook='" + contactFacebook + '\'' +
                ", contactUrl='" + contactUrl + '\'' +
                ", imageId=" + imageId +
                ", parentId=" + parentId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Organization that = (Organization) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
