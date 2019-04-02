package io.geekgirl.thots.manager.events;

import java.util.List;

import io.geekgirl.thots.utils.DebugLog;

/**
 * Created by Rim Gazzah on 31/03/19
 */
public class NearbyUsersEvent {
    private List<String> usersId;
    public NearbyUsersEvent(List<String> usersId){
        this.usersId = usersId;
        DebugLog.d(usersId.size() + "");
    }

    public List<String> getUsersId() {
        return usersId;
    }

    public void setUsersId(List<String> usersId) {
        this.usersId = usersId;
    }
}
