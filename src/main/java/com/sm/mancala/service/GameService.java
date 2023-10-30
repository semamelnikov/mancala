package com.sm.mancala.service;

import com.sm.mancala.domain.game.Game;
import com.sm.mancala.web.model.GameMove;
import com.sm.mancala.web.model.GameMoveResult;
import java.util.UUID;

public interface GameService {

    GameMoveResult makeMove(GameMove gameMove);

    Game createGame();

    Game getGameByCustomId(UUID gameUuid);
}
