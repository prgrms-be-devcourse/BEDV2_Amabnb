package com.prgrms.amabnb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AmabnbApplication {

    public static void main(String[] args) {
        SpringApplication.run(AmabnbApplication.class, args);
    }

}
