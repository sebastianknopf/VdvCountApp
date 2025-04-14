package de.vdvcount.app.ui.stopselect;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.navigation.NavController;
import androidx.navigation.NavDirections;

import de.vdvcount.app.AppActivity;
import de.vdvcount.app.R;
import de.vdvcount.app.adapter.StopListAdapter;
import de.vdvcount.app.databinding.FragmentStopSelectBinding;

public class StopSelectFragment extends Fragment {

    private FragmentStopSelectBinding dataBinding;
    private StopSelectViewModel viewModel;

    private NavController navigationController;

    private String currentStopName;
    private Runnable stopInputDebounceRunnable;
    private Handler stopInputDebounceHandler;
    private StopListAdapter stopListAdapter;

    public static StopSelectFragment newInstance() {
        return new StopSelectFragment();
    }

    public StopSelectFragment() {
        this.stopInputDebounceHandler = new Handler();
        this.stopListAdapter = new StopListAdapter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_stop_select, container, false);
        this.dataBinding.setLifecycleOwner(this.getViewLifecycleOwner());

        this.dataBinding.lstStops.setAdapter(this.stopListAdapter);

        StopSelectFragmentArgs args = StopSelectFragmentArgs.fromBundle(this.getArguments());
        if (args.getStopName() != null) {
            this.dataBinding.edtStopName.setText(args.getStopName());
            this.currentStopName = args.getStopName();
        }

        return this.dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        super.onViewCreated(view, savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(StopSelectViewModel.class);

        if (this.currentStopName != null) {
            this.viewModel.loadStationsByLookupName(this.currentStopName);
        }

        this.initViewEvents();
        this.initObserverEvents();
    }

    @Override
    public void onStart() {
        super.onStart();

        AppActivity appActivity = (AppActivity) this.getActivity();
        appActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appActivity.setTitle(R.string.stop_select_title);

        this.setHasOptionsMenu(true);

        this.navigationController = appActivity.getNavigationController();
    }

    @Override
    public void onResume() {
        super.onResume();

        // open keyboard and focus search edit text
        this.dataBinding.edtStopName.requestFocus();
        InputMethodManager imm = (InputMethodManager) this.requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(this.dataBinding.edtStopName, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.navigationController.navigate(R.id.action_stopSelectFragment_to_departureFragment);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void initViewEvents() {
        this.dataBinding.edtStopName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (stopInputDebounceRunnable != null) {
                    stopInputDebounceHandler.removeCallbacks(stopInputDebounceRunnable);
                }

                stopInputDebounceRunnable = () -> {
                    String lookupName = editable.toString().trim();

                    if (!lookupName.isEmpty()) {
                        viewModel.loadStationsByLookupName(lookupName);
                    }
                };

                stopInputDebounceHandler.postDelayed(stopInputDebounceRunnable, 300);
            }
        });

        this.stopListAdapter.setOnItemClickListener(station -> {
            StopSelectFragmentDirections.ActionStopSelectFragmentToDepartureFragment action = StopSelectFragmentDirections.actionStopSelectFragmentToDepartureFragment();
            action.setStopId(station.getId());
            action.setStopName(station.getName());

            this.navigationController.navigate(action);
        });
    }

    private void initObserverEvents() {
        this.viewModel.getStations().observe(this.getViewLifecycleOwner(), stations -> {
            this.stopListAdapter.setStationList(stations);
        });
    }
}