package de.vdvcount.app.ui.stopselect;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import de.vdvcount.app.model.Station;
import de.vdvcount.app.remote.RemoteRepository;

public class StopSelectViewModel extends ViewModel {

    private MutableLiveData<List<Station>> stations;

    public StopSelectViewModel() {
        this.stations = new MutableLiveData<>();
    }

    public LiveData<List<Station>> getStations() {
        return this.stations;
    }

    public void loadStationsByLookupName(String lookupName) {
        Runnable runnable = () -> {
            RemoteRepository repository = RemoteRepository.getInstance();
            List<Station> stations = repository.getStopsByLookupName(lookupName);

            if (stations != null) {
                this.stations.postValue(stations);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }
}