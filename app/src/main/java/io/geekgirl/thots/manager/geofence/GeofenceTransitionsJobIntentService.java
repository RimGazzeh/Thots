/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.geekgirl.thots.manager.geofence;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import io.geekgirl.thots.R;
import io.geekgirl.thots.manager.events.NearbyUsersEvent;
import io.geekgirl.thots.utils.DebugLog;


public class GeofenceTransitionsJobIntentService extends JobIntentService {

    private List<String> userIdList;
    private static final int JOB_ID = 573;


    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, GeofenceTransitionsJobIntentService.class, JOB_ID, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DebugLog.d("onCreated");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            DebugLog.e(errorMessage);
            return;
        }

        DebugLog.d("onHandleWork");
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition,
                    triggeringGeofences);

            // Send notification and log the transition details.
            DebugLog.i(geofenceTransitionDetails);
        } else {
            // Log the error.
            DebugLog.e(getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }
    }


    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param geofenceTransition  The ID of the geofence transition.
     * @param triggeringGeofences The geofence(s) triggered.
     * @return The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);
        DebugLog.v("User: " + geofenceTransitionString);
        userIdList = new ArrayList<>();
        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            // Send notification + log the transition details
            DebugLog.i(geofence.getRequestId());

            /***send rxbus_event***/
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                DebugLog.i("user Entred");
                userIdList.add(geofence.getRequestId());
            } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                DebugLog.i("user left");
                if (userIdList.contains(geofence.getRequestId())) {
                    userIdList.remove(geofence.getRequestId());
                }
            }

            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        EventBus.getDefault().post(new NearbyUsersEvent(userIdList));

        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);
        DebugLog.i("users triggered" + triggeringGeofencesIdsString);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType A transition type constant defined in Geofence
     * @return A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }
}
