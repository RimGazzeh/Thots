package io.geekgirl.thots.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import io.geekgirl.thots.R;
import io.geekgirl.thots.databinding.ItemMessageBinding;
import io.geekgirl.thots.models.IMessage;

/**
 * Created by Rim Gazzah on 01/04/19
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<? extends IMessage> mMessagesList;
    private OnMessageClickListener mMessageClickListener;

    public void setMessagesList(List<? extends IMessage> usersList) {
        this.mMessagesList = usersList;
        notifyDataSetChanged();
    }

    public void setOnMessageClickListener(OnMessageClickListener onMessageClickListener) {
        this.mMessageClickListener = onMessageClickListener;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMessageBinding itemUserBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_message, parent, false);
        return new MessageAdapter.MessageViewHolder(itemUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {
        IMessage iMessage = mMessagesList.get(position);
        holder.itemMessageBinding.setMessage(iMessage);
        holder.itemMessageBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mMessagesList != null ? mMessagesList.size() : 0;
    }

    public interface OnMessageClickListener {
        void onMessageClick(int position);
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        ItemMessageBinding itemMessageBinding;

        public MessageViewHolder(@NonNull ItemMessageBinding binding) {
            super(binding.getRoot());
            itemMessageBinding = binding;
        }
    }
}
