package io.geekgirl.thots;

import android.app.Application;

import com.google.firebase.FirebaseApp;

/**
 * Created by Rim Gazzah on 31/03/19
 */
public class ThotsApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
