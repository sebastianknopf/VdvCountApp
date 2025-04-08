package de.vdvcount.app.ui.setup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

import de.vdvcount.app.R;
import de.vdvcount.app.ScanActivity;
import de.vdvcount.app.common.Status;
import de.vdvcount.app.databinding.FragmentSetupBinding;

public class SetupFragment extends Fragment {

    private FragmentSetupBinding dataBinding;
    private SetupViewModel viewModel;

    private ActivityResultLauncher<Intent> softwareScanningLauncher;

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

        this.initViewEvents();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.softwareScanningLauncher = this.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        this.performSetup(intent.getByteArrayExtra("dataBytes"));
                    }
                });
    }

    private void initViewEvents() {
        this.dataBinding.btnContinue.setOnClickListener(sender -> {
            String[] permissions = {
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.CAMERA
            };

            Permissions.check(this.getContext(), permissions, null, null, new PermissionHandler() {
                @Override
                public void onGranted() {
                    verifyStatus();
                }

                @Override
                public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                    super.onDenied(context, deniedPermissions);
                }
            });
        });
    }

    private void verifyStatus()
    {
        if (Status.getString(Status.STATUS, Status.Values.INITIAL).equals(Status.Values.INITIAL)) {
            // app must be initialized at first - scan setup code
            Intent softwareScannerIntent = new Intent(this.requireActivity(), ScanActivity.class);
            this.softwareScanningLauncher.launch(softwareScannerIntent);
        } else {
            // navigate to main fragment here
        }

    }

    private void performSetup(byte[] setupScanResults) {
        Log.d(this.getClass().getSimpleName(), new String(setupScanResults));
        this.viewModel.setupApplication(new String(setupScanResults));
    }
}