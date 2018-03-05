package me.trashout.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.trashout.R;

public class TrashPoint implements Parcelable, TrashMapItem {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("activityId")
    @Expose
    private int activityId;
    @SerializedName("images")
    @Expose
    private List<Image> images = new ArrayList<Image>();
    @SerializedName("gps")
    @Expose
    private Gps gps;
    @SerializedName("size")
    @Expose
    private Constants.TrashSize size;
    @SerializedName("types")
    @Expose
    private List<Constants.TrashType> types = new ArrayList<Constants.TrashType>();
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("userInfo")
    @Expose
    private UserInfo userInfo;
    @SerializedName("anonymous")
    @Expose
    private boolean anonymous;
    @SerializedName("accessibility")
    @Expose
    private Accessibility accessibility;
    @SerializedName("status")
    @Expose
    private Constants.TrashStatus status;
    @SerializedName("cleanedByMe")
    @Expose
    private boolean cleanedByMe;
    @SerializedName("created")
    @Expose
    private Date created;
    @SerializedName("updateTime")
    @Expose
    private Date updateTime;
    @SerializedName("updateHistory")
    @Expose
    private List<UpdateHistory> updateHistory = new ArrayList<UpdateHistory>();
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("events")
    @Expose
    private List<Event> events = new ArrayList<Event>();
    @SerializedName("updateNeeded")
    @Expose
    private boolean updateNeeded;
    @SerializedName("userId")
    @Expose
    private long userId;

