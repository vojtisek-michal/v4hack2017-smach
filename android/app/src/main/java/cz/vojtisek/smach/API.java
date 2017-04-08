package cz.vojtisek.smach;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {
    @POST("amp")
    Call<String> setAmp(
        @Query("charging_session_id") String chargingSessionId,
        @Query("amp") int amp);

    @GET("charging")
    Call<JsonObject> getCharging(@Query("charging_session_id") String chargingSessionId);
}