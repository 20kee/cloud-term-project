package com.example.webserver.config;

import com.example.webserver.controller.ReviewController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitialConfig {
    @Bean
    CommandLineRunner localServerStart(ReviewController reviewController) {
        return args -> {
            reviewController.saveAll();
        };
    }
}
