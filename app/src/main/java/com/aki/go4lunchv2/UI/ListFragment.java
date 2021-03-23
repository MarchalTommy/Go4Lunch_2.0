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
import com.aki.go4lunchv2.events.FromListToDetailEvent;
import com.aki.go4lunchv2.models.Result;
import com.aki.go4lunchv2.viewmodels.RestaurantViewModel;
import com.aki.go4lunchv2.viewmodels.SharedViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class ListFragment extends Fragment {

    SharedViewModel viewModel;
    RestaurantViewModel restaurantViewModel;

    ListAdapter adapter;
    NavController navController;
    FragmentListBinding bindings;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);
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

        //Observer for the restaurant list LiveData
        restaurantViewModel.getRestaurantsAround("48.866062,2.345450", this.getContext()).observe(this.getActivity(), new Observer<ArrayList<Result>>() {
            @Override
            public void onChanged(ArrayList<Result> results) {
                bindings.progressBar.setVisibility(View.GONE);
                if (results != null) {
                    bindings.noData.setVisibility(View.GONE);
                    adapter.updateList(results);
                } else {
                    Log.d(TAG, "onFailure: RESTAURANTS NOT FOUNDS");
                    bindings.noData.setVisibility(View.VISIBLE);
                    bindings.noData.setText("An error occured. Please retry later.");
                }
            }
        });
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
    public void onGettingDetail(FromListToDetailEvent event) {
        Log.d(TAG, "onGettingDetail: Event called successfully : \nRestaurant name : " + event.result.getName());
        viewModel.setRestaurant(event.result);
        navController.navigate(R.id.action_listFragment_to_detailFragment);
    }
}
