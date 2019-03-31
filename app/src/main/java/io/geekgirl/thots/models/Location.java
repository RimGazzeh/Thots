package io.geekgirl.thots.models;

/**
 * Created by Rim Gazzah on 31/03/19
 */
public class Location {
    private double latitude;
    private double longitude;

    public Location() {
    }

    public Location(double longitude, double latitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
