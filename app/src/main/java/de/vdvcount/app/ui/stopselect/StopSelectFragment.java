package de.vdvcount.app.ui.stopselect;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.NavController;
import de.vdvcount.app.AppActivity;
import de.vdvcount.app.R;
import de.vdvcount.app.adapter.StopListAdapter;
import de.vdvcount.app.databinding.FragmentStopSelectBinding;
import de.vdvcount.app.ui.setup.SetupViewModel;

public class StopSelectFragment extends Fragment {

    private FragmentStopSelectBinding dataBinding;
    private StopSelectViewModel viewModel;

    private NavController navigationController;

    private StopListAdapter stopListAdapter;

    public static StopSelectFragment newInstance() {
        return new StopSelectFragment();
    }

    public StopSelectFragment() {
        this.stopListAdapter = new StopListAdapter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_stop_select, container, false);
        this.dataBinding.setLifecycleOwner(this.getViewLifecycleOwner());

        this.dataBinding.lstStops.setAdapter(this.stopListAdapter);

        return this.dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        super.onViewCreated(view, savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(StopSelectViewModel.class);

        this.initViewEvents();
        this.initObserverEvents();
    }

    @Override
    public void onStart() {
        super.onStart();

        AppActivity appActivity = (AppActivity) this.getActivity();
        appActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appActivity.setTitle(R.string.stop_select_title);

        this.navigationController = appActivity.getNavigationController();
    }

    @Override
    public void onResume() {
        super.onResume();

        this.dataBinding.edtStopName.requestFocus();
    }

    private void initViewEvents() {
        this.dataBinding.edtStopName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                viewModel.loadStations(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        this.stopListAdapter.setOnItemClickListener(station -> {
            this.navigationController.navigate(R.id.action_stopSelectFragment_to_departureFragment);
        });
    }

    private void initObserverEvents() {
        this.viewModel.getStations().observe(this.getViewLifecycleOwner(), stations -> {
            this.stopListAdapter.setStationList(stations);
        });
    }
}