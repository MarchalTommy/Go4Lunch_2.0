package com.aki.go4lunchv2.UI;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.databinding.WorkmatesRecyclerviewItemBinding;
import com.aki.go4lunchv2.models.User;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.WorkmatesViewHolder> {

    Context context;
    private List<User> users = new ArrayList<>();

    public WorkmatesAdapter(Context context) {
        this.context = context;
    }

    void updateList(final List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.workmates_recyclerview_item, parent, false);
        return new WorkmatesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        if (users.isEmpty()) {
            return 0;
        } else {
            return users.size();
        }
    }

    class WorkmatesViewHolder extends RecyclerView.ViewHolder {

        WorkmatesRecyclerviewItemBinding binding;

        public WorkmatesViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = WorkmatesRecyclerviewItemBinding.bind(itemView);
        }

        public void bind(User user) {

            //Profile Pic
            Glide.with(context)
                    .load(user.getUrlPicture())
                    .circleCrop()
                    .into(binding.workmatesProfilePic);

            //Name and lunch
            if (user.getHasBooked()) {
                binding.workmatesNameAndLunch.setText(new StringBuilder()
                        .append(user.getUsername())
                        .append(" ")
                        .append(context.getString(R.string.is_eating_at))
                        .append(" ")
                        .append(user.getPlaceBooked()));
            } else {
                binding.workmatesNameAndLunch.setText(new StringBuilder()
                        .append(user.getUsername())
                        .append(" ")
                        .append(context.getString(R.string.not_decided)));
                binding.workmatesNameAndLunch.setTextColor(Color.parseColor("#B3B3B3"));
            }
        }
    }
}
