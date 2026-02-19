package dev.artiz.financeassistantapi;

import org.springframework.boot.SpringApplication;

public class TestFinanceAssistantApiApplication {

    public static void main(String[] args) {
        SpringApplication.from(FinanceAssistantApiApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
