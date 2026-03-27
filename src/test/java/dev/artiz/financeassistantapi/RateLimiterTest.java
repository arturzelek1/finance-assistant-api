package dev.artiz.financeassistantapi;

import dev.artiz.financeassistantapi.config.AppConfig;
import dev.artiz.financeassistantapi.controller.PredictionController;
import dev.artiz.financeassistantapi.service.PredictionService;
import dev.artiz.financeassistantapi.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PredictionController.class)
class RateLimiterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PredictionService predictionService;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private AppConfig appConfig;

    @Test
    void shouldBlockAfterFiveRequestsEvenWithValidJwt() throws Exception {
        String url = "/api/v1/predictions/next-month";
        String payload = "{\"category\":\"FOOD\"}";

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post(url)
                            .with(jwt().jwt(jwt -> jwt.claim("external_id", "test-user-id")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(post(url)
                        .with(jwt().jwt(jwt -> jwt.claim("external_id", "test-user-id")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.error")
                        .value("Too many requests. Please slow down. Prediction is expensive!"));
    }
}
