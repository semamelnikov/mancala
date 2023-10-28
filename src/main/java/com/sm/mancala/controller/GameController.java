package com.sm.mancala.controller;

import com.sm.mancala.service.GameService;
import com.sm.mancala.web.api.GameApi;
import com.sm.mancala.web.model.GameMove;
import com.sm.mancala.web.model.GameMoveResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController implements GameApi {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public ResponseEntity<GameMoveResult> makeGameMove(GameMove gameMove) {
        final var result = gameService.makeMove(gameMove);
        return ResponseEntity.ok(result);
    }
}
