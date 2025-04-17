package de.vdvcount.app.ui.tripdetails;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.NavController;
import de.vdvcount.app.AppActivity;
import de.vdvcount.app.R;
import de.vdvcount.app.databinding.FragmentTripDetailsBinding;
import de.vdvcount.app.databinding.FragmentTripParamsBinding;
import de.vdvcount.app.ui.tripparams.TripParamsFragmentArgs;
import de.vdvcount.app.ui.tripparams.TripParamsViewModel;

public class TripDetailsFragment extends Fragment {

    private FragmentTripDetailsBinding dataBinding;
    private TripDetailsViewModel viewModel;

    private NavController navigationController;

    public static TripDetailsFragment newInstance() {
        return new TripDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trip_details, container, false);
        this.dataBinding.setLifecycleOwner(this.getViewLifecycleOwner());

        TripDetailsFragmentArgs args = TripDetailsFragmentArgs.fromBundle(this.getArguments());
        if (args.getTripId() != -1 && args.getStartStopSequence() != -1) {
            this.viewModel.startCountedTrip(args.getTripId(), args.getStartStopSequence());
        }

        return this.dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(TripDetailsViewModel.class);

        //this.viewModel.loadVehicles();
        //this.viewModel.loadObjectClasses();

        this.initViewEvents();
        this.initObserverEvents();
    }

    @Override
    public void onStart() {
        super.onStart();

        AppActivity appActivity = (AppActivity) this.getActivity();
        appActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        appActivity.setTitle(R.string.trip_details_title);

        this.setHasOptionsMenu(false);

        this.navigationController = appActivity.getNavigationController();
    }

    private void initViewEvents() {
        this.dataBinding.btnQuit.setOnClickListener(view -> {
            this.viewModel.closeCountedTrip();
        });
    }

    private void initObserverEvents() {

    }
}