package de.vdvcount.app.ui.tripdetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.vdvcount.app.common.Status;
import de.vdvcount.app.filesystem.FilesystemRepository;
import de.vdvcount.app.model.CountedTrip;
import de.vdvcount.app.model.Trip;
import de.vdvcount.app.remote.RemoteRepository;

public class TripDetailsViewModel extends ViewModel {

    private MutableLiveData<TripDetailsFragment.State> state;
    private MutableLiveData<CountedTrip> countedTrip;

    public TripDetailsViewModel() {
        this.state = new MutableLiveData<>(TripDetailsFragment.State.READY);
        this.countedTrip = new MutableLiveData<>();
    }

    public LiveData<TripDetailsFragment.State> getState() {
        return this.state;
    }

    public LiveData<CountedTrip> getCountedTrip() {
        return this.countedTrip;
    }

    public void startCountedTrip(int tripId, String vehicleId, int startStopSequence) {
        Runnable runnable = () -> {
            this.state.postValue(TripDetailsFragment.State.LOADING);

            RemoteRepository remoteRepository = RemoteRepository.getInstance();
            Trip trip = remoteRepository.getTripByTripId(tripId);

            if (trip != null) {
                FilesystemRepository filesystemRepository = FilesystemRepository.getInstance();
                CountedTrip countedTrip = filesystemRepository.startCountedTrip(trip, vehicleId);

                this.countedTrip.postValue(countedTrip);
                this.state.postValue(TripDetailsFragment.State.READY);

                Status.setString(Status.STATUS, Status.Values.COUNTING);
                Status.setInt(Status.CURRENT_TRIP_ID, tripId);
                Status.setInt(Status.CURRENT_START_STOP_SEQUENCE, startStopSequence);
                Status.setString(Status.CURRENT_VEHICLE_ID, vehicleId);
            } else {
                this.state.postValue(TripDetailsFragment.State.ERROR);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void loadCountedTrip() {
        Runnable runnable = () -> {
            FilesystemRepository repository = FilesystemRepository.getInstance();
            this.countedTrip.postValue(repository.loadCountedTrip());
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }
}