    public TrashPoint() {
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return The activityId
     */
    public int getActivityId() {
        return activityId;
    }

    /**
     * @param activityId The activityId
     */
    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    /**
     * @return The images
     */
    public List<Image> getImages() {
        return images;
    }

    /**
     * @param images The images
     */
    public void setImages(List<Image> images) {
        this.images = images;
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
     * @return The size
     */
    public Constants.TrashSize getSize() {
        return size;
    }

    /**
     * @param size The size
     */
    public void setSize(Constants.TrashSize size) {
        this.size = size;
    }

    /**
     * @return The types
     */
    public List<Constants.TrashType> getTypes() {
        return types;
    }

    /**
     * @param types The types
     */
    public void setTypes(List<Constants.TrashType> types) {
        this.types = types;
    }

    /**
     * @return The note
     */
    public String getNote() {
        return note;
    }

    /**
     * @param note The note
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * @return The userInfo
     */
    public UserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * @param userInfo The userInfo
     */
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * @return The anonymous
     */
    public boolean isAnonymous() {
        return anonymous;
    }

    /**
     * @param anonymous The anonymous
     */
    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    /**
     * @return The accessibility
     */
    public Accessibility getAccessibility() {
        return accessibility;
    }

    /**
     * @param accessibility The accessibility
     */
    public void setAccessibility(Accessibility accessibility) {
        this.accessibility = accessibility;
    }

    /**
     * @return The status
     */
    public Constants.TrashStatus getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(Constants.TrashStatus status) {
        this.status = status;
    }

    /**
     * @return The cleanedByMe
     */
    public boolean isCleanedByMe() {
        return cleanedByMe;
    }

    /**
     * @param cleanedByMe The cleanedByMe
     */
    public void setCleanedByMe(boolean cleanedByMe) {
        this.cleanedByMe = cleanedByMe;
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
     * @return The updateTime
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime The updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }


    /**
     * @return The last change date
     */
    public Date getLastChangeDate() {
        return this.updateTime != null ? this.updateTime : this.created;
    }

    /**
     * @return The updateHistory
     */
    public List<UpdateHistory> getUpdateHistory() {
        return updateHistory;
    }

    /**
     * @param updateHistory The updateHistory
     */
    public void setUpdateHistory(List<UpdateHistory> updateHistory) {
        this.updateHistory = updateHistory;
    }

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return The events
     */
    public List<Event> getEvents() {
        return events;
    }

    /**
     * @param events The events
     */
    public void setEvents(List<Event> events) {
        this.events = events;
    }

    /**
     * @return The updateNeeded
     */

    @Override
    public int getUpdateNeeded() {
        return updateNeeded ? 1 : 0;
    }

    /**
     * @param updateNeeded The updateNeeded
     */
    public void setUpdateNeeded(boolean updateNeeded) {
        this.updateNeeded = updateNeeded;
    }

    /**
     * @return Is update needed
     */
    public boolean isUpdateNeeded() {
        return updateNeeded;
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
     * Get Last change Name or Annonymous
     *
     * @return
     */
    public String getLastChangeName(Context context) {
        if (anonymous) {
            return context.getString(R.string.trash_anonymous);
        } else if (updateHistory != null && !updateHistory.isEmpty()) {
            return updateHistory.get(0).getUserInfo().getFullName(context);
        } else if (userInfo != null) {
            return userInfo.getFullName(context);
        } else {
            return context.getString(R.string.global_unknow);
        }
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(getGps().getLat(), getGps().getLng());
    }

    @Override
    public int getTotal() {
        return 1;
    }

    @Override
    public int getRemains() {
        return (Constants.TrashStatus.CLEANED.equals(status) ? 0 : 1);
    }

    @Override
    public int getCleaned() {
        return (Constants.TrashStatus.CLEANED.equals(status) ? 1 : 0);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.activityId);
        dest.writeTypedList(this.images);
        dest.writeParcelable(this.gps, flags);
        dest.writeInt(this.size == null ? -1 : this.size.ordinal());
        dest.writeList(this.types);
        dest.writeString(this.note);
        dest.writeParcelable(this.userInfo, flags);
        dest.writeByte(this.anonymous ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.accessibility, flags);
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
        dest.writeByte(this.cleanedByMe ? (byte) 1 : (byte) 0);
        dest.writeLong(this.created != null ? this.created.getTime() : -1);
        dest.writeLong(this.updateTime != null ? this.updateTime.getTime() : -1);
        dest.writeTypedList(this.updateHistory);
        dest.writeString(this.url);
        dest.writeTypedList(this.events);
        dest.writeInt(this.updateNeeded ? (byte) 1 : (byte) 0);
        dest.writeLong(this.userId);
    }

    protected TrashPoint(Parcel in) {
        this.id = in.readLong();
        this.activityId = in.readInt();
        this.images = in.createTypedArrayList(Image.CREATOR);
        this.gps = in.readParcelable(Gps.class.getClassLoader());
        int tmpSize = in.readInt();
        this.size = tmpSize == -1 ? null : Constants.TrashSize.values()[tmpSize];
        this.types = new ArrayList<Constants.TrashType>();
        in.readList(this.types, Constants.TrashType.class.getClassLoader());
        this.note = in.readString();
        this.userInfo = in.readParcelable(UserInfo.class.getClassLoader());
        this.anonymous = in.readByte() != 0;
        this.accessibility = in.readParcelable(Accessibility.class.getClassLoader());
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : Constants.TrashStatus.values()[tmpStatus];
        this.cleanedByMe = in.readByte() != 0;
        long tmpCreated = in.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
        long tmpUpdateTime = in.readLong();
        this.updateTime = tmpUpdateTime == -1 ? null : new Date(tmpUpdateTime);
        this.updateHistory = in.createTypedArrayList(UpdateHistory.CREATOR);
        this.url = in.readString();
        this.events = in.createTypedArrayList(Event.CREATOR);
        this.updateNeeded = in.readByte() != 0;
        this.userId = in.readLong();
    }

    public static final Creator<TrashPoint> CREATOR = new Creator<TrashPoint>() {
        @Override
        public TrashPoint createFromParcel(Parcel source) {
            return new TrashPoint(source);
        }

        @Override
        public TrashPoint[] newArray(int size) {
            return new TrashPoint[size];
        }
    };

    @Override
    public String toString() {
        return "TrashPoint{" +
                "id=" + id +
                ", activityId=" + activityId +
                ", images=" + images +
                ", gps=" + gps +
                ", size=" + size +
                ", types=" + types +
                ", note='" + note + '\'' +
                ", userInfo=" + userInfo +
                ", anonymous=" + anonymous +
                ", accessibility=" + accessibility +
                ", status=" + status +
                ", cleanedByMe=" + cleanedByMe +
                ", created=" + created +
                ", updateTime=" + updateTime +
                ", updateHistory=" + updateHistory +
                ", url='" + url + '\'' +
                ", events=" + events +
                ", updateNeeded=" + updateNeeded +
                ", userId=" + userId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrashPoint that = (TrashPoint) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        return result;
    }
}