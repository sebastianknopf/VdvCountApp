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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

import androidx.navigation.NavController;
import de.vdvcount.app.AppActivity;
import de.vdvcount.app.R;
import de.vdvcount.app.ScanActivity;
import de.vdvcount.app.common.Logging;
import de.vdvcount.app.common.Status;
import de.vdvcount.app.databinding.FragmentSetupBinding;

public class SetupFragment extends Fragment {

    private FragmentSetupBinding dataBinding;
    private SetupViewModel viewModel;

    private NavController navigationController;

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
        this.initObserverEvents();
    }

    @Override
    public void onStart() {
        super.onStart();

        AppActivity appActivity = (AppActivity) this.getActivity();
        appActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        appActivity.setTitle(R.string.setup_title);

        this.navigationController = appActivity.getNavigationController();

        Logging.d(this.getClass().getName(), "Starting SetupFragment");

        if (Status.getString(Status.STATUS, Status.Values.INITIAL).equals(Status.Values.READY)) {
            Logging.i(this.getClass().getName(), "Current status is READY - Redirecting to DepartureFragment");

            // navigate to main fragment here
            SetupFragmentDirections.ActionSetupFragmentToDepartureFragment action = SetupFragmentDirections.actionSetupFragmentToDepartureFragment();
            this.navigationController.navigate(action);
        } else if (Status.getString(Status.STATUS, Status.Values.INITIAL).equals(Status.Values.COUNTING)) {
            Logging.i(this.getClass().getName(), "Current status is COUNTING - Redirecting to TripDetailsFragment");

            // navigate to counting fragment here
            SetupFragmentDirections.ActionSetupFragmentToTripDetailsFragment action = SetupFragmentDirections.actionSetupFragmentToTripDetailsFragment();
            this.navigationController.navigate(action);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.softwareScanningLauncher = this.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        byte[] scanData = intent.getByteArrayExtra("dataBytes");

                        this.viewModel.setupApplication(new String(scanData));
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
                    startSetup();
                }

                @Override
                public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                    super.onDenied(context, deniedPermissions);
                }
            });
        });
    }

    public void initObserverEvents() {
        this.viewModel.getSetupSuccessful().observe(this.getViewLifecycleOwner(), setupSuccessful -> {
            if (setupSuccessful) {
                Logging.i(this.getClass().getName(), "Setup performed successfully - Redirecting to DepartureFragment");

                Toast.makeText(this.requireContext(), R.string.setup_performed_successfully, Toast.LENGTH_LONG).show();

                // if everything worked until here, setup is complete, navigate to stop select fragment
                this.navigationController.navigate(R.id.action_setupFragment_to_departureFragment);
            } else {
                Logging.e(this.getClass().getName(), "Setup failed due to errors");

                // that did not work - inform the user about the error
                Toast.makeText(this.requireContext(), R.string.setup_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startSetup()
    {
        Intent softwareScannerIntent = new Intent(this.requireActivity(), ScanActivity.class);
        this.softwareScanningLauncher.launch(softwareScannerIntent);
    }
}