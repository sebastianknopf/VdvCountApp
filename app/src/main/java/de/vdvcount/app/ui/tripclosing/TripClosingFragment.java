package de.vdvcount.app.ui.tripclosing;

import androidx.activity.OnBackPressedCallback;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import de.vdvcount.app.AppActivity;
import de.vdvcount.app.R;
import de.vdvcount.app.common.Logging;
import de.vdvcount.app.databinding.FragmentTripClosingBinding;
import de.vdvcount.app.databinding.FragmentTripDetailsBinding;
import de.vdvcount.app.filesystem.FilesystemRepository;
import de.vdvcount.app.model.CountedStopTime;
import de.vdvcount.app.model.CountedTrip;
import de.vdvcount.app.ui.counting.CountingFragmentDirections;
import de.vdvcount.app.ui.tripdetails.TripDetailsFragment;
import de.vdvcount.app.ui.tripdetails.TripDetailsFragmentDirections;
import de.vdvcount.app.ui.tripdetails.TripDetailsViewModel;

public class TripClosingFragment extends Fragment {

    private FragmentTripClosingBinding dataBinding;
    private TripClosingViewModel viewModel;

    private NavController navigationController;


    private boolean closeTripRequested = false;

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

            this.viewModel.closeCountedTrip();
        });
    }

    private void initObserverEvents() {
        this.viewModel.getState().observe(this.getViewLifecycleOwner(), state -> {
            if (state == TripClosingFragment.State.DONE) {

                AppActivity appActivity = (AppActivity) this.getActivity();
                appActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    TripClosingFragmentDirections.ActionTripClosingFragmentToDepartureFragment action = TripClosingFragmentDirections.actionTripClosingFragmentToDepartureFragment();
                    this.navigationController.navigate(action);
                }, 3000);
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