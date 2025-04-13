package de.vdvcount.app.ui.departure;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.vdvcount.app.AppActivity;
import de.vdvcount.app.R;
import de.vdvcount.app.databinding.FragmentDepartureBinding;
import de.vdvcount.app.ui.stopselect.StopSelectViewModel;

public class DepartureFragment extends Fragment {

    private FragmentDepartureBinding dataBinding;
    private DepartureViewModel viewModel;

    private NavController navigationController;

    public static DepartureFragment newInstance() {
        return new DepartureFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_departure, container, false);
        this.dataBinding.setLifecycleOwner(this.getViewLifecycleOwner());

        return this.dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        super.onViewCreated(view, savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(DepartureViewModel.class);

        this.initViewEvents();
        this.initObserverEvents();
    }

    @Override
    public void onStart() {
        super.onStart();

        AppActivity appActivity = (AppActivity) this.getActivity();
        appActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        appActivity.setTitle(R.string.departure_title);

        this.setHasOptionsMenu(false);

        this.navigationController = appActivity.getNavigationController();
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    private void initViewEvents() {
        this.dataBinding.edtStopName.setOnClickListener(view -> {
            this.navigationController.navigate(R.id.action_departureFragment_to_stopSelectFragment);
        });
    }

    private void initObserverEvents() {
        this.viewModel.getDepartures().observe(this.getViewLifecycleOwner(), departures -> {

        });
    }
}