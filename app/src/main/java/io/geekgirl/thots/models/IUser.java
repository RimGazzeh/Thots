package io.geekgirl.thots.models;

/**
 * Created by Rim Gazzah on 01/04/19
 */
public interface IUser {
    String getUid();

    String getUserName();

    String getEmail();

    String getPhotoUrl();

    String getDataOfBirth();

    Location getLocation();
}
