package com.aki.go4lunchv2.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.databinding.FragmentRestaurantDetailBinding;
import com.aki.go4lunchv2.events.FromListToDetailEvent;
import com.aki.go4lunchv2.events.YourLunchEvent;
import com.aki.go4lunchv2.models.Result;
import com.aki.go4lunchv2.models.User;
import com.aki.go4lunchv2.viewmodels.UserViewModel;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class DetailFragment extends Fragment {

    Context context;
    UserViewModel userViewModel;
    DetailAdapter adapter;
    Result restaurant;
    List<User> userList = new ArrayList<>();
    FragmentRestaurantDetailBinding bindings;

    User localUser = User.getInstance();

    //Listener
    private final OnClickListener fabListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!localUser.getHasBooked()) {
                //Updating data for the UI
                userViewModel.updateHasBooked(true);
                userViewModel.updatePlaceBooked(restaurant.getName());
                updateAdapter();
                DrawableCompat.setTint(bindings.detailFab.getDrawable(), getResources().getColor(R.color.secondaryColor));
            } else if (localUser.getPlaceBooked().equals(restaurant.getName())) {
                DetailFragment.this.FABAlertDialog(1);
            } else {
                DetailFragment.this.FABAlertDialog(2);
            }
        }
    };
    @SuppressLint("NonConstantResourceId")
    private final OnClickListener callLikeWebsiteListener = view -> {
        switch (view.getId()) {
            case R.id.call_btn:
                break;
            case R.id.like_btn:
                break;
            case R.id.website_btn:
                break;
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getContext();
        //sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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

        //Getting data and updating the UI
        getDataFromViewModel();

        initAdapter();

        //FAB Listener
        bindings.detailFab.setOnClickListener(fabListener);

        bindings.callBtn.setOnClickListener(callLikeWebsiteListener);
        bindings.likeBtn.setOnClickListener(callLikeWebsiteListener);
        bindings.websiteBtn.setOnClickListener(callLikeWebsiteListener);

    }

    private void initAdapter() {
        adapter = new DetailAdapter(context);
        bindings.detailRecyclerview.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        bindings.detailRecyclerview.setHasFixedSize(false);
        bindings.detailRecyclerview.setAdapter(adapter);

        //RecyclerView update with users having their lunch set here
        userViewModel.getAllUsers().observe(getViewLifecycleOwner(), users -> {
            userList.clear();
            for (User u : users) {
                if (u.getPlaceBooked().equals(restaurant.getName())) {
                    userList.add(u);
                }
            }
            adapter.updateList(userList);
        });

    }

    private void updateAdapter(){
        userViewModel.getUsersOnPlace(restaurant.getName()).observe(getViewLifecycleOwner(), users -> adapter.updateList(users));
    }

    // I made it this way to prevent the app from updating each time the FAB is clicked.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        localUser.setHasBooked(Objects.requireNonNull(userViewModel.getCurrentUser().getValue()).getHasBooked());
        localUser.setPlaceBooked(userViewModel.getCurrentUser().getValue().getPlaceBooked());
    }

    private void getDataFromViewModel() {
        //Getting current user
        userViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {

            //Putting the info in local var currentUser when data is received
            localUser.setUrlPicture(user.getUrlPicture());
            localUser.setUsername(user.getUsername());
            localUser.setHasBooked(user.getHasBooked());
            localUser.setPlaceBooked(user.getPlaceBooked());
            localUser.setUid(user.getUid());

            //FAB design (green when lunch is here, black otherwise)
            if (localUser.getPlaceBooked().equals(restaurant.getName())) {
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
                            //Updating data for the UI
                            userViewModel.updateHasBooked(false);
                            userViewModel.updatePlaceBooked("");
                            updateAdapter();
                            DrawableCompat.setTint(bindings.detailFab.getDrawable(), getResources().getColor(R.color.black));
                        })
                        .setNegativeButton("Nope, forget it !", (dialogInterface, i) -> dialogInterface.dismiss())
                        .setMessage("You have already told your workmates that you were eating here !\nDo you really want to cancel your lunch ?").setTitle("Cancel your lunch ?")
                        .show();
                break;
            case 2:
                AlertDialog dialogBuilder2 = new MaterialAlertDialogBuilder(context)
                        .setPositiveButton("Yes please !", (dialogInterface, i) -> {

                            //Updating data for the UI
                            userViewModel.updatePlaceBooked(restaurant.getName());
                            updateAdapter();
                            DrawableCompat.setTint(bindings.detailFab.getDrawable(), getResources().getColor(R.color.secondaryColor));
                        })
                        .setNegativeButton("No thanks !", (dialogInterface, i) -> dialogInterface.dismiss())
                        .setMessage("Do you really want to change your lunch place for this restaurant ?").setTitle("Change your lunch ?")
                        .show();
                break;
        }
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

            String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?" +
                    "maxheight=400&photoreference=" +
                    restaurant.getPhotos().get(0).getPhotoReference() +
                    "&key=" + context.getResources().getString(R.string.GOOGLE_MAPS_API_KEY);

            //Photo binding
            Glide.with(context)
                    .load(photoUrl)
                    .centerCrop()
                    .into(bindings.restaurantDetailPic);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void fromMenu(YourLunchEvent event){
        restaurant.setName(event.result.getName());
        restaurant.setVicinity(event.result.getVicinity());
        restaurant.setRating(event.result.getRating());
        restaurant.setPhotos(event.result.getPhotos());

        updateRestaurantUI();

        EventBus.getDefault().removeStickyEvent(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onDetailFragment(FromListToDetailEvent event) {
        Log.d(TAG, "onDetailFragment: \n" + event.result.getName());

        restaurant.setName(event.result.getName());
        restaurant.setPhotos(event.result.getPhotos());
        restaurant.setRating(event.result.getRating());
        restaurant.setVicinity(event.result.getVicinity());

        updateRestaurantUI();

        EventBus.getDefault().removeStickyEvent(this);
    }
}

