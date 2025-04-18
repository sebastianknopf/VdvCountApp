package de.vdvcount.app.remote;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

interface RemoteAPI {

    @GET("stops/byLookupName/{lookupName}")
    Call<List<StationObject>> getStopsByLookupName(@Path("lookupName") String lookupName);

    @GET("departures/byParentStopId/{parentStopId}")
    Call<List<DepartureObject>> getDeparturesByParentStopId(@Path("parentStopId") int parentStopId);

    @GET("trips/byTripId/{tripId}")
    Call<TripObject> getTripByTripId(@Path("tripId") int tripId);

    @GET("masterdata/vehicles")
    Call<List<VehicleObject>> getAllVehicles();

    @GET("masterdata/objectClasses")
    Call<List<ObjectClassObject>> getAllObjectClasses();

    @POST("results/post/{guid}")
    Call<Void> postResults(@Path("guid") String guid, @Body CountedTripObject result);

    @GET("system/health")
    Call<SystemHealthObject> getSystemHealthCheck();
}
