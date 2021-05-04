package com.aki.go4lunchv2.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.databinding.RestaurantsRecyclerviewItemBinding;
import com.aki.go4lunchv2.events.FromAdapterToFragment;
import com.aki.go4lunchv2.models.Result;
import com.aki.go4lunchv2.models.User;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.RestaurantViewHolder> {

    Context context;
    private List<Result> results = new ArrayList<>();
    private List<User> allUsers = new ArrayList<>();
    private final User localUser = User.getInstance();

    public ListAdapter(Context context) {
        this.context = context;
    }

    void updateList(final List<Result> results, final List<User> users) {
        this.results = results;
        this.allUsers = users;
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
            EventBus.getDefault().post(new FromAdapterToFragment(results.get(position)));
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

        @SuppressLint("SetTextI18n")
        public void bind(Result result) {
            if (result != null) {


                if (result.getPhotos() != null) {

                    String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?" +
                            "maxheight=400&photoreference=" +
                            result.getPhotos().get(0).getPhotoReference() +
                            "&key=" + context.getResources().getString(R.string.GOOGLE_MAPS_API_KEY);

                    //Photo binding
                    Glide.with(context)
                            .load(photoUrl)
                            .centerCrop()
                            .into(binding.restaurantPic);
                } else {
                    Glide.with(context)
                            .load(R.drawable.restaurant_default)
                            .centerCrop()
                            .into(binding.restaurantPic);
                }


                //Opening time binding
                if (result.getOpeningHours() != null) {
                    if (result.getOpeningHours().getOpenNow()) {
                        binding.restaurantTime.setText(R.string.open_now);
                        binding.restaurantTime.setTextColor(Color.parseColor("#525252"));
                    } else {
                        binding.restaurantTime.setText(R.string.closed);
                        binding.restaurantTime.setTextColor(Color.RED);
                    }
                } else {
                    binding.restaurantTime.setText(R.string.no_info);
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

                //Workmates eating here
                int onPlace = 0;
                for (User u : allUsers) {
                    if (u.getPlaceBooked().equals(result.getName())) {
                        onPlace++;
                    }
                }
                binding.numberOfWorkmatesEatingHere.setText("" + onPlace);

                //Distance to the restaurant
                float[] distanceTo = new float[1];
                ArrayList<String> userLocation = new ArrayList<>(Arrays.asList(localUser.getLocation().split(",")));
                double lat, lng;
                lat = Double.parseDouble(userLocation.get(0));
                lng = Double.parseDouble(userLocation.get(1));
                Location.distanceBetween(lat, lng, result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng(), distanceTo);
                String string = String.valueOf((int) distanceTo[0] + " m");

                binding.restaurantDistance.setText(string);

            }
        }
    }
}
