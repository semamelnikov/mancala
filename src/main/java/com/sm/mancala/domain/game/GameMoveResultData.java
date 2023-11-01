package com.sm.mancala.domain.game;

import com.sm.mancala.web.model.GameMoveResultDataDto;
import com.sm.mancala.web.model.GameStatusDto;

public record GameMoveResultData(GameMoveResult gameMoveResult, Game game) {

    public GameMoveResultDataDto toDto() {
        return new GameMoveResultDataDto()
                .activePlayerId(gameMoveResult.getActivePlayerId())
                .winPlayerId(gameMoveResult.getWinPlayerId())
                .gameStatus(
                        GameStatusDto.valueOf(
                                gameMoveResult.getCurrentGameStatus().name().toUpperCase()
                        )
                )
                .game(game.toDto());
    }
}
