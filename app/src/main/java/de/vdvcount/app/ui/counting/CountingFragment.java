package de.vdvcount.app.ui.counting;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.navigation.NavController;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import de.vdvcount.app.App;
import de.vdvcount.app.AppActivity;
import de.vdvcount.app.R;
import de.vdvcount.app.adapter.CountingSequenceListAdapter;
import de.vdvcount.app.common.Logging;
import de.vdvcount.app.databinding.FragmentCountingBinding;
import de.vdvcount.app.model.CountingSequence;

public class CountingFragment extends Fragment {

    private FragmentCountingBinding dataBinding;
    private CountingViewModel viewModel;

    private NavController navigationController;

    private int currentStopSequence;
    private int currentAfterStopSequence;
    private String[] currentCountedDoorIds;
    private final CountingSequenceListAdapter countingSequenceListAdapter;

    public static CountingFragment newInstance() {
        return new CountingFragment();
    }

    public CountingFragment() {
        this.currentAfterStopSequence = -1;
        this.countingSequenceListAdapter = new CountingSequenceListAdapter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_counting, container, false);
        this.dataBinding.setLifecycleOwner(this.getViewLifecycleOwner());

        this.dataBinding.lstCountingSequences.setAdapter(this.countingSequenceListAdapter);

        CountingFragmentArgs args = CountingFragmentArgs.fromBundle(this.getArguments());
        if (args.getAfterStopSequence() == -1) {
            if (args.getStopName() != null) {
                this.dataBinding.lblStopInfo.setText(this.getString(R.string.counting_stop_info, args.getStopName()));
            }

            if (args.getStopSequence() != -1) {
                this.currentStopSequence = args.getStopSequence();
            }
        } else {
            this.dataBinding.lblStopInfo.setText(R.string.counting_additional_stop_info);

            this.currentAfterStopSequence = args.getAfterStopSequence();
        }

        this.currentCountedDoorIds = args.getCountedDoorIds();

        return this.dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(CountingViewModel.class);
        this.dataBinding.setViewModel(this.viewModel);

        List<CountingSequence> countingSequenceContainers = this.viewModel.generateCountingSequenceContainers(this.currentCountedDoorIds);
        this.countingSequenceListAdapter.setCountingSequenceList(countingSequenceContainers);

        this.initViewEvents();
        this.initObserverEvents();
    }

    @Override
    public void onStart() {
        super.onStart();

        AppActivity appActivity = (AppActivity) this.getActivity();
        appActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appActivity.setTitle(R.string.counting_title);

        this.setHasOptionsMenu(true);

        this.navigationController = appActivity.getNavigationController();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            CountingFragmentDirections.ActionCountingFragmentToTripDetailsFragment action = CountingFragmentDirections.actionCountingFragmentToTripDetailsFragment();
            this.navigationController.navigate(action);

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void initViewEvents() {
        this.dataBinding.btnSave.setOnClickListener(view -> {

            this.dataBinding.btnSave.setFocusable(true);
            this.dataBinding.btnSave.requestFocus();
            this.dataBinding.btnSave.setFocusable(false);

            String[] permissions = {
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.CAMERA
            };

            Permissions.check(this.getContext(), permissions, null, null, new PermissionHandler() {
                @Override
                public void onGranted() {
                    addPassengerCountingEvent();
                }

                @Override
                public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                    super.onDenied(context, deniedPermissions);

                    Logging.w(getClass().getName(), "Location permission refused");

                    addPassengerCountingEvent();
                }
            });
        });
    }

    private void initObserverEvents() {
        this.viewModel.getState().observe(this.getViewLifecycleOwner(), state -> {
            if (state == State.STORED) {
                CountingFragmentDirections.ActionCountingFragmentToTripDetailsFragment action = CountingFragmentDirections.actionCountingFragmentToTripDetailsFragment();
                this.navigationController.navigate(action);
            }
        });
    }

    private void addPassengerCountingEvent() {
        if (this.currentAfterStopSequence == -1) {
            this.viewModel.addPassengerCountingEvent(
                    this.currentStopSequence,
                    this.countingSequenceListAdapter.getCountingSequenceList()
            );
        } else {
            this.viewModel.addUnmatchedPassengerCountingEvent(
                    this.currentAfterStopSequence,
                    this.countingSequenceListAdapter.getCountingSequenceList()
            );
        }
    }

    public enum State {
        INITIAL,
        STORING,
        STORED
    }

}