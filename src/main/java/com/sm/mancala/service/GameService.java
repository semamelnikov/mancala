package com.sm.mancala.service;

import com.sm.mancala.domain.game.Game;
import com.sm.mancala.domain.game.GameMoveResultData;
import com.sm.mancala.web.model.GameMove;

public interface GameService {

    GameMoveResultData processMove(GameMove gameMove);

    Game createGame(Integer playersNumber, Integer stonesPerCup);

    Game getGameById(Long gameId);
}
