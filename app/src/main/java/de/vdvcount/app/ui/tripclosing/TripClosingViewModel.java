package de.vdvcount.app.ui.tripclosing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.vdvcount.app.common.Status;
import de.vdvcount.app.filesystem.FilesystemRepository;
import de.vdvcount.app.model.CountedTrip;
import de.vdvcount.app.remote.RemoteRepository;

public class TripClosingViewModel extends ViewModel {

    private MutableLiveData<TripClosingFragment.State> state;

    public TripClosingViewModel() {
        this.state = new MutableLiveData<>(TripClosingFragment.State.READY);
    }

    public LiveData<TripClosingFragment.State> getState() {
        return this.state;
    }

    public void closeCountedTrip(boolean stayInVehicle) {
        Runnable runnable = () -> {
            this.state.postValue(TripClosingFragment.State.LOADING);

            FilesystemRepository filesystemRepository = FilesystemRepository.getInstance();
            CountedTrip countedTrip = filesystemRepository.loadCountedTrip();

            RemoteRepository remoteRepository = RemoteRepository.getInstance();
            if (remoteRepository.postResults(countedTrip)) {
                filesystemRepository.closeCountedTrip();

                this.state.postValue(TripClosingFragment.State.DONE);

                Status.setString(Status.STATUS, Status.Values.READY);
                Status.setInt(Status.CURRENT_TRIP_ID, -1);
                Status.setInt(Status.CURRENT_START_STOP_SEQUENCE, -1);
                Status.setString(Status.CURRENT_VEHICLE_ID, "");
            } else {
                this.state.postValue(TripClosingFragment.State.ERROR);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }
}