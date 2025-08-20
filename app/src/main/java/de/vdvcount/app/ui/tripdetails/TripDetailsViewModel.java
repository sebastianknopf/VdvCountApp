package de.vdvcount.app.ui.tripdetails;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.vdvcount.app.common.LocationService;
import de.vdvcount.app.common.Logging;
import de.vdvcount.app.common.Status;
import de.vdvcount.app.filesystem.FilesystemRepository;
import de.vdvcount.app.model.CountedTrip;
import de.vdvcount.app.model.CountingSequence;
import de.vdvcount.app.model.PassengerCountingEvent;
import de.vdvcount.app.model.Trip;
import de.vdvcount.app.model.WayPoint;
import de.vdvcount.app.remote.RemoteRepository;
import de.vdvcount.app.ui.counting.CountingFragment;

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

    public void startCountedTrip(int tripId, String vehicleId, int vehicleNumDoors, int startStopSequence) {
        Runnable runnable = () -> {
            this.state.postValue(TripDetailsFragment.State.LOADING);

            RemoteRepository remoteRepository = RemoteRepository.getInstance();
            Trip trip = remoteRepository.getTripByTripId(tripId);

            if (trip != null) {
                FilesystemRepository filesystemRepository = FilesystemRepository.getInstance();
                CountedTrip countedTrip = filesystemRepository.startCountedTrip(trip, vehicleId, vehicleNumDoors);

                if (Status.getBoolean(Status.STAY_IN_VEHICLE, false)) {
                    String passengerCountingEventJson = Status.getString(Status.LAST_PCE, null);
                    if (passengerCountingEventJson != null) {
                        PassengerCountingEvent passengerCountingEvent = PassengerCountingEvent.deserialize(passengerCountingEventJson);
                        for (CountingSequence cs : passengerCountingEvent.getCountingSequences()) {
                            cs.setOut(0);

                            // see #57, PCEs with stayInVehicle flag should be re-started at the half of their duration + 1s
                            // this is done on CS level, because start and end timestamps are stored for each CS
                            Date startTimestamp = cs.getCountBeginTimestamp();
                            Date endTimestamp = cs.getCountEndTimestamp();

                            long midpoint = (startTimestamp.getTime() + endTimestamp.getTime()) / 2;
                            Date updatedStartDate = new Date(midpoint + 1000);
                            cs.setCountBeginTimestamp(updatedStartDate);
                        }

                        countedTrip.getCountedStopTimes().get(0).getPassengerCountingEvents().add(passengerCountingEvent);
                        filesystemRepository.updateCountedTrip(countedTrip);
                    }

                    Status.setBoolean(Status.STAY_IN_VEHICLE, false);
                    Status.setString(Status.LAST_PCE, null);
                    Status.setString(Status.LAST_VEHICLE_ID, null);
                    Status.setStringArray(Status.LAST_COUNTED_DOOR_IDS, new String[] {});
                }

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

    public void cancelCountedTrip() {
        Runnable runnable = () -> {
            FilesystemRepository filesystemRepository = FilesystemRepository.getInstance();
            filesystemRepository.cancelCountedTrip();

            Status.setString(Status.STATUS, Status.Values.READY);
            Status.setInt(Status.CURRENT_TRIP_ID, -1);
            Status.setInt(Status.CURRENT_START_STOP_SEQUENCE, -1);
            Status.setString(Status.CURRENT_VEHICLE_ID, "");
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void addRunThroughPassengerCountingEvent(int stopSequence) {
        Logging.i(this.getClass().getName(), "Adding run-through PCE");

        Runnable runnable = () -> {
            this.state.postValue(TripDetailsFragment.State.LOADING);

            FilesystemRepository repository = FilesystemRepository.getInstance();
            CountedTrip countedTrip = repository.loadCountedTrip();

            Location location = LocationService.getInstance().requestCurrentLocation();
            Date timestamp = new Date();

            CountingSequence countingSequence = new CountingSequence();
            countingSequence.setDoorId("0");
            countingSequence.setCountingAreaId("1");
            countingSequence.setObjectClass("Adult");
            countingSequence.setCountBeginTimestamp(timestamp);
            countingSequence.setCountEndTimestamp(timestamp);

            PassengerCountingEvent pce = new PassengerCountingEvent();
            pce.setCountingSequences(List.of(countingSequence));

            if (location != null) {
                pce.setLatitude(location.getLatitude());
                pce.setLongitude(location.getLongitude());
            } else {
                Logging.w(this.getClass().getName(), "Location object is null, no location assigned with this PCE");
            }

            countedTrip.getCountedStopTimes().get(stopSequence - 1).getPassengerCountingEvents().add(pce);

            repository.updateCountedTrip(countedTrip);

            this.countedTrip.postValue(countedTrip);
            this.state.postValue(TripDetailsFragment.State.READY);
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

    public void addWayPoint(Location location) {
        final WayPoint wayPoint = new WayPoint();
        wayPoint.setLatitude(location.getLatitude());
        wayPoint.setLongitude(location.getLongitude());
        wayPoint.setTimestamp(new Date());

        Runnable runnable = () -> {
            FilesystemRepository repository = FilesystemRepository.getInstance();
            CountedTrip countedTrip = repository.loadCountedTrip();

            // the LocationService may be started before the initial request for the counted trip
            // is proceeded. This would result in a NullReferenceException ...
            if (countedTrip != null) {
                countedTrip.getWayPoints().add(wayPoint);
                repository.updateCountedTrip(countedTrip);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }
}