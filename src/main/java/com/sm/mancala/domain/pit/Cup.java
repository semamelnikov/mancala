package com.sm.mancala.domain.pit;

import com.sm.mancala.domain.game.Field;
import com.sm.mancala.domain.player.Player;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("CUP")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cup extends Pit {

    private Cup(int stonesNumber, Player player, Field field) {
        super(stonesNumber, player, field);
    }

    public static Cup createCup(int stonesNumber, Player player, Field field) {
        return new Cup(stonesNumber, player, field);
    }

    @Override
    public PitType getType() {
        return PitType.CUP;
    }

    @Override
    public int pickUpStones() {
        int currentStonesNumber = getStoneCount();
        this.stoneCount = 0;
        return currentStonesNumber;
    }

    @Override
    public void sowStones(int stonesNumber) {
        throw new IllegalStateException("Number of stones cannot be specified for Mancala Pit");
    }
}
