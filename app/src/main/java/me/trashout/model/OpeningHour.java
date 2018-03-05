package me.trashout.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Marek on 18.12.2017.
 * Appmine.cz
 */
public class OpeningHour implements Parcelable {


    @SerializedName("Start")
    @Expose
    private int start;
    @SerializedName("Finish")
    @Expose
    private int finish;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getFinish() {
        return finish;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    public OpeningHour() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.start);
        dest.writeInt(this.finish);
    }

    protected OpeningHour(Parcel in) {
        this.start = in.readInt();
        this.finish = in.readInt();
    }

    public static final Creator<OpeningHour> CREATOR = new Creator<OpeningHour>() {
        @Override
        public OpeningHour createFromParcel(Parcel source) {
            return new OpeningHour(source);
        }

        @Override
        public OpeningHour[] newArray(int size) {
            return new OpeningHour[size];
        }
    };
}
