package dev.artiz.financeassistantapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RateLimiterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldBlockAfterFiveRequestsEvenWithValidJwt() throws Exception {
        String url = "/api/v1/predictions/next-month";
        String payload = "{\"category\":\"FOOD\"}";

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post(url)
                            .with(jwt())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(post(url)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.error").value("Too many requests. Please slow down. Prediction is expensive!"));
    }
}