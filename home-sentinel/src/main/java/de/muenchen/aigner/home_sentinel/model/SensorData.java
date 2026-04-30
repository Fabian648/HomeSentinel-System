package de.muenchen.aigner.home_sentinel.model;

import java.time.Instant;

public record SensorData (
  String deviceID,
  String sensorType, // TEMPERATURE, HUMIDITY
  double value,
  String unit,
  Instant timestamp
){

}
