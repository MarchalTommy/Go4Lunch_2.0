package com.aki.go4lunchv2.UI;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.databinding.DetailRecyclerviewItemBinding;
import com.aki.go4lunchv2.databinding.WorkmatesRecyclerviewItemBinding;
import com.aki.go4lunchv2.models.User;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.DetailViewHolder> {

    Context context;
    private List<User> users = new ArrayList<>();

    public DetailAdapter(Context context) {
        this.context = context;
    }

    void updateList(final List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_recyclerview_item, parent, false);
        return new DetailViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
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

    class DetailViewHolder extends RecyclerView.ViewHolder {

        DetailRecyclerviewItemBinding binding;

        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DetailRecyclerviewItemBinding.bind(itemView);
        }

        public void bind(User user) {

            binding.restaurantDetailWorkmateText.setText(user.getUsername() + " is eating here !");

            Glide.with(context)
                    .load(user.getUrlPicture())
                    .into(binding.restaurantDetailWorkmatePic);
        }
    }
}