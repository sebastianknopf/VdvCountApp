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

    private MutableLiveData<CountedTrip> countedTrip;

    public TripDetailsViewModel() {
        this.countedTrip = new MutableLiveData<>();
    }

    public LiveData<CountedTrip> getCountedTrip() {
        return this.countedTrip;
    }

    public void startCountedTrip(int tripId, String vehicleId, int startStopSequence) {
        Runnable runnable = () -> {
            RemoteRepository remoteRepository = RemoteRepository.getInstance();
            Trip trip = remoteRepository.getTripByTripId(tripId);

            FilesystemRepository filesystemRepository = FilesystemRepository.getInstance();
            CountedTrip countedTrip = filesystemRepository.startCountedTrip(trip, vehicleId);

            this.countedTrip.postValue(countedTrip);

            Status.setString(Status.STATUS, Status.Values.COUNTING);
            Status.setInt(Status.CURRENT_TRIP_ID, tripId);
            Status.setInt(Status.CURRENT_START_STOP_SEQUENCE, startStopSequence);
            Status.setString(Status.CURRENT_VEHICLE_ID, vehicleId);
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

    public void closeCountedTrip() {
        Runnable runnable = () -> {
            FilesystemRepository filesystemRepository = FilesystemRepository.getInstance();
            CountedTrip countedTrip = filesystemRepository.loadCountedTrip();

            RemoteRepository remoteRepository = RemoteRepository.getInstance();
            if (remoteRepository.postResults(countedTrip)) {
                filesystemRepository.closeCountedTrip();

                Status.setString(Status.STATUS, Status.Values.READY);
                Status.setInt(Status.CURRENT_TRIP_ID, -1);
                Status.setInt(Status.CURRENT_START_STOP_SEQUENCE, -1);
                Status.setString(Status.CURRENT_VEHICLE_ID, "");
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

}