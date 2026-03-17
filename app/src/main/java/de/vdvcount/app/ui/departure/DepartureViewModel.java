package de.vdvcount.app.ui.departure;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import de.vdvcount.app.common.Secret;
import de.vdvcount.app.common.Status;
import de.vdvcount.app.model.Departure;
import de.vdvcount.app.remote.RemoteRepository;

public class DepartureViewModel extends ViewModel {

    private MutableLiveData<List<Departure>> departures;
    private MutableLiveData<String> deviceId;

    public DepartureViewModel() {
        String deviceId = Secret.getSecretString(Secret.DEVICE_ID, "");

        this.departures = new MutableLiveData<List<Departure>>();
        this.deviceId = new MutableLiveData<>(deviceId);
    }

    public LiveData<List<Departure>> getDepartures() {
        return this.departures;
    }
    public LiveData<String> getDeviceId() {
        return this.deviceId;
    }

    public void loadDepartures(int parentStopId) {
        Runnable runnable = () -> {
            RemoteRepository repository = RemoteRepository.getInstance();
            List<Departure> departures = repository.getDeparturesByParentStopId(parentStopId);

            if (departures != null) {
                this.departures.postValue(departures);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }
}