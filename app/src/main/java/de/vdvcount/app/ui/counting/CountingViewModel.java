package de.vdvcount.app.ui.counting;

import android.location.Location;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.vdvcount.app.common.LocationService;
import de.vdvcount.app.filesystem.FilesystemRepository;
import de.vdvcount.app.model.CountedTrip;
import de.vdvcount.app.model.CountingSequence;
import de.vdvcount.app.model.PassengerCountingEvent;

public class CountingViewModel extends ViewModel {

    private MutableLiveData<CountingFragment.State> state;

    public CountingViewModel() {
        this.state = new MutableLiveData<>(CountingFragment.State.INITIAL);
    }

    public LiveData<CountingFragment.State> getState() {
        return this.state;
    }

    public List<CountingSequence> generateCountingSequenceContainers(String[] doorIds) {
        List<CountingSequence> countingSequences = new ArrayList<>();
        for (String doorId : doorIds) {
            CountingSequence cs = new CountingSequence();
            cs.setDoorId(doorId);
            cs.setCountingAreaId("1");
            cs.setObjectClass("Adult");
            cs.setCountBeginTimestamp(new Date());

            countingSequences.add(cs);
        }

        return countingSequences;
    }

    public void addPassengerCountingEvent(int stopSequence, List<CountingSequence> countingSequences) {
        Runnable runnable = () -> {
            this.state.postValue(CountingFragment.State.STORING);

            for (CountingSequence cs : countingSequences) {
                cs.setCountEndTimestamp(new Date());
            }

            FilesystemRepository repository = FilesystemRepository.getInstance();
            CountedTrip countedTrip = repository.loadCountedTrip();

            Location location = LocationService.requestCurrentLocation();

            PassengerCountingEvent pce = new PassengerCountingEvent();

            if (location != null) {
                pce.setLatitude(location.getLatitude());
                pce.setLongitude(location.getLongitude());
            }

            pce.setAfterStopSequence(-1);
            pce.setCountingSequences(countingSequences);

            countedTrip.getCountedStopTimes().get(stopSequence - 1).getPassengerCountingEvents().add(pce);
            repository.updateCountedTrip(countedTrip);

            this.state.postValue(CountingFragment.State.STORED);
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }
}