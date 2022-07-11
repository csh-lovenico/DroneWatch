package tech.tennoji.dronewatch.geojson

// A simple GeoJson Point implementation
data class GeoJsonPoint(override var coordinates: List<Double>) : GeoJson<List<Double>> {

    override var type = "Point"
//    override var coordinates = listOf(x, y)
}