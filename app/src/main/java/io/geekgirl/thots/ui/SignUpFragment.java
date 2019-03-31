package io.geekgirl.thots.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import io.geekgirl.thots.R;
import io.geekgirl.thots.databinding.FragmentSignUpBinding;


public class SignUpFragment extends Fragment {
    private FragmentSignUpBinding binding;
    private FirebaseAuth mAuth;
    private static final String TAG = "SignInFragment";
    private Activity activity;
    private NavController navController;


    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        activity = getActivity();
        if (activity == null)
            return;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

/*
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);

        showProgress(true);

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        onSuccessCnx(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(getActivity(), activity.getString(R.string.info_message_create_account_failed),
                                Toast.LENGTH_SHORT).show();
                    }

                    // [START_EXCLUDE]
                    showProgress(false);
                    // [END_EXCLUDE]
                });
        // [END create_user_with_email]
    }*/
}
