package com.sm.mancala.domain.pit;

import com.sm.mancala.domain.game.Board;
import com.sm.mancala.domain.player.Player;
import com.sm.mancala.exception.GameRuleException;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("MANCALA")
@NoArgsConstructor
@Getter
@Setter
public class Mancala extends Pit {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    public Mancala(Player player, Board board) {
        super(0, board);
        this.player = player;
    }

    @Override
    public int pickUpStones() {
        throw new GameRuleException("Stones cannot be picked up from Mancala Pit");
    }

    @Override
    public boolean isSowAllowedFor(Long playerId) {
        return isOwnedBy(playerId);
    }

    @Override
    public Long getPlayerId() {
        return player.getId();
    }

    @Override
    public void sowStones(int stonesNumber) {
        this.stoneCount += stonesNumber;
    }

    @Override
    public boolean isCup() {
        return false;
    }

    @Override
    public boolean isMancala() {
        return true;
    }
}
