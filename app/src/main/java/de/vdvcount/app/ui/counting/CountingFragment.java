package de.vdvcount.app.ui.counting;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.navigation.NavController;
import de.vdvcount.app.AppActivity;
import de.vdvcount.app.R;
import de.vdvcount.app.adapter.CountingSequenceListAdapter;
import de.vdvcount.app.databinding.FragmentCountingBinding;
import de.vdvcount.app.model.CountingSequence;

public class CountingFragment extends Fragment {

    private FragmentCountingBinding dataBinding;
    private CountingViewModel viewModel;

    private NavController navigationController;

    private int currentStopSequence;
    private String[] currentCountedDoorIds;
    private CountingSequenceListAdapter countingSequenceListAdapter;

    public static CountingFragment newInstance() {
        return new CountingFragment();
    }

    public CountingFragment() {
        this.countingSequenceListAdapter = new CountingSequenceListAdapter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_counting, container, false);
        this.dataBinding.setLifecycleOwner(this.getViewLifecycleOwner());

        this.dataBinding.lstCountingSequences.setAdapter(this.countingSequenceListAdapter);

        CountingFragmentArgs args = CountingFragmentArgs.fromBundle(this.getArguments());
        if (args.getStopName() != null) {
            this.dataBinding.lblStopInfo.setText(this.getString(R.string.counting_stop_info, args.getStopName()));
        }

        if (args.getStopSequence() != -1) {
            this.currentStopSequence = args.getStopSequence();
        }

        if (args.getCountedDoorIds() != null) {
            this.currentCountedDoorIds = args.getCountedDoorIds();
        }

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
            this.viewModel.addPassengerCountingEvent(this.currentStopSequence, this.countingSequenceListAdapter.getCountingSequenceList());


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

    public enum State {
        INITIAL,
        STORING,
        STORED
    }

}