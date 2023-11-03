package com.sm.mancala.domain.pit;

import com.sm.mancala.domain.game.Board;
import com.sm.mancala.domain.player.Player;
import com.sm.mancala.exception.GameRuleException;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("CUP")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cup extends Pit {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    public Cup(Integer stoneCount, Player player, Board board) {
        super(stoneCount, board);
        this.player = player;
    }

    @Override
    public int pickUpStones() {
        int currentStonesNumber = getStoneCount();
        this.stoneCount = 0;
        return currentStonesNumber;
    }

    @Override
    public boolean isSowAllowedFor(Long playerId) {
        return true;
    }

    @Override
    public Long getPlayerId() {
        return player.getId();
    }

    @Override
    public void sowStones(int stonesNumber) {
        throw new GameRuleException("Number of stones cannot be specified for Cup Pit");
    }

    @Override
    public boolean isCup() {
        return true;
    }

    @Override
    public boolean isMancala() {
        return false;
    }
}
