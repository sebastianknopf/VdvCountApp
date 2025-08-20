package de.vdvcount.app.ui.tripparams;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import de.vdvcount.app.AppActivity;
import de.vdvcount.app.R;
import de.vdvcount.app.adapter.DoorListAdapter;
import de.vdvcount.app.adapter.VehicleListAdapter;
import de.vdvcount.app.common.Status;
import de.vdvcount.app.databinding.FragmentTripParamsBinding;
import de.vdvcount.app.model.Vehicle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TripParamsFragment extends Fragment {

    private FragmentTripParamsBinding dataBinding;
    private TripParamsViewModel viewModel;

    private NavController navigationController;

    private int currentTripId;
    private String currentVehicleId;
    private int currentVehicleNumDoors;
    private VehicleListAdapter vehicleListAdapter;
    private DoorListAdapter doorListAdapter;
    private TextWatcher vehicleIdTextWatcher;

    public static TripParamsFragment newInstance() {
        return new TripParamsFragment();
    }

    public TripParamsFragment() {
        this.vehicleIdTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                currentVehicleId = null;
                currentVehicleNumDoors = -1;
                doorListAdapter.setDoorList(new ArrayList<>());

                validateInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.vehicleListAdapter = new VehicleListAdapter(this.requireContext());
        this.doorListAdapter = new DoorListAdapter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trip_params, container, false);
        this.dataBinding.setLifecycleOwner(this.getViewLifecycleOwner());

        this.dataBinding.edtVehicle.setAdapter(this.vehicleListAdapter);
        this.dataBinding.lstDoors.setAdapter(this.doorListAdapter);

        TripParamsFragmentArgs args = TripParamsFragmentArgs.fromBundle(this.getArguments());
        if (args.getTripId() != -1) {
            this.currentTripId = args.getTripId();
        }

        if (args.getLineName() != null && args.getTripHeadsign() != null && args.getFormattedDepartureTime() != null) {
            String tripInfo = this.requireContext().getString(
                    R.string.trip_params_trip_info,
                    args.getLineName(),
                    args.getTripHeadsign(),
                    args.getFormattedDepartureTime()
            );

            this.dataBinding.lblTripInfo.setText(tripInfo);
        }

        return this.dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(TripParamsViewModel.class);

        this.viewModel.loadVehicles();
        this.viewModel.loadObjectClasses();

        this.initViewEvents();
        this.initObserverEvents();
    }

    @Override
    public void onStart() {
        super.onStart();

        AppActivity appActivity = (AppActivity) this.getActivity();
        appActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appActivity.setTitle(R.string.trip_params_title);

        this.setHasOptionsMenu(true);

        this.navigationController = appActivity.getNavigationController();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            TripParamsFragmentDirections.ActionTripParamsFragmentToDepartureFragment action = TripParamsFragmentDirections.actionTripParamsFragmentToDepartureFragment();
            this.navigationController.navigate(action);

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void initViewEvents() {
        this.dataBinding.edtVehicle.addTextChangedListener(this.vehicleIdTextWatcher);

        this.dataBinding.edtVehicle.setOnItemClickListener((adapterView, view, i, l) -> {
            Vehicle vehicle = this.vehicleListAdapter.getItem(i);
            this.currentVehicleId = vehicle.getName();
            this.currentVehicleNumDoors = vehicle.getNumDoors();

            this.createDoorList(vehicle);

            this.validateInputs();
        });

        this.doorListAdapter.setOnItemSelectListener((item, selected) -> {
            this.validateInputs();
        });

        this.dataBinding.btnContinue.setOnClickListener(view -> {
            TripParamsFragmentDirections.ActionTripParamsFragmentToTripDetailsFragment action = TripParamsFragmentDirections.actionTripParamsFragmentToTripDetailsFragment(
                    this.currentTripId,
                    this.currentVehicleId,
                    0,
                    this.doorListAdapter.getSelectedDoorList().toArray(new String[0])
            );

            action.setVehicleNumDoors(this.currentVehicleNumDoors);

            this.navigationController.navigate(action);
        });
    }

    private void initObserverEvents() {
        this.viewModel.getVehicles().observe(this.getViewLifecycleOwner(), vehicles -> {
            this.vehicleListAdapter.setVehicles(vehicles);

            if (Status.getBoolean(Status.STAY_IN_VEHICLE, false)) {
                String vehicleId = Status.getString(Status.LAST_VEHICLE_ID, null);
                if (vehicleId != null) {
                    Optional<Vehicle> vehicle = this.vehicleListAdapter.getVehicles().stream().filter(v -> v.getName().equals(vehicleId)).findFirst();
                    if (vehicle.isPresent()) {
                        this.currentVehicleId = vehicleId;

                        // TextWatcher of edtVehicle needs to be removed temporary as it resets the
                        // internal field currentVehicleId which is then used to validate the input fields.
                        // In this case, no vehicle is selected by user, but loaded programmatically.
                        this.dataBinding.edtVehicle.removeTextChangedListener(this.vehicleIdTextWatcher);
                        this.dataBinding.edtVehicle.setText(vehicleId);
                        this.dataBinding.edtVehicle.addTextChangedListener(this.vehicleIdTextWatcher);

                        String[] selectedDoorIds = Status.getStringArray(Status.LAST_COUNTED_DOOR_IDS, new String[] {});
                        this.createDoorList(vehicle.get(), selectedDoorIds);

                        // disable edtVehicle and lstDoors; the user is not allowed to change them, if they're set before
                        // disabling the list entries is done by using the adapter...
                        this.dataBinding.edtVehicle.setEnabled(false);
                        this.doorListAdapter.setEnabled(false);

                        this.validateInputs();
                    }
                }
            }
        });

        this.viewModel.getObjectClasses().observe(this.getViewLifecycleOwner(), objectClasses -> {
        });
    }

    private void validateInputs() {
        boolean inputsValid = true;

        if (this.currentVehicleId == null) {
            inputsValid = false;
        }

        if (this.doorListAdapter.getSelectedDoorList().size() < 1) {
            inputsValid = false;
        }

        this.dataBinding.btnContinue.setEnabled(inputsValid);
    }

    private void createDoorList(Vehicle vehicle, String[] selectedDoorIds) {
        List<String> doorList = new ArrayList<>();
        for (int d = 1; d <= vehicle.getNumDoors(); d++) {
            doorList.add(String.valueOf(d));
        }
        this.doorListAdapter.setDoorList(doorList);
        this.doorListAdapter.setSelectedDoorList(Arrays.asList(selectedDoorIds));
    }

    private void createDoorList(Vehicle vehicle) {
        this.createDoorList(vehicle, new String[] {});
    }

}