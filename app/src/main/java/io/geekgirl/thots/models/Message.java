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

    public Message() {
    }

    public Message(String uidRecipient, String uidSender, String message) {
        this.uidRecipient = uidRecipient;
        this.uidSender = uidSender;
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
        dest.writeString(this.uidSender);
        dest.writeString(this.message);
    }

    protected Message(Parcel in) {
        this.id = in.readString();
        this.uidSender = in.readString();
        this.message = in.readString();
    }

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
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
