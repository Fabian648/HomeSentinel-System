package de.muenchen.aigner.homesentinel

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("api/v1/telemetry/{deviceID}")
    suspend fun getLatestData(
        @Path("deviceID") deviceID: String
    ): SensorData

}