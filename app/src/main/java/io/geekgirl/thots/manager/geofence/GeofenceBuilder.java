package io.geekgirl.thots.manager.geofence;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import io.geekgirl.thots.R;
import io.geekgirl.thots.manager.events.UserLocationEvent;
import io.geekgirl.thots.utils.Constants;
import io.geekgirl.thots.utils.DebugLog;


public class GeofenceBuilder implements ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener {


    /**
     * Context
     */
    private Activity mContext;

    /**
     * Google API client object.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Geofencing PendingIntent
     */
    private PendingIntent mPendingIntent;

    /**
     * List of geofences to monitor.
     */
    private ArrayList<Geofence> mGeofences;
    public static int requested = 0;

    /**
     * Geofence request.
     */
    private GeofencingRequest mGeofencingRequest;

    /**
     * Location Request object.
     */
    private LocationRequest mLocationRequest;
    private GeofencingClient geofencingClient;


    public GeofenceBuilder(Activity context){
        mContext = context;
        geofencingClient = LocationServices.getGeofencingClient(mContext);

    }
    public void setGeofencesList( List<Geofence> geofences) {
        mGeofences = new ArrayList<>(geofences);
        //Toast.makeText(context, " ffgggggggggggg"+mGeofences.size(), Toast.LENGTH_SHORT).show();
        // Build a new GoogleApiClient, specify that we want to use LocationServices
        // by adding the API to the client, specify the connection callbacks are in
        // this class as well as the OnConnectionFailed method.
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        // This is purely optional and has nothing to do with geofencing.
        // I added this as a way of debugging.
        // Define the LocationRequest.
        mLocationRequest = new LocationRequest();
        // We want a location update every 15 minutes // 1000 * 900 -- > 15 minutes
        //mLocationRequest.setInterval(1000 * 900);
        mLocationRequest.setInterval(1000);
        // We want the location to be as accurate as possible.
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        DebugLog.v("Connection failed.");
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // We're connected, now we need to create a GeofencingRequest with
        // the geofences we have stored.
        DebugLog.v("connected");
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            DebugLog.v("not accessed");
            if (requested == 0)
                requestAccessLocationPermissions();
        } else {
            initLocation();
        }

    }

    private void requestAccessLocationPermissions() {
        requested++;
        ActivityCompat.requestPermissions(mContext,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                Constants.REQUEST_ACCESS_LOCATION);
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    public void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                mContext.findViewById(android.R.id.content),
                mContext.getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(mContext.getString(actionStringId), listener).show();
    }

    public void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(mContext,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            DebugLog.i("Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    view -> {
                        // Request permission
                        ActivityCompat.requestPermissions(mContext,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                Constants.REQUEST_PERMISSIONS_REQUEST_CODE);
                    });
        } else {
            DebugLog.i("Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(mContext,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    public void initLocation() {
        DebugLog.v("initLocation");

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        DebugLog.v("initLocation okk");
        if (mGeofences != null && !mGeofences.isEmpty()) {
            DebugLog.v("adding geofences");
            mGeofencingRequest = new GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofences(mGeofences).build();
            mPendingIntent = createRequestPendingIntent();
            // Submitting the request to monitor geofences.
            geofencingClient
                    .addGeofences(mGeofencingRequest,
                            mPendingIntent).addOnSuccessListener(aVoid -> DebugLog.v("Success")).addOnFailureListener(e -> DebugLog.v("Fail"));


            // Set the result callbacks listener to this class.
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    public boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onConnectionSuspended(int cause) {
        DebugLog.v("Connection suspended.");
    }

    /**
     * This creates a PendingIntent that is to be fired when geofence transitions
     * take place. In this instance, we are using an IntentService to handle the
     * transitions.
     *
     * @return A PendingIntent that will handle geofence transitions.
     */
    private PendingIntent createRequestPendingIntent() {
        if (mPendingIntent != null) {
            return mPendingIntent;
        }

        DebugLog.v("Creating PendingIntent");
        Intent intent = new Intent(mContext, GeofenceBroadcastReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return mPendingIntent;
    }

    @Override
    public void onLocationChanged(Location location) {

        DebugLog.v("Location Information\n"
                + "==========\n"
                + "Provider:\t" + location.getProvider() + "\n"
                + "Lat & Long:\t" + location.getLatitude() + ", "
                + location.getLongitude() + "\n"
                + "Altitude:\t" + location.getAltitude() + "\n"
                + "Bearing:\t" + location.getBearing() + "\n"
                + "Speed:\t\t" + location.getSpeed() + "\n"
                + "Accuracy:\t" + location.getAccuracy() + "\n");
        EventBus.getDefault().post(new UserLocationEvent(location.getLongitude(), location.getLatitude()));
    }

    public void disconnect() {
        mGoogleApiClient.disconnect();
    }

}