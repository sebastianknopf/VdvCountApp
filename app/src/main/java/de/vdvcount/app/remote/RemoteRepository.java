package de.vdvcount.app.remote;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.vdvcount.app.common.Logging;
import de.vdvcount.app.common.Secret;
import de.vdvcount.app.model.CountedTrip;
import de.vdvcount.app.model.Departure;
import de.vdvcount.app.model.ObjectClass;
import de.vdvcount.app.model.Station;
import de.vdvcount.app.model.Trip;
import de.vdvcount.app.model.Vehicle;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
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
                .addConverterFactory(GsonConverterFactory.create(
                        new GsonBuilder()
                                .serializeNulls()
                                .create()
                ))
                .build();

        this.remoteApiClient = retrofit.create(RemoteAPI.class);
    }

    public List<Station> getStopsByLookupName(String lookupName) {
        try {
            Call<List<StationObject>> call = this.remoteApiClient.getStopsByLookupName(lookupName);
            Logging.d(this.getClass().getName(), String.format("Performing request [%s] %s", call.request().method(), call.request().url()));

            Response<List<StationObject>> response = call.execute();

            if (response.isSuccessful()) {
                List<StationObject> objectList = response.body();
                List<Station> resultList = new ArrayList<>();

                for (StationObject obj : objectList) {
                    resultList.add(obj.mapDomainModel());
                }

                Logging.i(this.getClass().getName(), "Request successful");

                return resultList;
            } else {
                Logging.e(this.getClass().getName(), "Error performing remote API request");
                Logging.d(this.getClass().getName(), String.format("HTTP status code %d", response.code()));

                return null;
            }
        } catch (IOException ex) {
            Logging.e(this.getClass().getName(), "Error performing remote API request", ex);
            return null;
        }
    }

    public List<Departure> getDeparturesByParentStopId(int parentStopId) {
        try {
            Call<List<DepartureObject>> call = this.remoteApiClient.getDeparturesByParentStopId(parentStopId);
            Logging.d(this.getClass().getName(), String.format("Performing request [%s] %s", call.request().method(), call.request().url()));

            Response<List<DepartureObject>> response = call.execute();

            if (response.isSuccessful()) {
                List<DepartureObject> objectList = response.body();
                List<Departure> resultList = new ArrayList<>();

                for (DepartureObject obj : objectList) {
                    resultList.add(obj.mapDomainModel());
                }

                Logging.i(this.getClass().getName(), "Request successful");

                return resultList;
            } else {
                Logging.e(this.getClass().getName(), "Error performing remote API request");
                Logging.d(this.getClass().getName(), String.format("HTTP status code %d", response.code()));

                return null;
            }
        } catch (IOException ex) {
            Logging.e(this.getClass().getName(), "Error performing remote API request", ex);
            return null;
        }
    }

    public Trip getTripByTripId(int tripId) {
        try {
            Call<TripObject> call = this.remoteApiClient.getTripByTripId(tripId);
            Logging.d(this.getClass().getName(), String.format("Performing request [%s] %s", call.request().method(), call.request().url()));

            Response<TripObject> response = call.execute();

            if (response.isSuccessful()) {
                TripObject object = response.body();

                Logging.i(this.getClass().getName(), "Request successful");

                return object.mapDomainModel();
            } else {
                Logging.e(this.getClass().getName(), "Error performing remote API request");
                Logging.d(this.getClass().getName(), String.format("HTTP status code %d", response.code()));

                return null;
            }
        } catch (IOException ex) {
            Logging.e(this.getClass().getName(), "Error performing remote API request", ex);
            return null;
        }
    }

    public boolean postResults(CountedTrip countedTrip) {
        try {
            String resultGuid = UUID.randomUUID().toString();

            Call<Void> call = this.remoteApiClient.postResults(resultGuid, countedTrip.mapApiObject());
            Logging.d(this.getClass().getName(), String.format("Performing request [%s] %s", call.request().method(), call.request().url()));
            Logging.i(this.getClass().getName(), String.format("Result UUID is %s", resultGuid));

            Response<Void> response = call.execute();

            if (response.isSuccessful()) {
                Logging.i(this.getClass().getName(), "Request successful");

                return true;
            } else {
                Logging.e(this.getClass().getName(), "Error performing remote API request");
                Logging.d(this.getClass().getName(), String.format("HTTP status code %d", response.code()));

                return false;
            }
        } catch (IOException ex) {
            Logging.e(this.getClass().getName(), "Error performing remote API request", ex);

            return false;
        }
    }

    public boolean postLogs(String logs) {
        try {
            String deviceId = Secret.getSecretString(Secret.DEVICE_ID, null);
            if (deviceId == null) {
                deviceId = UUID.randomUUID().toString();
            }

            RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), logs);

            Call<Void> call = this.remoteApiClient.postLogs(deviceId, requestBody);
            Logging.d(this.getClass().getName(), String.format("Performing request [%s] %s", call.request().method(), call.request().url()));

            Response<Void> response = call.execute();

            if (response.isSuccessful()) {
                Logging.i(this.getClass().getName(), "Request successful");

                return true;
            } else {
                Logging.e(this.getClass().getName(), "Error performing remote API request");
                Logging.d(this.getClass().getName(), String.format("HTTP status code %d", response.code()));

                return false;
            }
        } catch (IOException ex) {
            Logging.e(this.getClass().getName(), "Error performing remote API request", ex);

            return false;
        }
    }

    public List<Vehicle> getAllVehicles() {
        try {
            Call<List<VehicleObject>> call = this.remoteApiClient.getAllVehicles();
            Logging.d(this.getClass().getName(), String.format("Performing request [%s] %s", call.request().method(), call.request().url()));

            Response<List<VehicleObject>> response = call.execute();

            if (response.isSuccessful()) {
                List<VehicleObject> objectList = response.body();
                List<Vehicle> resultList = new ArrayList<>();

                for (VehicleObject obj : objectList) {
                    resultList.add(obj.mapDomainModel());
                }

                Logging.i(this.getClass().getName(), "Request successful");

                return resultList;
            } else {
                Logging.e(this.getClass().getName(), "Error performing remote API request");
                Logging.d(this.getClass().getName(), String.format("HTTP status code %d", response.code()));

                return null;
            }
        } catch (IOException ex) {
            Logging.e(this.getClass().getName(), "Error performing remote API request", ex);
            return null;
        }
    }

    public List<ObjectClass> getAllObjectClasses() {
        try {
            Call<List<ObjectClassObject>> call = this.remoteApiClient.getAllObjectClasses();
            Logging.d(this.getClass().getName(), String.format("Performing request [%s] %s", call.request().method(), call.request().url()));

            Response<List<ObjectClassObject>> response = call.execute();

            if (response.isSuccessful()) {
                List<ObjectClassObject> objectList = response.body();
                List<ObjectClass> resultList = new ArrayList<>();

                for (ObjectClassObject obj : objectList) {
                    resultList.add(obj.mapDomainModel());
                }

                Logging.i(this.getClass().getName(), "Request successful");

                return resultList;
            } else {
                Logging.e(this.getClass().getName(), "Error performing remote API request");
                Logging.d(this.getClass().getName(), String.format("HTTP status code %d", response.code()));

                return null;
            }
        } catch (IOException ex) {
            Logging.e(this.getClass().getName(), "Error performing remote API request", ex);
            return null;
        }
    }

    public boolean performHealthCheck() {
        try {
            Call<SystemHealthObject> call = this.remoteApiClient.getSystemHealthCheck();
            Logging.d(this.getClass().getName(), String.format("Performing request [%s] %s", call.request().method(), call.request().url()));

            Response<SystemHealthObject> response = call.execute();

            if (response.isSuccessful()) {
                SystemHealthObject obj = response.body();

                int currentUnixTimestamp = (int) (new Date().getTime() / 1000L);
                if (obj.getTimestamp() >= currentUnixTimestamp - 300) {
                    Logging.i(this.getClass().getName(), "Health check request successful");
                    return true;
                } else {
                    Logging.e(this.getClass().getName(), "Server and device time differs more than 300 seconds");
                    return false;
                }
            } else {
                Logging.e(this.getClass().getName(), "Error performing remote API request");
                Logging.d(this.getClass().getName(), String.format("HTTP status code %d", response.code()));

                return false;
            }
        } catch (IOException ex) {
            Logging.e(this.getClass().getName(), "Error performing remote API request", ex);
            return false;
        }
    }
}
