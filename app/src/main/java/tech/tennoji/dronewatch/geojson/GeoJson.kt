package tech.tennoji.dronewatch.geojson

// Simple GeoJson interface
// Inspired by Spring Data Mongo
interface GeoJson<T> {
    var type: String

    var coordinates: T
}