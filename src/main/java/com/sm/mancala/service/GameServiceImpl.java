package com.sm.mancala.service;

import com.sm.mancala.model.Game;
import com.sm.mancala.web.model.GameMove;
import com.sm.mancala.web.model.GameMoveResult;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GameServiceImpl implements GameService {

    private final Game game;

    public GameServiceImpl() {
        this.game = new Game(UUID.randomUUID().toString());
    }

    @Override
    public GameMoveResult makeMove(GameMove gameMove) {
        return null;
    }
}
