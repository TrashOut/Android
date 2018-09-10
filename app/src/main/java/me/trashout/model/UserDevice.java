package me.trashout.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDevice implements Parcelable {

    @SerializedName("tokenFCM")
    @Expose
    private String tokenFCM;

    @SerializedName("language")
    @Expose
    private String language;


    @SerializedName("deviceId")
    @Expose
    private String deviceId;

    @SerializedName("id")
    @Expose
    private int id;

    public String getTokenFCM() {
        return tokenFCM;
    }

    public void setTokenFCM(String tokenFCM) {
        this.tokenFCM = tokenFCM;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserDevice(String tokenFCM, String language, String deviceId) {
        this.tokenFCM = tokenFCM;
        this.language = language;
        this.deviceId = deviceId;
    }

    public UserDevice() {
    }

    @Override
    public String toString() {
        return "UserDevice{" +
                "tokenFCM='" + tokenFCM + '\'' +
                ", language='" + language + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", id=" + id +
                '}';
    }

    protected UserDevice(Parcel in) {
        tokenFCM = in.readString();
        language = in.readString();
        deviceId = in.readString();
        id = in.readInt();
    }

    public static final Creator<UserDevice> CREATOR = new Creator<UserDevice>() {
        @Override
        public UserDevice createFromParcel(Parcel in) {
            return new UserDevice(in);
        }

        @Override
        public UserDevice[] newArray(int size) {
            return new UserDevice[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tokenFCM);
        dest.writeString(language);
        dest.writeString(deviceId);
        dest.writeInt(id);
    }
}
