package com.sm.mancala.domain.player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.sm.mancala.domain.game.Board;
import com.sm.mancala.domain.pit.Mancala;
import com.sm.mancala.web.model.PlayersGroupDto;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PlayersGroupTest {

    private final int playersNumber = 2;

    @Test
    public void createPlayersGroup_playersSizeIsCorrect() {
        final PlayersGroup playersGroup = PlayersGroup.createPlayersGroup(playersNumber);

        assertThat(playersGroup.getPlayers().size()).isEqualTo(playersNumber);
    }

    @Test
    public void createPlayersGroup_initialActivePlayerIndexZero() {
        final PlayersGroup playersGroup = PlayersGroup.createPlayersGroup(playersNumber);

        final PlayersGroupDto playersGroupDto = playersGroup.toDto();
        assertThat(playersGroupDto.getActivePlayerIndex()).isEqualTo(0);
    }

    @Test
    public void moveToNextPlayer_activePlayerIndexMoved() {
        final PlayersGroup playersGroup = PlayersGroup.createPlayersGroup(playersNumber);

        final Player player = playersGroup.moveToNextPlayer();

        final PlayersGroupDto playersGroupDto = playersGroup.toDto();
        assertThat(player).isNotNull();
        assertThat(playersGroupDto.getActivePlayerIndex()).isEqualTo(1);
    }

    @Test
    public void moveToNextPlayer_activePlayerIndexLooped() {
        final PlayersGroup playersGroup = PlayersGroup.createPlayersGroup(playersNumber);

        for (int i = 0; i < playersNumber; i++) {
            playersGroup.moveToNextPlayer();
        }

        final PlayersGroupDto playersGroupDto = playersGroup.toDto();
        assertThat(playersGroupDto.getActivePlayerIndex()).isEqualTo(0);
    }

    @Test
    public void moveToNextPlayer_returnsReferenceOnActivePlayer() {
        final PlayersGroup playersGroup = PlayersGroup.createPlayersGroup(playersNumber);

        final Player activePlayer = playersGroup.moveToNextPlayer();

        assertSame(activePlayer, playersGroup.getPlayers().get(1));
    }

    @Test
    public void getActivePlayer_returnsReferenceOnInstanceFromPlayers_initialState() {
        final PlayersGroup playersGroup = PlayersGroup.createPlayersGroup(playersNumber);

        final Player activePlayer = playersGroup.getActivePlayer();

        assertSame(activePlayer, playersGroup.getPlayers().get(0));
    }

    @Test
    public void getActivePlayer_returnsReferenceOnInstanceFromPlayers_afterMove() {
        final PlayersGroup playersGroup = PlayersGroup.createPlayersGroup(playersNumber);
        playersGroup.moveToNextPlayer();

        final Player activePlayer = playersGroup.getActivePlayer();

        assertSame(activePlayer, playersGroup.getPlayers().get(1));
    }

    @Test
    public void getFinalMancalaStates_allStonesInMancalas() {
        final PlayersGroup playersGroup = PlayersGroup.createPlayersGroup(playersNumber);
        final int cupsNumber = 6;
        final int stonesPerCup = 4;
        Board.createBoardForPlayers(playersGroup, cupsNumber, stonesPerCup);

        final List<Mancala> finalMancalaStates = playersGroup.getFinalMancalaStates();

        assertThat(finalMancalaStates.size()).isEqualTo(playersNumber);
        assertThat(
                finalMancalaStates.stream()
                        .allMatch(mancala -> mancala.getStoneCount() == cupsNumber * stonesPerCup)
        ).isTrue();
        assertThat(
                playersGroup.getPlayers().stream().allMatch(Player::isFinished)
        ).isTrue();
    }

    @Test
    public void hasFinishedPlayer_false_initialState() {
        final PlayersGroup playersGroup = PlayersGroup.createPlayersGroup(playersNumber);
        Board.createBoardForPlayers(playersGroup, 6, 4);

        final boolean hasFinishedPlayer = playersGroup.hasFinishedPlayer();

        assertThat(hasFinishedPlayer).isFalse();
    }

    @Test
    public void hasFinishedPlayer_true_initialState() {
        final PlayersGroup playersGroup = PlayersGroup.createPlayersGroup(playersNumber);
        Board.createBoardForPlayers(playersGroup, 6, 4);
        playersGroup.getActivePlayer().collectStonesToMancala();

        final boolean hasFinishedPlayer = playersGroup.hasFinishedPlayer();

        assertThat(hasFinishedPlayer).isTrue();
    }
}
