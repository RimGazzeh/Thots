package io.geekgirl.thots.ui;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.geekgirl.thots.R;
import io.geekgirl.thots.databinding.FragmentNearByUsersBinding;
import io.geekgirl.thots.manager.events.NearbyUsersEvent;
import io.geekgirl.thots.models.User;
import io.geekgirl.thots.ui.adapters.UsersAdapter;
import io.geekgirl.thots.utils.Constants;
import io.geekgirl.thots.viewModel.UserViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class NearByUsersFragment extends Fragment implements UsersAdapter.OnUerClickListener {

    private UserViewModel mUserViewModel;
    private MutableLiveData<List<User>> listMutableLiveData;
    private UsersAdapter mUsersAdapter;
    private FragmentNearByUsersBinding mBinding;
    private Activity mActivity;
    private List<User> mUsersList = new ArrayList<>();
    private NavController navController;

    public NearByUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_near_by_users, container, false);

        // Inflate the layout for this fragment
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        mBinding.usersNearbyProgress.setVisibility(View.VISIBLE);
        mBinding.usersNearbyVoid.setVisibility(View.GONE);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        mBinding.usersNearbyList.setLayoutManager(mLayoutManager);
        mBinding.usersNearbyList.setItemAnimator(new DefaultItemAnimator());
        mBinding.usersNearbyList.setHasFixedSize(true);
        mUsersAdapter = new UsersAdapter();
        mUsersAdapter.setOnUerClickListener(this);
        mBinding.usersNearbyList.setAdapter(mUsersAdapter);
        mBinding.usersNearbyList.setVisibility(View.GONE);
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

    private void resultUI(boolean isVoid) {
        if (!isVoid) {
            mBinding.usersNearbyVoid.setVisibility(View.GONE);
            mBinding.usersNearbyList.setVisibility(View.VISIBLE);
        } else {
            mBinding.usersNearbyVoid.setVisibility(View.VISIBLE);
            mBinding.usersNearbyList.setVisibility(View.GONE);
        }
    }

    private void setUsersAdapter() {
        mBinding.usersNearbyProgress.setVisibility(View.GONE);
        if (mUsersList != null && !mUsersList.isEmpty()) {
            resultUI(false);
            mUsersAdapter.setUsersList(mUsersList);
        } else {
            resultUI(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUsersAdapter();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusListener(NearbyUsersEvent event) {
        List<String> usersIds = event.getUsersId();
        if (usersIds != null && !usersIds.isEmpty()) {
            mUserViewModel.getUserByUID(usersIds).observe(this, users -> {
                mUsersList = users;
                setUsersAdapter();
            });
        } else {
            mBinding.usersNearbyProgress.setVisibility(View.GONE);
            resultUI(true);

        }
    }

    @Override
    public void onUserClick(int position) {
        if (mUsersList != null && !mUsersList.isEmpty() && mUsersList.size() > position) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.RECIPIENT_USER, mUsersList.get(position));
            navController.navigate(R.id.action_nearByUsersFragment_to_messageDetailFragment, bundle);
        }

    }
}
