package com.sm.mancala.domain.game;

import com.sm.mancala.web.model.GameMoveResultDataDto;

public record GameMoveResultData(GameMoveResult gameMoveResult, Game game) {

    public GameMoveResultDataDto toDto() {
        return new GameMoveResultDataDto()
                .activePlayerId(gameMoveResult.getActivePlayerId())
                .winPlayerId(gameMoveResult.getWinPlayerId())
                .game(game.toDto());
    }
}
