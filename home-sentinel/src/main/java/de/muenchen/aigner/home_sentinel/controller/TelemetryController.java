package de.muenchen.aigner.home_sentinel.controller;


import de.muenchen.aigner.home_sentinel.model.SensorData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@RestController
@RequestMapping("api/v1/telemetry")
@RequiredArgsConstructor
public class TelemetryController {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<String> receiveTelemetry(@RequestBody SensorData data){
        try{

            String json = objectMapper.writeValueAsString(data);

            String key = "sensor:latest:" + data.deviceID();

            redisTemplate.opsForValue().set(key, json, Duration.ofHours(24));

            System.out.println("In Redis gespeichert: " + key + ": " + data.value());

            return ResponseEntity.accepted().build();

        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{deviceID}")
    public ResponseEntity<SensorData> getTelemetry(@PathVariable String deviceID){
        String key =  "sensor:latest:" + deviceID;
        String json =  redisTemplate.opsForValue().get(key);

        if(json == null){
            return ResponseEntity.notFound().build();
        }

        try{
            SensorData data = objectMapper.readValue(json, SensorData.class);
            return ResponseEntity.ok(data);
        }catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
}
