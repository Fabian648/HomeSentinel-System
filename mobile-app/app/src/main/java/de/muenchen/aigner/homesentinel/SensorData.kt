package de.muenchen.aigner.homesentinel

data class SensorData(
    val deviceID: String,
    val sensorType: String,
    val value: Double,
    val unit: String,
    val timestamp: String
)