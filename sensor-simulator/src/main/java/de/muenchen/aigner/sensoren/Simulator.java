package de.muenchen.aigner.sensoren;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Simulator implements Runnable {

    private static AtomicInteger counter;

    @Override
    public void run() {
        HttpClient client = HttpClient.newHttpClient();
        String backendHost = System.getenv("BACKEND_URL");
        String deviceApiKey = null;
        String deviceId = "ESP32-SIM-" + counter.incrementAndGet();

        if (backendHost == null) {
            backendHost = "http://localhost:8080";
        }
        String url = backendHost + "/api/v1/telemetry";

        System.out.println("🚀 Simulator gestartet. Sende Daten an: " + url);

        while (true) {

            if (deviceApiKey == null) {
                boolean run = true;
                while (run) {
                    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/register/" + deviceId)).header("X-API-KEY", "MeinGeheimesPasswort").build();
                    try {
                        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        if (response.statusCode() == 200) {
                            run = false;
                            deviceApiKey = response.body().toString();
                            System.out.println("✅ API-Key erfolgreich bekommen");

                        }
                        Thread.sleep(5000);
                    } catch (Exception e) {

                    }
                }
            }

            double temp = ThreadLocalRandom.current().nextDouble(20.0, 25.0);

            String json = String.format(Locale.US, """
                    {
                        "deviceID": "%s",
                        "sensorType": "TEMPERATURE",
                        "value": %.2f,
                        "unit": "°C",
                        "timestamp": "%s"
                    }
                    """.formatted(deviceId), temp, Instant.now());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url)).header("X-API-KEY", deviceApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse response = null;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 401){
                    deviceApiKey = null;
                }

                System.out.println("✅ Status " + response.statusCode() + " | Gesendet: " + temp + "°C");

            } catch (Exception e) {
                System.err.println("❌ Fehler: Backend nicht erreichbar?");
            }

            Thread.sleep(3000);
        }
    }
}