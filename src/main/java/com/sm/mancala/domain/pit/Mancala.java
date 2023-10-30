package com.sm.mancala.domain.pit;

import com.sm.mancala.domain.game.Field;
import com.sm.mancala.domain.player.Player;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("MANCALA")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mancala extends Pit {

    private Mancala(Player player, Field field) {
        super(0, player, field);
    }

    public static Mancala createMancala(Player player, Field field) {
        return new Mancala(player, field);
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
