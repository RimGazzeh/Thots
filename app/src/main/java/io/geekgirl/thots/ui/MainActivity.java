package io.geekgirl.thots.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;
import io.geekgirl.thots.R;
import io.geekgirl.thots.manager.events.UserLocationEvent;
import io.geekgirl.thots.manager.geofence.GeofenceStore;
import io.geekgirl.thots.utils.DebugLog;
import io.geekgirl.thots.utils.Tools;
import io.geekgirl.thots.viewModel.UserViewModel;

import static android.location.GpsStatus.GPS_EVENT_STARTED;
import static android.location.GpsStatus.GPS_EVENT_STOPPED;

/**
 * Created by Rim Gazzah on 28/03/19
 */

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    protected GoogleApiClient mGoogleApiClient;
    private  GeofenceStore mGeofenceStore;
    private ArrayList<Geofence> mGeofences;
    private UserViewModel userViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        buildGoogleApiClient();
    }

    public void initGeoLocation() {
        mGeofences = new ArrayList<>();
        mGeofenceStore = new GeofenceStore(this, mGeofences);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        DebugLog.v("Google api client - onConnected");
        final ResultCallback c = this;
        if (mGoogleApiClient.isConnected()) {
            //// TODO: 31/03/19 populateGeofenceList
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        DebugLog.v("Google api client - Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        DebugLog.v("Google api client - Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onResult(@NonNull Status status) {
        DebugLog.v(status.toString());
    }


    public void GpsStatusListner() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        boolean gps_enabled = false;
        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gps_enabled) {

        }
        lm.addGpsStatusListener(event -> {
            switch (event) {
                case GPS_EVENT_STARTED: {
                    Toast.makeText(MainActivity.this, "GPS_EVENT_STARTED", Toast.LENGTH_SHORT).show();
                    if (Tools.isNetworkAvailable(MainActivity.this) && mGeofenceStore != null) {
                        mGeofenceStore.initLocation();
                    } else {
                        if (mGeofenceStore == null) {
                           Toast.makeText(MainActivity.this, "mGeofenceStore is null", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                case GPS_EVENT_STOPPED: {
                    Toast.makeText(MainActivity.this, "GPS_EVENT_STOPPED", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initGeoLocation();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusListener(UserLocationEvent event){
        DebugLog.d("UserLocationEvent triggered");
        userViewModel.updateLocation(event.getLocation());
    }
}
