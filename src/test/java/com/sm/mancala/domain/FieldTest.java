package com.sm.mancala.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.sm.mancala.domain.game.Field;
import com.sm.mancala.domain.pit.Pit;
import com.sm.mancala.domain.player.PlayersGroup;
import java.util.List;
import org.junit.jupiter.api.Test;

public class FieldTest {

    /*@Test
    public void shouldCreateField() {
        final int cupsNumber = 6;
        final int stonesPerCup = 6;
        final int playersNumber = 2;
        final PlayersGroup gamePlayers = PlayersGroup.create(playersNumber);

        Field createdField = Field.createField(gamePlayers, cupsNumber, stonesPerCup);
        List<Pit> pits = createdField.pits();

        final int expectedPitsSize = playersNumber * (cupsNumber + 1);
        assertThat(pits.size()).isEqualTo(expectedPitsSize);
    }

    @Test
    public void move() {
        final int cupsNumber = 6;
        final int stonesPerCup = 13;
        final int playersNumber = 2;
        final PlayersGroup gamePlayers = PlayersGroup.create(playersNumber);

        Field field = Field.createField(gamePlayers, cupsNumber, stonesPerCup);

        field.makeMove(gamePlayers.getActivePlayer().uuid(), 2);
    }*/
}