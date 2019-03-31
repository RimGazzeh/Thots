package io.geekgirl.thots.viewModel;

import android.app.Application;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private static  DatabaseReference USERS_REF ;

    private FirebaseQueryLiveData mLiveData ;
    private List<User> mUsers = new ArrayList<>();

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

    private class Deserializer implements Function<DataSnapshot, List<User>> {
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

    @NonNull
    public LiveData<List<User>> getUserLiveData() {
        LiveData<List<User>> memoLiveData = Transformations.map(mLiveData, new Deserializer());
        return memoLiveData;
    }


    public void createAndSendToDataBase(User user) {
        Task uploadTask = mFirebaseDatabase
                .getReference()
                .child(Constants.USER_PATH)
                .child(mUserUID)
                .setValue(user);
        uploadTask.addOnSuccessListener(o -> userUpdatedIsSuccessful.setValue(true));
    }

    public void updateLocation(Location location) {
        Task uploadTask = mFirebaseDatabase
                .getReference()
                .child(Constants.USER_PATH)
                .child(mUserUID)
                .setValue(location);
        uploadTask.addOnSuccessListener(o -> userUpdatedIsSuccessful.setValue(true));
    }

}
