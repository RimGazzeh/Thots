package io.geekgirl.thots.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import androidx.fragment.app.Fragment;
import io.geekgirl.thots.R;
import io.geekgirl.thots.manager.events.NearbyUsersEvent;


/**
 * A simple {@link Fragment} subclass.
 */
public class NearByUsersFragment extends Fragment {


    public NearByUsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_near_by_users, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }


    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onEventBusListener(NearbyUsersEvent event){

    }

}
