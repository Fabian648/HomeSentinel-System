package de.muenchen.aigner.home_sentinel.controller;


import de.muenchen.aigner.home_sentinel.model.SensorData;
import de.muenchen.aigner.home_sentinel.utils.APIKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${auth.registration-key}")
    private String master_key;

    @PostMapping
    public ResponseEntity<String> receiveTelemetry(@RequestBody SensorData data){

        if(data.value() > 70.0 || data.value() < -100.0 || data.deviceID() == null){
            return ResponseEntity.badRequest().build();
        }

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

    @GetMapping("/register/{deviceID}")
    public ResponseEntity<String> registerTelemetry(@PathVariable String deviceID, @RequestHeader String key){


        if(deviceID == null){
            return ResponseEntity.badRequest().build();
        }
        if(key == null){
            return ResponseEntity.badRequest().build();
        }
        if(!master_key.equals(key)){
            return ResponseEntity.badRequest().build();
        }

        try{
            String apikey = APIKeyGenerator.generateAPIKey();

            redisTemplate.opsForValue().set("auth:key:" + apikey, deviceID);

            return ResponseEntity.ok(objectMapper.writeValueAsString(apikey));
        }catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }


    }

    @GetMapping("/{deviceID}")
    public ResponseEntity<SensorData> getTelemetry(@PathVariable String deviceID){
        if(deviceID == null){
            return ResponseEntity.notFound().build();
        }

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
