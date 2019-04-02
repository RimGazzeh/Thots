package io.geekgirl.thots.viewModel;

import android.app.Application;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import io.geekgirl.thots.data.FirebaseQueryLiveData;
import io.geekgirl.thots.models.Location;
import io.geekgirl.thots.models.User;
import io.geekgirl.thots.utils.Constants;
import io.geekgirl.thots.utils.Prefs;

/**
 * Created by Rim Gazzah on 31/03/19
 */
public class UserViewModel extends AndroidViewModel {

    private static String mUserUID;
    private FirebaseDatabase mFirebaseDatabase;
    private static DatabaseReference USERS_REF;

    private FirebaseQueryLiveData mLiveData;
    private List<User> mUsers = new ArrayList<>();
    private List<Geofence> mGeofences = new ArrayList<>();

    private final MutableLiveData<Boolean> userUpdatedIsSuccessful = new MutableLiveData<>();

    public MutableLiveData<Boolean> getUserUpdatedIsSuccessful() {
        return userUpdatedIsSuccessful;
    }


    public UserViewModel(@NonNull Application application) {
        super(application);
        mUserUID = Prefs.getPref(Prefs.USER_UID, application);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        USERS_REF = mFirebaseDatabase.getReference("/" + Constants.USER_PATH);
        mLiveData = new FirebaseQueryLiveData(USERS_REF);
    }

    /***********************************************************************************************************/
    private class UserDeserializer implements Function<DataSnapshot, List<User>> {
        @Override
        public List<User> apply(DataSnapshot dataSnapshot) {
            mUsers.clear();
            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                User user = snap.getValue(User.class);
                user.setUid(snap.getKey());
                mUsers.add(user);
            }
            return mUsers;
        }
    }

    private class GeofencesDeserializer implements Function<DataSnapshot, List<Geofence>> {
        @Override
        public List<Geofence> apply(DataSnapshot dataSnapshot) {
            mGeofences.clear();
            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                User user = snap.getValue(User.class);
                user.setUid(snap.getKey());
                if (!user.getUid().equals(mUserUID)) {
                    Geofence geofence = new Geofence.Builder().setRequestId(user.getUid()).setCircularRegion(
                            user.getLocation().getLatitude(),
                            user.getLocation().getLongitude(),
                            Constants.GEOFENCE_RADIUS_IN_METER
                    ).setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                    Geofence.GEOFENCE_TRANSITION_EXIT)
                            //it's must to set time in millis with dwell transition
                            .build();
                    mGeofences.add(geofence);
                }
            }
            return mGeofences;
        }
    }

    /***********************************************************************************************************/

    @NonNull
    public LiveData<List<User>> getUserLiveData() {
        LiveData<List<User>> userLiveData = Transformations.map(mLiveData, new UserDeserializer());
        return userLiveData;
    }

    @NonNull
    public LiveData<List<Geofence>> getGeofencesLiveData() {
        LiveData<List<Geofence>> geoLiveData = Transformations.map(mLiveData, new GeofencesDeserializer());
        return geoLiveData;
    }


    public void createAndSendToDataBase(User user) {
        Task uploadTask = mFirebaseDatabase
                .getReference()
                .child(Constants.USER_PATH)
                .child(user.getUid())
                .setValue(user);
        uploadTask.addOnSuccessListener(o -> userUpdatedIsSuccessful.setValue(true));
    }

    public void checkUser(User user) {
        mFirebaseDatabase.getReference().child(Constants.USER_PATH)
                .child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    createAndSendToDataBase(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateLocation(Location location) {
        Task uploadTask = mFirebaseDatabase
                .getReference()
                .child(Constants.USER_PATH)
                .child(mUserUID)
                .child(Constants.USER_LOCATION_PATH)
                .setValue(location);
        uploadTask.addOnSuccessListener(o -> userUpdatedIsSuccessful.setValue(true));
    }

}
