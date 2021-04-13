package com.aki.go4lunchv2.UI;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.databinding.FragmentListBinding;
import com.aki.go4lunchv2.events.FromAdapterToFragment;
import com.aki.go4lunchv2.events.FromListToDetailEvent;
import com.aki.go4lunchv2.events.FromSearchToFragment;
import com.aki.go4lunchv2.models.Result;
import com.aki.go4lunchv2.models.ResultDetailed;
import com.aki.go4lunchv2.models.ResultDetails;
import com.aki.go4lunchv2.models.User;
import com.aki.go4lunchv2.viewmodels.RestaurantViewModel;
import com.aki.go4lunchv2.viewmodels.UserViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ListFragment extends Fragment {

    RestaurantViewModel restaurantViewModel;
    UserViewModel userViewModel;

    User localUser = User.getInstance();
    ArrayList<User> allUsers = new ArrayList<>();

    ListAdapter adapter;
    NavController navController;
    FragmentListBinding bindings;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        restaurantViewModel = new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: FRAGMENT CREATED");
        super.onViewCreated(view, savedInstanceState);
        bindings = FragmentListBinding.bind(view);
        navController = Navigation.findNavController(view);

        bindings.progressBar.show();

        adapter = new ListAdapter(this.getContext());

        bindings.restaurantsRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(),
                LinearLayoutManager.VERTICAL, false));
        bindings.restaurantsRecyclerView.setAdapter(adapter);

        restaurantViewModel.getLocalRestaurantsData().observe(getViewLifecycleOwner(), results -> {
            bindings.progressBar.hide();
            if (results != null) {
                bindings.noData.setVisibility(View.GONE);
                adapter.updateList(results, getAllUsers());
            } else {
                Log.d(TAG, "onFailure: RESTAURANTS NOT FOUNDS");
                bindings.noData.setVisibility(View.VISIBLE);
                bindings.noData.setText(R.string.error_fetching_restaurants);
            }
        });
    }

    public ArrayList<User> getAllUsers() {
        userViewModel.getAllUsers().observe(getViewLifecycleOwner(), users -> {
            allUsers.clear();
            allUsers.addAll(users);
        });
        return allUsers;
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

    @Subscribe
    public void onSearchEvent(FromSearchToFragment event) {

        ResultDetailed resultDetailed = event.result.getResult();

        Result result = new Result();

        result.setPlaceId(resultDetailed.getPlaceId());
        result.setVicinity(resultDetailed.getFormattedAddress());
        result.setPhotos(resultDetailed.getPhotos());
        result.setRating(resultDetailed.getRating());
        result.setOpeningHours(resultDetailed.getOpeningHours());
        result.setName(resultDetailed.getName());
        result.setGeometry(resultDetailed.getGeometry());

        ArrayList<Result>results = new ArrayList<>();
        results.add(result);
        adapter.updateList(results, getAllUsers());
    }

    @Subscribe
    public void onGettingDetail(FromAdapterToFragment event) {
        Log.d(TAG, "onGettingDetail: Event called successfully : \nRestaurant name : " + event.result.getName());
        ResultDetailed details = new ResultDetailed();
        restaurantViewModel.getRestaurantDetail(event.result.getPlaceId(), requireContext()).observe(getViewLifecycleOwner(), resultDetails -> {
            if(resultDetails != null) {
                details.setFormattedAddress(resultDetails.getResult().getFormattedAddress());
                details.setName(resultDetails.getResult().getName());
                details.setInternationalPhoneNumber(resultDetails.getResult().getInternationalPhoneNumber());
                details.setPhotos(resultDetails.getResult().getPhotos());
                details.setRating(resultDetails.getResult().getRating());
                details.setWebsite(resultDetails.getResult().getWebsite());

                navController.navigate(R.id.action_listFragment_to_detailFragment);
                EventBus.getDefault().postSticky(new FromListToDetailEvent(details));
            }
        });

    }
}
