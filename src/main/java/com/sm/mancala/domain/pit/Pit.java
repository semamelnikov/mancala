package com.sm.mancala.domain.pit;

import static lombok.AccessLevel.PRIVATE;

import com.sm.mancala.domain.game.Field;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "pit_type", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter(value = PRIVATE)
public abstract class Pit {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "field_id")
    private Field field;

    private UUID playerUuid;

    protected int stoneCount;

    protected Pit(int stoneCount, UUID playerUuid, Field field) {
        this.stoneCount = stoneCount;
        this.playerUuid = playerUuid;
        this.field = field;
    }

    public boolean isEmpty() {
        return this.stoneCount == 0;
    }

    public boolean isMancala() {
        return this.getType().equals(PitType.MANCALA);
    }

    public boolean isCup() {
        return this.getType().equals(PitType.CUP);
    }

    public boolean isOwnedBy(UUID possibleOwnerPlayerUuid) {
        return playerUuid.equals(possibleOwnerPlayerUuid);
    }

    public boolean isSowAllowedTo(UUID playerUuid) {
        return isCup() || (isMancala() && isOwnedBy(playerUuid));
    }

    public void sowStones() {
        this.stoneCount += 1;
    }

    public abstract PitType getType();

    public abstract int pickUpStones();

    public abstract void sowStones(int stonesNumber);
}
