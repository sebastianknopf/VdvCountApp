package de.vdvcount.app.remote;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

import de.vdvcount.app.common.Secret;
import de.vdvcount.app.model.Departure;
import de.vdvcount.app.model.ObjectClass;
import de.vdvcount.app.model.Station;
import de.vdvcount.app.model.Trip;
import de.vdvcount.app.model.Vehicle;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RemoteRepository {

    private static RemoteRepository repository;

    private RemoteAPI remoteApiClient;

    public static RemoteRepository getInstance() {
        if (repository == null) {
            try {
                repository = new RemoteRepository();
            } catch (InvalidKeyException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }

        return repository;
    }

    private RemoteRepository() throws InvalidKeyException, IllegalAccessException {
        String remoteApiEndpoint = Secret.getSecretString(Secret.API_ENDPOINT, null);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RemoteAuthenticationInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(remoteApiEndpoint)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.remoteApiClient = retrofit.create(RemoteAPI.class);
    }

    public List<Station> getStopsByLookupName(String lookupName) {
        try {
            Call<List<StationObject>> call = this.remoteApiClient.getStopsByLookupName(lookupName);
            Response<List<StationObject>> response = call.execute();

            if (response.isSuccessful()) {
                List<StationObject> objectList = response.body();
                List<Station> resultList = new ArrayList<>();

                for (StationObject obj : objectList) {
                    resultList.add(obj.mapDomainModel());
                }

                return resultList;
            } else {
                return null;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<Departure> getDeparturesByParentStopId(int parentStopId) {
        try {
            Call<List<DepartureObject>> call = this.remoteApiClient.getDeparturesByParentStopId(parentStopId);
            Response<List<DepartureObject>> response = call.execute();

            if (response.isSuccessful()) {
                List<DepartureObject> objectList = response.body();
                List<Departure> resultList = new ArrayList<>();

                for (DepartureObject obj : objectList) {
                    resultList.add(obj.mapDomainModel());
                }

                return resultList;
            } else {
                return null;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Trip getTripByTripId(int tripId) {
        try {
            Call<TripObject> call = this.remoteApiClient.getTripByTripId(tripId);
            Response<TripObject> response = call.execute();

            if (response.isSuccessful()) {
                TripObject object = response.body();
                return object.mapDomainModel();
            } else {
                return null;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<Vehicle> getAllVehicles() {
        try {
            Call<List<VehicleObject>> call = this.remoteApiClient.getAllVehicles();
            Response<List<VehicleObject>> response = call.execute();

            if (response.isSuccessful()) {
                List<VehicleObject> objectList = response.body();
                List<Vehicle> resultList = new ArrayList<>();

                for (VehicleObject obj : objectList) {
                    resultList.add(obj.mapDomainModel());
                }

                return resultList;
            } else {
                return null;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<ObjectClass> getAllObjectClasses() {
        try {
            Call<List<ObjectClassObject>> call = this.remoteApiClient.getAllObjectClasses();
            Response<List<ObjectClassObject>> response = call.execute();

            if (response.isSuccessful()) {
                List<ObjectClassObject> objectList = response.body();
                List<ObjectClass> resultList = new ArrayList<>();

                for (ObjectClassObject obj : objectList) {
                    resultList.add(obj.mapDomainModel());
                }

                return resultList;
            } else {
                return null;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
