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
    Call<List<Object>> getTripByTripId(@Path("tripId") int tripId);

    @GET("masterdata/vehicles")
    Call<List<Object>> getAllVehicles();

    @GET("masterdata/objectClasses")
    Call<List<Object>> getAllObjectClasses();

    @POST("results/post")
    Call<List<Object>> postResults(@Body Object result);

    @GET("system/health")
    Call<List<Object>> getSystemHealthCheck();
}
