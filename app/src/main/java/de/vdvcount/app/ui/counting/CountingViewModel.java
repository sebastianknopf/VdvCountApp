package de.vdvcount.app.ui.counting;

import java.util.List;

import androidx.lifecycle.ViewModel;
import de.vdvcount.app.filesystem.FilesystemRepository;
import de.vdvcount.app.model.CountedTrip;
import de.vdvcount.app.model.CountingSequence;
import de.vdvcount.app.model.PassengerCountingEvent;

public class CountingViewModel extends ViewModel {

    public void addPassengerCountingEvent(int stopSequence, List<CountingSequence> countingSequences) {
        FilesystemRepository repository = FilesystemRepository.getInstance();
        CountedTrip countedTrip = repository.loadCountedTrip();

        PassengerCountingEvent pce = new PassengerCountingEvent();
        pce.setLatitude(48.8881773);
        pce.setLongitude(8.5466048);
        pce.setAfterStopSequence(-1);
        pce.setCountingSequences(countingSequences);

        countedTrip.getCountedStopTimes().get(stopSequence - 1).getPassengerCountingEvents().add(pce);
        repository.updateCountedTrip(countedTrip);
    }

}