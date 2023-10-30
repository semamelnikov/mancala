package com.sm.mancala.service;

import com.sm.mancala.domain.game.Game;
import com.sm.mancala.domain.game.GameMoveResultData;
import com.sm.mancala.properties.GameProperties;
import com.sm.mancala.repository.GameRepository;
import com.sm.mancala.web.model.GameMove;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    private final GameProperties gameProperties;

    public GameServiceImpl(GameRepository gameRepository, GameProperties gameProperties) {
        this.gameRepository = gameRepository;
        this.gameProperties = gameProperties;
    }

    @Transactional
    @Override
    public Game createGame() {
        final Game game = Game.createGame(
                gameProperties.getPlayersNumber(),
                gameProperties.getCupsNumber(),
                gameProperties.getStonesPerCup()
        );
        return gameRepository.save(game);
    }

    @Transactional
    @Override
    public GameMoveResultData processMove(GameMove gameMove) {
        final Game game = gameRepository.findById(gameMove.getGameId()).orElseThrow();

        final GameMoveResultData moveResultData =
                game.handleMoveAction(gameMove.getPlayerId(), gameMove.getCupNumber());

        gameRepository.save(moveResultData.game());

        return moveResultData;
    }

    @Override
    public Game getGameById(Long gameId) {
        return gameRepository.findById(gameId).orElseThrow();
    }
}
