package com.sm.mancala.domain.player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sm.mancala.domain.game.Board;
import com.sm.mancala.domain.pit.Cup;
import com.sm.mancala.domain.pit.Mancala;
import com.sm.mancala.exception.GameRuleException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PlayerTest {

    private final int cupsNumber = 6;
    private final int stonesPerCup = 6;
    private final PlayersGroup playersGroupRef = PlayersGroup.createPlayersGroup(2);
    private final Board boardRef = Board.createBoardForPlayers(
            playersGroupRef, cupsNumber, stonesPerCup
    );

    @Test
    public void isFinished_true_allCupsAreEmpty() {
        final Player player = Player.createPlayer(playersGroupRef);

        final List<Cup> cups = new ArrayList<>();
        cups.add(new Cup(0, player, boardRef));
        cups.add(new Cup(0, player, boardRef));
        final Mancala mancala = new Mancala(player, boardRef);
        player.setPits(cups, mancala);

        boolean isFinished = player.isFinished();

        assertThat(isFinished).isTrue();
    }

    @Test
    public void isFinished_false_cupsAreNotEntirelyEmpty() {
        final Player player = Player.createPlayer(playersGroupRef);

        final List<Cup> cups = new ArrayList<>();
        cups.add(new Cup(stonesPerCup, player, boardRef));
        cups.add(new Cup(0, player, boardRef));
        final Mancala mancala = new Mancala(player, boardRef);
        player.setPits(cups, mancala);

        boolean isFinished = player.isFinished();

        assertThat(isFinished).isFalse();
    }

    @Test
    public void collectStonesToMancala_stonesSuccessfullyCollected() {
        final Player player = Player.createPlayer(playersGroupRef);

        final List<Cup> cups = new ArrayList<>();
        cups.add(new Cup(stonesPerCup, player, boardRef));
        cups.add(new Cup(stonesPerCup, player, boardRef));
        final Mancala mancala = new Mancala(player, boardRef);
        player.setPits(cups, mancala);

        final Mancala mancalaWithStones = player.collectStonesToMancala();

        assertThat(mancalaWithStones.getStoneCount()).isEqualTo(stonesPerCup * cups.size());
        assertThat(player.isFinished()).isTrue();
    }

    @Test
    public void getCupsByNumber_validCupNumber() {
        final Player player = Player.createPlayer(playersGroupRef);
        final Cup cupToFindRef = new Cup(stonesPerCup, player, boardRef);

        final List<Cup> cups = new ArrayList<>();
        cups.add(cupToFindRef);
        cups.add(new Cup(0, player, boardRef));
        final Mancala mancala = new Mancala(player, boardRef);
        player.setPits(cups, mancala);

        final Cup foundCupRef = player.getCupByNumber(1);

        assertSame(foundCupRef, cupToFindRef);
    }

    @Test
    public void getCupsByNumber_invalidCupNumber() {
        final Player player = Player.createPlayer(playersGroupRef);

        final List<Cup> cups = new ArrayList<>();
        cups.add(new Cup(stonesPerCup, player, boardRef));
        final Mancala mancala = new Mancala(player, boardRef);

        player.setPits(cups, mancala);

        assertThrows(
                GameRuleException.class,
                () -> player.getCupByNumber(cups.size() + 1)
        );
    }
}
