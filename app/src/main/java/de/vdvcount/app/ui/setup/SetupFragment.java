package de.vdvcount.app.ui.setup;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setup, container, false);
        this.dataBinding.setLifecycleOwner(this.getViewLifecycleOwner());

        return this.dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(SetupViewModel.class);

        this.dataBinding.btnContinue.setOnClickListener(sender -> {
            String[] permissions = {
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.CAMERA
            };

            Permissions.check(this.getContext(), permissions, null, null, new PermissionHandler() {
                @Override
                public void onGranted() {
                    viewModel.initAppilcation();
                }

                @Override
                public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                    super.onDenied(context, deniedPermissions);
                }
            });
        });
    }
}