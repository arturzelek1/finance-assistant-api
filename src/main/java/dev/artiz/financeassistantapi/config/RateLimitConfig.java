package dev.artiz.financeassistantapi.config;

import com.giffing.bucket4j.spring.boot.starter.context.Bucket4jConfigurationHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

    @Bean
    public Bucket4jConfigurationHolder bucket4jConfigurationHolder() {
        return new Bucket4jConfigurationHolder();
    }
}
