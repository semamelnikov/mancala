package com.sm.mancala.service;

import com.sm.mancala.web.model.GameMove;
import com.sm.mancala.web.model.GameMoveResult;

public interface GameService {

    GameMoveResult makeMove(GameMove gameMove);
}
