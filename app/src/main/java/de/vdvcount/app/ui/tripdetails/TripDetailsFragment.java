package de.vdvcount.app.ui.tripdetails;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.NavController;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

import de.vdvcount.app.AppActivity;
import de.vdvcount.app.R;
import de.vdvcount.app.adapter.CountedTripAdapter;
import de.vdvcount.app.common.LocationService;
import de.vdvcount.app.common.Logging;
import de.vdvcount.app.common.Status;
import de.vdvcount.app.databinding.FragmentTripDetailsBinding;
import de.vdvcount.app.dialog.CountingActionDialog;
import de.vdvcount.app.model.CountedStopTime;
import de.vdvcount.app.model.CountedTrip;
import de.vdvcount.app.model.PassengerCountingEvent;

public class TripDetailsFragment extends Fragment {

    private FragmentTripDetailsBinding dataBinding;
    private TripDetailsViewModel viewModel;

    private NavController navigationController;

    private CountedTripAdapter countedTripAdapter;

    public static TripDetailsFragment newInstance() {
        return new TripDetailsFragment();
    }

    public TripDetailsFragment() {
        this.countedTripAdapter = new CountedTripAdapter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trip_details, container, false);
        this.dataBinding.setLifecycleOwner(this.getViewLifecycleOwner());

        this.dataBinding.lstCountedStopTimes.setAdapter(this.countedTripAdapter);

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

    @Override
    public void onResume() {
        super.onResume();

        LocationService.startLocationUpdates();
    }

    private void initViewEvents() {
        // this viewEvent can only be initialized with a countedTrip object present in the viewModel
        // hence, the initialisation is done in initObserverEvents()
        // see #20 for more information
        /*this.countedTripAdapter.setOnItemClickListener(countedStopTime -> {
            this.showActionDialog(countedStopTime, true, true);
        });*/

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
                this.countedTripAdapter.setCountedTrip(countedTrip);

                this.countedTripAdapter.setOnItemClickListener(countedStopTime -> {
                    CountedStopTime lastCountedStopTime = countedTrip.getCountedStopTimes().get(
                            countedTrip.getCountedStopTimes().size() - 1
                    );

                    // additional stops after the last stops are not allowed
                    if (countedStopTime.getSequence() < lastCountedStopTime.getSequence()) {
                        if (!countedStopTime.getPassengerCountingEvents().isEmpty()) {
                            PassengerCountingEvent passengerCountingEvent = countedStopTime.getPassengerCountingEvents().get(0);

                            if (passengerCountingEvent.isRunThrough()) {
                                this.showActionDialog(countedStopTime, false, true, false);
                            } else {
                                this.showActionDialog(countedStopTime, true, true, false);
                            }
                        } else {
                            this.showActionDialog(countedStopTime, true, true, true);
                        }
                    } else {
                        this.showActionDialog(countedStopTime, true, false, false);
                    }
                });
            }
        });

        LocationService.getLocation().observe(this.getViewLifecycleOwner(), location -> {
            if (location != null) {
                // still do nothing here... this is optional for recording WayPoints later
            }
        });
    }

    private void showActionDialog(final CountedStopTime countedStopTime, boolean actionCountingEnabled, boolean actionAdditionalStopEnabled, boolean actionRunThroughEnabled) {
        CountingActionDialog dialog = new CountingActionDialog(this.requireContext());

        if (actionCountingEnabled) {
            dialog.setOnActionCountingClickListener(view -> {
                TripDetailsFragmentDirections.ActionTripDetailsFragmentToCountingFragment action = TripDetailsFragmentDirections.actionTripDetailsFragmentToCountingFragment(
                        Status.getStringArray(Status.CURRENT_COUNTED_DOOR_IDS, new String[] {})
                );

                action.setStopName(countedStopTime.getStop().getName());
                action.setStopSequence(countedStopTime.getSequence());

                this.navigationController.navigate(action);
            });
        }

        if (actionAdditionalStopEnabled) {
            dialog.setOnActionAdditionalStopClickListener(view -> {
                TripDetailsFragmentDirections.ActionTripDetailsFragmentToCountingFragment action = TripDetailsFragmentDirections.actionTripDetailsFragmentToCountingFragment(
                        Status.getStringArray(Status.CURRENT_COUNTED_DOOR_IDS, new String[] {})
                );

                action.setAfterStopSequence(countedStopTime.getSequence());

                this.navigationController.navigate(action);
            });
        }

        if (actionRunThroughEnabled) {
            dialog.setOnActionRunThroughListener(view -> {
                String[] permissions = {
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.CAMERA
                };

                Permissions.check(this.getContext(), permissions, null, null, new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        viewModel.addRunThroughPassengerCountingEvent(countedStopTime.getSequence());
                    }

                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        super.onDenied(context, deniedPermissions);

                        Logging.w(getClass().getName(), "Location permission refused");

                        viewModel.addRunThroughPassengerCountingEvent(countedStopTime.getSequence());
                    }
                });
            });
        }

        dialog.show();
    }

    public enum State {
        READY,
        LOADING,
        ERROR
    }
}