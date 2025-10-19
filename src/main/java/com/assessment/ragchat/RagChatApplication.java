package com.assessment.ragchat;

import com.assessment.ragchat.property.ApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ApiProperties.class)
public class RagChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(RagChatApplication.class, args);
    }
}