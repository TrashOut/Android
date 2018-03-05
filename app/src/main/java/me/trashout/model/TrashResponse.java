package me.trashout.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrashResponse implements Parcelable {
    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("activityId")
    @Expose
    private int activityId;
    @SerializedName("statusCode")
    @Expose
    private int statusCode;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.activityId);
        dest.writeInt(this.statusCode);
    }

    public TrashResponse() {
    }

    protected TrashResponse(Parcel in) {
        this.id = in.readLong();
        this.activityId = in.readInt();
        this.statusCode = in.readInt();
    }

    public static final Parcelable.Creator<TrashResponse> CREATOR = new Parcelable.Creator<TrashResponse>() {
        @Override
        public TrashResponse createFromParcel(Parcel source) {
            return new TrashResponse(source);
        }

        @Override
        public TrashResponse[] newArray(int size) {
            return new TrashResponse[size];
        }
    };
}
