package io.geekgirl.thots.manager.events;

import io.geekgirl.thots.models.Location;

/**
 * Created by Rim Gazzah on 31/03/19
 */
public class UserLocationEvent {
    private Location location;

    public UserLocationEvent(double longitude, double latitude) {
        this.location = new Location(longitude, latitude);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
