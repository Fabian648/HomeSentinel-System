package de.muenchen.aigner.home_sentinel.controller;


import de.muenchen.aigner.home_sentinel.model.SensorData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/telemetry")
public class TelemetryController {

    @PostMapping
    public ResponseEntity<String> receiveTelemetry(@RequestBody SensorData data){
        System.out.println("Empfangen: " + data);
        return ResponseEntity.accepted().body("Daten erhalten");
    }
}
