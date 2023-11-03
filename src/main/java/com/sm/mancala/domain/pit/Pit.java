package com.sm.mancala.domain.pit;

import com.sm.mancala.domain.game.Board;
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

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "pit_type", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Pit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    protected Board board;

    protected int boardIndex;

    protected int boardNumberForPlayer;

    protected int stoneCount;

    protected Pit(Integer stoneCount, Board board) {
        this.stoneCount = stoneCount;
        this.board = board;
    }

    public void sowStones() {
        this.stoneCount += 1;
    }

    public boolean isOwnedBy(Long playerId) {
        return playerId.equals(getPlayerId());
    }

    public boolean isEmpty() {
        return getStoneCount() == 0;
    }

    public abstract int pickUpStones();

    public abstract boolean isSowAllowedFor(Long playerId);

    public abstract Long getPlayerId();

    public abstract void sowStones(int stonesNumber);

    public abstract boolean isMancala();

    public abstract boolean isCup();

    public int getStoneCount() {
        return stoneCount;
    }

    public int getBoardIndex() {
        return boardIndex;
    }

    public void setBoardIndex(int boardIndex) {
        this.boardIndex = boardIndex;
    }

    public void setBoardNumberForPlayer(int boardNumberForPlayer) {
        this.boardNumberForPlayer = boardNumberForPlayer;
    }

    public PitDto toDto() {
        return new PitDto()
                .id(id)
                .playerId(getPlayerId())
                .boardNumberForPlayer(boardNumberForPlayer)
                .stoneCount(stoneCount)
                .type(isCup() ? PitTypeDto.CUP : PitTypeDto.MANCALA);
    }
}
