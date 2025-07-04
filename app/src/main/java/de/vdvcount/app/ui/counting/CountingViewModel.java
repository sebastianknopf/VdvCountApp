package de.vdvcount.app.ui.counting;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.vdvcount.app.common.LocationService;
import de.vdvcount.app.common.Logging;
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
        Logging.i(this.getClass().getName(), String.format("Generating counting sequences beginning at %s", sdf.format(new Date())));
        Logging.d(this.getClass().getName(), String.format("Door IDs: %s", String.join(", ", doorIds)));

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
        Logging.i(this.getClass().getName(), "Adding regular PCE");
        this.addPassengerCountingEventInternal(stopSequence, countingSequences, false);
    }

    public void addUnmatchedPassengerCountingEvent(int afterStopSequence, List<CountingSequence> countingSequences) {
        Logging.i(this.getClass().getName(), "Adding unmatched PCE");
        this.addPassengerCountingEventInternal(afterStopSequence, countingSequences, true);
    }

    private void addPassengerCountingEventInternal(final int sequence, List<CountingSequence> countingSequences, boolean unmatched) {
        Runnable runnable = () -> {
            this.state.postValue(CountingFragment.State.STORING);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
            Logging.i(this.getClass().getName(), String.format("Ending %d counting sequences at %s", countingSequences.size(), sdf.format(new Date())));

            for (CountingSequence cs : countingSequences) {
                cs.setCountEndTimestamp(new Date());
            }

            FilesystemRepository repository = FilesystemRepository.getInstance();
            CountedTrip countedTrip = repository.loadCountedTrip();

            Location location = LocationService.getInstance().requestCurrentLocation();

            PassengerCountingEvent pce = new PassengerCountingEvent();
            pce.setCountingSequences(countingSequences);

            if (location != null) {
                pce.setLatitude(location.getLatitude());
                pce.setLongitude(location.getLongitude());
            } else {
                Logging.w(this.getClass().getName(), "Location object is null, no location assigned with this PCE");
            }

            if (unmatched) {
                pce.setAfterStopSequence(sequence);
                countedTrip.getUnmatchedPassengerCountingEvents().add(pce);
            } else {
                countedTrip.getCountedStopTimes().get(sequence - 1).getPassengerCountingEvents().add(pce);
            }

            repository.updateCountedTrip(countedTrip);

            this.state.postValue(CountingFragment.State.STORED);
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }
}