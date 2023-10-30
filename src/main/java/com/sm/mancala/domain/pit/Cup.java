package com.sm.mancala.domain.pit;

import com.sm.mancala.domain.game.Field;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("CUP")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cup extends Pit {

    private Cup(int stonesNumber, UUID playerUuid, Field field) {
        super(stonesNumber, playerUuid, field);
    }

    public static Cup createCup(int stonesNumber, UUID playerUuid, Field field) {
        return new Cup(stonesNumber, playerUuid, field);
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
