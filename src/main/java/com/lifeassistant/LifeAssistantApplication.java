package com.lifeassistant;

import com.lifeassistant.config.AiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AiProperties.class)
public class LifeAssistantApplication {
    public static void main(String[] args) {
        SpringApplication.run(LifeAssistantApplication.class, args);
    }
}
