package tech.tennoji.dronewatch.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*


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

    @FormUrlEncoded
    @POST("/subscribe")
    fun subscribeToTopicAsync(
        @Field("token") token: String,
        @Field("area") area: String
    ): Deferred<WebMessage<Int>>

    @FormUrlEncoded
    @POST("/unsubscribe")
    fun unsubscribeToTopicAsync(
        @Field("token") token: String,
        @Field("area") area: String
    ): Deferred<WebMessage<Int>>

    @GET("/getLatestRecord")
    fun getLatestDroneRecordAsync(@Query("droneId") droneId: String): Deferred<WebMessage<DroneRecord>>

    @FormUrlEncoded
    @POST("/getSubscribedAreas")
    fun getSubscribedAreasAsync(@Field("token") token: String): Deferred<WebMessage<List<String>>>

    @FormUrlEncoded
    @POST("/getNotSubscribedAreas")
    fun getNotSubscribedAreasAsync(@Field("token") token: String): Deferred<WebMessage<List<String>>>

    @FormUrlEncoded
    @POST("/getSubscribedAreaStatus")
    fun getSubscribedAreaStatusAsync(@Field("token") token: String): Deferred<WebMessage<List<FenceStatus>>>

    @GET("/getAreaDroneList")
    fun getAreaDroneListAsync(@Query("area") area: String): Deferred<WebMessage<List<String>>>

}

object Api {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
