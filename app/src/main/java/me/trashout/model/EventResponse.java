package me.trashout.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventResponse implements Parcelable {
    @SerializedName("id")
    @Expose
    private long id;

    @SerializedName("statusCode")
    @Expose
    private int statusCode;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        dest.writeInt(this.statusCode);
    }

    public EventResponse() {
    }

    protected EventResponse(Parcel in) {
        this.id = in.readLong();
        this.statusCode = in.readInt();
    }

    public static final Creator<EventResponse> CREATOR = new Creator<EventResponse>() {
        @Override
        public EventResponse createFromParcel(Parcel source) {
            return new EventResponse(source);
        }

        @Override
        public EventResponse[] newArray(int size) {
            return new EventResponse[size];
        }
    };
}
