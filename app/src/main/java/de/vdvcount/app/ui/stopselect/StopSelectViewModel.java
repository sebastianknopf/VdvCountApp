package de.vdvcount.app.ui.stopselect;

import androidx.databinding.ObservableList;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import de.vdvcount.app.model.Station;

public class StopSelectViewModel extends ViewModel {

    private MutableLiveData<List<Station>> stations;

    public StopSelectViewModel() {
        this.stations = new MutableLiveData<>();
    }

    public LiveData<List<Station>> getStations() {
        return this.stations;
    }

    public void loadStations(String lookupName) {
        Station station1 = new Station();
        station1.setId(4711);
        station1.setName("Test Station");

        this.stations.setValue(List.of(station1));
    }
}