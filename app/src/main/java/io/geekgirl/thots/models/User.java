package io.geekgirl.thots.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rim Gazzah on 28/03/19
 */
public class User implements Parcelable {

    private String uid;
    private String userName;
    private String email;
    private String photoUrl;
    private String dataOfBirth;
    private Location location;

    public User() {
    }

    public User(String uid, String userName, String email, String photoUrl) {
        this.uid = uid;
        this.userName = userName;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getDataOfBirth() {
        return dataOfBirth;
    }

    public void setDataOfBirth(String dataOfBirth) {
        this.dataOfBirth = dataOfBirth;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", dataOfBirth='" + dataOfBirth + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.userName);
        dest.writeString(this.email);
        dest.writeString(this.photoUrl);
        dest.writeString(this.dataOfBirth);
        dest.writeParcelable(this.location, flags);
    }

    protected User(Parcel in) {
        this.uid = in.readString();
        this.userName = in.readString();
        this.email = in.readString();
        this.photoUrl = in.readString();
        this.dataOfBirth = in.readString();
        this.location = in.readParcelable(Location.class.getClassLoader());
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
}
