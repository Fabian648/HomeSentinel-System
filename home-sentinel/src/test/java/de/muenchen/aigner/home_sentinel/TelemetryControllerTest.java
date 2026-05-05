package de.muenchen.aigner.home_sentinel;

import de.muenchen.aigner.home_sentinel.controller.TelemetryController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@WebMvcTest(TelemetryController.class)
@AutoConfigureMockMvc
public class TelemetryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StringRedisTemplate redisTemplate;

    @MockitoBean
    private ValueOperations<String, String> valueOperations;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(){
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void shouldAcceptValidTelemetry() throws Exception {
        String validJson = "{\"deviceID\": \"sensor-1\", \"value\": 25.5}";

        mockMvc.perform(post("/api/v1/telemetry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson))
                .andExpect(status().isAccepted());
    }

    @Test
    void shouldRejectInvalidTelemetry() throws Exception {
        String invalidJson_hightemp = "{\"deviceID\": \"sensor-1\", \"value\": 82.25}";
        String invalidJson_lowtemp = "{\"deviceID\": \"sensor-1\", \"value\": -378.42}";
        String invalidJson_id = "{\"deviceID\": null, \"value\": 55.36}";

        mockMvc.perform(post("/api/v1/telemetry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson_hightemp))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/v1/telemetry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson_lowtemp))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/v1/telemetry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson_id))
                .andExpect(status().isBadRequest());

    }
}
