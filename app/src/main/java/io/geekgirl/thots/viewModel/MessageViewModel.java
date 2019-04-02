package io.geekgirl.thots.viewModel;

import android.app.Application;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.geekgirl.thots.models.Message;
import io.geekgirl.thots.utils.Constants;
import io.geekgirl.thots.utils.Prefs;

/**
 * Created by Rim Gazzah on 31/03/19
 */
public class MessageViewModel extends AndroidViewModel {
    private static String mUserUID;
    private FirebaseDatabase mFirebaseDatabase;

    private final MutableLiveData<Boolean> messageUploadIsSuccessful = new MutableLiveData<>();

    public MutableLiveData<Boolean> getMessageUploadIsSuccessful() {
        return messageUploadIsSuccessful;
    }

    public MessageViewModel(@NonNull Application application) {
        super(application);
        mUserUID = Prefs.getPref(Prefs.USER_UID, application);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
    }

    public void sendMsg(@NonNull String recipientUid, @NonNull String msg) {
        Message message = new Message(recipientUid, mUserUID, msg);
        Task uploadTask = mFirebaseDatabase
                .getReference()
                .child(Constants.USER_PATH)
                .child(recipientUid)
                .child(Constants.MESSAGE_PATH)
                .push()
                .setValue(message);
        uploadTask.addOnSuccessListener(o -> messageUploadIsSuccessful.setValue(true));
        uploadTask.addOnFailureListener(o -> messageUploadIsSuccessful.setValue(false));
    }
}
