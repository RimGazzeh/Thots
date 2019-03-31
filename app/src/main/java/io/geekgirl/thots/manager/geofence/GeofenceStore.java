package io.geekgirl.thots.manager.geofence;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import io.geekgirl.thots.manager.events.UserLocationEvent;
import io.geekgirl.thots.utils.Constants;
import io.geekgirl.thots.utils.DebugLog;


public class GeofenceStore implements ConnectionCallbacks,
        OnConnectionFailedListener, ResultCallback<Status>, LocationListener {

    private final String TAG = this.getClass().getSimpleName();

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

    /**
     * Constructs a new GeofenceStore.
     *
     * @param context   The context to use.
     * @param geofences List of geofences to monitor.
     */
    public GeofenceStore(Activity context, ArrayList<Geofence> geofences) {
        mContext = context;
        mGeofences = new ArrayList<Geofence>(geofences);
        //Toast.makeText(context, " ffgggggggggggg"+mGeofences.size(), Toast.LENGTH_SHORT).show();
        mPendingIntent = null;

        // Build a new GoogleApiClient, specify that we want to use LocationServices
        // by adding the API to the client, specify the connection callbacks are in
        // this class as well as the OnConnectionFailed method.
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        // This is purely optional and has nothing to do with geofencing.
        // I added this as a way of debugging.
        // Define the LocationRequest.
        mLocationRequest = new LocationRequest();
        // We want a location update every 15 minutes // 1000 * 900 -- > 15 minutes
        mLocationRequest.setInterval(1000 * 900);
        // We want the location to be as accurate as possible.
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mGoogleApiClient.connect();
    }

    @Override
    public void onResult(Status result) {
        if (result.isSuccess()) {
            Log.v(TAG, "Success!");
        } else if (result.hasResolution()) {
            // TODO Handle resolution
        } else if (result.isCanceled()) {
            Log.v(TAG, "Canceled");
        } else if (result.isInterrupted()) {
            Log.v(TAG, "Interrupted");
        } else {

        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(TAG, "Connection failed.");
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

    public void initLocation() {
        DebugLog.v("initLocation");

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

        if (mGeofences != null && !mGeofences.isEmpty()) {
            mGeofencingRequest = new GeofencingRequest.Builder()
                    .addGeofences(mGeofences).build();
            mPendingIntent = createRequestPendingIntent();
            // Submitting the request to monitor geofences.
            PendingResult<Status> pendingResult = LocationServices.GeofencingApi
                    .addGeofences(mGoogleApiClient, mGeofencingRequest,
                            mPendingIntent);


            // Set the result callbacks listener to this class.
            pendingResult.setResultCallback(this);
        }
    }

    public void onRequestPermissionsResult(@NonNull int[] grantResults) {
        // Check if the only required permission has been granted
        if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            DebugLog.d("ACCESS_FINE_LOCATION && ACCESS_COARSE_LOCATION permission has now been granted.");
            initLocation();

        } else {
            DebugLog.w("ACCESS_FINE_LOCATION && ACCESS_COARSE_LOCATION permission was NOT granted.");
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.v(TAG, "Connection suspended.");
    }

    /**
     * This creates a PendingIntent that is to be fired when geofence transitions
     * take place. In this instance, we are using an IntentService to handle the
     * transitions.
     *
     * @return A PendingIntent that will handle geofence transitions.
     */
    private PendingIntent createRequestPendingIntent() {
        if (mPendingIntent == null) {
            Log.v(TAG, "Creating PendingIntent");
            Intent intent = new Intent(mContext, GeofenceTransitionsIntentService.class);
            mPendingIntent = PendingIntent.getService(mContext, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return mPendingIntent;
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.v(TAG, "Location Information\n"
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