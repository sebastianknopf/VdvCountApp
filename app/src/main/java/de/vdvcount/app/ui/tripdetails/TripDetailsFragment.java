package de.vdvcount.app.ui.tripdetails;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
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
import de.vdvcount.app.dialog.LocationWarningDialog;
import de.vdvcount.app.model.CountedStopTime;
import de.vdvcount.app.model.CountedTrip;
import de.vdvcount.app.model.PassengerCountingEvent;

public class TripDetailsFragment extends Fragment {

    private FragmentTripDetailsBinding dataBinding;
    private TripDetailsViewModel viewModel;

    private NavController navigationController;

    private CountedTripAdapter countedTripAdapter;
    private LocationWarningDialog locationWarningDialog;

    private boolean secondNextTripClick;
    private Runnable nextTripResetRunnable;
    private Handler nextTripResetHandler;

    private boolean secondCancellationClick;
    private Runnable cancellationResetRunnable;
    private Handler cancellationResetHandler;

    public static TripDetailsFragment newInstance() {
        return new TripDetailsFragment();
    }

    public TripDetailsFragment() {
        this.countedTripAdapter = new CountedTripAdapter();

        this.nextTripResetRunnable = () -> {
            this.secondNextTripClick = false;
            this.dataBinding.btnNextTrip.setText(R.string.trip_details_next_trip);
        };
        this.nextTripResetHandler = new Handler();

        this.cancellationResetRunnable = () -> {
            this.secondCancellationClick = false;
            this.dataBinding.btnCancel.setText(R.string.trip_details_cancel);
        };
        this.cancellationResetHandler = new Handler();
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

        if (args.getVehicleNumDoors() != -1) {
            Status.setInt(Status.CURRENT_VEHICLE_NUM_DOORS, args.getVehicleNumDoors());
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
                        Status.getInt(Status.CURRENT_VEHICLE_NUM_DOORS, -1),
                        Status.getInt(Status.CURRENT_START_STOP_SEQUENCE, -1)
                );
            } else {
                Logging.i(this.getClass().getName(), "Already in state COUNTING - Loading CountedTrip object");

                this.viewModel.loadCountedTrip();
            }
        }

        // must be initialized here to ensure that the context is already initialized
        this.locationWarningDialog = new LocationWarningDialog(this.getContext());

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

        // this call is additionally required for checking the GPS state
        // because when GPS is disabled while the fragment resumes, there will
        // be no call of the broadcast receiver
        //this.handleLocationAvailability();

        LocationService.getInstance().requireLocationEnabled(this.requireContext());
    }

    private void initViewEvents() {
        this.dataBinding.btnNextTrip.setOnClickListener(view -> {
            if (!this.secondNextTripClick) {
                this.secondNextTripClick = true;

                this.nextTripResetHandler.postDelayed(this.nextTripResetRunnable, 2000);
                this.dataBinding.btnNextTrip.setText(R.string.trip_details_next_trip_confirmation);
            } else {
                this.nextTripResetHandler.removeCallbacks(this.nextTripResetRunnable);

                Logging.i(this.getClass().getName(), "Switching to next connected trip ...");
                this.viewModel.closeCountedTripAndLoadNextTrip();
            }
        });

        this.dataBinding.btnCancel.setOnClickListener(view -> {
            if (!this.secondCancellationClick) {
                this.secondCancellationClick = true;

                this.cancellationResetHandler.postDelayed(this.cancellationResetRunnable, 2000);
                this.dataBinding.btnCancel.setText(R.string.trip_details_cancel_confirmation);
            } else {
                this.cancellationResetHandler.removeCallbacks(this.cancellationResetRunnable);

                Logging.i(this.getClass().getName(), "Cancelling current trip ...");
                this.viewModel.cancelCountedTrip();

                TripDetailsFragmentDirections.ActionTripDetailsFragmentToDepartureFragment action = TripDetailsFragmentDirections.actionTripDetailsFragmentToDepartureFragment();
                this.navigationController.navigate(action);
            }
        });

        this.dataBinding.btnQuit.setOnClickListener(view -> {
            CountedTrip countedTrip = this.viewModel.getCountedTrip().getValue();
            CountedStopTime lastCountedStopTime = countedTrip.getCountedStopTimes().get(countedTrip.getCountedStopTimes().size() - 1);

            TripDetailsFragmentDirections.ActionTripDetailsFragmentToTripClosingFragment action = TripDetailsFragmentDirections.actionTripDetailsFragmentToTripClosingFragment();
            action.setLastStationId(lastCountedStopTime.getStop().getParentId());
            action.setLastStationName(lastCountedStopTime.getStop().getName());

            this.resetCurrentVerticalScrollPosition();

            this.navigationController.navigate(action);
        });

        this.dataBinding.btnRetry.setOnClickListener(view -> {
            if (Status.getString(Status.STATUS, Status.Values.READY).equals(Status.Values.READY)
                            || Status.getString(Status.STATUS, Status.Values.READY).equals(Status.Values.COUNTING)) {
                Logging.i(this.getClass().getName(), "Retry requested - Trying to perform last action again");
                this.viewModel.retryLastAction();
            }
        });

        this.dataBinding.scrollView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (this.getCurrentVerticalScrollPosition() != -1) {
                this.dataBinding.scrollView.post(() -> this.dataBinding.scrollView.scrollTo(0, this.getCurrentVerticalScrollPosition()));
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

                // show 'next trip' button regarding whether the trip points to a next trip ID or not
                if (countedTrip.getNextTripId() != 0) {
                    this.dataBinding.btnNextTrip.setVisibility(View.VISIBLE);
                } else {
                    this.dataBinding.btnNextTrip.setVisibility(View.GONE);
                }
            }
        });

        LocationService locationService = LocationService.getInstance();
        locationService.getLocation().observe(this.getViewLifecycleOwner(), location -> {
            if (location != null) {
                this.viewModel.addWayPoint(location);
            }
        });

        locationService.getLocationAvailable().observe(this.getViewLifecycleOwner(), locationAvailable -> {
            if (locationAvailable != null) {
                handleLocationAvailability(locationAvailable);
            }
        });
    }

    private void showActionDialog(final CountedStopTime countedStopTime, boolean actionCountingEnabled, boolean actionAdditionalStopEnabled, boolean actionRunThroughEnabled) {
        CountingActionDialog dialog = new CountingActionDialog(this.requireContext());

        if (actionCountingEnabled) {
            dialog.setOnActionCountingClickListener(view -> {
                this.setCurrentVerticalScrollPosition();

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
                this.setCurrentVerticalScrollPosition();

                TripDetailsFragmentDirections.ActionTripDetailsFragmentToCountingFragment action = TripDetailsFragmentDirections.actionTripDetailsFragmentToCountingFragment(
                        Status.getStringArray(Status.CURRENT_COUNTED_DOOR_IDS, new String[] {})
                );

                action.setAfterStopSequence(countedStopTime.getSequence());

                this.navigationController.navigate(action);
            });
        }

        if (actionRunThroughEnabled) {
            dialog.setOnActionRunThroughListener(view -> {
                this.setCurrentVerticalScrollPosition();

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

    private int getCurrentVerticalScrollPosition() {
        return Status.getInt(Status.VIEW_TRIP_DETAILS_SCROLL_POSITION, -1);
    }

    private void resetCurrentVerticalScrollPosition() {
        Status.setInt(Status.VIEW_TRIP_DETAILS_SCROLL_POSITION, -1);
    }

    private void setCurrentVerticalScrollPosition() {
        Status.setInt(Status.VIEW_TRIP_DETAILS_SCROLL_POSITION, this.dataBinding.scrollView.getScrollY());
    }

    private void handleLocationAvailability(boolean locationAvailable) {
        if (locationAvailable) {
            LocationService.getInstance().startLocationUpdates();

            if (this.locationWarningDialog != null) {
                this.locationWarningDialog.hide();
            }
        } else {
            LocationService.getInstance().stopLocationUpdates();

            if (this.locationWarningDialog != null) {
                this.locationWarningDialog.show();
            }
        }
    }

    public enum State {
        READY,
        LOADING,
        ERROR
    }
}