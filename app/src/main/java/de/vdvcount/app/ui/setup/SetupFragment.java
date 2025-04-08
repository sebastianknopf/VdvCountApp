package de.vdvcount.app.ui.setup;

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
import de.vdvcount.app.databinding.FragmentSetupBinding;

public class SetupFragment extends Fragment {

    private FragmentSetupBinding dataBinding;
    private SetupViewModel viewModel;

    public static SetupFragment newInstance() {
        return new SetupFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.viewModel = new ViewModelProvider(this).get(SetupViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setup, container, false);
        this.dataBinding.setLifecycleOwner(this.getViewLifecycleOwner());

        this.viewModel.initAppilcation();

        return this.dataBinding.getRoot();
    }

}