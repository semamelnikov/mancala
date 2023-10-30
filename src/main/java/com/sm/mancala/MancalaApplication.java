package com.sm.mancala;

import com.sm.mancala.properties.GameProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(GameProperties.class)
public class MancalaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MancalaApplication.class, args);
    }

}
