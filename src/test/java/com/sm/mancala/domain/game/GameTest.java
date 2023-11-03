package com.sm.mancala.domain.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.sm.mancala.domain.player.PlayersGroup;
import org.junit.jupiter.api.Test;

public class GameTest {

    @Test
    public void createGame_activeGameCreated() {
        final PlayersGroup playersGroupRef = PlayersGroup.createPlayersGroup(2);
        final Board boardRef = Board.createBoardForPlayers(playersGroupRef, 6, 6);

        final Game game = Game.createGame(playersGroupRef, boardRef);

        assertThat(game.getStatus()).isEqualTo(GameStatus.ACTIVE);
        assertSame(game.getBoard(), boardRef);
        assertSame(game.getPlayersGroup(), playersGroupRef);
    }
}
