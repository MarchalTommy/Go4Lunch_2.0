package com.aki.go4lunchv2.UI;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.databinding.FragmentRestaurantDetailBinding;
import com.aki.go4lunchv2.models.Result;
import com.aki.go4lunchv2.models.User;
import com.aki.go4lunchv2.viewmodels.SharedViewModel;
import com.aki.go4lunchv2.viewmodels.UserViewModel;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment {
    Context context;
    SharedViewModel sharedViewModel;
    UserViewModel userViewModel;
    DetailAdapter adapter;
    Result restaurant;
    User currentUser;
    List<User> userList = new ArrayList<>();
    FragmentRestaurantDetailBinding bindings;

    //Listener
    private final OnClickListener fabListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!currentUser.getHasBooked()) {
                //Updating local data for the UI
                currentUser.setHasBooked(true);
                currentUser.setPlaceBooked(restaurant.getName());
                DrawableCompat.setTint(bindings.detailFab.getDrawable(), getResources().getColor(R.color.secondaryColor));
            } else if (currentUser.getPlaceBooked().equals(restaurant.getName())) {
                DetailFragment.this.FABAlertDialog(1);
            } else {
                DetailFragment.this.FABAlertDialog(2);
            }
            updateAdapter();
        }
    };
    private final OnClickListener callLikeWebsiteListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.call_btn:
                    break;
                case R.id.like_btn:
                    break;
                case R.id.website_btn:
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getContext();
        sharedViewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurant_detail, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindings = FragmentRestaurantDetailBinding.bind(view);

        //Initializing the restaurant to be able to receive the data
        restaurant = new Result();
        currentUser = new User();

        //Getting data and updating the UI
        getDataFromViewModel();

        //FAB Listener
        bindings.detailFab.setOnClickListener(fabListener);

        bindings.callBtn.setOnClickListener(callLikeWebsiteListener);
        bindings.likeBtn.setOnClickListener(callLikeWebsiteListener);
        bindings.websiteBtn.setOnClickListener(callLikeWebsiteListener);

        updateAdapter();
    }

    // onDestroy to update Online data when View is changed
    // I made it this way to prevent the app from updating each time the FAB is clicked.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sharedViewModel.setCurrentUser(currentUser);
        userViewModel.updateHasBooked(currentUser.getHasBooked());
        userViewModel.updatePlaceBooked(currentUser.getPlaceBooked());
    }

    private void getDataFromViewModel() {
        //Getting current restaurant
        sharedViewModel.getRestaurant().observe(getViewLifecycleOwner(), result -> {

            //Putting the info in local var restaurant when data is received
            restaurant.setName(result.getName());
            restaurant.setRating(result.getRating());
            restaurant.setVicinity(result.getVicinity());
            restaurant.setOpeningHours(result.getOpeningHours());
            restaurant.setPhotos(result.getPhotos());

            //Updating the UI when local var restaurant have been updated
            updateRestaurantUI();
        });

        //Getting current user
        sharedViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {

            //Putting the info in local var currentUser when data is received
            currentUser.setUrlPicture(user.getUrlPicture());
            currentUser.setUsername(user.getUsername());
            currentUser.setHasBooked(user.getHasBooked());
            currentUser.setPlaceBooked(user.getPlaceBooked());
            currentUser.setUid(user.getUid());

            //FAB design (green when lunch is here, black otherwise)
            if (currentUser.getPlaceBooked().equals(restaurant.getName())) {
                DrawableCompat.setTint(bindings.detailFab.getDrawable(), getResources().getColor(R.color.secondaryColor));
            } else {
                DrawableCompat.setTint(bindings.detailFab.getDrawable(), getResources().getColor(R.color.black));
            }
        });
    }

    private void FABAlertDialog(int c) {
        switch (c) {
            case 1:
                AlertDialog dialogBuilder = new MaterialAlertDialogBuilder(context)
                        .setPositiveButton("Yes !", (dialogInterface, i) -> {

                            //Updating local data for the UI
                            currentUser.setHasBooked(false);
                            currentUser.setPlaceBooked("");
                            DrawableCompat.setTint(bindings.detailFab.getDrawable(), getResources().getColor(R.color.black));
                        })
                        .setNegativeButton("Nope, forget it !", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .setMessage("You have already told your workmates that you were eating here !\nDo you really want to cancel your lunch ?").setTitle("Cancel your lunch ?")
                        .show();
                break;
            case 2:
                AlertDialog dialogBuilder2 = new MaterialAlertDialogBuilder(context)
                        .setPositiveButton("Yes please !", (dialogInterface, i) -> {

                            //Updating local data for the UI
                            currentUser.setPlaceBooked(restaurant.getName());
                            DrawableCompat.setTint(bindings.detailFab.getDrawable(), getResources().getColor(R.color.secondaryColor));
                        })
                        .setNegativeButton("No thanks !", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .setMessage("Do you really want to change your lunch place for this restaurant ?").setTitle("Change your lunch ?")
                        .show();
                break;
        }
    }

    private void updateAdapter() {
        adapter = new DetailAdapter(context);
        bindings.detailRecyclerview.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        bindings.detailRecyclerview.setHasFixedSize(false);
        bindings.detailRecyclerview.setAdapter(adapter);

        //RecyclerView update with users having their lunch set here
        userViewModel.getAllUsers().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                userList.clear();
                for (User u : users) {
                    if (u.getPlaceBooked().equals(restaurant.getName())) {
                        userList.add(u);
                    }
                }
                adapter.updateList(userList);
            }
        });
    }

    public void updateRestaurantUI() {

        //Rating
        bindings.detailRatingBar.setNumStars(3);
        bindings.detailRatingBar.setStepSize(1);
        bindings.detailRatingBar.setIsIndicator(true);
        if (restaurant.getRating() != null) {
            //Rating binding
            if (restaurant.getRating() <= 2 && restaurant.getRating() > 0) {
                bindings.detailRatingBar.setRating(1);
            } else if (restaurant.getRating() <= 4 && restaurant.getRating() > 2) {
                bindings.detailRatingBar.setRating(2);
            } else if (restaurant.getRating() > 4) {
                bindings.detailRatingBar.setRating(3);
            } else {
                bindings.detailRatingBar.setRating(0);
            }
        }

        //Basic info
        bindings.restaurantDetailAddress.setText(restaurant.getVicinity());
        bindings.restaurantDetailName.setText(restaurant.getName());

        //Photo
        if (restaurant.getPhotos() != null) {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("https://maps.googleapis.com/maps/api/place/photo?");
            stringBuilder.append("maxheight=400&photoreference=");
            stringBuilder.append(restaurant.getPhotos().get(0).getPhotoReference());
            stringBuilder.append("&key=" + context.getResources().getString(R.string.GOOGLE_MAPS_API_KEY));
            String photoUrl = stringBuilder.toString();

            //Photo binding
            Glide.with(context)
                    .load(photoUrl)
                    .centerCrop()
                    .into(bindings.restaurantDetailPic);
        }
    }
}

