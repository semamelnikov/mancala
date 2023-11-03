package com.sm.mancala.service;

import com.sm.mancala.domain.game.Board;
import com.sm.mancala.domain.game.Game;
import com.sm.mancala.domain.game.GameMoveResult;
import com.sm.mancala.domain.game.GameMoveResultData;
import com.sm.mancala.domain.game.GameStatus;
import com.sm.mancala.domain.pit.Cup;
import com.sm.mancala.domain.pit.Mancala;
import com.sm.mancala.domain.pit.Pit;
import com.sm.mancala.domain.player.Player;
import com.sm.mancala.domain.player.PlayersGroup;
import com.sm.mancala.exception.NotFoundException;
import com.sm.mancala.properties.GameProperties;
import com.sm.mancala.repository.GameRepository;
import com.sm.mancala.validator.GameValidator;
import com.sm.mancala.web.model.GameMove;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    private final GameProperties gameProperties;

    private final GameValidator gameValidator;

    public GameServiceImpl(GameRepository gameRepository, GameProperties gameProperties,
            GameValidator gameValidator) {
        this.gameRepository = gameRepository;
        this.gameProperties = gameProperties;
        this.gameValidator = gameValidator;
    }

    @Transactional
    @Override
    public Game createGame() {
        final PlayersGroup playersGroup = PlayersGroup.createPlayersGroup(
                gameProperties.getPlayersNumber());

        final Board board = Board.createBoardForPlayers(
                playersGroup,
                gameProperties.getCupsNumber(),
                gameProperties.getStonesPerCup()
        );

        final Game game = Game.createGame(playersGroup, board);

        return gameRepository.save(game);
    }

    @Override
    public Game getGameById(Long gameId) {
        return gameRepository.findById(gameId).orElseThrow(
                () -> new NotFoundException(String.format("Game with id = %s not found", gameId))
        );
    }

    @Transactional
    @Override
    public GameMoveResultData processMove(GameMove gameMove) {
        final Game game = getGameById(gameMove.getGameId());

        final GameMoveResultData moveResultData = handleMoveAction(
                game,
                gameMove.getPlayerId(),
                gameMove.getCupNumber()
        );

        gameRepository.save(moveResultData.game());

        return moveResultData;
    }

    private GameMoveResultData handleMoveAction(Game game, Long playerId, Integer cupNumber) {
        final GameMoveResult gameMoveResult = makeMove(game, playerId, cupNumber);
        return new GameMoveResultData(gameMoveResult, game);
    }

    private GameMoveResult makeMove(Game game, Long playerId, Integer cupNumber) {
        final PlayersGroup playersGroup = game.getPlayersGroup();
        final Player activePlayer = playersGroup.getActivePlayer();
        gameValidator.validateActivePlayer(activePlayer.getId(), playerId, game.getId());

        final Cup cup = activePlayer.getCupByNumber(cupNumber);
        gameValidator.validateCupMoveEligibility(cup, cupNumber);

        final Board board = game.getBoard();
        final Pit lastPit = board.makeMove(activePlayer, cup.getBoardIndex());

        if (playersGroup.hasFinishedPlayer()) {
            return processGameFinalResult(game);
        }

        return GameMoveResult.builder()
                .activePlayerId(getNextActivePlayerId(playerId, lastPit, playersGroup))
                .build();
    }

    private GameMoveResult processGameFinalResult(Game game) {
        final PlayersGroup playersGroup = game.getPlayersGroup();
        final List<Mancala> finalMancalaStates = playersGroup.getFinalMancalaStates();

        game.setStatus(
                isFinishedDraw(finalMancalaStates) ? GameStatus.DRAW : GameStatus.WIN
        );

        final GameMoveResult moveResult = new GameMoveResult();
        if (game.getStatus().equals(GameStatus.WIN)) {
            moveResult.setWinPlayerId(determineGameWinner(finalMancalaStates));
        }
        return moveResult;
    }

    private boolean isFinishedDraw(List<Mancala> finalMancalaStates) {
        return finalMancalaStates.stream()
                .map(Mancala::getStoneCount)
                .distinct()
                .count() == 1;
    }

    private Long determineGameWinner(List<Mancala> finalMancalaStates) {
        final Mancala winnerMancala = finalMancalaStates.stream()
                .max(Comparator.comparingInt(Mancala::getStoneCount))
                .orElseThrow(() -> new NotFoundException("Winners' mancala not found"));

        return winnerMancala.getPlayerId();
    }

    private Long getNextActivePlayerId(Long playerId, Pit lastPit, PlayersGroup playersGroup) {
        if (lastPit.isMancala() && lastPit.isOwnedBy(playerId)) {
            return playerId;
        }
        return playersGroup.moveToNextPlayer().getId();
    }
}
