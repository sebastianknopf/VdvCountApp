package de.vdvcount.app.ui.tripclosing;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import de.vdvcount.app.AppActivity;
import de.vdvcount.app.R;
import de.vdvcount.app.databinding.FragmentTripClosingBinding;
import de.vdvcount.app.databinding.FragmentTripDetailsBinding;
import de.vdvcount.app.ui.counting.CountingFragmentDirections;
import de.vdvcount.app.ui.tripdetails.TripDetailsViewModel;

public class TripClosingFragment extends Fragment {

    private FragmentTripClosingBinding dataBinding;
    private TripClosingViewModel viewModel;

    private NavController navigationController;

    public static TripClosingFragment newInstance() {
        return new TripClosingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trip_closing, container, false);
        this.dataBinding.setLifecycleOwner(this.getViewLifecycleOwner());

        return this.dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(TripClosingViewModel.class);
        this.dataBinding.setViewModel(this.viewModel);

        this.initViewEvents();
        this.initObserverEvents();
    }

    @Override
    public void onStart() {
        super.onStart();

        AppActivity appActivity = (AppActivity) this.getActivity();
        appActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appActivity.setTitle(R.string.trip_closing_title);

        this.setHasOptionsMenu(true);

        this.navigationController = appActivity.getNavigationController();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            TripClosingFragmentDirections.ActionTripClosingFragmentToTripDetailsFragment action = TripClosingFragmentDirections.actionTripClosingFragmentToTripDetailsFragment();
            this.navigationController.navigate(action);

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void initViewEvents() {

    }

    private void initObserverEvents() {

    }

    public enum State {
        READY,
        LOADING,
        ERROR,
        DONE
    }
}