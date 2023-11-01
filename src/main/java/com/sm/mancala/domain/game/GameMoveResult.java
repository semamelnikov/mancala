package com.sm.mancala.domain.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class GameMoveResult {

    private Long activePlayerId;
    private Long winPlayerId;
    private GameStatus currentGameStatus;
}
