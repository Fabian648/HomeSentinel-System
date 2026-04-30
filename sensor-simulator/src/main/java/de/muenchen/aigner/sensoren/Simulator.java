package de.muenchen.aigner.sensoren;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class Simulator {
    public static void main(String[] args) throws Exception {
        var client = HttpClient.newHttpClient();
        String url = "http://localhost:8080/api/v1/telemetry";

        System.out.println("🚀 Simulator gestartet. Sende Daten an: " + url);

        while (true) {
            double temp = ThreadLocalRandom.current().nextDouble(20.0, 25.0);

            // Unser JSON-Body (passend zum Record im Backend)
            String json = String.format(Locale.US, """
                {
                    "deviceID": "ESP32-SIM-01",
                    "sensorType": "TEMPERATURE",
                    "value": %.2f,
                    "unit": "°C",
                    "timestamp": "%s"
                }
                """, temp, Instant.now());

            var request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            try {
                var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("✅ Status " + response.statusCode() + " | Gesendet: " + temp + "°C");
            } catch (Exception e) {
                System.err.println("❌ Fehler: Backend nicht erreichbar?");
            }

            Thread.sleep(3000); // 3 Sekunden Pause
        }
    }
}