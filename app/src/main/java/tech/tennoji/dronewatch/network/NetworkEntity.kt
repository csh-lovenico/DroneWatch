package tech.tennoji.dronewatch.network

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson
import tech.tennoji.dronewatch.geojson.GeoJsonPoint
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class WebMessage<T>(
    val code: Int,
    val message: String,
    val data: T?
)

@JsonClass(generateAdapter = true)
data class DroneRecord(
    val id: String,
    val timestamp: LocalDateTime,
    val metadata: DroneMetaData,
    val location: GeoJsonPoint
)

data class DroneMetaData(
    val droneId: String
)

// Custom adapter for converting LocalDateTime object
class LocalDateTimeAdapter {
    @ToJson
    fun toJson(time: LocalDateTime): String {
        return time.toString()
    }

    @FromJson
    fun fromJson(timeString: String): LocalDateTime {
        return LocalDateTime.parse(timeString)
    }
}
