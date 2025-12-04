package com.segurosargos.hotelbook.actuator;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de los endpoints de Actuator utilizando el perfil test.
 * Se validan los endpoints /actuator/health y /actuator/info.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActuatorEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("/actuator/health devuelve 200 y status UP")
    void healthEndpoint_returnsUpStatus() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));
    }

    @Test
    @DisplayName("/actuator/info devuelve 200 e incluye el bloque app con name y environment")
    void infoEndpoint_returnsAppDetails() throws Exception {
        mockMvc.perform(get("/actuator/info")
                        .header("Authorization", basicAuth("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app").exists())
                .andExpect(jsonPath("$.app.name", is("HotelBook")))
                .andExpect(jsonPath("$.app.environment", notNullValue()));
    }

    private String basicAuth(String username, String password) {
        String token = username + ":" + password;
        byte[] encodedBytes = Base64.getEncoder().encode(token.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes, StandardCharsets.UTF_8);
    }
}
