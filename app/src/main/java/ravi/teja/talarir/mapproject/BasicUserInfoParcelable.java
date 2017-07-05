package ravi.teja.talarir.mapproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by talarir on 27/04/2017.
 */

public class BasicUserInfoParcelable implements Parcelable
{
    String fullName,lastName,emailId,userPhoto;


    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(fullName);
        dest.writeString(lastName);
        dest.writeString(emailId);
        dest.writeString(userPhoto);
    }

    public BasicUserInfoParcelable(String fullName,String lastName,String emailId,String userPhoto)
    {
        this.fullName=fullName;
        this.lastName=lastName;
        this.emailId=emailId;
        this.userPhoto=userPhoto;
    }
    public BasicUserInfoParcelable(Parcel in)
    {
        this.fullName=in.readString();
        this.lastName=in.readString();
        this.emailId=in.readString();
        this.userPhoto=in.readString();
    }
    public static final Parcelable.Creator<BasicUserInfoParcelable> CREATOR = new Parcelable.Creator<BasicUserInfoParcelable>() {

        @Override
        public BasicUserInfoParcelable createFromParcel(Parcel source) {
            return new BasicUserInfoParcelable(source);
        }

        @Override
        public BasicUserInfoParcelable[] newArray(int size) {
            return new BasicUserInfoParcelable[size];
        }
    };
}
