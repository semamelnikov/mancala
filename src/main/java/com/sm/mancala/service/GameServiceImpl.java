package com.sm.mancala.service;

import com.sm.mancala.domain.game.Game;
import com.sm.mancala.repository.GameRepository;
import com.sm.mancala.web.model.GameMove;
import com.sm.mancala.web.model.GameMoveResult;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    private Game game;

    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Transactional
    public Game createGame() {
        final int playersNumber = 2;
        final int stonesNumber = 6;
        final int cupsNumber = 6;

        Game game = Game.createGame(playersNumber, cupsNumber, stonesNumber);
        return gameRepository.save(game);
    }

    @Transactional
    @Override
    public Game getGameByCustomId(UUID gameUuid) {
        return gameRepository.findByCustomId(gameUuid).orElseThrow();
    }

    @Transactional
    @Override
    public GameMoveResult makeMove(GameMove gameMove) {
        return game.makeMove(
                UUID.fromString(gameMove.getPlayerId()),
                gameMove.getCupNumber()
        );
    }
}
