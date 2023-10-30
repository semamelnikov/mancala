package com.sm.mancala.domain.game;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.sm.mancala.domain.pit.Pit;
import com.sm.mancala.domain.player.Player;
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
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter(value = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "players_group_id")
    private PlayersGroup playersGroup;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "field_id")
    private Field field;

    @Enumerated(value = EnumType.STRING)
    private GameStatus status;

    private Game(PlayersGroup playersGroup, Field field, GameStatus status) {
        this.playersGroup = playersGroup;
        this.field = field;
        this.status = status;
    }

    public static Game createGame(int playersNumber, int cupsNumber, int stonesNumber) {
        final PlayersGroup playersGroup = PlayersGroup.create(playersNumber);
        final Field field = Field.createField(playersGroup, cupsNumber, stonesNumber);

        return new Game(
                playersGroup,
                field,
                GameStatus.ACTIVE
        );
    }

    public GameMoveResultData handleMoveAction(Long playerId, int cupNumber) {
        final GameMoveResult gameMoveResult = makeMove(playerId, cupNumber);
        return new GameMoveResultData(gameMoveResult, this);
    }

    private GameMoveResult makeMove(Long playerId, int cupNumber) {
        validateActivePlayer(playerId);

        final Pit moveLastPit = field.makeMove(playerId, cupNumber);

        if (field.isPlayerFinished(playerId)) {
            return processGameFinalResult(playerId);
        }

        return GameMoveResult.builder()
                .activePlayerId(getNextActivePlayerId(playerId, moveLastPit))
                .currentGameStatus(status)
                .build();
    }

    private void validateActivePlayer(Long playerId) {
        final Player activePlayer = playersGroup.getActivePlayer();
        if (!activePlayer.getId().equals(playerId)) {
            throw new IllegalArgumentException(
                    String.format("Player '%s' is not active game player", playerId)
            );
        }
    }

    private GameMoveResult processGameFinalResult(Long playerId) {
        field.collectRemainingStonesToMancala(playerId);
        final Map<Long, Integer> scorePerPlayer = field.getScorePerPlayer();
        updateGameFinalStatus(scorePerPlayer);

        final GameMoveResult moveResult = new GameMoveResult();
        moveResult.setCurrentGameStatus(this.status);
        if (status.equals(GameStatus.WIN)) {
            moveResult.setWinPlayerId(determineWinner(scorePerPlayer));
        }
        return moveResult;
    }

    private void updateGameFinalStatus(Map<Long, Integer> scorePerPlayer) {
        final boolean isDraw = scorePerPlayer.values().stream()
                .distinct()
                .count() == 1;
        if (isDraw) {
            this.status = GameStatus.DRAW;
        } else {
            this.status = GameStatus.WIN;
        }
    }

    private Long determineWinner(Map<Long, Integer> scorePerPlayer) {
        return scorePerPlayer.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private Long getNextActivePlayerId(Long playerId, Pit moveLastPit) {
        if (moveLastPit.isMancala() && moveLastPit.isOwnedBy(playerId)) {
            return playerId;
        }
        return playersGroup.moveToNextPlayer().getId();
    }

    public GameDto toDto() {
        return new GameDto()
                .id(id)
                .playersGroup(playersGroup.toDto())
                .field(field.toDto())
                .status(GameStatusDto.valueOf(status.name().toUpperCase()));
    }
}
