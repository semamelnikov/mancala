package com.sm.mancala.domain.game;

import static org.assertj.core.api.Assertions.assertThat;

import com.sm.mancala.domain.pit.Cup;
import com.sm.mancala.domain.pit.Pit;
import com.sm.mancala.domain.player.Player;
import com.sm.mancala.domain.player.PlayersGroup;
import com.sm.mancala.web.model.BoardDto;
import com.sm.mancala.web.model.PitDto;
import java.util.List;
import org.junit.jupiter.api.Test;

public class BoardTest {

    private final int cupsNumber = 6;
    private final int stonesPerCup = 6;
    private final int playersNumber = 2;
    private final int totalPitsPerPlayer = cupsNumber + 1;

    @Test
    public void createBoardForPlayers_referencesAndPitsCreated() {
        final PlayersGroup playersGroup = PlayersGroup.createPlayersGroup(playersNumber);
        final Board board = Board.createBoardForPlayers(playersGroup, cupsNumber, stonesPerCup);

        final BoardDto boardDto = board.toDto();

        assertThat(boardDto.getPits().size()).isEqualTo(totalPitsPerPlayer * playersNumber);
        assertThat(boardDto.getLastCupIndex()).isEqualTo(boardDto.getPits().size() - 2);

        int currentBoardNumber = 0;
        for (final PitDto pit : boardDto.getPits()) {
            final int expectedBoardNumber = (currentBoardNumber % totalPitsPerPlayer) + 1;
            assertThat(pit.getBoardNumberForPlayer()).isEqualTo(expectedBoardNumber);
            currentBoardNumber++;
        }

        int currentBoardIndex = 0;
        for (final Player player : playersGroup.getPlayers()) {
            for (int i = 0; i < cupsNumber; i++) {
                final Cup cup = player.getCupByNumber(i + 1);
                assertThat(cup).isNotNull();
                assertThat(cup.getBoardIndex()).isEqualTo(currentBoardIndex);
                currentBoardIndex++;
            }
            assertThat(player.getMancala()).isNotNull();
            currentBoardIndex++;
        }
        assertThat(currentBoardIndex).isEqualTo(totalPitsPerPlayer * playersNumber);
    }

    @Test
    public void makeMove_stonesDistributedToPlayerCupsAndMancala() {
        final PlayersGroup playersGroup = PlayersGroup.createPlayersGroup(playersNumber);
        addIdForPlayers(playersGroup);
        final Board board = Board.createBoardForPlayers(playersGroup, cupsNumber, stonesPerCup);

        final Player activePlayer = playersGroup.getActivePlayer();
        final int cupBoardIndex = 0;
        final Pit lastPit = board.makeMove(activePlayer, cupBoardIndex);

        assertThat(lastPit.isMancala()).isTrue();
        assertThat(lastPit.getStoneCount()).isEqualTo(1);
        assertThat(activePlayer.getCupByNumber(cupBoardIndex + 1).getStoneCount()).isEqualTo(0);
        for (int i = cupBoardIndex + 2; i < totalPitsPerPlayer; i++) {
            assertThat(activePlayer.getCupByNumber(i).getStoneCount()).isEqualTo(stonesPerCup + 1);
        }
    }

