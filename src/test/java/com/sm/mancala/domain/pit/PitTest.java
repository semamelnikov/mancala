package com.sm.mancala.domain.pit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sm.mancala.exception.GameRuleException;
import org.junit.jupiter.api.Test;

public class PitTest {

    @Test
    public void sowStones_stoneCountIncreasedByOne() {
        final int initialStoneCount = 6;
        final Cup cup = new Cup(initialStoneCount, null, null);
        cup.sowStones();

        final Mancala mancala = new Mancala(null, null);
        mancala.sowStones();

        assertThat(cup.getStoneCount()).isEqualTo(initialStoneCount + 1);
        assertThat(mancala.getStoneCount()).isEqualTo(1);
    }

    @Test
    public void sowStonesWithSpecifiedNumber_cup_rulesViolation() {
        final int initialStoneCount = 6;
        final int specifiedStonesNumber = 2;
        final Cup cup = new Cup(initialStoneCount, null, null);

        assertThrows(GameRuleException.class, () -> cup.sowStones(specifiedStonesNumber));
    }

    @Test
    public void sowStonesWithSpecifiedNumber_mancala_stoneNumberIncreased() {
        final int specifiedStonesNumber = 2;

        final Mancala mancala = new Mancala(null, null);
        mancala.sowStones(specifiedStonesNumber);

        assertThat(mancala.getStoneCount()).isEqualTo(specifiedStonesNumber);
    }

    @Test
    public void isEmpty_true_pitHasNoStones() {
        final Cup cup = new Cup(0, null, null);
        final Mancala mancala = new Mancala(null, null);

        assertThat(cup.isEmpty()).isTrue();
        assertThat(mancala.isEmpty()).isTrue();
    }

    @Test
    public void isEmpty_false_pitHasStones() {
        final int initialStoneCount = 6;
        final Cup cup = new Cup(initialStoneCount, null, null);

        final Mancala mancala = new Mancala(null, null);
        mancala.sowStones();

        assertThat(cup.isEmpty()).isFalse();
        assertThat(mancala.isEmpty()).isFalse();
    }

    @Test
    public void pickUpStones_cup_pickedUp() {
        final int initialStoneCount = 6;
        final Cup cup = new Cup(initialStoneCount, null, null);

        final int pickedUpStones = cup.pickUpStones();

        assertThat(pickedUpStones).isEqualTo(initialStoneCount);
        assertThat(cup.isEmpty()).isTrue();
    }

    @Test
    public void pickUpStones_mancala_rulesViolation() {
        final Mancala mancala = new Mancala(null, null);

        assertThrows(GameRuleException.class, mancala::pickUpStones);
    }

    @Test
    public void isMancala_resultDependsOnType() {
        final Cup cup = new Cup(6, null, null);
        final Mancala mancala = new Mancala(null, null);

        assertThat(cup.isMancala()).isFalse();
        assertThat(mancala.isMancala()).isTrue();
    }

    @Test
    public void isCup_resultDependsOnType() {
        final Cup cup = new Cup(6, null, null);
        final Mancala mancala = new Mancala(null, null);

        assertThat(cup.isCup()).isTrue();
        assertThat(mancala.isCup()).isFalse();
    }
}
