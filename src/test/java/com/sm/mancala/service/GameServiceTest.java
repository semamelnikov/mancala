package com.sm.mancala.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.sm.mancala.domain.game.Board;
import com.sm.mancala.domain.game.Game;
import com.sm.mancala.domain.game.GameMoveResultData;
import com.sm.mancala.domain.game.GameStatus;
import com.sm.mancala.domain.pit.Cup;
import com.sm.mancala.domain.player.Player;
import com.sm.mancala.domain.player.PlayersGroup;
import com.sm.mancala.exception.GameRuleException;
import com.sm.mancala.exception.NotFoundException;
import com.sm.mancala.properties.GameProperties;
import com.sm.mancala.repository.GameRepository;
import com.sm.mancala.web.model.GameMove;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GameServiceTest {

    private static final int PLAYERS_NUMBER = 2;
    private static final int CUPS_NUMBER = 6;
    private static final int STONES_PER_CUP = 6;
    private static final long GAME_ID = 1;

    @Mock
    private GameProperties gameProperties;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameServiceImpl gameService;

    @BeforeEach
    public void beforeEach() {
        when(gameProperties.getPlayersNumber()).thenReturn(PLAYERS_NUMBER);
        when(gameProperties.getCupsNumber()).thenReturn(CUPS_NUMBER);
        when(gameRepository.save(any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
    }

    @Test
    public void createGame_validationFailed_playersNumberIsOdd() {
        assertThrows(
                GameRuleException.class,
                () -> gameService.createGame(PLAYERS_NUMBER + 1, STONES_PER_CUP)
        );
    }

    @Test
    public void createGame_successfullyCreated() {
        final Game game = gameService.createGame(PLAYERS_NUMBER, STONES_PER_CUP);

        verify(gameRepository, times(1)).save(any());

        assertThat(game.getStatus()).isEqualTo(GameStatus.ACTIVE);
        assertThat(game.getBoard()).isNotNull();
        assertThat(game.getPlayersGroup()).isNotNull();
    }

    @Test
    public void getGameById_gameNotFound() {
        when(gameRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(
                NotFoundException.class,
                () -> gameService.getGameById(any())
        );
    }

    @Test
    public void processMove_gameNotFound() {
        when(gameRepository.findById(any())).thenReturn(Optional.empty());

        final GameMove gameMove = new GameMove(GAME_ID, 1L, STONES_PER_CUP);

        assertThrows(
                NotFoundException.class,
                () -> gameService.processMove(gameMove)
        );
    }

    @Test
    public void processMove_playerIsNotActive() {
        final int stonesPerCup = 6;
        final Game preparedGame = prepareTestGameScenario(stonesPerCup);
        when(gameRepository.findById(any())).thenReturn(Optional.of(preparedGame));

        final Long inactivePlayerId = 2L;
        final GameMove gameMove = new GameMove()
                .gameId(GAME_ID)
                .playerId(inactivePlayerId)
                .cupNumber(1);

        assertThrows(
                GameRuleException.class,
                () -> gameService.processMove(gameMove)
        );

        verify(gameRepository, times(0)).save(eq(preparedGame));
    }

    @Test
    public void processMove_selectedCupIneligible() {
        final int selectedCupNumber = 1;
        final int stonesPerCup = 6;
        final Game preparedGame = prepareTestGameScenario(stonesPerCup);
        makeCupEmpty(preparedGame.getPlayersGroup().getActivePlayer(), selectedCupNumber);
        when(gameRepository.findById(any())).thenReturn(Optional.of(preparedGame));

        final GameMove gameMove = new GameMove()
                .gameId(GAME_ID)
                .playerId(preparedGame.getPlayersGroup().getActivePlayer().getId())
                .cupNumber(selectedCupNumber);

        assertThrows(
                GameRuleException.class,
                () -> gameService.processMove(gameMove)
        );

        verify(gameRepository, times(0)).save(eq(preparedGame));
    }

    @Test
    public void processMove_activePlayerStayedTheSame_lastPitMancala() {
        final int selectedCupNumber = 1;
        final int stonesPerCup = 6;
        final Game preparedGame = prepareTestGameScenario(stonesPerCup);
        when(gameRepository.findById(any())).thenReturn(Optional.of(preparedGame));

        final Long activePlayerId = preparedGame.getPlayersGroup().getActivePlayer().getId();
        final GameMove gameMove = new GameMove()
                .gameId(GAME_ID)
                .playerId(activePlayerId)
                .cupNumber(selectedCupNumber);

        final GameMoveResultData moveResultData = gameService.processMove(gameMove);

        verify(gameRepository, times(1)).save(eq(preparedGame));

        final Game gameAfterMove = moveResultData.game();
        assertThat(moveResultData.gameMoveResult().getActivePlayerId()).isEqualTo(activePlayerId);
        assertThat(gameAfterMove.getStatus()).isEqualTo(GameStatus.ACTIVE);
        assertThat(
                gameAfterMove.getPlayersGroup().getActivePlayer().getMancala().getStoneCount()
        ).isEqualTo(1);
    }

    @Test
    public void processMove_activePlayerChanged_lastPitNotMancala() {
        final int selectedCupNumber = 2;
        final int stonesPerCup = 6;
        final Game preparedGame = prepareTestGameScenario(stonesPerCup);
        when(gameRepository.findById(any())).thenReturn(Optional.of(preparedGame));

        final Long activePlayerId = preparedGame.getPlayersGroup().getActivePlayer().getId();
        final GameMove gameMove = new GameMove()
                .gameId(GAME_ID)
                .playerId(activePlayerId)
                .cupNumber(selectedCupNumber);

        final GameMoveResultData moveResultData = gameService.processMove(gameMove);

        verify(gameRepository, times(1)).save(eq(preparedGame));

        final Game gameAfterMove = moveResultData.game();
        assertThat(moveResultData.gameMoveResult().getActivePlayerId()).isNotEqualTo(
                activePlayerId);
        assertThat(gameAfterMove.getStatus()).isEqualTo(GameStatus.ACTIVE);
        assertThat(
                gameAfterMove.getPlayersGroup().getActivePlayer().getCupByNumber(1).getStoneCount()
        ).isEqualTo(stonesPerCup + 1);
    }

    @Test
    public void processMove_activePlayerChanged_captureDone() {
        final int selectedCupNumber = 1;
        final int stonesPerCup = 3;
        final Game preparedGame = prepareTestGameScenario(stonesPerCup);
        makeCupEmpty(preparedGame.getPlayersGroup().getActivePlayer(), 4);
        when(gameRepository.findById(any())).thenReturn(Optional.of(preparedGame));

        final Long activePlayerId = preparedGame.getPlayersGroup().getActivePlayer().getId();
        final GameMove gameMove = new GameMove()
                .gameId(GAME_ID)
                .playerId(activePlayerId)
                .cupNumber(selectedCupNumber);

        final GameMoveResultData moveResultData = gameService.processMove(gameMove);

        verify(gameRepository, times(1)).save(eq(preparedGame));

        final Game gameAfterMove = moveResultData.game();
        assertThat(moveResultData.gameMoveResult().getActivePlayerId()).isNotEqualTo(
                activePlayerId);
        assertThat(gameAfterMove.getStatus()).isEqualTo(GameStatus.ACTIVE);
        assertThat(
                gameAfterMove.getPlayersGroup().getActivePlayer().getCupByNumber(3).isEmpty()
        ).isTrue();
        final Player previousPlayer = gameAfterMove.getPlayersGroup().moveToNextPlayer();
        assertThat(
                previousPlayer.getCupByNumber(selectedCupNumber).isEmpty()
        ).isTrue();
        assertThat(
                previousPlayer.getMancala().getStoneCount()
        ).isEqualTo(stonesPerCup + 1);
    }

    @Test
    public void processMove_processGameFinalResult_winViaCapture() {
        final int selectedCupNumber = 2;
        final int stonesPerCup = 1;
        final Game preparedGame = prepareTestGameScenario(stonesPerCup);
        clearAllCupsExceptOne(preparedGame.getPlayersGroup().getActivePlayer(), selectedCupNumber,
                CUPS_NUMBER);
        when(gameRepository.findById(any())).thenReturn(Optional.of(preparedGame));

        final Long activePlayerId = preparedGame.getPlayersGroup().getActivePlayer().getId();
        final GameMove gameMove = new GameMove()
                .gameId(GAME_ID)
                .playerId(activePlayerId)
                .cupNumber(selectedCupNumber);

        final GameMoveResultData moveResultData = gameService.processMove(gameMove);

        verify(gameRepository, times(1)).save(eq(preparedGame));

        final Game gameAfterMove = moveResultData.game();
        final Player activePlayerAfterMove = gameAfterMove.getPlayersGroup().getActivePlayer();

        assertThat(gameAfterMove.getStatus()).isEqualTo(GameStatus.WIN);
        assertThat(moveResultData.gameMoveResult().getWinPlayerId()).isNotEqualTo(activePlayerId);
        assertThat(activePlayerAfterMove.getId()).isEqualTo(activePlayerId);

        assertThat(
                activePlayerAfterMove.getMancala().getStoneCount()
        ).isEqualTo(stonesPerCup * 2);
        assertThat(activePlayerAfterMove.isFinished()).isTrue();

        final Player winner = gameAfterMove.getPlayersGroup().moveToNextPlayer();
        assertThat(winner.isFinished()).isTrue();
        assertThat(
                winner.getMancala().getStoneCount()
        ).isEqualTo(stonesPerCup * CUPS_NUMBER - 1);
    }

    @Test
    public void processMove_processGameFinalResult_draw() {
        final int selectedCupNumber = 6;
        final int stonesPerCup = 1;
        final Game preparedGame = prepareTestGameScenario(stonesPerCup);
        clearAllCupsExceptOne(
                preparedGame.getPlayersGroup().getPlayers().get(0),
                selectedCupNumber,
                CUPS_NUMBER
        );
        clearAllCupsExceptOne(
                preparedGame.getPlayersGroup().getPlayers().get(1),
                selectedCupNumber,
                CUPS_NUMBER
        );
        when(gameRepository.findById(any())).thenReturn(Optional.of(preparedGame));

        final Long activePlayerId = preparedGame.getPlayersGroup().getActivePlayer().getId();
        final GameMove gameMove = new GameMove()
                .gameId(GAME_ID)
                .playerId(activePlayerId)
                .cupNumber(selectedCupNumber);

        final GameMoveResultData moveResultData = gameService.processMove(gameMove);

        verify(gameRepository, times(1)).save(eq(preparedGame));

        final Game gameAfterMove = moveResultData.game();
        final Player activePlayerAfterMove = gameAfterMove.getPlayersGroup().getActivePlayer();

        assertThat(gameAfterMove.getStatus()).isEqualTo(GameStatus.DRAW);
        assertThat(moveResultData.gameMoveResult().getWinPlayerId()).isNull();

        assertThat(activePlayerAfterMove.isFinished()).isTrue();
        assertThat(
                activePlayerAfterMove.getMancala().getStoneCount()
        ).isEqualTo(1);

        final Player otherPlayer = gameAfterMove.getPlayersGroup().moveToNextPlayer();
        assertThat(otherPlayer.isFinished()).isTrue();
        assertThat(
                otherPlayer.getMancala().getStoneCount()
        ).isEqualTo(1);
    }

    private Game prepareTestGameScenario(int stonesPerCup) {
        final PlayersGroup playersGroup = PlayersGroup.createPlayersGroup(PLAYERS_NUMBER);
        addIdForPlayers(playersGroup);
        final Board board = Board.createBoardForPlayers(
                playersGroup,
                CUPS_NUMBER,
                stonesPerCup
        );
        final Game preparedGame = Game.createGame(
                playersGroup, board
        );
        preparedGame.setId(GAME_ID);

        return preparedGame;
    }

    private void makeCupEmpty(Player player, Integer cupNumber) {
        player.getCupByNumber(cupNumber).pickUpStones();
    }

    private void clearAllCupsExceptOne(Player player, int cupNumberToSkip, int cupsNumber) {
        for (int i = 0; i < cupsNumber; i++) {
            final int currentCupNumber = i + 1;
            if (currentCupNumber == cupNumberToSkip) {
                continue;
            }
            final Cup currentCup = player.getCupByNumber(i + 1);
            currentCup.pickUpStones();
        }
    }

    private void addIdForPlayers(PlayersGroup playersGroup) {
        final List<Player> players = playersGroup.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setId((long) (i + 1));
        }
    }
}
