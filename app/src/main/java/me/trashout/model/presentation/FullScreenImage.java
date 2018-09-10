package me.trashout.model.presentation;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import me.trashout.model.Image;

public class FullScreenImage implements Parcelable
{
    private Image image;
    private String userName;
    private Date imageCreated;

    protected FullScreenImage (Parcel in)
    {
        image = in.readParcelable(Image.class.getClassLoader());
        userName = in.readString();
        long tmpCreated = in.readLong();
        imageCreated = tmpCreated == -1 ? null : new Date(tmpCreated);
    }


    public FullScreenImage (Image image, String userName, Date imageCreated)
    {
        this.image = image;
        this.userName = userName;
        this.imageCreated = imageCreated;
    }

    public Image getImage ()
    {
        return image;
    }

    public void setImage (Image image)
    {
        this.image = image;
    }

    public String getUserName ()
    {
        return userName;
    }

    public void setUserName (String userName)
    {
        this.userName = userName;
    }

    public Date getImageCreated ()
    {
        return imageCreated;
    }

    public void setImageCreated (Date imageCreated)
    {
        this.imageCreated = imageCreated;
    }

    @Override
    public boolean equals (Object o)
    {
        if (this == o) return true;
        if (!(o instanceof FullScreenImage)) return false;
        FullScreenImage that = (FullScreenImage) o;
        return getImage().equals(that.getImage()) &&
                getUserName().equals(that.getUserName()) &&
                getImageCreated().equals(that.getImageCreated());
    }

    @Override
    public int hashCode ()
    {
        return (getImage().hashCode() + getUserName().hashCode());
    }

    @Override
    public String toString ()
    {
        return "FullScreenImage{" +
                "image=" + image +
                ", userName='" + userName + '\'' +
                ", imageCreated='" + imageCreated + '\'' +
                '}';
    }

    @Override
    public int describeContents ()
    {
        return 0;
    }

    @Override
    public void writeToParcel (Parcel dest, int flags)
    {
        dest.writeParcelable(this.image, flags);
        dest.writeString(this.userName);
        dest.writeLong(this.imageCreated != null ? this.imageCreated.getTime() : -1);
    }

    public static final Creator<FullScreenImage> CREATOR = new Creator<FullScreenImage>()
    {
        @Override
        public FullScreenImage createFromParcel (Parcel in)
        {
            return new FullScreenImage(in);
        }

        @Override
        public FullScreenImage[] newArray (int size)
        {
            return new FullScreenImage[size];
        }
    };
}
