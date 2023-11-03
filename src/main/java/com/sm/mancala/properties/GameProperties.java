package com.sm.mancala.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "game")
@Validated
@NoArgsConstructor
@Getter
@Setter
public class GameProperties {

    @NotNull
    private Integer playersNumber;

    @NotNull
    private Integer cupsNumber;
}
