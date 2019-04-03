package io.geekgirl.thots.ui;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.geekgirl.thots.R;
import io.geekgirl.thots.databinding.FragmentHomeBinding;
import io.geekgirl.thots.models.Message;
import io.geekgirl.thots.ui.adapters.MessageAdapter;
import io.geekgirl.thots.viewModel.MessageViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment implements MessageAdapter.OnMessageClickListener {

    private MessageViewModel mMessageViewModel;
    private FragmentHomeBinding mBinding;
    private Activity mActivity;
    private MessageAdapter mMessageAdapter;
    private List<Message> mMessagesList;

    public MessageFragment() {
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        mMessageViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.messagesProgress.setVisibility(View.VISIBLE);
        mBinding.messagesVoid.setVisibility(View.GONE);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        mBinding.messagesList.setLayoutManager(mLayoutManager);
        mBinding.messagesList.setItemAnimator(new DefaultItemAnimator());
        mBinding.messagesList.setHasFixedSize(true);
        mMessageAdapter = new MessageAdapter();
        mMessageAdapter.setOnMessageClickListener(this);
        mBinding.messagesList.setAdapter(mMessageAdapter);
        mBinding.messagesList.setVisibility(View.GONE);
        initData();
    }

    private void initData() {
        mMessagesList = new ArrayList<>();
        mMessageViewModel.loadMessages().observe(this, messages -> {
            mMessagesList = messages;
            setMessageAdapter();
        });
    }

    private void resultUI(boolean isVoid) {
        if (!isVoid) {
            mBinding.messagesVoid.setVisibility(View.GONE);
            mBinding.messagesList.setVisibility(View.VISIBLE);
        } else {
            mBinding.messagesVoid.setVisibility(View.VISIBLE);
            mBinding.messagesList.setVisibility(View.GONE);
        }
    }

    private void setMessageAdapter() {
        mBinding.messagesProgress.setVisibility(View.GONE);
        if (mMessagesList != null && !mMessagesList.isEmpty()) {
            resultUI(false);
            mMessageAdapter.setMessagesList(mMessagesList);
        } else {
            resultUI(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setMessageAdapter();
    }

    @Override
    public void onMessageClick(int position) {

    }
}
