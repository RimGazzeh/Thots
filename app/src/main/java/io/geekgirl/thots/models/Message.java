package io.geekgirl.thots.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rim Gazzah on 01/04/19
 */
public class Message implements IMessage, Parcelable {
    private String id;
    private String uidRecipient;
    private String uidSender;
    private String message;
    private User user;

    public Message() {
    }

    public String getUidRecipient() {
        return uidRecipient;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUidRecipient(String uidRecipient) {
        this.uidRecipient = uidRecipient;
    }

    public Message(String uidRecipient, String message) {
        this.uidRecipient = uidRecipient;
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", uidSender='" + uidSender + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUidSender() {
        return uidSender;
    }

    public void setUidSender(String uidSender) {
        this.uidSender = uidSender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.uidRecipient);
        dest.writeString(this.uidSender);
        dest.writeString(this.message);
        dest.writeParcelable(this.user, flags);
    }

    protected Message(Parcel in) {
        this.id = in.readString();
        this.uidRecipient = in.readString();
        this.uidSender = in.readString();
        this.message = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
