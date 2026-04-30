package de.muenchen.aigner.home_sentinel.controller;


import de.muenchen.aigner.home_sentinel.model.SensorData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
