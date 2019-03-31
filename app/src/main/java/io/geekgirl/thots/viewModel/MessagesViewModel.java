package io.geekgirl.thots.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import io.geekgirl.thots.utils.Prefs;

/**
 * Created by Rim Gazzah on 31/03/19
 */
public class MessagesViewModel extends AndroidViewModel {
    private static String mUserUID;

    public MessagesViewModel(@NonNull Application application) {
        super(application);
        mUserUID = Prefs.getPref(Prefs.USER_UID, application);
    }
}
