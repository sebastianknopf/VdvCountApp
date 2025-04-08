package de.vdvcount.app.ui.stopselect;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.vdvcount.app.R;
import de.vdvcount.app.databinding.FragmentStopSelectBinding;
import de.vdvcount.app.ui.setup.SetupViewModel;

public class StopSelectFragment extends Fragment {

    private FragmentStopSelectBinding dataBinding;
    private StopSelectViewModel viewModel;

    public static StopSelectFragment newInstance() {
        return new StopSelectFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_stop_select, container, false);
        this.dataBinding.setLifecycleOwner(this.getViewLifecycleOwner());

        return this.dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        super.onViewCreated(view, savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(StopSelectViewModel.class);

        this.initViewEvents();
    }

    private void initViewEvents() {

    }
}