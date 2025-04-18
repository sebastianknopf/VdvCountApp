package de.vdvcount.app.ui.tripdetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.vdvcount.app.common.Status;
import de.vdvcount.app.model.CountedTrip;

public class TripDetailsViewModel extends ViewModel {

    private MutableLiveData<CountedTrip> countedTrip;

    public TripDetailsViewModel() {
        this.countedTrip = new MutableLiveData<>();
    }

    public LiveData<CountedTrip> getCountedTrip() {
        return this.countedTrip;
    }

    public void startCountedTrip(int tripId, int startStopSequence) {
        Status.setString(Status.STATUS, Status.Values.COUNTING);
        Status.setInt(Status.CURRENT_TRIP_ID, tripId);
        Status.setInt(Status.CURRENT_START_STOP_SEQUENCE, startStopSequence);
    }

    public void loadCountedTrip() {

    }

    public void closeCountedTrip() {
        Status.setString(Status.STATUS, Status.Values.READY);
        Status.setInt(Status.CURRENT_TRIP_ID, -1);
        Status.setInt(Status.CURRENT_START_STOP_SEQUENCE, -1);
    }

}