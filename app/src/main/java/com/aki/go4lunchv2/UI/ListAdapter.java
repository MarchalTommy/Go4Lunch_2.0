package com.aki.go4lunchv2.UI;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.databinding.RestaurantsRecyclerviewItemBinding;
import com.aki.go4lunchv2.events.FromListToDetailEvent;
import com.aki.go4lunchv2.models.Result;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.RestaurantViewHolder> {

    Context context;
    private List<Result> results = new ArrayList<>();

    public ListAdapter(Context context) {
        this.context = context;
    }

    void updateList(final List<Result> results) {
        this.results = results;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurants_recyclerview_item, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        holder.bind(results.get(position));

        holder.itemView.setOnClickListener(view -> {
            EventBus.getDefault().post(new FromListToDetailEvent(results.get(position)));
        });
    }

    @Override
    public int getItemCount() {
        if (results.isEmpty()) {
            return 0;
        } else {
            return results.size();
        }
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {

        RestaurantsRecyclerviewItemBinding binding;

        public RestaurantViewHolder(View view) {
            super(view);
            binding = RestaurantsRecyclerviewItemBinding.bind(view);
        }

        public void bind(Result result) {
            if (result != null) {


                if (result.getPhotos() != null) {

                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("https://maps.googleapis.com/maps/api/place/photo?");
                    stringBuilder.append("maxheight=400&photoreference=");
                    stringBuilder.append(result.getPhotos().get(0).getPhotoReference());
                    stringBuilder.append("&key=" + context.getResources().getString(R.string.GOOGLE_MAPS_API_KEY));
                    String photoUrl = stringBuilder.toString();

                    //Photo binding
                    Glide.with(context)
                            .load(photoUrl)
                            .centerCrop()
                            .into(binding.restaurantPic);
                }


                //Opening time binding
                if (result.getOpeningHours() != null) {
                    if (result.getOpeningHours().toString().contains("Closed")) {
                        binding.restaurantTime.setText(result.getOpeningHours().toString());
                        binding.restaurantTime.setTextColor(Color.RED);
                    } else {
                        binding.restaurantTime.setText(result.getOpeningHours().toString());
                        binding.restaurantTime.setTextColor(Color.parseColor("#525252"));
                    }
                } else {
                    binding.restaurantTime.setText("No info");
                }

                //Address binding
                binding.restaurantAddress.setText(result.getVicinity());

                //Name Binding
                binding.restaurantName.setText(result.getName());


                //Rating binding
                if (result.getRating() <= 2 && result.getRating() > 0) {
                    binding.restaurantRatingBar.setRating(1);
                } else if (result.getRating() <= 4 && result.getRating() > 2) {
                    binding.restaurantRatingBar.setRating(2);
                } else if (result.getRating() > 4) {
                    binding.restaurantRatingBar.setRating(3);
                } else {
                    binding.restaurantRatingBar.setRating(0);
                }

                //TODO : finir les binds (distance, combien y sont, etc...)
            }
        }
    }
}
