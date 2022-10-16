package com.nikhilnishad.naukri;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NaukriBotSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(NaukriBotSpringBootApplication.class, args);
    }

}