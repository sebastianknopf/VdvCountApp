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
import androidx.navigation.NavDirections;

import de.vdvcount.app.AppActivity;
import de.vdvcount.app.R;
import de.vdvcount.app.adapter.CountedStopTimeListAdapter;
import de.vdvcount.app.common.Logging;
import de.vdvcount.app.common.Status;
import de.vdvcount.app.databinding.FragmentTripDetailsBinding;
import de.vdvcount.app.model.CountedStopTime;
import de.vdvcount.app.model.CountedTrip;

public class TripDetailsFragment extends Fragment {

    private FragmentTripDetailsBinding dataBinding;
    private TripDetailsViewModel viewModel;

    private NavController navigationController;

    private CountedStopTimeListAdapter countedStopTimeListAdapter;

    public static TripDetailsFragment newInstance() {
        return new TripDetailsFragment();
    }

    public TripDetailsFragment() {
        this.countedStopTimeListAdapter = new CountedStopTimeListAdapter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trip_details, container, false);
        this.dataBinding.setLifecycleOwner(this.getViewLifecycleOwner());

        this.dataBinding.lstCountedStopTimes.setAdapter(this.countedStopTimeListAdapter);

        TripDetailsFragmentArgs args = TripDetailsFragmentArgs.fromBundle(this.getArguments());
        if (args.getTripId() != -1) {
            Status.setInt(Status.CURRENT_TRIP_ID, args.getTripId());
        }

        if (args.getVehicleId() != null && !args.getVehicleId().isEmpty()) {
            Status.setString(Status.CURRENT_VEHICLE_ID, args.getVehicleId());
        }

        if (args.getStartStopSequence() != -1) {
            Status.setInt(Status.CURRENT_START_STOP_SEQUENCE, args.getStartStopSequence());
        }

        if (args.getCountedDoorIds() != null) {
            Status.setStringArray(Status.CURRENT_COUNTED_DOOR_IDS, args.getCountedDoorIds());
        }

        return this.dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(TripDetailsViewModel.class);
        this.dataBinding.setViewModel(this.viewModel);

        if (Status.getInt(Status.CURRENT_TRIP_ID, -1) != -1 && Status.getInt(Status.CURRENT_START_STOP_SEQUENCE, -1) != -1) {
            if (Status.getString(Status.STATUS, Status.Values.READY).equals(Status.Values.READY)) {
                Logging.i(this.getClass().getName(), String.format("Starting new CountedTrip (trip ID %d) object", Status.getInt(Status.CURRENT_TRIP_ID, -1)));

                this.viewModel.startCountedTrip(
                        Status.getInt(Status.CURRENT_TRIP_ID, -1),
                        Status.getString(Status.CURRENT_VEHICLE_ID, ""),
                        Status.getInt(Status.CURRENT_START_STOP_SEQUENCE, -1)
                );
            } else {
                Logging.i(this.getClass().getName(), "Already in state COUNTING - Loading CountedTrip object");

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
        this.countedStopTimeListAdapter.setOnItemClickListener(countedStopTime -> {
            TripDetailsFragmentDirections.ActionTripDetailsFragmentToCountingFragment action = TripDetailsFragmentDirections.actionTripDetailsFragmentToCountingFragment(
                countedStopTime.getStop().getName(),
                countedStopTime.getSequence(),
                Status.getStringArray(Status.CURRENT_COUNTED_DOOR_IDS, new String[] {})
            );

            this.navigationController.navigate(action);
        });

        this.dataBinding.btnQuit.setOnClickListener(view -> {
            CountedTrip countedTrip = this.viewModel.getCountedTrip().getValue();
            CountedStopTime lastCountedStopTime = countedTrip.getCountedStopTimes().get(countedTrip.getCountedStopTimes().size() - 1);

            TripDetailsFragmentDirections.ActionTripDetailsFragmentToTripClosingFragment action = TripDetailsFragmentDirections.actionTripDetailsFragmentToTripClosingFragment();
            action.setLastStationId(lastCountedStopTime.getStop().getParentId());
            action.setLastStationName(lastCountedStopTime.getStop().getName());

            this.navigationController.navigate(action);
        });

        this.dataBinding.btnRetry.setOnClickListener(view -> {
            if (Status.getString(Status.STATUS, Status.Values.READY).equals(Status.Values.READY)) {
                Logging.i(this.getClass().getName(), "Retry requested - Trying to load CountedTrip again");

                this.viewModel.startCountedTrip(
                        Status.getInt(Status.CURRENT_TRIP_ID, -1),
                        Status.getString(Status.CURRENT_VEHICLE_ID, ""),
                        Status.getInt(Status.CURRENT_START_STOP_SEQUENCE, -1)
                );
            }
        });
    }

    private void initObserverEvents() {
        this.viewModel.getCountedTrip().observe(this.getViewLifecycleOwner(), countedTrip -> {
            if (countedTrip != null) {
                this.countedStopTimeListAdapter.setCountedStopTimeList(countedTrip.getCountedStopTimes());
            }
        });
    }

    public enum State {
        READY,
        LOADING,
        ERROR
    }
}