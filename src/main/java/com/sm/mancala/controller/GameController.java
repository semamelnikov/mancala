package com.sm.mancala.controller;

import com.sm.mancala.service.GameService;
import com.sm.mancala.web.api.GamesApi;
import com.sm.mancala.web.model.CreateGameRequest;
import com.sm.mancala.web.model.GameDto;
import com.sm.mancala.web.model.GameMove;
import com.sm.mancala.web.model.GameMoveResultDataDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController implements GamesApi {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public ResponseEntity<GameMoveResultDataDto> makeGameMove(GameMove gameMove) {
        final var gameMoveResultData = gameService.processMove(gameMove);
        return ResponseEntity.ok(gameMoveResultData.toDto());
    }

    @Override
    public ResponseEntity<GameDto> createGame(CreateGameRequest createGameRequest) {
        final var game = gameService.createGame(
                createGameRequest.getPlayersNumber(),
                createGameRequest.getStonesPerCup()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(game.toDto());
    }

    @Override
    public ResponseEntity<GameDto> getGameById(Long gameId) {
        final var game = gameService.getGameById(gameId);
        return ResponseEntity.ok(game.toDto());
    }
}
