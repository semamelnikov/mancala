package com.sm.mancala.properties;

import static org.assertj.core.api.Assertions.assertThat;

import com.sm.mancala.MancalaApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MancalaApplication.class)
@EnableConfigurationProperties(value = GameProperties.class)
public class GamePropertiesTest {

    private static final int DEFAULT_CUPS_NUMBER = 6;
    private static final int PLAYERS_NUMBER = 2;

    @Autowired
    private GameProperties gameProperties;

    @Test
    void gameProperties_propertiesBindingSuccess() {
        assertThat(gameProperties.getCupsNumber()).isEqualTo(DEFAULT_CUPS_NUMBER);
        assertThat(gameProperties.getPlayersNumber()).isEqualTo(PLAYERS_NUMBER);
    }
}
