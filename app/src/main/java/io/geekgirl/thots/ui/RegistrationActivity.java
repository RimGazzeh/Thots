package io.geekgirl.thots.ui;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import io.geekgirl.thots.R;
import io.geekgirl.thots.utils.DebugLog;
import io.geekgirl.thots.utils.Prefs;

/**
 * Created by Rim Gazzah on 30/03/19
 */
public class RegistrationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth  = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            DebugLog.d("onAuthStateChanged:signed_in:" + user.getUid());
            Prefs.setPref(Prefs.USER_UID, user.getUid(), RegistrationActivity.this);
            Prefs.setPref(Prefs.USER_EMAIL, user.getEmail(), RegistrationActivity.this);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        } else if (user == null) {
            DebugLog.d("onAuthStateChanged:signed_out");
            setContentView(R.layout.activity_registration);
        }

    }
}
