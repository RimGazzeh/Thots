package io.geekgirl.thots.utils;

/**
 * Created by Rim Gazzah on 28/03/19
 */
public class Constants {
    public static final String SIGNED_USER = "signed_user";
    public static final int REQUEST_ACCESS_LOCATION = 123;
    public static final int GEOFENCE_RADIUS_IN_METER = 1000;
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 8;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;

    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    /**
     * Firebase constants
      */
    public static final String USER_PATH = "user";
    public static final String USER_LOCATION_PATH = "location";
}
