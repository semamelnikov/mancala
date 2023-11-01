package com.sm.mancala.domain.game;

import com.sm.mancala.domain.player.PlayersGroup;
import com.sm.mancala.web.model.GameDto;
import com.sm.mancala.web.model.GameStatusDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "players_group_id")
    private PlayersGroup playersGroup;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "board_id")
    private Board board;

    @Enumerated(value = EnumType.STRING)
    private GameStatus status;

    public static Game createGame(PlayersGroup playersGroup, Board board) {
        final Game game = new Game();
        game.setPlayersGroup(playersGroup);
        game.setBoard(board);
        game.setStatus(GameStatus.ACTIVE);
        return game;
    }

    public PlayersGroup getPlayersGroup() {
        return playersGroup;
    }

    public Board getBoard() {
        return board;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public GameDto toDto() {
        return new GameDto()
                .id(id)
                .playersGroup(playersGroup.toDto())
                .board(board.toDto())
                .status(GameStatusDto.valueOf(status.name().toUpperCase()));
    }
}
