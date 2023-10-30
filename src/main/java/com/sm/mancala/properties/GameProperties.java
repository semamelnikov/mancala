package com.sm.mancala.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "game")
@NoArgsConstructor
@Getter
@Setter
public class GameProperties {

    private Integer playersNumber;
    private Integer cupsNumber;
    private Integer stonesPerCup;
}
