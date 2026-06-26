package dev.artiz.financeassistantapi;

import dev.artiz.financeassistantapi.config.AppConfig;
import dev.artiz.financeassistantapi.config.RateLimitingFilter;
import dev.artiz.financeassistantapi.config.SecurityConfig;
import dev.artiz.financeassistantapi.predictions.PredictionController;
import dev.artiz.financeassistantapi.predictions.PredictionService;
import dev.artiz.financeassistantapi.transactions.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Autowired;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(PredictionController.class)
@Import({ SecurityConfig.class, RateLimitingFilter.class })
class RateLimiterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PredictionService predictionService;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private AppConfig appConfig;

    @Test
    void shouldBlockAfterFiveRequestsEvenWithValidJwt() {
        String url = "/api/v1/predictions/next-month";
        String payload = "{\"category\":\"FOOD\"}";
        when(predictionService.predictNextMonth(any())).thenReturn(
            Mono.empty()
        );

        for (int i = 0; i < 5; i++) {
            webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus()
                .isOk();
        }

        webTestClient
            .mutateWith(SecurityMockServerConfigurers.mockJwt())
            .post()
            .uri(url)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .exchange()
            .expectStatus()
            .isEqualTo(429)
            .expectBody()
            .jsonPath("$.error")
            .isEqualTo(
                    "Too many requests. Please slow down. Prediction is expensive!"
            );
    }
}
