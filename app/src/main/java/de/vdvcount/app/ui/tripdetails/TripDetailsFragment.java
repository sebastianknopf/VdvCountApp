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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.navigation.NavController;
import de.vdvcount.app.AppActivity;
import de.vdvcount.app.R;
import de.vdvcount.app.common.Status;
import de.vdvcount.app.databinding.FragmentTripDetailsBinding;
import de.vdvcount.app.databinding.FragmentTripParamsBinding;
import de.vdvcount.app.model.CountedStopTime;
import de.vdvcount.app.model.CountedTrip;
import de.vdvcount.app.ui.tripparams.TripParamsFragmentArgs;
import de.vdvcount.app.ui.tripparams.TripParamsViewModel;

public class TripDetailsFragment extends Fragment {

    private FragmentTripDetailsBinding dataBinding;
    private TripDetailsViewModel viewModel;

    private NavController navigationController;

    private List<String> currentCountedDoors;

    public static TripDetailsFragment newInstance() {
        return new TripDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trip_details, container, false);
        this.dataBinding.setLifecycleOwner(this.getViewLifecycleOwner());

        TripDetailsFragmentArgs args = TripDetailsFragmentArgs.fromBundle(this.getArguments());
        if (args.getTripId() != -1) {
            Status.setInt(Status.CURRENT_TRIP_ID, args.getTripId());
        }

        if (!args.getVehicleId().isEmpty()) {
            Status.setString(Status.CURRENT_VEHICLE_ID, args.getVehicleId());
        }

        if (args.getStartStopSequence() != -1) {
            Status.setInt(Status.CURRENT_START_STOP_SEQUENCE, args.getStartStopSequence());
        }

        if (args.getCountedDoorIds() != null) {
            this.currentCountedDoors = Arrays.asList(args.getCountedDoorIds());
        }

        return this.dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(TripDetailsViewModel.class);

        if (Status.getInt(Status.CURRENT_TRIP_ID, -1) != -1 && Status.getInt(Status.CURRENT_START_STOP_SEQUENCE, -1) != -1) {
            if (Status.getString(Status.STATUS, Status.Values.READY).equals(Status.Values.READY)) {
                this.viewModel.startCountedTrip(
                        Status.getInt(Status.CURRENT_TRIP_ID, -1),
                        Status.getString(Status.CURRENT_VEHICLE_ID, ""),
                        Status.getInt(Status.CURRENT_START_STOP_SEQUENCE, -1)
                );
            } else {
                this.viewModel.loadCountedTrip();
            }
        }

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
            CountedTrip countedTrip = this.viewModel.getCountedTrip().getValue();

            this.viewModel.closeCountedTrip();

            TripDetailsFragmentDirections.ActionTripDetailsFragmentToDepartureFragment action = TripDetailsFragmentDirections.actionTripDetailsFragmentToDepartureFragment();
            if (countedTrip != null) {
                CountedStopTime countedStopTime = countedTrip.getCountedStopTimes().get(countedTrip.getCountedStopTimes().size() - 1);
                action.setStationId(countedStopTime.getStop().getParentId());
                action.setStationName(countedStopTime.getStop().getName());
            }

            this.navigationController.navigate(action);
        });
    }

    private void initObserverEvents() {
        this.viewModel.getCountedTrip().observe(this.getViewLifecycleOwner(), countedTrip -> {
            if (countedTrip != null) {
                // TODO: set countedTripAdapter here
            }
        });
    }
}