package io.geekgirl.thots.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import io.geekgirl.thots.BuildConfig;
import io.geekgirl.thots.R;
import io.geekgirl.thots.manager.events.UserLocationEvent;
import io.geekgirl.thots.manager.geofence.GeofenceBuilder;
import io.geekgirl.thots.manager.geofence.GeofenceTransitionsJobIntentService;
import io.geekgirl.thots.models.User;
import io.geekgirl.thots.utils.Constants;
import io.geekgirl.thots.utils.DebugLog;
import io.geekgirl.thots.utils.Prefs;
import io.geekgirl.thots.utils.Tools;
import io.geekgirl.thots.viewModel.UserViewModel;

/**
 * Created by Rim Gazzah on 28/03/19
 */

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    protected GoogleApiClient mGoogleApiClient;
    private GeofenceBuilder mGeofenceBuilder;
    private UserViewModel userViewModel;
    private User mUser;
    public BroadcastReceiver networkChangeReceiver;
    public Boolean isConnected;
    private NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mGeofenceBuilder = new GeofenceBuilder(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initGeoLocation() {
        userViewModel.getGeofencesLiveData().observe(this, geofences -> {
            DebugLog.d("geofences size" + geofences.size());
            mGeofenceBuilder.setGeofencesList(geofences);
        });
    }

    public void registerToNetworkListener() {
        networkChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isConnected = Tools.isNetworkAvailable(MainActivity.this);
                if (isConnected) {
                    mGeofenceBuilder.initLocation();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);
    }

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        buildGoogleApiClient();
        if (!mGeofenceBuilder.checkPermissions()) {
            mGeofenceBuilder.requestPermissions();
        } else {

            mGeofenceBuilder.initLocation();
        }
        EventBus.getDefault().register(this);
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


    @Override
    protected void onResume() {
        super.onResume();
        registerToNetworkListener();
        initGeoLocation();
        mGeofenceBuilder.initLocation();
        Tools.isMyServiceRunning(this, GeofenceTransitionsJobIntentService.class);
    }

    @Override
    protected void onPause() {
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
            networkChangeReceiver = null;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
            networkChangeReceiver = null;
        }
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusListener(UserLocationEvent event) {
        DebugLog.d("UserLocationEvent triggered");
        userViewModel.updateLocation(event.getLocation());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.logout:
                logout();
                break;
            case R.id.messages:
                goToMessages();
                break;
            case R.id.nearby:
                navController.popBackStack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemNearBy = menu.findItem(R.id.nearby);
        MenuItem itemMsg = menu.findItem(R.id.messages);
        MenuItem itemLogout = menu.findItem(R.id.logout);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            switch (destination.getId()) {
                case R.id.nearByUsersFragment: {
                    itemNearBy.setVisible(false);
                    itemMsg.setVisible(true);
                    itemLogout.setVisible(true);
                }
                break;
                case R.id.messageDetailFragment:
                case R.id.MessageFragment: {
                    itemNearBy.setVisible(true);
                    itemMsg.setVisible(false);
                    itemLogout.setVisible(false);
                }
                break;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    private void goToMessages() {
        navController.navigate(R.id.action_to_MessageFragment);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Prefs.clear(this);
        navController.navigate(R.id.action_nearByUsersFragment_to_registrationActivity);
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        DebugLog.i("onRequestPermissionResult");
        if (requestCode == Constants.REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                DebugLog.i("User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                DebugLog.i("Permission granted.");
                initGeoLocation();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                mGeofenceBuilder.showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }
}
