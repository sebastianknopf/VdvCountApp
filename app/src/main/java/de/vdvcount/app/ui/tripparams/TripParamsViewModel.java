package de.vdvcount.app.ui.tripparams;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.vdvcount.app.model.ObjectClass;
import de.vdvcount.app.model.Station;
import de.vdvcount.app.model.Vehicle;
import de.vdvcount.app.remote.RemoteRepository;

public class TripParamsViewModel extends ViewModel {

    private MutableLiveData<List<Vehicle>> vehicles;
    private MutableLiveData<List<ObjectClass>> objectClasses;

    public TripParamsViewModel() {
        this.vehicles = new MutableLiveData<>();
        this.objectClasses = new MutableLiveData<>();
    }

    public LiveData<List<Vehicle>> getVehicles() {
        return this.vehicles;
    }

    public LiveData<List<ObjectClass>> getObjectClasses() {
        return this.objectClasses;
    }

    public void loadVehicles() {
        Runnable runnable = () -> {
            RemoteRepository repository = RemoteRepository.getInstance();
            List<Vehicle> vehicles = repository.getAllVehicles();

            if (vehicles != null) {
                this.vehicles.postValue(vehicles);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void loadObjectClasses() {
        Runnable runnable = () -> {
            RemoteRepository repository = RemoteRepository.getInstance();
            List<ObjectClass> objectClasses = repository.getAllObjectClasses();

            if (objectClasses != null) {
                this.objectClasses.postValue(objectClasses);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }
}