package de.vdvcount.app.ui.departure;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import de.vdvcount.app.model.Departure;
import de.vdvcount.app.remote.RemoteRepository;

public class DepartureViewModel extends ViewModel {

    private MutableLiveData<List<Departure>> departures;

    public DepartureViewModel() {
        this.departures = new MutableLiveData<List<Departure>>();
    }

    public LiveData<List<Departure>> getDepartures() {
        return this.departures;
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