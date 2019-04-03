package io.geekgirl.thots.ui;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import io.geekgirl.thots.R;
import io.geekgirl.thots.databinding.FragmentMessageDetailBinding;
import io.geekgirl.thots.models.User;
import io.geekgirl.thots.utils.Constants;
import io.geekgirl.thots.viewModel.MessageViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageDetailFragment extends Fragment {

    private MessageViewModel mMessageViewModel;
    private FragmentMessageDetailBinding mBinding;
    private User mRecipient;
    private Activity mActivity;
    private NavController navController;



    public MessageDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecipient = getArguments().getParcelable(Constants.RECIPIENT_USER);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_message_detail, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        mMessageViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        mBinding.sendMsgBt.setOnClickListener(v -> {
            String msg = mBinding.messageText.getText().toString();
            if (!msg.isEmpty()){
                mMessageViewModel.sendMsg(mRecipient.getUid(),msg);
            }else {
                Toast.makeText(mActivity, mActivity.getString(R.string.info_msg_empty), Toast.LENGTH_SHORT).show();
            }
        });
        mMessageViewModel.getMessageUploadIsSuccessful().observe(this, isSent -> {
            if (isSent!=null ){
                if(isSent){
                    Toast.makeText(mActivity, mActivity.getString(R.string.info_msg_send_success), Toast.LENGTH_SHORT).show();
                    navController.popBackStack();
                }
                else {
                    Toast.makeText(mActivity, mActivity.getString(R.string.info_msg_send_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
