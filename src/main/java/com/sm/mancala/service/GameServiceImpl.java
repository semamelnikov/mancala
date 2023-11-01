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
import com.sm.mancala.properties.GameProperties;
import com.sm.mancala.repository.GameRepository;
import com.sm.mancala.web.model.GameMove;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
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
        final PlayersGroup playersGroup = createPlayersGroup(gameProperties.getPlayersNumber());

        final Board board = createBoardWithPlayers(
                playersGroup,
                gameProperties.getCupsNumber(),
                gameProperties.getStonesPerCup()
        );

        final Game game = Game.builder()
                .playersGroup(playersGroup)
                .board(board)
                .status(GameStatus.ACTIVE)
                .build();

        return gameRepository.save(game);
    }

    @Transactional
    @Override
    public GameMoveResultData processMove(GameMove gameMove) {
        final Game game = gameRepository.findById(gameMove.getGameId()).orElseThrow();

        // playerId
        // cupNumber E [1,6]

        final GameMoveResultData moveResultData = handleMoveAction(game, gameMove.getPlayerId(),
                gameMove.getCupNumber());

        gameRepository.save(moveResultData.game());

        return moveResultData;
    }

    public GameMoveResultData handleMoveAction(Game game, Long playerId, Integer cupNumber) {
        final GameMoveResult gameMoveResult = makeMove(game, playerId, cupNumber);
        return new GameMoveResultData(gameMoveResult, game);
    }

    public GameMoveResult makeMove(Game game, Long playerId, Integer cupNumber) {
        final PlayersGroup playersGroup = game.getPlayersGroup();

        final Player activePlayer = playersGroup.getActivePlayer();

        validateActivePlayer(activePlayer.getId(), playerId);

        validateCupNumberRange(cupNumber);

        final Cup cup = activePlayer.getCups().get(cupNumber - 1);
        validateCupMoveEligibility(cup, cupNumber);

        final int cupBoardIndex = cup.getBoardIndex();

        final Board board = game.getBoard();
        final Pit lastPit = board.makeMove(activePlayer, cupBoardIndex);

        if (activePlayer.isFinished()) {
            return processGameFinalResult(game);
        }

        return GameMoveResult.builder()
                .activePlayerId(getNextActivePlayerId(playerId, lastPit, playersGroup))
                .currentGameStatus(game.getStatus())
                .build();
    }

    private void validateCupMoveEligibility(Cup cup, int cupNumber) {
        if (cup.isEmpty()) {
            throw new IllegalStateException(
                    String.format("Cup number '%s' is empty", cupNumber)
            );
        }
    }

    private void validateActivePlayer(Long activePlayerId, Long currentPlayerId) {
        if (!activePlayerId.equals(currentPlayerId)) {
            throw new IllegalArgumentException(
                    String.format("Player '%s' is not active game player", currentPlayerId)
            );
        }
    }

    private void validateCupNumberRange(int cupNumber) {
        if (cupNumber < 1 || cupNumber > gameProperties.getCupsNumber()) {
            throw new IllegalStateException(
                    String.format("Cup number '%s' is out of range", cupNumber)
            );
        }
    }

    private Long getNextActivePlayerId(Long playerId, Pit lastPit, PlayersGroup playersGroup) {
        if (lastPit.isMancala() && lastPit.isOwnedBy(playerId)) {
            return playerId;
        }
        return playersGroup.moveToNextPlayer().getId();
    }

    private GameMoveResult processGameFinalResult(Game game) {
        final PlayersGroup playersGroup = game.getPlayersGroup();
        final List<Mancala> finalMancalaStates = playersGroup.getFinalMancalaStates();

        game.setStatus(
                isFinishedDraw(finalMancalaStates) ? GameStatus.DRAW : GameStatus.WIN
        );

        final GameMoveResult moveResult = new GameMoveResult();
        moveResult.setCurrentGameStatus(game.getStatus());
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
                .orElseThrow();

        return winnerMancala.getPlayerId();
    }

    @Override
    public Game getGameById(Long gameId) {
        return gameRepository.findById(gameId).orElseThrow();
    }

    private PlayersGroup createPlayersGroup(Integer playersNumber) {
        final PlayersGroup playersGroup = new PlayersGroup();

        final List<Player> players = new ArrayList<>(playersNumber);
        for (int i = 0; i < playersNumber; i++) {
            final Player player = new Player(playersGroup);
            players.add(player);
        }

        playersGroup.setPlayers(players);
        playersGroup.setActivePlayerIndex(0);

        return playersGroup;
    }

    private Board createBoardWithPlayers(
            PlayersGroup playersGroup,
            Integer cupsNumber,
            Integer stonesPerCup
    ) {
        final Board board = new Board();

        final List<Player> players = playersGroup.getPlayers();

        final List<Pit> pits = new ArrayList<>((cupsNumber + 1) * players.size());

        for (final Player player : players) {
            // For each player we need to generate cups and one mancala
            final List<Cup> cups = IntStream.range(0, cupsNumber)
                    .mapToObj(i -> new Cup(stonesPerCup, player, board))
                    .toList();

            final Mancala mancala = new Mancala(player, board);

            player.setCups(cups);
            player.setMancala(mancala);

            pits.addAll(cups);
            pits.add(mancala);
        }

        int currentPitIndex = 0;
        for (final Pit pit : pits) {
            pit.setBoardIndex(currentPitIndex);
            currentPitIndex++;
        }

        final int lastCupIndex = pits.size() - 2;

        // we have all the objects in pits list, and references on the same objects are stored
        // in each player for easy access.

        board.setPits(pits);
        board.setLastCupIndex(lastCupIndex);

        return board;
    }
}
