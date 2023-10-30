package com.sm.mancala.domain.pit;

import static lombok.AccessLevel.PRIVATE;

import com.sm.mancala.domain.game.Field;
import com.sm.mancala.domain.player.Player;
import com.sm.mancala.web.model.PitDto;
import com.sm.mancala.web.model.PitTypeDto;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "pit_type", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = PRIVATE)
public abstract class Pit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id")
    private Field field;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    protected int stoneCount;

    protected Pit(int stoneCount, Player player, Field field) {
        this.stoneCount = stoneCount;
        this.field = field;
        this.player = player;
    }

    public boolean isEmpty() {
        return this.stoneCount == 0;
    }

    public int getStoneCount() {
        return stoneCount;
    }

    public Long getPlayerId() {
        return player.getId();
    }

    public boolean isMancala() {
        return this.getType().equals(PitType.MANCALA);
    }

    public boolean isCup() {
        return this.getType().equals(PitType.CUP);
    }

    public boolean isOwnedBy(Long possibleOwnerPlayerId) {
        return player.getId().equals(possibleOwnerPlayerId);
    }

    public boolean isSowAllowedTo(Long playerId) {
        return isCup() || (isMancala() && isOwnedBy(playerId));
    }

    public void sowStones() {
        this.stoneCount += 1;
    }

    public abstract PitType getType();

    public abstract int pickUpStones();

    public abstract void sowStones(int stonesNumber);

    public PitDto toDto() {
        return new PitDto()
                .id(id)
                .playerId(getPlayerId())
                .stoneCount(stoneCount)
                .type(PitTypeDto.valueOf(getType().name().toUpperCase()));
    }
}
