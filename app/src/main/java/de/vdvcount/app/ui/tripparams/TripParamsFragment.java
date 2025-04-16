package de.vdvcount.app.ui.tripparams;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import de.vdvcount.app.AppActivity;
import de.vdvcount.app.R;
import de.vdvcount.app.databinding.FragmentTripParamsBinding;
import de.vdvcount.app.ui.stationselect.StationSelectFragmentArgs;
import de.vdvcount.app.ui.stationselect.StationSelectViewModel;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class TripParamsFragment extends Fragment {

    private FragmentTripParamsBinding dataBinding;
    private TripParamsViewModel viewModel;

    private NavController navigationController;

    private int currentTripId;

    public static TripParamsFragment newInstance() {
        return new TripParamsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trip_params, container, false);
        this.dataBinding.setLifecycleOwner(this.getViewLifecycleOwner());

        //this.dataBinding.lstStops.setAdapter(this.stopListAdapter);

        TripParamsFragmentArgs args = TripParamsFragmentArgs.fromBundle(this.getArguments());
        if (args.getTripId() != -1) {
            this.currentTripId = args.getTripId();
        }

        if (args.getLineName() != null && args.getTripHeadsign() != null) {
            String tripInfo = this.requireContext().getString(
                    R.string.trip_params_trip_info,
                    args.getLineName(),
                    args.getTripHeadsign()
            );

            this.dataBinding.lblTripInfo.setText(tripInfo);
        }

        return this.dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(TripParamsViewModel.class);

        this.initViewEvents();
        this.initObserverEvents();
    }

    @Override
    public void onStart() {
        super.onStart();

        AppActivity appActivity = (AppActivity) this.getActivity();
        appActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appActivity.setTitle(R.string.trip_params_title);

        this.setHasOptionsMenu(true);

        this.navigationController = appActivity.getNavigationController();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            TripParamsFragmentDirections.ActionTripParamsFragmentToDepartureFragment action = TripParamsFragmentDirections.actionTripParamsFragmentToDepartureFragment();
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

}