package dev.artiz.financeassistantapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FinanceAssistantApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceAssistantApiApplication.class, args);
    }

}