    @Test
    public void makeMove_stonesDistributionLoopedCounterclockwise() {
        final PlayersGroup playersGroup = PlayersGroup.createPlayersGroup(playersNumber);
        addIdForPlayers(playersGroup);
        final Board board = Board.createBoardForPlayers(playersGroup, cupsNumber, stonesPerCup);

        final Player activePlayer = playersGroup.getActivePlayer();
        final int cupBoardIndex = 3;
        final Pit lastPit = board.makeMove(activePlayer, cupBoardIndex);

        assertThat(lastPit.isCup()).isTrue();
        assertThat(lastPit.isOwnedBy(activePlayer.getId())).isFalse();
        assertThat(lastPit.getStoneCount()).isEqualTo(stonesPerCup + 1);

        final int cupBoardNumberForPlayer = cupBoardIndex + 1;
        assertThat(activePlayer.getCupByNumber(cupBoardNumberForPlayer).getStoneCount())
                .isEqualTo(0);
        for (int i = cupBoardNumberForPlayer + 1; i < totalPitsPerPlayer; i++) {
            assertThat(activePlayer.getCupByNumber(i).getStoneCount()).isEqualTo(stonesPerCup + 1);
        }

        assertThat(activePlayer.getMancala().getStoneCount()).isEqualTo(1);

        final Player nextPlayer = playersGroup.moveToNextPlayer();
        for (int i = 0; i < cupBoardIndex; i++) {
            assertThat(nextPlayer.getCupByNumber(i + 1).getStoneCount())
                    .isEqualTo(stonesPerCup + 1);
        }
    }

    @Test
    public void makeMove_stonesNotPlacedInNotOwnedMancala() {
        final PlayersGroup playersGroup = PlayersGroup.createPlayersGroup(playersNumber);
        addIdForPlayers(playersGroup);
        final int stonesPerCup = 8;
        final Board board = Board.createBoardForPlayers(playersGroup, cupsNumber, stonesPerCup);

        final Player activePlayer = playersGroup.getActivePlayer();
        final int cupBoardIndex = 5;
        final Pit lastPit = board.makeMove(activePlayer, cupBoardIndex);

        assertThat(lastPit.isCup()).isTrue();
        assertThat(lastPit.isOwnedBy(activePlayer.getId())).isTrue();
        assertThat(lastPit.getStoneCount()).isEqualTo(stonesPerCup + 1);

        final int cupBoardNumberForPlayer = cupBoardIndex + 1;
        assertThat(activePlayer.getCupByNumber(cupBoardNumberForPlayer).getStoneCount())
                .isEqualTo(0);
        assertThat(activePlayer.getMancala().getStoneCount()).isEqualTo(1);

        final Player nextPlayer = playersGroup.moveToNextPlayer();
        for (int i = 0; i < cupsNumber; i++) {
            assertThat(nextPlayer.getCupByNumber(i + 1).getStoneCount()).isEqualTo(
                    stonesPerCup + 1);
        }
        assertThat(nextPlayer.getMancala().getStoneCount()).isEqualTo(0);
    }

    @Test
    public void makeMove_stonesShouldBeCapturedInEndedInOwnEmptyCup() {
        final PlayersGroup playersGroup = PlayersGroup.createPlayersGroup(playersNumber);
        addIdForPlayers(playersGroup);
        final int stonesPerCup = 8;
        final Board board = Board.createBoardForPlayers(playersGroup, cupsNumber, stonesPerCup);

        final Player activePlayer = playersGroup.getActivePlayer();
        final int firstCupBoardIndex = 1;
        board.makeMove(activePlayer, firstCupBoardIndex);
        final int cupBoardIndex = 5;
        final Pit lastPit = board.makeMove(activePlayer, cupBoardIndex);

        assertThat(lastPit.isCup()).isTrue();
        assertThat(lastPit.isOwnedBy(activePlayer.getId())).isTrue();
        assertThat(lastPit.isEmpty()).isTrue();

        // +1 - first move
        // +1 - second move
        // 1 from own cup + 9 from the opposite = +10 - capture
        // total in mancala = 10 + 1 + 1 = 12
        assertThat(activePlayer.getMancala().getStoneCount()).isEqualTo(12);

        final Player nextPlayer = playersGroup.moveToNextPlayer();
        assertThat(nextPlayer.getCupByNumber(5).isEmpty()).isTrue();
        assertThat(nextPlayer.getMancala().isEmpty()).isTrue();
    }

    private void addIdForPlayers(PlayersGroup playersGroup) {
        final List<Player> players = playersGroup.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setId((long) (i + 1));
        }
    }
}
