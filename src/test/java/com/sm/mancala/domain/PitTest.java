package com.sm.mancala.domain;


import static org.assertj.core.api.Assertions.assertThat;

import com.sm.mancala.domain.pit.Cup;
import com.sm.mancala.domain.pit.Mancala;
import com.sm.mancala.domain.pit.Pit;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class PitTest {

    /*@Test
    public void shouldInitCupWithSpecifiedStonesNumber() {
        int stonesNumber = 6;

        Pit cup = new Cup(stonesNumber, getPlayerUuid());
        assertThat(cup.getStoneCount()).isEqualTo(stonesNumber);
    }

    @Test
    public void shouldInitEmptyMancala() {
        Pit mancala = new Mancala(getPlayerUuid());
        assertThat(mancala.getStoneCount()).isEqualTo(0);
    }

    @Test
    public void shouldPickUpAllStonesFromCup() {
        int initialStonesNumber = 6;
        Cup cup = new Cup(initialStonesNumber, getPlayerUuid());

        int currentStonesNumber = cup.pickUpStones();

        assertThat(currentStonesNumber).isEqualTo(initialStonesNumber);
        assertThat(cup.getStoneCount()).isEqualTo(0);
    }

    @Test
    public void shouldSowOnlyOneStoneToCup() {
        int initialStonesNumber = 6;
        Cup cup = new Cup(initialStonesNumber, getPlayerUuid());

        int currentStonesNumber = cup.sowStones();

        int expectedStonesNumber = initialStonesNumber + 1;
        assertThat(currentStonesNumber).isEqualTo(expectedStonesNumber);
        assertThat(cup.getStoneCount()).isEqualTo(expectedStonesNumber);
    }

    @Test
    public void shouldSowDefinedStonesNumberToMancala() {
        int stonesNumberToSow = 6;
        Mancala mancala = new Mancala(getPlayerUuid());

        int currentStonesNumber = mancala.sowStones(stonesNumberToSow);

        assertThat(currentStonesNumber).isEqualTo(stonesNumberToSow);
        assertThat(mancala.getStoneCount()).isEqualTo(stonesNumberToSow);
    }

    private UUID getPlayerUuid() {
        return UUID.randomUUID();
    }*/
}
