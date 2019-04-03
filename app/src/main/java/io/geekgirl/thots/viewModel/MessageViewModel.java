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
import io.geekgirl.thots.models.Message;
import io.geekgirl.thots.utils.Constants;
import io.geekgirl.thots.utils.Prefs;

/**
 * Created by Rim Gazzah on 31/03/19
 */
public class MessageViewModel extends AndroidViewModel {
    private static String mUserUID;
    private FirebaseDatabase mFirebaseDatabase;
    private static DatabaseReference MSG_REF;
    private FirebaseQueryLiveData mLiveData;

    private final MutableLiveData<Boolean> messageUploadIsSuccessful = new MutableLiveData<>();
    private List<Message> mMessages = new ArrayList<>();

    public MutableLiveData<Boolean> getMessageUploadIsSuccessful() {
        return messageUploadIsSuccessful;
    }

    public MessageViewModel(@NonNull Application application) {
        super(application);
        mUserUID = Prefs.getPref(Prefs.USER_UID, application);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        MSG_REF = mFirebaseDatabase.getReference("/" + Constants.USER_PATH + "/" + mUserUID + "/" + Constants.MESSAGE_PATH);
        mLiveData = new FirebaseQueryLiveData(MSG_REF);
    }

    private class MessageDeserializer implements Function<DataSnapshot, List<Message>> {
        @Override
        public List<Message> apply(DataSnapshot dataSnapshot) {
            mMessages.clear();
            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                Message msg = snap.getValue(Message.class);
                msg.setId(snap.getKey());
                mMessages.add(msg);
            }
            return mMessages;
        }
    }

    @NonNull
    public LiveData<List<Message>> loadMessages() {
        LiveData<List<Message>> userLiveData = Transformations.map(mLiveData, new MessageDeserializer());
        return userLiveData;
    }

    public void sendMsg(@NonNull String recipientUid, @NonNull String msg) {
        Message message = new Message(recipientUid, msg);
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
