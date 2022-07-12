package tech.tennoji.dronewatch.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


const val BASE_URL = "http://10.0.2.2:8080"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory()).add(LocalDateTimeAdapter())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()


interface ApiService {

    @POST("/subscribe")
    fun subscribeToTopicAsync(
        @Query("token") token: String,
        @Query("area") area: String
    ): Deferred<WebMessage<Int>>

    @POST("/unsubscribe")
    fun unsubscribeToTopicAsync(
        @Query("token") token: String,
        @Query("area") area: String
    ): Deferred<WebMessage<Int>>

    @GET("/getLatestRecord")
    fun getLatestDroneRecordAsync(@Query("droneId") droneId: String): Deferred<WebMessage<DroneRecord>>

    @GET("/getSubscribedAreas")
    fun getSubscribedAreasAsync(@Query("token") token: String): Deferred<List<String>>

    @GET("/getNotSubscribedAreas")
    fun getNotSubscribedAreasAsync(@Query("token") token: String): Deferred<List<String>>

    @GET("/getSubscribedAreaStatus")
    fun getSubscribedAreaStatusAsync(@Query("token") token: String): Deferred<List<FenceStatus>>

}

object Api {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
