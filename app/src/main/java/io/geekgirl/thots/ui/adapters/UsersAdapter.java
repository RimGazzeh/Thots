package io.geekgirl.thots.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import io.geekgirl.thots.R;
import io.geekgirl.thots.databinding.ItemUserBinding;
import io.geekgirl.thots.models.IUser;

/**
 * Created by Rim Gazzah on 01/04/19
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private List<? extends IUser> mUsersList;
    private OnUerClickListener mOnUerClickListener;

    public void setUsersList(List<? extends IUser> usersList) {
        this.mUsersList = usersList;
        notifyDataSetChanged();
    }

    public void setOnUerClickListener(OnUerClickListener onUerClickListener) {
        this.mOnUerClickListener = onUerClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding itemUserBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_user, parent, false);
        return new UserViewHolder(itemUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        IUser iUser = mUsersList.get(position);
        holder.itemUserBinding.setUser(iUser);
        holder.itemUserBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mUsersList != null ? mUsersList.size() : 0;
    }

    public interface OnUerClickListener {
        void onUserClick(int position);
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        ItemUserBinding itemUserBinding;

        public UserViewHolder(@NonNull ItemUserBinding binding) {
            super(binding.getRoot());
            itemUserBinding = binding;
        }
    }
}
