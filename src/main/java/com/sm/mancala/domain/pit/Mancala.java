package com.sm.mancala.domain.pit;

import com.sm.mancala.domain.game.Field;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("MANCALA")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mancala extends Pit {

    private Mancala(UUID playerUuid, Field field) {
        super(0, playerUuid, field);
    }

    public static Mancala createMancala(UUID playerUuid, Field field) {
        return new Mancala(playerUuid, field);
    }

    @Override
    public PitType getType() {
        return PitType.MANCALA;
    }

    @Override
    public int pickUpStones() {
        throw new IllegalStateException("Stones cannot be picked up from Mancala Pit");
    }

    @Override
    public void sowStones(int stonesNumber) {
        this.stoneCount += stonesNumber;
    }
}
