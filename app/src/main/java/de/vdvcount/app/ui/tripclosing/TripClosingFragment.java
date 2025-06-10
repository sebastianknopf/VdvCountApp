package de.vdvcount.app.ui.tripclosing;

import androidx.activity.OnBackPressedCallback;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import de.vdvcount.app.AppActivity;
import de.vdvcount.app.R;
import de.vdvcount.app.common.LocationService;
import de.vdvcount.app.common.Logging;
import de.vdvcount.app.databinding.FragmentTripClosingBinding;

public class TripClosingFragment extends Fragment {

    private FragmentTripClosingBinding dataBinding;
    private TripClosingViewModel viewModel;

    private NavController navigationController;

    private int lastStationId = -1;
    private String lastStationName = null;
    private boolean closeTripRequested = false;

    public static TripClosingFragment newInstance() {
        return new TripClosingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trip_closing, container, false);
        this.dataBinding.setLifecycleOwner(this.getViewLifecycleOwner());

        TripClosingFragmentArgs args = TripClosingFragmentArgs.fromBundle(this.getArguments());
        if (args.getLastStationId() != -1) {
            this.lastStationId = args.getLastStationId();
        }

        if (args.getLastStationName() != null) {
            this.lastStationName = args.getLastStationName();
        }

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

        // avoid back navigation when closing was requested
        appActivity.getOnBackPressedDispatcher().addCallback(
                this.getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (!closeTripRequested) {
                            this.setEnabled(false);

                            appActivity.getOnBackPressedDispatcher().onBackPressed();
                        }
                    }
                }
        );

        this.setHasOptionsMenu(true);

        this.navigationController = appActivity.getNavigationController();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!this.closeTripRequested) {
                TripClosingFragmentDirections.ActionTripClosingFragmentToTripDetailsFragment action = TripClosingFragmentDirections.actionTripClosingFragmentToTripDetailsFragment();
                this.navigationController.navigate(action);
            }

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void initViewEvents() {
        this.dataBinding.cbxStayInVehicle.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                this.dataBinding.btnCloseTrip.setText(R.string.trip_closing_next_trip);
            } else {
                this.dataBinding.btnCloseTrip.setText(R.string.trip_closing_close_trip);
            }
        });

        this.dataBinding.btnCloseTrip.setOnClickListener(view -> {
            Logging.i(this.getClass().getName(), String.format("Closing CountedTrip"));

            this.closeTripRequested = true;

            this.viewModel.closeCountedTrip(
                    this.dataBinding.cbxStayInVehicle.isChecked()
            );
        });

        this.dataBinding.btnRetry.setOnClickListener(view -> {
            Logging.i(this.getClass().getName(), "Retry requested - Trying to close CountedTrip again");

            this.closeTripRequested = true;

            this.viewModel.closeCountedTrip(
                    this.dataBinding.cbxStayInVehicle.isChecked()
            );
        });
    }

    private void initObserverEvents() {
        this.viewModel.getState().observe(this.getViewLifecycleOwner(), state -> {
            if (state == State.DONE || state == State.LOADING || state == State.ERROR) {
                AppActivity appActivity = (AppActivity) this.getActivity();
                appActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }

            if (state == TripClosingFragment.State.DONE) {
                // stop location updates in LocationService
                // see #40 for more information
                LocationService.stopLocationUpdates();

                final int countDownMillis = 2000;

                // count down until trip is finally closed
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    TripClosingFragmentDirections.ActionTripClosingFragmentToDepartureFragment action = TripClosingFragmentDirections.actionTripClosingFragmentToDepartureFragment();
                    action.setStationId(lastStationId);
                    action.setStationName(lastStationName);

                    this.navigationController.navigate(action);
                }, countDownMillis);

                new CountDownTimer(countDownMillis, 10) {
                    public void onTick(long millisUntilFinished) {
                        int progress = (int) (millisUntilFinished * 100 / countDownMillis);
                        dataBinding.pgbDoneCountdown.setProgress(progress);
                    }

                    public void onFinish() {
                        dataBinding.pgbDoneCountdown.setProgress(0);
                    }
                }.start();
            }
        });
    }

    public enum State {
        READY,
        LOADING,
        ERROR,
        DONE
    }
}