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

/**
 * @author Miroslav Cupalka
 * @package me.trashout.model
 * @since 13.02.2017
 */
public class NewsDetail implements Parcelable {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("bodyMarkdown")
    @Expose
    private String bodyMarkdown;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("tags")
    @Expose
    private String tags;
    @SerializedName("userId")
    @Expose
    private int userId;
    @SerializedName("appIosUrl")
    @Expose
    private String appIosUrl;
    @SerializedName("appAndroidUrl")
    @Expose
    private String appAndroidUrl;
    @SerializedName("appWindowsUrl")
    @Expose
    private String appWindowsUrl;
    @SerializedName("created")
    @Expose
    private Date created;
    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("areaId")
    @Expose
    private int areaId;
    //    @SerializedName("prContentImage")
//    @Expose
//    private List<NewsImage> newsImages = null;
    @SerializedName("prContentVideo")
    @Expose
    private List<NewsVideo> newsVideos = null;
    @SerializedName("images")
    @Expose
    private List<Image> images = null;
    @SerializedName("user")
    @Expose
    private User user = null;
    @SerializedName("area")
    @Expose
    private Area area = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public String getBodyMarkdown() {
        return bodyMarkdown;
    }

    public void setBodyMarkdown(String bodyMarkdown) {
        this.bodyMarkdown = bodyMarkdown;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAppIosUrl() {
        return appIosUrl;
    }

    public void setAppIosUrl(String appIosUrl) {
        this.appIosUrl = appIosUrl;
    }

    public String getAppAndroidUrl() {
        return appAndroidUrl;
    }

    public void setAppAndroidUrl(String appAndroidUrl) {
        this.appAndroidUrl = appAndroidUrl;
    }

    public String getAppWindowsUrl() {
        return appWindowsUrl;
    }

    public void setAppWindowsUrl(String appWindowsUrl) {
        this.appWindowsUrl = appWindowsUrl;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    //    public List<NewsImage> getNewsImages() {
//        return newsImages;
//    }
//
//    public List<Image> getNewsConvertedImages() {
//        List<Image> imageList = new ArrayList<>();
//
//        if (newsImages != null) {
//            for (NewsImage newsImage : this.newsImages) {
//                if (ViewUtils.checkImageStorage(newsImage.getImage()))
//                    imageList.add(newsImage.getImage());
//            }
//        }
//
//        return imageList;
//    }
//
//    public void setNewsImages(List<NewsImage> newsImages) {
//        this.newsImages = newsImages;
//    }

    public List<NewsVideo> getNewsVideos() {
        return newsVideos;
    }

    public void setNewsVideos(List<NewsVideo> newsVideos) {
        this.newsVideos = newsVideos;
    }

//    public boolean isContainImages() {
//        return this.newsImages != null && !this.newsImages.isEmpty();
//    }

    public boolean isContainImages() {
        return this.images != null && !this.images.isEmpty();
    }

    public boolean isContainVideos() {
        return this.newsVideos != null && !this.newsVideos.isEmpty();
    }

    // Images from list
    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public NewsDetail() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.body);
        dest.writeString(this.bodyMarkdown);
        dest.writeString(this.url);
        dest.writeString(this.language);
        dest.writeString(this.tags);
        dest.writeInt(this.userId);
        dest.writeString(this.appIosUrl);
        dest.writeString(this.appAndroidUrl);
        dest.writeString(this.appWindowsUrl);
        dest.writeLong(this.created != null ? this.created.getTime() : -1);
        dest.writeLong(this.id);
        dest.writeInt(this.areaId);
        dest.writeTypedList(this.newsVideos);
        dest.writeTypedList(this.images);
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.area, flags);
    }

    protected NewsDetail(Parcel in) {
        this.title = in.readString();
        this.body = in.readString();
        this.bodyMarkdown = in.readString();
        this.url = in.readString();
        this.language = in.readString();
        this.tags = in.readString();
        this.userId = in.readInt();
        this.appIosUrl = in.readString();
        this.appAndroidUrl = in.readString();
        this.appWindowsUrl = in.readString();
        long tmpCreated = in.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
        this.id = in.readLong();
        this.areaId = in.readInt();
        this.newsVideos = in.createTypedArrayList(NewsVideo.CREATOR);
        this.images = in.createTypedArrayList(Image.CREATOR);
        this.user = in.readParcelable(User.class.getClassLoader());
        this.area = in.readParcelable(Area.class.getClassLoader());
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel source) {
            return new News(source);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    @Override
    public String toString() {
        return "News{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", url='" + url + '\'' +
                ", language='" + language + '\'' +
                ", tags='" + tags + '\'' +
                ", userId=" + userId +
                ", appIosUrl='" + appIosUrl + '\'' +
                ", appAndroidUrl='" + appAndroidUrl + '\'' +
                ", appWindowsUrl='" + appWindowsUrl + '\'' +
                ", created=" + created +
                ", id=" + id +
                ", areaId=" + areaId +
//                ", newsImages=" + newsImages +
                ", newsVideos=" + newsVideos +
                ", images=" + images +
                '}';
    }
}
