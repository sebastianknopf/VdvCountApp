package de.vdvcount.app.ui.tripdetails;

import androidx.lifecycle.ViewModel;
import de.vdvcount.app.common.Status;

public class TripDetailsViewModel extends ViewModel {

    public void startCountedTrip(int tripId, int startStopSequence) {
        Status.setString(Status.STATUS, Status.Values.COUNTING);
        Status.setInt(Status.CURRENT_TRIP_ID, tripId);
        Status.setInt(Status.CURRENT_START_STOP_SEQUENCE, startStopSequence);
    }

    public void closeCountedTrip() {
        Status.setString(Status.STATUS, Status.Values.READY);
        Status.setInt(Status.CURRENT_TRIP_ID, -1);
        Status.setInt(Status.CURRENT_START_STOP_SEQUENCE, -1);
    }

}