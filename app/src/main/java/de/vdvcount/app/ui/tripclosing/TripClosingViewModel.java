package de.vdvcount.app.ui.tripclosing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.vdvcount.app.common.Status;
import de.vdvcount.app.filesystem.FilesystemRepository;
import de.vdvcount.app.model.CountedStopTime;
import de.vdvcount.app.model.CountedTrip;
import de.vdvcount.app.model.CountingSequence;
import de.vdvcount.app.model.PassengerCountingEvent;
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

            if (stayInVehicle) {
                PassengerCountingEvent passengerCountingEvent = this.extractLastPce(countedTrip);
                Status.setString(Status.LAST_PCE, passengerCountingEvent != null ? passengerCountingEvent.serialize() : null);

                if (passengerCountingEvent != null) {
                    for (CountingSequence cs : passengerCountingEvent.getCountingSequences()) {
                        cs.setIn(0);
                    }
                }
            }

            RemoteRepository remoteRepository = RemoteRepository.getInstance();
            if (remoteRepository.postResults(countedTrip)) {
                filesystemRepository.closeCountedTrip();

                this.state.postValue(TripClosingFragment.State.DONE);

                Status.setString(Status.STATUS, Status.Values.READY);
                Status.setInt(Status.CURRENT_TRIP_ID, -1);
                Status.setInt(Status.CURRENT_START_STOP_SEQUENCE, -1);
                Status.setString(Status.CURRENT_VEHICLE_ID, "");

                if (stayInVehicle) {
                    String[] countedDoorIds = Status.getStringArray(Status.CURRENT_COUNTED_DOOR_IDS, new String[] {});

                    Status.setString(Status.LAST_VEHICLE_ID, countedTrip.getVehicleId());
                    Status.setStringArray(Status.LAST_COUNTED_DOOR_IDS, countedDoorIds);
                    Status.setBoolean(Status.STAY_IN_VEHICLE, true);
                }
            } else {
                this.state.postValue(TripClosingFragment.State.ERROR);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    private PassengerCountingEvent extractLastPce(CountedTrip trip) {
        CountedStopTime lastCountedStopTime = trip.getCountedStopTimes().get(trip.getCountedStopTimes().size() - 1);

        PassengerCountingEvent lastPassengerCountingEvent = null;
        if (!lastCountedStopTime.getPassengerCountingEvents().isEmpty()) {
            lastPassengerCountingEvent = lastCountedStopTime.getPassengerCountingEvents().get(lastCountedStopTime.getPassengerCountingEvents().size() - 1);
        }

        return lastPassengerCountingEvent;
    }
}